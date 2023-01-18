/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Either
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.world.level.levelgen.heightproviders;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;

public class ConstantHeight
extends HeightProvider {
    public static final ConstantHeight ZERO = new ConstantHeight(VerticalAnchor.absolute(0));
    public static final Codec<ConstantHeight> CODEC = Codec.either(VerticalAnchor.CODEC, (Codec)RecordCodecBuilder.create($$02 -> $$02.group((App)VerticalAnchor.CODEC.fieldOf("value").forGetter($$0 -> $$0.value)).apply((Applicative)$$02, ConstantHeight::new))).xmap($$02 -> (ConstantHeight)$$02.map(ConstantHeight::of, $$0 -> $$0), $$0 -> Either.left((Object)$$0.value));
    private final VerticalAnchor value;

    public static ConstantHeight of(VerticalAnchor $$0) {
        return new ConstantHeight($$0);
    }

    private ConstantHeight(VerticalAnchor $$0) {
        this.value = $$0;
    }

    public VerticalAnchor getValue() {
        return this.value;
    }

    @Override
    public int sample(RandomSource $$0, WorldGenerationContext $$1) {
        return this.value.resolveY($$1);
    }

    @Override
    public HeightProviderType<?> getType() {
        return HeightProviderType.CONSTANT;
    }

    public String toString() {
        return this.value.toString();
    }
}