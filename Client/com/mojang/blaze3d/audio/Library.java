/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  java.lang.CharSequence
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.nio.IntBuffer
 *  java.util.Collections
 *  java.util.List
 *  java.util.Locale
 *  java.util.Objects
 *  java.util.OptionalLong
 *  java.util.Set
 *  javax.annotation.Nullable
 *  org.lwjgl.openal.AL
 *  org.lwjgl.openal.AL10
 *  org.lwjgl.openal.ALC
 *  org.lwjgl.openal.ALC10
 *  org.lwjgl.openal.ALC11
 *  org.lwjgl.openal.ALCCapabilities
 *  org.lwjgl.openal.ALCapabilities
 *  org.lwjgl.openal.ALUtil
 *  org.lwjgl.openal.SOFTHRTF
 *  org.lwjgl.system.MemoryStack
 *  org.slf4j.Logger
 */
package com.mojang.blaze3d.audio;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.audio.Channel;
import com.mojang.blaze3d.audio.Listener;
import com.mojang.blaze3d.audio.OpenAlUtil;
import com.mojang.logging.LogUtils;
import java.nio.IntBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.util.Mth;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.ALCapabilities;
import org.lwjgl.openal.ALUtil;
import org.lwjgl.openal.SOFTHRTF;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;

public class Library {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int NO_DEVICE = 0;
    private static final int DEFAULT_CHANNEL_COUNT = 30;
    private long currentDevice;
    private long context;
    private boolean supportsDisconnections;
    @Nullable
    private String defaultDeviceName;
    private static final ChannelPool EMPTY = new ChannelPool(){

        @Override
        @Nullable
        public Channel acquire() {
            return null;
        }

        @Override
        public boolean release(Channel $$0) {
            return false;
        }

        @Override
        public void cleanup() {
        }

        @Override
        public int getMaxCount() {
            return 0;
        }

        @Override
        public int getUsedCount() {
            return 0;
        }
    };
    private ChannelPool staticChannels = EMPTY;
    private ChannelPool streamingChannels = EMPTY;
    private final Listener listener = new Listener();

    public Library() {
        this.defaultDeviceName = Library.getDefaultDeviceName();
    }

    public void init(@Nullable String $$0, boolean $$1) {
        this.currentDevice = Library.openDeviceOrFallback($$0);
        this.supportsDisconnections = ALC10.alcIsExtensionPresent((long)this.currentDevice, (CharSequence)"ALC_EXT_disconnect");
        ALCCapabilities $$2 = ALC.createCapabilities((long)this.currentDevice);
        if (OpenAlUtil.checkALCError(this.currentDevice, "Get capabilities")) {
            throw new IllegalStateException("Failed to get OpenAL capabilities");
        }
        if (!$$2.OpenALC11) {
            throw new IllegalStateException("OpenAL 1.1 not supported");
        }
        this.setHrtf($$2.ALC_SOFT_HRTF && $$1);
        this.context = ALC10.alcCreateContext((long)this.currentDevice, (IntBuffer)null);
        ALC10.alcMakeContextCurrent((long)this.context);
        int $$3 = this.getChannelCount();
        int $$4 = Mth.clamp((int)Mth.sqrt($$3), 2, 8);
        int $$5 = Mth.clamp($$3 - $$4, 8, 255);
        this.staticChannels = new CountingChannelPool($$5);
        this.streamingChannels = new CountingChannelPool($$4);
        ALCapabilities $$6 = AL.createCapabilities((ALCCapabilities)$$2);
        OpenAlUtil.checkALError("Initialization");
        if (!$$6.AL_EXT_source_distance_model) {
            throw new IllegalStateException("AL_EXT_source_distance_model is not supported");
        }
        AL10.alEnable((int)512);
        if (!$$6.AL_EXT_LINEAR_DISTANCE) {
            throw new IllegalStateException("AL_EXT_LINEAR_DISTANCE is not supported");
        }
        OpenAlUtil.checkALError("Enable per-source distance models");
        LOGGER.info("OpenAL initialized on device {}", (Object)this.getCurrentDeviceName());
    }

    private void setHrtf(boolean $$0) {
        int $$1 = ALC10.alcGetInteger((long)this.currentDevice, (int)6548);
        if ($$1 > 0) {
            try (MemoryStack $$2 = MemoryStack.stackPush();){
                IntBuffer $$3 = $$2.callocInt(10).put(6546).put($$0 ? 1 : 0).put(6550).put(0).put(0).flip();
                if (!SOFTHRTF.alcResetDeviceSOFT((long)this.currentDevice, (IntBuffer)$$3)) {
                    LOGGER.warn("Failed to reset device: {}", (Object)ALC10.alcGetString((long)this.currentDevice, (int)ALC10.alcGetError((long)this.currentDevice)));
                }
            }
        }
    }

    private int getChannelCount() {
        try (MemoryStack $$0 = MemoryStack.stackPush();){
            int $$1 = ALC10.alcGetInteger((long)this.currentDevice, (int)4098);
            if (OpenAlUtil.checkALCError(this.currentDevice, "Get attributes size")) {
                throw new IllegalStateException("Failed to get OpenAL attributes");
            }
            IntBuffer $$2 = $$0.mallocInt($$1);
            ALC10.alcGetIntegerv((long)this.currentDevice, (int)4099, (IntBuffer)$$2);
            if (OpenAlUtil.checkALCError(this.currentDevice, "Get attributes")) {
                throw new IllegalStateException("Failed to get OpenAL attributes");
            }
            int $$3 = 0;
            while ($$3 < $$1) {
                int $$4;
                if (($$4 = $$2.get($$3++)) == 0) {
                    break;
                }
                int $$5 = $$2.get($$3++);
                if ($$4 != 4112) continue;
                int n = $$5;
                return n;
            }
        }
        return 30;
    }

