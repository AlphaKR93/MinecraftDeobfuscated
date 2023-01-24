/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  java.lang.Deprecated
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

@Deprecated(forRemoval=true)
public class LegacyUpgradeRecipeBuilder {
    private final Ingredient base;
    private final Ingredient addition;
    private final RecipeCategory category;
    private final Item result;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    private final RecipeSerializer<?> type;

    public LegacyUpgradeRecipeBuilder(RecipeSerializer<?> $$0, Ingredient $$1, Ingredient $$2, RecipeCategory $$3, Item $$4) {
        this.category = $$3;
        this.type = $$0;
        this.base = $$1;
        this.addition = $$2;
        this.result = $$4;
    }

    public static LegacyUpgradeRecipeBuilder smithing(Ingredient $$0, Ingredient $$1, RecipeCategory $$2, Item $$3) {
        return new LegacyUpgradeRecipeBuilder(RecipeSerializer.SMITHING, $$0, $$1, $$2, $$3);
    }

    public LegacyUpgradeRecipeBuilder unlocks(String $$0, CriterionTriggerInstance $$1) {
        this.advancement.addCriterion($$0, $$1);
        return this;
    }

    public void save(Consumer<FinishedRecipe> $$0, String $$1) {
        this.save($$0, new ResourceLocation($$1));
    }

    public void save(Consumer<FinishedRecipe> $$0, ResourceLocation $$1) {
        this.ensureValid($$1);
        this.advancement.parent(RecipeBuilder.ROOT_RECIPE_ADVANCEMENT).addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked($$1)).rewards(AdvancementRewards.Builder.recipe($$1)).requirements(RequirementsStrategy.OR);
        $$0.accept((Object)new Result($$1, this.type, this.base, this.addition, this.result, this.advancement, $$1.withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }

    private void ensureValid(ResourceLocation $$0) {
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + $$0);
        }
    }

    public static class Result
    implements FinishedRecipe {
        private final ResourceLocation id;
        private final Ingredient base;
        private final Ingredient addition;
        private final Item result;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;
        private final RecipeSerializer<?> type;

        public Result(ResourceLocation $$0, RecipeSerializer<?> $$1, Ingredient $$2, Ingredient $$3, Item $$4, Advancement.Builder $$5, ResourceLocation $$6) {
            this.id = $$0;
            this.type = $$1;
            this.base = $$2;
            this.addition = $$3;
            this.result = $$4;
            this.advancement = $$5;
            this.advancementId = $$6;
        }

        @Override
        public void serializeRecipeData(JsonObject $$0) {
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