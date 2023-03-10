/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.core.registries;

import com.mojang.serialization.Codec;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.ChatType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.util.valueproviders.FloatProviderType;
import net.minecraft.util.valueproviders.IntProviderType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.providers.nbt.LootNbtProviderType;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.score.LootScoreProviderType;

public class Registries {
    public static final ResourceKey<Registry<Activity>> ACTIVITY = Registries.createRegistryKey("activity");
    public static final ResourceKey<Registry<Attribute>> ATTRIBUTE = Registries.createRegistryKey("attribute");
    public static final ResourceKey<Registry<BannerPattern>> BANNER_PATTERN = Registries.createRegistryKey("banner_pattern");
    public static final ResourceKey<Registry<Codec<? extends BiomeSource>>> BIOME_SOURCE = Registries.createRegistryKey("worldgen/biome_source");
    public static final ResourceKey<Registry<Block>> BLOCK = Registries.createRegistryKey("block");
    public static final ResourceKey<Registry<BlockEntityType<?>>> BLOCK_ENTITY_TYPE = Registries.createRegistryKey("block_entity_type");
    public static final ResourceKey<Registry<BlockPredicateType<?>>> BLOCK_PREDICATE_TYPE = Registries.createRegistryKey("block_predicate_type");
    public static final ResourceKey<Registry<BlockStateProviderType<?>>> BLOCK_STATE_PROVIDER_TYPE = Registries.createRegistryKey("worldgen/block_state_provider_type");
    public static final ResourceKey<Registry<WorldCarver<?>>> CARVER = Registries.createRegistryKey("worldgen/carver");
    public static final ResourceKey<Registry<CatVariant>> CAT_VARIANT = Registries.createRegistryKey("cat_variant");
    public static final ResourceKey<Registry<Codec<? extends ChunkGenerator>>> CHUNK_GENERATOR = Registries.createRegistryKey("worldgen/chunk_generator");
    public static final ResourceKey<Registry<ChunkStatus>> CHUNK_STATUS = Registries.createRegistryKey("chunk_status");
    public static final ResourceKey<Registry<ArgumentTypeInfo<?, ?>>> COMMAND_ARGUMENT_TYPE = Registries.createRegistryKey("command_argument_type");
    public static final ResourceKey<Registry<ResourceLocation>> CUSTOM_STAT = Registries.createRegistryKey("custom_stat");
    public static final ResourceKey<Registry<Codec<? extends DensityFunction>>> DENSITY_FUNCTION_TYPE = Registries.createRegistryKey("worldgen/density_function_type");
    public static final ResourceKey<Registry<Enchantment>> ENCHANTMENT = Registries.createRegistryKey("enchantment");
    public static final ResourceKey<Registry<EntityType<?>>> ENTITY_TYPE = Registries.createRegistryKey("entity_type");
    public static final ResourceKey<Registry<Feature<?>>> FEATURE = Registries.createRegistryKey("worldgen/feature");
    public static final ResourceKey<Registry<FeatureSizeType<?>>> FEATURE_SIZE_TYPE = Registries.createRegistryKey("worldgen/feature_size_type");
    public static final ResourceKey<Registry<FloatProviderType<?>>> FLOAT_PROVIDER_TYPE = Registries.createRegistryKey("float_provider_type");
    public static final ResourceKey<Registry<Fluid>> FLUID = Registries.createRegistryKey("fluid");
    public static final ResourceKey<Registry<FoliagePlacerType<?>>> FOLIAGE_PLACER_TYPE = Registries.createRegistryKey("worldgen/foliage_placer_type");
    public static final ResourceKey<Registry<FrogVariant>> FROG_VARIANT = Registries.createRegistryKey("frog_variant");
    public static final ResourceKey<Registry<GameEvent>> GAME_EVENT = Registries.createRegistryKey("game_event");
    public static final ResourceKey<Registry<HeightProviderType<?>>> HEIGHT_PROVIDER_TYPE = Registries.createRegistryKey("height_provider_type");
    public static final ResourceKey<Registry<Instrument>> INSTRUMENT = Registries.createRegistryKey("instrument");
    public static final ResourceKey<Registry<IntProviderType<?>>> INT_PROVIDER_TYPE = Registries.createRegistryKey("int_provider_type");
    public static final ResourceKey<Registry<Item>> ITEM = Registries.createRegistryKey("item");
    public static final ResourceKey<Registry<LootItemConditionType>> LOOT_CONDITION_TYPE = Registries.createRegistryKey("loot_condition_type");
    public static final ResourceKey<Registry<LootItemFunctionType>> LOOT_FUNCTION_TYPE = Registries.createRegistryKey("loot_function_type");
    public static final ResourceKey<Registry<LootNbtProviderType>> LOOT_NBT_PROVIDER_TYPE = Registries.createRegistryKey("loot_nbt_provider_type");
    public static final ResourceKey<Registry<LootNumberProviderType>> LOOT_NUMBER_PROVIDER_TYPE = Registries.createRegistryKey("loot_number_provider_type");
    public static final ResourceKey<Registry<LootPoolEntryType>> LOOT_POOL_ENTRY_TYPE = Registries.createRegistryKey("loot_pool_entry_type");
    public static final ResourceKey<Registry<LootScoreProviderType>> LOOT_SCORE_PROVIDER_TYPE = Registries.createRegistryKey("loot_score_provider_type");
    public static final ResourceKey<Registry<Codec<? extends SurfaceRules.ConditionSource>>> MATERIAL_CONDITION = Registries.createRegistryKey("worldgen/material_condition");
    public static final ResourceKey<Registry<Codec<? extends SurfaceRules.RuleSource>>> MATERIAL_RULE = Registries.createRegistryKey("worldgen/material_rule");
    public static final ResourceKey<Registry<MemoryModuleType<?>>> MEMORY_MODULE_TYPE = Registries.createRegistryKey("memory_module_type");
    public static final ResourceKey<Registry<MenuType<?>>> MENU = Registries.createRegistryKey("menu");
    public static final ResourceKey<Registry<MobEffect>> MOB_EFFECT = Registries.createRegistryKey("mob_effect");
    public static final ResourceKey<Registry<PaintingVariant>> PAINTING_VARIANT = Registries.createRegistryKey("painting_variant");
    public static final ResourceKey<Registry<ParticleType<?>>> PARTICLE_TYPE = Registries.createRegistryKey("particle_type");
    public static final ResourceKey<Registry<PlacementModifierType<?>>> PLACEMENT_MODIFIER_TYPE = Registries.createRegistryKey("worldgen/placement_modifier_type");
    public static final ResourceKey<Registry<PoiType>> POINT_OF_INTEREST_TYPE = Registries.createRegistryKey("point_of_interest_type");
    public static final ResourceKey<Registry<PositionSourceType<?>>> POSITION_SOURCE_TYPE = Registries.createRegistryKey("position_source_type");
    public static final ResourceKey<Registry<PosRuleTestType<?>>> POS_RULE_TEST = Registries.createRegistryKey("pos_rule_test");
    public static final ResourceKey<Registry<Potion>> POTION = Registries.createRegistryKey("potion");
    public static final ResourceKey<Registry<RecipeSerializer<?>>> RECIPE_SERIALIZER = Registries.createRegistryKey("recipe_serializer");
    public static final ResourceKey<Registry<RecipeType<?>>> RECIPE_TYPE = Registries.createRegistryKey("recipe_type");
    public static final ResourceKey<Registry<RootPlacerType<?>>> ROOT_PLACER_TYPE = Registries.createRegistryKey("worldgen/root_placer_type");
    public static final ResourceKey<Registry<RuleTestType<?>>> RULE_TEST = Registries.createRegistryKey("rule_test");
    public static final ResourceKey<Registry<Schedule>> SCHEDULE = Registries.createRegistryKey("schedule");
    public static final ResourceKey<Registry<SensorType<?>>> SENSOR_TYPE = Registries.createRegistryKey("sensor_type");
    public static final ResourceKey<Registry<SoundEvent>> SOUND_EVENT = Registries.createRegistryKey("sound_event");
    public static final ResourceKey<Registry<StatType<?>>> STAT_TYPE = Registries.createRegistryKey("stat_type");
    public static final ResourceKey<Registry<StructurePieceType>> STRUCTURE_PIECE = Registries.createRegistryKey("worldgen/structure_piece");
    public static final ResourceKey<Registry<StructurePlacementType<?>>> STRUCTURE_PLACEMENT = Registries.createRegistryKey("worldgen/structure_placement");
    public static final ResourceKey<Registry<StructurePoolElementType<?>>> STRUCTURE_POOL_ELEMENT = Registries.createRegistryKey("worldgen/structure_pool_element");
    public static final ResourceKey<Registry<StructureProcessorType<?>>> STRUCTURE_PROCESSOR = Registries.createRegistryKey("worldgen/structure_processor");
    public static final ResourceKey<Registry<StructureType<?>>> STRUCTURE_TYPE = Registries.createRegistryKey("worldgen/structure_type");
    public static final ResourceKey<Registry<TreeDecoratorType<?>>> TREE_DECORATOR_TYPE = Registries.createRegistryKey("worldgen/tree_decorator_type");
    public static final ResourceKey<Registry<TrunkPlacerType<?>>> TRUNK_PLACER_TYPE = Registries.createRegistryKey("worldgen/trunk_placer_type");
    public static final ResourceKey<Registry<VillagerProfession>> VILLAGER_PROFESSION = Registries.createRegistryKey("villager_profession");
    public static final ResourceKey<Registry<VillagerType>> VILLAGER_TYPE = Registries.createRegistryKey("villager_type");
    public static final ResourceKey<Registry<Biome>> BIOME = Registries.createRegistryKey("worldgen/biome");
    public static final ResourceKey<Registry<ChatType>> CHAT_TYPE = Registries.createRegistryKey("chat_type");
    public static final ResourceKey<Registry<ConfiguredWorldCarver<?>>> CONFIGURED_CARVER = Registries.createRegistryKey("worldgen/configured_carver");
    public static final ResourceKey<Registry<ConfiguredFeature<?, ?>>> CONFIGURED_FEATURE = Registries.createRegistryKey("worldgen/configured_feature");
    public static final ResourceKey<Registry<DensityFunction>> DENSITY_FUNCTION = Registries.createRegistryKey("worldgen/density_function");
    public static final ResourceKey<Registry<DimensionType>> DIMENSION_TYPE = Registries.createRegistryKey("dimension_type");
    public static final ResourceKey<Registry<FlatLevelGeneratorPreset>> FLAT_LEVEL_GENERATOR_PRESET = Registries.createRegistryKey("worldgen/flat_level_generator_preset");
    public static final ResourceKey<Registry<NoiseGeneratorSettings>> NOISE_SETTINGS = Registries.createRegistryKey("worldgen/noise_settings");
    public static final ResourceKey<Registry<NormalNoise.NoiseParameters>> NOISE = Registries.createRegistryKey("worldgen/noise");
    public static final ResourceKey<Registry<PlacedFeature>> PLACED_FEATURE = Registries.createRegistryKey("worldgen/placed_feature");
    public static final ResourceKey<Registry<Structure>> STRUCTURE = Registries.createRegistryKey("worldgen/structure");
    public static final ResourceKey<Registry<StructureProcessorList>> PROCESSOR_LIST = Registries.createRegistryKey("worldgen/processor_list");
    public static final ResourceKey<Registry<StructureSet>> STRUCTURE_SET = Registries.createRegistryKey("worldgen/structure_set");
    public static final ResourceKey<Registry<StructureTemplatePool>> TEMPLATE_POOL = Registries.createRegistryKey("worldgen/template_pool");
    public static final ResourceKey<Registry<TrimMaterial>> TRIM_MATERIAL = Registries.createRegistryKey("trim_material");
    public static final ResourceKey<Registry<TrimPattern>> TRIM_PATTERN = Registries.createRegistryKey("trim_pattern");
    public static final ResourceKey<Registry<WorldPreset>> WORLD_PRESET = Registries.createRegistryKey("worldgen/world_preset");
    public static final ResourceKey<Registry<Level>> DIMENSION = Registries.createRegistryKey("dimension");
    public static final ResourceKey<Registry<LevelStem>> LEVEL_STEM = Registries.createRegistryKey("dimension");

    public static ResourceKey<Level> levelStemToLevel(ResourceKey<LevelStem> $$0) {
        return ResourceKey.create(DIMENSION, $$0.location());
    }

    public static ResourceKey<LevelStem> levelToLevelStem(ResourceKey<Level> $$0) {
        return ResourceKey.create(LEVEL_STEM, $$0.location());
    }

    private static <T> ResourceKey<Registry<T>> createRegistryKey(String $$0) {
        return ResourceKey.createRegistryKey(new ResourceLocation($$0));
    }
}