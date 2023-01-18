/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.ParseResults
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.context.CommandContextBuilder
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.RootCommandNode
 *  com.mojang.logging.LogUtils
 *  java.lang.CharSequence
 *  java.lang.Exception
 *  java.lang.FunctionalInterface
 *  java.lang.IllegalStateException
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.StackTraceElement
 *  java.lang.String
 *  java.util.HashMap
 *  java.util.Map
 *  java.util.Optional
 *  java.util.Set
 *  java.util.function.Predicate
 *  java.util.function.Supplier
 *  java.util.function.UnaryOperator
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.commands;

import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.ArgumentUtils;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.gametest.framework.TestCommand;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.commands.AdvancementCommands;
import net.minecraft.server.commands.AttributeCommand;
import net.minecraft.server.commands.BanIpCommands;
import net.minecraft.server.commands.BanListCommands;
import net.minecraft.server.commands.BanPlayerCommands;
import net.minecraft.server.commands.BossBarCommands;
import net.minecraft.server.commands.ClearInventoryCommands;
import net.minecraft.server.commands.CloneCommands;
import net.minecraft.server.commands.DataPackCommand;
import net.minecraft.server.commands.DeOpCommands;
import net.minecraft.server.commands.DebugCommand;
import net.minecraft.server.commands.DefaultGameModeCommands;
import net.minecraft.server.commands.DifficultyCommand;
import net.minecraft.server.commands.EffectCommands;
import net.minecraft.server.commands.EmoteCommands;
import net.minecraft.server.commands.EnchantCommand;
import net.minecraft.server.commands.ExecuteCommand;
import net.minecraft.server.commands.ExperienceCommand;
import net.minecraft.server.commands.FillBiomeCommand;
import net.minecraft.server.commands.FillCommand;
import net.minecraft.server.commands.ForceLoadCommand;
import net.minecraft.server.commands.FunctionCommand;
import net.minecraft.server.commands.GameModeCommand;
import net.minecraft.server.commands.GameRuleCommand;
import net.minecraft.server.commands.GiveCommand;
import net.minecraft.server.commands.HelpCommand;
import net.minecraft.server.commands.ItemCommands;
import net.minecraft.server.commands.JfrCommand;
import net.minecraft.server.commands.KickCommand;
import net.minecraft.server.commands.KillCommand;
import net.minecraft.server.commands.ListPlayersCommand;
import net.minecraft.server.commands.LocateCommand;
import net.minecraft.server.commands.LootCommand;
import net.minecraft.server.commands.MsgCommand;
import net.minecraft.server.commands.OpCommand;
import net.minecraft.server.commands.PardonCommand;
import net.minecraft.server.commands.PardonIpCommand;
import net.minecraft.server.commands.ParticleCommand;
import net.minecraft.server.commands.PerfCommand;
import net.minecraft.server.commands.PlaceCommand;
import net.minecraft.server.commands.PlaySoundCommand;
import net.minecraft.server.commands.PublishCommand;
import net.minecraft.server.commands.RecipeCommand;
import net.minecraft.server.commands.ReloadCommand;
import net.minecraft.server.commands.SaveAllCommand;
import net.minecraft.server.commands.SaveOffCommand;
import net.minecraft.server.commands.SaveOnCommand;
import net.minecraft.server.commands.SayCommand;
import net.minecraft.server.commands.ScheduleCommand;
import net.minecraft.server.commands.ScoreboardCommand;
import net.minecraft.server.commands.SeedCommand;
import net.minecraft.server.commands.SetBlockCommand;
import net.minecraft.server.commands.SetPlayerIdleTimeoutCommand;
import net.minecraft.server.commands.SetSpawnCommand;
import net.minecraft.server.commands.SetWorldSpawnCommand;
import net.minecraft.server.commands.SpectateCommand;
import net.minecraft.server.commands.SpreadPlayersCommand;
import net.minecraft.server.commands.StopCommand;
import net.minecraft.server.commands.StopSoundCommand;
import net.minecraft.server.commands.SummonCommand;
import net.minecraft.server.commands.TagCommand;
import net.minecraft.server.commands.TeamCommand;
import net.minecraft.server.commands.TeamMsgCommand;
import net.minecraft.server.commands.TeleportCommand;
import net.minecraft.server.commands.TellRawCommand;
import net.minecraft.server.commands.TimeCommand;
import net.minecraft.server.commands.TitleCommand;
import net.minecraft.server.commands.TriggerCommand;
import net.minecraft.server.commands.WeatherCommand;
import net.minecraft.server.commands.WhitelistCommand;
import net.minecraft.server.commands.WorldBorderCommand;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import org.slf4j.Logger;

