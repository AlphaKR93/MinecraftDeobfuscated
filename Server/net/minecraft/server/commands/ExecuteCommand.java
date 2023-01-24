/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.Command
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.RedirectModifier
 *  com.mojang.brigadier.ResultConsumer
 *  com.mojang.brigadier.arguments.DoubleArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  java.lang.FunctionalInterface
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.List
 *  java.util.Optional
 *  java.util.OptionalInt
 *  java.util.function.BiPredicate
 *  java.util.function.BinaryOperator
 *  java.util.function.Function
 *  java.util.function.IntFunction
 *  java.util.stream.Stream
 */
package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.RedirectModifier;
import com.mojang.brigadier.ResultConsumer;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.RangeArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.RotationArgument;
import net.minecraft.commands.arguments.coordinates.SwizzleArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.commands.BossBarCommands;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.DataCommands;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;

public class ExecuteCommand {
    private static final int MAX_TEST_AREA = 32768;
    private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType(($$0, $$1) -> Component.translatable("commands.execute.blocks.toobig", $$0, $$1));
    private static final SimpleCommandExceptionType ERROR_CONDITIONAL_FAILED = new SimpleCommandExceptionType((Message)Component.translatable("commands.execute.conditional.fail"));
    private static final DynamicCommandExceptionType ERROR_CONDITIONAL_FAILED_COUNT = new DynamicCommandExceptionType($$0 -> Component.translatable("commands.execute.conditional.fail_count", $$0));
    private static final BinaryOperator<ResultConsumer<CommandSourceStack>> CALLBACK_CHAINER = ($$0, $$1) -> ($$2, $$3, $$4) -> {
        $$0.onCommandComplete($$2, $$3, $$4);
        $$1.onCommandComplete($$2, $$3, $$4);
    };
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_PREDICATE = ($$0, $$1) -> {
        PredicateManager $$2 = ((CommandSourceStack)$$0.getSource()).getServer().getPredicateManager();
        return SharedSuggestionProvider.suggestResource($$2.getKeys(), $$1);
    };

