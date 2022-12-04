package blackout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import unsw.blackout.BlackoutController;
import unsw.blackout.FileTransferException;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
public class Task2FileTransferTests {
    @Test
    public void testFileTransferFileNotFoundException() {
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "StandardSatellite", 5000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
        controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));

        String msg = "Hey";
        controller.addFileToDevice("DeviceC", "FileAlpha", msg);
        assertThrows(FileTransferException.VirtualFileNotFoundException.class, () -> controller.sendFile("NonExistentFile", "DeviceC", "Satellite1"));
    }

    @Test
    public void testFileTransferFileAlreadyExistsException() {
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "StandardSatellite", 5000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
        controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));

        String msg = "Hey";
        controller.addFileToDevice("DeviceC", "FileAlpha", msg);
        assertThrows(FileTransferException.VirtualFileNotFoundException.class, () -> controller.sendFile("NonExistentFile", "DeviceC", "Satellite1"));

        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
        assertEquals(
                new FileInfoResponse("FileAlpha", "", msg.length(), false),
                controller.getInfo("Satellite1").getFiles().get("FileAlpha")
        );

        controller.simulate(msg.length() * 2);
        assertThrows(FileTransferException.VirtualFileAlreadyExistsException.class, () -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
    }

    @Test
    public void testFileTransferBandwidthException() {
        BlackoutController controller = new BlackoutController();

        controller.createDevice("D1", "LaptopDevice", Angle.fromDegrees(310));
        controller.createSatellite("S1", "TeleportingSatellite", 5000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));

        String msg = "1";
        for (int i = 0; i < 15; i++) {
            controller.addFileToDevice("D1", String.valueOf(i), msg);
            int finalI = i;
            assertDoesNotThrow(() -> controller.sendFile(String.valueOf(finalI), "D1", "S1"));
        }

        controller.addFileToDevice("D1", "NoBandwidth", msg);
        assertThrows(FileTransferException.VirtualFileNoBandwidthException.class, () -> controller.sendFile("NoBandwidth", "D1", "S1"));
    }

    @Test
    public void testFileTransferNoStorageException() {
        BlackoutController controller = new BlackoutController();

        controller.createDevice("D1", "LaptopDevice", Angle.fromDegrees(310));
        controller.createSatellite("S1", "TeleportingSatellite", 5000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));

        // Message with length 20
        String msg = "01234567890123456789";
        for (int i = 0; i < 10; i++) {
            controller.addFileToDevice("D1", String.valueOf(i), msg);
            int finalI = i;
            assertDoesNotThrow(() -> controller.sendFile(String.valueOf(finalI), "D1", "S1"));
        }

        controller.addFileToDevice("D1", "NoStorage", msg);
        assertThrows(FileTransferException.VirtualFileNoStorageSpaceException.class, () -> controller.sendFile("NoStorage", "D1", "S1"));
    }

    @Test
    public void testGeneralTransferSuccessfully() {
        // Task 2
        // Example from the specification
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "StandardSatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
        controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));

        String msg = "Hey";
        controller.addFileToDevice("DeviceC", "FileAlpha", msg);
        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
        assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false), controller.getInfo("Satellite1").getFiles().get("FileAlpha"));

        controller.simulate(msg.length() * 2);
        assertEquals(new FileInfoResponse("FileAlpha", msg, msg.length(), true), controller.getInfo("Satellite1").getFiles().get("FileAlpha"));

        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "Satellite1", "DeviceB"));
        assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false), controller.getInfo("DeviceB").getFiles().get("FileAlpha"));

        controller.simulate(msg.length());
        assertEquals(new FileInfoResponse("FileAlpha", msg, msg.length(), true), controller.getInfo("DeviceB").getFiles().get("FileAlpha"));

        // Hints for further testing:
        // - What about checking about the progress of the message half way through?
        // - Device/s get out of range of satellite
        // ... and so on.
    }

    @Test
    public void testSatelliteGoesOutOfRangeDuringTransfer() {
        BlackoutController controller = new BlackoutController();

        controller.createDevice("D1", "LaptopDevice", Angle.fromDegrees(0));
        controller.createSatellite("S1", "StandardSatellite", 149000 + RADIUS_OF_JUPITER, Angle.fromDegrees(0));

        controller.addFileToDevice("D1", "F1", "10-1234567");
        assertDoesNotThrow(() -> controller.sendFile("F1", "D1", "S1"));
        assertEquals(new FileInfoResponse("F1", "", 10, false), controller.getInfo("S1").getFiles().get("F1"));

        // Satellite is not out of range so transfer should stop
        controller.simulate(5);
        assertNull(controller.getInfo("S1").getFiles().get("F1"));
    }

    @Test
    public void testFileTransferWithRelaySatellite() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("S1", "StandardSatellite", 160000 + RADIUS_OF_JUPITER, Angle.fromDegrees(0));
        controller.createSatellite("R1", "RelaySatellite", 100000 + RADIUS_OF_JUPITER, Angle.fromDegrees(0));
        controller.createDevice("D1", "LaptopDevice", Angle.fromDegrees(0));

        // S1 has a range of 150000 but is 160000 away from D1, hence no devices will be in range
        controller.addFileToDevice("D1", "F1", "10-1234567");
        assertDoesNotThrow(() -> controller.sendFile("F1", "D1", "S1"));
        assertEquals(new FileInfoResponse("F1", "", 10, false), controller.getInfo("S1").getFiles().get("F1"));
    }

    @Test
    public void testFileTransferWithMultipleRelaySatellites() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("S1", "StandardSatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(0));
        controller.createSatellite("R1", "RelaySatellite", 50000 + RADIUS_OF_JUPITER, Angle.fromDegrees(66));
        controller.createSatellite("R2", "RelaySatellite", 50000 + RADIUS_OF_JUPITER, Angle.fromDegrees(132));
        controller.createDevice("D1", "LaptopDevice", Angle.fromDegrees(180));

        controller.addFileToDevice("D1", "F1", "10-1234567");
        assertDoesNotThrow(() -> controller.sendFile("F1", "D1", "S1"));
        assertEquals(new FileInfoResponse("F1", "", 10, false), controller.getInfo("S1").getFiles().get("F1"));
    }

    @Test
    public void testFileTransferBetweenSatellites() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("S1", "StandardSatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(0));
        controller.createDevice("D1", "LaptopDevice", Angle.fromDegrees(0));

        // Transfer to Satellite 1
        controller.addFileToDevice("D1", "F1", "10-1234567");
        assertDoesNotThrow(() -> controller.sendFile("F1", "D1", "S1"));
        assertEquals(new FileInfoResponse("F1", "", 10, false), controller.getInfo("S1").getFiles().get("F1"));

        controller.simulate(20);
        controller.createSatellite("S2", "StandardSatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(50));
        assertDoesNotThrow(() -> controller.sendFile("F1", "S1", "S2"));
        assertEquals(new FileInfoResponse("F1", "", 10, false), controller.getInfo("S2").getFiles().get("F1"));
    }

    @Test
    public void testFileTransferExceptionWhileFileNotFullyTransferred() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("S1", "StandardSatellite", 120000 + RADIUS_OF_JUPITER, Angle.fromDegrees(0));
        controller.createDevice("D1", "LaptopDevice", Angle.fromDegrees(0));

        // Transfer to Satellite 1
        controller.addFileToDevice("D1", "F1", "10-1234567");
        assertDoesNotThrow(() -> controller.sendFile("F1", "D1", "S1"));
        assertEquals(new FileInfoResponse("F1", "", 10, false), controller.getInfo("S1").getFiles().get("F1"));

        controller.createSatellite("S2", "StandardSatellite", 120000 + RADIUS_OF_JUPITER, Angle.fromDegrees(50));
        assertThrows(FileTransferException.VirtualFileNotFoundException.class, () -> controller.sendFile("F1", "S1", "S2"));
    }

    @Test
    public void testFileTransferWithTeleportingSatellitesRemovingT() {
        BlackoutController controller = new BlackoutController();

        controller.createDevice("D1", "LaptopDevice", Angle.fromDegrees(180));
        String msg = "tooTtooTtooTtooTtooTtooTtooTtooTtooTtooTtooTtooTtooTtooTtooTtooTtooTtooTtooTtooTtooT";
        controller.addFileToDevice("D1", "F1", msg);

        controller.createSatellite("S1", "TeleportingSatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(178));
        assertDoesNotThrow(() -> controller.sendFile("F1", "D1", "S1"));

        controller.simulate(15);
        assertEquals(new FileInfoResponse("F1", msg.replace("t", ""), msg.replace("t", "").length(), true),
                controller.getInfo("S1").getFiles().get("F1"));
    }

    @Test
    public void testFileTransferFromTeleportingSatelliteToSatellite() {
        BlackoutController controller = new BlackoutController();

        controller.createDevice("D1", "LaptopDevice", Angle.fromDegrees(180));
        String msg = "tooTtooTtooTtooTtooTtooTtooTtooTtooTtooTtooT";
        controller.addFileToDevice("D1", "F1", msg);

        controller.createSatellite("S1", "TeleportingSatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(160));
        assertDoesNotThrow(() -> controller.sendFile("F1", "D1", "S1"));

        controller.simulate(15);
        assertEquals(new FileInfoResponse("F1", msg, msg.length(), true), controller.getInfo("S1").getFiles().get("F1"));

        controller.createSatellite("S2", "StandardSatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(180));
        assertDoesNotThrow(() -> controller.sendFile("F1", "S1", "S2"));

        controller.simulate(15);
        assertEquals(new FileInfoResponse("F1", msg.replace("t", ""), msg.replace("t", "").length(), true),
                controller.getInfo("S2").getFiles().get("F1"));
    }
}
