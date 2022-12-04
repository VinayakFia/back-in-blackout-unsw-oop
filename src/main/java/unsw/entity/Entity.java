package unsw.entity;

import unsw.blackout.FileTransferException;
import unsw.file.File;
import unsw.move.Move;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Entity {
    private final int range;
    private Angle position;
    private final String id;
    private double height;
    private int direction;
    private final boolean isMoving;
    private final int velocity;
    private final Map<String, File> files = new HashMap<>();
    private final List<String> supportedEntities = new ArrayList<>();
    private Map<String, File> outgoingFiles = new HashMap<>();
    private Map<String, File> incomingFiles = new HashMap<>();
    private final double fileStorageLimit;
    private final double byteStorageLimit;
    private final double byteInBandwidth;
    private final double byteOutBandwidth;
    private final Move move;

    protected Entity(int range, Angle position, String id, double height,
                     int direction, boolean isMoving, int velocity,
                     List<String> supportedEntities, Move move,
                     double fileStorageLimit, double byteStorageLimit,
                     double byteInBandwidth, double byteOutBandwidth) {

        this.range = range;
        this.position = position;
        this.id = id;
        this.height = height;
        this.direction = direction;
        this.isMoving = isMoving;
        this.velocity = velocity;
        this.supportedEntities.addAll(supportedEntities);
        this.move = move;
        this.fileStorageLimit = fileStorageLimit;
        this.byteStorageLimit = byteStorageLimit;
        this.byteInBandwidth = byteInBandwidth;
        this.byteOutBandwidth = byteOutBandwidth;
    }

    public Angle getPosition() {
        // Makes sure angle is between 0 and 360
        setPosition(Angle.fromDegrees(position.toDegrees()));
        return position;
    }

    public double getPositionInDegrees() {
        return position.toDegrees();
    }

    public String getId() {
        return id;
    }

    public int getRange() {
        return range;
    }

    public double getHeight() {
        return height;
    }

    public int getVelocity() {
        return velocity;
    }

    public int getDirection() {
        return direction;
    }

    public double getByteOutBandwidth() {
        return byteOutBandwidth;
    }

    public double getByteInBandwidth() {
        return byteInBandwidth;
    }

    protected Map<String, File> getIncomingFiles() {
        return incomingFiles;
    }

    protected void setIncomingFiles(Map<String, File> files) {
        this.incomingFiles = files;
    }

    public void setOutgoingFiles(Map<String, File> outgoingFiles) {
        this.outgoingFiles = outgoingFiles;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setPosition(Angle position) {
        this.position = position;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    private Map<String, File> getStoredFiles() {
        Map<String, File> map = new HashMap<>();
        map.putAll(files);
        map.putAll(incomingFiles);
        return map;
    }

    public File getFile(String fileName) {
        return getStoredFiles().get(fileName);
    }

    public void addFile(String fileName, String content) {
        files.put(fileName, new File(fileName, content));
    }

    public abstract String getType();

    public void step(Function<Angle, Double> getPlanetHeight,
                     Function<String, List<String>> communicableEntitiesInRange,
                     BiFunction<String, String, Boolean> isFileBeingTransferred,
                     BiFunction<String, String, Boolean> isFileBeingTransferredFrom) {

        moveEntity(getPlanetHeight);
        continueTransfer(communicableEntitiesInRange, isFileBeingTransferred, isFileBeingTransferredFrom);
    }

    protected void moveEntity(Function<Angle, Double> getPlanetHeight) {
        if (isMoving) move.execute(getPlanetHeight, this);
    }

    private int numBytesStored() {
        int size = 0;
        for (File file : Stream.concat(files.values().stream(), incomingFiles.values().stream())
                .collect(Collectors.toList())) {

            size += file.getFileSize();
        }
        return size;
    }

    public void sendFile(File file, String fromId)
            throws FileTransferException {

        if (!getFile(file.getName()).isTransferCompleted()) {
            throw new FileTransferException.VirtualFileNotFoundException(
                    file.getName() + " has not completed transfer to " + getId()
            );
        }

        if (outgoingFiles.size() + 1 > byteOutBandwidth) {
            throw new FileTransferException.VirtualFileNoBandwidthException(
                    "Exceeded " + fromId + "'s bandwidth"
            );
        }

        outgoingFiles.put(file.getName(), file);
    }

    public void receiveFile(File file, String toId)
            throws FileTransferException {

        if (file.getFileSize() + numBytesStored() > byteStorageLimit) {
            throw new FileTransferException.VirtualFileNoStorageSpaceException(
                    "Max Storage Reached on " + toId
            );
        }

        if (files.size() + 1 > fileStorageLimit) {
            throw new FileTransferException.VirtualFileNoStorageSpaceException(
                    "Max Storage Reached on " + toId
            );
        }

        if (incomingFiles.size() + 1 > byteInBandwidth) {
            throw new FileTransferException.VirtualFileNoBandwidthException(
                    "Exceeded " + toId + "'s bandwidth"
            );
        }

        incomingFiles.put(file.getName(), file);
    }

    protected void continueTransfer(Function<String, List<String>> communicableEntitiesInRange,
                                    BiFunction<String, String, Boolean> isFileBeingTransferredTo,
                                    BiFunction<String, String, Boolean> isFileBeingTransferredFrom) {

        int transferRate = (int) Math.floor(byteInBandwidth / incomingFiles.size());

        for (File file : incomingFiles.values()) {
            // If a file is not being transferred from a teleporting satellite it implies
            // that the satellite has made a teleport, the case for which is handled below
            if (!isFileBeingTransferredFrom.apply(file.getName(), file.getFrom())
                    && file.getFromType().equals("TeleportingSatellite")) {

                completeIncomingFileTransfer(new File(file.getName(),
                        file.getContentTransferring().replaceAll("t", "")));
                continue;
            }

            // If the entities are out of range the file cannot be transferred
            if (!communicableEntitiesInRange.apply(file.getFrom()).contains(file.getTo())) {
                incomingFiles.remove(file.getName());
                continue;
            }

            file.setTransferRate(Math.max(transferRate, file.getMaxTransferRate()));
            file.stepFileTransfer();

            if (file.isTransferCompleted()) {
                completeIncomingFileTransfer(file);
            }
        }

        // If a file has completed its transfer to its destination entity, it does not
        // need to be transferred from this device
        for (File file : outgoingFiles.values()) {
            if (!isFileBeingTransferredTo.apply(file.getName(), file.getTo())) {
                outgoingFiles.remove(file.getName());
            }
        }
    }

    /**
     * Moves a file from incoming Files, which is the intermediary stage
     * of file transfer, to files, which serves as the main storage
     * @param file
     */
    protected void completeIncomingFileTransfer(File file) {
        File fileCopy = new File(file.getName(), file.getContent());

        incomingFiles.remove(fileCopy.getName());
        files.remove(fileCopy.getName());
        files.put(fileCopy.getName(), fileCopy);
    }

    public Map<String, FileInfoResponse> getFileInfoResponse() {
        Map<String, FileInfoResponse> map = new HashMap<>();
        for (File file : getStoredFiles().values()) {
            map.put(file.getName(), new FileInfoResponse(file.getName(),
                    file.getContent(), file.getFileSize(), file.isTransferCompleted()));
        }
        return map;
    }

    public boolean supportsEntity(Entity entity) {
        return supportedEntities.contains(entity.getType());
    }

    public boolean isFileBeingTransferredTo(String fileName) {
        return getFile(fileName) == null ? false : getFile(fileName).isTransferCompleted();
    }

    public boolean isFileBeingTransferredFrom(String fileName) {
        return outgoingFiles.containsKey(fileName);
    }
}
