/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  java.lang.Object
 *  java.util.ArrayList
 *  java.util.Comparator
 *  java.util.List
 *  java.util.Set
 *  net.minecraft.client.Minecraft
 */
package com.mojang.realmsclient.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.realmsclient.dto.RealmsServer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import net.minecraft.client.Minecraft;

public class RealmsServerList {
    private final Minecraft minecraft;
    private final Set<RealmsServer> removedServers = Sets.newHashSet();
    private List<RealmsServer> servers = Lists.newArrayList();

    public RealmsServerList(Minecraft $$0) {
        this.minecraft = $$0;
    }

    public List<RealmsServer> updateServersList(List<RealmsServer> $$0) {
        ArrayList $$1 = new ArrayList($$0);
        $$1.sort((Comparator)new RealmsServer.McoServerComparator(this.minecraft.getUser().getName()));
        boolean $$2 = $$1.removeAll(this.removedServers);
        if (!$$2) {
            this.removedServers.clear();
        }
        this.servers = $$1;
        return List.copyOf(this.servers);
    }

    public synchronized List<RealmsServer> removeItem(RealmsServer $$0) {
        this.servers.remove((Object)$$0);
        this.removedServers.add((Object)$$0);
        return List.copyOf(this.servers);
    }
}