/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CropBlock
extends BushBlock
implements BonemealableBlock {
    public static final int MAX_AGE = 7;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_7;
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0), Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)};

    protected CropBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(this.getAgeProperty(), 0));
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE_BY_AGE[$$0.getValue(this.getAgeProperty())];
    }

    @Override
    protected boolean mayPlaceOn(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return $$0.is(Blocks.FARMLAND);
    }

    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    public int getMaxAge() {
        return 7;
    }

    protected int getAge(BlockState $$0) {
        return $$0.getValue(this.getAgeProperty());
    }

    public BlockState getStateForAge(int $$0) {
        return (BlockState)this.defaultBlockState().setValue(this.getAgeProperty(), $$0);
    }

    public boolean isMaxAge(BlockState $$0) {
        return $$0.getValue(this.getAgeProperty()) >= this.getMaxAge();
    }

    @Override
    public boolean isRandomlyTicking(BlockState $$0) {
        return !this.isMaxAge($$0);
    }

    @Override
    public void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        float $$5;
        int $$4;
        if ($$1.getRawBrightness($$2, 0) >= 9 && ($$4 = this.getAge($$0)) < this.getMaxAge() && $$3.nextInt((int)(25.0f / ($$5 = CropBlock.getGrowthSpeed(this, $$1, $$2))) + 1) == 0) {
            $$1.setBlock($$2, this.getStateForAge($$4 + 1), 2);
        }
    }

    public void growCrops(Level $$0, BlockPos $$1, BlockState $$2) {
        int $$4;
        int $$3 = this.getAge($$2) + this.getBonemealAgeIncrease($$0);
        if ($$3 > ($$4 = this.getMaxAge())) {
            $$3 = $$4;
        }
        $$0.setBlock($$1, this.getStateForAge($$3), 2);
    }

    protected int getBonemealAgeIncrease(Level $$0) {
        return Mth.nextInt($$0.random, 2, 5);
    }

    protected static float getGrowthSpeed(Block $$0, BlockGetter $$1, BlockPos $$2) {
        boolean $$14;
        float $$3 = 1.0f;
        Vec3i $$4 = $$2.below();
        for (int $$5 = -1; $$5 <= 1; ++$$5) {
            for (int $$6 = -1; $$6 <= 1; ++$$6) {
                float $$7 = 0.0f;
                BlockState $$8 = $$1.getBlockState(((BlockPos)$$4).offset($$5, 0, $$6));
                if ($$8.is(Blocks.FARMLAND)) {
                    $$7 = 1.0f;
                    if ($$8.getValue(FarmBlock.MOISTURE) > 0) {
                        $$7 = 3.0f;
                    }
                }
                if ($$5 != 0 || $$6 != 0) {
                    $$7 /= 4.0f;
                }
                $$3 += $$7;
            }
        }
        Vec3i $$9 = $$2.north();
        Vec3i $$10 = $$2.south();
        Vec3i $$11 = $$2.west();
        Vec3i $$12 = $$2.east();
        boolean $$13 = $$1.getBlockState((BlockPos)$$11).is($$0) || $$1.getBlockState((BlockPos)$$12).is($$0);
        boolean bl = $$14 = $$1.getBlockState((BlockPos)$$9).is($$0) || $$1.getBlockState((BlockPos)$$10).is($$0);
        if ($$13 && $$14) {
            $$3 /= 2.0f;
        } else {
            boolean $$15;
            boolean bl2 = $$15 = $$1.getBlockState((BlockPos)((BlockPos)$$11).north()).is($$0) || $$1.getBlockState((BlockPos)((BlockPos)$$12).north()).is($$0) || $$1.getBlockState((BlockPos)((BlockPos)$$12).south()).is($$0) || $$1.getBlockState((BlockPos)((BlockPos)$$11).south()).is($$0);
            if ($$15) {
                $$3 /= 2.0f;
            }
        }
        return $$3;
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        return ($$1.getRawBrightness($$2, 0) >= 8 || $$1.canSeeSky($$2)) && super.canSurvive($$0, $$1, $$2);
    }

    @Override
    public void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3) {
        if ($$3 instanceof Ravager && $$1.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            $$1.destroyBlock($$2, true, $$3);
        }
        super.entityInside($$0, $$1, $$2, $$3);
    }

    protected ItemLike getBaseSeedId() {
        return Items.WHEAT_SEEDS;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter $$0, BlockPos $$1, BlockState $$2) {
        return new ItemStack(this.getBaseSeedId());
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader $$0, BlockPos $$1, BlockState $$2, boolean $$3) {
        return !this.isMaxAge($$2);
    }

    @Override
    public boolean isBonemealSuccess(Level $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel $$0, RandomSource $$1, BlockPos $$2, BlockState $$3) {
        this.growCrops($$0, $$2, $$3);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(AGE);
    }
}