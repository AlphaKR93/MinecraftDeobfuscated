/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 *  javax.annotation.Nullable
 */
package net.minecraft.client.resources.sounds;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.AmbientSoundHandler;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.AmbientAdditionsSettings;
import net.minecraft.world.level.biome.AmbientMoodSettings;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;

public class BiomeAmbientSoundsHandler
implements AmbientSoundHandler {
    private static final int LOOP_SOUND_CROSS_FADE_TIME = 40;
    private static final float SKY_MOOD_RECOVERY_RATE = 0.001f;
    private final LocalPlayer player;
    private final SoundManager soundManager;
    private final BiomeManager biomeManager;
    private final RandomSource random;
    private final Object2ObjectArrayMap<Biome, LoopSoundInstance> loopSounds = new Object2ObjectArrayMap();
    private Optional<AmbientMoodSettings> moodSettings = Optional.empty();
    private Optional<AmbientAdditionsSettings> additionsSettings = Optional.empty();
    private float moodiness;
    @Nullable
    private Biome previousBiome;

    public BiomeAmbientSoundsHandler(LocalPlayer $$0, SoundManager $$1, BiomeManager $$2) {
        this.random = $$0.level.getRandom();
        this.player = $$0;
        this.soundManager = $$1;
        this.biomeManager = $$2;
    }

    public float getMoodiness() {
        return this.moodiness;
    }

    @Override
    public void tick() {
        this.loopSounds.values().removeIf(AbstractTickableSoundInstance::isStopped);
        Biome $$02 = this.biomeManager.getNoiseBiomeAtPosition(this.player.getX(), this.player.getY(), this.player.getZ()).value();
        if ($$02 != this.previousBiome) {
            this.previousBiome = $$02;
            this.moodSettings = $$02.getAmbientMood();
            this.additionsSettings = $$02.getAmbientAdditions();
            this.loopSounds.values().forEach(LoopSoundInstance::fadeOut);
            $$02.getAmbientLoop().ifPresent($$12 -> this.loopSounds.compute((Object)$$02, ($$1, $$2) -> {
                if ($$2 == null) {
                    $$2 = new LoopSoundInstance((SoundEvent)$$12.value());
                    this.soundManager.play((SoundInstance)$$2);
                }
                $$2.fadeIn();
                return $$2;
            }));
        }
        this.additionsSettings.ifPresent($$0 -> {
            if (this.random.nextDouble() < $$0.getTickChance()) {
                this.soundManager.play(SimpleSoundInstance.forAmbientAddition($$0.getSoundEvent().value()));
            }
        });
        this.moodSettings.ifPresent($$0 -> {
            Level $$1 = this.player.level;
            int $$2 = $$0.getBlockSearchExtent() * 2 + 1;
            BlockPos $$3 = new BlockPos(this.player.getX() + (double)this.random.nextInt($$2) - (double)$$0.getBlockSearchExtent(), this.player.getEyeY() + (double)this.random.nextInt($$2) - (double)$$0.getBlockSearchExtent(), this.player.getZ() + (double)this.random.nextInt($$2) - (double)$$0.getBlockSearchExtent());
            int $$4 = $$1.getBrightness(LightLayer.SKY, $$3);
            this.moodiness = $$4 > 0 ? (this.moodiness -= (float)$$4 / (float)$$1.getMaxLightLevel() * 0.001f) : (this.moodiness -= (float)($$1.getBrightness(LightLayer.BLOCK, $$3) - 1) / (float)$$0.getTickDelay());
            if (this.moodiness >= 1.0f) {
                double $$5 = (double)$$3.getX() + 0.5;
                double $$6 = (double)$$3.getY() + 0.5;
                double $$7 = (double)$$3.getZ() + 0.5;
                double $$8 = $$5 - this.player.getX();
                double $$9 = $$6 - this.player.getEyeY();
                double $$10 = $$7 - this.player.getZ();
                double $$11 = Math.sqrt((double)($$8 * $$8 + $$9 * $$9 + $$10 * $$10));
                double $$12 = $$11 + $$0.getSoundPositionOffset();
                SimpleSoundInstance $$13 = SimpleSoundInstance.forAmbientMood($$0.getSoundEvent().value(), this.random, this.player.getX() + $$8 / $$11 * $$12, this.player.getEyeY() + $$9 / $$11 * $$12, this.player.getZ() + $$10 / $$11 * $$12);
                this.soundManager.play($$13);
                this.moodiness = 0.0f;
            } else {
                this.moodiness = Math.max((float)this.moodiness, (float)0.0f);
            }
        });
    }

    public static class LoopSoundInstance
    extends AbstractTickableSoundInstance {
        private int fadeDirection;
        private int fade;

        public LoopSoundInstance(SoundEvent $$0) {
            super($$0, SoundSource.AMBIENT, SoundInstance.createUnseededRandom());
            this.looping = true;
            this.delay = 0;
            this.volume = 1.0f;
            this.relative = true;
        }

        @Override
        public void tick() {
            if (this.fade < 0) {
                this.stop();
            }
            this.fade += this.fadeDirection;
            this.volume = Mth.clamp((float)this.fade / 40.0f, 0.0f, 1.0f);
        }

        public void fadeOut() {
            this.fade = Math.min((int)this.fade, (int)40);
            this.fadeDirection = -1;
        }

        public void fadeIn() {
            this.fade = Math.max((int)0, (int)this.fade);
            this.fadeDirection = 1;
        }
    }
}