/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.kinds.Applicative
 *  java.lang.Integer
 *  java.lang.Object
 *  java.util.Comparator
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Optional
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.Maps;
import com.mojang.datafixers.kinds.Applicative;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.phys.Vec3;

public class PlayTagWithOtherKids {
    private static final int MAX_FLEE_XZ_DIST = 20;
    private static final int MAX_FLEE_Y_DIST = 8;
    private static final float FLEE_SPEED_MODIFIER = 0.6f;
    private static final float CHASE_SPEED_MODIFIER = 0.6f;
    private static final int MAX_CHASERS_PER_TARGET = 5;
    private static final int AVERAGE_WAIT_TIME_BETWEEN_RUNS = 10;

    public static BehaviorControl<PathfinderMob> create() {
        return BehaviorBuilder.create($$0 -> $$0.group($$0.present(MemoryModuleType.VISIBLE_VILLAGER_BABIES), $$0.absent(MemoryModuleType.WALK_TARGET), $$0.registered(MemoryModuleType.LOOK_TARGET), $$0.registered(MemoryModuleType.INTERACTION_TARGET)).apply((Applicative)$$0, ($$1, $$2, $$3, $$4) -> ($$5, $$6, $$7) -> {
            if ($$5.getRandom().nextInt(10) != 0) {
                return false;
            }
            List $$8 = (List)$$0.get($$1);
            Optional $$9 = $$8.stream().filter($$1 -> PlayTagWithOtherKids.isFriendChasingMe($$6, $$1)).findAny();
            if ($$9.isPresent()) {
                for (int $$10 = 0; $$10 < 10; ++$$10) {
                    Vec3 $$11 = LandRandomPos.getPos($$6, 20, 8);
                    if ($$11 == null || !$$5.isVillage(new BlockPos($$11))) continue;
                    $$2.set(new WalkTarget($$11, 0.6f, 0));
                    break;
                }
                return true;
            }
            Optional<LivingEntity> $$122 = PlayTagWithOtherKids.findSomeoneBeingChased((List<LivingEntity>)$$8);
            if ($$122.isPresent()) {
                PlayTagWithOtherKids.chaseKid($$4, $$3, $$2, (LivingEntity)$$122.get());
                return true;
            }
            $$8.stream().findAny().ifPresent($$3 -> PlayTagWithOtherKids.chaseKid($$4, $$3, $$2, $$3));
            return true;
        }));
    }

    private static void chaseKid(MemoryAccessor<?, LivingEntity> $$0, MemoryAccessor<?, PositionTracker> $$1, MemoryAccessor<?, WalkTarget> $$2, LivingEntity $$3) {
        $$0.set($$3);
        $$1.set(new EntityTracker($$3, true));
        $$2.set(new WalkTarget(new EntityTracker($$3, false), 0.6f, 1));
    }

    private static Optional<LivingEntity> findSomeoneBeingChased(List<LivingEntity> $$02) {
        Map<LivingEntity, Integer> $$1 = PlayTagWithOtherKids.checkHowManyChasersEachFriendHas($$02);
        return $$1.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getValue)).filter($$0 -> (Integer)$$0.getValue() > 0 && (Integer)$$0.getValue() <= 5).map(Map.Entry::getKey).findFirst();
    }

    private static Map<LivingEntity, Integer> checkHowManyChasersEachFriendHas(List<LivingEntity> $$0) {
        HashMap $$1 = Maps.newHashMap();
        $$0.stream().filter(PlayTagWithOtherKids::isChasingSomeone).forEach(arg_0 -> PlayTagWithOtherKids.lambda$checkHowManyChasersEachFriendHas$7((Map)$$1, arg_0));
        return $$1;
    }

    private static LivingEntity whoAreYouChasing(LivingEntity $$0) {
        return (LivingEntity)$$0.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).get();
    }

    private static boolean isChasingSomeone(LivingEntity $$0) {
        return $$0.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
    }

    private static boolean isFriendChasingMe(LivingEntity $$0, LivingEntity $$12) {
        return $$12.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).filter($$1 -> $$1 == $$0).isPresent();
    }

    private static /* synthetic */ void lambda$checkHowManyChasersEachFriendHas$7(Map $$02, LivingEntity $$12) {
        $$02.compute((Object)PlayTagWithOtherKids.whoAreYouChasing($$12), ($$0, $$1) -> $$1 == null ? 1 : $$1 + 1);
    }
}