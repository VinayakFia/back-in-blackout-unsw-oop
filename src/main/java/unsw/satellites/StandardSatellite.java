package unsw.satellites;

import unsw.move.MoveStandardSatellite;
import unsw.utils.Angle;
import unsw.utils.MathsHelper;

import java.util.Arrays;

public class StandardSatellite extends Satellite {
    public StandardSatellite(String satelliteId, double height, Angle position) {
        super(150000, position, satelliteId, height, MathsHelper.CLOCKWISE,
                2500, Arrays.asList("HandheldDevice", "LaptopDevice",
                        "StandardSatellite", "RelaySatellite", "TeleportingSatellite"),
                new MoveStandardSatellite(), 3, 80, 1, 1);
    }

    @Override
    public String getType() {
        return "StandardSatellite";
    }
}
