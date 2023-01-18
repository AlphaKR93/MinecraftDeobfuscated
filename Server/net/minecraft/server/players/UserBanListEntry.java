/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.authlib.GameProfile
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.Date
 *  java.util.Objects
 *  java.util.UUID
 *  javax.annotation.Nullable
 */
package net.minecraft.server.players;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.BanListEntry;

public class UserBanListEntry
extends BanListEntry<GameProfile> {
    public UserBanListEntry(GameProfile $$0) {
        this($$0, (Date)null, (String)null, (Date)null, (String)null);
    }

    public UserBanListEntry(GameProfile $$0, @Nullable Date $$1, @Nullable String $$2, @Nullable Date $$3, @Nullable String $$4) {
        super($$0, $$1, $$2, $$3, $$4);
    }

    public UserBanListEntry(JsonObject $$0) {
        super(UserBanListEntry.createGameProfile($$0), $$0);
    }

    @Override
    protected void serialize(JsonObject $$0) {
        if (this.getUser() == null) {
            return;
        }
        $$0.addProperty("uuid", ((GameProfile)this.getUser()).getId() == null ? "" : ((GameProfile)this.getUser()).getId().toString());
        $$0.addProperty("name", ((GameProfile)this.getUser()).getName());
        super.serialize($$0);
    }

    @Override
    public Component getDisplayName() {
        GameProfile $$0 = (GameProfile)this.getUser();
        return Component.literal($$0.getName() != null ? $$0.getName() : Objects.toString((Object)$$0.getId(), (String)"(Unknown)"));
    }

    /*
     * WARNING - void declaration
     */
    private static GameProfile createGameProfile(JsonObject $$0) {
        void $$4;
        if (!$$0.has("uuid") || !$$0.has("name")) {
            return null;
        }
        String $$1 = $$0.get("uuid").getAsString();
        try {
            UUID $$2 = UUID.fromString((String)$$1);
        }
        catch (Throwable $$3) {
            return null;
        }
        return new GameProfile((UUID)$$4, $$0.get("name").getAsString());
    }
}