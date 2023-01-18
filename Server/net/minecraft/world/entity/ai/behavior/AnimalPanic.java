/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 *  java.util.Optional
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;

public class AnimalPanic
extends Behavior<PathfinderMob> {
    private static final int PANIC_MIN_DURATION = 100;
    private static final int PANIC_MAX_DURATION = 120;
    private static final int PANIC_DISTANCE_HORIZONTAL = 5;
    private static final int PANIC_DISTANCE_VERTICAL = 4;
    private final float speedMultiplier;

    public AnimalPanic(float $$0) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.IS_PANICKING, (Object)((Object)MemoryStatus.REGISTERED), MemoryModuleType.HURT_BY, (Object)((Object)MemoryStatus.VALUE_PRESENT)), 100, 120);
        this.speedMultiplier = $$0;
    }

    @Override
    protected boolean canStillUse(ServerLevel $$0, PathfinderMob $$1, long $$2) {
        return true;
    }

    @Override
    protected void tick(ServerLevel $$0, PathfinderMob $$1, long $$2) {
        $$1.getBrain().setMemory(MemoryModuleType.IS_PANICKING, true);
        $$1.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
    }

    @Override
    protected void tick(ServerLevel $$0, PathfinderMob $$1, long $$2) {
        Brain<?> $$3 = $$1.getBrain();
        $$3.eraseMemory(MemoryModuleType.IS_PANICKING);
    }

    @Override
    protected void tick(ServerLevel $$0, PathfinderMob $$1, long $$2) {
        Vec3 $$3;
        if ($$1.getNavigation().isDone() && ($$3 = this.getPanicPos($$1, $$0)) != null) {
            $$1.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget($$3, this.speedMultiplier, 0));
        }
    }

    @Nullable
    private Vec3 getPanicPos(PathfinderMob $$0, ServerLevel $$1) {
        Optional $$2;
        if ($$0.isOnFire() && ($$2 = this.lookForWater($$1, $$0).map(Vec3::atBottomCenterOf)).isPresent()) {
            return (Vec3)$$2.get();
        }
        return LandRandomPos.getPos($$0, 5, 4);
    }

    private Optional<BlockPos> lookForWater(BlockGetter $$0, Entity $$12) {
        BlockPos $$2 = $$12.blockPosition();
        if (!$$0.getBlockState($$2).getCollisionShape($$0, $$2).isEmpty()) {
            return Optional.empty();
        }
        return BlockPos.findClosestMatch($$2, 5, 1, (Predicate<BlockPos>)((Predicate)$$1 -> $$0.getFluidState((BlockPos)$$1).is(FluidTags.WATER)));
    }
}