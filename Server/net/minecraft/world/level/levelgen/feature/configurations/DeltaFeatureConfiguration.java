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
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class DeltaFeatureConfiguration
implements FeatureConfiguration {
    public static final Codec<DeltaFeatureConfiguration> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)BlockState.CODEC.fieldOf("contents").forGetter($$0 -> $$0.contents), (App)BlockState.CODEC.fieldOf("rim").forGetter($$0 -> $$0.rim), (App)IntProvider.codec(0, 16).fieldOf("size").forGetter($$0 -> $$0.size), (App)IntProvider.codec(0, 16).fieldOf("rim_size").forGetter($$0 -> $$0.rimSize)).apply((Applicative)$$02, DeltaFeatureConfiguration::new));
    private final BlockState contents;
    private final BlockState rim;
    private final IntProvider size;
    private final IntProvider rimSize;

    public DeltaFeatureConfiguration(BlockState $$0, BlockState $$1, IntProvider $$2, IntProvider $$3) {
        this.contents = $$0;
        this.rim = $$1;
        this.size = $$2;
        this.rimSize = $$3;
    }

    public BlockState contents() {
        return this.contents;
    }

    public BlockState rim() {
        return this.rim;
    }

    public IntProvider size() {
        return this.size;
    }

    public IntProvider rimSize() {
        return this.rimSize;
    }
}