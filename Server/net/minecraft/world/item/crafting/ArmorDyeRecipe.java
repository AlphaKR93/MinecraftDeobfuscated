/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.List
 */
package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class ArmorDyeRecipe
extends CustomRecipe {
    public ArmorDyeRecipe(ResourceLocation $$0, CraftingBookCategory $$1) {
        super($$0, $$1);
    }

    @Override
    public boolean matches(CraftingContainer $$0, Level $$1) {
        ItemStack $$2 = ItemStack.EMPTY;
        ArrayList $$3 = Lists.newArrayList();
        for (int $$4 = 0; $$4 < $$0.getContainerSize(); ++$$4) {
            ItemStack $$5 = $$0.getItem($$4);
            if ($$5.isEmpty()) continue;
            if ($$5.getItem() instanceof DyeableLeatherItem) {
                if (!$$2.isEmpty()) {
                    return false;
                }
                $$2 = $$5;
                continue;
            }
            if ($$5.getItem() instanceof DyeItem) {
                $$3.add((Object)$$5);
                continue;
            }
            return false;
        }
        return !$$2.isEmpty() && !$$3.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer $$0) {
        ArrayList $$1 = Lists.newArrayList();
        ItemStack $$2 = ItemStack.EMPTY;
        for (int $$3 = 0; $$3 < $$0.getContainerSize(); ++$$3) {
            ItemStack $$4 = $$0.getItem($$3);
            if ($$4.isEmpty()) continue;
            Item $$5 = $$4.getItem();
            if ($$5 instanceof DyeableLeatherItem) {
                if (!$$2.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                $$2 = $$4.copy();
                continue;
            }
            if ($$5 instanceof DyeItem) {
                $$1.add((Object)((DyeItem)$$5));
                continue;
            }
            return ItemStack.EMPTY;
        }
        if ($$2.isEmpty() || $$1.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return DyeableLeatherItem.dyeArmor($$2, (List<DyeItem>)$$1);
    }

    @Override
    public boolean canCraftInDimensions(int $$0, int $$1) {
        return $$0 * $$1 >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.ARMOR_DYE;
    }
}