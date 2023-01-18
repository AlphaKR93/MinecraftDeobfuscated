/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.hash.Hashing
 *  com.google.common.io.Files
 *  com.mojang.logging.LogUtils
 *  java.io.File
 *  java.io.IOException
 *  java.lang.CharSequence
 *  java.lang.Exception
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.Throwable
 *  java.lang.Void
 *  java.net.URL
 *  java.nio.charset.StandardCharsets
 *  java.nio.file.Files
 *  java.nio.file.LinkOption
 *  java.nio.file.Path
 *  java.util.ArrayList
 *  java.util.Locale
 *  java.util.Map
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.concurrent.locks.ReentrantLock
 *  java.util.function.Consumer
 *  java.util.regex.Pattern
 *  javax.annotation.Nullable
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.comparator.LastModifiedFileComparator
 *  org.apache.commons.io.filefilter.IOFileFilter
 *  org.apache.commons.io.filefilter.TrueFileFilter
 *  org.slf4j.Logger
 */
package net.minecraft.client.resources;

import com.google.common.hash.Hashing;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.ProgressScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.util.HttpUtil;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;

public class DownloadedPackSource
implements RepositorySource {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Pattern SHA1 = Pattern.compile((String)"^[a-fA-F0-9]{40}$");
    private static final int MAX_PACK_SIZE_BYTES = 0xFA00000;
    private static final int MAX_KEPT_PACKS = 10;
    private static final String SERVER_ID = "server";
    private static final Component SERVER_NAME = Component.translatable("resourcePack.server.name");
    private static final Component APPLYING_PACK_TEXT = Component.translatable("multiplayer.applyingPack");
    private final File serverPackDir;
    private final ReentrantLock downloadLock = new ReentrantLock();
    @Nullable
    private CompletableFuture<?> currentDownload;
    @Nullable
    private Pack serverPack;

    public DownloadedPackSource(File $$0) {
        this.serverPackDir = $$0;
    }

    @Override
    public void loadPacks(Consumer<Pack> $$0) {
        if (this.serverPack != null) {
            $$0.accept((Object)this.serverPack);
        }
    }

    private static Map<String, String> getDownloadHeaders() {
        return Map.of((Object)"X-Minecraft-Username", (Object)Minecraft.getInstance().getUser().getName(), (Object)"X-Minecraft-UUID", (Object)Minecraft.getInstance().getUser().getUuid(), (Object)"X-Minecraft-Version", (Object)SharedConstants.getCurrentVersion().getName(), (Object)"X-Minecraft-Version-ID", (Object)SharedConstants.getCurrentVersion().getId(), (Object)"X-Minecraft-Pack-Format", (Object)String.valueOf((int)PackType.CLIENT_RESOURCES.getVersion(SharedConstants.getCurrentVersion())), (Object)"User-Agent", (Object)("Minecraft Java/" + SharedConstants.getCurrentVersion().getName()));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CompletableFuture<?> downloadAndSelectResourcePack(URL $$02, String $$1, boolean $$2) {
        String $$3 = Hashing.sha1().hashString((CharSequence)$$02.toString(), StandardCharsets.UTF_8).toString();
        String $$42 = SHA1.matcher((CharSequence)$$1).matches() ? $$1 : "";
        this.downloadLock.lock();
        try {
            CompletableFuture<?> $$10;
            Minecraft $$5 = Minecraft.getInstance();
            File $$6 = new File(this.serverPackDir, $$3);
            if ($$6.exists()) {
                CompletableFuture $$7 = CompletableFuture.completedFuture((Object)"");
            } else {
                ProgressScreen $$8 = new ProgressScreen($$2);
                Map<String, String> $$9 = DownloadedPackSource.getDownloadHeaders();
                $$5.executeBlocking(() -> $$5.setScreen($$8));
                $$10 = HttpUtil.downloadTo($$6, $$02, $$9, 0xFA00000, $$8, $$5.getProxy());
            }
            this.currentDownload = $$10.thenCompose($$4 -> {
                if (!this.checkHash($$42, $$6)) {
                    return CompletableFuture.failedFuture((Throwable)new RuntimeException("Hash check failure for file " + $$6 + ", see log"));
                }
                $$5.execute(() -> {
                    if (!$$2) {
                        $$5.setScreen(new GenericDirtMessageScreen(APPLYING_PACK_TEXT));
                    }
                });
                return this.setServerPack($$6, PackSource.SERVER);
            }).exceptionallyCompose($$22 -> this.clearServerPack().thenAcceptAsync($$2 -> {
                LOGGER.warn("Pack application failed: {}, deleting file {}", (Object)$$22.getMessage(), (Object)$$6);
                DownloadedPackSource.deleteQuietly($$6);
            }, (Executor)Util.ioPool()).thenAcceptAsync($$12 -> $$5.setScreen(new ConfirmScreen($$1 -> {
                if ($$1) {
                    $$5.setScreen(null);
                } else {
                    ClientPacketListener $$2 = $$5.getConnection();
                    if ($$2 != null) {
                        $$2.getConnection().disconnect(Component.translatable("connect.aborted"));
                    }
                }
            }, Component.translatable("multiplayer.texturePrompt.failure.line1"), Component.translatable("multiplayer.texturePrompt.failure.line2"), CommonComponents.GUI_PROCEED, Component.translatable("menu.disconnect"))), (Executor)$$5)).thenAcceptAsync($$0 -> this.clearOldDownloads(), (Executor)Util.ioPool());
            CompletableFuture<?> completableFuture = this.currentDownload;
            return completableFuture;
        }
        finally {
            this.downloadLock.unlock();
        }
    }

    private static void deleteQuietly(File $$0) {
        try {
            Files.delete((Path)$$0.toPath());
        }
        catch (IOException $$1) {
            LOGGER.warn("Failed to delete file {}: {}", (Object)$$0, (Object)$$1.getMessage());
        }
    }

    public CompletableFuture<Void> clearServerPack() {
        this.downloadLock.lock();
        try {
            if (this.currentDownload != null) {
                this.currentDownload.cancel(true);
            }
            this.currentDownload = null;
            if (this.serverPack != null) {
                this.serverPack = null;
                CompletableFuture<Void> completableFuture = Minecraft.getInstance().delayTextureReload();
                return completableFuture;
            }
        }
        finally {
            this.downloadLock.unlock();
        }
        return CompletableFuture.completedFuture(null);
    }

    private boolean checkHash(String $$0, File $$1) {
        try {
            String $$2 = com.google.common.io.Files.asByteSource((File)$$1).hash(Hashing.sha1()).toString();
            if ($$0.isEmpty()) {
                LOGGER.info("Found file {} without verification hash", (Object)$$1);
                return true;
            }
            if ($$2.toLowerCase(Locale.ROOT).equals((Object)$$0.toLowerCase(Locale.ROOT))) {
                LOGGER.info("Found file {} matching requested hash {}", (Object)$$1, (Object)$$0);
                return true;
            }
            LOGGER.warn("File {} had wrong hash (expected {}, found {}).", new Object[]{$$1, $$0, $$2});
        }
        catch (IOException $$3) {
            LOGGER.warn("File {} couldn't be hashed.", (Object)$$1, (Object)$$3);
        }
        return false;
    }

    private void clearOldDownloads() {
        if (!this.serverPackDir.isDirectory()) {
            return;
        }
        try {
            ArrayList $$0 = new ArrayList(FileUtils.listFiles((File)this.serverPackDir, (IOFileFilter)TrueFileFilter.TRUE, null));
            $$0.sort(LastModifiedFileComparator.LASTMODIFIED_REVERSE);
            int $$1 = 0;
            for (File $$2 : $$0) {
                if ($$1++ < 10) continue;
                LOGGER.info("Deleting old server resource pack {}", (Object)$$2.getName());
                FileUtils.deleteQuietly((File)$$2);
            }
        }
        catch (Exception $$3) {
            LOGGER.error("Error while deleting old server resource pack : {}", (Object)$$3.getMessage());
        }
    }

    public CompletableFuture<Void> setServerPack(File $$0, PackSource $$12) {
        Pack.ResourcesSupplier $$2 = $$1 -> new FilePackResources($$1, $$0, false);
        Pack.Info $$3 = Pack.readPackInfo(SERVER_ID, $$2);
        if ($$3 == null) {
            return CompletableFuture.failedFuture((Throwable)new IllegalArgumentException("Invalid pack metadata at " + $$0));
        }
        LOGGER.info("Applying server pack {}", (Object)$$0);
        this.serverPack = Pack.create(SERVER_ID, SERVER_NAME, true, $$2, $$3, PackType.CLIENT_RESOURCES, Pack.Position.TOP, true, $$12);
        return Minecraft.getInstance().delayTextureReload();
    }

    public CompletableFuture<Void> loadBundledResourcePack(LevelStorageSource.LevelStorageAccess $$0) {
        Path $$1 = $$0.getLevelPath(LevelResource.MAP_RESOURCE_FILE);
        if (Files.exists((Path)$$1, (LinkOption[])new LinkOption[0]) && !Files.isDirectory((Path)$$1, (LinkOption[])new LinkOption[0])) {
            return this.setServerPack($$1.toFile(), PackSource.WORLD);
        }
        return CompletableFuture.completedFuture(null);
    }
}