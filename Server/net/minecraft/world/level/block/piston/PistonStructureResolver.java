/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Object
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.List
 */
package net.minecraft.world.level.block.piston;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;

public class PistonStructureResolver {
    public static final int MAX_PUSH_DEPTH = 12;
    private final Level level;
    private final BlockPos pistonPos;
    private final boolean extending;
    private final BlockPos startPos;
    private final Direction pushDirection;
    private final List<BlockPos> toPush = Lists.newArrayList();
    private final List<BlockPos> toDestroy = Lists.newArrayList();
    private final Direction pistonDirection;

    public PistonStructureResolver(Level $$0, BlockPos $$1, Direction $$2, boolean $$3) {
        this.level = $$0;
        this.pistonPos = $$1;
        this.pistonDirection = $$2;
        this.extending = $$3;
        if ($$3) {
            this.pushDirection = $$2;
            this.startPos = $$1.relative($$2);
        } else {
            this.pushDirection = $$2.getOpposite();
            this.startPos = $$1.relative($$2, 2);
        }
    }

    public boolean resolve() {
        this.toPush.clear();
        this.toDestroy.clear();
        BlockState $$0 = this.level.getBlockState(this.startPos);
        if (!PistonBaseBlock.isPushable($$0, this.level, this.startPos, this.pushDirection, false, this.pistonDirection)) {
            if (this.extending && $$0.getPistonPushReaction() == PushReaction.DESTROY) {
                this.toDestroy.add((Object)this.startPos);
                return true;
            }
            return false;
        }
        if (!this.addBlockLine(this.startPos, this.pushDirection)) {
            return false;
        }
        for (int $$1 = 0; $$1 < this.toPush.size(); ++$$1) {
            BlockPos $$2 = (BlockPos)this.toPush.get($$1);
            if (!PistonStructureResolver.isSticky(this.level.getBlockState($$2)) || this.addBranchingBlocks($$2)) continue;
            return false;
        }
        return true;
    }

    private static boolean isSticky(BlockState $$0) {
        return $$0.is(Blocks.SLIME_BLOCK) || $$0.is(Blocks.HONEY_BLOCK);
    }

    private static boolean canStickToEachOther(BlockState $$0, BlockState $$1) {
        if ($$0.is(Blocks.HONEY_BLOCK) && $$1.is(Blocks.SLIME_BLOCK)) {
            return false;
        }
        if ($$0.is(Blocks.SLIME_BLOCK) && $$1.is(Blocks.HONEY_BLOCK)) {
            return false;
        }
        return PistonStructureResolver.isSticky($$0) || PistonStructureResolver.isSticky($$1);
    }

    private boolean addBlockLine(BlockPos $$0, Direction $$1) {
        BlockState $$2 = this.level.getBlockState($$0);
        if ($$2.isAir()) {
            return true;
        }
        if (!PistonBaseBlock.isPushable($$2, this.level, $$0, this.pushDirection, false, $$1)) {
            return true;
        }
        if ($$0.equals(this.pistonPos)) {
            return true;
        }
        if (this.toPush.contains((Object)$$0)) {
            return true;
        }
        int $$3 = 1;
        if ($$3 + this.toPush.size() > 12) {
            return false;
        }
        while (PistonStructureResolver.isSticky($$2)) {
            BlockPos $$4 = $$0.relative(this.pushDirection.getOpposite(), $$3);
            BlockState $$5 = $$2;
            $$2 = this.level.getBlockState($$4);
            if ($$2.isAir() || !PistonStructureResolver.canStickToEachOther($$5, $$2) || !PistonBaseBlock.isPushable($$2, this.level, $$4, this.pushDirection, false, this.pushDirection.getOpposite()) || $$4.equals(this.pistonPos)) break;
            if (++$$3 + this.toPush.size() <= 12) continue;
            return false;
        }
        int $$6 = 0;
        for (int $$7 = $$3 - 1; $$7 >= 0; --$$7) {
            this.toPush.add((Object)$$0.relative(this.pushDirection.getOpposite(), $$7));
            ++$$6;
        }
        int $$8 = 1;
        while (true) {
            BlockPos $$9;
            int $$10;
            if (($$10 = this.toPush.indexOf((Object)($$9 = $$0.relative(this.pushDirection, $$8)))) > -1) {
                this.reorderListAtCollision($$6, $$10);
                for (int $$11 = 0; $$11 <= $$10 + $$6; ++$$11) {
                    BlockPos $$12 = (BlockPos)this.toPush.get($$11);
                    if (!PistonStructureResolver.isSticky(this.level.getBlockState($$12)) || this.addBranchingBlocks($$12)) continue;
                    return false;
                }
                return true;
            }
            $$2 = this.level.getBlockState($$9);
            if ($$2.isAir()) {
                return true;
            }
            if (!PistonBaseBlock.isPushable($$2, this.level, $$9, this.pushDirection, true, this.pushDirection) || $$9.equals(this.pistonPos)) {
                return false;
            }
            if ($$2.getPistonPushReaction() == PushReaction.DESTROY) {
                this.toDestroy.add((Object)$$9);
                return true;
            }
            if (this.toPush.size() >= 12) {
                return false;
            }
            this.toPush.add((Object)$$9);
            ++$$6;
            ++$$8;
        }
    }

    private void reorderListAtCollision(int $$0, int $$1) {
        ArrayList $$2 = Lists.newArrayList();
        ArrayList $$3 = Lists.newArrayList();
        ArrayList $$4 = Lists.newArrayList();
        $$2.addAll((Collection)this.toPush.subList(0, $$1));
        $$3.addAll((Collection)this.toPush.subList(this.toPush.size() - $$0, this.toPush.size()));
        $$4.addAll((Collection)this.toPush.subList($$1, this.toPush.size() - $$0));
        this.toPush.clear();
        this.toPush.addAll((Collection)$$2);
        this.toPush.addAll((Collection)$$3);
        this.toPush.addAll((Collection)$$4);
    }

    private boolean addBranchingBlocks(BlockPos $$0) {
        BlockState $$1 = this.level.getBlockState($$0);
        for (Direction $$2 : Direction.values()) {
            Vec3i $$3;
            BlockState $$4;
            if ($$2.getAxis() == this.pushDirection.getAxis() || !PistonStructureResolver.canStickToEachOther($$4 = this.level.getBlockState((BlockPos)($$3 = $$0.relative($$2))), $$1) || this.addBlockLine((BlockPos)$$3, $$2)) continue;
            return false;
        }
        return true;
    }

    public Direction getPushDirection() {
        return this.pushDirection;
    }

    public List<BlockPos> getToPush() {
        return this.toPush;
    }

    public List<BlockPos> getToDestroy() {
        return this.toDestroy;
    }
}