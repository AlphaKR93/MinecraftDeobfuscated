/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
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
import net.minecraft.network.chat.Component;

public class WeatherCommand {
    private static final int DEFAULT_TIME = 6000;

    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("weather").requires($$0 -> $$0.hasPermission(2))).then(((LiteralArgumentBuilder)Commands.literal("clear").executes($$0 -> WeatherCommand.setClear((CommandSourceStack)$$0.getSource(), 6000))).then(Commands.argument("duration", IntegerArgumentType.integer((int)0, (int)1000000)).executes($$0 -> WeatherCommand.setClear((CommandSourceStack)$$0.getSource(), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"duration") * 20))))).then(((LiteralArgumentBuilder)Commands.literal("rain").executes($$0 -> WeatherCommand.setRain((CommandSourceStack)$$0.getSource(), 6000))).then(Commands.argument("duration", IntegerArgumentType.integer((int)0, (int)1000000)).executes($$0 -> WeatherCommand.setRain((CommandSourceStack)$$0.getSource(), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"duration") * 20))))).then(((LiteralArgumentBuilder)Commands.literal("thunder").executes($$0 -> WeatherCommand.setThunder((CommandSourceStack)$$0.getSource(), 6000))).then(Commands.argument("duration", IntegerArgumentType.integer((int)0, (int)1000000)).executes($$0 -> WeatherCommand.setThunder((CommandSourceStack)$$0.getSource(), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"duration") * 20)))));
    }

    private static int setClear(CommandSourceStack $$0, int $$1) {
        $$0.getLevel().setWeatherParameters($$1, 0, false, false);
        $$0.sendSuccess(Component.translatable("commands.weather.set.clear"), true);
        return $$1;
    }

    private static int setRain(CommandSourceStack $$0, int $$1) {
        $$0.getLevel().setWeatherParameters(0, $$1, true, false);
        $$0.sendSuccess(Component.translatable("commands.weather.set.rain"), true);
        return $$1;
    }

    private static int setThunder(CommandSourceStack $$0, int $$1) {
        $$0.getLevel().setWeatherParameters(0, $$1, true, true);
        $$0.sendSuccess(Component.translatable("commands.weather.set.thunder"), true);
        return $$1;
    }
}