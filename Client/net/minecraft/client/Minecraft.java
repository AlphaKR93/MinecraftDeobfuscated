/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Queues
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.exceptions.AuthenticationException
 *  com.mojang.authlib.minecraft.BanDetails
 *  com.mojang.authlib.minecraft.MinecraftSessionService
 *  com.mojang.authlib.minecraft.UserApiService
 *  com.mojang.authlib.minecraft.UserApiService$UserFlag
 *  com.mojang.authlib.properties.PropertyMap
 *  com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2BooleanFunction
 *  java.io.File
 *  java.io.FileNotFoundException
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.UncheckedIOException
 *  java.lang.CharSequence
 *  java.lang.Exception
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.InterruptedException
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.OutOfMemoryError
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.lang.Runtime
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.System
 *  java.lang.Thread
 *  java.lang.Throwable
 *  java.lang.Void
 *  java.net.Proxy
 *  java.net.SocketAddress
 *  java.nio.Buffer
 *  java.nio.ByteBuffer
 *  java.nio.file.Path
 *  java.nio.file.Paths
 *  java.text.DecimalFormat
 *  java.text.DecimalFormatSymbols
 *  java.time.Duration
 *  java.time.Instant
 *  java.time.temporal.Temporal
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.MissingResourceException
 *  java.util.Optional
 *  java.util.Queue
 *  java.util.UUID
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.concurrent.atomic.AtomicReference
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  java.util.function.Supplier
 *  java.util.function.UnaryOperator
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.apache.commons.io.FileUtils
 *  org.joml.Matrix4f
 *  org.lwjgl.util.tinyfd.TinyFileDialogs
 *  org.slf4j.Logger
 */
package net.minecraft.client;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Queues;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.BanDetails;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.GlDebug;
import com.mojang.blaze3d.platform.GlUtil;
import com.mojang.blaze3d.platform.MacosUtil;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.TimerQuery;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.FileUtil;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.SystemReport;
import net.minecraft.Util;
import net.minecraft.client.CameraType;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Game;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.HotbarManager;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.Options;
import net.minecraft.client.PeriodicNotificationManager;
import net.minecraft.client.Realms32BitWarningStatus;
import net.minecraft.client.ResourceLoadStateTracker;
import net.minecraft.client.Screenshot;
import net.minecraft.client.Timer;
import net.minecraft.client.User;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.gui.components.toasts.TutorialToast;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.screens.BanNoticeScreen;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.InBedChatScreen;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.OutOfMemoryScreen;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.social.PlayerSocialManager;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.client.gui.screens.worldselection.WorldOpenFlows;
import net.minecraft.client.main.GameConfig;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.ProfileKeyPairManager;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.client.multiplayer.chat.report.ReportEnvironment;
import net.minecraft.client.multiplayer.chat.report.ReportingContext;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.profiling.ClientMetricsSamplersProvider;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.GpuWarnlistManager;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderBuffers;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.VirtualScreen;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.client.resources.DownloadedPackSource;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.client.resources.PaintingTextureManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.SplashManager;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.searchtree.FullTextSearchTree;
import net.minecraft.client.searchtree.IdSearchTree;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.telemetry.ClientTelemetryManager;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.KeybindResolver;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.progress.ProcessorChunkProgressListener;
import net.minecraft.server.level.progress.StoringChunkProgressListener;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.repository.FolderRepositorySource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FileZipper;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.MemoryReserve;
import net.minecraft.util.ModCheck;
import net.minecraft.util.Mth;
import net.minecraft.util.SignatureValidator;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.Unit;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.profiling.ContinuousProfiler;
import net.minecraft.util.profiling.EmptyProfileResults;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.ResultField;
import net.minecraft.util.profiling.SingleTickProfiler;
import net.minecraft.util.profiling.metrics.profiling.ActiveMetricsRecorder;
import net.minecraft.util.profiling.metrics.profiling.InactiveMetricsRecorder;
import net.minecraft.util.profiling.metrics.profiling.MetricsRecorder;
import net.minecraft.util.profiling.metrics.storage.MetricsPersister;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.apache.commons.io.FileUtils;
import org.joml.Matrix4f;
import org.lwjgl.util.tinyfd.TinyFileDialogs;
import org.slf4j.Logger;

