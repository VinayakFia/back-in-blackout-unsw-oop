package unsw.blackout;

import unsw.devices.DesktopDevice;
import unsw.devices.Device;
import unsw.devices.HandheldDevice;
import unsw.devices.LaptopDevice;
import unsw.entity.Entity;
import unsw.file.File;
import unsw.planet.Planet;
import unsw.response.models.EntityInfoResponse;
import unsw.satellites.RelaySatellite;
import unsw.satellites.Satellite;
import unsw.satellites.StandardSatellite;
import unsw.satellites.TeleportingSatellite;
import unsw.utils.Angle;
import unsw.utils.MathsHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BlackoutController {
    private final Map<String, Device> devices = new HashMap<>();
    private final Map<String, Satellite> satellites = new HashMap<>();
    private final Planet planet = new Planet();

    public void createDevice(String deviceId, String type, Angle position) {
        addDevice(deviceId, type, position, false);
    }

    public void removeDevice(String deviceId) {
        devices.remove(deviceId);
    }

    public void createSatellite(String satelliteId, String type, double height, Angle position) {
        switch (type) {
            case "StandardSatellite":
                satellites.put(satelliteId, new StandardSatellite(satelliteId, height, position));
                break;
            case "RelaySatellite":
                satellites.put(satelliteId, new RelaySatellite(satelliteId, height, position));
                break;
            case "TeleportingSatellite":
                satellites.put(satelliteId, new TeleportingSatellite(satelliteId, height, position));
                break;
            default:
        }
    }

    public void removeSatellite(String satelliteId) {
        satellites.remove(satelliteId);
    }

    public List<String> listDeviceIds() {
        return devices.values().stream().map(Device::getId).collect(Collectors.toList());
    }

    public List<String> listSatelliteIds() {
        return satellites.values().stream().map(Satellite::getId).collect(Collectors.toList());
    }

    public void addFileToDevice(String deviceId, String fileName, String content) {
        Device device = devices.get(deviceId);
        assert device != null;
        device.addFile(fileName, content);
    }

    public EntityInfoResponse getInfo(String id) {
        Entity entity = getEntity(id);
        assert entity != null;
        return new EntityInfoResponse(id, entity.getPosition(),
                entity.getHeight(), entity.getType(),
                entity.getFileInfoResponse());
    }

    public void simulate() {
        for (Entity entity : getEntities()) {
            entity.step(this::getPlanetHeight,
                    this::communicableEntitiesInRange,
                    this::isFileBeingTransferredTo,
                    this::isFileBeingTransferredFrom);
        }
    }

    /**
     * Simulate for the specified number of minutes.
     * You shouldn't need to modify this function.
     */
    public void simulate(int numberOfMinutes) {
        for (int i = 0; i < numberOfMinutes; i++) {
            simulate();
        }
    }

    public List<String> communicableEntitiesInRange(String id) {
        List<String> ids = getCommunicableEntitiesInRange(id, new ArrayList<>());
        Entity entity = getEntity(id);
        // Remove Self from List
        ids.removeIf(x -> x.equals(id));
        // Remove Unsupported Entities from List
        ids.removeIf(x -> !entity.supportsEntity(getEntity(x)));
        return ids;
    }

    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        Entity from = getEntity(fromId);
        Entity to = getEntity(toId);
        assert to != null;
        assert from != null;

        if (from instanceof Device && to instanceof Device) {
            return;
        }

        File file = from.getFile(fileName);

        if (file == null) {
            throw new FileTransferException.VirtualFileNotFoundException(
                    fileName + " not found on " + from.getId()
            );
        }

        if (to.getFile(fileName) != null) {
            throw new FileTransferException.VirtualFileAlreadyExistsException(
                    fileName + " already exists on " + to.getId()
            );
        }

        file = new File(file, fromId, from.getType(), toId,
                (int) Math.min(from.getByteOutBandwidth(), to.getByteInBandwidth()));

        from.sendFile(file, from.getId());
        to.receiveFile(file, to.getId());
    }

    public void createDevice(String deviceId, String type, Angle position, boolean isMoving) {
        addDevice(deviceId, type, position, isMoving);
    }

    public void createSlope(int startAngle, int endAngle, int gradient) {
        planet.addSlope(startAngle, endAngle, gradient);
    }

    private List<String> getCommunicableEntitiesInRange(String id, List<String> idsVisited) {
        return findCommunicableEntitiesInRange(id, idsVisited);
    }

    private List<String> findCommunicableEntitiesInRange(String id, List<String> idsVisited) {
        List<String> ids = new ArrayList<>();

        List<Entity> entities = getEntities();
        Entity entity = getEntity(id);
        assert entity != null;

        for (Entity other : entities) {
            if (other.equals(entity)) continue;
            // If a id has been visited ignore it and move on to the next id
            // as this would create infinite loops
            if (idsVisited.contains(other.getId())) continue;

            double distance = MathsHelper.getDistance(
                    entity.getHeight(),
                    entity.getPosition(),
                    other.getHeight(),
                    other.getPosition());
            boolean inRange = distance <= entity.getRange() && distance <= other.getRange();
            boolean visible = MathsHelper.isVisible(
                    entity.getHeight(),
                    entity.getPosition(),
                    other.getHeight(),
                    other.getPosition());

            if (inRange && visible && entity.supportsEntity(other) && !ids.contains(other.getId())) {
                ids.add(other.getId());
                idsVisited.add(other.getId());
                if (other instanceof RelaySatellite) {
                    ids.addAll(getCommunicableEntitiesInRange(other.getId(), idsVisited));
                }
            }
        }

        return ids;
    }

    /**
     * checks whether a file is still being transferred to
     * a particular entity
     * @param fileName
     * @param toId
     * @return
     */
    private boolean isFileBeingTransferredTo(String fileName, String toId) {
        Entity entity = getEntity(toId);
        return entity == null ? false : entity.isFileBeingTransferredTo(fileName);
    }

    /**
     * checks whether a file is still being transferred from
     * a particular entity
     * @param fileName
     * @param fromId
     * @return
     */
    private boolean isFileBeingTransferredFrom(String fileName, String fromId) {
        Entity entity = getEntity(fromId);
        return entity == null ? false : entity.isFileBeingTransferredFrom(fileName);
    }

    private double getPlanetHeight(Angle angle) {
        return planet.getHeight(angle);
    }

    private void addDevice(String deviceId, String type, Angle position, boolean isMoving) {
        switch (type) {
            case "HandheldDevice":
                devices.put(deviceId, new HandheldDevice(deviceId, position, isMoving));
                break;
            case "DesktopDevice":
                devices.put(deviceId, new DesktopDevice(deviceId, position, isMoving));
                break;
            case "LaptopDevice":
                devices.put(deviceId, new LaptopDevice(deviceId, position, isMoving));
                break;
            default:
        }
    }

    private List<Entity> getEntities() {
        return Stream.concat(devices.values().stream(), satellites.values().stream())
                .collect(Collectors.toList());
    }

    private Entity getEntity(String id) {
        for (Entity entity : getEntities()) {
            if (entity.getId().equals(id)) return entity;
        }
        return null;
    }
}
