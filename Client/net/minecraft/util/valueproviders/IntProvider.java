/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.util.valueproviders;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProviderType;

public abstract class IntProvider {
    private static final Codec<Either<Integer, IntProvider>> CONSTANT_OR_DISPATCH_CODEC = Codec.either((Codec)Codec.INT, (Codec)BuiltInRegistries.INT_PROVIDER_TYPE.byNameCodec().dispatch(IntProvider::getType, IntProviderType::codec));
    public static final Codec<IntProvider> CODEC = CONSTANT_OR_DISPATCH_CODEC.xmap($$02 -> (IntProvider)$$02.map(ConstantInt::of, $$0 -> $$0), $$0 -> $$0.getType() == IntProviderType.CONSTANT ? Either.left((Object)((ConstantInt)$$0).getValue()) : Either.right((Object)$$0));
    public static final Codec<IntProvider> NON_NEGATIVE_CODEC = IntProvider.codec(0, Integer.MAX_VALUE);
    public static final Codec<IntProvider> POSITIVE_CODEC = IntProvider.codec(1, Integer.MAX_VALUE);

    public static Codec<IntProvider> codec(int $$0, int $$1) {
        return ExtraCodecs.validate(CODEC, $$2 -> {
            if ($$2.getMinValue() < $$0) {
                return DataResult.error((String)("Value provider too low: " + $$0 + " [" + $$2.getMinValue() + "-" + $$2.getMaxValue() + "]"));
            }
            if ($$2.getMaxValue() > $$1) {
                return DataResult.error((String)("Value provider too high: " + $$1 + " [" + $$2.getMinValue() + "-" + $$2.getMaxValue() + "]"));
            }
            return DataResult.success((Object)$$2);
        });
    }

    public abstract int sample(RandomSource var1);

    public abstract int getMinValue();

    public abstract int getMaxValue();

    public abstract IntProviderType<?> getType();
}