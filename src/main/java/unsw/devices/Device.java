package unsw.devices;

import unsw.entity.Entity;
import unsw.move.MoveDevice;
import unsw.utils.Angle;
import unsw.utils.MathsHelper;

import java.util.Arrays;

public abstract class Device extends Entity {
    protected Device(Angle position, String id, int range, int velocity, boolean isMoving) {
        super(range, position, id, MathsHelper.RADIUS_OF_JUPITER,
                MathsHelper.ANTI_CLOCKWISE, isMoving, velocity,
                Arrays.asList("StandardSatellite", "RelaySatellite", "TeleportingSatellite"),
                new MoveDevice(), Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }
}
