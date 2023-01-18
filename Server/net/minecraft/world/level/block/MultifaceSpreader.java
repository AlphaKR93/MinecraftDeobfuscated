/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  java.lang.FunctionalInterface
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;

public class MultifaceSpreader {
    public static final SpreadType[] DEFAULT_SPREAD_ORDER = new SpreadType[]{SpreadType.SAME_POSITION, SpreadType.SAME_PLANE, SpreadType.WRAP_AROUND};
    private final SpreadConfig config;

    public MultifaceSpreader(MultifaceBlock $$0) {
        this(new DefaultSpreaderConfig($$0));
    }

    public MultifaceSpreader(SpreadConfig $$0) {
        this.config = $$0;
    }

    public boolean canSpreadInAnyDirection(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        return Direction.stream().anyMatch($$4 -> this.getSpreadFromFaceTowardDirection($$0, $$1, $$2, $$3, (Direction)$$4, this.config::canSpreadInto).isPresent());
    }

    public Optional<SpreadPos> spreadFromRandomFaceTowardRandomDirection(BlockState $$0, LevelAccessor $$12, BlockPos $$2, RandomSource $$3) {
        return (Optional)Direction.allShuffled($$3).stream().filter($$1 -> this.config.canSpreadFrom($$0, (Direction)$$1)).map($$4 -> this.spreadFromFaceTowardRandomDirection($$0, $$12, $$2, (Direction)$$4, $$3, false)).filter(Optional::isPresent).findFirst().orElse((Object)Optional.empty());
    }

    public long spreadAll(BlockState $$0, LevelAccessor $$12, BlockPos $$2, boolean $$3) {
        return (Long)Direction.stream().filter($$1 -> this.config.canSpreadFrom($$0, (Direction)$$1)).map($$4 -> this.spreadFromFaceTowardAllDirections($$0, $$12, $$2, (Direction)$$4, $$3)).reduce((Object)0L, Long::sum);
    }

    public Optional<SpreadPos> spreadFromFaceTowardRandomDirection(BlockState $$0, LevelAccessor $$1, BlockPos $$2, Direction $$3, RandomSource $$4, boolean $$52) {
        return (Optional)Direction.allShuffled($$4).stream().map($$5 -> this.spreadFromFaceTowardDirection($$0, $$1, $$2, $$3, (Direction)$$5, $$52)).filter(Optional::isPresent).findFirst().orElse((Object)Optional.empty());
    }

    private long spreadFromFaceTowardAllDirections(BlockState $$0, LevelAccessor $$1, BlockPos $$2, Direction $$3, boolean $$4) {
        return Direction.stream().map($$5 -> this.spreadFromFaceTowardDirection($$0, $$1, $$2, $$3, (Direction)$$5, $$4)).filter(Optional::isPresent).count();
    }

    @VisibleForTesting
    public Optional<SpreadPos> spreadFromFaceTowardDirection(BlockState $$0, LevelAccessor $$1, BlockPos $$22, Direction $$3, Direction $$4, boolean $$5) {
        return this.getSpreadFromFaceTowardDirection($$0, $$1, $$22, $$3, $$4, this.config::canSpreadInto).flatMap($$2 -> this.spreadToFace($$1, (SpreadPos)((Object)$$2), $$5));
    }

    public Optional<SpreadPos> getSpreadFromFaceTowardDirection(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3, Direction $$4, SpreadPredicate $$5) {
        if ($$4.getAxis() == $$3.getAxis()) {
            return Optional.empty();
        }
        if (!(this.config.isOtherBlockValidAsSource($$0) || this.config.hasFace($$0, $$3) && !this.config.hasFace($$0, $$4))) {
            return Optional.empty();
        }
        for (SpreadType $$6 : this.config.getSpreadTypes()) {
            SpreadPos $$7 = $$6.getSpreadPos($$2, $$4, $$3);
            if (!$$5.test($$1, $$2, $$7)) continue;
            return Optional.of((Object)((Object)$$7));
        }
        return Optional.empty();
    }

