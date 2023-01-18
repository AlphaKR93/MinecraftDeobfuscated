/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 *  java.util.Set
 *  java.util.function.Predicate
 */
package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;

public abstract class NearestVisibleLivingEntitySensor
extends Sensor<LivingEntity> {
    protected abstract boolean isMatchingEntity(LivingEntity var1, LivingEntity var2);

    protected abstract MemoryModuleType<LivingEntity> getMemory();

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(this.getMemory());
    }

    @Override
    protected void doTick(ServerLevel $$0, LivingEntity $$1) {
        $$1.getBrain().setMemory(this.getMemory(), this.getNearestEntity($$1));
    }

    private Optional<LivingEntity> getNearestEntity(LivingEntity $$0) {
        return this.getVisibleEntities($$0).flatMap($$12 -> $$12.findClosest((Predicate<LivingEntity>)((Predicate)$$1 -> this.isMatchingEntity($$0, (LivingEntity)$$1))));
    }

    protected Optional<NearestVisibleLivingEntities> getVisibleEntities(LivingEntity $$0) {
        return $$0.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
    }
}