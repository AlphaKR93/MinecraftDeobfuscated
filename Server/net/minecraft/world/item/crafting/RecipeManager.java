/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.Comparator
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Optional
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.item.crafting;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public class RecipeManager
extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();
    private Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipes = ImmutableMap.of();
    private Map<ResourceLocation, Recipe<?>> byName = ImmutableMap.of();
    private boolean hasErrors;

    public RecipeManager() {
        super(GSON, "recipes");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> $$02, ResourceManager $$1, ProfilerFiller $$2) {
        this.hasErrors = false;
        HashMap $$3 = Maps.newHashMap();
        ImmutableMap.Builder $$4 = ImmutableMap.builder();
        for (Map.Entry $$5 : $$02.entrySet()) {
            ResourceLocation $$6 = (ResourceLocation)$$5.getKey();
            try {
                Recipe<?> $$7 = RecipeManager.fromJson($$6, GsonHelper.convertToJsonObject((JsonElement)$$5.getValue(), "top element"));
                ((ImmutableMap.Builder)$$3.computeIfAbsent($$7.getType(), $$0 -> ImmutableMap.builder())).put((Object)$$6, $$7);
                $$4.put((Object)$$6, $$7);
            }
            catch (JsonParseException | IllegalArgumentException $$8) {
                LOGGER.error("Parsing error loading recipe {}", (Object)$$6, (Object)$$8);
            }
        }
        this.recipes = (Map)$$3.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, $$0 -> ((ImmutableMap.Builder)$$0.getValue()).build()));
        this.byName = $$4.build();
        LOGGER.info("Loaded {} recipes", (Object)$$3.size());
    }

    public boolean hadErrorsLoading() {
        return this.hasErrors;
    }

    public <C extends Container, T extends Recipe<C>> Optional<T> getRecipeFor(RecipeType<T> $$0, C $$1, Level $$22) {
        return this.byType($$0).values().stream().filter($$2 -> $$2.matches($$1, $$22)).findFirst();
    }

    public <C extends Container, T extends Recipe<C>> Optional<Pair<ResourceLocation, T>> getRecipeFor(RecipeType<T> $$02, C $$1, Level $$22, @Nullable ResourceLocation $$3) {
        Recipe $$5;
        Map<ResourceLocation, T> $$4 = this.byType($$02);
        if ($$3 != null && ($$5 = (Recipe)$$4.get((Object)$$3)) != null && $$5.matches($$1, $$22)) {
            return Optional.of((Object)Pair.of((Object)$$3, (Object)$$5));
        }
        return $$4.entrySet().stream().filter($$2 -> ((Recipe)$$2.getValue()).matches($$1, $$22)).findFirst().map($$0 -> Pair.of((Object)((ResourceLocation)$$0.getKey()), (Object)((Recipe)$$0.getValue())));
    }

    public <C extends Container, T extends Recipe<C>> List<T> getAllRecipesFor(RecipeType<T> $$0) {
        return List.copyOf((Collection)this.byType($$0).values());
    }

    public <C extends Container, T extends Recipe<C>> List<T> getRecipesFor(RecipeType<T> $$02, C $$1, Level $$22) {
        return (List)this.byType($$02).values().stream().filter($$2 -> $$2.matches($$1, $$22)).sorted(Comparator.comparing($$0 -> $$0.getResultItem().getDescriptionId())).collect(Collectors.toList());
    }

    private <C extends Container, T extends Recipe<C>> Map<ResourceLocation, T> byType(RecipeType<T> $$0) {
        return (Map)this.recipes.getOrDefault($$0, (Object)Collections.emptyMap());
    }

    public <C extends Container, T extends Recipe<C>> NonNullList<ItemStack> getRemainingItemsFor(RecipeType<T> $$0, C $$1, Level $$2) {
        Optional<T> $$3 = this.getRecipeFor($$0, $$1, $$2);
        if ($$3.isPresent()) {
            return ((Recipe)$$3.get()).getRemainingItems($$1);
        }
        NonNullList<ItemStack> $$4 = NonNullList.withSize($$1.getContainerSize(), ItemStack.EMPTY);
        for (int $$5 = 0; $$5 < $$4.size(); ++$$5) {
            $$4.set($$5, $$1.getItem($$5));
        }
        return $$4;
    }

    public Optional<? extends Recipe<?>> byKey(ResourceLocation $$0) {
        return Optional.ofNullable((Object)((Recipe)this.byName.get((Object)$$0)));
    }

    public Collection<Recipe<?>> getRecipes() {
        return (Collection)this.recipes.values().stream().flatMap($$0 -> $$0.values().stream()).collect(Collectors.toSet());
    }

    public Stream<ResourceLocation> getRecipeIds() {
        return this.recipes.values().stream().flatMap($$0 -> $$0.keySet().stream());
    }

    public static Recipe<?> fromJson(ResourceLocation $$0, JsonObject $$1) {
        String $$2 = GsonHelper.getAsString($$1, "type");
        return ((RecipeSerializer)BuiltInRegistries.RECIPE_SERIALIZER.getOptional(new ResourceLocation($$2)).orElseThrow(() -> new JsonSyntaxException("Invalid or unsupported recipe type '" + $$2 + "'"))).fromJson($$0, $$1);
    }

    public void replaceRecipes(Iterable<Recipe<?>> $$0) {
        this.hasErrors = false;
        HashMap $$1 = Maps.newHashMap();
        ImmutableMap.Builder $$2 = ImmutableMap.builder();
        $$0.forEach(arg_0 -> RecipeManager.lambda$replaceRecipes$11((Map)$$1, $$2, arg_0));
        this.recipes = ImmutableMap.copyOf((Map)$$1);
        this.byName = $$2.build();
    }

    public static <C extends Container, T extends Recipe<C>> CachedCheck<C, T> createCheck(final RecipeType<T> $$0) {
        return new CachedCheck<C, T>(){
            @Nullable
            private ResourceLocation lastRecipe;

            @Override
            public Optional<T> getRecipeFor(C $$02, Level $$1) {
                RecipeManager $$2 = $$1.getRecipeManager();
                Optional $$3 = $$2.getRecipeFor($$0, $$02, $$1, this.lastRecipe);
                if ($$3.isPresent()) {
                    Pair $$4 = (Pair)$$3.get();
                    this.lastRecipe = (ResourceLocation)$$4.getFirst();
                    return Optional.of((Object)((Recipe)$$4.getSecond()));
                }
                return Optional.empty();
            }
        };
    }

    private static /* synthetic */ void lambda$replaceRecipes$11(Map $$02, ImmutableMap.Builder $$1, Recipe $$2) {
        Map $$3 = (Map)$$02.computeIfAbsent($$2.getType(), $$0 -> Maps.newHashMap());
        ResourceLocation $$4 = $$2.getId();
        Recipe $$5 = (Recipe)$$3.put((Object)$$4, (Object)$$2);
        $$1.put((Object)$$4, (Object)$$2);
        if ($$5 != null) {
            throw new IllegalStateException("Duplicate recipe ignored with ID " + $$4);
        }
    }

    public static interface CachedCheck<C extends Container, T extends Recipe<C>> {
        public Optional<T> getRecipeFor(C var1, Level var2);
    }
}