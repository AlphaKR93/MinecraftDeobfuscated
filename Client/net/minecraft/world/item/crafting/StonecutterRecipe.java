/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.world.item.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class StonecutterRecipe
extends SingleItemRecipe {
    public StonecutterRecipe(ResourceLocation $$0, String $$1, Ingredient $$2, ItemStack $$3) {
        super(RecipeType.STONECUTTING, RecipeSerializer.STONECUTTER, $$0, $$1, $$2, $$3);
    }

    @Override
    public boolean matches(Container $$0, Level $$1) {
        return this.ingredient.test($$0.getItem(0));
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(Blocks.STONECUTTER);
    }
}