/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  java.lang.Object
 *  javax.annotation.Nullable
 */
package net.minecraft.data.recipes;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public interface FinishedRecipe {
    public void serializeRecipeData(JsonObject var1);

    default public JsonObject serializeRecipe() {
        JsonObject $$0 = new JsonObject();
        $$0.addProperty("type", BuiltInRegistries.RECIPE_SERIALIZER.getKey(this.getType()).toString());
        this.serializeRecipeData($$0);
        return $$0;
    }

    public ResourceLocation getId();

    public RecipeSerializer<?> getType();

    @Nullable
    public JsonObject serializeAdvancement();

    @Nullable
    public ResourceLocation getAdvancementId();
}