/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  net.minecraft.world.level.Level
 */
package net.minecraft.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public interface Recipe<C extends Container> {
    public boolean matches(C var1, Level var2);

    public ItemStack assemble(C var1, RegistryAccess var2);

    public boolean canCraftInDimensions(int var1, int var2);

    public ItemStack getResultItem(RegistryAccess var1);

    default public NonNullList<ItemStack> getRemainingItems(C $$0) {
        NonNullList<ItemStack> $$1 = NonNullList.withSize($$0.getContainerSize(), ItemStack.EMPTY);
        for (int $$2 = 0; $$2 < $$1.size(); ++$$2) {
            Item $$3 = $$0.getItem($$2).getItem();
            if (!$$3.hasCraftingRemainingItem()) continue;
            $$1.set($$2, new ItemStack($$3.getCraftingRemainingItem()));
        }
        return $$1;
    }

    default public NonNullList<Ingredient> getIngredients() {
        return NonNullList.create();
    }

    default public boolean isSpecial() {
        return false;
    }

    default public String getGroup() {
        return "";
    }

    default public ItemStack getToastSymbol() {
        return new ItemStack(Blocks.CRAFTING_TABLE);
    }

    public ResourceLocation getId();

    public RecipeSerializer<?> getSerializer();

    public RecipeType<?> getType();

    default public boolean isIncomplete() {
        NonNullList<Ingredient> $$02 = this.getIngredients();
        return $$02.isEmpty() || $$02.stream().anyMatch($$0 -> $$0.getItems().length == 0);
    }
}