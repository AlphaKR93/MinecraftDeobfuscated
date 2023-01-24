/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Comparator
 *  java.util.List
 *  java.util.Set
 *  net.minecraft.world.entity.LivingEntity
 */
package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.phys.AABB;

public class NearestLivingEntitySensor<T extends LivingEntity>
extends Sensor<T> {
    @Override
    protected void doTick(ServerLevel $$0, T $$12) {
        AABB $$2 = $$12.getBoundingBox().inflate(this.radiusXZ(), this.radiusY(), this.radiusXZ());
        List $$3 = $$0.getEntitiesOfClass(LivingEntity.class, $$2, $$1 -> $$1 != $$12 && $$1.isAlive());
        $$3.sort(Comparator.comparingDouble(arg_0 -> $$12.distanceToSqr(arg_0)));
        Brain $$4 = $$12.getBrain();
        $$4.setMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES, $$3);
        $$4.setMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, new NearestVisibleLivingEntities((LivingEntity)$$12, (List<LivingEntity>)$$3));
    }

    protected int radiusXZ() {
        return 16;
    }

    protected int radiusY() {
        return 16;
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
    }
}