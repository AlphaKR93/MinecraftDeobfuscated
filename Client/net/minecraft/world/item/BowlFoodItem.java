/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class BowlFoodItem
extends Item {
    public BowlFoodItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack $$0, Level $$1, LivingEntity $$2) {
        ItemStack $$3 = super.finishUsingItem($$0, $$1, $$2);
        if ($$2 instanceof Player && ((Player)$$2).getAbilities().instabuild) {
            return $$3;
        }
        return new ItemStack(Items.BOWL);
    }
}