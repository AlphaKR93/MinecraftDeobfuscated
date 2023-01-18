/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  java.util.regex.Matcher
 *  java.util.regex.Pattern
 *  javax.annotation.Nullable
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.IpBanList;
import net.minecraft.server.players.IpBanListEntry;

public class BanIpCommands {
    public static final Pattern IP_ADDRESS_PATTERN = Pattern.compile((String)"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    private static final SimpleCommandExceptionType ERROR_INVALID_IP = new SimpleCommandExceptionType((Message)Component.translatable("commands.banip.invalid"));
    private static final SimpleCommandExceptionType ERROR_ALREADY_BANNED = new SimpleCommandExceptionType((Message)Component.translatable("commands.banip.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("ban-ip").requires($$0 -> $$0.hasPermission(3))).then(((RequiredArgumentBuilder)Commands.argument("target", StringArgumentType.word()).executes($$0 -> BanIpCommands.banIpOrName((CommandSourceStack)$$0.getSource(), StringArgumentType.getString((CommandContext)$$0, (String)"target"), null))).then(Commands.argument("reason", MessageArgument.message()).executes($$0 -> BanIpCommands.banIpOrName((CommandSourceStack)$$0.getSource(), StringArgumentType.getString((CommandContext)$$0, (String)"target"), MessageArgument.getMessage((CommandContext<CommandSourceStack>)$$0, "reason"))))));
    }

    private static int banIpOrName(CommandSourceStack $$0, String $$1, @Nullable Component $$2) throws CommandSyntaxException {
        Matcher $$3 = IP_ADDRESS_PATTERN.matcher((CharSequence)$$1);
        if ($$3.matches()) {
            return BanIpCommands.banIp($$0, $$1, $$2);
        }
        ServerPlayer $$4 = $$0.getServer().getPlayerList().getPlayerByName($$1);
        if ($$4 != null) {
            return BanIpCommands.banIp($$0, $$4.getIpAddress(), $$2);
        }
        throw ERROR_INVALID_IP.create();
    }

    private static int banIp(CommandSourceStack $$0, String $$1, @Nullable Component $$2) throws CommandSyntaxException {
        IpBanList $$3 = $$0.getServer().getPlayerList().getIpBans();
        if ($$3.isBanned($$1)) {
            throw ERROR_ALREADY_BANNED.create();
        }
        List<ServerPlayer> $$4 = $$0.getServer().getPlayerList().getPlayersWithAddress($$1);
        IpBanListEntry $$5 = new IpBanListEntry($$1, null, $$0.getTextName(), null, $$2 == null ? null : $$2.getString());
        $$3.add($$5);
        $$0.sendSuccess(Component.translatable("commands.banip.success", $$1, $$5.getReason()), true);
        if (!$$4.isEmpty()) {
            $$0.sendSuccess(Component.translatable("commands.banip.info", $$4.size(), EntitySelector.joinNames($$4)), true);
        }
        for (ServerPlayer $$6 : $$4) {
            $$6.connection.disconnect(Component.translatable("multiplayer.disconnect.ip_banned"));
        }
        return $$4.size();
    }
}