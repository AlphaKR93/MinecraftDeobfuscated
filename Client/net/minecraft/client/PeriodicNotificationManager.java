/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.math.LongMath
 *  com.google.gson.JsonParser
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.objects.Object2BooleanFunction
 *  java.io.BufferedReader
 *  java.io.Reader
 *  java.lang.AutoCloseable
 *  java.lang.Boolean
 *  java.lang.Exception
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.Collection
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Timer
 *  java.util.TimerTask
 *  java.util.concurrent.TimeUnit
 *  java.util.concurrent.atomic.AtomicLong
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.math.LongMath;
import com.google.gson.JsonParser;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public class PeriodicNotificationManager
extends SimplePreparableReloadListener<Map<String, List<Notification>>>
implements AutoCloseable {
    private static final Codec<Map<String, List<Notification>>> CODEC = Codec.unboundedMap((Codec)Codec.STRING, (Codec)RecordCodecBuilder.create($$0 -> $$0.group((App)Codec.LONG.optionalFieldOf("delay", (Object)0L).forGetter(Notification::delay), (App)Codec.LONG.fieldOf("period").forGetter(Notification::period), (App)Codec.STRING.fieldOf("title").forGetter(Notification::title), (App)Codec.STRING.fieldOf("message").forGetter(Notification::message)).apply((Applicative)$$0, Notification::new)).listOf());
    private static final Logger LOGGER = LogUtils.getLogger();
    private final ResourceLocation notifications;
    private final Object2BooleanFunction<String> selector;
    @Nullable
    private Timer timer;
    @Nullable
    private NotificationTask notificationTask;

    public PeriodicNotificationManager(ResourceLocation $$0, Object2BooleanFunction<String> $$1) {
        this.notifications = $$0;
        this.selector = $$1;
    }

    @Override
    protected Map<String, List<Notification>> prepare(ResourceManager $$0, ProfilerFiller $$1) {
        Map map;
        block8: {
            BufferedReader $$2 = $$0.openAsReader(this.notifications);
            try {
                map = (Map)CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)JsonParser.parseReader((Reader)$$2)).result().orElseThrow();
                if ($$2 == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if ($$2 != null) {
                        try {
                            $$2.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (Exception $$3) {
                    LOGGER.warn("Failed to load {}", (Object)this.notifications, (Object)$$3);
                    return ImmutableMap.of();
                }
            }
            $$2.close();
        }
        return map;
    }

    @Override
    protected void apply(Map<String, List<Notification>> $$02, ResourceManager $$1, ProfilerFiller $$2) {
        List $$3 = (List)$$02.entrySet().stream().filter($$0 -> (Boolean)this.selector.apply((Object)((String)$$0.getKey()))).map(Map.Entry::getValue).flatMap(Collection::stream).collect(Collectors.toList());
        if ($$3.isEmpty()) {
            this.stopTimer();
            return;
        }
        if ($$3.stream().anyMatch($$0 -> $$0.period == 0L)) {
            Util.logAndPauseIfInIde("A periodic notification in " + this.notifications + " has a period of zero minutes");
            this.stopTimer();
            return;
        }
        long $$4 = this.calculateInitialDelay((List<Notification>)$$3);
        long $$5 = this.calculateOptimalPeriod((List<Notification>)$$3, $$4);
        if (this.timer == null) {
            this.timer = new Timer();
        }
        this.notificationTask = this.notificationTask == null ? new NotificationTask((List<Notification>)$$3, $$4, $$5) : this.notificationTask.reset((List<Notification>)$$3, $$5);
        this.timer.scheduleAtFixedRate((TimerTask)this.notificationTask, TimeUnit.MINUTES.toMillis($$4), TimeUnit.MINUTES.toMillis($$5));
    }

    public void close() {
        this.stopTimer();
    }

    private void stopTimer() {
        if (this.timer != null) {
            this.timer.cancel();
        }
    }

    private long calculateOptimalPeriod(List<Notification> $$0, long $$12) {
        return $$0.stream().mapToLong($$1 -> {
            long $$2 = $$1.delay - $$12;
            return LongMath.gcd((long)$$2, (long)$$1.period);
        }).reduce(LongMath::gcd).orElseThrow(() -> new IllegalStateException("Empty notifications from: " + this.notifications));
    }

    private long calculateInitialDelay(List<Notification> $$02) {
        return $$02.stream().mapToLong($$0 -> $$0.delay).min().orElse(0L);
    }

    static class NotificationTask
    extends TimerTask {
        private final Minecraft minecraft = Minecraft.getInstance();
        private final List<Notification> notifications;
        private final long period;
        private final AtomicLong elapsed;

        public NotificationTask(List<Notification> $$0, long $$1, long $$2) {
            this.notifications = $$0;
            this.period = $$2;
            this.elapsed = new AtomicLong($$1);
        }

        public NotificationTask reset(List<Notification> $$0, long $$1) {
            this.cancel();
            return new NotificationTask($$0, this.elapsed.get(), $$1);
        }

        public void run() {
            long $$0 = this.elapsed.getAndAdd(this.period);
            long $$1 = this.elapsed.get();
            for (Notification $$2 : this.notifications) {
                long $$4;
                long $$3;
                if ($$0 < $$2.delay || ($$3 = $$0 / $$2.period) == ($$4 = $$1 / $$2.period)) continue;
                this.minecraft.execute(() -> SystemToast.add(Minecraft.getInstance().getToasts(), SystemToast.SystemToastIds.PERIODIC_NOTIFICATION, Component.translatable($$0.title, $$3), Component.translatable($$0.message, $$3)));
                return;
            }
        }
    }

    public record Notification(long delay, long period, String title, String message) {
        public Notification(long $$0, long $$1, String $$2, String $$3) {
            this.delay = $$0 != 0L ? $$0 : $$1;
            this.period = $$1;
            this.title = $$2;
            this.message = $$3;
        }
    }
}