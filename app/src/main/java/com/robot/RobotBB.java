package com.robot;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;

import com.orbotix.command.SleepCommand;
import com.orbotix.common.DLog;
import com.orbotix.common.DiscoveryAgentProxy;
import com.orbotix.common.RobotBase;
import com.orbotix.common.internal.BootloaderCommandId;
import com.orbotix.common.internal.CoreCommandId;
import com.orbotix.common.internal.DeviceCommand;
import com.orbotix.common.internal.DeviceId;
import com.orbotix.common.internal.MainProcessorState;
import com.orbotix.common.internal.RadioConnectionState;
import com.orbotix.common.internal.RadioLink;
import com.orbotix.common.utilities.ApplicationLifecycleMonitor;
import com.orbotix.le.DiscoveryAgentLE;
import com.orbotix.le.LeLinkInterface;
import com.orbotix.le.RobotLeRadioAckDelegate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Oleg_Dudar on 3/16/2016.
 */
public class RobotBB extends RobotBase implements com.orbotix.le.LeLinkRadioACKListener {

    private e a;
    private float b = -96.0F;
    private Integer c = Integer.valueOf(0);
    private boolean d = false;
    private final List<RobotLeRadioAckDelegate> e = new ArrayList();

    public RobotBB(BluetoothDevice bleDevice, RobotRadioDescriptor radioDescriptor, Handler mainThreadHandler) {
        super(DiscoveryAgentLE.getInstance(), mainThreadHandler);
        this.a = new e(bleDevice, this, radioDescriptor, this._mainThreadHandler);
    }

    public void addRadioAckListener(RobotLeRadioAckDelegate listener) {
        if(this.e.contains(listener)) {
            DLog.w("%s is already a radio ack listener for %s", new Object[]{listener, this.getName()});
        } else {
            this.e.add(listener);
        }
    }

    public void removeRadioAckListener(RobotLeRadioAckDelegate listener) {
        if(this.e.contains(listener)) {
            this.e.remove(listener);
        }

    }

    public String toString() {
        return this.a != null?String.format("<RobotLE %s %2.0f%% %s isConnecting: %s isConnected: %s>", new Object[]{this.getName(), Float.valueOf(this.getSignalQuality()), this.a.toString(), Boolean.valueOf(this.isConnecting()), Boolean.valueOf(this.isConnected())}):String.format("<RobotLE (no link)>", new Object[0]);
    }

    public long getAckLatency() {
        return this.a.getAckLatency().longValue();
    }

    public void setDeveloperMode(boolean enabled) {
        this.a.c(enabled);
    }

    protected void setAdPower(int txpower) {
        this.c = Integer.valueOf(txpower);
    }

    protected Integer adjustedRSSI() {
        return Integer.valueOf(this.a.d().intValue() - this.c.intValue());
    }

    public void setRSSI(Integer rssi) {
        this.a.a(rssi);
    }

    public float getSignalQuality() {
        int var1 = this.c.intValue() == -10?48:30;
        float var2 = 1.0F / -(Math.abs(this.b) - (float)var1);
        float var3 = (1.0F - (Math.abs(this.b) + (float)this.a.d().intValue())) * var2;
        if(var3 > 1.0F) {
            var3 = 1.0F;
        }

        return var3 * 100.0F;
    }

    public boolean isConnected() {
        return this.a.isConnected();
    }

    public boolean isConnecting() {
        return this.a.isConnecting();
    }

    public boolean isOnline() {
        return this.a.getMpState() == MainProcessorState.InMainApp;
    }

    public void sendCommand(DeviceCommand command) {
        this.a(command, false);
    }

    public void streamCommand(DeviceCommand command) {
        this.a(command, true);
    }

    private void a(DeviceCommand var1, boolean var2) {
        if(this.d && this.a(var1)) {
            this.d = false;
            this.a.sendCommand(var1);
            if(ApplicationLifecycleMonitor.getInstance().applicationIsBackground()) {
                DLog.i("Sleep requested while in bootloader, sleeping robot.");
                this.sleep();
            } else {
                DLog.i("Sleep was requested, but the application is active on jump to main. Not sleeping.");
            }

        } else if(this.a.getMpState() == MainProcessorState.InBootloader && this.b(var1)) {
            DLog.i("Postponing sleep command since robot is in bootloader");
            this.d = true;
        } else {
            this.a.sendCommand(var1);
        }
    }

    private void setMaintainBackgroundConnection(boolean maintainBackgroundConnection) {
        this.a.d(maintainBackgroundConnection);
    }

    private boolean getMaintainBackgroundConnection() {
        return this.a.f();
    }

    private boolean a(DeviceCommand var1) {
        return var1.getDeviceId() == DeviceId.BOOTLOADER.getValue() && var1.getCommandId() == BootloaderCommandId.LEAVE_BOOTLOADER.getValue();
    }

    private boolean b(DeviceCommand var1) {
        return var1.getDeviceId() == DeviceId.CORE.getValue() && var1.getCommandId() == CoreCommandId.SLEEP.getValue();
    }

    void a(boolean var1) {
        this.a.a(var1);
    }

    public void disconnect() {
        DLog.i("RobotLE.disconnect()");
        this.sleep();
    }

    public void disconnectForReals() {
        this.clearResponseListeners();
        this.a.close();
    }

    public void sleep() {
        this.sleep(SleepCommand.SleepType.LOW_POWER);
    }

    public void sleep(SleepCommand.SleepType mode) {
//        switch(null.a[mode.ordinal()]) {
//            case 1:
//                this.sendCommand(new SleepCommand(0, 0));
//                break;
//            case 2:
                this.sleep(SleepCommand.SleepType.NORMAL);
                if(this.a.getMpState() != MainProcessorState.InBootloader) {
                    this.a.a((short)2);
                }
//                break;
//            case 3:
//                if(this.a.getRfState() == RadioConnectionState.Connected) {
//                    DLog.v("Link is online, deep sleeping.");
//                    this.a.b();
//                } else {
//                    DLog.w("Link is offline, deep sleep not possible");
//                }
//        }

    }

    protected RadioLink getRadioLink() {
        return this.a;
    }

    public LeLinkInterface getLeLink() {
        return this.a;
    }

    public void didACK() {
        this._mainThreadHandler.post(new Runnable() {
            public void run() {
                Iterator var1 = RobotBB.this.e.iterator();

                while(var1.hasNext()) {
                    RobotLeRadioAckDelegate var2 = (RobotLeRadioAckDelegate)var1.next();
                    var2.handleACK(RobotBB.this);
                }

            }
        });
    }

    public void handleLinkDidWake() {
        super.handleLinkDidWake();
        this.a.b(true);
    }

    public void handleLinkDidSleep() {
        super.handleLinkDidSleep();
        this.a.b(false);
    }
}
