/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  com.mojang.datafixers.util.Either
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Optional
 *  java.util.concurrent.CompletableFuture
 *  java.util.function.Function
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 */
package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ItemParser {
    private static final SimpleCommandExceptionType ERROR_NO_TAGS_ALLOWED = new SimpleCommandExceptionType((Message)Component.translatable("argument.item.tag.disallowed"));
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_ITEM = new DynamicCommandExceptionType($$0 -> Component.translatable("argument.item.id.invalid", $$0));
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType($$0 -> Component.translatable("arguments.item.tag.unknown", $$0));
    private static final char SYNTAX_START_NBT = '{';
    private static final char SYNTAX_TAG = '#';
    private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_NOTHING = SuggestionsBuilder::buildFuture;
    private final HolderLookup<Item> items;
    private final StringReader reader;
    private final boolean allowTags;
    private Either<Holder<Item>, HolderSet<Item>> result;
    @Nullable
    private CompoundTag nbt;
    private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions = SUGGEST_NOTHING;

    private ItemParser(HolderLookup<Item> $$0, StringReader $$1, boolean $$2) {
        this.items = $$0;
        this.reader = $$1;
        this.allowTags = $$2;
    }

    public static ItemResult parseForItem(HolderLookup<Item> $$0, StringReader $$1) throws CommandSyntaxException {
        int $$2 = $$1.getCursor();
        try {
            ItemParser $$3 = new ItemParser($$0, $$1, false);
            $$3.parse();
            Holder $$4 = (Holder)$$3.result.left().orElseThrow(() -> new IllegalStateException("Parser returned unexpected tag name"));
            return new ItemResult($$4, $$3.nbt);
        }
        catch (CommandSyntaxException $$5) {
            $$1.setCursor($$2);
            throw $$5;
        }
    }

    public static Either<ItemResult, TagResult> parseForTesting(HolderLookup<Item> $$0, StringReader $$12) throws CommandSyntaxException {
        int $$2 = $$12.getCursor();
        try {
            ItemParser $$3 = new ItemParser($$0, $$12, true);
            $$3.parse();
            return $$3.result.mapBoth($$1 -> new ItemResult((Holder<Item>)$$1, $$0.nbt), $$1 -> new TagResult((HolderSet<Item>)$$1, $$0.nbt));
        }
        catch (CommandSyntaxException $$4) {
            $$12.setCursor($$2);
            throw $$4;
        }
    }

    public static CompletableFuture<Suggestions> fillSuggestions(HolderLookup<Item> $$0, SuggestionsBuilder $$1, boolean $$2) {
        StringReader $$3 = new StringReader($$1.getInput());
        $$3.setCursor($$1.getStart());
        ItemParser $$4 = new ItemParser($$0, $$3, $$2);
        try {
            $$4.parse();
        }
        catch (CommandSyntaxException commandSyntaxException) {
            // empty catch block
        }
        return (CompletableFuture)$$4.suggestions.apply((Object)$$1.createOffset($$3.getCursor()));
    }

    private void readItem() throws CommandSyntaxException {
        int $$0 = this.reader.getCursor();
        ResourceLocation $$1 = ResourceLocation.read(this.reader);
        Optional $$2 = this.items.get(ResourceKey.create(Registries.ITEM, $$1));
        this.result = Either.left((Object)((Holder)$$2.orElseThrow(() -> {
            this.reader.setCursor($$0);
            return ERROR_UNKNOWN_ITEM.createWithContext((ImmutableStringReader)this.reader, (Object)$$1);
        })));
    }

    private void readTag() throws CommandSyntaxException {
        if (!this.allowTags) {
            throw ERROR_NO_TAGS_ALLOWED.createWithContext((ImmutableStringReader)this.reader);
        }
        int $$0 = this.reader.getCursor();
        this.reader.expect('#');
        this.suggestions = this::suggestTag;
        ResourceLocation $$1 = ResourceLocation.read(this.reader);
        Optional $$2 = this.items.get(TagKey.create(Registries.ITEM, $$1));
        this.result = Either.right((Object)((HolderSet)$$2.orElseThrow(() -> {
            this.reader.setCursor($$0);
            return ERROR_UNKNOWN_TAG.createWithContext((ImmutableStringReader)this.reader, (Object)$$1);
        })));
    }

    private void readNbt() throws CommandSyntaxException {
        this.nbt = new TagParser(this.reader).readStruct();
    }

    private void parse() throws CommandSyntaxException {
        this.suggestions = this.allowTags ? this::suggestItemIdOrTag : this::suggestItem;
        if (this.reader.canRead() && this.reader.peek() == '#') {
            this.readTag();
        } else {
            this.readItem();
        }
        this.suggestions = this::suggestOpenNbt;
        if (this.reader.canRead() && this.reader.peek() == '{') {
            this.suggestions = SUGGEST_NOTHING;
            this.readNbt();
        }
    }

    private CompletableFuture<Suggestions> suggestOpenNbt(SuggestionsBuilder $$0) {
        if ($$0.getRemaining().isEmpty()) {
            $$0.suggest(String.valueOf((char)'{'));
        }
        return $$0.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestTag(SuggestionsBuilder $$0) {
        return SharedSuggestionProvider.suggestResource((Stream<ResourceLocation>)this.items.listTagIds().map(TagKey::location), $$0, String.valueOf((char)'#'));
    }

    private CompletableFuture<Suggestions> suggestItem(SuggestionsBuilder $$0) {
        return SharedSuggestionProvider.suggestResource((Stream<ResourceLocation>)this.items.listElementIds().map(ResourceKey::location), $$0);
    }

    private CompletableFuture<Suggestions> suggestItemIdOrTag(SuggestionsBuilder $$0) {
        this.suggestTag($$0);
        return this.suggestItem($$0);
    }

    public record ItemResult(Holder<Item> item, @Nullable CompoundTag nbt) {
    }

    public record TagResult(HolderSet<Item> tag, @Nullable CompoundTag nbt) {
    }
}