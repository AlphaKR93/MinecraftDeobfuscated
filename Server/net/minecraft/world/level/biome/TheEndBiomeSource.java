/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 */
package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.DensityFunction;

public class TheEndBiomeSource
extends BiomeSource {
    public static final Codec<TheEndBiomeSource> CODEC = RecordCodecBuilder.create($$0 -> $$0.group(RegistryOps.retrieveElement(Biomes.THE_END), RegistryOps.retrieveElement(Biomes.END_HIGHLANDS), RegistryOps.retrieveElement(Biomes.END_MIDLANDS), RegistryOps.retrieveElement(Biomes.SMALL_END_ISLANDS), RegistryOps.retrieveElement(Biomes.END_BARRENS)).apply((Applicative)$$0, $$0.stable(TheEndBiomeSource::new)));
    private final Holder<Biome> end;
    private final Holder<Biome> highlands;
    private final Holder<Biome> midlands;
    private final Holder<Biome> islands;
    private final Holder<Biome> barrens;

    public static TheEndBiomeSource create(HolderGetter<Biome> $$0) {
        return new TheEndBiomeSource($$0.getOrThrow(Biomes.THE_END), $$0.getOrThrow(Biomes.END_HIGHLANDS), $$0.getOrThrow(Biomes.END_MIDLANDS), $$0.getOrThrow(Biomes.SMALL_END_ISLANDS), $$0.getOrThrow(Biomes.END_BARRENS));
    }

    private TheEndBiomeSource(Holder<Biome> $$0, Holder<Biome> $$1, Holder<Biome> $$2, Holder<Biome> $$3, Holder<Biome> $$4) {
        super((List<Holder<Biome>>)ImmutableList.of($$0, $$1, $$2, $$3, $$4));
        this.end = $$0;
        this.highlands = $$1;
        this.midlands = $$2;
        this.islands = $$3;
        this.barrens = $$4;
    }

    @Override
    protected Codec<? extends BiomeSource> codec() {
        return CODEC;
    }

    @Override
    public Holder<Biome> getNoiseBiome(int $$0, int $$1, int $$2, Climate.Sampler $$3) {
        int $$8;
        int $$4 = QuartPos.toBlock($$0);
        int $$5 = QuartPos.toBlock($$1);
        int $$6 = QuartPos.toBlock($$2);
        int $$7 = SectionPos.blockToSectionCoord($$4);
        if ((long)$$7 * (long)$$7 + (long)($$8 = SectionPos.blockToSectionCoord($$6)) * (long)$$8 <= 4096L) {
            return this.end;
        }
        int $$9 = (SectionPos.blockToSectionCoord($$4) * 2 + 1) * 8;
        int $$10 = (SectionPos.blockToSectionCoord($$6) * 2 + 1) * 8;
        double $$11 = $$3.erosion().compute(new DensityFunction.SinglePointContext($$9, $$5, $$10));
        if ($$11 > 0.25) {
            return this.highlands;
        }
        if ($$11 >= -0.0625) {
            return this.midlands;
        }
        if ($$11 < -0.21875) {
            return this.islands;
        }
        return this.barrens;
    }
}