/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Lifecycle
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  java.io.IOException
 *  java.lang.Boolean
 *  java.lang.Exception
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.function.Function
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screens.worldselection;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.DatapackLoadFailureScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.EditWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldCreationContext;
import net.minecraft.commands.Commands;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.WorldStem;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.storage.WorldData;
import org.slf4j.Logger;

public class WorldOpenFlows {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Minecraft minecraft;
    private final LevelStorageSource levelSource;

    public WorldOpenFlows(Minecraft $$0, LevelStorageSource $$1) {
        this.minecraft = $$0;
        this.levelSource = $$1;
    }

    public void loadLevel(Screen $$0, String $$1) {
        this.doLoadLevel($$0, $$1, false, true);
    }

    public void createFreshLevel(String $$0, LevelSettings $$1, WorldOptions $$2, Function<RegistryAccess, WorldDimensions> $$32) {
        LevelStorageSource.LevelStorageAccess $$4 = this.createWorldAccess($$0);
        if ($$4 == null) {
            return;
        }
        PackRepository $$5 = ServerPacksSource.createPackRepository($$4);
        WorldDataConfiguration $$6 = $$1.getDataConfiguration();
        try {
            WorldLoader.PackConfig $$7 = new WorldLoader.PackConfig($$5, $$6, false, false);
            WorldStem $$8 = this.loadWorldDataBlocking($$7, $$3 -> {
                WorldDimensions.Complete $$4 = ((WorldDimensions)((Object)((Object)$$32.apply((Object)$$3.datapackWorldgen())))).bake($$3.datapackDimensions().registryOrThrow(Registries.LEVEL_STEM));
                return new WorldLoader.DataLoadOutput<PrimaryLevelData>(new PrimaryLevelData($$1, $$2, $$4.specialWorldProperty(), $$4.lifecycle()), $$4.dimensionsRegistryAccess());
            }, WorldStem::new);
            this.minecraft.doWorldLoad($$0, $$4, $$5, $$8, true);
        }
        catch (Exception $$9) {
            LOGGER.warn("Failed to load datapacks, can't proceed with server load", (Throwable)$$9);
            WorldOpenFlows.safeCloseAccess($$4, $$0);
        }
    }

    @Nullable
    private LevelStorageSource.LevelStorageAccess createWorldAccess(String $$0) {
        try {
            return this.levelSource.createAccess($$0);
        }
        catch (IOException $$1) {
            LOGGER.warn("Failed to read level {} data", (Object)$$0, (Object)$$1);
            SystemToast.onWorldAccessFailure(this.minecraft, $$0);
            this.minecraft.setScreen(null);
            return null;
        }
    }

    public void createLevelFromExistingSettings(LevelStorageSource.LevelStorageAccess $$0, ReloadableServerResources $$1, LayeredRegistryAccess<RegistryLayer> $$2, WorldData $$3) {
        PackRepository $$4 = ServerPacksSource.createPackRepository($$0);
        CloseableResourceManager $$5 = (CloseableResourceManager)new WorldLoader.PackConfig($$4, $$3.getDataConfiguration(), false, false).createResourceManager().getSecond();
        this.minecraft.doWorldLoad($$0.getLevelId(), $$0, $$4, new WorldStem($$5, $$1, $$2, $$3), true);
    }

    private WorldStem loadWorldStem(LevelStorageSource.LevelStorageAccess $$0, boolean $$12, PackRepository $$2) throws Exception {
        WorldLoader.PackConfig $$3 = this.getPackConfigFromLevelData($$0, $$12, $$2);
        return this.loadWorldDataBlocking($$3, $$1 -> {
            RegistryOps<Tag> $$2 = RegistryOps.create(NbtOps.INSTANCE, $$1.datapackWorldgen());
            Registry $$3 = $$1.datapackDimensions().registryOrThrow(Registries.LEVEL_STEM);
            Pair<WorldData, WorldDimensions.Complete> $$4 = $$0.getDataTag($$2, $$1.dataConfiguration(), $$3, $$1.datapackWorldgen().allRegistriesLifecycle());
            if ($$4 == null) {
                throw new IllegalStateException("Failed to load world");
            }
            return new WorldLoader.DataLoadOutput<WorldData>((WorldData)$$4.getFirst(), ((WorldDimensions.Complete)((Object)((Object)$$4.getSecond()))).dimensionsRegistryAccess());
        }, WorldStem::new);
    }

