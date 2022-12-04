package unsw.satellites;

import unsw.file.File;
import unsw.move.MoveTeleportingSatellite;
import unsw.utils.Angle;
import unsw.utils.MathsHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class TeleportingSatellite extends Satellite {
    public TeleportingSatellite(String satelliteId, double height, Angle position) {
        super(200000, position, satelliteId, height, MathsHelper.ANTI_CLOCKWISE,
                1000, Arrays.asList("HandheldDevice", "LaptopDevice", "DesktopDevice",
                        "StandardSatellite", "RelaySatellite", "TeleportingSatellite"),
                new MoveTeleportingSatellite(), 200, 200, 15, 10);
    }

    @Override
    public void step(Function<Angle, Double> getPlanetHeight,
                     Function<String, List<String>> communicableEntitiesInRange,
                     BiFunction<String, String, Boolean> isFileBeingTransferredTo,
                     BiFunction<String, String, Boolean> isFileBeingTransferredFrom) {

        int initialDirection = getDirection();

        moveEntity(getPlanetHeight);

        // If the direction has changed the satellite has to have teleported
        if (initialDirection != getDirection()) {
            // Teleporting is receiver
            for (File file : getIncomingFiles().values()) {
                completeIncomingFileTransfer(new File(file.getName(),
                        file.getContentTransferring().replaceAll("t", "")));
            }
            setIncomingFiles(new HashMap<>());

            // Teleporting is sender
            setOutgoingFiles(new HashMap<>());
        }

        continueTransfer(communicableEntitiesInRange, isFileBeingTransferredTo, isFileBeingTransferredFrom);
    }

    @Override
    public String getType() {
        return "TeleportingSatellite";
    }
}
