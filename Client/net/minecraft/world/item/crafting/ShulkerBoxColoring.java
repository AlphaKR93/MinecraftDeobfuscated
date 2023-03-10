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
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;

public class ShulkerBoxColoring
extends CustomRecipe {
    public ShulkerBoxColoring(ResourceLocation $$0, CraftingBookCategory $$1) {
        super($$0, $$1);
    }

    @Override
    public boolean matches(CraftingContainer $$0, Level $$1) {
        int $$2 = 0;
        int $$3 = 0;
        for (int $$4 = 0; $$4 < $$0.getContainerSize(); ++$$4) {
            ItemStack $$5 = $$0.getItem($$4);
            if ($$5.isEmpty()) continue;
            if (Block.byItem($$5.getItem()) instanceof ShulkerBoxBlock) {
                ++$$2;
            } else if ($$5.getItem() instanceof DyeItem) {
                ++$$3;
            } else {
                return false;
            }
            if ($$3 <= 1 && $$2 <= 1) continue;
            return false;
        }
        return $$2 == 1 && $$3 == 1;
    }

    @Override
    public ItemStack assemble(CraftingContainer $$0, RegistryAccess $$1) {
        ItemStack $$2 = ItemStack.EMPTY;
        DyeItem $$3 = (DyeItem)Items.WHITE_DYE;
        for (int $$4 = 0; $$4 < $$0.getContainerSize(); ++$$4) {
            ItemStack $$5 = $$0.getItem($$4);
            if ($$5.isEmpty()) continue;
            Item $$6 = $$5.getItem();
            if (Block.byItem($$6) instanceof ShulkerBoxBlock) {
                $$2 = $$5;
                continue;
            }
            if (!($$6 instanceof DyeItem)) continue;
            $$3 = (DyeItem)$$6;
        }
        ItemStack $$7 = ShulkerBoxBlock.getColoredItemStack($$3.getDyeColor());
        if ($$2.hasTag()) {
            $$7.setTag($$2.getTag().copy());
        }
        return $$7;
    }

    @Override
    public boolean canCraftInDimensions(int $$0, int $$1) {
        return $$0 * $$1 >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHULKER_BOX_COLORING;
    }
}