/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class RedStoneOreBlock
extends Block {
    public static final BooleanProperty LIT = RedstoneTorchBlock.LIT;

    public RedStoneOreBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue(LIT, false));
    }

    @Override
    public void attack(BlockState $$0, Level $$1, BlockPos $$2, Player $$3) {
        RedStoneOreBlock.interact($$0, $$1, $$2);
        super.attack($$0, $$1, $$2, $$3);
    }

    @Override
    public void stepOn(Level $$0, BlockPos $$1, BlockState $$2, Entity $$3) {
        if (!$$3.isSteppingCarefully()) {
            RedStoneOreBlock.interact($$2, $$0, $$1);
        }
        super.stepOn($$0, $$1, $$2, $$3);
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        if ($$1.isClientSide) {
            RedStoneOreBlock.spawnParticles($$1, $$2);
        } else {
            RedStoneOreBlock.interact($$0, $$1, $$2);
        }
        ItemStack $$6 = $$3.getItemInHand($$4);
        if ($$6.getItem() instanceof BlockItem && new BlockPlaceContext($$3, $$4, $$6, $$5).canPlace()) {
            return InteractionResult.PASS;
        }
        return InteractionResult.SUCCESS;
    }

    private static void interact(BlockState $$0, Level $$1, BlockPos $$2) {
        RedStoneOreBlock.spawnParticles($$1, $$2);
        if (!$$0.getValue(LIT).booleanValue()) {
            $$1.setBlock($$2, (BlockState)$$0.setValue(LIT, true), 3);
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState $$0) {
        return $$0.getValue(LIT);
    }

    @Override
    public void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if ($$0.getValue(LIT).booleanValue()) {
            $$1.setBlock($$2, (BlockState)$$0.setValue(LIT, false), 3);
        }
    }

    @Override
    public void spawnAfterBreak(BlockState $$0, ServerLevel $$1, BlockPos $$2, ItemStack $$3, boolean $$4) {
        super.spawnAfterBreak($$0, $$1, $$2, $$3, $$4);
        if ($$4 && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, $$3) == 0) {
            int $$5 = 1 + $$1.random.nextInt(5);
            this.popExperience($$1, $$2, $$5);
        }
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        if ($$0.getValue(LIT).booleanValue()) {
            RedStoneOreBlock.spawnParticles($$1, $$2);
        }
    }

    private static void spawnParticles(Level $$0, BlockPos $$1) {
        double $$2 = 0.5625;
        RandomSource $$3 = $$0.random;
        for (Direction $$4 : Direction.values()) {
            Vec3i $$5 = $$1.relative($$4);
            if ($$0.getBlockState((BlockPos)$$5).isSolidRender($$0, (BlockPos)$$5)) continue;
            Direction.Axis $$6 = $$4.getAxis();
            double $$7 = $$6 == Direction.Axis.X ? 0.5 + 0.5625 * (double)$$4.getStepX() : (double)$$3.nextFloat();
            double $$8 = $$6 == Direction.Axis.Y ? 0.5 + 0.5625 * (double)$$4.getStepY() : (double)$$3.nextFloat();
            double $$9 = $$6 == Direction.Axis.Z ? 0.5 + 0.5625 * (double)$$4.getStepZ() : (double)$$3.nextFloat();
            $$0.addParticle(DustParticleOptions.REDSTONE, (double)$$1.getX() + $$7, (double)$$1.getY() + $$8, (double)$$1.getZ() + $$9, 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(LIT);
    }
}