/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.grower;

import javax.annotation.Nullable;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class MangroveTreeGrower
extends AbstractTreeGrower {
    private final float tallProbability;

    public MangroveTreeGrower(float $$0) {
        this.tallProbability = $$0;
    }

    @Override
    @Nullable
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource $$0, boolean $$1) {
        if ($$0.nextFloat() < this.tallProbability) {
            return TreeFeatures.TALL_MANGROVE;
        }
        return TreeFeatures.MANGROVE;
    }
}