    public Pair<LevelSettings, WorldCreationContext> recreateWorldData(LevelStorageSource.LevelStorageAccess $$02) throws Exception {
        record Data(LevelSettings levelSettings, WorldOptions options, Registry<LevelStem> existingDimensions) {
        }
        PackRepository $$12 = ServerPacksSource.createPackRepository($$02);
        WorldLoader.PackConfig $$22 = this.getPackConfigFromLevelData($$02, false, $$12);
        return this.loadWorldDataBlocking($$22, $$1 -> {
            RegistryOps<Tag> $$2 = RegistryOps.create(NbtOps.INSTANCE, $$1.datapackWorldgen());
            Registry<LevelStem> $$3 = new MappedRegistry<LevelStem>(Registries.LEVEL_STEM, Lifecycle.stable()).freeze();
            Pair<WorldData, WorldDimensions.Complete> $$4 = $$02.getDataTag($$2, $$1.dataConfiguration(), $$3, $$1.datapackWorldgen().allRegistriesLifecycle());
            if ($$4 == null) {
                throw new IllegalStateException("Failed to load world");
            }
            return new WorldLoader.DataLoadOutput<Data>(new Data(((WorldData)$$4.getFirst()).getLevelSettings(), ((WorldData)$$4.getFirst()).worldGenOptions(), ((WorldDimensions.Complete)((Object)((Object)$$4.getSecond()))).dimensions()), $$1.datapackDimensions());
        }, ($$0, $$1, $$2, $$3) -> {
            $$0.close();
            return Pair.of((Object)$$3.levelSettings, (Object)((Object)new WorldCreationContext($$3.options, new WorldDimensions($$3.existingDimensions), $$2, $$1, $$3.levelSettings.getDataConfiguration())));
        });
    }

    private WorldLoader.PackConfig getPackConfigFromLevelData(LevelStorageSource.LevelStorageAccess $$0, boolean $$1, PackRepository $$2) {
        WorldDataConfiguration $$3 = $$0.getDataConfiguration();
        if ($$3 == null) {
            throw new IllegalStateException("Failed to load data pack config");
        }
        return new WorldLoader.PackConfig($$2, $$3, $$1, false);
    }

    public WorldStem loadWorldStem(LevelStorageSource.LevelStorageAccess $$0, boolean $$1) throws Exception {
        PackRepository $$2 = ServerPacksSource.createPackRepository($$0);
        return this.loadWorldStem($$0, $$1, $$2);
    }

    private <D, R> R loadWorldDataBlocking(WorldLoader.PackConfig $$0, WorldLoader.WorldDataSupplier<D> $$1, WorldLoader.ResultFactory<D, R> $$2) throws Exception {
        WorldLoader.InitConfig $$3 = new WorldLoader.InitConfig($$0, Commands.CommandSelection.INTEGRATED, 2);
        CompletableFuture<R> $$4 = WorldLoader.load($$3, $$1, $$2, (Executor)Util.backgroundExecutor(), this.minecraft);
        this.minecraft.managedBlock(() -> $$4.isDone());
        return (R)$$4.get();
    }

