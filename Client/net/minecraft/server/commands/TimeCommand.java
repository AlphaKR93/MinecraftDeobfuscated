/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;

public class TimeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("time").requires($$0 -> $$0.hasPermission(2))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("set").then(Commands.literal("day").executes($$0 -> TimeCommand.setTime((CommandSourceStack)$$0.getSource(), 1000)))).then(Commands.literal("noon").executes($$0 -> TimeCommand.setTime((CommandSourceStack)$$0.getSource(), 6000)))).then(Commands.literal("night").executes($$0 -> TimeCommand.setTime((CommandSourceStack)$$0.getSource(), 13000)))).then(Commands.literal("midnight").executes($$0 -> TimeCommand.setTime((CommandSourceStack)$$0.getSource(), 18000)))).then(Commands.argument("time", TimeArgument.time()).executes($$0 -> TimeCommand.setTime((CommandSourceStack)$$0.getSource(), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"time")))))).then(Commands.literal("add").then(Commands.argument("time", TimeArgument.time()).executes($$0 -> TimeCommand.addTime((CommandSourceStack)$$0.getSource(), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"time")))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("query").then(Commands.literal("daytime").executes($$0 -> TimeCommand.queryTime((CommandSourceStack)$$0.getSource(), TimeCommand.getDayTime(((CommandSourceStack)$$0.getSource()).getLevel()))))).then(Commands.literal("gametime").executes($$0 -> TimeCommand.queryTime((CommandSourceStack)$$0.getSource(), (int)(((CommandSourceStack)$$0.getSource()).getLevel().getGameTime() % Integer.MAX_VALUE))))).then(Commands.literal("day").executes($$0 -> TimeCommand.queryTime((CommandSourceStack)$$0.getSource(), (int)(((CommandSourceStack)$$0.getSource()).getLevel().getDayTime() / 24000L % Integer.MAX_VALUE))))));
    }

    private static int getDayTime(ServerLevel $$0) {
        return (int)($$0.getDayTime() % 24000L);
    }

    private static int queryTime(CommandSourceStack $$0, int $$1) {
        $$0.sendSuccess(Component.translatable("commands.time.query", $$1), false);
        return $$1;
    }

    public static int setTime(CommandSourceStack $$0, int $$1) {
        for (ServerLevel $$2 : $$0.getServer().getAllLevels()) {
            $$2.setDayTime($$1);
        }
        $$0.sendSuccess(Component.translatable("commands.time.set", $$1), true);
        return TimeCommand.getDayTime($$0.getLevel());
    }

    public static int addTime(CommandSourceStack $$0, int $$1) {
        for (ServerLevel $$2 : $$0.getServer().getAllLevels()) {
            $$2.setDayTime($$2.getDayTime() + (long)$$1);
        }
        int $$3 = TimeCommand.getDayTime($$0.getLevel());
        $$0.sendSuccess(Component.translatable("commands.time.set", $$3), true);
        return $$3;
    }
}