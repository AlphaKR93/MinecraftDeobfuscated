/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class MilkBucketItem
extends Item {
    private static final int DRINK_DURATION = 32;

    public MilkBucketItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack $$0, Level $$1, LivingEntity $$2) {
        if ($$2 instanceof ServerPlayer) {
            ServerPlayer $$3 = (ServerPlayer)$$2;
            CriteriaTriggers.CONSUME_ITEM.trigger($$3, $$0);
            $$3.awardStat(Stats.ITEM_USED.get(this));
        }
        if ($$2 instanceof Player && !((Player)$$2).getAbilities().instabuild) {
            $$0.shrink(1);
        }
        if (!$$1.isClientSide) {
            $$2.removeAllEffects();
        }
        if ($$0.isEmpty()) {
            return new ItemStack(Items.BUCKET);
        }
        return $$0;
    }

    @Override
    public int getUseDuration(ItemStack $$0) {
        return 32;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack $$0) {
        return UseAnim.DRINK;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level $$0, Player $$1, InteractionHand $$2) {
        return ItemUtils.startUsingInstantly($$0, $$1, $$2);
    }
}