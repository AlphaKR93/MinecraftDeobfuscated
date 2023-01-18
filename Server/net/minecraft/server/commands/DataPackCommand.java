/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.List
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 */
package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.commands.ReloadCommand;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;

public class DataPackCommand {
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_PACK = new DynamicCommandExceptionType($$0 -> Component.translatable("commands.datapack.unknown", $$0));
    private static final DynamicCommandExceptionType ERROR_PACK_ALREADY_ENABLED = new DynamicCommandExceptionType($$0 -> Component.translatable("commands.datapack.enable.failed", $$0));
    private static final DynamicCommandExceptionType ERROR_PACK_ALREADY_DISABLED = new DynamicCommandExceptionType($$0 -> Component.translatable("commands.datapack.disable.failed", $$0));
    private static final Dynamic2CommandExceptionType ERROR_PACK_FEATURES_NOT_ENABLED = new Dynamic2CommandExceptionType(($$0, $$1) -> Component.translatable("commands.datapack.enable.failed.no_flags", $$0, $$1));
    private static final SuggestionProvider<CommandSourceStack> SELECTED_PACKS = ($$0, $$1) -> SharedSuggestionProvider.suggest((Stream<String>)((CommandSourceStack)$$0.getSource()).getServer().getPackRepository().getSelectedIds().stream().map(StringArgumentType::escapeIfRequired), $$1);
    private static final SuggestionProvider<CommandSourceStack> UNSELECTED_PACKS = ($$0, $$12) -> {
        PackRepository $$2 = ((CommandSourceStack)$$0.getSource()).getServer().getPackRepository();
        Collection<String> $$3 = $$2.getSelectedIds();
        FeatureFlagSet $$4 = ((CommandSourceStack)$$0.getSource()).enabledFeatures();
        return SharedSuggestionProvider.suggest((Stream<String>)$$2.getAvailablePacks().stream().filter($$1 -> $$1.getRequestedFeatures().isSubsetOf($$4)).map(Pack::getId).filter($$1 -> !$$3.contains($$1)).map(StringArgumentType::escapeIfRequired), $$12);
    };

