/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  com.mojang.datafixers.util.Either
 *  java.lang.Comparable
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.Iterator
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Optional
 *  java.util.concurrent.CompletableFuture
 *  java.util.function.Function
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 */
package net.minecraft.commands.arguments.blocks;

import com.google.common.collect.Maps;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockStateParser {
    public static final SimpleCommandExceptionType ERROR_NO_TAGS_ALLOWED = new SimpleCommandExceptionType((Message)Component.translatable("argument.block.tag.disallowed"));
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_BLOCK = new DynamicCommandExceptionType($$0 -> Component.translatable("argument.block.id.invalid", $$0));
    public static final Dynamic2CommandExceptionType ERROR_UNKNOWN_PROPERTY = new Dynamic2CommandExceptionType(($$0, $$1) -> Component.translatable("argument.block.property.unknown", $$0, $$1));
    public static final Dynamic2CommandExceptionType ERROR_DUPLICATE_PROPERTY = new Dynamic2CommandExceptionType(($$0, $$1) -> Component.translatable("argument.block.property.duplicate", $$1, $$0));
    public static final Dynamic3CommandExceptionType ERROR_INVALID_VALUE = new Dynamic3CommandExceptionType(($$0, $$1, $$2) -> Component.translatable("argument.block.property.invalid", $$0, $$2, $$1));
    public static final Dynamic2CommandExceptionType ERROR_EXPECTED_VALUE = new Dynamic2CommandExceptionType(($$0, $$1) -> Component.translatable("argument.block.property.novalue", $$0, $$1));
    public static final SimpleCommandExceptionType ERROR_EXPECTED_END_OF_PROPERTIES = new SimpleCommandExceptionType((Message)Component.translatable("argument.block.property.unclosed"));
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType($$0 -> Component.translatable("arguments.block.tag.unknown", $$0));
    private static final char SYNTAX_START_PROPERTIES = '[';
    private static final char SYNTAX_START_NBT = '{';
    private static final char SYNTAX_END_PROPERTIES = ']';
    private static final char SYNTAX_EQUALS = '=';
    private static final char SYNTAX_PROPERTY_SEPARATOR = ',';
    private static final char SYNTAX_TAG = '#';
    private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_NOTHING = SuggestionsBuilder::buildFuture;
    private final HolderLookup<Block> blocks;
    private final StringReader reader;
    private final boolean forTesting;
    private final boolean allowNbt;
    private final Map<Property<?>, Comparable<?>> properties = Maps.newHashMap();
    private final Map<String, String> vagueProperties = Maps.newHashMap();
    private ResourceLocation id = new ResourceLocation("");
    @Nullable
    private StateDefinition<Block, BlockState> definition;
    @Nullable
    private BlockState state;
    @Nullable
    private CompoundTag nbt;
    @Nullable
    private HolderSet<Block> tag;
    private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions = SUGGEST_NOTHING;

    private BlockStateParser(HolderLookup<Block> $$0, StringReader $$1, boolean $$2, boolean $$3) {
        this.blocks = $$0;
        this.reader = $$1;
        this.forTesting = $$2;
        this.allowNbt = $$3;
    }

    public static BlockResult parseForBlock(HolderLookup<Block> $$0, String $$1, boolean $$2) throws CommandSyntaxException {
        return BlockStateParser.parseForBlock($$0, new StringReader($$1), $$2);
    }

    public static BlockResult parseForBlock(HolderLookup<Block> $$0, StringReader $$1, boolean $$2) throws CommandSyntaxException {
        int $$3 = $$1.getCursor();
        try {
            BlockStateParser $$4 = new BlockStateParser($$0, $$1, false, $$2);
            $$4.parse();
            return new BlockResult($$4.state, $$4.properties, $$4.nbt);
        }
        catch (CommandSyntaxException $$5) {
            $$1.setCursor($$3);
            throw $$5;
        }
    }

    public static Either<BlockResult, TagResult> parseForTesting(HolderLookup<Block> $$0, String $$1, boolean $$2) throws CommandSyntaxException {
        return BlockStateParser.parseForTesting($$0, new StringReader($$1), $$2);
    }

    public static Either<BlockResult, TagResult> parseForTesting(HolderLookup<Block> $$0, StringReader $$1, boolean $$2) throws CommandSyntaxException {
        int $$3 = $$1.getCursor();
        try {
            BlockStateParser $$4 = new BlockStateParser($$0, $$1, true, $$2);
            $$4.parse();
            if ($$4.tag != null) {
                return Either.right((Object)((Object)new TagResult($$4.tag, $$4.vagueProperties, $$4.nbt)));
            }
            return Either.left((Object)((Object)new BlockResult($$4.state, $$4.properties, $$4.nbt)));
        }
        catch (CommandSyntaxException $$5) {
            $$1.setCursor($$3);
            throw $$5;
        }
    }

    public static CompletableFuture<Suggestions> fillSuggestions(HolderLookup<Block> $$0, SuggestionsBuilder $$1, boolean $$2, boolean $$3) {
        StringReader $$4 = new StringReader($$1.getInput());
        $$4.setCursor($$1.getStart());
        BlockStateParser $$5 = new BlockStateParser($$0, $$4, $$2, $$3);
        try {
            $$5.parse();
        }
        catch (CommandSyntaxException commandSyntaxException) {
            // empty catch block
        }
        return (CompletableFuture)$$5.suggestions.apply((Object)$$1.createOffset($$4.getCursor()));
    }

    private void parse() throws CommandSyntaxException {
        this.suggestions = this.forTesting ? this::suggestBlockIdOrTag : this::suggestItem;
        if (this.reader.canRead() && this.reader.peek() == '#') {
            this.readTag();
            this.suggestions = this::suggestOpenVaguePropertiesOrNbt;
            if (this.reader.canRead() && this.reader.peek() == '[') {
                this.readVagueProperties();
                this.suggestions = this::suggestOpenNbt;
            }
        } else {
            this.readBlock();
            this.suggestions = this::suggestOpenPropertiesOrNbt;
            if (this.reader.canRead() && this.reader.peek() == '[') {
                this.readProperties();
                this.suggestions = this::suggestOpenNbt;
            }
        }
        if (this.allowNbt && this.reader.canRead() && this.reader.peek() == '{') {
            this.suggestions = SUGGEST_NOTHING;
            this.readNbt();
        }
    }

    private CompletableFuture<Suggestions> suggestPropertyNameOrEnd(SuggestionsBuilder $$0) {
        if ($$0.getRemaining().isEmpty()) {
            $$0.suggest(String.valueOf((char)']'));
        }
        return this.suggestPropertyName($$0);
    }

    private CompletableFuture<Suggestions> suggestVaguePropertyNameOrEnd(SuggestionsBuilder $$0) {
        if ($$0.getRemaining().isEmpty()) {
            $$0.suggest(String.valueOf((char)']'));
        }
        return this.suggestVaguePropertyName($$0);
    }

    private CompletableFuture<Suggestions> suggestPropertyName(SuggestionsBuilder $$0) {
        String $$1 = $$0.getRemaining().toLowerCase(Locale.ROOT);
        for (Property $$2 : this.state.getProperties()) {
            if (this.properties.containsKey((Object)$$2) || !$$2.getName().startsWith($$1)) continue;
            $$0.suggest($$2.getName() + "=");
        }
        return $$0.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestVaguePropertyName(SuggestionsBuilder $$0) {
        String $$1 = $$0.getRemaining().toLowerCase(Locale.ROOT);
        if (this.tag != null) {
            for (Holder holder : this.tag) {
                for (Property $$3 : ((Block)holder.value()).getStateDefinition().getProperties()) {
                    if (this.vagueProperties.containsKey((Object)$$3.getName()) || !$$3.getName().startsWith($$1)) continue;
                    $$0.suggest($$3.getName() + "=");
                }
            }
        }
        return $$0.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestOpenNbt(SuggestionsBuilder $$0) {
        if ($$0.getRemaining().isEmpty() && this.hasBlockEntity()) {
            $$0.suggest(String.valueOf((char)'{'));
        }
        return $$0.buildFuture();
    }

    private boolean hasBlockEntity() {
        if (this.state != null) {
            return this.state.hasBlockEntity();
        }
        if (this.tag != null) {
            for (Holder holder : this.tag) {
                if (!((Block)holder.value()).defaultBlockState().hasBlockEntity()) continue;
                return true;
            }
        }
        return false;
    }

    private CompletableFuture<Suggestions> suggestEquals(SuggestionsBuilder $$0) {
        if ($$0.getRemaining().isEmpty()) {
            $$0.suggest(String.valueOf((char)'='));
        }
        return $$0.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestNextPropertyOrEnd(SuggestionsBuilder $$0) {
        if ($$0.getRemaining().isEmpty()) {
            $$0.suggest(String.valueOf((char)']'));
        }
        if ($$0.getRemaining().isEmpty() && this.properties.size() < this.state.getProperties().size()) {
            $$0.suggest(String.valueOf((char)','));
        }
        return $$0.buildFuture();
    }

    private static <T extends Comparable<T>> SuggestionsBuilder addSuggestions(SuggestionsBuilder $$0, Property<T> $$1) {
        for (Comparable $$2 : $$1.getPossibleValues()) {
            if ($$2 instanceof Integer) {
                Integer $$3 = (Integer)$$2;
                $$0.suggest($$3.intValue());
                continue;
            }
            $$0.suggest($$1.getName($$2));
        }
        return $$0;
    }

    private CompletableFuture<Suggestions> suggestVaguePropertyValue(SuggestionsBuilder $$0, String $$1) {
        boolean $$2 = false;
        if (this.tag != null) {
            block0: for (Holder holder : this.tag) {
                Block $$4 = (Block)holder.value();
                Property<?> $$5 = $$4.getStateDefinition().getProperty($$1);
                if ($$5 != null) {
                    BlockStateParser.addSuggestions($$0, $$5);
                }
                if ($$2) continue;
                for (Property $$6 : $$4.getStateDefinition().getProperties()) {
                    if (this.vagueProperties.containsKey((Object)$$6.getName())) continue;
                    $$2 = true;
                    continue block0;
                }
            }
        }
        if ($$2) {
            $$0.suggest(String.valueOf((char)','));
        }
        $$0.suggest(String.valueOf((char)']'));
        return $$0.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestOpenVaguePropertiesOrNbt(SuggestionsBuilder $$0) {
        if ($$0.getRemaining().isEmpty() && this.tag != null) {
            Holder $$3;
            Block $$4;
            boolean $$1 = false;
            boolean $$2 = false;
            Iterator iterator = this.tag.iterator();
            while (!(!iterator.hasNext() || ($$1 |= !($$4 = (Block)($$3 = (Holder)iterator.next()).value()).getStateDefinition().getProperties().isEmpty()) && ($$2 |= $$4.defaultBlockState().hasBlockEntity()))) {
            }
            if ($$1) {
                $$0.suggest(String.valueOf((char)'['));
            }
            if ($$2) {
                $$0.suggest(String.valueOf((char)'{'));
            }
        }
        return $$0.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestOpenPropertiesOrNbt(SuggestionsBuilder $$0) {
        if ($$0.getRemaining().isEmpty()) {
            if (!this.definition.getProperties().isEmpty()) {
                $$0.suggest(String.valueOf((char)'['));
            }
            if (this.state.hasBlockEntity()) {
                $$0.suggest(String.valueOf((char)'{'));
            }
        }
        return $$0.buildFuture();
    }

    private CompletableFuture<Suggestions> suggestTag(SuggestionsBuilder $$0) {
        return SharedSuggestionProvider.suggestResource((Stream<ResourceLocation>)this.blocks.listTagIds().map(TagKey::location), $$0, String.valueOf((char)'#'));
    }

    private CompletableFuture<Suggestions> suggestItem(SuggestionsBuilder $$0) {
        return SharedSuggestionProvider.suggestResource((Stream<ResourceLocation>)this.blocks.listElementIds().map(ResourceKey::location), $$0);
    }

    private CompletableFuture<Suggestions> suggestBlockIdOrTag(SuggestionsBuilder $$0) {
        this.suggestTag($$0);
        this.suggestItem($$0);
        return $$0.buildFuture();
    }

    private void readBlock() throws CommandSyntaxException {
        int $$0 = this.reader.getCursor();
        this.id = ResourceLocation.read(this.reader);
        Block $$1 = (Block)((Holder.Reference)this.blocks.get(ResourceKey.create(Registries.BLOCK, this.id)).orElseThrow(() -> {
            this.reader.setCursor($$0);
            return ERROR_UNKNOWN_BLOCK.createWithContext((ImmutableStringReader)this.reader, (Object)this.id.toString());
        })).value();
        this.definition = $$1.getStateDefinition();
        this.state = $$1.defaultBlockState();
    }

    private void readTag() throws CommandSyntaxException {
        if (!this.forTesting) {
            throw ERROR_NO_TAGS_ALLOWED.createWithContext((ImmutableStringReader)this.reader);
        }
        int $$0 = this.reader.getCursor();
        this.reader.expect('#');
        this.suggestions = this::suggestTag;
        ResourceLocation $$1 = ResourceLocation.read(this.reader);
        this.tag = (HolderSet)this.blocks.get(TagKey.create(Registries.BLOCK, $$1)).orElseThrow(() -> {
            this.reader.setCursor($$0);
            return ERROR_UNKNOWN_TAG.createWithContext((ImmutableStringReader)this.reader, (Object)$$1.toString());
        });
    }

    private void readProperties() throws CommandSyntaxException {
        this.reader.skip();
        this.suggestions = this::suggestPropertyNameOrEnd;
        this.reader.skipWhitespace();
        while (this.reader.canRead() && this.reader.peek() != ']') {
            this.reader.skipWhitespace();
            int $$0 = this.reader.getCursor();
            String $$12 = this.reader.readString();
            Property<?> $$2 = this.definition.getProperty($$12);
            if ($$2 == null) {
                this.reader.setCursor($$0);
                throw ERROR_UNKNOWN_PROPERTY.createWithContext((ImmutableStringReader)this.reader, (Object)this.id.toString(), (Object)$$12);
            }
            if (this.properties.containsKey($$2)) {
                this.reader.setCursor($$0);
                throw ERROR_DUPLICATE_PROPERTY.createWithContext((ImmutableStringReader)this.reader, (Object)this.id.toString(), (Object)$$12);
            }
            this.reader.skipWhitespace();
            this.suggestions = this::suggestEquals;
            if (!this.reader.canRead() || this.reader.peek() != '=') {
                throw ERROR_EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader, (Object)this.id.toString(), (Object)$$12);
            }
            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestions = $$1 -> BlockStateParser.addSuggestions($$1, $$2).buildFuture();
            int $$3 = this.reader.getCursor();
            this.setValue($$2, this.reader.readString(), $$3);
            this.suggestions = this::suggestNextPropertyOrEnd;
            this.reader.skipWhitespace();
            if (!this.reader.canRead()) continue;
            if (this.reader.peek() == ',') {
                this.reader.skip();
                this.suggestions = this::suggestPropertyName;
                continue;
            }
            if (this.reader.peek() == ']') break;
            throw ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext((ImmutableStringReader)this.reader);
        }
        if (!this.reader.canRead()) {
            throw ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext((ImmutableStringReader)this.reader);
        }
        this.reader.skip();
    }

    private void readVagueProperties() throws CommandSyntaxException {
        this.reader.skip();
        this.suggestions = this::suggestVaguePropertyNameOrEnd;
        int $$0 = -1;
        this.reader.skipWhitespace();
        while (this.reader.canRead() && this.reader.peek() != ']') {
            this.reader.skipWhitespace();
            int $$12 = this.reader.getCursor();
            String $$2 = this.reader.readString();
            if (this.vagueProperties.containsKey((Object)$$2)) {
                this.reader.setCursor($$12);
                throw ERROR_DUPLICATE_PROPERTY.createWithContext((ImmutableStringReader)this.reader, (Object)this.id.toString(), (Object)$$2);
            }
            this.reader.skipWhitespace();
            if (!this.reader.canRead() || this.reader.peek() != '=') {
                this.reader.setCursor($$12);
                throw ERROR_EXPECTED_VALUE.createWithContext((ImmutableStringReader)this.reader, (Object)this.id.toString(), (Object)$$2);
            }
            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestions = $$1 -> this.suggestVaguePropertyValue((SuggestionsBuilder)$$1, $$2);
            $$0 = this.reader.getCursor();
            String $$3 = this.reader.readString();
            this.vagueProperties.put((Object)$$2, (Object)$$3);
            this.reader.skipWhitespace();
            if (!this.reader.canRead()) continue;
            $$0 = -1;
            if (this.reader.peek() == ',') {
                this.reader.skip();
                this.suggestions = this::suggestVaguePropertyName;
                continue;
            }
            if (this.reader.peek() == ']') break;
            throw ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext((ImmutableStringReader)this.reader);
        }
        if (!this.reader.canRead()) {
            if ($$0 >= 0) {
                this.reader.setCursor($$0);
            }
            throw ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext((ImmutableStringReader)this.reader);
        }
        this.reader.skip();
    }

    private void readNbt() throws CommandSyntaxException {
        this.nbt = new TagParser(this.reader).readStruct();
    }

    private <T extends Comparable<T>> void setValue(Property<T> $$0, String $$1, int $$2) throws CommandSyntaxException {
        Optional<T> $$3 = $$0.getValue($$1);
        if (!$$3.isPresent()) {
            this.reader.setCursor($$2);
            throw ERROR_INVALID_VALUE.createWithContext((ImmutableStringReader)this.reader, (Object)this.id.toString(), (Object)$$0.getName(), (Object)$$1);
        }
        this.state = (BlockState)this.state.setValue($$0, (Comparable)$$3.get());
        this.properties.put($$0, (Object)((Comparable)$$3.get()));
    }

    public static String serialize(BlockState $$02) {
        StringBuilder $$1 = new StringBuilder((String)$$02.getBlockHolder().unwrapKey().map($$0 -> $$0.location().toString()).orElse((Object)"air"));
        if (!$$02.getProperties().isEmpty()) {
            $$1.append('[');
            boolean $$2 = false;
            for (Map.Entry $$3 : $$02.getValues().entrySet()) {
                if ($$2) {
                    $$1.append(',');
                }
                BlockStateParser.appendProperty($$1, (Property)$$3.getKey(), (Comparable)$$3.getValue());
                $$2 = true;
            }
            $$1.append(']');
        }
        return $$1.toString();
    }

    private static <T extends Comparable<T>> void appendProperty(StringBuilder $$0, Property<T> $$1, Comparable<?> $$2) {
        $$0.append($$1.getName());
        $$0.append('=');
        $$0.append($$1.getName($$2));
    }

    public record BlockResult(BlockState blockState, Map<Property<?>, Comparable<?>> properties, @Nullable CompoundTag nbt) {
    }

    public record TagResult(HolderSet<Block> tag, Map<String, String> vagueProperties, @Nullable CompoundTag nbt) {
    }
}