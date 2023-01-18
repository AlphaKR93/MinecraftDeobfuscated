/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 *  java.lang.Object
 *  java.util.Optional
 *  java.util.function.Function
 */
package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.kinds.Applicative;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class StayCloseToTarget {
    public static BehaviorControl<LivingEntity> create(Function<LivingEntity, Optional<PositionTracker>> $$0, int $$1, int $$2, float $$3) {
        return BehaviorBuilder.create($$42 -> $$42.group($$42.registered(MemoryModuleType.LOOK_TARGET), $$42.absent(MemoryModuleType.WALK_TARGET)).apply((Applicative)$$42, ($$4, $$5) -> ($$6, $$7, $$8) -> {
            Optional $$9 = (Optional)$$0.apply((Object)$$7);
            if ($$9.isEmpty()) {
                return false;
            }
            PositionTracker $$10 = (PositionTracker)$$9.get();
            if ($$7.position().closerThan($$10.currentPosition(), $$2)) {
                return false;
            }
            PositionTracker $$11 = (PositionTracker)$$9.get();
            $$4.set($$11);
            $$5.set(new WalkTarget($$11, $$3, $$1));
            return true;
        }));
    }
}