/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  java.lang.Float
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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

public class SimpleCookingRecipeBuilder
implements RecipeBuilder {
    private final RecipeCategory category;
    private final CookingBookCategory bookCategory;
    private final Item result;
    private final Ingredient ingredient;
    private final float experience;
    private final int cookingTime;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    @Nullable
    private String group;
    private final RecipeSerializer<? extends AbstractCookingRecipe> serializer;

    private SimpleCookingRecipeBuilder(RecipeCategory $$0, CookingBookCategory $$1, ItemLike $$2, Ingredient $$3, float $$4, int $$5, RecipeSerializer<? extends AbstractCookingRecipe> $$6) {
        this.category = $$0;
        this.bookCategory = $$1;
        this.result = $$2.asItem();
        this.ingredient = $$3;
        this.experience = $$4;
        this.cookingTime = $$5;
        this.serializer = $$6;
    }

    public static SimpleCookingRecipeBuilder generic(Ingredient $$0, RecipeCategory $$1, ItemLike $$2, float $$3, int $$4, RecipeSerializer<? extends AbstractCookingRecipe> $$5) {
        return new SimpleCookingRecipeBuilder($$1, SimpleCookingRecipeBuilder.determineRecipeCategory($$5, $$2), $$2, $$0, $$3, $$4, $$5);
    }

    public static SimpleCookingRecipeBuilder campfireCooking(Ingredient $$0, RecipeCategory $$1, ItemLike $$2, float $$3, int $$4) {
        return new SimpleCookingRecipeBuilder($$1, CookingBookCategory.FOOD, $$2, $$0, $$3, $$4, RecipeSerializer.CAMPFIRE_COOKING_RECIPE);
    }

    public static SimpleCookingRecipeBuilder blasting(Ingredient $$0, RecipeCategory $$1, ItemLike $$2, float $$3, int $$4) {
        return new SimpleCookingRecipeBuilder($$1, SimpleCookingRecipeBuilder.determineBlastingRecipeCategory($$2), $$2, $$0, $$3, $$4, RecipeSerializer.BLASTING_RECIPE);
    }

    public static SimpleCookingRecipeBuilder smelting(Ingredient $$0, RecipeCategory $$1, ItemLike $$2, float $$3, int $$4) {
        return new SimpleCookingRecipeBuilder($$1, SimpleCookingRecipeBuilder.determineSmeltingRecipeCategory($$2), $$2, $$0, $$3, $$4, RecipeSerializer.SMELTING_RECIPE);
    }

    public static SimpleCookingRecipeBuilder smoking(Ingredient $$0, RecipeCategory $$1, ItemLike $$2, float $$3, int $$4) {
        return new SimpleCookingRecipeBuilder($$1, CookingBookCategory.FOOD, $$2, $$0, $$3, $$4, RecipeSerializer.SMOKING_RECIPE);
    }

    @Override
    public SimpleCookingRecipeBuilder unlockedBy(String $$0, CriterionTriggerInstance $$1) {
        this.advancement.addCriterion($$0, $$1);
        return this;
    }

    @Override
    public SimpleCookingRecipeBuilder group(@Nullable String $$0) {
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
        $$0.accept((Object)new Result($$1, this.group == null ? "" : this.group, this.bookCategory, this.ingredient, this.result, this.experience, this.cookingTime, this.advancement, $$1.withPrefix("recipes/" + this.category.getFolderName() + "/"), this.serializer));
    }

    private static CookingBookCategory determineSmeltingRecipeCategory(ItemLike $$0) {
        if ($$0.asItem().isEdible()) {
            return CookingBookCategory.FOOD;
        }
        if ($$0.asItem() instanceof BlockItem) {
            return CookingBookCategory.BLOCKS;
        }
        return CookingBookCategory.MISC;
    }

    private static CookingBookCategory determineBlastingRecipeCategory(ItemLike $$0) {
        if ($$0.asItem() instanceof BlockItem) {
            return CookingBookCategory.BLOCKS;
        }
        return CookingBookCategory.MISC;
    }

    private static CookingBookCategory determineRecipeCategory(RecipeSerializer<? extends AbstractCookingRecipe> $$0, ItemLike $$1) {
        if ($$0 == RecipeSerializer.SMELTING_RECIPE) {
            return SimpleCookingRecipeBuilder.determineSmeltingRecipeCategory($$1);
        }
        if ($$0 == RecipeSerializer.BLASTING_RECIPE) {
            return SimpleCookingRecipeBuilder.determineBlastingRecipeCategory($$1);
        }
        if ($$0 == RecipeSerializer.SMOKING_RECIPE || $$0 == RecipeSerializer.CAMPFIRE_COOKING_RECIPE) {
            return CookingBookCategory.FOOD;
        }
        throw new IllegalStateException("Unknown cooking recipe type");
    }

    private void ensureValid(ResourceLocation $$0) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + $$0);
        }
    }

    static class Result
    implements FinishedRecipe {
        private final ResourceLocation id;
        private final String group;
        private final CookingBookCategory category;
        private final Ingredient ingredient;
        private final Item result;
        private final float experience;
        private final int cookingTime;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;
        private final RecipeSerializer<? extends AbstractCookingRecipe> serializer;

        public Result(ResourceLocation $$0, String $$1, CookingBookCategory $$2, Ingredient $$3, Item $$4, float $$5, int $$6, Advancement.Builder $$7, ResourceLocation $$8, RecipeSerializer<? extends AbstractCookingRecipe> $$9) {
            this.id = $$0;
            this.group = $$1;
            this.category = $$2;
            this.ingredient = $$3;
            this.result = $$4;
            this.experience = $$5;
            this.cookingTime = $$6;
            this.advancement = $$7;
            this.advancementId = $$8;
            this.serializer = $$9;
        }

        @Override
        public void serializeRecipeData(JsonObject $$0) {
            if (!this.group.isEmpty()) {
                $$0.addProperty("group", this.group);
            }
            $$0.addProperty("category", this.category.getSerializedName());
            $$0.add("ingredient", this.ingredient.toJson());
            $$0.addProperty("result", BuiltInRegistries.ITEM.getKey(this.result).toString());
            $$0.addProperty("experience", (Number)Float.valueOf((float)this.experience));
            $$0.addProperty("cookingtime", (Number)Integer.valueOf((int)this.cookingTime));
        }

        @Override
        public RecipeSerializer<?> getType() {
            return this.serializer;
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