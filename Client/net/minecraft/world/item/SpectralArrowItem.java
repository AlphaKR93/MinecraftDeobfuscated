/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.SpectralArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SpectralArrowItem
extends ArrowItem {
    public SpectralArrowItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public AbstractArrow createArrow(Level $$0, ItemStack $$1, LivingEntity $$2) {
        return new SpectralArrow($$0, $$2);
    }
}