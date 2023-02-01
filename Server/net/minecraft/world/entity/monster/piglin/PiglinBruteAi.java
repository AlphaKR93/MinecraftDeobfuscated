/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableSet
 *  com.mojang.datafixers.util.Pair
 *  java.lang.Integer
 *  java.lang.Object
 *  java.util.List
 *  java.util.Optional
 *  java.util.Set
 *  java.util.function.Predicate
 */
package net.minecraft.world.entity.monster.piglin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.InteractWith;
import net.minecraft.world.entity.ai.behavior.InteractWithDoor;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MeleeAttack;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.behavior.SetLookAndInteract;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromAttackTargetIfTargetOutOfReach;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.behavior.StopBeingAngryIfTargetDead;
import net.minecraft.world.entity.ai.behavior.StrollAroundPoi;
import net.minecraft.world.entity.ai.behavior.StrollToPoi;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.schedule.Activity;

public class PiglinBruteAi {
    private static final int ANGER_DURATION = 600;
    private static final int MELEE_ATTACK_COOLDOWN = 20;
    private static final double ACTIVITY_SOUND_LIKELIHOOD_PER_TICK = 0.0125;
    private static final int MAX_LOOK_DIST = 8;
    private static final int INTERACTION_RANGE = 8;
    private static final double TARGETING_RANGE = 12.0;
    private static final float SPEED_MULTIPLIER_WHEN_IDLING = 0.6f;
    private static final int HOME_CLOSE_ENOUGH_DISTANCE = 2;
    private static final int HOME_TOO_FAR_DISTANCE = 100;
    private static final int HOME_STROLL_AROUND_DISTANCE = 5;

    protected static Brain<?> makeBrain(PiglinBrute $$0, Brain<PiglinBrute> $$1) {
        PiglinBruteAi.initCoreActivity($$0, $$1);
        PiglinBruteAi.initIdleActivity($$0, $$1);
        PiglinBruteAi.initFightActivity($$0, $$1);
        $$1.setCoreActivities((Set<Activity>)ImmutableSet.of((Object)Activity.CORE));
        $$1.setDefaultActivity(Activity.IDLE);
        $$1.useDefaultActivity();
        return $$1;
    }

    protected static void initMemories(PiglinBrute $$0) {
        GlobalPos $$1 = GlobalPos.of($$0.level.dimension(), $$0.blockPosition());
        $$0.getBrain().setMemory(MemoryModuleType.HOME, $$1);
    }

    private static void initCoreActivity(PiglinBrute $$0, Brain<PiglinBrute> $$1) {
        $$1.addActivity(Activity.CORE, 0, (ImmutableList<BehaviorControl<PiglinBrute>>)ImmutableList.of((Object)new LookAtTargetSink(45, 90), (Object)new MoveToTargetSink(), InteractWithDoor.create(), StopBeingAngryIfTargetDead.create()));
    }

    private static void initIdleActivity(PiglinBrute $$0, Brain<PiglinBrute> $$1) {
        $$1.addActivity(Activity.IDLE, 10, (ImmutableList<BehaviorControl<PiglinBrute>>)ImmutableList.of(StartAttacking.create(PiglinBruteAi::findNearestValidAttackTarget), PiglinBruteAi.createIdleLookBehaviors(), PiglinBruteAi.createIdleMovementBehaviors(), SetLookAndInteract.create(EntityType.PLAYER, 4)));
    }

