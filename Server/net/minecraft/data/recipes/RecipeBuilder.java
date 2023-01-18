/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.String
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 */
package net.minecraft.data.recipes;

import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public interface RecipeBuilder {
    public static final ResourceLocation ROOT_RECIPE_ADVANCEMENT = new ResourceLocation("recipes/root");

    public RecipeBuilder unlockedBy(String var1, CriterionTriggerInstance var2);

    public RecipeBuilder group(@Nullable String var1);

    public Item getResult();

    public void save(Consumer<FinishedRecipe> var1, ResourceLocation var2);

    default public void save(Consumer<FinishedRecipe> $$0) {
        this.save($$0, RecipeBuilder.getDefaultRecipeId(this.getResult()));
    }

    default public void save(Consumer<FinishedRecipe> $$0, String $$1) {
        ResourceLocation $$3 = new ResourceLocation($$1);
        ResourceLocation $$2 = RecipeBuilder.getDefaultRecipeId(this.getResult());
        if ($$3.equals($$2)) {
            throw new IllegalStateException("Recipe " + $$1 + " should remove its 'save' argument as it is equal to default one");
        }
        this.save($$0, $$3);
    }

    public static ResourceLocation getDefaultRecipeId(ItemLike $$0) {
        return BuiltInRegistries.ITEM.getKey($$0.asItem());
    }
}