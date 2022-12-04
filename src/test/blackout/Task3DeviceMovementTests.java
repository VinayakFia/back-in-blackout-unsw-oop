package blackout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import unsw.blackout.BlackoutController;
import unsw.utils.Angle;
import unsw.utils.MathsHelper;

import static org.junit.jupiter.api.Assertions.*;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
public class Task3DeviceMovementTests {
    @Test
    public void testBasicMovement() {
        BlackoutController controller = new BlackoutController();

        controller.createDevice("D1", "HandheldDevice", Angle.fromDegrees(0), true);

        controller.simulate(100);
        assertEquals(1, controller.getInfo("D1").getPosition().compareTo(Angle.fromDegrees(0)));
    }

    @Test
    public void movementWithSlope() {
        BlackoutController controller = new BlackoutController();

        controller.createDevice("D1", "HandheldDevice", Angle.fromDegrees(0), true);
        controller.createSlope(5, 50, 300);

        controller.simulate(1200);
        assertTrue(controller.getInfo("D1").getHeight() > RADIUS_OF_JUPITER);
    }

    @Test
    public void movementWithMultipleSlopesLayered() {
        BlackoutController controller = new BlackoutController();

        controller.createDevice("D1", "HandheldDevice", Angle.fromDegrees(359), true);
        controller.createSlope(0, 50, 300);
        controller.createSlope(5, 50, 800);

        controller.simulate(200);
        double height = controller.getInfo("D1").getHeight();
        Angle position = controller.getInfo("D1").getPosition();
        assertTrue(height > 300 * position.toDegrees() + RADIUS_OF_JUPITER);
    }
}
