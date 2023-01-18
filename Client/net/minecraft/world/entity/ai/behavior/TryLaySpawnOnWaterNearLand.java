/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 *  java.lang.Object
 */
package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.kinds.Applicative;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;

public class TryLaySpawnOnWaterNearLand {
    public static BehaviorControl<LivingEntity> create(Block $$0) {
        return BehaviorBuilder.create($$12 -> $$12.group($$12.absent(MemoryModuleType.ATTACK_TARGET), $$12.present(MemoryModuleType.WALK_TARGET), $$12.present(MemoryModuleType.IS_PREGNANT)).apply((Applicative)$$12, ($$1, $$22, $$32) -> ($$2, $$3, $$4) -> {
            if ($$3.isInWater() || !$$3.isOnGround()) {
                return false;
            }
            Vec3i $$5 = $$3.blockPosition().below();
            for (Direction $$6 : Direction.Plane.HORIZONTAL) {
                Vec3i $$8;
                Vec3i $$7 = ((BlockPos)$$5).relative($$6);
                if (!$$2.getBlockState((BlockPos)$$7).getCollisionShape($$2, (BlockPos)$$7).getFaceShape(Direction.UP).isEmpty() || !$$2.getFluidState((BlockPos)$$7).is(Fluids.WATER) || !$$2.getBlockState((BlockPos)($$8 = ((BlockPos)$$7).above())).isAir()) continue;
                $$2.setBlock((BlockPos)$$8, $$0.defaultBlockState(), 3);
                $$2.playSound(null, $$3, SoundEvents.FROG_LAY_SPAWN, SoundSource.BLOCKS, 1.0f, 1.0f);
                $$32.erase();
                return true;
            }
            return true;
        }));
    }
}