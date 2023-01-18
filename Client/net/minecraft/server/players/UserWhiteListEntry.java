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
 *  java.util.UUID
 */
package net.minecraft.server.players;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.server.players.StoredUserEntry;

public class UserWhiteListEntry
extends StoredUserEntry<GameProfile> {
    public UserWhiteListEntry(GameProfile $$0) {
        super($$0);
    }

    public UserWhiteListEntry(JsonObject $$0) {
        super(UserWhiteListEntry.createGameProfile($$0));
    }

    @Override
    protected void serialize(JsonObject $$0) {
        if (this.getUser() == null) {
            return;
        }
        $$0.addProperty("uuid", ((GameProfile)this.getUser()).getId() == null ? "" : ((GameProfile)this.getUser()).getId().toString());
        $$0.addProperty("name", ((GameProfile)this.getUser()).getName());
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