/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Lifecycle
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Map
 *  java.util.function.Supplier
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 */
package net.minecraft.core.registries;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.util.valueproviders.FloatProviderType;
import net.minecraft.util.valueproviders.IntProviderType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.decoration.PaintingVariants;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Instruments;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.BiomeSources;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatterns;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGenerators;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.minecraft.world.level.storage.loot.providers.nbt.LootNbtProviderType;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProviders;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.minecraft.world.level.storage.loot.providers.score.LootScoreProviderType;
import net.minecraft.world.level.storage.loot.providers.score.ScoreboardNameProviders;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public class BuiltInRegistries {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Map<ResourceLocation, Supplier<?>> LOADERS = Maps.newLinkedHashMap();
    public static final ResourceLocation ROOT_REGISTRY_NAME = new ResourceLocation("root");
    private static final WritableRegistry<WritableRegistry<?>> WRITABLE_REGISTRY = new MappedRegistry(ResourceKey.createRegistryKey(ROOT_REGISTRY_NAME), Lifecycle.stable());
    public static final DefaultedRegistry<GameEvent> GAME_EVENT = BuiltInRegistries.registerDefaultedWithIntrusiveHolders(Registries.GAME_EVENT, "step", $$0 -> GameEvent.STEP);
    public static final Registry<SoundEvent> SOUND_EVENT = BuiltInRegistries.registerSimple(Registries.SOUND_EVENT, $$0 -> SoundEvents.ITEM_PICKUP);
    public static final DefaultedRegistry<Fluid> FLUID = BuiltInRegistries.registerDefaultedWithIntrusiveHolders(Registries.FLUID, "empty", $$0 -> Fluids.EMPTY);
    public static final Registry<MobEffect> MOB_EFFECT = BuiltInRegistries.registerSimple(Registries.MOB_EFFECT, $$0 -> MobEffects.LUCK);
    public static final DefaultedRegistry<Block> BLOCK = BuiltInRegistries.registerDefaultedWithIntrusiveHolders(Registries.BLOCK, "air", $$0 -> Blocks.AIR);
    public static final Registry<Enchantment> ENCHANTMENT = BuiltInRegistries.registerSimple(Registries.ENCHANTMENT, $$0 -> Enchantments.BLOCK_FORTUNE);
    public static final DefaultedRegistry<EntityType<?>> ENTITY_TYPE = BuiltInRegistries.registerDefaultedWithIntrusiveHolders(Registries.ENTITY_TYPE, "pig", $$0 -> EntityType.PIG);
    public static final DefaultedRegistry<Item> ITEM = BuiltInRegistries.registerDefaultedWithIntrusiveHolders(Registries.ITEM, "air", $$0 -> Items.AIR);
    public static final DefaultedRegistry<Potion> POTION = BuiltInRegistries.registerDefaulted(Registries.POTION, "empty", $$0 -> Potions.EMPTY);
    public static final Registry<ParticleType<?>> PARTICLE_TYPE = BuiltInRegistries.registerSimple(Registries.PARTICLE_TYPE, $$0 -> ParticleTypes.BLOCK);
    public static final Registry<BlockEntityType<?>> BLOCK_ENTITY_TYPE = BuiltInRegistries.registerSimple(Registries.BLOCK_ENTITY_TYPE, $$0 -> BlockEntityType.FURNACE);
    public static final DefaultedRegistry<PaintingVariant> PAINTING_VARIANT = BuiltInRegistries.registerDefaulted(Registries.PAINTING_VARIANT, "kebab", PaintingVariants::bootstrap);
    public static final Registry<ResourceLocation> CUSTOM_STAT = BuiltInRegistries.registerSimple(Registries.CUSTOM_STAT, $$0 -> Stats.JUMP);
    public static final DefaultedRegistry<ChunkStatus> CHUNK_STATUS = BuiltInRegistries.registerDefaulted(Registries.CHUNK_STATUS, "empty", $$0 -> ChunkStatus.EMPTY);
    public static final Registry<RuleTestType<?>> RULE_TEST = BuiltInRegistries.registerSimple(Registries.RULE_TEST, $$0 -> RuleTestType.ALWAYS_TRUE_TEST);
    public static final Registry<PosRuleTestType<?>> POS_RULE_TEST = BuiltInRegistries.registerSimple(Registries.POS_RULE_TEST, $$0 -> PosRuleTestType.ALWAYS_TRUE_TEST);
    public static final Registry<MenuType<?>> MENU = BuiltInRegistries.registerSimple(Registries.MENU, $$0 -> MenuType.ANVIL);
    public static final Registry<RecipeType<?>> RECIPE_TYPE = BuiltInRegistries.registerSimple(Registries.RECIPE_TYPE, $$0 -> RecipeType.CRAFTING);
    public static final Registry<RecipeSerializer<?>> RECIPE_SERIALIZER = BuiltInRegistries.registerSimple(Registries.RECIPE_SERIALIZER, $$0 -> RecipeSerializer.SHAPELESS_RECIPE);
    public static final Registry<Attribute> ATTRIBUTE = BuiltInRegistries.registerSimple(Registries.ATTRIBUTE, $$0 -> Attributes.LUCK);
    public static final Registry<PositionSourceType<?>> POSITION_SOURCE_TYPE = BuiltInRegistries.registerSimple(Registries.POSITION_SOURCE_TYPE, $$0 -> PositionSourceType.BLOCK);
    public static final Registry<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPE = BuiltInRegistries.registerSimple(Registries.COMMAND_ARGUMENT_TYPE, ArgumentTypeInfos::bootstrap);
    public static final Registry<StatType<?>> STAT_TYPE = BuiltInRegistries.registerSimple(Registries.STAT_TYPE, $$0 -> Stats.ITEM_USED);
    public static final DefaultedRegistry<VillagerType> VILLAGER_TYPE = BuiltInRegistries.registerDefaulted(Registries.VILLAGER_TYPE, "plains", $$0 -> VillagerType.PLAINS);
    public static final DefaultedRegistry<VillagerProfession> VILLAGER_PROFESSION = BuiltInRegistries.registerDefaulted(Registries.VILLAGER_PROFESSION, "none", $$0 -> VillagerProfession.NONE);
    public static final Registry<PoiType> POINT_OF_INTEREST_TYPE = BuiltInRegistries.registerSimple(Registries.POINT_OF_INTEREST_TYPE, PoiTypes::bootstrap);
    public static final DefaultedRegistry<MemoryModuleType<?>> MEMORY_MODULE_TYPE = BuiltInRegistries.registerDefaulted(Registries.MEMORY_MODULE_TYPE, "dummy", $$0 -> MemoryModuleType.DUMMY);
    public static final DefaultedRegistry<SensorType<?>> SENSOR_TYPE = BuiltInRegistries.registerDefaulted(Registries.SENSOR_TYPE, "dummy", $$0 -> SensorType.DUMMY);
    public static final Registry<Schedule> SCHEDULE = BuiltInRegistries.registerSimple(Registries.SCHEDULE, $$0 -> Schedule.EMPTY);
    public static final Registry<Activity> ACTIVITY = BuiltInRegistries.registerSimple(Registries.ACTIVITY, $$0 -> Activity.IDLE);
    public static final Registry<LootPoolEntryType> LOOT_POOL_ENTRY_TYPE = BuiltInRegistries.registerSimple(Registries.LOOT_POOL_ENTRY_TYPE, $$0 -> LootPoolEntries.EMPTY);
    public static final Registry<LootItemFunctionType> LOOT_FUNCTION_TYPE = BuiltInRegistries.registerSimple(Registries.LOOT_FUNCTION_TYPE, $$0 -> LootItemFunctions.SET_COUNT);
    public static final Registry<LootItemConditionType> LOOT_CONDITION_TYPE = BuiltInRegistries.registerSimple(Registries.LOOT_CONDITION_TYPE, $$0 -> LootItemConditions.INVERTED);
    public static final Registry<LootNumberProviderType> LOOT_NUMBER_PROVIDER_TYPE = BuiltInRegistries.registerSimple(Registries.LOOT_NUMBER_PROVIDER_TYPE, $$0 -> NumberProviders.CONSTANT);
    public static final Registry<LootNbtProviderType> LOOT_NBT_PROVIDER_TYPE = BuiltInRegistries.registerSimple(Registries.LOOT_NBT_PROVIDER_TYPE, $$0 -> NbtProviders.CONTEXT);
    public static final Registry<LootScoreProviderType> LOOT_SCORE_PROVIDER_TYPE = BuiltInRegistries.registerSimple(Registries.LOOT_SCORE_PROVIDER_TYPE, $$0 -> ScoreboardNameProviders.CONTEXT);
    public static final Registry<FloatProviderType<?>> FLOAT_PROVIDER_TYPE = BuiltInRegistries.registerSimple(Registries.FLOAT_PROVIDER_TYPE, $$0 -> FloatProviderType.CONSTANT);
    public static final Registry<IntProviderType<?>> INT_PROVIDER_TYPE = BuiltInRegistries.registerSimple(Registries.INT_PROVIDER_TYPE, $$0 -> IntProviderType.CONSTANT);
    public static final Registry<HeightProviderType<?>> HEIGHT_PROVIDER_TYPE = BuiltInRegistries.registerSimple(Registries.HEIGHT_PROVIDER_TYPE, $$0 -> HeightProviderType.CONSTANT);
    public static final Registry<BlockPredicateType<?>> BLOCK_PREDICATE_TYPE = BuiltInRegistries.registerSimple(Registries.BLOCK_PREDICATE_TYPE, $$0 -> BlockPredicateType.NOT);
    public static final Registry<WorldCarver<?>> CARVER = BuiltInRegistries.registerSimple(Registries.CARVER, $$0 -> WorldCarver.CAVE);
    public static final Registry<Feature<?>> FEATURE = BuiltInRegistries.registerSimple(Registries.FEATURE, $$0 -> Feature.ORE);
    public static final Registry<StructurePlacementType<?>> STRUCTURE_PLACEMENT = BuiltInRegistries.registerSimple(Registries.STRUCTURE_PLACEMENT, $$0 -> StructurePlacementType.RANDOM_SPREAD);
    public static final Registry<StructurePieceType> STRUCTURE_PIECE = BuiltInRegistries.registerSimple(Registries.STRUCTURE_PIECE, $$0 -> StructurePieceType.MINE_SHAFT_ROOM);
    public static final Registry<StructureType<?>> STRUCTURE_TYPE = BuiltInRegistries.registerSimple(Registries.STRUCTURE_TYPE, $$0 -> StructureType.JIGSAW);
    public static final Registry<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPE = BuiltInRegistries.registerSimple(Registries.PLACEMENT_MODIFIER_TYPE, $$0 -> PlacementModifierType.COUNT);
    public static final Registry<BlockStateProviderType<?>> BLOCKSTATE_PROVIDER_TYPE = BuiltInRegistries.registerSimple(Registries.BLOCK_STATE_PROVIDER_TYPE, $$0 -> BlockStateProviderType.SIMPLE_STATE_PROVIDER);
    public static final Registry<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPE = BuiltInRegistries.registerSimple(Registries.FOLIAGE_PLACER_TYPE, $$0 -> FoliagePlacerType.BLOB_FOLIAGE_PLACER);
    public static final Registry<TrunkPlacerType<?>> TRUNK_PLACER_TYPE = BuiltInRegistries.registerSimple(Registries.TRUNK_PLACER_TYPE, $$0 -> TrunkPlacerType.STRAIGHT_TRUNK_PLACER);
    public static final Registry<RootPlacerType<?>> ROOT_PLACER_TYPE = BuiltInRegistries.registerSimple(Registries.ROOT_PLACER_TYPE, $$0 -> RootPlacerType.MANGROVE_ROOT_PLACER);
    public static final Registry<TreeDecoratorType<?>> TREE_DECORATOR_TYPE = BuiltInRegistries.registerSimple(Registries.TREE_DECORATOR_TYPE, $$0 -> TreeDecoratorType.LEAVE_VINE);
    public static final Registry<FeatureSizeType<?>> FEATURE_SIZE_TYPE = BuiltInRegistries.registerSimple(Registries.FEATURE_SIZE_TYPE, $$0 -> FeatureSizeType.TWO_LAYERS_FEATURE_SIZE);
    public static final Registry<Codec<? extends BiomeSource>> BIOME_SOURCE = BuiltInRegistries.registerSimple(Registries.BIOME_SOURCE, Lifecycle.stable(), BiomeSources::bootstrap);
    public static final Registry<Codec<? extends ChunkGenerator>> CHUNK_GENERATOR = BuiltInRegistries.registerSimple(Registries.CHUNK_GENERATOR, Lifecycle.stable(), ChunkGenerators::bootstrap);
    public static final Registry<Codec<? extends SurfaceRules.ConditionSource>> MATERIAL_CONDITION = BuiltInRegistries.registerSimple(Registries.MATERIAL_CONDITION, SurfaceRules.ConditionSource::bootstrap);
    public static final Registry<Codec<? extends SurfaceRules.RuleSource>> MATERIAL_RULE = BuiltInRegistries.registerSimple(Registries.MATERIAL_RULE, SurfaceRules.RuleSource::bootstrap);
    public static final Registry<Codec<? extends DensityFunction>> DENSITY_FUNCTION_TYPE = BuiltInRegistries.registerSimple(Registries.DENSITY_FUNCTION_TYPE, DensityFunctions::bootstrap);
    public static final Registry<StructureProcessorType<?>> STRUCTURE_PROCESSOR = BuiltInRegistries.registerSimple(Registries.STRUCTURE_PROCESSOR, $$0 -> StructureProcessorType.BLOCK_IGNORE);
    public static final Registry<StructurePoolElementType<?>> STRUCTURE_POOL_ELEMENT = BuiltInRegistries.registerSimple(Registries.STRUCTURE_POOL_ELEMENT, $$0 -> StructurePoolElementType.EMPTY);
    public static final Registry<CatVariant> CAT_VARIANT = BuiltInRegistries.registerSimple(Registries.CAT_VARIANT, CatVariant::bootstrap);
    public static final Registry<FrogVariant> FROG_VARIANT = BuiltInRegistries.registerSimple(Registries.FROG_VARIANT, $$0 -> FrogVariant.TEMPERATE);
    public static final Registry<BannerPattern> BANNER_PATTERN = BuiltInRegistries.registerSimple(Registries.BANNER_PATTERN, BannerPatterns::bootstrap);
    public static final Registry<Instrument> INSTRUMENT = BuiltInRegistries.registerSimple(Registries.INSTRUMENT, Instruments::bootstrap);
    public static final Registry<? extends Registry<?>> REGISTRY = WRITABLE_REGISTRY;

    private static <T> Registry<T> registerSimple(ResourceKey<? extends Registry<T>> $$0, RegistryBootstrap<T> $$1) {
        return BuiltInRegistries.registerSimple($$0, Lifecycle.stable(), $$1);
    }

    private static <T> DefaultedRegistry<T> registerDefaulted(ResourceKey<? extends Registry<T>> $$0, String $$1, RegistryBootstrap<T> $$2) {
        return BuiltInRegistries.registerDefaulted($$0, $$1, Lifecycle.stable(), $$2);
    }

    private static <T> DefaultedRegistry<T> registerDefaultedWithIntrusiveHolders(ResourceKey<? extends Registry<T>> $$0, String $$1, RegistryBootstrap<T> $$2) {
        return BuiltInRegistries.registerDefaultedWithIntrusiveHolders($$0, $$1, Lifecycle.stable(), $$2);
    }

    private static <T> Registry<T> registerSimple(ResourceKey<? extends Registry<T>> $$0, Lifecycle $$1, RegistryBootstrap<T> $$2) {
        return BuiltInRegistries.internalRegister($$0, new MappedRegistry($$0, $$1, false), $$2, $$1);
    }

    private static <T> DefaultedRegistry<T> registerDefaulted(ResourceKey<? extends Registry<T>> $$0, String $$1, Lifecycle $$2, RegistryBootstrap<T> $$3) {
        return BuiltInRegistries.internalRegister($$0, new DefaultedMappedRegistry($$1, $$0, $$2, false), $$3, $$2);
    }

    private static <T> DefaultedRegistry<T> registerDefaultedWithIntrusiveHolders(ResourceKey<? extends Registry<T>> $$0, String $$1, Lifecycle $$2, RegistryBootstrap<T> $$3) {
        return BuiltInRegistries.internalRegister($$0, new DefaultedMappedRegistry($$1, $$0, $$2, true), $$3, $$2);
    }

    private static <T, R extends WritableRegistry<T>> R internalRegister(ResourceKey<? extends Registry<T>> $$0, R $$1, RegistryBootstrap<T> $$2, Lifecycle $$3) {
        ResourceLocation $$4 = $$0.location();
        LOADERS.put((Object)$$4, () -> $$2.run($$1));
        WRITABLE_REGISTRY.register($$0, $$1, $$3);
        return $$1;
    }

    public static void bootStrap() {
        BuiltInRegistries.createContents();
        BuiltInRegistries.freeze();
        BuiltInRegistries.validate(REGISTRY);
    }

    private static void createContents() {
        LOADERS.forEach(($$0, $$1) -> {
            if ($$1.get() == null) {
                LOGGER.error("Unable to bootstrap registry '{}'", $$0);
            }
        });
    }

    private static void freeze() {
        REGISTRY.freeze();
        for (Registry registry : REGISTRY) {
            registry.freeze();
        }
    }

    private static <T extends Registry<?>> void validate(Registry<T> $$0) {
        $$0.forEach($$1 -> {
            if ($$1.keySet().isEmpty()) {
                Util.logAndPauseIfInIde("Registry '" + $$0.getKey($$1) + "' was empty after loading");
            }
            if ($$1 instanceof DefaultedRegistry) {
                ResourceLocation $$2 = ((DefaultedRegistry)$$1).getDefaultKey();
                Validate.notNull($$1.get($$2), (String)("Missing default of DefaultedMappedRegistry: " + $$2), (Object[])new Object[0]);
            }
        });
    }

    @FunctionalInterface
    static interface RegistryBootstrap<T> {
        public T run(Registry<T> var1);
    }
}