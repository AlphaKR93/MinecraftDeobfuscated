/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Set
 *  java.util.stream.Collectors
 */
package net.minecraft.world.flag;

import com.mojang.serialization.Codec;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlagRegistry;
import net.minecraft.world.flag.FeatureFlagSet;

public class FeatureFlags {
    public static final FeatureFlag VANILLA;
    public static final FeatureFlag BUNDLE;
    public static final FeatureFlag UPDATE_1_20;
    public static final FeatureFlagRegistry REGISTRY;
    public static final Codec<FeatureFlagSet> CODEC;
    public static final FeatureFlagSet VANILLA_SET;
    public static final FeatureFlagSet DEFAULT_FLAGS;

    public static String printMissingFlags(FeatureFlagSet $$0, FeatureFlagSet $$1) {
        return FeatureFlags.printMissingFlags(REGISTRY, $$0, $$1);
    }

    public static String printMissingFlags(FeatureFlagRegistry $$0, FeatureFlagSet $$12, FeatureFlagSet $$2) {
        Set<ResourceLocation> $$3 = $$0.toNames($$2);
        Set<ResourceLocation> $$4 = $$0.toNames($$12);
        return (String)$$3.stream().filter($$1 -> !$$4.contains($$1)).map(ResourceLocation::toString).collect(Collectors.joining((CharSequence)", "));
    }

    public static boolean isExperimental(FeatureFlagSet $$0) {
        return !$$0.isSubsetOf(VANILLA_SET);
    }

    static {
        FeatureFlagRegistry.Builder $$0 = new FeatureFlagRegistry.Builder("main");
        VANILLA = $$0.createVanilla("vanilla");
        BUNDLE = $$0.createVanilla("bundle");
        UPDATE_1_20 = $$0.createVanilla("update_1_20");
        REGISTRY = $$0.build();
        CODEC = REGISTRY.codec();
        DEFAULT_FLAGS = VANILLA_SET = FeatureFlagSet.of(VANILLA);
    }
}