    @Nullable
    public static String getDefaultDeviceName() {
        if (!ALC10.alcIsExtensionPresent((long)0L, (CharSequence)"ALC_ENUMERATE_ALL_EXT")) {
            return null;
        }
        ALUtil.getStringList((long)0L, (int)4115);
        return ALC10.alcGetString((long)0L, (int)4114);
    }

    public String getCurrentDeviceName() {
        String $$0 = ALC10.alcGetString((long)this.currentDevice, (int)4115);
        if ($$0 == null) {
            $$0 = ALC10.alcGetString((long)this.currentDevice, (int)4101);
        }
        if ($$0 == null) {
            $$0 = "Unknown";
        }
        return $$0;
    }

    public synchronized boolean hasDefaultDeviceChanged() {
        String $$0 = Library.getDefaultDeviceName();
        if (Objects.equals((Object)this.defaultDeviceName, (Object)$$0)) {
            return false;
        }
        this.defaultDeviceName = $$0;
        return true;
    }

    private static long openDeviceOrFallback(@Nullable String $$0) {
        OptionalLong $$1 = OptionalLong.empty();
        if ($$0 != null) {
            $$1 = Library.tryOpenDevice($$0);
        }
        if ($$1.isEmpty()) {
            $$1 = Library.tryOpenDevice(Library.getDefaultDeviceName());
        }
        if ($$1.isEmpty()) {
            $$1 = Library.tryOpenDevice(null);
        }
        if ($$1.isEmpty()) {
            throw new IllegalStateException("Failed to open OpenAL device");
        }
        return $$1.getAsLong();
    }

    private static OptionalLong tryOpenDevice(@Nullable String $$0) {
        long $$1 = ALC10.alcOpenDevice((CharSequence)$$0);
        if ($$1 != 0L && !OpenAlUtil.checkALCError($$1, "Open device")) {
            return OptionalLong.of((long)$$1);
        }
        return OptionalLong.empty();
    }

    public void cleanup() {
        this.staticChannels.cleanup();
        this.streamingChannels.cleanup();
        ALC10.alcDestroyContext((long)this.context);
        if (this.currentDevice != 0L) {
            ALC10.alcCloseDevice((long)this.currentDevice);
        }
    }

    public Listener getListener() {
        return this.listener;
    }

    @Nullable
    public Channel acquireChannel(Pool $$0) {
        return ($$0 == Pool.STREAMING ? this.streamingChannels : this.staticChannels).acquire();
    }

    public void releaseChannel(Channel $$0) {
        if (!this.staticChannels.release($$0) && !this.streamingChannels.release($$0)) {
            throw new IllegalStateException("Tried to release unknown channel");
        }
    }

    public String getDebugString() {
        return String.format((Locale)Locale.ROOT, (String)"Sounds: %d/%d + %d/%d", (Object[])new Object[]{this.staticChannels.getUsedCount(), this.staticChannels.getMaxCount(), this.streamingChannels.getUsedCount(), this.streamingChannels.getMaxCount()});
    }

    public List<String> getAvailableSoundDevices() {
        List $$0 = ALUtil.getStringList((long)0L, (int)4115);
        if ($$0 == null) {
            return Collections.emptyList();
        }
        return $$0;
    }

    public boolean isCurrentDeviceDisconnected() {
        return this.supportsDisconnections && ALC11.alcGetInteger((long)this.currentDevice, (int)787) == 0;
    }

    static interface ChannelPool {
        @Nullable
        public Channel acquire();

        public boolean release(Channel var1);

        public void cleanup();

        public int getMaxCount();

        public int getUsedCount();
    }

    static class CountingChannelPool
    implements ChannelPool {
        private final int limit;
        private final Set<Channel> activeChannels = Sets.newIdentityHashSet();

        public CountingChannelPool(int $$0) {
            this.limit = $$0;
        }

        @Override
        @Nullable
        public Channel acquire() {
            if (this.activeChannels.size() >= this.limit) {
                if (SharedConstants.IS_RUNNING_IN_IDE) {
                    LOGGER.warn("Maximum sound pool size {} reached", (Object)this.limit);
                }
                return null;
            }
            Channel $$0 = Channel.create();
            if ($$0 != null) {
                this.activeChannels.add((Object)$$0);
            }
            return $$0;
        }

        @Override
        public boolean release(Channel $$0) {
            if (!this.activeChannels.remove((Object)$$0)) {
                return false;
            }
            $$0.destroy();
            return true;
        }

        @Override
        public void cleanup() {
            this.activeChannels.forEach(Channel::destroy);
            this.activeChannels.clear();
        }

        @Override
        public int getMaxCount() {
            return this.limit;
        }

        @Override
        public int getUsedCount() {
            return this.activeChannels.size();
        }
    }

    public static enum Pool {
        STATIC,
        STREAMING;

    }
}