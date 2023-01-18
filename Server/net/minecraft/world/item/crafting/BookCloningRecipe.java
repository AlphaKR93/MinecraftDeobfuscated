/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class BookCloningRecipe
extends CustomRecipe {
    public BookCloningRecipe(ResourceLocation $$0, CraftingBookCategory $$1) {
        super($$0, $$1);
    }

    @Override
    public boolean matches(CraftingContainer $$0, Level $$1) {
        int $$2 = 0;
        ItemStack $$3 = ItemStack.EMPTY;
        for (int $$4 = 0; $$4 < $$0.getContainerSize(); ++$$4) {
            ItemStack $$5 = $$0.getItem($$4);
            if ($$5.isEmpty()) continue;
            if ($$5.is(Items.WRITTEN_BOOK)) {
                if (!$$3.isEmpty()) {
                    return false;
                }
                $$3 = $$5;
                continue;
            }
            if ($$5.is(Items.WRITABLE_BOOK)) {
                ++$$2;
                continue;
            }
            return false;
        }
        return !$$3.isEmpty() && $$3.hasTag() && $$2 > 0;
    }

    @Override
    public ItemStack assemble(CraftingContainer $$0) {
        int $$1 = 0;
        ItemStack $$2 = ItemStack.EMPTY;
        for (int $$3 = 0; $$3 < $$0.getContainerSize(); ++$$3) {
            ItemStack $$4 = $$0.getItem($$3);
            if ($$4.isEmpty()) continue;
            if ($$4.is(Items.WRITTEN_BOOK)) {
                if (!$$2.isEmpty()) {
                    return ItemStack.EMPTY;
                }
                $$2 = $$4;
                continue;
            }
            if ($$4.is(Items.WRITABLE_BOOK)) {
                ++$$1;
                continue;
            }
            return ItemStack.EMPTY;
        }
        if ($$2.isEmpty() || !$$2.hasTag() || $$1 < 1 || WrittenBookItem.getGeneration($$2) >= 2) {
            return ItemStack.EMPTY;
        }
        ItemStack $$5 = new ItemStack(Items.WRITTEN_BOOK, $$1);
        CompoundTag $$6 = $$2.getTag().copy();
        $$6.putInt("generation", WrittenBookItem.getGeneration($$2) + 1);
        $$5.setTag($$6);
        return $$5;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer $$0) {
        NonNullList<ItemStack> $$1 = NonNullList.withSize($$0.getContainerSize(), ItemStack.EMPTY);
        for (int $$2 = 0; $$2 < $$1.size(); ++$$2) {
            ItemStack $$3 = $$0.getItem($$2);
            if ($$3.getItem().hasCraftingRemainingItem()) {
                $$1.set($$2, new ItemStack($$3.getItem().getCraftingRemainingItem()));
                continue;
            }
            if (!($$3.getItem() instanceof WrittenBookItem)) continue;
            ItemStack $$4 = $$3.copy();
            $$4.setCount(1);
            $$1.set($$2, $$4);
            break;
        }
        return $$1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.BOOK_CLONING;
    }

    @Override
    public boolean canCraftInDimensions(int $$0, int $$1) {
        return $$0 >= 3 && $$1 >= 3;
    }
}