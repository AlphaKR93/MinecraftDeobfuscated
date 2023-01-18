/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.List
 *  java.util.Optional
 *  java.util.concurrent.CompletableFuture
 *  java.util.function.Consumer
 */
package net.minecraft.commands.arguments;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class GameProfileArgument
implements ArgumentType<Result> {
    private static final Collection<String> EXAMPLES = Arrays.asList((Object[])new String[]{"Player", "0123", "dd12be42-52a9-4a91-a8a1-11c01849e498", "@e"});
    public static final SimpleCommandExceptionType ERROR_UNKNOWN_PLAYER = new SimpleCommandExceptionType((Message)Component.translatable("argument.player.unknown"));

    public static Collection<GameProfile> getGameProfiles(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
        return ((Result)$$0.getArgument($$1, Result.class)).getNames((CommandSourceStack)$$0.getSource());
    }

    public static GameProfileArgument gameProfile() {
        return new GameProfileArgument();
    }

    public Result parse(StringReader $$0) throws CommandSyntaxException {
        if ($$0.canRead() && $$0.peek() == '@') {
            EntitySelectorParser $$12 = new EntitySelectorParser($$0);
            EntitySelector $$2 = $$12.parse();
            if ($$2.includesEntities()) {
                throw EntityArgument.ERROR_ONLY_PLAYERS_ALLOWED.create();
            }
            return new SelectorResult($$2);
        }
        int $$3 = $$0.getCursor();
        while ($$0.canRead() && $$0.peek() != ' ') {
            $$0.skip();
        }
        String $$4 = $$0.getString().substring($$3, $$0.getCursor());
        return $$1 -> {
            Optional<GameProfile> $$2 = $$1.getServer().getProfileCache().get($$4);
            return Collections.singleton((Object)((GameProfile)$$2.orElseThrow(() -> ((SimpleCommandExceptionType)ERROR_UNKNOWN_PLAYER).create())));
        };
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> $$0, SuggestionsBuilder $$12) {
        if ($$0.getSource() instanceof SharedSuggestionProvider) {
            StringReader $$2 = new StringReader($$12.getInput());
            $$2.setCursor($$12.getStart());
            EntitySelectorParser $$3 = new EntitySelectorParser($$2);
            try {
                $$3.parse();
            }
            catch (CommandSyntaxException commandSyntaxException) {
                // empty catch block
            }
            return $$3.fillSuggestions($$12, (Consumer<SuggestionsBuilder>)((Consumer)$$1 -> SharedSuggestionProvider.suggest(((SharedSuggestionProvider)$$0.getSource()).getOnlinePlayerNames(), $$1)));
        }
        return Suggestions.empty();
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @FunctionalInterface
    public static interface Result {
        public Collection<GameProfile> getNames(CommandSourceStack var1) throws CommandSyntaxException;
    }

    public static class SelectorResult
    implements Result {
        private final EntitySelector selector;

        public SelectorResult(EntitySelector $$0) {
            this.selector = $$0;
        }

        @Override
        public Collection<GameProfile> getNames(CommandSourceStack $$0) throws CommandSyntaxException {
            List<ServerPlayer> $$1 = this.selector.findPlayers($$0);
            if ($$1.isEmpty()) {
                throw EntityArgument.NO_PLAYERS_FOUND.create();
            }
            ArrayList $$2 = Lists.newArrayList();
            for (ServerPlayer $$3 : $$1) {
                $$2.add((Object)$$3.getGameProfile());
            }
            return $$2;
        }
    }
}