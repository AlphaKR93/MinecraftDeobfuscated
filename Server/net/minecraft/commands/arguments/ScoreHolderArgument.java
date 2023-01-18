/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonObject
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionProvider
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
 *  java.util.Set
 *  java.util.function.Consumer
 *  java.util.function.Supplier
 */
package net.minecraft.commands.arguments;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public class ScoreHolderArgument
implements ArgumentType<Result> {
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_SCORE_HOLDERS = ($$0, $$12) -> {
        StringReader $$2 = new StringReader($$12.getInput());
        $$2.setCursor($$12.getStart());
        EntitySelectorParser $$3 = new EntitySelectorParser($$2);
        try {
            $$3.parse();
        }
        catch (CommandSyntaxException commandSyntaxException) {
            // empty catch block
        }
        return $$3.fillSuggestions($$12, (Consumer<SuggestionsBuilder>)((Consumer)$$1 -> SharedSuggestionProvider.suggest(((CommandSourceStack)$$0.getSource()).getOnlinePlayerNames(), $$1)));
    };
    private static final Collection<String> EXAMPLES = Arrays.asList((Object[])new String[]{"Player", "0123", "*", "@e"});
    private static final SimpleCommandExceptionType ERROR_NO_RESULTS = new SimpleCommandExceptionType((Message)Component.translatable("argument.scoreHolder.empty"));
    final boolean multiple;

    public ScoreHolderArgument(boolean $$0) {
        this.multiple = $$0;
    }

    public static String getName(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
        return (String)ScoreHolderArgument.getNames($$0, $$1).iterator().next();
    }

    public static Collection<String> getNames(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
        return ScoreHolderArgument.getNames($$0, $$1, (Supplier<Collection<String>>)((Supplier)Collections::emptyList));
    }

    public static Collection<String> getNamesWithDefaultWildcard(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
        return ScoreHolderArgument.getNames($$0, $$1, (Supplier<Collection<String>>)((Supplier)((CommandSourceStack)$$0.getSource()).getServer().getScoreboard()::getTrackedPlayers));
    }

    public static Collection<String> getNames(CommandContext<CommandSourceStack> $$0, String $$1, Supplier<Collection<String>> $$2) throws CommandSyntaxException {
        Collection<String> $$3 = ((Result)$$0.getArgument($$1, Result.class)).getNames((CommandSourceStack)$$0.getSource(), $$2);
        if ($$3.isEmpty()) {
            throw EntityArgument.NO_ENTITIES_FOUND.create();
        }
        return $$3;
    }

    public static ScoreHolderArgument scoreHolder() {
        return new ScoreHolderArgument(false);
    }

    public static ScoreHolderArgument scoreHolders() {
        return new ScoreHolderArgument(true);
    }

    public Result parse(StringReader $$02) throws CommandSyntaxException {
        if ($$02.canRead() && $$02.peek() == '@') {
            EntitySelectorParser $$12 = new EntitySelectorParser($$02);
            EntitySelector $$2 = $$12.parse();
            if (!this.multiple && $$2.getMaxResults() > 1) {
                throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
            }
            return new SelectorResult($$2);
        }
        int $$3 = $$02.getCursor();
        while ($$02.canRead() && $$02.peek() != ' ') {
            $$02.skip();
        }
        String $$4 = $$02.getString().substring($$3, $$02.getCursor());
        if ($$4.equals((Object)"*")) {
            return ($$0, $$1) -> {
                Collection $$2 = (Collection)$$1.get();
                if ($$2.isEmpty()) {
                    throw ERROR_NO_RESULTS.create();
                }
                return $$2;
            };
        }
        Set $$5 = Collections.singleton((Object)$$4);
        return (arg_0, arg_1) -> ScoreHolderArgument.lambda$parse$3((Collection)$$5, arg_0, arg_1);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    private static /* synthetic */ Collection lambda$parse$3(Collection $$0, CommandSourceStack $$1, Supplier $$2) throws CommandSyntaxException {
        return $$0;
    }

    @FunctionalInterface
    public static interface Result {
        public Collection<String> getNames(CommandSourceStack var1, Supplier<Collection<String>> var2) throws CommandSyntaxException;
    }

    public static class SelectorResult
    implements Result {
        private final EntitySelector selector;

        public SelectorResult(EntitySelector $$0) {
            this.selector = $$0;
        }

        @Override
        public Collection<String> getNames(CommandSourceStack $$0, Supplier<Collection<String>> $$1) throws CommandSyntaxException {
            List<? extends Entity> $$2 = this.selector.findEntities($$0);
            if ($$2.isEmpty()) {
                throw EntityArgument.NO_ENTITIES_FOUND.create();
            }
            ArrayList $$3 = Lists.newArrayList();
            for (Entity $$4 : $$2) {
                $$3.add((Object)$$4.getScoreboardName());
            }
            return $$3;
        }
    }

    public static class Info
    implements ArgumentTypeInfo<ScoreHolderArgument, Template> {
        private static final byte FLAG_MULTIPLE = 1;

        @Override
        public void serializeToNetwork(Template $$0, FriendlyByteBuf $$1) {
            int $$2 = 0;
            if ($$0.multiple) {
                $$2 |= 1;
            }
            $$1.writeByte($$2);
        }

        @Override
        public Template deserializeFromNetwork(FriendlyByteBuf $$0) {
            byte $$1 = $$0.readByte();
            boolean $$2 = ($$1 & 1) != 0;
            return new Template($$2);
        }

        @Override
        public void serializeToJson(Template $$0, JsonObject $$1) {
            $$1.addProperty("amount", $$0.multiple ? "multiple" : "single");
        }

        @Override
        public Template unpack(ScoreHolderArgument $$0) {
            return new Template($$0.multiple);
        }

        public final class Template
        implements ArgumentTypeInfo.Template<ScoreHolderArgument> {
            final boolean multiple;

            Template(boolean $$1) {
                this.multiple = $$1;
            }

            @Override
            public ScoreHolderArgument instantiate(CommandBuildContext $$0) {
                return new ScoreHolderArgument(this.multiple);
            }

            @Override
            public ArgumentTypeInfo<ScoreHolderArgument, ?> type() {
                return Info.this;
            }
        }
    }
}