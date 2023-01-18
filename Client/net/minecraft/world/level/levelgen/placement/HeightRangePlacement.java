/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.stream.Stream
 */
package net.minecraft.world.level.levelgen.placement;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class HeightRangePlacement
extends PlacementModifier {
    public static final Codec<HeightRangePlacement> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)HeightProvider.CODEC.fieldOf("height").forGetter($$0 -> $$0.height)).apply((Applicative)$$02, HeightRangePlacement::new));
    private final HeightProvider height;

    private HeightRangePlacement(HeightProvider $$0) {
        this.height = $$0;
    }

    public static HeightRangePlacement of(HeightProvider $$0) {
        return new HeightRangePlacement($$0);
    }

    public static HeightRangePlacement uniform(VerticalAnchor $$0, VerticalAnchor $$1) {
        return HeightRangePlacement.of(UniformHeight.of($$0, $$1));
    }

    public static HeightRangePlacement triangle(VerticalAnchor $$0, VerticalAnchor $$1) {
        return HeightRangePlacement.of(TrapezoidHeight.of($$0, $$1));
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext $$0, RandomSource $$1, BlockPos $$2) {
        return Stream.of((Object)$$2.atY(this.height.sample($$1, $$0)));
    }

    @Override
    public PlacementModifierType<?> type() {
        return PlacementModifierType.HEIGHT_RANGE;
    }
}