public class Minecraft
extends ReentrantBlockableEventLoop<Runnable>
implements WindowEventHandler {
    static Minecraft instance;
    private static final Logger LOGGER;
    public static final boolean ON_OSX;
    private static final int MAX_TICKS_PER_UPDATE = 10;
    public static final ResourceLocation DEFAULT_FONT;
    public static final ResourceLocation UNIFORM_FONT;
    public static final ResourceLocation ALT_FONT;
    private static final ResourceLocation REGIONAL_COMPLIANCIES;
    private static final CompletableFuture<Unit> RESOURCE_RELOAD_INITIAL_TASK;
    private static final Component SOCIAL_INTERACTIONS_NOT_AVAILABLE;
    public static final String UPDATE_DRIVERS_ADVICE = "Please make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions).";
    private final Path resourcePackDirectory;
    private final PropertyMap profileProperties;
    private final TextureManager textureManager;
    private final DataFixer fixerUpper;
    private final VirtualScreen virtualScreen;
    private final Window window;
    private final Timer timer = new Timer(20.0f, 0L);
    private final RenderBuffers renderBuffers;
    public final LevelRenderer levelRenderer;
    private final EntityRenderDispatcher entityRenderDispatcher;
    private final ItemRenderer itemRenderer;
    public final ParticleEngine particleEngine;
    private final SearchRegistry searchRegistry = new SearchRegistry();
    private final User user;
    public final Font font;
    public final Font fontFilterFishy;
    public final GameRenderer gameRenderer;
    public final DebugRenderer debugRenderer;
    private final AtomicReference<StoringChunkProgressListener> progressListener = new AtomicReference();
    public final Gui gui;
    public final Options options;
    private final HotbarManager hotbarManager;
    public final MouseHandler mouseHandler;
    public final KeyboardHandler keyboardHandler;
    public final File gameDirectory;
    private final String launchedVersion;
    private final String versionType;
    private final Proxy proxy;
    private final LevelStorageSource levelSource;
    public final FrameTimer frameTimer = new FrameTimer();
    private final boolean is64bit;
    private final boolean demo;
    private final boolean allowsMultiplayer;
    private final boolean allowsChat;
    private final ReloadableResourceManager resourceManager;
    private final VanillaPackResources vanillaPackResources;
    private final DownloadedPackSource downloadedPackSource;
    private final PackRepository resourcePackRepository;
    private final LanguageManager languageManager;
    private final BlockColors blockColors;
    private final ItemColors itemColors;
    private final RenderTarget mainRenderTarget;
    private final SoundManager soundManager;
    private final MusicManager musicManager;
    private final FontManager fontManager;
    private final SplashManager splashManager;
    private final GpuWarnlistManager gpuWarnlistManager;
    private final PeriodicNotificationManager regionalCompliancies = new PeriodicNotificationManager(REGIONAL_COMPLIANCIES, (Object2BooleanFunction<String>)((Object2BooleanFunction)Minecraft::countryEqualsISO3));
    private final YggdrasilAuthenticationService authenticationService;
    private final MinecraftSessionService minecraftSessionService;
    private final SignatureValidator serviceSignatureValidator;
    private final UserApiService userApiService;
    private final SkinManager skinManager;
    private final ModelManager modelManager;
    private final BlockRenderDispatcher blockRenderer;
    private final PaintingTextureManager paintingTextures;
    private final MobEffectTextureManager mobEffectTextures;
    private final ToastComponent toast;
    private final Game game = new Game(this);
    private final Tutorial tutorial;
    private final PlayerSocialManager playerSocialManager;
    private final EntityModelSet entityModels;
    private final BlockEntityRenderDispatcher blockEntityRenderDispatcher;
    private final ClientTelemetryManager telemetryManager;
    private final ProfileKeyPairManager profileKeyPairManager;
    private final RealmsDataFetcher realmsDataFetcher;
    @Nullable
    public MultiPlayerGameMode gameMode;
    @Nullable
    public ClientLevel level;
    @Nullable
    public LocalPlayer player;
    @Nullable
    private IntegratedServer singleplayerServer;
    @Nullable
    private Connection pendingConnection;
    private boolean isLocalServer;
    @Nullable
    public Entity cameraEntity;
    @Nullable
    public Entity crosshairPickEntity;
    @Nullable
    public HitResult hitResult;
    private int rightClickDelay;
    protected int missTime;
    private volatile boolean pause;
    private float pausePartialTick;
    private long lastNanoTime = Util.getNanos();
    private long lastTime;
    private int frames;
    public boolean noRender;
    @Nullable
    public Screen screen;
    @Nullable
    private Overlay overlay;
    private boolean connectedToRealms;
    private Thread gameThread;
    private volatile boolean running;
    @Nullable
    private Supplier<CrashReport> delayedCrash;
    private static int fps;
    public String fpsString = "";
    private long frameTimeNs;
    public boolean wireframe;
    public boolean chunkPath;
    public boolean chunkVisibility;
    public boolean smartCull = true;
    private boolean windowActive;
    private final Queue<Runnable> progressTasks = Queues.newConcurrentLinkedQueue();
    @Nullable
    private CompletableFuture<Void> pendingReload;
    @Nullable
    private TutorialToast socialInteractionsToast;
    private ProfilerFiller profiler = InactiveProfiler.INSTANCE;
    private int fpsPieRenderTicks;
    private final ContinuousProfiler fpsPieProfiler = new ContinuousProfiler(Util.timeSource, () -> this.fpsPieRenderTicks);
    @Nullable
    private ProfileResults fpsPieResults;
    private MetricsRecorder metricsRecorder = InactiveMetricsRecorder.INSTANCE;
    private final ResourceLoadStateTracker reloadStateTracker = new ResourceLoadStateTracker();
    private long savedCpuDuration;
    private double gpuUtilization;
    @Nullable
    private TimerQuery.FrameProfile currentFrameProfile;
    private final Realms32BitWarningStatus realms32BitWarningStatus;
    private final GameNarrator narrator;
    private final ChatListener chatListener;
    private ReportingContext reportingContext;
    private String debugPath = "root";

    public Minecraft(GameConfig $$02) {
        super("Client");
        DisplayData $$9;
        int $$7;
        String $$6;
        instance = this;
        this.gameDirectory = $$02.location.gameDirectory;
        File $$1 = $$02.location.assetDirectory;
        this.resourcePackDirectory = $$02.location.resourcePackDirectory.toPath();
        this.launchedVersion = $$02.game.launchVersion;
        this.versionType = $$02.game.versionType;
        this.profileProperties = $$02.user.profileProperties;
        ClientPackSource $$2 = new ClientPackSource($$02.location.getExternalAssetSource());
        this.downloadedPackSource = new DownloadedPackSource(new File(this.gameDirectory, "server-resource-packs"));
        FolderRepositorySource $$3 = new FolderRepositorySource(this.resourcePackDirectory, PackType.CLIENT_RESOURCES, PackSource.DEFAULT);
        this.resourcePackRepository = new PackRepository($$2, this.downloadedPackSource, $$3);
        this.vanillaPackResources = $$2.getVanillaPack();
        this.proxy = $$02.user.proxy;
        this.authenticationService = new YggdrasilAuthenticationService(this.proxy);
        this.minecraftSessionService = this.authenticationService.createMinecraftSessionService();
        this.userApiService = this.createUserApiService(this.authenticationService, $$02);
        this.serviceSignatureValidator = SignatureValidator.from(this.authenticationService.getServicesKey());
        this.user = $$02.user.user;
        LOGGER.info("Setting user: {}", (Object)this.user.getName());
        LOGGER.debug("(Session ID is {})", (Object)this.user.getSessionId());
        this.demo = $$02.game.demo;
        this.allowsMultiplayer = !$$02.game.disableMultiplayer;
        this.allowsChat = !$$02.game.disableChat;
        this.is64bit = Minecraft.checkIs64Bit();
        this.singleplayerServer = null;
        if (this.allowsMultiplayer() && $$02.server.hostname != null) {
            String $$4 = $$02.server.hostname;
            int $$5 = $$02.server.port;
        } else {
            $$6 = null;
            $$7 = 0;
        }
        KeybindResolver.setKeyResolver((Function<String, Supplier<Component>>)((Function)KeyMapping::createNameSupplier));
        this.fixerUpper = DataFixers.getDataFixer();
        this.toast = new ToastComponent(this);
        this.gameThread = Thread.currentThread();
        this.options = new Options(this, this.gameDirectory);
        this.running = true;
        this.tutorial = new Tutorial(this, this.options);
        this.hotbarManager = new HotbarManager(this.gameDirectory, this.fixerUpper);
        LOGGER.info("Backend library: {}", (Object)RenderSystem.getBackendDescription());
        if (this.options.overrideHeight > 0 && this.options.overrideWidth > 0) {
            DisplayData $$8 = new DisplayData(this.options.overrideWidth, this.options.overrideHeight, $$02.display.fullscreenWidth, $$02.display.fullscreenHeight, $$02.display.isFullscreen);
        } else {
            $$9 = $$02.display;
        }
        Util.timeSource = RenderSystem.initBackendSystem();
        this.virtualScreen = new VirtualScreen(this);
        this.window = this.virtualScreen.newWindow($$9, this.options.fullscreenVideoModeString, this.createTitle());
        this.setWindowActive(true);
        try {
            if (ON_OSX) {
                MacosUtil.loadIcon(this.getIconFile("icons", "minecraft.icns"));
            } else {
                this.window.setIcon(this.getIconFile("icons", "icon_16x16.png"), this.getIconFile("icons", "icon_32x32.png"));
            }
        }
        catch (IOException $$10) {
            LOGGER.error("Couldn't set icon", (Throwable)$$10);
        }
        this.window.setFramerateLimit(this.options.framerateLimit().get());
        this.mouseHandler = new MouseHandler(this);
        this.mouseHandler.setup(this.window.getWindow());
        this.keyboardHandler = new KeyboardHandler(this);
        this.keyboardHandler.setup(this.window.getWindow());
        RenderSystem.initRenderer(this.options.glDebugVerbosity, false);
        this.mainRenderTarget = new MainTarget(this.window.getWidth(), this.window.getHeight());
        this.mainRenderTarget.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        this.mainRenderTarget.clear(ON_OSX);
        this.resourceManager = new ReloadableResourceManager(PackType.CLIENT_RESOURCES);
        this.resourcePackRepository.reload();
        this.options.loadSelectedResourcePacks(this.resourcePackRepository);
        this.languageManager = new LanguageManager(this.options.languageCode);
        this.resourceManager.registerReloadListener(this.languageManager);
        this.textureManager = new TextureManager(this.resourceManager);
        this.resourceManager.registerReloadListener(this.textureManager);
        this.skinManager = new SkinManager(this.textureManager, new File($$1, "skins"), this.minecraftSessionService);
        this.levelSource = new LevelStorageSource(this.gameDirectory.toPath().resolve("saves"), this.gameDirectory.toPath().resolve("backups"), this.fixerUpper);
        this.soundManager = new SoundManager(this.options);
        this.resourceManager.registerReloadListener(this.soundManager);
        this.splashManager = new SplashManager(this.user);
        this.resourceManager.registerReloadListener(this.splashManager);
        this.musicManager = new MusicManager(this);
        this.fontManager = new FontManager(this.textureManager);
        this.font = this.fontManager.createFont();
        this.fontFilterFishy = this.fontManager.createFontFilterFishy();
        this.resourceManager.registerReloadListener(this.fontManager.getReloadListener());
        this.selectMainFont(this.isEnforceUnicode());
        this.resourceManager.registerReloadListener(new GrassColorReloadListener());
        this.resourceManager.registerReloadListener(new FoliageColorReloadListener());
        this.window.setErrorSection("Startup");
        RenderSystem.setupDefaultState(0, 0, this.window.getWidth(), this.window.getHeight());
        this.window.setErrorSection("Post startup");
        this.blockColors = BlockColors.createDefault();
        this.itemColors = ItemColors.createDefault(this.blockColors);
        this.modelManager = new ModelManager(this.textureManager, this.blockColors, this.options.mipmapLevels().get());
        this.resourceManager.registerReloadListener(this.modelManager);
        this.entityModels = new EntityModelSet();
        this.resourceManager.registerReloadListener(this.entityModels);
        this.blockEntityRenderDispatcher = new BlockEntityRenderDispatcher(this.font, this.entityModels, (Supplier<BlockRenderDispatcher>)((Supplier)this::getBlockRenderer), (Supplier<ItemRenderer>)((Supplier)this::getItemRenderer), (Supplier<EntityRenderDispatcher>)((Supplier)this::getEntityRenderDispatcher));
        this.resourceManager.registerReloadListener(this.blockEntityRenderDispatcher);
        BlockEntityWithoutLevelRenderer $$11 = new BlockEntityWithoutLevelRenderer(this.blockEntityRenderDispatcher, this.entityModels);
        this.resourceManager.registerReloadListener($$11);
        this.itemRenderer = new ItemRenderer(this.textureManager, this.modelManager, this.itemColors, $$11);
        this.resourceManager.registerReloadListener(this.itemRenderer);
        this.renderBuffers = new RenderBuffers();
        this.playerSocialManager = new PlayerSocialManager(this, this.userApiService);
        this.blockRenderer = new BlockRenderDispatcher(this.modelManager.getBlockModelShaper(), $$11, this.blockColors);
        this.resourceManager.registerReloadListener(this.blockRenderer);
        this.entityRenderDispatcher = new EntityRenderDispatcher(this, this.textureManager, this.itemRenderer, this.blockRenderer, this.font, this.options, this.entityModels);
        this.resourceManager.registerReloadListener(this.entityRenderDispatcher);
        this.gameRenderer = new GameRenderer(this, this.entityRenderDispatcher.getItemInHandRenderer(), this.resourceManager, this.renderBuffers);
        this.resourceManager.registerReloadListener(this.gameRenderer.createReloadListener());
        this.levelRenderer = new LevelRenderer(this, this.entityRenderDispatcher, this.blockEntityRenderDispatcher, this.renderBuffers);
        this.resourceManager.registerReloadListener(this.levelRenderer);
        this.createSearchTrees();
        this.resourceManager.registerReloadListener(this.searchRegistry);
        this.particleEngine = new ParticleEngine(this.level, this.textureManager);
        this.resourceManager.registerReloadListener(this.particleEngine);
        this.paintingTextures = new PaintingTextureManager(this.textureManager);
        this.resourceManager.registerReloadListener(this.paintingTextures);
        this.mobEffectTextures = new MobEffectTextureManager(this.textureManager);
        this.resourceManager.registerReloadListener(this.mobEffectTextures);
        this.gpuWarnlistManager = new GpuWarnlistManager();
        this.resourceManager.registerReloadListener(this.gpuWarnlistManager);
        this.resourceManager.registerReloadListener(this.regionalCompliancies);
        this.gui = new Gui(this, this.itemRenderer);
        this.debugRenderer = new DebugRenderer(this);
        this.realmsDataFetcher = new RealmsDataFetcher(RealmsClient.create(this));
        RenderSystem.setErrorCallback(this::onFullscreenError);
        if (this.mainRenderTarget.width != this.window.getWidth() || this.mainRenderTarget.height != this.window.getHeight()) {
            StringBuilder $$12 = new StringBuilder("Recovering from unsupported resolution (" + this.window.getWidth() + "x" + this.window.getHeight() + ").\nPlease make sure you have up-to-date drivers (see aka.ms/mcdriver for instructions).");
            if (GlDebug.isDebugEnabled()) {
                $$12.append("\n\nReported GL debug messages:\n").append(String.join((CharSequence)"\n", GlDebug.getLastOpenGlDebugMessages()));
            }
            this.window.setWindowed(this.mainRenderTarget.width, this.mainRenderTarget.height);
            TinyFileDialogs.tinyfd_messageBox((CharSequence)"Minecraft", (CharSequence)$$12.toString(), (CharSequence)"ok", (CharSequence)"error", (boolean)false);
        } else if (this.options.fullscreen().get().booleanValue() && !this.window.isFullscreen()) {
            this.window.toggleFullScreen();
            this.options.fullscreen().set(this.window.isFullscreen());
        }
        this.window.updateVsync(this.options.enableVsync().get());
        this.window.updateRawMouseInput(this.options.rawMouseInput().get());
        this.window.setDefaultErrorCallback();
        this.resizeDisplay();
        this.gameRenderer.preloadUiShader(this.vanillaPackResources.asProvider());
        this.telemetryManager = new ClientTelemetryManager(this, this.userApiService, this.user);
        this.profileKeyPairManager = ProfileKeyPairManager.create(this.userApiService, this.user, this.gameDirectory.toPath());
        this.realms32BitWarningStatus = new Realms32BitWarningStatus(this);
        this.narrator = new GameNarrator(this);
        this.chatListener = new ChatListener(this);
        this.chatListener.setMessageDelay(this.options.chatDelay().get());
        this.reportingContext = ReportingContext.create(ReportEnvironment.local(), this.userApiService);
        LoadingOverlay.registerTextures(this);
        List<PackResources> $$13 = this.resourcePackRepository.openAllSelected();
        this.reloadStateTracker.startReload(ResourceLoadStateTracker.ReloadReason.INITIAL, $$13);
        ReloadInstance $$14 = this.resourceManager.createReload((Executor)Util.backgroundExecutor(), this, RESOURCE_RELOAD_INITIAL_TASK, $$13);
        this.setOverlay(new LoadingOverlay(this, $$14, (Consumer<Optional<Throwable>>)((Consumer)$$0 -> Util.ifElse($$0, this::rollbackResourcePacks, () -> {
            if (SharedConstants.IS_RUNNING_IN_IDE) {
                this.selfTest();
            }
            this.reloadStateTracker.finishReload();
        })), false));
        if ($$6 != null) {
            ServerAddress $$15 = new ServerAddress($$6, $$7);
            $$14.done().thenRunAsync(() -> ConnectScreen.startConnecting(new TitleScreen(), this, $$15, new ServerData(I18n.get("selectServer.defaultName", new Object[0]), $$15.toString(), false)), (Executor)this);
        } else if (this.shouldShowBanNotice()) {
            this.setScreen(BanNoticeScreen.create($$0 -> {
                if ($$0) {
                    Util.getPlatform().openUri("https://aka.ms/mcjavamoderation");
                }
                this.setScreen(new TitleScreen(true));
            }, this.multiplayerBan()));
        } else {
            this.setScreen(new TitleScreen(true));
        }
    }

    private IoSupplier<InputStream> getIconFile(String ... $$0) throws IOException {
        IoSupplier<InputStream> $$1 = this.vanillaPackResources.getRootResource($$0);
        if ($$1 == null) {
            throw new FileNotFoundException(String.join((CharSequence)"/", (CharSequence[])$$0));
        }
        return $$1;
    }

    private static boolean countryEqualsISO3(Object $$0) {
        try {
            return Locale.getDefault().getISO3Country().equals($$0);
        }
        catch (MissingResourceException $$1) {
            return false;
        }
    }

    public void updateTitle() {
        this.window.setTitle(this.createTitle());
    }

    private String createTitle() {
        StringBuilder $$0 = new StringBuilder("Minecraft");
        if (Minecraft.checkModStatus().shouldReportAsModified()) {
            $$0.append("*");
        }
        $$0.append(" ");
        $$0.append(SharedConstants.getCurrentVersion().getName());
        ClientPacketListener $$1 = this.getConnection();
        if ($$1 != null && $$1.getConnection().isConnected()) {
            $$0.append(" - ");
            if (this.singleplayerServer != null && !this.singleplayerServer.isPublished()) {
                $$0.append(I18n.get("title.singleplayer", new Object[0]));
            } else if (this.isConnectedToRealms()) {
                $$0.append(I18n.get("title.multiplayer.realms", new Object[0]));
            } else if (this.singleplayerServer != null || this.getCurrentServer() != null && this.getCurrentServer().isLan()) {
                $$0.append(I18n.get("title.multiplayer.lan", new Object[0]));
            } else {
                $$0.append(I18n.get("title.multiplayer.other", new Object[0]));
            }
        }
        return $$0.toString();
    }

    private UserApiService createUserApiService(YggdrasilAuthenticationService $$0, GameConfig $$1) {
        try {
            return $$0.createUserApiService($$1.user.user.getAccessToken());
        }
        catch (AuthenticationException $$2) {
            LOGGER.error("Failed to verify authentication", (Throwable)$$2);
            return UserApiService.OFFLINE;
        }
    }

    public static ModCheck checkModStatus() {
        return ModCheck.identify("vanilla", (Supplier<String>)((Supplier)ClientBrandRetriever::getClientModName), "Client", Minecraft.class);
    }

    private void rollbackResourcePacks(Throwable $$0) {
        if (this.resourcePackRepository.getSelectedIds().size() > 1) {
            this.clearResourcePacksOnError($$0, null);
        } else {
            Util.throwAsRuntime($$0);
        }
    }

    public void clearResourcePacksOnError(Throwable $$0, @Nullable Component $$1) {
        LOGGER.info("Caught error loading resourcepacks, removing all selected resourcepacks", $$0);
        this.reloadStateTracker.startRecovery($$0);
        this.resourcePackRepository.setSelected((Collection<String>)Collections.emptyList());
        this.options.resourcePacks.clear();
        this.options.incompatibleResourcePacks.clear();
        this.options.save();
        this.reloadResourcePacks(true).thenRun(() -> {
            ToastComponent $$1 = this.getToasts();
            SystemToast.addOrUpdate($$1, SystemToast.SystemToastIds.PACK_LOAD_FAILURE, Component.translatable("resourcePack.load_fail"), $$1);
        });
    }

    public void run() {
        this.gameThread = Thread.currentThread();
        if (Runtime.getRuntime().availableProcessors() > 4) {
            this.gameThread.setPriority(10);
        }
        try {
            boolean $$0 = false;
            while (this.running) {
                if (this.delayedCrash != null) {
                    Minecraft.crash((CrashReport)this.delayedCrash.get());
                    return;
                }
                try {
                    SingleTickProfiler $$1 = SingleTickProfiler.createTickProfiler("Renderer");
                    boolean $$2 = this.shouldRenderFpsPie();
                    this.profiler = this.constructProfiler($$2, $$1);
                    this.profiler.startTick();
                    this.metricsRecorder.startTick();
                    this.runTick(!$$0);
                    this.metricsRecorder.endTick();
                    this.profiler.endTick();
                    this.finishProfilers($$2, $$1);
                }
                catch (OutOfMemoryError $$3) {
                    if ($$0) {
                        throw $$3;
                    }
                    this.emergencySave();
                    this.setScreen(new OutOfMemoryScreen());
                    System.gc();
                    LOGGER.error(LogUtils.FATAL_MARKER, "Out of memory", (Throwable)$$3);
                    $$0 = true;
                }
            }
        }
        catch (ReportedException $$4) {
            this.fillReport($$4.getReport());
            this.emergencySave();
            LOGGER.error(LogUtils.FATAL_MARKER, "Reported exception thrown!", (Throwable)((Object)$$4));
            Minecraft.crash($$4.getReport());
        }
        catch (Throwable $$5) {
            CrashReport $$6 = this.fillReport(new CrashReport("Unexpected error", $$5));
            LOGGER.error(LogUtils.FATAL_MARKER, "Unreported exception thrown!", $$5);
            this.emergencySave();
            Minecraft.crash($$6);
        }
    }

    void selectMainFont(boolean $$0) {
        this.fontManager.setRenames((Map<ResourceLocation, ResourceLocation>)($$0 ? ImmutableMap.of((Object)DEFAULT_FONT, (Object)UNIFORM_FONT) : ImmutableMap.of()));
    }

    private void createSearchTrees() {
        this.searchRegistry.register(SearchRegistry.CREATIVE_NAMES, $$03 -> new FullTextSearchTree($$02 -> $$02.getTooltipLines(null, TooltipFlag.Default.NORMAL.asCreative()).stream().map($$0 -> ChatFormatting.stripFormatting($$0.getString()).trim()).filter($$0 -> !$$0.isEmpty()), $$0 -> Stream.of((Object)BuiltInRegistries.ITEM.getKey($$0.getItem())), $$03));
        this.searchRegistry.register(SearchRegistry.CREATIVE_TAGS, $$02 -> new IdSearchTree($$0 -> $$0.getTags().map(TagKey::location), $$02));
        this.searchRegistry.register(SearchRegistry.RECIPE_COLLECTIONS, $$0 -> new FullTextSearchTree($$02 -> $$02.getRecipes().stream().flatMap($$0 -> $$0.getResultItem().getTooltipLines(null, TooltipFlag.Default.NORMAL).stream()).map($$0 -> ChatFormatting.stripFormatting($$0.getString()).trim()).filter($$0 -> !$$0.isEmpty()), $$02 -> $$02.getRecipes().stream().map($$0 -> BuiltInRegistries.ITEM.getKey($$0.getResultItem().getItem())), $$0));
        CreativeModeTabs.searchTab().setSearchTreeBuilder((Consumer<List<ItemStack>>)((Consumer)$$0 -> {
            this.populateSearchTree(SearchRegistry.CREATIVE_NAMES, (List)$$0);
            this.populateSearchTree(SearchRegistry.CREATIVE_TAGS, (List)$$0);
        }));
    }

    private void onFullscreenError(int $$0, long $$1) {
        this.options.enableVsync().set(false);
        this.options.save();
    }

    private static boolean checkIs64Bit() {
        String[] $$0;
        for (String $$1 : $$0 = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"}) {
            String $$2 = System.getProperty((String)$$1);
            if ($$2 == null || !$$2.contains((CharSequence)"64")) continue;
            return true;
        }
        return false;
    }

    public RenderTarget getMainRenderTarget() {
        return this.mainRenderTarget;
    }

    public String getLaunchedVersion() {
        return this.launchedVersion;
    }

    public String getVersionType() {
        return this.versionType;
    }

    public void delayCrash(CrashReport $$0) {
        this.delayedCrash = () -> this.fillReport($$0);
    }

    public void delayCrashRaw(CrashReport $$0) {
        this.delayedCrash = () -> $$0;
    }

    public static void crash(CrashReport $$0) {
        File $$1 = new File(Minecraft.getInstance().gameDirectory, "crash-reports");
        File $$2 = new File($$1, "crash-" + Util.getFilenameFormattedDateTime() + "-client.txt");
        Bootstrap.realStdoutPrintln($$0.getFriendlyReport());
        if ($$0.getSaveFile() != null) {
            Bootstrap.realStdoutPrintln("#@!@# Game crashed! Crash report saved to: #@!@# " + $$0.getSaveFile());
            System.exit((int)-1);
        } else if ($$0.saveToFile($$2)) {
            Bootstrap.realStdoutPrintln("#@!@# Game crashed! Crash report saved to: #@!@# " + $$2.getAbsolutePath());
            System.exit((int)-1);
        } else {
            Bootstrap.realStdoutPrintln("#@?@# Game crashed! Crash report could not be saved. #@?@#");
            System.exit((int)-2);
        }
    }

    public boolean isEnforceUnicode() {
        return this.options.forceUnicodeFont().get();
    }

    public CompletableFuture<Void> reloadResourcePacks() {
        return this.reloadResourcePacks(false);
    }

    private CompletableFuture<Void> reloadResourcePacks(boolean $$0) {
        if (this.pendingReload != null) {
            return this.pendingReload;
        }
        CompletableFuture $$12 = new CompletableFuture();
        if (!$$0 && this.overlay instanceof LoadingOverlay) {
            this.pendingReload = $$12;
            return $$12;
        }
        this.resourcePackRepository.reload();
        List<PackResources> $$2 = this.resourcePackRepository.openAllSelected();
        if (!$$0) {
            this.reloadStateTracker.startReload(ResourceLoadStateTracker.ReloadReason.MANUAL, $$2);
        }
        this.setOverlay(new LoadingOverlay(this, this.resourceManager.createReload((Executor)Util.backgroundExecutor(), this, RESOURCE_RELOAD_INITIAL_TASK, $$2), (Consumer<Optional<Throwable>>)((Consumer)$$1 -> Util.ifElse($$1, this::rollbackResourcePacks, () -> {
            this.levelRenderer.allChanged();
            this.reloadStateTracker.finishReload();
            $$12.complete(null);
        })), true));
        return $$12;
    }

    private void selfTest() {
        boolean $$0 = false;
        BlockModelShaper $$1 = this.getBlockRenderer().getBlockModelShaper();
        BakedModel $$2 = $$1.getModelManager().getMissingModel();
        for (Block $$3 : BuiltInRegistries.BLOCK) {
            for (BlockState $$4 : $$3.getStateDefinition().getPossibleStates()) {
                BakedModel $$5;
                if ($$4.getRenderShape() != RenderShape.MODEL || ($$5 = $$1.getBlockModel($$4)) != $$2) continue;
                LOGGER.debug("Missing model for: {}", (Object)$$4);
                $$0 = true;
            }
        }
        TextureAtlasSprite $$6 = $$2.getParticleIcon();
        for (Block $$7 : BuiltInRegistries.BLOCK) {
            for (BlockState $$8 : $$7.getStateDefinition().getPossibleStates()) {
                TextureAtlasSprite $$9 = $$1.getParticleIcon($$8);
                if ($$8.isAir() || $$9 != $$6) continue;
                LOGGER.debug("Missing particle icon for: {}", (Object)$$8);
                $$0 = true;
            }
        }
        for (Item $$10 : BuiltInRegistries.ITEM) {
            ItemStack $$11 = $$10.getDefaultInstance();
            String $$12 = $$11.getDescriptionId();
            String $$13 = Component.translatable($$12).getString();
            if (!$$13.toLowerCase(Locale.ROOT).equals((Object)$$10.getDescriptionId())) continue;
            LOGGER.debug("Missing translation for: {} {} {}", new Object[]{$$11, $$12, $$10});
        }
        $$0 |= MenuScreens.selfTest();
        if ($$0 |= EntityRenderers.validateRegistrations()) {
            throw new IllegalStateException("Your game data is foobar, fix the errors above!");
        }
    }

    public LevelStorageSource getLevelSource() {
        return this.levelSource;
    }

    private void openChatScreen(String $$02) {
        ChatStatus $$1 = this.getChatStatus();
        if (!$$1.isChatAllowed(this.isLocalServer())) {
            if (this.gui.isShowingChatDisabledByPlayer()) {
                this.gui.setChatDisabledByPlayerShown(false);
                this.setScreen(new ConfirmLinkScreen($$0 -> {
                    if ($$0) {
                        Util.getPlatform().openUri("https://aka.ms/JavaAccountSettings");
                    }
                    this.setScreen(null);
                }, ChatStatus.INFO_DISABLED_BY_PROFILE, "https://aka.ms/JavaAccountSettings", true));
            } else {
                Component $$2 = $$1.getMessage();
                this.gui.setOverlayMessage($$2, false);
                this.narrator.sayNow($$2);
                this.gui.setChatDisabledByPlayerShown($$1 == ChatStatus.DISABLED_BY_PROFILE);
            }
        } else {
            this.setScreen(new ChatScreen($$02));
        }
    }

    public void setScreen(@Nullable Screen $$0) {
        if (SharedConstants.IS_RUNNING_IN_IDE && Thread.currentThread() != this.gameThread) {
            LOGGER.error("setScreen called from non-game thread");
        }
        if (this.screen != null) {
            this.screen.removed();
        }
        if ($$0 == null && this.level == null) {
            $$0 = new TitleScreen();
        } else if ($$0 == null && this.player.isDeadOrDying()) {
            if (this.player.shouldShowDeathScreen()) {
                $$0 = new DeathScreen(null, this.level.getLevelData().isHardcore());
            } else {
                this.player.respawn();
            }
        }
        this.screen = $$0;
        BufferUploader.reset();
        if ($$0 != null) {
            this.mouseHandler.releaseMouse();
            KeyMapping.releaseAll();
            $$0.init(this, this.window.getGuiScaledWidth(), this.window.getGuiScaledHeight());
            this.noRender = false;
        } else {
            this.soundManager.resume();
            this.mouseHandler.grabMouse();
        }
        this.updateTitle();
    }

    public void setOverlay(@Nullable Overlay $$0) {
        this.overlay = $$0;
    }

    public void destroy() {
        try {
            LOGGER.info("Stopping!");
            try {
                this.narrator.destroy();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            try {
                if (this.level != null) {
                    this.level.disconnect();
                }
                this.clearLevel();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            if (this.screen != null) {
                this.screen.removed();
            }
            this.close();
        }
        finally {
            Util.timeSource = System::nanoTime;
            if (this.delayedCrash == null) {
                System.exit((int)0);
            }
        }
    }

    @Override
    public void close() {
        if (this.currentFrameProfile != null) {
            this.currentFrameProfile.cancel();
        }
        try {
            this.telemetryManager.close();
            this.regionalCompliancies.close();
            this.modelManager.close();
            this.fontManager.close();
            this.gameRenderer.close();
            this.levelRenderer.close();
            this.soundManager.destroy();
            this.particleEngine.close();
            this.mobEffectTextures.close();
            this.paintingTextures.close();
            this.textureManager.close();
            this.resourceManager.close();
            Util.shutdownExecutors();
        }
        catch (Throwable $$0) {
            LOGGER.error("Shutdown failure!", $$0);
            throw $$0;
        }
        finally {
            this.virtualScreen.close();
            this.window.close();
        }
    }

    private void runTick(boolean $$02) {
        boolean $$11;
        boolean $$8;
        Runnable $$3;
        this.window.setErrorSection("Pre render");
        long $$1 = Util.getNanos();
        if (this.window.shouldClose()) {
            this.stop();
        }
        if (this.pendingReload != null && !(this.overlay instanceof LoadingOverlay)) {
            CompletableFuture<Void> $$2 = this.pendingReload;
            this.pendingReload = null;
            this.reloadResourcePacks().thenRun(() -> $$2.complete(null));
        }
        while (($$3 = (Runnable)this.progressTasks.poll()) != null) {
            $$3.run();
        }
        if ($$02) {
            int $$4 = this.timer.advanceTime(Util.getMillis());
            this.profiler.push("scheduledExecutables");
            this.runAllTasks();
            this.profiler.pop();
            this.profiler.push("tick");
            for (int $$5 = 0; $$5 < Math.min((int)10, (int)$$4); ++$$5) {
                this.profiler.incrementCounter("clientTick");
                this.tick();
            }
            this.profiler.pop();
        }
        this.mouseHandler.turnPlayer();
        this.window.setErrorSection("Render");
        this.profiler.push("sound");
        this.soundManager.updateSource(this.gameRenderer.getMainCamera());
        this.profiler.pop();
        this.profiler.push("render");
        long $$6 = Util.getNanos();
        if (this.options.renderDebug || this.metricsRecorder.isRecording()) {
            boolean $$7;
            boolean bl = $$7 = this.currentFrameProfile == null || this.currentFrameProfile.isDone();
            if ($$7) {
                TimerQuery.getInstance().ifPresent(TimerQuery::beginProfile);
            }
        } else {
            $$8 = false;
            this.gpuUtilization = 0.0;
        }
        PoseStack $$9 = RenderSystem.getModelViewStack();
        $$9.pushPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.clear(16640, ON_OSX);
        this.mainRenderTarget.bindWrite(true);
        FogRenderer.setupNoFog();
        this.profiler.push("display");
        RenderSystem.enableTexture();
        RenderSystem.enableCull();
        this.profiler.pop();
        if (!this.noRender) {
            this.profiler.popPush("gameRenderer");
            this.gameRenderer.render(this.pause ? this.pausePartialTick : this.timer.partialTick, $$1, $$02);
            this.profiler.popPush("toasts");
            this.toast.render(new PoseStack());
            this.profiler.pop();
        }
        if (this.fpsPieResults != null) {
            this.profiler.push("fpsPie");
            this.renderFpsMeter(new PoseStack(), this.fpsPieResults);
            this.profiler.pop();
        }
        this.profiler.push("blit");
        this.mainRenderTarget.unbindWrite();
        $$9.popPose();
        $$9.pushPose();
        RenderSystem.applyModelViewMatrix();
        this.mainRenderTarget.blitToScreen(this.window.getWidth(), this.window.getHeight());
        this.frameTimeNs = Util.getNanos() - $$6;
        if ($$8) {
            TimerQuery.getInstance().ifPresent($$0 -> {
                this.currentFrameProfile = $$0.endProfile();
            });
        }
        $$9.popPose();
        RenderSystem.applyModelViewMatrix();
        this.profiler.popPush("updateDisplay");
        this.window.updateDisplay();
        int $$10 = this.getFramerateLimit();
        if ($$10 < 260) {
            RenderSystem.limitDisplayFPS($$10);
        }
        this.profiler.popPush("yield");
        Thread.yield();
        this.profiler.pop();
        this.window.setErrorSection("Post render");
        ++this.frames;
        boolean bl = $$11 = this.hasSingleplayerServer() && (this.screen != null && this.screen.isPauseScreen() || this.overlay != null && this.overlay.isPauseScreen()) && !this.singleplayerServer.isPublished();
        if (this.pause != $$11) {
            if (this.pause) {
                this.pausePartialTick = this.timer.partialTick;
            } else {
                this.timer.partialTick = this.pausePartialTick;
            }
            this.pause = $$11;
        }
        long $$12 = Util.getNanos();
        long $$13 = $$12 - this.lastNanoTime;
        if ($$8) {
            this.savedCpuDuration = $$13;
        }
        this.frameTimer.logFrameDuration($$13);
        this.lastNanoTime = $$12;
        this.profiler.push("fpsUpdate");
        if (this.currentFrameProfile != null && this.currentFrameProfile.isDone()) {
            this.gpuUtilization = (double)this.currentFrameProfile.get() * 100.0 / (double)this.savedCpuDuration;
        }
        while (Util.getMillis() >= this.lastTime + 1000L) {
            String $$15;
            if (this.gpuUtilization > 0.0) {
                String $$14 = " GPU: " + (this.gpuUtilization > 100.0 ? ChatFormatting.RED + "100%" : Math.round((double)this.gpuUtilization) + "%");
            } else {
                $$15 = "";
            }
            fps = this.frames;
            this.fpsString = String.format((Locale)Locale.ROOT, (String)"%d fps T: %s%s%s%s B: %d%s", (Object[])new Object[]{fps, $$10 == 260 ? "inf" : Integer.valueOf((int)$$10), this.options.enableVsync().get() != false ? " vsync" : "", this.options.graphicsMode().get(), this.options.cloudStatus().get() == CloudStatus.OFF ? "" : (this.options.cloudStatus().get() == CloudStatus.FAST ? " fast-clouds" : " fancy-clouds"), this.options.biomeBlendRadius().get(), $$15});
            this.lastTime += 1000L;
            this.frames = 0;
        }
        this.profiler.pop();
    }

    private boolean shouldRenderFpsPie() {
        return this.options.renderDebug && this.options.renderDebugCharts && !this.options.hideGui;
    }

    private ProfilerFiller constructProfiler(boolean $$0, @Nullable SingleTickProfiler $$1) {
        ProfilerFiller $$3;
        if (!$$0) {
            this.fpsPieProfiler.disable();
            if (!this.metricsRecorder.isRecording() && $$1 == null) {
                return InactiveProfiler.INSTANCE;
            }
        }
        if ($$0) {
            if (!this.fpsPieProfiler.isEnabled()) {
                this.fpsPieRenderTicks = 0;
                this.fpsPieProfiler.enable();
            }
            ++this.fpsPieRenderTicks;
            ProfilerFiller $$2 = this.fpsPieProfiler.getFiller();
        } else {
            $$3 = InactiveProfiler.INSTANCE;
        }
        if (this.metricsRecorder.isRecording()) {
            $$3 = ProfilerFiller.tee($$3, this.metricsRecorder.getProfiler());
        }
        return SingleTickProfiler.decorateFiller($$3, $$1);
    }

    private void finishProfilers(boolean $$0, @Nullable SingleTickProfiler $$1) {
        if ($$1 != null) {
            $$1.endTick();
        }
        this.fpsPieResults = $$0 ? this.fpsPieProfiler.getResults() : null;
        this.profiler = this.fpsPieProfiler.getFiller();
    }

    @Override
    public void resizeDisplay() {
        int $$0 = this.window.calculateScale(this.options.guiScale().get(), this.isEnforceUnicode());
        this.window.setGuiScale($$0);
        if (this.screen != null) {
            this.screen.resize(this, this.window.getGuiScaledWidth(), this.window.getGuiScaledHeight());
        }
        RenderTarget $$1 = this.getMainRenderTarget();
        $$1.resize(this.window.getWidth(), this.window.getHeight(), ON_OSX);
        this.gameRenderer.resize(this.window.getWidth(), this.window.getHeight());
        this.mouseHandler.setIgnoreFirstMove();
    }

    @Override
    public void cursorEntered() {
        this.mouseHandler.cursorEntered();
    }

    public int getFps() {
        return fps;
    }

    public long getFrameTimeNs() {
        return this.frameTimeNs;
    }

    private int getFramerateLimit() {
        if (this.level == null && (this.screen != null || this.overlay != null)) {
            return 60;
        }
        return this.window.getFramerateLimit();
    }

    public void emergencySave() {
        try {
            MemoryReserve.release();
            this.levelRenderer.clear();
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            System.gc();
            if (this.isLocalServer && this.singleplayerServer != null) {
                this.singleplayerServer.halt(true);
            }
            this.clearLevel(new GenericDirtMessageScreen(Component.translatable("menu.savingLevel")));
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        System.gc();
    }

    public boolean debugClientMetricsStart(Consumer<Component> $$02) {
        Consumer $$8;
        if (this.metricsRecorder.isRecording()) {
            this.debugClientMetricsStop();
            return false;
        }
        Consumer $$13 = $$1 -> {
            if ($$1 == EmptyProfileResults.EMPTY) {
                return;
            }
            int $$2 = $$1.getTickDuration();
            double $$3 = (double)$$1.getNanoDuration() / (double)TimeUtil.NANOSECONDS_PER_SECOND;
            this.execute(() -> $$02.accept((Object)Component.translatable("commands.debug.stopped", String.format((Locale)Locale.ROOT, (String)"%.2f", (Object[])new Object[]{$$3}), $$2, String.format((Locale)Locale.ROOT, (String)"%.2f", (Object[])new Object[]{(double)$$2 / $$3}))));
        };
        Consumer $$22 = $$12 -> {
            MutableComponent $$2 = Component.literal($$12.toString()).withStyle(ChatFormatting.UNDERLINE).withStyle((UnaryOperator<Style>)((UnaryOperator)$$1 -> $$1.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, $$12.toFile().getParent()))));
            this.execute(() -> $$02.accept((Object)Component.translatable("debug.profiling.stop", $$2)));
        };
        SystemReport $$3 = Minecraft.fillSystemReport(new SystemReport(), this, this.languageManager, this.launchedVersion, this.options);
        Consumer $$4 = $$2 -> {
            Path $$3 = this.archiveProfilingReport($$3, (List<Path>)$$2);
            $$22.accept((Object)$$3);
        };
        if (this.singleplayerServer == null) {
            Consumer $$5 = $$1 -> $$4.accept((Object)ImmutableList.of((Object)$$1));
        } else {
            this.singleplayerServer.fillSystemReport($$3);
            CompletableFuture $$6 = new CompletableFuture();
            CompletableFuture $$7 = new CompletableFuture();
            CompletableFuture.allOf((CompletableFuture[])new CompletableFuture[]{$$6, $$7}).thenRunAsync(() -> $$4.accept((Object)ImmutableList.of((Object)((Path)$$6.join()), (Object)((Path)$$7.join()))), (Executor)Util.ioPool());
            this.singleplayerServer.startRecordingMetrics((Consumer<ProfileResults>)((Consumer)$$0 -> {}), (Consumer<Path>)((Consumer)arg_0 -> ((CompletableFuture)$$7).complete(arg_0)));
            $$8 = arg_0 -> ((CompletableFuture)$$6).complete(arg_0);
        }
        this.metricsRecorder = ActiveMetricsRecorder.createStarted(new ClientMetricsSamplersProvider(Util.timeSource, this.levelRenderer), Util.timeSource, (Executor)Util.ioPool(), new MetricsPersister("client"), (Consumer<ProfileResults>)((Consumer)$$1 -> {
            this.metricsRecorder = InactiveMetricsRecorder.INSTANCE;
            $$13.accept($$1);
        }), (Consumer<Path>)$$8);
        return true;
    }

    private void debugClientMetricsStop() {
        this.metricsRecorder.end();
        if (this.singleplayerServer != null) {
            this.singleplayerServer.finishRecordingMetrics();
        }
    }

    private void debugClientMetricsCancel() {
        this.metricsRecorder.cancel();
        if (this.singleplayerServer != null) {
            this.singleplayerServer.cancelRecordingMetrics();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * WARNING - void declaration
     */
    private Path archiveProfilingReport(SystemReport $$0, List<Path> $$1) {
        void $$9;
        String $$4;
        if (this.isLocalServer()) {
            String $$2 = this.getSingleplayerServer().getWorldData().getLevelName();
        } else {
            ServerData $$3 = this.getCurrentServer();
            $$4 = $$3 != null ? $$3.name : "unknown";
        }
        try {
            String $$5 = String.format((Locale)Locale.ROOT, (String)"%s-%s-%s", (Object[])new Object[]{Util.getFilenameFormattedDateTime(), $$4, SharedConstants.getCurrentVersion().getId()});
            String $$6 = FileUtil.findAvailableName(MetricsPersister.PROFILING_RESULTS_DIR, $$5, ".zip");
            Path $$7 = MetricsPersister.PROFILING_RESULTS_DIR.resolve($$6);
        }
        catch (IOException $$8) {
            throw new UncheckedIOException($$8);
        }
        try (FileZipper $$10 = new FileZipper((Path)$$9);){
            $$10.add(Paths.get((String)"system.txt", (String[])new String[0]), $$0.toLineSeparatedString());
            $$10.add(Paths.get((String)"client", (String[])new String[0]).resolve(this.options.getFile().getName()), this.options.dumpOptionsForReport());
            $$1.forEach($$10::add);
        }
        finally {
            for (Path $$11 : $$1) {
                try {
                    FileUtils.forceDelete((File)$$11.toFile());
                }
                catch (IOException $$12) {
                    LOGGER.warn("Failed to delete temporary profiling result {}", (Object)$$11, (Object)$$12);
                }
            }
        }
        return $$9;
    }

    public void debugFpsMeterKeyPress(int $$0) {
        if (this.fpsPieResults == null) {
            return;
        }
        List<ResultField> $$1 = this.fpsPieResults.getTimes(this.debugPath);
        if ($$1.isEmpty()) {
            return;
        }
        ResultField $$2 = (ResultField)$$1.remove(0);
        if ($$0 == 0) {
            int $$3;
            if (!$$2.name.isEmpty() && ($$3 = this.debugPath.lastIndexOf(30)) >= 0) {
                this.debugPath = this.debugPath.substring(0, $$3);
            }
        } else if (--$$0 < $$1.size() && !"unspecified".equals((Object)((ResultField)$$1.get((int)$$0)).name)) {
            if (!this.debugPath.isEmpty()) {
                this.debugPath = this.debugPath + "\u001e";
            }
            this.debugPath = this.debugPath + ((ResultField)$$1.get((int)$$0)).name;
        }
    }

    private void renderFpsMeter(PoseStack $$0, ProfileResults $$1) {
        List<ResultField> $$2 = $$1.getTimes(this.debugPath);
        ResultField $$3 = (ResultField)$$2.remove(0);
        RenderSystem.clear(256, ON_OSX);
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionColorShader));
        Matrix4f $$4 = new Matrix4f().setOrtho(0.0f, (float)this.window.getWidth(), (float)this.window.getHeight(), 0.0f, 1000.0f, 3000.0f);
        RenderSystem.setProjectionMatrix($$4);
        PoseStack $$5 = RenderSystem.getModelViewStack();
        $$5.setIdentity();
        $$5.translate(0.0f, 0.0f, -2000.0f);
        RenderSystem.applyModelViewMatrix();
        RenderSystem.lineWidth(1.0f);
        RenderSystem.disableTexture();
        Tesselator $$6 = Tesselator.getInstance();
        BufferBuilder $$7 = $$6.getBuilder();
        int $$8 = 160;
        int $$9 = this.window.getWidth() - 160 - 10;
        int $$10 = this.window.getHeight() - 320;
        RenderSystem.enableBlend();
        $$7.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        $$7.vertex((float)$$9 - 176.0f, (float)$$10 - 96.0f - 16.0f, 0.0).color(200, 0, 0, 0).endVertex();
        $$7.vertex((float)$$9 - 176.0f, $$10 + 320, 0.0).color(200, 0, 0, 0).endVertex();
        $$7.vertex((float)$$9 + 176.0f, $$10 + 320, 0.0).color(200, 0, 0, 0).endVertex();
        $$7.vertex((float)$$9 + 176.0f, (float)$$10 - 96.0f - 16.0f, 0.0).color(200, 0, 0, 0).endVertex();
        $$6.end();
        RenderSystem.disableBlend();
        double $$11 = 0.0;
        for (ResultField $$12 : $$2) {
            int $$13 = Mth.floor($$12.percentage / 4.0) + 1;
            $$7.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);
            int $$14 = $$12.getColor();
            int $$15 = $$14 >> 16 & 0xFF;
            int $$16 = $$14 >> 8 & 0xFF;
            int $$17 = $$14 & 0xFF;
            $$7.vertex($$9, $$10, 0.0).color($$15, $$16, $$17, 255).endVertex();
            for (int $$18 = $$13; $$18 >= 0; --$$18) {
                float $$19 = (float)(($$11 + $$12.percentage * (double)$$18 / (double)$$13) * 6.2831854820251465 / 100.0);
                float $$20 = Mth.sin($$19) * 160.0f;
                float $$21 = Mth.cos($$19) * 160.0f * 0.5f;
                $$7.vertex((float)$$9 + $$20, (float)$$10 - $$21, 0.0).color($$15, $$16, $$17, 255).endVertex();
            }
            $$6.end();
            $$7.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_COLOR);
            for (int $$22 = $$13; $$22 >= 0; --$$22) {
                float $$23 = (float)(($$11 + $$12.percentage * (double)$$22 / (double)$$13) * 6.2831854820251465 / 100.0);
                float $$24 = Mth.sin($$23) * 160.0f;
                float $$25 = Mth.cos($$23) * 160.0f * 0.5f;
                if ($$25 > 0.0f) continue;
                $$7.vertex((float)$$9 + $$24, (float)$$10 - $$25, 0.0).color($$15 >> 1, $$16 >> 1, $$17 >> 1, 255).endVertex();
                $$7.vertex((float)$$9 + $$24, (float)$$10 - $$25 + 10.0f, 0.0).color($$15 >> 1, $$16 >> 1, $$17 >> 1, 255).endVertex();
            }
            $$6.end();
            $$11 += $$12.percentage;
        }
        DecimalFormat $$26 = new DecimalFormat("##0.00");
        $$26.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance((Locale)Locale.ROOT));
        RenderSystem.enableTexture();
        String $$27 = ProfileResults.demanglePath($$3.name);
        String $$28 = "";
        if (!"unspecified".equals((Object)$$27)) {
            $$28 = $$28 + "[0] ";
        }
        $$28 = $$27.isEmpty() ? $$28 + "ROOT " : $$28 + $$27 + " ";
        int $$29 = 0xFFFFFF;
        this.font.drawShadow($$0, $$28, (float)($$9 - 160), (float)($$10 - 80 - 16), 0xFFFFFF);
        $$28 = $$26.format($$3.globalPercentage) + "%";
        this.font.drawShadow($$0, $$28, (float)($$9 + 160 - this.font.width($$28)), (float)($$10 - 80 - 16), 0xFFFFFF);
        for (int $$30 = 0; $$30 < $$2.size(); ++$$30) {
            ResultField $$31 = (ResultField)$$2.get($$30);
            StringBuilder $$32 = new StringBuilder();
            if ("unspecified".equals((Object)$$31.name)) {
                $$32.append("[?] ");
            } else {
                $$32.append("[").append($$30 + 1).append("] ");
            }
            String $$33 = $$32.append($$31.name).toString();
            this.font.drawShadow($$0, $$33, (float)($$9 - 160), (float)($$10 + 80 + $$30 * 8 + 20), $$31.getColor());
            $$33 = $$26.format($$31.percentage) + "%";
            this.font.drawShadow($$0, $$33, (float)($$9 + 160 - 50 - this.font.width($$33)), (float)($$10 + 80 + $$30 * 8 + 20), $$31.getColor());
            $$33 = $$26.format($$31.globalPercentage) + "%";
            this.font.drawShadow($$0, $$33, (float)($$9 + 160 - this.font.width($$33)), (float)($$10 + 80 + $$30 * 8 + 20), $$31.getColor());
        }
    }

    public void stop() {
        this.running = false;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void pauseGame(boolean $$0) {
        boolean $$1;
        if (this.screen != null) {
            return;
        }
        boolean bl = $$1 = this.hasSingleplayerServer() && !this.singleplayerServer.isPublished();
        if ($$1) {
            this.setScreen(new PauseScreen(!$$0));
            this.soundManager.pause();
        } else {
            this.setScreen(new PauseScreen(true));
        }
    }

    private void continueAttack(boolean $$0) {
        if (!$$0) {
            this.missTime = 0;
        }
        if (this.missTime > 0 || this.player.isUsingItem()) {
            return;
        }
        if ($$0 && this.hitResult != null && this.hitResult.getType() == HitResult.Type.BLOCK) {
            Direction $$3;
            BlockHitResult $$1 = (BlockHitResult)this.hitResult;
            BlockPos $$2 = $$1.getBlockPos();
            if (!this.level.getBlockState($$2).isAir() && this.gameMode.continueDestroyBlock($$2, $$3 = $$1.getDirection())) {
                this.particleEngine.crack($$2, $$3);
                this.player.swing(InteractionHand.MAIN_HAND);
            }
            return;
        }
        this.gameMode.stopDestroyBlock();
    }

    private boolean startAttack() {
        if (this.missTime > 0) {
            return false;
        }
        if (this.hitResult == null) {
            LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");
            if (this.gameMode.hasMissTime()) {
                this.missTime = 10;
            }
            return false;
        }
        if (this.player.isHandsBusy()) {
            return false;
        }
        ItemStack $$0 = this.player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!$$0.isItemEnabled(this.level.enabledFeatures())) {
            return false;
        }
        boolean $$1 = false;
        switch (this.hitResult.getType()) {
            case ENTITY: {
                this.gameMode.attack(this.player, ((EntityHitResult)this.hitResult).getEntity());
                break;
            }
            case BLOCK: {
                BlockHitResult $$2 = (BlockHitResult)this.hitResult;
                BlockPos $$3 = $$2.getBlockPos();
                if (!this.level.getBlockState($$3).isAir()) {
                    this.gameMode.startDestroyBlock($$3, $$2.getDirection());
                    if (!this.level.getBlockState($$3).isAir()) break;
                    $$1 = true;
                    break;
                }
            }
            case MISS: {
                if (this.gameMode.hasMissTime()) {
                    this.missTime = 10;
                }
                this.player.resetAttackStrengthTicker();
            }
        }
        this.player.swing(InteractionHand.MAIN_HAND);
        return $$1;
    }

    private void startUseItem() {
        if (this.gameMode.isDestroying()) {
            return;
        }
        this.rightClickDelay = 4;
        if (this.player.isHandsBusy()) {
            return;
        }
        if (this.hitResult == null) {
            LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
        }
        for (InteractionHand $$0 : InteractionHand.values()) {
            InteractionResult $$8;
            ItemStack $$1 = this.player.getItemInHand($$0);
            if (!$$1.isItemEnabled(this.level.enabledFeatures())) {
                return;
            }
            if (this.hitResult != null) {
                switch (this.hitResult.getType()) {
                    case ENTITY: {
                        EntityHitResult $$2 = (EntityHitResult)this.hitResult;
                        Entity $$3 = $$2.getEntity();
                        if (!this.level.getWorldBorder().isWithinBounds($$3.blockPosition())) {
                            return;
                        }
                        InteractionResult $$4 = this.gameMode.interactAt(this.player, $$3, $$2, $$0);
                        if (!$$4.consumesAction()) {
                            $$4 = this.gameMode.interact(this.player, $$3, $$0);
                        }
                        if (!$$4.consumesAction()) break;
                        if ($$4.shouldSwing()) {
                            this.player.swing($$0);
                        }
                        return;
                    }
                    case BLOCK: {
                        BlockHitResult $$5 = (BlockHitResult)this.hitResult;
                        int $$6 = $$1.getCount();
                        InteractionResult $$7 = this.gameMode.useItemOn(this.player, $$0, $$5);
                        if ($$7.consumesAction()) {
                            if ($$7.shouldSwing()) {
                                this.player.swing($$0);
                                if (!$$1.isEmpty() && ($$1.getCount() != $$6 || this.gameMode.hasInfiniteItems())) {
                                    this.gameRenderer.itemInHandRenderer.itemUsed($$0);
                                }
                            }
                            return;
                        }
                        if ($$7 != InteractionResult.FAIL) break;
                        return;
                    }
                }
            }
            if ($$1.isEmpty() || !($$8 = this.gameMode.useItem(this.player, $$0)).consumesAction()) continue;
            if ($$8.shouldSwing()) {
                this.player.swing($$0);
            }
            this.gameRenderer.itemInHandRenderer.itemUsed($$0);
            return;
        }
    }

    public MusicManager getMusicManager() {
        return this.musicManager;
    }

    public void tick() {
        if (this.rightClickDelay > 0) {
            --this.rightClickDelay;
        }
        this.profiler.push("gui");
        this.chatListener.tick();
        this.gui.tick(this.pause);
        this.profiler.pop();
        this.gameRenderer.pick(1.0f);
        this.tutorial.onLookAt(this.level, this.hitResult);
        this.profiler.push("gameMode");
        if (!this.pause && this.level != null) {
            this.gameMode.tick();
        }
        this.profiler.popPush("textures");
        if (this.level != null) {
            this.textureManager.tick();
        }
        if (this.screen == null && this.player != null) {
            if (this.player.isDeadOrDying() && !(this.screen instanceof DeathScreen)) {
                this.setScreen(null);
            } else if (this.player.isSleeping() && this.level != null) {
                this.setScreen(new InBedChatScreen());
            }
        } else {
            Screen screen = this.screen;
            if (screen instanceof InBedChatScreen) {
                InBedChatScreen $$0 = (InBedChatScreen)screen;
                if (!this.player.isSleeping()) {
                    $$0.onPlayerWokeUp();
                }
            }
        }
        if (this.screen != null) {
            this.missTime = 10000;
        }
        if (this.screen != null) {
            Screen.wrapScreenError(() -> this.screen.tick(), "Ticking screen", this.screen.getClass().getCanonicalName());
        }
        if (!this.options.renderDebug) {
            this.gui.clearCache();
        }
        if (this.overlay == null && (this.screen == null || this.screen.passEvents)) {
            this.profiler.popPush("Keybindings");
            this.handleKeybinds();
            if (this.missTime > 0) {
                --this.missTime;
            }
        }
        if (this.level != null) {
            this.profiler.popPush("gameRenderer");
            if (!this.pause) {
                this.gameRenderer.tick();
            }
            this.profiler.popPush("levelRenderer");
            if (!this.pause) {
                this.levelRenderer.tick();
            }
            this.profiler.popPush("level");
            if (!this.pause) {
                if (this.level.getSkyFlashTime() > 0) {
                    this.level.setSkyFlashTime(this.level.getSkyFlashTime() - 1);
                }
                this.level.tickEntities();
            }
        } else if (this.gameRenderer.currentEffect() != null) {
            this.gameRenderer.shutdownEffect();
        }
        if (!this.pause) {
            this.musicManager.tick();
        }
        this.soundManager.tick(this.pause);
        if (this.level != null) {
            if (!this.pause) {
                if (!this.options.joinedFirstServer && this.isMultiplayerServer()) {
                    MutableComponent $$1 = Component.translatable("tutorial.socialInteractions.title");
                    MutableComponent $$2 = Component.translatable("tutorial.socialInteractions.description", Tutorial.key("socialInteractions"));
                    this.socialInteractionsToast = new TutorialToast(TutorialToast.Icons.SOCIAL_INTERACTIONS, $$1, $$2, true);
                    this.tutorial.addTimedToast(this.socialInteractionsToast, 160);
                    this.options.joinedFirstServer = true;
                    this.options.save();
                }
                this.tutorial.tick();
                try {
                    this.level.tick(() -> true);
                }
                catch (Throwable $$3) {
                    CrashReport $$4 = CrashReport.forThrowable($$3, "Exception in world tick");
                    if (this.level == null) {
                        CrashReportCategory $$5 = $$4.addCategory("Affected level");
                        $$5.setDetail("Problem", "Level is null!");
                    } else {
                        this.level.fillReportDetails($$4);
                    }
                    throw new ReportedException($$4);
                }
            }
            this.profiler.popPush("animateTick");
            if (!this.pause && this.level != null) {
                this.level.animateTick(this.player.getBlockX(), this.player.getBlockY(), this.player.getBlockZ());
            }
            this.profiler.popPush("particles");
            if (!this.pause) {
                this.particleEngine.tick();
            }
        } else if (this.pendingConnection != null) {
            this.profiler.popPush("pendingConnection");
            this.pendingConnection.tick();
        }
        this.profiler.popPush("keyboard");
        this.keyboardHandler.tick();
        this.profiler.pop();
    }

    private boolean isMultiplayerServer() {
        return !this.isLocalServer || this.singleplayerServer != null && this.singleplayerServer.isPublished();
    }

    private void handleKeybinds() {
        while (this.options.keyTogglePerspective.consumeClick()) {
            CameraType $$0 = this.options.getCameraType();
            this.options.setCameraType(this.options.getCameraType().cycle());
            if ($$0.isFirstPerson() != this.options.getCameraType().isFirstPerson()) {
                this.gameRenderer.checkEntityPostEffect(this.options.getCameraType().isFirstPerson() ? this.getCameraEntity() : null);
            }
            this.levelRenderer.needsUpdate();
        }
        while (this.options.keySmoothCamera.consumeClick()) {
            this.options.smoothCamera = !this.options.smoothCamera;
        }
        for (int $$1 = 0; $$1 < 9; ++$$1) {
            boolean $$2 = this.options.keySaveHotbarActivator.isDown();
            boolean $$3 = this.options.keyLoadHotbarActivator.isDown();
            if (!this.options.keyHotbarSlots[$$1].consumeClick()) continue;
            if (this.player.isSpectator()) {
                this.gui.getSpectatorGui().onHotbarSelected($$1);
                continue;
            }
            if (this.player.isCreative() && this.screen == null && ($$3 || $$2)) {
                CreativeModeInventoryScreen.handleHotbarLoadOrSave(this, $$1, $$3, $$2);
                continue;
            }
            this.player.getInventory().selected = $$1;
        }
        while (this.options.keySocialInteractions.consumeClick()) {
            if (!this.isMultiplayerServer()) {
                this.player.displayClientMessage(SOCIAL_INTERACTIONS_NOT_AVAILABLE, true);
                this.narrator.sayNow(SOCIAL_INTERACTIONS_NOT_AVAILABLE);
                continue;
            }
            if (this.socialInteractionsToast != null) {
                this.tutorial.removeTimedToast(this.socialInteractionsToast);
                this.socialInteractionsToast = null;
            }
            this.setScreen(new SocialInteractionsScreen());
        }
        while (this.options.keyInventory.consumeClick()) {
            if (this.gameMode.isServerControlledInventory()) {
                this.player.sendOpenInventory();
                continue;
            }
            this.tutorial.onOpenInventory();
            this.setScreen(new InventoryScreen(this.player));
        }
        while (this.options.keyAdvancements.consumeClick()) {
            this.setScreen(new AdvancementsScreen(this.player.connection.getAdvancements()));
        }
        while (this.options.keySwapOffhand.consumeClick()) {
            if (this.player.isSpectator()) continue;
            this.getConnection().send(new ServerboundPlayerActionPacket(ServerboundPlayerActionPacket.Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
        }
        while (this.options.keyDrop.consumeClick()) {
            if (this.player.isSpectator() || !this.player.drop(Screen.hasControlDown())) continue;
            this.player.swing(InteractionHand.MAIN_HAND);
        }
        while (this.options.keyChat.consumeClick()) {
            this.openChatScreen("");
        }
        if (this.screen == null && this.overlay == null && this.options.keyCommand.consumeClick()) {
            this.openChatScreen("/");
        }
        boolean $$4 = false;
        if (this.player.isUsingItem()) {
            if (!this.options.keyUse.isDown()) {
                this.gameMode.releaseUsingItem(this.player);
            }
            while (this.options.keyAttack.consumeClick()) {
            }
            while (this.options.keyUse.consumeClick()) {
            }
            while (this.options.keyPickItem.consumeClick()) {
            }
        } else {
            while (this.options.keyAttack.consumeClick()) {
                $$4 |= this.startAttack();
            }
            while (this.options.keyUse.consumeClick()) {
                this.startUseItem();
            }
            while (this.options.keyPickItem.consumeClick()) {
                this.pickBlock();
            }
        }
        if (this.options.keyUse.isDown() && this.rightClickDelay == 0 && !this.player.isUsingItem()) {
            this.startUseItem();
        }
        this.continueAttack(this.screen == null && !$$4 && this.options.keyAttack.isDown() && this.mouseHandler.isMouseGrabbed());
    }

    public ClientTelemetryManager getTelemetryManager() {
        return this.telemetryManager;
    }

    public double getGpuUtilization() {
        return this.gpuUtilization;
    }

    public ProfileKeyPairManager getProfileKeyPairManager() {
        return this.profileKeyPairManager;
    }

    public WorldOpenFlows createWorldOpenFlows() {
        return new WorldOpenFlows(this, this.levelSource);
    }

    public void doWorldLoad(String $$02, LevelStorageSource.LevelStorageAccess $$1, PackRepository $$2, WorldStem $$3, boolean $$42) {
        this.clearLevel();
        this.progressListener.set(null);
        Instant $$5 = Instant.now();
        try {
            $$1.saveDataTag($$3.registries().compositeAccess(), $$3.worldData());
            Services $$6 = Services.create(this.authenticationService, this.gameDirectory);
            $$6.profileCache().setExecutor(this);
            SkullBlockEntity.setup($$6, this);
            GameProfileCache.setUsesAuthentication(false);
            this.singleplayerServer = (IntegratedServer)MinecraftServer.spin($$4 -> new IntegratedServer((Thread)$$4, this, $$1, $$2, $$3, $$6, $$0 -> {
                PackRepository $$2 = new StoringChunkProgressListener($$0 + 0);
                this.progressListener.set((Object)$$2);
                return ProcessorChunkProgressListener.createStarted($$2, arg_0 -> this.progressTasks.add(arg_0));
            }));
            this.isLocalServer = true;
            this.updateReportEnvironment(ReportEnvironment.local());
        }
        catch (Throwable $$7) {
            CrashReport $$8 = CrashReport.forThrowable($$7, "Starting integrated server");
            CrashReportCategory $$9 = $$8.addCategory("Starting integrated server");
            $$9.setDetail("Level ID", $$02);
            $$9.setDetail("Level Name", () -> $$3.worldData().getLevelName());
            throw new ReportedException($$8);
        }
        while (this.progressListener.get() == null) {
            Thread.yield();
        }
        LevelLoadingScreen $$10 = new LevelLoadingScreen((StoringChunkProgressListener)this.progressListener.get());
        this.setScreen($$10);
        this.profiler.push("waitForServer");
        while (!this.singleplayerServer.isReady()) {
            $$10.tick();
            this.runTick(false);
            try {
                Thread.sleep((long)16L);
            }
            catch (InterruptedException $$8) {
                // empty catch block
            }
            if (this.delayedCrash == null) continue;
            Minecraft.crash((CrashReport)this.delayedCrash.get());
            return;
        }
        this.profiler.pop();
        Duration $$11 = Duration.between((Temporal)$$5, (Temporal)Instant.now());
        SocketAddress $$12 = this.singleplayerServer.getConnection().startMemoryChannel();
        Connection $$13 = Connection.connectToLocalServer($$12);
        $$13.setListener(new ClientHandshakePacketListenerImpl($$13, this, null, null, $$42, $$11, (Consumer<Component>)((Consumer)$$0 -> {})));
        $$13.send(new ClientIntentionPacket($$12.toString(), 0, ConnectionProtocol.LOGIN));
        $$13.send(new ServerboundHelloPacket(this.getUser().getName(), (Optional<UUID>)Optional.ofNullable((Object)this.getUser().getProfileId())));
        this.pendingConnection = $$13;
    }

    public void setLevel(ClientLevel $$0) {
        ProgressScreen $$1 = new ProgressScreen(true);
        $$1.progressStartNoAbort(Component.translatable("connect.joining"));
        this.updateScreenAndTick($$1);
        this.level = $$0;
        this.updateLevelInEngines($$0);
        if (!this.isLocalServer) {
            Services $$2 = Services.create(this.authenticationService, this.gameDirectory);
            $$2.profileCache().setExecutor(this);
            SkullBlockEntity.setup($$2, this);
            GameProfileCache.setUsesAuthentication(false);
        }
    }

    public void clearLevel() {
        this.clearLevel(new ProgressScreen(true));
    }

    public void clearLevel(Screen $$0) {
        ClientPacketListener $$1 = this.getConnection();
        if ($$1 != null) {
            this.dropAllTasks();
            $$1.close();
        }
        this.playerSocialManager.stopOnlineMode();
        if (this.metricsRecorder.isRecording()) {
            this.debugClientMetricsCancel();
        }
        IntegratedServer $$2 = this.singleplayerServer;
        this.singleplayerServer = null;
        this.gameRenderer.resetData();
        this.gameMode = null;
        this.narrator.clear();
        this.updateScreenAndTick($$0);
        if (this.level != null) {
            if ($$2 != null) {
                this.profiler.push("waitForServer");
                while (!$$2.isShutdown()) {
                    this.runTick(false);
                }
                this.profiler.pop();
            }
            this.downloadedPackSource.clearServerPack();
            this.gui.onDisconnected();
            this.isLocalServer = false;
            this.game.onLeaveGameSession();
        }
        this.level = null;
        this.updateLevelInEngines(null);
        this.player = null;
        SkullBlockEntity.clear();
    }

    private void updateScreenAndTick(Screen $$0) {
        this.profiler.push("forcedTick");
        this.soundManager.stop();
        this.cameraEntity = null;
        this.pendingConnection = null;
        this.setScreen($$0);
        this.runTick(false);
        this.profiler.pop();
    }

    public void forceSetScreen(Screen $$0) {
        this.profiler.push("forcedTick");
        this.setScreen($$0);
        this.runTick(false);
        this.profiler.pop();
    }

    private void updateLevelInEngines(@Nullable ClientLevel $$0) {
        this.levelRenderer.setLevel($$0);
        this.particleEngine.setLevel($$0);
        this.blockEntityRenderDispatcher.setLevel($$0);
        this.updateTitle();
    }

    public boolean telemetryOptInExtra() {
        return this.extraTelemetryAvailable() && this.options.telemetryOptInExtra().get() != false;
    }

    public boolean extraTelemetryAvailable() {
        return this.allowsTelemetry() && this.userApiService.properties().flag(UserApiService.UserFlag.OPTIONAL_TELEMETRY_AVAILABLE);
    }

    public boolean allowsTelemetry() {
        return this.userApiService.properties().flag(UserApiService.UserFlag.TELEMETRY_ENABLED);
    }

    public boolean allowsMultiplayer() {
        return this.allowsMultiplayer && this.userApiService.properties().flag(UserApiService.UserFlag.SERVERS_ALLOWED) && this.multiplayerBan() == null;
    }

    public boolean allowsRealms() {
        return this.userApiService.properties().flag(UserApiService.UserFlag.REALMS_ALLOWED) && this.multiplayerBan() == null;
    }

    public boolean shouldShowBanNotice() {
        return this.multiplayerBan() != null;
    }

    @Nullable
    public BanDetails multiplayerBan() {
        return (BanDetails)this.userApiService.properties().bannedScopes().get((Object)"MULTIPLAYER");
    }

    public boolean isBlocked(UUID $$0) {
        if (!this.getChatStatus().isChatAllowed(false)) {
            return (this.player == null || !$$0.equals((Object)this.player.getUUID())) && !$$0.equals((Object)Util.NIL_UUID);
        }
        return this.playerSocialManager.shouldHideMessageFrom($$0);
    }

    public ChatStatus getChatStatus() {
        if (this.options.chatVisibility().get() == ChatVisiblity.HIDDEN) {
            return ChatStatus.DISABLED_BY_OPTIONS;
        }
        if (!this.allowsChat) {
            return ChatStatus.DISABLED_BY_LAUNCHER;
        }
        if (!this.userApiService.properties().flag(UserApiService.UserFlag.CHAT_ALLOWED)) {
            return ChatStatus.DISABLED_BY_PROFILE;
        }
        return ChatStatus.ENABLED;
    }

    public final boolean isDemo() {
        return this.demo;
    }

    @Nullable
    public ClientPacketListener getConnection() {
        return this.player == null ? null : this.player.connection;
    }

    public static boolean renderNames() {
        return !Minecraft.instance.options.hideGui;
    }

    public static boolean useFancyGraphics() {
        return Minecraft.instance.options.graphicsMode().get().getId() >= GraphicsStatus.FANCY.getId();
    }

    public static boolean useShaderTransparency() {
        return !Minecraft.instance.gameRenderer.isPanoramicMode() && Minecraft.instance.options.graphicsMode().get().getId() >= GraphicsStatus.FABULOUS.getId();
    }

    public static boolean useAmbientOcclusion() {
        return Minecraft.instance.options.ambientOcclusion().get();
    }

    /*
     * WARNING - void declaration
     */
    private void pickBlock() {
        void $$9;
        if (this.hitResult == null || this.hitResult.getType() == HitResult.Type.MISS) {
            return;
        }
        boolean $$0 = this.player.getAbilities().instabuild;
        BlockEntity $$1 = null;
        HitResult.Type $$2 = this.hitResult.getType();
        if ($$2 == HitResult.Type.BLOCK) {
            BlockPos $$3 = ((BlockHitResult)this.hitResult).getBlockPos();
            BlockState $$4 = this.level.getBlockState($$3);
            if ($$4.isAir()) {
                return;
            }
            Block $$5 = $$4.getBlock();
            ItemStack $$6 = $$5.getCloneItemStack(this.level, $$3, $$4);
            if ($$6.isEmpty()) {
                return;
            }
            if ($$0 && Screen.hasControlDown() && $$4.hasBlockEntity()) {
                $$1 = this.level.getBlockEntity($$3);
            }
        } else if ($$2 == HitResult.Type.ENTITY && $$0) {
            Entity $$7 = ((EntityHitResult)this.hitResult).getEntity();
            ItemStack $$8 = $$7.getPickResult();
            if ($$8 == null) {
                return;
            }
        } else {
            return;
        }
        if ($$9.isEmpty()) {
            String $$10 = "";
            if ($$2 == HitResult.Type.BLOCK) {
                $$10 = BuiltInRegistries.BLOCK.getKey(this.level.getBlockState(((BlockHitResult)this.hitResult).getBlockPos()).getBlock()).toString();
            } else if ($$2 == HitResult.Type.ENTITY) {
                $$10 = BuiltInRegistries.ENTITY_TYPE.getKey(((EntityHitResult)this.hitResult).getEntity().getType()).toString();
            }
            LOGGER.warn("Picking on: [{}] {} gave null item", (Object)$$2, (Object)$$10);
            return;
        }
        Inventory $$11 = this.player.getInventory();
        if ($$1 != null) {
            this.addCustomNbtData((ItemStack)$$9, $$1);
        }
        int $$12 = $$11.findSlotMatchingItem((ItemStack)$$9);
        if ($$0) {
            $$11.setPickedItem((ItemStack)$$9);
            this.gameMode.handleCreativeModeItemAdd(this.player.getItemInHand(InteractionHand.MAIN_HAND), 36 + $$11.selected);
        } else if ($$12 != -1) {
            if (Inventory.isHotbarSlot($$12)) {
                $$11.selected = $$12;
            } else {
                this.gameMode.handlePickItem($$12);
            }
        }
    }

    private void addCustomNbtData(ItemStack $$0, BlockEntity $$1) {
        CompoundTag $$2 = $$1.saveWithFullMetadata();
        BlockItem.setBlockEntityData($$0, $$1.getType(), $$2);
        if ($$0.getItem() instanceof PlayerHeadItem && $$2.contains("SkullOwner")) {
            CompoundTag $$3 = $$2.getCompound("SkullOwner");
            CompoundTag $$4 = $$0.getOrCreateTag();
            $$4.put("SkullOwner", $$3);
            CompoundTag $$5 = $$4.getCompound("BlockEntityTag");
            $$5.remove("SkullOwner");
            $$5.remove("x");
            $$5.remove("y");
            $$5.remove("z");
            return;
        }
        CompoundTag $$6 = new CompoundTag();
        ListTag $$7 = new ListTag();
        $$7.add(StringTag.valueOf("\"(+NBT)\""));
        $$6.put("Lore", $$7);
        $$0.addTagElement("display", $$6);
    }

    public CrashReport fillReport(CrashReport $$0) {
        SystemReport $$1 = $$0.getSystemReport();
        Minecraft.fillSystemReport($$1, this, this.languageManager, this.launchedVersion, this.options);
        if (this.level != null) {
            this.level.fillReportDetails($$0);
        }
        if (this.singleplayerServer != null) {
            this.singleplayerServer.fillSystemReport($$1);
        }
        this.reloadStateTracker.fillCrashReport($$0);
        return $$0;
    }

    public static void fillReport(@Nullable Minecraft $$0, @Nullable LanguageManager $$1, String $$2, @Nullable Options $$3, CrashReport $$4) {
        SystemReport $$5 = $$4.getSystemReport();
        Minecraft.fillSystemReport($$5, $$0, $$1, $$2, $$3);
    }

    private static SystemReport fillSystemReport(SystemReport $$0, @Nullable Minecraft $$1, @Nullable LanguageManager $$2, String $$3, Options $$4) {
        $$0.setDetail("Launched Version", (Supplier<String>)((Supplier)() -> $$3));
        $$0.setDetail("Backend library", (Supplier<String>)((Supplier)RenderSystem::getBackendDescription));
        $$0.setDetail("Backend API", (Supplier<String>)((Supplier)RenderSystem::getApiDescription));
        $$0.setDetail("Window size", (Supplier<String>)((Supplier)() -> $$1 != null ? $$0.window.getWidth() + "x" + $$0.window.getHeight() : "<not initialized>"));
        $$0.setDetail("GL Caps", (Supplier<String>)((Supplier)RenderSystem::getCapsString));
        $$0.setDetail("GL debug messages", (Supplier<String>)((Supplier)() -> GlDebug.isDebugEnabled() ? String.join((CharSequence)"\n", GlDebug.getLastOpenGlDebugMessages()) : "<disabled>"));
        $$0.setDetail("Using VBOs", (Supplier<String>)((Supplier)() -> "Yes"));
        $$0.setDetail("Is Modded", (Supplier<String>)((Supplier)() -> Minecraft.checkModStatus().fullDescription()));
        $$0.setDetail("Type", "Client (map_client.txt)");
        if ($$4 != null) {
            String $$5;
            if (instance != null && ($$5 = instance.getGpuWarnlistManager().getAllWarnings()) != null) {
                $$0.setDetail("GPU Warnings", $$5);
            }
            $$0.setDetail("Graphics mode", $$4.graphicsMode().get().toString());
            $$0.setDetail("Resource Packs", (Supplier<String>)((Supplier)() -> {
                StringBuilder $$1 = new StringBuilder();
                for (String $$2 : $$0.resourcePacks) {
                    if ($$1.length() > 0) {
                        $$1.append(", ");
                    }
                    $$1.append($$2);
                    if (!$$0.incompatibleResourcePacks.contains((Object)$$2)) continue;
                    $$1.append(" (incompatible)");
                }
                return $$1.toString();
            }));
        }
        if ($$2 != null) {
            $$0.setDetail("Current Language", (Supplier<String>)((Supplier)() -> $$2.getSelected().toString()));
        }
        $$0.setDetail("CPU", (Supplier<String>)((Supplier)GlUtil::getCpuInfo));
        return $$0;
    }

    public static Minecraft getInstance() {
        return instance;
    }

    public CompletableFuture<Void> delayTextureReload() {
        return this.submit(this::reloadResourcePacks).thenCompose($$0 -> $$0);
    }

    public void updateReportEnvironment(ReportEnvironment $$0) {
        if (!this.reportingContext.matches($$0)) {
            this.reportingContext = ReportingContext.create($$0, this.userApiService);
        }
    }

    @Nullable
    public ServerData getCurrentServer() {
        return (ServerData)Util.mapNullable(this.getConnection(), ClientPacketListener::getServerData);
    }

    public boolean isLocalServer() {
        return this.isLocalServer;
    }

    public boolean hasSingleplayerServer() {
        return this.isLocalServer && this.singleplayerServer != null;
    }

    @Nullable
    public IntegratedServer getSingleplayerServer() {
        return this.singleplayerServer;
    }

    public boolean isSingleplayer() {
        IntegratedServer $$0 = this.getSingleplayerServer();
        return $$0 != null && !$$0.isPublished();
    }

    public User getUser() {
        return this.user;
    }

    public PropertyMap getProfileProperties() {
        if (this.profileProperties.isEmpty()) {
            GameProfile $$0 = this.getMinecraftSessionService().fillProfileProperties(this.user.getGameProfile(), false);
            this.profileProperties.putAll((Multimap)$$0.getProperties());
        }
        return this.profileProperties;
    }

    public Proxy getProxy() {
        return this.proxy;
    }

    public TextureManager getTextureManager() {
        return this.textureManager;
    }

    public ResourceManager getResourceManager() {
        return this.resourceManager;
    }

    public PackRepository getResourcePackRepository() {
        return this.resourcePackRepository;
    }

    public VanillaPackResources getVanillaPackResources() {
        return this.vanillaPackResources;
    }

    public DownloadedPackSource getDownloadedPackSource() {
        return this.downloadedPackSource;
    }

    public Path getResourcePackDirectory() {
        return this.resourcePackDirectory;
    }

    public LanguageManager getLanguageManager() {
        return this.languageManager;
    }

    public Function<ResourceLocation, TextureAtlasSprite> getTextureAtlas(ResourceLocation $$0) {
        return this.modelManager.getAtlas($$0)::getSprite;
    }

    public boolean is64Bit() {
        return this.is64bit;
    }

    public boolean isPaused() {
        return this.pause;
    }

    public GpuWarnlistManager getGpuWarnlistManager() {
        return this.gpuWarnlistManager;
    }

    public SoundManager getSoundManager() {
        return this.soundManager;
    }

    public Music getSituationalMusic() {
        if (this.screen instanceof WinScreen) {
            return Musics.CREDITS;
        }
        if (this.player != null) {
            if (this.player.level.dimension() == Level.END) {
                if (this.gui.getBossOverlay().shouldPlayMusic()) {
                    return Musics.END_BOSS;
                }
                return Musics.END;
            }
            Holder $$0 = this.player.level.getBiome(this.player.blockPosition());
            if (this.musicManager.isPlayingMusic(Musics.UNDER_WATER) || this.player.isUnderWater() && $$0.is(BiomeTags.PLAYS_UNDERWATER_MUSIC)) {
                return Musics.UNDER_WATER;
            }
            if (this.player.level.dimension() != Level.NETHER && this.player.getAbilities().instabuild && this.player.getAbilities().mayfly) {
                return Musics.CREATIVE;
            }
            return (Music)((Biome)$$0.value()).getBackgroundMusic().orElse((Object)Musics.GAME);
        }
        return Musics.MENU;
    }

    public MinecraftSessionService getMinecraftSessionService() {
        return this.minecraftSessionService;
    }

    public SkinManager getSkinManager() {
        return this.skinManager;
    }

    @Nullable
    public Entity getCameraEntity() {
        return this.cameraEntity;
    }

    public void setCameraEntity(Entity $$0) {
        this.cameraEntity = $$0;
        this.gameRenderer.checkEntityPostEffect($$0);
    }

    public boolean shouldEntityAppearGlowing(Entity $$0) {
        return $$0.isCurrentlyGlowing() || this.player != null && this.player.isSpectator() && this.options.keySpectatorOutlines.isDown() && $$0.getType() == EntityType.PLAYER;
    }

    @Override
    protected Thread getRunningThread() {
        return this.gameThread;
    }

    @Override
    protected Runnable wrapRunnable(Runnable $$0) {
        return $$0;
    }

    @Override
    protected boolean shouldRun(Runnable $$0) {
        return true;
    }

    public BlockRenderDispatcher getBlockRenderer() {
        return this.blockRenderer;
    }

    public EntityRenderDispatcher getEntityRenderDispatcher() {
        return this.entityRenderDispatcher;
    }

    public BlockEntityRenderDispatcher getBlockEntityRenderDispatcher() {
        return this.blockEntityRenderDispatcher;
    }

    public ItemRenderer getItemRenderer() {
        return this.itemRenderer;
    }

    public <T> SearchTree<T> getSearchTree(SearchRegistry.Key<T> $$0) {
        return this.searchRegistry.getTree($$0);
    }

    public <T> void populateSearchTree(SearchRegistry.Key<T> $$0, List<T> $$1) {
        this.searchRegistry.populate($$0, $$1);
    }

    public FrameTimer getFrameTimer() {
        return this.frameTimer;
    }

    public boolean isConnectedToRealms() {
        return this.connectedToRealms;
    }

    public void setConnectedToRealms(boolean $$0) {
        this.connectedToRealms = $$0;
    }

    public DataFixer getFixerUpper() {
        return this.fixerUpper;
    }

    public float getFrameTime() {
        return this.timer.partialTick;
    }

    public float getDeltaFrameTime() {
        return this.timer.tickDelta;
    }

    public BlockColors getBlockColors() {
        return this.blockColors;
    }

    public boolean showOnlyReducedInfo() {
        return this.player != null && this.player.isReducedDebugInfo() || this.options.reducedDebugInfo().get() != false;
    }

    public ToastComponent getToasts() {
        return this.toast;
    }

    public Tutorial getTutorial() {
        return this.tutorial;
    }

    public boolean isWindowActive() {
        return this.windowActive;
    }

    public HotbarManager getHotbarManager() {
        return this.hotbarManager;
    }

    public ModelManager getModelManager() {
        return this.modelManager;
    }

    public PaintingTextureManager getPaintingTextures() {
        return this.paintingTextures;
    }

    public MobEffectTextureManager getMobEffectTextures() {
        return this.mobEffectTextures;
    }

    @Override
    public void setWindowActive(boolean $$0) {
        this.windowActive = $$0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Component grabPanoramixScreenshot(File $$02, int $$12, int $$2) {
        int $$3 = this.window.getWidth();
        int $$4 = this.window.getHeight();
        TextureTarget $$5 = new TextureTarget($$12, $$2, true, ON_OSX);
        float $$6 = this.player.getXRot();
        float $$7 = this.player.getYRot();
        float $$8 = this.player.xRotO;
        float $$9 = this.player.yRotO;
        this.gameRenderer.setRenderBlockOutline(false);
        try {
            this.gameRenderer.setPanoramicMode(true);
            this.levelRenderer.graphicsChanged();
            this.window.setWidth($$12);
            this.window.setHeight($$2);
            for (int $$10 = 0; $$10 < 6; ++$$10) {
                switch ($$10) {
                    case 0: {
                        this.player.setYRot($$7);
                        this.player.setXRot(0.0f);
                        break;
                    }
                    case 1: {
                        this.player.setYRot(($$7 + 90.0f) % 360.0f);
                        this.player.setXRot(0.0f);
                        break;
                    }
                    case 2: {
                        this.player.setYRot(($$7 + 180.0f) % 360.0f);
                        this.player.setXRot(0.0f);
                        break;
                    }
                    case 3: {
                        this.player.setYRot(($$7 - 90.0f) % 360.0f);
                        this.player.setXRot(0.0f);
                        break;
                    }
                    case 4: {
                        this.player.setYRot($$7);
                        this.player.setXRot(-90.0f);
                        break;
                    }
                    default: {
                        this.player.setYRot($$7);
                        this.player.setXRot(90.0f);
                    }
                }
                this.player.yRotO = this.player.getYRot();
                this.player.xRotO = this.player.getXRot();
                $$5.bindWrite(true);
                this.gameRenderer.renderLevel(1.0f, 0L, new PoseStack());
                try {
                    Thread.sleep((long)10L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
                Screenshot.grab($$02, "panorama_" + $$10 + ".png", $$5, (Consumer<Component>)((Consumer)$$0 -> {}));
            }
            MutableComponent $$11 = Component.literal($$02.getName()).withStyle(ChatFormatting.UNDERLINE).withStyle((UnaryOperator<Style>)((UnaryOperator)$$1 -> $$1.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, $$02.getAbsolutePath()))));
            MutableComponent mutableComponent = Component.translatable("screenshot.success", $$11);
            return mutableComponent;
        }
        catch (Exception $$122) {
            LOGGER.error("Couldn't save image", (Throwable)$$122);
            MutableComponent mutableComponent = Component.translatable("screenshot.failure", $$122.getMessage());
            return mutableComponent;
        }
        finally {
            this.player.setXRot($$6);
            this.player.setYRot($$7);
            this.player.xRotO = $$8;
            this.player.yRotO = $$9;
            this.gameRenderer.setRenderBlockOutline(true);
            this.window.setWidth($$3);
            this.window.setHeight($$4);
            $$5.destroyBuffers();
            this.gameRenderer.setPanoramicMode(false);
            this.levelRenderer.graphicsChanged();
            this.getMainRenderTarget().bindWrite(true);
        }
    }

    private Component grabHugeScreenshot(File $$0, int $$12, int $$2, int $$3, int $$4) {
        try {
            ByteBuffer $$5 = GlUtil.allocateMemory($$12 * $$2 * 3);
            Screenshot $$6 = new Screenshot($$0, $$3, $$4, $$2);
            float $$7 = (float)$$3 / (float)$$12;
            float $$8 = (float)$$4 / (float)$$2;
            float $$9 = $$7 > $$8 ? $$7 : $$8;
            for (int $$10 = ($$4 - 1) / $$2 * $$2; $$10 >= 0; $$10 -= $$2) {
                for (int $$11 = 0; $$11 < $$3; $$11 += $$12) {
                    RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
                    float $$122 = (float)($$3 - $$12) / 2.0f * 2.0f - (float)($$11 * 2);
                    float $$13 = (float)($$4 - $$2) / 2.0f * 2.0f - (float)($$10 * 2);
                    this.gameRenderer.renderZoomed($$9, $$122 /= (float)$$12, $$13 /= (float)$$2);
                    $$5.clear();
                    RenderSystem.pixelStore(3333, 1);
                    RenderSystem.pixelStore(3317, 1);
                    RenderSystem.readPixels(0, 0, $$12, $$2, 32992, 5121, $$5);
                    $$6.addRegion($$5, $$11, $$10, $$12, $$2);
                }
                $$6.saveRow();
            }
            File $$14 = $$6.close();
            GlUtil.freeMemory((Buffer)$$5);
            MutableComponent $$15 = Component.literal($$14.getName()).withStyle(ChatFormatting.UNDERLINE).withStyle((UnaryOperator<Style>)((UnaryOperator)$$1 -> $$1.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, $$14.getAbsolutePath()))));
            return Component.translatable("screenshot.success", $$15);
        }
        catch (Exception $$16) {
            LOGGER.warn("Couldn't save screenshot", (Throwable)$$16);
            return Component.translatable("screenshot.failure", $$16.getMessage());
        }
    }

    public ProfilerFiller getProfiler() {
        return this.profiler;
    }

    public Game getGame() {
        return this.game;
    }

    @Nullable
    public StoringChunkProgressListener getProgressListener() {
        return (StoringChunkProgressListener)this.progressListener.get();
    }

    public SplashManager getSplashManager() {
        return this.splashManager;
    }

    @Nullable
    public Overlay getOverlay() {
        return this.overlay;
    }

    public PlayerSocialManager getPlayerSocialManager() {
        return this.playerSocialManager;
    }

    public boolean renderOnThread() {
        return false;
    }

    public Window getWindow() {
        return this.window;
    }

    public RenderBuffers renderBuffers() {
        return this.renderBuffers;
    }

    public void updateMaxMipLevel(int $$0) {
        this.modelManager.updateMaxMipLevel($$0);
    }

    public EntityModelSet getEntityModels() {
        return this.entityModels;
    }

    public boolean isTextFilteringEnabled() {
        return this.userApiService.properties().flag(UserApiService.UserFlag.PROFANITY_FILTER_ENABLED);
    }

    public void prepareForMultiplayer() {
        this.playerSocialManager.startOnlineMode();
        this.getProfileKeyPairManager().prepareKeyPair();
    }

    public Realms32BitWarningStatus getRealms32BitWarningStatus() {
        return this.realms32BitWarningStatus;
    }

    public SignatureValidator getServiceSignatureValidator() {
        return this.serviceSignatureValidator;
    }

    public GameNarrator getNarrator() {
        return this.narrator;
    }

    public ChatListener getChatListener() {
        return this.chatListener;
    }

    public ReportingContext getReportingContext() {
        return this.reportingContext;
    }

    public RealmsDataFetcher realmsDataFetcher() {
        return this.realmsDataFetcher;
    }

    static {
        LOGGER = LogUtils.getLogger();
        ON_OSX = Util.getPlatform() == Util.OS.OSX;
        DEFAULT_FONT = new ResourceLocation("default");
        UNIFORM_FONT = new ResourceLocation("uniform");
        ALT_FONT = new ResourceLocation("alt");
        REGIONAL_COMPLIANCIES = new ResourceLocation("regional_compliancies.json");
        RESOURCE_RELOAD_INITIAL_TASK = CompletableFuture.completedFuture((Object)((Object)Unit.INSTANCE));
        SOCIAL_INTERACTIONS_NOT_AVAILABLE = Component.translatable("multiplayer.socialInteractions.not_available");
    }

    /*
     * Uses 'sealed' constructs - enablewith --sealed true
     */
    public static enum ChatStatus {
        ENABLED(CommonComponents.EMPTY){

            @Override
            public boolean isChatAllowed(boolean $$0) {
                return true;
            }
        }
        ,
        DISABLED_BY_OPTIONS(Component.translatable("chat.disabled.options").withStyle(ChatFormatting.RED)){

            @Override
            public boolean isChatAllowed(boolean $$0) {
                return false;
            }
        }
        ,
        DISABLED_BY_LAUNCHER(Component.translatable("chat.disabled.launcher").withStyle(ChatFormatting.RED)){

            @Override
            public boolean isChatAllowed(boolean $$0) {
                return $$0;
            }
        }
        ,
        DISABLED_BY_PROFILE(Component.translatable("chat.disabled.profile", Component.keybind(Minecraft.instance.options.keyChat.getName())).withStyle(ChatFormatting.RED)){

            @Override
            public boolean isChatAllowed(boolean $$0) {
                return $$0;
            }
        };

        static final Component INFO_DISABLED_BY_PROFILE;
        private static final String URL_DISABLED_BY_PROFILE = "https://aka.ms/JavaAccountSettings";
        private final Component message;

        ChatStatus(Component $$0) {
            this.message = $$0;
        }

        public Component getMessage() {
            return this.message;
        }

        public abstract boolean isChatAllowed(boolean var1);

        static {
            INFO_DISABLED_BY_PROFILE = Component.translatable("chat.disabled.profile.moreInfo");
        }
    }
}