    /*
     * WARNING - void declaration
     */
    private void doLoadLevel(Screen $$02, String $$1, boolean $$2, boolean $$3) {
        boolean $$11;
        void $$8;
        LevelStorageSource.LevelStorageAccess $$4 = this.createWorldAccess($$1);
        if ($$4 == null) {
            return;
        }
        PackRepository $$5 = ServerPacksSource.createPackRepository($$4);
        try {
            WorldStem $$6 = this.loadWorldStem($$4, $$2, $$5);
        }
        catch (Exception $$7) {
            LOGGER.warn("Failed to load level data or datapacks, can't proceed with server load", (Throwable)$$7);
            this.minecraft.setScreen(new DatapackLoadFailureScreen(() -> this.doLoadLevel($$02, $$1, true, $$3)));
            WorldOpenFlows.safeCloseAccess($$4, $$1);
            return;
        }
        WorldData $$9 = $$8.worldData();
        boolean $$10 = $$9.worldGenOptions().isOldCustomizedWorld();
        boolean bl = $$11 = $$9.worldGenSettingsLifecycle() != Lifecycle.stable();
        if ($$3 && ($$10 || $$11)) {
            this.askForBackup($$02, $$1, $$10, () -> this.doLoadLevel($$02, $$1, $$2, false));
            $$8.close();
            WorldOpenFlows.safeCloseAccess($$4, $$1);
            return;
        }
        this.minecraft.getDownloadedPackSource().loadBundledResourcePack($$4).thenApply($$0 -> true).exceptionallyComposeAsync($$0 -> {
            LOGGER.warn("Failed to load pack: ", $$0);
            return this.promptBundledPackLoadFailure();
        }, (Executor)this.minecraft).thenAcceptAsync(arg_0 -> this.lambda$doLoadLevel$9($$1, $$4, $$5, (WorldStem)$$8, $$02, arg_0), (Executor)this.minecraft).exceptionally($$0 -> {
            this.minecraft.delayCrash(CrashReport.forThrowable($$0, "Load world"));
            return null;
        });
    }

    private CompletableFuture<Boolean> promptBundledPackLoadFailure() {
        CompletableFuture $$0 = new CompletableFuture();
        this.minecraft.setScreen(new ConfirmScreen(arg_0 -> ((CompletableFuture)$$0).complete(arg_0), Component.translatable("multiplayer.texturePrompt.failure.line1"), Component.translatable("multiplayer.texturePrompt.failure.line2"), CommonComponents.GUI_PROCEED, CommonComponents.GUI_CANCEL));
        return $$0;
    }

    private static void safeCloseAccess(LevelStorageSource.LevelStorageAccess $$0, String $$1) {
        try {
            $$0.close();
        }
        catch (IOException $$2) {
            LOGGER.warn("Failed to unlock access to level {}", (Object)$$1, (Object)$$2);
        }
    }

    private void askForBackup(Screen $$0, String $$1, boolean $$22, Runnable $$32) {
        MutableComponent $$7;
        MutableComponent $$6;
        if ($$22) {
            MutableComponent $$4 = Component.translatable("selectWorld.backupQuestion.customized");
            MutableComponent $$5 = Component.translatable("selectWorld.backupWarning.customized");
        } else {
            $$6 = Component.translatable("selectWorld.backupQuestion.experimental");
            $$7 = Component.translatable("selectWorld.backupWarning.experimental");
        }
        this.minecraft.setScreen(new BackupConfirmScreen($$0, ($$2, $$3) -> {
            if ($$2) {
                EditWorldScreen.makeBackupAndShowToast(this.levelSource, $$1);
            }
            $$32.run();
        }, $$6, $$7, false));
    }

    public static void confirmWorldCreation(Minecraft $$0, CreateWorldScreen $$1, Lifecycle $$2, Runnable $$32) {
        BooleanConsumer $$4 = $$3 -> {
            if ($$3) {
                $$32.run();
            } else {
                $$0.setScreen($$1);
            }
        };
        if ($$2 == Lifecycle.stable()) {
            $$32.run();
        } else if ($$2 == Lifecycle.experimental()) {
            $$0.setScreen(new ConfirmScreen($$4, Component.translatable("selectWorld.warning.experimental.title"), Component.translatable("selectWorld.warning.experimental.question")));
        } else {
            $$0.setScreen(new ConfirmScreen($$4, Component.translatable("selectWorld.warning.deprecated.title"), Component.translatable("selectWorld.warning.deprecated.question")));
        }
    }

    private /* synthetic */ void lambda$doLoadLevel$9(String $$0, LevelStorageSource.LevelStorageAccess $$1, PackRepository $$2, WorldStem $$3, Screen $$4, Boolean $$5) {
        if ($$5.booleanValue()) {
            this.minecraft.doWorldLoad($$0, $$1, $$2, $$3, false);
        } else {
            $$3.close();
            WorldOpenFlows.safeCloseAccess($$1, $$0);
            this.minecraft.getDownloadedPackSource().clearServerPack().thenRunAsync(() -> this.minecraft.setScreen($$4), (Executor)this.minecraft);
        }
    }
}