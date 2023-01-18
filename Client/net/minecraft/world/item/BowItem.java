/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Predicate
 */
package net.minecraft.world.item;

import java.util.function.Predicate;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

public class BowItem
extends ProjectileWeaponItem
implements Vanishable {
    public static final int MAX_DRAW_DURATION = 20;
    public static final int DEFAULT_RANGE = 15;

    public BowItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public void releaseUsing(ItemStack $$0, Level $$12, LivingEntity $$2, int $$3) {
        boolean $$9;
        int $$7;
        float $$8;
        if (!($$2 instanceof Player)) {
            return;
        }
        Player $$4 = (Player)$$2;
        boolean $$5 = $$4.getAbilities().instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, $$0) > 0;
        ItemStack $$6 = $$4.getProjectile($$0);
        if ($$6.isEmpty() && !$$5) {
            return;
        }
        if ($$6.isEmpty()) {
            $$6 = new ItemStack(Items.ARROW);
        }
        if ((double)($$8 = BowItem.getPowerForTime($$7 = this.getUseDuration($$0) - $$3)) < 0.1) {
            return;
        }
        boolean bl = $$9 = $$5 && $$6.is(Items.ARROW);
        if (!$$12.isClientSide) {
            int $$13;
            int $$122;
            ArrowItem $$10 = (ArrowItem)($$6.getItem() instanceof ArrowItem ? $$6.getItem() : Items.ARROW);
            AbstractArrow $$11 = $$10.createArrow($$12, $$6, $$4);
            $$11.shootFromRotation($$4, $$4.getXRot(), $$4.getYRot(), 0.0f, $$8 * 3.0f, 1.0f);
            if ($$8 == 1.0f) {
                $$11.setCritArrow(true);
            }
            if (($$122 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, $$0)) > 0) {
                $$11.setBaseDamage($$11.getBaseDamage() + (double)$$122 * 0.5 + 0.5);
            }
            if (($$13 = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, $$0)) > 0) {
                $$11.setKnockback($$13);
            }
            if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, $$0) > 0) {
                $$11.setSecondsOnFire(100);
            }
            $$0.hurtAndBreak(1, $$4, $$1 -> $$1.broadcastBreakEvent($$4.getUsedItemHand()));
            if ($$9 || $$4.getAbilities().instabuild && ($$6.is(Items.SPECTRAL_ARROW) || $$6.is(Items.TIPPED_ARROW))) {
                $$11.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
            $$12.addFreshEntity($$11);
        }
        $$12.playSound(null, $$4.getX(), $$4.getY(), $$4.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0f, 1.0f / ($$12.getRandom().nextFloat() * 0.4f + 1.2f) + $$8 * 0.5f);
        if (!$$9 && !$$4.getAbilities().instabuild) {
            $$6.shrink(1);
            if ($$6.isEmpty()) {
                $$4.getInventory().removeItem($$6);
            }
        }
        $$4.awardStat(Stats.ITEM_USED.get(this));
    }

    public static float getPowerForTime(int $$0) {
        float $$1 = (float)$$0 / 20.0f;
        if (($$1 = ($$1 * $$1 + $$1 * 2.0f) / 3.0f) > 1.0f) {
            $$1 = 1.0f;
        }
        return $$1;
    }

    @Override
    public int getUseDuration(ItemStack $$0) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack $$0) {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level $$0, Player $$1, InteractionHand $$2) {
        boolean $$4;
        ItemStack $$3 = $$1.getItemInHand($$2);
        boolean bl = $$4 = !$$1.getProjectile($$3).isEmpty();
        if ($$1.getAbilities().instabuild || $$4) {
            $$1.startUsingItem($$2);
            return InteractionResultHolder.consume($$3);
        }
        return InteractionResultHolder.fail($$3);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_ONLY;
    }

    @Override
    public int getDefaultProjectileRange() {
        return 15;
    }
}