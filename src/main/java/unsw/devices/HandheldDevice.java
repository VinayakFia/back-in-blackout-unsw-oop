package unsw.devices;

import unsw.utils.Angle;

public class HandheldDevice extends Device {
    public HandheldDevice(String deviceId, Angle position, boolean isMoving) {
        super(position, deviceId, 50000, 50, isMoving);
    }

    @Override
    public String getType() {
        return "HandheldDevice";
    }
}
