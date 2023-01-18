/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collection
 *  java.util.Map
 *  javax.annotation.Nullable
 */
package net.minecraft.server.bossevents;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.level.ServerPlayer;

public class CustomBossEvents {
    private final Map<ResourceLocation, CustomBossEvent> events = Maps.newHashMap();

    @Nullable
    public CustomBossEvent get(ResourceLocation $$0) {
        return (CustomBossEvent)this.events.get((Object)$$0);
    }

    public CustomBossEvent create(ResourceLocation $$0, Component $$1) {
        CustomBossEvent $$2 = new CustomBossEvent($$0, $$1);
        this.events.put((Object)$$0, (Object)$$2);
        return $$2;
    }

    public void remove(CustomBossEvent $$0) {
        this.events.remove((Object)$$0.getTextId());
    }

    public Collection<ResourceLocation> getIds() {
        return this.events.keySet();
    }

    public Collection<CustomBossEvent> getEvents() {
        return this.events.values();
    }

    public CompoundTag save() {
        CompoundTag $$0 = new CompoundTag();
        for (CustomBossEvent $$1 : this.events.values()) {
            $$0.put($$1.getTextId().toString(), $$1.save());
        }
        return $$0;
    }

    public void load(CompoundTag $$0) {
        for (String $$1 : $$0.getAllKeys()) {
            ResourceLocation $$2 = new ResourceLocation($$1);
            this.events.put((Object)$$2, (Object)CustomBossEvent.load($$0.getCompound($$1), $$2));
        }
    }

    public void onPlayerConnect(ServerPlayer $$0) {
        for (CustomBossEvent $$1 : this.events.values()) {
            $$1.onPlayerConnect($$0);
        }
    }

    public void onPlayerDisconnect(ServerPlayer $$0) {
        for (CustomBossEvent $$1 : this.events.values()) {
            $$1.onPlayerDisconnect($$0);
        }
    }
}