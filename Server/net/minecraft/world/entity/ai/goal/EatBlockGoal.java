/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Enum
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.EnumSet
 *  java.util.function.Predicate
 */
package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;

public class EatBlockGoal
extends Goal {
    private static final int EAT_ANIMATION_TICKS = 40;
    private static final Predicate<BlockState> IS_TALL_GRASS = BlockStatePredicate.forBlock(Blocks.GRASS);
    private final Mob mob;
    private final Level level;
    private int eatAnimationTick;

    public EatBlockGoal(Mob $$0) {
        this.mob = $$0;
        this.level = $$0.level;
        this.setFlags((EnumSet<Goal.Flag>)EnumSet.of((Enum)Goal.Flag.MOVE, (Enum)Goal.Flag.LOOK, (Enum)Goal.Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (this.mob.getRandom().nextInt(this.mob.isBaby() ? 50 : 1000) != 0) {
            return false;
        }
        BlockPos $$0 = this.mob.blockPosition();
        if (IS_TALL_GRASS.test((Object)this.level.getBlockState($$0))) {
            return true;
        }
        return this.level.getBlockState((BlockPos)$$0.below()).is(Blocks.GRASS_BLOCK);
    }

    @Override
    public void start() {
        this.eatAnimationTick = this.adjustedTickDelay(40);
        this.level.broadcastEntityEvent(this.mob, (byte)10);
        this.mob.getNavigation().stop();
    }

    @Override
    public void stop() {
        this.eatAnimationTick = 0;
    }

    @Override
    public boolean canContinueToUse() {
        return this.eatAnimationTick > 0;
    }

    public int getEatAnimationTick() {
        return this.eatAnimationTick;
    }

    @Override
    public void tick() {
        this.eatAnimationTick = Math.max((int)0, (int)(this.eatAnimationTick - 1));
        if (this.eatAnimationTick != this.adjustedTickDelay(4)) {
            return;
        }
        BlockPos $$0 = this.mob.blockPosition();
        if (IS_TALL_GRASS.test((Object)this.level.getBlockState($$0))) {
            if (this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                this.level.destroyBlock($$0, false);
            }
            this.mob.ate();
        } else {
            Vec3i $$1 = $$0.below();
            if (this.level.getBlockState((BlockPos)$$1).is(Blocks.GRASS_BLOCK)) {
                if (this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                    this.level.levelEvent(2001, (BlockPos)$$1, Block.getId(Blocks.GRASS_BLOCK.defaultBlockState()));
                    this.level.setBlock((BlockPos)$$1, Blocks.DIRT.defaultBlockState(), 2);
                }
                this.mob.ate();
            }
        }
    }
}