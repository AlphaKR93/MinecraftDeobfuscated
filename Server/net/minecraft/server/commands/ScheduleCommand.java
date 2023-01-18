/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collection
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.TimeArgument;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.commands.FunctionCommand;
import net.minecraft.world.level.timers.FunctionCallback;
import net.minecraft.world.level.timers.FunctionTagCallback;
import net.minecraft.world.level.timers.TimerQueue;

public class ScheduleCommand {
    private static final SimpleCommandExceptionType ERROR_SAME_TICK = new SimpleCommandExceptionType((Message)Component.translatable("commands.schedule.same_tick"));
    private static final DynamicCommandExceptionType ERROR_CANT_REMOVE = new DynamicCommandExceptionType($$0 -> Component.translatable("commands.schedule.cleared.failure", $$0));
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_SCHEDULE = ($$0, $$1) -> SharedSuggestionProvider.suggest(((CommandSourceStack)$$0.getSource()).getServer().getWorldData().overworldData().getScheduledEvents().getEventsIds(), $$1);

    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("schedule").requires($$0 -> $$0.hasPermission(2))).then(Commands.literal("function").then(Commands.argument("function", FunctionArgument.functions()).suggests(FunctionCommand.SUGGEST_FUNCTION).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("time", TimeArgument.time()).executes($$0 -> ScheduleCommand.schedule((CommandSourceStack)$$0.getSource(), FunctionArgument.getFunctionOrTag((CommandContext<CommandSourceStack>)$$0, "function"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"time"), true))).then(Commands.literal("append").executes($$0 -> ScheduleCommand.schedule((CommandSourceStack)$$0.getSource(), FunctionArgument.getFunctionOrTag((CommandContext<CommandSourceStack>)$$0, "function"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"time"), false)))).then(Commands.literal("replace").executes($$0 -> ScheduleCommand.schedule((CommandSourceStack)$$0.getSource(), FunctionArgument.getFunctionOrTag((CommandContext<CommandSourceStack>)$$0, "function"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"time"), true))))))).then(Commands.literal("clear").then(Commands.argument("function", StringArgumentType.greedyString()).suggests(SUGGEST_SCHEDULE).executes($$0 -> ScheduleCommand.remove((CommandSourceStack)$$0.getSource(), StringArgumentType.getString((CommandContext)$$0, (String)"function"))))));
    }

    private static int schedule(CommandSourceStack $$0, Pair<ResourceLocation, Either<CommandFunction, Collection<CommandFunction>>> $$1, int $$2, boolean $$3) throws CommandSyntaxException {
        if ($$2 == 0) {
            throw ERROR_SAME_TICK.create();
        }
        long $$4 = $$0.getLevel().getGameTime() + (long)$$2;
        ResourceLocation $$5 = (ResourceLocation)$$1.getFirst();
        TimerQueue<MinecraftServer> $$62 = $$0.getServer().getWorldData().overworldData().getScheduledEvents();
        ((Either)$$1.getSecond()).ifLeft($$6 -> {
            String $$7 = $$5.toString();
            if ($$3) {
                $$62.remove($$7);
            }
            $$62.schedule($$7, $$4, new FunctionCallback($$5));
            $$0.sendSuccess(Component.translatable("commands.schedule.created.function", $$5, $$2, $$4), true);
        }).ifRight($$6 -> {
            String $$7 = "#" + $$5;
            if ($$3) {
                $$62.remove($$7);
            }
            $$62.schedule($$7, $$4, new FunctionTagCallback($$5));
            $$0.sendSuccess(Component.translatable("commands.schedule.created.tag", $$5, $$2, $$4), true);
        });
        return Math.floorMod((long)$$4, (int)Integer.MAX_VALUE);
    }

    private static int remove(CommandSourceStack $$0, String $$1) throws CommandSyntaxException {
        int $$2 = $$0.getServer().getWorldData().overworldData().getScheduledEvents().remove($$1);
        if ($$2 == 0) {
            throw ERROR_CANT_REMOVE.create((Object)$$1);
        }
        $$0.sendSuccess(Component.translatable("commands.schedule.cleared.success", $$2, $$1), true);
        return $$2;
    }
}