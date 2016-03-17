package com.robot;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Build;

import com.orbotix.common.DiscoveryAgentEventListener;
import com.orbotix.common.DiscoveryException;
import com.orbotix.common.DiscoveryStateChangedListener;
import com.orbotix.common.Robot;
import com.orbotix.common.RobotChangedStateListener;
import com.orbotix.le.DiscoveryAgentLEProxy;
import com.orbotix.le.RadioDescriptor;
import com.orbotix.le.connectstrategy.ConnectStrategy;

import java.util.List;


public class DiscoveryAgentLE implements DiscoveryAgentLEProxy {
    private static DiscoveryAgentLE a;
    private b b1 = b.a();

    private DiscoveryAgentLE() {
    }

    public static DiscoveryAgentLE getInstance() {
        if(Build.VERSION.SDK_INT < 18) {
            throw new UnsupportedOperationException();
        } else {
            if(a == null) {
                a = new DiscoveryAgentLE();
            }

            return a;
        }
    }

    public void setRadioDescriptor(RadioDescriptor descriptor) {
        this.b1.a(descriptor);
    }

    public void setConnectStrategy(ConnectStrategy connectStrategy) {
        this.b1.a(connectStrategy);
    }

    public void fireRobotStateChange(Robot robot, RobotChangedStateListener.RobotChangedStateNotificationType type) {
        this.b1.fireRobotStateChange(robot, type);
    }

    public boolean startDiscovery(Context context) throws DiscoveryException {
        return this.b1.startDiscovery(context);
    }

    public Robot startDiscovery(Context context, BluetoothDevice dev) throws DiscoveryException {
        return this.b1.startDiscovery(context, dev);
    }

    public void stopDiscovery() {
        this.b1.stopDiscovery();
    }

    public List<Robot> getRobots() {
        return this.b1.getRobots();
    }

    public List<Robot> getConnectingRobots() {
        return this.b1.getConnectingRobots();
    }

    public List<Robot> getConnectedRobots() {
        return this.b1.getConnectedRobots();
    }

    public List<Robot> getOnlineRobots() {
        return this.b1.getOnlineRobots();
    }

    public void connect(Robot robot) {
        this.b1.connect(robot);
    }

    public void disconnectAll() {
        this.b1.disconnectAll();
    }

    public void addDiscoveryListener(DiscoveryAgentEventListener listener) {
        this.b1.addDiscoveryListener(listener);
    }

    public void removeDiscoveryListener(DiscoveryAgentEventListener listener) {
        this.b1.removeDiscoveryListener(listener);
    }

    public void addRobotStateListener(RobotChangedStateListener listener) {
        this.b1.addRobotStateListener(listener);
    }

    public void removeRobotStateListener(RobotChangedStateListener listener) {
        this.b1.addRobotStateListener(listener);
    }

    public void addDiscoveryChangedStateListener(DiscoveryStateChangedListener listener) {
        this.b1.addDiscoveryChangedStateListener(listener);
    }

    public void removeDiscoveryChangedStateListener(DiscoveryStateChangedListener listener) {
        this.b1.removeDiscoveryChangedStateListener(listener);
    }

    public Context getContext() {
        return this.b1.getContext();
    }

    public int getMaxConnectedRobots() {
        return this.b1.getMaxConnectedRobots();
    }

    public void setMaxConnectedRobots(int maxConnectedRobots) {
        this.b1.setMaxConnectedRobots(maxConnectedRobots);
    }

    public boolean isDiscovering() {
        return this.b1.isDiscovering();
    }

    public ConnectStrategy getConnectStrategy() {
        return this.b1.b();
    }
}