/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.collect.ComparisonChain
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.mojang.logging.LogUtils
 *  java.lang.Comparable
 *  java.lang.Exception
 *  java.lang.Integer
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Comparator
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Objects
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.dto;

import com.google.common.base.Joiner;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsServerPing;
import com.mojang.realmsclient.dto.RealmsServerPlayerList;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.slf4j.Logger;

public class RealmsServer
extends ValueObject {
    private static final Logger LOGGER = LogUtils.getLogger();
    public long id;
    public String remoteSubscriptionId;
    public String name;
    public String motd;
    public State state;
    public String owner;
    public String ownerUUID;
    public List<PlayerInfo> players;
    public Map<Integer, RealmsWorldOptions> slots;
    public boolean expired;
    public boolean expiredTrial;
    public int daysLeft;
    public WorldType worldType;
    public int activeSlot;
    public String minigameName;
    public int minigameId;
    public String minigameImage;
    public RealmsServerPing serverPing = new RealmsServerPing();

    public String getDescription() {
        return this.motd;
    }

    public String getName() {
        return this.name;
    }

    public String getMinigameName() {
        return this.minigameName;
    }

    public void setName(String $$0) {
        this.name = $$0;
    }

    public void setDescription(String $$0) {
        this.motd = $$0;
    }

    public void updateServerPing(RealmsServerPlayerList $$0) {
        ArrayList $$1 = Lists.newArrayList();
        int $$2 = 0;
        for (String $$3 : $$0.players) {
            if ($$3.equals((Object)Minecraft.getInstance().getUser().getUuid())) continue;
            String $$4 = "";
            try {
                $$4 = RealmsUtil.uuidToName($$3);
            }
            catch (Exception $$5) {
                LOGGER.error("Could not get name for {}", (Object)$$3, (Object)$$5);
                continue;
            }
            $$1.add((Object)$$4);
            ++$$2;
        }
        this.serverPing.nrOfPlayers = String.valueOf((int)$$2);
        this.serverPing.playerList = Joiner.on((char)'\n').join((Iterable)$$1);
    }

    public static RealmsServer parse(JsonObject $$0) {
        RealmsServer $$1 = new RealmsServer();
        try {
            $$1.id = JsonUtils.getLongOr("id", $$0, -1L);
            $$1.remoteSubscriptionId = JsonUtils.getStringOr("remoteSubscriptionId", $$0, null);
            $$1.name = JsonUtils.getStringOr("name", $$0, null);
            $$1.motd = JsonUtils.getStringOr("motd", $$0, null);
            $$1.state = RealmsServer.getState(JsonUtils.getStringOr("state", $$0, State.CLOSED.name()));
            $$1.owner = JsonUtils.getStringOr("owner", $$0, null);
            if ($$0.get("players") != null && $$0.get("players").isJsonArray()) {
                $$1.players = RealmsServer.parseInvited($$0.get("players").getAsJsonArray());
                RealmsServer.sortInvited($$1);
            } else {
                $$1.players = Lists.newArrayList();
            }
            $$1.daysLeft = JsonUtils.getIntOr("daysLeft", $$0, 0);
            $$1.expired = JsonUtils.getBooleanOr("expired", $$0, false);
            $$1.expiredTrial = JsonUtils.getBooleanOr("expiredTrial", $$0, false);
            $$1.worldType = RealmsServer.getWorldType(JsonUtils.getStringOr("worldType", $$0, WorldType.NORMAL.name()));
            $$1.ownerUUID = JsonUtils.getStringOr("ownerUUID", $$0, "");
            $$1.slots = $$0.get("slots") != null && $$0.get("slots").isJsonArray() ? RealmsServer.parseSlots($$0.get("slots").getAsJsonArray()) : RealmsServer.createEmptySlots();
            $$1.minigameName = JsonUtils.getStringOr("minigameName", $$0, null);
            $$1.activeSlot = JsonUtils.getIntOr("activeSlot", $$0, -1);
            $$1.minigameId = JsonUtils.getIntOr("minigameId", $$0, -1);
            $$1.minigameImage = JsonUtils.getStringOr("minigameImage", $$0, null);
        }
        catch (Exception $$2) {
            LOGGER.error("Could not parse McoServer: {}", (Object)$$2.getMessage());
        }
        return $$1;
    }

    private static void sortInvited(RealmsServer $$02) {
        $$02.players.sort(($$0, $$1) -> ComparisonChain.start().compareFalseFirst($$1.getAccepted(), $$0.getAccepted()).compare((Comparable)$$0.getName().toLowerCase(Locale.ROOT), (Comparable)$$1.getName().toLowerCase(Locale.ROOT)).result());
    }

    private static List<PlayerInfo> parseInvited(JsonArray $$0) {
        ArrayList $$1 = Lists.newArrayList();
        for (JsonElement $$2 : $$0) {
            try {
                JsonObject $$3 = $$2.getAsJsonObject();
                PlayerInfo $$4 = new PlayerInfo();
                $$4.setName(JsonUtils.getStringOr("name", $$3, null));
                $$4.setUuid(JsonUtils.getStringOr("uuid", $$3, null));
                $$4.setOperator(JsonUtils.getBooleanOr("operator", $$3, false));
                $$4.setAccepted(JsonUtils.getBooleanOr("accepted", $$3, false));
                $$4.setOnline(JsonUtils.getBooleanOr("online", $$3, false));
                $$1.add((Object)$$4);
            }
            catch (Exception exception) {}
        }
        return $$1;
    }

    private static Map<Integer, RealmsWorldOptions> parseSlots(JsonArray $$0) {
        HashMap $$1 = Maps.newHashMap();
        for (JsonElement $$2 : $$0) {
            try {
                RealmsWorldOptions $$7;
                JsonObject $$3 = $$2.getAsJsonObject();
                JsonParser $$4 = new JsonParser();
                JsonElement $$5 = $$4.parse($$3.get("options").getAsString());
                if ($$5 == null) {
                    RealmsWorldOptions $$6 = RealmsWorldOptions.createDefaults();
                } else {
                    $$7 = RealmsWorldOptions.parse($$5.getAsJsonObject());
                }
                int $$8 = JsonUtils.getIntOr("slotId", $$3, -1);
                $$1.put((Object)$$8, (Object)$$7);
            }
            catch (Exception exception) {}
        }
        for (int $$9 = 1; $$9 <= 3; ++$$9) {
            if ($$1.containsKey((Object)$$9)) continue;
            $$1.put((Object)$$9, (Object)RealmsWorldOptions.createEmptyDefaults());
        }
        return $$1;
    }

    private static Map<Integer, RealmsWorldOptions> createEmptySlots() {
        HashMap $$0 = Maps.newHashMap();
        $$0.put((Object)1, (Object)RealmsWorldOptions.createEmptyDefaults());
        $$0.put((Object)2, (Object)RealmsWorldOptions.createEmptyDefaults());
        $$0.put((Object)3, (Object)RealmsWorldOptions.createEmptyDefaults());
        return $$0;
    }

    public static RealmsServer parse(String $$0) {
        try {
            return RealmsServer.parse(new JsonParser().parse($$0).getAsJsonObject());
        }
        catch (Exception $$1) {
            LOGGER.error("Could not parse McoServer: {}", (Object)$$1.getMessage());
            return new RealmsServer();
        }
    }

    private static State getState(String $$0) {
        try {
            return State.valueOf($$0);
        }
        catch (Exception $$1) {
            return State.CLOSED;
        }
    }

    private static WorldType getWorldType(String $$0) {
        try {
            return WorldType.valueOf($$0);
        }
        catch (Exception $$1) {
            return WorldType.NORMAL;
        }
    }

    public int hashCode() {
        return Objects.hash((Object[])new Object[]{this.id, this.name, this.motd, this.state, this.owner, this.expired});
    }

    public boolean equals(Object $$0) {
        if ($$0 == null) {
            return false;
        }
        if ($$0 == this) {
            return true;
        }
        if ($$0.getClass() != this.getClass()) {
            return false;
        }
        RealmsServer $$1 = (RealmsServer)$$0;
        return new EqualsBuilder().append(this.id, $$1.id).append((Object)this.name, (Object)$$1.name).append((Object)this.motd, (Object)$$1.motd).append((Object)this.state, (Object)$$1.state).append((Object)this.owner, (Object)$$1.owner).append(this.expired, $$1.expired).append((Object)this.worldType, (Object)this.worldType).isEquals();
    }

    public RealmsServer clone() {
        RealmsServer $$0 = new RealmsServer();
        $$0.id = this.id;
        $$0.remoteSubscriptionId = this.remoteSubscriptionId;
        $$0.name = this.name;
        $$0.motd = this.motd;
        $$0.state = this.state;
        $$0.owner = this.owner;
        $$0.players = this.players;
        $$0.slots = this.cloneSlots(this.slots);
        $$0.expired = this.expired;
        $$0.expiredTrial = this.expiredTrial;
        $$0.daysLeft = this.daysLeft;
        $$0.serverPing = new RealmsServerPing();
        $$0.serverPing.nrOfPlayers = this.serverPing.nrOfPlayers;
        $$0.serverPing.playerList = this.serverPing.playerList;
        $$0.worldType = this.worldType;
        $$0.ownerUUID = this.ownerUUID;
        $$0.minigameName = this.minigameName;
        $$0.activeSlot = this.activeSlot;
        $$0.minigameId = this.minigameId;
        $$0.minigameImage = this.minigameImage;
        return $$0;
    }

    public Map<Integer, RealmsWorldOptions> cloneSlots(Map<Integer, RealmsWorldOptions> $$0) {
        HashMap $$1 = Maps.newHashMap();
        for (Map.Entry $$2 : $$0.entrySet()) {
            $$1.put((Object)((Integer)$$2.getKey()), (Object)((RealmsWorldOptions)$$2.getValue()).clone());
        }
        return $$1;
    }

    public String getWorldName(int $$0) {
        return this.name + " (" + ((RealmsWorldOptions)this.slots.get((Object)$$0)).getSlotName($$0) + ")";
    }

    public ServerData toServerData(String $$0) {
        return new ServerData(this.name, $$0, false);
    }

    public static enum State {
        CLOSED,
        OPEN,
        UNINITIALIZED;

    }

    public static enum WorldType {
        NORMAL,
        MINIGAME,
        ADVENTUREMAP,
        EXPERIENCE,
        INSPIRATION;

    }

    public static class McoServerComparator
    implements Comparator<RealmsServer> {
        private final String refOwner;

        public McoServerComparator(String $$0) {
            this.refOwner = $$0;
        }

        public int compare(RealmsServer $$0, RealmsServer $$1) {
            return ComparisonChain.start().compareTrueFirst($$0.state == State.UNINITIALIZED, $$1.state == State.UNINITIALIZED).compareTrueFirst($$0.expiredTrial, $$1.expiredTrial).compareTrueFirst($$0.owner.equals((Object)this.refOwner), $$1.owner.equals((Object)this.refOwner)).compareFalseFirst($$0.expired, $$1.expired).compareTrueFirst($$0.state == State.OPEN, $$1.state == State.OPEN).compare($$0.id, $$1.id).result();
        }
    }
}