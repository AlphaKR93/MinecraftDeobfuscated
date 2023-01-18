/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 *  java.lang.Object
 *  java.util.function.BiPredicate
 */
package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.kinds.Applicative;
import java.util.function.BiPredicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class DismountOrSkipMounting {
    public static <E extends LivingEntity> BehaviorControl<E> create(int $$0, BiPredicate<E, Entity> $$1) {
        return BehaviorBuilder.create($$2 -> $$2.group($$2.registered(MemoryModuleType.RIDE_TARGET)).apply((Applicative)$$2, $$3 -> ($$4, $$5, $$6) -> {
            Entity $$9;
            Entity $$7 = $$5.getVehicle();
            Entity $$8 = (Entity)$$2.tryGet($$3).orElse(null);
            if ($$7 == null && $$8 == null) {
                return false;
            }
            Entity entity = $$9 = $$7 == null ? $$8 : $$7;
            if (!DismountOrSkipMounting.isVehicleValid($$5, $$9, $$0) || $$1.test((Object)$$5, (Object)$$9)) {
                $$5.stopRiding();
                $$3.erase();
                return true;
            }
            return false;
        }));
    }

    private static boolean isVehicleValid(LivingEntity $$0, Entity $$1, int $$2) {
        return $$1.isAlive() && $$1.closerThan($$0, $$2) && $$1.level == $$0.level;
    }
}