/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.authlib.GameProfile
 *  com.mojang.authlib.minecraft.UserApiService
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map
 *  java.util.Set
 *  java.util.UUID
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 */
package net.minecraft.client.gui.screens.social;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.UserApiService;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.client.multiplayer.PlayerInfo;

public class PlayerSocialManager {
    private final Minecraft minecraft;
    private final Set<UUID> hiddenPlayers = Sets.newHashSet();
    private final UserApiService service;
    private final Map<String, UUID> discoveredNamesToUUID = Maps.newHashMap();
    private boolean onlineMode;
    private CompletableFuture<?> pendingBlockListRefresh = CompletableFuture.completedFuture(null);

    public PlayerSocialManager(Minecraft $$0, UserApiService $$1) {
        this.minecraft = $$0;
        this.service = $$1;
    }

    public void hidePlayer(UUID $$0) {
        this.hiddenPlayers.add((Object)$$0);
    }

    public void showPlayer(UUID $$0) {
        this.hiddenPlayers.remove((Object)$$0);
    }

    public boolean shouldHideMessageFrom(UUID $$0) {
        return this.isHidden($$0) || this.isBlocked($$0);
    }

    public boolean isHidden(UUID $$0) {
        return this.hiddenPlayers.contains((Object)$$0);
    }

    public void startOnlineMode() {
        this.onlineMode = true;
        this.pendingBlockListRefresh = this.pendingBlockListRefresh.thenRunAsync(() -> ((UserApiService)this.service).refreshBlockList(), (Executor)Util.ioPool());
    }

    public void stopOnlineMode() {
        this.onlineMode = false;
    }

    public boolean isBlocked(UUID $$0) {
        if (!this.onlineMode) {
            return false;
        }
        this.pendingBlockListRefresh.join();
        return this.service.isBlockedPlayer($$0);
    }

    public Set<UUID> getHiddenPlayers() {
        return this.hiddenPlayers;
    }

    public UUID getDiscoveredUUID(String $$0) {
        return (UUID)this.discoveredNamesToUUID.getOrDefault((Object)$$0, (Object)Util.NIL_UUID);
    }

    public void addPlayer(PlayerInfo $$0) {
        Screen $$2;
        GameProfile $$1 = $$0.getProfile();
        if ($$1.isComplete()) {
            this.discoveredNamesToUUID.put((Object)$$1.getName(), (Object)$$1.getId());
        }
        if (($$2 = this.minecraft.screen) instanceof SocialInteractionsScreen) {
            SocialInteractionsScreen $$3 = (SocialInteractionsScreen)$$2;
            $$3.onAddPlayer($$0);
        }
    }

    public void removePlayer(UUID $$0) {
        Screen $$1 = this.minecraft.screen;
        if ($$1 instanceof SocialInteractionsScreen) {
            SocialInteractionsScreen $$2 = (SocialInteractionsScreen)$$1;
            $$2.onRemovePlayer($$0);
        }
    }
}