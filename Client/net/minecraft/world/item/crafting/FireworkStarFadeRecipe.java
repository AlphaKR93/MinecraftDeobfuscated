/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.List
 */
package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class FireworkStarFadeRecipe
extends CustomRecipe {
    private static final Ingredient STAR_INGREDIENT = Ingredient.of(Items.FIREWORK_STAR);

    public FireworkStarFadeRecipe(ResourceLocation $$0, CraftingBookCategory $$1) {
        super($$0, $$1);
    }

    @Override
    public boolean matches(CraftingContainer $$0, Level $$1) {
        boolean $$2 = false;
        boolean $$3 = false;
        for (int $$4 = 0; $$4 < $$0.getContainerSize(); ++$$4) {
            ItemStack $$5 = $$0.getItem($$4);
            if ($$5.isEmpty()) continue;
            if ($$5.getItem() instanceof DyeItem) {
                $$2 = true;
                continue;
            }
            if (STAR_INGREDIENT.test($$5)) {
                if ($$3) {
                    return false;
                }
                $$3 = true;
                continue;
            }
            return false;
        }
        return $$3 && $$2;
    }

    @Override
    public ItemStack assemble(CraftingContainer $$0, RegistryAccess $$1) {
        ArrayList $$2 = Lists.newArrayList();
        ItemStack $$3 = null;
        for (int $$4 = 0; $$4 < $$0.getContainerSize(); ++$$4) {
            ItemStack $$5 = $$0.getItem($$4);
            Item $$6 = $$5.getItem();
            if ($$6 instanceof DyeItem) {
                $$2.add((Object)((DyeItem)$$6).getDyeColor().getFireworkColor());
                continue;
            }
            if (!STAR_INGREDIENT.test($$5)) continue;
            $$3 = $$5.copy();
            $$3.setCount(1);
        }
        if ($$3 == null || $$2.isEmpty()) {
            return ItemStack.EMPTY;
        }
        $$3.getOrCreateTagElement("Explosion").putIntArray("FadeColors", (List<Integer>)$$2);
        return $$3;
    }

    @Override
    public boolean canCraftInDimensions(int $$0, int $$1) {
        return $$0 * $$1 >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.FIREWORK_STAR_FADE;
    }
}