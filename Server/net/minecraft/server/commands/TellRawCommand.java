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
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

public class TellRawCommand {
    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("tellraw").requires($$0 -> $$0.hasPermission(2))).then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("message", ComponentArgument.textComponent()).executes($$0 -> {
            int $$1 = 0;
            for (ServerPlayer $$2 : EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$0, "targets")) {
                $$2.sendSystemMessage(ComponentUtils.updateForEntity((CommandSourceStack)$$0.getSource(), ComponentArgument.getComponent((CommandContext<CommandSourceStack>)$$0, "message"), (Entity)$$2, 0), false);
                ++$$1;
            }
            return $$1;
        }))));
    }
}