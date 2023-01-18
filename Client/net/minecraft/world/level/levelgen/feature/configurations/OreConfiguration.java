/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Float
 *  java.lang.Object
 *  java.util.List
 */
package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

public class OreConfiguration
implements FeatureConfiguration {
    public static final Codec<OreConfiguration> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.list(TargetBlockState.CODEC).fieldOf("targets").forGetter($$0 -> $$0.targetStates), (App)Codec.intRange((int)0, (int)64).fieldOf("size").forGetter($$0 -> $$0.size), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("discard_chance_on_air_exposure").forGetter($$0 -> Float.valueOf((float)$$0.discardChanceOnAirExposure))).apply((Applicative)$$02, OreConfiguration::new));
    public final List<TargetBlockState> targetStates;
    public final int size;
    public final float discardChanceOnAirExposure;

    public OreConfiguration(List<TargetBlockState> $$0, int $$1, float $$2) {
        this.size = $$1;
        this.targetStates = $$0;
        this.discardChanceOnAirExposure = $$2;
    }

    public OreConfiguration(List<TargetBlockState> $$0, int $$1) {
        this($$0, $$1, 0.0f);
    }

    public OreConfiguration(RuleTest $$0, BlockState $$1, int $$2, float $$3) {
        this((List<TargetBlockState>)ImmutableList.of((Object)new TargetBlockState($$0, $$1)), $$2, $$3);
    }

    public OreConfiguration(RuleTest $$0, BlockState $$1, int $$2) {
        this((List<TargetBlockState>)ImmutableList.of((Object)new TargetBlockState($$0, $$1)), $$2, 0.0f);
    }

    public static TargetBlockState target(RuleTest $$0, BlockState $$1) {
        return new TargetBlockState($$0, $$1);
    }

    public static class TargetBlockState {
        public static final Codec<TargetBlockState> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)RuleTest.CODEC.fieldOf("target").forGetter($$0 -> $$0.target), (App)BlockState.CODEC.fieldOf("state").forGetter($$0 -> $$0.state)).apply((Applicative)$$02, TargetBlockState::new));
        public final RuleTest target;
        public final BlockState state;

        TargetBlockState(RuleTest $$0, BlockState $$1) {
            this.target = $$0;
            this.state = $$1;
        }
    }
}