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
 *  java.util.OptionalInt
 */
package net.minecraft.world.level.levelgen.feature.featuresize;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.OptionalInt;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSize;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeType;

public class ThreeLayersFeatureSize
extends FeatureSize {
    public static final Codec<ThreeLayersFeatureSize> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.intRange((int)0, (int)80).fieldOf("limit").orElse((Object)1).forGetter($$0 -> $$0.limit), (App)Codec.intRange((int)0, (int)80).fieldOf("upper_limit").orElse((Object)1).forGetter($$0 -> $$0.upperLimit), (App)Codec.intRange((int)0, (int)16).fieldOf("lower_size").orElse((Object)0).forGetter($$0 -> $$0.lowerSize), (App)Codec.intRange((int)0, (int)16).fieldOf("middle_size").orElse((Object)1).forGetter($$0 -> $$0.middleSize), (App)Codec.intRange((int)0, (int)16).fieldOf("upper_size").orElse((Object)1).forGetter($$0 -> $$0.upperSize), ThreeLayersFeatureSize.minClippedHeightCodec()).apply((Applicative)$$02, ThreeLayersFeatureSize::new));
    private final int limit;
    private final int upperLimit;
    private final int lowerSize;
    private final int middleSize;
    private final int upperSize;

    public ThreeLayersFeatureSize(int $$0, int $$1, int $$2, int $$3, int $$4, OptionalInt $$5) {
        super($$5);
        this.limit = $$0;
        this.upperLimit = $$1;
        this.lowerSize = $$2;
        this.middleSize = $$3;
        this.upperSize = $$4;
    }

    @Override
    protected FeatureSizeType<?> type() {
        return FeatureSizeType.THREE_LAYERS_FEATURE_SIZE;
    }

    @Override
    public int getSizeAtHeight(int $$0, int $$1) {
        if ($$1 < this.limit) {
            return this.lowerSize;
        }
        if ($$1 >= $$0 - this.upperLimit) {
            return this.upperSize;
        }
        return this.middleSize;
    }
}