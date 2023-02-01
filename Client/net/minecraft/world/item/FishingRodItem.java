/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  net.minecraft.world.item.ItemStack
 */
package net.minecraft.world.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class FishingRodItem
extends Item
implements Vanishable {
    public FishingRodItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level $$0, Player $$12, InteractionHand $$2) {
        ItemStack $$3 = $$12.getItemInHand($$2);
        if ($$12.fishing != null) {
            if (!$$0.isClientSide) {
                int $$4 = $$12.fishing.retrieve($$3);
                $$3.hurtAndBreak($$4, (LivingEntity)$$12, $$1 -> $$1.broadcastBreakEvent($$2));
            }
            $$0.playSound(null, $$12.getX(), $$12.getY(), $$12.getZ(), SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL, 1.0f, 0.4f / ($$0.getRandom().nextFloat() * 0.4f + 0.8f));
            $$12.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
        } else {
            $$0.playSound(null, $$12.getX(), $$12.getY(), $$12.getZ(), SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL, 0.5f, 0.4f / ($$0.getRandom().nextFloat() * 0.4f + 0.8f));
            if (!$$0.isClientSide) {
                int $$5 = EnchantmentHelper.getFishingSpeedBonus($$3);
                int $$6 = EnchantmentHelper.getFishingLuckBonus($$3);
                $$0.addFreshEntity(new FishingHook($$12, $$0, $$6, $$5));
            }
            $$12.awardStat(Stats.ITEM_USED.get(this));
            $$12.gameEvent(GameEvent.ITEM_INTERACT_START);
        }
        return InteractionResultHolder.sidedSuccess($$3, $$0.isClientSide());
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }
}