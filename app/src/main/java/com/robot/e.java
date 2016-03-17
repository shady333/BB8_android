package com.robot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Handler;

import com.orbotix.command.SleepCommand;
import com.orbotix.common.DLog;
import com.orbotix.common.internal.AdHocCommand;
import com.orbotix.common.internal.AsyncMessage;
import com.orbotix.common.internal.DeviceCommand;
import com.orbotix.common.internal.MainProcessorSession;
import com.orbotix.common.internal.MainProcessorState;
import com.orbotix.common.internal.RadioConnectionState;
import com.orbotix.common.internal.RadioLink;
import com.orbotix.common.utilities.binary.ByteUtil;
import com.orbotix.le.DiscoveryAgentLE;
import com.orbotix.le.LeLinkInterface;
import com.orbotix.le.LeLinkRadioACKListener;
import com.orbotix.le.RobotLE;
//import com.orbotix.le.*;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class e extends RadioLink implements MainProcessorSession.MainProcessorSessionDelegate, LeLinkInterface {
    private static final boolean a = false;
    private static final boolean b = false;
    private static final String c = "011i3";
    private static final int d = 2;
    private static final int e = 0;
    private static final int f = 133;
    private BluetoothGattCharacteristic g;
    private BluetoothGattCharacteristic h;
    private BluetoothGattCharacteristic i;
    private BluetoothGattCharacteristic j;
    private BluetoothGattCharacteristic k;
    private BluetoothGattCharacteristic l;
    private BluetoothGattCharacteristic m;
    private BluetoothGatt n;
    private int o;
    private Integer p = Integer.valueOf(-98);
    private ExecutorService q = Executors.newSingleThreadExecutor();
    private final a r;
    private final LeLinkRadioACKListener s;
    private String t;
    private String u;
    private boolean v = false;
    private RobotRadioDescriptor w;
    private boolean x = false;
    private final Handler y;
    private boolean z = false;
    private boolean A = true;
    private BluetoothGattCallback B = new BluetoothGattCallback() {
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            DLog.v(e.this.getName() + " Connection State Changed to " + newState + " status: " + status);
            if(newState == 2) {
                if(e.this.n != null) {
                    e.this.n.disconnect();
                    e.this.n.close();
                }

                e.this.n = gatt;
                e.this.r.a();
                e.this.n.discoverServices();
            } else if(newState == 0) {
                boolean var4 = false;
                if(status == 133) {
                    DLog.e("GATT could not be attached. Retrying.");
                    var4 = true;
                } else {
                    e.this.n.close();
                }

                if(e.this.getRfState() == RadioConnectionState.Connecting) {
                    DLog.v("Failed to connect - reconnecting " + e.this.getName());
                    e.this.handleConnectionFailed();
                    var4 = true;
                } else if(e.this.getRfState() == RadioConnectionState.Connected) {
                    DLog.v("Connection Dropped - reconnecting " + e.this.getName());
                    var4 = true;
                } else if(e.this.v) {
                    DLog.v("Maintain in background is set - reconnecting " + e.this.getName());
                    var4 = true;
                } else {
                    DLog.v("Not in a state for reconnection");
                }

                e.this.handleConnectionClosed();
                if(var4) {
                    e.this.open();
                }
            }

        }

        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if(!e.this.n.equals(gatt)) {
                DLog.e("Mismatch GATT - see vves");
            } else {
                DLog.v(String.format("onServicesDiscovered count=%d status=%d", new Object[]{Integer.valueOf(gatt.getServices().size()), Integer.valueOf(status)}));
                BluetoothGattService var3 = gatt.getService(e.this.w.getUUIDRadioService());

                assert var3 != null;

                e.this.k = var3.getCharacteristic(e.this.w.getUUIDTxPowerCharacteristic());
                e.this.i = var3.getCharacteristic(e.this.w.getUUIDAntiDOSCharacteristic());
                e.this.l = var3.getCharacteristic(e.this.w.getUUIDAntiDOSTimeoutCharactertistic());
                e.this.m = var3.getCharacteristic(e.this.w.getUUIDDeepSleepCharacteristic());
                e.this.handleConnectionSucceeded();
                BluetoothGattService var4 = gatt.getService(e.this.w.getUUIDRobotService());

                assert var4 != null;

                e.this.h = var4.getCharacteristic(e.this.w.getUUIDResponseCharacteristic());
                e.this.j = var3.getCharacteristic(e.this.w.getUUIDWakecharacteristic());
                e.this.g = var4.getCharacteristic(e.this.w.getUUIDControlCharacteristic());
                e.this.g();
            }
        }

        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if(e.this.z) {
                e.this.s.didACK();
                e.this.z = false;
                if(e.this.g.equals(characteristic)) {
                    e.this.handleCommandWritten();
                }
            }

            if(e.this.j.equals(characteristic)) {
                DLog.v("Wrote wake");
                e.this.h();
            }

            if(e.this.k.equals(characteristic)) {
                DLog.v("TX Power Set");
                if(e.this.getMpState() == MainProcessorState.Offline) {
                    e.this.c();
                }
            }

            if(e.this.i.equals(characteristic)) {
                DLog.v("DOS ACK OK");
                e.this.a((short)7);
            }

            e.this.r.b();
        }

        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            if(e.this.h.equals(characteristic)) {
                byte[] var3 = characteristic.getValue();
                if(var3.length == 1 && !e.this.x) {
                    DLog.w("");
                    DLog.w("WARNING: Single byte value will be dropped %s possibly breaking all the things!", new Object[]{ByteUtil.byteArrayToHex(var3)});
                    DLog.w("");
                } else {
                    e.this.processRawData(var3);
                }

                e.this.x = true;
            }

        }

        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if(characteristic.getUuid().equals(e.this.w.getUUIDModelNumberCharacteristic())) {
                e.this.t = characteristic.getStringValue(0);
                gatt.readCharacteristic(characteristic.getService().getCharacteristic(e.this.w.getUUIDRadioFirmwareVersionCharacteristic()));
            } else if(characteristic.getUuid().equals(e.this.w.getUUIDRadioFirmwareVersionCharacteristic())) {
                e.this.u = characteristic.getStringValue(0);
                gatt.readCharacteristic(characteristic.getService().getCharacteristic(e.this.w.getUUIDSerialNumberCharacteristic()));
            } else if(characteristic.getUuid().equals(e.this.w.getUUIDSerialNumberCharacteristic())) {
                String var4 = characteristic.getStringValue(0);
                if(!BluetoothAdapter.checkBluetoothAddress(var4)) {
                    DLog.e("Detected garbage serial number, refreshing device...");
                    e.this.a(gatt);
                    return;
                }

                e.this.setSerialNumber(characteristic.getStringValue(0));
                DLog.v("Read all aux characteristics, jumping to main");
                e.this.i();
            } else {
                DLog.w("Unhandled characteristic read: " + characteristic.getUuid().toString());
            }

        }
    };

    public e(BluetoothDevice var1, RobotBB var2, RobotRadioDescriptor var3, Handler var4) {
        super(var1, var2, var2);
        this.w = var3;
        this.s = var2;
        DLog.i(String.format("Creating LeLink for %s", new Object[]{var1.getAddress()}));
        a.b var5 = new a.b() {
            public void a(final BluetoothGattCharacteristic var1, final byte[] var2, final int var3) {
                e.this.q.execute(new Runnable() {
                    public void run() {
                        if(e.this.n != null && var1 != null) {
                            var1.setWriteType(var3);
                            var1.setValue(var2);
                            e.this.z = var3 == 2;
                            e.this.n.writeCharacteristic(var1);
                        }
                    }
                });
            }
        };
        this.r = new a(var5);
        this.y = var4;
    }

    public void open() {
        this.a(false);
    }

    void a(final boolean var1) {
        super.open();
        if(BluetoothAdapter.getDefaultAdapter().getState() != 10 && BluetoothAdapter.getDefaultAdapter().getState() != 13) {
            if(this.isConnected() && this.a()) {
                DLog.v("Radio link is already connected, waking main");
                this.c();
            } else {
                this.setMpState(MainProcessorState.Offline);
                this.handleConnectionInitiated();
                this.y.post(new Runnable() {
                    public void run() {
                        e.this.getDevice().connectGatt(DiscoveryAgentLE.getInstance().getContext(), var1, e.this.B);
                    }
                });
            }
        }
    }

    public void b(boolean var1) {
        this.A = var1;
    }

    public boolean a() {
        return this.A;
    }

    public void close() {
        this.handleConnectionEnding();
        if(this.n != null) {
            DLog.i("gatt.disconnect() requested");
            this.n.disconnect();
        }

    }

    public String getRadioFirmwareRevision() {
        return this.u;
    }

    public void a(short var1) {
        if(this.k != null && this.n != null) {
            DLog.i("Writing TX Power: " + var1);
            byte[] var2 = new byte[]{(byte)var1};
            this.r.a(this.k, var2);
        } else {
            DLog.e("TX power characteristic or gatt is null and cannot write power");
        }
    }

    public void b() {
        if(this.getMpState() == MainProcessorState.InMainApp) {
            this.sendCommand(new SleepCommand('\uffff', 0));
        } else {
            this.r.a(this.m, "011i3".getBytes());
        }

    }

    protected void sendCommandInternal(DeviceCommand command) {
        this.r.a(this.g, command);
    }

    private void a(BluetoothGatt var1) {
        try {
            Method var2 = var1.getClass().getMethod("refresh", new Class[0]);
            if(var2 != null) {
                var2.invoke(var1, new Object[0]);
                DLog.v("Refresh completed successfully!");
            }
        } catch (Exception var3) {
            DLog.e("An exception occurred while refreshing device");
        }

        this.n.discoverServices();
    }

    private void g() {
        this.r.a(this.i, "011i3".getBytes());
    }

    protected void c() {
        byte[] var1 = new byte[]{(byte)1};
        this.setMpState(MainProcessorState.PowerOnRequested);
        this.r.a(this.j, var1);
    }

    private void h() {
        DLog.v("Enable Notify on Response Characteristic");
        if(this.n.setCharacteristicNotification(this.h, true)) {
            this.b(this.n);
        }

    }

    private void i() {
        this.setMpState(MainProcessorState.PoweredOn);

        try {
            Thread.sleep(100L);
        } catch (InterruptedException var2) {
            var2.printStackTrace();
        }

        this.sendCommand(new AdHocCommand(1, 4));
    }

    private void b(BluetoothGatt var1) {
        BluetoothGattService var2 = var1.getService(this.w.getUUIDDeviceInformationService());
        BluetoothGattCharacteristic var3 = var2.getCharacteristic(this.w.getUUIDModelNumberCharacteristic());
        var1.readCharacteristic(var3);
    }

    public void handleAsyncMessageCreated(AsyncMessage asyncMessage) {
        if(asyncMessage.getType() == AsyncMessage.Type.DidSleepAsyncMessage) {
            if(this.v) {
                DLog.v("Skipping async sleep disconnect: maintain background connection is set");
            } else {
                this.close();
            }
        }

        super.handleAsyncMessageCreated(asyncMessage);
    }

    public String toString() {
        return "<LeLink " + this.getAddress() + " rf" + this.getRfState() + " mp" + this.getMpState();
    }

    public void c(boolean var1) {
        byte[] var2 = new byte[]{(byte)10};
        if(var1) {
            var2[0] = 0;
        }

        DLog.w("Setting Developer Mode");
        this.r.a(this.l, var2);
    }

    public Integer d() {
        return this.p;
    }

    public void a(Integer var1) {
        if(var1.intValue() <= -5) {
            if(this.p == null) {
                this.p = var1;
                this.o = this.p.intValue();
            }

            this.p = Integer.valueOf((this.o + this.p.intValue() + var1.intValue()) / 3);
            this.o = var1.intValue();
        }
    }

    public void d(boolean var1) {
        this.v = var1;
    }

    public boolean e() {
        return this.v;
    }

    public void sendRaw(byte[] data) {
        this.r.a(this.g, data);
    }

    public boolean f() {
        return this.v;
    }

    protected void handleSleepResponse() {
    }
}
