/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  java.io.File
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.List
 *  java.util.concurrent.Executor
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.util.thread.ProcessorMailbox;
import org.slf4j.Logger;

public class ServerList {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ProcessorMailbox<Runnable> IO_MAILBOX = ProcessorMailbox.create((Executor)Util.backgroundExecutor(), "server-list-io");
    private static final int MAX_HIDDEN_SERVERS = 16;
    private final Minecraft minecraft;
    private final List<ServerData> serverList = Lists.newArrayList();
    private final List<ServerData> hiddenServerList = Lists.newArrayList();

    public ServerList(Minecraft $$0) {
        this.minecraft = $$0;
        this.load();
    }

    public void load() {
        try {
            this.serverList.clear();
            this.hiddenServerList.clear();
            CompoundTag $$0 = NbtIo.read(new File(this.minecraft.gameDirectory, "servers.dat"));
            if ($$0 == null) {
                return;
            }
            ListTag $$1 = $$0.getList("servers", 10);
            for (int $$2 = 0; $$2 < $$1.size(); ++$$2) {
                CompoundTag $$3 = $$1.getCompound($$2);
                ServerData $$4 = ServerData.read($$3);
                if ($$3.getBoolean("hidden")) {
                    this.hiddenServerList.add((Object)$$4);
                    continue;
                }
                this.serverList.add((Object)$$4);
            }
        }
        catch (Exception $$5) {
            LOGGER.error("Couldn't load server list", (Throwable)$$5);
        }
    }

    public void save() {
        try {
            ListTag $$0 = new ListTag();
            for (ServerData $$1 : this.serverList) {
                CompoundTag $$2 = $$1.write();
                $$2.putBoolean("hidden", false);
                $$0.add($$2);
            }
            for (ServerData $$3 : this.hiddenServerList) {
                CompoundTag $$4 = $$3.write();
                $$4.putBoolean("hidden", true);
                $$0.add($$4);
            }
            CompoundTag $$5 = new CompoundTag();
            $$5.put("servers", $$0);
            File $$6 = File.createTempFile((String)"servers", (String)".dat", (File)this.minecraft.gameDirectory);
            NbtIo.write($$5, $$6);
            File $$7 = new File(this.minecraft.gameDirectory, "servers.dat_old");
            File $$8 = new File(this.minecraft.gameDirectory, "servers.dat");
            Util.safeReplaceFile($$8, $$6, $$7);
        }
        catch (Exception $$9) {
            LOGGER.error("Couldn't save server list", (Throwable)$$9);
        }
    }

    public ServerData get(int $$0) {
        return (ServerData)this.serverList.get($$0);
    }

    @Nullable
    public ServerData get(String $$0) {
        for (ServerData $$1 : this.serverList) {
            if (!$$1.ip.equals((Object)$$0)) continue;
            return $$1;
        }
        for (ServerData $$2 : this.hiddenServerList) {
            if (!$$2.ip.equals((Object)$$0)) continue;
            return $$2;
        }
        return null;
    }

    @Nullable
    public ServerData unhide(String $$0) {
        for (int $$1 = 0; $$1 < this.hiddenServerList.size(); ++$$1) {
            ServerData $$2 = (ServerData)this.hiddenServerList.get($$1);
            if (!$$2.ip.equals((Object)$$0)) continue;
            this.hiddenServerList.remove($$1);
            this.serverList.add((Object)$$2);
            return $$2;
        }
        return null;
    }

    public void remove(ServerData $$0) {
        if (!this.serverList.remove((Object)$$0)) {
            this.hiddenServerList.remove((Object)$$0);
        }
    }

    public void add(ServerData $$0, boolean $$1) {
        if ($$1) {
            this.hiddenServerList.add(0, (Object)$$0);
            while (this.hiddenServerList.size() > 16) {
                this.hiddenServerList.remove(this.hiddenServerList.size() - 1);
            }
        } else {
            this.serverList.add((Object)$$0);
        }
    }

    public int size() {
        return this.serverList.size();
    }

    public void swap(int $$0, int $$1) {
        ServerData $$2 = this.get($$0);
        this.serverList.set($$0, (Object)this.get($$1));
        this.serverList.set($$1, (Object)$$2);
        this.save();
    }

    public void replace(int $$0, ServerData $$1) {
        this.serverList.set($$0, (Object)$$1);
    }

    private static boolean set(ServerData $$0, List<ServerData> $$1) {
        for (int $$2 = 0; $$2 < $$1.size(); ++$$2) {
            ServerData $$3 = (ServerData)$$1.get($$2);
            if (!$$3.name.equals((Object)$$0.name) || !$$3.ip.equals((Object)$$0.ip)) continue;
            $$1.set($$2, (Object)$$0);
            return true;
        }
        return false;
    }

    public static void saveSingleServer(ServerData $$0) {
        IO_MAILBOX.tell(() -> {
            ServerList $$1 = new ServerList(Minecraft.getInstance());
            $$1.load();
            if (!ServerList.set($$0, $$1.serverList)) {
                ServerList.set($$0, $$1.hiddenServerList);
            }
            $$1.save();
        });
    }
}