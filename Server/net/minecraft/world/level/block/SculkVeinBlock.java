/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Collection
 */
package net.minecraft.world.level.block;

import java.util.Collection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.MultifaceSpreader;
import net.minecraft.world.level.block.SculkBehaviour;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;

public class SculkVeinBlock
extends MultifaceBlock
implements SculkBehaviour,
SimpleWaterloggedBlock {
    private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private final MultifaceSpreader veinSpreader = new MultifaceSpreader(new SculkVeinSpreaderConfig(MultifaceSpreader.DEFAULT_SPREAD_ORDER));
    private final MultifaceSpreader sameSpaceSpreader = new MultifaceSpreader(new SculkVeinSpreaderConfig(MultifaceSpreader.SpreadType.SAME_POSITION));

    public SculkVeinBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue(WATERLOGGED, false));
    }

    @Override
    public MultifaceSpreader getSpreader() {
        return this.veinSpreader;
    }

    public MultifaceSpreader getSameSpaceSpreader() {
        return this.sameSpaceSpreader;
    }

    public static boolean regrow(LevelAccessor $$0, BlockPos $$1, BlockState $$2, Collection<Direction> $$3) {
        boolean $$4 = false;
        BlockState $$5 = Blocks.SCULK_VEIN.defaultBlockState();
        for (Direction $$6 : $$3) {
            Vec3i $$7;
            if (!SculkVeinBlock.canAttachTo($$0, $$6, (BlockPos)($$7 = $$1.relative($$6)), $$0.getBlockState((BlockPos)$$7))) continue;
            $$5 = (BlockState)$$5.setValue(SculkVeinBlock.getFaceProperty($$6), true);
            $$4 = true;
        }
        if (!$$4) {
            return false;
        }
        if (!$$2.getFluidState().isEmpty()) {
            $$5 = (BlockState)$$5.setValue(WATERLOGGED, true);
        }
        $$0.setBlock($$1, $$5, 3);
        return true;
    }

    @Override
    public void onDischarged(LevelAccessor $$0, BlockState $$1, BlockPos $$2, RandomSource $$3) {
        if (!$$1.is(this)) {
            return;
        }
        for (Direction $$4 : DIRECTIONS) {
            BooleanProperty $$5 = SculkVeinBlock.getFaceProperty($$4);
            if (!$$1.getValue($$5).booleanValue() || !$$0.getBlockState((BlockPos)$$2.relative($$4)).is(Blocks.SCULK)) continue;
            $$1 = (BlockState)$$1.setValue($$5, false);
        }
        if (!SculkVeinBlock.hasAnyFace($$1)) {
            FluidState $$6 = $$0.getFluidState($$2);
            $$1 = ($$6.isEmpty() ? Blocks.AIR : Blocks.WATER).defaultBlockState();
        }
        $$0.setBlock($$2, $$1, 3);
        SculkBehaviour.super.onDischarged($$0, $$1, $$2, $$3);
    }

    @Override
    public int attemptUseCharge(SculkSpreader.ChargeCursor $$0, LevelAccessor $$1, BlockPos $$2, RandomSource $$3, SculkSpreader $$4, boolean $$5) {
        if ($$5 && this.attemptPlaceSculk($$4, $$1, $$0.getPos(), $$3)) {
            return $$0.getCharge() - 1;
        }
        return $$3.nextInt($$4.chargeDecayRate()) == 0 ? Mth.floor((float)$$0.getCharge() * 0.5f) : $$0.getCharge();
    }

    private boolean attemptPlaceSculk(SculkSpreader $$0, LevelAccessor $$1, BlockPos $$2, RandomSource $$3) {
        BlockState $$4 = $$1.getBlockState($$2);
        TagKey<Block> $$5 = $$0.replaceableBlocks();
        for (Direction $$6 : Direction.allShuffled($$3)) {
            Vec3i $$7;
            BlockState $$8;
            if (!SculkVeinBlock.hasFace($$4, $$6) || !($$8 = $$1.getBlockState((BlockPos)($$7 = $$2.relative($$6)))).is($$5)) continue;
            BlockState $$9 = Blocks.SCULK.defaultBlockState();
            $$1.setBlock((BlockPos)$$7, $$9, 3);
            Block.pushEntitiesUp($$8, $$9, $$1, (BlockPos)$$7);
            $$1.playSound(null, (BlockPos)$$7, SoundEvents.SCULK_BLOCK_SPREAD, SoundSource.BLOCKS, 1.0f, 1.0f);
            this.veinSpreader.spreadAll($$9, $$1, (BlockPos)$$7, $$0.isWorldGeneration());
            Direction $$10 = $$6.getOpposite();
            for (Direction $$11 : DIRECTIONS) {
                Vec3i $$12;
                BlockState $$13;
                if ($$11 == $$10 || !($$13 = $$1.getBlockState((BlockPos)($$12 = ((BlockPos)$$7).relative($$11)))).is(this)) continue;
                this.onDischarged($$1, $$13, (BlockPos)$$12, $$3);
            }
            return true;
        }
        return false;
    }

    public static boolean hasSubstrateAccess(LevelAccessor $$0, BlockState $$1, BlockPos $$2) {
        if (!$$1.is(Blocks.SCULK_VEIN)) {
            return false;
        }
        for (Direction $$3 : DIRECTIONS) {
            if (!SculkVeinBlock.hasFace($$1, $$3) || !$$0.getBlockState((BlockPos)$$2.relative($$3)).is(BlockTags.SCULK_REPLACEABLE)) continue;
            return true;
        }
        return false;
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$3.scheduleTick($$4, Fluids.WATER, Fluids.WATER.getTickDelay($$3));
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        super.createBlockStateDefinition($$0);
        $$0.add(WATERLOGGED);
    }

    @Override
    public boolean canBeReplaced(BlockState $$0, BlockPlaceContext $$1) {
        return !$$1.getItemInHand().is(Items.SCULK_VEIN) || super.canBeReplaced($$0, $$1);
    }

    @Override
    public FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState $$0) {
        return PushReaction.DESTROY;
    }

    class SculkVeinSpreaderConfig
    extends MultifaceSpreader.DefaultSpreaderConfig {
        private final MultifaceSpreader.SpreadType[] spreadTypes;

        public SculkVeinSpreaderConfig(MultifaceSpreader.SpreadType ... $$0) {
            super(SculkVeinBlock.this);
            this.spreadTypes = $$0;
        }

        @Override
        public boolean stateCanBeReplaced(BlockGetter $$0, BlockPos $$1, BlockPos $$2, Direction $$3, BlockState $$4) {
            Vec3i $$6;
            BlockState $$5 = $$0.getBlockState((BlockPos)$$2.relative($$3));
            if ($$5.is(Blocks.SCULK) || $$5.is(Blocks.SCULK_CATALYST) || $$5.is(Blocks.MOVING_PISTON)) {
                return false;
            }
            if ($$1.distManhattan($$2) == 2 && $$0.getBlockState((BlockPos)($$6 = $$1.relative($$3.getOpposite()))).isFaceSturdy($$0, (BlockPos)$$6, $$3)) {
                return false;
            }
            FluidState $$7 = $$4.getFluidState();
            if (!$$7.isEmpty() && !$$7.is(Fluids.WATER)) {
                return false;
            }
            Material $$8 = $$4.getMaterial();
            if ($$8 == Material.FIRE) {
                return false;
            }
            return $$4.canBeReplaced() || super.stateCanBeReplaced($$0, $$1, $$2, $$3, $$4);
        }

        @Override
        public MultifaceSpreader.SpreadType[] getSpreadTypes() {
            return this.spreadTypes;
        }

        @Override
        public boolean isOtherBlockValidAsSource(BlockState $$0) {
            return !$$0.is(Blocks.SCULK_VEIN);
        }
    }
}