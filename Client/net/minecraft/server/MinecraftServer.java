/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.GameProfileRepository
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  java.awt.image.BufferedImage
 *  java.awt.image.RenderedImage
 *  java.io.BufferedWriter
 *  java.io.ByteArrayOutputStream
 *  java.io.File
 *  java.io.IOException
 *  java.io.OutputStream
 *  java.lang.AutoCloseable
 *  java.lang.Boolean
 *  java.lang.CharSequence
 *  java.lang.Exception
 *  java.lang.Float
 *  java.lang.IllegalStateException
 *  java.lang.InterruptedException
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.NullPointerException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.lang.Runtime
 *  java.lang.String
 *  java.lang.System
 *  java.lang.Thread
 *  java.lang.Throwable
 *  java.lang.Void
 *  java.lang.management.ManagementFactory
 *  java.lang.management.ThreadInfo
 *  java.lang.management.ThreadMXBean
 *  java.net.Proxy
 *  java.nio.charset.StandardCharsets
 *  java.nio.file.Files
 *  java.nio.file.OpenOption
 *  java.nio.file.Path
 *  java.nio.file.attribute.FileAttribute
 *  java.security.KeyPair
 *  java.util.ArrayList
 *  java.util.Arrays
 *  java.util.Base64
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.Comparator
 *  java.util.LinkedHashSet
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.Set
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.concurrent.RejectedExecutionException
 *  java.util.concurrent.atomic.AtomicReference
 *  java.util.function.BooleanSupplier
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  java.util.function.Supplier
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 *  javax.imageio.ImageIO
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 */
package net.minecraft.server;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.SystemReport;
import net.minecraft.Util;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.gametest.framework.GameTestTicker;
import net.minecraft.network.chat.ChatDecorator;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.Services;
import net.minecraft.server.TickTask;
import net.minecraft.server.WorldStem;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.server.level.DemoMode;
import net.minecraft.server.level.PlayerRespawnLogic;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.network.ServerConnectionListener;
import net.minecraft.server.network.TextFilter;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.util.Crypt;
import net.minecraft.util.CryptException;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.ModCheck;
import net.minecraft.util.Mth;
import net.minecraft.util.NativeModuleLister;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SignatureValidator;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.EmptyProfileResults;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.ResultField;
import net.minecraft.util.profiling.SingleTickProfiler;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.profiling.jfr.callback.ProfiledDuration;
import net.minecraft.util.profiling.metrics.profiling.ActiveMetricsRecorder;
import net.minecraft.util.profiling.metrics.profiling.InactiveMetricsRecorder;
import net.minecraft.util.profiling.metrics.profiling.MetricsRecorder;
import net.minecraft.util.profiling.metrics.profiling.ServerMetricsSamplersProvider;
import net.minecraft.util.profiling.metrics.storage.MetricsPersister;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.ai.village.VillageSiege;
import net.minecraft.world.entity.npc.CatSpawner;
import net.minecraft.world.entity.npc.WanderingTraderSpawner;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.DataPackConfig;
import net.minecraft.world.level.ForcedChunksSavedData;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.PatrolSpawner;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.CommandStorage;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.storage.loot.ItemModifierManager;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public abstract class MinecraftServer
extends ReentrantBlockableEventLoop<TickTask>
implements CommandSource,
AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String VANILLA_BRAND = "vanilla";
    private static final float AVERAGE_TICK_TIME_SMOOTHING = 0.8f;
    private static final int TICK_STATS_SPAN = 100;
    public static final int MS_PER_TICK = 50;
    private static final int OVERLOADED_THRESHOLD = 2000;
    private static final int OVERLOADED_WARNING_INTERVAL = 15000;
    private static final long STATUS_EXPIRE_TIME_NS = 5000000000L;
    private static final int MAX_STATUS_PLAYER_SAMPLE = 12;
    public static final int START_CHUNK_RADIUS = 11;
    private static final int START_TICKING_CHUNK_COUNT = 441;
    private static final int AUTOSAVE_INTERVAL = 6000;
    private static final int MAX_TICK_LATENCY = 3;
    public static final int ABSOLUTE_MAX_WORLD_SIZE = 29999984;
    public static final LevelSettings DEMO_SETTINGS = new LevelSettings("Demo World", GameType.SURVIVAL, false, Difficulty.NORMAL, false, new GameRules(), WorldDataConfiguration.DEFAULT);
    private static final long DELAYED_TASKS_TICK_EXTENSION = 50L;
    public static final GameProfile ANONYMOUS_PLAYER_PROFILE = new GameProfile(Util.NIL_UUID, "Anonymous Player");
    protected final LevelStorageSource.LevelStorageAccess storageSource;
    protected final PlayerDataStorage playerDataStorage;
    private final List<Runnable> tickables = Lists.newArrayList();
    private MetricsRecorder metricsRecorder = InactiveMetricsRecorder.INSTANCE;
    private ProfilerFiller profiler = this.metricsRecorder.getProfiler();
    private Consumer<ProfileResults> onMetricsRecordingStopped = $$0 -> this.stopRecordingMetrics();
    private Consumer<Path> onMetricsRecordingFinished = $$0 -> {};
    private boolean willStartRecordingMetrics;
    @Nullable
    private TimeProfiler debugCommandProfiler;
    private boolean debugCommandProfilerDelayStart;
    private final ServerConnectionListener connection;
    private final ChunkProgressListenerFactory progressListenerFactory;
    private final ServerStatus status = new ServerStatus();
    private final RandomSource random = RandomSource.create();
    private final DataFixer fixerUpper;
    private String localIp;
    private int port = -1;
    private final LayeredRegistryAccess<RegistryLayer> registries;
    private final Map<ResourceKey<Level>, ServerLevel> levels = Maps.newLinkedHashMap();
    private PlayerList playerList;
    private volatile boolean running = true;
    private boolean stopped;
    private int tickCount;
    protected final Proxy proxy;
    private boolean onlineMode;
    private boolean preventProxyConnections;
    private boolean pvp;
    private boolean allowFlight;
    @Nullable
    private String motd;
    private int playerIdleTimeout;
    public final long[] tickTimes = new long[100];
    @Nullable
    private KeyPair keyPair;
    @Nullable
    private GameProfile singleplayerProfile;
    private boolean isDemo;
    private volatile boolean isReady;
    private long lastOverloadWarning;
    protected final Services services;
    private long lastServerStatus;
    private final Thread serverThread;
    private long nextTickTime = Util.getMillis();
    private long delayedTasksMaxNextTickTime;
    private boolean mayHaveDelayedTasks;
    private final PackRepository packRepository;
    private final ServerScoreboard scoreboard = new ServerScoreboard(this);
    @Nullable
    private CommandStorage commandStorage;
    private final CustomBossEvents customBossEvents = new CustomBossEvents();
    private final ServerFunctionManager functionManager;
    private final FrameTimer frameTimer = new FrameTimer();
    private boolean enforceWhitelist;
    private float averageTickTime;
    private final Executor executor;
    @Nullable
    private String serverId;
    private ReloadableResources resources;
    private final StructureTemplateManager structureTemplateManager;
    protected final WorldData worldData;
    private volatile boolean isSaving;

    public static <S extends MinecraftServer> S spin(Function<Thread, S> $$02) {
        AtomicReference $$12 = new AtomicReference();
        Thread $$2 = new Thread(() -> ((MinecraftServer)$$12.get()).runServer(), "Server thread");
        $$2.setUncaughtExceptionHandler(($$0, $$1) -> LOGGER.error("Uncaught exception in server thread", $$1));
        if (Runtime.getRuntime().availableProcessors() > 4) {
            $$2.setPriority(8);
        }
        MinecraftServer $$3 = (MinecraftServer)$$02.apply((Object)$$2);
        $$12.set((Object)$$3);
        $$2.start();
        return (S)$$3;
    }

    public MinecraftServer(Thread $$02, LevelStorageSource.LevelStorageAccess $$1, PackRepository $$2, WorldStem $$3, Proxy $$4, DataFixer $$5, Services $$6, ChunkProgressListenerFactory $$7) {
        super("Server");
        this.registries = $$3.registries();
        this.worldData = $$3.worldData();
        if (!this.registries.compositeAccess().registryOrThrow(Registries.LEVEL_STEM).containsKey(LevelStem.OVERWORLD)) {
            throw new IllegalStateException("Missing Overworld dimension data");
        }
        this.proxy = $$4;
        this.packRepository = $$2;
        this.resources = new ReloadableResources($$3.resourceManager(), $$3.dataPackResources());
        this.services = $$6;
        if ($$6.profileCache() != null) {
            $$6.profileCache().setExecutor(this);
        }
        this.connection = new ServerConnectionListener(this);
        this.progressListenerFactory = $$7;
        this.storageSource = $$1;
        this.playerDataStorage = $$1.createPlayerStorage();
        this.fixerUpper = $$5;
        this.functionManager = new ServerFunctionManager(this, this.resources.managers.getFunctionLibrary());
        HolderLookup<Block> $$8 = this.registries.compositeAccess().registryOrThrow(Registries.BLOCK).asLookup().filterFeatures(this.worldData.enabledFeatures());
        this.structureTemplateManager = new StructureTemplateManager($$3.resourceManager(), $$1, $$5, $$8);
        this.serverThread = $$02;
        this.executor = Util.backgroundExecutor();
    }

    private void readScoreboard(DimensionDataStorage $$0) {
        $$0.computeIfAbsent(this.getScoreboard()::createData, this.getScoreboard()::createData, "scoreboard");
    }

    protected abstract boolean initServer() throws IOException;

    protected void loadLevel() {
        if (!JvmProfiler.INSTANCE.isRunning()) {
            // empty if block
        }
        boolean $$0 = false;
        ProfiledDuration $$1 = JvmProfiler.INSTANCE.onWorldLoadedStarted();
        this.worldData.setModdedInfo(this.getServerModName(), this.getModdedStatus().shouldReportAsModified());
        ChunkProgressListener $$2 = this.progressListenerFactory.create(11);
        this.createLevels($$2);
        this.forceDifficulty();
        this.prepareLevels($$2);
        if ($$1 != null) {
            $$1.finish();
        }
        if ($$0) {
            try {
                JvmProfiler.INSTANCE.stop();
            }
            catch (Throwable $$3) {
                LOGGER.warn("Failed to stop JFR profiling", $$3);
            }
        }
    }

    protected void forceDifficulty() {
    }

    protected void createLevels(ChunkProgressListener $$0) {
        ServerLevelData $$1 = this.worldData.overworldData();
        boolean $$2 = this.worldData.isDebugWorld();
        Registry $$3 = this.registries.compositeAccess().registryOrThrow(Registries.LEVEL_STEM);
        WorldOptions $$4 = this.worldData.worldGenOptions();
        long $$5 = $$4.seed();
        long $$6 = BiomeManager.obfuscateSeed($$5);
        ImmutableList $$7 = ImmutableList.of((Object)new PhantomSpawner(), (Object)new PatrolSpawner(), (Object)new CatSpawner(), (Object)new VillageSiege(), (Object)new WanderingTraderSpawner($$1));
        LevelStem $$8 = $$3.get(LevelStem.OVERWORLD);
        ServerLevel $$9 = new ServerLevel(this, this.executor, this.storageSource, $$1, Level.OVERWORLD, $$8, $$0, $$2, $$6, (List<CustomSpawner>)$$7, true);
        this.levels.put(Level.OVERWORLD, (Object)$$9);
        DimensionDataStorage $$10 = $$9.getDataStorage();
        this.readScoreboard($$10);
        this.commandStorage = new CommandStorage($$10);
        WorldBorder $$11 = $$9.getWorldBorder();
        if (!$$1.isInitialized()) {
            try {
                MinecraftServer.setInitialSpawn($$9, $$1, $$4.generateBonusChest(), $$2);
                $$1.setInitialized(true);
                if ($$2) {
                    this.setupDebugLevel(this.worldData);
                }
            }
            catch (Throwable $$12) {
                CrashReport $$13 = CrashReport.forThrowable($$12, "Exception initializing level");
                try {
                    $$9.fillReportDetails($$13);
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                throw new ReportedException($$13);
            }
            $$1.setInitialized(true);
        }
        this.getPlayerList().addWorldborderListener($$9);
        if (this.worldData.getCustomBossEvents() != null) {
            this.getCustomBossEvents().load(this.worldData.getCustomBossEvents());
        }
        for (Map.Entry $$14 : $$3.entrySet()) {
            ResourceKey $$15 = (ResourceKey)$$14.getKey();
            if ($$15 == LevelStem.OVERWORLD) continue;
            ResourceKey<Level> $$16 = ResourceKey.create(Registries.DIMENSION, $$15.location());
            DerivedLevelData $$17 = new DerivedLevelData(this.worldData, $$1);
            ServerLevel $$18 = new ServerLevel(this, this.executor, this.storageSource, $$17, $$16, (LevelStem)((Object)$$14.getValue()), $$0, $$2, $$6, (List<CustomSpawner>)ImmutableList.of(), false);
            $$11.addListener(new BorderChangeListener.DelegateBorderChangeListener($$18.getWorldBorder()));
            this.levels.put($$16, (Object)$$18);
        }
        $$11.applySettings($$1.getWorldBorder());
    }

    private static void setInitialSpawn(ServerLevel $$02, ServerLevelData $$1, boolean $$2, boolean $$32) {
        if ($$32) {
            $$1.setSpawn((BlockPos)BlockPos.ZERO.above(80), 0.0f);
            return;
        }
        ServerChunkCache $$4 = $$02.getChunkSource();
        ChunkPos $$5 = new ChunkPos($$4.randomState().sampler().findSpawnPosition());
        int $$6 = $$4.getGenerator().getSpawnHeight($$02);
        if ($$6 < $$02.getMinBuildHeight()) {
            BlockPos $$7 = $$5.getWorldPosition();
            $$6 = $$02.getHeight(Heightmap.Types.WORLD_SURFACE, $$7.getX() + 8, $$7.getZ() + 8);
        }
        $$1.setSpawn($$5.getWorldPosition().offset(8, $$6, 8), 0.0f);
        int $$8 = 0;
        int $$9 = 0;
        int $$10 = 0;
        int $$11 = -1;
        int $$12 = 5;
        for (int $$13 = 0; $$13 < Mth.square(11); ++$$13) {
            BlockPos $$14;
            if ($$8 >= -5 && $$8 <= 5 && $$9 >= -5 && $$9 <= 5 && ($$14 = PlayerRespawnLogic.getSpawnPosInChunk($$02, new ChunkPos($$5.x + $$8, $$5.z + $$9))) != null) {
                $$1.setSpawn($$14, 0.0f);
                break;
            }
            if ($$8 == $$9 || $$8 < 0 && $$8 == -$$9 || $$8 > 0 && $$8 == 1 - $$9) {
                int $$15 = $$10;
                $$10 = -$$11;
                $$11 = $$15;
            }
            $$8 += $$10;
            $$9 += $$11;
        }
        if ($$2) {
            $$02.registryAccess().registry(Registries.CONFIGURED_FEATURE).flatMap($$0 -> $$0.getHolder(MiscOverworldFeatures.BONUS_CHEST)).ifPresent($$3 -> ((ConfiguredFeature)((Object)((Object)$$3.value()))).place($$02, $$4.getGenerator(), $$0.random, new BlockPos($$1.getXSpawn(), $$1.getYSpawn(), $$1.getZSpawn())));
        }
    }

    private void setupDebugLevel(WorldData $$0) {
        $$0.setDifficulty(Difficulty.PEACEFUL);
        $$0.setDifficultyLocked(true);
        ServerLevelData $$1 = $$0.overworldData();
        $$1.setRaining(false);
        $$1.setThundering(false);
        $$1.setClearWeatherTime(1000000000);
        $$1.setDayTime(6000L);
        $$1.setGameType(GameType.SPECTATOR);
    }

    private void prepareLevels(ChunkProgressListener $$0) {
        ServerLevel $$1 = this.overworld();
        LOGGER.info("Preparing start region for dimension {}", (Object)$$1.dimension().location());
        BlockPos $$2 = $$1.getSharedSpawnPos();
        $$0.updateSpawnPos(new ChunkPos($$2));
        ServerChunkCache $$3 = $$1.getChunkSource();
        $$3.getLightEngine().setTaskPerBatch(500);
        this.nextTickTime = Util.getMillis();
        $$3.addRegionTicket(TicketType.START, new ChunkPos($$2), 11, Unit.INSTANCE);
        while ($$3.getTickingGenerated() != 441) {
            this.nextTickTime = Util.getMillis() + 10L;
            this.waitUntilNextTick();
        }
        this.nextTickTime = Util.getMillis() + 10L;
        this.waitUntilNextTick();
        for (ServerLevel $$4 : this.levels.values()) {
            ForcedChunksSavedData $$5 = (ForcedChunksSavedData)$$4.getDataStorage().get(ForcedChunksSavedData::load, "chunks");
            if ($$5 == null) continue;
            LongIterator $$6 = $$5.getChunks().iterator();
            while ($$6.hasNext()) {
                long $$7 = $$6.nextLong();
                ChunkPos $$8 = new ChunkPos($$7);
                $$4.getChunkSource().updateChunkForced($$8, true);
            }
        }
        this.nextTickTime = Util.getMillis() + 10L;
        this.waitUntilNextTick();
        $$0.stop();
        $$3.getLightEngine().setTaskPerBatch(5);
        this.updateMobSpawningFlags();
    }

    public GameType getDefaultGameType() {
        return this.worldData.getGameType();
    }

    public boolean isHardcore() {
        return this.worldData.isHardcore();
    }

    public abstract int getOperatorUserPermissionLevel();

    public abstract int getFunctionCompilationLevel();

    public abstract boolean shouldRconBroadcast();

    public boolean saveAllChunks(boolean $$0, boolean $$1, boolean $$2) {
        boolean $$3 = false;
        for (ServerLevel $$4 : this.getAllLevels()) {
            if (!$$0) {
                LOGGER.info("Saving chunks for level '{}'/{}", (Object)$$4, (Object)$$4.dimension().location());
            }
            $$4.save(null, $$1, $$4.noSave && !$$2);
            $$3 = true;
        }
        ServerLevel $$5 = this.overworld();
        ServerLevelData $$6 = this.worldData.overworldData();
        $$6.setWorldBorder($$5.getWorldBorder().createSettings());
        this.worldData.setCustomBossEvents(this.getCustomBossEvents().save());
        this.storageSource.saveDataTag(this.registryAccess(), this.worldData, this.getPlayerList().getSingleplayerData());
        if ($$1) {
            for (ServerLevel $$7 : this.getAllLevels()) {
                LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", (Object)$$7.getChunkSource().chunkMap.getStorageName());
            }
            LOGGER.info("ThreadedAnvilChunkStorage: All dimensions are saved");
        }
        return $$3;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean saveEverything(boolean $$0, boolean $$1, boolean $$2) {
        try {
            this.isSaving = true;
            this.getPlayerList().saveAll();
            boolean bl = this.saveAllChunks($$0, $$1, $$2);
            return bl;
        }
        finally {
            this.isSaving = false;
        }
    }

    @Override
    public void close() {
        this.stopServer();
    }

    public void stopServer() {
        if (this.metricsRecorder.isRecording()) {
            this.cancelRecordingMetrics();
        }
        LOGGER.info("Stopping server");
        if (this.getConnection() != null) {
            this.getConnection().stop();
        }
        this.isSaving = true;
        if (this.playerList != null) {
            LOGGER.info("Saving players");
            this.playerList.saveAll();
            this.playerList.removeAll();
        }
        LOGGER.info("Saving worlds");
        for (ServerLevel $$02 : this.getAllLevels()) {
            if ($$02 == null) continue;
            $$02.noSave = false;
        }
        while (this.levels.values().stream().anyMatch($$0 -> $$0.getChunkSource().chunkMap.hasWork())) {
            this.nextTickTime = Util.getMillis() + 1L;
            for (ServerLevel $$1 : this.getAllLevels()) {
                $$1.getChunkSource().removeTicketsOnClosing();
                $$1.getChunkSource().tick(() -> true, false);
            }
            this.waitUntilNextTick();
        }
        this.saveAllChunks(false, true, false);
        for (ServerLevel $$2 : this.getAllLevels()) {
            if ($$2 == null) continue;
            try {
                $$2.close();
            }
            catch (IOException $$3) {
                LOGGER.error("Exception closing the level", (Throwable)$$3);
            }
        }
        this.isSaving = false;
        this.resources.close();
        try {
            this.storageSource.close();
        }
        catch (IOException $$4) {
            LOGGER.error("Failed to unlock level {}", (Object)this.storageSource.getLevelId(), (Object)$$4);
        }
    }

    public String getLocalIp() {
        return this.localIp;
    }

    public void setLocalIp(String $$0) {
        this.localIp = $$0;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void halt(boolean $$0) {
        this.running = false;
        if ($$0) {
            try {
                this.serverThread.join();
            }
            catch (InterruptedException $$1) {
                LOGGER.error("Error while shutting down", (Throwable)$$1);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void runServer() {
        block25: {
            try {
                if (this.initServer()) {
                    this.nextTickTime = Util.getMillis();
                    this.status.setDescription(Component.literal(this.motd));
                    this.status.setVersion(new ServerStatus.Version(SharedConstants.getCurrentVersion().getName(), SharedConstants.getCurrentVersion().getProtocolVersion()));
                    this.status.setEnforcesSecureChat(this.enforceSecureProfile());
                    this.updateStatusIcon(this.status);
                    while (this.running) {
                        long $$0 = Util.getMillis() - this.nextTickTime;
                        if ($$0 > 2000L && this.nextTickTime - this.lastOverloadWarning >= 15000L) {
                            long $$1 = $$0 / 50L;
                            LOGGER.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", (Object)$$0, (Object)$$1);
                            this.nextTickTime += $$1 * 50L;
                            this.lastOverloadWarning = this.nextTickTime;
                        }
                        if (this.debugCommandProfilerDelayStart) {
                            this.debugCommandProfilerDelayStart = false;
                            this.debugCommandProfiler = new TimeProfiler(Util.getNanos(), this.tickCount);
                        }
                        this.nextTickTime += 50L;
                        this.startMetricsRecordingTick();
                        this.profiler.push("tick");
                        this.tickServer(this::haveTime);
                        this.profiler.popPush("nextTickWait");
                        this.mayHaveDelayedTasks = true;
                        this.delayedTasksMaxNextTickTime = Math.max((long)(Util.getMillis() + 50L), (long)this.nextTickTime);
                        this.waitUntilNextTick();
                        this.profiler.pop();
                        this.endMetricsRecordingTick();
                        this.isReady = true;
                        JvmProfiler.INSTANCE.onServerTick(this.averageTickTime);
                    }
                    break block25;
                }
                throw new IllegalStateException("Failed to initialize server");
            }
            catch (Throwable $$3) {
                LOGGER.error("Encountered an unexpected exception", $$3);
                CrashReport $$4 = MinecraftServer.constructOrExtractCrashReport($$3);
                this.fillSystemReport($$4.getSystemReport());
                File $$5 = new File(new File(this.getServerDirectory(), "crash-reports"), "crash-" + Util.getFilenameFormattedDateTime() + "-server.txt");
                if ($$4.saveToFile($$5)) {
                    LOGGER.error("This crash report has been saved to: {}", (Object)$$5.getAbsolutePath());
                } else {
                    LOGGER.error("We were unable to save this crash report to disk.");
                }
                this.onServerCrash($$4);
            }
            finally {
                try {
                    this.stopped = true;
                    this.stopServer();
                }
                catch (Throwable $$2) {
                    LOGGER.error("Exception stopping the server", $$2);
                }
                finally {
                    if (this.services.profileCache() != null) {
                        this.services.profileCache().clearExecutor();
                    }
                    this.onServerExit();
                }
            }
        }
    }

    private static CrashReport constructOrExtractCrashReport(Throwable $$0) {
        CrashReport $$5;
        ReportedException $$1 = null;
        for (Throwable $$2 = $$0; $$2 != null; $$2 = $$2.getCause()) {
            ReportedException $$3;
            if (!($$2 instanceof ReportedException)) continue;
            $$1 = $$3 = (ReportedException)((Object)$$2);
        }
        if ($$1 != null) {
            CrashReport $$4 = $$1.getReport();
            if ($$1 != $$0) {
                $$4.addCategory("Wrapped in").setDetailError("Wrapping exception", $$0);
            }
        } else {
            $$5 = new CrashReport("Exception in server tick loop", $$0);
        }
        return $$5;
    }

    private boolean haveTime() {
        return this.runningTask() || Util.getMillis() < (this.mayHaveDelayedTasks ? this.delayedTasksMaxNextTickTime : this.nextTickTime);
    }

    protected void waitUntilNextTick() {
        this.runAllTasks();
        this.managedBlock(() -> !this.haveTime());
    }

    @Override
    protected TickTask wrapRunnable(Runnable $$0) {
        return new TickTask(this.tickCount, $$0);
    }

    @Override
    protected boolean shouldRun(TickTask $$0) {
        return $$0.getTick() + 3 < this.tickCount || this.haveTime();
    }

    @Override
    public boolean pollTask() {
        boolean $$0;
        this.mayHaveDelayedTasks = $$0 = this.pollTaskInternal();
        return $$0;
    }

    private boolean pollTaskInternal() {
        if (super.pollTask()) {
            return true;
        }
        if (this.haveTime()) {
            for (ServerLevel $$0 : this.getAllLevels()) {
                if (!$$0.getChunkSource().pollTask()) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doRunTask(TickTask $$0) {
        this.getProfiler().incrementCounter("runTask");
        super.doRunTask($$0);
    }

    private void updateStatusIcon(ServerStatus $$0) {
        Optional $$12 = Optional.of((Object)this.getFile("server-icon.png")).filter(File::isFile);
        if (!$$12.isPresent()) {
            $$12 = this.storageSource.getIconFile().map(Path::toFile).filter(File::isFile);
        }
        $$12.ifPresent($$1 -> {
            try {
                BufferedImage $$2 = ImageIO.read((File)$$1);
                Validate.validState(($$2.getWidth() == 64 ? 1 : 0) != 0, (String)"Must be 64 pixels wide", (Object[])new Object[0]);
                Validate.validState(($$2.getHeight() == 64 ? 1 : 0) != 0, (String)"Must be 64 pixels high", (Object[])new Object[0]);
                ByteArrayOutputStream $$3 = new ByteArrayOutputStream();
                ImageIO.write((RenderedImage)$$2, (String)"PNG", (OutputStream)$$3);
                byte[] $$4 = Base64.getEncoder().encode($$3.toByteArray());
                $$0.setFavicon("data:image/png;base64," + new String($$4, StandardCharsets.UTF_8));
            }
            catch (Exception $$5) {
                LOGGER.error("Couldn't load server icon", (Throwable)$$5);
            }
        });
    }

    public Optional<Path> getWorldScreenshotFile() {
        return this.storageSource.getIconFile();
    }

    public File getServerDirectory() {
        return new File(".");
    }

    public void onServerCrash(CrashReport $$0) {
    }

    public void onServerExit() {
    }

    public void tickServer(BooleanSupplier $$0) {
        long $$1 = Util.getNanos();
        ++this.tickCount;
        this.tickChildren($$0);
        if ($$1 - this.lastServerStatus >= 5000000000L) {
            this.lastServerStatus = $$1;
            this.status.setPlayers(new ServerStatus.Players(this.getMaxPlayers(), this.getPlayerCount()));
            if (!this.hidesOnlinePlayers()) {
                Object[] $$2 = new GameProfile[Math.min((int)this.getPlayerCount(), (int)12)];
                int $$3 = Mth.nextInt(this.random, 0, this.getPlayerCount() - $$2.length);
                for (int $$4 = 0; $$4 < $$2.length; ++$$4) {
                    ServerPlayer $$5 = (ServerPlayer)this.playerList.getPlayers().get($$3 + $$4);
                    $$2[$$4] = $$5.allowsListing() ? $$5.getGameProfile() : ANONYMOUS_PLAYER_PROFILE;
                }
                Collections.shuffle((List)Arrays.asList((Object[])$$2));
                this.status.getPlayers().setSample((GameProfile[])$$2);
            }
        }
        if (this.tickCount % 6000 == 0) {
            LOGGER.debug("Autosave started");
            this.profiler.push("save");
            this.saveEverything(true, false, false);
            this.profiler.pop();
            LOGGER.debug("Autosave finished");
        }
        this.profiler.push("tallying");
        long l = Util.getNanos() - $$1;
        this.tickTimes[this.tickCount % 100] = l;
        long $$6 = l;
        this.averageTickTime = this.averageTickTime * 0.8f + (float)$$6 / 1000000.0f * 0.19999999f;
        long $$7 = Util.getNanos();
        this.frameTimer.logFrameDuration($$7 - $$1);
        this.profiler.pop();
    }

    public void tickChildren(BooleanSupplier $$0) {
        this.profiler.push("commandFunctions");
        this.getFunctions().tick();
        this.profiler.popPush("levels");
        for (ServerLevel $$1 : this.getAllLevels()) {
            this.profiler.push((Supplier<String>)((Supplier)() -> $$1 + " " + $$1.dimension().location()));
            if (this.tickCount % 20 == 0) {
                this.profiler.push("timeSync");
                this.playerList.broadcastAll(new ClientboundSetTimePacket($$1.getGameTime(), $$1.getDayTime(), $$1.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)), $$1.dimension());
                this.profiler.pop();
            }
            this.profiler.push("tick");
            try {
                $$1.tick($$0);
            }
            catch (Throwable $$2) {
                CrashReport $$3 = CrashReport.forThrowable($$2, "Exception ticking world");
                $$1.fillReportDetails($$3);
                throw new ReportedException($$3);
            }
            this.profiler.pop();
            this.profiler.pop();
        }
        this.profiler.popPush("connection");
        this.getConnection().tick();
        this.profiler.popPush("players");
        this.playerList.tick();
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            GameTestTicker.SINGLETON.tick();
        }
        this.profiler.popPush("server gui refresh");
        for (int $$4 = 0; $$4 < this.tickables.size(); ++$$4) {
            ((Runnable)this.tickables.get($$4)).run();
        }
        this.profiler.pop();
    }

    public boolean isNetherEnabled() {
        return true;
    }

    public void addTickable(Runnable $$0) {
        this.tickables.add((Object)$$0);
    }

    protected void setId(String $$0) {
        this.serverId = $$0;
    }

    public boolean isShutdown() {
        return !this.serverThread.isAlive();
    }

    public File getFile(String $$0) {
        return new File(this.getServerDirectory(), $$0);
    }

    public final ServerLevel overworld() {
        return (ServerLevel)this.levels.get(Level.OVERWORLD);
    }

    @Nullable
    public ServerLevel getLevel(ResourceKey<Level> $$0) {
        return (ServerLevel)this.levels.get($$0);
    }

    public Set<ResourceKey<Level>> levelKeys() {
        return this.levels.keySet();
    }

    public Iterable<ServerLevel> getAllLevels() {
        return this.levels.values();
    }

    public String getServerVersion() {
        return SharedConstants.getCurrentVersion().getName();
    }

    public int getPlayerCount() {
        return this.playerList.getPlayerCount();
    }

    public int getMaxPlayers() {
        return this.playerList.getMaxPlayers();
    }

    public String[] getPlayerNames() {
        return this.playerList.getPlayerNamesArray();
    }

    @DontObfuscate
    public String getServerModName() {
        return VANILLA_BRAND;
    }

    public SystemReport fillSystemReport(SystemReport $$0) {
        $$0.setDetail("Server Running", (Supplier<String>)((Supplier)() -> Boolean.toString((boolean)this.running)));
        if (this.playerList != null) {
            $$0.setDetail("Player Count", (Supplier<String>)((Supplier)() -> this.playerList.getPlayerCount() + " / " + this.playerList.getMaxPlayers() + "; " + this.playerList.getPlayers()));
        }
        $$0.setDetail("Data Packs", (Supplier<String>)((Supplier)() -> (String)this.packRepository.getSelectedPacks().stream().map($$0 -> $$0.getId() + ($$0.getCompatibility().isCompatible() ? "" : " (incompatible)")).collect(Collectors.joining((CharSequence)", "))));
        $$0.setDetail("Enabled Feature Flags", (Supplier<String>)((Supplier)() -> (String)FeatureFlags.REGISTRY.toNames(this.worldData.enabledFeatures()).stream().map(ResourceLocation::toString).collect(Collectors.joining((CharSequence)", "))));
        $$0.setDetail("World Generation", (Supplier<String>)((Supplier)() -> this.worldData.worldGenSettingsLifecycle().toString()));
        if (this.serverId != null) {
            $$0.setDetail("Server Id", (Supplier<String>)((Supplier)() -> this.serverId));
        }
        return this.fillServerSystemReport($$0);
    }

    public abstract SystemReport fillServerSystemReport(SystemReport var1);

    public ModCheck getModdedStatus() {
        return ModCheck.identify(VANILLA_BRAND, (Supplier<String>)((Supplier)this::getServerModName), "Server", MinecraftServer.class);
    }

    @Override
    public void sendSystemMessage(Component $$0) {
        LOGGER.info($$0.getString());
    }

    public KeyPair getKeyPair() {
        return this.keyPair;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int $$0) {
        this.port = $$0;
    }

    @Nullable
    public GameProfile getSingleplayerProfile() {
        return this.singleplayerProfile;
    }

    public void setSingleplayerProfile(@Nullable GameProfile $$0) {
        this.singleplayerProfile = $$0;
    }

    public boolean isSingleplayer() {
        return this.singleplayerProfile != null;
    }

    protected void initializeKeyPair() {
        LOGGER.info("Generating keypair");
        try {
            this.keyPair = Crypt.generateKeyPair();
        }
        catch (CryptException $$0) {
            throw new IllegalStateException("Failed to generate key pair", (Throwable)((Object)$$0));
        }
    }

    public void setDifficulty(Difficulty $$0, boolean $$1) {
        if (!$$1 && this.worldData.isDifficultyLocked()) {
            return;
        }
        this.worldData.setDifficulty(this.worldData.isHardcore() ? Difficulty.HARD : $$0);
        this.updateMobSpawningFlags();
        this.getPlayerList().getPlayers().forEach(this::sendDifficultyUpdate);
    }

    public int getScaledTrackingDistance(int $$0) {
        return $$0;
    }

    private void updateMobSpawningFlags() {
        for (ServerLevel $$0 : this.getAllLevels()) {
            $$0.setSpawnSettings(this.isSpawningMonsters(), this.isSpawningAnimals());
        }
    }

    public void setDifficultyLocked(boolean $$0) {
        this.worldData.setDifficultyLocked($$0);
        this.getPlayerList().getPlayers().forEach(this::sendDifficultyUpdate);
    }

    private void sendDifficultyUpdate(ServerPlayer $$0) {
        LevelData $$1 = $$0.getLevel().getLevelData();
        $$0.connection.send(new ClientboundChangeDifficultyPacket($$1.getDifficulty(), $$1.isDifficultyLocked()));
    }

    public boolean isSpawningMonsters() {
        return this.worldData.getDifficulty() != Difficulty.PEACEFUL;
    }

    public boolean isDemo() {
        return this.isDemo;
    }

    public void setDemo(boolean $$0) {
        this.isDemo = $$0;
    }

    public Optional<ServerResourcePackInfo> getServerResourcePack() {
        return Optional.empty();
    }

    public boolean isResourcePackRequired() {
        return this.getServerResourcePack().filter(ServerResourcePackInfo::isRequired).isPresent();
    }

    public abstract boolean isDedicatedServer();

    public abstract int getRateLimitPacketsPerSecond();

    public boolean usesAuthentication() {
        return this.onlineMode;
    }

    public void setUsesAuthentication(boolean $$0) {
        this.onlineMode = $$0;
    }

    public boolean getPreventProxyConnections() {
        return this.preventProxyConnections;
    }

    public void setPreventProxyConnections(boolean $$0) {
        this.preventProxyConnections = $$0;
    }

    public boolean isSpawningAnimals() {
        return true;
    }

    public boolean areNpcsEnabled() {
        return true;
    }

    public abstract boolean isEpollEnabled();

    public boolean isPvpAllowed() {
        return this.pvp;
    }

    public void setPvpAllowed(boolean $$0) {
        this.pvp = $$0;
    }

    public boolean isFlightAllowed() {
        return this.allowFlight;
    }

    public void setFlightAllowed(boolean $$0) {
        this.allowFlight = $$0;
    }

    public abstract boolean isCommandBlockEnabled();

    public String getMotd() {
        return this.motd;
    }

    public void setMotd(String $$0) {
        this.motd = $$0;
    }

    public boolean isStopped() {
        return this.stopped;
    }

    public PlayerList getPlayerList() {
        return this.playerList;
    }

    public void setPlayerList(PlayerList $$0) {
        this.playerList = $$0;
    }

    public abstract boolean isPublished();

    public void setDefaultGameType(GameType $$0) {
        this.worldData.setGameType($$0);
    }

    @Nullable
    public ServerConnectionListener getConnection() {
        return this.connection;
    }

    public boolean isReady() {
        return this.isReady;
    }

    public boolean hasGui() {
        return false;
    }

    public boolean publishServer(@Nullable GameType $$0, boolean $$1, int $$2) {
        return false;
    }

    public int getTickCount() {
        return this.tickCount;
    }

    public int getSpawnProtectionRadius() {
        return 16;
    }

    public boolean isUnderSpawnProtection(ServerLevel $$0, BlockPos $$1, Player $$2) {
        return false;
    }

    public boolean repliesToStatus() {
        return true;
    }

    public boolean hidesOnlinePlayers() {
        return false;
    }

    public Proxy getProxy() {
        return this.proxy;
    }

    public int getPlayerIdleTimeout() {
        return this.playerIdleTimeout;
    }

    public void setPlayerIdleTimeout(int $$0) {
        this.playerIdleTimeout = $$0;
    }

    public MinecraftSessionService getSessionService() {
        return this.services.sessionService();
    }

    public SignatureValidator getServiceSignatureValidator() {
        return this.services.serviceSignatureValidator();
    }

    public GameProfileRepository getProfileRepository() {
        return this.services.profileRepository();
    }

    public GameProfileCache getProfileCache() {
        return this.services.profileCache();
    }

    public ServerStatus getStatus() {
        return this.status;
    }

    public void invalidateStatus() {
        this.lastServerStatus = 0L;
    }

    public int getAbsoluteMaxWorldSize() {
        return 29999984;
    }

    @Override
    public boolean scheduleExecutables() {
        return super.scheduleExecutables() && !this.isStopped();
    }

    @Override
    public void executeIfPossible(Runnable $$0) {
        if (this.isStopped()) {
            throw new RejectedExecutionException("Server already shutting down");
        }
        super.executeIfPossible($$0);
    }

    @Override
    public Thread getRunningThread() {
        return this.serverThread;
    }

    public int getCompressionThreshold() {
        return 256;
    }

    public boolean enforceSecureProfile() {
        return false;
    }

    public long getNextTickTime() {
        return this.nextTickTime;
    }

    public DataFixer getFixerUpper() {
        return this.fixerUpper;
    }

    public int getSpawnRadius(@Nullable ServerLevel $$0) {
        if ($$0 != null) {
            return $$0.getGameRules().getInt(GameRules.RULE_SPAWN_RADIUS);
        }
        return 10;
    }

    public ServerAdvancementManager getAdvancements() {
        return this.resources.managers.getAdvancements();
    }

    public ServerFunctionManager getFunctions() {
        return this.functionManager;
    }

    public CompletableFuture<Void> reloadResources(Collection<String> $$0) {
        RegistryAccess.Frozen $$13 = this.registries.getAccessForLoading(RegistryLayer.RELOADABLE);
        CompletableFuture $$2 = CompletableFuture.supplyAsync(() -> (ImmutableList)$$0.stream().map(this.packRepository::getPack).filter(Objects::nonNull).map(Pack::open).collect(ImmutableList.toImmutableList()), (Executor)this).thenCompose($$12 -> {
            MultiPackResourceManager $$22 = new MultiPackResourceManager(PackType.SERVER_DATA, (List<PackResources>)$$12);
            return ReloadableServerResources.loadResources($$22, $$13, this.worldData.enabledFeatures(), this.isDedicatedServer() ? Commands.CommandSelection.DEDICATED : Commands.CommandSelection.INTEGRATED, this.getFunctionCompilationLevel(), this.executor, this).whenComplete(($$1, $$2) -> {
                if ($$2 != null) {
                    $$22.close();
                }
            }).thenApply($$1 -> new ReloadableResources($$22, (ReloadableServerResources)$$1));
        }).thenAcceptAsync($$1 -> {
            this.resources.close();
            this.resources = $$1;
            this.packRepository.setSelected($$0);
            WorldDataConfiguration $$2 = new WorldDataConfiguration(MinecraftServer.getSelectedPacks(this.packRepository), this.worldData.enabledFeatures());
            this.worldData.setDataConfiguration($$2);
            this.resources.managers.updateRegistryTags(this.registryAccess());
            this.getPlayerList().saveAll();
            this.getPlayerList().reloadResources();
            this.functionManager.replaceLibrary(this.resources.managers.getFunctionLibrary());
            this.structureTemplateManager.onResourceManagerReload(this.resources.resourceManager);
        }, (Executor)this);
        if (this.isSameThread()) {
            this.managedBlock(() -> ((CompletableFuture)$$2).isDone());
        }
        return $$2;
    }

    public static WorldDataConfiguration configurePackRepository(PackRepository $$0, DataPackConfig $$1, boolean $$2, FeatureFlagSet $$3) {
        $$0.reload();
        if ($$2) {
            $$0.setSelected((Collection<String>)Collections.singleton((Object)VANILLA_BRAND));
            return WorldDataConfiguration.DEFAULT;
        }
        LinkedHashSet $$4 = Sets.newLinkedHashSet();
        for (String $$5 : $$1.getEnabled()) {
            if ($$0.isAvailable($$5)) {
                $$4.add((Object)$$5);
                continue;
            }
            LOGGER.warn("Missing data pack {}", (Object)$$5);
        }
        for (Pack $$6 : $$0.getAvailablePacks()) {
            String $$7 = $$6.getId();
            if ($$1.getDisabled().contains((Object)$$7)) continue;
            FeatureFlagSet $$8 = $$6.getRequestedFeatures();
            boolean $$9 = $$4.contains((Object)$$7);
            if (!$$9 && $$6.getPackSource().shouldAddAutomatically()) {
                if ($$8.isSubsetOf($$3)) {
                    LOGGER.info("Found new data pack {}, loading it automatically", (Object)$$7);
                    $$4.add((Object)$$7);
                } else {
                    LOGGER.info("Found new data pack {}, but can't load it due to missing features {}", (Object)$$7, (Object)FeatureFlags.printMissingFlags($$3, $$8));
                }
            }
            if (!$$9 || $$8.isSubsetOf($$3)) continue;
            LOGGER.warn("Pack {} requires features {} that are not enabled for this world, disabling pack.", (Object)$$7, (Object)FeatureFlags.printMissingFlags($$3, $$8));
            $$4.remove((Object)$$7);
        }
        if ($$4.isEmpty()) {
            LOGGER.info("No datapacks selected, forcing vanilla");
            $$4.add((Object)VANILLA_BRAND);
        }
        $$0.setSelected((Collection<String>)$$4);
        DataPackConfig $$10 = MinecraftServer.getSelectedPacks($$0);
        FeatureFlagSet $$11 = $$0.getRequestedFeatureFlags();
        return new WorldDataConfiguration($$10, $$11);
    }

    private static DataPackConfig getSelectedPacks(PackRepository $$0) {
        Collection<String> $$12 = $$0.getSelectedIds();
        ImmutableList $$2 = ImmutableList.copyOf($$12);
        List $$3 = (List)$$0.getAvailableIds().stream().filter($$1 -> !$$12.contains($$1)).collect(ImmutableList.toImmutableList());
        return new DataPackConfig((List<String>)$$2, (List<String>)$$3);
    }

    public void kickUnlistedPlayers(CommandSourceStack $$0) {
        if (!this.isEnforceWhitelist()) {
            return;
        }
        PlayerList $$1 = $$0.getServer().getPlayerList();
        UserWhiteList $$2 = $$1.getWhiteList();
        ArrayList $$3 = Lists.newArrayList($$1.getPlayers());
        for (ServerPlayer $$4 : $$3) {
            if ($$2.isWhiteListed($$4.getGameProfile())) continue;
            $$4.connection.disconnect(Component.translatable("multiplayer.disconnect.not_whitelisted"));
        }
    }

    public PackRepository getPackRepository() {
        return this.packRepository;
    }

    public Commands getCommands() {
        return this.resources.managers.getCommands();
    }

    public CommandSourceStack createCommandSourceStack() {
        ServerLevel $$0 = this.overworld();
        return new CommandSourceStack(this, $$0 == null ? Vec3.ZERO : Vec3.atLowerCornerOf($$0.getSharedSpawnPos()), Vec2.ZERO, $$0, 4, "Server", Component.literal("Server"), this, null);
    }

    @Override
    public boolean acceptsSuccess() {
        return true;
    }

    @Override
    public boolean acceptsFailure() {
        return true;
    }

    @Override
    public abstract boolean shouldInformAdmins();

    public RecipeManager getRecipeManager() {
        return this.resources.managers.getRecipeManager();
    }

    public ServerScoreboard getScoreboard() {
        return this.scoreboard;
    }

    public CommandStorage getCommandStorage() {
        if (this.commandStorage == null) {
            throw new NullPointerException("Called before server init");
        }
        return this.commandStorage;
    }

    public LootTables getLootTables() {
        return this.resources.managers.getLootTables();
    }

    public PredicateManager getPredicateManager() {
        return this.resources.managers.getPredicateManager();
    }

    public ItemModifierManager getItemModifierManager() {
        return this.resources.managers.getItemModifierManager();
    }

    public GameRules getGameRules() {
        return this.overworld().getGameRules();
    }

    public CustomBossEvents getCustomBossEvents() {
        return this.customBossEvents;
    }

    public boolean isEnforceWhitelist() {
        return this.enforceWhitelist;
    }

    public void setEnforceWhitelist(boolean $$0) {
        this.enforceWhitelist = $$0;
    }

    public float getAverageTickTime() {
        return this.averageTickTime;
    }

    public int getProfilePermissions(GameProfile $$0) {
        if (this.getPlayerList().isOp($$0)) {
            ServerOpListEntry $$1 = (ServerOpListEntry)this.getPlayerList().getOps().get($$0);
            if ($$1 != null) {
                return $$1.getLevel();
            }
            if (this.isSingleplayerOwner($$0)) {
                return 4;
            }
            if (this.isSingleplayer()) {
                return this.getPlayerList().isAllowCheatsForAllPlayers() ? 4 : 0;
            }
            return this.getOperatorUserPermissionLevel();
        }
        return 0;
    }

    public FrameTimer getFrameTimer() {
        return this.frameTimer;
    }

    public ProfilerFiller getProfiler() {
        return this.profiler;
    }

    public abstract boolean isSingleplayerOwner(GameProfile var1);

    public void dumpServerProperties(Path $$0) throws IOException {
    }

    private void saveDebugReport(Path $$0) {
        Path $$1 = $$0.resolve("levels");
        try {
            for (Map.Entry $$2 : this.levels.entrySet()) {
                ResourceLocation $$3 = ((ResourceKey)$$2.getKey()).location();
                Path $$4 = $$1.resolve($$3.getNamespace()).resolve($$3.getPath());
                Files.createDirectories((Path)$$4, (FileAttribute[])new FileAttribute[0]);
                ((ServerLevel)$$2.getValue()).saveDebugReport($$4);
            }
            this.dumpGameRules($$0.resolve("gamerules.txt"));
            this.dumpClasspath($$0.resolve("classpath.txt"));
            this.dumpMiscStats($$0.resolve("stats.txt"));
            this.dumpThreads($$0.resolve("threads.txt"));
            this.dumpServerProperties($$0.resolve("server.properties.txt"));
            this.dumpNativeModules($$0.resolve("modules.txt"));
        }
        catch (IOException $$5) {
            LOGGER.warn("Failed to save debug report", (Throwable)$$5);
        }
    }

    private void dumpMiscStats(Path $$0) throws IOException {
        try (BufferedWriter $$1 = Files.newBufferedWriter((Path)$$0, (OpenOption[])new OpenOption[0]);){
            $$1.write(String.format((Locale)Locale.ROOT, (String)"pending_tasks: %d\n", (Object[])new Object[]{this.getPendingTasksCount()}));
            $$1.write(String.format((Locale)Locale.ROOT, (String)"average_tick_time: %f\n", (Object[])new Object[]{Float.valueOf((float)this.getAverageTickTime())}));
            $$1.write(String.format((Locale)Locale.ROOT, (String)"tick_times: %s\n", (Object[])new Object[]{Arrays.toString((long[])this.tickTimes)}));
            $$1.write(String.format((Locale)Locale.ROOT, (String)"queue: %s\n", (Object[])new Object[]{Util.backgroundExecutor()}));
        }
    }

    private void dumpGameRules(Path $$0) throws IOException {
        try (BufferedWriter $$1 = Files.newBufferedWriter((Path)$$0, (OpenOption[])new OpenOption[0]);){
            ArrayList $$2 = Lists.newArrayList();
            GameRules $$3 = this.getGameRules();
            GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor(){
                final /* synthetic */ List val$entries;
                final /* synthetic */ GameRules val$gameRules;
                {
                    this.val$entries = list;
                    this.val$gameRules = gameRules;
                }

                @Override
                public <T extends GameRules.Value<T>> void visit(GameRules.Key<T> $$0, GameRules.Type<T> $$1) {
                    this.val$entries.add((Object)String.format((Locale)Locale.ROOT, (String)"%s=%s\n", (Object[])new Object[]{$$0.getId(), this.val$gameRules.getRule($$0)}));
                }
            });
            for (String $$4 : $$2) {
                $$1.write($$4);
            }
        }
    }

    private void dumpClasspath(Path $$0) throws IOException {
        try (BufferedWriter $$1 = Files.newBufferedWriter((Path)$$0, (OpenOption[])new OpenOption[0]);){
            String $$2 = System.getProperty((String)"java.class.path");
            String $$3 = System.getProperty((String)"path.separator");
            for (String $$4 : Splitter.on((String)$$3).split((CharSequence)$$2)) {
                $$1.write($$4);
                $$1.write("\n");
            }
        }
    }

    private void dumpThreads(Path $$0) throws IOException {
        ThreadMXBean $$1 = ManagementFactory.getThreadMXBean();
        Object[] $$2 = $$1.dumpAllThreads(true, true);
        Arrays.sort((Object[])$$2, (Comparator)Comparator.comparing(ThreadInfo::getThreadName));
        try (BufferedWriter $$3 = Files.newBufferedWriter((Path)$$0, (OpenOption[])new OpenOption[0]);){
            for (Object $$4 : $$2) {
                $$3.write($$4.toString());
                $$3.write(10);
            }
        }
    }

    /*
     * WARNING - void declaration
     */
    private void dumpNativeModules(Path $$02) throws IOException {
        BufferedWriter $$1 = Files.newBufferedWriter((Path)$$02, (OpenOption[])new OpenOption[0]);
        try {
            void $$4;
            try {
                ArrayList $$2 = Lists.newArrayList(NativeModuleLister.listModules());
            }
            catch (Throwable $$3) {
                LOGGER.warn("Failed to list native modules", $$3);
                if ($$1 != null) {
                    $$1.close();
                }
                return;
            }
            $$4.sort(Comparator.comparing($$0 -> $$0.name));
            for (NativeModuleLister.NativeModuleInfo $$5 : $$4) {
                $$1.write($$5.toString());
                $$1.write(10);
            }
        }
        finally {
            if ($$1 != null) {
                try {
                    $$1.close();
                }
                catch (Throwable throwable) {
                    Throwable throwable2;
                    throwable2.addSuppressed(throwable);
                }
            }
        }
    }

    private void startMetricsRecordingTick() {
        if (this.willStartRecordingMetrics) {
            this.metricsRecorder = ActiveMetricsRecorder.createStarted(new ServerMetricsSamplersProvider(Util.timeSource, this.isDedicatedServer()), Util.timeSource, (Executor)Util.ioPool(), new MetricsPersister("server"), this.onMetricsRecordingStopped, (Consumer<Path>)((Consumer)$$0 -> {
                this.executeBlocking(() -> this.saveDebugReport($$0.resolve("server")));
                this.onMetricsRecordingFinished.accept($$0);
            }));
            this.willStartRecordingMetrics = false;
        }
        this.profiler = SingleTickProfiler.decorateFiller(this.metricsRecorder.getProfiler(), SingleTickProfiler.createTickProfiler("Server"));
        this.metricsRecorder.startTick();
        this.profiler.startTick();
    }

    private void endMetricsRecordingTick() {
        this.profiler.endTick();
        this.metricsRecorder.endTick();
    }

    public boolean isRecordingMetrics() {
        return this.metricsRecorder.isRecording();
    }

    public void startRecordingMetrics(Consumer<ProfileResults> $$0, Consumer<Path> $$12) {
        this.onMetricsRecordingStopped = $$1 -> {
            this.stopRecordingMetrics();
            $$0.accept($$1);
        };
        this.onMetricsRecordingFinished = $$12;
        this.willStartRecordingMetrics = true;
    }

    public void stopRecordingMetrics() {
        this.metricsRecorder = InactiveMetricsRecorder.INSTANCE;
    }

    public void finishRecordingMetrics() {
        this.metricsRecorder.end();
    }

    public void cancelRecordingMetrics() {
        this.metricsRecorder.cancel();
        this.profiler = this.metricsRecorder.getProfiler();
    }

    public Path getWorldPath(LevelResource $$0) {
        return this.storageSource.getLevelPath($$0);
    }

    public boolean forceSynchronousWrites() {
        return true;
    }

    public StructureTemplateManager getStructureManager() {
        return this.structureTemplateManager;
    }

    public WorldData getWorldData() {
        return this.worldData;
    }

    public RegistryAccess.Frozen registryAccess() {
        return this.registries.compositeAccess();
    }

    public LayeredRegistryAccess<RegistryLayer> registries() {
        return this.registries;
    }

    public TextFilter createTextFilterForPlayer(ServerPlayer $$0) {
        return TextFilter.DUMMY;
    }

    public ServerPlayerGameMode createGameModeForPlayer(ServerPlayer $$0) {
        return this.isDemo() ? new DemoMode($$0) : new ServerPlayerGameMode($$0);
    }

    @Nullable
    public GameType getForcedGameType() {
        return null;
    }

    public ResourceManager getResourceManager() {
        return this.resources.resourceManager;
    }

    public boolean isCurrentlySaving() {
        return this.isSaving;
    }

    public boolean isTimeProfilerRunning() {
        return this.debugCommandProfilerDelayStart || this.debugCommandProfiler != null;
    }

    public void startTimeProfiler() {
        this.debugCommandProfilerDelayStart = true;
    }

    public ProfileResults stopTimeProfiler() {
        if (this.debugCommandProfiler == null) {
            return EmptyProfileResults.EMPTY;
        }
        ProfileResults $$0 = this.debugCommandProfiler.stop(Util.getNanos(), this.tickCount);
        this.debugCommandProfiler = null;
        return $$0;
    }

    public int getMaxChainedNeighborUpdates() {
        return 1000000;
    }

    public void logChatMessage(Component $$0, ChatType.Bound $$1, @Nullable String $$2) {
        String $$3 = $$1.decorate($$0).getString();
        if ($$2 != null) {
            LOGGER.info("[{}] {}", (Object)$$2, (Object)$$3);
        } else {
            LOGGER.info("{}", (Object)$$3);
        }
    }

    public ChatDecorator getChatDecorator() {
        return ChatDecorator.PLAIN;
    }

    record ReloadableResources(CloseableResourceManager resourceManager, ReloadableServerResources managers) implements AutoCloseable
    {
        public void close() {
            this.resourceManager.close();
        }
    }

    static class TimeProfiler {
        final long startNanos;
        final int startTick;

        TimeProfiler(long $$0, int $$1) {
            this.startNanos = $$0;
            this.startTick = $$1;
        }

        ProfileResults stop(final long $$0, final int $$1) {
            return new ProfileResults(){

                @Override
                public List<ResultField> getTimes(String $$02) {
                    return Collections.emptyList();
                }

                @Override
                public boolean saveResults(Path $$02) {
                    return false;
                }

                @Override
                public long getStartTimeNano() {
                    return startNanos;
                }

                @Override
                public int getStartTimeTicks() {
                    return startTick;
                }

                @Override
                public long getEndTimeNano() {
                    return $$0;
                }

                @Override
                public int getEndTimeTicks() {
                    return $$1;
                }

                @Override
                public String getProfilerResults() {
                    return "";
                }
            };
        }
    }

    public record ServerResourcePackInfo(String url, String hash, boolean isRequired, @Nullable Component prompt) {
    }
}