/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.UUID
 *  java.util.stream.Stream
 */
package net.minecraft.world.level.entity;

import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import net.minecraft.world.phys.AABB;

public interface EntityAccess {
    public int getId();

    public UUID getUUID();

    public BlockPos blockPosition();

    public AABB getBoundingBox();

    public void setLevelCallback(EntityInLevelCallback var1);

    public Stream<? extends EntityAccess> getSelfAndPassengers();

    public Stream<? extends EntityAccess> getPassengersAndSelf();

    public void setRemoved(Entity.RemovalReason var1);

    public boolean shouldBeSaved();

    public boolean isAlwaysTicking();
}