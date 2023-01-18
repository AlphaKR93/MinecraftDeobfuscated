/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collection
 *  java.util.stream.Stream
 */
package net.minecraft.server.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.server.players.UserWhiteListEntry;

public class WhitelistCommand {
    private static final SimpleCommandExceptionType ERROR_ALREADY_ENABLED = new SimpleCommandExceptionType((Message)Component.translatable("commands.whitelist.alreadyOn"));
    private static final SimpleCommandExceptionType ERROR_ALREADY_DISABLED = new SimpleCommandExceptionType((Message)Component.translatable("commands.whitelist.alreadyOff"));
    private static final SimpleCommandExceptionType ERROR_ALREADY_WHITELISTED = new SimpleCommandExceptionType((Message)Component.translatable("commands.whitelist.add.failed"));
    private static final SimpleCommandExceptionType ERROR_NOT_WHITELISTED = new SimpleCommandExceptionType((Message)Component.translatable("commands.whitelist.remove.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> $$03) {
        $$03.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("whitelist").requires($$0 -> $$0.hasPermission(3))).then(Commands.literal("on").executes($$0 -> WhitelistCommand.enableWhitelist((CommandSourceStack)$$0.getSource())))).then(Commands.literal("off").executes($$0 -> WhitelistCommand.disableWhitelist((CommandSourceStack)$$0.getSource())))).then(Commands.literal("list").executes($$0 -> WhitelistCommand.showList((CommandSourceStack)$$0.getSource())))).then(Commands.literal("add").then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests(($$02, $$12) -> {
            PlayerList $$2 = ((CommandSourceStack)$$02.getSource()).getServer().getPlayerList();
            return SharedSuggestionProvider.suggest((Stream<String>)$$2.getPlayers().stream().filter($$1 -> !$$2.getWhiteList().isWhiteListed($$1.getGameProfile())).map($$0 -> $$0.getGameProfile().getName()), $$12);
        }).executes($$0 -> WhitelistCommand.addPlayers((CommandSourceStack)$$0.getSource(), GameProfileArgument.getGameProfiles((CommandContext<CommandSourceStack>)$$0, "targets")))))).then(Commands.literal("remove").then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests(($$0, $$1) -> SharedSuggestionProvider.suggest(((CommandSourceStack)$$0.getSource()).getServer().getPlayerList().getWhiteListNames(), $$1)).executes($$0 -> WhitelistCommand.removePlayers((CommandSourceStack)$$0.getSource(), GameProfileArgument.getGameProfiles((CommandContext<CommandSourceStack>)$$0, "targets")))))).then(Commands.literal("reload").executes($$0 -> WhitelistCommand.reload((CommandSourceStack)$$0.getSource()))));
    }

    private static int reload(CommandSourceStack $$0) {
        $$0.getServer().getPlayerList().reloadWhiteList();
        $$0.sendSuccess(Component.translatable("commands.whitelist.reloaded"), true);
        $$0.getServer().kickUnlistedPlayers($$0);
        return 1;
    }

    private static int addPlayers(CommandSourceStack $$0, Collection<GameProfile> $$1) throws CommandSyntaxException {
        UserWhiteList $$2 = $$0.getServer().getPlayerList().getWhiteList();
        int $$3 = 0;
        for (GameProfile $$4 : $$1) {
            if ($$2.isWhiteListed($$4)) continue;
            UserWhiteListEntry $$5 = new UserWhiteListEntry($$4);
            $$2.add($$5);
            $$0.sendSuccess(Component.translatable("commands.whitelist.add.success", ComponentUtils.getDisplayName($$4)), true);
            ++$$3;
        }
        if ($$3 == 0) {
            throw ERROR_ALREADY_WHITELISTED.create();
        }
        return $$3;
    }

    private static int removePlayers(CommandSourceStack $$0, Collection<GameProfile> $$1) throws CommandSyntaxException {
        UserWhiteList $$2 = $$0.getServer().getPlayerList().getWhiteList();
        int $$3 = 0;
        for (GameProfile $$4 : $$1) {
            if (!$$2.isWhiteListed($$4)) continue;
            UserWhiteListEntry $$5 = new UserWhiteListEntry($$4);
            $$2.remove($$5);
            $$0.sendSuccess(Component.translatable("commands.whitelist.remove.success", ComponentUtils.getDisplayName($$4)), true);
            ++$$3;
        }
        if ($$3 == 0) {
            throw ERROR_NOT_WHITELISTED.create();
        }
        $$0.getServer().kickUnlistedPlayers($$0);
        return $$3;
    }

    private static int enableWhitelist(CommandSourceStack $$0) throws CommandSyntaxException {
        PlayerList $$1 = $$0.getServer().getPlayerList();
        if ($$1.isUsingWhitelist()) {
            throw ERROR_ALREADY_ENABLED.create();
        }
        $$1.setUsingWhiteList(true);
        $$0.sendSuccess(Component.translatable("commands.whitelist.enabled"), true);
        $$0.getServer().kickUnlistedPlayers($$0);
        return 1;
    }

    private static int disableWhitelist(CommandSourceStack $$0) throws CommandSyntaxException {
        PlayerList $$1 = $$0.getServer().getPlayerList();
        if (!$$1.isUsingWhitelist()) {
            throw ERROR_ALREADY_DISABLED.create();
        }
        $$1.setUsingWhiteList(false);
        $$0.sendSuccess(Component.translatable("commands.whitelist.disabled"), true);
        return 1;
    }

    private static int showList(CommandSourceStack $$0) {
        CharSequence[] $$1 = $$0.getServer().getPlayerList().getWhiteListNames();
        if ($$1.length == 0) {
            $$0.sendSuccess(Component.translatable("commands.whitelist.none"), false);
        } else {
            $$0.sendSuccess(Component.translatable("commands.whitelist.list", $$1.length, String.join((CharSequence)", ", (CharSequence[])$$1)), false);
        }
        return $$1.length;
    }
}