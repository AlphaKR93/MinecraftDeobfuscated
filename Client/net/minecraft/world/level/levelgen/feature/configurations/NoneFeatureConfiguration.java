/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 */
package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class NoneFeatureConfiguration
implements FeatureConfiguration {
    public static final Codec<NoneFeatureConfiguration> CODEC = Codec.unit(() -> INSTANCE);
    public static final NoneFeatureConfiguration INSTANCE = new NoneFeatureConfiguration();
}