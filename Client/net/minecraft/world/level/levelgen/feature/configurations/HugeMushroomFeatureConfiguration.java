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
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class HugeMushroomFeatureConfiguration
implements FeatureConfiguration {
    public static final Codec<HugeMushroomFeatureConfiguration> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)BlockStateProvider.CODEC.fieldOf("cap_provider").forGetter($$0 -> $$0.capProvider), (App)BlockStateProvider.CODEC.fieldOf("stem_provider").forGetter($$0 -> $$0.stemProvider), (App)Codec.INT.fieldOf("foliage_radius").orElse((Object)2).forGetter($$0 -> $$0.foliageRadius)).apply((Applicative)$$02, HugeMushroomFeatureConfiguration::new));
    public final BlockStateProvider capProvider;
    public final BlockStateProvider stemProvider;
    public final int foliageRadius;

    public HugeMushroomFeatureConfiguration(BlockStateProvider $$0, BlockStateProvider $$1, int $$2) {
        this.capProvider = $$0;
        this.stemProvider = $$1;
        this.foliageRadius = $$2;
    }
}