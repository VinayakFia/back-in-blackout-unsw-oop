package unsw.move;

import unsw.entity.Entity;
import unsw.utils.Angle;
import unsw.utils.MathsHelper;

import java.util.function.Function;

public class MoveRelaySatellite implements Move {
    @Override
    public void execute(Function<Angle, Double> getPlanetHeight, Entity e) {
        double angularVelocity = e.getVelocity() / e.getHeight();
        if (e.getPositionInDegrees() <= 190
                && e.getPositionInDegrees() >= 140) {
            e.setPosition(e.getPosition().add(Angle.fromRadians(angularVelocity * e.getDirection())));
        } else {
            if (Math.abs(140 - e.getPositionInDegrees()) < Math.abs(190 - e.getPositionInDegrees())) {
                e.setDirection(MathsHelper.ANTI_CLOCKWISE);
            } else {
                e.setDirection(MathsHelper.CLOCKWISE);
            }
            e.setPosition(e.getPosition().add(Angle.fromRadians(angularVelocity * e.getDirection())));
        }
    }
}
