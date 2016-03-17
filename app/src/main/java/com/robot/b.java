package com.robot;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;

import com.orbotix.common.DLog;
import com.orbotix.common.DiscoveryAgentBase;
import com.orbotix.common.DiscoveryException;
import com.orbotix.common.DiscoveryExceptionCode;
import com.orbotix.common.Robot;
import com.orbotix.common.RobotChangedStateListener;
import com.orbotix.common.utilities.ApplicationLifecycleMonitor;

import com.orbotix.le.RadioDescriptor;
import com.orbotix.le.connectstrategy.ConnectStrategy;
import com.orbotix.le.connectstrategy.IfOneConnectStrategy;
import com.orbotix.le.connectstrategy.MultipleConditionConnectStrategy;
import com.orbotix.le.connectstrategy.ProximityConnectStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

class b extends DiscoveryAgentBase implements BluetoothAdapter.LeScanCallback {
    private static final Comparator<Robot> a = new Comparator() {
        @Override
        public int compare(Object lhs, Object rhs) {
            return 0;
        }

        public int a(Robot var1, Robot var2) {
            return Float.compare(var2.getSignalQuality(), var1.getSignalQuality());
        }
    };
    private static String[] b = new String[]{"SM-G800F"};
    private static b c;
    private BluetoothAdapter d;
    private ConnectStrategy e;
    private Set<Robot> f = new HashSet();
    private c g;
    private Handler h;
    private Handler i;
    private RadioDescriptor j;

    private b() {
        HandlerThread var1 = new HandlerThread("com.orbotix.dale.parse");
        var1.start();
        this.h = new Handler(var1.getLooper());
        HandlerThread var2 = new HandlerThread("com.orbotix.dale.notify");
        var2.start();
        this.i = new Handler(var2.getLooper());
        this.e = this.c();
        this.j = new RobotRadioDescriptor();
    }

    public static b a() {
        if(c == null) {
            c = new b();
        }

        return c;
    }

    public boolean startDiscovery(Context context) throws DiscoveryException {
        if(!super.startDiscovery(context)) {
            return false;
        } else {
            if(context instanceof Activity) {
                ApplicationLifecycleMonitor.getInstance().setListeningApplication(((Activity)context).getApplication());
            } else {
                DLog.w("Cannot start activity lifecycle monitor: Context used for discovery is not an activity.");
            }

            if(!context.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
                DLog.d("Device does not have BT 4.0");
                throw new DiscoveryException(DiscoveryExceptionCode.BluetoothLENotSupported);
            } else {
                BluetoothManager var2 = (BluetoothManager)context.getSystemService(Context.BLUETOOTH_SERVICE);
                this.d = var2.getAdapter();
                DLog.v("BluetoothManager: " + var2);
                DLog.v("Bluetooth Adapter: " + this.d);
                ArrayList var3 = new ArrayList(var2.getConnectedDevices(7));
                DLog.v("Found " + var3.size() + " preconnected devices");
                Iterator var4 = var3.iterator();

                while(var4.hasNext()) {
                    BluetoothDevice var5 = (BluetoothDevice)var4.next();
                    if(this.j.nameStartsWithValidPrefix(var5.getName())) {
                        RobotBB var6 = (RobotBB)this.b(var5);
                        if(!this.f.contains(var6)) {
                            DLog.v("Robot? " + var6 + " List: " + this.f.toString());
                            this.addRobot(var6);
                            this.connect(var6);
                            if(this.getConnectingOrConnectedRobotsCount() >= this.getMaxConnectedRobots()) {
                                DLog.v("Connecting a preconnected robot puts connected or connecting robots count at maximum. Stopping discovery.");
                                return false;
                            }
                        } else {
                            var6.disconnectForReals();
                        }
                    }
                }

                DLog.d(String.format("Bluetooth 4.0 Discovery requested: %d connected %d visible", new Object[]{Integer.valueOf(this.getConnectedRobots().size()), Integer.valueOf(super.getRobots().size())}));
                this.g = new c(this.d, this);
                (new Thread(this.g)).start();
                this.notifyListenersOfDiscoveryStart();
                return true;
            }
        }
    }

    public Robot startDiscovery(Context context, BluetoothDevice device) throws DiscoveryException {
        RobotBB var6 = null;

        if(!super.startDiscovery(context)) {
            return null;
        } else {
            if(context instanceof Activity) {
                ApplicationLifecycleMonitor.getInstance().setListeningApplication(((Activity)context).getApplication());
            } else {
                DLog.w("Cannot start activity lifecycle monitor: Context used for discovery is not an activity.");
            }

            if(!context.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
                DLog.d("Device does not have BT 4.0");
                throw new DiscoveryException(DiscoveryExceptionCode.BluetoothLENotSupported);
            } else {
//                BluetoothManager var2 = (BluetoothManager)context.getSystemService(Context.BLUETOOTH_SERVICE);
//                this.d = var2.getAdapter();
//                DLog.v("BluetoothManager: " + var2);
//                DLog.v("Bluetooth Adapter: " + this.d);
//                ArrayList var3 = new ArrayList(var2.getConnectedDevices(7));
//                DLog.v("Found " + var3.size() + " preconnected devices");
//                Iterator var4 = var3.iterator();
//
//                while(var4.hasNext()) {
//                    BluetoothDevice var5 = (BluetoothDevice)var4.next();
                    if(this.j.nameStartsWithValidPrefix(device.getName())) {
                        var6 = (RobotBB)this.b(device);
                        if(!this.f.contains(var6)) {
                            DLog.v("Robot? " + var6 + " List: " + this.f.toString());
                            this.addRobot(var6);
                            try{
                                this.connect(var6);
                                this.fireRobotStateChange(var6, RobotChangedStateListener.RobotChangedStateNotificationType.Connected);
                            }
                            catch (Exception e){
                                String a = e.getLocalizedMessage();
                                DLog.v(a);
                            }
                            if(this.getConnectingOrConnectedRobotsCount() >= this.getMaxConnectedRobots()) {
                                DLog.v("Connecting a preconnected robot puts connected or connecting robots count at maximum. Stopping discovery.");
                                //return null;
                            }
                        } else {
                            var6.disconnectForReals();
                        }
                    }
//                }

                DLog.d(String.format("Bluetooth 4.0 Discovery requested: %d connected %d visible", new Object[]{Integer.valueOf(this.getConnectedRobots().size()), Integer.valueOf(super.getRobots().size())}));
                //this.g = new c(this.d, this);
                //(new Thread(this.g)).start();
                //this.notifyListenersOfDiscoveryStart();
                return var6;
            }
        }
    }

