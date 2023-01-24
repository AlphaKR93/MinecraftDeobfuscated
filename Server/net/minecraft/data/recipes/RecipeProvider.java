/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  java.lang.Character
 *  java.lang.Deprecated
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.HashSet
 *  java.util.List
 *  java.util.Map
 *  java.util.Set
 *  java.util.concurrent.CompletableFuture
 *  java.util.function.BiFunction
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 */
package net.minecraft.data.recipes;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.LegacyUpgradeRecipeBuilder;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.data.recipes.SmithingTrimRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public abstract class RecipeProvider
implements DataProvider {
    private final PackOutput.PathProvider recipePathProvider;
    private final PackOutput.PathProvider advancementPathProvider;
    private static final Map<BlockFamily.Variant, BiFunction<ItemLike, ItemLike, RecipeBuilder>> SHAPE_BUILDERS = ImmutableMap.builder().put((Object)BlockFamily.Variant.BUTTON, ($$0, $$1) -> RecipeProvider.buttonBuilder($$0, Ingredient.of($$1))).put((Object)BlockFamily.Variant.CHISELED, ($$0, $$1) -> RecipeProvider.chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, $$0, Ingredient.of($$1))).put((Object)BlockFamily.Variant.CUT, ($$0, $$1) -> RecipeProvider.cutBuilder(RecipeCategory.BUILDING_BLOCKS, $$0, Ingredient.of($$1))).put((Object)BlockFamily.Variant.DOOR, ($$0, $$1) -> RecipeProvider.doorBuilder($$0, Ingredient.of($$1))).put((Object)BlockFamily.Variant.CUSTOM_FENCE, ($$0, $$1) -> RecipeProvider.fenceBuilder($$0, Ingredient.of($$1))).put((Object)BlockFamily.Variant.FENCE, ($$0, $$1) -> RecipeProvider.fenceBuilder($$0, Ingredient.of($$1))).put((Object)BlockFamily.Variant.CUSTOM_FENCE_GATE, ($$0, $$1) -> RecipeProvider.fenceGateBuilder($$0, Ingredient.of($$1))).put((Object)BlockFamily.Variant.FENCE_GATE, ($$0, $$1) -> RecipeProvider.fenceGateBuilder($$0, Ingredient.of($$1))).put((Object)BlockFamily.Variant.SIGN, ($$0, $$1) -> RecipeProvider.signBuilder($$0, Ingredient.of($$1))).put((Object)BlockFamily.Variant.SLAB, ($$0, $$1) -> RecipeProvider.slabBuilder(RecipeCategory.BUILDING_BLOCKS, $$0, Ingredient.of($$1))).put((Object)BlockFamily.Variant.STAIRS, ($$0, $$1) -> RecipeProvider.stairBuilder($$0, Ingredient.of($$1))).put((Object)BlockFamily.Variant.PRESSURE_PLATE, ($$0, $$1) -> RecipeProvider.pressurePlateBuilder(RecipeCategory.REDSTONE, $$0, Ingredient.of($$1))).put((Object)BlockFamily.Variant.POLISHED, ($$0, $$1) -> RecipeProvider.polishedBuilder(RecipeCategory.BUILDING_BLOCKS, $$0, Ingredient.of($$1))).put((Object)BlockFamily.Variant.TRAPDOOR, ($$0, $$1) -> RecipeProvider.trapdoorBuilder($$0, Ingredient.of($$1))).put((Object)BlockFamily.Variant.WALL, ($$0, $$1) -> RecipeProvider.wallBuilder(RecipeCategory.DECORATIONS, $$0, Ingredient.of($$1))).build();

    public RecipeProvider(PackOutput $$0) {
        this.recipePathProvider = $$0.createPathProvider(PackOutput.Target.DATA_PACK, "recipes");
        this.advancementPathProvider = $$0.createPathProvider(PackOutput.Target.DATA_PACK, "advancements");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput $$0) {
        HashSet $$1 = Sets.newHashSet();
        ArrayList $$2 = new ArrayList();
        this.buildRecipes((Consumer<FinishedRecipe>)((Consumer)arg_0 -> this.lambda$run$0((Set)$$1, (List)$$2, $$0, arg_0)));
        return CompletableFuture.allOf((CompletableFuture[])((CompletableFuture[])$$2.toArray(CompletableFuture[]::new)));
    }

    protected CompletableFuture<?> buildAdvancement(CachedOutput $$0, ResourceLocation $$1, Advancement.Builder $$2) {
        return DataProvider.saveStable($$0, (JsonElement)$$2.serializeToJson(), this.advancementPathProvider.json($$1));
    }

    protected abstract void buildRecipes(Consumer<FinishedRecipe> var1);

    protected static void generateForEnabledBlockFamilies(Consumer<FinishedRecipe> $$0, FeatureFlagSet $$12) {
        BlockFamilies.getAllFamilies().filter($$1 -> $$1.shouldGenerateRecipe($$12)).forEach($$1 -> RecipeProvider.generateRecipes($$0, $$1));
    }

    protected static void oneToOneConversionRecipe(Consumer<FinishedRecipe> $$0, ItemLike $$1, ItemLike $$2, @Nullable String $$3) {
        RecipeProvider.oneToOneConversionRecipe($$0, $$1, $$2, $$3, 1);
    }

    protected static void oneToOneConversionRecipe(Consumer<FinishedRecipe> $$0, ItemLike $$1, ItemLike $$2, @Nullable String $$3, int $$4) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, $$1, $$4).requires($$2).group($$3).unlockedBy(RecipeProvider.getHasName($$2), RecipeProvider.has($$2)).save($$0, RecipeProvider.getConversionRecipeName($$1, $$2));
    }

    protected static void oreSmelting(Consumer<FinishedRecipe> $$0, List<ItemLike> $$1, RecipeCategory $$2, ItemLike $$3, float $$4, int $$5, String $$6) {
        RecipeProvider.oreCooking($$0, RecipeSerializer.SMELTING_RECIPE, $$1, $$2, $$3, $$4, $$5, $$6, "_from_smelting");
    }

    protected static void oreBlasting(Consumer<FinishedRecipe> $$0, List<ItemLike> $$1, RecipeCategory $$2, ItemLike $$3, float $$4, int $$5, String $$6) {
        RecipeProvider.oreCooking($$0, RecipeSerializer.BLASTING_RECIPE, $$1, $$2, $$3, $$4, $$5, $$6, "_from_blasting");
    }

    private static void oreCooking(Consumer<FinishedRecipe> $$0, RecipeSerializer<? extends AbstractCookingRecipe> $$1, List<ItemLike> $$2, RecipeCategory $$3, ItemLike $$4, float $$5, int $$6, String $$7, String $$8) {
        for (ItemLike $$9 : $$2) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of($$9), $$3, $$4, $$5, $$6, $$1).group($$7).unlockedBy(RecipeProvider.getHasName($$9), RecipeProvider.has($$9)).save($$0, RecipeProvider.getItemName($$4) + $$8 + "_" + RecipeProvider.getItemName($$9));
        }
    }

    @Deprecated
    protected static void legacyNetheriteSmithing(Consumer<FinishedRecipe> $$0, Item $$1, RecipeCategory $$2, Item $$3) {
        LegacyUpgradeRecipeBuilder.smithing(Ingredient.of($$1), Ingredient.of(Items.NETHERITE_INGOT), $$2, $$3).unlocks("has_netherite_ingot", RecipeProvider.has(Items.NETHERITE_INGOT)).save($$0, RecipeProvider.getItemName($$3) + "_smithing");
    }

    protected static void netheriteSmithing(Consumer<FinishedRecipe> $$0, Item $$1, RecipeCategory $$2, Item $$3) {
        SmithingTransformRecipeBuilder.smithing(Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of($$1), Ingredient.of(Items.NETHERITE_INGOT), $$2, $$3).unlocks("has_netherite_ingot", RecipeProvider.has(Items.NETHERITE_INGOT)).save($$0, RecipeProvider.getItemName($$3) + "_smithing");
    }

    protected static void trimSmithing(Consumer<FinishedRecipe> $$0, Item $$1) {
        SmithingTrimRecipeBuilder.smithingTrim(Ingredient.of($$1), Ingredient.of(ItemTags.TRIMMABLE_ARMOR), Ingredient.of(ItemTags.TRIM_MATERIALS), RecipeCategory.MISC).unlocks("has_smithing_trim_template", RecipeProvider.has($$1)).save($$0, RecipeProvider.getItemName($$1) + "_smithing_trim");
    }

    protected static void twoByTwoPacker(Consumer<FinishedRecipe> $$0, RecipeCategory $$1, ItemLike $$2, ItemLike $$3) {
        ShapedRecipeBuilder.shaped($$1, $$2, 1).define(Character.valueOf((char)'#'), $$3).pattern("##").pattern("##").unlockedBy(RecipeProvider.getHasName($$3), RecipeProvider.has($$3)).save($$0);
    }

    protected static void threeByThreePacker(Consumer<FinishedRecipe> $$0, RecipeCategory $$1, ItemLike $$2, ItemLike $$3, String $$4) {
        ShapelessRecipeBuilder.shapeless($$1, $$2).requires($$3, 9).unlockedBy($$4, RecipeProvider.has($$3)).save($$0);
    }

    protected static void threeByThreePacker(Consumer<FinishedRecipe> $$0, RecipeCategory $$1, ItemLike $$2, ItemLike $$3) {
        RecipeProvider.threeByThreePacker($$0, $$1, $$2, $$3, RecipeProvider.getHasName($$3));
    }

    protected static void planksFromLog(Consumer<FinishedRecipe> $$0, ItemLike $$1, TagKey<Item> $$2, int $$3) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, $$1, $$3).requires($$2).group("planks").unlockedBy("has_log", RecipeProvider.has($$2)).save($$0);
    }

    protected static void planksFromLogs(Consumer<FinishedRecipe> $$0, ItemLike $$1, TagKey<Item> $$2, int $$3) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, $$1, $$3).requires($$2).group("planks").unlockedBy("has_logs", RecipeProvider.has($$2)).save($$0);
    }

    protected static void woodFromLogs(Consumer<FinishedRecipe> $$0, ItemLike $$1, ItemLike $$2) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, $$1, 3).define(Character.valueOf((char)'#'), $$2).pattern("##").pattern("##").group("bark").unlockedBy("has_log", RecipeProvider.has($$2)).save($$0);
    }

    protected static void woodenBoat(Consumer<FinishedRecipe> $$0, ItemLike $$1, ItemLike $$2) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TRANSPORTATION, $$1).define(Character.valueOf((char)'#'), $$2).pattern("# #").pattern("###").group("boat").unlockedBy("in_water", RecipeProvider.insideOf(Blocks.WATER)).save($$0);
    }

    protected static void chestBoat(Consumer<FinishedRecipe> $$0, ItemLike $$1, ItemLike $$2) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TRANSPORTATION, $$1).requires(Blocks.CHEST).requires($$2).group("chest_boat").unlockedBy("has_boat", RecipeProvider.has(ItemTags.BOATS)).save($$0);
    }

    private static RecipeBuilder buttonBuilder(ItemLike $$0, Ingredient $$1) {
        return ShapelessRecipeBuilder.shapeless(RecipeCategory.REDSTONE, $$0).requires($$1);
    }

    protected static RecipeBuilder doorBuilder(ItemLike $$0, Ingredient $$1) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, $$0, 3).define(Character.valueOf((char)'#'), $$1).pattern("##").pattern("##").pattern("##");
    }

    private static RecipeBuilder fenceBuilder(ItemLike $$0, Ingredient $$1) {
        int $$2 = $$0 == Blocks.NETHER_BRICK_FENCE ? 6 : 3;
        Item $$3 = $$0 == Blocks.NETHER_BRICK_FENCE ? Items.NETHER_BRICK : Items.STICK;
        return ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, $$0, $$2).define(Character.valueOf((char)'W'), $$1).define(Character.valueOf((char)'#'), $$3).pattern("W#W").pattern("W#W");
    }

    private static RecipeBuilder fenceGateBuilder(ItemLike $$0, Ingredient $$1) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, $$0).define(Character.valueOf((char)'#'), Items.STICK).define(Character.valueOf((char)'W'), $$1).pattern("#W#").pattern("#W#");
    }

    protected static void pressurePlate(Consumer<FinishedRecipe> $$0, ItemLike $$1, ItemLike $$2) {
        RecipeProvider.pressurePlateBuilder(RecipeCategory.REDSTONE, $$1, Ingredient.of($$2)).unlockedBy(RecipeProvider.getHasName($$2), RecipeProvider.has($$2)).save($$0);
    }

    private static RecipeBuilder pressurePlateBuilder(RecipeCategory $$0, ItemLike $$1, Ingredient $$2) {
        return ShapedRecipeBuilder.shaped($$0, $$1).define(Character.valueOf((char)'#'), $$2).pattern("##");
    }

    protected static void slab(Consumer<FinishedRecipe> $$0, RecipeCategory $$1, ItemLike $$2, ItemLike $$3) {
        RecipeProvider.slabBuilder($$1, $$2, Ingredient.of($$3)).unlockedBy(RecipeProvider.getHasName($$3), RecipeProvider.has($$3)).save($$0);
    }

    protected static RecipeBuilder slabBuilder(RecipeCategory $$0, ItemLike $$1, Ingredient $$2) {
        return ShapedRecipeBuilder.shaped($$0, $$1, 6).define(Character.valueOf((char)'#'), $$2).pattern("###");
    }

    protected static RecipeBuilder stairBuilder(ItemLike $$0, Ingredient $$1) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, $$0, 4).define(Character.valueOf((char)'#'), $$1).pattern("#  ").pattern("## ").pattern("###");
    }

    private static RecipeBuilder trapdoorBuilder(ItemLike $$0, Ingredient $$1) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.REDSTONE, $$0, 2).define(Character.valueOf((char)'#'), $$1).pattern("###").pattern("###");
    }

    private static RecipeBuilder signBuilder(ItemLike $$0, Ingredient $$1) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, $$0, 3).group("sign").define(Character.valueOf((char)'#'), $$1).define(Character.valueOf((char)'X'), Items.STICK).pattern("###").pattern("###").pattern(" X ");
    }

    protected static void hangingSign(Consumer<FinishedRecipe> $$0, ItemLike $$1, ItemLike $$2) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, $$1, 6).group("hanging_sign").define(Character.valueOf((char)'#'), $$2).define(Character.valueOf((char)'X'), Items.CHAIN).pattern("X X").pattern("###").pattern("###").unlockedBy("has_stripped_logs", RecipeProvider.has($$2)).save($$0);
    }

    protected static void coloredWoolFromWhiteWoolAndDye(Consumer<FinishedRecipe> $$0, ItemLike $$1, ItemLike $$2) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, $$1).requires($$2).requires(Blocks.WHITE_WOOL).group("wool").unlockedBy("has_white_wool", RecipeProvider.has(Blocks.WHITE_WOOL)).save($$0);
    }

    protected static void carpet(Consumer<FinishedRecipe> $$0, ItemLike $$1, ItemLike $$2) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, $$1, 3).define(Character.valueOf((char)'#'), $$2).pattern("##").group("carpet").unlockedBy(RecipeProvider.getHasName($$2), RecipeProvider.has($$2)).save($$0);
    }

    protected static void coloredCarpetFromWhiteCarpetAndDye(Consumer<FinishedRecipe> $$0, ItemLike $$1, ItemLike $$2) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, $$1, 8).define(Character.valueOf((char)'#'), Blocks.WHITE_CARPET).define(Character.valueOf((char)'$'), $$2).pattern("###").pattern("#$#").pattern("###").group("carpet").unlockedBy("has_white_carpet", RecipeProvider.has(Blocks.WHITE_CARPET)).unlockedBy(RecipeProvider.getHasName($$2), RecipeProvider.has($$2)).save($$0, RecipeProvider.getConversionRecipeName($$1, Blocks.WHITE_CARPET));
    }

    protected static void bedFromPlanksAndWool(Consumer<FinishedRecipe> $$0, ItemLike $$1, ItemLike $$2) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, $$1).define(Character.valueOf((char)'#'), $$2).define(Character.valueOf((char)'X'), ItemTags.PLANKS).pattern("###").pattern("XXX").group("bed").unlockedBy(RecipeProvider.getHasName($$2), RecipeProvider.has($$2)).save($$0);
    }

    protected static void bedFromWhiteBedAndDye(Consumer<FinishedRecipe> $$0, ItemLike $$1, ItemLike $$2) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, $$1).requires(Items.WHITE_BED).requires($$2).group("dyed_bed").unlockedBy("has_bed", RecipeProvider.has(Items.WHITE_BED)).save($$0, RecipeProvider.getConversionRecipeName($$1, Items.WHITE_BED));
    }

    protected static void banner(Consumer<FinishedRecipe> $$0, ItemLike $$1, ItemLike $$2) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, $$1).define(Character.valueOf((char)'#'), $$2).define(Character.valueOf((char)'|'), Items.STICK).pattern("###").pattern("###").pattern(" | ").group("banner").unlockedBy(RecipeProvider.getHasName($$2), RecipeProvider.has($$2)).save($$0);
    }

    protected static void stainedGlassFromGlassAndDye(Consumer<FinishedRecipe> $$0, ItemLike $$1, ItemLike $$2) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, $$1, 8).define(Character.valueOf((char)'#'), Blocks.GLASS).define(Character.valueOf((char)'X'), $$2).pattern("###").pattern("#X#").pattern("###").group("stained_glass").unlockedBy("has_glass", RecipeProvider.has(Blocks.GLASS)).save($$0);
    }

    protected static void stainedGlassPaneFromStainedGlass(Consumer<FinishedRecipe> $$0, ItemLike $$1, ItemLike $$2) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, $$1, 16).define(Character.valueOf((char)'#'), $$2).pattern("###").pattern("###").group("stained_glass_pane").unlockedBy("has_glass", RecipeProvider.has($$2)).save($$0);
    }

    protected static void stainedGlassPaneFromGlassPaneAndDye(Consumer<FinishedRecipe> $$0, ItemLike $$1, ItemLike $$2) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, $$1, 8).define(Character.valueOf((char)'#'), Blocks.GLASS_PANE).define(Character.valueOf((char)'$'), $$2).pattern("###").pattern("#$#").pattern("###").group("stained_glass_pane").unlockedBy("has_glass_pane", RecipeProvider.has(Blocks.GLASS_PANE)).unlockedBy(RecipeProvider.getHasName($$2), RecipeProvider.has($$2)).save($$0, RecipeProvider.getConversionRecipeName($$1, Blocks.GLASS_PANE));
    }

    protected static void coloredTerracottaFromTerracottaAndDye(Consumer<FinishedRecipe> $$0, ItemLike $$1, ItemLike $$2) {
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, $$1, 8).define(Character.valueOf((char)'#'), Blocks.TERRACOTTA).define(Character.valueOf((char)'X'), $$2).pattern("###").pattern("#X#").pattern("###").group("stained_terracotta").unlockedBy("has_terracotta", RecipeProvider.has(Blocks.TERRACOTTA)).save($$0);
    }

    protected static void concretePowder(Consumer<FinishedRecipe> $$0, ItemLike $$1, ItemLike $$2) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, $$1, 8).requires($$2).requires(Blocks.SAND, 4).requires(Blocks.GRAVEL, 4).group("concrete_powder").unlockedBy("has_sand", RecipeProvider.has(Blocks.SAND)).unlockedBy("has_gravel", RecipeProvider.has(Blocks.GRAVEL)).save($$0);
    }

    protected static void candle(Consumer<FinishedRecipe> $$0, ItemLike $$1, ItemLike $$2) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, $$1).requires(Blocks.CANDLE).requires($$2).group("dyed_candle").unlockedBy(RecipeProvider.getHasName($$2), RecipeProvider.has($$2)).save($$0);
    }

    protected static void wall(Consumer<FinishedRecipe> $$0, RecipeCategory $$1, ItemLike $$2, ItemLike $$3) {
        RecipeProvider.wallBuilder($$1, $$2, Ingredient.of($$3)).unlockedBy(RecipeProvider.getHasName($$3), RecipeProvider.has($$3)).save($$0);
    }

    private static RecipeBuilder wallBuilder(RecipeCategory $$0, ItemLike $$1, Ingredient $$2) {
        return ShapedRecipeBuilder.shaped($$0, $$1, 6).define(Character.valueOf((char)'#'), $$2).pattern("###").pattern("###");
    }

    protected static void polished(Consumer<FinishedRecipe> $$0, RecipeCategory $$1, ItemLike $$2, ItemLike $$3) {
        RecipeProvider.polishedBuilder($$1, $$2, Ingredient.of($$3)).unlockedBy(RecipeProvider.getHasName($$3), RecipeProvider.has($$3)).save($$0);
    }

    private static RecipeBuilder polishedBuilder(RecipeCategory $$0, ItemLike $$1, Ingredient $$2) {
        return ShapedRecipeBuilder.shaped($$0, $$1, 4).define(Character.valueOf((char)'S'), $$2).pattern("SS").pattern("SS");
    }

    protected static void cut(Consumer<FinishedRecipe> $$0, RecipeCategory $$1, ItemLike $$2, ItemLike $$3) {
        RecipeProvider.cutBuilder($$1, $$2, Ingredient.of($$3)).unlockedBy(RecipeProvider.getHasName($$3), RecipeProvider.has($$3)).save($$0);
    }

    private static ShapedRecipeBuilder cutBuilder(RecipeCategory $$0, ItemLike $$1, Ingredient $$2) {
        return ShapedRecipeBuilder.shaped($$0, $$1, 4).define(Character.valueOf((char)'#'), $$2).pattern("##").pattern("##");
    }

    protected static void chiseled(Consumer<FinishedRecipe> $$0, RecipeCategory $$1, ItemLike $$2, ItemLike $$3) {
        RecipeProvider.chiseledBuilder($$1, $$2, Ingredient.of($$3)).unlockedBy(RecipeProvider.getHasName($$3), RecipeProvider.has($$3)).save($$0);
    }

    protected static void mosaicBuilder(Consumer<FinishedRecipe> $$0, RecipeCategory $$1, ItemLike $$2, ItemLike $$3) {
        ShapedRecipeBuilder.shaped($$1, $$2).define(Character.valueOf((char)'#'), $$3).pattern("#").pattern("#").unlockedBy(RecipeProvider.getHasName($$3), RecipeProvider.has($$3)).save($$0);
    }

    protected static ShapedRecipeBuilder chiseledBuilder(RecipeCategory $$0, ItemLike $$1, Ingredient $$2) {
        return ShapedRecipeBuilder.shaped($$0, $$1).define(Character.valueOf((char)'#'), $$2).pattern("#").pattern("#");
    }

    protected static void stonecutterResultFromBase(Consumer<FinishedRecipe> $$0, RecipeCategory $$1, ItemLike $$2, ItemLike $$3) {
        RecipeProvider.stonecutterResultFromBase($$0, $$1, $$2, $$3, 1);
    }

    protected static void stonecutterResultFromBase(Consumer<FinishedRecipe> $$0, RecipeCategory $$1, ItemLike $$2, ItemLike $$3, int $$4) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of($$3), $$1, $$2, $$4).unlockedBy(RecipeProvider.getHasName($$3), RecipeProvider.has($$3)).save($$0, RecipeProvider.getConversionRecipeName($$2, $$3) + "_stonecutting");
    }

    private static void smeltingResultFromBase(Consumer<FinishedRecipe> $$0, ItemLike $$1, ItemLike $$2) {
        SimpleCookingRecipeBuilder.smelting(Ingredient.of($$2), RecipeCategory.BUILDING_BLOCKS, $$1, 0.1f, 200).unlockedBy(RecipeProvider.getHasName($$2), RecipeProvider.has($$2)).save($$0);
    }

    protected static void nineBlockStorageRecipes(Consumer<FinishedRecipe> $$0, RecipeCategory $$1, ItemLike $$2, RecipeCategory $$3, ItemLike $$4) {
        RecipeProvider.nineBlockStorageRecipes($$0, $$1, $$2, $$3, $$4, RecipeProvider.getSimpleRecipeName($$4), null, RecipeProvider.getSimpleRecipeName($$2), null);
    }

    protected static void nineBlockStorageRecipesWithCustomPacking(Consumer<FinishedRecipe> $$0, RecipeCategory $$1, ItemLike $$2, RecipeCategory $$3, ItemLike $$4, String $$5, String $$6) {
        RecipeProvider.nineBlockStorageRecipes($$0, $$1, $$2, $$3, $$4, $$5, $$6, RecipeProvider.getSimpleRecipeName($$2), null);
    }

    protected static void nineBlockStorageRecipesRecipesWithCustomUnpacking(Consumer<FinishedRecipe> $$0, RecipeCategory $$1, ItemLike $$2, RecipeCategory $$3, ItemLike $$4, String $$5, String $$6) {
        RecipeProvider.nineBlockStorageRecipes($$0, $$1, $$2, $$3, $$4, RecipeProvider.getSimpleRecipeName($$4), null, $$5, $$6);
    }

    private static void nineBlockStorageRecipes(Consumer<FinishedRecipe> $$0, RecipeCategory $$1, ItemLike $$2, RecipeCategory $$3, ItemLike $$4, String $$5, @Nullable String $$6, String $$7, @Nullable String $$8) {
        ShapelessRecipeBuilder.shapeless($$1, $$2, 9).requires($$4).group($$8).unlockedBy(RecipeProvider.getHasName($$4), RecipeProvider.has($$4)).save($$0, new ResourceLocation($$7));
        ShapedRecipeBuilder.shaped($$3, $$4).define(Character.valueOf((char)'#'), $$2).pattern("###").pattern("###").pattern("###").group($$6).unlockedBy(RecipeProvider.getHasName($$2), RecipeProvider.has($$2)).save($$0, new ResourceLocation($$5));
    }

    protected static void copySmithingTemplate(Consumer<FinishedRecipe> $$0, ItemLike $$1, TagKey<Item> $$2) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, $$1, 2).define(Character.valueOf((char)'#'), Items.DIAMOND).define(Character.valueOf((char)'C'), $$2).define(Character.valueOf((char)'S'), $$1).pattern("#S#").pattern("#C#").pattern("###").unlockedBy(RecipeProvider.getHasName($$1), RecipeProvider.has($$1)).save($$0);
    }

    protected static void copySmithingTemplate(Consumer<FinishedRecipe> $$0, ItemLike $$1, ItemLike $$2) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, $$1, 2).define(Character.valueOf((char)'#'), Items.DIAMOND).define(Character.valueOf((char)'C'), $$2).define(Character.valueOf((char)'S'), $$1).pattern("#S#").pattern("#C#").pattern("###").unlockedBy(RecipeProvider.getHasName($$1), RecipeProvider.has($$1)).save($$0);
    }

    protected static void cookRecipes(Consumer<FinishedRecipe> $$0, String $$1, RecipeSerializer<? extends AbstractCookingRecipe> $$2, int $$3) {
        RecipeProvider.simpleCookingRecipe($$0, $$1, $$2, $$3, Items.BEEF, Items.COOKED_BEEF, 0.35f);
        RecipeProvider.simpleCookingRecipe($$0, $$1, $$2, $$3, Items.CHICKEN, Items.COOKED_CHICKEN, 0.35f);
        RecipeProvider.simpleCookingRecipe($$0, $$1, $$2, $$3, Items.COD, Items.COOKED_COD, 0.35f);
        RecipeProvider.simpleCookingRecipe($$0, $$1, $$2, $$3, Items.KELP, Items.DRIED_KELP, 0.1f);
        RecipeProvider.simpleCookingRecipe($$0, $$1, $$2, $$3, Items.SALMON, Items.COOKED_SALMON, 0.35f);
        RecipeProvider.simpleCookingRecipe($$0, $$1, $$2, $$3, Items.MUTTON, Items.COOKED_MUTTON, 0.35f);
        RecipeProvider.simpleCookingRecipe($$0, $$1, $$2, $$3, Items.PORKCHOP, Items.COOKED_PORKCHOP, 0.35f);
        RecipeProvider.simpleCookingRecipe($$0, $$1, $$2, $$3, Items.POTATO, Items.BAKED_POTATO, 0.35f);
        RecipeProvider.simpleCookingRecipe($$0, $$1, $$2, $$3, Items.RABBIT, Items.COOKED_RABBIT, 0.35f);
    }

    private static void simpleCookingRecipe(Consumer<FinishedRecipe> $$0, String $$1, RecipeSerializer<? extends AbstractCookingRecipe> $$2, int $$3, ItemLike $$4, ItemLike $$5, float $$6) {
        SimpleCookingRecipeBuilder.generic(Ingredient.of($$4), RecipeCategory.FOOD, $$5, $$6, $$3, $$2).unlockedBy(RecipeProvider.getHasName($$4), RecipeProvider.has($$4)).save($$0, RecipeProvider.getItemName($$5) + "_from_" + $$1);
    }

    protected static void waxRecipes(Consumer<FinishedRecipe> $$0) {
        ((BiMap)HoneycombItem.WAXABLES.get()).forEach(($$1, $$2) -> ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, $$2).requires((ItemLike)$$1).requires(Items.HONEYCOMB).group(RecipeProvider.getItemName($$2)).unlockedBy(RecipeProvider.getHasName($$1), RecipeProvider.has($$1)).save($$0, RecipeProvider.getConversionRecipeName($$2, Items.HONEYCOMB)));
    }

    protected static void generateRecipes(Consumer<FinishedRecipe> $$0, BlockFamily $$1) {
        $$1.getVariants().forEach(($$22, $$3) -> {
            BiFunction $$4 = (BiFunction)SHAPE_BUILDERS.get((Object)$$22);
            Block $$5 = RecipeProvider.getBaseBlock($$1, $$22);
            if ($$4 != null) {
                RecipeBuilder $$6 = (RecipeBuilder)$$4.apply($$3, (Object)$$5);
                $$1.getRecipeGroupPrefix().ifPresent($$2 -> $$6.group($$2 + ($$22 == BlockFamily.Variant.CUT ? "" : "_" + $$22.getName())));
                $$6.unlockedBy((String)$$1.getRecipeUnlockedBy().orElseGet(() -> RecipeProvider.getHasName($$5)), RecipeProvider.has($$5));
                $$6.save($$0);
            }
            if ($$22 == BlockFamily.Variant.CRACKED) {
                RecipeProvider.smeltingResultFromBase($$0, $$3, $$5);
            }
        });
    }

    private static Block getBaseBlock(BlockFamily $$0, BlockFamily.Variant $$1) {
        if ($$1 == BlockFamily.Variant.CHISELED) {
            if (!$$0.getVariants().containsKey((Object)BlockFamily.Variant.SLAB)) {
                throw new IllegalStateException("Slab is not defined for the family.");
            }
            return $$0.get(BlockFamily.Variant.SLAB);
        }
        return $$0.getBaseBlock();
    }

    private static EnterBlockTrigger.TriggerInstance insideOf(Block $$0) {
        return new EnterBlockTrigger.TriggerInstance(EntityPredicate.Composite.ANY, $$0, StatePropertiesPredicate.ANY);
    }

    private static InventoryChangeTrigger.TriggerInstance has(MinMaxBounds.Ints $$0, ItemLike $$1) {
        return RecipeProvider.inventoryTrigger(ItemPredicate.Builder.item().of($$1).withCount($$0).build());
    }

    protected static InventoryChangeTrigger.TriggerInstance has(ItemLike $$0) {
        return RecipeProvider.inventoryTrigger(ItemPredicate.Builder.item().of($$0).build());
    }

    protected static InventoryChangeTrigger.TriggerInstance has(TagKey<Item> $$0) {
        return RecipeProvider.inventoryTrigger(ItemPredicate.Builder.item().of($$0).build());
    }

    private static InventoryChangeTrigger.TriggerInstance inventoryTrigger(ItemPredicate ... $$0) {
        return new InventoryChangeTrigger.TriggerInstance(EntityPredicate.Composite.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, $$0);
    }

    protected static String getHasName(ItemLike $$0) {
        return "has_" + RecipeProvider.getItemName($$0);
    }

    protected static String getItemName(ItemLike $$0) {
        return BuiltInRegistries.ITEM.getKey($$0.asItem()).getPath();
    }

    protected static String getSimpleRecipeName(ItemLike $$0) {
        return RecipeProvider.getItemName($$0);
    }

    protected static String getConversionRecipeName(ItemLike $$0, ItemLike $$1) {
        return RecipeProvider.getItemName($$0) + "_from_" + RecipeProvider.getItemName($$1);
    }

    protected static String getSmeltingRecipeName(ItemLike $$0) {
        return RecipeProvider.getItemName($$0) + "_from_smelting";
    }

    protected static String getBlastingRecipeName(ItemLike $$0) {
        return RecipeProvider.getItemName($$0) + "_from_blasting";
    }

    @Override
    public final String getName() {
        return "Recipes";
    }

    private /* synthetic */ void lambda$run$0(Set $$0, List $$1, CachedOutput $$2, FinishedRecipe $$3) {
        if (!$$0.add((Object)$$3.getId())) {
            throw new IllegalStateException("Duplicate recipe " + $$3.getId());
        }
        $$1.add(DataProvider.saveStable($$2, (JsonElement)$$3.serializeRecipe(), this.recipePathProvider.json($$3.getId())));
        JsonObject $$4 = $$3.serializeAdvancement();
        if ($$4 != null) {
            $$1.add(DataProvider.saveStable($$2, (JsonElement)$$4, this.advancementPathProvider.json($$3.getAdvancementId())));
        }
    }
}