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
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.material.FluidState;

public class SpringConfiguration
implements FeatureConfiguration {
    public static final Codec<SpringConfiguration> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)FluidState.CODEC.fieldOf("state").forGetter($$0 -> $$0.state), (App)Codec.BOOL.fieldOf("requires_block_below").orElse((Object)true).forGetter($$0 -> $$0.requiresBlockBelow), (App)Codec.INT.fieldOf("rock_count").orElse((Object)4).forGetter($$0 -> $$0.rockCount), (App)Codec.INT.fieldOf("hole_count").orElse((Object)1).forGetter($$0 -> $$0.holeCount), (App)RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("valid_blocks").forGetter($$0 -> $$0.validBlocks)).apply((Applicative)$$02, SpringConfiguration::new));
    public final FluidState state;
    public final boolean requiresBlockBelow;
    public final int rockCount;
    public final int holeCount;
    public final HolderSet<Block> validBlocks;

    public SpringConfiguration(FluidState $$0, boolean $$1, int $$2, int $$3, HolderSet<Block> $$4) {
        this.state = $$0;
        this.requiresBlockBelow = $$1;
        this.rockCount = $$2;
        this.holeCount = $$3;
        this.validBlocks = $$4;
    }
}