/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.List
 *  java.util.Optional
 */
package net.minecraft.data.worldgen;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.BuiltinStructureSets;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;

public interface StructureSets {
    public static void bootstrap(BootstapContext<StructureSet> $$0) {
        HolderGetter<Structure> $$1 = $$0.lookup(Registries.STRUCTURE);
        HolderGetter<Biome> $$2 = $$0.lookup(Registries.BIOME);
        Holder.Reference<StructureSet> $$3 = $$0.register(BuiltinStructureSets.VILLAGES, new StructureSet((List<StructureSet.StructureSelectionEntry>)List.of((Object)((Object)StructureSet.entry($$1.getOrThrow(BuiltinStructures.VILLAGE_PLAINS))), (Object)((Object)StructureSet.entry($$1.getOrThrow(BuiltinStructures.VILLAGE_DESERT))), (Object)((Object)StructureSet.entry($$1.getOrThrow(BuiltinStructures.VILLAGE_SAVANNA))), (Object)((Object)StructureSet.entry($$1.getOrThrow(BuiltinStructures.VILLAGE_SNOWY))), (Object)((Object)StructureSet.entry($$1.getOrThrow(BuiltinStructures.VILLAGE_TAIGA)))), (StructurePlacement)new RandomSpreadStructurePlacement(34, 8, RandomSpreadType.LINEAR, 10387312)));
        $$0.register(BuiltinStructureSets.DESERT_PYRAMIDS, new StructureSet($$1.getOrThrow(BuiltinStructures.DESERT_PYRAMID), (StructurePlacement)new RandomSpreadStructurePlacement(32, 8, RandomSpreadType.LINEAR, 14357617)));
        $$0.register(BuiltinStructureSets.IGLOOS, new StructureSet($$1.getOrThrow(BuiltinStructures.IGLOO), (StructurePlacement)new RandomSpreadStructurePlacement(32, 8, RandomSpreadType.LINEAR, 14357618)));
        $$0.register(BuiltinStructureSets.JUNGLE_TEMPLES, new StructureSet($$1.getOrThrow(BuiltinStructures.JUNGLE_TEMPLE), (StructurePlacement)new RandomSpreadStructurePlacement(32, 8, RandomSpreadType.LINEAR, 14357619)));
        $$0.register(BuiltinStructureSets.SWAMP_HUTS, new StructureSet($$1.getOrThrow(BuiltinStructures.SWAMP_HUT), (StructurePlacement)new RandomSpreadStructurePlacement(32, 8, RandomSpreadType.LINEAR, 14357620)));
        $$0.register(BuiltinStructureSets.PILLAGER_OUTPOSTS, new StructureSet($$1.getOrThrow(BuiltinStructures.PILLAGER_OUTPOST), (StructurePlacement)new RandomSpreadStructurePlacement(Vec3i.ZERO, StructurePlacement.FrequencyReductionMethod.LEGACY_TYPE_1, 0.2f, 165745296, (Optional<StructurePlacement.ExclusionZone>)Optional.of((Object)((Object)new StructurePlacement.ExclusionZone($$3, 10))), 32, 8, RandomSpreadType.LINEAR)));
        $$0.register(BuiltinStructureSets.ANCIENT_CITIES, new StructureSet($$1.getOrThrow(BuiltinStructures.ANCIENT_CITY), (StructurePlacement)new RandomSpreadStructurePlacement(24, 8, RandomSpreadType.LINEAR, 20083232)));
        $$0.register(BuiltinStructureSets.OCEAN_MONUMENTS, new StructureSet($$1.getOrThrow(BuiltinStructures.OCEAN_MONUMENT), (StructurePlacement)new RandomSpreadStructurePlacement(32, 5, RandomSpreadType.TRIANGULAR, 10387313)));
        $$0.register(BuiltinStructureSets.WOODLAND_MANSIONS, new StructureSet($$1.getOrThrow(BuiltinStructures.WOODLAND_MANSION), (StructurePlacement)new RandomSpreadStructurePlacement(80, 20, RandomSpreadType.TRIANGULAR, 10387319)));
        $$0.register(BuiltinStructureSets.BURIED_TREASURES, new StructureSet($$1.getOrThrow(BuiltinStructures.BURIED_TREASURE), (StructurePlacement)new RandomSpreadStructurePlacement(new Vec3i(9, 0, 9), StructurePlacement.FrequencyReductionMethod.LEGACY_TYPE_2, 0.01f, 0, (Optional<StructurePlacement.ExclusionZone>)Optional.empty(), 1, 0, RandomSpreadType.LINEAR)));
        $$0.register(BuiltinStructureSets.MINESHAFTS, new StructureSet((List<StructureSet.StructureSelectionEntry>)List.of((Object)((Object)StructureSet.entry($$1.getOrThrow(BuiltinStructures.MINESHAFT))), (Object)((Object)StructureSet.entry($$1.getOrThrow(BuiltinStructures.MINESHAFT_MESA)))), (StructurePlacement)new RandomSpreadStructurePlacement(Vec3i.ZERO, StructurePlacement.FrequencyReductionMethod.LEGACY_TYPE_3, 0.004f, 0, (Optional<StructurePlacement.ExclusionZone>)Optional.empty(), 1, 0, RandomSpreadType.LINEAR)));
        $$0.register(BuiltinStructureSets.RUINED_PORTALS, new StructureSet((List<StructureSet.StructureSelectionEntry>)List.of((Object)((Object)StructureSet.entry($$1.getOrThrow(BuiltinStructures.RUINED_PORTAL_STANDARD))), (Object)((Object)StructureSet.entry($$1.getOrThrow(BuiltinStructures.RUINED_PORTAL_DESERT))), (Object)((Object)StructureSet.entry($$1.getOrThrow(BuiltinStructures.RUINED_PORTAL_JUNGLE))), (Object)((Object)StructureSet.entry($$1.getOrThrow(BuiltinStructures.RUINED_PORTAL_SWAMP))), (Object)((Object)StructureSet.entry($$1.getOrThrow(BuiltinStructures.RUINED_PORTAL_MOUNTAIN))), (Object)((Object)StructureSet.entry($$1.getOrThrow(BuiltinStructures.RUINED_PORTAL_OCEAN))), (Object)((Object)StructureSet.entry($$1.getOrThrow(BuiltinStructures.RUINED_PORTAL_NETHER)))), (StructurePlacement)new RandomSpreadStructurePlacement(40, 15, RandomSpreadType.LINEAR, 34222645)));
        $$0.register(BuiltinStructureSets.SHIPWRECKS, new StructureSet((List<StructureSet.StructureSelectionEntry>)List.of((Object)((Object)StructureSet.entry($$1.getOrThrow(BuiltinStructures.SHIPWRECK))), (Object)((Object)StructureSet.entry($$1.getOrThrow(BuiltinStructures.SHIPWRECK_BEACHED)))), (StructurePlacement)new RandomSpreadStructurePlacement(24, 4, RandomSpreadType.LINEAR, 165745295)));
        $$0.register(BuiltinStructureSets.OCEAN_RUINS, new StructureSet((List<StructureSet.StructureSelectionEntry>)List.of((Object)((Object)StructureSet.entry($$1.getOrThrow(BuiltinStructures.OCEAN_RUIN_COLD))), (Object)((Object)StructureSet.entry($$1.getOrThrow(BuiltinStructures.OCEAN_RUIN_WARM)))), (StructurePlacement)new RandomSpreadStructurePlacement(20, 8, RandomSpreadType.LINEAR, 14357621)));
        $$0.register(BuiltinStructureSets.NETHER_COMPLEXES, new StructureSet((List<StructureSet.StructureSelectionEntry>)List.of((Object)((Object)StructureSet.entry($$1.getOrThrow(BuiltinStructures.FORTRESS), 2)), (Object)((Object)StructureSet.entry($$1.getOrThrow(BuiltinStructures.BASTION_REMNANT), 3))), (StructurePlacement)new RandomSpreadStructurePlacement(27, 4, RandomSpreadType.LINEAR, 30084232)));
        $$0.register(BuiltinStructureSets.NETHER_FOSSILS, new StructureSet($$1.getOrThrow(BuiltinStructures.NETHER_FOSSIL), (StructurePlacement)new RandomSpreadStructurePlacement(2, 1, RandomSpreadType.LINEAR, 14357921)));
        $$0.register(BuiltinStructureSets.END_CITIES, new StructureSet($$1.getOrThrow(BuiltinStructures.END_CITY), (StructurePlacement)new RandomSpreadStructurePlacement(20, 11, RandomSpreadType.TRIANGULAR, 10387313)));
        $$0.register(BuiltinStructureSets.STRONGHOLDS, new StructureSet($$1.getOrThrow(BuiltinStructures.STRONGHOLD), (StructurePlacement)new ConcentricRingsStructurePlacement(32, 3, 128, $$2.getOrThrow(BiomeTags.STRONGHOLD_BIASED_TO))));
    }
}