/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Supplier
 */
package net.minecraft.world.level.block;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StemBlock
extends BushBlock
implements BonemealableBlock {
    public static final int MAX_AGE = 7;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_7;
    protected static final float AABB_OFFSET = 1.0f;
    protected static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{Block.box(7.0, 0.0, 7.0, 9.0, 2.0, 9.0), Block.box(7.0, 0.0, 7.0, 9.0, 4.0, 9.0), Block.box(7.0, 0.0, 7.0, 9.0, 6.0, 9.0), Block.box(7.0, 0.0, 7.0, 9.0, 8.0, 9.0), Block.box(7.0, 0.0, 7.0, 9.0, 10.0, 9.0), Block.box(7.0, 0.0, 7.0, 9.0, 12.0, 9.0), Block.box(7.0, 0.0, 7.0, 9.0, 14.0, 9.0), Block.box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0)};
    private final StemGrownBlock fruit;
    private final Supplier<Item> seedSupplier;

    protected StemBlock(StemGrownBlock $$0, Supplier<Item> $$1, BlockBehaviour.Properties $$2) {
        super($$2);
        this.fruit = $$0;
        this.seedSupplier = $$1;
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE_BY_AGE[$$0.getValue(AGE)];
    }

    @Override
    protected boolean mayPlaceOn(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return $$0.is(Blocks.FARMLAND);
    }

    @Override
    public void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if ($$1.getRawBrightness($$2, 0) < 9) {
            return;
        }
        float $$4 = CropBlock.getGrowthSpeed(this, $$1, $$2);
        if ($$3.nextInt((int)(25.0f / $$4) + 1) == 0) {
            int $$5 = $$0.getValue(AGE);
            if ($$5 < 7) {
                $$0 = (BlockState)$$0.setValue(AGE, $$5 + 1);
                $$1.setBlock($$2, $$0, 2);
            } else {
                Direction $$6 = Direction.Plane.HORIZONTAL.getRandomDirection($$3);
                Vec3i $$7 = $$2.relative($$6);
                BlockState $$8 = $$1.getBlockState((BlockPos)((BlockPos)$$7).below());
                if ($$1.getBlockState((BlockPos)$$7).isAir() && ($$8.is(Blocks.FARMLAND) || $$8.is(BlockTags.DIRT))) {
                    $$1.setBlockAndUpdate((BlockPos)$$7, this.fruit.defaultBlockState());
                    $$1.setBlockAndUpdate($$2, (BlockState)this.fruit.getAttachedStem().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, $$6));
                }
            }
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter $$0, BlockPos $$1, BlockState $$2) {
        return new ItemStack((ItemLike)this.seedSupplier.get());
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
        return $$2.getValue(AGE) != 7;
    }

    @Override
    public boolean isBonemealSuccess(Level $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        int $$4 = Math.min((int)7, (int)($$3.getValue(AGE) + Mth.nextInt($$0.random, 2, 5)));
        BlockState $$5 = (BlockState)$$3.setValue(AGE, $$4);
        $$0.setBlock($$2, $$5, 2);
        if ($$4 == 7) {
            $$5.randomTick($$0, $$2, $$0.random);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(AGE);
    }

    public StemGrownBlock getFruit() {
        return this.fruit;
    }
}