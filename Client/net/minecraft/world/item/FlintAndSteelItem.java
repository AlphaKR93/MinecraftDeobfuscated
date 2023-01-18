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
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;

public class FlintAndSteelItem
extends Item {
    public FlintAndSteelItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public InteractionResult useOn(UseOnContext $$0) {
        BlockPos $$3;
        Player $$12 = $$0.getPlayer();
        Level $$2 = $$0.getLevel();
        BlockState $$4 = $$2.getBlockState($$3 = $$0.getClickedPos());
        if (CampfireBlock.canLight($$4) || CandleBlock.canLight($$4) || CandleCakeBlock.canLight($$4)) {
            $$2.playSound($$12, $$3, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0f, $$2.getRandom().nextFloat() * 0.4f + 0.8f);
            $$2.setBlock($$3, (BlockState)$$4.setValue(BlockStateProperties.LIT, true), 11);
            $$2.gameEvent($$12, GameEvent.BLOCK_CHANGE, $$3);
            if ($$12 != null) {
                $$0.getItemInHand().hurtAndBreak(1, $$12, $$1 -> $$1.broadcastBreakEvent($$0.getHand()));
            }
            return InteractionResult.sidedSuccess($$2.isClientSide());
        }
        Vec3i $$5 = $$3.relative($$0.getClickedFace());
        if (BaseFireBlock.canBePlacedAt($$2, (BlockPos)$$5, $$0.getHorizontalDirection())) {
            $$2.playSound($$12, (BlockPos)$$5, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0f, $$2.getRandom().nextFloat() * 0.4f + 0.8f);
            BlockState $$6 = BaseFireBlock.getState($$2, (BlockPos)$$5);
            $$2.setBlock((BlockPos)$$5, $$6, 11);
            $$2.gameEvent($$12, GameEvent.BLOCK_PLACE, $$3);
            ItemStack $$7 = $$0.getItemInHand();
            if ($$12 instanceof ServerPlayer) {
                CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)$$12, (BlockPos)$$5, $$7);
                $$7.hurtAndBreak(1, $$12, $$1 -> $$1.broadcastBreakEvent($$0.getHand()));
            }
            return InteractionResult.sidedSuccess($$2.isClientSide());
        }
        return InteractionResult.FAIL;
    }
}