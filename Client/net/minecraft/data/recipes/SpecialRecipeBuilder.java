/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 */
package net.minecraft.data.recipes;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.data.recipes.CraftingRecipeBuilder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class SpecialRecipeBuilder
extends CraftingRecipeBuilder {
    final RecipeSerializer<?> serializer;

    public SpecialRecipeBuilder(RecipeSerializer<?> $$0) {
        this.serializer = $$0;
    }

    public static SpecialRecipeBuilder special(RecipeSerializer<? extends CraftingRecipe> $$0) {
        return new SpecialRecipeBuilder($$0);
    }

    public void save(Consumer<FinishedRecipe> $$0, final String $$1) {
        $$0.accept((Object)new CraftingRecipeBuilder.CraftingResult(CraftingBookCategory.MISC){

            @Override
            public RecipeSerializer<?> getType() {
                return SpecialRecipeBuilder.this.serializer;
            }

            @Override
            public ResourceLocation getId() {
                return new ResourceLocation($$1);
            }

            @Override
            @Nullable
            public JsonObject serializeAdvancement() {
                return null;
            }

            @Override
            public ResourceLocation getAdvancementId() {
                return new ResourceLocation("");
            }
        });
    }
}