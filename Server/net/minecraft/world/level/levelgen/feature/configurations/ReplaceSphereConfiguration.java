/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 */
package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class ReplaceSphereConfiguration
implements FeatureConfiguration {
    public static final Codec<ReplaceSphereConfiguration> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)BlockState.CODEC.fieldOf("target").forGetter($$0 -> $$0.targetState), (App)BlockState.CODEC.fieldOf("state").forGetter($$0 -> $$0.replaceState), (App)IntProvider.codec(0, 12).fieldOf("radius").forGetter($$0 -> $$0.radius)).apply((Applicative)$$02, ReplaceSphereConfiguration::new));
    public final BlockState targetState;
    public final BlockState replaceState;
    private final IntProvider radius;

    public ReplaceSphereConfiguration(BlockState $$0, BlockState $$1, IntProvider $$2) {
        this.targetState = $$0;
        this.replaceState = $$1;
        this.radius = $$2;
    }

    public IntProvider radius() {
        return this.radius;
    }
}