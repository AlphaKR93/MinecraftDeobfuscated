/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  java.lang.Object
 *  java.util.Collection
 *  java.util.Collections
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.GameModeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;

public class GameModeCommand {
    public static final int PERMISSION_LEVEL = 2;

    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("gamemode").requires($$0 -> $$0.hasPermission(2))).then(((RequiredArgumentBuilder)Commands.argument("gamemode", GameModeArgument.gameMode()).executes($$0 -> GameModeCommand.setMode((CommandContext<CommandSourceStack>)$$0, (Collection<ServerPlayer>)Collections.singleton((Object)((CommandSourceStack)$$0.getSource()).getPlayerOrException()), GameModeArgument.getGameMode((CommandContext<CommandSourceStack>)$$0, "gamemode")))).then(Commands.argument("target", EntityArgument.players()).executes($$0 -> GameModeCommand.setMode((CommandContext<CommandSourceStack>)$$0, EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$0, "target"), GameModeArgument.getGameMode((CommandContext<CommandSourceStack>)$$0, "gamemode"))))));
    }

    private static void logGamemodeChange(CommandSourceStack $$0, ServerPlayer $$1, GameType $$2) {
        MutableComponent $$3 = Component.translatable("gameMode." + $$2.getName());
        if ($$0.getEntity() == $$1) {
            $$0.sendSuccess(Component.translatable("commands.gamemode.success.self", $$3), true);
        } else {
            if ($$0.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK)) {
                $$1.sendSystemMessage(Component.translatable("gameMode.changed", $$3));
            }
            $$0.sendSuccess(Component.translatable("commands.gamemode.success.other", $$1.getDisplayName(), $$3), true);
        }
    }

    private static int setMode(CommandContext<CommandSourceStack> $$0, Collection<ServerPlayer> $$1, GameType $$2) {
        int $$3 = 0;
        for (ServerPlayer $$4 : $$1) {
            if (!$$4.setGameMode($$2)) continue;
            GameModeCommand.logGamemodeChange((CommandSourceStack)$$0.getSource(), $$4, $$2);
            ++$$3;
        }
        return $$3;
    }
}