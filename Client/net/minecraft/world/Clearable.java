/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  javax.annotation.Nullable
 */
package net.minecraft.world;

import javax.annotation.Nullable;

public interface Clearable {
    public void clearContent();

    public static void tryClear(@Nullable Object $$0) {
        if ($$0 instanceof Clearable) {
            ((Clearable)$$0).clearContent();
        }
    }
}