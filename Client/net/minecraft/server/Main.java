/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
 *  com.mojang.datafixers.DataFixer
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Lifecycle
 *  java.awt.GraphicsEnvironment
 *  java.io.File
 *  java.io.OutputStream
 *  java.lang.Exception
 *  java.lang.Integer
 *  java.lang.InterruptedException
 *  java.lang.Object
 *  java.lang.Runtime
 *  java.lang.String
 *  java.lang.System
 *  java.lang.Thread
 *  java.lang.Thread$UncaughtExceptionHandler
 *  java.lang.Throwable
 *  java.net.Proxy
 *  java.nio.file.Path
 *  java.nio.file.Paths
 *  java.util.Optional
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.function.BooleanSupplier
 *  joptsimple.AbstractOptionSpec
 *  joptsimple.ArgumentAcceptingOptionSpec
 *  joptsimple.NonOptionArgumentSpec
 *  joptsimple.OptionParser
 *  joptsimple.OptionSet
 *  joptsimple.OptionSpec
 *  joptsimple.OptionSpecBuilder
 *  org.slf4j.Logger
 */
package net.minecraft.server;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.OutputStream;
import java.net.Proxy;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import joptsimple.AbstractOptionSpec;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;
import net.minecraft.CrashReport;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.obfuscate.DontObfuscate;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.Eula;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldStem;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.dedicated.DedicatedServerSettings;
import net.minecraft.server.level.progress.LoggerChunkProgressListener;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.profiling.jfr.Environment;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.worldupdate.WorldUpgrader;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import org.slf4j.Logger;

public class Main {
    private static final Logger LOGGER = LogUtils.getLogger();

