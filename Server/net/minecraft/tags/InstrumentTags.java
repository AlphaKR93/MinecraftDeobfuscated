/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Instrument;

public interface InstrumentTags {
    public static final TagKey<Instrument> REGULAR_GOAT_HORNS = InstrumentTags.create("regular_goat_horns");
    public static final TagKey<Instrument> SCREAMING_GOAT_HORNS = InstrumentTags.create("screaming_goat_horns");
    public static final TagKey<Instrument> GOAT_HORNS = InstrumentTags.create("goat_horns");

    private static TagKey<Instrument> create(String $$0) {
        return TagKey.create(Registries.INSTRUMENT, new ResourceLocation($$0));
    }
}