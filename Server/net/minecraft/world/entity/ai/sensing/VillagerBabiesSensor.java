/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Set
 *  java.util.function.Predicate
 *  net.minecraft.server.level.ServerLevel
 */
package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;

public class VillagerBabiesSensor
extends Sensor<LivingEntity> {
    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES);
    }

    @Override
    protected void doTick(ServerLevel $$0, LivingEntity $$1) {
        $$1.getBrain().setMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES, this.getNearestVillagerBabies($$1));
    }

    private List<LivingEntity> getNearestVillagerBabies(LivingEntity $$0) {
        return ImmutableList.copyOf(this.getVisibleEntities($$0).findAll((Predicate<LivingEntity>)((Predicate)this::isVillagerBaby)));
    }

    private boolean isVillagerBaby(LivingEntity $$0) {
        return $$0.getType() == EntityType.VILLAGER && $$0.isBaby();
    }

    private NearestVisibleLivingEntities getVisibleEntities(LivingEntity $$0) {
        return (NearestVisibleLivingEntities)$$0.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse((Object)NearestVisibleLivingEntities.empty());
    }
}