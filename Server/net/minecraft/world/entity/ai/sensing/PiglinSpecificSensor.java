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
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;

public class PiglinSpecificSensor
extends Sensor<LivingEntity> {
    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, MemoryModuleType.NEAREST_LIVING_ENTITIES, MemoryModuleType.NEAREST_VISIBLE_NEMESIS, MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, (Object[])new MemoryModuleType[]{MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, MemoryModuleType.NEARBY_ADULT_PIGLINS, MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, MemoryModuleType.NEAREST_REPELLENT});
    }

    @Override
    protected void doTick(ServerLevel $$02, LivingEntity $$1) {
        Brain<?> $$2 = $$1.getBrain();
        $$2.setMemory(MemoryModuleType.NEAREST_REPELLENT, PiglinSpecificSensor.findNearestRepellent($$02, $$1));
        Optional $$3 = Optional.empty();
        Optional $$4 = Optional.empty();
        Optional $$5 = Optional.empty();
        Optional $$6 = Optional.empty();
        Optional $$7 = Optional.empty();
        Optional $$8 = Optional.empty();
        Optional $$9 = Optional.empty();
        int $$10 = 0;
        ArrayList $$11 = Lists.newArrayList();
        ArrayList $$12 = Lists.newArrayList();
        NearestVisibleLivingEntities $$13 = (NearestVisibleLivingEntities)$$2.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).orElse((Object)NearestVisibleLivingEntities.empty());
        for (LivingEntity $$14 : $$13.findAll((Predicate<LivingEntity>)((Predicate)$$0 -> true))) {
            if ($$14 instanceof Hoglin) {
                Hoglin $$15 = (Hoglin)$$14;
                if ($$15.isBaby() && $$5.isEmpty()) {
                    $$5 = Optional.of((Object)$$15);
                    continue;
                }
                if (!$$15.isAdult()) continue;
                ++$$10;
                if (!$$4.isEmpty() || !$$15.canBeHunted()) continue;
                $$4 = Optional.of((Object)$$15);
                continue;
            }
            if ($$14 instanceof PiglinBrute) {
                PiglinBrute $$16 = (PiglinBrute)$$14;
                $$11.add((Object)$$16);
                continue;
            }
            if ($$14 instanceof Piglin) {
                Piglin $$17 = (Piglin)$$14;
                if ($$17.isBaby() && $$6.isEmpty()) {
                    $$6 = Optional.of((Object)$$17);
                    continue;
                }
                if (!$$17.isAdult()) continue;
                $$11.add((Object)$$17);
                continue;
            }
            if ($$14 instanceof Player) {
                Player $$18 = (Player)$$14;
                if ($$8.isEmpty() && !PiglinAi.isWearingGold($$18) && $$1.canAttack($$14)) {
                    $$8 = Optional.of((Object)$$18);
                }
                if (!$$9.isEmpty() || $$18.isSpectator() || !PiglinAi.isPlayerHoldingLovedItem($$18)) continue;
                $$9 = Optional.of((Object)$$18);
                continue;
            }
            if ($$3.isEmpty() && ($$14 instanceof WitherSkeleton || $$14 instanceof WitherBoss)) {
                $$3 = Optional.of((Object)((Mob)$$14));
                continue;
            }
            if (!$$7.isEmpty() || !PiglinAi.isZombified($$14.getType())) continue;
            $$7 = Optional.of((Object)$$14);
        }
        List $$19 = (List)$$2.getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES).orElse((Object)ImmutableList.of());
        for (LivingEntity $$20 : $$19) {
            AbstractPiglin $$21;
            if (!($$20 instanceof AbstractPiglin) || !($$21 = (AbstractPiglin)$$20).isAdult()) continue;
            $$12.add((Object)$$21);
        }
        $$2.setMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS, $$3);
        $$2.setMemory(MemoryModuleType.NEAREST_VISIBLE_HUNTABLE_HOGLIN, $$4);
        $$2.setMemory(MemoryModuleType.NEAREST_VISIBLE_BABY_HOGLIN, $$5);
        $$2.setMemory(MemoryModuleType.NEAREST_VISIBLE_ZOMBIFIED, $$7);
        $$2.setMemory(MemoryModuleType.NEAREST_TARGETABLE_PLAYER_NOT_WEARING_GOLD, $$8);
        $$2.setMemory(MemoryModuleType.NEAREST_PLAYER_HOLDING_WANTED_ITEM, $$9);
        $$2.setMemory(MemoryModuleType.NEARBY_ADULT_PIGLINS, $$12);
        $$2.setMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT_PIGLINS, $$11);
        $$2.setMemory(MemoryModuleType.VISIBLE_ADULT_PIGLIN_COUNT, $$11.size());
        $$2.setMemory(MemoryModuleType.VISIBLE_ADULT_HOGLIN_COUNT, $$10);
    }

    private static Optional<BlockPos> findNearestRepellent(ServerLevel $$0, LivingEntity $$12) {
        return BlockPos.findClosestMatch($$12.blockPosition(), 8, 4, (Predicate<BlockPos>)((Predicate)$$1 -> PiglinSpecificSensor.isValidRepellent($$0, $$1)));
    }

    private static boolean isValidRepellent(ServerLevel $$0, BlockPos $$1) {
        BlockState $$2 = $$0.getBlockState($$1);
        boolean $$3 = $$2.is(BlockTags.PIGLIN_REPELLENTS);
        if ($$3 && $$2.is(Blocks.SOUL_CAMPFIRE)) {
            return CampfireBlock.isLitCampfire($$2);
        }
        return $$3;
    }
}