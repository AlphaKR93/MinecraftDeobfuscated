/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.util.Map
 *  java.util.concurrent.CompletableFuture
 */
package net.minecraft.commands.synchronization;

import com.google.common.collect.Maps;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class SuggestionProviders {
    private static final Map<ResourceLocation, SuggestionProvider<SharedSuggestionProvider>> PROVIDERS_BY_NAME = Maps.newHashMap();
    private static final ResourceLocation DEFAULT_NAME = new ResourceLocation("ask_server");
    public static final SuggestionProvider<SharedSuggestionProvider> ASK_SERVER = SuggestionProviders.register(DEFAULT_NAME, (SuggestionProvider<SharedSuggestionProvider>)((SuggestionProvider)($$0, $$1) -> ((SharedSuggestionProvider)$$0.getSource()).customSuggestion($$0)));
    public static final SuggestionProvider<CommandSourceStack> ALL_RECIPES = SuggestionProviders.register(new ResourceLocation("all_recipes"), (SuggestionProvider<SharedSuggestionProvider>)((SuggestionProvider)($$0, $$1) -> SharedSuggestionProvider.suggestResource(((SharedSuggestionProvider)$$0.getSource()).getRecipeNames(), $$1)));
    public static final SuggestionProvider<CommandSourceStack> AVAILABLE_SOUNDS = SuggestionProviders.register(new ResourceLocation("available_sounds"), (SuggestionProvider<SharedSuggestionProvider>)((SuggestionProvider)($$0, $$1) -> SharedSuggestionProvider.suggestResource(((SharedSuggestionProvider)$$0.getSource()).getAvailableSounds(), $$1)));
    public static final SuggestionProvider<CommandSourceStack> SUMMONABLE_ENTITIES = SuggestionProviders.register(new ResourceLocation("summonable_entities"), (SuggestionProvider<SharedSuggestionProvider>)((SuggestionProvider)($$02, $$12) -> SharedSuggestionProvider.suggestResource(BuiltInRegistries.ENTITY_TYPE.stream().filter($$1 -> $$1.isEnabled(((SharedSuggestionProvider)$$02.getSource()).enabledFeatures()) && $$1.canSummon()), $$12, EntityType::getKey, $$0 -> Component.translatable(Util.makeDescriptionId("entity", EntityType.getKey($$0))))));

    public static <S extends SharedSuggestionProvider> SuggestionProvider<S> register(ResourceLocation $$0, SuggestionProvider<SharedSuggestionProvider> $$1) {
        if (PROVIDERS_BY_NAME.containsKey((Object)$$0)) {
            throw new IllegalArgumentException("A command suggestion provider is already registered with the name " + $$0);
        }
        PROVIDERS_BY_NAME.put((Object)$$0, $$1);
        return new Wrapper($$0, $$1);
    }

    public static SuggestionProvider<SharedSuggestionProvider> getProvider(ResourceLocation $$0) {
        return (SuggestionProvider)PROVIDERS_BY_NAME.getOrDefault((Object)$$0, ASK_SERVER);
    }

    public static ResourceLocation getName(SuggestionProvider<SharedSuggestionProvider> $$0) {
        if ($$0 instanceof Wrapper) {
            return ((Wrapper)$$0).name;
        }
        return DEFAULT_NAME;
    }

    public static SuggestionProvider<SharedSuggestionProvider> safelySwap(SuggestionProvider<SharedSuggestionProvider> $$0) {
        if ($$0 instanceof Wrapper) {
            return $$0;
        }
        return ASK_SERVER;
    }

    protected static class Wrapper
    implements SuggestionProvider<SharedSuggestionProvider> {
        private final SuggestionProvider<SharedSuggestionProvider> delegate;
        final ResourceLocation name;

        public Wrapper(ResourceLocation $$0, SuggestionProvider<SharedSuggestionProvider> $$1) {
            this.delegate = $$1;
            this.name = $$0;
        }

        public CompletableFuture<Suggestions> getSuggestions(CommandContext<SharedSuggestionProvider> $$0, SuggestionsBuilder $$1) throws CommandSyntaxException {
            return this.delegate.getSuggestions($$0, $$1);
        }
    }
}