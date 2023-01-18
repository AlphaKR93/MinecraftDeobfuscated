/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.text.ParseException
 *  java.util.Collections
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.client.multiplayer;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

public class ServerData {
    public String name;
    public String ip;
    public Component status;
    public Component motd;
    public long ping;
    public int protocol = SharedConstants.getCurrentVersion().getProtocolVersion();
    public Component version = Component.literal(SharedConstants.getCurrentVersion().getName());
    public boolean pinged;
    public List<Component> playerList = Collections.emptyList();
    private ServerPackStatus packStatus = ServerPackStatus.PROMPT;
    @Nullable
    private String iconB64;
    private boolean lan;
    private boolean enforcesSecureChat;

    public ServerData(String $$0, String $$1, boolean $$2) {
        this.name = $$0;
        this.ip = $$1;
        this.lan = $$2;
    }

    public CompoundTag write() {
        CompoundTag $$0 = new CompoundTag();
        $$0.putString("name", this.name);
        $$0.putString("ip", this.ip);
        if (this.iconB64 != null) {
            $$0.putString("icon", this.iconB64);
        }
        if (this.packStatus == ServerPackStatus.ENABLED) {
            $$0.putBoolean("acceptTextures", true);
        } else if (this.packStatus == ServerPackStatus.DISABLED) {
            $$0.putBoolean("acceptTextures", false);
        }
        return $$0;
    }

    public ServerPackStatus getResourcePackStatus() {
        return this.packStatus;
    }

    public void setResourcePackStatus(ServerPackStatus $$0) {
        this.packStatus = $$0;
    }

    public static ServerData read(CompoundTag $$0) {
        ServerData $$1 = new ServerData($$0.getString("name"), $$0.getString("ip"), false);
        if ($$0.contains("icon", 8)) {
            $$1.setIconB64($$0.getString("icon"));
        }
        if ($$0.contains("acceptTextures", 1)) {
            if ($$0.getBoolean("acceptTextures")) {
                $$1.setResourcePackStatus(ServerPackStatus.ENABLED);
            } else {
                $$1.setResourcePackStatus(ServerPackStatus.DISABLED);
            }
        } else {
            $$1.setResourcePackStatus(ServerPackStatus.PROMPT);
        }
        return $$1;
    }

    @Nullable
    public String getIconB64() {
        return this.iconB64;
    }

    public static String parseFavicon(String $$0) throws ParseException {
        if ($$0.startsWith("data:image/png;base64,")) {
            return $$0.substring("data:image/png;base64,".length());
        }
        throw new ParseException("Unknown format", 0);
    }

    public void setIconB64(@Nullable String $$0) {
        this.iconB64 = $$0;
    }

    public boolean isLan() {
        return this.lan;
    }

    public void setEnforcesSecureChat(boolean $$0) {
        this.enforcesSecureChat = $$0;
    }

    public boolean enforcesSecureChat() {
        return this.enforcesSecureChat;
    }

    public void copyNameIconFrom(ServerData $$0) {
        this.ip = $$0.ip;
        this.name = $$0.name;
        this.iconB64 = $$0.iconB64;
    }

    public void copyFrom(ServerData $$0) {
        this.copyNameIconFrom($$0);
        this.setResourcePackStatus($$0.getResourcePackStatus());
        this.lan = $$0.lan;
        this.enforcesSecureChat = $$0.enforcesSecureChat;
    }

    public static enum ServerPackStatus {
        ENABLED("enabled"),
        DISABLED("disabled"),
        PROMPT("prompt");

        private final Component name;

        private ServerPackStatus(String $$0) {
            this.name = Component.translatable("addServer.resourcePack." + $$0);
        }

        public Component getName() {
            return this.name;
        }
    }
}