/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.nbt;

import net.minecraft.nbt.Tag;

public abstract class NumericTag
implements Tag {
    protected NumericTag() {
    }

    public abstract long getAsLong();

    public abstract int getAsInt();

    public abstract short getAsShort();

    public abstract byte getAsByte();

    public abstract double getAsDouble();

    public abstract float getAsFloat();

    public abstract Number getAsNumber();

    @Override
    public String toString() {
        return this.getAsString();
    }
}