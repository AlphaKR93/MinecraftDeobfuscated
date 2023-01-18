/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.item.enchantment;

import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentInstance
extends WeightedEntry.IntrusiveBase {
    public final Enchantment enchantment;
    public final int level;

    public EnchantmentInstance(Enchantment $$0, int $$1) {
        super($$0.getRarity().getWeight());
        this.enchantment = $$0;
        this.level = $$1;
    }
}