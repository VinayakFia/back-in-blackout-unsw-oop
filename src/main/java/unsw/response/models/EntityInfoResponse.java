package unsw.response.models;

import unsw.utils.Angle;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a generic response for entity information.
 *
 * @author Braedon Wooding
 * @note You can't store this class in BlackoutController and should just create
 * it when needed, using this will make you lose marks for design
 * modelling. (it's an okay start, but there is much more work to be
 * done).
 * <p>
 * You shouldn't modify this file.
 */
public final class EntityInfoResponse {
    private final String id;
    // We store it in positionRadians, to avoid weird JSON outputs.
    private final Angle position;
    private final double height;
    private final String type;
    private final Map<String, FileInfoResponse> files;

    public EntityInfoResponse(
            String id, Angle position, double height, String type, Map<String, FileInfoResponse> files) {
        this.id = id;
        this.position = position;
        this.type = type;
        this.files = files;
        this.height = height;
    }

    public EntityInfoResponse(String id, Angle position, double height, String type) {
        this(id, position, height, type, new HashMap<>());
    }

    public Map<String, FileInfoResponse> getFiles() {
        return files;
    }

    public String getType() {
        return type;
    }

    public Angle getPosition() {
        return position;
    }

    public String getDeviceId() {
        return id;
    }

    public double getHeight() {
        return height;
    }

    @Override
    public String toString() {
        return "EntityInfoResponse [files=" + files + ", height=" + height + ", id=" + id + ", positionRadians="
                + position.toRadians() + ", positionDegrees=" + position.toDegrees() + ", type=" + type + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        EntityInfoResponse other = (EntityInfoResponse) obj;
        return Objects.equals(files, other.files) && Objects.equals(id, other.id)
                && Math.abs(position.toRadians() - other.position.toRadians()) < 0.001
                && Math.abs(height - other.height) < 0.001
                && Objects.equals(type, other.type);
    }
}
