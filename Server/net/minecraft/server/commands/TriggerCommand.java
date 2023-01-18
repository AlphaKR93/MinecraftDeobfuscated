/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.concurrent.CompletableFuture
 */
package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class TriggerCommand {
    private static final SimpleCommandExceptionType ERROR_NOT_PRIMED = new SimpleCommandExceptionType((Message)Component.translatable("commands.trigger.failed.unprimed"));
    private static final SimpleCommandExceptionType ERROR_INVALID_OBJECTIVE = new SimpleCommandExceptionType((Message)Component.translatable("commands.trigger.failed.invalid"));

    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        $$02.register((LiteralArgumentBuilder)Commands.literal("trigger").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("objective", ObjectiveArgument.objective()).suggests(($$0, $$1) -> TriggerCommand.suggestObjectives((CommandSourceStack)$$0.getSource(), $$1)).executes($$0 -> TriggerCommand.simpleTrigger((CommandSourceStack)$$0.getSource(), TriggerCommand.getScore(((CommandSourceStack)$$0.getSource()).getPlayerOrException(), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)$$0, "objective"))))).then(Commands.literal("add").then(Commands.argument("value", IntegerArgumentType.integer()).executes($$0 -> TriggerCommand.addValue((CommandSourceStack)$$0.getSource(), TriggerCommand.getScore(((CommandSourceStack)$$0.getSource()).getPlayerOrException(), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)$$0, "objective")), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"value")))))).then(Commands.literal("set").then(Commands.argument("value", IntegerArgumentType.integer()).executes($$0 -> TriggerCommand.setValue((CommandSourceStack)$$0.getSource(), TriggerCommand.getScore(((CommandSourceStack)$$0.getSource()).getPlayerOrException(), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)$$0, "objective")), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"value")))))));
    }

    public static CompletableFuture<Suggestions> suggestObjectives(CommandSourceStack $$0, SuggestionsBuilder $$1) {
        Entity $$2 = $$0.getEntity();
        ArrayList $$3 = Lists.newArrayList();
        if ($$2 != null) {
            ServerScoreboard $$4 = $$0.getServer().getScoreboard();
            String $$5 = $$2.getScoreboardName();
            for (Objective $$6 : $$4.getObjectives()) {
                Score $$7;
                if ($$6.getCriteria() != ObjectiveCriteria.TRIGGER || !$$4.hasPlayerScore($$5, $$6) || ($$7 = $$4.getOrCreatePlayerScore($$5, $$6)).isLocked()) continue;
                $$3.add((Object)$$6.getName());
            }
        }
        return SharedSuggestionProvider.suggest((Iterable<String>)$$3, $$1);
    }

    private static int addValue(CommandSourceStack $$0, Score $$1, int $$2) {
        $$1.add($$2);
        $$0.sendSuccess(Component.translatable("commands.trigger.add.success", $$1.getObjective().getFormattedDisplayName(), $$2), true);
        return $$1.getScore();
    }

    private static int setValue(CommandSourceStack $$0, Score $$1, int $$2) {
        $$1.setScore($$2);
        $$0.sendSuccess(Component.translatable("commands.trigger.set.success", $$1.getObjective().getFormattedDisplayName(), $$2), true);
        return $$2;
    }

    private static int simpleTrigger(CommandSourceStack $$0, Score $$1) {
        $$1.add(1);
        $$0.sendSuccess(Component.translatable("commands.trigger.simple.success", $$1.getObjective().getFormattedDisplayName()), true);
        return $$1.getScore();
    }

    private static Score getScore(ServerPlayer $$0, Objective $$1) throws CommandSyntaxException {
        String $$3;
        if ($$1.getCriteria() != ObjectiveCriteria.TRIGGER) {
            throw ERROR_INVALID_OBJECTIVE.create();
        }
        Scoreboard $$2 = $$0.getScoreboard();
        if (!$$2.hasPlayerScore($$3 = $$0.getScoreboardName(), $$1)) {
            throw ERROR_NOT_PRIMED.create();
        }
        Score $$4 = $$2.getOrCreatePlayerScore($$3, $$1);
        if ($$4.isLocked()) {
            throw ERROR_NOT_PRIMED.create();
        }
        $$4.setLocked(true);
        return $$4;
    }
}