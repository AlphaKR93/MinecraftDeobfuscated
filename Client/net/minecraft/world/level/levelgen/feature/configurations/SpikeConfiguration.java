/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.util.List
 *  java.util.Optional
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class SpikeConfiguration
implements FeatureConfiguration {
    public static final Codec<SpikeConfiguration> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.BOOL.fieldOf("crystal_invulnerable").orElse((Object)false).forGetter($$0 -> $$0.crystalInvulnerable), (App)SpikeFeature.EndSpike.CODEC.listOf().fieldOf("spikes").forGetter($$0 -> $$0.spikes), (App)BlockPos.CODEC.optionalFieldOf("crystal_beam_target").forGetter($$0 -> Optional.ofNullable((Object)$$0.crystalBeamTarget))).apply((Applicative)$$02, SpikeConfiguration::new));
    private final boolean crystalInvulnerable;
    private final List<SpikeFeature.EndSpike> spikes;
    @Nullable
    private final BlockPos crystalBeamTarget;

    public SpikeConfiguration(boolean $$0, List<SpikeFeature.EndSpike> $$1, @Nullable BlockPos $$2) {
        this($$0, $$1, (Optional<BlockPos>)Optional.ofNullable((Object)$$2));
    }

    private SpikeConfiguration(boolean $$0, List<SpikeFeature.EndSpike> $$1, Optional<BlockPos> $$2) {
        this.crystalInvulnerable = $$0;
        this.spikes = $$1;
        this.crystalBeamTarget = (BlockPos)$$2.orElse(null);
    }

    public boolean isCrystalInvulnerable() {
        return this.crystalInvulnerable;
    }

    public List<SpikeFeature.EndSpike> getSpikes() {
        return this.spikes;
    }

    @Nullable
    public BlockPos getCrystalBeamTarget() {
        return this.crystalBeamTarget;
    }
}