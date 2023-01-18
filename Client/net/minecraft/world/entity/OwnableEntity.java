/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.UUID
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;

public interface OwnableEntity {
    @Nullable
    public UUID getOwnerUUID();

    @Nullable
    public Entity getOwner();
}