/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Date
 *  javax.annotation.Nullable
 */
package net.minecraft.server.players;

import com.google.gson.JsonObject;
import java.util.Date;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.BanListEntry;

public class IpBanListEntry
extends BanListEntry<String> {
    public IpBanListEntry(String $$0) {
        this($$0, (Date)null, (String)null, (Date)null, (String)null);
    }

    public IpBanListEntry(String $$0, @Nullable Date $$1, @Nullable String $$2, @Nullable Date $$3, @Nullable String $$4) {
        super($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal(String.valueOf(this.getUser()));
    }

    public IpBanListEntry(JsonObject $$0) {
        super(IpBanListEntry.createIpInfo($$0), $$0);
    }

    private static String createIpInfo(JsonObject $$0) {
        return $$0.has("ip") ? $$0.get("ip").getAsString() : null;
    }

    @Override
    protected void serialize(JsonObject $$0) {
        if (this.getUser() == null) {
            return;
        }
        $$0.addProperty("ip", (String)this.getUser());
        super.serialize($$0);
    }
}