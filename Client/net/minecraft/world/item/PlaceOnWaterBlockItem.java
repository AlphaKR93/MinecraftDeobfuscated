/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;

public class PlaceOnWaterBlockItem
extends BlockItem {
    public PlaceOnWaterBlockItem(Block $$0, Item.Properties $$1) {
        super($$0, $$1);
    }

    @Override
    public InteractionResult useOn(UseOnContext $$0) {
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level $$0, Player $$1, InteractionHand $$2) {
        BlockHitResult $$3 = PlaceOnWaterBlockItem.getPlayerPOVHitResult($$0, $$1, ClipContext.Fluid.SOURCE_ONLY);
        BlockHitResult $$4 = $$3.withPosition((BlockPos)$$3.getBlockPos().above());
        InteractionResult $$5 = super.useOn(new UseOnContext($$1, $$2, $$4));
        return new InteractionResultHolder<ItemStack>($$5, $$1.getItemInHand($$2));
    }
}