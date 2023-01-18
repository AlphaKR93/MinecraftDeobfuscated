/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Collection
 *  java.util.HashSet
 *  java.util.Set
 *  java.util.UUID
 *  java.util.function.UnaryOperator
 */
package net.minecraft.server.bossevents;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.UnaryOperator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;

public class CustomBossEvent
extends ServerBossEvent {
    private final ResourceLocation id;
    private final Set<UUID> players = Sets.newHashSet();
    private int value;
    private int max = 100;

    public CustomBossEvent(ResourceLocation $$0, Component $$1) {
        super($$1, BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.PROGRESS);
        this.id = $$0;
        this.setProgress(0.0f);
    }

    public ResourceLocation getTextId() {
        return this.id;
    }

    @Override
    public void addPlayer(ServerPlayer $$0) {
        super.addPlayer($$0);
        this.players.add((Object)$$0.getUUID());
    }

    public void addOfflinePlayer(UUID $$0) {
        this.players.add((Object)$$0);
    }

    @Override
    public void removePlayer(ServerPlayer $$0) {
        super.removePlayer($$0);
        this.players.remove((Object)$$0.getUUID());
    }

    @Override
    public void removeAllPlayers() {
        super.removeAllPlayers();
        this.players.clear();
    }

    public int getValue() {
        return this.value;
    }

    public int getMax() {
        return this.max;
    }

    public void setValue(int $$0) {
        this.value = $$0;
        this.setProgress(Mth.clamp((float)$$0 / (float)this.max, 0.0f, 1.0f));
    }

    public void setMax(int $$0) {
        this.max = $$0;
        this.setProgress(Mth.clamp((float)this.value / (float)$$0, 0.0f, 1.0f));
    }

    public final Component getDisplayName() {
        return ComponentUtils.wrapInSquareBrackets(this.getName()).withStyle((UnaryOperator<Style>)((UnaryOperator)$$0 -> $$0.withColor(this.getColor().getFormatting()).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(this.getTextId().toString()))).withInsertion(this.getTextId().toString())));
    }

    public boolean setPlayers(Collection<ServerPlayer> $$0) {
        HashSet $$1 = Sets.newHashSet();
        HashSet $$2 = Sets.newHashSet();
        for (UUID $$3 : this.players) {
            boolean $$4 = false;
            for (ServerPlayer $$5 : $$0) {
                if (!$$5.getUUID().equals((Object)$$3)) continue;
                $$4 = true;
                break;
            }
            if ($$4) continue;
            $$1.add((Object)$$3);
        }
        for (ServerPlayer $$6 : $$0) {
            boolean $$7 = false;
            for (UUID $$8 : this.players) {
                if (!$$6.getUUID().equals((Object)$$8)) continue;
                $$7 = true;
                break;
            }
            if ($$7) continue;
            $$2.add((Object)$$6);
        }
        for (UUID $$9 : $$1) {
            for (ServerPlayer $$10 : this.getPlayers()) {
                if (!$$10.getUUID().equals((Object)$$9)) continue;
                this.removePlayer($$10);
                break;
            }
            this.players.remove((Object)$$9);
        }
        for (ServerPlayer $$11 : $$2) {
            this.addPlayer($$11);
        }
        return !$$1.isEmpty() || !$$2.isEmpty();
    }

    public CompoundTag save() {
        CompoundTag $$0 = new CompoundTag();
        $$0.putString("Name", Component.Serializer.toJson(this.name));
        $$0.putBoolean("Visible", this.isVisible());
        $$0.putInt("Value", this.value);
        $$0.putInt("Max", this.max);
        $$0.putString("Color", this.getColor().getName());
        $$0.putString("Overlay", this.getOverlay().getName());
        $$0.putBoolean("DarkenScreen", this.shouldDarkenScreen());
        $$0.putBoolean("PlayBossMusic", this.shouldPlayBossMusic());
        $$0.putBoolean("CreateWorldFog", this.shouldCreateWorldFog());
        ListTag $$1 = new ListTag();
        for (UUID $$2 : this.players) {
            $$1.add(NbtUtils.createUUID($$2));
        }
        $$0.put("Players", $$1);
        return $$0;
    }

    public static CustomBossEvent load(CompoundTag $$0, ResourceLocation $$1) {
        CustomBossEvent $$2 = new CustomBossEvent($$1, Component.Serializer.fromJson($$0.getString("Name")));
        $$2.setVisible($$0.getBoolean("Visible"));
        $$2.setValue($$0.getInt("Value"));
        $$2.setMax($$0.getInt("Max"));
        $$2.setColor(BossEvent.BossBarColor.byName($$0.getString("Color")));
        $$2.setOverlay(BossEvent.BossBarOverlay.byName($$0.getString("Overlay")));
        $$2.setDarkenScreen($$0.getBoolean("DarkenScreen"));
        $$2.setPlayBossMusic($$0.getBoolean("PlayBossMusic"));
        $$2.setCreateWorldFog($$0.getBoolean("CreateWorldFog"));
        ListTag $$3 = $$0.getList("Players", 11);
        for (int $$4 = 0; $$4 < $$3.size(); ++$$4) {
            $$2.addOfflinePlayer(NbtUtils.loadUUID($$3.get($$4)));
        }
        return $$2;
    }

    public void onPlayerConnect(ServerPlayer $$0) {
        if (this.players.contains((Object)$$0.getUUID())) {
            this.addPlayer($$0);
        }
    }

    public void onPlayerDisconnect(ServerPlayer $$0) {
        super.removePlayer($$0);
    }
}