/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.String
 *  java.util.function.Function
 */
package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Function;
import net.minecraft.world.level.levelgen.DensityFunction;

public record NoiseRouter(DensityFunction barrierNoise, DensityFunction fluidLevelFloodednessNoise, DensityFunction fluidLevelSpreadNoise, DensityFunction lavaNoise, DensityFunction temperature, DensityFunction vegetation, DensityFunction continents, DensityFunction erosion, DensityFunction depth, DensityFunction ridges, DensityFunction initialDensityWithoutJaggedness, DensityFunction finalDensity, DensityFunction veinToggle, DensityFunction veinRidged, DensityFunction veinGap) {
    public static final Codec<NoiseRouter> CODEC = RecordCodecBuilder.create($$0 -> $$0.group(NoiseRouter.field("barrier", (Function<NoiseRouter, DensityFunction>)((Function)NoiseRouter::barrierNoise)), NoiseRouter.field("fluid_level_floodedness", (Function<NoiseRouter, DensityFunction>)((Function)NoiseRouter::fluidLevelFloodednessNoise)), NoiseRouter.field("fluid_level_spread", (Function<NoiseRouter, DensityFunction>)((Function)NoiseRouter::fluidLevelSpreadNoise)), NoiseRouter.field("lava", (Function<NoiseRouter, DensityFunction>)((Function)NoiseRouter::lavaNoise)), NoiseRouter.field("temperature", (Function<NoiseRouter, DensityFunction>)((Function)NoiseRouter::temperature)), NoiseRouter.field("vegetation", (Function<NoiseRouter, DensityFunction>)((Function)NoiseRouter::vegetation)), NoiseRouter.field("continents", (Function<NoiseRouter, DensityFunction>)((Function)NoiseRouter::continents)), NoiseRouter.field("erosion", (Function<NoiseRouter, DensityFunction>)((Function)NoiseRouter::erosion)), NoiseRouter.field("depth", (Function<NoiseRouter, DensityFunction>)((Function)NoiseRouter::depth)), NoiseRouter.field("ridges", (Function<NoiseRouter, DensityFunction>)((Function)NoiseRouter::ridges)), NoiseRouter.field("initial_density_without_jaggedness", (Function<NoiseRouter, DensityFunction>)((Function)NoiseRouter::initialDensityWithoutJaggedness)), NoiseRouter.field("final_density", (Function<NoiseRouter, DensityFunction>)((Function)NoiseRouter::finalDensity)), NoiseRouter.field("vein_toggle", (Function<NoiseRouter, DensityFunction>)((Function)NoiseRouter::veinToggle)), NoiseRouter.field("vein_ridged", (Function<NoiseRouter, DensityFunction>)((Function)NoiseRouter::veinRidged)), NoiseRouter.field("vein_gap", (Function<NoiseRouter, DensityFunction>)((Function)NoiseRouter::veinGap))).apply((Applicative)$$0, NoiseRouter::new));

    private static RecordCodecBuilder<NoiseRouter, DensityFunction> field(String $$0, Function<NoiseRouter, DensityFunction> $$1) {
        return DensityFunction.HOLDER_HELPER_CODEC.fieldOf($$0).forGetter($$1);
    }

    public NoiseRouter mapAll(DensityFunction.Visitor $$0) {
        return new NoiseRouter(this.barrierNoise.mapAll($$0), this.fluidLevelFloodednessNoise.mapAll($$0), this.fluidLevelSpreadNoise.mapAll($$0), this.lavaNoise.mapAll($$0), this.temperature.mapAll($$0), this.vegetation.mapAll($$0), this.continents.mapAll($$0), this.erosion.mapAll($$0), this.depth.mapAll($$0), this.ridges.mapAll($$0), this.initialDensityWithoutJaggedness.mapAll($$0), this.finalDensity.mapAll($$0), this.veinToggle.mapAll($$0), this.veinRidged.mapAll($$0), this.veinGap.mapAll($$0));
    }
}