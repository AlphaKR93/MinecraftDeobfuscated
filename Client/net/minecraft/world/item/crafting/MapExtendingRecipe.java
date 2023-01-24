/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class MapExtendingRecipe
extends ShapedRecipe {
    public MapExtendingRecipe(ResourceLocation $$0, CraftingBookCategory $$1) {
        super($$0, "", $$1, 3, 3, NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.FILLED_MAP), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER)), new ItemStack(Items.MAP));
    }

    @Override
    public boolean matches(CraftingContainer $$0, Level $$1) {
        if (!super.matches($$0, $$1)) {
            return false;
        }
        ItemStack $$2 = ItemStack.EMPTY;
        for (int $$3 = 0; $$3 < $$0.getContainerSize() && $$2.isEmpty(); ++$$3) {
            ItemStack $$4 = $$0.getItem($$3);
            if (!$$4.is(Items.FILLED_MAP)) continue;
            $$2 = $$4;
        }
        if ($$2.isEmpty()) {
            return false;
        }
        MapItemSavedData $$5 = MapItem.getSavedData($$2, $$1);
        if ($$5 == null) {
            return false;
        }
        if ($$5.isExplorationMap()) {
            return false;
        }
        return $$5.scale < 4;
    }

    @Override
    public ItemStack assemble(CraftingContainer $$0, RegistryAccess $$1) {
        ItemStack $$2 = ItemStack.EMPTY;
        for (int $$3 = 0; $$3 < $$0.getContainerSize() && $$2.isEmpty(); ++$$3) {
            ItemStack $$4 = $$0.getItem($$3);
            if (!$$4.is(Items.FILLED_MAP)) continue;
            $$2 = $$4;
        }
        $$2 = $$2.copy();
        $$2.setCount(1);
        $$2.getOrCreateTag().putInt("map_scale_direction", 1);
        return $$2;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.MAP_EXTENDING;
    }
}