/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Character
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Consumer
 */
package net.minecraft.data.recipes.packs;

import java.util.function.Consumer;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class UpdateOneTwentyRecipeProvider
extends RecipeProvider {
    public UpdateOneTwentyRecipeProvider(PackOutput $$0) {
        super($$0);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> $$0) {
        UpdateOneTwentyRecipeProvider.generateForEnabledBlockFamilies($$0, FeatureFlagSet.of(FeatureFlags.UPDATE_1_20));
        UpdateOneTwentyRecipeProvider.threeByThreePacker($$0, RecipeCategory.BUILDING_BLOCKS, Blocks.BAMBOO_BLOCK, Items.BAMBOO);
        UpdateOneTwentyRecipeProvider.planksFromLogs($$0, Blocks.BAMBOO_PLANKS, ItemTags.BAMBOO_BLOCKS, 2);
        UpdateOneTwentyRecipeProvider.mosaicBuilder($$0, RecipeCategory.DECORATIONS, Blocks.BAMBOO_MOSAIC, Blocks.BAMBOO_SLAB);
        UpdateOneTwentyRecipeProvider.woodenBoat($$0, Items.BAMBOO_RAFT, Blocks.BAMBOO_PLANKS);
        UpdateOneTwentyRecipeProvider.chestBoat($$0, Items.BAMBOO_CHEST_RAFT, Items.BAMBOO_RAFT);
        UpdateOneTwentyRecipeProvider.hangingSign($$0, Items.OAK_HANGING_SIGN, Blocks.STRIPPED_OAK_LOG);
        UpdateOneTwentyRecipeProvider.hangingSign($$0, Items.SPRUCE_HANGING_SIGN, Blocks.STRIPPED_SPRUCE_LOG);
        UpdateOneTwentyRecipeProvider.hangingSign($$0, Items.BIRCH_HANGING_SIGN, Blocks.STRIPPED_BIRCH_LOG);
        UpdateOneTwentyRecipeProvider.hangingSign($$0, Items.JUNGLE_HANGING_SIGN, Blocks.STRIPPED_JUNGLE_LOG);
        UpdateOneTwentyRecipeProvider.hangingSign($$0, Items.ACACIA_HANGING_SIGN, Blocks.STRIPPED_ACACIA_LOG);
        UpdateOneTwentyRecipeProvider.hangingSign($$0, Items.DARK_OAK_HANGING_SIGN, Blocks.STRIPPED_DARK_OAK_LOG);
        UpdateOneTwentyRecipeProvider.hangingSign($$0, Items.MANGROVE_HANGING_SIGN, Blocks.STRIPPED_MANGROVE_LOG);
        UpdateOneTwentyRecipeProvider.hangingSign($$0, Items.BAMBOO_HANGING_SIGN, Items.STRIPPED_BAMBOO_BLOCK);
        UpdateOneTwentyRecipeProvider.hangingSign($$0, Items.CRIMSON_HANGING_SIGN, Blocks.STRIPPED_CRIMSON_STEM);
        UpdateOneTwentyRecipeProvider.hangingSign($$0, Items.WARPED_HANGING_SIGN, Blocks.STRIPPED_WARPED_STEM);
        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, Blocks.CHISELED_BOOKSHELF).define(Character.valueOf((char)'#'), ItemTags.PLANKS).define(Character.valueOf((char)'X'), ItemTags.WOODEN_SLABS).pattern("###").pattern("XXX").pattern("###").unlockedBy("has_book", UpdateOneTwentyRecipeProvider.has(Items.BOOK)).save($$0);
        UpdateOneTwentyRecipeProvider.trimSmithing($$0, Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE);
        UpdateOneTwentyRecipeProvider.trimSmithing($$0, Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE);
        UpdateOneTwentyRecipeProvider.trimSmithing($$0, Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE);
        UpdateOneTwentyRecipeProvider.trimSmithing($$0, Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE);
        UpdateOneTwentyRecipeProvider.trimSmithing($$0, Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE);
        UpdateOneTwentyRecipeProvider.trimSmithing($$0, Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE);
        UpdateOneTwentyRecipeProvider.trimSmithing($$0, Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE);
        UpdateOneTwentyRecipeProvider.trimSmithing($$0, Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE);
        UpdateOneTwentyRecipeProvider.trimSmithing($$0, Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE);
        UpdateOneTwentyRecipeProvider.trimSmithing($$0, Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE);
        UpdateOneTwentyRecipeProvider.trimSmithing($$0, Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE);
        UpdateOneTwentyRecipeProvider.netheriteSmithing($$0, Items.DIAMOND_CHESTPLATE, RecipeCategory.COMBAT, Items.NETHERITE_CHESTPLATE);
        UpdateOneTwentyRecipeProvider.netheriteSmithing($$0, Items.DIAMOND_LEGGINGS, RecipeCategory.COMBAT, Items.NETHERITE_LEGGINGS);
        UpdateOneTwentyRecipeProvider.netheriteSmithing($$0, Items.DIAMOND_HELMET, RecipeCategory.COMBAT, Items.NETHERITE_HELMET);
        UpdateOneTwentyRecipeProvider.netheriteSmithing($$0, Items.DIAMOND_BOOTS, RecipeCategory.COMBAT, Items.NETHERITE_BOOTS);
        UpdateOneTwentyRecipeProvider.netheriteSmithing($$0, Items.DIAMOND_SWORD, RecipeCategory.COMBAT, Items.NETHERITE_SWORD);
        UpdateOneTwentyRecipeProvider.netheriteSmithing($$0, Items.DIAMOND_AXE, RecipeCategory.TOOLS, Items.NETHERITE_AXE);
        UpdateOneTwentyRecipeProvider.netheriteSmithing($$0, Items.DIAMOND_PICKAXE, RecipeCategory.TOOLS, Items.NETHERITE_PICKAXE);
        UpdateOneTwentyRecipeProvider.netheriteSmithing($$0, Items.DIAMOND_HOE, RecipeCategory.TOOLS, Items.NETHERITE_HOE);
        UpdateOneTwentyRecipeProvider.netheriteSmithing($$0, Items.DIAMOND_SHOVEL, RecipeCategory.TOOLS, Items.NETHERITE_SHOVEL);
        UpdateOneTwentyRecipeProvider.copySmithingTemplate($$0, (ItemLike)Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, Items.NETHERRACK);
        UpdateOneTwentyRecipeProvider.copySmithingTemplate($$0, (ItemLike)Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLESTONE);
        UpdateOneTwentyRecipeProvider.copySmithingTemplate($$0, (ItemLike)Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.SANDSTONE);
        UpdateOneTwentyRecipeProvider.copySmithingTemplate($$0, (ItemLike)Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLESTONE);
        UpdateOneTwentyRecipeProvider.copySmithingTemplate($$0, (ItemLike)Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE, Items.MOSSY_COBBLESTONE);
        UpdateOneTwentyRecipeProvider.copySmithingTemplate($$0, (ItemLike)Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLED_DEEPSLATE);
        UpdateOneTwentyRecipeProvider.copySmithingTemplate($$0, (ItemLike)Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.END_STONE);
        UpdateOneTwentyRecipeProvider.copySmithingTemplate($$0, (ItemLike)Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE, Items.COBBLESTONE);
        UpdateOneTwentyRecipeProvider.copySmithingTemplate($$0, (ItemLike)Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.PRISMARINE);
        UpdateOneTwentyRecipeProvider.copySmithingTemplate($$0, (ItemLike)Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE, Items.BLACKSTONE);
        UpdateOneTwentyRecipeProvider.copySmithingTemplate($$0, (ItemLike)Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE, Items.NETHERRACK);
        UpdateOneTwentyRecipeProvider.copySmithingTemplate($$0, (ItemLike)Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, Items.PURPUR_BLOCK);
    }
}