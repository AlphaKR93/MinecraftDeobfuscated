/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerBlockEntity;

public class BannerDuplicateRecipe
extends CustomRecipe {
    public BannerDuplicateRecipe(ResourceLocation $$0, CraftingBookCategory $$1) {
        super($$0, $$1);
    }

    @Override
    public boolean matches(CraftingContainer $$0, Level $$1) {
        DyeColor $$2 = null;
        ItemStack $$3 = null;
        ItemStack $$4 = null;
        for (int $$5 = 0; $$5 < $$0.getContainerSize(); ++$$5) {
            ItemStack $$6 = $$0.getItem($$5);
            if ($$6.isEmpty()) continue;
            Item $$7 = $$6.getItem();
            if (!($$7 instanceof BannerItem)) {
                return false;
            }
            BannerItem $$8 = (BannerItem)$$7;
            if ($$2 == null) {
                $$2 = $$8.getColor();
            } else if ($$2 != $$8.getColor()) {
                return false;
            }
            int $$9 = BannerBlockEntity.getPatternCount($$6);
            if ($$9 > 6) {
                return false;
            }
            if ($$9 > 0) {
                if ($$3 == null) {
                    $$3 = $$6;
                    continue;
                }
                return false;
            }
            if ($$4 == null) {
                $$4 = $$6;
                continue;
            }
            return false;
        }
        return $$3 != null && $$4 != null;
    }

    @Override
    public ItemStack assemble(CraftingContainer $$0) {
        for (int $$1 = 0; $$1 < $$0.getContainerSize(); ++$$1) {
            int $$3;
            ItemStack $$2 = $$0.getItem($$1);
            if ($$2.isEmpty() || ($$3 = BannerBlockEntity.getPatternCount($$2)) <= 0 || $$3 > 6) continue;
            ItemStack $$4 = $$2.copy();
            $$4.setCount(1);
            return $$4;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer $$0) {
        NonNullList<ItemStack> $$1 = NonNullList.withSize($$0.getContainerSize(), ItemStack.EMPTY);
        for (int $$2 = 0; $$2 < $$1.size(); ++$$2) {
            ItemStack $$3 = $$0.getItem($$2);
            if ($$3.isEmpty()) continue;
            if ($$3.getItem().hasCraftingRemainingItem()) {
                $$1.set($$2, new ItemStack($$3.getItem().getCraftingRemainingItem()));
                continue;
            }
            if (!$$3.hasTag() || BannerBlockEntity.getPatternCount($$3) <= 0) continue;
            ItemStack $$4 = $$3.copy();
            $$4.setCount(1);
            $$1.set($$2, $$4);
        }
        return $$1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.BANNER_DUPLICATE;
    }

    @Override
    public boolean canCraftInDimensions(int $$0, int $$1) {
        return $$0 * $$1 >= 2;
    }
}