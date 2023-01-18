/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collection
 *  java.util.concurrent.CompletableFuture
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 */
package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class DimensionArgument
implements ArgumentType<ResourceLocation> {
    private static final Collection<String> EXAMPLES = (Collection)Stream.of((Object[])new ResourceKey[]{Level.OVERWORLD, Level.NETHER}).map($$0 -> $$0.location().toString()).collect(Collectors.toList());
    private static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType($$0 -> Component.translatable("argument.dimension.invalid", $$0));

    public ResourceLocation parse(StringReader $$0) throws CommandSyntaxException {
        return ResourceLocation.read($$0);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> $$0, SuggestionsBuilder $$1) {
        if ($$0.getSource() instanceof SharedSuggestionProvider) {
            return SharedSuggestionProvider.suggestResource((Stream<ResourceLocation>)((SharedSuggestionProvider)$$0.getSource()).levels().stream().map(ResourceKey::location), $$1);
        }
        return Suggestions.empty();
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static DimensionArgument dimension() {
        return new DimensionArgument();
    }

    public static ServerLevel getDimension(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
        ResourceLocation $$2 = (ResourceLocation)$$0.getArgument($$1, ResourceLocation.class);
        ResourceKey<Level> $$3 = ResourceKey.create(Registries.DIMENSION, $$2);
        ServerLevel $$4 = ((CommandSourceStack)$$0.getSource()).getServer().getLevel($$3);
        if ($$4 == null) {
            throw ERROR_INVALID_VALUE.create((Object)$$2);
        }
        return $$4;
    }
}