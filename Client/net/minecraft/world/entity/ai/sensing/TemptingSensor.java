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
 *  java.util.stream.Collectors
 */
package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class TemptingSensor
extends Sensor<PathfinderMob> {
    public static final int TEMPTATION_RANGE = 10;
    private static final TargetingConditions TEMPT_TARGETING = TargetingConditions.forNonCombat().range(10.0).ignoreLineOfSight();
    private final Ingredient temptations;

    public TemptingSensor(Ingredient $$0) {
        this.temptations = $$0;
    }

    @Override
    protected void doTick(ServerLevel $$0, PathfinderMob $$12) {
        Brain<?> $$2 = $$12.getBrain();
        List $$3 = (List)$$0.players().stream().filter(EntitySelector.NO_SPECTATORS).filter($$1 -> TEMPT_TARGETING.test($$12, (LivingEntity)$$1)).filter($$1 -> $$12.closerThan((Entity)$$1, 10.0)).filter(this::playerHoldingTemptation).filter($$1 -> !$$12.hasPassenger((Entity)$$1)).sorted(Comparator.comparingDouble($$12::distanceToSqr)).collect(Collectors.toList());
        if (!$$3.isEmpty()) {
            Player $$4 = (Player)$$3.get(0);
            $$2.setMemory(MemoryModuleType.TEMPTING_PLAYER, $$4);
        } else {
            $$2.eraseMemory(MemoryModuleType.TEMPTING_PLAYER);
        }
    }

    private boolean playerHoldingTemptation(Player $$0) {
        return this.isTemptation($$0.getMainHandItem()) || this.isTemptation($$0.getOffhandItem());
    }

    private boolean isTemptation(ItemStack $$0) {
        return this.temptations.test($$0);
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.TEMPTING_PLAYER);
    }
}