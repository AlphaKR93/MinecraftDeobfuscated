/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.client.resources.sounds;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;

public class SimpleSoundInstance
extends AbstractSoundInstance {
    public SimpleSoundInstance(SoundEvent $$0, SoundSource $$1, float $$2, float $$3, RandomSource $$4, BlockPos $$5) {
        this($$0, $$1, $$2, $$3, $$4, (double)$$5.getX() + 0.5, (double)$$5.getY() + 0.5, (double)$$5.getZ() + 0.5);
    }

    public static SimpleSoundInstance forUI(SoundEvent $$0, float $$1) {
        return SimpleSoundInstance.forUI($$0, $$1, 0.25f);
    }

    public static SimpleSoundInstance forUI(Holder<SoundEvent> $$0, float $$1) {
        return SimpleSoundInstance.forUI($$0.value(), $$1);
    }

    public static SimpleSoundInstance forUI(SoundEvent $$0, float $$1, float $$2) {
        return new SimpleSoundInstance($$0.getLocation(), SoundSource.MASTER, $$2, $$1, SoundInstance.createUnseededRandom(), false, 0, SoundInstance.Attenuation.NONE, 0.0, 0.0, 0.0, true);
    }

    public static SimpleSoundInstance forMusic(SoundEvent $$0) {
        return new SimpleSoundInstance($$0.getLocation(), SoundSource.MUSIC, 1.0f, 1.0f, SoundInstance.createUnseededRandom(), false, 0, SoundInstance.Attenuation.NONE, 0.0, 0.0, 0.0, true);
    }

    public static SimpleSoundInstance forRecord(SoundEvent $$0, Vec3 $$1) {
        return new SimpleSoundInstance($$0, SoundSource.RECORDS, 4.0f, 1.0f, SoundInstance.createUnseededRandom(), false, 0, SoundInstance.Attenuation.LINEAR, $$1.x, $$1.y, $$1.z);
    }

    public static SimpleSoundInstance forLocalAmbience(SoundEvent $$0, float $$1, float $$2) {
        return new SimpleSoundInstance($$0.getLocation(), SoundSource.AMBIENT, $$2, $$1, SoundInstance.createUnseededRandom(), false, 0, SoundInstance.Attenuation.NONE, 0.0, 0.0, 0.0, true);
    }

    public static SimpleSoundInstance forAmbientAddition(SoundEvent $$0) {
        return SimpleSoundInstance.forLocalAmbience($$0, 1.0f, 1.0f);
    }

    public static SimpleSoundInstance forAmbientMood(SoundEvent $$0, RandomSource $$1, double $$2, double $$3, double $$4) {
        return new SimpleSoundInstance($$0, SoundSource.AMBIENT, 1.0f, 1.0f, $$1, false, 0, SoundInstance.Attenuation.LINEAR, $$2, $$3, $$4);
    }

    public SimpleSoundInstance(SoundEvent $$0, SoundSource $$1, float $$2, float $$3, RandomSource $$4, double $$5, double $$6, double $$7) {
        this($$0, $$1, $$2, $$3, $$4, false, 0, SoundInstance.Attenuation.LINEAR, $$5, $$6, $$7);
    }

    private SimpleSoundInstance(SoundEvent $$0, SoundSource $$1, float $$2, float $$3, RandomSource $$4, boolean $$5, int $$6, SoundInstance.Attenuation $$7, double $$8, double $$9, double $$10) {
        this($$0.getLocation(), $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, $$10, false);
    }

    public SimpleSoundInstance(ResourceLocation $$0, SoundSource $$1, float $$2, float $$3, RandomSource $$4, boolean $$5, int $$6, SoundInstance.Attenuation $$7, double $$8, double $$9, double $$10, boolean $$11) {
        super($$0, $$1, $$4);
        this.volume = $$2;
        this.pitch = $$3;
        this.x = $$8;
        this.y = $$9;
        this.z = $$10;
        this.looping = $$5;
        this.delay = $$6;
        this.attenuation = $$7;
        this.relative = $$11;
    }
}