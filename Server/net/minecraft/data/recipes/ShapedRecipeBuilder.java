/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  java.lang.Character
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Iterable
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.HashSet
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 */
package net.minecraft.data.recipes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

public class ShapedRecipeBuilder
extends CraftingRecipeBuilder
implements RecipeBuilder {
    private final RecipeCategory category;
    private final Item result;
    private final int count;
    private final List<String> rows = Lists.newArrayList();
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    @Nullable
    private String group;

    public ShapedRecipeBuilder(RecipeCategory $$0, ItemLike $$1, int $$2) {
        this.category = $$0;
        this.result = $$1.asItem();
        this.count = $$2;
    }

    public static ShapedRecipeBuilder shaped(RecipeCategory $$0, ItemLike $$1) {
        return ShapedRecipeBuilder.shaped($$0, $$1, 1);
    }

    public static ShapedRecipeBuilder shaped(RecipeCategory $$0, ItemLike $$1, int $$2) {
        return new ShapedRecipeBuilder($$0, $$1, $$2);
    }

    public ShapedRecipeBuilder define(Character $$0, TagKey<Item> $$1) {
        return this.define($$0, Ingredient.of($$1));
    }

    public ShapedRecipeBuilder define(Character $$0, ItemLike $$1) {
        return this.define($$0, Ingredient.of($$1));
    }

    public ShapedRecipeBuilder define(Character $$0, Ingredient $$1) {
        if (this.key.containsKey((Object)$$0)) {
            throw new IllegalArgumentException("Symbol '" + $$0 + "' is already defined!");
        }
        if ($$0.charValue() == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        }
        this.key.put((Object)$$0, (Object)$$1);
        return this;
    }

    public ShapedRecipeBuilder pattern(String $$0) {
        if (!this.rows.isEmpty() && $$0.length() != ((String)this.rows.get(0)).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        }
        this.rows.add((Object)$$0);
        return this;
    }

    @Override
    public ShapedRecipeBuilder unlockedBy(String $$0, CriterionTriggerInstance $$1) {
        this.advancement.addCriterion($$0, $$1);
        return this;
    }

    @Override
    public ShapedRecipeBuilder group(@Nullable String $$0) {
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
        $$0.accept((Object)new Result($$1, this.result, this.count, this.group == null ? "" : this.group, ShapedRecipeBuilder.determineBookCategory(this.category), this.rows, this.key, this.advancement, $$1.withPrefix("recipes/" + this.category.getFolderName() + "/")));
    }

    private void ensureValid(ResourceLocation $$0) {
        if (this.rows.isEmpty()) {
            throw new IllegalStateException("No pattern is defined for shaped recipe " + $$0 + "!");
        }
        HashSet $$1 = Sets.newHashSet((Iterable)this.key.keySet());
        $$1.remove((Object)Character.valueOf((char)' '));
        for (String $$2 : this.rows) {
            for (int $$3 = 0; $$3 < $$2.length(); ++$$3) {
                char $$4 = $$2.charAt($$3);
                if (!this.key.containsKey((Object)Character.valueOf((char)$$4)) && $$4 != ' ') {
                    throw new IllegalStateException("Pattern in recipe " + $$0 + " uses undefined symbol '" + $$4 + "'");
                }
                $$1.remove((Object)Character.valueOf((char)$$4));
            }
        }
        if (!$$1.isEmpty()) {
            throw new IllegalStateException("Ingredients are defined but not used in pattern for recipe " + $$0);
        }
        if (this.rows.size() == 1 && ((String)this.rows.get(0)).length() == 1) {
            throw new IllegalStateException("Shaped recipe " + $$0 + " only takes in a single item - should it be a shapeless recipe instead?");
        }
        if (this.advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe " + $$0);
        }
    }

    static class Result
    extends CraftingRecipeBuilder.CraftingResult {
        private final ResourceLocation id;
        private final Item result;
        private final int count;
        private final String group;
        private final List<String> pattern;
        private final Map<Character, Ingredient> key;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation $$0, Item $$1, int $$2, String $$3, CraftingBookCategory $$4, List<String> $$5, Map<Character, Ingredient> $$6, Advancement.Builder $$7, ResourceLocation $$8) {
            super($$4);
            this.id = $$0;
            this.result = $$1;
            this.count = $$2;
            this.group = $$3;
            this.pattern = $$5;
            this.key = $$6;
            this.advancement = $$7;
            this.advancementId = $$8;
        }

        @Override
        public void serializeRecipeData(JsonObject $$0) {
            super.serializeRecipeData($$0);
            if (!this.group.isEmpty()) {
                $$0.addProperty("group", this.group);
            }
            JsonArray $$1 = new JsonArray();
            for (String $$2 : this.pattern) {
                $$1.add($$2);
            }
            $$0.add("pattern", (JsonElement)$$1);
            JsonObject $$3 = new JsonObject();
            for (Map.Entry $$4 : this.key.entrySet()) {
                $$3.add(String.valueOf((Object)$$4.getKey()), ((Ingredient)$$4.getValue()).toJson());
            }
            $$0.add("key", (JsonElement)$$3);
            JsonObject $$5 = new JsonObject();
            $$5.addProperty("item", BuiltInRegistries.ITEM.getKey(this.result).toString());
            if (this.count > 1) {
                $$5.addProperty("count", (Number)Integer.valueOf((int)this.count));
            }
            $$0.add("result", (JsonElement)$$5);
        }

        @Override
        public RecipeSerializer<?> getType() {
            return RecipeSerializer.SHAPED_RECIPE;
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