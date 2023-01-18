/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChorusPlantBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class ChorusFlowerBlock
extends Block {
    public static final int DEAD_AGE = 5;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_5;
    private final ChorusPlantBlock plant;

    protected ChorusFlowerBlock(ChorusPlantBlock $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.plant = $$0;
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if (!$$0.canSurvive($$1, $$2)) {
            $$1.destroyBlock($$2, true);
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState $$0) {
        return $$0.getValue(AGE) < 5;
    }

    @Override
    public void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        Vec3i $$4 = $$2.above();
        if (!$$1.isEmptyBlock((BlockPos)$$4) || $$4.getY() >= $$1.getMaxBuildHeight()) {
            return;
        }
        int $$5 = $$0.getValue(AGE);
        if ($$5 >= 5) {
            return;
        }
        boolean $$6 = false;
        boolean $$7 = false;
        BlockState $$8 = $$1.getBlockState((BlockPos)$$2.below());
        if ($$8.is(Blocks.END_STONE)) {
            $$6 = true;
        } else if ($$8.is(this.plant)) {
            int $$9 = 1;
            for (int $$10 = 0; $$10 < 4; ++$$10) {
                BlockState $$11 = $$1.getBlockState((BlockPos)$$2.below($$9 + 1));
                if ($$11.is(this.plant)) {
                    ++$$9;
                    continue;
                }
                if (!$$11.is(Blocks.END_STONE)) break;
                $$7 = true;
                break;
            }
            if ($$9 < 2 || $$9 <= $$3.nextInt($$7 ? 5 : 4)) {
                $$6 = true;
            }
        } else if ($$8.isAir()) {
            $$6 = true;
        }
        if ($$6 && ChorusFlowerBlock.allNeighborsEmpty($$1, (BlockPos)$$4, null) && $$1.isEmptyBlock((BlockPos)$$2.above(2))) {
            $$1.setBlock($$2, this.plant.getStateForPlacement($$1, $$2), 2);
            this.placeGrownFlower($$1, (BlockPos)$$4, $$5);
        } else if ($$5 < 4) {
            int $$12 = $$3.nextInt(4);
            if ($$7) {
                ++$$12;
            }
            boolean $$13 = false;
            for (int $$14 = 0; $$14 < $$12; ++$$14) {
                Direction $$15 = Direction.Plane.HORIZONTAL.getRandomDirection($$3);
                Vec3i $$16 = $$2.relative($$15);
                if (!$$1.isEmptyBlock((BlockPos)$$16) || !$$1.isEmptyBlock((BlockPos)((BlockPos)$$16).below()) || !ChorusFlowerBlock.allNeighborsEmpty($$1, (BlockPos)$$16, $$15.getOpposite())) continue;
                this.placeGrownFlower($$1, (BlockPos)$$16, $$5 + 1);
                $$13 = true;
            }
            if ($$13) {
                $$1.setBlock($$2, this.plant.getStateForPlacement($$1, $$2), 2);
            } else {
                this.placeDeadFlower($$1, $$2);
            }
        } else {
            this.placeDeadFlower($$1, $$2);
        }
    }

    private void placeGrownFlower(Level $$0, BlockPos $$1, int $$2) {
        $$0.setBlock($$1, (BlockState)this.defaultBlockState().setValue(AGE, $$2), 2);
        $$0.levelEvent(1033, $$1, 0);
    }

    private void placeDeadFlower(Level $$0, BlockPos $$1) {
        $$0.setBlock($$1, (BlockState)this.defaultBlockState().setValue(AGE, 5), 2);
        $$0.levelEvent(1034, $$1, 0);
    }

    private static boolean allNeighborsEmpty(LevelReader $$0, BlockPos $$1, @Nullable Direction $$2) {
        for (Direction $$3 : Direction.Plane.HORIZONTAL) {
            if ($$3 == $$2 || $$0.isEmptyBlock((BlockPos)$$1.relative($$3))) continue;
            return false;
        }
        return true;
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$1 != Direction.UP && !$$0.canSurvive($$3, $$4)) {
            $$3.scheduleTick($$4, this, 1);
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        BlockState $$3 = $$1.getBlockState((BlockPos)$$2.below());
        if ($$3.is(this.plant) || $$3.is(Blocks.END_STONE)) {
            return true;
        }
        if (!$$3.isAir()) {
            return false;
        }
        boolean $$4 = false;
        for (Direction $$5 : Direction.Plane.HORIZONTAL) {
            BlockState $$6 = $$1.getBlockState((BlockPos)$$2.relative($$5));
            if ($$6.is(this.plant)) {
                if ($$4) {
                    return false;
                }
                $$4 = true;
                continue;
            }
            if ($$6.isAir()) continue;
            return false;
        }
        return $$4;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(AGE);
    }

    public static void generatePlant(LevelAccessor $$0, BlockPos $$1, RandomSource $$2, int $$3) {
        $$0.setBlock($$1, ((ChorusPlantBlock)Blocks.CHORUS_PLANT).getStateForPlacement($$0, $$1), 2);
        ChorusFlowerBlock.growTreeRecursive($$0, $$1, $$2, $$1, $$3, 0);
    }

    private static void growTreeRecursive(LevelAccessor $$0, BlockPos $$1, RandomSource $$2, BlockPos $$3, int $$4, int $$5) {
        ChorusPlantBlock $$6 = (ChorusPlantBlock)Blocks.CHORUS_PLANT;
        int $$7 = $$2.nextInt(4) + 1;
        if ($$5 == 0) {
            ++$$7;
        }
        for (int $$8 = 0; $$8 < $$7; ++$$8) {
            Vec3i $$9 = $$1.above($$8 + 1);
            if (!ChorusFlowerBlock.allNeighborsEmpty($$0, (BlockPos)$$9, null)) {
                return;
            }
            $$0.setBlock((BlockPos)$$9, $$6.getStateForPlacement($$0, (BlockPos)$$9), 2);
            $$0.setBlock((BlockPos)((BlockPos)$$9).below(), $$6.getStateForPlacement($$0, (BlockPos)((BlockPos)$$9).below()), 2);
        }
        boolean $$10 = false;
        if ($$5 < 4) {
            int $$11 = $$2.nextInt(4);
            if ($$5 == 0) {
                ++$$11;
            }
            for (int $$12 = 0; $$12 < $$11; ++$$12) {
                Direction $$13 = Direction.Plane.HORIZONTAL.getRandomDirection($$2);
                Vec3i $$14 = ((BlockPos)$$1.above($$7)).relative($$13);
                if (Math.abs((int)($$14.getX() - $$3.getX())) >= $$4 || Math.abs((int)($$14.getZ() - $$3.getZ())) >= $$4 || !$$0.isEmptyBlock((BlockPos)$$14) || !$$0.isEmptyBlock((BlockPos)((BlockPos)$$14).below()) || !ChorusFlowerBlock.allNeighborsEmpty($$0, (BlockPos)$$14, $$13.getOpposite())) continue;
                $$10 = true;
                $$0.setBlock((BlockPos)$$14, $$6.getStateForPlacement($$0, (BlockPos)$$14), 2);
                $$0.setBlock((BlockPos)((BlockPos)$$14).relative($$13.getOpposite()), $$6.getStateForPlacement($$0, (BlockPos)((BlockPos)$$14).relative($$13.getOpposite())), 2);
                ChorusFlowerBlock.growTreeRecursive($$0, (BlockPos)$$14, $$2, $$3, $$4, $$5 + 1);
            }
        }
        if (!$$10) {
            $$0.setBlock((BlockPos)$$1.above($$7), (BlockState)Blocks.CHORUS_FLOWER.defaultBlockState().setValue(AGE, 5), 2);
        }
    }

    @Override
    public void onProjectileHit(Level $$0, BlockState $$1, BlockHitResult $$2, Projectile $$3) {
        BlockPos $$4 = $$2.getBlockPos();
        if (!$$0.isClientSide && $$3.mayInteract($$0, $$4) && $$3.getType().is(EntityTypeTags.IMPACT_PROJECTILES)) {
            $$0.destroyBlock($$4, true, $$3);
        }
    }
}