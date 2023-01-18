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
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.feature.configurations.BlockPileConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class NetherForestVegetationConfig
extends BlockPileConfiguration {
    public static final Codec<NetherForestVegetationConfig> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)BlockStateProvider.CODEC.fieldOf("state_provider").forGetter($$0 -> $$0.stateProvider), (App)ExtraCodecs.POSITIVE_INT.fieldOf("spread_width").forGetter($$0 -> $$0.spreadWidth), (App)ExtraCodecs.POSITIVE_INT.fieldOf("spread_height").forGetter($$0 -> $$0.spreadHeight)).apply((Applicative)$$02, NetherForestVegetationConfig::new));
    public final int spreadWidth;
    public final int spreadHeight;

    public NetherForestVegetationConfig(BlockStateProvider $$0, int $$1, int $$2) {
        super($$0);
        this.spreadWidth = $$1;
        this.spreadHeight = $$2;
    }
}