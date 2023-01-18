/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  java.util.Optional
 *  java.util.stream.Stream
 *  org.apache.commons.lang3.mutable.MutableBoolean
 */
package net.minecraft.world.level.levelgen.placement;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import org.apache.commons.lang3.mutable.MutableBoolean;

public record PlacedFeature(Holder<ConfiguredFeature<?, ?>> feature, List<PlacementModifier> placement) {
    public static final Codec<PlacedFeature> DIRECT_CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)ConfiguredFeature.CODEC.fieldOf("feature").forGetter($$0 -> $$0.feature), (App)PlacementModifier.CODEC.listOf().fieldOf("placement").forGetter($$0 -> $$0.placement)).apply((Applicative)$$02, PlacedFeature::new));
    public static final Codec<Holder<PlacedFeature>> CODEC = RegistryFileCodec.create(Registries.PLACED_FEATURE, DIRECT_CODEC);
    public static final Codec<HolderSet<PlacedFeature>> LIST_CODEC = RegistryCodecs.homogeneousList(Registries.PLACED_FEATURE, DIRECT_CODEC);
    public static final Codec<List<HolderSet<PlacedFeature>>> LIST_OF_LISTS_CODEC = RegistryCodecs.homogeneousList(Registries.PLACED_FEATURE, DIRECT_CODEC, true).listOf();

    public boolean place(WorldGenLevel $$0, ChunkGenerator $$1, RandomSource $$2, BlockPos $$3) {
        return this.placeWithContext(new PlacementContext($$0, $$1, (Optional<PlacedFeature>)Optional.empty()), $$2, $$3);
    }

    public boolean placeWithBiomeCheck(WorldGenLevel $$0, ChunkGenerator $$1, RandomSource $$2, BlockPos $$3) {
        return this.placeWithContext(new PlacementContext($$0, $$1, (Optional<PlacedFeature>)Optional.of((Object)((Object)this))), $$2, $$3);
    }

    private boolean placeWithContext(PlacementContext $$0, RandomSource $$1, BlockPos $$2) {
        Stream $$32 = Stream.of((Object)$$2);
        for (PlacementModifier $$42 : this.placement) {
            $$32 = $$32.flatMap($$3 -> $$42.getPositions($$0, $$1, (BlockPos)$$3));
        }
        ConfiguredFeature<?, ?> $$5 = this.feature.value();
        MutableBoolean $$6 = new MutableBoolean();
        $$32.forEach($$4 -> {
            if ($$5.place($$0.getLevel(), $$0.generator(), $$1, (BlockPos)$$4)) {
                $$6.setTrue();
            }
        });
        return $$6.isTrue();
    }

    public Stream<ConfiguredFeature<?, ?>> getFeatures() {
        return this.feature.value().getFeatures();
    }

    public String toString() {
        return "Placed " + this.feature;
    }
}