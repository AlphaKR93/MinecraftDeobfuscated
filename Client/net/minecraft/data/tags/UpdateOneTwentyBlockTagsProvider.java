/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.concurrent.CompletableFuture
 */
package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class UpdateOneTwentyBlockTagsProvider
extends IntrinsicHolderTagsProvider<Block> {
    public UpdateOneTwentyBlockTagsProvider(PackOutput $$02, CompletableFuture<HolderLookup.Provider> $$1) {
        super($$02, Registries.BLOCK, $$1, $$0 -> $$0.builtInRegistryHolder().key());
    }

    @Override
    protected void addTags(HolderLookup.Provider $$0) {
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)BlockTags.PLANKS)).add(Blocks.BAMBOO_PLANKS);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)BlockTags.WOODEN_BUTTONS)).add(Blocks.BAMBOO_BUTTON);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)BlockTags.WOODEN_DOORS)).add(Blocks.BAMBOO_DOOR);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)BlockTags.WOODEN_STAIRS)).add(Blocks.BAMBOO_STAIRS);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)BlockTags.WOODEN_SLABS)).add(Blocks.BAMBOO_SLAB);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)BlockTags.WOODEN_FENCES)).add(Blocks.BAMBOO_FENCE);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)BlockTags.WOODEN_PRESSURE_PLATES)).add(Blocks.BAMBOO_PRESSURE_PLATE);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)BlockTags.WOODEN_TRAPDOORS)).add(Blocks.BAMBOO_TRAPDOOR);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)BlockTags.STANDING_SIGNS)).add(Blocks.BAMBOO_SIGN);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)BlockTags.WALL_SIGNS)).add(Blocks.BAMBOO_WALL_SIGN);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)BlockTags.FENCE_GATES)).add(Blocks.BAMBOO_FENCE_GATE);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)BlockTags.BAMBOO_BLOCKS)).add(Blocks.BAMBOO_BLOCK, Blocks.STRIPPED_BAMBOO_BLOCK);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)BlockTags.CEILING_HANGING_SIGNS)).add(Blocks.OAK_HANGING_SIGN, Blocks.SPRUCE_HANGING_SIGN, Blocks.BIRCH_HANGING_SIGN, Blocks.ACACIA_HANGING_SIGN, Blocks.JUNGLE_HANGING_SIGN, Blocks.DARK_OAK_HANGING_SIGN, Blocks.CRIMSON_HANGING_SIGN, Blocks.WARPED_HANGING_SIGN, Blocks.MANGROVE_HANGING_SIGN, Blocks.BAMBOO_HANGING_SIGN);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)BlockTags.WALL_HANGING_SIGNS)).add(Blocks.OAK_WALL_HANGING_SIGN, Blocks.SPRUCE_WALL_HANGING_SIGN, Blocks.BIRCH_WALL_HANGING_SIGN, Blocks.ACACIA_WALL_HANGING_SIGN, Blocks.JUNGLE_WALL_HANGING_SIGN, Blocks.DARK_OAK_WALL_HANGING_SIGN, Blocks.CRIMSON_WALL_HANGING_SIGN, Blocks.WARPED_WALL_HANGING_SIGN, Blocks.MANGROVE_WALL_HANGING_SIGN, Blocks.BAMBOO_WALL_HANGING_SIGN);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)BlockTags.ALL_HANGING_SIGNS)).addTag((TagKey)BlockTags.CEILING_HANGING_SIGNS)).addTag((TagKey)BlockTags.WALL_HANGING_SIGNS);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)BlockTags.ALL_SIGNS)).addTag((TagKey)BlockTags.ALL_HANGING_SIGNS);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)((IntrinsicHolderTagsProvider.IntrinsicTagAppender)((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)BlockTags.MINEABLE_WITH_AXE)).addTag((TagKey)BlockTags.ALL_HANGING_SIGNS)).add(Blocks.BAMBOO_MOSAIC, Blocks.BAMBOO_MOSAIC_SLAB, Blocks.BAMBOO_MOSAIC_STAIRS).addTag((TagKey)BlockTags.BAMBOO_BLOCKS)).add(Blocks.CHISELED_BOOKSHELF);
    }
}