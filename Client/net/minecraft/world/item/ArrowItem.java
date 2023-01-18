/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ArrowItem
extends Item {
    public ArrowItem(Item.Properties $$0) {
        super($$0);
    }

    public AbstractArrow createArrow(Level $$0, ItemStack $$1, LivingEntity $$2) {
        Arrow $$3 = new Arrow($$0, $$2);
        $$3.setEffectsFromItem($$1);
        return $$3;
    }
}