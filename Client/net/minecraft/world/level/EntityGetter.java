/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 *  java.lang.Class
 *  java.lang.Object
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.UUID
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface EntityGetter {
    public List<Entity> getEntities(@Nullable Entity var1, AABB var2, Predicate<? super Entity> var3);

    public <T extends Entity> List<T> getEntities(EntityTypeTest<Entity, T> var1, AABB var2, Predicate<? super T> var3);

    default public <T extends Entity> List<T> getEntitiesOfClass(Class<T> $$0, AABB $$1, Predicate<? super T> $$2) {
        return this.getEntities(EntityTypeTest.forClass($$0), $$1, $$2);
    }

    public List<? extends Player> players();

    default public List<Entity> getEntities(@Nullable Entity $$0, AABB $$1) {
        return this.getEntities($$0, $$1, EntitySelector.NO_SPECTATORS);
    }

    default public boolean isUnobstructed(@Nullable Entity $$0, VoxelShape $$1) {
        if ($$1.isEmpty()) {
            return true;
        }
        for (Entity $$2 : this.getEntities($$0, $$1.bounds())) {
            if ($$2.isRemoved() || !$$2.blocksBuilding || $$0 != null && $$2.isPassengerOfSameVehicle($$0) || !Shapes.joinIsNotEmpty($$1, Shapes.create($$2.getBoundingBox()), BooleanOp.AND)) continue;
            return false;
        }
        return true;
    }

    default public <T extends Entity> List<T> getEntitiesOfClass(Class<T> $$0, AABB $$1) {
        return this.getEntitiesOfClass($$0, $$1, EntitySelector.NO_SPECTATORS);
    }

    default public List<VoxelShape> getEntityCollisions(@Nullable Entity $$0, AABB $$1) {
        if ($$1.getSize() < 1.0E-7) {
            return List.of();
        }
        Predicate $$2 = $$0 == null ? EntitySelector.CAN_BE_COLLIDED_WITH : EntitySelector.NO_SPECTATORS.and($$0::canCollideWith);
        List<Entity> $$3 = this.getEntities($$0, $$1.inflate(1.0E-7), (Predicate<? super Entity>)$$2);
        if ($$3.isEmpty()) {
            return List.of();
        }
        ImmutableList.Builder $$4 = ImmutableList.builderWithExpectedSize((int)$$3.size());
        for (Entity $$5 : $$3) {
            $$4.add((Object)Shapes.create($$5.getBoundingBox()));
        }
        return $$4.build();
    }

    @Nullable
    default public Player getNearestPlayer(double $$0, double $$1, double $$2, double $$3, @Nullable Predicate<Entity> $$4) {
        double $$5 = -1.0;
        Player $$6 = null;
        for (Player $$7 : this.players()) {
            if ($$4 != null && !$$4.test((Object)$$7)) continue;
            double $$8 = $$7.distanceToSqr($$0, $$1, $$2);
            if (!($$3 < 0.0) && !($$8 < $$3 * $$3) || $$5 != -1.0 && !($$8 < $$5)) continue;
            $$5 = $$8;
            $$6 = $$7;
        }
        return $$6;
    }

    @Nullable
    default public Player getNearestPlayer(Entity $$0, double $$1) {
        return this.getNearestPlayer($$0.getX(), $$0.getY(), $$0.getZ(), $$1, false);
    }

    @Nullable
    default public Player getNearestPlayer(double $$0, double $$1, double $$2, double $$3, boolean $$4) {
        Predicate<Entity> $$5 = $$4 ? EntitySelector.NO_CREATIVE_OR_SPECTATOR : EntitySelector.NO_SPECTATORS;
        return this.getNearestPlayer($$0, $$1, $$2, $$3, $$5);
    }

    default public boolean hasNearbyAlivePlayer(double $$0, double $$1, double $$2, double $$3) {
        for (Player $$4 : this.players()) {
            if (!EntitySelector.NO_SPECTATORS.test((Object)$$4) || !EntitySelector.LIVING_ENTITY_STILL_ALIVE.test((Object)$$4)) continue;
            double $$5 = $$4.distanceToSqr($$0, $$1, $$2);
            if (!($$3 < 0.0) && !($$5 < $$3 * $$3)) continue;
            return true;
        }
        return false;
    }

    @Nullable
    default public Player getNearestPlayer(TargetingConditions $$0, LivingEntity $$1) {
        return this.getNearestEntity(this.players(), $$0, $$1, $$1.getX(), $$1.getY(), $$1.getZ());
    }

    @Nullable
    default public Player getNearestPlayer(TargetingConditions $$0, LivingEntity $$1, double $$2, double $$3, double $$4) {
        return this.getNearestEntity(this.players(), $$0, $$1, $$2, $$3, $$4);
    }

    @Nullable
    default public Player getNearestPlayer(TargetingConditions $$0, double $$1, double $$2, double $$3) {
        return this.getNearestEntity(this.players(), $$0, null, $$1, $$2, $$3);
    }

    @Nullable
    default public <T extends LivingEntity> T getNearestEntity(Class<? extends T> $$02, TargetingConditions $$1, @Nullable LivingEntity $$2, double $$3, double $$4, double $$5, AABB $$6) {
        return this.getNearestEntity(this.getEntitiesOfClass($$02, $$6, $$0 -> true), $$1, $$2, $$3, $$4, $$5);
    }

    @Nullable
    default public <T extends LivingEntity> T getNearestEntity(List<? extends T> $$0, TargetingConditions $$1, @Nullable LivingEntity $$2, double $$3, double $$4, double $$5) {
        double $$6 = -1.0;
        LivingEntity $$7 = null;
        for (LivingEntity $$8 : $$0) {
            if (!$$1.test($$2, $$8)) continue;
            double $$9 = $$8.distanceToSqr($$3, $$4, $$5);
            if ($$6 != -1.0 && !($$9 < $$6)) continue;
            $$6 = $$9;
            $$7 = $$8;
        }
        return (T)$$7;
    }

    default public List<Player> getNearbyPlayers(TargetingConditions $$0, LivingEntity $$1, AABB $$2) {
        ArrayList $$3 = Lists.newArrayList();
        for (Player $$4 : this.players()) {
            if (!$$2.contains($$4.getX(), $$4.getY(), $$4.getZ()) || !$$0.test($$1, $$4)) continue;
            $$3.add((Object)$$4);
        }
        return $$3;
    }

    default public <T extends LivingEntity> List<T> getNearbyEntities(Class<T> $$02, TargetingConditions $$1, LivingEntity $$2, AABB $$3) {
        List<T> $$4 = this.getEntitiesOfClass($$02, $$3, $$0 -> true);
        ArrayList $$5 = Lists.newArrayList();
        for (LivingEntity $$6 : $$4) {
            if (!$$1.test($$2, $$6)) continue;
            $$5.add((Object)$$6);
        }
        return $$5;
    }

    @Nullable
    default public Player getPlayerByUUID(UUID $$0) {
        for (int $$1 = 0; $$1 < this.players().size(); ++$$1) {
            Player $$2 = (Player)this.players().get($$1);
            if (!$$0.equals((Object)$$2.getUUID())) continue;
            return $$2;
        }
        return null;
    }
}