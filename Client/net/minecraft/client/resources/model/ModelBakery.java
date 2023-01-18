/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Splitter
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.google.gson.JsonElement
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  java.io.FileNotFoundException
 *  java.io.IOException
 *  java.io.Reader
 *  java.io.StringReader
 *  java.lang.CharSequence
 *  java.lang.Comparable
 *  java.lang.Exception
 *  java.lang.IllegalStateException
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.Collection
 *  java.util.HashMap
 *  java.util.IdentityHashMap
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.Set
 *  java.util.function.BiFunction
 *  java.util.function.Function
 *  java.util.function.Predicate
 *  java.util.function.Supplier
 *  java.util.stream.Collectors
 *  java.util.stream.IntStream
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.resources.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.math.Transformation;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.renderer.block.model.multipart.MultiPart;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import org.slf4j.Logger;

public class ModelBakery {
    public static final Material FIRE_0 = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("block/fire_0"));
    public static final Material FIRE_1 = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("block/fire_1"));
    public static final Material LAVA_FLOW = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("block/lava_flow"));
    public static final Material WATER_FLOW = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("block/water_flow"));
    public static final Material WATER_OVERLAY = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("block/water_overlay"));
    public static final Material BANNER_BASE = new Material(Sheets.BANNER_SHEET, new ResourceLocation("entity/banner_base"));
    public static final Material SHIELD_BASE = new Material(Sheets.SHIELD_SHEET, new ResourceLocation("entity/shield_base"));
    public static final Material NO_PATTERN_SHIELD = new Material(Sheets.SHIELD_SHEET, new ResourceLocation("entity/shield_base_nopattern"));
    public static final int DESTROY_STAGE_COUNT = 10;
    public static final List<ResourceLocation> DESTROY_STAGES = (List)IntStream.range((int)0, (int)10).mapToObj($$0 -> new ResourceLocation("block/destroy_stage_" + $$0)).collect(Collectors.toList());
    public static final List<ResourceLocation> BREAKING_LOCATIONS = (List)DESTROY_STAGES.stream().map($$0 -> new ResourceLocation("textures/" + $$0.getPath() + ".png")).collect(Collectors.toList());
    public static final List<RenderType> DESTROY_TYPES = (List)BREAKING_LOCATIONS.stream().map(RenderType::crumbling).collect(Collectors.toList());
    static final int SINGLETON_MODEL_GROUP = -1;
    private static final int INVISIBLE_MODEL_GROUP = 0;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String BUILTIN_SLASH = "builtin/";
    private static final String BUILTIN_SLASH_GENERATED = "builtin/generated";
    private static final String BUILTIN_BLOCK_ENTITY = "builtin/entity";
    private static final String MISSING_MODEL_NAME = "missing";
    public static final ModelResourceLocation MISSING_MODEL_LOCATION = ModelResourceLocation.vanilla("builtin/missing", "missing");
    public static final FileToIdConverter BLOCKSTATE_LISTER = FileToIdConverter.json("blockstates");
    public static final FileToIdConverter MODEL_LISTER = FileToIdConverter.json("models");
    @VisibleForTesting
    public static final String MISSING_MODEL_MESH = ("{    'textures': {       'particle': '" + MissingTextureAtlasSprite.getLocation().getPath() + "',       'missingno': '" + MissingTextureAtlasSprite.getLocation().getPath() + "'    },    'elements': [         {  'from': [ 0, 0, 0 ],            'to': [ 16, 16, 16 ],            'faces': {                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}").replace('\'', '\"');
    private static final Map<String, String> BUILTIN_MODELS = Maps.newHashMap((Map)ImmutableMap.of((Object)"missing", (Object)MISSING_MODEL_MESH));
    private static final Splitter COMMA_SPLITTER = Splitter.on((char)',');
    private static final Splitter EQUAL_SPLITTER = Splitter.on((char)'=').limit(2);
    public static final BlockModel GENERATION_MARKER = Util.make(BlockModel.fromString("{\"gui_light\": \"front\"}"), $$0 -> {
        $$0.name = "generation marker";
    });
    public static final BlockModel BLOCK_ENTITY_MARKER = Util.make(BlockModel.fromString("{\"gui_light\": \"side\"}"), $$0 -> {
        $$0.name = "block entity marker";
    });
    private static final StateDefinition<Block, BlockState> ITEM_FRAME_FAKE_DEFINITION = new StateDefinition.Builder(Blocks.AIR).add(BooleanProperty.create("map")).create(Block::defaultBlockState, BlockState::new);
    static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
    private static final Map<ResourceLocation, StateDefinition<Block, BlockState>> STATIC_DEFINITIONS = ImmutableMap.of((Object)new ResourceLocation("item_frame"), ITEM_FRAME_FAKE_DEFINITION, (Object)new ResourceLocation("glow_item_frame"), ITEM_FRAME_FAKE_DEFINITION);
    private final BlockColors blockColors;
    private final Map<ResourceLocation, BlockModel> modelResources;
    private final Map<ResourceLocation, List<LoadedJson>> blockStateResources;
    private final Set<ResourceLocation> loadingStack = Sets.newHashSet();
    private final BlockModelDefinition.Context context = new BlockModelDefinition.Context();
    private final Map<ResourceLocation, UnbakedModel> unbakedCache = Maps.newHashMap();
    final Map<BakedCacheKey, BakedModel> bakedCache = Maps.newHashMap();
    private final Map<ResourceLocation, UnbakedModel> topLevelModels = Maps.newHashMap();
    private final Map<ResourceLocation, BakedModel> bakedTopLevelModels = Maps.newHashMap();
    private int nextModelGroup = 1;
    private final Object2IntMap<BlockState> modelGroups = (Object2IntMap)Util.make(new Object2IntOpenHashMap(), $$0 -> $$0.defaultReturnValue(-1));

    public ModelBakery(BlockColors $$02, ProfilerFiller $$1, Map<ResourceLocation, BlockModel> $$2, Map<ResourceLocation, List<LoadedJson>> $$3) {
        this.blockColors = $$02;
        this.modelResources = $$2;
        this.blockStateResources = $$3;
        $$1.push("missing_model");
        try {
            this.unbakedCache.put((Object)MISSING_MODEL_LOCATION, (Object)this.loadBlockModel(MISSING_MODEL_LOCATION));
            this.loadTopLevel(MISSING_MODEL_LOCATION);
        }
        catch (IOException $$4) {
            LOGGER.error("Error loading missing model, should never happen :(", (Throwable)$$4);
            throw new RuntimeException((Throwable)$$4);
        }
        $$1.popPush("static_definitions");
        STATIC_DEFINITIONS.forEach(($$0, $$12) -> $$12.getPossibleStates().forEach($$1 -> this.loadTopLevel(BlockModelShaper.stateToModelLocation($$0, $$1))));
        $$1.popPush("blocks");
        for (Block $$5 : BuiltInRegistries.BLOCK) {
            $$5.getStateDefinition().getPossibleStates().forEach($$0 -> this.loadTopLevel(BlockModelShaper.stateToModelLocation($$0)));
        }
        $$1.popPush("items");
        for (ResourceLocation $$6 : BuiltInRegistries.ITEM.keySet()) {
            this.loadTopLevel(new ModelResourceLocation($$6, "inventory"));
        }
        $$1.popPush("special");
        this.loadTopLevel(ItemRenderer.TRIDENT_IN_HAND_MODEL);
        this.loadTopLevel(ItemRenderer.SPYGLASS_IN_HAND_MODEL);
        this.topLevelModels.values().forEach($$0 -> $$0.resolveParents((Function<ResourceLocation, UnbakedModel>)((Function)this::getModel)));
        $$1.pop();
    }

    public void bakeModels(BiFunction<ResourceLocation, Material, TextureAtlasSprite> $$0) {
        this.topLevelModels.keySet().forEach($$1 -> {
            BakedModel $$2 = null;
            try {
                $$2 = new ModelBakerImpl($$0, (ResourceLocation)$$1).bake((ResourceLocation)$$1, BlockModelRotation.X0_Y0);
            }
            catch (Exception $$3) {
                LOGGER.warn("Unable to bake model: '{}': {}", $$1, (Object)$$3);
            }
            if ($$2 != null) {
                this.bakedTopLevelModels.put($$1, (Object)$$2);
            }
        });
    }

    private static Predicate<BlockState> predicate(StateDefinition<Block, BlockState> $$0, String $$1) {
        HashMap $$2 = Maps.newHashMap();
        for (String $$3 : COMMA_SPLITTER.split((CharSequence)$$1)) {
            Iterator $$4 = EQUAL_SPLITTER.split((CharSequence)$$3).iterator();
            if (!$$4.hasNext()) continue;
            String $$5 = (String)$$4.next();
            Property<?> $$6 = $$0.getProperty($$5);
            if ($$6 != null && $$4.hasNext()) {
                String $$7 = (String)$$4.next();
                Object $$8 = ModelBakery.getValueHelper($$6, $$7);
                if ($$8 != null) {
                    $$2.put($$6, $$8);
                    continue;
                }
                throw new RuntimeException("Unknown value: '" + $$7 + "' for blockstate property: '" + $$5 + "' " + $$6.getPossibleValues());
            }
            if ($$5.isEmpty()) continue;
            throw new RuntimeException("Unknown blockstate property: '" + $$5 + "'");
        }
        Block $$9 = $$0.getOwner();
        return arg_0 -> ModelBakery.lambda$predicate$10($$9, (Map)$$2, arg_0);
    }

    @Nullable
    static <T extends Comparable<T>> T getValueHelper(Property<T> $$0, String $$1) {
        return (T)((Comparable)$$0.getValue($$1).orElse(null));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public UnbakedModel getModel(ResourceLocation $$0) {
        if (this.unbakedCache.containsKey((Object)$$0)) {
            return (UnbakedModel)this.unbakedCache.get((Object)$$0);
        }
        if (this.loadingStack.contains((Object)$$0)) {
            throw new IllegalStateException("Circular reference while loading " + $$0);
        }
        this.loadingStack.add((Object)$$0);
        UnbakedModel $$1 = (UnbakedModel)this.unbakedCache.get((Object)MISSING_MODEL_LOCATION);
        while (!this.loadingStack.isEmpty()) {
            ResourceLocation $$2 = (ResourceLocation)this.loadingStack.iterator().next();
            try {
                if (this.unbakedCache.containsKey((Object)$$2)) continue;
                this.loadModel($$2);
            }
            catch (BlockStateDefinitionException $$3) {
                LOGGER.warn($$3.getMessage());
                this.unbakedCache.put((Object)$$2, (Object)$$1);
            }
            catch (Exception $$4) {
                LOGGER.warn("Unable to load model: '{}' referenced from: {}: {}", new Object[]{$$2, $$0, $$4});
                this.unbakedCache.put((Object)$$2, (Object)$$1);
            }
            finally {
                this.loadingStack.remove((Object)$$2);
            }
        }
        return (UnbakedModel)this.unbakedCache.getOrDefault((Object)$$0, (Object)$$1);
    }

    private void loadModel(ResourceLocation $$02) throws Exception {
        if (!($$02 instanceof ModelResourceLocation)) {
            this.cacheAndQueueDependencies($$02, this.loadBlockModel($$02));
            return;
        }
        ModelResourceLocation $$12 = (ModelResourceLocation)$$02;
        if (Objects.equals((Object)$$12.getVariant(), (Object)"inventory")) {
            ResourceLocation $$2 = $$02.withPrefix("item/");
            BlockModel $$3 = this.loadBlockModel($$2);
            this.cacheAndQueueDependencies($$12, $$3);
            this.unbakedCache.put((Object)$$2, (Object)$$3);
        } else {
            ResourceLocation $$4 = new ResourceLocation($$02.getNamespace(), $$02.getPath());
            StateDefinition $$5 = (StateDefinition)Optional.ofNullable((Object)((StateDefinition)STATIC_DEFINITIONS.get((Object)$$4))).orElseGet(() -> BuiltInRegistries.BLOCK.get($$4).getStateDefinition());
            this.context.setDefinition($$5);
            ImmutableList $$6 = ImmutableList.copyOf(this.blockColors.getColoringProperties((Block)$$5.getOwner()));
            ImmutableList $$7 = $$5.getPossibleStates();
            HashMap $$8 = Maps.newHashMap();
            $$7.forEach(arg_0 -> ModelBakery.lambda$loadModel$12((Map)$$8, $$4, arg_0));
            HashMap $$9 = Maps.newHashMap();
            ResourceLocation $$10 = BLOCKSTATE_LISTER.idToFile($$02);
            UnbakedModel $$11 = (UnbakedModel)this.unbakedCache.get((Object)MISSING_MODEL_LOCATION);
            ModelGroupKey $$122 = new ModelGroupKey((List<UnbakedModel>)ImmutableList.of((Object)$$11), (List<Object>)ImmutableList.of());
            Pair $$13 = Pair.of((Object)$$11, () -> $$122);
            try {
                List $$14 = ((List)this.blockStateResources.getOrDefault((Object)$$10, (Object)List.of())).stream().map($$1 -> {
                    try {
                        return Pair.of((Object)$$1.source, (Object)BlockModelDefinition.fromJsonElement(this.context, $$1.data));
                    }
                    catch (Exception $$2) {
                        throw new BlockStateDefinitionException(String.format((Locale)Locale.ROOT, (String)"Exception loading blockstate definition: '%s' in resourcepack: '%s': %s", (Object[])new Object[]{$$10, $$1.source, $$2.getMessage()}));
                    }
                }).toList();
                for (Pair $$15 : $$14) {
                    MultiPart $$19;
                    BlockModelDefinition $$16 = (BlockModelDefinition)$$15.getSecond();
                    IdentityHashMap $$17 = Maps.newIdentityHashMap();
                    if ($$16.isMultiPart()) {
                        MultiPart $$18 = $$16.getMultiPart();
                        $$7.forEach(arg_0 -> ModelBakery.lambda$loadModel$16((Map)$$17, $$18, (List)$$6, arg_0));
                    } else {
                        $$19 = null;
                    }
                    $$16.getVariants().forEach((arg_0, arg_1) -> ModelBakery.lambda$loadModel$20($$7, $$5, (Map)$$17, (List)$$6, $$19, $$13, $$16, $$10, $$15, arg_0, arg_1));
                    $$9.putAll((Map)$$17);
                }
            }
            catch (BlockStateDefinitionException $$21) {
                throw $$21;
            }
            catch (Exception $$22) {
                throw new BlockStateDefinitionException(String.format((Locale)Locale.ROOT, (String)"Exception loading blockstate definition: '%s': %s", (Object[])new Object[]{$$10, $$22}));
            }
            finally {
                HashMap $$23 = Maps.newHashMap();
                $$8.forEach((arg_0, arg_1) -> this.lambda$loadModel$22((Map)$$9, $$10, $$13, (Map)$$23, arg_0, arg_1));
                $$23.forEach(($$0, $$1) -> {
                    Iterator $$2 = $$1.iterator();
                    while ($$2.hasNext()) {
                        BlockState $$3 = (BlockState)$$2.next();
                        if ($$3.getRenderShape() == RenderShape.MODEL) continue;
                        $$2.remove();
                        this.modelGroups.put((Object)$$3, 0);
                    }
                    if ($$1.size() > 1) {
                        this.registerModelGroup((Iterable<BlockState>)$$1);
                    }
                });
            }
        }
    }

    private void cacheAndQueueDependencies(ResourceLocation $$0, UnbakedModel $$1) {
        this.unbakedCache.put((Object)$$0, (Object)$$1);
        this.loadingStack.addAll($$1.getDependencies());
    }

    private void loadTopLevel(ModelResourceLocation $$0) {
        UnbakedModel $$1 = this.getModel($$0);
        this.unbakedCache.put((Object)$$0, (Object)$$1);
        this.topLevelModels.put((Object)$$0, (Object)$$1);
    }

    private void registerModelGroup(Iterable<BlockState> $$0) {
        int $$12 = this.nextModelGroup++;
        $$0.forEach($$1 -> this.modelGroups.put($$1, $$12));
    }

    private BlockModel loadBlockModel(ResourceLocation $$0) throws IOException {
        String $$1 = $$0.getPath();
        if (BUILTIN_SLASH_GENERATED.equals((Object)$$1)) {
            return GENERATION_MARKER;
        }
        if (BUILTIN_BLOCK_ENTITY.equals((Object)$$1)) {
            return BLOCK_ENTITY_MARKER;
        }
        if ($$1.startsWith(BUILTIN_SLASH)) {
            String $$2 = $$1.substring(BUILTIN_SLASH.length());
            String $$3 = (String)BUILTIN_MODELS.get((Object)$$2);
            if ($$3 == null) {
                throw new FileNotFoundException($$0.toString());
            }
            StringReader $$4 = new StringReader($$3);
            BlockModel $$5 = BlockModel.fromStream((Reader)$$4);
            $$5.name = $$0.toString();
            return $$5;
        }
        ResourceLocation $$6 = MODEL_LISTER.idToFile($$0);
        BlockModel $$7 = (BlockModel)this.modelResources.get((Object)$$6);
        if ($$7 == null) {
            throw new FileNotFoundException($$6.toString());
        }
        $$7.name = $$0.toString();
        return $$7;
    }

    public Map<ResourceLocation, BakedModel> getBakedTopLevelModels() {
        return this.bakedTopLevelModels;
    }

    public Object2IntMap<BlockState> getModelGroups() {
        return this.modelGroups;
    }

    private /* synthetic */ void lambda$loadModel$22(Map $$02, ResourceLocation $$1, Pair $$2, Map $$3, ModelResourceLocation $$4, BlockState $$5) {
        Pair $$6 = (Pair)$$02.get((Object)$$5);
        if ($$6 == null) {
            LOGGER.warn("Exception loading blockstate definition: '{}' missing model for variant: '{}'", (Object)$$1, (Object)$$4);
            $$6 = $$2;
        }
        this.cacheAndQueueDependencies($$4, (UnbakedModel)$$6.getFirst());
        try {
            ModelGroupKey $$7 = (ModelGroupKey)((Supplier)$$6.getSecond()).get();
            ((Set)$$3.computeIfAbsent((Object)$$7, $$0 -> Sets.newIdentityHashSet())).add((Object)$$5);
        }
        catch (Exception $$8) {
            LOGGER.warn("Exception evaluating model definition: '{}'", (Object)$$4, (Object)$$8);
        }
    }

    private static /* synthetic */ void lambda$loadModel$20(ImmutableList $$0, StateDefinition $$1, Map $$2, List $$3, MultiPart $$4, Pair $$5, BlockModelDefinition $$62, ResourceLocation $$7, Pair $$8, String $$9, MultiVariant $$10) {
        try {
            $$0.stream().filter(ModelBakery.predicate($$1, $$9)).forEach($$6 -> {
                Pair $$7 = (Pair)$$2.put($$6, (Object)Pair.of((Object)$$10, () -> ModelGroupKey.create($$6, $$10, $$3)));
                if ($$7 != null && $$7.getFirst() != $$4) {
                    $$2.put($$6, (Object)$$5);
                    throw new RuntimeException("Overlapping definition with: " + (String)((Map.Entry)$$62.getVariants().entrySet().stream().filter($$1 -> $$1.getValue() == $$7.getFirst()).findFirst().get()).getKey());
                }
            });
        }
        catch (Exception $$11) {
            LOGGER.warn("Exception loading blockstate definition: '{}' in resourcepack: '{}' for variant: '{}': {}", new Object[]{$$7, $$8.getFirst(), $$9, $$11.getMessage()});
        }
    }

    private static /* synthetic */ void lambda$loadModel$16(Map $$0, MultiPart $$1, List $$2, BlockState $$3) {
        $$0.put((Object)$$3, (Object)Pair.of((Object)$$1, () -> ModelGroupKey.create($$3, $$1, $$2)));
    }

    private static /* synthetic */ void lambda$loadModel$12(Map $$0, ResourceLocation $$1, BlockState $$2) {
        $$0.put((Object)BlockModelShaper.stateToModelLocation($$1, $$2), (Object)$$2);
    }

    private static /* synthetic */ boolean lambda$predicate$10(Block $$0, Map $$1, BlockState $$2) {
        if ($$2 == null || !$$2.is($$0)) {
            return false;
        }
        for (Map.Entry $$3 : $$1.entrySet()) {
            if (Objects.equals($$2.getValue((Property)$$3.getKey()), (Object)$$3.getValue())) continue;
            return false;
        }
        return true;
    }

    static class BlockStateDefinitionException
    extends RuntimeException {
        public BlockStateDefinitionException(String $$0) {
            super($$0);
        }
    }

    static class ModelGroupKey {
        private final List<UnbakedModel> models;
        private final List<Object> coloringValues;

        public ModelGroupKey(List<UnbakedModel> $$0, List<Object> $$1) {
            this.models = $$0;
            this.coloringValues = $$1;
        }

        public boolean equals(Object $$0) {
            if (this == $$0) {
                return true;
            }
            if ($$0 instanceof ModelGroupKey) {
                ModelGroupKey $$1 = (ModelGroupKey)$$0;
                return Objects.equals(this.models, $$1.models) && Objects.equals(this.coloringValues, $$1.coloringValues);
            }
            return false;
        }

        public int hashCode() {
            return 31 * this.models.hashCode() + this.coloringValues.hashCode();
        }

        public static ModelGroupKey create(BlockState $$0, MultiPart $$1, Collection<Property<?>> $$22) {
            StateDefinition<Block, BlockState> $$3 = $$0.getBlock().getStateDefinition();
            List $$4 = (List)$$1.getSelectors().stream().filter($$2 -> $$2.getPredicate($$3).test((Object)$$0)).map(Selector::getVariant).collect(ImmutableList.toImmutableList());
            List<Object> $$5 = ModelGroupKey.getColoringValues($$0, $$22);
            return new ModelGroupKey((List<UnbakedModel>)$$4, $$5);
        }

        public static ModelGroupKey create(BlockState $$0, UnbakedModel $$1, Collection<Property<?>> $$2) {
            List<Object> $$3 = ModelGroupKey.getColoringValues($$0, $$2);
            return new ModelGroupKey((List<UnbakedModel>)ImmutableList.of((Object)$$1), $$3);
        }

        private static List<Object> getColoringValues(BlockState $$0, Collection<Property<?>> $$1) {
            return (List)$$1.stream().map($$0::getValue).collect(ImmutableList.toImmutableList());
        }
    }

    public record LoadedJson(String source, JsonElement data) {
    }

    class ModelBakerImpl
    implements ModelBaker {
        private final Function<Material, TextureAtlasSprite> modelTextureGetter = $$2 -> (TextureAtlasSprite)$$0.apply((Object)$$1, $$2);

        ModelBakerImpl(BiFunction<ResourceLocation, Material, TextureAtlasSprite> $$0, ResourceLocation $$1) {
        }

        @Override
        public UnbakedModel getModel(ResourceLocation $$0) {
            return ModelBakery.this.getModel($$0);
        }

        @Override
        public BakedModel bake(ResourceLocation $$0, ModelState $$1) {
            BlockModel $$5;
            BakedCacheKey $$2 = new BakedCacheKey($$0, $$1.getRotation(), $$1.isUvLocked());
            BakedModel $$3 = (BakedModel)ModelBakery.this.bakedCache.get((Object)$$2);
            if ($$3 != null) {
                return $$3;
            }
            UnbakedModel $$4 = this.getModel($$0);
            if ($$4 instanceof BlockModel && ($$5 = (BlockModel)$$4).getRootModel() == GENERATION_MARKER) {
                return ITEM_MODEL_GENERATOR.generateBlockModel(this.modelTextureGetter, $$5).bake(this, $$5, this.modelTextureGetter, $$1, $$0, false);
            }
            BakedModel $$6 = $$4.bake(this, this.modelTextureGetter, $$1, $$0);
            ModelBakery.this.bakedCache.put((Object)$$2, (Object)$$6);
            return $$6;
        }
    }

    record BakedCacheKey(ResourceLocation id, Transformation transformation, boolean isUvLocked) {
    }
}