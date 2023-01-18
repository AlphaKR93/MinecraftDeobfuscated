/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntMaps
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  java.lang.Exception
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.Locale
 *  java.util.Optional
 *  java.util.concurrent.TimeUnit
 *  org.apache.commons.lang3.mutable.MutableInt
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.levelgen.feature;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;

public class FeatureCountTracker {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final LoadingCache<ServerLevel, LevelData> data = CacheBuilder.newBuilder().weakKeys().expireAfterAccess(5L, TimeUnit.MINUTES).build((CacheLoader)new CacheLoader<ServerLevel, LevelData>(){

        public LevelData load(ServerLevel $$0) {
            return new LevelData((Object2IntMap<FeatureData>)Object2IntMaps.synchronize((Object2IntMap)new Object2IntOpenHashMap()), new MutableInt(0));
        }
    });

    public static void chunkDecorated(ServerLevel $$0) {
        try {
            ((LevelData)((Object)data.get((Object)$$0))).chunksWithFeatures().increment();
        }
        catch (Exception $$1) {
            LOGGER.error("Failed to increment chunk count", (Throwable)$$1);
        }
    }

    public static void featurePlaced(ServerLevel $$02, ConfiguredFeature<?, ?> $$12, Optional<PlacedFeature> $$2) {
        try {
            ((LevelData)((Object)data.get((Object)$$02))).featureData().computeInt((Object)new FeatureData($$12, $$2), ($$0, $$1) -> $$1 == null ? 1 : $$1 + 1);
        }
        catch (Exception $$3) {
            LOGGER.error("Failed to increment feature count", (Throwable)$$3);
        }
    }

    public static void clearCounts() {
        data.invalidateAll();
        LOGGER.debug("Cleared feature counts");
    }

    public static void logCounts() {
        LOGGER.debug("Logging feature counts:");
        data.asMap().forEach(($$0, $$1) -> {
            String $$2 = $$0.dimension().location().toString();
            boolean $$32 = $$0.getServer().isRunning();
            Registry<PlacedFeature> $$42 = $$0.registryAccess().registryOrThrow(Registries.PLACED_FEATURE);
            String $$5 = ($$32 ? "running" : "dead") + " " + $$2;
            Integer $$6 = $$1.chunksWithFeatures().getValue();
            LOGGER.debug($$5 + " total_chunks: " + $$6);
            $$1.featureData().forEach(($$3, $$4) -> LOGGER.debug($$5 + " " + String.format((Locale)Locale.ROOT, (String)"%10d ", (Object[])new Object[]{$$4}) + String.format((Locale)Locale.ROOT, (String)"%10f ", (Object[])new Object[]{(double)$$4.intValue() / (double)$$6.intValue()}) + $$3.topFeature().flatMap($$42::getResourceKey).map(ResourceKey::location) + " " + $$3.feature().feature() + " " + $$3.feature()));
        });
    }

    record LevelData(Object2IntMap<FeatureData> featureData, MutableInt chunksWithFeatures) {
    }

    record FeatureData(ConfiguredFeature<?, ?> feature, Optional<PlacedFeature> topFeature) {
    }
}