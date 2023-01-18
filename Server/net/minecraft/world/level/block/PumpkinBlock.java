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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AttachedStemBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.StemGrownBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class PumpkinBlock
extends StemGrownBlock {
    protected PumpkinBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$12, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        ItemStack $$6 = $$3.getItemInHand($$4);
        if ($$6.is(Items.SHEARS)) {
            if (!$$12.isClientSide) {
                Direction $$7 = $$5.getDirection();
                Direction $$8 = $$7.getAxis() == Direction.Axis.Y ? $$3.getDirection().getOpposite() : $$7;
                $$12.playSound(null, $$2, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0f, 1.0f);
                $$12.setBlock($$2, (BlockState)Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, $$8), 11);
                ItemEntity $$9 = new ItemEntity($$12, (double)$$2.getX() + 0.5 + (double)$$8.getStepX() * 0.65, (double)$$2.getY() + 0.1, (double)$$2.getZ() + 0.5 + (double)$$8.getStepZ() * 0.65, new ItemStack(Items.PUMPKIN_SEEDS, 4));
                $$9.setDeltaMovement(0.05 * (double)$$8.getStepX() + $$12.random.nextDouble() * 0.02, 0.05, 0.05 * (double)$$8.getStepZ() + $$12.random.nextDouble() * 0.02);
                $$12.addFreshEntity($$9);
                $$6.hurtAndBreak(1, $$3, $$1 -> $$1.broadcastBreakEvent($$4));
                $$12.gameEvent($$3, GameEvent.SHEAR, $$2);
                $$3.awardStat(Stats.ITEM_USED.get(Items.SHEARS));
            }
            return InteractionResult.sidedSuccess($$12.isClientSide);
        }
        return super.use($$0, $$12, $$2, $$3, $$4, $$5);
    }

    @Override
    public StemBlock getStem() {
        return (StemBlock)Blocks.PUMPKIN_STEM;
    }

    @Override
    public AttachedStemBlock getAttachedStem() {
        return (AttachedStemBlock)Blocks.ATTACHED_PUMPKIN_STEM;
    }
}