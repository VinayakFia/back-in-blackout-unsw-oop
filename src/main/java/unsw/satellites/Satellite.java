package unsw.satellites;

import unsw.entity.Entity;
import unsw.move.Move;
import unsw.utils.Angle;

import java.util.List;

public abstract class Satellite extends Entity {
    protected Satellite(int range, Angle position, String id, double height,
                        int direction, int velocity, List<String> supportedEntities, Move move,
                        double fileStorageLimit, double byteStorageLimit,
                        double byteInBandwidth, double byteOutBandwidth) {
        super(range, position, id, height, direction, true, velocity,
                supportedEntities, move, fileStorageLimit, byteStorageLimit,
                byteInBandwidth, byteOutBandwidth);
    }
}
