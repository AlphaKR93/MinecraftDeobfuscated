/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  java.lang.Object
 *  javax.annotation.Nullable
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;

public class SpectateCommand {
    private static final SimpleCommandExceptionType ERROR_SELF = new SimpleCommandExceptionType((Message)Component.translatable("commands.spectate.self"));
    private static final DynamicCommandExceptionType ERROR_NOT_SPECTATOR = new DynamicCommandExceptionType($$0 -> Component.translatable("commands.spectate.not_spectator", $$0));

    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("spectate").requires($$0 -> $$0.hasPermission(2))).executes($$0 -> SpectateCommand.spectate((CommandSourceStack)$$0.getSource(), null, ((CommandSourceStack)$$0.getSource()).getPlayerOrException()))).then(((RequiredArgumentBuilder)Commands.argument("target", EntityArgument.entity()).executes($$0 -> SpectateCommand.spectate((CommandSourceStack)$$0.getSource(), EntityArgument.getEntity((CommandContext<CommandSourceStack>)$$0, "target"), ((CommandSourceStack)$$0.getSource()).getPlayerOrException()))).then(Commands.argument("player", EntityArgument.player()).executes($$0 -> SpectateCommand.spectate((CommandSourceStack)$$0.getSource(), EntityArgument.getEntity((CommandContext<CommandSourceStack>)$$0, "target"), EntityArgument.getPlayer((CommandContext<CommandSourceStack>)$$0, "player"))))));
    }

    private static int spectate(CommandSourceStack $$0, @Nullable Entity $$1, ServerPlayer $$2) throws CommandSyntaxException {
        if ($$2 == $$1) {
            throw ERROR_SELF.create();
        }
        if ($$2.gameMode.getGameModeForPlayer() != GameType.SPECTATOR) {
            throw ERROR_NOT_SPECTATOR.create((Object)$$2.getDisplayName());
        }
        $$2.setCamera($$1);
        if ($$1 != null) {
            $$0.sendSuccess(Component.translatable("commands.spectate.success.started", $$1.getDisplayName()), false);
        } else {
            $$0.sendSuccess(Component.translatable("commands.spectate.success.stopped"), false);
        }
        return 1;
    }
}