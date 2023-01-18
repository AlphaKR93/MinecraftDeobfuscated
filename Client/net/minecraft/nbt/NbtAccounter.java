/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.RuntimeException
 */
package net.minecraft.nbt;

import com.google.common.annotations.VisibleForTesting;

public class NbtAccounter {
    public static final NbtAccounter UNLIMITED = new NbtAccounter(0L){

        @Override
        public void accountBytes(long $$0) {
        }
    };
    private final long quota;
    private long usage;

    public NbtAccounter(long $$0) {
        this.quota = $$0;
    }

    public void accountBytes(long $$0) {
        this.usage += $$0;
        if (this.usage > this.quota) {
            throw new RuntimeException("Tried to read NBT tag that was too big; tried to allocate: " + this.usage + "bytes where max allowed: " + this.quota);
        }
    }

    @VisibleForTesting
    public long getUsage() {
        return this.usage;
    }
}