/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashBasedTable
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.logging.LogUtils
 *  java.lang.IncompatibleClassChangeError
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collections
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Map
 *  org.slf4j.Logger
 */
package net.minecraft.client;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import org.slf4j.Logger;

public class ClientRecipeBook
extends RecipeBook {
    private static final Logger LOGGER = LogUtils.getLogger();
    private Map<RecipeBookCategories, List<RecipeCollection>> collectionsByTab = ImmutableMap.of();
    private List<RecipeCollection> allCollections = ImmutableList.of();

    public void setupCollections(Iterable<Recipe<?>> $$0) {
        Map<RecipeBookCategories, List<List<Recipe<?>>>> $$1 = ClientRecipeBook.categorizeAndGroupRecipes($$0);
        HashMap $$2 = Maps.newHashMap();
        ImmutableList.Builder $$3 = ImmutableList.builder();
        $$1.forEach((arg_0, arg_1) -> ClientRecipeBook.lambda$setupCollections$0((Map)$$2, $$3, arg_0, arg_1));
        RecipeBookCategories.AGGREGATE_CATEGORIES.forEach((arg_0, arg_1) -> ClientRecipeBook.lambda$setupCollections$2((Map)$$2, arg_0, arg_1));
        this.collectionsByTab = ImmutableMap.copyOf((Map)$$2);
        this.allCollections = $$3.build();
    }

    private static Map<RecipeBookCategories, List<List<Recipe<?>>>> categorizeAndGroupRecipes(Iterable<Recipe<?>> $$02) {
        HashMap $$1 = Maps.newHashMap();
        HashBasedTable $$2 = HashBasedTable.create();
        for (Recipe $$3 : $$02) {
            if ($$3.isSpecial() || $$3.isIncomplete()) continue;
            RecipeBookCategories $$4 = ClientRecipeBook.getCategory($$3);
            String $$5 = $$3.getGroup();
            if ($$5.isEmpty()) {
                ((List)$$1.computeIfAbsent((Object)$$4, $$0 -> Lists.newArrayList())).add((Object)ImmutableList.of((Object)$$3));
                continue;
            }
            List $$6 = (List)$$2.get((Object)$$4, (Object)$$5);
            if ($$6 == null) {
                $$6 = Lists.newArrayList();
                $$2.put((Object)$$4, (Object)$$5, (Object)$$6);
                ((List)$$1.computeIfAbsent((Object)$$4, $$0 -> Lists.newArrayList())).add((Object)$$6);
            }
            $$6.add((Object)$$3);
        }
        return $$1;
    }

    private static RecipeBookCategories getCategory(Recipe<?> $$0) {
        if ($$0 instanceof CraftingRecipe) {
            CraftingRecipe $$1 = (CraftingRecipe)$$0;
            return switch ($$1.category()) {
                default -> throw new IncompatibleClassChangeError();
                case CraftingBookCategory.BUILDING -> RecipeBookCategories.CRAFTING_BUILDING_BLOCKS;
                case CraftingBookCategory.EQUIPMENT -> RecipeBookCategories.CRAFTING_EQUIPMENT;
                case CraftingBookCategory.REDSTONE -> RecipeBookCategories.CRAFTING_REDSTONE;
                case CraftingBookCategory.MISC -> RecipeBookCategories.CRAFTING_MISC;
            };
        }
        RecipeType<?> $$2 = $$0.getType();
        if ($$0 instanceof AbstractCookingRecipe) {
            AbstractCookingRecipe $$3 = (AbstractCookingRecipe)$$0;
            CookingBookCategory $$4 = $$3.category();
            if ($$2 == RecipeType.SMELTING) {
                return switch ($$4) {
                    default -> throw new IncompatibleClassChangeError();
                    case CookingBookCategory.BLOCKS -> RecipeBookCategories.FURNACE_BLOCKS;
                    case CookingBookCategory.FOOD -> RecipeBookCategories.FURNACE_FOOD;
                    case CookingBookCategory.MISC -> RecipeBookCategories.FURNACE_MISC;
                };
            }
            if ($$2 == RecipeType.BLASTING) {
                return $$4 == CookingBookCategory.BLOCKS ? RecipeBookCategories.BLAST_FURNACE_BLOCKS : RecipeBookCategories.BLAST_FURNACE_MISC;
            }
            if ($$2 == RecipeType.SMOKING) {
                return RecipeBookCategories.SMOKER_FOOD;
            }
            if ($$2 == RecipeType.CAMPFIRE_COOKING) {
                return RecipeBookCategories.CAMPFIRE;
            }
        }
        if ($$2 == RecipeType.STONECUTTING) {
            return RecipeBookCategories.STONECUTTER;
        }
        if ($$2 == RecipeType.SMITHING) {
            return RecipeBookCategories.SMITHING;
        }
        LOGGER.warn("Unknown recipe category: {}/{}", LogUtils.defer(() -> BuiltInRegistries.RECIPE_TYPE.getKey($$0.getType())), LogUtils.defer($$0::getId));
        return RecipeBookCategories.UNKNOWN;
    }

    public List<RecipeCollection> getCollections() {
        return this.allCollections;
    }

    public List<RecipeCollection> getCollection(RecipeBookCategories $$0) {
        return (List)this.collectionsByTab.getOrDefault((Object)$$0, (Object)Collections.emptyList());
    }

    private static /* synthetic */ void lambda$setupCollections$2(Map $$0, RecipeBookCategories $$12, List $$2) {
        $$0.put((Object)$$12, (Object)((List)$$2.stream().flatMap($$1 -> ((List)$$0.getOrDefault((Object)$$1, (Object)ImmutableList.of())).stream()).collect(ImmutableList.toImmutableList())));
    }

    private static /* synthetic */ void lambda$setupCollections$0(Map $$0, ImmutableList.Builder $$1, RecipeBookCategories $$2, List $$3) {
        $$0.put((Object)$$2, (Object)((List)$$3.stream().map(RecipeCollection::new).peek(arg_0 -> ((ImmutableList.Builder)$$1).add(arg_0)).collect(ImmutableList.toImmutableList())));
    }
}