    /*
     * WARNING - void declaration
     */
    @DontObfuscate
    public static void main(String[] $$0) {
        SharedConstants.tryDetectVersion();
        OptionParser $$1 = new OptionParser();
        OptionSpecBuilder $$2 = $$1.accepts("nogui");
        OptionSpecBuilder $$3 = $$1.accepts("initSettings", "Initializes 'server.properties' and 'eula.txt', then quits");
        OptionSpecBuilder $$4 = $$1.accepts("demo");
        OptionSpecBuilder $$5 = $$1.accepts("bonusChest");
        OptionSpecBuilder $$6 = $$1.accepts("forceUpgrade");
        OptionSpecBuilder $$7 = $$1.accepts("eraseCache");
        OptionSpecBuilder $$8 = $$1.accepts("safeMode", "Loads level with vanilla datapack only");
        AbstractOptionSpec $$9 = $$1.accepts("help").forHelp();
        ArgumentAcceptingOptionSpec $$10 = $$1.accepts("singleplayer").withRequiredArg();
        ArgumentAcceptingOptionSpec $$11 = $$1.accepts("universe").withRequiredArg().defaultsTo((Object)".", (Object[])new String[0]);
        ArgumentAcceptingOptionSpec $$12 = $$1.accepts("world").withRequiredArg();
        ArgumentAcceptingOptionSpec $$13 = $$1.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo((Object)-1, (Object[])new Integer[0]);
        ArgumentAcceptingOptionSpec $$14 = $$1.accepts("serverId").withRequiredArg();
        OptionSpecBuilder $$15 = $$1.accepts("jfrProfile");
        NonOptionArgumentSpec $$16 = $$1.nonOptions();
        try {
            void $$33;
            boolean $$28;
            OptionSet $$17 = $$1.parse($$0);
            if ($$17.has((OptionSpec)$$9)) {
                $$1.printHelpOn((OutputStream)System.err);
                return;
            }
            CrashReport.preload();
            if ($$17.has((OptionSpec)$$15)) {
                JvmProfiler.INSTANCE.start(Environment.SERVER);
            }
            Bootstrap.bootStrap();
            Bootstrap.validate();
            Util.startTimerHackThread();
            Path $$18 = Paths.get((String)"server.properties", (String[])new String[0]);
            DedicatedServerSettings $$19 = new DedicatedServerSettings($$18);
            $$19.forceSave();
            Path $$20 = Paths.get((String)"eula.txt", (String[])new String[0]);
            Eula $$21 = new Eula($$20);
            if ($$17.has((OptionSpec)$$3)) {
                LOGGER.info("Initialized '{}' and '{}'", (Object)$$18.toAbsolutePath(), (Object)$$20.toAbsolutePath());
                return;
            }
            if (!$$21.hasAgreedToEULA()) {
                LOGGER.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
                return;
            }
            File $$22 = new File((String)$$17.valueOf((OptionSpec)$$11));
            Services $$23 = Services.create(new YggdrasilAuthenticationService(Proxy.NO_PROXY), $$22);
            String $$24 = (String)Optional.ofNullable((Object)((String)$$17.valueOf((OptionSpec)$$12))).orElse((Object)$$19.getProperties().levelName);
            LevelStorageSource $$25 = LevelStorageSource.createDefault($$22.toPath());
            LevelStorageSource.LevelStorageAccess $$26 = $$25.createAccess($$24);
            LevelSummary $$27 = $$26.getSummary();
            if ($$27 != null) {
                if ($$27.requiresManualConversion()) {
                    LOGGER.info("This world must be opened in an older version (like 1.6.4) to be safely converted");
                    return;
                }
                if (!$$27.isCompatible()) {
                    LOGGER.info("This world was created by an incompatible version.");
                    return;
                }
            }
            if ($$28 = $$17.has((OptionSpec)$$8)) {
                LOGGER.warn("Safe mode active, only vanilla datapack will be loaded");
            }
            PackRepository $$29 = ServerPacksSource.createPackRepository($$26.getLevelPath(LevelResource.DATAPACK_DIR));
            try {
                WorldLoader.InitConfig $$30 = Main.loadOrCreateConfig($$19.getProperties(), $$26, $$28, $$29);
                WorldStem $$31 = (WorldStem)((Object)Util.blockUntilDone(arg_0 -> Main.lambda$main$1($$30, $$26, $$17, (OptionSpec)$$4, $$19, (OptionSpec)$$5, arg_0)).get());
            }
            catch (Exception $$32) {
                LOGGER.warn("Failed to load datapacks, can't proceed with server load. You can either fix your datapacks or reset to vanilla with --safeMode", (Throwable)$$32);
                return;
            }
            RegistryAccess.Frozen $$34 = $$33.registries().compositeAccess();
            if ($$17.has((OptionSpec)$$6)) {
                Main.forceUpgrade($$26, DataFixers.getDataFixer(), $$17.has((OptionSpec)$$7), () -> true, $$34.registryOrThrow(Registries.LEVEL_STEM));
            }
            WorldData $$35 = $$33.worldData();
            $$26.saveDataTag($$34, $$35);
            final DedicatedServer $$36 = (DedicatedServer)MinecraftServer.spin(arg_0 -> Main.lambda$main$3($$26, $$29, (WorldStem)$$33, $$19, $$23, $$17, (OptionSpec)$$10, (OptionSpec)$$13, (OptionSpec)$$4, (OptionSpec)$$14, (OptionSpec)$$2, (OptionSpec)$$16, arg_0));
            Thread $$37 = new Thread("Server Shutdown Thread"){

                public void run() {
                    $$36.halt(true);
                }
            };
            $$37.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new DefaultUncaughtExceptionHandler(LOGGER));
            Runtime.getRuntime().addShutdownHook($$37);
        }
        catch (Exception $$38) {
            LOGGER.error(LogUtils.FATAL_MARKER, "Failed to start the minecraft server", (Throwable)$$38);
        }
    }

    private static WorldLoader.InitConfig loadOrCreateConfig(DedicatedServerProperties $$0, LevelStorageSource.LevelStorageAccess $$1, boolean $$2, PackRepository $$3) {
        WorldDataConfiguration $$8;
        boolean $$7;
        WorldDataConfiguration $$4 = $$1.getDataConfiguration();
        if ($$4 != null) {
            boolean $$5 = false;
            WorldDataConfiguration $$6 = $$4;
        } else {
            $$7 = true;
            $$8 = new WorldDataConfiguration($$0.initialDataPackConfiguration, FeatureFlags.DEFAULT_FLAGS);
        }
        WorldLoader.PackConfig $$9 = new WorldLoader.PackConfig($$3, $$8, $$2, $$7);
        return new WorldLoader.InitConfig($$9, Commands.CommandSelection.DEDICATED, $$0.functionPermissionLevel);
    }

    private static void forceUpgrade(LevelStorageSource.LevelStorageAccess $$0, DataFixer $$1, boolean $$2, BooleanSupplier $$3, Registry<LevelStem> $$4) {
        LOGGER.info("Forcing world upgrade!");
        WorldUpgrader $$5 = new WorldUpgrader($$0, $$1, $$4, $$2);
        Component $$6 = null;
        while (!$$5.isFinished()) {
            int $$8;
            Component $$7 = $$5.getStatus();
            if ($$6 != $$7) {
                $$6 = $$7;
                LOGGER.info($$5.getStatus().getString());
            }
            if (($$8 = $$5.getTotalChunks()) > 0) {
                int $$9 = $$5.getConverted() + $$5.getSkipped();
                LOGGER.info("{}% completed ({} / {} chunks)...", new Object[]{Mth.floor((float)$$9 / (float)$$8 * 100.0f), $$9, $$8});
            }
            if (!$$3.getAsBoolean()) {
                $$5.cancel();
                continue;
            }
            try {
                Thread.sleep((long)1000L);
            }
            catch (InterruptedException interruptedException) {}
        }
    }

    private static /* synthetic */ DedicatedServer lambda$main$3(LevelStorageSource.LevelStorageAccess $$0, PackRepository $$1, WorldStem $$2, DedicatedServerSettings $$3, Services $$4, OptionSet $$5, OptionSpec $$6, OptionSpec $$7, OptionSpec $$8, OptionSpec $$9, OptionSpec $$10, OptionSpec $$11, Thread $$12) {
        boolean $$14;
        DedicatedServer $$13 = new DedicatedServer($$12, $$0, $$1, $$2, $$3, DataFixers.getDataFixer(), $$4, LoggerChunkProgressListener::new);
        $$13.setSingleplayerProfile($$5.has($$6) ? new GameProfile(null, (String)$$5.valueOf($$6)) : null);
        $$13.setPort((Integer)$$5.valueOf($$7));
        $$13.setDemo($$5.has($$8));
        $$13.setId((String)$$5.valueOf($$9));
        boolean bl = $$14 = !$$5.has($$10) && !$$5.valuesOf($$11).contains((Object)"nogui");
        if ($$14 && !GraphicsEnvironment.isHeadless()) {
            $$13.showGui();
        }
        return $$13;
    }

    private static /* synthetic */ CompletableFuture lambda$main$1(WorldLoader.InitConfig $$0, LevelStorageSource.LevelStorageAccess $$1, OptionSet $$2, OptionSpec $$3, DedicatedServerSettings $$4, OptionSpec $$52, Executor $$6) {
        return WorldLoader.load($$0, $$5 -> {
            WorldDimensions $$15;
            WorldOptions $$14;
            LevelSettings $$13;
            Registry $$6 = $$5.datapackDimensions().registryOrThrow(Registries.LEVEL_STEM);
            RegistryOps<Tag> $$7 = RegistryOps.create(NbtOps.INSTANCE, $$5.datapackWorldgen());
            Pair<WorldData, WorldDimensions.Complete> $$8 = $$1.getDataTag($$7, $$5.dataConfiguration(), $$6, $$5.datapackWorldgen().allRegistriesLifecycle());
            if ($$8 != null) {
                return new WorldLoader.DataLoadOutput<WorldData>((WorldData)$$8.getFirst(), ((WorldDimensions.Complete)((Object)((Object)$$8.getSecond()))).dimensionsRegistryAccess());
            }
            if ($$2.has($$3)) {
                LevelSettings $$9 = MinecraftServer.DEMO_SETTINGS;
                WorldOptions $$10 = WorldOptions.DEMO_OPTIONS;
                WorldDimensions $$11 = WorldPresets.createNormalWorldDimensions($$5.datapackWorldgen());
            } else {
                DedicatedServerProperties $$12 = $$4.getProperties();
                $$13 = new LevelSettings($$12.levelName, $$12.gamemode, $$12.hardcore, $$12.difficulty, false, new GameRules(), $$5.dataConfiguration());
                $$14 = $$2.has($$52) ? $$12.worldOptions.withBonusChest(true) : $$12.worldOptions;
                $$15 = $$12.createDimensions($$5.datapackWorldgen());
            }
            WorldDimensions.Complete $$16 = $$15.bake($$6);
            Lifecycle $$17 = $$16.lifecycle().add($$5.datapackWorldgen().allRegistriesLifecycle());
            return new WorldLoader.DataLoadOutput<PrimaryLevelData>(new PrimaryLevelData($$13, $$14, $$16.specialWorldProperty(), $$17), $$16.dimensionsRegistryAccess());
        }, WorldStem::new, (Executor)Util.backgroundExecutor(), $$6);
    }
}