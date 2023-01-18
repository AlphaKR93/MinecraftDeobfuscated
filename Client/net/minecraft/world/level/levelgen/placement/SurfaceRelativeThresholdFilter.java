/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.levelgen.placement;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class SurfaceRelativeThresholdFilter
extends PlacementFilter {
    public static final Codec<SurfaceRelativeThresholdFilter> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Heightmap.Types.CODEC.fieldOf("heightmap").forGetter($$0 -> $$0.heightmap), (App)Codec.INT.optionalFieldOf("min_inclusive", (Object)Integer.MIN_VALUE).forGetter($$0 -> $$0.minInclusive), (App)Codec.INT.optionalFieldOf("max_inclusive", (Object)Integer.MAX_VALUE).forGetter($$0 -> $$0.maxInclusive)).apply((Applicative)$$02, SurfaceRelativeThresholdFilter::new));
    private final Heightmap.Types heightmap;
    private final int minInclusive;
    private final int maxInclusive;

    private SurfaceRelativeThresholdFilter(Heightmap.Types $$0, int $$1, int $$2) {
        this.heightmap = $$0;
        this.minInclusive = $$1;
        this.maxInclusive = $$2;
    }

    public static SurfaceRelativeThresholdFilter of(Heightmap.Types $$0, int $$1, int $$2) {
        return new SurfaceRelativeThresholdFilter($$0, $$1, $$2);
    }

    @Override
    protected boolean shouldPlace(PlacementContext $$0, RandomSource $$1, BlockPos $$2) {
        long $$3 = $$0.getHeight(this.heightmap, $$2.getX(), $$2.getZ());
        long $$4 = $$3 + (long)this.minInclusive;
        long $$5 = $$3 + (long)this.maxInclusive;
        return $$4 <= (long)$$2.getY() && (long)$$2.getY() <= $$5;
    }

    @Override
    public PlacementModifierType<?> type() {
        return PlacementModifierType.SURFACE_RELATIVE_THRESHOLD_FILTER;
    }
}