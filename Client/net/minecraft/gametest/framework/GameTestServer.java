/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Stopwatch
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Lifecycle
 *  java.lang.Exception
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.System
 *  java.lang.Thread
 *  java.lang.Throwable
 *  java.net.Proxy
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.List
 *  java.util.concurrent.Executor
 *  java.util.concurrent.TimeUnit
 *  java.util.function.BooleanSupplier
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.gametest.framework;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.SystemReport;
import net.minecraft.Util;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestBatch;
import net.minecraft.gametest.framework.GameTestInfo;
import net.minecraft.gametest.framework.GameTestRunner;
import net.minecraft.gametest.framework.GameTestTicker;
import net.minecraft.gametest.framework.GlobalTestReporter;
import net.minecraft.gametest.framework.MultipleTestTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.LoggerChunkProgressListener;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.SignatureValidator;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.Difficulty;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import org.slf4j.Logger;

public class GameTestServer
extends MinecraftServer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int PROGRESS_REPORT_INTERVAL = 20;
    private static final Services NO_SERVICES = new Services(null, SignatureValidator.NO_VALIDATION, null, null);
    private final List<GameTestBatch> testBatches;
    private final BlockPos spawnPos;
    private static final GameRules TEST_GAME_RULES = Util.make(new GameRules(), $$0 -> {
        $$0.getRule(GameRules.RULE_DOMOBSPAWNING).set(false, null);
        $$0.getRule(GameRules.RULE_WEATHER_CYCLE).set(false, null);
    });
    private static final WorldOptions WORLD_OPTIONS = new WorldOptions(0L, false, false);
    @Nullable
    private MultipleTestTracker testTracker;

    public static GameTestServer create(Thread $$0, LevelStorageSource.LevelStorageAccess $$1, PackRepository $$22, Collection<GameTestBatch> $$3, BlockPos $$4) {
        if ($$3.isEmpty()) {
            throw new IllegalArgumentException("No test batches were given!");
        }
        $$22.reload();
        WorldDataConfiguration $$5 = new WorldDataConfiguration(new DataPackConfig((List<String>)new ArrayList($$22.getAvailableIds()), (List<String>)List.of()), FeatureFlags.REGISTRY.allFlags());
        LevelSettings $$6 = new LevelSettings("Test Level", GameType.CREATIVE, false, Difficulty.NORMAL, true, TEST_GAME_RULES, $$5);
        WorldLoader.PackConfig $$7 = new WorldLoader.PackConfig($$22, $$5, false, true);
        WorldLoader.InitConfig $$8 = new WorldLoader.InitConfig($$7, Commands.CommandSelection.DEDICATED, 4);
        try {
            LOGGER.debug("Starting resource loading");
            Stopwatch $$9 = Stopwatch.createStarted();
            WorldStem $$10 = (WorldStem)((Object)Util.blockUntilDone($$2 -> WorldLoader.load($$8, $$1 -> {
                Object $$2 = new MappedRegistry<LevelStem>(Registries.LEVEL_STEM, Lifecycle.stable()).freeze();
                WorldDimensions.Complete $$3 = $$1.datapackWorldgen().registryOrThrow(Registries.WORLD_PRESET).getHolderOrThrow(WorldPresets.FLAT).value().createWorldDimensions().bake((Registry<LevelStem>)$$2);
                return new WorldLoader.DataLoadOutput<PrimaryLevelData>(new PrimaryLevelData($$6, WORLD_OPTIONS, $$3.specialWorldProperty(), $$3.lifecycle()), $$3.dimensionsRegistryAccess());
            }, WorldStem::new, (Executor)Util.backgroundExecutor(), $$2)).get());
            $$9.stop();
            LOGGER.debug("Finished resource loading after {} ms", (Object)$$9.elapsed(TimeUnit.MILLISECONDS));
            return new GameTestServer($$0, $$1, $$22, $$10, $$3, $$4);
        }
        catch (Exception $$11) {
            LOGGER.warn("Failed to load vanilla datapack, bit oops", (Throwable)$$11);
            System.exit((int)-1);
            throw new IllegalStateException();
        }
    }

    private GameTestServer(Thread $$0, LevelStorageSource.LevelStorageAccess $$1, PackRepository $$2, WorldStem $$3, Collection<GameTestBatch> $$4, BlockPos $$5) {
        super($$0, $$1, $$2, $$3, Proxy.NO_PROXY, DataFixers.getDataFixer(), NO_SERVICES, LoggerChunkProgressListener::new);
        this.testBatches = Lists.newArrayList($$4);
        this.spawnPos = $$5;
    }

    @Override
    public boolean initServer() {
        this.setPlayerList(new PlayerList(this, this.registries(), this.playerDataStorage, 1){});
        this.loadLevel();
        ServerLevel $$0 = this.overworld();
        $$0.setDefaultSpawnPos(this.spawnPos, 0.0f);
        int $$1 = 20000000;
        $$0.setWeatherParameters(20000000, 20000000, false, false);
        LOGGER.info("Started game test server");
        return true;
    }

    @Override
    public void tickServer(BooleanSupplier $$02) {
        super.tickServer($$02);
        ServerLevel $$1 = this.overworld();
        if (!this.haveTestsStarted()) {
            this.startTests($$1);
        }
        if ($$1.getGameTime() % 20L == 0L) {
            LOGGER.info(this.testTracker.getProgressBar());
        }
        if (this.testTracker.isDone()) {
            this.halt(false);
            LOGGER.info(this.testTracker.getProgressBar());
            GlobalTestReporter.finish();
            LOGGER.info("========= {} GAME TESTS COMPLETE ======================", (Object)this.testTracker.getTotalCount());
            if (this.testTracker.hasFailedRequired()) {
                LOGGER.info("{} required tests failed :(", (Object)this.testTracker.getFailedRequiredCount());
                this.testTracker.getFailedRequired().forEach($$0 -> LOGGER.info("   - {}", (Object)$$0.getTestName()));
            } else {
                LOGGER.info("All {} required tests passed :)", (Object)this.testTracker.getTotalCount());
            }
            if (this.testTracker.hasFailedOptional()) {
                LOGGER.info("{} optional tests failed", (Object)this.testTracker.getFailedOptionalCount());
                this.testTracker.getFailedOptional().forEach($$0 -> LOGGER.info("   - {}", (Object)$$0.getTestName()));
            }
            LOGGER.info("====================================================");
        }
    }

    @Override
    public void waitUntilNextTick() {
        this.runAllTasks();
    }

    @Override
    public SystemReport fillServerSystemReport(SystemReport $$0) {
        $$0.setDetail("Type", "Game test server");
        return $$0;
    }

    @Override
    public void onServerExit() {
        super.onServerExit();
        LOGGER.info("Game test server shutting down");
        System.exit((int)this.testTracker.getFailedRequiredCount());
    }

    @Override
    public void onServerCrash(CrashReport $$0) {
        super.onServerCrash($$0);
        LOGGER.error("Game test server crashed\n{}", (Object)$$0.getFriendlyReport());
        System.exit((int)1);
    }

    private void startTests(ServerLevel $$0) {
        Collection<GameTestInfo> $$1 = GameTestRunner.runTestBatches(this.testBatches, new BlockPos(0, -60, 0), Rotation.NONE, $$0, GameTestTicker.SINGLETON, 8);
        this.testTracker = new MultipleTestTracker($$1);
        LOGGER.info("{} tests are now running!", (Object)this.testTracker.getTotalCount());
    }

    private boolean haveTestsStarted() {
        return this.testTracker != null;
    }

    @Override
    public boolean isHardcore() {
        return false;
    }

    @Override
    public int getOperatorUserPermissionLevel() {
        return 0;
    }

    @Override
    public int getFunctionCompilationLevel() {
        return 4;
    }

    @Override
    public boolean shouldRconBroadcast() {
        return false;
    }

    @Override
    public boolean isDedicatedServer() {
        return false;
    }

    @Override
    public int getRateLimitPacketsPerSecond() {
        return 0;
    }

    @Override
    public boolean isEpollEnabled() {
        return false;
    }

    @Override
    public boolean isCommandBlockEnabled() {
        return true;
    }

    @Override
    public boolean isPublished() {
        return false;
    }

    @Override
    public boolean shouldInformAdmins() {
        return false;
    }

    @Override
    public boolean isSingleplayerOwner(GameProfile $$0) {
        return false;
    }
}