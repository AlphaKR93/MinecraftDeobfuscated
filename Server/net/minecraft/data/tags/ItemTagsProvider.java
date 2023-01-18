/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.concurrent.CompletableFuture
 *  java.util.function.Function
 */
package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public abstract class ItemTagsProvider
extends IntrinsicHolderTagsProvider<Item> {
    private final Function<TagKey<Block>, TagBuilder> blockTags = $$2::getOrCreateRawBuilder;

    public ItemTagsProvider(PackOutput $$02, CompletableFuture<HolderLookup.Provider> $$1, TagsProvider<Block> $$2) {
        super($$02, Registries.ITEM, $$1, $$0 -> $$0.builtInRegistryHolder().key());
    }

    protected void copy(TagKey<Block> $$0, TagKey<Item> $$1) {
        TagBuilder $$2 = this.getOrCreateRawBuilder($$1);
        TagBuilder $$3 = (TagBuilder)this.blockTags.apply($$0);
        $$3.build().forEach($$2::add);
    }
}