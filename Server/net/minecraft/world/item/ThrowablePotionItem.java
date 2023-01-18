/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.Level;

public class ThrowablePotionItem
extends PotionItem {
    public ThrowablePotionItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level $$0, Player $$1, InteractionHand $$2) {
        ItemStack $$3 = $$1.getItemInHand($$2);
        if (!$$0.isClientSide) {
            ThrownPotion $$4 = new ThrownPotion($$0, $$1);
            $$4.setItem($$3);
            $$4.shootFromRotation($$1, $$1.getXRot(), $$1.getYRot(), -20.0f, 0.5f, 1.0f);
            $$0.addFreshEntity($$4);
        }
        $$1.awardStat(Stats.ITEM_USED.get(this));
        if (!$$1.getAbilities().instabuild) {
            $$3.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess($$3, $$0.isClientSide());
    }
}