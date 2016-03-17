package com.robot;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;

import com.orbotix.common.Robot;
import com.orbotix.le.RobotRadioDescriptor;

/**
 * Created by Oleg_Dudar on 3/15/2016.
 */
public class BB8 extends com.orbotix.ConvenienceRobot {
    public BB8(Robot robot) {
        super(robot);
    }

    public void disconnect() {
        this._robot.sleep();
    }
}
