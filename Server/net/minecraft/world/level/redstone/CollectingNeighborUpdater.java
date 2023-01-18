/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayDeque
 *  java.util.ArrayList
 *  java.util.List
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.redstone;

import com.mojang.logging.LogUtils;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.NeighborUpdater;
import org.slf4j.Logger;

public class CollectingNeighborUpdater
implements NeighborUpdater {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Level level;
    private final int maxChainedNeighborUpdates;
    private final ArrayDeque<NeighborUpdates> stack = new ArrayDeque();
    private final List<NeighborUpdates> addedThisLayer = new ArrayList();
    private int count = 0;

    public CollectingNeighborUpdater(Level $$0, int $$1) {
        this.level = $$0;
        this.maxChainedNeighborUpdates = $$1;
    }

    @Override
    public void shapeUpdate(Direction $$0, BlockState $$1, BlockPos $$2, BlockPos $$3, int $$4, int $$5) {
        this.addAndRun($$2, new ShapeUpdate($$0, $$1, $$2.immutable(), $$3.immutable(), $$4));
    }

    @Override
    public void neighborChanged(BlockPos $$0, Block $$1, BlockPos $$2) {
        this.addAndRun($$0, new SimpleNeighborUpdate($$0, $$1, $$2.immutable()));
    }

    @Override
    public void neighborChanged(BlockState $$0, BlockPos $$1, Block $$2, BlockPos $$3, boolean $$4) {
        this.addAndRun($$1, new FullNeighborUpdate($$0, $$1.immutable(), $$2, $$3.immutable(), $$4));
    }

    @Override
    public void updateNeighborsAtExceptFromFacing(BlockPos $$0, Block $$1, @Nullable Direction $$2) {
        this.addAndRun($$0, new MultiNeighborUpdate($$0.immutable(), $$1, $$2));
    }

    private void addAndRun(BlockPos $$0, NeighborUpdates $$1) {
        boolean $$2 = this.count > 0;
        boolean $$3 = this.maxChainedNeighborUpdates >= 0 && this.count >= this.maxChainedNeighborUpdates;
        ++this.count;
        if (!$$3) {
            if ($$2) {
                this.addedThisLayer.add((Object)$$1);
            } else {
                this.stack.push((Object)$$1);
            }
        } else if (this.count - 1 == this.maxChainedNeighborUpdates) {
            LOGGER.error("Too many chained neighbor updates. Skipping the rest. First skipped position: " + $$0.toShortString());
        }
        if (!$$2) {
            this.runUpdates();
        }
    }

    private void runUpdates() {
        try {
            block3: while (!this.stack.isEmpty() || !this.addedThisLayer.isEmpty()) {
                for (int $$0 = this.addedThisLayer.size() - 1; $$0 >= 0; --$$0) {
                    this.stack.push((Object)((NeighborUpdates)this.addedThisLayer.get($$0)));
                }
                this.addedThisLayer.clear();
                NeighborUpdates $$1 = (NeighborUpdates)this.stack.peek();
                while (this.addedThisLayer.isEmpty()) {
                    if ($$1.runNext(this.level)) continue;
                    this.stack.pop();
                    continue block3;
                }
            }
        }
        finally {
            this.stack.clear();
            this.addedThisLayer.clear();
            this.count = 0;
        }
    }

    record ShapeUpdate(Direction direction, BlockState state, BlockPos pos, BlockPos neighborPos, int updateFlags) implements NeighborUpdates
    {
        @Override
        public boolean runNext(Level $$0) {
            NeighborUpdater.executeShapeUpdate($$0, this.direction, this.state, this.pos, this.neighborPos, this.updateFlags, 512);
            return false;
        }
    }

    static interface NeighborUpdates {
        public boolean runNext(Level var1);
    }

    record SimpleNeighborUpdate(BlockPos pos, Block block, BlockPos neighborPos) implements NeighborUpdates
    {
        @Override
        public boolean runNext(Level $$0) {
            BlockState $$1 = $$0.getBlockState(this.pos);
            NeighborUpdater.executeUpdate($$0, $$1, this.pos, this.block, this.neighborPos, false);
            return false;
        }
    }

    record FullNeighborUpdate(BlockState state, BlockPos pos, Block block, BlockPos neighborPos, boolean movedByPiston) implements NeighborUpdates
    {
        @Override
        public boolean runNext(Level $$0) {
            NeighborUpdater.executeUpdate($$0, this.state, this.pos, this.block, this.neighborPos, this.movedByPiston);
            return false;
        }
    }

    static final class MultiNeighborUpdate
    implements NeighborUpdates {
        private final BlockPos sourcePos;
        private final Block sourceBlock;
        @Nullable
        private final Direction skipDirection;
        private int idx = 0;

        MultiNeighborUpdate(BlockPos $$0, Block $$1, @Nullable Direction $$2) {
            this.sourcePos = $$0;
            this.sourceBlock = $$1;
            this.skipDirection = $$2;
            if (NeighborUpdater.UPDATE_ORDER[this.idx] == $$2) {
                ++this.idx;
            }
        }

        @Override
        public boolean runNext(Level $$0) {
            Vec3i $$1 = this.sourcePos.relative(NeighborUpdater.UPDATE_ORDER[this.idx++]);
            BlockState $$2 = $$0.getBlockState((BlockPos)$$1);
            $$2.neighborChanged($$0, (BlockPos)$$1, this.sourceBlock, this.sourcePos, false);
            if (this.idx < NeighborUpdater.UPDATE_ORDER.length && NeighborUpdater.UPDATE_ORDER[this.idx] == this.skipDirection) {
                ++this.idx;
            }
            return this.idx < NeighborUpdater.UPDATE_ORDER.length;
        }
    }
}