    public void fireRobotStateChange(Robot robot, RobotChangedStateListener.RobotChangedStateNotificationType type) {
        DLog.v("State Change: " + type);
        if(type == RobotChangedStateListener.RobotChangedStateNotificationType.Disconnected) {
            DLog.v("Adding robot to blacklist " + robot);
            this.f.add(robot);
        } else if(type == RobotChangedStateListener.RobotChangedStateNotificationType.Connected && this.f.contains(robot)) {
            DLog.v("Removing robot from blacklist " + robot);
            this.f.remove(robot);
        }

        super.fireRobotStateChange(robot, type);
    }

    public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
        if(!this.a(device)) {
            this.h.post(new Runnable() {
                public void run() {
                    b.this.a(device, rssi, scanRecord);
                }
            });
        }
    }

    private boolean a(BluetoothDevice var1) {
        return !this.j.nameStartsWithValidPrefix(var1.getName());
    }

    private void a(BluetoothDevice var1, int var2, byte[] var3) {
        String ba = d.getRemoteDevice(var3).getAddress();
        List<String> var4 = new ArrayList<String>(Arrays.asList(ba.split(":")));
        if(var4.size() >= 3) {
//            byte[] var5 = ((d)var4.get(2)).c;
            byte[] var5 = var4.get(2).getBytes();

            byte var6 = var5[0];
            RobotBB var7 = (RobotBB)this.b(var1);
            var7.setAdPower(var6);
            var7.setRSSI(Integer.valueOf(var2));
            RobotBB var8 = (RobotBB)this.e.getRobotToConnectFromAvailableNodes(super.getRobots(), var7);
            if(var8 != null) {
                DLog.v(String.format("CONNECTING %s with signal quality %f and ad power %d", new Object[]{var8.getName(), Float.valueOf(var8.getSignalQuality()), Integer.valueOf(var6)}));
                this.connect(var8);
            }

            this.i.post(new Runnable() {
                public void run() {
                    b.this.updateAvailableRobots();
                }
            });
        }
    }

    public void stopDiscovery() {
        if(this.isDiscovering()) {
            super.stopDiscovery();
            this.g.a();
            this.h.removeCallbacksAndMessages((Object)null);
            this.notifyListenersOfDiscoveryStop();
        }
    }

    public synchronized void connect(Robot robot) {
        if(!(robot instanceof RobotBB)) {
            throw new IllegalArgumentException("DiscoveryAgentLE cannot connect to robots of type " + robot.getClass().getName());
        } else {
            DLog.i("Connect Request: " + robot.toString());
            if(this.getConnectingOrConnectedRobotsCount() < this.getMaxConnectedRobots()) {
                this.a((RobotBB)robot, Boolean.valueOf(false));
                if(this.getConnectingOrConnectedRobotsCount() + 1 >= this.getMaxConnectedRobots()) {
                    //this.stopDiscovery();
                }
            } else {
                DLog.i("Skipping connect request because max connected robots already connected");
            }

        }
    }

    void a(RobotBB var1, Boolean var2) {
        DLog.i(var1.getName() + " Bluetooth Low-Energy Connecting");
        var1.a(var2.booleanValue());
    }

    public List<Robot> getRobots() {
        this.sortRobotsUsingComparator(a);
        return super.getRobots();
    }

    public void disconnectAll() {
        List var1 = super.getRobots();
        Iterator var2 = var1.iterator();

        while(var2.hasNext()) {
            Robot var3 = (Robot)var2.next();
            if(var3.isConnected()) {
                var3.sleep();
            } else if(var3.isConnecting()) {
                var3.disconnect();
            }
        }

    }

    private Robot b(BluetoothDevice var1) {
        Iterator var2 = super.getRobots().iterator();

        RobotBB var4;
        do {
            if(!var2.hasNext()) {
                RobotBB var5 = new RobotBB(var1, new RobotRadioDescriptor(), this._mainThreadHandler);
                this.addRobot(var5);
                return var5;
            }

            Robot var3 = (Robot)var2.next();
            var4 = (RobotBB)var3;
        } while(!var4.getAddress().equals(var1.getAddress()));

        return var4;
    }

    public void a(RadioDescriptor var1) {
        this.j = var1;
    }

    public void a(ConnectStrategy var1) {
        this.e = var1;
    }

    public ConnectStrategy b() {
        return this.e;
    }

    private ConnectStrategy c() {
        String var1 = Build.MODEL;
        String[] var2 = b;
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String var5 = var2[var4];
            if(var5.equals(var1)) {
                DLog.w("This model does not support proximity connection, using fallback strategy");
                return new MultipleConditionConnectStrategy(new ConnectStrategy[]{new ProximityConnectStrategy(), new IfOneConnectStrategy()});
            }
        }

        return new ProximityConnectStrategy();
    }
}
