/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.client.sounds;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.Weighted;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public class WeighedSoundEvents
implements Weighted<Sound> {
    private final List<Weighted<Sound>> list = Lists.newArrayList();
    @Nullable
    private final Component subtitle;

    public WeighedSoundEvents(ResourceLocation $$0, @Nullable String $$1) {
        this.subtitle = $$1 == null ? null : Component.translatable($$1);
    }

    @Override
    public int getWeight() {
        int $$0 = 0;
        for (Weighted $$1 : this.list) {
            $$0 += $$1.getWeight();
        }
        return $$0;
    }

    @Override
    public Sound getSound(RandomSource $$0) {
        int $$1 = this.getWeight();
        if (this.list.isEmpty() || $$1 == 0) {
            return SoundManager.EMPTY_SOUND;
        }
        int $$2 = $$0.nextInt($$1);
        for (Weighted $$3 : this.list) {
            if (($$2 -= $$3.getWeight()) >= 0) continue;
            return (Sound)$$3.getSound($$0);
        }
        return SoundManager.EMPTY_SOUND;
    }

    public void addSound(Weighted<Sound> $$0) {
        this.list.add($$0);
    }

    @Nullable
    public Component getSubtitle() {
        return this.subtitle;
    }

    @Override
    public void preloadIfRequired(SoundEngine $$0) {
        for (Weighted $$1 : this.list) {
            $$1.preloadIfRequired($$0);
        }
    }
}