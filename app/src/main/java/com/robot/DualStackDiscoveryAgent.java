package com.robot;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import com.orbotix.classic.DiscoveryAgentClassic;
import com.orbotix.classic.RobotClassic;
import com.orbotix.common.DiscoveryAgentEventListener;
import com.orbotix.common.DiscoveryException;
import com.orbotix.common.DiscoveryExceptionCode;
import com.orbotix.common.Robot;
import com.orbotix.common.RobotChangedStateListener;
import com.orbotix.common.utilities.SynchronousSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DualStackDiscoveryAgent implements DiscoveryAgent, RobotChangedStateListener {
    private static final Object a = new Object();
    private static DualStackDiscoveryAgent b;
    private Context c;
    private DiscoveryAgentLE d = DiscoveryAgentLE.getInstance();
    private DiscoveryAgentClassic e;
    private boolean f;
    private int g = 1;
    private SynchronousSet<DiscoveryAgentEventListener> h = new SynchronousSet();
    private SynchronousSet<RobotChangedStateListener> i = new SynchronousSet();
    private SynchronousSet<DiscoveryStateChangedListener> j = new SynchronousSet();

    public static DualStackDiscoveryAgent getInstance() {
        Object var0 = a;
        synchronized(a) {
            if(b == null) {
                b = new DualStackDiscoveryAgent();
            }
        }

        return b;
    }

    private DualStackDiscoveryAgent() {
        this.d.addRobotStateListener(this);
        this.e = DiscoveryAgentClassic.getInstance();
        this.e.addRobotStateListener(this);
    }

    private void a(boolean var1) {
        this.f = var1;
        Iterator var2 = this.j.iterator();

        while(var2.hasNext()) {
            DiscoveryStateChangedListener var3 = (DiscoveryStateChangedListener)var2.next();
            if(this.f) {
                var3.onDiscoveryDidStart(this);
            } else {
                var3.onDiscoveryDidStop(this);
            }
        }

    }

    private int a() {
        return this.d.getConnectedRobots().size() + this.d.getConnectingRobots().size() + this.e.getConnectingRobots().size() + this.e.getConnectedRobots().size();
    }

    public void disconnectAll() {
        this.e.disconnectAll();
        this.d.disconnectAll();
    }

    public void addDiscoveryListener(DiscoveryAgentEventListener listener) {
        this.h.add(listener);
    }

    public void removeDiscoveryListener(DiscoveryAgentEventListener listener) {
        this.h.remove(listener);
    }

    public void addRobotStateListener(RobotChangedStateListener listener) {
        this.i.add(listener);
    }

    public void removeRobotStateListener(RobotChangedStateListener listener) {
        this.i.remove(listener);
    }

    @Override
    public void addDiscoveryChangedStateListener(com.orbotix.common.DiscoveryStateChangedListener var1) {

    }

    @Override
    public void removeDiscoveryChangedStateListener(com.orbotix.common.DiscoveryStateChangedListener var1) {

    }

    public void addDiscoveryChangedStateListener(DiscoveryStateChangedListener listener) {
        this.j.add(listener);
    }

    public void removeDiscoveryChangedStateListener(DiscoveryStateChangedListener listener) {
        this.j.remove(listener);
    }

    public Context getContext() {
        return this.c;
    }

    public List<Robot> getRobots() {
        ArrayList var1 = new ArrayList();
        var1.addAll(this.d.getRobots());
        var1.addAll(this.e.getRobots());
        return var1;
    }

    public List<Robot> getConnectingRobots() {
        ArrayList var1 = new ArrayList();
        var1.addAll(this.d.getConnectingRobots());
        var1.addAll(this.e.getConnectingRobots());
        return var1;
    }

    public List<Robot> getConnectedRobots() {
        ArrayList var1 = new ArrayList();
        var1.addAll(this.d.getConnectedRobots());
        var1.addAll(this.e.getConnectedRobots());
        return var1;
    }

    public List<Robot> getOnlineRobots() {
        ArrayList var1 = new ArrayList();
        var1.addAll(this.d.getOnlineRobots());
        var1.addAll(this.e.getOnlineRobots());
        return var1;
    }

    public boolean startDiscovery(Context context) throws DiscoveryException {
        if(context == null) {
            throw new DiscoveryException(DiscoveryExceptionCode.NullContext);
        } else {
            this.c = context;
            boolean var2 = this.d.startDiscovery(this.c);
            boolean var3 = this.e.startDiscovery(this.c);
            if(var2 || var3) {
                this.a(true);
            }

            return var2 && var3;
        }
    }

    @Override
    public Robot startDiscovery(Context context, BluetoothDevice dev) throws DiscoveryException {
        if(context == null) {
            throw new DiscoveryException(DiscoveryExceptionCode.NullContext);
        } else {
            this.c = context;
            Robot var2 = this.d.startDiscovery(this.c, dev);
            if(var2 != null) {
                this.a(true);
            }

            return var2;
        }
    }

    public void stopDiscovery() {
        this.a(false);
        this.d.stopDiscovery();
        this.e.stopDiscovery();
    }

    public void connect(Robot robot) {
        if(robot instanceof RobotBB) {
            this.d.connect((RobotBB)robot);
        } else if(robot instanceof RobotClassic) {
            this.e.connect((RobotClassic)robot);
        }

    }

    public boolean isDiscovering() {
        return this.f;
    }

    public int getMaxConnectedRobots() {
        return this.g;
    }

    public void setMaxConnectedRobots(int maxConnectedRobots) {
        this.g = maxConnectedRobots;
    }

    public void handleRobotChangedState(Robot robot, RobotChangedStateNotificationType type) {
        int var3 = this.a();
        if(type == RobotChangedStateNotificationType.Connecting) {
            if(var3 == this.g) {
                this.stopDiscovery();
            }
        } else if(type == RobotChangedStateNotificationType.Online && var3 > this.g) {
            if(robot instanceof RobotBB) {
                robot.sleep();
            } else if(robot instanceof RobotClassic) {
                robot.disconnect();
            }
        }

        Iterator var4 = this.i.iterator();

        while(var4.hasNext()) {
            RobotChangedStateListener var5 = (RobotChangedStateListener)var4.next();
            if(var5 != null) {
                var5.handleRobotChangedState(robot, type);
            }
        }

    }
}