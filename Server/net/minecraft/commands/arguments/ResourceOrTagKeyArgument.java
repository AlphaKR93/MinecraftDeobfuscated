/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  com.mojang.datafixers.util.Either
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.Optional
 *  java.util.concurrent.CompletableFuture
 *  java.util.function.Predicate
 */
package net.minecraft.commands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class ResourceOrTagKeyArgument<T>
implements ArgumentType<Result<T>> {
    private static final Collection<String> EXAMPLES = Arrays.asList((Object[])new String[]{"foo", "foo:bar", "012", "#skeletons", "#minecraft:skeletons"});
    final ResourceKey<? extends Registry<T>> registryKey;

    public ResourceOrTagKeyArgument(ResourceKey<? extends Registry<T>> $$0) {
        this.registryKey = $$0;
    }

    public static <T> ResourceOrTagKeyArgument<T> resourceOrTagKey(ResourceKey<? extends Registry<T>> $$0) {
        return new ResourceOrTagKeyArgument<T>($$0);
    }

    public static <T> Result<T> getResourceOrTagKey(CommandContext<CommandSourceStack> $$0, String $$1, ResourceKey<Registry<T>> $$2, DynamicCommandExceptionType $$3) throws CommandSyntaxException {
        Result $$4 = (Result)$$0.getArgument($$1, Result.class);
        Optional<Result<T>> $$5 = $$4.cast($$2);
        return (Result)$$5.orElseThrow(() -> $$3.create((Object)$$4));
    }

    public Result<T> parse(StringReader $$0) throws CommandSyntaxException {
        if ($$0.canRead() && $$0.peek() == '#') {
            int $$1 = $$0.getCursor();
            try {
                $$0.skip();
                ResourceLocation $$2 = ResourceLocation.read($$0);
                return new TagResult(TagKey.create(this.registryKey, $$2));
            }
            catch (CommandSyntaxException $$3) {
                $$0.setCursor($$1);
                throw $$3;
            }
        }
        ResourceLocation $$4 = ResourceLocation.read($$0);
        return new ResourceResult(ResourceKey.create(this.registryKey, $$4));
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> $$0, SuggestionsBuilder $$1) {
        Object object = $$0.getSource();
        if (object instanceof SharedSuggestionProvider) {
            SharedSuggestionProvider $$2 = (SharedSuggestionProvider)object;
            return $$2.suggestRegistryElements(this.registryKey, SharedSuggestionProvider.ElementSuggestionType.ALL, $$1, $$0);
        }
        return $$1.buildFuture();
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static interface Result<T>
    extends Predicate<Holder<T>> {
        public Either<ResourceKey<T>, TagKey<T>> unwrap();

        public <E> Optional<Result<E>> cast(ResourceKey<? extends Registry<E>> var1);

        public String asPrintable();
    }

    record TagResult<T>(TagKey<T> key) implements Result<T>
    {
        @Override
        public Either<ResourceKey<T>, TagKey<T>> unwrap() {
            return Either.right(this.key);
        }

        @Override
        public <E> Optional<Result<E>> cast(ResourceKey<? extends Registry<E>> $$0) {
            return this.key.cast($$0).map(TagResult::new);
        }

        public boolean test(Holder<T> $$0) {
            return $$0.is(this.key);
        }

        @Override
        public String asPrintable() {
            return "#" + this.key.location();
        }
    }

    record ResourceResult<T>(ResourceKey<T> key) implements Result<T>
    {
        @Override
        public Either<ResourceKey<T>, TagKey<T>> unwrap() {
            return Either.left(this.key);
        }

        @Override
        public <E> Optional<Result<E>> cast(ResourceKey<? extends Registry<E>> $$0) {
            return this.key.cast($$0).map(ResourceResult::new);
        }

        public boolean test(Holder<T> $$0) {
            return $$0.is(this.key);
        }

        @Override
        public String asPrintable() {
            return this.key.location().toString();
        }
    }

    public static class Info<T>
    implements ArgumentTypeInfo<ResourceOrTagKeyArgument<T>, Template> {
        @Override
        public void serializeToNetwork(Template $$0, FriendlyByteBuf $$1) {
            $$1.writeResourceLocation($$0.registryKey.location());
        }

        @Override
        public Template deserializeFromNetwork(FriendlyByteBuf $$0) {
            ResourceLocation $$1 = $$0.readResourceLocation();
            return new Template(ResourceKey.createRegistryKey($$1));
        }

        @Override
        public void serializeToJson(Template $$0, JsonObject $$1) {
            $$1.addProperty("registry", $$0.registryKey.location().toString());
        }

        @Override
        public Template unpack(ResourceOrTagKeyArgument<T> $$0) {
            return new Template($$0.registryKey);
        }

        public final class Template
        implements ArgumentTypeInfo.Template<ResourceOrTagKeyArgument<T>> {
            final ResourceKey<? extends Registry<T>> registryKey;

            Template(ResourceKey<? extends Registry<T>> $$1) {
                this.registryKey = $$1;
            }

            @Override
            public ResourceOrTagKeyArgument<T> instantiate(CommandBuildContext $$0) {
                return new ResourceOrTagKeyArgument(this.registryKey);
            }

            @Override
            public ArgumentTypeInfo<ResourceOrTagKeyArgument<T>, ?> type() {
                return Info.this;
            }
        }
    }
}