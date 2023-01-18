/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Comparator
 *  java.util.List
 *  java.util.Optional
 *  java.util.Set
 *  java.util.stream.Collectors
 */
package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.player.Player;

public class PlayerSensor
extends Sensor<LivingEntity> {
    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER);
    }

    @Override
    protected void doTick(ServerLevel $$0, LivingEntity $$12) {
        List $$2 = (List)$$0.players().stream().filter(EntitySelector.NO_SPECTATORS).filter($$1 -> $$12.closerThan((Entity)$$1, 16.0)).sorted(Comparator.comparingDouble($$12::distanceToSqr)).collect(Collectors.toList());
        Brain<?> $$3 = $$12.getBrain();
        $$3.setMemory(MemoryModuleType.NEAREST_PLAYERS, $$2);
        List $$4 = (List)$$2.stream().filter($$1 -> PlayerSensor.isEntityTargetable($$12, $$1)).collect(Collectors.toList());
        $$3.setMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER, $$4.isEmpty() ? null : (Player)$$4.get(0));
        Optional $$5 = $$4.stream().filter($$1 -> PlayerSensor.isEntityAttackable($$12, $$1)).findFirst();
        $$3.setMemory(MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER, $$5);
    }
}