package unsw.file;

public class File {
    private final String name;
    private String content;
    private final int fileSize;
    private boolean transferCompleted;
    private String contentTransferring;
    private String from = "";
    private String fromType = "";
    private String to = "";
    private int transferRate = 0;
    private int maxTransferRate = 0;
    private int bytesTransferred = 0;

    public File(String name, String content) {
        this.name = name;
        this.content = content;
        this.fileSize = content.length();
        this.transferCompleted = true;
    }

    public File(File file, String from, String fromType, String to, int maxTransferRate) {
        this.name = file.getName();
        this.content = "";
        this.contentTransferring = file.getContent();
        this.fileSize = file.getContent().length();
        this.transferCompleted = false;
        this.from = from;
        this.fromType = fromType;
        this.to = to;
        this.maxTransferRate = maxTransferRate;
    }

    public void stepFileTransfer() {
        int end = Math.min(bytesTransferred + transferRate, getFileSize());
        content += contentTransferring.substring(bytesTransferred, end);
        bytesTransferred += transferRate;
        if (end >= fileSize) {
            transferCompleted = true;
        }
    }

    public String getName() {
        return name;
    }

    public int getFileSize() {
        return fileSize;
    }

    public String getContent() {
        return content;
    }

    public String getContentTransferring() {
        return contentTransferring;
    }

    public boolean isTransferCompleted() {
        return transferCompleted;
    }

    public String getFromType() {
        return fromType;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getMaxTransferRate() {
        return maxTransferRate;
    }

    public void setTransferRate(int transferRate) {
        this.transferRate = transferRate;
    }
}
