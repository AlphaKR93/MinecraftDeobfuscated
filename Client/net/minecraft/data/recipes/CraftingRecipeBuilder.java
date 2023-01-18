/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.data.recipes;

import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.crafting.CraftingBookCategory;

public abstract class CraftingRecipeBuilder {
    protected static CraftingBookCategory determineBookCategory(RecipeCategory $$0) {
        return switch ($$0) {
            case RecipeCategory.BUILDING_BLOCKS -> CraftingBookCategory.BUILDING;
            case RecipeCategory.TOOLS, RecipeCategory.COMBAT -> CraftingBookCategory.EQUIPMENT;
            case RecipeCategory.REDSTONE -> CraftingBookCategory.REDSTONE;
            default -> CraftingBookCategory.MISC;
        };
    }

    protected static abstract class CraftingResult
    implements FinishedRecipe {
        private final CraftingBookCategory category;

        protected CraftingResult(CraftingBookCategory $$0) {
            this.category = $$0;
        }

        @Override
        public void serializeRecipeData(JsonObject $$0) {
            $$0.addProperty("category", this.category.getSerializedName());
        }
    }
}