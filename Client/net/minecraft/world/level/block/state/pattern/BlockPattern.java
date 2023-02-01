/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.MoreObjects
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  java.lang.IllegalArgumentException
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 *  net.minecraft.core.BlockPos
 */
package net.minecraft.world.level.block.state.pattern;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public class BlockPattern {
    private final Predicate<BlockInWorld>[][][] pattern;
    private final int depth;
    private final int height;
    private final int width;

    public BlockPattern(Predicate<BlockInWorld>[][][] $$0) {
        this.pattern = $$0;
        this.depth = $$0.length;
        if (this.depth > 0) {
            this.height = $$0[0].length;
            this.width = this.height > 0 ? $$0[0][0].length : 0;
        } else {
            this.height = 0;
            this.width = 0;
        }
    }

    public int getDepth() {
        return this.depth;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    @VisibleForTesting
    public Predicate<BlockInWorld>[][][] getPattern() {
        return this.pattern;
    }

    @Nullable
    @VisibleForTesting
    public BlockPatternMatch matches(LevelReader $$0, BlockPos $$1, Direction $$2, Direction $$3) {
        LoadingCache<BlockPos, BlockInWorld> $$4 = BlockPattern.createLevelCache($$0, false);
        return this.matches($$1, $$2, $$3, $$4);
    }

    @Nullable
    private BlockPatternMatch matches(BlockPos $$0, Direction $$1, Direction $$2, LoadingCache<BlockPos, BlockInWorld> $$3) {
        for (int $$4 = 0; $$4 < this.width; ++$$4) {
            for (int $$5 = 0; $$5 < this.height; ++$$5) {
                for (int $$6 = 0; $$6 < this.depth; ++$$6) {
                    if (this.pattern[$$6][$$5][$$4].test((Object)((BlockInWorld)$$3.getUnchecked((Object)BlockPattern.translateAndRotate($$0, $$1, $$2, $$4, $$5, $$6))))) continue;
                    return null;
                }
            }
        }
        return new BlockPatternMatch($$0, $$1, $$2, $$3, this.width, this.height, this.depth);
    }

    @Nullable
    public BlockPatternMatch find(LevelReader $$0, BlockPos $$1) {
        LoadingCache<BlockPos, BlockInWorld> $$2 = BlockPattern.createLevelCache($$0, false);
        int $$3 = Math.max((int)Math.max((int)this.width, (int)this.height), (int)this.depth);
        for (BlockPos $$4 : BlockPos.betweenClosed((BlockPos)$$1, (BlockPos)$$1.offset($$3 - 1, $$3 - 1, $$3 - 1))) {
            for (Direction $$5 : Direction.values()) {
                for (Direction $$6 : Direction.values()) {
                    BlockPatternMatch $$7;
                    if ($$6 == $$5 || $$6 == $$5.getOpposite() || ($$7 = this.matches($$4, $$5, $$6, $$2)) == null) continue;
                    return $$7;
                }
            }
        }
        return null;
    }

    public static LoadingCache<BlockPos, BlockInWorld> createLevelCache(LevelReader $$0, boolean $$1) {
        return CacheBuilder.newBuilder().build((CacheLoader)new BlockCacheLoader($$0, $$1));
    }

    protected static BlockPos translateAndRotate(BlockPos $$0, Direction $$1, Direction $$2, int $$3, int $$4, int $$5) {
        if ($$1 == $$2 || $$1 == $$2.getOpposite()) {
            throw new IllegalArgumentException("Invalid forwards & up combination");
        }
        Vec3i $$6 = new Vec3i($$1.getStepX(), $$1.getStepY(), $$1.getStepZ());
        Vec3i $$7 = new Vec3i($$2.getStepX(), $$2.getStepY(), $$2.getStepZ());
        Vec3i $$8 = $$6.cross($$7);
        return $$0.offset($$7.getX() * -$$4 + $$8.getX() * $$3 + $$6.getX() * $$5, $$7.getY() * -$$4 + $$8.getY() * $$3 + $$6.getY() * $$5, $$7.getZ() * -$$4 + $$8.getZ() * $$3 + $$6.getZ() * $$5);
    }

    public static class BlockPatternMatch {
        private final BlockPos frontTopLeft;
        private final Direction forwards;
        private final Direction up;
        private final LoadingCache<BlockPos, BlockInWorld> cache;
        private final int width;
        private final int height;
        private final int depth;

        public BlockPatternMatch(BlockPos $$0, Direction $$1, Direction $$2, LoadingCache<BlockPos, BlockInWorld> $$3, int $$4, int $$5, int $$6) {
            this.frontTopLeft = $$0;
            this.forwards = $$1;
            this.up = $$2;
            this.cache = $$3;
            this.width = $$4;
            this.height = $$5;
            this.depth = $$6;
        }

        public BlockPos getFrontTopLeft() {
            return this.frontTopLeft;
        }

        public Direction getForwards() {
            return this.forwards;
        }

        public Direction getUp() {
            return this.up;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        public int getDepth() {
            return this.depth;
        }

        public BlockInWorld getBlock(int $$0, int $$1, int $$2) {
            return (BlockInWorld)this.cache.getUnchecked((Object)BlockPattern.translateAndRotate(this.frontTopLeft, this.getForwards(), this.getUp(), $$0, $$1, $$2));
        }

        public String toString() {
            return MoreObjects.toStringHelper((Object)this).add("up", (Object)this.up).add("forwards", (Object)this.forwards).add("frontTopLeft", (Object)this.frontTopLeft).toString();
        }
    }

    static class BlockCacheLoader
    extends CacheLoader<BlockPos, BlockInWorld> {
        private final LevelReader level;
        private final boolean loadChunks;

        public BlockCacheLoader(LevelReader $$0, boolean $$1) {
            this.level = $$0;
            this.loadChunks = $$1;
        }

        public BlockInWorld load(BlockPos $$0) {
            return new BlockInWorld(this.level, $$0, this.loadChunks);
        }
    }
}