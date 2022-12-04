package unsw.move;

import unsw.entity.Entity;
import unsw.utils.Angle;

import java.util.function.Function;

public class MoveStandardSatellite implements Move {
    @Override
    public void execute(Function<Angle, Double> getPlanetHeight, Entity e) {
        double angularVelocity = e.getVelocity() / e.getHeight();
        e.setPosition(e.getPosition().add(Angle.fromRadians(angularVelocity * e.getDirection())));
    }
}
