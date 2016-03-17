//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.robot;

import com.orbotix.le.RadioDescriptor;

import java.util.UUID;

public class RobotRadioDescriptor extends RadioDescriptor {
    private static final String a = "2B-";
    private static final String b = "1C-";
    private static final UUID c = UUID.fromString("22bb746f-2ba0-7554-2d6f-726568705327");
    private static final UUID d = UUID.fromString("22bb746F-2ba1-7554-2D6F-726568705327");
    private static final UUID e = UUID.fromString("22bb746F-2ba6-7554-2D6F-726568705327");
    private static final UUID f = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    private static final UUID g = UUID.fromString("00002A24-0000-1000-8000-00805f9b34fb");
    private static final UUID h = UUID.fromString("00002A25-0000-1000-8000-00805f9b34fb");
    private static final UUID i = UUID.fromString("00002A26-0000-1000-8000-00805f9b34fb");
    private UUID j;
    private UUID k;
    private UUID l;
    private UUID m;
    private UUID n;
    private UUID o;
    private UUID p;

    public static RobotRadioDescriptor getOllieDescriptor() {
        RobotRadioDescriptor var0 = new RobotRadioDescriptor();
        var0.setNamePrefixes(new String[]{"2B-"});
        return var0;
    }

    public static RobotRadioDescriptor getBBDescriptor() {
        RobotRadioDescriptor var0 = new RobotRadioDescriptor();
        var0.setNamePrefixes(new String[]{"BB-"});
        return var0;
    }

    public static RobotRadioDescriptor getWeBallDescriptor() {
        RobotRadioDescriptor var0 = new RobotRadioDescriptor();
        var0.setNamePrefixes(new String[]{"1C-"});
        return var0;
    }

    public RobotRadioDescriptor() {
        this.m = f;
        this.n = g;
        this.o = h;
        this.p = i;
        this.j = c;
        this.k = d;
        this.l = e;
        this._namePrefixes = new String[]{"2B-", "1C-", "BB-"};
        this._advertisedUUIDs = new UUID[]{this.j};
        this._requiredUUIDs = new UUID[]{this.j, this._uuidRadioService, f};
    }

    public UUID getUUIDRobotService() {
        return this.j;
    }

    public UUID getUUIDControlCharacteristic() {
        return this.k;
    }

    public UUID getUUIDResponseCharacteristic() {
        return this.l;
    }

    public UUID getUUIDDeviceInformationService() {
        return this.m;
    }

    public UUID getUUIDModelNumberCharacteristic() {
        return this.n;
    }

    public UUID getUUIDSerialNumberCharacteristic() {
        return this.o;
    }

    public UUID getUUIDRadioFirmwareVersionCharacteristic() {
        return this.p;
    }
}
