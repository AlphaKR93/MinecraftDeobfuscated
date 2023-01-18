/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 *  java.lang.Object
 *  java.util.Optional
 *  java.util.function.Function
 *  java.util.function.Predicate
 */
package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.kinds.Applicative;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class StartAttacking {
    public static <E extends Mob> BehaviorControl<E> create(Function<E, Optional<? extends LivingEntity>> $$02) {
        return StartAttacking.create($$0 -> true, $$02);
    }

    public static <E extends Mob> BehaviorControl<E> create(Predicate<E> $$0, Function<E, Optional<? extends LivingEntity>> $$1) {
        return BehaviorBuilder.create($$22 -> $$22.group($$22.absent(MemoryModuleType.ATTACK_TARGET), $$22.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)).apply((Applicative)$$22, ($$2, $$3) -> ($$4, $$5, $$6) -> {
            if (!$$0.test((Object)$$5)) {
                return false;
            }
            Optional $$7 = (Optional)$$1.apply((Object)$$5);
            if ($$7.isEmpty()) {
                return false;
            }
            LivingEntity $$8 = (LivingEntity)$$7.get();
            if (!$$5.canAttack($$8)) {
                return false;
            }
            $$2.set($$8);
            $$3.erase();
            return true;
        }));
    }
}