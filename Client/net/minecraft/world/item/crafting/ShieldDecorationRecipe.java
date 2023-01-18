/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item.crafting;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ShieldDecorationRecipe
extends CustomRecipe {
    public ShieldDecorationRecipe(ResourceLocation $$0, CraftingBookCategory $$1) {
        super($$0, $$1);
    }

    @Override
    public boolean matches(CraftingContainer $$0, Level $$1) {
        ItemStack $$2 = ItemStack.EMPTY;
        ItemStack $$3 = ItemStack.EMPTY;
        for (int $$4 = 0; $$4 < $$0.getContainerSize(); ++$$4) {
            ItemStack $$5 = $$0.getItem($$4);
            if ($$5.isEmpty()) continue;
            if ($$5.getItem() instanceof BannerItem) {
                if (!$$3.isEmpty()) {
                    return false;
                }
                $$3 = $$5;
                continue;
            }
            if ($$5.is(Items.SHIELD)) {
                if (!$$2.isEmpty()) {
                    return false;
                }
                if (BlockItem.getBlockEntityData($$5) != null) {
                    return false;
                }
                $$2 = $$5;
                continue;
            }
            return false;
        }
        return !$$2.isEmpty() && !$$3.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer $$0) {
        ItemStack $$1 = ItemStack.EMPTY;
        ItemStack $$2 = ItemStack.EMPTY;
        for (int $$3 = 0; $$3 < $$0.getContainerSize(); ++$$3) {
            ItemStack $$4 = $$0.getItem($$3);
            if ($$4.isEmpty()) continue;
            if ($$4.getItem() instanceof BannerItem) {
                $$1 = $$4;
                continue;
            }
            if (!$$4.is(Items.SHIELD)) continue;
            $$2 = $$4.copy();
        }
        if ($$2.isEmpty()) {
            return $$2;
        }
        CompoundTag $$5 = BlockItem.getBlockEntityData($$1);
        CompoundTag $$6 = $$5 == null ? new CompoundTag() : $$5.copy();
        $$6.putInt("Base", ((BannerItem)$$1.getItem()).getColor().getId());
        BlockItem.setBlockEntityData($$2, BlockEntityType.BANNER, $$6);
        return $$2;
    }

    @Override
    public boolean canCraftInDimensions(int $$0, int $$1) {
        return $$0 * $$1 >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SHIELD_DECORATION;
    }
}