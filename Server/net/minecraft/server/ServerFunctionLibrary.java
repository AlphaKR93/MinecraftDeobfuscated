/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  java.io.BufferedReader
 *  java.io.IOException
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.lang.Void
 *  java.util.Collection
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Optional
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.CompletionException
 *  java.util.concurrent.CompletionStage
 *  java.util.concurrent.Executor
 *  org.slf4j.Logger
 */
package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class ServerFunctionLibrary
implements PreparableReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final FileToIdConverter LISTER = new FileToIdConverter("functions", ".mcfunction");
    private volatile Map<ResourceLocation, CommandFunction> functions = ImmutableMap.of();
    private final TagLoader<CommandFunction> tagsLoader = new TagLoader(this::getFunction, "tags/functions");
    private volatile Map<ResourceLocation, Collection<CommandFunction>> tags = Map.of();
    private final int functionCompilationLevel;
    private final CommandDispatcher<CommandSourceStack> dispatcher;

    public Optional<CommandFunction> getFunction(ResourceLocation $$0) {
        return Optional.ofNullable((Object)((CommandFunction)this.functions.get((Object)$$0)));
    }

    public Map<ResourceLocation, CommandFunction> getFunctions() {
        return this.functions;
    }

    public Collection<CommandFunction> getTag(ResourceLocation $$0) {
        return (Collection)this.tags.getOrDefault((Object)$$0, (Object)List.of());
    }

    public Iterable<ResourceLocation> getAvailableTags() {
        return this.tags.keySet();
    }

    public ServerFunctionLibrary(int $$0, CommandDispatcher<CommandSourceStack> $$1) {
        this.functionCompilationLevel = $$0;
        this.dispatcher = $$1;
    }

    @Override
    public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier $$02, ResourceManager $$12, ProfilerFiller $$2, ProfilerFiller $$3, Executor $$4, Executor $$5) {
        CompletableFuture $$6 = CompletableFuture.supplyAsync(() -> this.tagsLoader.load($$12), (Executor)$$4);
        CompletableFuture $$7 = CompletableFuture.supplyAsync(() -> LISTER.listMatchingResources($$12), (Executor)$$4).thenCompose($$1 -> {
            HashMap $$2 = Maps.newHashMap();
            CommandSourceStack $$3 = new CommandSourceStack(CommandSource.NULL, Vec3.ZERO, Vec2.ZERO, null, this.functionCompilationLevel, "", CommonComponents.EMPTY, null, null);
            for (Map.Entry $$4 : $$1.entrySet()) {
                ResourceLocation $$5 = (ResourceLocation)$$4.getKey();
                ResourceLocation $$6 = LISTER.fileToId($$5);
                $$2.put((Object)$$6, (Object)CompletableFuture.supplyAsync(() -> {
                    List<String> $$3 = ServerFunctionLibrary.readLines((Resource)$$4.getValue());
                    return CommandFunction.fromLines($$6, this.dispatcher, $$3, $$3);
                }, (Executor)$$4));
            }
            CompletableFuture[] $$7 = (CompletableFuture[])$$2.values().toArray((Object[])new CompletableFuture[0]);
            return CompletableFuture.allOf((CompletableFuture[])$$7).handle((arg_0, arg_1) -> ServerFunctionLibrary.lambda$reload$3((Map)$$2, arg_0, arg_1));
        });
        return $$6.thenCombine((CompletionStage)$$7, Pair::of).thenCompose($$02::wait).thenAcceptAsync($$0 -> {
            Map $$12 = (Map)$$0.getSecond();
            ImmutableMap.Builder $$2 = ImmutableMap.builder();
            $$12.forEach(($$1, $$22) -> $$22.handle(($$2, $$3) -> {
                if ($$3 != null) {
                    LOGGER.error("Failed to load function {}", $$1, $$3);
                } else {
                    $$2.put($$1, $$2);
                }
                return null;
            }).join());
            this.functions = $$2.build();
            this.tags = this.tagsLoader.build((Map<ResourceLocation, List<TagLoader.EntryWithSource>>)((Map)$$0.getFirst()));
        }, $$5);
    }

    private static List<String> readLines(Resource $$0) {
        List list;
        block8: {
            BufferedReader $$1 = $$0.openAsReader();
            try {
                list = $$1.lines().toList();
                if ($$1 == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if ($$1 != null) {
                        try {
                            $$1.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException $$2) {
                    throw new CompletionException((Throwable)$$2);
                }
            }
            $$1.close();
        }
        return list;
    }

    private static /* synthetic */ Map lambda$reload$3(Map $$0, Void $$1, Throwable $$2) {
        return $$0;
    }
}