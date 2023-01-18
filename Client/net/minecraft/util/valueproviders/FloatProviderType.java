/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.util.valueproviders;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.valueproviders.ClampedNormalFloat;
import net.minecraft.util.valueproviders.ConstantFloat;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.TrapezoidFloat;
import net.minecraft.util.valueproviders.UniformFloat;

public interface FloatProviderType<P extends FloatProvider> {
    public static final FloatProviderType<ConstantFloat> CONSTANT = FloatProviderType.register("constant", ConstantFloat.CODEC);
    public static final FloatProviderType<UniformFloat> UNIFORM = FloatProviderType.register("uniform", UniformFloat.CODEC);
    public static final FloatProviderType<ClampedNormalFloat> CLAMPED_NORMAL = FloatProviderType.register("clamped_normal", ClampedNormalFloat.CODEC);
    public static final FloatProviderType<TrapezoidFloat> TRAPEZOID = FloatProviderType.register("trapezoid", TrapezoidFloat.CODEC);

    public Codec<P> codec();

    public static <P extends FloatProvider> FloatProviderType<P> register(String $$0, Codec<P> $$1) {
        return Registry.register(BuiltInRegistries.FLOAT_PROVIDER_TYPE, $$0, () -> $$1);
    }
}