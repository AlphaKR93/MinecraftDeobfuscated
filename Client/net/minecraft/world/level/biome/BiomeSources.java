/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 */
package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.CheckerboardColumnBiomeSource;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.TheEndBiomeSource;

public class BiomeSources {
    public static Codec<? extends BiomeSource> bootstrap(Registry<Codec<? extends BiomeSource>> $$0) {
        Registry.register($$0, "fixed", FixedBiomeSource.CODEC);
        Registry.register($$0, "multi_noise", MultiNoiseBiomeSource.CODEC);
        Registry.register($$0, "checkerboard", CheckerboardColumnBiomeSource.CODEC);
        return Registry.register($$0, "the_end", TheEndBiomeSource.CODEC);
    }
}