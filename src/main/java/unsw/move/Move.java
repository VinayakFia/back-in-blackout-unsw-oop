package unsw.move;

import unsw.entity.Entity;
import unsw.utils.Angle;

import java.util.function.Function;

public interface Move {
    void execute(Function<Angle, Double> getPlanetHeight, Entity e);
}
