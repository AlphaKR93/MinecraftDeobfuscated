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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;

public abstract class CustomRecipe
implements CraftingRecipe {
    private final ResourceLocation id;
    private final CraftingBookCategory category;

    public CustomRecipe(ResourceLocation $$0, CraftingBookCategory $$1) {
        this.id = $$0;
        this.category = $$1;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess $$0) {
        return ItemStack.EMPTY;
    }

    @Override
    public CraftingBookCategory category() {
        return this.category;
    }
}