/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.world.level.block.state.properties;

import net.minecraft.util.StringRepresentable;

public enum Tilt implements StringRepresentable
{
    NONE("none", true),
    UNSTABLE("unstable", false),
    PARTIAL("partial", true),
    FULL("full", true);

    private final String name;
    private final boolean causesVibration;

    private Tilt(String $$0, boolean $$1) {
        this.name = $$0;
        this.causesVibration = $$1;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public boolean causesVibration() {
        return this.causesVibration;
    }
}