    private static void initFightActivity(PiglinBrute $$0, Brain<PiglinBrute> $$12) {
        $$12.addActivityAndRemoveMemoryWhenStopped(Activity.FIGHT, 10, (ImmutableList<BehaviorControl<PiglinBrute>>)ImmutableList.of(StopAttackingIfTargetInvalid.create((Predicate<LivingEntity>)((Predicate)$$1 -> !PiglinBruteAi.isNearestValidAttackTarget($$0, $$1))), SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1.0f), MeleeAttack.create(20)), MemoryModuleType.ATTACK_TARGET);
    }

    private static RunOne<PiglinBrute> createIdleLookBehaviors() {
        return new RunOne<PiglinBrute>((List<Pair<BehaviorControl<PiglinBrute>, Integer>>)ImmutableList.of((Object)Pair.of(SetEntityLookTarget.create(EntityType.PLAYER, 8.0f), (Object)1), (Object)Pair.of(SetEntityLookTarget.create(EntityType.PIGLIN, 8.0f), (Object)1), (Object)Pair.of(SetEntityLookTarget.create(EntityType.PIGLIN_BRUTE, 8.0f), (Object)1), (Object)Pair.of(SetEntityLookTarget.create(8.0f), (Object)1), (Object)Pair.of((Object)new DoNothing(30, 60), (Object)1)));
    }

    private static RunOne<PiglinBrute> createIdleMovementBehaviors() {
        return new RunOne<PiglinBrute>((List<Pair<BehaviorControl<PiglinBrute>, Integer>>)ImmutableList.of((Object)Pair.of(RandomStroll.stroll(0.6f), (Object)2), (Object)Pair.of(InteractWith.of(EntityType.PIGLIN, 8, MemoryModuleType.INTERACTION_TARGET, 0.6f, 2), (Object)2), (Object)Pair.of(InteractWith.of(EntityType.PIGLIN_BRUTE, 8, MemoryModuleType.INTERACTION_TARGET, 0.6f, 2), (Object)2), (Object)Pair.of(StrollToPoi.create(MemoryModuleType.HOME, 0.6f, 2, 100), (Object)2), (Object)Pair.of(StrollAroundPoi.create(MemoryModuleType.HOME, 0.6f, 5), (Object)2), (Object)Pair.of((Object)new DoNothing(30, 60), (Object)1)));
    }

    protected static void updateActivity(PiglinBrute $$0) {
        Brain<PiglinBrute> $$1 = $$0.getBrain();
        Activity $$2 = (Activity)$$1.getActiveNonCoreActivity().orElse(null);
        $$1.setActiveActivityToFirstValid((List<Activity>)ImmutableList.of((Object)Activity.FIGHT, (Object)Activity.IDLE));
        Activity $$3 = (Activity)$$1.getActiveNonCoreActivity().orElse(null);
        if ($$2 != $$3) {
            PiglinBruteAi.playActivitySound($$0);
        }
        $$0.setAggressive($$1.hasMemoryValue(MemoryModuleType.ATTACK_TARGET));
    }

    private static boolean isNearestValidAttackTarget(AbstractPiglin $$0, LivingEntity $$12) {
        return PiglinBruteAi.findNearestValidAttackTarget($$0).filter($$1 -> $$1 == $$12).isPresent();
    }

    private static Optional<? extends LivingEntity> findNearestValidAttackTarget(AbstractPiglin $$0) {
        Optional<LivingEntity> $$1 = BehaviorUtils.getLivingEntityFromUUIDMemory($$0, MemoryModuleType.ANGRY_AT);
        if ($$1.isPresent() && Sensor.isEntityAttackableIgnoringLineOfSight($$0, (LivingEntity)((Object)$$1.get()))) {
            return $$1;
        }
        Optional<? extends LivingEntity> $$2 = PiglinBruteAi.getTargetIfWithinRange($$0, MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER);
        if ($$2.isPresent()) {
            return $$2;
        }
        return $$0.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_NEMESIS);
    }

    private static Optional<? extends LivingEntity> getTargetIfWithinRange(AbstractPiglin $$0, MemoryModuleType<? extends LivingEntity> $$12) {
        return $$0.getBrain().getMemory($$12).filter($$1 -> $$1.closerThan($$0, 12.0));
    }

    protected static void wasHurtBy(PiglinBrute $$0, LivingEntity $$1) {
        if ($$1 instanceof AbstractPiglin) {
            return;
        }
        PiglinAi.maybeRetaliate($$0, $$1);
    }

    protected static void setAngerTarget(PiglinBrute $$0, LivingEntity $$1) {
        $$0.getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        $$0.getBrain().setMemoryWithExpiry(MemoryModuleType.ANGRY_AT, $$1.getUUID(), 600L);
    }

    protected static void maybePlayActivitySound(PiglinBrute $$0) {
        if ((double)$$0.level.random.nextFloat() < 0.0125) {
            PiglinBruteAi.playActivitySound($$0);
        }
    }

    private static void playActivitySound(PiglinBrute $$0) {
        $$0.getBrain().getActiveNonCoreActivity().ifPresent($$1 -> {
            if ($$1 == Activity.FIGHT) {
                $$0.playAngrySound();
            }
        });
    }
}