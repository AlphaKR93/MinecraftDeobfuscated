/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item.crafting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class MapCloningRecipe
extends CustomRecipe {
    public MapCloningRecipe(ResourceLocation $$0, CraftingBookCategory $$1) {
        super($$0, $$1);
    }

    @Override
    public boolean matches(CraftingContainer $$0, Level $$1) {
        int $$2 = 0;
        ItemStack $$3 = ItemStack.EMPTY;
        for (int $$4 = 0; $$4 < $$0.getContainerSize(); ++$$4) {
            ItemStack $$5 = $$0.getItem($$4);
            if ($$5.isEmpty()) continue;
            if ($$5.is(Items.FILLED_MAP)) {
                if (!$$3.isEmpty()) {
                    return false;
                }
                $$3 = $$5;
                continue;
            }
            if ($$5.is(Items.MAP)) {
                ++$$2;
                continue;
            }
            return false;
        }
        return !$$3.isEmpty() && $$2 > 0;
    }

    @Override
    public ItemStack assemble(CraftingContainer $$0, RegistryAccess $$1) {
        int $$2 = 0;
        ItemStack $$3 = ItemStack.EMPTY;
        for (int $$4 = 0; $$4 < $$0.getContainerSize(); ++$$4) {
            ItemStack $$5 = $$0.getItem($$4);
            if ($$5.isEmpty()) continue;
            if ($$5.is(Items.FILLED_MAP)) {
                if (!$$3.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                $$3 = $$5;
                continue;
            }
            if ($$5.is(Items.MAP)) {
                ++$$2;
                continue;
            }
            return ItemStack.EMPTY;
        }
        if ($$3.isEmpty() || $$2 < 1) {
            return ItemStack.EMPTY;
        }
        ItemStack $$6 = $$3.copy();
        $$6.setCount($$2 + 1);
        return $$6;
    }

    @Override
    public boolean canCraftInDimensions(int $$0, int $$1) {
        return $$0 >= 3 && $$1 >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.MAP_CLONING;
    }
}