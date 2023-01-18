/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Maps
 *  com.mojang.logging.LogUtils
 *  java.lang.Class
 *  java.lang.Exception
 *  java.lang.Object
 *  java.util.Map
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.timers;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.timers.FunctionCallback;
import net.minecraft.world.level.timers.FunctionTagCallback;
import net.minecraft.world.level.timers.TimerCallback;
import org.slf4j.Logger;

public class TimerCallbacks<C> {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final TimerCallbacks<MinecraftServer> SERVER_CALLBACKS = new TimerCallbacks<MinecraftServer>().register(new FunctionCallback.Serializer()).register(new FunctionTagCallback.Serializer());
    private final Map<ResourceLocation, TimerCallback.Serializer<C, ?>> idToSerializer = Maps.newHashMap();
    private final Map<Class<?>, TimerCallback.Serializer<C, ?>> classToSerializer = Maps.newHashMap();

    @VisibleForTesting
    public TimerCallbacks() {
    }

    public TimerCallbacks<C> register(TimerCallback.Serializer<C, ?> $$0) {
        this.idToSerializer.put((Object)$$0.getId(), $$0);
        this.classToSerializer.put($$0.getCls(), $$0);
        return this;
    }

    private <T extends TimerCallback<C>> TimerCallback.Serializer<C, T> getSerializer(Class<?> $$0) {
        return (TimerCallback.Serializer)this.classToSerializer.get($$0);
    }

    public <T extends TimerCallback<C>> CompoundTag serialize(T $$0) {
        TimerCallback.Serializer<T, T> $$1 = this.getSerializer($$0.getClass());
        CompoundTag $$2 = new CompoundTag();
        $$1.serialize($$2, $$0);
        $$2.putString("Type", $$1.getId().toString());
        return $$2;
    }

    @Nullable
    public TimerCallback<C> deserialize(CompoundTag $$0) {
        ResourceLocation $$1 = ResourceLocation.tryParse($$0.getString("Type"));
        TimerCallback.Serializer $$2 = (TimerCallback.Serializer)this.idToSerializer.get((Object)$$1);
        if ($$2 == null) {
            LOGGER.error("Failed to deserialize timer callback: {}", (Object)$$0);
            return null;
        }
        try {
            return $$2.deserialize($$0);
        }
        catch (Exception $$3) {
            LOGGER.error("Failed to deserialize timer callback: {}", (Object)$$0, (Object)$$3);
            return null;
        }
    }
}