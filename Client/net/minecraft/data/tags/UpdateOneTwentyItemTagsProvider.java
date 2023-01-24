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
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class UpdateOneTwentyItemTagsProvider
extends ItemTagsProvider {
    public UpdateOneTwentyItemTagsProvider(PackOutput $$0, CompletableFuture<HolderLookup.Provider> $$1, TagsProvider<Block> $$2) {
        super($$0, $$1, $$2);
    }

    @Override
    protected void addTags(HolderLookup.Provider $$0) {
        this.copy(BlockTags.PLANKS, ItemTags.PLANKS);
        this.copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
        this.copy(BlockTags.BUTTONS, ItemTags.BUTTONS);
        this.copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
        this.copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
        this.copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
        this.copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
        this.copy(BlockTags.FENCE_GATES, ItemTags.FENCE_GATES);
        this.copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
        this.copy(BlockTags.DOORS, ItemTags.DOORS);
        this.copy(BlockTags.SLABS, ItemTags.SLABS);
        this.copy(BlockTags.STAIRS, ItemTags.STAIRS);
        this.copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
        this.copy(BlockTags.TRAPDOORS, ItemTags.TRAPDOORS);
        this.copy(BlockTags.FENCES, ItemTags.FENCES);
        this.copy(BlockTags.STANDING_SIGNS, ItemTags.SIGNS);
        this.copy(BlockTags.BAMBOO_BLOCKS, ItemTags.BAMBOO_BLOCKS);
        this.copy(BlockTags.CEILING_HANGING_SIGNS, ItemTags.HANGING_SIGNS);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)ItemTags.CHEST_BOATS)).add(Items.BAMBOO_CHEST_RAFT);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)ItemTags.BOATS)).add(Items.BAMBOO_RAFT);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)ItemTags.BOOKSHELF_BOOKS)).add(Items.BOOK, Items.WRITTEN_BOOK, Items.ENCHANTED_BOOK, Items.WRITABLE_BOOK);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)ItemTags.NON_FLAMMABLE_WOOD)).add(Items.WARPED_HANGING_SIGN, Items.CRIMSON_HANGING_SIGN);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)ItemTags.NOTE_BLOCK_TOP_INSTRUMENTS)).add(Items.ZOMBIE_HEAD, Items.SKELETON_SKULL, Items.CREEPER_HEAD, Items.DRAGON_HEAD, Items.WITHER_SKELETON_SKULL, Items.PIGLIN_HEAD, Items.PLAYER_HEAD);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)ItemTags.TRIMMABLE_ARMOR)).add(Items.NETHERITE_HELMET).add(Items.NETHERITE_CHESTPLATE).add(Items.NETHERITE_LEGGINGS).add(Items.NETHERITE_BOOTS).add(Items.DIAMOND_HELMET).add(Items.DIAMOND_CHESTPLATE).add(Items.DIAMOND_LEGGINGS).add(Items.DIAMOND_BOOTS).add(Items.GOLDEN_HELMET).add(Items.GOLDEN_CHESTPLATE).add(Items.GOLDEN_LEGGINGS).add(Items.GOLDEN_BOOTS).add(Items.IRON_HELMET).add(Items.IRON_CHESTPLATE).add(Items.IRON_LEGGINGS).add(Items.IRON_BOOTS).add(Items.CHAINMAIL_HELMET).add(Items.CHAINMAIL_CHESTPLATE).add(Items.CHAINMAIL_LEGGINGS).add(Items.CHAINMAIL_BOOTS).add(Items.TURTLE_HELMET);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)ItemTags.TRIM_MATERIALS)).add(Items.IRON_INGOT).add(Items.COPPER_INGOT).add(Items.GOLD_INGOT).add(Items.LAPIS_LAZULI).add(Items.EMERALD).add(Items.DIAMOND).add(Items.NETHERITE_INGOT).add(Items.REDSTONE).add(Items.QUARTZ).add(Items.AMETHYST_SHARD);
        ((IntrinsicHolderTagsProvider.IntrinsicTagAppender)this.tag((TagKey)ItemTags.TRIM_TEMPLATES)).add(Items.WARD_ARMOR_TRIM_SMITHING_TEMPLATE).add(Items.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE).add(Items.COAST_ARMOR_TRIM_SMITHING_TEMPLATE).add(Items.EYE_ARMOR_TRIM_SMITHING_TEMPLATE).add(Items.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE).add(Items.WILD_ARMOR_TRIM_SMITHING_TEMPLATE).add(Items.RIB_ARMOR_TRIM_SMITHING_TEMPLATE).add(Items.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE).add(Items.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE).add(Items.VEX_ARMOR_TRIM_SMITHING_TEMPLATE).add(Items.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE);
    }
}