    public static void register(CommandDispatcher<CommandSourceStack> $$03) {
        $$03.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("datapack").requires($$0 -> $$0.hasPermission(2))).then(Commands.literal("enable").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("name", StringArgumentType.string()).suggests(UNSELECTED_PACKS).executes($$0 -> DataPackCommand.enablePack((CommandSourceStack)$$0.getSource(), DataPackCommand.getPack((CommandContext<CommandSourceStack>)$$0, "name", true), ($$02, $$1) -> $$1.getDefaultPosition().insert($$02, $$1, $$0 -> $$0, false)))).then(Commands.literal("after").then(Commands.argument("existing", StringArgumentType.string()).suggests(SELECTED_PACKS).executes($$0 -> DataPackCommand.enablePack((CommandSourceStack)$$0.getSource(), DataPackCommand.getPack((CommandContext<CommandSourceStack>)$$0, "name", true), ($$1, $$2) -> $$1.add($$1.indexOf((Object)DataPackCommand.getPack((CommandContext<CommandSourceStack>)$$0, "existing", false)) + 1, (Object)$$2)))))).then(Commands.literal("before").then(Commands.argument("existing", StringArgumentType.string()).suggests(SELECTED_PACKS).executes($$0 -> DataPackCommand.enablePack((CommandSourceStack)$$0.getSource(), DataPackCommand.getPack((CommandContext<CommandSourceStack>)$$0, "name", true), ($$1, $$2) -> $$1.add($$1.indexOf((Object)DataPackCommand.getPack((CommandContext<CommandSourceStack>)$$0, "existing", false)), (Object)$$2)))))).then(Commands.literal("last").executes($$0 -> DataPackCommand.enablePack((CommandSourceStack)$$0.getSource(), DataPackCommand.getPack((CommandContext<CommandSourceStack>)$$0, "name", true), List::add)))).then(Commands.literal("first").executes($$02 -> DataPackCommand.enablePack((CommandSourceStack)$$02.getSource(), DataPackCommand.getPack((CommandContext<CommandSourceStack>)$$02, "name", true), ($$0, $$1) -> $$0.add(0, (Object)$$1))))))).then(Commands.literal("disable").then(Commands.argument("name", StringArgumentType.string()).suggests(SELECTED_PACKS).executes($$0 -> DataPackCommand.disablePack((CommandSourceStack)$$0.getSource(), DataPackCommand.getPack((CommandContext<CommandSourceStack>)$$0, "name", false)))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("list").executes($$0 -> DataPackCommand.listPacks((CommandSourceStack)$$0.getSource()))).then(Commands.literal("available").executes($$0 -> DataPackCommand.listAvailablePacks((CommandSourceStack)$$0.getSource())))).then(Commands.literal("enabled").executes($$0 -> DataPackCommand.listEnabledPacks((CommandSourceStack)$$0.getSource())))));
    }

    private static int enablePack(CommandSourceStack $$0, Pack $$1, Inserter $$2) throws CommandSyntaxException {
        PackRepository $$3 = $$0.getServer().getPackRepository();
        ArrayList $$4 = Lists.newArrayList($$3.getSelectedPacks());
        $$2.apply((List<Pack>)$$4, $$1);
        $$0.sendSuccess(Component.translatable("commands.datapack.modify.enable", $$1.getChatLink(true)), true);
        ReloadCommand.reloadPacks((Collection<String>)((Collection)$$4.stream().map(Pack::getId).collect(Collectors.toList())), $$0);
        return $$4.size();
    }

    private static int disablePack(CommandSourceStack $$0, Pack $$1) {
        PackRepository $$2 = $$0.getServer().getPackRepository();
        ArrayList $$3 = Lists.newArrayList($$2.getSelectedPacks());
        $$3.remove((Object)$$1);
        $$0.sendSuccess(Component.translatable("commands.datapack.modify.disable", $$1.getChatLink(true)), true);
        ReloadCommand.reloadPacks((Collection<String>)((Collection)$$3.stream().map(Pack::getId).collect(Collectors.toList())), $$0);
        return $$3.size();
    }

    private static int listPacks(CommandSourceStack $$0) {
        return DataPackCommand.listEnabledPacks($$0) + DataPackCommand.listAvailablePacks($$0);
    }

    private static int listAvailablePacks(CommandSourceStack $$02) {
        PackRepository $$1 = $$02.getServer().getPackRepository();
        $$1.reload();
        Collection<Pack> $$22 = $$1.getSelectedPacks();
        Collection<Pack> $$3 = $$1.getAvailablePacks();
        FeatureFlagSet $$4 = $$02.enabledFeatures();
        List $$5 = $$3.stream().filter($$2 -> !$$22.contains($$2) && $$2.getRequestedFeatures().isSubsetOf($$4)).toList();
        if ($$5.isEmpty()) {
            $$02.sendSuccess(Component.translatable("commands.datapack.list.available.none"), false);
        } else {
            $$02.sendSuccess(Component.translatable("commands.datapack.list.available.success", $$5.size(), ComponentUtils.formatList($$5, $$0 -> $$0.getChatLink(false))), false);
        }
        return $$5.size();
    }

    private static int listEnabledPacks(CommandSourceStack $$02) {
        PackRepository $$1 = $$02.getServer().getPackRepository();
        $$1.reload();
        Collection<Pack> $$2 = $$1.getSelectedPacks();
        if ($$2.isEmpty()) {
            $$02.sendSuccess(Component.translatable("commands.datapack.list.enabled.none"), false);
        } else {
            $$02.sendSuccess(Component.translatable("commands.datapack.list.enabled.success", $$2.size(), ComponentUtils.formatList($$2, $$0 -> $$0.getChatLink(true))), false);
        }
        return $$2.size();
    }

    private static Pack getPack(CommandContext<CommandSourceStack> $$0, String $$1, boolean $$2) throws CommandSyntaxException {
        String $$3 = StringArgumentType.getString($$0, (String)$$1);
        PackRepository $$4 = ((CommandSourceStack)$$0.getSource()).getServer().getPackRepository();
        Pack $$5 = $$4.getPack($$3);
        if ($$5 == null) {
            throw ERROR_UNKNOWN_PACK.create((Object)$$3);
        }
        boolean $$6 = $$4.getSelectedPacks().contains((Object)$$5);
        if ($$2 && $$6) {
            throw ERROR_PACK_ALREADY_ENABLED.create((Object)$$3);
        }
        if (!$$2 && !$$6) {
            throw ERROR_PACK_ALREADY_DISABLED.create((Object)$$3);
        }
        FeatureFlagSet $$7 = ((CommandSourceStack)$$0.getSource()).enabledFeatures();
        FeatureFlagSet $$8 = $$5.getRequestedFeatures();
        if (!$$8.isSubsetOf($$7)) {
            throw ERROR_PACK_FEATURES_NOT_ENABLED.create((Object)$$3, (Object)FeatureFlags.printMissingFlags($$7, $$8));
        }
        return $$5;
    }

    static interface Inserter {
        public void apply(List<Pack> var1, Pack var2) throws CommandSyntaxException;
    }
}