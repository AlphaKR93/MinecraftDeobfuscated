/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.authlib.GameProfile
 *  java.io.File
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Objects
 */
package net.minecraft.server.players;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.util.Objects;
import net.minecraft.server.players.ServerOpListEntry;
import net.minecraft.server.players.StoredUserEntry;
import net.minecraft.server.players.StoredUserList;

public class ServerOpList
extends StoredUserList<GameProfile, ServerOpListEntry> {
    public ServerOpList(File $$0) {
        super($$0);
    }

    @Override
    protected StoredUserEntry<GameProfile> createEntry(JsonObject $$0) {
        return new ServerOpListEntry($$0);
    }

    @Override
    public String[] getUserList() {
        return (String[])this.getEntries().stream().map(StoredUserEntry::getUser).filter(Objects::nonNull).map(GameProfile::getName).toArray(String[]::new);
    }

    public boolean canBypassPlayerLimit(GameProfile $$0) {
        ServerOpListEntry $$1 = (ServerOpListEntry)this.get($$0);
        if ($$1 != null) {
            return $$1.getBypassesPlayerLimit();
        }
        return false;
    }

    @Override
    protected String getKeyForUser(GameProfile $$0) {
        return $$0.getId().toString();
    }
}