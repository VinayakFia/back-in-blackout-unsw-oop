package blackout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import unsw.blackout.BlackoutController;
import unsw.utils.Angle;

import java.util.Arrays;
import java.util.List;

import static blackout.TestHelpers.assertListAreEqualIgnoringOrder;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
public class Task2CommunicableEntitiesTests {
    @Test
    public void testEntitiesInRange() {
        // Task 2
        // Example from the specification
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createSatellite("Satellite2", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(315));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
        controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));
        controller.createDevice("DeviceD", "HandheldDevice", Angle.fromDegrees(180));
        controller.createSatellite("Satellite3", "StandardSatellite", 2000 + RADIUS_OF_JUPITER, Angle.fromDegrees(175));

        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceC", "Satellite2"), controller.communicableEntitiesInRange("Satellite1"));
        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceB", "DeviceC", "Satellite1"), controller.communicableEntitiesInRange("Satellite2"));
        assertListAreEqualIgnoringOrder(List.of("Satellite2"), controller.communicableEntitiesInRange("DeviceB"));

        assertListAreEqualIgnoringOrder(List.of("DeviceD"), controller.communicableEntitiesInRange("Satellite3"));
    }

    @Test
    public void testEntitiesInRangeWithRelayOvercomingRange() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("S1", "StandardSatellite", 160000 + RADIUS_OF_JUPITER, Angle.fromDegrees(0));
        controller.createDevice("D1", "LaptopDevice", Angle.fromDegrees(0));

        // S1 has a range of 150000 but is 160000 away from D1, hence no devices will be in range
        assertListAreEqualIgnoringOrder(List.of(), controller.communicableEntitiesInRange("S1"));

        controller.createSatellite("R1", "RelaySatellite", 100000 + RADIUS_OF_JUPITER, Angle.fromDegrees(0));

        assertListAreEqualIgnoringOrder(Arrays.asList("D1", "R1"), controller.communicableEntitiesInRange("S1"));
    }

    @Test
    public void testEntitiesInRangeWithRelayOvercomingOutOfSight() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("S1", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(0));
        controller.createDevice("D1", "LaptopDevice", Angle.fromDegrees(20));

        // Even though D1 is in S1's range, it will be blocking by the planet and not visible
        assertListAreEqualIgnoringOrder(List.of(), controller.communicableEntitiesInRange("S1"));

        controller.createSatellite("R1", "RelaySatellite", 50000 + RADIUS_OF_JUPITER, Angle.fromDegrees(10));

        assertListAreEqualIgnoringOrder(Arrays.asList("D1", "R1"), controller.communicableEntitiesInRange("S1"));
    }

    @Test
    public void testEntitiesInRangeWithMultipleRelaySatellites() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("S1", "StandardSatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(0));
        controller.createDevice("D1", "LaptopDevice", Angle.fromDegrees(180));

        // D1 is not visible to S1
        assertListAreEqualIgnoringOrder(List.of(), controller.communicableEntitiesInRange("S1"));

        controller.createSatellite("R1", "RelaySatellite", 50000 + RADIUS_OF_JUPITER, Angle.fromDegrees(66));

        assertListAreEqualIgnoringOrder(Arrays.asList("R1"), controller.communicableEntitiesInRange("S1"));

        controller.createSatellite("R2", "RelaySatellite", 50000 + RADIUS_OF_JUPITER, Angle.fromDegrees(132));

        assertListAreEqualIgnoringOrder(Arrays.asList("R1", "R2", "D1"), controller.communicableEntitiesInRange("S1"));
    }

    @Test
    public void testRangeOfEntities() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("R1", "TeleportingSatellite", 150000 + RADIUS_OF_JUPITER, Angle.fromDegrees(0));
        controller.createSatellite("R2", "TeleportingSatellite", 75000 + RADIUS_OF_JUPITER, Angle.fromDegrees(0));
        controller.createSatellite("R3", "TeleportingSatellite", 25000 + RADIUS_OF_JUPITER, Angle.fromDegrees(0));
        controller.createDevice("D1", "DesktopDevice", Angle.fromDegrees(0));
        controller.createDevice("D2", "LaptopDevice", Angle.fromDegrees(0));
        controller.createDevice("D3", "HandheldDevice", Angle.fromDegrees(0));

        System.out.println(controller.communicableEntitiesInRange("D1"));
        System.out.println(controller.communicableEntitiesInRange("D2"));
        System.out.println(controller.communicableEntitiesInRange("D3"));

        assertListAreEqualIgnoringOrder(Arrays.asList("R1", "R2", "R3"), controller.communicableEntitiesInRange("D1"));
        assertListAreEqualIgnoringOrder(Arrays.asList("R2", "R3"), controller.communicableEntitiesInRange("D2"));
        assertListAreEqualIgnoringOrder(Arrays.asList("R3"), controller.communicableEntitiesInRange("D3"));
    }

    @Test
    public void testRangeOfSatellites() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("R1", "StandardSatellite", 100000 + RADIUS_OF_JUPITER, Angle.fromDegrees(0));
        controller.createSatellite("R2", "StandardSatellite", 360000 + RADIUS_OF_JUPITER, Angle.fromDegrees(0));

        assertListAreEqualIgnoringOrder(List.of(), controller.communicableEntitiesInRange("R2"));

        controller.createSatellite("R3", "RelaySatellite", 510000 + RADIUS_OF_JUPITER, Angle.fromDegrees(0));

        assertListAreEqualIgnoringOrder(Arrays.asList("R3"), controller.communicableEntitiesInRange("R2"));
    }
}
