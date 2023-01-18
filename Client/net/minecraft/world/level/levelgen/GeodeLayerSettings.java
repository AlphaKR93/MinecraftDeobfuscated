/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Double
 *  java.lang.Object
 */
package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class GeodeLayerSettings {
    private static final Codec<Double> LAYER_RANGE = Codec.doubleRange((double)0.01, (double)50.0);
    public static final Codec<GeodeLayerSettings> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)LAYER_RANGE.fieldOf("filling").orElse((Object)1.7).forGetter($$0 -> $$0.filling), (App)LAYER_RANGE.fieldOf("inner_layer").orElse((Object)2.2).forGetter($$0 -> $$0.innerLayer), (App)LAYER_RANGE.fieldOf("middle_layer").orElse((Object)3.2).forGetter($$0 -> $$0.middleLayer), (App)LAYER_RANGE.fieldOf("outer_layer").orElse((Object)4.2).forGetter($$0 -> $$0.outerLayer)).apply((Applicative)$$02, GeodeLayerSettings::new));
    public final double filling;
    public final double innerLayer;
    public final double middleLayer;
    public final double outerLayer;

    public GeodeLayerSettings(double $$0, double $$1, double $$2, double $$3) {
        this.filling = $$0;
        this.innerLayer = $$1;
        this.middleLayer = $$2;
        this.outerLayer = $$3;
    }
}