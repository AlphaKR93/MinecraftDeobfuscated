/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  java.lang.Object
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class StopCommand {
    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("stop").requires($$0 -> $$0.hasPermission(4))).executes($$0 -> {
            ((CommandSourceStack)$$0.getSource()).sendSuccess(Component.translatable("commands.stop.stopping"), true);
            ((CommandSourceStack)$$0.getSource()).getServer().halt(false);
            return 1;
        }));
    }
}