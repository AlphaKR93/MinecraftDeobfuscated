/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Number
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
import net.minecraft.world.level.ItemLike;

public class SingleItemRecipeBuilder
implements RecipeBuilder {
    private final RecipeCategory category;
    private final Item result;
    private final Ingredient ingredient;
    private final int count;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    @Nullable
    private String group;
    private final RecipeSerializer<?> type;

    public SingleItemRecipeBuilder(RecipeCategory $$0, RecipeSerializer<?> $$1, Ingredient $$2, ItemLike $$3, int $$4) {
        this.category = $$0;
        this.type = $$1;
        this.result = $$3.asItem();
        this.ingredient = $$2;
        this.count = $$4;
    }

    public static SingleItemRecipeBuilder stonecutting(Ingredient $$0, RecipeCategory $$1, ItemLike $$2) {
        return new SingleItemRecipeBuilder($$1, RecipeSerializer.STONECUTTER, $$0, $$2, 1);
    }

    public static SingleItemRecipeBuilder stonecutting(Ingredient $$0, RecipeCategory $$1, ItemLike $$2, int $$3) {
        return new SingleItemRecipeBuilder($$1, RecipeSerializer.STONECUTTER, $$0, $$2, $$3);
    }

    @Override
    public SingleItemRecipeBuilder unlockedBy(String $$0, CriterionTriggerInstance $$1) {
        this.advancement.addCriterion($$0, $$1);
        return this;
    }

    @Override
    public SingleItemRecipeBuilder group(@Nullable String $$0) {
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
        $$0.accept((Object)new Result($$1, this.type, this.group == null ? "" : this.group, this.ingredient, this.result, this.count, this.advancement, $$1.withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }

    private void ensureValid(ResourceLocation $$0) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + $$0);
        }
    }

    public static class Result
    implements FinishedRecipe {
        private final ResourceLocation id;
        private final String group;
        private final Ingredient ingredient;
        private final Item result;
        private final int count;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;
        private final RecipeSerializer<?> type;

        public Result(ResourceLocation $$0, RecipeSerializer<?> $$1, String $$2, Ingredient $$3, Item $$4, int $$5, Advancement.Builder $$6, ResourceLocation $$7) {
            this.id = $$0;
            this.type = $$1;
            this.group = $$2;
            this.ingredient = $$3;
            this.result = $$4;
            this.count = $$5;
            this.advancement = $$6;
            this.advancementId = $$7;
        }

        @Override
        public void serializeRecipeData(JsonObject $$0) {
            if (!this.group.isEmpty()) {
                $$0.addProperty("group", this.group);
            }
            $$0.add("ingredient", this.ingredient.toJson());
            $$0.addProperty("result", BuiltInRegistries.ITEM.getKey(this.result).toString());
            $$0.addProperty("count", (Number)Integer.valueOf((int)this.count));
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