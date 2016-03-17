package com.robot;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.orbotix.common.DiscoveryAgentEventListener;
import com.orbotix.common.DiscoveryException;
import com.orbotix.common.DiscoveryStateChangedListener;
import com.orbotix.common.Robot;
import com.orbotix.common.RobotChangedStateListener;

import java.util.List;

public interface DiscoveryAgent {
    boolean startDiscovery(Context var1) throws DiscoveryException;
    Robot startDiscovery(Context var1, BluetoothDevice dev) throws DiscoveryException;

    void stopDiscovery();

    List<Robot> getRobots();

    List<Robot> getConnectingRobots();

    List<Robot> getConnectedRobots();

    List<Robot> getOnlineRobots();

    void connect(Robot var1);

    void disconnectAll();

    void addDiscoveryListener(DiscoveryAgentEventListener var1);

    void removeDiscoveryListener(DiscoveryAgentEventListener var1);

    void addRobotStateListener(RobotChangedStateListener var1);

    void removeRobotStateListener(RobotChangedStateListener var1);

    void addDiscoveryChangedStateListener(DiscoveryStateChangedListener var1);

    void removeDiscoveryChangedStateListener(DiscoveryStateChangedListener var1);

    Context getContext();

    int getMaxConnectedRobots();

    void setMaxConnectedRobots(int var1);

    boolean isDiscovering();
}
