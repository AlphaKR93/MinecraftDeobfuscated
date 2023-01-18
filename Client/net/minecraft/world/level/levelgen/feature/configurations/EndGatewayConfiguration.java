/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.util.Optional
 */
package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class EndGatewayConfiguration
implements FeatureConfiguration {
    public static final Codec<EndGatewayConfiguration> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)BlockPos.CODEC.optionalFieldOf("exit").forGetter($$0 -> $$0.exit), (App)Codec.BOOL.fieldOf("exact").forGetter($$0 -> $$0.exact)).apply((Applicative)$$02, EndGatewayConfiguration::new));
    private final Optional<BlockPos> exit;
    private final boolean exact;

    private EndGatewayConfiguration(Optional<BlockPos> $$0, boolean $$1) {
        this.exit = $$0;
        this.exact = $$1;
    }

    public static EndGatewayConfiguration knownExit(BlockPos $$0, boolean $$1) {
        return new EndGatewayConfiguration((Optional<BlockPos>)Optional.of((Object)$$0), $$1);
    }

    public static EndGatewayConfiguration delayedExitSearch() {
        return new EndGatewayConfiguration((Optional<BlockPos>)Optional.empty(), false);
    }

    public Optional<BlockPos> getExit() {
        return this.exit;
    }

    public boolean isExitExact() {
        return this.exact;
    }
}