/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.Arrays
 *  java.util.List
 *  java.util.Map
 *  java.util.Optional
 *  java.util.stream.Collectors
 */
package net.minecraft.data.worldgen;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.AncientCityStructurePieces;
import net.minecraft.data.worldgen.BastionPieces;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.DesertVillagePools;
import net.minecraft.data.worldgen.PillagerOutpostPools;
import net.minecraft.data.worldgen.PlainVillagePools;
import net.minecraft.data.worldgen.SavannaVillagePools;
import net.minecraft.data.worldgen.SnowyVillagePools;
import net.minecraft.data.worldgen.TaigaVillagePools;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.structures.BuriedTreasureStructure;
import net.minecraft.world.level.levelgen.structure.structures.DesertPyramidStructure;
import net.minecraft.world.level.levelgen.structure.structures.EndCityStructure;
import net.minecraft.world.level.levelgen.structure.structures.IglooStructure;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.structures.JungleTempleStructure;
import net.minecraft.world.level.levelgen.structure.structures.MineshaftStructure;
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressStructure;
import net.minecraft.world.level.levelgen.structure.structures.NetherFossilStructure;
import net.minecraft.world.level.levelgen.structure.structures.OceanMonumentStructure;
import net.minecraft.world.level.levelgen.structure.structures.OceanRuinStructure;
import net.minecraft.world.level.levelgen.structure.structures.RuinedPortalPiece;
import net.minecraft.world.level.levelgen.structure.structures.RuinedPortalStructure;
import net.minecraft.world.level.levelgen.structure.structures.ShipwreckStructure;
import net.minecraft.world.level.levelgen.structure.structures.StrongholdStructure;
import net.minecraft.world.level.levelgen.structure.structures.SwampHutStructure;
import net.minecraft.world.level.levelgen.structure.structures.WoodlandMansionStructure;

public class Structures {
    private static Structure.StructureSettings structure(HolderSet<Biome> $$0, Map<MobCategory, StructureSpawnOverride> $$1, GenerationStep.Decoration $$2, TerrainAdjustment $$3) {
        return new Structure.StructureSettings($$0, $$1, $$2, $$3);
    }

    private static Structure.StructureSettings structure(HolderSet<Biome> $$0, GenerationStep.Decoration $$1, TerrainAdjustment $$2) {
        return Structures.structure($$0, (Map<MobCategory, StructureSpawnOverride>)Map.of(), $$1, $$2);
    }

    private static Structure.StructureSettings structure(HolderSet<Biome> $$0, TerrainAdjustment $$1) {
        return Structures.structure($$0, (Map<MobCategory, StructureSpawnOverride>)Map.of(), GenerationStep.Decoration.SURFACE_STRUCTURES, $$1);
    }