    public Optional<SpreadPos> spreadToFace(LevelAccessor $$0, SpreadPos $$1, boolean $$2) {
        BlockState $$3 = $$0.getBlockState($$1.pos());
        if (this.config.placeBlock($$0, $$1, $$3, $$2)) {
            return Optional.of((Object)((Object)$$1));
        }
        return Optional.empty();
    }

    public static class DefaultSpreaderConfig
    implements SpreadConfig {
        protected MultifaceBlock block;

        public DefaultSpreaderConfig(MultifaceBlock $$0) {
            this.block = $$0;
        }

        @Override
        @Nullable
        public BlockState getStateForPlacement(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
            return this.block.getStateForPlacement($$0, $$1, $$2, $$3);
        }

        protected boolean stateCanBeReplaced(BlockGetter $$0, BlockPos $$1, BlockPos $$2, Direction $$3, BlockState $$4) {
            return $$4.isAir() || $$4.is(this.block) || $$4.is(Blocks.WATER) && $$4.getFluidState().isSource();
        }

        @Override
        public boolean canSpreadInto(BlockGetter $$0, BlockPos $$1, SpreadPos $$2) {
            BlockState $$3 = $$0.getBlockState($$2.pos());
            return this.stateCanBeReplaced($$0, $$1, $$2.pos(), $$2.face(), $$3) && this.block.isValidStateForPlacement($$0, $$3, $$2.pos(), $$2.face());
        }
    }

    public static interface SpreadConfig {
        @Nullable
        public BlockState getStateForPlacement(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4);

        public boolean canSpreadInto(BlockGetter var1, BlockPos var2, SpreadPos var3);

        default public SpreadType[] getSpreadTypes() {
            return DEFAULT_SPREAD_ORDER;
        }

        default public boolean hasFace(BlockState $$0, Direction $$1) {
            return MultifaceBlock.hasFace($$0, $$1);
        }

        default public boolean isOtherBlockValidAsSource(BlockState $$0) {
            return false;
        }

        default public boolean canSpreadFrom(BlockState $$0, Direction $$1) {
            return this.isOtherBlockValidAsSource($$0) || this.hasFace($$0, $$1);
        }

        default public boolean placeBlock(LevelAccessor $$0, SpreadPos $$1, BlockState $$2, boolean $$3) {
            BlockState $$4 = this.getStateForPlacement($$2, $$0, $$1.pos(), $$1.face());
            if ($$4 != null) {
                if ($$3) {
                    $$0.getChunk($$1.pos()).markPosForPostprocessing($$1.pos());
                }
                return $$0.setBlock($$1.pos(), $$4, 2);
            }
            return false;
        }
    }

    @FunctionalInterface
    public static interface SpreadPredicate {
        public boolean test(BlockGetter var1, BlockPos var2, SpreadPos var3);
    }

    /*
     * Uses 'sealed' constructs - enablewith --sealed true
     */
    public static enum SpreadType {
        SAME_POSITION{

            @Override
            public SpreadPos getSpreadPos(BlockPos $$0, Direction $$1, Direction $$2) {
                return new SpreadPos($$0, $$1);
            }
        }
        ,
        SAME_PLANE{

            @Override
            public SpreadPos getSpreadPos(BlockPos $$0, Direction $$1, Direction $$2) {
                return new SpreadPos((BlockPos)$$0.relative($$1), $$2);
            }
        }
        ,
        WRAP_AROUND{

            @Override
            public SpreadPos getSpreadPos(BlockPos $$0, Direction $$1, Direction $$2) {
                return new SpreadPos((BlockPos)((BlockPos)$$0.relative($$1)).relative($$2), $$1.getOpposite());
            }
        };


        public abstract SpreadPos getSpreadPos(BlockPos var1, Direction var2, Direction var3);
    }

    public record SpreadPos(BlockPos pos, Direction face) {
    }
}