/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  java.io.File
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.net.SocketAddress
 *  javax.annotation.Nullable
 */
package net.minecraft.server.players;

import com.google.gson.JsonObject;
import java.io.File;
import java.net.SocketAddress;
import javax.annotation.Nullable;
import net.minecraft.server.players.IpBanListEntry;
import net.minecraft.server.players.StoredUserEntry;
import net.minecraft.server.players.StoredUserList;

public class IpBanList
extends StoredUserList<String, IpBanListEntry> {
    public IpBanList(File $$0) {
        super($$0);
    }

    @Override
    protected StoredUserEntry<String> createEntry(JsonObject $$0) {
        return new IpBanListEntry($$0);
    }

    public boolean isBanned(SocketAddress $$0) {
        String $$1 = this.getIpFromAddress($$0);
        return this.contains($$1);
    }

    public boolean isBanned(String $$0) {
        return this.contains($$0);
    }

    @Override
    @Nullable
    public IpBanListEntry get(SocketAddress $$0) {
        String $$1 = this.getIpFromAddress($$0);
        return (IpBanListEntry)this.get($$1);
    }

    private String getIpFromAddress(SocketAddress $$0) {
        String $$1 = $$0.toString();
        if ($$1.contains((CharSequence)"/")) {
            $$1 = $$1.substring($$1.indexOf(47) + 1);
        }
        if ($$1.contains((CharSequence)":")) {
            $$1 = $$1.substring(0, $$1.indexOf(58));
        }
        return $$1;
    }
}