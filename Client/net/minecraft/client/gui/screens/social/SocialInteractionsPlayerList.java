/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Strings
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 *  it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Collection
 *  java.util.Comparator
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.UUID
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.social;

import com.google.common.base.Strings;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.screens.social.PlayerEntry;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.chat.ChatLog;
import net.minecraft.client.multiplayer.chat.LoggedChatEvent;
import net.minecraft.client.multiplayer.chat.LoggedChatMessage;
import net.minecraft.resources.ResourceLocation;

public class SocialInteractionsPlayerList
extends ContainerObjectSelectionList<PlayerEntry> {
    private final SocialInteractionsScreen socialInteractionsScreen;
    private final List<PlayerEntry> players = Lists.newArrayList();
    @Nullable
    private String filter;

    public SocialInteractionsPlayerList(SocialInteractionsScreen $$0, Minecraft $$1, int $$2, int $$3, int $$4, int $$5, int $$6) {
        super($$1, $$2, $$3, $$4, $$5, $$6);
        this.socialInteractionsScreen = $$0;
        this.setRenderBackground(false);
        this.setRenderTopAndBottom(false);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        double $$4 = this.minecraft.getWindow().getGuiScale();
        RenderSystem.enableScissor((int)((double)this.getRowLeft() * $$4), (int)((double)(this.height - this.y1) * $$4), (int)((double)(this.getScrollbarPosition() + 6) * $$4), (int)((double)(this.height - (this.height - this.y1) - this.y0 - 4) * $$4));
        super.render($$0, $$1, $$2, $$3);
        RenderSystem.disableScissor();
    }

    public void updatePlayerList(Collection<UUID> $$0, double $$1, boolean $$2) {
        HashMap $$3 = new HashMap();
        this.addOnlinePlayers($$0, (Map<UUID, PlayerEntry>)$$3);
        this.updatePlayersFromChatLog((Map<UUID, PlayerEntry>)$$3, $$2);
        this.updateFiltersAndScroll((Collection<PlayerEntry>)$$3.values(), $$1);
    }

    private void addOnlinePlayers(Collection<UUID> $$0, Map<UUID, PlayerEntry> $$1) {
        ClientPacketListener $$2 = this.minecraft.player.connection;
        for (UUID $$3 : $$0) {
            PlayerInfo $$4 = $$2.getPlayerInfo($$3);
            if ($$4 == null) continue;
            boolean $$5 = $$4.hasVerifiableChat();
            $$1.put((Object)$$3, (Object)new PlayerEntry(this.minecraft, this.socialInteractionsScreen, $$3, $$4.getProfile().getName(), (Supplier<ResourceLocation>)((Supplier)$$4::getSkinLocation), $$5));
        }
    }

    private void updatePlayersFromChatLog(Map<UUID, PlayerEntry> $$0, boolean $$12) {
        Collection<GameProfile> $$2 = SocialInteractionsPlayerList.collectProfilesFromChatLog(this.minecraft.getReportingContext().chatLog());
        for (GameProfile $$3 : $$2) {
            PlayerEntry $$5;
            if ($$12) {
                PlayerEntry $$4 = (PlayerEntry)$$0.computeIfAbsent((Object)$$3.getId(), $$1 -> {
                    PlayerEntry $$2 = new PlayerEntry(this.minecraft, this.socialInteractionsScreen, $$3.getId(), $$3.getName(), (Supplier<ResourceLocation>)Suppliers.memoize(() -> this.minecraft.getSkinManager().getInsecureSkinLocation($$3)), true);
                    $$2.setRemoved(true);
                    return $$2;
                });
            } else {
                $$5 = (PlayerEntry)$$0.get((Object)$$3.getId());
                if ($$5 == null) continue;
            }
            $$5.setHasRecentMessages(true);
        }
    }

    private static Collection<GameProfile> collectProfilesFromChatLog(ChatLog $$0) {
        ObjectLinkedOpenHashSet $$1 = new ObjectLinkedOpenHashSet();
        for (int $$2 = $$0.end(); $$2 >= $$0.start(); --$$2) {
            LoggedChatMessage.Player $$4;
            LoggedChatEvent $$3 = $$0.lookup($$2);
            if (!($$3 instanceof LoggedChatMessage.Player) || !($$4 = (LoggedChatMessage.Player)$$3).message().hasSignature()) continue;
            $$1.add((Object)$$4.profile());
        }
        return $$1;
    }

    private void sortPlayerEntries() {
        this.players.sort(Comparator.comparing($$0 -> {
            if ($$0.getPlayerId().equals((Object)this.minecraft.getUser().getProfileId())) {
                return 0;
            }
            if ($$0.getPlayerId().version() == 2) {
                return 4;
            }
            if (this.minecraft.getReportingContext().hasDraftReportFor($$0.getPlayerId())) {
                return 1;
            }
            if ($$0.hasRecentMessages()) {
                return 2;
            }
            return 3;
        }).thenComparing($$0 -> {
            int $$1;
            if (!$$0.getPlayerName().isBlank() && (($$1 = $$0.getPlayerName().codePointAt(0)) == 95 || $$1 >= 97 && $$1 <= 122 || $$1 >= 65 && $$1 <= 90 || $$1 >= 48 && $$1 <= 57)) {
                return 0;
            }
            return 1;
        }).thenComparing(PlayerEntry::getPlayerName, String::compareToIgnoreCase));
    }

    private void updateFiltersAndScroll(Collection<PlayerEntry> $$0, double $$1) {
        this.players.clear();
        this.players.addAll($$0);
        this.sortPlayerEntries();
        this.updateFilteredPlayers();
        this.replaceEntries(this.players);
        this.setScrollAmount($$1);
    }

    private void updateFilteredPlayers() {
        if (this.filter != null) {
            this.players.removeIf($$0 -> !$$0.getPlayerName().toLowerCase(Locale.ROOT).contains((CharSequence)this.filter));
            this.replaceEntries(this.players);
        }
    }

    public void setFilter(String $$0) {
        this.filter = $$0;
    }

    public boolean isEmpty() {
        return this.players.isEmpty();
    }

    public void addPlayer(PlayerInfo $$0, SocialInteractionsScreen.Page $$1) {
        UUID $$2 = $$0.getProfile().getId();
        for (PlayerEntry $$3 : this.players) {
            if (!$$3.getPlayerId().equals((Object)$$2)) continue;
            $$3.setRemoved(false);
            return;
        }
        if (($$1 == SocialInteractionsScreen.Page.ALL || this.minecraft.getPlayerSocialManager().shouldHideMessageFrom($$2)) && (Strings.isNullOrEmpty((String)this.filter) || $$0.getProfile().getName().toLowerCase(Locale.ROOT).contains((CharSequence)this.filter))) {
            boolean $$4 = $$0.hasVerifiableChat();
            PlayerEntry $$5 = new PlayerEntry(this.minecraft, this.socialInteractionsScreen, $$0.getProfile().getId(), $$0.getProfile().getName(), (Supplier<ResourceLocation>)((Supplier)$$0::getSkinLocation), $$4);
            this.addEntry($$5);
            this.players.add((Object)$$5);
        }
    }

    public void removePlayer(UUID $$0) {
        for (PlayerEntry $$1 : this.players) {
            if (!$$1.getPlayerId().equals((Object)$$0)) continue;
            $$1.setRemoved(true);
            return;
        }
    }
}