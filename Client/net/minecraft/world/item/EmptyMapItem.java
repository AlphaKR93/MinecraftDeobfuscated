/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ComplexItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;

public class EmptyMapItem
extends ComplexItem {
    public EmptyMapItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level $$0, Player $$1, InteractionHand $$2) {
        ItemStack $$3 = $$1.getItemInHand($$2);
        if ($$0.isClientSide) {
            return InteractionResultHolder.success($$3);
        }
        if (!$$1.getAbilities().instabuild) {
            $$3.shrink(1);
        }
        $$1.awardStat(Stats.ITEM_USED.get(this));
        $$1.level.playSound(null, $$1, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, $$1.getSoundSource(), 1.0f, 1.0f);
        ItemStack $$4 = MapItem.create($$0, $$1.getBlockX(), $$1.getBlockZ(), (byte)0, true, false);
        if ($$3.isEmpty()) {
            return InteractionResultHolder.consume($$4);
        }
        if (!$$1.getInventory().add($$4.copy())) {
            $$1.drop($$4, false);
        }
        return InteractionResultHolder.consume($$3);
    }
}