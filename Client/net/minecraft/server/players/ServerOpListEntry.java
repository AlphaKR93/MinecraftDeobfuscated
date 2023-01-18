/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.authlib.GameProfile
 *  java.lang.Boolean
 *  java.lang.Integer
 *  java.lang.Number
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.UUID
 *  javax.annotation.Nullable
 */
package net.minecraft.server.players;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.server.players.StoredUserEntry;

public class ServerOpListEntry
extends StoredUserEntry<GameProfile> {
    private final int level;
    private final boolean bypassesPlayerLimit;

    public ServerOpListEntry(GameProfile $$0, int $$1, boolean $$2) {
        super($$0);
        this.level = $$1;
        this.bypassesPlayerLimit = $$2;
    }

    public ServerOpListEntry(JsonObject $$0) {
        super(ServerOpListEntry.createGameProfile($$0));
        this.level = $$0.has("level") ? $$0.get("level").getAsInt() : 0;
        this.bypassesPlayerLimit = $$0.has("bypassesPlayerLimit") && $$0.get("bypassesPlayerLimit").getAsBoolean();
    }

    public int getLevel() {
        return this.level;
    }

    public boolean getBypassesPlayerLimit() {
        return this.bypassesPlayerLimit;
    }

    @Override
    protected void serialize(JsonObject $$0) {
        if (this.getUser() == null) {
            return;
        }
        $$0.addProperty("uuid", ((GameProfile)this.getUser()).getId() == null ? "" : ((GameProfile)this.getUser()).getId().toString());
        $$0.addProperty("name", ((GameProfile)this.getUser()).getName());
        $$0.addProperty("level", (Number)Integer.valueOf((int)this.level));
        $$0.addProperty("bypassesPlayerLimit", Boolean.valueOf((boolean)this.bypassesPlayerLimit));
    }

    /*
     * WARNING - void declaration
     */
    @Nullable
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