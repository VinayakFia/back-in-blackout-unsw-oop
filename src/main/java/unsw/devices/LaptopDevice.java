package unsw.devices;

import unsw.utils.Angle;

public class LaptopDevice extends Device {
    public LaptopDevice(String deviceId, Angle position, boolean isMoving) {
        super(position, deviceId, 100000, 30, isMoving);
    }

    @Override
    public String getType() {
        return "LaptopDevice";
    }
}
