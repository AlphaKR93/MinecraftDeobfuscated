/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.data.registries;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPatterns;

public class UpdateOneTwentyRegistries {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder().add(Registries.TRIM_MATERIAL, TrimMaterials::nextUpdate).add(Registries.TRIM_PATTERN, TrimPatterns::nextUpdate);

    public static HolderLookup.Provider createLookup() {
        RegistryAccess.Frozen $$0 = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
        return BUILDER.build($$0);
    }
}