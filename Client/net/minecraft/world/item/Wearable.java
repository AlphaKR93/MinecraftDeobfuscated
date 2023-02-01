/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.item;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.level.Level;

public interface Wearable
extends Vanishable {
    default public InteractionResultHolder<ItemStack> swapWithEquipmentSlot(Item $$0, Level $$1, Player $$2, InteractionHand $$3) {
        EquipmentSlot $$5;
        ItemStack $$6;
        ItemStack $$4 = $$2.getItemInHand($$3);
        if (ItemStack.matches($$4, $$6 = $$2.getItemBySlot($$5 = Mob.getEquipmentSlotForItem($$4)))) {
            return InteractionResultHolder.fail($$4);
        }
        $$2.setItemSlot($$5, $$4.copy());
        if (!$$1.isClientSide()) {
            $$2.awardStat(Stats.ITEM_USED.get($$0));
        }
        if ($$6.isEmpty()) {
            $$4.setCount(0);
        } else {
            $$2.setItemInHand($$3, $$6.copy());
        }
        return InteractionResultHolder.sidedSuccess($$4, $$1.isClientSide());
    }
}