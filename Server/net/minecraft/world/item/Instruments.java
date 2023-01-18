/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.world.item;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Instrument;

public interface Instruments {
    public static final int GOAT_HORN_RANGE_BLOCKS = 256;
    public static final int GOAT_HORN_DURATION = 140;
    public static final ResourceKey<Instrument> PONDER_GOAT_HORN = Instruments.create("ponder_goat_horn");
    public static final ResourceKey<Instrument> SING_GOAT_HORN = Instruments.create("sing_goat_horn");
    public static final ResourceKey<Instrument> SEEK_GOAT_HORN = Instruments.create("seek_goat_horn");
    public static final ResourceKey<Instrument> FEEL_GOAT_HORN = Instruments.create("feel_goat_horn");
    public static final ResourceKey<Instrument> ADMIRE_GOAT_HORN = Instruments.create("admire_goat_horn");
    public static final ResourceKey<Instrument> CALL_GOAT_HORN = Instruments.create("call_goat_horn");
    public static final ResourceKey<Instrument> YEARN_GOAT_HORN = Instruments.create("yearn_goat_horn");
    public static final ResourceKey<Instrument> DREAM_GOAT_HORN = Instruments.create("dream_goat_horn");

    private static ResourceKey<Instrument> create(String $$0) {
        return ResourceKey.create(Registries.INSTRUMENT, new ResourceLocation($$0));
    }

    public static Instrument bootstrap(Registry<Instrument> $$0) {
        Registry.register($$0, PONDER_GOAT_HORN, new Instrument((Holder)SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(0), 140, 256.0f));
        Registry.register($$0, SING_GOAT_HORN, new Instrument((Holder)SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(1), 140, 256.0f));
        Registry.register($$0, SEEK_GOAT_HORN, new Instrument((Holder)SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(2), 140, 256.0f));
        Registry.register($$0, FEEL_GOAT_HORN, new Instrument((Holder)SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(3), 140, 256.0f));
        Registry.register($$0, ADMIRE_GOAT_HORN, new Instrument((Holder)SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(4), 140, 256.0f));
        Registry.register($$0, CALL_GOAT_HORN, new Instrument((Holder)SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(5), 140, 256.0f));
        Registry.register($$0, YEARN_GOAT_HORN, new Instrument((Holder)SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(6), 140, 256.0f));
        return Registry.register($$0, DREAM_GOAT_HORN, new Instrument((Holder)SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(7), 140, 256.0f));
    }
}