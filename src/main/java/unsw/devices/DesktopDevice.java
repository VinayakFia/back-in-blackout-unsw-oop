package unsw.devices;

import unsw.utils.Angle;

public class DesktopDevice extends Device {
    public DesktopDevice(String deviceId, Angle position, boolean isMoving) {
        super(position, deviceId, 200000, 20, isMoving);
    }

    @Override
    public String getType() {
        return "DesktopDevice";
    }
}