    public static void bootstrap(BootstapContext<Structure> $$02) {
        HolderGetter<Biome> $$1 = $$02.lookup(Registries.BIOME);
        HolderGetter<StructureTemplatePool> $$2 = $$02.lookup(Registries.TEMPLATE_POOL);
        $$02.register(BuiltinStructures.PILLAGER_OUTPOST, new JigsawStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_PILLAGER_OUTPOST), (Map<MobCategory, StructureSpawnOverride>)Map.of((Object)MobCategory.MONSTER, (Object)((Object)new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, WeightedRandomList.create((WeightedEntry[])new MobSpawnSettings.SpawnerData[]{new MobSpawnSettings.SpawnerData(EntityType.PILLAGER, 1, 1, 1)})))), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.BEARD_THIN), $$2.getOrThrow(PillagerOutpostPools.START), 7, ConstantHeight.of(VerticalAnchor.absolute(0)), true, Heightmap.Types.WORLD_SURFACE_WG));
        $$02.register(BuiltinStructures.MINESHAFT, new MineshaftStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_MINESHAFT), GenerationStep.Decoration.UNDERGROUND_STRUCTURES, TerrainAdjustment.NONE), MineshaftStructure.Type.NORMAL));
        $$02.register(BuiltinStructures.MINESHAFT_MESA, new MineshaftStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_MINESHAFT_MESA), GenerationStep.Decoration.UNDERGROUND_STRUCTURES, TerrainAdjustment.NONE), MineshaftStructure.Type.MESA));
        $$02.register(BuiltinStructures.WOODLAND_MANSION, new WoodlandMansionStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_WOODLAND_MANSION), TerrainAdjustment.NONE)));
        $$02.register(BuiltinStructures.JUNGLE_TEMPLE, new JungleTempleStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_JUNGLE_TEMPLE), TerrainAdjustment.NONE)));
        $$02.register(BuiltinStructures.DESERT_PYRAMID, new DesertPyramidStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_DESERT_PYRAMID), TerrainAdjustment.NONE)));
        $$02.register(BuiltinStructures.IGLOO, new IglooStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_IGLOO), TerrainAdjustment.NONE)));
        $$02.register(BuiltinStructures.SHIPWRECK, new ShipwreckStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_SHIPWRECK), TerrainAdjustment.NONE), false));
        $$02.register(BuiltinStructures.SHIPWRECK_BEACHED, new ShipwreckStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_SHIPWRECK_BEACHED), TerrainAdjustment.NONE), true));
        $$02.register(BuiltinStructures.SWAMP_HUT, new SwampHutStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_SWAMP_HUT), (Map<MobCategory, StructureSpawnOverride>)Map.of((Object)MobCategory.MONSTER, (Object)((Object)new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.PIECE, WeightedRandomList.create((WeightedEntry[])new MobSpawnSettings.SpawnerData[]{new MobSpawnSettings.SpawnerData(EntityType.WITCH, 1, 1, 1)}))), (Object)MobCategory.CREATURE, (Object)((Object)new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.PIECE, WeightedRandomList.create((WeightedEntry[])new MobSpawnSettings.SpawnerData[]{new MobSpawnSettings.SpawnerData(EntityType.CAT, 1, 1, 1)})))), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
        $$02.register(BuiltinStructures.STRONGHOLD, new StrongholdStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_STRONGHOLD), TerrainAdjustment.BURY)));
        $$02.register(BuiltinStructures.OCEAN_MONUMENT, new OceanMonumentStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_OCEAN_MONUMENT), (Map<MobCategory, StructureSpawnOverride>)Map.of((Object)MobCategory.MONSTER, (Object)((Object)new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, WeightedRandomList.create((WeightedEntry[])new MobSpawnSettings.SpawnerData[]{new MobSpawnSettings.SpawnerData(EntityType.GUARDIAN, 1, 2, 4)}))), (Object)MobCategory.UNDERGROUND_WATER_CREATURE, (Object)((Object)new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, MobSpawnSettings.EMPTY_MOB_LIST)), (Object)MobCategory.AXOLOTLS, (Object)((Object)new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, MobSpawnSettings.EMPTY_MOB_LIST))), GenerationStep.Decoration.SURFACE_STRUCTURES, TerrainAdjustment.NONE)));
        $$02.register(BuiltinStructures.OCEAN_RUIN_COLD, new OceanRuinStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_OCEAN_RUIN_COLD), TerrainAdjustment.NONE), OceanRuinStructure.Type.COLD, 0.3f, 0.9f));
        $$02.register(BuiltinStructures.OCEAN_RUIN_WARM, new OceanRuinStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_OCEAN_RUIN_WARM), TerrainAdjustment.NONE), OceanRuinStructure.Type.WARM, 0.3f, 0.9f));
        $$02.register(BuiltinStructures.FORTRESS, new NetherFortressStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_NETHER_FORTRESS), (Map<MobCategory, StructureSpawnOverride>)Map.of((Object)MobCategory.MONSTER, (Object)((Object)new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.PIECE, NetherFortressStructure.FORTRESS_ENEMIES))), GenerationStep.Decoration.UNDERGROUND_DECORATION, TerrainAdjustment.NONE)));
        $$02.register(BuiltinStructures.NETHER_FOSSIL, new NetherFossilStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_NETHER_FOSSIL), GenerationStep.Decoration.UNDERGROUND_DECORATION, TerrainAdjustment.BEARD_THIN), UniformHeight.of(VerticalAnchor.absolute(32), VerticalAnchor.belowTop(2))));
        $$02.register(BuiltinStructures.END_CITY, new EndCityStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_END_CITY), TerrainAdjustment.NONE)));
        $$02.register(BuiltinStructures.BURIED_TREASURE, new BuriedTreasureStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_BURIED_TREASURE), GenerationStep.Decoration.UNDERGROUND_STRUCTURES, TerrainAdjustment.NONE)));
        $$02.register(BuiltinStructures.BASTION_REMNANT, new JigsawStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_BASTION_REMNANT), TerrainAdjustment.NONE), $$2.getOrThrow(BastionPieces.START), 6, ConstantHeight.of(VerticalAnchor.absolute(33)), false));
        $$02.register(BuiltinStructures.VILLAGE_PLAINS, new JigsawStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_VILLAGE_PLAINS), TerrainAdjustment.BEARD_THIN), $$2.getOrThrow(PlainVillagePools.START), 6, ConstantHeight.of(VerticalAnchor.absolute(0)), true, Heightmap.Types.WORLD_SURFACE_WG));
        $$02.register(BuiltinStructures.VILLAGE_DESERT, new JigsawStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_VILLAGE_DESERT), TerrainAdjustment.BEARD_THIN), $$2.getOrThrow(DesertVillagePools.START), 6, ConstantHeight.of(VerticalAnchor.absolute(0)), true, Heightmap.Types.WORLD_SURFACE_WG));
        $$02.register(BuiltinStructures.VILLAGE_SAVANNA, new JigsawStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_VILLAGE_SAVANNA), TerrainAdjustment.BEARD_THIN), $$2.getOrThrow(SavannaVillagePools.START), 6, ConstantHeight.of(VerticalAnchor.absolute(0)), true, Heightmap.Types.WORLD_SURFACE_WG));
        $$02.register(BuiltinStructures.VILLAGE_SNOWY, new JigsawStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_VILLAGE_SNOWY), TerrainAdjustment.BEARD_THIN), $$2.getOrThrow(SnowyVillagePools.START), 6, ConstantHeight.of(VerticalAnchor.absolute(0)), true, Heightmap.Types.WORLD_SURFACE_WG));
        $$02.register(BuiltinStructures.VILLAGE_TAIGA, new JigsawStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_VILLAGE_TAIGA), TerrainAdjustment.BEARD_THIN), $$2.getOrThrow(TaigaVillagePools.START), 6, ConstantHeight.of(VerticalAnchor.absolute(0)), true, Heightmap.Types.WORLD_SURFACE_WG));
        $$02.register(BuiltinStructures.RUINED_PORTAL_STANDARD, new RuinedPortalStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_RUINED_PORTAL_STANDARD), TerrainAdjustment.NONE), (List<RuinedPortalStructure.Setup>)List.of((Object)((Object)new RuinedPortalStructure.Setup(RuinedPortalPiece.VerticalPlacement.UNDERGROUND, 1.0f, 0.2f, false, false, true, false, 0.5f)), (Object)((Object)new RuinedPortalStructure.Setup(RuinedPortalPiece.VerticalPlacement.ON_LAND_SURFACE, 0.5f, 0.2f, false, false, true, false, 0.5f)))));
        $$02.register(BuiltinStructures.RUINED_PORTAL_DESERT, new RuinedPortalStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_RUINED_PORTAL_DESERT), TerrainAdjustment.NONE), new RuinedPortalStructure.Setup(RuinedPortalPiece.VerticalPlacement.PARTLY_BURIED, 0.0f, 0.0f, false, false, false, false, 1.0f)));
        $$02.register(BuiltinStructures.RUINED_PORTAL_JUNGLE, new RuinedPortalStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_RUINED_PORTAL_JUNGLE), TerrainAdjustment.NONE), new RuinedPortalStructure.Setup(RuinedPortalPiece.VerticalPlacement.ON_LAND_SURFACE, 0.5f, 0.8f, true, true, false, false, 1.0f)));
        $$02.register(BuiltinStructures.RUINED_PORTAL_SWAMP, new RuinedPortalStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_RUINED_PORTAL_SWAMP), TerrainAdjustment.NONE), new RuinedPortalStructure.Setup(RuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR, 0.0f, 0.5f, false, true, false, false, 1.0f)));
        $$02.register(BuiltinStructures.RUINED_PORTAL_MOUNTAIN, new RuinedPortalStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_RUINED_PORTAL_MOUNTAIN), TerrainAdjustment.NONE), (List<RuinedPortalStructure.Setup>)List.of((Object)((Object)new RuinedPortalStructure.Setup(RuinedPortalPiece.VerticalPlacement.IN_MOUNTAIN, 1.0f, 0.2f, false, false, true, false, 0.5f)), (Object)((Object)new RuinedPortalStructure.Setup(RuinedPortalPiece.VerticalPlacement.ON_LAND_SURFACE, 0.5f, 0.2f, false, false, true, false, 0.5f)))));
        $$02.register(BuiltinStructures.RUINED_PORTAL_OCEAN, new RuinedPortalStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_RUINED_PORTAL_OCEAN), TerrainAdjustment.NONE), new RuinedPortalStructure.Setup(RuinedPortalPiece.VerticalPlacement.ON_OCEAN_FLOOR, 0.0f, 0.8f, false, false, true, false, 1.0f)));
        $$02.register(BuiltinStructures.RUINED_PORTAL_NETHER, new RuinedPortalStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_RUINED_PORTAL_NETHER), TerrainAdjustment.NONE), new RuinedPortalStructure.Setup(RuinedPortalPiece.VerticalPlacement.IN_NETHER, 0.5f, 0.0f, false, false, false, true, 1.0f)));
        $$02.register(BuiltinStructures.ANCIENT_CITY, new JigsawStructure(Structures.structure($$1.getOrThrow(BiomeTags.HAS_ANCIENT_CITY), (Map<MobCategory, StructureSpawnOverride>)((Map)Arrays.stream((Object[])MobCategory.values()).collect(Collectors.toMap($$0 -> $$0, $$0 -> new StructureSpawnOverride(StructureSpawnOverride.BoundingBoxType.STRUCTURE, WeightedRandomList.create())))), GenerationStep.Decoration.UNDERGROUND_DECORATION, TerrainAdjustment.BEARD_BOX), $$2.getOrThrow(AncientCityStructurePieces.START), (Optional<ResourceLocation>)Optional.of((Object)new ResourceLocation("city_anchor")), 7, ConstantHeight.of(VerticalAnchor.absolute(-27)), false, (Optional<Heightmap.Types>)Optional.empty(), 116));
    }
}