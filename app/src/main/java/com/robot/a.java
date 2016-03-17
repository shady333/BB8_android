package com.robot;

import android.bluetooth.BluetoothGattCharacteristic;

import com.orbotix.common.internal.DeviceCommand;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

class a {
    private final Queue<a.aa> a = new LinkedList();
    private a.b b;
    private final Object c = new Object();
    private boolean d;
    private a.aa e;
    private static final int f = 20;

    public a(a.b var1) {
        this.b = var1;
        this.d = false;
    }

    public void a() {
        Queue var1 = this.a;
        synchronized(this.a) {
            this.a.clear();
        }

        Object var6 = this.c;
        synchronized(this.c) {
            this.d = false;
        }
    }

    public void a(BluetoothGattCharacteristic var1, DeviceCommand var2) {
        byte[] var3 = var2.getPacket();
        int var4 = (int)Math.ceil((double)((float)var3.length / 20.0F));

        for(int var5 = 0; var5 < var4; ++var5) {
            int var6 = var5 * 20;
            int var7 = Math.min(20, var3.length - var6);
            int var8 = var6 + var7;
            byte[] var9 = Arrays.copyOfRange(var3, var6, var8);
            boolean var10 = var5 >= var4 - 1;
            int var11 = var10?2:1;
            Queue var12 = this.a;
            synchronized(this.a) {
                this.a.add(new a.aa(var1, var9, var11));
            }
        }

        this.c();
    }

    public void a(BluetoothGattCharacteristic var1, byte[] var2, int var3) {
        Queue var4 = this.a;
        synchronized(this.a) {
            this.a.add(new a.aa(var1, var2, var3));
        }

        this.c();
    }

    public void a(BluetoothGattCharacteristic var1, byte[] var2) {
        this.a(var1, var2, 2);
    }

    public void b() {
        Queue var1 = this.a;
        synchronized(this.a) {
            if(this.a.size() == 0) {
                Object var2 = this.c;
                synchronized(this.c) {
                    this.d = false;
                }

                return;
            }

            this.e = (a.aa)this.a.remove();
        }

        this.b.a(this.e.a, this.e.c, this.e.b);
    }

    private void c() {
        boolean var1 = false;
        Object var2 = this.c;
        synchronized(this.c) {
            if(!this.d) {
                this.d = true;
                var1 = true;
            }
        }

        if(var1) {
            this.b();
        }

    }

    private class aa {
        public BluetoothGattCharacteristic a;
        public int b;
        public byte[] c;

        public aa(BluetoothGattCharacteristic var2, byte[] var3, int var4) {
            this.a = var2;
            this.b = var4;
            this.c = var3;
        }
    }

    interface b {
        void a(BluetoothGattCharacteristic var1, byte[] var2, int var3);
    }
}
