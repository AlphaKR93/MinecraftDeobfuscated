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
    }
}