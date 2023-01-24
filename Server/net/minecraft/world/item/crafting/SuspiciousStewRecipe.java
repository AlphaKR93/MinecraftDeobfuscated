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
import net.minecraft.tags.ItemTags;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SuspiciousEffectHolder;

public class SuspiciousStewRecipe
extends CustomRecipe {
    public SuspiciousStewRecipe(ResourceLocation $$0, CraftingBookCategory $$1) {
        super($$0, $$1);
    }

    @Override
    public boolean matches(CraftingContainer $$0, Level $$1) {
        boolean $$2 = false;
        boolean $$3 = false;
        boolean $$4 = false;
        boolean $$5 = false;
        for (int $$6 = 0; $$6 < $$0.getContainerSize(); ++$$6) {
            ItemStack $$7 = $$0.getItem($$6);
            if ($$7.isEmpty()) continue;
            if ($$7.is(Blocks.BROWN_MUSHROOM.asItem()) && !$$4) {
                $$4 = true;
                continue;
            }
            if ($$7.is(Blocks.RED_MUSHROOM.asItem()) && !$$3) {
                $$3 = true;
                continue;
            }
            if ($$7.is(ItemTags.SMALL_FLOWERS) && !$$2) {
                $$2 = true;
                continue;
            }
            if ($$7.is(Items.BOWL) && !$$5) {
                $$5 = true;
                continue;
            }
            return false;
        }
        return $$2 && $$4 && $$3 && $$5;
    }

    @Override
    public ItemStack assemble(CraftingContainer $$0, RegistryAccess $$1) {
        ItemStack $$2 = new ItemStack(Items.SUSPICIOUS_STEW, 1);
        for (int $$3 = 0; $$3 < $$0.getContainerSize(); ++$$3) {
            SuspiciousEffectHolder $$5;
            ItemStack $$4 = $$0.getItem($$3);
            if ($$4.isEmpty() || ($$5 = SuspiciousEffectHolder.tryGet($$4.getItem())) == null) continue;
            SuspiciousStewItem.saveMobEffect($$2, $$5.getSuspiciousEffect(), $$5.getEffectDuration());
            break;
        }
        return $$2;
    }

    @Override
    public boolean canCraftInDimensions(int $$0, int $$1) {
        return $$0 >= 2 && $$1 >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SUSPICIOUS_STEW;
    }
}