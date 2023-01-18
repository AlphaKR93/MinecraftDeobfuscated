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
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;

public class AdultSensor
extends Sensor<AgeableMob> {
    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
    }

    @Override
    protected void doTick(ServerLevel $$0, AgeableMob $$12) {
        $$12.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).ifPresent($$1 -> this.setNearestVisibleAdult($$12, (NearestVisibleLivingEntities)$$1));
    }

    private void setNearestVisibleAdult(AgeableMob $$0, NearestVisibleLivingEntities $$12) {
        Optional $$2 = $$12.findClosest((Predicate<LivingEntity>)((Predicate)$$1 -> $$1.getType() == $$0.getType() && !$$1.isBaby())).map(arg_0 -> AgeableMob.class.cast(arg_0));
        $$0.getBrain().setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT, $$2);
    }
}