/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  javax.annotation.concurrent.Immutable
 */
package net.minecraft.world;

import javax.annotation.concurrent.Immutable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

@Immutable
public class LockCode {
    public static final LockCode NO_LOCK = new LockCode("");
    public static final String TAG_LOCK = "Lock";
    private final String key;

    public LockCode(String $$0) {
        this.key = $$0;
    }

    public boolean unlocksWith(ItemStack $$0) {
        return this.key.isEmpty() || !$$0.isEmpty() && $$0.hasCustomHoverName() && this.key.equals((Object)$$0.getHoverName().getString());
    }

    public void addToTag(CompoundTag $$0) {
        if (!this.key.isEmpty()) {
            $$0.putString(TAG_LOCK, this.key);
        }
    }

    public static LockCode fromTag(CompoundTag $$0) {
        if ($$0.contains(TAG_LOCK, 8)) {
            return new LockCode($$0.getString(TAG_LOCK));
        }
        return NO_LOCK;
    }
}