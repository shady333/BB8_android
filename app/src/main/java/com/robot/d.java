package com.robot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class d {
    private static final String d = "OBX-LeAdRecord";
    public int a = -1;
    public int b = -1;
    public byte[] c = null;

    public d(int var1, int var2, byte[] var3) {
        this.a = var1;
        this.b = var2;
        this.c = var3;
    }

    public static List<d> a(byte[] var0) {
        ArrayList var1 = new ArrayList();

        byte var3;
        for(int var2 = 0; var2 < var0.length; var2 += var3) {
            var3 = var0[var2++];
            if(var3 == 0) {
                break;
            }

            byte var4 = var0[var2];
            if(var4 == 0) {
                break;
            }

            byte[] var5 = Arrays.copyOfRange(var0, var2 + 1, var2 + var3);
            var1.add(new d(var3, var4, var5));
        }

        return var1;
    }
}
