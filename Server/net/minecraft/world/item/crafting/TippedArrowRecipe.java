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
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class TippedArrowRecipe
extends CustomRecipe {
    public TippedArrowRecipe(ResourceLocation $$0, CraftingBookCategory $$1) {
        super($$0, $$1);
    }

    @Override
    public boolean matches(CraftingContainer $$0, Level $$1) {
        if ($$0.getWidth() != 3 || $$0.getHeight() != 3) {
            return false;
        }
        for (int $$2 = 0; $$2 < $$0.getWidth(); ++$$2) {
            for (int $$3 = 0; $$3 < $$0.getHeight(); ++$$3) {
                ItemStack $$4 = $$0.getItem($$2 + $$3 * $$0.getWidth());
                if ($$4.isEmpty()) {
                    return false;
                }
                if (!($$2 == 1 && $$3 == 1 ? !$$4.is(Items.LINGERING_POTION) : !$$4.is(Items.ARROW))) continue;
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingContainer $$0, RegistryAccess $$1) {
        ItemStack $$2 = $$0.getItem(1 + $$0.getWidth());
        if (!$$2.is(Items.LINGERING_POTION)) {
            return ItemStack.EMPTY;
        }
        ItemStack $$3 = new ItemStack(Items.TIPPED_ARROW, 8);
        PotionUtils.setPotion($$3, PotionUtils.getPotion($$2));
        PotionUtils.setCustomEffects($$3, PotionUtils.getCustomEffects($$2));
        return $$3;
    }

    @Override
    public boolean canCraftInDimensions(int $$0, int $$1) {
        return $$0 >= 2 && $$1 >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.TIPPED_ARROW;
    }
}