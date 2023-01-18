/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Unit
 *  com.mojang.logging.LogUtils
 *  java.lang.Float
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.System
 *  java.lang.UnsupportedOperationException
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Locale
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.CompletionStage
 *  java.util.concurrent.Executor
 *  java.util.function.Function
 *  org.slf4j.Logger
 */
package net.minecraft.server.commands;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Unit;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class ResetChunksCommand {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("resetchunks").requires($$0 -> $$0.hasPermission(2))).executes($$0 -> ResetChunksCommand.resetChunks((CommandSourceStack)$$0.getSource(), 0, true))).then(((RequiredArgumentBuilder)Commands.argument("range", IntegerArgumentType.integer((int)0, (int)5)).executes($$0 -> ResetChunksCommand.resetChunks((CommandSourceStack)$$0.getSource(), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"range"), true))).then(Commands.argument("skipOldChunks", BoolArgumentType.bool()).executes($$0 -> ResetChunksCommand.resetChunks((CommandSourceStack)$$0.getSource(), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"range"), BoolArgumentType.getBool((CommandContext)$$0, (String)"skipOldChunks"))))));
    }

    private static int resetChunks(CommandSourceStack $$0, int $$1, boolean $$2) {
        ServerLevel $$3 = $$0.getLevel();
        ServerChunkCache $$4 = $$3.getChunkSource();
        $$4.chunkMap.debugReloadGenerator();
        Vec3 $$5 = $$0.getPosition();
        ChunkPos $$6 = new ChunkPos(new BlockPos($$5));
        int $$7 = $$6.z - $$1;
        int $$8 = $$6.z + $$1;
        int $$9 = $$6.x - $$1;
        int $$10 = $$6.x + $$1;
        for (int $$11 = $$7; $$11 <= $$8; ++$$11) {
            for (int $$12 = $$9; $$12 <= $$10; ++$$12) {
                ChunkPos $$13 = new ChunkPos($$12, $$11);
                LevelChunk $$14 = $$4.getChunk($$12, $$11, false);
                if ($$14 == null || $$2 && $$14.isOldNoiseGeneration()) continue;
                for (BlockPos $$15 : BlockPos.betweenClosed($$13.getMinBlockX(), $$3.getMinBuildHeight(), $$13.getMinBlockZ(), $$13.getMaxBlockX(), $$3.getMaxBuildHeight() - 1, $$13.getMaxBlockZ())) {
                    $$3.setBlock($$15, Blocks.AIR.defaultBlockState(), 16);
                }
            }
        }
        ProcessorMailbox<Runnable> $$16 = ProcessorMailbox.create((Executor)Util.backgroundExecutor(), "worldgen-resetchunks");
        long $$17 = System.currentTimeMillis();
        int $$18 = ($$1 * 2 + 1) * ($$1 * 2 + 1);
        for (ChunkStatus $$19 : ImmutableList.of((Object)ChunkStatus.BIOMES, (Object)ChunkStatus.NOISE, (Object)ChunkStatus.SURFACE, (Object)ChunkStatus.CARVERS, (Object)ChunkStatus.LIQUID_CARVERS, (Object)ChunkStatus.FEATURES)) {
            long $$20 = System.currentTimeMillis();
            CompletableFuture $$21 = CompletableFuture.supplyAsync(() -> Unit.INSTANCE, $$16::tell);
            for (int $$22 = $$6.z - $$1; $$22 <= $$6.z + $$1; ++$$22) {
                for (int $$23 = $$6.x - $$1; $$23 <= $$6.x + $$1; ++$$23) {
                    ChunkPos $$24 = new ChunkPos($$23, $$22);
                    LevelChunk $$25 = $$4.getChunk($$23, $$22, false);
                    if ($$25 == null || $$2 && $$25.isOldNoiseGeneration()) continue;
                    ArrayList $$26 = Lists.newArrayList();
                    int $$27 = Math.max((int)1, (int)$$19.getRange());
                    for (int $$28 = $$24.z - $$27; $$28 <= $$24.z + $$27; ++$$28) {
                        for (int $$29 = $$24.x - $$27; $$29 <= $$24.x + $$27; ++$$29) {
                            ChunkAccess $$33;
                            ChunkAccess $$30 = $$4.getChunk($$29, $$28, $$19.getParent(), true);
                            if ($$30 instanceof ImposterProtoChunk) {
                                ImposterProtoChunk $$31 = new ImposterProtoChunk(((ImposterProtoChunk)$$30).getWrapped(), true);
                            } else if ($$30 instanceof LevelChunk) {
                                ImposterProtoChunk $$32 = new ImposterProtoChunk((LevelChunk)$$30, true);
                            } else {
                                $$33 = $$30;
                            }
                            $$26.add((Object)$$33);
                        }
                    }
                    $$21 = $$21.thenComposeAsync(arg_0 -> ResetChunksCommand.lambda$resetChunks$8($$19, $$16, $$3, $$4, (List)$$26, arg_0), $$16::tell);
                }
            }
            $$0.getServer().managedBlock(() -> ((CompletableFuture)$$21).isDone());
            LOGGER.debug($$19.getName() + " took " + (System.currentTimeMillis() - $$20) + " ms");
        }
        long $$34 = System.currentTimeMillis();
        for (int $$35 = $$6.z - $$1; $$35 <= $$6.z + $$1; ++$$35) {
            for (int $$36 = $$6.x - $$1; $$36 <= $$6.x + $$1; ++$$36) {
                ChunkPos $$37 = new ChunkPos($$36, $$35);
                LevelChunk $$38 = $$4.getChunk($$36, $$35, false);
                if ($$38 == null || $$2 && $$38.isOldNoiseGeneration()) continue;
                for (BlockPos $$39 : BlockPos.betweenClosed($$37.getMinBlockX(), $$3.getMinBuildHeight(), $$37.getMinBlockZ(), $$37.getMaxBlockX(), $$3.getMaxBuildHeight() - 1, $$37.getMaxBlockZ())) {
                    $$4.blockChanged($$39);
                }
            }
        }
        LOGGER.debug("blockChanged took " + (System.currentTimeMillis() - $$34) + " ms");
        long $$40 = System.currentTimeMillis() - $$17;
        $$0.sendSuccess(Component.literal(String.format((Locale)Locale.ROOT, (String)"%d chunks have been reset. This took %d ms for %d chunks, or %02f ms per chunk", (Object[])new Object[]{$$18, $$40, $$18, Float.valueOf((float)((float)$$40 / (float)$$18))})), true);
        return 1;
    }

    private static /* synthetic */ CompletionStage lambda$resetChunks$8(ChunkStatus $$02, ProcessorMailbox $$12, ServerLevel $$2, ServerChunkCache $$3, List $$4, Unit $$5) {
        return $$02.generate($$12::tell, $$2, $$3.getGenerator(), $$2.getStructureManager(), $$3.getLightEngine(), (Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>>)((Function)$$0 -> {
            throw new UnsupportedOperationException("Not creating full chunks here");
        }), (List<ChunkAccess>)$$4, true).thenApply($$1 -> {
            if ($$02 == ChunkStatus.NOISE) {
                $$1.left().ifPresent($$0 -> Heightmap.primeHeightmaps($$0, ChunkStatus.POST_FEATURES));
            }
            return Unit.INSTANCE;
        });
    }
}