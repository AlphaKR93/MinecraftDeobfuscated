/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.world.level.levelgen.structure.placement;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

public interface StructurePlacementType<SP extends StructurePlacement> {
    public static final StructurePlacementType<RandomSpreadStructurePlacement> RANDOM_SPREAD = StructurePlacementType.register("random_spread", RandomSpreadStructurePlacement.CODEC);
    public static final StructurePlacementType<ConcentricRingsStructurePlacement> CONCENTRIC_RINGS = StructurePlacementType.register("concentric_rings", ConcentricRingsStructurePlacement.CODEC);

    public Codec<SP> codec();

    private static <SP extends StructurePlacement> StructurePlacementType<SP> register(String $$0, Codec<SP> $$1) {
        return Registry.register(BuiltInRegistries.STRUCTURE_PLACEMENT, $$0, () -> $$1);
    }
}