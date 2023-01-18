/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  java.lang.Object
 *  java.lang.Override
 *  java.net.SocketAddress
 */
package net.minecraft.client.server;

import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.storage.PlayerDataStorage;

public class IntegratedPlayerList
extends PlayerList {
    private CompoundTag playerData;

    public IntegratedPlayerList(IntegratedServer $$0, LayeredRegistryAccess<RegistryLayer> $$1, PlayerDataStorage $$2) {
        super($$0, $$1, $$2, 8);
        this.setViewDistance(10);
    }

    @Override
    protected void save(ServerPlayer $$0) {
        if (this.getServer().isSingleplayerOwner($$0.getGameProfile())) {
            this.playerData = $$0.saveWithoutId(new CompoundTag());
        }
        super.save($$0);
    }

    @Override
    public Component canPlayerLogin(SocketAddress $$0, GameProfile $$1) {
        if (this.getServer().isSingleplayerOwner($$1) && this.getPlayerByName($$1.getName()) != null) {
            return Component.translatable("multiplayer.disconnect.name_taken");
        }
        return super.canPlayerLogin($$0, $$1);
    }

    @Override
    public IntegratedServer getServer() {
        return (IntegratedServer)super.getServer();
    }

    @Override
    public CompoundTag getSingleplayerData() {
        return this.playerData;
    }
}