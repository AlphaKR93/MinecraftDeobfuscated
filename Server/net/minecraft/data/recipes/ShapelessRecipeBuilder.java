/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 */
package net.minecraft.data.recipes;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.CraftingRecipeBuilder;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

public class ShapelessRecipeBuilder
extends CraftingRecipeBuilder
implements RecipeBuilder {
    private final RecipeCategory category;
    private final Item result;
    private final int count;
    private final List<Ingredient> ingredients = Lists.newArrayList();
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    @Nullable
    private String group;

    public ShapelessRecipeBuilder(RecipeCategory $$0, ItemLike $$1, int $$2) {
        this.category = $$0;
        this.result = $$1.asItem();
        this.count = $$2;
    }

    public static ShapelessRecipeBuilder shapeless(RecipeCategory $$0, ItemLike $$1) {
        return new ShapelessRecipeBuilder($$0, $$1, 1);
    }

    public static ShapelessRecipeBuilder shapeless(RecipeCategory $$0, ItemLike $$1, int $$2) {
        return new ShapelessRecipeBuilder($$0, $$1, $$2);
    }

    public ShapelessRecipeBuilder requires(TagKey<Item> $$0) {
        return this.requires(Ingredient.of($$0));
    }

    public ShapelessRecipeBuilder requires(ItemLike $$0) {
        return this.requires($$0, 1);
    }

    public ShapelessRecipeBuilder requires(ItemLike $$0, int $$1) {
        for (int $$2 = 0; $$2 < $$1; ++$$2) {
            this.requires(Ingredient.of($$0));
        }
        return this;
    }

    public ShapelessRecipeBuilder requires(Ingredient $$0) {
        return this.requires($$0, 1);
    }

    public ShapelessRecipeBuilder requires(Ingredient $$0, int $$1) {
        for (int $$2 = 0; $$2 < $$1; ++$$2) {
            this.ingredients.add((Object)$$0);
        }
        return this;
    }

    @Override
    public ShapelessRecipeBuilder unlockedBy(String $$0, CriterionTriggerInstance $$1) {
        this.advancement.addCriterion($$0, $$1);
        return this;
    }

    @Override
    public ShapelessRecipeBuilder group(@Nullable String $$0) {
        this.group = $$0;
        return this;
    }

    @Override
    public Item getResult() {
        return this.result;
    }

    @Override
    public void save(Consumer<FinishedRecipe> $$0, ResourceLocation $$1) {
        this.ensureValid($$1);
        this.advancement.parent(ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked($$1)).rewards(AdvancementRewards.Builder.recipe($$1)).requirements(RequirementsStrategy.OR);
        $$0.accept((Object)new Result($$1, this.result, this.count, this.group == null ? "" : this.group, ShapelessRecipeBuilder.determineBookCategory(this.category), this.ingredients, this.advancement, $$1.withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }

    private void ensureValid(ResourceLocation $$0) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + $$0);
        }
    }

    public static class Result
    extends CraftingRecipeBuilder.CraftingResult {
        private final ResourceLocation id;
        private final Item result;
        private final int count;
        private final String group;
        private final List<Ingredient> ingredients;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation $$0, Item $$1, int $$2, String $$3, CraftingBookCategory $$4, List<Ingredient> $$5, Advancement.Builder $$6, ResourceLocation $$7) {
            super($$4);
            this.id = $$0;
            this.result = $$1;
            this.count = $$2;
            this.group = $$3;
            this.ingredients = $$5;
            this.advancement = $$6;
            this.advancementId = $$7;
        }

        @Override
        public void serializeRecipeData(JsonObject $$0) {
            super.serializeRecipeData($$0);
            if (!this.group.isEmpty()) {
                $$0.addProperty("group", this.group);
            }
            JsonArray $$1 = new JsonArray();
            for (Ingredient $$2 : this.ingredients) {
                $$1.add($$2.toJson());
            }
            $$0.add("ingredients", (JsonElement)$$1);
            JsonObject $$3 = new JsonObject();
            $$3.addProperty("item", BuiltInRegistries.ITEM.getKey(this.result).toString());
            if (this.count > 1) {
                $$3.addProperty("count", (Number)Integer.valueOf((int)this.count));
            }
            $$0.add("result", (JsonElement)$$3);
        }

        @Override
        public RecipeSerializer<?> getType() {
            return RecipeSerializer.SHAPELESS_RECIPE;
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
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