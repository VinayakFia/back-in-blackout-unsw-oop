package unsw.planet;

import unsw.utils.Angle;
import unsw.utils.MathsHelper;

import java.util.ArrayList;

public class Planet {
    private final ArrayList<Slope> slopes = new ArrayList<>();

    public void addSlope(int startAngle, int endAngle, int gradient) {
        slopes.add(new Slope(startAngle, endAngle, gradient, getHeight(Angle.fromDegrees(startAngle))));
    }

    public double getHeight(Angle angle) {
        double height = MathsHelper.RADIUS_OF_JUPITER;

        for (Slope slope : slopes) {
            if (slope.getHeightAt(angle) > height) {
                height = slope.getHeightAt(angle);
            }
        }

        return Math.max(MathsHelper.RADIUS_OF_JUPITER, height);
    }
}
