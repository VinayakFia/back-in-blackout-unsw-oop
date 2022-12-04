package unsw.move;

import unsw.entity.Entity;
import unsw.utils.Angle;
import unsw.utils.MathsHelper;

import java.util.function.Function;

public class MoveTeleportingSatellite implements Move {
    @Override
    public void execute(Function<Angle, Double> getPlanetHeight, Entity e) {
        double angularVelocity = e.getVelocity() / e.getHeight();
        Angle newPos = e.getPosition().add(Angle.fromRadians(angularVelocity * e.getDirection()));

        if (e.getDirection() == MathsHelper.ANTI_CLOCKWISE
                && newPos.toDegrees() >= 180
                && e.getPositionInDegrees() < 180) {
            e.setPosition(new Angle());
            e.setDirection(e.getDirection() * -1);
            return;
        }

        if (e.getDirection() == MathsHelper.CLOCKWISE
                && newPos.toDegrees() <= 180
                && e.getPositionInDegrees() > 180) {
            e.setPosition(new Angle());
            e.setDirection(e.getDirection() * -1);
            return;
        }

        e.setPosition(newPos);
    }
}
