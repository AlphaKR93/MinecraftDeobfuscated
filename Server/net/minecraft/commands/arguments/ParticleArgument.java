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
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.concurrent.CompletableFuture
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
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class ParticleArgument
implements ArgumentType<ParticleOptions> {
    private static final Collection<String> EXAMPLES = Arrays.asList((Object[])new String[]{"foo", "foo:bar", "particle with options"});
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_PARTICLE = new DynamicCommandExceptionType($$0 -> Component.translatable("particle.notFound", $$0));
    private final HolderLookup<ParticleType<?>> particles;

    public ParticleArgument(CommandBuildContext $$0) {
        this.particles = $$0.holderLookup(Registries.PARTICLE_TYPE);
    }

    public static ParticleArgument particle(CommandBuildContext $$0) {
        return new ParticleArgument($$0);
    }

    public static ParticleOptions getParticle(CommandContext<CommandSourceStack> $$0, String $$1) {
        return (ParticleOptions)$$0.getArgument($$1, ParticleOptions.class);
    }

    public ParticleOptions parse(StringReader $$0) throws CommandSyntaxException {
        return ParticleArgument.readParticle($$0, this.particles);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static ParticleOptions readParticle(StringReader $$0, HolderLookup<ParticleType<?>> $$1) throws CommandSyntaxException {
        ParticleType<?> $$2 = ParticleArgument.readParticleType($$0, $$1);
        return ParticleArgument.readParticle($$0, $$2);
    }

    private static ParticleType<?> readParticleType(StringReader $$0, HolderLookup<ParticleType<?>> $$1) throws CommandSyntaxException {
        ResourceLocation $$2 = ResourceLocation.read($$0);
        ResourceKey<ParticleType<?>> $$3 = ResourceKey.create(Registries.PARTICLE_TYPE, $$2);
        return (ParticleType)((Holder.Reference)$$1.get($$3).orElseThrow(() -> ERROR_UNKNOWN_PARTICLE.create((Object)$$2))).value();
    }

    private static <T extends ParticleOptions> T readParticle(StringReader $$0, ParticleType<T> $$1) throws CommandSyntaxException {
        return $$1.getDeserializer().fromCommand($$1, $$0);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> $$0, SuggestionsBuilder $$1) {
        return SharedSuggestionProvider.suggestResource((Stream<ResourceLocation>)this.particles.listElementIds().map(ResourceKey::location), $$1);
    }
}