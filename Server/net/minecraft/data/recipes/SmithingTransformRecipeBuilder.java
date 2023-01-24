/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 */
package net.minecraft.data.recipes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class SmithingTransformRecipeBuilder {
    private final Ingredient template;
    private final Ingredient base;
    private final Ingredient addition;
    private final RecipeCategory category;
    private final Item result;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    private final RecipeSerializer<?> type;

    public SmithingTransformRecipeBuilder(RecipeSerializer<?> $$0, Ingredient $$1, Ingredient $$2, Ingredient $$3, RecipeCategory $$4, Item $$5) {
        this.category = $$4;
        this.type = $$0;
        this.template = $$1;
        this.base = $$2;
        this.addition = $$3;
        this.result = $$5;
    }

    public static SmithingTransformRecipeBuilder smithing(Ingredient $$0, Ingredient $$1, Ingredient $$2, RecipeCategory $$3, Item $$4) {
        return new SmithingTransformRecipeBuilder(RecipeSerializer.SMITHING_TRANSFORM, $$0, $$1, $$2, $$3, $$4);
    }

    public SmithingTransformRecipeBuilder unlocks(String $$0, CriterionTriggerInstance $$1) {
        this.advancement.addCriterion($$0, $$1);
        return this;
    }

    public void save(Consumer<FinishedRecipe> $$0, String $$1) {
        this.save($$0, new ResourceLocation($$1));
    }

    public void save(Consumer<FinishedRecipe> $$0, ResourceLocation $$1) {
        this.ensureValid($$1);
        this.advancement.parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked($$1)).rewards(AdvancementRewards.Builder.recipe($$1)).requirements(RequirementsStrategy.OR);
        $$0.accept((Object)new Result($$1, this.type, this.template, this.base, this.addition, this.result, this.advancement, $$1.withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }

    private void ensureValid(ResourceLocation $$0) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + $$0);
        }
    }

    public record Result(ResourceLocation id, RecipeSerializer<?> type, Ingredient template, Ingredient base, Ingredient addition, Item result, Advancement.Builder advancement, ResourceLocation advancementId) implements FinishedRecipe
    {
        @Override
        public void serializeRecipeData(JsonObject $$0) {
            $$0.add("template", this.template.toJson());
            $$0.add("base", this.base.toJson());
            $$0.add("addition", this.addition.toJson());
            JsonObject $$1 = new JsonObject();
            $$1.addProperty("item", BuiltInRegistries.ITEM.getKey(this.result).toString());
            $$0.add("result", (JsonElement)$$1);
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return this.type;
        }

        @Override
        @Nullable
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Override
        @Nullable
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}