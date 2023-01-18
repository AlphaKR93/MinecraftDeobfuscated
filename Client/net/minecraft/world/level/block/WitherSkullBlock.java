/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockMaterialPredicate;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.material.Material;

public class WitherSkullBlock
extends SkullBlock {
    @Nullable
    private static BlockPattern witherPatternFull;
    @Nullable
    private static BlockPattern witherPatternBase;

    protected WitherSkullBlock(BlockBehaviour.Properties $$0) {
        super(SkullBlock.Types.WITHER_SKELETON, $$0);
    }

    @Override
    public void setPlacedBy(Level $$0, BlockPos $$1, BlockState $$2, @Nullable LivingEntity $$3, ItemStack $$4) {
        super.setPlacedBy($$0, $$1, $$2, $$3, $$4);
        BlockEntity $$5 = $$0.getBlockEntity($$1);
        if ($$5 instanceof SkullBlockEntity) {
            WitherSkullBlock.checkSpawn($$0, $$1, (SkullBlockEntity)$$5);
        }
    }

    public static void checkSpawn(Level $$0, BlockPos $$1, SkullBlockEntity $$2) {
        boolean $$4;
        if ($$0.isClientSide) {
            return;
        }
        BlockState $$3 = $$2.getBlockState();
        boolean bl = $$4 = $$3.is(Blocks.WITHER_SKELETON_SKULL) || $$3.is(Blocks.WITHER_SKELETON_WALL_SKULL);
        if (!$$4 || $$1.getY() < $$0.getMinBuildHeight() || $$0.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }
        BlockPattern.BlockPatternMatch $$5 = WitherSkullBlock.getOrCreateWitherFull().find($$0, $$1);
        if ($$5 == null) {
            return;
        }
        WitherBoss $$6 = EntityType.WITHER.create($$0);
        if ($$6 != null) {
            CarvedPumpkinBlock.clearPatternBlocks($$0, $$5);
            BlockPos $$7 = $$5.getBlock(1, 2, 0).getPos();
            $$6.moveTo((double)$$7.getX() + 0.5, (double)$$7.getY() + 0.55, (double)$$7.getZ() + 0.5, $$5.getForwards().getAxis() == Direction.Axis.X ? 0.0f : 90.0f, 0.0f);
            $$6.yBodyRot = $$5.getForwards().getAxis() == Direction.Axis.X ? 0.0f : 90.0f;
            $$6.makeInvulnerable();
            for (ServerPlayer $$8 : $$0.getEntitiesOfClass(ServerPlayer.class, $$6.getBoundingBox().inflate(50.0))) {
                CriteriaTriggers.SUMMONED_ENTITY.trigger($$8, $$6);
            }
            $$0.addFreshEntity($$6);
            CarvedPumpkinBlock.updatePatternBlocks($$0, $$5);
        }
    }

    public static boolean canSpawnMob(Level $$0, BlockPos $$1, ItemStack $$2) {
        if ($$2.is(Items.WITHER_SKELETON_SKULL) && $$1.getY() >= $$0.getMinBuildHeight() + 2 && $$0.getDifficulty() != Difficulty.PEACEFUL && !$$0.isClientSide) {
            return WitherSkullBlock.getOrCreateWitherBase().find($$0, $$1) != null;
        }
        return false;
    }

    private static BlockPattern getOrCreateWitherFull() {
        if (witherPatternFull == null) {
            witherPatternFull = BlockPatternBuilder.start().aisle("^^^", "###", "~#~").where('#', (Predicate<BlockInWorld>)((Predicate)$$0 -> $$0.getState().is(BlockTags.WITHER_SUMMON_BASE_BLOCKS))).where('^', BlockInWorld.hasState((Predicate<BlockState>)BlockStatePredicate.forBlock(Blocks.WITHER_SKELETON_SKULL).or(BlockStatePredicate.forBlock(Blocks.WITHER_SKELETON_WALL_SKULL)))).where('~', BlockInWorld.hasState(BlockMaterialPredicate.forMaterial(Material.AIR))).build();
        }
        return witherPatternFull;
    }

    private static BlockPattern getOrCreateWitherBase() {
        if (witherPatternBase == null) {
            witherPatternBase = BlockPatternBuilder.start().aisle("   ", "###", "~#~").where('#', (Predicate<BlockInWorld>)((Predicate)$$0 -> $$0.getState().is(BlockTags.WITHER_SUMMON_BASE_BLOCKS))).where('~', BlockInWorld.hasState(BlockMaterialPredicate.forMaterial(Material.AIR))).build();
        }
        return witherPatternBase;
    }
}