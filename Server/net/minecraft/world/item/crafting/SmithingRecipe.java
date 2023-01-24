/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item.crafting;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Blocks;

public interface SmithingRecipe
extends Recipe<Container> {
    @Override
    default public RecipeType<?> getType() {
        return RecipeType.SMITHING;
    }

    @Override
    default public boolean canCraftInDimensions(int $$0, int $$1) {
        return $$0 >= 3 && $$1 >= 1;
    }

    @Override
    default public ItemStack getToastSymbol() {
        return new ItemStack(Blocks.SMITHING_TABLE);
    }

    public boolean isTemplateIngredient(ItemStack var1);

    public boolean isBaseIngredient(ItemStack var1);

    public boolean isAdditionIngredient(ItemStack var1);
}