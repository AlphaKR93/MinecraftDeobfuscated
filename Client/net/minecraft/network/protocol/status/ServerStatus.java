/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  com.mojang.authlib.GameProfile
 *  java.lang.Boolean
 *  java.lang.Integer
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.reflect.Type
 *  java.util.UUID
 *  javax.annotation.Nullable
 */
package net.minecraft.network.protocol.status;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Type;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;

public class ServerStatus {
    public static final int FAVICON_WIDTH = 64;
    public static final int FAVICON_HEIGHT = 64;
    @Nullable
    private Component description;
    @Nullable
    private Players players;
    @Nullable
    private Version version;
    @Nullable
    private String favicon;
    private boolean enforcesSecureChat;

    @Nullable
    public Component getDescription() {
        return this.description;
    }

    public void setDescription(Component $$0) {
        this.description = $$0;
    }

    @Nullable
    public Players getPlayers() {
        return this.players;
    }

    public void setPlayers(Players $$0) {
        this.players = $$0;
    }

    @Nullable
    public Version getVersion() {
        return this.version;
    }

    public void setVersion(Version $$0) {
        this.version = $$0;
    }

    public void setFavicon(String $$0) {
        this.favicon = $$0;
    }

    @Nullable
    public String getFavicon() {
        return this.favicon;
    }

    public void setEnforcesSecureChat(boolean $$0) {
        this.enforcesSecureChat = $$0;
    }

    public boolean enforcesSecureChat() {
        return this.enforcesSecureChat;
    }

    public static class Players {
        private final int maxPlayers;
        private final int numPlayers;
        @Nullable
        private GameProfile[] sample;

        public Players(int $$0, int $$1) {
            this.maxPlayers = $$0;
            this.numPlayers = $$1;
        }

        public int getMaxPlayers() {
            return this.maxPlayers;
        }

        public int getNumPlayers() {
            return this.numPlayers;
        }

        @Nullable
        public GameProfile[] getSample() {
            return this.sample;
        }

        public void setSample(GameProfile[] $$0) {
            this.sample = $$0;
        }

        public static class Serializer
        implements JsonDeserializer<Players>,
        JsonSerializer<Players> {
            public Players deserialize(JsonElement $$0, Type $$1, JsonDeserializationContext $$2) throws JsonParseException {
                JsonArray $$5;
                JsonObject $$3 = GsonHelper.convertToJsonObject($$0, "players");
                Players $$4 = new Players(GsonHelper.getAsInt($$3, "max"), GsonHelper.getAsInt($$3, "online"));
                if (GsonHelper.isArrayNode($$3, "sample") && ($$5 = GsonHelper.getAsJsonArray($$3, "sample")).size() > 0) {
                    GameProfile[] $$6 = new GameProfile[$$5.size()];
                    for (int $$7 = 0; $$7 < $$6.length; ++$$7) {
                        JsonObject $$8 = GsonHelper.convertToJsonObject($$5.get($$7), "player[" + $$7 + "]");
                        String $$9 = GsonHelper.getAsString($$8, "id");
                        $$6[$$7] = new GameProfile(UUID.fromString((String)$$9), GsonHelper.getAsString($$8, "name"));
                    }
                    $$4.setSample($$6);
                }
                return $$4;
            }

            public JsonElement serialize(Players $$0, Type $$1, JsonSerializationContext $$2) {
                JsonObject $$3 = new JsonObject();
                $$3.addProperty("max", (Number)Integer.valueOf((int)$$0.getMaxPlayers()));
                $$3.addProperty("online", (Number)Integer.valueOf((int)$$0.getNumPlayers()));
                GameProfile[] $$4 = $$0.getSample();
                if ($$4 != null && $$4.length > 0) {
                    JsonArray $$5 = new JsonArray();
                    for (int $$6 = 0; $$6 < $$4.length; ++$$6) {
                        JsonObject $$7 = new JsonObject();
                        UUID $$8 = $$4[$$6].getId();
                        $$7.addProperty("id", $$8 == null ? "" : $$8.toString());
                        $$7.addProperty("name", $$4[$$6].getName());
                        $$5.add((JsonElement)$$7);
                    }
                    $$3.add("sample", (JsonElement)$$5);
                }
                return $$3;
            }
        }
    }

    public static class Version {
        private final String name;
        private final int protocol;

        public Version(String $$0, int $$1) {
            this.name = $$0;
            this.protocol = $$1;
        }

        public String getName() {
            return this.name;
        }

        public int getProtocol() {
            return this.protocol;
        }

        public static class Serializer
        implements JsonDeserializer<Version>,
        JsonSerializer<Version> {
            public Version deserialize(JsonElement $$0, Type $$1, JsonDeserializationContext $$2) throws JsonParseException {
                JsonObject $$3 = GsonHelper.convertToJsonObject($$0, "version");
                return new Version(GsonHelper.getAsString($$3, "name"), GsonHelper.getAsInt($$3, "protocol"));
            }

            public JsonElement serialize(Version $$0, Type $$1, JsonSerializationContext $$2) {
                JsonObject $$3 = new JsonObject();
                $$3.addProperty("name", $$0.getName());
                $$3.addProperty("protocol", (Number)Integer.valueOf((int)$$0.getProtocol()));
                return $$3;
            }
        }
    }

    public static class Serializer
    implements JsonDeserializer<ServerStatus>,
    JsonSerializer<ServerStatus> {
        public ServerStatus deserialize(JsonElement $$0, Type $$1, JsonDeserializationContext $$2) throws JsonParseException {
            JsonObject $$3 = GsonHelper.convertToJsonObject($$0, "status");
            ServerStatus $$4 = new ServerStatus();
            if ($$3.has("description")) {
                $$4.setDescription((Component)$$2.deserialize($$3.get("description"), Component.class));
            }
            if ($$3.has("players")) {
                $$4.setPlayers((Players)$$2.deserialize($$3.get("players"), Players.class));
            }
            if ($$3.has("version")) {
                $$4.setVersion((Version)$$2.deserialize($$3.get("version"), Version.class));
            }
            if ($$3.has("favicon")) {
                $$4.setFavicon(GsonHelper.getAsString($$3, "favicon"));
            }
            if ($$3.has("enforcesSecureChat")) {
                $$4.setEnforcesSecureChat(GsonHelper.getAsBoolean($$3, "enforcesSecureChat"));
            }
            return $$4;
        }

        public JsonElement serialize(ServerStatus $$0, Type $$1, JsonSerializationContext $$2) {
            JsonObject $$3 = new JsonObject();
            $$3.addProperty("enforcesSecureChat", Boolean.valueOf((boolean)$$0.enforcesSecureChat()));
            if ($$0.getDescription() != null) {
                $$3.add("description", $$2.serialize((Object)$$0.getDescription()));
            }
            if ($$0.getPlayers() != null) {
                $$3.add("players", $$2.serialize((Object)$$0.getPlayers()));
            }
            if ($$0.getVersion() != null) {
                $$3.add("version", $$2.serialize((Object)$$0.getVersion()));
            }
            if ($$0.getFavicon() != null) {
                $$3.addProperty("favicon", $$0.getFavicon());
            }
            return $$3;
        }
    }
}