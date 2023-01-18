/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  java.lang.Object
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameModeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class DefaultGameModeCommands {
    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("defaultgamemode").requires($$0 -> $$0.hasPermission(2))).then(Commands.argument("gamemode", GameModeArgument.gameMode()).executes($$0 -> DefaultGameModeCommands.setMode((CommandSourceStack)$$0.getSource(), GameModeArgument.getGameMode((CommandContext<CommandSourceStack>)$$0, "gamemode")))));
    }

    private static int setMode(CommandSourceStack $$0, GameType $$1) {
        int $$2 = 0;
        MinecraftServer $$3 = $$0.getServer();
        $$3.setDefaultGameType($$1);
        GameType $$4 = $$3.getForcedGameType();
        if ($$4 != null) {
            for (ServerPlayer $$5 : $$3.getPlayerList().getPlayers()) {
                if (!$$5.setGameMode($$4)) continue;
                ++$$2;
            }
        }
        $$0.sendSuccess(Component.translatable("commands.defaultgamemode.success", $$1.getLongDisplayName()), true);
        return $$2;
    }
}