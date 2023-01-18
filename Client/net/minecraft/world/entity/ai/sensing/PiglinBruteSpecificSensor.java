/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Optional
 *  java.util.Set
 *  java.util.function.Predicate
 */
package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;

public class PiglinBruteSpecificSensor
extends Sensor<LivingEntity> {
    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.NEARBY_ADULT_PIGLINS);
    }

    @Override
    protected void doTick(ServerLevel $$02, LivingEntity $$1) {
        Brain<?> $$2 = $$1.getBrain();
        ArrayList $$3 = Lists.newArrayList();
        NearestVisibleLivingEntities $$4 = (NearestVisibleLivingEntities)$$2.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse((Object)NearestVisibleLivingEntities.empty());
        Optional $$5 = $$4.findClosest((Predicate<LivingEntity>)((Predicate)$$0 -> $$0 instanceof WitherSkeleton || $$0 instanceof WitherBoss)).map(arg_0 -> Mob.class.cast(arg_0));
        List $$6 = (List)$$2.getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).orElse((Object)ImmutableList.of());
        for (LivingEntity $$7 : $$6) {
            if (!($$7 instanceof AbstractPiglin) || !((AbstractPiglin)$$7).isAdult()) continue;
            $$3.add((Object)((AbstractPiglin)$$7));
        }
        $$2.setMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, $$5);
        $$2.setMemory(MemoryModuleType.NEARBY_ADULT_PIGLINS, $$3);
    }
}