    public static void register(CommandDispatcher<CommandSourceStack> $$02, CommandBuildContext $$1) {
        LiteralCommandNode $$2 = $$02.register((LiteralArgumentBuilder)Commands.literal("execute").requires($$0 -> $$0.hasPermission(2)));
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("execute").requires($$0 -> $$0.hasPermission(2))).then(Commands.literal("run").redirect((CommandNode)$$02.getRoot()))).then(ExecuteCommand.addConditionals((CommandNode<CommandSourceStack>)$$2, Commands.literal("if"), true, $$1))).then(ExecuteCommand.addConditionals((CommandNode<CommandSourceStack>)$$2, Commands.literal("unless"), false, $$1))).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork((CommandNode)$$2, $$0 -> {
            ArrayList $$1 = Lists.newArrayList();
            for (Entity $$2 : EntityArgument.getOptionalEntities((CommandContext<CommandSourceStack>)$$0, "targets")) {
                $$1.add((Object)((CommandSourceStack)$$0.getSource()).withEntity($$2));
            }
            return $$1;
        })))).then(Commands.literal("at").then(Commands.argument("targets", EntityArgument.entities()).fork((CommandNode)$$2, $$0 -> {
            ArrayList $$1 = Lists.newArrayList();
            for (Entity $$2 : EntityArgument.getOptionalEntities((CommandContext<CommandSourceStack>)$$0, "targets")) {
                $$1.add((Object)((CommandSourceStack)$$0.getSource()).withLevel((ServerLevel)$$2.level).withPosition($$2.position()).withRotation($$2.getRotationVector()));
            }
            return $$1;
        })))).then(((LiteralArgumentBuilder)Commands.literal("store").then(ExecuteCommand.wrapStores((LiteralCommandNode<CommandSourceStack>)$$2, Commands.literal("result"), true))).then(ExecuteCommand.wrapStores((LiteralCommandNode<CommandSourceStack>)$$2, Commands.literal("success"), false)))).then(((LiteralArgumentBuilder)Commands.literal("positioned").then(Commands.argument("pos", Vec3Argument.vec3()).redirect((CommandNode)$$2, $$0 -> ((CommandSourceStack)$$0.getSource()).withPosition(Vec3Argument.getVec3((CommandContext<CommandSourceStack>)$$0, "pos")).withAnchor(EntityAnchorArgument.Anchor.FEET)))).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork((CommandNode)$$2, $$0 -> {
            ArrayList $$1 = Lists.newArrayList();
            for (Entity $$2 : EntityArgument.getOptionalEntities((CommandContext<CommandSourceStack>)$$0, "targets")) {
                $$1.add((Object)((CommandSourceStack)$$0.getSource()).withPosition($$2.position()));
            }
            return $$1;
        }))))).then(((LiteralArgumentBuilder)Commands.literal("rotated").then(Commands.argument("rot", RotationArgument.rotation()).redirect((CommandNode)$$2, $$0 -> ((CommandSourceStack)$$0.getSource()).withRotation(RotationArgument.getRotation((CommandContext<CommandSourceStack>)$$0, "rot").getRotation((CommandSourceStack)$$0.getSource()))))).then(Commands.literal("as").then(Commands.argument("targets", EntityArgument.entities()).fork((CommandNode)$$2, $$0 -> {
            ArrayList $$1 = Lists.newArrayList();
            for (Entity $$2 : EntityArgument.getOptionalEntities((CommandContext<CommandSourceStack>)$$0, "targets")) {
                $$1.add((Object)((CommandSourceStack)$$0.getSource()).withRotation($$2.getRotationVector()));
            }
            return $$1;
        }))))).then(((LiteralArgumentBuilder)Commands.literal("facing").then(Commands.literal("entity").then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("anchor", EntityAnchorArgument.anchor()).fork((CommandNode)$$2, $$0 -> {
            ArrayList $$1 = Lists.newArrayList();
            EntityAnchorArgument.Anchor $$2 = EntityAnchorArgument.getAnchor((CommandContext<CommandSourceStack>)$$0, "anchor");
            for (Entity $$3 : EntityArgument.getOptionalEntities((CommandContext<CommandSourceStack>)$$0, "targets")) {
                $$1.add((Object)((CommandSourceStack)$$0.getSource()).facing($$3, $$2));
            }
            return $$1;
        }))))).then(Commands.argument("pos", Vec3Argument.vec3()).redirect((CommandNode)$$2, $$0 -> ((CommandSourceStack)$$0.getSource()).facing(Vec3Argument.getVec3((CommandContext<CommandSourceStack>)$$0, "pos")))))).then(Commands.literal("align").then(Commands.argument("axes", SwizzleArgument.swizzle()).redirect((CommandNode)$$2, $$0 -> ((CommandSourceStack)$$0.getSource()).withPosition(((CommandSourceStack)$$0.getSource()).getPosition().align(SwizzleArgument.getSwizzle((CommandContext<CommandSourceStack>)$$0, "axes"))))))).then(Commands.literal("anchored").then(Commands.argument("anchor", EntityAnchorArgument.anchor()).redirect((CommandNode)$$2, $$0 -> ((CommandSourceStack)$$0.getSource()).withAnchor(EntityAnchorArgument.getAnchor((CommandContext<CommandSourceStack>)$$0, "anchor")))))).then(Commands.literal("in").then(Commands.argument("dimension", DimensionArgument.dimension()).redirect((CommandNode)$$2, $$0 -> ((CommandSourceStack)$$0.getSource()).withLevel(DimensionArgument.getDimension((CommandContext<CommandSourceStack>)$$0, "dimension")))))).then(ExecuteCommand.createRelationOperations((CommandNode<CommandSourceStack>)$$2, Commands.literal("on"))));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> wrapStores(LiteralCommandNode<CommandSourceStack> $$0, LiteralArgumentBuilder<CommandSourceStack> $$12, boolean $$2) {
        $$12.then(Commands.literal("score").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).redirect($$0, $$1 -> ExecuteCommand.storeValue((CommandSourceStack)$$1.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard((CommandContext<CommandSourceStack>)$$1, "targets"), ObjectiveArgument.getObjective((CommandContext<CommandSourceStack>)$$1, "objective"), $$2)))));
        $$12.then(Commands.literal("bossbar").then(((RequiredArgumentBuilder)Commands.argument("id", ResourceLocationArgument.id()).suggests(BossBarCommands.SUGGEST_BOSS_BAR).then(Commands.literal("value").redirect($$0, $$1 -> ExecuteCommand.storeValue((CommandSourceStack)$$1.getSource(), BossBarCommands.getBossBar((CommandContext<CommandSourceStack>)$$1), true, $$2)))).then(Commands.literal("max").redirect($$0, $$1 -> ExecuteCommand.storeValue((CommandSourceStack)$$1.getSource(), BossBarCommands.getBossBar((CommandContext<CommandSourceStack>)$$1), false, $$2)))));
        for (DataCommands.DataProvider $$32 : DataCommands.TARGET_PROVIDERS) {
            $$32.wrap((ArgumentBuilder<CommandSourceStack, ?>)$$12, (Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>>)((Function)$$3 -> $$3.then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("path", NbtPathArgument.nbtPath()).then(Commands.literal("int").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect((CommandNode)$$0, $$2 -> ExecuteCommand.storeData((CommandSourceStack)$$2.getSource(), $$32.access((CommandContext<CommandSourceStack>)$$2), NbtPathArgument.getPath((CommandContext<CommandSourceStack>)$$2, "path"), (IntFunction<Tag>)((IntFunction)$$1 -> IntTag.valueOf((int)((double)$$1 * DoubleArgumentType.getDouble((CommandContext)$$2, (String)"scale")))), $$2))))).then(Commands.literal("float").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect((CommandNode)$$0, $$2 -> ExecuteCommand.storeData((CommandSourceStack)$$2.getSource(), $$32.access((CommandContext<CommandSourceStack>)$$2), NbtPathArgument.getPath((CommandContext<CommandSourceStack>)$$2, "path"), (IntFunction<Tag>)((IntFunction)$$1 -> FloatTag.valueOf((float)((double)$$1 * DoubleArgumentType.getDouble((CommandContext)$$2, (String)"scale")))), $$2))))).then(Commands.literal("short").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect((CommandNode)$$0, $$2 -> ExecuteCommand.storeData((CommandSourceStack)$$2.getSource(), $$32.access((CommandContext<CommandSourceStack>)$$2), NbtPathArgument.getPath((CommandContext<CommandSourceStack>)$$2, "path"), (IntFunction<Tag>)((IntFunction)$$1 -> ShortTag.valueOf((short)((double)$$1 * DoubleArgumentType.getDouble((CommandContext)$$2, (String)"scale")))), $$2))))).then(Commands.literal("long").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect((CommandNode)$$0, $$2 -> ExecuteCommand.storeData((CommandSourceStack)$$2.getSource(), $$32.access((CommandContext<CommandSourceStack>)$$2), NbtPathArgument.getPath((CommandContext<CommandSourceStack>)$$2, "path"), (IntFunction<Tag>)((IntFunction)$$1 -> LongTag.valueOf((long)((double)$$1 * DoubleArgumentType.getDouble((CommandContext)$$2, (String)"scale")))), $$2))))).then(Commands.literal("double").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect((CommandNode)$$0, $$2 -> ExecuteCommand.storeData((CommandSourceStack)$$2.getSource(), $$32.access((CommandContext<CommandSourceStack>)$$2), NbtPathArgument.getPath((CommandContext<CommandSourceStack>)$$2, "path"), (IntFunction<Tag>)((IntFunction)$$1 -> DoubleTag.valueOf((double)$$1 * DoubleArgumentType.getDouble((CommandContext)$$2, (String)"scale"))), $$2))))).then(Commands.literal("byte").then(Commands.argument("scale", DoubleArgumentType.doubleArg()).redirect((CommandNode)$$0, $$2 -> ExecuteCommand.storeData((CommandSourceStack)$$2.getSource(), $$32.access((CommandContext<CommandSourceStack>)$$2), NbtPathArgument.getPath((CommandContext<CommandSourceStack>)$$2, "path"), (IntFunction<Tag>)((IntFunction)$$1 -> ByteTag.valueOf((byte)((double)$$1 * DoubleArgumentType.getDouble((CommandContext)$$2, (String)"scale")))), $$2)))))));
        }
        return $$12;
    }

    private static CommandSourceStack storeValue(CommandSourceStack $$0, Collection<String> $$1, Objective $$2, boolean $$3) {
        ServerScoreboard $$42 = $$0.getServer().getScoreboard();
        return $$0.withCallback((ResultConsumer<CommandSourceStack>)((ResultConsumer)($$4, $$5, $$6) -> {
            for (String $$7 : $$1) {
                Score $$8 = $$42.getOrCreatePlayerScore($$7, $$2);
                int $$9 = $$3 ? $$6 : ($$5 ? 1 : 0);
                $$8.setScore($$9);
            }
        }), CALLBACK_CHAINER);
    }

    private static CommandSourceStack storeValue(CommandSourceStack $$0, CustomBossEvent $$1, boolean $$2, boolean $$32) {
        return $$0.withCallback((ResultConsumer<CommandSourceStack>)((ResultConsumer)($$3, $$4, $$5) -> {
            int $$6;
            int n = $$32 ? $$5 : ($$6 = $$4 ? 1 : 0);
            if ($$2) {
                $$1.setValue($$6);
            } else {
                $$1.setMax($$6);
            }
        }), CALLBACK_CHAINER);
    }

    private static CommandSourceStack storeData(CommandSourceStack $$0, DataAccessor $$1, NbtPathArgument.NbtPath $$2, IntFunction<Tag> $$3, boolean $$42) {
        return $$0.withCallback((ResultConsumer<CommandSourceStack>)((ResultConsumer)($$4, $$5, $$6) -> {
            try {
                CompoundTag $$7 = $$1.getData();
                int $$8 = $$42 ? $$6 : ($$5 ? 1 : 0);
                $$2.set($$7, (Tag)$$3.apply($$8));
                $$1.setData($$7);
            }
            catch (CommandSyntaxException commandSyntaxException) {
                // empty catch block
            }
        }), CALLBACK_CHAINER);
    }

    private static boolean isChunkLoaded(ServerLevel $$0, BlockPos $$1) {
        int $$2 = SectionPos.blockToSectionCoord($$1.getX());
        int $$3 = SectionPos.blockToSectionCoord($$1.getZ());
        LevelChunk $$4 = $$0.getChunkSource().getChunkNow($$2, $$3);
        if ($$4 != null) {
            return $$4.getFullStatus() == ChunkHolder.FullChunkStatus.ENTITY_TICKING;
        }
        return false;
    }

    private static ArgumentBuilder<CommandSourceStack, ?> addConditionals(CommandNode<CommandSourceStack> $$03, LiteralArgumentBuilder<CommandSourceStack> $$12, boolean $$2, CommandBuildContext $$32) {
        ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)$$12.then(Commands.literal("block").then(Commands.argument("pos", BlockPosArgument.blockPos()).then(ExecuteCommand.addConditional($$03, Commands.argument("block", BlockPredicateArgument.blockPredicate($$32)), $$2, $$0 -> BlockPredicateArgument.getBlockPredicate((CommandContext<CommandSourceStack>)$$0, "block").test((Object)new BlockInWorld(((CommandSourceStack)$$0.getSource()).getLevel(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "pos"), true))))))).then(Commands.literal("biome").then(Commands.argument("pos", BlockPosArgument.blockPos()).then(ExecuteCommand.addConditional($$03, Commands.argument("biome", ResourceOrTagArgument.resourceOrTag($$32, Registries.BIOME)), $$2, $$0 -> ResourceOrTagArgument.getResourceOrTag((CommandContext<CommandSourceStack>)$$0, "biome", Registries.BIOME).test(((CommandSourceStack)$$0.getSource()).getLevel().getBiome(BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "pos")))))))).then(Commands.literal("loaded").then(Commands.argument("pos", BlockPosArgument.blockPos()).fork($$03, $$1 -> ExecuteCommand.expect((CommandContext<CommandSourceStack>)$$1, $$2, ExecuteCommand.isChunkLoaded(((CommandSourceStack)$$1.getSource()).getLevel(), BlockPosArgument.getBlockPos((CommandContext<CommandSourceStack>)$$1, "pos"))))))).then(Commands.literal("dimension").then(ExecuteCommand.addConditional($$03, Commands.argument("dimension", DimensionArgument.dimension()), $$2, $$0 -> DimensionArgument.getDimension((CommandContext<CommandSourceStack>)$$0, "dimension") == ((CommandSourceStack)$$0.getSource()).getLevel())))).then(Commands.literal("score").then(Commands.argument("target", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targetObjective", ObjectiveArgument.objective()).then(Commands.literal("=").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(ExecuteCommand.addConditional($$03, Commands.argument("sourceObjective", ObjectiveArgument.objective()), $$2, $$0 -> ExecuteCommand.checkScore((CommandContext<CommandSourceStack>)$$0, (BiPredicate<Integer, Integer>)((BiPredicate)Integer::equals))))))).then(Commands.literal("<").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(ExecuteCommand.addConditional($$03, Commands.argument("sourceObjective", ObjectiveArgument.objective()), $$2, $$02 -> ExecuteCommand.checkScore((CommandContext<CommandSourceStack>)$$02, (BiPredicate<Integer, Integer>)((BiPredicate)($$0, $$1) -> $$0 < $$1))))))).then(Commands.literal("<=").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(ExecuteCommand.addConditional($$03, Commands.argument("sourceObjective", ObjectiveArgument.objective()), $$2, $$02 -> ExecuteCommand.checkScore((CommandContext<CommandSourceStack>)$$02, (BiPredicate<Integer, Integer>)((BiPredicate)($$0, $$1) -> $$0 <= $$1))))))).then(Commands.literal(">").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(ExecuteCommand.addConditional($$03, Commands.argument("sourceObjective", ObjectiveArgument.objective()), $$2, $$02 -> ExecuteCommand.checkScore((CommandContext<CommandSourceStack>)$$02, (BiPredicate<Integer, Integer>)((BiPredicate)($$0, $$1) -> $$0 > $$1))))))).then(Commands.literal(">=").then(Commands.argument("source", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(ExecuteCommand.addConditional($$03, Commands.argument("sourceObjective", ObjectiveArgument.objective()), $$2, $$02 -> ExecuteCommand.checkScore((CommandContext<CommandSourceStack>)$$02, (BiPredicate<Integer, Integer>)((BiPredicate)($$0, $$1) -> $$0 >= $$1))))))).then(Commands.literal("matches").then(ExecuteCommand.addConditional($$03, Commands.argument("range", RangeArgument.intRange()), $$2, $$0 -> ExecuteCommand.checkScore((CommandContext<CommandSourceStack>)$$0, RangeArgument.Ints.getRange((CommandContext<CommandSourceStack>)$$0, "range"))))))))).then(Commands.literal("blocks").then(Commands.argument("start", BlockPosArgument.blockPos()).then(Commands.argument("end", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)Commands.argument("destination", BlockPosArgument.blockPos()).then(ExecuteCommand.addIfBlocksConditional($$03, Commands.literal("all"), $$2, false))).then(ExecuteCommand.addIfBlocksConditional($$03, Commands.literal("masked"), $$2, true))))))).then(Commands.literal("entity").then(((RequiredArgumentBuilder)Commands.argument("entities", EntityArgument.entities()).fork($$03, $$1 -> ExecuteCommand.expect((CommandContext<CommandSourceStack>)$$1, $$2, !EntityArgument.getOptionalEntities((CommandContext<CommandSourceStack>)$$1, "entities").isEmpty()))).executes(ExecuteCommand.createNumericConditionalHandler($$2, $$0 -> EntityArgument.getOptionalEntities((CommandContext<CommandSourceStack>)$$0, "entities").size()))))).then(Commands.literal("predicate").then(ExecuteCommand.addConditional($$03, Commands.argument("predicate", ResourceLocationArgument.id()).suggests(SUGGEST_PREDICATE), $$2, $$0 -> ExecuteCommand.checkCustomPredicate((CommandSourceStack)$$0.getSource(), ResourceLocationArgument.getPredicate((CommandContext<CommandSourceStack>)$$0, "predicate")))));
        for (DataCommands.DataProvider $$4 : DataCommands.SOURCE_PROVIDERS) {
            $$12.then($$4.wrap((ArgumentBuilder<CommandSourceStack, ?>)Commands.literal("data"), (Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>>)((Function)$$3 -> $$3.then(((RequiredArgumentBuilder)Commands.argument("path", NbtPathArgument.nbtPath()).fork($$03, $$2 -> ExecuteCommand.expect((CommandContext<CommandSourceStack>)$$2, $$2, ExecuteCommand.checkMatchingData($$4.access((CommandContext<CommandSourceStack>)$$2), NbtPathArgument.getPath((CommandContext<CommandSourceStack>)$$2, "path")) > 0))).executes(ExecuteCommand.createNumericConditionalHandler($$2, $$1 -> ExecuteCommand.checkMatchingData($$4.access((CommandContext<CommandSourceStack>)$$1), NbtPathArgument.getPath((CommandContext<CommandSourceStack>)$$1, "path"))))))));
        }
        return $$12;
    }

    private static Command<CommandSourceStack> createNumericConditionalHandler(boolean $$0, CommandNumericPredicate $$12) {
        if ($$0) {
            return $$1 -> {
                int $$2 = $$12.test((CommandContext<CommandSourceStack>)$$1);
                if ($$2 > 0) {
                    ((CommandSourceStack)$$1.getSource()).sendSuccess(Component.translatable("commands.execute.conditional.pass_count", $$2), false);
                    return $$2;
                }
                throw ERROR_CONDITIONAL_FAILED.create();
            };
        }
        return $$1 -> {
            int $$2 = $$12.test((CommandContext<CommandSourceStack>)$$1);
            if ($$2 == 0) {
                ((CommandSourceStack)$$1.getSource()).sendSuccess(Component.translatable("commands.execute.conditional.pass"), false);
                return 1;
            }
            throw ERROR_CONDITIONAL_FAILED_COUNT.create((Object)$$2);
        };
    }

    private static int checkMatchingData(DataAccessor $$0, NbtPathArgument.NbtPath $$1) throws CommandSyntaxException {
        return $$1.countMatching($$0.getData());
    }

    private static boolean checkScore(CommandContext<CommandSourceStack> $$0, BiPredicate<Integer, Integer> $$1) throws CommandSyntaxException {
        String $$2 = ScoreHolderArgument.getName($$0, "target");
        Objective $$3 = ObjectiveArgument.getObjective($$0, "targetObjective");
        String $$4 = ScoreHolderArgument.getName($$0, "source");
        Objective $$5 = ObjectiveArgument.getObjective($$0, "sourceObjective");
        ServerScoreboard $$6 = ((CommandSourceStack)$$0.getSource()).getServer().getScoreboard();
        if (!$$6.hasPlayerScore($$2, $$3) || !$$6.hasPlayerScore($$4, $$5)) {
            return false;
        }
        Score $$7 = $$6.getOrCreatePlayerScore($$2, $$3);
        Score $$8 = $$6.getOrCreatePlayerScore($$4, $$5);
        return $$1.test((Object)$$7.getScore(), (Object)$$8.getScore());
    }

    private static boolean checkScore(CommandContext<CommandSourceStack> $$0, MinMaxBounds.Ints $$1) throws CommandSyntaxException {
        String $$2 = ScoreHolderArgument.getName($$0, "target");
        Objective $$3 = ObjectiveArgument.getObjective($$0, "targetObjective");
        ServerScoreboard $$4 = ((CommandSourceStack)$$0.getSource()).getServer().getScoreboard();
        if (!$$4.hasPlayerScore($$2, $$3)) {
            return false;
        }
        return $$1.matches($$4.getOrCreatePlayerScore($$2, $$3).getScore());
    }

    private static boolean checkCustomPredicate(CommandSourceStack $$0, LootItemCondition $$1) {
        ServerLevel $$2 = $$0.getLevel();
        LootContext.Builder $$3 = new LootContext.Builder($$2).withParameter(LootContextParams.ORIGIN, $$0.getPosition()).withOptionalParameter(LootContextParams.THIS_ENTITY, $$0.getEntity());
        return $$1.test($$3.create(LootContextParamSets.COMMAND));
    }

    private static Collection<CommandSourceStack> expect(CommandContext<CommandSourceStack> $$0, boolean $$1, boolean $$2) {
        if ($$2 == $$1) {
            return Collections.singleton((Object)((CommandSourceStack)$$0.getSource()));
        }
        return Collections.emptyList();
    }

    private static ArgumentBuilder<CommandSourceStack, ?> addConditional(CommandNode<CommandSourceStack> $$0, ArgumentBuilder<CommandSourceStack, ?> $$1, boolean $$22, CommandPredicate $$3) {
        return $$1.fork($$0, $$2 -> ExecuteCommand.expect((CommandContext<CommandSourceStack>)$$2, $$22, $$3.test((CommandContext<CommandSourceStack>)$$2))).executes($$2 -> {
            if ($$22 == $$3.test((CommandContext<CommandSourceStack>)$$2)) {
                ((CommandSourceStack)$$2.getSource()).sendSuccess(Component.translatable("commands.execute.conditional.pass"), false);
                return 1;
            }
            throw ERROR_CONDITIONAL_FAILED.create();
        });
    }

    private static ArgumentBuilder<CommandSourceStack, ?> addIfBlocksConditional(CommandNode<CommandSourceStack> $$0, ArgumentBuilder<CommandSourceStack, ?> $$12, boolean $$22, boolean $$3) {
        return $$12.fork($$0, $$2 -> ExecuteCommand.expect((CommandContext<CommandSourceStack>)$$2, $$22, ExecuteCommand.checkRegions((CommandContext<CommandSourceStack>)$$2, $$3).isPresent())).executes($$22 ? $$1 -> ExecuteCommand.checkIfRegions((CommandContext<CommandSourceStack>)$$1, $$3) : $$1 -> ExecuteCommand.checkUnlessRegions((CommandContext<CommandSourceStack>)$$1, $$3));
    }

    private static int checkIfRegions(CommandContext<CommandSourceStack> $$0, boolean $$1) throws CommandSyntaxException {
        OptionalInt $$2 = ExecuteCommand.checkRegions($$0, $$1);
        if ($$2.isPresent()) {
            ((CommandSourceStack)$$0.getSource()).sendSuccess(Component.translatable("commands.execute.conditional.pass_count", $$2.getAsInt()), false);
            return $$2.getAsInt();
        }
        throw ERROR_CONDITIONAL_FAILED.create();
    }

    private static int checkUnlessRegions(CommandContext<CommandSourceStack> $$0, boolean $$1) throws CommandSyntaxException {
        OptionalInt $$2 = ExecuteCommand.checkRegions($$0, $$1);
        if ($$2.isPresent()) {
            throw ERROR_CONDITIONAL_FAILED_COUNT.create((Object)$$2.getAsInt());
        }
        ((CommandSourceStack)$$0.getSource()).sendSuccess(Component.translatable("commands.execute.conditional.pass"), false);
        return 1;
    }

    private static OptionalInt checkRegions(CommandContext<CommandSourceStack> $$0, boolean $$1) throws CommandSyntaxException {
        return ExecuteCommand.checkRegions(((CommandSourceStack)$$0.getSource()).getLevel(), BlockPosArgument.getLoadedBlockPos($$0, "start"), BlockPosArgument.getLoadedBlockPos($$0, "end"), BlockPosArgument.getLoadedBlockPos($$0, "destination"), $$1);
    }

    private static OptionalInt checkRegions(ServerLevel $$0, BlockPos $$1, BlockPos $$2, BlockPos $$3, boolean $$4) throws CommandSyntaxException {
        BoundingBox $$5 = BoundingBox.fromCorners($$1, $$2);
        BoundingBox $$6 = BoundingBox.fromCorners($$3, $$3.offset($$5.getLength()));
        BlockPos $$7 = new BlockPos($$6.minX() - $$5.minX(), $$6.minY() - $$5.minY(), $$6.minZ() - $$5.minZ());
        int $$8 = $$5.getXSpan() * $$5.getYSpan() * $$5.getZSpan();
        if ($$8 > 32768) {
            throw ERROR_AREA_TOO_LARGE.create((Object)32768, (Object)$$8);
        }
        int $$9 = 0;
        for (int $$10 = $$5.minZ(); $$10 <= $$5.maxZ(); ++$$10) {
            for (int $$11 = $$5.minY(); $$11 <= $$5.maxY(); ++$$11) {
                for (int $$12 = $$5.minX(); $$12 <= $$5.maxX(); ++$$12) {
                    BlockPos $$13 = new BlockPos($$12, $$11, $$10);
                    Vec3i $$14 = $$13.offset($$7);
                    BlockState $$15 = $$0.getBlockState($$13);
                    if ($$4 && $$15.is(Blocks.AIR)) continue;
                    if ($$15 != $$0.getBlockState((BlockPos)$$14)) {
                        return OptionalInt.empty();
                    }
                    BlockEntity $$16 = $$0.getBlockEntity($$13);
                    BlockEntity $$17 = $$0.getBlockEntity((BlockPos)$$14);
                    if ($$16 != null) {
                        CompoundTag $$19;
                        if ($$17 == null) {
                            return OptionalInt.empty();
                        }
                        if ($$17.getType() != $$16.getType()) {
                            return OptionalInt.empty();
                        }
                        CompoundTag $$18 = $$16.saveWithoutMetadata();
                        if (!$$18.equals($$19 = $$17.saveWithoutMetadata())) {
                            return OptionalInt.empty();
                        }
                    }
                    ++$$9;
                }
            }
        }
        return OptionalInt.of((int)$$9);
    }

    private static RedirectModifier<CommandSourceStack> expandOneToOneEntityRelation(Function<Entity, Optional<Entity>> $$0) {
        return $$12 -> {
            CommandSourceStack $$2 = (CommandSourceStack)$$12.getSource();
            Entity $$3 = $$2.getEntity();
            if ($$3 == null) {
                return List.of();
            }
            return (Collection)((Optional)$$0.apply((Object)$$3)).filter($$0 -> !$$0.isRemoved()).map($$1 -> List.of((Object)$$2.withEntity((Entity)$$1))).orElse((Object)List.of());
        };
    }

    private static RedirectModifier<CommandSourceStack> expandOneToManyEntityRelation(Function<Entity, Stream<Entity>> $$0) {
        return $$1 -> {
            CommandSourceStack $$2 = (CommandSourceStack)$$1.getSource();
            Entity $$3 = $$2.getEntity();
            if ($$3 == null) {
                return List.of();
            }
            return ((Stream)$$0.apply((Object)$$3)).filter($$0 -> !$$0.isRemoved()).map($$2::withEntity).toList();
        };
    }

    private static LiteralArgumentBuilder<CommandSourceStack> createRelationOperations(CommandNode<CommandSourceStack> $$02, LiteralArgumentBuilder<CommandSourceStack> $$1) {
        return (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)$$1.then(Commands.literal("owner").fork($$02, ExecuteCommand.expandOneToOneEntityRelation((Function<Entity, Optional<Entity>>)((Function)$$0 -> {
            Optional optional;
            if ($$0 instanceof TamableAnimal) {
                TamableAnimal $$1 = (TamableAnimal)$$0;
                optional = Optional.ofNullable((Object)$$1.getOwner());
            } else {
                optional = Optional.empty();
            }
            return optional;
        }))))).then(Commands.literal("leasher").fork($$02, ExecuteCommand.expandOneToOneEntityRelation((Function<Entity, Optional<Entity>>)((Function)$$0 -> {
            Optional optional;
            if ($$0 instanceof Mob) {
                Mob $$1 = (Mob)$$0;
                optional = Optional.ofNullable((Object)$$1.getLeashHolder());
            } else {
                optional = Optional.empty();
            }
            return optional;
        }))))).then(Commands.literal("target").fork($$02, ExecuteCommand.expandOneToOneEntityRelation((Function<Entity, Optional<Entity>>)((Function)$$0 -> {
            Optional optional;
            if ($$0 instanceof Mob) {
                Mob $$1 = (Mob)$$0;
                optional = Optional.ofNullable((Object)$$1.getTarget());
            } else {
                optional = Optional.empty();
            }
            return optional;
        }))))).then(Commands.literal("attacker").fork($$02, ExecuteCommand.expandOneToOneEntityRelation((Function<Entity, Optional<Entity>>)((Function)$$0 -> {
            Optional optional;
            if ($$0 instanceof LivingEntity) {
                LivingEntity $$1 = (LivingEntity)$$0;
                optional = Optional.ofNullable((Object)$$1.getLastHurtByMob());
            } else {
                optional = Optional.empty();
            }
            return optional;
        }))))).then(Commands.literal("vehicle").fork($$02, ExecuteCommand.expandOneToOneEntityRelation((Function<Entity, Optional<Entity>>)((Function)$$0 -> Optional.ofNullable((Object)$$0.getVehicle())))))).then(Commands.literal("controller").fork($$02, ExecuteCommand.expandOneToOneEntityRelation((Function<Entity, Optional<Entity>>)((Function)$$0 -> Optional.ofNullable((Object)$$0.getControllingPassenger())))))).then(Commands.literal("origin").fork($$02, ExecuteCommand.expandOneToOneEntityRelation((Function<Entity, Optional<Entity>>)((Function)$$0 -> {
            Optional optional;
            if ($$0 instanceof TraceableEntity) {
                TraceableEntity $$1 = (TraceableEntity)((Object)$$0);
                optional = Optional.ofNullable((Object)$$1.getOwner());
            } else {
                optional = Optional.empty();
            }
            return optional;
        }))))).then(Commands.literal("passengers").fork($$02, ExecuteCommand.expandOneToManyEntityRelation((Function<Entity, Stream<Entity>>)((Function)$$0 -> $$0.getPassengers().stream()))));
    }

    @FunctionalInterface
    static interface CommandPredicate {
        public boolean test(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException;
    }

    @FunctionalInterface
    static interface CommandNumericPredicate {
        public int test(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException;
    }
}