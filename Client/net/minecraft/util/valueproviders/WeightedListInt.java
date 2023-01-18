/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 */
package net.minecraft.util.valueproviders;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviderType;

public class WeightedListInt
extends IntProvider {
    public static final Codec<WeightedListInt> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)SimpleWeightedRandomList.wrappedCodec(IntProvider.CODEC).fieldOf("distribution").forGetter($$0 -> $$0.distribution)).apply((Applicative)$$02, WeightedListInt::new));
    private final SimpleWeightedRandomList<IntProvider> distribution;
    private final int minValue;
    private final int maxValue;

    public WeightedListInt(SimpleWeightedRandomList<IntProvider> $$0) {
        this.distribution = $$0;
        List $$1 = $$0.unwrap();
        int $$2 = Integer.MAX_VALUE;
        int $$3 = Integer.MIN_VALUE;
        for (WeightedEntry.Wrapper $$4 : $$1) {
            int $$5 = ((IntProvider)$$4.getData()).getMinValue();
            int $$6 = ((IntProvider)$$4.getData()).getMaxValue();
            $$2 = Math.min((int)$$2, (int)$$5);
            $$3 = Math.max((int)$$3, (int)$$6);
        }
        this.minValue = $$2;
        this.maxValue = $$3;
    }

    @Override
    public int sample(RandomSource $$0) {
        return ((IntProvider)this.distribution.getRandomValue($$0).orElseThrow(IllegalStateException::new)).sample($$0);
    }

    @Override
    public int getMinValue() {
        return this.minValue;
    }

    @Override
    public int getMaxValue() {
        return this.maxValue;
    }

    @Override
    public IntProviderType<?> getType() {
        return IntProviderType.WEIGHTED_LIST;
    }
}