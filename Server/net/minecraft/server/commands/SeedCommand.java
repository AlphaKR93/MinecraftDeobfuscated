/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;

public class SeedCommand {
    public static void register(CommandDispatcher<CommandSourceStack> $$02, boolean $$12) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("seed").requires($$1 -> !$$12 || $$1.hasPermission(2))).executes($$0 -> {
            long $$1 = ((CommandSourceStack)$$0.getSource()).getLevel().getSeed();
            MutableComponent $$2 = ComponentUtils.copyOnClickText(String.valueOf((long)$$1));
            ((CommandSourceStack)$$0.getSource()).sendSuccess(Component.translatable("commands.seed.success", $$2), false);
            return (int)$$1;
        }));
    }
}