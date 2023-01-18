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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ItemSteerable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class FoodOnAStickItem<T extends Entity>
extends Item {
    private final EntityType<T> canInteractWith;
    private final int consumeItemDamage;

    public FoodOnAStickItem(Item.Properties $$0, EntityType<T> $$1, int $$2) {
        super($$0);
        this.canInteractWith = $$1;
        this.consumeItemDamage = $$2;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level $$0, Player $$12, InteractionHand $$2) {
        ItemSteerable $$5;
        ItemStack $$3 = $$12.getItemInHand($$2);
        if ($$0.isClientSide) {
            return InteractionResultHolder.pass($$3);
        }
        Entity $$4 = $$12.getVehicle();
        if ($$12.isPassenger() && $$4 instanceof ItemSteerable && $$4.getType() == this.canInteractWith && ($$5 = (ItemSteerable)((Object)$$4)).boost()) {
            $$3.hurtAndBreak(this.consumeItemDamage, $$12, $$1 -> $$1.broadcastBreakEvent($$2));
            if ($$3.isEmpty()) {
                ItemStack $$6 = new ItemStack(Items.FISHING_ROD);
                $$6.setTag($$3.getTag());
                return InteractionResultHolder.success($$6);
            }
            return InteractionResultHolder.success($$3);
        }
        $$12.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.pass($$3);
    }
}