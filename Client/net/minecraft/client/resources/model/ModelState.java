/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.client.resources.model;

import com.mojang.math.Transformation;

public interface ModelState {
    default public Transformation getRotation() {
        return Transformation.identity();
    }

    default public boolean isUvLocked() {
        return false;
    }
}