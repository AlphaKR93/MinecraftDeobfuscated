/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Multimap
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.Collection
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Set
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.atomic.AtomicReference
 *  java.util.function.Consumer
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.joml.Vector3f
 *  org.slf4j.Logger
 *  org.slf4j.Marker
 *  org.slf4j.MarkerFactory
 */
package net.minecraft.client.sounds;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.Library;
import com.mojang.blaze3d.audio.Listener;
import com.mojang.blaze3d.audio.SoundBuffer;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Options;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.client.sounds.SoundEngineExecutor;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class SoundEngine {
    private static final Marker MARKER = MarkerFactory.getMarker((String)"SOUNDS");
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final float PITCH_MIN = 0.5f;
    private static final float PITCH_MAX = 2.0f;
    private static final float VOLUME_MIN = 0.0f;
    private static final float VOLUME_MAX = 1.0f;
    private static final int MIN_SOURCE_LIFETIME = 20;
    private static final Set<ResourceLocation> ONLY_WARN_ONCE = Sets.newHashSet();
    private static final long DEFAULT_DEVICE_CHECK_INTERVAL_MS = 1000L;
    public static final String MISSING_SOUND = "FOR THE DEBUG!";
    public static final String OPEN_AL_SOFT_PREFIX = "OpenAL Soft on ";
    public static final int OPEN_AL_SOFT_PREFIX_LENGTH = "OpenAL Soft on ".length();
    private final SoundManager soundManager;
    private final Options options;
    private boolean loaded;
    private final Library library = new Library();
    private final Listener listener = this.library.getListener();
    private final SoundBufferLibrary soundBuffers;
    private final SoundEngineExecutor executor = new SoundEngineExecutor();
    private final ChannelAccess channelAccess = new ChannelAccess(this.library, this.executor);
    private int tickCount;
    private long lastDeviceCheckTime;
    private final AtomicReference<DeviceCheckState> devicePoolState = new AtomicReference((Object)DeviceCheckState.NO_CHANGE);
    private final Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel = Maps.newHashMap();
    private final Multimap<SoundSource, SoundInstance> instanceBySource = HashMultimap.create();
    private final List<TickableSoundInstance> tickingSounds = Lists.newArrayList();
    private final Map<SoundInstance, Integer> queuedSounds = Maps.newHashMap();
    private final Map<SoundInstance, Integer> soundDeleteTime = Maps.newHashMap();
    private final List<SoundEventListener> listeners = Lists.newArrayList();
    private final List<TickableSoundInstance> queuedTickableSounds = Lists.newArrayList();
    private final List<Sound> preloadQueue = Lists.newArrayList();

    public SoundEngine(SoundManager $$0, Options $$1, ResourceProvider $$2) {
        this.soundManager = $$0;
        this.options = $$1;
        this.soundBuffers = new SoundBufferLibrary($$2);
    }

    public void reload() {
        ONLY_WARN_ONCE.clear();
        for (SoundEvent $$0 : BuiltInRegistries.SOUND_EVENT) {
            ResourceLocation $$1 = $$0.getLocation();
            if (this.soundManager.getSoundEvent($$1) != null) continue;
            LOGGER.warn("Missing sound for event: {}", (Object)BuiltInRegistries.SOUND_EVENT.getKey($$0));
            ONLY_WARN_ONCE.add((Object)$$1);
        }
        this.destroy();
        this.loadLibrary();
    }

    private synchronized void loadLibrary() {
        if (this.loaded) {
            return;
        }
        try {
            String $$0 = this.options.soundDevice().get();
            this.library.init("".equals((Object)$$0) ? null : $$0, this.options.directionalAudio().get());
            this.listener.reset();
            this.listener.setGain(this.options.getSoundSourceVolume(SoundSource.MASTER));
            this.soundBuffers.preload((Collection<Sound>)this.preloadQueue).thenRun(() -> this.preloadQueue.clear());
            this.loaded = true;
            LOGGER.info(MARKER, "Sound engine started");
        }
        catch (RuntimeException $$1) {
            LOGGER.error(MARKER, "Error starting SoundSystem. Turning off sounds & music", (Throwable)$$1);
        }
    }

    private float getVolume(@Nullable SoundSource $$0) {
        if ($$0 == null || $$0 == SoundSource.MASTER) {
            return 1.0f;
        }
        return this.options.getSoundSourceVolume($$0);
    }

    public void updateCategoryVolume(SoundSource $$02, float $$1) {
        if (!this.loaded) {
            return;
        }
        if ($$02 == SoundSource.MASTER) {
            this.listener.setGain($$1);
            return;
        }
        this.instanceToChannel.forEach(($$0, $$12) -> {
            float $$2 = this.calculateVolume((SoundInstance)$$0);
            $$12.execute((Consumer<Channel>)((Consumer)$$1 -> {
                if ($$2 <= 0.0f) {
                    $$1.stop();
                } else {
                    $$1.setVolume($$2);
                }
            }));
        });
    }

    public void destroy() {
        if (this.loaded) {
            this.stopAll();
            this.soundBuffers.clear();
            this.library.cleanup();
            this.loaded = false;
        }
    }

    public void stop(SoundInstance $$0) {
        ChannelAccess.ChannelHandle $$1;
        if (this.loaded && ($$1 = (ChannelAccess.ChannelHandle)this.instanceToChannel.get((Object)$$0)) != null) {
            $$1.execute((Consumer<Channel>)((Consumer)Channel::stop));
        }
    }

    public void stopAll() {
        if (this.loaded) {
            this.executor.flush();
            this.instanceToChannel.values().forEach($$0 -> $$0.execute((Consumer<Channel>)((Consumer)Channel::stop)));
            this.instanceToChannel.clear();
            this.channelAccess.clear();
            this.queuedSounds.clear();
            this.tickingSounds.clear();
            this.instanceBySource.clear();
            this.soundDeleteTime.clear();
            this.queuedTickableSounds.clear();
        }
    }

    public void addEventListener(SoundEventListener $$0) {
        this.listeners.add((Object)$$0);
    }

    public void removeEventListener(SoundEventListener $$0) {
        this.listeners.remove((Object)$$0);
    }

    private boolean shouldChangeDevice() {
        boolean $$1;
        if (this.library.isCurrentDeviceDisconnected()) {
            LOGGER.info("Audio device was lost!");
            return true;
        }
        long $$0 = Util.getMillis();
        boolean bl = $$1 = $$0 - this.lastDeviceCheckTime >= 1000L;
        if ($$1) {
            this.lastDeviceCheckTime = $$0;
            if (this.devicePoolState.compareAndSet((Object)DeviceCheckState.NO_CHANGE, (Object)DeviceCheckState.ONGOING)) {
                String $$2 = this.options.soundDevice().get();
                Util.ioPool().execute(() -> {
                    if ("".equals((Object)$$2)) {
                        if (this.library.hasDefaultDeviceChanged()) {
                            LOGGER.info("System default audio device has changed!");
                            this.devicePoolState.compareAndSet((Object)DeviceCheckState.ONGOING, (Object)DeviceCheckState.CHANGE_DETECTED);
                        }
                    } else if (!this.library.getCurrentDeviceName().equals((Object)$$2) && this.library.getAvailableSoundDevices().contains((Object)$$2)) {
                        LOGGER.info("Preferred audio device has become available!");
                        this.devicePoolState.compareAndSet((Object)DeviceCheckState.ONGOING, (Object)DeviceCheckState.CHANGE_DETECTED);
                    }
                    this.devicePoolState.compareAndSet((Object)DeviceCheckState.ONGOING, (Object)DeviceCheckState.NO_CHANGE);
                });
            }
        }
        return this.devicePoolState.compareAndSet((Object)DeviceCheckState.CHANGE_DETECTED, (Object)DeviceCheckState.NO_CHANGE);
    }

    public void tick(boolean $$0) {
        if (this.shouldChangeDevice()) {
            this.reload();
        }
        if (!$$0) {
            this.tickNonPaused();
        }
        this.channelAccess.scheduleTick();
    }

    private void tickNonPaused() {
        ++this.tickCount;
        this.queuedTickableSounds.stream().filter(SoundInstance::canPlaySound).forEach(this::play);
        this.queuedTickableSounds.clear();
        for (TickableSoundInstance $$0 : this.tickingSounds) {
            if (!$$0.canPlaySound()) {
                this.stop($$0);
            }
            $$0.tick();
            if ($$0.isStopped()) {
                this.stop($$0);
                continue;
            }
            float $$1 = this.calculateVolume($$0);
            float $$2 = this.calculatePitch($$0);
            Vec3 $$32 = new Vec3($$0.getX(), $$0.getY(), $$0.getZ());
            ChannelAccess.ChannelHandle $$4 = (ChannelAccess.ChannelHandle)this.instanceToChannel.get((Object)$$0);
            if ($$4 == null) continue;
            $$4.execute((Consumer<Channel>)((Consumer)$$3 -> {
                $$3.setVolume($$1);
                $$3.setPitch($$2);
                $$3.setSelfPosition($$32);
            }));
        }
        Iterator $$5 = this.instanceToChannel.entrySet().iterator();
        while ($$5.hasNext()) {
            int $$10;
            Map.Entry $$6 = (Map.Entry)$$5.next();
            ChannelAccess.ChannelHandle $$7 = (ChannelAccess.ChannelHandle)$$6.getValue();
            SoundInstance $$8 = (SoundInstance)$$6.getKey();
            float $$9 = this.options.getSoundSourceVolume($$8.getSource());
            if ($$9 <= 0.0f) {
                $$7.execute((Consumer<Channel>)((Consumer)Channel::stop));
                $$5.remove();
                continue;
            }
            if (!$$7.isStopped() || ($$10 = ((Integer)this.soundDeleteTime.get((Object)$$8)).intValue()) > this.tickCount) continue;
            if (SoundEngine.shouldLoopManually($$8)) {
                this.queuedSounds.put((Object)$$8, (Object)(this.tickCount + $$8.getDelay()));
            }
            $$5.remove();
            LOGGER.debug(MARKER, "Removed channel {} because it's not playing anymore", (Object)$$7);
            this.soundDeleteTime.remove((Object)$$8);
            try {
                this.instanceBySource.remove((Object)$$8.getSource(), (Object)$$8);
            }
            catch (RuntimeException runtimeException) {
                // empty catch block
            }
            if (!($$8 instanceof TickableSoundInstance)) continue;
            this.tickingSounds.remove((Object)$$8);
        }
        Iterator $$11 = this.queuedSounds.entrySet().iterator();
        while ($$11.hasNext()) {
            Map.Entry $$12 = (Map.Entry)$$11.next();
            if (this.tickCount < (Integer)$$12.getValue()) continue;
            SoundInstance $$13 = (SoundInstance)$$12.getKey();
            if ($$13 instanceof TickableSoundInstance) {
                ((TickableSoundInstance)$$13).tick();
            }
            this.play($$13);
            $$11.remove();
        }
    }

    private static boolean requiresManualLooping(SoundInstance $$0) {
        return $$0.getDelay() > 0;
    }

    private static boolean shouldLoopManually(SoundInstance $$0) {
        return $$0.isLooping() && SoundEngine.requiresManualLooping($$0);
    }

    private static boolean shouldLoopAutomatically(SoundInstance $$0) {
        return $$0.isLooping() && !SoundEngine.requiresManualLooping($$0);
    }

    public boolean isActive(SoundInstance $$0) {
        if (!this.loaded) {
            return false;
        }
        if (this.soundDeleteTime.containsKey((Object)$$0) && (Integer)this.soundDeleteTime.get((Object)$$0) <= this.tickCount) {
            return true;
        }
        return this.instanceToChannel.containsKey((Object)$$0);
    }

    public void play(SoundInstance $$0) {
        if (!this.loaded) {
            return;
        }
        if (!$$0.canPlaySound()) {
            return;
        }
        WeighedSoundEvents $$1 = $$0.resolve(this.soundManager);
        ResourceLocation $$2 = $$0.getLocation();
        if ($$1 == null) {
            if (ONLY_WARN_ONCE.add((Object)$$2)) {
                LOGGER.warn(MARKER, "Unable to play unknown soundEvent: {}", (Object)$$2);
            }
            return;
        }
        Sound $$3 = $$0.getSound();
        if ($$3 == SoundManager.EMPTY_SOUND) {
            if (ONLY_WARN_ONCE.add((Object)$$2)) {
                LOGGER.warn(MARKER, "Unable to play empty soundEvent: {}", (Object)$$2);
            }
            return;
        }
        float $$4 = $$0.getVolume();
        float $$5 = Math.max((float)$$4, (float)1.0f) * (float)$$3.getAttenuationDistance();
        SoundSource $$6 = $$0.getSource();
        float $$7 = this.calculateVolume($$4, $$6);
        float $$82 = this.calculatePitch($$0);
        SoundInstance.Attenuation $$9 = $$0.getAttenuation();
        boolean $$10 = $$0.isRelative();
        if ($$7 == 0.0f && !$$0.canStartSilent()) {
            LOGGER.debug(MARKER, "Skipped playing sound {}, volume was zero.", (Object)$$3.getLocation());
            return;
        }
        Vec3 $$11 = new Vec3($$0.getX(), $$0.getY(), $$0.getZ());
        if (!this.listeners.isEmpty()) {
            boolean $$122;
            boolean bl = $$122 = $$10 || $$9 == SoundInstance.Attenuation.NONE || this.listener.getListenerPosition().distanceToSqr($$11) < (double)($$5 * $$5);
            if ($$122) {
                for (SoundEventListener $$13 : this.listeners) {
                    $$13.onPlaySound($$0, $$1);
                }
            } else {
                LOGGER.debug(MARKER, "Did not notify listeners of soundEvent: {}, it is too far away to hear", (Object)$$2);
            }
        }
        if (this.listener.getGain() <= 0.0f) {
            LOGGER.debug(MARKER, "Skipped playing soundEvent: {}, master volume was zero", (Object)$$2);
            return;
        }
        boolean $$14 = SoundEngine.shouldLoopAutomatically($$0);
        boolean $$15 = $$3.shouldStream();
        CompletableFuture<ChannelAccess.ChannelHandle> $$16 = this.channelAccess.createHandle($$3.shouldStream() ? Library.Pool.STREAMING : Library.Pool.STATIC);
        ChannelAccess.ChannelHandle $$17 = (ChannelAccess.ChannelHandle)$$16.join();
        if ($$17 == null) {
            if (SharedConstants.IS_RUNNING_IN_IDE) {
                LOGGER.warn("Failed to create new sound handle");
            }
            return;
        }
        LOGGER.debug(MARKER, "Playing sound {} for event {}", (Object)$$3.getLocation(), (Object)$$2);
        this.soundDeleteTime.put((Object)$$0, (Object)(this.tickCount + 20));
        this.instanceToChannel.put((Object)$$0, (Object)$$17);
        this.instanceBySource.put((Object)$$6, (Object)$$0);
        $$17.execute((Consumer<Channel>)((Consumer)$$8 -> {
            $$8.setPitch($$82);
            $$8.setVolume($$7);
            if ($$9 == SoundInstance.Attenuation.LINEAR) {
                $$8.linearAttenuation($$5);
            } else {
                $$8.disableAttenuation();
            }
            $$8.setLooping($$14 && !$$15);
            $$8.setSelfPosition($$11);
            $$8.setRelative($$10);
        }));
        if (!$$15) {
            this.soundBuffers.getCompleteBuffer($$3.getPath()).thenAccept($$12 -> $$17.execute((Consumer<Channel>)((Consumer)$$1 -> {
                $$1.attachStaticBuffer((SoundBuffer)$$12);
                $$1.play();
            })));
        } else {
            this.soundBuffers.getStream($$3.getPath(), $$14).thenAccept($$12 -> $$17.execute((Consumer<Channel>)((Consumer)$$1 -> {
                $$1.attachBufferStream((AudioStream)$$12);
                $$1.play();
            })));
        }
        if ($$0 instanceof TickableSoundInstance) {
            this.tickingSounds.add((Object)((TickableSoundInstance)$$0));
        }
    }

    public void queueTickingSound(TickableSoundInstance $$0) {
        this.queuedTickableSounds.add((Object)$$0);
    }

    public void requestPreload(Sound $$0) {
        this.preloadQueue.add((Object)$$0);
    }

    private float calculatePitch(SoundInstance $$0) {
        return Mth.clamp($$0.getPitch(), 0.5f, 2.0f);
    }

    private float calculateVolume(SoundInstance $$0) {
        return this.calculateVolume($$0.getVolume(), $$0.getSource());
    }

    private float calculateVolume(float $$0, SoundSource $$1) {
        return Mth.clamp($$0 * this.getVolume($$1), 0.0f, 1.0f);
    }

    public void pause() {
        if (this.loaded) {
            this.channelAccess.executeOnChannels((Consumer<Stream<Channel>>)((Consumer)$$0 -> $$0.forEach(Channel::pause)));
        }
    }

    public void resume() {
        if (this.loaded) {
            this.channelAccess.executeOnChannels((Consumer<Stream<Channel>>)((Consumer)$$0 -> $$0.forEach(Channel::unpause)));
        }
    }

    public void playDelayed(SoundInstance $$0, int $$1) {
        this.queuedSounds.put((Object)$$0, (Object)(this.tickCount + $$1));
    }

    public void updateSource(Camera $$0) {
        if (!this.loaded || !$$0.isInitialized()) {
            return;
        }
        Vec3 $$1 = $$0.getPosition();
        Vector3f $$2 = $$0.getLookVector();
        Vector3f $$3 = $$0.getUpVector();
        this.executor.execute(() -> {
            this.listener.setListenerPosition($$1);
            this.listener.setListenerOrientation($$2, $$3);
        });
    }

    public void stop(@Nullable ResourceLocation $$0, @Nullable SoundSource $$1) {
        if ($$1 != null) {
            for (SoundInstance $$2 : this.instanceBySource.get((Object)$$1)) {
                if ($$0 != null && !$$2.getLocation().equals($$0)) continue;
                this.stop($$2);
            }
        } else if ($$0 == null) {
            this.stopAll();
        } else {
            for (SoundInstance $$3 : this.instanceToChannel.keySet()) {
                if (!$$3.getLocation().equals($$0)) continue;
                this.stop($$3);
            }
        }
    }

    public String getDebugString() {
        return this.library.getDebugString();
    }

    public List<String> getAvailableSoundDevices() {
        return this.library.getAvailableSoundDevices();
    }

    static enum DeviceCheckState {
        ONGOING,
        CHANGE_DETECTED,
        NO_CHANGE;

    }
}