public class Commands {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int LEVEL_ALL = 0;
    public static final int LEVEL_MODERATORS = 1;
    public static final int LEVEL_GAMEMASTERS = 2;
    public static final int LEVEL_ADMINS = 3;
    public static final int LEVEL_OWNERS = 4;
    private final CommandDispatcher<CommandSourceStack> dispatcher = new CommandDispatcher();

    public Commands(CommandSelection $$02, CommandBuildContext $$12) {
        AdvancementCommands.register(this.dispatcher);
        AttributeCommand.register(this.dispatcher, $$12);
        ExecuteCommand.register(this.dispatcher, $$12);
        BossBarCommands.register(this.dispatcher);
        ClearInventoryCommands.register(this.dispatcher, $$12);
        CloneCommands.register(this.dispatcher, $$12);
        DataCommands.register(this.dispatcher);
        DataPackCommand.register(this.dispatcher);
        DebugCommand.register(this.dispatcher);
        DefaultGameModeCommands.register(this.dispatcher);
        DifficultyCommand.register(this.dispatcher);
        EffectCommands.register(this.dispatcher, $$12);
        EmoteCommands.register(this.dispatcher);
        EnchantCommand.register(this.dispatcher, $$12);
        ExperienceCommand.register(this.dispatcher);
        FillCommand.register(this.dispatcher, $$12);
        FillBiomeCommand.register(this.dispatcher, $$12);
        ForceLoadCommand.register(this.dispatcher);
        FunctionCommand.register(this.dispatcher);
        GameModeCommand.register(this.dispatcher);
        GameRuleCommand.register(this.dispatcher);
        GiveCommand.register(this.dispatcher, $$12);
        HelpCommand.register(this.dispatcher);
        ItemCommands.register(this.dispatcher, $$12);
        KickCommand.register(this.dispatcher);
        KillCommand.register(this.dispatcher);
        ListPlayersCommand.register(this.dispatcher);
        LocateCommand.register(this.dispatcher, $$12);
        LootCommand.register(this.dispatcher, $$12);
        MsgCommand.register(this.dispatcher);
        ParticleCommand.register(this.dispatcher, $$12);
        PlaceCommand.register(this.dispatcher);
        PlaySoundCommand.register(this.dispatcher);
        ReloadCommand.register(this.dispatcher);
        RecipeCommand.register(this.dispatcher);
        SayCommand.register(this.dispatcher);
        ScheduleCommand.register(this.dispatcher);
        ScoreboardCommand.register(this.dispatcher);
        SeedCommand.register(this.dispatcher, $$02 != CommandSelection.INTEGRATED);
        SetBlockCommand.register(this.dispatcher, $$12);
        SetSpawnCommand.register(this.dispatcher);
        SetWorldSpawnCommand.register(this.dispatcher);
        SpectateCommand.register(this.dispatcher);
        SpreadPlayersCommand.register(this.dispatcher);
        StopSoundCommand.register(this.dispatcher);
        SummonCommand.register(this.dispatcher, $$12);
        TagCommand.register(this.dispatcher);
        TeamCommand.register(this.dispatcher);
        TeamMsgCommand.register(this.dispatcher);
        TeleportCommand.register(this.dispatcher);
        TellRawCommand.register(this.dispatcher);
        TimeCommand.register(this.dispatcher);
        TitleCommand.register(this.dispatcher);
        TriggerCommand.register(this.dispatcher);
        WeatherCommand.register(this.dispatcher);
        WorldBorderCommand.register(this.dispatcher);
        if (JvmProfiler.INSTANCE.isAvailable()) {
            JfrCommand.register(this.dispatcher);
        }
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            TestCommand.register(this.dispatcher);
        }
        if ($$02.includeDedicated) {
            BanIpCommands.register(this.dispatcher);
            BanListCommands.register(this.dispatcher);
            BanPlayerCommands.register(this.dispatcher);
            DeOpCommands.register(this.dispatcher);
            OpCommand.register(this.dispatcher);
            PardonCommand.register(this.dispatcher);
            PardonIpCommand.register(this.dispatcher);
            PerfCommand.register(this.dispatcher);
            SaveAllCommand.register(this.dispatcher);
            SaveOffCommand.register(this.dispatcher);
            SaveOnCommand.register(this.dispatcher);
            SetPlayerIdleTimeoutCommand.register(this.dispatcher);
            StopCommand.register(this.dispatcher);
            WhitelistCommand.register(this.dispatcher);
        }
        if ($$02.includeIntegrated) {
            PublishCommand.register(this.dispatcher);
        }
        this.dispatcher.setConsumer(($$0, $$1, $$2) -> ((CommandSourceStack)$$0.getSource()).onCommandComplete((CommandContext<CommandSourceStack>)$$0, $$1, $$2));
    }

    public static <S> ParseResults<S> mapSource(ParseResults<S> $$0, UnaryOperator<S> $$1) {
        CommandContextBuilder $$2 = $$0.getContext();
        CommandContextBuilder $$3 = $$2.withSource($$1.apply($$2.getSource()));
        return new ParseResults($$3, $$0.getReader(), $$0.getExceptions());
    }

    public int performPrefixedCommand(CommandSourceStack $$0, String $$1) {
        $$1 = $$1.startsWith("/") ? $$1.substring(1) : $$1;
        return this.performCommand((ParseResults<CommandSourceStack>)this.dispatcher.parse($$1, (Object)$$0), $$1);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int performCommand(ParseResults<CommandSourceStack> $$0, String $$12) {
        CommandSourceStack $$2 = (CommandSourceStack)$$0.getContext().getSource();
        $$2.getServer().getProfiler().push((Supplier<String>)((Supplier)() -> "/" + $$12));
        try {
            int n = this.dispatcher.execute($$0);
            return n;
        }
        catch (CommandRuntimeException $$3) {
            $$2.sendFailure($$3.getComponent());
            int n = 0;
            return n;
        }
        catch (CommandSyntaxException $$4) {
            int $$5;
            $$2.sendFailure(ComponentUtils.fromMessage($$4.getRawMessage()));
            if ($$4.getInput() != null && $$4.getCursor() >= 0) {
                $$5 = Math.min((int)$$4.getInput().length(), (int)$$4.getCursor());
                MutableComponent $$6 = Component.empty().withStyle(ChatFormatting.GRAY).withStyle((UnaryOperator<Style>)((UnaryOperator)$$1 -> $$1.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + $$12))));
                if ($$5 > 10) {
                    $$6.append(CommonComponents.ELLIPSIS);
                }
                $$6.append($$4.getInput().substring(Math.max((int)0, (int)($$5 - 10)), $$5));
                if ($$5 < $$4.getInput().length()) {
                    MutableComponent $$7 = Component.literal($$4.getInput().substring($$5)).withStyle(ChatFormatting.RED, ChatFormatting.UNDERLINE);
                    $$6.append($$7);
                }
                $$6.append(Component.translatable("command.context.here").withStyle(ChatFormatting.RED, ChatFormatting.ITALIC));
                $$2.sendFailure($$6);
            }
            $$5 = 0;
            return $$5;
        }
        catch (Exception $$8) {
            MutableComponent $$9 = Component.literal($$8.getMessage() == null ? $$8.getClass().getName() : $$8.getMessage());
            if (LOGGER.isDebugEnabled()) {
                LOGGER.error("Command exception: /{}", (Object)$$12, (Object)$$8);
                StackTraceElement[] $$10 = $$8.getStackTrace();
                for (int $$11 = 0; $$11 < Math.min((int)$$10.length, (int)3); ++$$11) {
                    $$9.append("\n\n").append($$10[$$11].getMethodName()).append("\n ").append($$10[$$11].getFileName()).append(":").append(String.valueOf((int)$$10[$$11].getLineNumber()));
                }
            }
            $$2.sendFailure(Component.translatable("command.failed").withStyle((UnaryOperator<Style>)((UnaryOperator)$$1 -> $$1.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, $$9)))));
            if (SharedConstants.IS_RUNNING_IN_IDE) {
                $$2.sendFailure(Component.literal(Util.describeError($$8)));
                LOGGER.error("'/{}' threw an exception", (Object)$$12, (Object)$$8);
            }
            int n = 0;
            return n;
        }
        finally {
            $$2.getServer().getProfiler().pop();
        }
    }

    public void sendCommands(ServerPlayer $$0) {
        HashMap $$1 = Maps.newHashMap();
        RootCommandNode $$2 = new RootCommandNode();
        $$1.put((Object)this.dispatcher.getRoot(), (Object)$$2);
        this.fillUsableCommands((CommandNode<CommandSourceStack>)this.dispatcher.getRoot(), (CommandNode<SharedSuggestionProvider>)$$2, $$0.createCommandSourceStack(), (Map<CommandNode<CommandSourceStack>, CommandNode<SharedSuggestionProvider>>)$$1);
        $$0.connection.send(new ClientboundCommandsPacket((RootCommandNode<SharedSuggestionProvider>)$$2));
    }

    private void fillUsableCommands(CommandNode<CommandSourceStack> $$02, CommandNode<SharedSuggestionProvider> $$1, CommandSourceStack $$2, Map<CommandNode<CommandSourceStack>, CommandNode<SharedSuggestionProvider>> $$3) {
        for (CommandNode $$4 : $$02.getChildren()) {
            RequiredArgumentBuilder $$6;
            if (!$$4.canUse((Object)$$2)) continue;
            ArgumentBuilder $$5 = $$4.createBuilder();
            $$5.requires($$0 -> true);
            if ($$5.getCommand() != null) {
                $$5.executes($$0 -> 0);
            }
            if ($$5 instanceof RequiredArgumentBuilder && ($$6 = (RequiredArgumentBuilder)$$5).getSuggestionsProvider() != null) {
                $$6.suggests(SuggestionProviders.safelySwap((SuggestionProvider<SharedSuggestionProvider>)$$6.getSuggestionsProvider()));
            }
            if ($$5.getRedirect() != null) {
                $$5.redirect((CommandNode)$$3.get((Object)$$5.getRedirect()));
            }
            CommandNode $$7 = $$5.build();
            $$3.put((Object)$$4, (Object)$$7);
            $$1.addChild($$7);
            if ($$4.getChildren().isEmpty()) continue;
            this.fillUsableCommands((CommandNode<CommandSourceStack>)$$4, (CommandNode<SharedSuggestionProvider>)$$7, $$2, $$3);
        }
    }

    public static LiteralArgumentBuilder<CommandSourceStack> literal(String $$0) {
        return LiteralArgumentBuilder.literal((String)$$0);
    }

    public static <T> RequiredArgumentBuilder<CommandSourceStack, T> argument(String $$0, ArgumentType<T> $$1) {
        return RequiredArgumentBuilder.argument((String)$$0, $$1);
    }

    public static Predicate<String> createValidator(ParseFunction $$0) {
        return $$1 -> {
            try {
                $$0.parse(new StringReader($$1));
                return true;
            }
            catch (CommandSyntaxException $$2) {
                return false;
            }
        };
    }

    public CommandDispatcher<CommandSourceStack> getDispatcher() {
        return this.dispatcher;
    }

    @Nullable
    public static <S> CommandSyntaxException getParseException(ParseResults<S> $$0) {
        if (!$$0.getReader().canRead()) {
            return null;
        }
        if ($$0.getExceptions().size() == 1) {
            return (CommandSyntaxException)((Object)$$0.getExceptions().values().iterator().next());
        }
        if ($$0.getContext().getRange().isEmpty()) {
            return CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext($$0.getReader());
        }
        return CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext($$0.getReader());
    }

    public static CommandBuildContext createValidationContext(final HolderLookup.Provider $$0) {
        return new CommandBuildContext(){

            @Override
            public <T> HolderLookup<T> holderLookup(ResourceKey<? extends Registry<T>> $$02) {
                final HolderLookup.RegistryLookup $$1 = $$0.lookupOrThrow($$02);
                return new HolderLookup.Delegate<T>($$1){

                    @Override
                    public Optional<HolderSet.Named<T>> get(TagKey<T> $$0) {
                        return Optional.of(this.getOrThrow($$0));
                    }

                    @Override
                    public HolderSet.Named<T> getOrThrow(TagKey<T> $$0) {
                        Optional $$12 = $$1.get($$0);
                        return (HolderSet.Named)$$12.orElseGet(() -> HolderSet.emptyNamed($$1, $$0));
                    }
                };
            }
        };
    }

    public static void validate() {
        CommandBuildContext $$02 = Commands.createValidationContext(VanillaRegistries.createLookup());
        CommandDispatcher<CommandSourceStack> $$12 = new Commands(CommandSelection.ALL, $$02).getDispatcher();
        RootCommandNode $$22 = $$12.getRoot();
        $$12.findAmbiguities(($$1, $$2, $$3, $$4) -> LOGGER.warn("Ambiguity between arguments {} and {} with inputs: {}", new Object[]{$$12.getPath($$2), $$12.getPath($$3), $$4}));
        Set<ArgumentType<?>> $$32 = ArgumentUtils.findUsedArgumentTypes($$22);
        Set $$42 = (Set)$$32.stream().filter($$0 -> !ArgumentTypeInfos.isClassRecognized($$0.getClass())).collect(Collectors.toSet());
        if (!$$42.isEmpty()) {
            LOGGER.warn("Missing type registration for following arguments:\n {}", $$42.stream().map($$0 -> "\t" + $$0).collect(Collectors.joining((CharSequence)",\n")));
            throw new IllegalStateException("Unregistered argument types");
        }
    }

    public static enum CommandSelection {
        ALL(true, true),
        DEDICATED(false, true),
        INTEGRATED(true, false);

        final boolean includeIntegrated;
        final boolean includeDedicated;

        private CommandSelection(boolean $$0, boolean $$1) {
            this.includeIntegrated = $$0;
            this.includeDedicated = $$1;
        }
    }

    @FunctionalInterface
    public static interface ParseFunction {
        public void parse(StringReader var1) throws CommandSyntaxException;
    }
}