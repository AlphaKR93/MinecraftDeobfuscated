/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ShearsItem
extends Item {
    public ShearsItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public boolean mineBlock(ItemStack $$02, Level $$1, BlockState $$2, BlockPos $$3, LivingEntity $$4) {
        if (!$$1.isClientSide && !$$2.is(BlockTags.FIRE)) {
            $$02.hurtAndBreak(1, $$4, $$0 -> $$0.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        }
        if ($$2.is(BlockTags.LEAVES) || $$2.is(Blocks.COBWEB) || $$2.is(Blocks.GRASS) || $$2.is(Blocks.FERN) || $$2.is(Blocks.DEAD_BUSH) || $$2.is(Blocks.HANGING_ROOTS) || $$2.is(Blocks.VINE) || $$2.is(Blocks.TRIPWIRE) || $$2.is(BlockTags.WOOL)) {
            return true;
        }
        return super.mineBlock($$02, $$1, $$2, $$3, $$4);
    }

    @Override
    public boolean isCorrectToolForDrops(BlockState $$0) {
        return $$0.is(Blocks.COBWEB) || $$0.is(Blocks.REDSTONE_WIRE) || $$0.is(Blocks.TRIPWIRE);
    }

    @Override
    public float getDestroySpeed(ItemStack $$0, BlockState $$1) {
        if ($$1.is(Blocks.COBWEB) || $$1.is(BlockTags.LEAVES)) {
            return 15.0f;
        }
        if ($$1.is(BlockTags.WOOL)) {
            return 5.0f;
        }
        if ($$1.is(Blocks.VINE) || $$1.is(Blocks.GLOW_LICHEN)) {
            return 2.0f;
        }
        return super.getDestroySpeed($$0, $$1);
    }

    @Override
    public InteractionResult useOn(UseOnContext $$0) {
        GrowingPlantHeadBlock $$5;
        BlockPos $$2;
        Level $$12 = $$0.getLevel();
        BlockState $$3 = $$12.getBlockState($$2 = $$0.getClickedPos());
        Block $$4 = $$3.getBlock();
        if ($$4 instanceof GrowingPlantHeadBlock && !($$5 = (GrowingPlantHeadBlock)$$4).isMaxAge($$3)) {
            Player $$6 = $$0.getPlayer();
            ItemStack $$7 = $$0.getItemInHand();
            if ($$6 instanceof ServerPlayer) {
                CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger((ServerPlayer)$$6, $$2, $$7);
            }
            $$12.playSound($$6, $$2, SoundEvents.GROWING_PLANT_CROP, SoundSource.BLOCKS, 1.0f, 1.0f);
            $$12.setBlockAndUpdate($$2, $$5.getMaxAgeState($$3));
            if ($$6 != null) {
                $$7.hurtAndBreak(1, $$6, $$1 -> $$1.broadcastBreakEvent($$0.getHand()));
            }
            return InteractionResult.sidedSuccess($$12.isClientSide);
        }
        return super.useOn($$0);
    }
}