/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.net.InetSocketAddress
 *  java.net.Socket
 *  java.net.SocketAddress
 *  java.util.ArrayList
 *  java.util.Comparator
 *  java.util.List
 *  org.apache.commons.io.IOUtils
 */
package com.mojang.realmsclient.client;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.dto.RegionPingResult;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.Util;
import org.apache.commons.io.IOUtils;

public class Ping {
    public static List<RegionPingResult> ping(Region ... $$0) {
        for (Region $$1 : $$0) {
            Ping.ping($$1.endpoint);
        }
        ArrayList $$2 = Lists.newArrayList();
        for (Region $$3 : $$0) {
            $$2.add((Object)new RegionPingResult($$3.name, Ping.ping($$3.endpoint)));
        }
        $$2.sort(Comparator.comparingInt(RegionPingResult::ping));
        return $$2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static int ping(String $$0) {
        int $$1 = 700;
        long $$2 = 0L;
        Socket $$3 = null;
        for (int $$4 = 0; $$4 < 5; ++$$4) {
            try {
                InetSocketAddress $$5 = new InetSocketAddress($$0, 80);
                $$3 = new Socket();
                long $$6 = Ping.now();
                $$3.connect((SocketAddress)$$5, 700);
                $$2 += Ping.now() - $$6;
                IOUtils.closeQuietly((Socket)$$3);
                continue;
            }
            catch (Exception $$7) {
                $$2 += 700L;
                continue;
            }
            finally {
                IOUtils.closeQuietly($$3);
            }
        }
        return (int)((double)$$2 / 5.0);
    }

    private static long now() {
        return Util.getMillis();
    }

    public static List<RegionPingResult> pingAllRegions() {
        return Ping.ping(Region.values());
    }

    static enum Region {
        US_EAST_1("us-east-1", "ec2.us-east-1.amazonaws.com"),
        US_WEST_2("us-west-2", "ec2.us-west-2.amazonaws.com"),
        US_WEST_1("us-west-1", "ec2.us-west-1.amazonaws.com"),
        EU_WEST_1("eu-west-1", "ec2.eu-west-1.amazonaws.com"),
        AP_SOUTHEAST_1("ap-southeast-1", "ec2.ap-southeast-1.amazonaws.com"),
        AP_SOUTHEAST_2("ap-southeast-2", "ec2.ap-southeast-2.amazonaws.com"),
        AP_NORTHEAST_1("ap-northeast-1", "ec2.ap-northeast-1.amazonaws.com"),
        SA_EAST_1("sa-east-1", "ec2.sa-east-1.amazonaws.com");

        final String name;
        final String endpoint;

        private Region(String $$0, String $$1) {
            this.name = $$0;
            this.endpoint = $$1;
        }
    }
}