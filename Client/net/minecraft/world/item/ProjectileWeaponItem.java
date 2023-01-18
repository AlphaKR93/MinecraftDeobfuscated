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
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public abstract class ProjectileWeaponItem
extends Item {
    public static final Predicate<ItemStack> ARROW_ONLY = $$0 -> $$0.is(ItemTags.ARROWS);
    public static final Predicate<ItemStack> ARROW_OR_FIREWORK = ARROW_ONLY.or($$0 -> $$0.is(Items.FIREWORK_ROCKET));

    public ProjectileWeaponItem(Item.Properties $$0) {
        super($$0);
    }

    public Predicate<ItemStack> getSupportedHeldProjectiles() {
        return this.getAllSupportedProjectiles();
    }

    public abstract Predicate<ItemStack> getAllSupportedProjectiles();

    public static ItemStack getHeldProjectile(LivingEntity $$0, Predicate<ItemStack> $$1) {
        if ($$1.test((Object)$$0.getItemInHand(InteractionHand.OFF_HAND))) {
            return $$0.getItemInHand(InteractionHand.OFF_HAND);
        }
        if ($$1.test((Object)$$0.getItemInHand(InteractionHand.MAIN_HAND))) {
            return $$0.getItemInHand(InteractionHand.MAIN_HAND);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }

    public abstract int getDefaultProjectileRange();
}