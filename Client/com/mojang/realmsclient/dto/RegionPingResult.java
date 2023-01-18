/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.annotations.SerializedName
 *  java.lang.Float
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Locale
 */
package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import com.mojang.realmsclient.dto.ReflectionBasedSerialization;
import com.mojang.realmsclient.dto.ValueObject;
import java.util.Locale;

public class RegionPingResult
extends ValueObject
implements ReflectionBasedSerialization {
    @SerializedName(value="regionName")
    private final String regionName;
    @SerializedName(value="ping")
    private final int ping;

    public RegionPingResult(String $$0, int $$1) {
        this.regionName = $$0;
        this.ping = $$1;
    }

    public int ping() {
        return this.ping;
    }

    @Override
    public String toString() {
        return String.format((Locale)Locale.ROOT, (String)"%s --> %.2f ms", (Object[])new Object[]{this.regionName, Float.valueOf((float)this.ping)});
    }
}