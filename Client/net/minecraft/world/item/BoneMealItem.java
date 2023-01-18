/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseCoralWallFanBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BoneMealItem
extends Item {
    public static final int GRASS_SPREAD_WIDTH = 3;
    public static final int GRASS_SPREAD_HEIGHT = 1;
    public static final int GRASS_COUNT_MULTIPLIER = 3;

    public BoneMealItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public InteractionResult useOn(UseOnContext $$0) {
        Level $$1 = $$0.getLevel();
        BlockPos $$2 = $$0.getClickedPos();
        Vec3i $$3 = $$2.relative($$0.getClickedFace());
        if (BoneMealItem.growCrop($$0.getItemInHand(), $$1, $$2)) {
            if (!$$1.isClientSide) {
                $$1.levelEvent(1505, $$2, 0);
            }
            return InteractionResult.sidedSuccess($$1.isClientSide);
        }
        BlockState $$4 = $$1.getBlockState($$2);
        boolean $$5 = $$4.isFaceSturdy($$1, $$2, $$0.getClickedFace());
        if ($$5 && BoneMealItem.growWaterPlant($$0.getItemInHand(), $$1, (BlockPos)$$3, $$0.getClickedFace())) {
            if (!$$1.isClientSide) {
                $$1.levelEvent(1505, (BlockPos)$$3, 0);
            }
            return InteractionResult.sidedSuccess($$1.isClientSide);
        }
        return InteractionResult.PASS;
    }

    public static boolean growCrop(ItemStack $$0, Level $$1, BlockPos $$2) {
        BonemealableBlock $$4;
        BlockState $$3 = $$1.getBlockState($$2);
        if ($$3.getBlock() instanceof BonemealableBlock && ($$4 = (BonemealableBlock)((Object)$$3.getBlock())).isValidBonemealTarget($$1, $$2, $$3, $$1.isClientSide)) {
            if ($$1 instanceof ServerLevel) {
                if ($$4.isBonemealSuccess($$1, $$1.random, $$2, $$3)) {
                    $$4.performBonemeal((ServerLevel)$$1, $$1.random, $$2, $$3);
                }
                $$0.shrink(1);
            }
            return true;
        }
        return false;
    }

    public static boolean growWaterPlant(ItemStack $$02, Level $$12, BlockPos $$2, @Nullable Direction $$3) {
        if (!$$12.getBlockState($$2).is(Blocks.WATER) || $$12.getFluidState($$2).getAmount() != 8) {
            return false;
        }
        if (!($$12 instanceof ServerLevel)) {
            return true;
        }
        RandomSource $$4 = $$12.getRandom();
        block0: for (int $$5 = 0; $$5 < 128; ++$$5) {
            BlockPos $$6 = $$2;
            BlockState $$7 = Blocks.SEAGRASS.defaultBlockState();
            for (int $$8 = 0; $$8 < $$5 / 16; ++$$8) {
                if ($$12.getBlockState($$6 = $$6.offset($$4.nextInt(3) - 1, ($$4.nextInt(3) - 1) * $$4.nextInt(3) / 2, $$4.nextInt(3) - 1)).isCollisionShapeFullBlock($$12, $$6)) continue block0;
            }
            Holder $$9 = $$12.getBiome($$6);
            if ($$9.is(BiomeTags.PRODUCES_CORALS_FROM_BONEMEAL)) {
                if ($$5 == 0 && $$3 != null && $$3.getAxis().isHorizontal()) {
                    $$7 = (BlockState)BuiltInRegistries.BLOCK.getTag(BlockTags.WALL_CORALS).flatMap($$1 -> $$1.getRandomElement($$0.random)).map($$0 -> ((Block)$$0.value()).defaultBlockState()).orElse((Object)$$7);
                    if ($$7.hasProperty(BaseCoralWallFanBlock.FACING)) {
                        $$7 = (BlockState)$$7.setValue(BaseCoralWallFanBlock.FACING, $$3);
                    }
                } else if ($$4.nextInt(4) == 0) {
                    $$7 = (BlockState)BuiltInRegistries.BLOCK.getTag(BlockTags.UNDERWATER_BONEMEALS).flatMap($$1 -> $$1.getRandomElement($$0.random)).map($$0 -> ((Block)$$0.value()).defaultBlockState()).orElse((Object)$$7);
                }
            }
            if ($$7.is(BlockTags.WALL_CORALS, (Predicate<BlockBehaviour.BlockStateBase>)((Predicate)$$0 -> $$0.hasProperty(BaseCoralWallFanBlock.FACING)))) {
                for (int $$10 = 0; !$$7.canSurvive($$12, $$6) && $$10 < 4; ++$$10) {
                    $$7 = (BlockState)$$7.setValue(BaseCoralWallFanBlock.FACING, Direction.Plane.HORIZONTAL.getRandomDirection($$4));
                }
            }
            if (!$$7.canSurvive($$12, $$6)) continue;
            BlockState $$11 = $$12.getBlockState($$6);
            if ($$11.is(Blocks.WATER) && $$12.getFluidState($$6).getAmount() == 8) {
                $$12.setBlock($$6, $$7, 3);
                continue;
            }
            if (!$$11.is(Blocks.SEAGRASS) || $$4.nextInt(10) != 0) continue;
            ((BonemealableBlock)((Object)Blocks.SEAGRASS)).performBonemeal((ServerLevel)$$12, $$4, $$6, $$11);
        }
        $$02.shrink(1);
        return true;
    }

    public static void addGrowthParticles(LevelAccessor $$0, BlockPos $$1, int $$2) {
        double $$7;
        BlockState $$3;
        if ($$2 == 0) {
            $$2 = 15;
        }
        if (($$3 = $$0.getBlockState((BlockPos)$$1)).isAir()) {
            return;
        }
        double $$4 = 0.5;
        if ($$3.is(Blocks.WATER)) {
            $$2 *= 3;
            double $$5 = 1.0;
            $$4 = 3.0;
        } else if ($$3.isSolidRender($$0, (BlockPos)$$1)) {
            $$1 = ((BlockPos)$$1).above();
            $$2 *= 3;
            $$4 = 3.0;
            double $$6 = 1.0;
        } else {
            $$7 = $$3.getShape($$0, (BlockPos)$$1).max(Direction.Axis.Y);
        }
        $$0.addParticle(ParticleTypes.HAPPY_VILLAGER, (double)$$1.getX() + 0.5, (double)$$1.getY() + 0.5, (double)$$1.getZ() + 0.5, 0.0, 0.0, 0.0);
        RandomSource $$8 = $$0.getRandom();
        for (int $$9 = 0; $$9 < $$2; ++$$9) {
            double $$16;
            double $$15;
            double $$10 = $$8.nextGaussian() * 0.02;
            double $$11 = $$8.nextGaussian() * 0.02;
            double $$12 = $$8.nextGaussian() * 0.02;
            double $$13 = 0.5 - $$4;
            double $$14 = (double)$$1.getX() + $$13 + $$8.nextDouble() * $$4 * 2.0;
            if ($$0.getBlockState((BlockPos)new BlockPos($$14, $$15 = (double)$$1.getY() + $$8.nextDouble() * $$7, $$16 = (double)$$1.getZ() + $$13 + $$8.nextDouble() * $$4 * 2.0).below()).isAir()) continue;
            $$0.addParticle(ParticleTypes.HAPPY_VILLAGER, $$14, $$15, $$16, $$10, $$11, $$12);
        }
    }
}