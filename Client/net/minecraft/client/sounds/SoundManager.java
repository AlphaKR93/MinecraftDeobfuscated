/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.reflect.TypeToken
 *  com.mojang.logging.LogUtils
 *  java.io.BufferedReader
 *  java.io.IOException
 *  java.io.Reader
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.util.Collection
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.sounds;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.client.Camera;
import net.minecraft.client.Options;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundEventRegistration;
import net.minecraft.client.resources.sounds.SoundEventRegistrationSerializer;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundEventListener;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.client.sounds.Weighted;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.MultipliedFloats;
import org.slf4j.Logger;

public class SoundManager
extends SimplePreparableReloadListener<Preparations> {
    public static final Sound EMPTY_SOUND = new Sound("meta:missing_sound", ConstantFloat.of(1.0f), ConstantFloat.of(1.0f), 1, Sound.Type.FILE, false, false, 16);
    static final Logger LOGGER = LogUtils.getLogger();
    private static final String SOUNDS_PATH = "sounds.json";
    private static final Gson GSON = new GsonBuilder().registerTypeHierarchyAdapter(Component.class, (Object)new Component.Serializer()).registerTypeAdapter(SoundEventRegistration.class, (Object)new SoundEventRegistrationSerializer()).create();
    private static final TypeToken<Map<String, SoundEventRegistration>> SOUND_EVENT_REGISTRATION_TYPE = new TypeToken<Map<String, SoundEventRegistration>>(){};
    private final Map<ResourceLocation, WeighedSoundEvents> registry = Maps.newHashMap();
    private final SoundEngine soundEngine;
    private final Map<ResourceLocation, Resource> soundCache = new HashMap();

    public SoundManager(Options $$0) {
        this.soundEngine = new SoundEngine(this, $$0, ResourceProvider.fromMap(this.soundCache));
    }

    @Override
    protected Preparations prepare(ResourceManager $$0, ProfilerFiller $$1) {
        Preparations $$2 = new Preparations();
        $$1.startTick();
        $$1.push("list");
        $$2.listResources($$0);
        $$1.pop();
        for (String $$3 : $$0.getNamespaces()) {
            $$1.push($$3);
            try {
                List<Resource> $$4 = $$0.getResourceStack(new ResourceLocation($$3, SOUNDS_PATH));
                for (Resource $$5 : $$4) {
                    $$1.push($$5.sourcePackId());
                    try (BufferedReader $$6 = $$5.openAsReader();){
                        $$1.push("parse");
                        Map<String, SoundEventRegistration> $$7 = GsonHelper.fromJson(GSON, (Reader)$$6, SOUND_EVENT_REGISTRATION_TYPE);
                        $$1.popPush("register");
                        for (Map.Entry $$8 : $$7.entrySet()) {
                            $$2.handleRegistration(new ResourceLocation($$3, (String)$$8.getKey()), (SoundEventRegistration)$$8.getValue());
                        }
                        $$1.pop();
                    }
                    catch (RuntimeException $$9) {
                        LOGGER.warn("Invalid {} in resourcepack: '{}'", new Object[]{SOUNDS_PATH, $$5.sourcePackId(), $$9});
                    }
                    $$1.pop();
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
            $$1.pop();
        }
        $$1.endTick();
        return $$2;
    }

    @Override
    protected void apply(Preparations $$0, ResourceManager $$1, ProfilerFiller $$2) {
        $$0.apply(this.registry, this.soundCache, this.soundEngine);
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            for (ResourceLocation $$3 : this.registry.keySet()) {
                WeighedSoundEvents $$4 = (WeighedSoundEvents)this.registry.get((Object)$$3);
                if (ComponentUtils.isTranslationResolvable($$4.getSubtitle()) || !BuiltInRegistries.SOUND_EVENT.containsKey($$3)) continue;
                LOGGER.error("Missing subtitle {} for sound event: {}", (Object)$$4.getSubtitle(), (Object)$$3);
            }
        }
        if (LOGGER.isDebugEnabled()) {
            for (ResourceLocation $$5 : this.registry.keySet()) {
                if (BuiltInRegistries.SOUND_EVENT.containsKey($$5)) continue;
                LOGGER.debug("Not having sound event for: {}", (Object)$$5);
            }
        }
        this.soundEngine.reload();
    }

    public List<String> getAvailableSoundDevices() {
        return this.soundEngine.getAvailableSoundDevices();
    }

    static boolean validateSoundResource(Sound $$0, ResourceLocation $$1, ResourceProvider $$2) {
        ResourceLocation $$3 = $$0.getPath();
        if ($$2.getResource($$3).isEmpty()) {
            LOGGER.warn("File {} does not exist, cannot add it to event {}", (Object)$$3, (Object)$$1);
            return false;
        }
        return true;
    }

    @Nullable
    public WeighedSoundEvents getSoundEvent(ResourceLocation $$0) {
        return (WeighedSoundEvents)this.registry.get((Object)$$0);
    }

    public Collection<ResourceLocation> getAvailableSounds() {
        return this.registry.keySet();
    }

    public void queueTickingSound(TickableSoundInstance $$0) {
        this.soundEngine.queueTickingSound($$0);
    }

    public void play(SoundInstance $$0) {
        this.soundEngine.play($$0);
    }

    public void playDelayed(SoundInstance $$0, int $$1) {
        this.soundEngine.playDelayed($$0, $$1);
    }

    public void updateSource(Camera $$0) {
        this.soundEngine.updateSource($$0);
    }

    public void pause() {
        this.soundEngine.pause();
    }

    public void stop() {
        this.soundEngine.stopAll();
    }

    public void destroy() {
        this.soundEngine.destroy();
    }

    public void tick(boolean $$0) {
        this.soundEngine.tick($$0);
    }

    public void resume() {
        this.soundEngine.resume();
    }

    public void updateSourceVolume(SoundSource $$0, float $$1) {
        if ($$0 == SoundSource.MASTER && $$1 <= 0.0f) {
            this.stop();
        }
        this.soundEngine.updateCategoryVolume($$0, $$1);
    }

    public void stop(SoundInstance $$0) {
        this.soundEngine.stop($$0);
    }

    public boolean isActive(SoundInstance $$0) {
        return this.soundEngine.isActive($$0);
    }

    public void addListener(SoundEventListener $$0) {
        this.soundEngine.addEventListener($$0);
    }

    public void removeListener(SoundEventListener $$0) {
        this.soundEngine.removeEventListener($$0);
    }

    public void stop(@Nullable ResourceLocation $$0, @Nullable SoundSource $$1) {
        this.soundEngine.stop($$0, $$1);
    }

    public String getDebugString() {
        return this.soundEngine.getDebugString();
    }

    public void reload() {
        this.soundEngine.reload();
    }

    protected static class Preparations {
        final Map<ResourceLocation, WeighedSoundEvents> registry = Maps.newHashMap();
        private Map<ResourceLocation, Resource> soundCache = Map.of();

        protected Preparations() {
        }

        void listResources(ResourceManager $$0) {
            this.soundCache = Sound.SOUND_LISTER.listMatchingResources($$0);
        }

        /*
         * WARNING - void declaration
         */
        void handleRegistration(ResourceLocation $$0, SoundEventRegistration $$1) {
            boolean $$3;
            WeighedSoundEvents $$2 = (WeighedSoundEvents)this.registry.get((Object)$$0);
            boolean bl = $$3 = $$2 == null;
            if ($$3 || $$1.isReplace()) {
                if (!$$3) {
                    LOGGER.debug("Replaced sound event location {}", (Object)$$0);
                }
                $$2 = new WeighedSoundEvents($$0, $$1.getSubtitle());
                this.registry.put((Object)$$0, (Object)$$2);
            }
            ResourceProvider $$4 = ResourceProvider.fromMap(this.soundCache);
            block4: for (final Sound $$5 : $$1.getSounds()) {
                void $$9;
                final ResourceLocation $$6 = $$5.getLocation();
                switch ($$5.getType()) {
                    case FILE: {
                        if (!SoundManager.validateSoundResource($$5, $$0, $$4)) continue block4;
                        Sound $$7 = $$5;
                        break;
                    }
                    case SOUND_EVENT: {
                        Weighted<Sound> $$8 = new Weighted<Sound>(){

                            @Override
                            public int getWeight() {
                                WeighedSoundEvents $$0 = (WeighedSoundEvents)registry.get((Object)$$6);
                                return $$0 == null ? 0 : $$0.getWeight();
                            }

                            @Override
                            public Sound getSound(RandomSource $$0) {
                                WeighedSoundEvents $$1 = (WeighedSoundEvents)registry.get((Object)$$6);
                                if ($$1 == null) {
                                    return EMPTY_SOUND;
                                }
                                Sound $$2 = $$1.getSound($$0);
                                return new Sound($$2.getLocation().toString(), new MultipliedFloats($$2.getVolume(), $$5.getVolume()), new MultipliedFloats($$2.getPitch(), $$5.getPitch()), $$5.getWeight(), Sound.Type.FILE, $$2.shouldStream() || $$5.shouldStream(), $$2.shouldPreload(), $$2.getAttenuationDistance());
                            }

                            @Override
                            public void preloadIfRequired(SoundEngine $$0) {
                                WeighedSoundEvents $$1 = (WeighedSoundEvents)registry.get((Object)$$6);
                                if ($$1 == null) {
                                    return;
                                }
                                $$1.preloadIfRequired($$0);
                            }
                        };
                        break;
                    }
                    default: {
                        throw new IllegalStateException("Unknown SoundEventRegistration type: " + $$5.getType());
                    }
                }
                $$2.addSound((Weighted<Sound>)$$9);
            }
        }

        public void apply(Map<ResourceLocation, WeighedSoundEvents> $$0, Map<ResourceLocation, Resource> $$1, SoundEngine $$2) {
            $$0.clear();
            $$1.clear();
            $$1.putAll(this.soundCache);
            for (Map.Entry $$3 : this.registry.entrySet()) {
                $$0.put((Object)((ResourceLocation)$$3.getKey()), (Object)((WeighedSoundEvents)$$3.getValue()));
                ((WeighedSoundEvents)$$3.getValue()).preloadIfRequired($$2);
            }
        }
    }
}