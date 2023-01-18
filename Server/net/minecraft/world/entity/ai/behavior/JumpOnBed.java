/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 *  java.util.Optional
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;

public class JumpOnBed
extends Behavior<Mob> {
    private static final int MAX_TIME_TO_REACH_BED = 100;
    private static final int MIN_JUMPS = 3;
    private static final int MAX_JUMPS = 6;
    private static final int COOLDOWN_BETWEEN_JUMPS = 5;
    private final float speedModifier;
    @Nullable
    private BlockPos targetBed;
    private int remainingTimeToReachBed;
    private int remainingJumps;
    private int remainingCooldownUntilNextJump;

    public JumpOnBed(float $$0) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.NEAREST_BED, (Object)((Object)MemoryStatus.VALUE_PRESENT), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryStatus.VALUE_ABSENT)));
        this.speedModifier = $$0;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel $$0, Mob $$1) {
        return $$1.isBaby() && this.nearBed($$0, $$1);
    }

    @Override
    protected void start(ServerLevel $$0, Mob $$1, long $$22) {
        super.start($$0, $$1, $$22);
        this.getNearestBed($$1).ifPresent($$2 -> {
            this.targetBed = $$2;
            this.remainingTimeToReachBed = 100;
            this.remainingJumps = 3 + $$0.random.nextInt(4);
            this.remainingCooldownUntilNextJump = 0;
            this.startWalkingTowardsBed($$1, (BlockPos)$$2);
        });
    }

    @Override
    protected void stop(ServerLevel $$0, Mob $$1, long $$2) {
        super.stop($$0, $$1, $$2);
        this.targetBed = null;
        this.remainingTimeToReachBed = 0;
        this.remainingJumps = 0;
        this.remainingCooldownUntilNextJump = 0;
    }

    @Override
    protected boolean canStillUse(ServerLevel $$0, Mob $$1, long $$2) {
        return $$1.isBaby() && this.targetBed != null && this.isBed($$0, this.targetBed) && !this.tiredOfWalking($$0, $$1) && !this.tiredOfJumping($$0, $$1);
    }

    @Override
    protected boolean timedOut(long $$0) {
        return false;
    }

    @Override
    protected void start(ServerLevel $$0, Mob $$1, long $$2) {
        if (!this.onOrOverBed($$0, $$1)) {
            --this.remainingTimeToReachBed;
            return;
        }
        if (this.remainingCooldownUntilNextJump > 0) {
            --this.remainingCooldownUntilNextJump;
            return;
        }
        if (this.onBedSurface($$0, $$1)) {
            $$1.getJumpControl().jump();
            --this.remainingJumps;
            this.remainingCooldownUntilNextJump = 5;
        }
    }

    private void startWalkingTowardsBed(Mob $$0, BlockPos $$1) {
        $$0.getBrain().setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget($$1, this.speedModifier, 0));
    }

    private boolean nearBed(ServerLevel $$0, Mob $$1) {
        return this.onOrOverBed($$0, $$1) || this.getNearestBed($$1).isPresent();
    }

    private boolean onOrOverBed(ServerLevel $$0, Mob $$1) {
        BlockPos $$2 = $$1.blockPosition();
        Vec3i $$3 = $$2.below();
        return this.isBed($$0, $$2) || this.isBed($$0, (BlockPos)$$3);
    }

    private boolean onBedSurface(ServerLevel $$0, Mob $$1) {
        return this.isBed($$0, $$1.blockPosition());
    }

    private boolean isBed(ServerLevel $$0, BlockPos $$1) {
        return $$0.getBlockState($$1).is(BlockTags.BEDS);
    }

    private Optional<BlockPos> getNearestBed(Mob $$0) {
        return $$0.getBrain().getMemory(MemoryModuleType.NEAREST_BED);
    }

    private boolean tiredOfWalking(ServerLevel $$0, Mob $$1) {
        return !this.onOrOverBed($$0, $$1) && this.remainingTimeToReachBed <= 0;
    }

    private boolean tiredOfJumping(ServerLevel $$0, Mob $$1) {
        return this.onOrOverBed($$0, $$1) && this.remainingJumps <= 0;
    }
}