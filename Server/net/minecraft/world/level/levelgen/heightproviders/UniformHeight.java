/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
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
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import org.slf4j.Logger;

public class UniformHeight
extends HeightProvider {
    public static final Codec<UniformHeight> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)VerticalAnchor.CODEC.fieldOf("min_inclusive").forGetter($$0 -> $$0.minInclusive), (App)VerticalAnchor.CODEC.fieldOf("max_inclusive").forGetter($$0 -> $$0.maxInclusive)).apply((Applicative)$$02, UniformHeight::new));
    private static final Logger LOGGER = LogUtils.getLogger();
    private final VerticalAnchor minInclusive;
    private final VerticalAnchor maxInclusive;
    private final LongSet warnedFor = new LongOpenHashSet();

    private UniformHeight(VerticalAnchor $$0, VerticalAnchor $$1) {
        this.minInclusive = $$0;
        this.maxInclusive = $$1;
    }

    public static UniformHeight of(VerticalAnchor $$0, VerticalAnchor $$1) {
        return new UniformHeight($$0, $$1);
    }

    @Override
    public int sample(RandomSource $$0, WorldGenerationContext $$1) {
        int $$3;
        int $$2 = this.minInclusive.resolveY($$1);
        if ($$2 > ($$3 = this.maxInclusive.resolveY($$1))) {
            if (this.warnedFor.add((long)$$2 << 32 | (long)$$3)) {
                LOGGER.warn("Empty height range: {}", (Object)this);
            }
            return $$2;
        }
        return Mth.randomBetweenInclusive($$0, $$2, $$3);
    }

    @Override
    public HeightProviderType<?> getType() {
        return HeightProviderType.UNIFORM;
    }

    public String toString() {
        return "[" + this.minInclusive + "-" + this.maxInclusive + "]";
    }
}