package unsw.planet;

import unsw.utils.Angle;
import unsw.utils.MathsHelper;

public class Slope {
    private final Angle startAngle;
    private final Angle endAngle;
    private final int gradient;
    private final double startHeight;

    public Slope(int startAngle, int endAngle, int gradient, double startHeight) {
        this.startAngle = Angle.fromDegrees(startAngle);
        this.endAngle = Angle.fromDegrees(endAngle);
        this.gradient = gradient;
        this.startHeight = startHeight;
    }

    public double getHeightAt(Angle angle) {
        if (containsAngle(angle)) {
            double angleDiff = Math.abs(startAngle.toDegrees() - angle.toDegrees());
            return startHeight + angleDiff * gradient;
        }
        return MathsHelper.RADIUS_OF_JUPITER;
    }

    // https://stackoverflow.com/questions/11406189/determine-if-angle-lies-between-2-other-angles
    private boolean containsAngle(Angle angle) {
        double angle2 = endAngle.toDegrees();
        double angle1 = startAngle.toDegrees();
        double target = angle.toDegrees();
        // make the angle from angle1 to angle2 to be <= 180 degrees
        double rAngle = ((angle2 - angle1) % 360 + 360) % 360;
        if (rAngle >= 180) {
            double temp = angle1;
            angle1 = angle2;
            angle2 = temp;
        }

        // check if it passes through zero
        if (angle1 <= angle2)
            return target >= angle1 && target <= angle2;
        else
            return target >= angle1 || target <= angle2;
    }
}
