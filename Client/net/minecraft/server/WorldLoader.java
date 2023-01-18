/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  java.lang.Exception
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.lang.Throwable
 *  java.util.List
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  org.slf4j.Logger
 */
package net.minecraft.server;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.commands.Commands;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.WorldDataConfiguration;
import org.slf4j.Logger;

public class WorldLoader {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static <D, R> CompletableFuture<R> load(InitConfig $$0, WorldDataSupplier<D> $$12, ResultFactory<D, R> $$22, Executor $$3, Executor $$4) {
        try {
            Pair<WorldDataConfiguration, CloseableResourceManager> $$52 = $$0.packConfig.createResourceManager();
            CloseableResourceManager $$6 = (CloseableResourceManager)$$52.getSecond();
            LayeredRegistryAccess<RegistryLayer> $$7 = RegistryLayer.createRegistryAccess();
            LayeredRegistryAccess<RegistryLayer> $$8 = WorldLoader.loadAndReplaceLayer($$6, $$7, RegistryLayer.WORLDGEN, RegistryDataLoader.WORLDGEN_REGISTRIES);
            RegistryAccess.Frozen $$9 = $$8.getAccessForLoading(RegistryLayer.DIMENSIONS);
            RegistryAccess.Frozen $$10 = RegistryDataLoader.load($$6, $$9, RegistryDataLoader.DIMENSION_REGISTRIES);
            WorldDataConfiguration $$11 = (WorldDataConfiguration)((Object)$$52.getFirst());
            DataLoadOutput<D> $$122 = $$12.get(new DataLoadContext($$6, $$11, $$9, $$10));
            LayeredRegistryAccess<RegistryLayer> $$13 = $$8.replaceFrom(RegistryLayer.DIMENSIONS, $$122.finalDimensions);
            RegistryAccess.Frozen $$14 = $$13.getAccessForLoading(RegistryLayer.RELOADABLE);
            return ReloadableServerResources.loadResources($$6, $$14, $$11.enabledFeatures(), $$0.commandSelection(), $$0.functionCompilationLevel(), $$3, $$4).whenComplete(($$1, $$2) -> {
                if ($$2 != null) {
                    $$6.close();
                }
            }).thenApplyAsync($$5 -> {
                $$5.updateRegistryTags($$14);
                return $$22.create($$6, (ReloadableServerResources)$$5, $$13, $$4.cookie);
            }, $$4);
        }
        catch (Exception $$15) {
            return CompletableFuture.failedFuture((Throwable)$$15);
        }
    }

    private static RegistryAccess.Frozen loadLayer(ResourceManager $$0, LayeredRegistryAccess<RegistryLayer> $$1, RegistryLayer $$2, List<RegistryDataLoader.RegistryData<?>> $$3) {
        RegistryAccess.Frozen $$4 = $$1.getAccessForLoading($$2);
        return RegistryDataLoader.load($$0, $$4, $$3);
    }

    private static LayeredRegistryAccess<RegistryLayer> loadAndReplaceLayer(ResourceManager $$0, LayeredRegistryAccess<RegistryLayer> $$1, RegistryLayer $$2, List<RegistryDataLoader.RegistryData<?>> $$3) {
        RegistryAccess.Frozen $$4 = WorldLoader.loadLayer($$0, $$1, $$2, $$3);
        return $$1.replaceFrom($$2, $$4);
    }

    public record InitConfig(PackConfig packConfig, Commands.CommandSelection commandSelection, int functionCompilationLevel) {
    }

    public record PackConfig(PackRepository packRepository, WorldDataConfiguration initialDataConfig, boolean safeMode, boolean initMode) {
        public Pair<WorldDataConfiguration, CloseableResourceManager> createResourceManager() {
            FeatureFlagSet $$0 = this.initMode ? FeatureFlags.REGISTRY.allFlags() : this.initialDataConfig.enabledFeatures();
            WorldDataConfiguration $$1 = MinecraftServer.configurePackRepository(this.packRepository, this.initialDataConfig.dataPacks(), this.safeMode, $$0);
            if (!this.initMode) {
                $$1 = $$1.expandFeatures(this.initialDataConfig.enabledFeatures());
            }
            List<PackResources> $$2 = this.packRepository.openAllSelected();
            MultiPackResourceManager $$3 = new MultiPackResourceManager(PackType.SERVER_DATA, $$2);
            return Pair.of((Object)((Object)$$1), (Object)$$3);
        }
    }

    public record DataLoadContext(ResourceManager resources, WorldDataConfiguration dataConfiguration, RegistryAccess.Frozen datapackWorldgen, RegistryAccess.Frozen datapackDimensions) {
    }

    @FunctionalInterface
    public static interface WorldDataSupplier<D> {
        public DataLoadOutput<D> get(DataLoadContext var1);
    }

    public record DataLoadOutput<D>(D cookie, RegistryAccess.Frozen finalDimensions) {
    }

    @FunctionalInterface
    public static interface ResultFactory<D, R> {
        public R create(CloseableResourceManager var1, ReloadableServerResources var2, LayeredRegistryAccess<RegistryLayer> var3, D var4);
    }
}