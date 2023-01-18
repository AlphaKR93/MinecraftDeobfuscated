/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Float
 *  java.lang.Object
 */
package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class PointedDripstoneConfiguration
implements FeatureConfiguration {
    public static final Codec<PointedDripstoneConfiguration> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("chance_of_taller_dripstone").orElse((Object)Float.valueOf((float)0.2f)).forGetter($$0 -> Float.valueOf((float)$$0.chanceOfTallerDripstone)), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("chance_of_directional_spread").orElse((Object)Float.valueOf((float)0.7f)).forGetter($$0 -> Float.valueOf((float)$$0.chanceOfDirectionalSpread)), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("chance_of_spread_radius2").orElse((Object)Float.valueOf((float)0.5f)).forGetter($$0 -> Float.valueOf((float)$$0.chanceOfSpreadRadius2)), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("chance_of_spread_radius3").orElse((Object)Float.valueOf((float)0.5f)).forGetter($$0 -> Float.valueOf((float)$$0.chanceOfSpreadRadius3))).apply((Applicative)$$02, PointedDripstoneConfiguration::new));
    public final float chanceOfTallerDripstone;
    public final float chanceOfDirectionalSpread;
    public final float chanceOfSpreadRadius2;
    public final float chanceOfSpreadRadius3;

    public PointedDripstoneConfiguration(float $$0, float $$1, float $$2, float $$3) {
        this.chanceOfTallerDripstone = $$0;
        this.chanceOfDirectionalSpread = $$1;
        this.chanceOfSpreadRadius2 = $$2;
        this.chanceOfSpreadRadius3 = $$3;
    }
}