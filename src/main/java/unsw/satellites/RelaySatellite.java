package unsw.satellites;

import unsw.move.MoveRelaySatellite;
import unsw.utils.Angle;
import unsw.utils.MathsHelper;

import java.util.Arrays;

public class RelaySatellite extends Satellite {
    public RelaySatellite(String satelliteId, double height, Angle position) {
        super(300000, position, satelliteId, height, MathsHelper.CLOCKWISE,
                1500, Arrays.asList("HandheldDevice", "LaptopDevice", "DesktopDevice",
                        "StandardSatellite", "RelaySatellite", "TeleportingSatellite"),
                new MoveRelaySatellite(), 0, 0, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    @Override
    public String getType() {
        return "RelaySatellite";
    }
}
