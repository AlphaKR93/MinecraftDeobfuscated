/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.arguments.StringArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.concurrent.CompletableFuture
 */
package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.ObjectiveCriteriaArgument;
import net.minecraft.commands.arguments.OperationArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.arguments.ScoreboardSlotArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ScoreboardCommand {
    private static final SimpleCommandExceptionType ERROR_OBJECTIVE_ALREADY_EXISTS = new SimpleCommandExceptionType((Message)Component.translatable("commands.scoreboard.objectives.add.duplicate"));
    private static final SimpleCommandExceptionType ERROR_DISPLAY_SLOT_ALREADY_EMPTY = new SimpleCommandExceptionType((Message)Component.translatable("commands.scoreboard.objectives.display.alreadyEmpty"));
    private static final SimpleCommandExceptionType ERROR_DISPLAY_SLOT_ALREADY_SET = new SimpleCommandExceptionType((Message)Component.translatable("commands.scoreboard.objectives.display.alreadySet"));
    private static final SimpleCommandExceptionType ERROR_TRIGGER_ALREADY_ENABLED = new SimpleCommandExceptionType((Message)Component.translatable("commands.scoreboard.players.enable.failed"));
    private static final SimpleCommandExceptionType ERROR_NOT_TRIGGER = new SimpleCommandExceptionType((Message)Component.translatable("commands.scoreboard.players.enable.invalid"));
    private static final Dynamic2CommandExceptionType ERROR_NO_VALUE = new Dynamic2CommandExceptionType(($$0, $$1) -> Component.translatable("commands.scoreboard.players.get.null", $$0, $$1));

    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("scoreboard").requires($$0 -> $$0.hasPermission(2))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("objectives").then(Commands.literal("list").executes($$0 -> ScoreboardCommand.listObjectives((CommandSourceStack)$$0.getSource())))).then(Commands.literal("add").then(Commands.argument("objective", StringArgumentType.word()).then(((RequiredArgumentBuilder)Commands.argument("criteria", ObjectiveCriteriaArgument.criteria()).executes($$0 -> ScoreboardCommand.addObjective((CommandSourceStack)$$0.getSource(), StringArgumentType.getString((CommandContext)$$0, (String)"objective"), ObjectiveCriteriaArgument.getCriteria((CommandContext<CommandSourceStack>)$$0, "criteria"), Component.literal(StringArgumentType.getString((CommandContext)$$0, (String)"objective"))))).then(Commands.argument("displayName", ComponentArgument.textComponent()).executes($$0 -> ScoreboardCommand.addObjective((CommandSourceStack)$$0.getSource(), StringArgumentType.getString((CommandContext)$$0, (String)"objective"), ObjectiveCriteriaArgument.getCriteria((CommandContext<CommandSourceStack>)$$0, "criteria"), ComponentArgument.getComponent((CommandContext<CommandSourceStack>)$$0, "displayName")))))))).then(Commands.literal("modify").then(((RequiredArgumentBuilder)Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.literal("displayname").then(Commands.argument("displayName", ComponentArgument.textComponent()).executes($$0 -> ScoreboardCommand.setDisplayName((CommandSourceStack)$$0.getSource(), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)$$0, "objective"), ComponentArgument.getComponent((CommandContext<CommandSourceStack>)$$0, "displayName")))))).then(ScoreboardCommand.createRenderTypeModify())))).then(Commands.literal("remove").then(Commands.argument("objective", ObjectiveArgument.objective()).executes($$0 -> ScoreboardCommand.removeObjective((CommandSourceStack)$$0.getSource(), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)$$0, "objective")))))).then(Commands.literal("setdisplay").then(((RequiredArgumentBuilder)Commands.argument("slot", ScoreboardSlotArgument.displaySlot()).executes($$0 -> ScoreboardCommand.clearDisplaySlot((CommandSourceStack)$$0.getSource(), ScoreboardSlotArgument.getDisplaySlot((CommandContext<CommandSourceStack>)$$0, "slot")))).then(Commands.argument("objective", ObjectiveArgument.objective()).executes($$0 -> ScoreboardCommand.setDisplaySlot((CommandSourceStack)$$0.getSource(), ScoreboardSlotArgument.getDisplaySlot((CommandContext<CommandSourceStack>)$$0, "slot"), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)$$0, "objective")))))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("players").then(((LiteralArgumentBuilder)Commands.literal("list").executes($$0 -> ScoreboardCommand.listTrackedPlayers((CommandSourceStack)$$0.getSource()))).then(Commands.argument("target", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).executes($$0 -> ScoreboardCommand.listTrackedPlayerScores((CommandSourceStack)$$0.getSource(), ScoreHolderArgument.getName((CommandContext<CommandSourceStack>)$$0, "target")))))).then(Commands.literal("set").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.argument("score", IntegerArgumentType.integer()).executes($$0 -> ScoreboardCommand.setScore((CommandSourceStack)$$0.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)$$0, "targets"), ObjectiveArgument.getWritableObjective((CommandContext<CommandSourceStack>)$$0, "objective"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"score")))))))).then(Commands.literal("get").then(Commands.argument("target", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).executes($$0 -> ScoreboardCommand.getScore((CommandSourceStack)$$0.getSource(), ScoreHolderArgument.getName((CommandContext<CommandSourceStack>)$$0, "target"), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)$$0, "objective"))))))).then(Commands.literal("add").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.argument("score", IntegerArgumentType.integer((int)0)).executes($$0 -> ScoreboardCommand.addScore((CommandSourceStack)$$0.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)$$0, "targets"), ObjectiveArgument.getWritableObjective((CommandContext<CommandSourceStack>)$$0, "objective"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"score")))))))).then(Commands.literal("remove").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.argument("score", IntegerArgumentType.integer((int)0)).executes($$0 -> ScoreboardCommand.removeScore((CommandSourceStack)$$0.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)$$0, "targets"), ObjectiveArgument.getWritableObjective((CommandContext<CommandSourceStack>)$$0, "objective"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"score")))))))).then(Commands.literal("reset").then(((RequiredArgumentBuilder)Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).executes($$0 -> ScoreboardCommand.resetScores((CommandSourceStack)$$0.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)$$0, "targets")))).then(Commands.argument("objective", ObjectiveArgument.objective()).executes($$0 -> ScoreboardCommand.resetScore((CommandSourceStack)$$0.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)$$0, "targets"), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)$$0, "objective"))))))).then(Commands.literal("enable").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).suggests(($$0, $$1) -> ScoreboardCommand.suggestTriggers((CommandSourceStack)$$0.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)$$0, "targets"), $$1)).executes($$0 -> ScoreboardCommand.enableTrigger((CommandSourceStack)$$0.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)$$0, "targets"), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)$$0, "objective"))))))).then(Commands.literal("operation").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("targetObjective", ObjectiveArgument.objective()).then(Commands.argument("operation", OperationArgument.operation()).then(Commands.argument("source", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("sourceObjective", ObjectiveArgument.objective()).executes($$0 -> ScoreboardCommand.performOperation((CommandSourceStack)$$0.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)$$0, "targets"), ObjectiveArgument.getWritableObjective((CommandContext<CommandSourceStack>)$$0, "targetObjective"), OperationArgument.getOperation((CommandContext<CommandSourceStack>)$$0, "operation"), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)$$0, "source"), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)$$0, "sourceObjective")))))))))));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> createRenderTypeModify() {
        LiteralArgumentBuilder<CommandSourceStack> $$0 = Commands.literal("rendertype");
        for (ObjectiveCriteria.RenderType $$12 : ObjectiveCriteria.RenderType.values()) {
            $$0.then(Commands.literal($$12.getId()).executes($$1 -> ScoreboardCommand.setRenderType((CommandSourceStack)$$1.getSource(), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)$$1, "objective"), $$12)));
        }
        return $$0;
    }

    private static CompletableFuture<Suggestions> suggestTriggers(CommandSourceStack $$0, Collection<String> $$1, SuggestionsBuilder $$2) {
        ArrayList $$3 = Lists.newArrayList();
        ServerScoreboard $$4 = $$0.getServer().getScoreboard();
        for (Objective $$5 : $$4.getObjectives()) {
            if ($$5.getCriteria() != ObjectiveCriteria.TRIGGER) continue;
            boolean $$6 = false;
            for (String $$7 : $$1) {
                if ($$4.hasPlayerScore($$7, $$5) && !$$4.getOrCreatePlayerScore($$7, $$5).isLocked()) continue;
                $$6 = true;
                break;
            }
            if (!$$6) continue;
            $$3.add((Object)$$5.getName());
        }
        return SharedSuggestionProvider.suggest((Iterable<String>)$$3, $$2);
    }

    private static int getScore(CommandSourceStack $$0, String $$1, Objective $$2) throws CommandSyntaxException {
        ServerScoreboard $$3 = $$0.getServer().getScoreboard();
        if (!$$3.hasPlayerScore($$1, $$2)) {
            throw ERROR_NO_VALUE.create((Object)$$2.getName(), (Object)$$1);
        }
        Score $$4 = $$3.getOrCreatePlayerScore($$1, $$2);
        $$0.sendSuccess(Component.translatable("commands.scoreboard.players.get.success", $$1, $$4.getScore(), $$2.getFormattedDisplayName()), false);
        return $$4.getScore();
    }

    private static int performOperation(CommandSourceStack $$0, Collection<String> $$1, Objective $$2, OperationArgument.Operation $$3, Collection<String> $$4, Objective $$5) throws CommandSyntaxException {
        ServerScoreboard $$6 = $$0.getServer().getScoreboard();
        int $$7 = 0;
        for (String $$8 : $$1) {
            Score $$9 = $$6.getOrCreatePlayerScore($$8, $$2);
            for (String $$10 : $$4) {
                Score $$11 = $$6.getOrCreatePlayerScore($$10, $$5);
                $$3.apply($$9, $$11);
            }
            $$7 += $$9.getScore();
        }
        if ($$1.size() == 1) {
            $$0.sendSuccess(Component.translatable("commands.scoreboard.players.operation.success.single", $$2.getFormattedDisplayName(), $$1.iterator().next(), $$7), true);
        } else {
            $$0.sendSuccess(Component.translatable("commands.scoreboard.players.operation.success.multiple", $$2.getFormattedDisplayName(), $$1.size()), true);
        }
        return $$7;
    }

    private static int enableTrigger(CommandSourceStack $$0, Collection<String> $$1, Objective $$2) throws CommandSyntaxException {
        if ($$2.getCriteria() != ObjectiveCriteria.TRIGGER) {
            throw ERROR_NOT_TRIGGER.create();
        }
        ServerScoreboard $$3 = $$0.getServer().getScoreboard();
        int $$4 = 0;
        for (String $$5 : $$1) {
            Score $$6 = $$3.getOrCreatePlayerScore($$5, $$2);
            if (!$$6.isLocked()) continue;
            $$6.setLocked(false);
            ++$$4;
        }
        if ($$4 == 0) {
            throw ERROR_TRIGGER_ALREADY_ENABLED.create();
        }
        if ($$1.size() == 1) {
            $$0.sendSuccess(Component.translatable("commands.scoreboard.players.enable.success.single", $$2.getFormattedDisplayName(), $$1.iterator().next()), true);
        } else {
            $$0.sendSuccess(Component.translatable("commands.scoreboard.players.enable.success.multiple", $$2.getFormattedDisplayName(), $$1.size()), true);
        }
        return $$4;
    }

    private static int resetScores(CommandSourceStack $$0, Collection<String> $$1) {
        ServerScoreboard $$2 = $$0.getServer().getScoreboard();
        for (String $$3 : $$1) {
            $$2.resetPlayerScore($$3, null);
        }
        if ($$1.size() == 1) {
            $$0.sendSuccess(Component.translatable("commands.scoreboard.players.reset.all.single", $$1.iterator().next()), true);
        } else {
            $$0.sendSuccess(Component.translatable("commands.scoreboard.players.reset.all.multiple", $$1.size()), true);
        }
        return $$1.size();
    }

    private static int resetScore(CommandSourceStack $$0, Collection<String> $$1, Objective $$2) {
        ServerScoreboard $$3 = $$0.getServer().getScoreboard();
        for (String $$4 : $$1) {
            $$3.resetPlayerScore($$4, $$2);
        }
        if ($$1.size() == 1) {
            $$0.sendSuccess(Component.translatable("commands.scoreboard.players.reset.specific.single", $$2.getFormattedDisplayName(), $$1.iterator().next()), true);
        } else {
            $$0.sendSuccess(Component.translatable("commands.scoreboard.players.reset.specific.multiple", $$2.getFormattedDisplayName(), $$1.size()), true);
        }
        return $$1.size();
    }

    private static int setScore(CommandSourceStack $$0, Collection<String> $$1, Objective $$2, int $$3) {
        ServerScoreboard $$4 = $$0.getServer().getScoreboard();
        for (String $$5 : $$1) {
            Score $$6 = $$4.getOrCreatePlayerScore($$5, $$2);
            $$6.setScore($$3);
        }
        if ($$1.size() == 1) {
            $$0.sendSuccess(Component.translatable("commands.scoreboard.players.set.success.single", $$2.getFormattedDisplayName(), $$1.iterator().next(), $$3), true);
        } else {
            $$0.sendSuccess(Component.translatable("commands.scoreboard.players.set.success.multiple", $$2.getFormattedDisplayName(), $$1.size(), $$3), true);
        }
        return $$3 * $$1.size();
    }

    private static int addScore(CommandSourceStack $$0, Collection<String> $$1, Objective $$2, int $$3) {
        ServerScoreboard $$4 = $$0.getServer().getScoreboard();
        int $$5 = 0;
        for (String $$6 : $$1) {
            Score $$7 = $$4.getOrCreatePlayerScore($$6, $$2);
            $$7.setScore($$7.getScore() + $$3);
            $$5 += $$7.getScore();
        }
        if ($$1.size() == 1) {
            $$0.sendSuccess(Component.translatable("commands.scoreboard.players.add.success.single", $$3, $$2.getFormattedDisplayName(), $$1.iterator().next(), $$5), true);
        } else {
            $$0.sendSuccess(Component.translatable("commands.scoreboard.players.add.success.multiple", $$3, $$2.getFormattedDisplayName(), $$1.size()), true);
        }
        return $$5;
    }

    private static int removeScore(CommandSourceStack $$0, Collection<String> $$1, Objective $$2, int $$3) {
        ServerScoreboard $$4 = $$0.getServer().getScoreboard();
        int $$5 = 0;
        for (String $$6 : $$1) {
            Score $$7 = $$4.getOrCreatePlayerScore($$6, $$2);
            $$7.setScore($$7.getScore() - $$3);
            $$5 += $$7.getScore();
        }
        if ($$1.size() == 1) {
            $$0.sendSuccess(Component.translatable("commands.scoreboard.players.remove.success.single", $$3, $$2.getFormattedDisplayName(), $$1.iterator().next(), $$5), true);
        } else {
            $$0.sendSuccess(Component.translatable("commands.scoreboard.players.remove.success.multiple", $$3, $$2.getFormattedDisplayName(), $$1.size()), true);
        }
        return $$5;
    }

    private static int listTrackedPlayers(CommandSourceStack $$0) {
        Collection<String> $$1 = $$0.getServer().getScoreboard().getTrackedPlayers();
        if ($$1.isEmpty()) {
            $$0.sendSuccess(Component.translatable("commands.scoreboard.players.list.empty"), false);
        } else {
            $$0.sendSuccess(Component.translatable("commands.scoreboard.players.list.success", $$1.size(), ComponentUtils.formatList($$1)), false);
        }
        return $$1.size();
    }

    private static int listTrackedPlayerScores(CommandSourceStack $$0, String $$1) {
        Map<Objective, Score> $$2 = $$0.getServer().getScoreboard().getPlayerScores($$1);
        if ($$2.isEmpty()) {
            $$0.sendSuccess(Component.translatable("commands.scoreboard.players.list.entity.empty", $$1), false);
        } else {
            $$0.sendSuccess(Component.translatable("commands.scoreboard.players.list.entity.success", $$1, $$2.size()), false);
            for (Map.Entry $$3 : $$2.entrySet()) {
                $$0.sendSuccess(Component.translatable("commands.scoreboard.players.list.entity.entry", ((Objective)$$3.getKey()).getFormattedDisplayName(), ((Score)$$3.getValue()).getScore()), false);
            }
        }
        return $$2.size();
    }

    private static int clearDisplaySlot(CommandSourceStack $$0, int $$1) throws CommandSyntaxException {
        ServerScoreboard $$2 = $$0.getServer().getScoreboard();
        if ($$2.getDisplayObjective($$1) == null) {
            throw ERROR_DISPLAY_SLOT_ALREADY_EMPTY.create();
        }
        ((Scoreboard)$$2).setDisplayObjective($$1, null);
        $$0.sendSuccess(Component.translatable("commands.scoreboard.objectives.display.cleared", Scoreboard.getDisplaySlotNames()[$$1]), true);
        return 0;
    }

    private static int setDisplaySlot(CommandSourceStack $$0, int $$1, Objective $$2) throws CommandSyntaxException {
        ServerScoreboard $$3 = $$0.getServer().getScoreboard();
        if ($$3.getDisplayObjective($$1) == $$2) {
            throw ERROR_DISPLAY_SLOT_ALREADY_SET.create();
        }
        ((Scoreboard)$$3).setDisplayObjective($$1, $$2);
        $$0.sendSuccess(Component.translatable("commands.scoreboard.objectives.display.set", Scoreboard.getDisplaySlotNames()[$$1], $$2.getDisplayName()), true);
        return 0;
    }

    private static int setDisplayName(CommandSourceStack $$0, Objective $$1, Component $$2) {
        if (!$$1.getDisplayName().equals($$2)) {
            $$1.setDisplayName($$2);
            $$0.sendSuccess(Component.translatable("commands.scoreboard.objectives.modify.displayname", $$1.getName(), $$1.getFormattedDisplayName()), true);
        }
        return 0;
    }

    private static int setRenderType(CommandSourceStack $$0, Objective $$1, ObjectiveCriteria.RenderType $$2) {
        if ($$1.getRenderType() != $$2) {
            $$1.setRenderType($$2);
            $$0.sendSuccess(Component.translatable("commands.scoreboard.objectives.modify.rendertype", $$1.getFormattedDisplayName()), true);
        }
        return 0;
    }

    private static int removeObjective(CommandSourceStack $$0, Objective $$1) {
        ServerScoreboard $$2 = $$0.getServer().getScoreboard();
        $$2.removeObjective($$1);
        $$0.sendSuccess(Component.translatable("commands.scoreboard.objectives.remove.success", $$1.getFormattedDisplayName()), true);
        return $$2.getObjectives().size();
    }

    private static int addObjective(CommandSourceStack $$0, String $$1, ObjectiveCriteria $$2, Component $$3) throws CommandSyntaxException {
        ServerScoreboard $$4 = $$0.getServer().getScoreboard();
        if ($$4.getObjective($$1) != null) {
            throw ERROR_OBJECTIVE_ALREADY_EXISTS.create();
        }
        $$4.addObjective($$1, $$2, $$3, $$2.getDefaultRenderType());
        Objective $$5 = $$4.getObjective($$1);
        $$0.sendSuccess(Component.translatable("commands.scoreboard.objectives.add.success", $$5.getFormattedDisplayName()), true);
        return $$4.getObjectives().size();
    }

    private static int listObjectives(CommandSourceStack $$0) {
        Collection<Objective> $$1 = $$0.getServer().getScoreboard().getObjectives();
        if ($$1.isEmpty()) {
            $$0.sendSuccess(Component.translatable("commands.scoreboard.objectives.list.empty"), false);
        } else {
            $$0.sendSuccess(Component.translatable("commands.scoreboard.objectives.list.success", $$1.size(), ComponentUtils.formatList($$1, Objective::getFormattedDisplayName)), false);
        }
        return $$1.size();
    }
}