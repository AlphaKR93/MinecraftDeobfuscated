/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 *  java.lang.Long
 *  java.lang.Object
 *  java.util.Optional
 *  java.util.function.BiConsumer
 *  java.util.function.Predicate
 */
package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.kinds.Applicative;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class StopAttackingIfTargetInvalid {
    private static final int TIMEOUT_TO_GET_WITHIN_ATTACK_RANGE = 200;

    public static <E extends Mob> BehaviorControl<E> create(BiConsumer<E, LivingEntity> $$02) {
        return StopAttackingIfTargetInvalid.create((Predicate<LivingEntity>)((Predicate)$$0 -> false), $$02, true);
    }

    public static <E extends Mob> BehaviorControl<E> create(Predicate<LivingEntity> $$02) {
        return StopAttackingIfTargetInvalid.create($$02, ($$0, $$1) -> {}, true);
    }

    public static <E extends Mob> BehaviorControl<E> create() {
        return StopAttackingIfTargetInvalid.create((Predicate<LivingEntity>)((Predicate)$$0 -> false), ($$0, $$1) -> {}, true);
    }

    public static <E extends Mob> BehaviorControl<E> create(Predicate<LivingEntity> $$0, BiConsumer<E, LivingEntity> $$1, boolean $$2) {
        return BehaviorBuilder.create($$3 -> $$3.group($$3.present(MemoryModuleType.ATTACK_TARGET), $$3.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)).apply((Applicative)$$3, ($$4, $$5) -> ($$6, $$7, $$8) -> {
            LivingEntity $$9 = (LivingEntity)$$3.get($$4);
            if (!$$7.canAttack($$9) || $$2 && StopAttackingIfTargetInvalid.isTiredOfTryingToReachTarget($$7, $$3.tryGet($$5)) || !$$9.isAlive() || $$9.level != $$7.level || $$0.test((Object)$$9)) {
                $$1.accept((Object)$$7, (Object)$$9);
                $$4.erase();
                return true;
            }
            return true;
        }));
    }

    private static boolean isTiredOfTryingToReachTarget(LivingEntity $$0, Optional<Long> $$1) {
        return $$1.isPresent() && $$0.level.getGameTime() - (Long)$$1.get() > 200L;
    }
}