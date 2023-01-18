/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  com.mojang.logging.LogUtils
 *  java.io.File
 *  java.io.IOException
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Thread
 *  java.util.ArrayList
 *  java.util.UUID
 *  java.util.concurrent.Executor
 *  java.util.function.BooleanSupplier
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.server;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.SharedConstants;
import net.minecraft.SystemReport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.server.IntegratedPlayerList;
import net.minecraft.client.server.LanServerPinger;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.Services;
import net.minecraft.server.WorldStem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.stats.Stats;
import net.minecraft.util.ModCheck;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.ProfileKeyPair;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.slf4j.Logger;

public class IntegratedServer
extends MinecraftServer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MIN_SIM_DISTANCE = 2;
    private final Minecraft minecraft;
    private boolean paused = true;
    private int publishedPort = -1;
    @Nullable
    private GameType publishedGameType;
    @Nullable
    private LanServerPinger lanPinger;
    @Nullable
    private UUID uuid;
    private int previousSimulationDistance = 0;

    public IntegratedServer(Thread $$0, Minecraft $$1, LevelStorageSource.LevelStorageAccess $$2, PackRepository $$3, WorldStem $$4, Services $$5, ChunkProgressListenerFactory $$6) {
        super($$0, $$2, $$3, $$4, $$1.getProxy(), $$1.getFixerUpper(), $$5, $$6);
        this.setSingleplayerProfile($$1.getUser().getGameProfile());
        this.setDemo($$1.isDemo());
        this.setPlayerList(new IntegratedPlayerList(this, this.registries(), this.playerDataStorage));
        this.minecraft = $$1;
    }

    @Override
    public boolean initServer() {
        LOGGER.info("Starting integrated minecraft server version {}", (Object)SharedConstants.getCurrentVersion().getName());
        this.setUsesAuthentication(true);
        this.setPvpAllowed(true);
        this.setFlightAllowed(true);
        this.initializeKeyPair();
        this.loadLevel();
        GameProfile $$0 = this.getSingleplayerProfile();
        String $$1 = this.getWorldData().getLevelName();
        this.setMotd((String)($$0 != null ? $$0.getName() + " - " + $$1 : $$1));
        return true;
    }

    @Override
    public void tickServer(BooleanSupplier $$0) {
        int $$5;
        boolean $$3;
        boolean $$1 = this.paused;
        this.paused = Minecraft.getInstance().isPaused();
        ProfilerFiller $$2 = this.getProfiler();
        if (!$$1 && this.paused) {
            $$2.push("autoSave");
            LOGGER.info("Saving and pausing game...");
            this.saveEverything(false, false, false);
            $$2.pop();
        }
        boolean bl = $$3 = Minecraft.getInstance().getConnection() != null;
        if ($$3 && this.paused) {
            this.tickPaused();
            return;
        }
        super.tickServer($$0);
        int $$4 = Math.max((int)2, (int)this.minecraft.options.renderDistance().get());
        if ($$4 != this.getPlayerList().getViewDistance()) {
            LOGGER.info("Changing view distance to {}, from {}", (Object)$$4, (Object)this.getPlayerList().getViewDistance());
            this.getPlayerList().setViewDistance($$4);
        }
        if (($$5 = Math.max((int)2, (int)this.minecraft.options.simulationDistance().get())) != this.previousSimulationDistance) {
            LOGGER.info("Changing simulation distance to {}, from {}", (Object)$$5, (Object)this.previousSimulationDistance);
            this.getPlayerList().setSimulationDistance($$5);
            this.previousSimulationDistance = $$5;
        }
    }

    private void tickPaused() {
        for (ServerPlayer $$0 : this.getPlayerList().getPlayers()) {
            $$0.awardStat(Stats.TOTAL_WORLD_TIME);
        }
    }

    @Override
    public boolean shouldRconBroadcast() {
        return true;
    }

    @Override
    public boolean shouldInformAdmins() {
        return true;
    }

    @Override
    public File getServerDirectory() {
        return this.minecraft.gameDirectory;
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
    public void onServerCrash(CrashReport $$0) {
        this.minecraft.delayCrashRaw($$0);
    }

    @Override
    public SystemReport fillServerSystemReport(SystemReport $$0) {
        $$0.setDetail("Type", "Integrated Server (map_client.txt)");
        $$0.setDetail("Is Modded", (Supplier<String>)((Supplier)() -> this.getModdedStatus().fullDescription()));
        $$0.setDetail("Launched Version", (Supplier<String>)((Supplier)this.minecraft::getLaunchedVersion));
        return $$0;
    }

    @Override
    public ModCheck getModdedStatus() {
        return Minecraft.checkModStatus().merge(super.getModdedStatus());
    }

    @Override
    public boolean publishServer(@Nullable GameType $$0, boolean $$1, int $$2) {
        try {
            this.minecraft.prepareForMultiplayer();
            this.minecraft.getProfileKeyPairManager().prepareKeyPair().thenAcceptAsync($$02 -> $$02.ifPresent($$0 -> {
                ClientPacketListener $$1 = this.minecraft.getConnection();
                if ($$1 != null) {
                    $$1.setKeyPair((ProfileKeyPair)((Object)((Object)$$0)));
                }
            }), (Executor)this.minecraft);
            this.getConnection().startTcpServerListener(null, $$2);
            LOGGER.info("Started serving on {}", (Object)$$2);
            this.publishedPort = $$2;
            this.lanPinger = new LanServerPinger(this.getMotd(), "" + $$2);
            this.lanPinger.start();
            this.publishedGameType = $$0;
            this.getPlayerList().setAllowCheatsForAllPlayers($$1);
            int $$3 = this.getProfilePermissions(this.minecraft.player.getGameProfile());
            this.minecraft.player.setPermissionLevel($$3);
            for (ServerPlayer $$4 : this.getPlayerList().getPlayers()) {
                this.getCommands().sendCommands($$4);
            }
            return true;
        }
        catch (IOException iOException) {
            return false;
        }
    }

    @Override
    public void stopServer() {
        super.stopServer();
        if (this.lanPinger != null) {
            this.lanPinger.interrupt();
            this.lanPinger = null;
        }
    }

    @Override
    public void halt(boolean $$0) {
        this.executeBlocking(() -> {
            ArrayList $$0 = Lists.newArrayList(this.getPlayerList().getPlayers());
            for (ServerPlayer $$1 : $$0) {
                if ($$1.getUUID().equals((Object)this.uuid)) continue;
                this.getPlayerList().remove($$1);
            }
        });
        super.halt($$0);
        if (this.lanPinger != null) {
            this.lanPinger.interrupt();
            this.lanPinger = null;
        }
    }

    @Override
    public boolean isPublished() {
        return this.publishedPort > -1;
    }

    @Override
    public int getPort() {
        return this.publishedPort;
    }

    @Override
    public void setDefaultGameType(GameType $$0) {
        super.setDefaultGameType($$0);
        this.publishedGameType = null;
    }

    @Override
    public boolean isCommandBlockEnabled() {
        return true;
    }

    @Override
    public int getOperatorUserPermissionLevel() {
        return 2;
    }

    @Override
    public int getFunctionCompilationLevel() {
        return 2;
    }

    public void setUUID(UUID $$0) {
        this.uuid = $$0;
    }

    @Override
    public boolean isSingleplayerOwner(GameProfile $$0) {
        return this.getSingleplayerProfile() != null && $$0.getName().equalsIgnoreCase(this.getSingleplayerProfile().getName());
    }

    @Override
    public int getScaledTrackingDistance(int $$0) {
        return (int)(this.minecraft.options.entityDistanceScaling().get() * (double)$$0);
    }

    @Override
    public boolean forceSynchronousWrites() {
        return this.minecraft.options.syncWrites;
    }

    @Override
    @Nullable
    public GameType getForcedGameType() {
        if (this.isPublished()) {
            return (GameType)MoreObjects.firstNonNull((Object)this.publishedGameType, (Object)this.worldData.getGameType());
        }
        return null;
    }
}