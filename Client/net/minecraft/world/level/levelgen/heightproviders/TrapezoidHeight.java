/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import org.slf4j.Logger;

public class TrapezoidHeight
extends HeightProvider {
    public static final Codec<TrapezoidHeight> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)VerticalAnchor.CODEC.fieldOf("min_inclusive").forGetter($$0 -> $$0.minInclusive), (App)VerticalAnchor.CODEC.fieldOf("max_inclusive").forGetter($$0 -> $$0.maxInclusive), (App)Codec.INT.optionalFieldOf("plateau", (Object)0).forGetter($$0 -> $$0.plateau)).apply((Applicative)$$02, TrapezoidHeight::new));
    private static final Logger LOGGER = LogUtils.getLogger();
    private final VerticalAnchor minInclusive;
    private final VerticalAnchor maxInclusive;
    private final int plateau;

    private TrapezoidHeight(VerticalAnchor $$0, VerticalAnchor $$1, int $$2) {
        this.minInclusive = $$0;
        this.maxInclusive = $$1;
        this.plateau = $$2;
    }

    public static TrapezoidHeight of(VerticalAnchor $$0, VerticalAnchor $$1, int $$2) {
        return new TrapezoidHeight($$0, $$1, $$2);
    }

    public static TrapezoidHeight of(VerticalAnchor $$0, VerticalAnchor $$1) {
        return TrapezoidHeight.of($$0, $$1, 0);
    }

    @Override
    public int sample(RandomSource $$0, WorldGenerationContext $$1) {
        int $$3;
        int $$2 = this.minInclusive.resolveY($$1);
        if ($$2 > ($$3 = this.maxInclusive.resolveY($$1))) {
            LOGGER.warn("Empty height range: {}", (Object)this);
            return $$2;
        }
        int $$4 = $$3 - $$2;
        if (this.plateau >= $$4) {
            return Mth.randomBetweenInclusive($$0, $$2, $$3);
        }
        int $$5 = ($$4 - this.plateau) / 2;
        int $$6 = $$4 - $$5;
        return $$2 + Mth.randomBetweenInclusive($$0, 0, $$6) + Mth.randomBetweenInclusive($$0, 0, $$5);
    }

    @Override
    public HeightProviderType<?> getType() {
        return HeightProviderType.TRAPEZOID;
    }

    public String toString() {
        if (this.plateau == 0) {
            return "triangle (" + this.minInclusive + "-" + this.maxInclusive + ")";
        }
        return "trapezoid(" + this.plateau + ") in [" + this.minInclusive + "-" + this.maxInclusive + "]";
    }
}