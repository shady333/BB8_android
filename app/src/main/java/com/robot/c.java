package com.robot;

import android.bluetooth.BluetoothAdapter;

import com.orbotix.common.DLog;

class c implements Runnable {
    private static final long a = 1000L;
    private BluetoothAdapter b;
    private BluetoothAdapter.LeScanCallback c;
    private boolean d = false;

    public c(BluetoothAdapter var1, BluetoothAdapter.LeScanCallback var2) {
        this.b = var1;
        this.c = var2;
    }

    public void run() {
        for(this.d = true; this.d; this.b.stopLeScan(this.c)) {
            this.b.startLeScan(this.c);

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException var2) {
                DLog.v("Discovery runnable interrupted");
                return;
            }
        }

        DLog.v("Discovery runnable no longer running");
    }

    public void a() {
        DLog.v("Interrupted discovery runnable");
        this.d = false;
    }
}

