/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Exception
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Locale
 */
package com.mojang.realmsclient;

import java.util.Locale;

public enum Unit {
    B,
    KB,
    MB,
    GB;

    private static final int BASE_UNIT = 1024;

    public static Unit getLargest(long $$0) {
        if ($$0 < 1024L) {
            return B;
        }
        try {
            int $$1 = (int)(Math.log((double)$$0) / Math.log((double)1024.0));
            String $$2 = String.valueOf((char)"KMGTPE".charAt($$1 - 1));
            return Unit.valueOf($$2 + "B");
        }
        catch (Exception $$3) {
            return GB;
        }
    }

    public static double convertTo(long $$0, Unit $$1) {
        if ($$1 == B) {
            return $$0;
        }
        return (double)$$0 / Math.pow((double)1024.0, (double)$$1.ordinal());
    }

    public static String humanReadable(long $$0) {
        int $$1 = 1024;
        if ($$0 < 1024L) {
            return $$0 + " B";
        }
        int $$2 = (int)(Math.log((double)$$0) / Math.log((double)1024.0));
        String $$3 = "" + "KMGTPE".charAt($$2 - 1);
        return String.format((Locale)Locale.ROOT, (String)"%.1f %sB", (Object[])new Object[]{(double)$$0 / Math.pow((double)1024.0, (double)$$2), $$3});
    }

    public static String humanReadable(long $$0, Unit $$1) {
        return String.format((Locale)Locale.ROOT, (String)("%." + ($$1 == GB ? "1" : "0") + "f %s"), (Object[])new Object[]{Unit.convertTo($$0, $$1), $$1.name()});
    }
}