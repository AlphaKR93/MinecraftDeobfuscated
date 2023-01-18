/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;

public class FireChargeItem
extends Item {
    public FireChargeItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public InteractionResult useOn(UseOnContext $$0) {
        Level $$1 = $$0.getLevel();
        Vec3i $$2 = $$0.getClickedPos();
        BlockState $$3 = $$1.getBlockState((BlockPos)$$2);
        boolean $$4 = false;
        if (CampfireBlock.canLight($$3) || CandleBlock.canLight($$3) || CandleCakeBlock.canLight($$3)) {
            this.playSound($$1, (BlockPos)$$2);
            $$1.setBlockAndUpdate((BlockPos)$$2, (BlockState)$$3.setValue(BlockStateProperties.LIT, true));
            $$1.gameEvent($$0.getPlayer(), GameEvent.BLOCK_CHANGE, (BlockPos)$$2);
            $$4 = true;
        } else if (BaseFireBlock.canBePlacedAt($$1, $$2 = $$2.relative($$0.getClickedFace()), $$0.getHorizontalDirection())) {
            this.playSound($$1, (BlockPos)$$2);
            $$1.setBlockAndUpdate((BlockPos)$$2, BaseFireBlock.getState($$1, $$2));
            $$1.gameEvent($$0.getPlayer(), GameEvent.BLOCK_PLACE, (BlockPos)$$2);
            $$4 = true;
        }
        if ($$4) {
            $$0.getItemInHand().shrink(1);
            return InteractionResult.sidedSuccess($$1.isClientSide);
        }
        return InteractionResult.FAIL;
    }

    private void playSound(Level $$0, BlockPos $$1) {
        RandomSource $$2 = $$0.getRandom();
        $$0.playSound(null, $$1, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0f, ($$2.nextFloat() - $$2.nextFloat()) * 0.2f + 1.0f);
    }
}