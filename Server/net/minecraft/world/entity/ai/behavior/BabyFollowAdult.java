/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 *  java.lang.Float
 *  java.lang.Object
 *  java.util.function.Function
 */
package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.kinds.Applicative;
import java.util.function.Function;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class BabyFollowAdult {
    public static OneShot<AgeableMob> create(UniformInt $$0, float $$12) {
        return BabyFollowAdult.create($$0, (Function<LivingEntity, Float>)((Function)$$1 -> Float.valueOf((float)$$12)));
    }

    public static OneShot<AgeableMob> create(UniformInt $$0, Function<LivingEntity, Float> $$1) {
        return BehaviorBuilder.create($$2 -> $$2.group($$2.present(MemoryModuleType.NEAREST_VISIBLE_ADULT), $$2.registered(MemoryModuleType.LOOK_TARGET), $$2.absent(MemoryModuleType.WALK_TARGET)).apply((Applicative)$$2, ($$3, $$4, $$5) -> ($$6, $$7, $$8) -> {
            if (!$$7.isBaby()) {
                return false;
            }
            AgeableMob $$9 = (AgeableMob)$$2.get($$3);
            if ($$7.closerThan($$9, $$0.getMaxValue() + 1) && !$$7.closerThan($$9, $$0.getMinValue())) {
                WalkTarget $$10 = new WalkTarget(new EntityTracker($$9, false), ((Float)$$1.apply((Object)$$7)).floatValue(), $$0.getMinValue() - 1);
                $$4.set(new EntityTracker($$9, true));
                $$5.set($$10);
                return true;
            }
            return false;
        }));
    }
}