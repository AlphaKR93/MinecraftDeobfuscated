/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.properties.PropertyMap
 *  java.io.File
 *  java.lang.Object
 *  java.lang.String
 *  java.net.Proxy
 *  java.nio.file.Path
 *  javax.annotation.Nullable
 */
package net.minecraft.client.main;

import com.mojang.authlib.properties.PropertyMap;
import com.mojang.blaze3d.platform.DisplayData;
import java.io.File;
import java.net.Proxy;
import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.client.User;
import net.minecraft.client.resources.IndexedAssetSource;

public class GameConfig {
    public final UserData user;
    public final DisplayData display;
    public final FolderData location;
    public final GameData game;
    public final ServerData server;

    public GameConfig(UserData $$0, DisplayData $$1, FolderData $$2, GameData $$3, ServerData $$4) {
        this.user = $$0;
        this.display = $$1;
        this.location = $$2;
        this.game = $$3;
        this.server = $$4;
    }

    public static class UserData {
        public final User user;
        public final PropertyMap userProperties;
        public final PropertyMap profileProperties;
        public final Proxy proxy;

        public UserData(User $$0, PropertyMap $$1, PropertyMap $$2, Proxy $$3) {
            this.user = $$0;
            this.userProperties = $$1;
            this.profileProperties = $$2;
            this.proxy = $$3;
        }
    }

    public static class FolderData {
        public final File gameDirectory;
        public final File resourcePackDirectory;
        public final File assetDirectory;
        @Nullable
        public final String assetIndex;

        public FolderData(File $$0, File $$1, File $$2, @Nullable String $$3) {
            this.gameDirectory = $$0;
            this.resourcePackDirectory = $$1;
            this.assetDirectory = $$2;
            this.assetIndex = $$3;
        }

        public Path getExternalAssetSource() {
            return this.assetIndex == null ? this.assetDirectory.toPath() : IndexedAssetSource.createIndexFs(this.assetDirectory.toPath(), this.assetIndex);
        }
    }

    public static class GameData {
        public final boolean demo;
        public final String launchVersion;
        public final String versionType;
        public final boolean disableMultiplayer;
        public final boolean disableChat;

        public GameData(boolean $$0, String $$1, String $$2, boolean $$3, boolean $$4) {
            this.demo = $$0;
            this.launchVersion = $$1;
            this.versionType = $$2;
            this.disableMultiplayer = $$3;
            this.disableChat = $$4;
        }
    }

    public static class ServerData {
        @Nullable
        public final String hostname;
        public final int port;

        public ServerData(@Nullable String $$0, int $$1) {
            this.hostname = $$0;
            this.port = $$1;
        }
    }
}