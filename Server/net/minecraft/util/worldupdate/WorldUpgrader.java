/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Lists
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2FloatMap
 *  it.unimi.dsi.fastutil.objects.Object2FloatMaps
 *  it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap
 *  java.io.File
 *  java.io.IOException
 *  java.lang.CharSequence
 *  java.lang.Integer
 *  java.lang.InterruptedException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Thread
 *  java.lang.Throwable
 *  java.nio.file.Path
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.ListIterator
 *  java.util.Optional
 *  java.util.Set
 *  java.util.concurrent.CompletionException
 *  java.util.concurrent.ThreadFactory
 *  java.util.function.Supplier
 *  java.util.regex.Matcher
 *  java.util.regex.Pattern
 *  java.util.stream.Collectors
 *  org.slf4j.Logger
 */
package net.minecraft.util.worldupdate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatMaps;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenCustomHashMap;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ThreadFactory;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.slf4j.Logger;

public class WorldUpgrader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ThreadFactory THREAD_FACTORY = new ThreadFactoryBuilder().setDaemon(true).build();
    private final Registry<LevelStem> dimensions;
    private final Set<ResourceKey<Level>> levels;
    private final boolean eraseCache;
    private final LevelStorageSource.LevelStorageAccess levelStorage;
    private final Thread thread;
    private final DataFixer dataFixer;
    private volatile boolean running = true;
    private volatile boolean finished;
    private volatile float progress;
    private volatile int totalChunks;
    private volatile int converted;
    private volatile int skipped;
    private final Object2FloatMap<ResourceKey<Level>> progressMap = Object2FloatMaps.synchronize((Object2FloatMap)new Object2FloatOpenCustomHashMap(Util.identityStrategy()));
    private volatile Component status = Component.translatable("optimizeWorld.stage.counting");
    private static final Pattern REGEX = Pattern.compile((String)"^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
    private final DimensionDataStorage overworldDataStorage;

    public WorldUpgrader(LevelStorageSource.LevelStorageAccess $$02, DataFixer $$12, Registry<LevelStem> $$2, boolean $$3) {
        this.dimensions = $$2;
        this.levels = (Set)$$2.registryKeySet().stream().map(Registries::levelStemToLevel).collect(Collectors.toUnmodifiableSet());
        this.eraseCache = $$3;
        this.dataFixer = $$12;
        this.levelStorage = $$02;
        this.overworldDataStorage = new DimensionDataStorage(this.levelStorage.getDimensionPath(Level.OVERWORLD).resolve("data").toFile(), $$12);
        this.thread = THREAD_FACTORY.newThread(this::work);
        this.thread.setUncaughtExceptionHandler(($$0, $$1) -> {
            LOGGER.error("Error upgrading world", $$1);
            this.status = Component.translatable("optimizeWorld.stage.failed");
            this.finished = true;
        });
        this.thread.start();
    }

    public void cancel() {
        this.running = false;
        try {
            this.thread.join();
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    private void work() {
        this.totalChunks = 0;
        ImmutableMap.Builder $$0 = ImmutableMap.builder();
        for (ResourceKey $$1 : this.levels) {
            List<ChunkPos> $$2 = this.getAllChunkPos($$1);
            $$0.put((Object)$$1, (Object)$$2.listIterator());
            this.totalChunks += $$2.size();
        }
        if (this.totalChunks == 0) {
            this.finished = true;
            return;
        }
        float $$3 = this.totalChunks;
        ImmutableMap $$4 = $$0.build();
        ImmutableMap.Builder $$5 = ImmutableMap.builder();
        for (ResourceKey $$6 : this.levels) {
            Path $$7 = this.levelStorage.getDimensionPath($$6);
            $$5.put((Object)$$6, (Object)new ChunkStorage($$7.resolve("region"), this.dataFixer, true));
        }
        ImmutableMap $$8 = $$5.build();
        long $$9 = Util.getMillis();
        this.status = Component.translatable("optimizeWorld.stage.upgrading");
        while (this.running) {
            boolean $$10 = false;
            float $$11 = 0.0f;
            for (ResourceKey $$12 : this.levels) {
                ListIterator $$13 = (ListIterator)$$4.get((Object)$$12);
                ChunkStorage $$14 = (ChunkStorage)$$8.get((Object)$$12);
                if ($$13.hasNext()) {
                    ChunkPos $$15 = (ChunkPos)$$13.next();
                    boolean $$16 = false;
                    try {
                        CompoundTag $$17 = (CompoundTag)((Optional)$$14.read($$15).join()).orElse(null);
                        if ($$17 != null) {
                            boolean $$22;
                            int $$18 = ChunkStorage.getVersion($$17);
                            ChunkGenerator $$19 = this.dimensions.getOrThrow(Registries.levelToLevelStem($$12)).generator();
                            CompoundTag $$20 = $$14.upgradeChunkTag($$12, (Supplier<DimensionDataStorage>)((Supplier)() -> this.overworldDataStorage), $$17, $$19.getTypeNameForDataFixer());
                            ChunkPos $$21 = new ChunkPos($$20.getInt("xPos"), $$20.getInt("zPos"));
                            if (!$$21.equals($$15)) {
                                LOGGER.warn("Chunk {} has invalid position {}", (Object)$$15, (Object)$$21);
                            }
                            boolean bl = $$22 = $$18 < SharedConstants.getCurrentVersion().getDataVersion().getVersion();
                            if (this.eraseCache) {
                                $$22 = $$22 || $$20.contains("Heightmaps");
                                $$20.remove("Heightmaps");
                                $$22 = $$22 || $$20.contains("isLightOn");
                                $$20.remove("isLightOn");
                                ListTag $$23 = $$20.getList("sections", 10);
                                for (int $$24 = 0; $$24 < $$23.size(); ++$$24) {
                                    CompoundTag $$25 = $$23.getCompound($$24);
                                    $$22 = $$22 || $$25.contains("BlockLight");
                                    $$25.remove("BlockLight");
                                    $$22 = $$22 || $$25.contains("SkyLight");
                                    $$25.remove("SkyLight");
                                }
                            }
                            if ($$22) {
                                $$14.write($$15, $$20);
                                $$16 = true;
                            }
                        }
                    }
                    catch (CompletionException | ReportedException $$26) {
                        Throwable $$27 = $$26.getCause();
                        if ($$27 instanceof IOException) {
                            LOGGER.error("Error upgrading chunk {}", (Object)$$15, (Object)$$27);
                        }
                        throw $$26;
                    }
                    if ($$16) {
                        ++this.converted;
                    } else {
                        ++this.skipped;
                    }
                    $$10 = true;
                }
                float $$28 = (float)$$13.nextIndex() / $$3;
                this.progressMap.put((Object)$$12, $$28);
                $$11 += $$28;
            }
            this.progress = $$11;
            if ($$10) continue;
            this.running = false;
        }
        this.status = Component.translatable("optimizeWorld.stage.finished");
        for (ChunkStorage $$29 : $$8.values()) {
            try {
                $$29.close();
            }
            catch (IOException $$30) {
                LOGGER.error("Error upgrading chunk", (Throwable)$$30);
            }
        }
        this.overworldDataStorage.save();
        $$9 = Util.getMillis() - $$9;
        LOGGER.info("World optimizaton finished after {} ms", (Object)$$9);
        this.finished = true;
    }

    private List<ChunkPos> getAllChunkPos(ResourceKey<Level> $$02) {
        File $$12 = this.levelStorage.getDimensionPath($$02).toFile();
        File $$2 = new File($$12, "region");
        File[] $$3 = $$2.listFiles(($$0, $$1) -> $$1.endsWith(".mca"));
        if ($$3 == null) {
            return ImmutableList.of();
        }
        ArrayList $$4 = Lists.newArrayList();
        for (File $$5 : $$3) {
            Matcher $$6 = REGEX.matcher((CharSequence)$$5.getName());
            if (!$$6.matches()) continue;
            int $$7 = Integer.parseInt((String)$$6.group(1)) << 5;
            int $$8 = Integer.parseInt((String)$$6.group(2)) << 5;
            try (RegionFile $$9 = new RegionFile($$5.toPath(), $$2.toPath(), true);){
                for (int $$10 = 0; $$10 < 32; ++$$10) {
                    for (int $$11 = 0; $$11 < 32; ++$$11) {
                        ChunkPos $$122 = new ChunkPos($$10 + $$7, $$11 + $$8);
                        if (!$$9.doesChunkExist($$122)) continue;
                        $$4.add((Object)$$122);
                    }
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return $$4;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public Set<ResourceKey<Level>> levels() {
        return this.levels;
    }

    public float dimensionProgress(ResourceKey<Level> $$0) {
        return this.progressMap.getFloat($$0);
    }

    public float getProgress() {
        return this.progress;
    }

    public int getTotalChunks() {
        return this.totalChunks;
    }

    public int getConverted() {
        return this.converted;
    }

    public int getSkipped() {
        return this.skipped;
    }

    public Component getStatus() {
        return this.status;
    }
}