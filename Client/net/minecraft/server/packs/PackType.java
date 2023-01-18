/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.bridge.game.GameVersion
 *  com.mojang.bridge.game.PackType
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.server.packs;

import com.mojang.bridge.game.GameVersion;

public enum PackType {
    CLIENT_RESOURCES("assets", com.mojang.bridge.game.PackType.RESOURCE),
    SERVER_DATA("data", com.mojang.bridge.game.PackType.DATA);

    private final String directory;
    private final com.mojang.bridge.game.PackType bridgeType;

    private PackType(String $$0, com.mojang.bridge.game.PackType $$1) {
        this.directory = $$0;
        this.bridgeType = $$1;
    }

    public String getDirectory() {
        return this.directory;
    }

    public int getVersion(GameVersion $$0) {
        return $$0.getPackVersion(this.bridgeType);
    }
}