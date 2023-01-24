/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.yggdrasil.request.AbuseReportRequest$ClientInfo
 *  com.mojang.authlib.yggdrasil.request.AbuseReportRequest$RealmInfo
 *  com.mojang.authlib.yggdrasil.request.AbuseReportRequest$ThirdPartyServerInfo
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  javax.annotation.Nullable
 */
package net.minecraft.client.multiplayer.chat.report;

import com.mojang.authlib.yggdrasil.request.AbuseReportRequest;
import com.mojang.realmsclient.dto.RealmsServer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;

public record ReportEnvironment(String clientVersion, @Nullable Server server) {
    public static ReportEnvironment local() {
        return ReportEnvironment.create(null);
    }

    public static ReportEnvironment thirdParty(String $$0) {
        return ReportEnvironment.create(new Server.ThirdParty($$0));
    }

    public static ReportEnvironment realm(RealmsServer $$0) {
        return ReportEnvironment.create(new Server.Realm($$0));
    }

    public static ReportEnvironment create(@Nullable Server $$0) {
        return new ReportEnvironment(ReportEnvironment.getClientVersion(), $$0);
    }

    public AbuseReportRequest.ClientInfo clientInfo() {
        return new AbuseReportRequest.ClientInfo(this.clientVersion);
    }

    @Nullable
    public AbuseReportRequest.ThirdPartyServerInfo thirdPartyServerInfo() {
        Server server = this.server;
        if (server instanceof Server.ThirdParty) {
            Server.ThirdParty $$0 = (Server.ThirdParty)server;
            return new AbuseReportRequest.ThirdPartyServerInfo($$0.ip);
        }
        return null;
    }

    @Nullable
    public AbuseReportRequest.RealmInfo realmInfo() {
        Server server = this.server;
        if (server instanceof Server.Realm) {
            Server.Realm $$0 = (Server.Realm)server;
            return new AbuseReportRequest.RealmInfo(String.valueOf((long)$$0.realmId()), $$0.slotId());
        }
        return null;
    }

    private static String getClientVersion() {
        StringBuilder $$0 = new StringBuilder();
        $$0.append("23w04a");
        if (Minecraft.checkModStatus().shouldReportAsModified()) {
            $$0.append(" (modded)");
        }
        return $$0.toString();
    }

    public static interface Server {

        public record Realm(long realmId, int slotId) implements Server
        {
            public Realm(RealmsServer $$0) {
                this($$0.id, $$0.activeSlot);
            }
        }

        public record ThirdParty(String ip) implements Server
        {
        }
    }
}