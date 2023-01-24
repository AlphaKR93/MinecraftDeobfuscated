/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity;

import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;

public interface TraceableEntity {
    @Nullable
    public Entity getOwner();
}