/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonElement
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  java.lang.Boolean
 *  java.lang.Comparable
 *  java.lang.FunctionalInterface
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.UnsupportedOperationException
 *  java.util.Arrays
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.function.BiConsumer
 *  java.util.function.BiFunction
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  java.util.function.Supplier
 *  java.util.function.UnaryOperator
 *  java.util.stream.Collectors
 *  java.util.stream.IntStream
 *  javax.annotation.Nullable
 *  net.minecraft.world.level.block.Blocks
 */
package net.minecraft.data.models;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.blockstates.Condition;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.DelegatedModel;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.MangrovePropaguleBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.ComparatorMode;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.level.block.state.properties.SculkSensorPhase;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.block.state.properties.StructureMode;
import net.minecraft.world.level.block.state.properties.Tilt;
import net.minecraft.world.level.block.state.properties.WallSide;

public class BlockModelGenerators {
    final Consumer<BlockStateGenerator> blockStateOutput;
    final BiConsumer<ResourceLocation, Supplier<JsonElement>> modelOutput;
    private final Consumer<Item> skippedAutoModelsOutput;
    final List<Block> nonOrientableTrapdoor = ImmutableList.of((Object)Blocks.OAK_TRAPDOOR, (Object)Blocks.DARK_OAK_TRAPDOOR, (Object)Blocks.IRON_TRAPDOOR);
    final Map<Block, BlockStateGeneratorSupplier> fullBlockModelCustomGenerators = ImmutableMap.builder().put((Object)Blocks.STONE, BlockModelGenerators::createMirroredCubeGenerator).put((Object)Blocks.DEEPSLATE, BlockModelGenerators::createMirroredColumnGenerator).put((Object)Blocks.MUD_BRICKS, BlockModelGenerators::createNorthWestMirroredCubeGenerator).build();
    final Map<Block, TexturedModel> texturedModels = ImmutableMap.builder().put((Object)Blocks.SANDSTONE, (Object)TexturedModel.TOP_BOTTOM_WITH_WALL.get(Blocks.SANDSTONE)).put((Object)Blocks.RED_SANDSTONE, (Object)TexturedModel.TOP_BOTTOM_WITH_WALL.get(Blocks.RED_SANDSTONE)).put((Object)Blocks.SMOOTH_SANDSTONE, (Object)TexturedModel.createAllSame(TextureMapping.getBlockTexture(Blocks.SANDSTONE, "_top"))).put((Object)Blocks.SMOOTH_RED_SANDSTONE, (Object)TexturedModel.createAllSame(TextureMapping.getBlockTexture(Blocks.RED_SANDSTONE, "_top"))).put((Object)Blocks.CUT_SANDSTONE, (Object)TexturedModel.COLUMN.get(Blocks.SANDSTONE).updateTextures((Consumer<TextureMapping>)((Consumer)$$0 -> $$0.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CUT_SANDSTONE))))).put((Object)Blocks.CUT_RED_SANDSTONE, (Object)TexturedModel.COLUMN.get(Blocks.RED_SANDSTONE).updateTextures((Consumer<TextureMapping>)((Consumer)$$0 -> $$0.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CUT_RED_SANDSTONE))))).put((Object)Blocks.QUARTZ_BLOCK, (Object)TexturedModel.COLUMN.get(Blocks.QUARTZ_BLOCK)).put((Object)Blocks.SMOOTH_QUARTZ, (Object)TexturedModel.createAllSame(TextureMapping.getBlockTexture(Blocks.QUARTZ_BLOCK, "_bottom"))).put((Object)Blocks.BLACKSTONE, (Object)TexturedModel.COLUMN_WITH_WALL.get(Blocks.BLACKSTONE)).put((Object)Blocks.DEEPSLATE, (Object)TexturedModel.COLUMN_WITH_WALL.get(Blocks.DEEPSLATE)).put((Object)Blocks.CHISELED_QUARTZ_BLOCK, (Object)TexturedModel.COLUMN.get(Blocks.CHISELED_QUARTZ_BLOCK).updateTextures((Consumer<TextureMapping>)((Consumer)$$0 -> $$0.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CHISELED_QUARTZ_BLOCK))))).put((Object)Blocks.CHISELED_SANDSTONE, (Object)TexturedModel.COLUMN.get(Blocks.CHISELED_SANDSTONE).updateTextures((Consumer<TextureMapping>)((Consumer)$$0 -> {
        $$0.put(TextureSlot.END, TextureMapping.getBlockTexture(Blocks.SANDSTONE, "_top"));
        $$0.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CHISELED_SANDSTONE));
    }))).put((Object)Blocks.CHISELED_RED_SANDSTONE, (Object)TexturedModel.COLUMN.get(Blocks.CHISELED_RED_SANDSTONE).updateTextures((Consumer<TextureMapping>)((Consumer)$$0 -> {
        $$0.put(TextureSlot.END, TextureMapping.getBlockTexture(Blocks.RED_SANDSTONE, "_top"));
        $$0.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.CHISELED_RED_SANDSTONE));
    }))).build();
    static final Map<BlockFamily.Variant, BiConsumer<BlockFamilyProvider, Block>> SHAPE_CONSUMERS = ImmutableMap.builder().put((Object)BlockFamily.Variant.BUTTON, BlockFamilyProvider::button).put((Object)BlockFamily.Variant.DOOR, BlockFamilyProvider::door).put((Object)BlockFamily.Variant.CHISELED, BlockFamilyProvider::fullBlockVariant).put((Object)BlockFamily.Variant.CRACKED, BlockFamilyProvider::fullBlockVariant).put((Object)BlockFamily.Variant.CUSTOM_FENCE, BlockFamilyProvider::customFence).put((Object)BlockFamily.Variant.FENCE, BlockFamilyProvider::fence).put((Object)BlockFamily.Variant.CUSTOM_FENCE_GATE, BlockFamilyProvider::customFenceGate).put((Object)BlockFamily.Variant.FENCE_GATE, BlockFamilyProvider::fenceGate).put((Object)BlockFamily.Variant.SIGN, BlockFamilyProvider::sign).put((Object)BlockFamily.Variant.SLAB, BlockFamilyProvider::slab).put((Object)BlockFamily.Variant.STAIRS, BlockFamilyProvider::stairs).put((Object)BlockFamily.Variant.PRESSURE_PLATE, BlockFamilyProvider::pressurePlate).put((Object)BlockFamily.Variant.TRAPDOOR, BlockFamilyProvider::trapdoor).put((Object)BlockFamily.Variant.WALL, BlockFamilyProvider::wall).build();
    public static final List<Pair<BooleanProperty, Function<ResourceLocation, Variant>>> MULTIFACE_GENERATOR = List.of((Object)Pair.of((Object)BlockStateProperties.NORTH, $$0 -> Variant.variant().with(VariantProperties.MODEL, $$0)), (Object)Pair.of((Object)BlockStateProperties.EAST, $$0 -> Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)), (Object)Pair.of((Object)BlockStateProperties.SOUTH, $$0 -> Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)), (Object)Pair.of((Object)BlockStateProperties.WEST, $$0 -> Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)), (Object)Pair.of((Object)BlockStateProperties.UP, $$0 -> Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)), (Object)Pair.of((Object)BlockStateProperties.DOWN, $$0 -> Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)));
    private static final Map<BookSlotModelCacheKey, ResourceLocation> CHISELED_BOOKSHELF_SLOT_MODEL_CACHE = new HashMap();

    private static BlockStateGenerator createMirroredCubeGenerator(Block $$0, ResourceLocation $$1, TextureMapping $$2, BiConsumer<ResourceLocation, Supplier<JsonElement>> $$3) {
        ResourceLocation $$4 = ModelTemplates.CUBE_MIRRORED_ALL.create($$0, $$2, $$3);
        return BlockModelGenerators.createRotatedVariant($$0, $$1, $$4);
    }

    private static BlockStateGenerator createNorthWestMirroredCubeGenerator(Block $$0, ResourceLocation $$1, TextureMapping $$2, BiConsumer<ResourceLocation, Supplier<JsonElement>> $$3) {
        ResourceLocation $$4 = ModelTemplates.CUBE_NORTH_WEST_MIRRORED_ALL.create($$0, $$2, $$3);
        return BlockModelGenerators.createSimpleBlock($$0, $$4);
    }

    private static BlockStateGenerator createMirroredColumnGenerator(Block $$0, ResourceLocation $$1, TextureMapping $$2, BiConsumer<ResourceLocation, Supplier<JsonElement>> $$3) {
        ResourceLocation $$4 = ModelTemplates.CUBE_COLUMN_MIRRORED.create($$0, $$2, $$3);
        return BlockModelGenerators.createRotatedVariant($$0, $$1, $$4).with(BlockModelGenerators.createRotatedPillar());
    }

    public BlockModelGenerators(Consumer<BlockStateGenerator> $$02, BiConsumer<ResourceLocation, Supplier<JsonElement>> $$1, Consumer<Item> $$2) {
        this.blockStateOutput = $$02;
        this.modelOutput = $$1;
        this.skippedAutoModelsOutput = $$2;
    }

    void skipAutoItemBlock(Block $$0) {
        this.skippedAutoModelsOutput.accept((Object)$$0.asItem());
    }

    void delegateItemModel(Block $$0, ResourceLocation $$1) {
        this.modelOutput.accept((Object)ModelLocationUtils.getModelLocation($$0.asItem()), (Object)new DelegatedModel($$1));
    }

    private void delegateItemModel(Item $$0, ResourceLocation $$1) {
        this.modelOutput.accept((Object)ModelLocationUtils.getModelLocation($$0), (Object)new DelegatedModel($$1));
    }

    void createSimpleFlatItemModel(Item $$0) {
        ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation($$0), TextureMapping.layer0($$0), this.modelOutput);
    }

    private void createSimpleFlatItemModel(Block $$0) {
        Item $$1 = $$0.asItem();
        if ($$1 != Items.AIR) {
            ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation($$1), TextureMapping.layer0($$0), this.modelOutput);
        }
    }

    private void createSimpleFlatItemModel(Block $$0, String $$1) {
        Item $$2 = $$0.asItem();
        ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation($$2), TextureMapping.layer0(TextureMapping.getBlockTexture($$0, $$1)), this.modelOutput);
    }

    private static PropertyDispatch createHorizontalFacingDispatch() {
        return PropertyDispatch.property(BlockStateProperties.HORIZONTAL_FACING).select(Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.NORTH, Variant.variant());
    }

    private static PropertyDispatch createHorizontalFacingDispatchAlt() {
        return PropertyDispatch.property(BlockStateProperties.HORIZONTAL_FACING).select(Direction.SOUTH, Variant.variant()).select(Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.NORTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270));
    }

    private static PropertyDispatch createTorchHorizontalDispatch() {
        return PropertyDispatch.property(BlockStateProperties.HORIZONTAL_FACING).select(Direction.EAST, Variant.variant()).select(Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.NORTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270));
    }

    private static PropertyDispatch createFacingDispatch() {
        return PropertyDispatch.property(BlockStateProperties.FACING).select(Direction.DOWN, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)).select(Direction.UP, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)).select(Direction.NORTH, Variant.variant()).select(Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90));
    }

    private static MultiVariantGenerator createRotatedVariant(Block $$0, ResourceLocation $$1) {
        return MultiVariantGenerator.multiVariant($$0, BlockModelGenerators.createRotatedVariants($$1));
    }

    private static Variant[] createRotatedVariants(ResourceLocation $$0) {
        return new Variant[]{Variant.variant().with(VariantProperties.MODEL, $$0), Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90), Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180), Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)};
    }

    private static MultiVariantGenerator createRotatedVariant(Block $$0, ResourceLocation $$1, ResourceLocation $$2) {
        return MultiVariantGenerator.multiVariant($$0, Variant.variant().with(VariantProperties.MODEL, $$1), Variant.variant().with(VariantProperties.MODEL, $$2), Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180), Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180));
    }

    private static PropertyDispatch createBooleanModelDispatch(BooleanProperty $$0, ResourceLocation $$1, ResourceLocation $$2) {
        return PropertyDispatch.property($$0).select((Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$1)).select((Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$2));
    }

    private void createRotatedMirroredVariantBlock(Block $$0) {
        ResourceLocation $$1 = TexturedModel.CUBE.create($$0, this.modelOutput);
        ResourceLocation $$2 = TexturedModel.CUBE_MIRRORED.create($$0, this.modelOutput);
        this.blockStateOutput.accept((Object)BlockModelGenerators.createRotatedVariant($$0, $$1, $$2));
    }

    private void createRotatedVariantBlock(Block $$0) {
        ResourceLocation $$1 = TexturedModel.CUBE.create($$0, this.modelOutput);
        this.blockStateOutput.accept((Object)BlockModelGenerators.createRotatedVariant($$0, $$1));
    }

    static BlockStateGenerator createButton(Block $$0, ResourceLocation $$1, ResourceLocation $$2) {
        return MultiVariantGenerator.multiVariant($$0).with(PropertyDispatch.property(BlockStateProperties.POWERED).select((Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$1)).select((Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$2))).with(PropertyDispatch.properties(BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING).select(AttachFace.FLOOR, Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(AttachFace.FLOOR, Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(AttachFace.FLOOR, Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(AttachFace.FLOOR, Direction.NORTH, Variant.variant()).select(AttachFace.WALL, Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(AttachFace.WALL, Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(AttachFace.WALL, Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(AttachFace.WALL, Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(AttachFace.CEILING, Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)).select(AttachFace.CEILING, Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)).select(AttachFace.CEILING, Direction.SOUTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)).select(AttachFace.CEILING, Direction.NORTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)));
    }

    private static PropertyDispatch.C4<Direction, DoubleBlockHalf, DoorHingeSide, Boolean> configureDoorHalf(PropertyDispatch.C4<Direction, DoubleBlockHalf, DoorHingeSide, Boolean> $$0, DoubleBlockHalf $$1, ResourceLocation $$2, ResourceLocation $$3, ResourceLocation $$4, ResourceLocation $$5) {
        return $$0.select(Direction.EAST, $$1, DoorHingeSide.LEFT, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$2)).select(Direction.SOUTH, $$1, DoorHingeSide.LEFT, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.WEST, $$1, DoorHingeSide.LEFT, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.NORTH, $$1, DoorHingeSide.LEFT, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.EAST, $$1, DoorHingeSide.RIGHT, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$4)).select(Direction.SOUTH, $$1, DoorHingeSide.RIGHT, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.WEST, $$1, DoorHingeSide.RIGHT, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.NORTH, $$1, DoorHingeSide.RIGHT, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.EAST, $$1, DoorHingeSide.LEFT, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.SOUTH, $$1, DoorHingeSide.LEFT, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.WEST, $$1, DoorHingeSide.LEFT, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.NORTH, $$1, DoorHingeSide.LEFT, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$3)).select(Direction.EAST, $$1, DoorHingeSide.RIGHT, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$5).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.SOUTH, $$1, DoorHingeSide.RIGHT, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$5)).select(Direction.WEST, $$1, DoorHingeSide.RIGHT, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$5).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.NORTH, $$1, DoorHingeSide.RIGHT, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$5).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180));
    }

    private static BlockStateGenerator createDoor(Block $$0, ResourceLocation $$1, ResourceLocation $$2, ResourceLocation $$3, ResourceLocation $$4, ResourceLocation $$5, ResourceLocation $$6, ResourceLocation $$7, ResourceLocation $$8) {
        return MultiVariantGenerator.multiVariant($$0).with(BlockModelGenerators.configureDoorHalf(BlockModelGenerators.configureDoorHalf(PropertyDispatch.properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.DOUBLE_BLOCK_HALF, BlockStateProperties.DOOR_HINGE, BlockStateProperties.OPEN), DoubleBlockHalf.LOWER, $$1, $$2, $$3, $$4), DoubleBlockHalf.UPPER, $$5, $$6, $$7, $$8));
    }

    static BlockStateGenerator createCustomFence(Block $$0, ResourceLocation $$1, ResourceLocation $$2, ResourceLocation $$3, ResourceLocation $$4, ResourceLocation $$5) {
        return MultiPartGenerator.multiPart($$0).with(Variant.variant().with(VariantProperties.MODEL, $$1)).with((Condition)Condition.condition().term(BlockStateProperties.NORTH, true), Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.UV_LOCK, false)).with((Condition)Condition.condition().term(BlockStateProperties.EAST, true), Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.UV_LOCK, false)).with((Condition)Condition.condition().term(BlockStateProperties.SOUTH, true), Variant.variant().with(VariantProperties.MODEL, $$4).with(VariantProperties.UV_LOCK, false)).with((Condition)Condition.condition().term(BlockStateProperties.WEST, true), Variant.variant().with(VariantProperties.MODEL, $$5).with(VariantProperties.UV_LOCK, false));
    }

    static BlockStateGenerator createFence(Block $$0, ResourceLocation $$1, ResourceLocation $$2) {
        return MultiPartGenerator.multiPart($$0).with(Variant.variant().with(VariantProperties.MODEL, $$1)).with((Condition)Condition.condition().term(BlockStateProperties.NORTH, true), Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.UV_LOCK, true)).with((Condition)Condition.condition().term(BlockStateProperties.EAST, true), Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).with((Condition)Condition.condition().term(BlockStateProperties.SOUTH, true), Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).with((Condition)Condition.condition().term(BlockStateProperties.WEST, true), Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true));
    }

    static BlockStateGenerator createWall(Block $$0, ResourceLocation $$1, ResourceLocation $$2, ResourceLocation $$3) {
        return MultiPartGenerator.multiPart($$0).with((Condition)Condition.condition().term(BlockStateProperties.UP, true), Variant.variant().with(VariantProperties.MODEL, $$1)).with((Condition)Condition.condition().term(BlockStateProperties.NORTH_WALL, WallSide.LOW), Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.UV_LOCK, true)).with((Condition)Condition.condition().term(BlockStateProperties.EAST_WALL, WallSide.LOW), Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).with((Condition)Condition.condition().term(BlockStateProperties.SOUTH_WALL, WallSide.LOW), Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).with((Condition)Condition.condition().term(BlockStateProperties.WEST_WALL, WallSide.LOW), Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).with((Condition)Condition.condition().term(BlockStateProperties.NORTH_WALL, WallSide.TALL), Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.UV_LOCK, true)).with((Condition)Condition.condition().term(BlockStateProperties.EAST_WALL, WallSide.TALL), Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).with((Condition)Condition.condition().term(BlockStateProperties.SOUTH_WALL, WallSide.TALL), Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).with((Condition)Condition.condition().term(BlockStateProperties.WEST_WALL, WallSide.TALL), Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true));
    }

    static BlockStateGenerator createFenceGate(Block $$0, ResourceLocation $$1, ResourceLocation $$2, ResourceLocation $$3, ResourceLocation $$4, boolean $$5) {
        return MultiVariantGenerator.multiVariant($$0, Variant.variant().with(VariantProperties.UV_LOCK, $$5)).with(BlockModelGenerators.createHorizontalFacingDispatchAlt()).with(PropertyDispatch.properties(BlockStateProperties.IN_WALL, BlockStateProperties.OPEN).select((Boolean)false, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$2)).select((Boolean)true, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$4)).select((Boolean)false, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$1)).select((Boolean)true, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$3)));
    }

    static BlockStateGenerator createStairs(Block $$0, ResourceLocation $$1, ResourceLocation $$2, ResourceLocation $$3) {
        return MultiVariantGenerator.multiVariant($$0).with(PropertyDispatch.properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HALF, BlockStateProperties.STAIRS_SHAPE).select(Direction.EAST, Half.BOTTOM, StairsShape.STRAIGHT, Variant.variant().with(VariantProperties.MODEL, $$2)).select(Direction.WEST, Half.BOTTOM, StairsShape.STRAIGHT, Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.SOUTH, Half.BOTTOM, StairsShape.STRAIGHT, Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(Direction.NORTH, Half.BOTTOM, StairsShape.STRAIGHT, Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).select(Direction.EAST, Half.BOTTOM, StairsShape.OUTER_RIGHT, Variant.variant().with(VariantProperties.MODEL, $$3)).select(Direction.WEST, Half.BOTTOM, StairsShape.OUTER_RIGHT, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.SOUTH, Half.BOTTOM, StairsShape.OUTER_RIGHT, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(Direction.NORTH, Half.BOTTOM, StairsShape.OUTER_RIGHT, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).select(Direction.EAST, Half.BOTTOM, StairsShape.OUTER_LEFT, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).select(Direction.WEST, Half.BOTTOM, StairsShape.OUTER_LEFT, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(Direction.SOUTH, Half.BOTTOM, StairsShape.OUTER_LEFT, Variant.variant().with(VariantProperties.MODEL, $$3)).select(Direction.NORTH, Half.BOTTOM, StairsShape.OUTER_LEFT, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.EAST, Half.BOTTOM, StairsShape.INNER_RIGHT, Variant.variant().with(VariantProperties.MODEL, $$1)).select(Direction.WEST, Half.BOTTOM, StairsShape.INNER_RIGHT, Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.SOUTH, Half.BOTTOM, StairsShape.INNER_RIGHT, Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(Direction.NORTH, Half.BOTTOM, StairsShape.INNER_RIGHT, Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).select(Direction.EAST, Half.BOTTOM, StairsShape.INNER_LEFT, Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).select(Direction.WEST, Half.BOTTOM, StairsShape.INNER_LEFT, Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(Direction.SOUTH, Half.BOTTOM, StairsShape.INNER_LEFT, Variant.variant().with(VariantProperties.MODEL, $$1)).select(Direction.NORTH, Half.BOTTOM, StairsShape.INNER_LEFT, Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.EAST, Half.TOP, StairsShape.STRAIGHT, Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.WEST, Half.TOP, StairsShape.STRAIGHT, Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.SOUTH, Half.TOP, StairsShape.STRAIGHT, Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(Direction.NORTH, Half.TOP, StairsShape.STRAIGHT, Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).select(Direction.EAST, Half.TOP, StairsShape.OUTER_RIGHT, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(Direction.WEST, Half.TOP, StairsShape.OUTER_RIGHT, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).select(Direction.SOUTH, Half.TOP, StairsShape.OUTER_RIGHT, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.NORTH, Half.TOP, StairsShape.OUTER_RIGHT, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.EAST, Half.TOP, StairsShape.OUTER_LEFT, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.WEST, Half.TOP, StairsShape.OUTER_LEFT, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.SOUTH, Half.TOP, StairsShape.OUTER_LEFT, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(Direction.NORTH, Half.TOP, StairsShape.OUTER_LEFT, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).select(Direction.EAST, Half.TOP, StairsShape.INNER_RIGHT, Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(Direction.WEST, Half.TOP, StairsShape.INNER_RIGHT, Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).select(Direction.SOUTH, Half.TOP, StairsShape.INNER_RIGHT, Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.NORTH, Half.TOP, StairsShape.INNER_RIGHT, Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.EAST, Half.TOP, StairsShape.INNER_LEFT, Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.WEST, Half.TOP, StairsShape.INNER_LEFT, Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).select(Direction.SOUTH, Half.TOP, StairsShape.INNER_LEFT, Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).select(Direction.NORTH, Half.TOP, StairsShape.INNER_LEFT, Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)));
    }

    private static BlockStateGenerator createOrientableTrapdoor(Block $$0, ResourceLocation $$1, ResourceLocation $$2, ResourceLocation $$3) {
        return MultiVariantGenerator.multiVariant($$0).with(PropertyDispatch.properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HALF, BlockStateProperties.OPEN).select(Direction.NORTH, Half.BOTTOM, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$2)).select(Direction.SOUTH, Half.BOTTOM, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.EAST, Half.BOTTOM, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.WEST, Half.BOTTOM, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.NORTH, Half.TOP, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$1)).select(Direction.SOUTH, Half.TOP, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.EAST, Half.TOP, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.WEST, Half.TOP, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.NORTH, Half.BOTTOM, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$3)).select(Direction.SOUTH, Half.BOTTOM, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.EAST, Half.BOTTOM, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.WEST, Half.BOTTOM, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.NORTH, Half.TOP, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.SOUTH, Half.TOP, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R0)).select(Direction.EAST, Half.TOP, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.WEST, Half.TOP, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)));
    }

    private static BlockStateGenerator createTrapdoor(Block $$0, ResourceLocation $$1, ResourceLocation $$2, ResourceLocation $$3) {
        return MultiVariantGenerator.multiVariant($$0).with(PropertyDispatch.properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HALF, BlockStateProperties.OPEN).select(Direction.NORTH, Half.BOTTOM, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$2)).select(Direction.SOUTH, Half.BOTTOM, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$2)).select(Direction.EAST, Half.BOTTOM, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$2)).select(Direction.WEST, Half.BOTTOM, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$2)).select(Direction.NORTH, Half.TOP, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$1)).select(Direction.SOUTH, Half.TOP, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$1)).select(Direction.EAST, Half.TOP, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$1)).select(Direction.WEST, Half.TOP, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$1)).select(Direction.NORTH, Half.BOTTOM, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$3)).select(Direction.SOUTH, Half.BOTTOM, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.EAST, Half.BOTTOM, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.WEST, Half.BOTTOM, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.NORTH, Half.TOP, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$3)).select(Direction.SOUTH, Half.TOP, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.EAST, Half.TOP, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.WEST, Half.TOP, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)));
    }

    static MultiVariantGenerator createSimpleBlock(Block $$0, ResourceLocation $$1) {
        return MultiVariantGenerator.multiVariant($$0, Variant.variant().with(VariantProperties.MODEL, $$1));
    }

    private static PropertyDispatch createRotatedPillar() {
        return PropertyDispatch.property(BlockStateProperties.AXIS).select(Direction.Axis.Y, Variant.variant()).select(Direction.Axis.Z, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)).select(Direction.Axis.X, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90));
    }

    static BlockStateGenerator createPillarBlockUVLocked(Block $$0, TextureMapping $$1, BiConsumer<ResourceLocation, Supplier<JsonElement>> $$2) {
        ResourceLocation $$3 = ModelTemplates.CUBE_COLUMN_UV_LOCKED_X.create($$0, $$1, $$2);
        ResourceLocation $$4 = ModelTemplates.CUBE_COLUMN_UV_LOCKED_Y.create($$0, $$1, $$2);
        ResourceLocation $$5 = ModelTemplates.CUBE_COLUMN_UV_LOCKED_Z.create($$0, $$1, $$2);
        ResourceLocation $$6 = ModelTemplates.CUBE_COLUMN.create($$0, $$1, $$2);
        return MultiVariantGenerator.multiVariant($$0, Variant.variant().with(VariantProperties.MODEL, $$6)).with(PropertyDispatch.property(BlockStateProperties.AXIS).select(Direction.Axis.X, Variant.variant().with(VariantProperties.MODEL, $$3)).select(Direction.Axis.Y, Variant.variant().with(VariantProperties.MODEL, $$4)).select(Direction.Axis.Z, Variant.variant().with(VariantProperties.MODEL, $$5)));
    }

    static BlockStateGenerator createAxisAlignedPillarBlock(Block $$0, ResourceLocation $$1) {
        return MultiVariantGenerator.multiVariant($$0, Variant.variant().with(VariantProperties.MODEL, $$1)).with(BlockModelGenerators.createRotatedPillar());
    }

    private void createAxisAlignedPillarBlockCustomModel(Block $$0, ResourceLocation $$1) {
        this.blockStateOutput.accept((Object)BlockModelGenerators.createAxisAlignedPillarBlock($$0, $$1));
    }

    private void createAxisAlignedPillarBlock(Block $$0, TexturedModel.Provider $$1) {
        ResourceLocation $$2 = $$1.create($$0, this.modelOutput);
        this.blockStateOutput.accept((Object)BlockModelGenerators.createAxisAlignedPillarBlock($$0, $$2));
    }

    private void createHorizontallyRotatedBlock(Block $$0, TexturedModel.Provider $$1) {
        ResourceLocation $$2 = $$1.create($$0, this.modelOutput);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant($$0, Variant.variant().with(VariantProperties.MODEL, $$2)).with(BlockModelGenerators.createHorizontalFacingDispatch()));
    }

    static BlockStateGenerator createRotatedPillarWithHorizontalVariant(Block $$0, ResourceLocation $$1, ResourceLocation $$2) {
        return MultiVariantGenerator.multiVariant($$0).with(PropertyDispatch.property(BlockStateProperties.AXIS).select(Direction.Axis.Y, Variant.variant().with(VariantProperties.MODEL, $$1)).select(Direction.Axis.Z, Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)).select(Direction.Axis.X, Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)));
    }

    private void createRotatedPillarWithHorizontalVariant(Block $$0, TexturedModel.Provider $$1, TexturedModel.Provider $$2) {
        ResourceLocation $$3 = $$1.create($$0, this.modelOutput);
        ResourceLocation $$4 = $$2.create($$0, this.modelOutput);
        this.blockStateOutput.accept((Object)BlockModelGenerators.createRotatedPillarWithHorizontalVariant($$0, $$3, $$4));
    }

    private ResourceLocation createSuffixedVariant(Block $$0, String $$1, ModelTemplate $$2, Function<ResourceLocation, TextureMapping> $$3) {
        return $$2.createWithSuffix($$0, $$1, (TextureMapping)$$3.apply((Object)TextureMapping.getBlockTexture($$0, $$1)), this.modelOutput);
    }

    static BlockStateGenerator createPressurePlate(Block $$0, ResourceLocation $$1, ResourceLocation $$2) {
        return MultiVariantGenerator.multiVariant($$0).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.POWERED, $$2, $$1));
    }

    static BlockStateGenerator createSlab(Block $$0, ResourceLocation $$1, ResourceLocation $$2, ResourceLocation $$3) {
        return MultiVariantGenerator.multiVariant($$0).with(PropertyDispatch.property(BlockStateProperties.SLAB_TYPE).select(SlabType.BOTTOM, Variant.variant().with(VariantProperties.MODEL, $$1)).select(SlabType.TOP, Variant.variant().with(VariantProperties.MODEL, $$2)).select(SlabType.DOUBLE, Variant.variant().with(VariantProperties.MODEL, $$3)));
    }

    private void createTrivialCube(Block $$0) {
        this.createTrivialBlock($$0, TexturedModel.CUBE);
    }

    private void createTrivialBlock(Block $$0, TexturedModel.Provider $$1) {
        this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock($$0, $$1.create($$0, this.modelOutput)));
    }

    private void createTrivialBlock(Block $$0, TextureMapping $$1, ModelTemplate $$2) {
        ResourceLocation $$3 = $$2.create($$0, $$1, this.modelOutput);
        this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock($$0, $$3));
    }

    private BlockFamilyProvider family(Block $$0) {
        TexturedModel $$1 = (TexturedModel)this.texturedModels.getOrDefault((Object)$$0, (Object)TexturedModel.CUBE.get($$0));
        return new BlockFamilyProvider($$1.getMapping()).fullBlock($$0, $$1.getTemplate());
    }

    public void createHangingSign(Block $$0, Block $$1, Block $$2) {
        TextureMapping $$3 = TextureMapping.particle($$0);
        ResourceLocation $$4 = ModelTemplates.PARTICLE_ONLY.create($$1, $$3, this.modelOutput);
        this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock($$1, $$4));
        this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock($$2, $$4));
        this.createSimpleFlatItemModel($$1.asItem());
        this.skipAutoItemBlock($$2);
    }

    void createDoor(Block $$0) {
        TextureMapping $$1 = TextureMapping.door($$0);
        ResourceLocation $$2 = ModelTemplates.DOOR_BOTTOM_LEFT.create($$0, $$1, this.modelOutput);
        ResourceLocation $$3 = ModelTemplates.DOOR_BOTTOM_LEFT_OPEN.create($$0, $$1, this.modelOutput);
        ResourceLocation $$4 = ModelTemplates.DOOR_BOTTOM_RIGHT.create($$0, $$1, this.modelOutput);
        ResourceLocation $$5 = ModelTemplates.DOOR_BOTTOM_RIGHT_OPEN.create($$0, $$1, this.modelOutput);
        ResourceLocation $$6 = ModelTemplates.DOOR_TOP_LEFT.create($$0, $$1, this.modelOutput);
        ResourceLocation $$7 = ModelTemplates.DOOR_TOP_LEFT_OPEN.create($$0, $$1, this.modelOutput);
        ResourceLocation $$8 = ModelTemplates.DOOR_TOP_RIGHT.create($$0, $$1, this.modelOutput);
        ResourceLocation $$9 = ModelTemplates.DOOR_TOP_RIGHT_OPEN.create($$0, $$1, this.modelOutput);
        this.createSimpleFlatItemModel($$0.asItem());
        this.blockStateOutput.accept((Object)BlockModelGenerators.createDoor($$0, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9));
    }

    void createOrientableTrapdoor(Block $$0) {
        TextureMapping $$1 = TextureMapping.defaultTexture($$0);
        ResourceLocation $$2 = ModelTemplates.ORIENTABLE_TRAPDOOR_TOP.create($$0, $$1, this.modelOutput);
        ResourceLocation $$3 = ModelTemplates.ORIENTABLE_TRAPDOOR_BOTTOM.create($$0, $$1, this.modelOutput);
        ResourceLocation $$4 = ModelTemplates.ORIENTABLE_TRAPDOOR_OPEN.create($$0, $$1, this.modelOutput);
        this.blockStateOutput.accept((Object)BlockModelGenerators.createOrientableTrapdoor($$0, $$2, $$3, $$4));
        this.delegateItemModel($$0, $$3);
    }

    void createTrapdoor(Block $$0) {
        TextureMapping $$1 = TextureMapping.defaultTexture($$0);
        ResourceLocation $$2 = ModelTemplates.TRAPDOOR_TOP.create($$0, $$1, this.modelOutput);
        ResourceLocation $$3 = ModelTemplates.TRAPDOOR_BOTTOM.create($$0, $$1, this.modelOutput);
        ResourceLocation $$4 = ModelTemplates.TRAPDOOR_OPEN.create($$0, $$1, this.modelOutput);
        this.blockStateOutput.accept((Object)BlockModelGenerators.createTrapdoor($$0, $$2, $$3, $$4));
        this.delegateItemModel($$0, $$3);
    }

    private void createBigDripLeafBlock() {
        this.skipAutoItemBlock(Blocks.BIG_DRIPLEAF);
        ResourceLocation $$0 = ModelLocationUtils.getModelLocation(Blocks.BIG_DRIPLEAF);
        ResourceLocation $$1 = ModelLocationUtils.getModelLocation(Blocks.BIG_DRIPLEAF, "_partial_tilt");
        ResourceLocation $$2 = ModelLocationUtils.getModelLocation(Blocks.BIG_DRIPLEAF, "_full_tilt");
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.BIG_DRIPLEAF).with(BlockModelGenerators.createHorizontalFacingDispatch()).with(PropertyDispatch.property(BlockStateProperties.TILT).select(Tilt.NONE, Variant.variant().with(VariantProperties.MODEL, $$0)).select(Tilt.UNSTABLE, Variant.variant().with(VariantProperties.MODEL, $$0)).select(Tilt.PARTIAL, Variant.variant().with(VariantProperties.MODEL, $$1)).select(Tilt.FULL, Variant.variant().with(VariantProperties.MODEL, $$2))));
    }

    private WoodProvider woodProvider(Block $$0) {
        return new WoodProvider(TextureMapping.logColumn($$0));
    }

    private void createNonTemplateModelBlock(Block $$0) {
        this.createNonTemplateModelBlock($$0, $$0);
    }

    private void createNonTemplateModelBlock(Block $$0, Block $$1) {
        this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock($$0, ModelLocationUtils.getModelLocation($$1)));
    }

    private void createCrossBlockWithDefaultItem(Block $$0, TintState $$1) {
        this.createSimpleFlatItemModel($$0);
        this.createCrossBlock($$0, $$1);
    }

    private void createCrossBlockWithDefaultItem(Block $$0, TintState $$1, TextureMapping $$2) {
        this.createSimpleFlatItemModel($$0);
        this.createCrossBlock($$0, $$1, $$2);
    }

    private void createCrossBlock(Block $$0, TintState $$1) {
        TextureMapping $$2 = TextureMapping.cross($$0);
        this.createCrossBlock($$0, $$1, $$2);
    }

    private void createCrossBlock(Block $$0, TintState $$1, TextureMapping $$2) {
        ResourceLocation $$3 = $$1.getCross().create($$0, $$2, this.modelOutput);
        this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock($$0, $$3));
    }

    private void createPlant(Block $$0, Block $$1, TintState $$2) {
        this.createCrossBlockWithDefaultItem($$0, $$2);
        TextureMapping $$3 = TextureMapping.plant($$0);
        ResourceLocation $$4 = $$2.getCrossPot().create($$1, $$3, this.modelOutput);
        this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock($$1, $$4));
    }

    private void createCoralFans(Block $$0, Block $$1) {
        TexturedModel $$2 = TexturedModel.CORAL_FAN.get($$0);
        ResourceLocation $$3 = $$2.create($$0, this.modelOutput);
        this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock($$0, $$3));
        ResourceLocation $$4 = ModelTemplates.CORAL_WALL_FAN.create($$1, $$2.getMapping(), this.modelOutput);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant($$1, Variant.variant().with(VariantProperties.MODEL, $$4)).with(BlockModelGenerators.createHorizontalFacingDispatch()));
        this.createSimpleFlatItemModel($$0);
    }

    private void createStems(Block $$0, Block $$1) {
        this.createSimpleFlatItemModel($$0.asItem());
        TextureMapping $$22 = TextureMapping.stem($$0);
        TextureMapping $$3 = TextureMapping.attachedStem($$0, $$1);
        ResourceLocation $$4 = ModelTemplates.ATTACHED_STEM.create($$1, $$3, this.modelOutput);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant($$1, Variant.variant().with(VariantProperties.MODEL, $$4)).with(PropertyDispatch.property(BlockStateProperties.HORIZONTAL_FACING).select(Direction.WEST, Variant.variant()).select(Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.NORTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))));
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant($$0).with(PropertyDispatch.property(BlockStateProperties.AGE_7).generate((Function<Integer, Variant>)((Function)$$2 -> Variant.variant().with(VariantProperties.MODEL, ModelTemplates.STEMS[$$2].create($$0, $$22, this.modelOutput))))));
    }

    private void createCoral(Block $$0, Block $$1, Block $$2, Block $$3, Block $$4, Block $$5, Block $$6, Block $$7) {
        this.createCrossBlockWithDefaultItem($$0, TintState.NOT_TINTED);
        this.createCrossBlockWithDefaultItem($$1, TintState.NOT_TINTED);
        this.createTrivialCube($$2);
        this.createTrivialCube($$3);
        this.createCoralFans($$4, $$6);
        this.createCoralFans($$5, $$7);
    }

    private void createDoublePlant(Block $$0, TintState $$1) {
        this.createSimpleFlatItemModel($$0, "_top");
        ResourceLocation $$2 = this.createSuffixedVariant($$0, "_top", $$1.getCross(), (Function<ResourceLocation, TextureMapping>)((Function)TextureMapping::cross));
        ResourceLocation $$3 = this.createSuffixedVariant($$0, "_bottom", $$1.getCross(), (Function<ResourceLocation, TextureMapping>)((Function)TextureMapping::cross));
        this.createDoubleBlock($$0, $$2, $$3);
    }

    private void createSunflower() {
        this.createSimpleFlatItemModel(Blocks.SUNFLOWER, "_front");
        ResourceLocation $$0 = ModelLocationUtils.getModelLocation(Blocks.SUNFLOWER, "_top");
        ResourceLocation $$1 = this.createSuffixedVariant(Blocks.SUNFLOWER, "_bottom", TintState.NOT_TINTED.getCross(), (Function<ResourceLocation, TextureMapping>)((Function)TextureMapping::cross));
        this.createDoubleBlock(Blocks.SUNFLOWER, $$0, $$1);
    }

    private void createTallSeagrass() {
        ResourceLocation $$0 = this.createSuffixedVariant(Blocks.TALL_SEAGRASS, "_top", ModelTemplates.SEAGRASS, (Function<ResourceLocation, TextureMapping>)((Function)TextureMapping::defaultTexture));
        ResourceLocation $$1 = this.createSuffixedVariant(Blocks.TALL_SEAGRASS, "_bottom", ModelTemplates.SEAGRASS, (Function<ResourceLocation, TextureMapping>)((Function)TextureMapping::defaultTexture));
        this.createDoubleBlock(Blocks.TALL_SEAGRASS, $$0, $$1);
    }

    private void createSmallDripleaf() {
        this.skipAutoItemBlock(Blocks.SMALL_DRIPLEAF);
        ResourceLocation $$0 = ModelLocationUtils.getModelLocation(Blocks.SMALL_DRIPLEAF, "_top");
        ResourceLocation $$1 = ModelLocationUtils.getModelLocation(Blocks.SMALL_DRIPLEAF, "_bottom");
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.SMALL_DRIPLEAF).with(BlockModelGenerators.createHorizontalFacingDispatch()).with(PropertyDispatch.property(BlockStateProperties.DOUBLE_BLOCK_HALF).select(DoubleBlockHalf.LOWER, Variant.variant().with(VariantProperties.MODEL, $$1)).select(DoubleBlockHalf.UPPER, Variant.variant().with(VariantProperties.MODEL, $$0))));
    }

    private void createDoubleBlock(Block $$0, ResourceLocation $$1, ResourceLocation $$2) {
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant($$0).with(PropertyDispatch.property(BlockStateProperties.DOUBLE_BLOCK_HALF).select(DoubleBlockHalf.LOWER, Variant.variant().with(VariantProperties.MODEL, $$2)).select(DoubleBlockHalf.UPPER, Variant.variant().with(VariantProperties.MODEL, $$1))));
    }

    private void createPassiveRail(Block $$0) {
        TextureMapping $$1 = TextureMapping.rail($$0);
        TextureMapping $$2 = TextureMapping.rail(TextureMapping.getBlockTexture($$0, "_corner"));
        ResourceLocation $$3 = ModelTemplates.RAIL_FLAT.create($$0, $$1, this.modelOutput);
        ResourceLocation $$4 = ModelTemplates.RAIL_CURVED.create($$0, $$2, this.modelOutput);
        ResourceLocation $$5 = ModelTemplates.RAIL_RAISED_NE.create($$0, $$1, this.modelOutput);
        ResourceLocation $$6 = ModelTemplates.RAIL_RAISED_SW.create($$0, $$1, this.modelOutput);
        this.createSimpleFlatItemModel($$0);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant($$0).with(PropertyDispatch.property(BlockStateProperties.RAIL_SHAPE).select(RailShape.NORTH_SOUTH, Variant.variant().with(VariantProperties.MODEL, $$3)).select(RailShape.EAST_WEST, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(RailShape.ASCENDING_EAST, Variant.variant().with(VariantProperties.MODEL, $$5).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(RailShape.ASCENDING_WEST, Variant.variant().with(VariantProperties.MODEL, $$6).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(RailShape.ASCENDING_NORTH, Variant.variant().with(VariantProperties.MODEL, $$5)).select(RailShape.ASCENDING_SOUTH, Variant.variant().with(VariantProperties.MODEL, $$6)).select(RailShape.SOUTH_EAST, Variant.variant().with(VariantProperties.MODEL, $$4)).select(RailShape.SOUTH_WEST, Variant.variant().with(VariantProperties.MODEL, $$4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(RailShape.NORTH_WEST, Variant.variant().with(VariantProperties.MODEL, $$4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(RailShape.NORTH_EAST, Variant.variant().with(VariantProperties.MODEL, $$4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))));
    }

    private void createActiveRail(Block $$0) {
        ResourceLocation $$1 = this.createSuffixedVariant($$0, "", ModelTemplates.RAIL_FLAT, (Function<ResourceLocation, TextureMapping>)((Function)TextureMapping::rail));
        ResourceLocation $$2 = this.createSuffixedVariant($$0, "", ModelTemplates.RAIL_RAISED_NE, (Function<ResourceLocation, TextureMapping>)((Function)TextureMapping::rail));
        ResourceLocation $$3 = this.createSuffixedVariant($$0, "", ModelTemplates.RAIL_RAISED_SW, (Function<ResourceLocation, TextureMapping>)((Function)TextureMapping::rail));
        ResourceLocation $$4 = this.createSuffixedVariant($$0, "_on", ModelTemplates.RAIL_FLAT, (Function<ResourceLocation, TextureMapping>)((Function)TextureMapping::rail));
        ResourceLocation $$5 = this.createSuffixedVariant($$0, "_on", ModelTemplates.RAIL_RAISED_NE, (Function<ResourceLocation, TextureMapping>)((Function)TextureMapping::rail));
        ResourceLocation $$62 = this.createSuffixedVariant($$0, "_on", ModelTemplates.RAIL_RAISED_SW, (Function<ResourceLocation, TextureMapping>)((Function)TextureMapping::rail));
        PropertyDispatch $$72 = PropertyDispatch.properties(BlockStateProperties.POWERED, BlockStateProperties.RAIL_SHAPE_STRAIGHT).generate((BiFunction<Boolean, RailShape, Variant>)((BiFunction)($$6, $$7) -> {
            switch ($$7) {
                case NORTH_SOUTH: {
                    return Variant.variant().with(VariantProperties.MODEL, $$6 != false ? $$4 : $$1);
                }
                case EAST_WEST: {
                    return Variant.variant().with(VariantProperties.MODEL, $$6 != false ? $$4 : $$1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
                }
                case ASCENDING_EAST: {
                    return Variant.variant().with(VariantProperties.MODEL, $$6 != false ? $$5 : $$2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
                }
                case ASCENDING_WEST: {
                    return Variant.variant().with(VariantProperties.MODEL, $$6 != false ? $$62 : $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
                }
                case ASCENDING_NORTH: {
                    return Variant.variant().with(VariantProperties.MODEL, $$6 != false ? $$5 : $$2);
                }
                case ASCENDING_SOUTH: {
                    return Variant.variant().with(VariantProperties.MODEL, $$6 != false ? $$62 : $$3);
                }
            }
            throw new UnsupportedOperationException("Fix you generator!");
        }));
        this.createSimpleFlatItemModel($$0);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant($$0).with($$72));
    }

    private BlockEntityModelGenerator blockEntityModels(ResourceLocation $$0, Block $$1) {
        return new BlockEntityModelGenerator($$0, $$1);
    }

    private BlockEntityModelGenerator blockEntityModels(Block $$0, Block $$1) {
        return new BlockEntityModelGenerator(ModelLocationUtils.getModelLocation($$0), $$1);
    }

    private void createAirLikeBlock(Block $$0, Item $$1) {
        ResourceLocation $$2 = ModelTemplates.PARTICLE_ONLY.create($$0, TextureMapping.particleFromItem($$1), this.modelOutput);
        this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock($$0, $$2));
    }

    private void createAirLikeBlock(Block $$0, ResourceLocation $$1) {
        ResourceLocation $$2 = ModelTemplates.PARTICLE_ONLY.create($$0, TextureMapping.particle($$1), this.modelOutput);
        this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock($$0, $$2));
    }

    private void createFullAndCarpetBlocks(Block $$0, Block $$1) {
        this.createTrivialCube($$0);
        ResourceLocation $$2 = TexturedModel.CARPET.get($$0).create($$1, this.modelOutput);
        this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock($$1, $$2));
    }

    private void createColoredBlockWithRandomRotations(TexturedModel.Provider $$0, Block ... $$1) {
        for (Block $$2 : $$1) {
            ResourceLocation $$3 = $$0.create($$2, this.modelOutput);
            this.blockStateOutput.accept((Object)BlockModelGenerators.createRotatedVariant($$2, $$3));
        }
    }

    private void createColoredBlockWithStateRotations(TexturedModel.Provider $$0, Block ... $$1) {
        for (Block $$2 : $$1) {
            ResourceLocation $$3 = $$0.create($$2, this.modelOutput);
            this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant($$2, Variant.variant().with(VariantProperties.MODEL, $$3)).with(BlockModelGenerators.createHorizontalFacingDispatchAlt()));
        }
    }

    private void createGlassBlocks(Block $$0, Block $$1) {
        this.createTrivialCube($$0);
        TextureMapping $$2 = TextureMapping.pane($$0, $$1);
        ResourceLocation $$3 = ModelTemplates.STAINED_GLASS_PANE_POST.create($$1, $$2, this.modelOutput);
        ResourceLocation $$4 = ModelTemplates.STAINED_GLASS_PANE_SIDE.create($$1, $$2, this.modelOutput);
        ResourceLocation $$5 = ModelTemplates.STAINED_GLASS_PANE_SIDE_ALT.create($$1, $$2, this.modelOutput);
        ResourceLocation $$6 = ModelTemplates.STAINED_GLASS_PANE_NOSIDE.create($$1, $$2, this.modelOutput);
        ResourceLocation $$7 = ModelTemplates.STAINED_GLASS_PANE_NOSIDE_ALT.create($$1, $$2, this.modelOutput);
        Item $$8 = $$1.asItem();
        ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation($$8), TextureMapping.layer0($$0), this.modelOutput);
        this.blockStateOutput.accept((Object)MultiPartGenerator.multiPart($$1).with(Variant.variant().with(VariantProperties.MODEL, $$3)).with((Condition)Condition.condition().term(BlockStateProperties.NORTH, true), Variant.variant().with(VariantProperties.MODEL, $$4)).with((Condition)Condition.condition().term(BlockStateProperties.EAST, true), Variant.variant().with(VariantProperties.MODEL, $$4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).with((Condition)Condition.condition().term(BlockStateProperties.SOUTH, true), Variant.variant().with(VariantProperties.MODEL, $$5)).with((Condition)Condition.condition().term(BlockStateProperties.WEST, true), Variant.variant().with(VariantProperties.MODEL, $$5).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).with((Condition)Condition.condition().term(BlockStateProperties.NORTH, false), Variant.variant().with(VariantProperties.MODEL, $$6)).with((Condition)Condition.condition().term(BlockStateProperties.EAST, false), Variant.variant().with(VariantProperties.MODEL, $$7)).with((Condition)Condition.condition().term(BlockStateProperties.SOUTH, false), Variant.variant().with(VariantProperties.MODEL, $$7).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).with((Condition)Condition.condition().term(BlockStateProperties.WEST, false), Variant.variant().with(VariantProperties.MODEL, $$6).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)));
    }

    private void createCommandBlock(Block $$0) {
        TextureMapping $$12 = TextureMapping.commandBlock($$0);
        ResourceLocation $$2 = ModelTemplates.COMMAND_BLOCK.create($$0, $$12, this.modelOutput);
        ResourceLocation $$3 = this.createSuffixedVariant($$0, "_conditional", ModelTemplates.COMMAND_BLOCK, (Function<ResourceLocation, TextureMapping>)((Function)$$1 -> $$12.copyAndUpdate(TextureSlot.SIDE, (ResourceLocation)$$1)));
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant($$0).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.CONDITIONAL, $$3, $$2)).with(BlockModelGenerators.createFacingDispatch()));
    }

    private void createAnvil(Block $$0) {
        ResourceLocation $$1 = TexturedModel.ANVIL.create($$0, this.modelOutput);
        this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock($$0, $$1).with(BlockModelGenerators.createHorizontalFacingDispatchAlt()));
    }

    private List<Variant> createBambooModels(int $$0) {
        String $$12 = "_age" + $$0;
        return (List)IntStream.range((int)1, (int)5).mapToObj($$1 -> Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.BAMBOO, $$1 + $$12))).collect(Collectors.toList());
    }

    private void createBamboo() {
        this.skipAutoItemBlock(Blocks.BAMBOO);
        this.blockStateOutput.accept((Object)MultiPartGenerator.multiPart(Blocks.BAMBOO).with((Condition)Condition.condition().term(BlockStateProperties.AGE_1, 0), this.createBambooModels(0)).with((Condition)Condition.condition().term(BlockStateProperties.AGE_1, 1), this.createBambooModels(1)).with((Condition)Condition.condition().term(BlockStateProperties.BAMBOO_LEAVES, BambooLeaves.SMALL), Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.BAMBOO, "_small_leaves"))).with((Condition)Condition.condition().term(BlockStateProperties.BAMBOO_LEAVES, BambooLeaves.LARGE), Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.BAMBOO, "_large_leaves"))));
    }

    private PropertyDispatch createColumnWithFacing() {
        return PropertyDispatch.property(BlockStateProperties.FACING).select(Direction.DOWN, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)).select(Direction.UP, Variant.variant()).select(Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)).select(Direction.SOUTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.WEST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.EAST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90));
    }

    private void createBarrel() {
        ResourceLocation $$0 = TextureMapping.getBlockTexture(Blocks.BARREL, "_top_open");
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.BARREL).with(this.createColumnWithFacing()).with(PropertyDispatch.property(BlockStateProperties.OPEN).select((Boolean)false, Variant.variant().with(VariantProperties.MODEL, TexturedModel.CUBE_TOP_BOTTOM.create(Blocks.BARREL, this.modelOutput))).select((Boolean)true, Variant.variant().with(VariantProperties.MODEL, TexturedModel.CUBE_TOP_BOTTOM.get(Blocks.BARREL).updateTextures((Consumer<TextureMapping>)((Consumer)$$1 -> $$1.put(TextureSlot.TOP, $$0))).createWithSuffix(Blocks.BARREL, "_open", this.modelOutput)))));
    }

    private static <T extends Comparable<T>> PropertyDispatch createEmptyOrFullDispatch(Property<T> $$0, T $$1, ResourceLocation $$2, ResourceLocation $$32) {
        Variant $$4 = Variant.variant().with(VariantProperties.MODEL, $$2);
        Variant $$5 = Variant.variant().with(VariantProperties.MODEL, $$32);
        return PropertyDispatch.property($$0).generate($$3 -> {
            boolean $$4 = $$3.compareTo((Object)$$1) >= 0;
            return $$4 ? $$4 : $$5;
        });
    }

    private void createBeeNest(Block $$0, Function<Block, TextureMapping> $$1) {
        TextureMapping $$2 = ((TextureMapping)$$1.apply((Object)$$0)).copyForced(TextureSlot.SIDE, TextureSlot.PARTICLE);
        TextureMapping $$3 = $$2.copyAndUpdate(TextureSlot.FRONT, TextureMapping.getBlockTexture($$0, "_front_honey"));
        ResourceLocation $$4 = ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM.create($$0, $$2, this.modelOutput);
        ResourceLocation $$5 = ModelTemplates.CUBE_ORIENTABLE_TOP_BOTTOM.createWithSuffix($$0, "_honey", $$3, this.modelOutput);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant($$0).with(BlockModelGenerators.createHorizontalFacingDispatch()).with(BlockModelGenerators.createEmptyOrFullDispatch(BlockStateProperties.LEVEL_HONEY, 5, $$5, $$4)));
    }

    private void createCropBlock(Block $$0, Property<Integer> $$1, int ... $$2) {
        if ($$1.getPossibleValues().size() != $$2.length) {
            throw new IllegalArgumentException();
        }
        Int2ObjectOpenHashMap $$3 = new Int2ObjectOpenHashMap();
        PropertyDispatch $$4 = PropertyDispatch.property($$1).generate((Function<Integer, Variant>)((Function)arg_0 -> this.lambda$createCropBlock$12($$2, (Int2ObjectMap)$$3, $$0, arg_0)));
        this.createSimpleFlatItemModel($$0.asItem());
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant($$0).with($$4));
    }

    private void createBell() {
        ResourceLocation $$0 = ModelLocationUtils.getModelLocation(Blocks.BELL, "_floor");
        ResourceLocation $$1 = ModelLocationUtils.getModelLocation(Blocks.BELL, "_ceiling");
        ResourceLocation $$2 = ModelLocationUtils.getModelLocation(Blocks.BELL, "_wall");
        ResourceLocation $$3 = ModelLocationUtils.getModelLocation(Blocks.BELL, "_between_walls");
        this.createSimpleFlatItemModel(Items.BELL);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.BELL).with(PropertyDispatch.properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.BELL_ATTACHMENT).select(Direction.NORTH, BellAttachType.FLOOR, Variant.variant().with(VariantProperties.MODEL, $$0)).select(Direction.SOUTH, BellAttachType.FLOOR, Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.EAST, BellAttachType.FLOOR, Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.WEST, BellAttachType.FLOOR, Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.NORTH, BellAttachType.CEILING, Variant.variant().with(VariantProperties.MODEL, $$1)).select(Direction.SOUTH, BellAttachType.CEILING, Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.EAST, BellAttachType.CEILING, Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.WEST, BellAttachType.CEILING, Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.NORTH, BellAttachType.SINGLE_WALL, Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.SOUTH, BellAttachType.SINGLE_WALL, Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.EAST, BellAttachType.SINGLE_WALL, Variant.variant().with(VariantProperties.MODEL, $$2)).select(Direction.WEST, BellAttachType.SINGLE_WALL, Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.SOUTH, BellAttachType.DOUBLE_WALL, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.NORTH, BellAttachType.DOUBLE_WALL, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(Direction.EAST, BellAttachType.DOUBLE_WALL, Variant.variant().with(VariantProperties.MODEL, $$3)).select(Direction.WEST, BellAttachType.DOUBLE_WALL, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))));
    }

    private void createGrindstone() {
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.GRINDSTONE, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.GRINDSTONE))).with(PropertyDispatch.properties(BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING).select(AttachFace.FLOOR, Direction.NORTH, Variant.variant()).select(AttachFace.FLOOR, Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(AttachFace.FLOOR, Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(AttachFace.FLOOR, Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(AttachFace.WALL, Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)).select(AttachFace.WALL, Direction.EAST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(AttachFace.WALL, Direction.SOUTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(AttachFace.WALL, Direction.WEST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(AttachFace.CEILING, Direction.SOUTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)).select(AttachFace.CEILING, Direction.WEST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(AttachFace.CEILING, Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(AttachFace.CEILING, Direction.EAST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))));
    }

    private void createFurnace(Block $$0, TexturedModel.Provider $$12) {
        ResourceLocation $$2 = $$12.create($$0, this.modelOutput);
        ResourceLocation $$3 = TextureMapping.getBlockTexture($$0, "_front_on");
        ResourceLocation $$4 = $$12.get($$0).updateTextures((Consumer<TextureMapping>)((Consumer)$$1 -> $$1.put(TextureSlot.FRONT, $$3))).createWithSuffix($$0, "_on", this.modelOutput);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant($$0).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.LIT, $$4, $$2)).with(BlockModelGenerators.createHorizontalFacingDispatch()));
    }

    private void createCampfires(Block ... $$0) {
        ResourceLocation $$1 = ModelLocationUtils.decorateBlockModelLocation("campfire_off");
        for (Block $$2 : $$0) {
            ResourceLocation $$3 = ModelTemplates.CAMPFIRE.create($$2, TextureMapping.campfire($$2), this.modelOutput);
            this.createSimpleFlatItemModel($$2.asItem());
            this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant($$2).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.LIT, $$3, $$1)).with(BlockModelGenerators.createHorizontalFacingDispatchAlt()));
        }
    }

    private void createAzalea(Block $$0) {
        ResourceLocation $$1 = ModelTemplates.AZALEA.create($$0, TextureMapping.cubeTop($$0), this.modelOutput);
        this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock($$0, $$1));
    }

    private void createPottedAzalea(Block $$0) {
        ResourceLocation $$1 = ModelTemplates.POTTED_AZALEA.create($$0, TextureMapping.cubeTop($$0), this.modelOutput);
        this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock($$0, $$1));
    }

    private void createBookshelf() {
        TextureMapping $$0 = TextureMapping.column(TextureMapping.getBlockTexture(Blocks.BOOKSHELF), TextureMapping.getBlockTexture(Blocks.OAK_PLANKS));
        ResourceLocation $$1 = ModelTemplates.CUBE_COLUMN.create(Blocks.BOOKSHELF, $$0, this.modelOutput);
        this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock(Blocks.BOOKSHELF, $$1));
    }

    private void createRedstoneWire() {
        this.createSimpleFlatItemModel(Items.REDSTONE);
        this.blockStateOutput.accept((Object)MultiPartGenerator.multiPart(Blocks.REDSTONE_WIRE).with(Condition.or(Condition.condition().term(BlockStateProperties.NORTH_REDSTONE, RedstoneSide.NONE).term(BlockStateProperties.EAST_REDSTONE, RedstoneSide.NONE).term(BlockStateProperties.SOUTH_REDSTONE, RedstoneSide.NONE).term(BlockStateProperties.WEST_REDSTONE, RedstoneSide.NONE), Condition.condition().term(BlockStateProperties.NORTH_REDSTONE, (Comparable)RedstoneSide.SIDE, (Comparable[])new RedstoneSide[]{RedstoneSide.UP}).term(BlockStateProperties.EAST_REDSTONE, (Comparable)RedstoneSide.SIDE, (Comparable[])new RedstoneSide[]{RedstoneSide.UP}), Condition.condition().term(BlockStateProperties.EAST_REDSTONE, (Comparable)RedstoneSide.SIDE, (Comparable[])new RedstoneSide[]{RedstoneSide.UP}).term(BlockStateProperties.SOUTH_REDSTONE, (Comparable)RedstoneSide.SIDE, (Comparable[])new RedstoneSide[]{RedstoneSide.UP}), Condition.condition().term(BlockStateProperties.SOUTH_REDSTONE, (Comparable)RedstoneSide.SIDE, (Comparable[])new RedstoneSide[]{RedstoneSide.UP}).term(BlockStateProperties.WEST_REDSTONE, (Comparable)RedstoneSide.SIDE, (Comparable[])new RedstoneSide[]{RedstoneSide.UP}), Condition.condition().term(BlockStateProperties.WEST_REDSTONE, (Comparable)RedstoneSide.SIDE, (Comparable[])new RedstoneSide[]{RedstoneSide.UP}).term(BlockStateProperties.NORTH_REDSTONE, (Comparable)RedstoneSide.SIDE, (Comparable[])new RedstoneSide[]{RedstoneSide.UP})), Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.decorateBlockModelLocation("redstone_dust_dot"))).with((Condition)Condition.condition().term(BlockStateProperties.NORTH_REDSTONE, (Comparable)RedstoneSide.SIDE, (Comparable[])new RedstoneSide[]{RedstoneSide.UP}), Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.decorateBlockModelLocation("redstone_dust_side0"))).with((Condition)Condition.condition().term(BlockStateProperties.SOUTH_REDSTONE, (Comparable)RedstoneSide.SIDE, (Comparable[])new RedstoneSide[]{RedstoneSide.UP}), Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.decorateBlockModelLocation("redstone_dust_side_alt0"))).with((Condition)Condition.condition().term(BlockStateProperties.EAST_REDSTONE, (Comparable)RedstoneSide.SIDE, (Comparable[])new RedstoneSide[]{RedstoneSide.UP}), Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.decorateBlockModelLocation("redstone_dust_side_alt1")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).with((Condition)Condition.condition().term(BlockStateProperties.WEST_REDSTONE, (Comparable)RedstoneSide.SIDE, (Comparable[])new RedstoneSide[]{RedstoneSide.UP}), Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.decorateBlockModelLocation("redstone_dust_side1")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).with((Condition)Condition.condition().term(BlockStateProperties.NORTH_REDSTONE, RedstoneSide.UP), Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.decorateBlockModelLocation("redstone_dust_up"))).with((Condition)Condition.condition().term(BlockStateProperties.EAST_REDSTONE, RedstoneSide.UP), Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.decorateBlockModelLocation("redstone_dust_up")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).with((Condition)Condition.condition().term(BlockStateProperties.SOUTH_REDSTONE, RedstoneSide.UP), Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.decorateBlockModelLocation("redstone_dust_up")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).with((Condition)Condition.condition().term(BlockStateProperties.WEST_REDSTONE, RedstoneSide.UP), Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.decorateBlockModelLocation("redstone_dust_up")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)));
    }

    private void createComparator() {
        this.createSimpleFlatItemModel(Items.COMPARATOR);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.COMPARATOR).with(BlockModelGenerators.createHorizontalFacingDispatchAlt()).with(PropertyDispatch.properties(BlockStateProperties.MODE_COMPARATOR, BlockStateProperties.POWERED).select(ComparatorMode.COMPARE, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.COMPARATOR))).select(ComparatorMode.COMPARE, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.COMPARATOR, "_on"))).select(ComparatorMode.SUBTRACT, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.COMPARATOR, "_subtract"))).select(ComparatorMode.SUBTRACT, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.COMPARATOR, "_on_subtract")))));
    }

    private void createSmoothStoneSlab() {
        TextureMapping $$0 = TextureMapping.cube(Blocks.SMOOTH_STONE);
        TextureMapping $$1 = TextureMapping.column(TextureMapping.getBlockTexture(Blocks.SMOOTH_STONE_SLAB, "_side"), $$0.get(TextureSlot.TOP));
        ResourceLocation $$2 = ModelTemplates.SLAB_BOTTOM.create(Blocks.SMOOTH_STONE_SLAB, $$1, this.modelOutput);
        ResourceLocation $$3 = ModelTemplates.SLAB_TOP.create(Blocks.SMOOTH_STONE_SLAB, $$1, this.modelOutput);
        ResourceLocation $$4 = ModelTemplates.CUBE_COLUMN.createWithOverride(Blocks.SMOOTH_STONE_SLAB, "_double", $$1, this.modelOutput);
        this.blockStateOutput.accept((Object)BlockModelGenerators.createSlab(Blocks.SMOOTH_STONE_SLAB, $$2, $$3, $$4));
        this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock(Blocks.SMOOTH_STONE, ModelTemplates.CUBE_ALL.create(Blocks.SMOOTH_STONE, $$0, this.modelOutput)));
    }

    private void createBrewingStand() {
        this.createSimpleFlatItemModel(Items.BREWING_STAND);
        this.blockStateOutput.accept((Object)MultiPartGenerator.multiPart(Blocks.BREWING_STAND).with(Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.BREWING_STAND))).with((Condition)Condition.condition().term(BlockStateProperties.HAS_BOTTLE_0, true), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.BREWING_STAND, "_bottle0"))).with((Condition)Condition.condition().term(BlockStateProperties.HAS_BOTTLE_1, true), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.BREWING_STAND, "_bottle1"))).with((Condition)Condition.condition().term(BlockStateProperties.HAS_BOTTLE_2, true), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.BREWING_STAND, "_bottle2"))).with((Condition)Condition.condition().term(BlockStateProperties.HAS_BOTTLE_0, false), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.BREWING_STAND, "_empty0"))).with((Condition)Condition.condition().term(BlockStateProperties.HAS_BOTTLE_1, false), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.BREWING_STAND, "_empty1"))).with((Condition)Condition.condition().term(BlockStateProperties.HAS_BOTTLE_2, false), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.BREWING_STAND, "_empty2"))));
    }

    private void createMushroomBlock(Block $$0) {
        ResourceLocation $$1 = ModelTemplates.SINGLE_FACE.create($$0, TextureMapping.defaultTexture($$0), this.modelOutput);
        ResourceLocation $$2 = ModelLocationUtils.decorateBlockModelLocation("mushroom_block_inside");
        this.blockStateOutput.accept((Object)MultiPartGenerator.multiPart($$0).with((Condition)Condition.condition().term(BlockStateProperties.NORTH, true), Variant.variant().with(VariantProperties.MODEL, $$1)).with((Condition)Condition.condition().term(BlockStateProperties.EAST, true), Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).with((Condition)Condition.condition().term(BlockStateProperties.SOUTH, true), Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).with((Condition)Condition.condition().term(BlockStateProperties.WEST, true), Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).with((Condition)Condition.condition().term(BlockStateProperties.UP, true), Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).with((Condition)Condition.condition().term(BlockStateProperties.DOWN, true), Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).with((Condition)Condition.condition().term(BlockStateProperties.NORTH, false), Variant.variant().with(VariantProperties.MODEL, $$2)).with((Condition)Condition.condition().term(BlockStateProperties.EAST, false), Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, false)).with((Condition)Condition.condition().term(BlockStateProperties.SOUTH, false), Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, false)).with((Condition)Condition.condition().term(BlockStateProperties.WEST, false), Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, false)).with((Condition)Condition.condition().term(BlockStateProperties.UP, false), Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, false)).with((Condition)Condition.condition().term(BlockStateProperties.DOWN, false), Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, false)));
        this.delegateItemModel($$0, TexturedModel.CUBE.createWithSuffix($$0, "_inventory", this.modelOutput));
    }

    private void createCakeBlock() {
        this.createSimpleFlatItemModel(Items.CAKE);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.CAKE).with(PropertyDispatch.property(BlockStateProperties.BITES).select((Integer)0, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.CAKE))).select((Integer)1, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.CAKE, "_slice1"))).select((Integer)2, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.CAKE, "_slice2"))).select((Integer)3, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.CAKE, "_slice3"))).select((Integer)4, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.CAKE, "_slice4"))).select((Integer)5, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.CAKE, "_slice5"))).select((Integer)6, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.CAKE, "_slice6")))));
    }

    private void createCartographyTable() {
        TextureMapping $$0 = new TextureMapping().put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_side3")).put(TextureSlot.DOWN, TextureMapping.getBlockTexture(Blocks.DARK_OAK_PLANKS)).put(TextureSlot.UP, TextureMapping.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_top")).put(TextureSlot.NORTH, TextureMapping.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_side3")).put(TextureSlot.EAST, TextureMapping.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_side3")).put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_side1")).put(TextureSlot.WEST, TextureMapping.getBlockTexture(Blocks.CARTOGRAPHY_TABLE, "_side2"));
        this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock(Blocks.CARTOGRAPHY_TABLE, ModelTemplates.CUBE.create(Blocks.CARTOGRAPHY_TABLE, $$0, this.modelOutput)));
    }

    private void createSmithingTable() {
        TextureMapping $$0 = new TextureMapping().put(TextureSlot.PARTICLE, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_front")).put(TextureSlot.DOWN, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_bottom")).put(TextureSlot.UP, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_top")).put(TextureSlot.NORTH, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_front")).put(TextureSlot.SOUTH, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_front")).put(TextureSlot.EAST, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_side")).put(TextureSlot.WEST, TextureMapping.getBlockTexture(Blocks.SMITHING_TABLE, "_side"));
        this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock(Blocks.SMITHING_TABLE, ModelTemplates.CUBE.create(Blocks.SMITHING_TABLE, $$0, this.modelOutput)));
    }

    private void createCraftingTableLike(Block $$0, Block $$1, BiFunction<Block, Block, TextureMapping> $$2) {
        TextureMapping $$3 = (TextureMapping)$$2.apply((Object)$$0, (Object)$$1);
        this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock($$0, ModelTemplates.CUBE.create($$0, $$3, this.modelOutput)));
    }

    private void createPumpkins() {
        TextureMapping $$0 = TextureMapping.column(Blocks.PUMPKIN);
        this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock(Blocks.PUMPKIN, ModelLocationUtils.getModelLocation(Blocks.PUMPKIN)));
        this.createPumpkinVariant(Blocks.CARVED_PUMPKIN, $$0);
        this.createPumpkinVariant(Blocks.JACK_O_LANTERN, $$0);
    }

    private void createPumpkinVariant(Block $$0, TextureMapping $$1) {
        ResourceLocation $$2 = ModelTemplates.CUBE_ORIENTABLE.create($$0, $$1.copyAndUpdate(TextureSlot.FRONT, TextureMapping.getBlockTexture($$0)), this.modelOutput);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant($$0, Variant.variant().with(VariantProperties.MODEL, $$2)).with(BlockModelGenerators.createHorizontalFacingDispatch()));
    }

    private void createCauldrons() {
        this.createSimpleFlatItemModel(Items.CAULDRON);
        this.createNonTemplateModelBlock(Blocks.CAULDRON);
        this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock(Blocks.LAVA_CAULDRON, ModelTemplates.CAULDRON_FULL.create(Blocks.LAVA_CAULDRON, TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.LAVA, "_still")), this.modelOutput)));
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.WATER_CAULDRON).with(PropertyDispatch.property(LayeredCauldronBlock.LEVEL).select((Integer)1, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.CAULDRON_LEVEL1.createWithSuffix(Blocks.WATER_CAULDRON, "_level1", TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.WATER, "_still")), this.modelOutput))).select((Integer)2, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.CAULDRON_LEVEL2.createWithSuffix(Blocks.WATER_CAULDRON, "_level2", TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.WATER, "_still")), this.modelOutput))).select((Integer)3, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.CAULDRON_FULL.createWithSuffix(Blocks.WATER_CAULDRON, "_full", TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.WATER, "_still")), this.modelOutput)))));
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.POWDER_SNOW_CAULDRON).with(PropertyDispatch.property(LayeredCauldronBlock.LEVEL).select((Integer)1, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.CAULDRON_LEVEL1.createWithSuffix(Blocks.POWDER_SNOW_CAULDRON, "_level1", TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.POWDER_SNOW)), this.modelOutput))).select((Integer)2, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.CAULDRON_LEVEL2.createWithSuffix(Blocks.POWDER_SNOW_CAULDRON, "_level2", TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.POWDER_SNOW)), this.modelOutput))).select((Integer)3, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.CAULDRON_FULL.createWithSuffix(Blocks.POWDER_SNOW_CAULDRON, "_full", TextureMapping.cauldron(TextureMapping.getBlockTexture(Blocks.POWDER_SNOW)), this.modelOutput)))));
    }

    private void createChorusFlower() {
        TextureMapping $$0 = TextureMapping.defaultTexture(Blocks.CHORUS_FLOWER);
        ResourceLocation $$12 = ModelTemplates.CHORUS_FLOWER.create(Blocks.CHORUS_FLOWER, $$0, this.modelOutput);
        ResourceLocation $$2 = this.createSuffixedVariant(Blocks.CHORUS_FLOWER, "_dead", ModelTemplates.CHORUS_FLOWER, (Function<ResourceLocation, TextureMapping>)((Function)$$1 -> $$0.copyAndUpdate(TextureSlot.TEXTURE, (ResourceLocation)$$1)));
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.CHORUS_FLOWER).with(BlockModelGenerators.createEmptyOrFullDispatch(BlockStateProperties.AGE_5, 5, $$2, $$12)));
    }

    private void createDispenserBlock(Block $$0) {
        TextureMapping $$1 = new TextureMapping().put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.FURNACE, "_top")).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.FURNACE, "_side")).put(TextureSlot.FRONT, TextureMapping.getBlockTexture($$0, "_front"));
        TextureMapping $$2 = new TextureMapping().put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.FURNACE, "_top")).put(TextureSlot.FRONT, TextureMapping.getBlockTexture($$0, "_front_vertical"));
        ResourceLocation $$3 = ModelTemplates.CUBE_ORIENTABLE.create($$0, $$1, this.modelOutput);
        ResourceLocation $$4 = ModelTemplates.CUBE_ORIENTABLE_VERTICAL.create($$0, $$2, this.modelOutput);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant($$0).with(PropertyDispatch.property(BlockStateProperties.FACING).select(Direction.DOWN, Variant.variant().with(VariantProperties.MODEL, $$4).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)).select(Direction.UP, Variant.variant().with(VariantProperties.MODEL, $$4)).select(Direction.NORTH, Variant.variant().with(VariantProperties.MODEL, $$3)).select(Direction.EAST, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.SOUTH, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.WEST, Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))));
    }

    private void createEndPortalFrame() {
        ResourceLocation $$0 = ModelLocationUtils.getModelLocation(Blocks.END_PORTAL_FRAME);
        ResourceLocation $$1 = ModelLocationUtils.getModelLocation(Blocks.END_PORTAL_FRAME, "_filled");
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.END_PORTAL_FRAME).with(PropertyDispatch.property(BlockStateProperties.EYE).select((Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$0)).select((Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$1))).with(BlockModelGenerators.createHorizontalFacingDispatchAlt()));
    }

    private void createChorusPlant() {
        ResourceLocation $$0 = ModelLocationUtils.getModelLocation(Blocks.CHORUS_PLANT, "_side");
        ResourceLocation $$1 = ModelLocationUtils.getModelLocation(Blocks.CHORUS_PLANT, "_noside");
        ResourceLocation $$2 = ModelLocationUtils.getModelLocation(Blocks.CHORUS_PLANT, "_noside1");
        ResourceLocation $$3 = ModelLocationUtils.getModelLocation(Blocks.CHORUS_PLANT, "_noside2");
        ResourceLocation $$4 = ModelLocationUtils.getModelLocation(Blocks.CHORUS_PLANT, "_noside3");
        this.blockStateOutput.accept((Object)MultiPartGenerator.multiPart(Blocks.CHORUS_PLANT).with((Condition)Condition.condition().term(BlockStateProperties.NORTH, true), Variant.variant().with(VariantProperties.MODEL, $$0)).with((Condition)Condition.condition().term(BlockStateProperties.EAST, true), Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).with((Condition)Condition.condition().term(BlockStateProperties.SOUTH, true), Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).with((Condition)Condition.condition().term(BlockStateProperties.WEST, true), Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).with((Condition)Condition.condition().term(BlockStateProperties.UP, true), Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).with((Condition)Condition.condition().term(BlockStateProperties.DOWN, true), Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).with((Condition)Condition.condition().term(BlockStateProperties.NORTH, false), Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.WEIGHT, 2), Variant.variant().with(VariantProperties.MODEL, $$2), Variant.variant().with(VariantProperties.MODEL, $$3), Variant.variant().with(VariantProperties.MODEL, $$4)).with((Condition)Condition.condition().term(BlockStateProperties.EAST, false), Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true), Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true), Variant.variant().with(VariantProperties.MODEL, $$4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true), Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.WEIGHT, 2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)).with((Condition)Condition.condition().term(BlockStateProperties.SOUTH, false), Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true), Variant.variant().with(VariantProperties.MODEL, $$4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true), Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.WEIGHT, 2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true), Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.UV_LOCK, true)).with((Condition)Condition.condition().term(BlockStateProperties.WEST, false), Variant.variant().with(VariantProperties.MODEL, $$4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true), Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.WEIGHT, 2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true), Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true), Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).with((Condition)Condition.condition().term(BlockStateProperties.UP, false), Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.WEIGHT, 2).with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true), Variant.variant().with(VariantProperties.MODEL, $$4).with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true), Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true), Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.UV_LOCK, true)).with((Condition)Condition.condition().term(BlockStateProperties.DOWN, false), Variant.variant().with(VariantProperties.MODEL, $$4).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true), Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true), Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true), Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.WEIGHT, 2).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.UV_LOCK, true)));
    }

    private void createComposter() {
        this.blockStateOutput.accept((Object)MultiPartGenerator.multiPart(Blocks.COMPOSTER).with(Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER))).with((Condition)Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 1), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents1"))).with((Condition)Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 2), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents2"))).with((Condition)Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 3), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents3"))).with((Condition)Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 4), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents4"))).with((Condition)Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 5), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents5"))).with((Condition)Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 6), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents6"))).with((Condition)Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 7), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents7"))).with((Condition)Condition.condition().term(BlockStateProperties.LEVEL_COMPOSTER, 8), Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.COMPOSTER, "_contents_ready"))));
    }

    private void createAmethystCluster(Block $$0) {
        this.skipAutoItemBlock($$0);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant($$0, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.CROSS.create($$0, TextureMapping.cross($$0), this.modelOutput))).with(this.createColumnWithFacing()));
    }

    private void createAmethystClusters() {
        this.createAmethystCluster(Blocks.SMALL_AMETHYST_BUD);
        this.createAmethystCluster(Blocks.MEDIUM_AMETHYST_BUD);
        this.createAmethystCluster(Blocks.LARGE_AMETHYST_BUD);
        this.createAmethystCluster(Blocks.AMETHYST_CLUSTER);
    }

    private void createPointedDripstone() {
        this.skipAutoItemBlock(Blocks.POINTED_DRIPSTONE);
        PropertyDispatch.C2<Direction, DripstoneThickness> $$0 = PropertyDispatch.properties(BlockStateProperties.VERTICAL_DIRECTION, BlockStateProperties.DRIPSTONE_THICKNESS);
        for (DripstoneThickness $$1 : DripstoneThickness.values()) {
            $$0.select(Direction.UP, $$1, this.createPointedDripstoneVariant(Direction.UP, $$1));
        }
        for (DripstoneThickness $$2 : DripstoneThickness.values()) {
            $$0.select(Direction.DOWN, $$2, this.createPointedDripstoneVariant(Direction.DOWN, $$2));
        }
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.POINTED_DRIPSTONE).with($$0));
    }

    private Variant createPointedDripstoneVariant(Direction $$0, DripstoneThickness $$1) {
        String $$2 = "_" + $$0.getSerializedName() + "_" + $$1.getSerializedName();
        TextureMapping $$3 = TextureMapping.cross(TextureMapping.getBlockTexture(Blocks.POINTED_DRIPSTONE, $$2));
        return Variant.variant().with(VariantProperties.MODEL, ModelTemplates.POINTED_DRIPSTONE.createWithSuffix(Blocks.POINTED_DRIPSTONE, $$2, $$3, this.modelOutput));
    }

    private void createNyliumBlock(Block $$0) {
        TextureMapping $$1 = new TextureMapping().put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(Blocks.NETHERRACK)).put(TextureSlot.TOP, TextureMapping.getBlockTexture($$0)).put(TextureSlot.SIDE, TextureMapping.getBlockTexture($$0, "_side"));
        this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock($$0, ModelTemplates.CUBE_BOTTOM_TOP.create($$0, $$1, this.modelOutput)));
    }

    private void createDaylightDetector() {
        ResourceLocation $$0 = TextureMapping.getBlockTexture(Blocks.DAYLIGHT_DETECTOR, "_side");
        TextureMapping $$1 = new TextureMapping().put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.DAYLIGHT_DETECTOR, "_top")).put(TextureSlot.SIDE, $$0);
        TextureMapping $$2 = new TextureMapping().put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.DAYLIGHT_DETECTOR, "_inverted_top")).put(TextureSlot.SIDE, $$0);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.DAYLIGHT_DETECTOR).with(PropertyDispatch.property(BlockStateProperties.INVERTED).select((Boolean)false, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.DAYLIGHT_DETECTOR.create(Blocks.DAYLIGHT_DETECTOR, $$1, this.modelOutput))).select((Boolean)true, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.DAYLIGHT_DETECTOR.create(ModelLocationUtils.getModelLocation(Blocks.DAYLIGHT_DETECTOR, "_inverted"), $$2, this.modelOutput)))));
    }

    private void createRotatableColumn(Block $$0) {
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant($$0, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation($$0))).with(this.createColumnWithFacing()));
    }

    private void createLightningRod() {
        Block $$0 = Blocks.LIGHTNING_ROD;
        ResourceLocation $$1 = ModelLocationUtils.getModelLocation($$0, "_on");
        ResourceLocation $$2 = ModelLocationUtils.getModelLocation($$0);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant($$0, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation($$0))).with(this.createColumnWithFacing()).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.POWERED, $$1, $$2)));
    }

    private void createFarmland() {
        TextureMapping $$0 = new TextureMapping().put(TextureSlot.DIRT, TextureMapping.getBlockTexture(Blocks.DIRT)).put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.FARMLAND));
        TextureMapping $$1 = new TextureMapping().put(TextureSlot.DIRT, TextureMapping.getBlockTexture(Blocks.DIRT)).put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.FARMLAND, "_moist"));
        ResourceLocation $$2 = ModelTemplates.FARMLAND.create(Blocks.FARMLAND, $$0, this.modelOutput);
        ResourceLocation $$3 = ModelTemplates.FARMLAND.create(TextureMapping.getBlockTexture(Blocks.FARMLAND, "_moist"), $$1, this.modelOutput);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.FARMLAND).with(BlockModelGenerators.createEmptyOrFullDispatch(BlockStateProperties.MOISTURE, 7, $$3, $$2)));
    }

    private List<ResourceLocation> createFloorFireModels(Block $$0) {
        ResourceLocation $$1 = ModelTemplates.FIRE_FLOOR.create(ModelLocationUtils.getModelLocation($$0, "_floor0"), TextureMapping.fire0($$0), this.modelOutput);
        ResourceLocation $$2 = ModelTemplates.FIRE_FLOOR.create(ModelLocationUtils.getModelLocation($$0, "_floor1"), TextureMapping.fire1($$0), this.modelOutput);
        return ImmutableList.of((Object)$$1, (Object)$$2);
    }

    private List<ResourceLocation> createSideFireModels(Block $$0) {
        ResourceLocation $$1 = ModelTemplates.FIRE_SIDE.create(ModelLocationUtils.getModelLocation($$0, "_side0"), TextureMapping.fire0($$0), this.modelOutput);
        ResourceLocation $$2 = ModelTemplates.FIRE_SIDE.create(ModelLocationUtils.getModelLocation($$0, "_side1"), TextureMapping.fire1($$0), this.modelOutput);
        ResourceLocation $$3 = ModelTemplates.FIRE_SIDE_ALT.create(ModelLocationUtils.getModelLocation($$0, "_side_alt0"), TextureMapping.fire0($$0), this.modelOutput);
        ResourceLocation $$4 = ModelTemplates.FIRE_SIDE_ALT.create(ModelLocationUtils.getModelLocation($$0, "_side_alt1"), TextureMapping.fire1($$0), this.modelOutput);
        return ImmutableList.of((Object)$$1, (Object)$$2, (Object)$$3, (Object)$$4);
    }

    private List<ResourceLocation> createTopFireModels(Block $$0) {
        ResourceLocation $$1 = ModelTemplates.FIRE_UP.create(ModelLocationUtils.getModelLocation($$0, "_up0"), TextureMapping.fire0($$0), this.modelOutput);
        ResourceLocation $$2 = ModelTemplates.FIRE_UP.create(ModelLocationUtils.getModelLocation($$0, "_up1"), TextureMapping.fire1($$0), this.modelOutput);
        ResourceLocation $$3 = ModelTemplates.FIRE_UP_ALT.create(ModelLocationUtils.getModelLocation($$0, "_up_alt0"), TextureMapping.fire0($$0), this.modelOutput);
        ResourceLocation $$4 = ModelTemplates.FIRE_UP_ALT.create(ModelLocationUtils.getModelLocation($$0, "_up_alt1"), TextureMapping.fire1($$0), this.modelOutput);
        return ImmutableList.of((Object)$$1, (Object)$$2, (Object)$$3, (Object)$$4);
    }

    private static List<Variant> wrapModels(List<ResourceLocation> $$02, UnaryOperator<Variant> $$1) {
        return (List)$$02.stream().map($$0 -> Variant.variant().with(VariantProperties.MODEL, $$0)).map($$1).collect(Collectors.toList());
    }

    private void createFire() {
        Condition.TerminalCondition $$02 = Condition.condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, false).term(BlockStateProperties.UP, false);
        List<ResourceLocation> $$1 = this.createFloorFireModels(Blocks.FIRE);
        List<ResourceLocation> $$2 = this.createSideFireModels(Blocks.FIRE);
        List<ResourceLocation> $$3 = this.createTopFireModels(Blocks.FIRE);
        this.blockStateOutput.accept((Object)MultiPartGenerator.multiPart(Blocks.FIRE).with((Condition)$$02, BlockModelGenerators.wrapModels($$1, (UnaryOperator<Variant>)((UnaryOperator)$$0 -> $$0))).with(Condition.or(Condition.condition().term(BlockStateProperties.NORTH, true), $$02), BlockModelGenerators.wrapModels($$2, (UnaryOperator<Variant>)((UnaryOperator)$$0 -> $$0))).with(Condition.or(Condition.condition().term(BlockStateProperties.EAST, true), $$02), BlockModelGenerators.wrapModels($$2, (UnaryOperator<Variant>)((UnaryOperator)$$0 -> $$0.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)))).with(Condition.or(Condition.condition().term(BlockStateProperties.SOUTH, true), $$02), BlockModelGenerators.wrapModels($$2, (UnaryOperator<Variant>)((UnaryOperator)$$0 -> $$0.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)))).with(Condition.or(Condition.condition().term(BlockStateProperties.WEST, true), $$02), BlockModelGenerators.wrapModels($$2, (UnaryOperator<Variant>)((UnaryOperator)$$0 -> $$0.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)))).with((Condition)Condition.condition().term(BlockStateProperties.UP, true), BlockModelGenerators.wrapModels($$3, (UnaryOperator<Variant>)((UnaryOperator)$$0 -> $$0))));
    }

    private void createSoulFire() {
        List<ResourceLocation> $$02 = this.createFloorFireModels(Blocks.SOUL_FIRE);
        List<ResourceLocation> $$1 = this.createSideFireModels(Blocks.SOUL_FIRE);
        this.blockStateOutput.accept((Object)MultiPartGenerator.multiPart(Blocks.SOUL_FIRE).with(BlockModelGenerators.wrapModels($$02, (UnaryOperator<Variant>)((UnaryOperator)$$0 -> $$0))).with(BlockModelGenerators.wrapModels($$1, (UnaryOperator<Variant>)((UnaryOperator)$$0 -> $$0))).with(BlockModelGenerators.wrapModels($$1, (UnaryOperator<Variant>)((UnaryOperator)$$0 -> $$0.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)))).with(BlockModelGenerators.wrapModels($$1, (UnaryOperator<Variant>)((UnaryOperator)$$0 -> $$0.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)))).with(BlockModelGenerators.wrapModels($$1, (UnaryOperator<Variant>)((UnaryOperator)$$0 -> $$0.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)))));
    }

    private void createLantern(Block $$0) {
        ResourceLocation $$1 = TexturedModel.LANTERN.create($$0, this.modelOutput);
        ResourceLocation $$2 = TexturedModel.HANGING_LANTERN.create($$0, this.modelOutput);
        this.createSimpleFlatItemModel($$0.asItem());
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant($$0).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.HANGING, $$2, $$1)));
    }

    private void createMuddyMangroveRoots() {
        TextureMapping $$0 = TextureMapping.column(TextureMapping.getBlockTexture(Blocks.MUDDY_MANGROVE_ROOTS, "_side"), TextureMapping.getBlockTexture(Blocks.MUDDY_MANGROVE_ROOTS, "_top"));
        ResourceLocation $$1 = ModelTemplates.CUBE_COLUMN.create(Blocks.MUDDY_MANGROVE_ROOTS, $$0, this.modelOutput);
        this.blockStateOutput.accept((Object)BlockModelGenerators.createAxisAlignedPillarBlock(Blocks.MUDDY_MANGROVE_ROOTS, $$1));
    }

    private void createMangrovePropagule() {
        this.createSimpleFlatItemModel(Items.MANGROVE_PROPAGULE);
        Block $$0 = Blocks.MANGROVE_PROPAGULE;
        PropertyDispatch.C2<Boolean, Integer> $$1 = PropertyDispatch.properties(MangrovePropaguleBlock.HANGING, MangrovePropaguleBlock.AGE);
        ResourceLocation $$2 = ModelLocationUtils.getModelLocation($$0);
        for (int $$3 = 0; $$3 <= 4; ++$$3) {
            ResourceLocation $$4 = ModelLocationUtils.getModelLocation($$0, "_hanging_" + $$3);
            $$1.select((Boolean)true, (Integer)$$3, Variant.variant().with(VariantProperties.MODEL, $$4));
            $$1.select((Boolean)false, (Integer)$$3, Variant.variant().with(VariantProperties.MODEL, $$2));
        }
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.MANGROVE_PROPAGULE).with($$1));
    }

    private void createFrostedIce() {
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.FROSTED_ICE).with(PropertyDispatch.property(BlockStateProperties.AGE_3).select((Integer)0, Variant.variant().with(VariantProperties.MODEL, this.createSuffixedVariant(Blocks.FROSTED_ICE, "_0", ModelTemplates.CUBE_ALL, (Function<ResourceLocation, TextureMapping>)((Function)TextureMapping::cube)))).select((Integer)1, Variant.variant().with(VariantProperties.MODEL, this.createSuffixedVariant(Blocks.FROSTED_ICE, "_1", ModelTemplates.CUBE_ALL, (Function<ResourceLocation, TextureMapping>)((Function)TextureMapping::cube)))).select((Integer)2, Variant.variant().with(VariantProperties.MODEL, this.createSuffixedVariant(Blocks.FROSTED_ICE, "_2", ModelTemplates.CUBE_ALL, (Function<ResourceLocation, TextureMapping>)((Function)TextureMapping::cube)))).select((Integer)3, Variant.variant().with(VariantProperties.MODEL, this.createSuffixedVariant(Blocks.FROSTED_ICE, "_3", ModelTemplates.CUBE_ALL, (Function<ResourceLocation, TextureMapping>)((Function)TextureMapping::cube))))));
    }

    private void createGrassBlocks() {
        ResourceLocation $$0 = TextureMapping.getBlockTexture(Blocks.DIRT);
        TextureMapping $$12 = new TextureMapping().put(TextureSlot.BOTTOM, $$0).copyForced(TextureSlot.BOTTOM, TextureSlot.PARTICLE).put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.GRASS_BLOCK, "_top")).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.GRASS_BLOCK, "_snow"));
        Variant $$2 = Variant.variant().with(VariantProperties.MODEL, ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(Blocks.GRASS_BLOCK, "_snow", $$12, this.modelOutput));
        this.createGrassLikeBlock(Blocks.GRASS_BLOCK, ModelLocationUtils.getModelLocation(Blocks.GRASS_BLOCK), $$2);
        ResourceLocation $$3 = TexturedModel.CUBE_TOP_BOTTOM.get(Blocks.MYCELIUM).updateTextures((Consumer<TextureMapping>)((Consumer)$$1 -> $$1.put(TextureSlot.BOTTOM, $$0))).create(Blocks.MYCELIUM, this.modelOutput);
        this.createGrassLikeBlock(Blocks.MYCELIUM, $$3, $$2);
        ResourceLocation $$4 = TexturedModel.CUBE_TOP_BOTTOM.get(Blocks.PODZOL).updateTextures((Consumer<TextureMapping>)((Consumer)$$1 -> $$1.put(TextureSlot.BOTTOM, $$0))).create(Blocks.PODZOL, this.modelOutput);
        this.createGrassLikeBlock(Blocks.PODZOL, $$4, $$2);
    }

    private void createGrassLikeBlock(Block $$0, ResourceLocation $$1, Variant $$2) {
        List $$3 = Arrays.asList((Object[])BlockModelGenerators.createRotatedVariants($$1));
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant($$0).with(PropertyDispatch.property(BlockStateProperties.SNOWY).select((Boolean)true, $$2).select((Boolean)false, (List<Variant>)$$3)));
    }

    private void createCocoa() {
        this.createSimpleFlatItemModel(Items.COCOA_BEANS);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.COCOA).with(PropertyDispatch.property(BlockStateProperties.AGE_2).select((Integer)0, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.COCOA, "_stage0"))).select((Integer)1, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.COCOA, "_stage1"))).select((Integer)2, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.COCOA, "_stage2")))).with(BlockModelGenerators.createHorizontalFacingDispatchAlt()));
    }

    private void createDirtPath() {
        this.blockStateOutput.accept((Object)BlockModelGenerators.createRotatedVariant(Blocks.DIRT_PATH, ModelLocationUtils.getModelLocation(Blocks.DIRT_PATH)));
    }

    private void createWeightedPressurePlate(Block $$0, Block $$1) {
        TextureMapping $$2 = TextureMapping.defaultTexture($$1);
        ResourceLocation $$3 = ModelTemplates.PRESSURE_PLATE_UP.create($$0, $$2, this.modelOutput);
        ResourceLocation $$4 = ModelTemplates.PRESSURE_PLATE_DOWN.create($$0, $$2, this.modelOutput);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant($$0).with(BlockModelGenerators.createEmptyOrFullDispatch(BlockStateProperties.POWER, 1, $$4, $$3)));
    }

    private void createHopper() {
        ResourceLocation $$0 = ModelLocationUtils.getModelLocation(Blocks.HOPPER);
        ResourceLocation $$1 = ModelLocationUtils.getModelLocation(Blocks.HOPPER, "_side");
        this.createSimpleFlatItemModel(Items.HOPPER);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.HOPPER).with(PropertyDispatch.property(BlockStateProperties.FACING_HOPPER).select(Direction.DOWN, Variant.variant().with(VariantProperties.MODEL, $$0)).select(Direction.NORTH, Variant.variant().with(VariantProperties.MODEL, $$1)).select(Direction.EAST, Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(Direction.SOUTH, Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(Direction.WEST, Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))));
    }

    private void copyModel(Block $$0, Block $$1) {
        ResourceLocation $$2 = ModelLocationUtils.getModelLocation($$0);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant($$1, Variant.variant().with(VariantProperties.MODEL, $$2)));
        this.delegateItemModel($$1, $$2);
    }

    private void createIronBars() {
        ResourceLocation $$0 = ModelLocationUtils.getModelLocation(Blocks.IRON_BARS, "_post_ends");
        ResourceLocation $$1 = ModelLocationUtils.getModelLocation(Blocks.IRON_BARS, "_post");
        ResourceLocation $$2 = ModelLocationUtils.getModelLocation(Blocks.IRON_BARS, "_cap");
        ResourceLocation $$3 = ModelLocationUtils.getModelLocation(Blocks.IRON_BARS, "_cap_alt");
        ResourceLocation $$4 = ModelLocationUtils.getModelLocation(Blocks.IRON_BARS, "_side");
        ResourceLocation $$5 = ModelLocationUtils.getModelLocation(Blocks.IRON_BARS, "_side_alt");
        this.blockStateOutput.accept((Object)MultiPartGenerator.multiPart(Blocks.IRON_BARS).with(Variant.variant().with(VariantProperties.MODEL, $$0)).with((Condition)Condition.condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, false), Variant.variant().with(VariantProperties.MODEL, $$1)).with((Condition)Condition.condition().term(BlockStateProperties.NORTH, true).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, false), Variant.variant().with(VariantProperties.MODEL, $$2)).with((Condition)Condition.condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, true).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, false), Variant.variant().with(VariantProperties.MODEL, $$2).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).with((Condition)Condition.condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, true).term(BlockStateProperties.WEST, false), Variant.variant().with(VariantProperties.MODEL, $$3)).with((Condition)Condition.condition().term(BlockStateProperties.NORTH, false).term(BlockStateProperties.EAST, false).term(BlockStateProperties.SOUTH, false).term(BlockStateProperties.WEST, true), Variant.variant().with(VariantProperties.MODEL, $$3).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).with((Condition)Condition.condition().term(BlockStateProperties.NORTH, true), Variant.variant().with(VariantProperties.MODEL, $$4)).with((Condition)Condition.condition().term(BlockStateProperties.EAST, true), Variant.variant().with(VariantProperties.MODEL, $$4).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).with((Condition)Condition.condition().term(BlockStateProperties.SOUTH, true), Variant.variant().with(VariantProperties.MODEL, $$5)).with((Condition)Condition.condition().term(BlockStateProperties.WEST, true), Variant.variant().with(VariantProperties.MODEL, $$5).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)));
        this.createSimpleFlatItemModel(Blocks.IRON_BARS);
    }

    private void createNonTemplateHorizontalBlock(Block $$0) {
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant($$0, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation($$0))).with(BlockModelGenerators.createHorizontalFacingDispatch()));
    }

    private void createLever() {
        ResourceLocation $$0 = ModelLocationUtils.getModelLocation(Blocks.LEVER);
        ResourceLocation $$1 = ModelLocationUtils.getModelLocation(Blocks.LEVER, "_on");
        this.createSimpleFlatItemModel(Blocks.LEVER);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.LEVER).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.POWERED, $$0, $$1)).with(PropertyDispatch.properties(BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING).select(AttachFace.CEILING, Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(AttachFace.CEILING, Direction.EAST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(AttachFace.CEILING, Direction.SOUTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)).select(AttachFace.CEILING, Direction.WEST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R180).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(AttachFace.FLOOR, Direction.NORTH, Variant.variant()).select(AttachFace.FLOOR, Direction.EAST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(AttachFace.FLOOR, Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(AttachFace.FLOOR, Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select(AttachFace.WALL, Direction.NORTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90)).select(AttachFace.WALL, Direction.EAST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select(AttachFace.WALL, Direction.SOUTH, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select(AttachFace.WALL, Direction.WEST, Variant.variant().with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))));
    }

    private void createLilyPad() {
        this.createSimpleFlatItemModel(Blocks.LILY_PAD);
        this.blockStateOutput.accept((Object)BlockModelGenerators.createRotatedVariant(Blocks.LILY_PAD, ModelLocationUtils.getModelLocation(Blocks.LILY_PAD)));
    }

    private void createFrogspawnBlock() {
        this.createSimpleFlatItemModel(Blocks.FROGSPAWN);
        this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock(Blocks.FROGSPAWN, ModelLocationUtils.getModelLocation(Blocks.FROGSPAWN)));
    }

    private void createNetherPortalBlock() {
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.NETHER_PORTAL).with(PropertyDispatch.property(BlockStateProperties.HORIZONTAL_AXIS).select(Direction.Axis.X, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.NETHER_PORTAL, "_ns"))).select(Direction.Axis.Z, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.NETHER_PORTAL, "_ew")))));
    }

    private void createNetherrack() {
        ResourceLocation $$0 = TexturedModel.CUBE.create(Blocks.NETHERRACK, this.modelOutput);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.NETHERRACK, Variant.variant().with(VariantProperties.MODEL, $$0), Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90), Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180), Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.X_ROT, VariantProperties.Rotation.R270), Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90), Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90), Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180), Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90).with(VariantProperties.X_ROT, VariantProperties.Rotation.R270), Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180), Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90), Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180), Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180).with(VariantProperties.X_ROT, VariantProperties.Rotation.R270), Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270), Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.X_ROT, VariantProperties.Rotation.R90), Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.X_ROT, VariantProperties.Rotation.R180), Variant.variant().with(VariantProperties.MODEL, $$0).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270).with(VariantProperties.X_ROT, VariantProperties.Rotation.R270)));
    }

    private void createObserver() {
        ResourceLocation $$0 = ModelLocationUtils.getModelLocation(Blocks.OBSERVER);
        ResourceLocation $$1 = ModelLocationUtils.getModelLocation(Blocks.OBSERVER, "_on");
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.OBSERVER).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.POWERED, $$1, $$0)).with(BlockModelGenerators.createFacingDispatch()));
    }

    private void createPistons() {
        TextureMapping $$0 = new TextureMapping().put(TextureSlot.BOTTOM, TextureMapping.getBlockTexture(Blocks.PISTON, "_bottom")).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.PISTON, "_side"));
        ResourceLocation $$1 = TextureMapping.getBlockTexture(Blocks.PISTON, "_top_sticky");
        ResourceLocation $$2 = TextureMapping.getBlockTexture(Blocks.PISTON, "_top");
        TextureMapping $$3 = $$0.copyAndUpdate(TextureSlot.PLATFORM, $$1);
        TextureMapping $$4 = $$0.copyAndUpdate(TextureSlot.PLATFORM, $$2);
        ResourceLocation $$5 = ModelLocationUtils.getModelLocation(Blocks.PISTON, "_base");
        this.createPistonVariant(Blocks.PISTON, $$5, $$4);
        this.createPistonVariant(Blocks.STICKY_PISTON, $$5, $$3);
        ResourceLocation $$6 = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(Blocks.PISTON, "_inventory", $$0.copyAndUpdate(TextureSlot.TOP, $$2), this.modelOutput);
        ResourceLocation $$7 = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(Blocks.STICKY_PISTON, "_inventory", $$0.copyAndUpdate(TextureSlot.TOP, $$1), this.modelOutput);
        this.delegateItemModel(Blocks.PISTON, $$6);
        this.delegateItemModel(Blocks.STICKY_PISTON, $$7);
    }

    private void createPistonVariant(Block $$0, ResourceLocation $$1, TextureMapping $$2) {
        ResourceLocation $$3 = ModelTemplates.PISTON.create($$0, $$2, this.modelOutput);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant($$0).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.EXTENDED, $$1, $$3)).with(BlockModelGenerators.createFacingDispatch()));
    }

    private void createPistonHeads() {
        TextureMapping $$0 = new TextureMapping().put(TextureSlot.UNSTICKY, TextureMapping.getBlockTexture(Blocks.PISTON, "_top")).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.PISTON, "_side"));
        TextureMapping $$1 = $$0.copyAndUpdate(TextureSlot.PLATFORM, TextureMapping.getBlockTexture(Blocks.PISTON, "_top_sticky"));
        TextureMapping $$2 = $$0.copyAndUpdate(TextureSlot.PLATFORM, TextureMapping.getBlockTexture(Blocks.PISTON, "_top"));
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.PISTON_HEAD).with(PropertyDispatch.properties(BlockStateProperties.SHORT, BlockStateProperties.PISTON_TYPE).select((Boolean)false, PistonType.DEFAULT, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.PISTON_HEAD.createWithSuffix(Blocks.PISTON, "_head", $$2, this.modelOutput))).select((Boolean)false, PistonType.STICKY, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.PISTON_HEAD.createWithSuffix(Blocks.PISTON, "_head_sticky", $$1, this.modelOutput))).select((Boolean)true, PistonType.DEFAULT, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.PISTON_HEAD_SHORT.createWithSuffix(Blocks.PISTON, "_head_short", $$2, this.modelOutput))).select((Boolean)true, PistonType.STICKY, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.PISTON_HEAD_SHORT.createWithSuffix(Blocks.PISTON, "_head_short_sticky", $$1, this.modelOutput)))).with(BlockModelGenerators.createFacingDispatch()));
    }

    private void createSculkSensor() {
        ResourceLocation $$0 = ModelLocationUtils.getModelLocation(Blocks.SCULK_SENSOR, "_inactive");
        ResourceLocation $$1 = ModelLocationUtils.getModelLocation(Blocks.SCULK_SENSOR, "_active");
        this.delegateItemModel(Blocks.SCULK_SENSOR, $$0);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.SCULK_SENSOR).with(PropertyDispatch.property(BlockStateProperties.SCULK_SENSOR_PHASE).generate((Function<SculkSensorPhase, Variant>)((Function)$$2 -> Variant.variant().with(VariantProperties.MODEL, $$2 == SculkSensorPhase.ACTIVE ? $$1 : $$0)))));
    }

    private void createSculkShrieker() {
        ResourceLocation $$0 = ModelTemplates.SCULK_SHRIEKER.create(Blocks.SCULK_SHRIEKER, TextureMapping.sculkShrieker(false), this.modelOutput);
        ResourceLocation $$1 = ModelTemplates.SCULK_SHRIEKER.createWithSuffix(Blocks.SCULK_SHRIEKER, "_can_summon", TextureMapping.sculkShrieker(true), this.modelOutput);
        this.delegateItemModel(Blocks.SCULK_SHRIEKER, $$0);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.SCULK_SHRIEKER).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.CAN_SUMMON, $$1, $$0)));
    }

    private void createScaffolding() {
        ResourceLocation $$0 = ModelLocationUtils.getModelLocation(Blocks.SCAFFOLDING, "_stable");
        ResourceLocation $$1 = ModelLocationUtils.getModelLocation(Blocks.SCAFFOLDING, "_unstable");
        this.delegateItemModel(Blocks.SCAFFOLDING, $$0);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.SCAFFOLDING).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.BOTTOM, $$1, $$0)));
    }

    private void createCaveVines() {
        ResourceLocation $$0 = this.createSuffixedVariant(Blocks.CAVE_VINES, "", ModelTemplates.CROSS, (Function<ResourceLocation, TextureMapping>)((Function)TextureMapping::cross));
        ResourceLocation $$1 = this.createSuffixedVariant(Blocks.CAVE_VINES, "_lit", ModelTemplates.CROSS, (Function<ResourceLocation, TextureMapping>)((Function)TextureMapping::cross));
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.CAVE_VINES).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.BERRIES, $$1, $$0)));
        ResourceLocation $$2 = this.createSuffixedVariant(Blocks.CAVE_VINES_PLANT, "", ModelTemplates.CROSS, (Function<ResourceLocation, TextureMapping>)((Function)TextureMapping::cross));
        ResourceLocation $$3 = this.createSuffixedVariant(Blocks.CAVE_VINES_PLANT, "_lit", ModelTemplates.CROSS, (Function<ResourceLocation, TextureMapping>)((Function)TextureMapping::cross));
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.CAVE_VINES_PLANT).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.BERRIES, $$3, $$2)));
    }

    private void createRedstoneLamp() {
        ResourceLocation $$0 = TexturedModel.CUBE.create(Blocks.REDSTONE_LAMP, this.modelOutput);
        ResourceLocation $$1 = this.createSuffixedVariant(Blocks.REDSTONE_LAMP, "_on", ModelTemplates.CUBE_ALL, (Function<ResourceLocation, TextureMapping>)((Function)TextureMapping::cube));
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.REDSTONE_LAMP).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.LIT, $$1, $$0)));
    }

    private void createNormalTorch(Block $$0, Block $$1) {
        TextureMapping $$2 = TextureMapping.torch($$0);
        this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock($$0, ModelTemplates.TORCH.create($$0, $$2, this.modelOutput)));
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant($$1, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.WALL_TORCH.create($$1, $$2, this.modelOutput))).with(BlockModelGenerators.createTorchHorizontalDispatch()));
        this.createSimpleFlatItemModel($$0);
        this.skipAutoItemBlock($$1);
    }

    private void createRedstoneTorch() {
        TextureMapping $$0 = TextureMapping.torch(Blocks.REDSTONE_TORCH);
        TextureMapping $$1 = TextureMapping.torch(TextureMapping.getBlockTexture(Blocks.REDSTONE_TORCH, "_off"));
        ResourceLocation $$2 = ModelTemplates.TORCH.create(Blocks.REDSTONE_TORCH, $$0, this.modelOutput);
        ResourceLocation $$3 = ModelTemplates.TORCH.createWithSuffix(Blocks.REDSTONE_TORCH, "_off", $$1, this.modelOutput);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.REDSTONE_TORCH).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.LIT, $$2, $$3)));
        ResourceLocation $$4 = ModelTemplates.WALL_TORCH.create(Blocks.REDSTONE_WALL_TORCH, $$0, this.modelOutput);
        ResourceLocation $$5 = ModelTemplates.WALL_TORCH.createWithSuffix(Blocks.REDSTONE_WALL_TORCH, "_off", $$1, this.modelOutput);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.REDSTONE_WALL_TORCH).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.LIT, $$4, $$5)).with(BlockModelGenerators.createTorchHorizontalDispatch()));
        this.createSimpleFlatItemModel(Blocks.REDSTONE_TORCH);
        this.skipAutoItemBlock(Blocks.REDSTONE_WALL_TORCH);
    }

    private void createRepeater() {
        this.createSimpleFlatItemModel(Items.REPEATER);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.REPEATER).with(PropertyDispatch.properties(BlockStateProperties.DELAY, BlockStateProperties.LOCKED, BlockStateProperties.POWERED).generate(($$0, $$1, $$2) -> {
            StringBuilder $$3 = new StringBuilder();
            $$3.append('_').append($$0).append("tick");
            if ($$2.booleanValue()) {
                $$3.append("_on");
            }
            if ($$1.booleanValue()) {
                $$3.append("_locked");
            }
            return Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.REPEATER, $$3.toString()));
        })).with(BlockModelGenerators.createHorizontalFacingDispatchAlt()));
    }

    private void createSeaPickle() {
        this.createSimpleFlatItemModel(Items.SEA_PICKLE);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.SEA_PICKLE).with(PropertyDispatch.properties(BlockStateProperties.PICKLES, BlockStateProperties.WATERLOGGED).select((Integer)1, (Boolean)false, (List<Variant>)Arrays.asList((Object[])BlockModelGenerators.createRotatedVariants(ModelLocationUtils.decorateBlockModelLocation("dead_sea_pickle")))).select((Integer)2, (Boolean)false, (List<Variant>)Arrays.asList((Object[])BlockModelGenerators.createRotatedVariants(ModelLocationUtils.decorateBlockModelLocation("two_dead_sea_pickles")))).select((Integer)3, (Boolean)false, (List<Variant>)Arrays.asList((Object[])BlockModelGenerators.createRotatedVariants(ModelLocationUtils.decorateBlockModelLocation("three_dead_sea_pickles")))).select((Integer)4, (Boolean)false, (List<Variant>)Arrays.asList((Object[])BlockModelGenerators.createRotatedVariants(ModelLocationUtils.decorateBlockModelLocation("four_dead_sea_pickles")))).select((Integer)1, (Boolean)true, (List<Variant>)Arrays.asList((Object[])BlockModelGenerators.createRotatedVariants(ModelLocationUtils.decorateBlockModelLocation("sea_pickle")))).select((Integer)2, (Boolean)true, (List<Variant>)Arrays.asList((Object[])BlockModelGenerators.createRotatedVariants(ModelLocationUtils.decorateBlockModelLocation("two_sea_pickles")))).select((Integer)3, (Boolean)true, (List<Variant>)Arrays.asList((Object[])BlockModelGenerators.createRotatedVariants(ModelLocationUtils.decorateBlockModelLocation("three_sea_pickles")))).select((Integer)4, (Boolean)true, (List<Variant>)Arrays.asList((Object[])BlockModelGenerators.createRotatedVariants(ModelLocationUtils.decorateBlockModelLocation("four_sea_pickles"))))));
    }

    private void createSnowBlocks() {
        TextureMapping $$0 = TextureMapping.cube(Blocks.SNOW);
        ResourceLocation $$12 = ModelTemplates.CUBE_ALL.create(Blocks.SNOW_BLOCK, $$0, this.modelOutput);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.SNOW).with(PropertyDispatch.property(BlockStateProperties.LAYERS).generate((Function<Integer, Variant>)((Function)$$1 -> Variant.variant().with(VariantProperties.MODEL, $$1 < 8 ? ModelLocationUtils.getModelLocation(Blocks.SNOW, "_height" + $$1 * 2) : $$12)))));
        this.delegateItemModel(Blocks.SNOW, ModelLocationUtils.getModelLocation(Blocks.SNOW, "_height2"));
        this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock(Blocks.SNOW_BLOCK, $$12));
    }

    private void createStonecutter() {
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.STONECUTTER, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.STONECUTTER))).with(BlockModelGenerators.createHorizontalFacingDispatch()));
    }

    private void createStructureBlock() {
        ResourceLocation $$02 = TexturedModel.CUBE.create(Blocks.STRUCTURE_BLOCK, this.modelOutput);
        this.delegateItemModel(Blocks.STRUCTURE_BLOCK, $$02);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.STRUCTURE_BLOCK).with(PropertyDispatch.property(BlockStateProperties.STRUCTUREBLOCK_MODE).generate((Function<StructureMode, Variant>)((Function)$$0 -> Variant.variant().with(VariantProperties.MODEL, this.createSuffixedVariant(Blocks.STRUCTURE_BLOCK, "_" + $$0.getSerializedName(), ModelTemplates.CUBE_ALL, (Function<ResourceLocation, TextureMapping>)((Function)TextureMapping::cube)))))));
    }

    private void createSweetBerryBush() {
        this.createSimpleFlatItemModel(Items.SWEET_BERRIES);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.SWEET_BERRY_BUSH).with(PropertyDispatch.property(BlockStateProperties.AGE_3).generate((Function<Integer, Variant>)((Function)$$0 -> Variant.variant().with(VariantProperties.MODEL, this.createSuffixedVariant(Blocks.SWEET_BERRY_BUSH, "_stage" + $$0, ModelTemplates.CROSS, (Function<ResourceLocation, TextureMapping>)((Function)TextureMapping::cross)))))));
    }

    private void createTripwire() {
        this.createSimpleFlatItemModel(Items.STRING);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.TRIPWIRE).with(PropertyDispatch.properties(BlockStateProperties.ATTACHED, BlockStateProperties.EAST, BlockStateProperties.NORTH, BlockStateProperties.SOUTH, BlockStateProperties.WEST).select((Boolean)false, (Boolean)false, (Boolean)false, (Boolean)false, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ns"))).select((Boolean)false, (Boolean)true, (Boolean)false, (Boolean)false, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_n")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select((Boolean)false, (Boolean)false, (Boolean)true, (Boolean)false, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_n"))).select((Boolean)false, (Boolean)false, (Boolean)false, (Boolean)true, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_n")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select((Boolean)false, (Boolean)false, (Boolean)false, (Boolean)false, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_n")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select((Boolean)false, (Boolean)true, (Boolean)true, (Boolean)false, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ne"))).select((Boolean)false, (Boolean)true, (Boolean)false, (Boolean)true, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ne")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select((Boolean)false, (Boolean)false, (Boolean)false, (Boolean)true, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ne")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select((Boolean)false, (Boolean)false, (Boolean)true, (Boolean)false, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ne")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select((Boolean)false, (Boolean)false, (Boolean)true, (Boolean)true, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ns"))).select((Boolean)false, (Boolean)true, (Boolean)false, (Boolean)false, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_ns")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select((Boolean)false, (Boolean)true, (Boolean)true, (Boolean)true, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_nse"))).select((Boolean)false, (Boolean)true, (Boolean)false, (Boolean)true, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_nse")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select((Boolean)false, (Boolean)false, (Boolean)true, (Boolean)true, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_nse")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select((Boolean)false, (Boolean)true, (Boolean)true, (Boolean)false, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_nse")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select((Boolean)false, (Boolean)true, (Boolean)true, (Boolean)true, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_nsew"))).select((Boolean)true, (Boolean)false, (Boolean)false, (Boolean)false, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ns"))).select((Boolean)true, (Boolean)false, (Boolean)true, (Boolean)false, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_n"))).select((Boolean)true, (Boolean)false, (Boolean)false, (Boolean)true, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_n")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select((Boolean)true, (Boolean)true, (Boolean)false, (Boolean)false, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_n")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select((Boolean)true, (Boolean)false, (Boolean)false, (Boolean)false, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_n")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select((Boolean)true, (Boolean)true, (Boolean)true, (Boolean)false, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ne"))).select((Boolean)true, (Boolean)true, (Boolean)false, (Boolean)true, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ne")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select((Boolean)true, (Boolean)false, (Boolean)false, (Boolean)true, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ne")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select((Boolean)true, (Boolean)false, (Boolean)true, (Boolean)false, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ne")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select((Boolean)true, (Boolean)false, (Boolean)true, (Boolean)true, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ns"))).select((Boolean)true, (Boolean)true, (Boolean)false, (Boolean)false, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_ns")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select((Boolean)true, (Boolean)true, (Boolean)true, (Boolean)true, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_nse"))).select((Boolean)true, (Boolean)true, (Boolean)false, (Boolean)true, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_nse")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)).select((Boolean)true, (Boolean)false, (Boolean)true, (Boolean)true, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_nse")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)).select((Boolean)true, (Boolean)true, (Boolean)true, (Boolean)false, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_nse")).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)).select((Boolean)true, (Boolean)true, (Boolean)true, (Boolean)true, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(Blocks.TRIPWIRE, "_attached_nsew")))));
    }

    private void createTripwireHook() {
        this.createSimpleFlatItemModel(Blocks.TRIPWIRE_HOOK);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.TRIPWIRE_HOOK).with(PropertyDispatch.properties(BlockStateProperties.ATTACHED, BlockStateProperties.POWERED).generate((BiFunction<Boolean, Boolean, Variant>)((BiFunction)($$0, $$1) -> Variant.variant().with(VariantProperties.MODEL, TextureMapping.getBlockTexture(Blocks.TRIPWIRE_HOOK, ($$0 != false ? "_attached" : "") + ($$1 != false ? "_on" : "")))))).with(BlockModelGenerators.createHorizontalFacingDispatch()));
    }

    private ResourceLocation createTurtleEggModel(int $$0, String $$1, TextureMapping $$2) {
        switch ($$0) {
            case 1: {
                return ModelTemplates.TURTLE_EGG.create(ModelLocationUtils.decorateBlockModelLocation($$1 + "turtle_egg"), $$2, this.modelOutput);
            }
            case 2: {
                return ModelTemplates.TWO_TURTLE_EGGS.create(ModelLocationUtils.decorateBlockModelLocation("two_" + $$1 + "turtle_eggs"), $$2, this.modelOutput);
            }
            case 3: {
                return ModelTemplates.THREE_TURTLE_EGGS.create(ModelLocationUtils.decorateBlockModelLocation("three_" + $$1 + "turtle_eggs"), $$2, this.modelOutput);
            }
            case 4: {
                return ModelTemplates.FOUR_TURTLE_EGGS.create(ModelLocationUtils.decorateBlockModelLocation("four_" + $$1 + "turtle_eggs"), $$2, this.modelOutput);
            }
        }
        throw new UnsupportedOperationException();
    }

    private ResourceLocation createTurtleEggModel(Integer $$0, Integer $$1) {
        switch ($$1) {
            case 0: {
                return this.createTurtleEggModel($$0, "", TextureMapping.cube(TextureMapping.getBlockTexture(Blocks.TURTLE_EGG)));
            }
            case 1: {
                return this.createTurtleEggModel($$0, "slightly_cracked_", TextureMapping.cube(TextureMapping.getBlockTexture(Blocks.TURTLE_EGG, "_slightly_cracked")));
            }
            case 2: {
                return this.createTurtleEggModel($$0, "very_cracked_", TextureMapping.cube(TextureMapping.getBlockTexture(Blocks.TURTLE_EGG, "_very_cracked")));
            }
        }
        throw new UnsupportedOperationException();
    }

    private void createTurtleEgg() {
        this.createSimpleFlatItemModel(Items.TURTLE_EGG);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.TURTLE_EGG).with(PropertyDispatch.properties(BlockStateProperties.EGGS, BlockStateProperties.HATCH).generateList((BiFunction<Integer, Integer, List<Variant>>)((BiFunction)($$0, $$1) -> Arrays.asList((Object[])BlockModelGenerators.createRotatedVariants(this.createTurtleEggModel((Integer)$$0, (Integer)$$1)))))));
    }

    private void createMultiface(Block $$0) {
        this.createSimpleFlatItemModel($$0);
        ResourceLocation $$12 = ModelLocationUtils.getModelLocation($$0);
        MultiPartGenerator $$2 = MultiPartGenerator.multiPart($$0);
        Condition.TerminalCondition $$3 = Util.make(Condition.condition(), $$1 -> MULTIFACE_GENERATOR.stream().map(Pair::getFirst).forEach($$2 -> {
            if ($$0.defaultBlockState().hasProperty($$2)) {
                $$1.term($$2, false);
            }
        }));
        for (Pair $$4 : MULTIFACE_GENERATOR) {
            BooleanProperty $$5 = (BooleanProperty)$$4.getFirst();
            Function $$6 = (Function)$$4.getSecond();
            if (!$$0.defaultBlockState().hasProperty($$5)) continue;
            $$2.with((Condition)Condition.condition().term($$5, true), (Variant)$$6.apply((Object)$$12));
            $$2.with((Condition)$$3, (Variant)$$6.apply((Object)$$12));
        }
        this.blockStateOutput.accept((Object)$$2);
    }

    private void createSculkCatalyst() {
        ResourceLocation $$0 = TextureMapping.getBlockTexture(Blocks.SCULK_CATALYST, "_bottom");
        TextureMapping $$1 = new TextureMapping().put(TextureSlot.BOTTOM, $$0).put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.SCULK_CATALYST, "_top")).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.SCULK_CATALYST, "_side"));
        TextureMapping $$22 = new TextureMapping().put(TextureSlot.BOTTOM, $$0).put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.SCULK_CATALYST, "_top_bloom")).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.SCULK_CATALYST, "_side_bloom"));
        ResourceLocation $$3 = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(Blocks.SCULK_CATALYST, "", $$1, this.modelOutput);
        ResourceLocation $$4 = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(Blocks.SCULK_CATALYST, "_bloom", $$22, this.modelOutput);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.SCULK_CATALYST).with(PropertyDispatch.property(BlockStateProperties.BLOOM).generate((Function<Boolean, Variant>)((Function)$$2 -> Variant.variant().with(VariantProperties.MODEL, $$2 != false ? $$4 : $$3)))));
        this.delegateItemModel(Items.SCULK_CATALYST, $$3);
    }

    private void createChiseledBookshelf() {
        Block $$0 = Blocks.CHISELED_BOOKSHELF;
        ResourceLocation $$1 = ModelLocationUtils.getModelLocation($$0);
        MultiPartGenerator $$22 = MultiPartGenerator.multiPart($$0);
        Map.of((Object)Direction.NORTH, (Object)((Object)VariantProperties.Rotation.R0), (Object)Direction.EAST, (Object)((Object)VariantProperties.Rotation.R90), (Object)Direction.SOUTH, (Object)((Object)VariantProperties.Rotation.R180), (Object)Direction.WEST, (Object)((Object)VariantProperties.Rotation.R270)).forEach(($$2, $$3) -> {
            Condition.TerminalCondition $$4 = Condition.condition().term(BlockStateProperties.HORIZONTAL_FACING, $$2);
            $$22.with((Condition)$$4, Variant.variant().with(VariantProperties.MODEL, $$1).with(VariantProperties.Y_ROT, $$3).with(VariantProperties.UV_LOCK, true));
            this.addSlotStateAndRotationVariants($$22, $$4, (VariantProperties.Rotation)((Object)$$3));
        });
        this.blockStateOutput.accept((Object)$$22);
        this.delegateItemModel($$0, ModelLocationUtils.getModelLocation($$0, "_inventory"));
        CHISELED_BOOKSHELF_SLOT_MODEL_CACHE.clear();
    }

    private void addSlotStateAndRotationVariants(MultiPartGenerator $$0, Condition.TerminalCondition $$1, VariantProperties.Rotation $$2) {
        Map.of((Object)BlockStateProperties.CHISELED_BOOKSHELF_SLOT_0_OCCUPIED, (Object)ModelTemplates.CHISELED_BOOKSHELF_SLOT_TOP_LEFT, (Object)BlockStateProperties.CHISELED_BOOKSHELF_SLOT_1_OCCUPIED, (Object)ModelTemplates.CHISELED_BOOKSHELF_SLOT_TOP_MID, (Object)BlockStateProperties.CHISELED_BOOKSHELF_SLOT_2_OCCUPIED, (Object)ModelTemplates.CHISELED_BOOKSHELF_SLOT_TOP_RIGHT, (Object)BlockStateProperties.CHISELED_BOOKSHELF_SLOT_3_OCCUPIED, (Object)ModelTemplates.CHISELED_BOOKSHELF_SLOT_BOTTOM_LEFT, (Object)BlockStateProperties.CHISELED_BOOKSHELF_SLOT_4_OCCUPIED, (Object)ModelTemplates.CHISELED_BOOKSHELF_SLOT_BOTTOM_MID, (Object)BlockStateProperties.CHISELED_BOOKSHELF_SLOT_5_OCCUPIED, (Object)ModelTemplates.CHISELED_BOOKSHELF_SLOT_BOTTOM_RIGHT).forEach(($$3, $$4) -> {
            this.addBookSlotModel($$0, $$1, $$2, (BooleanProperty)$$3, (ModelTemplate)$$4, true);
            this.addBookSlotModel($$0, $$1, $$2, (BooleanProperty)$$3, (ModelTemplate)$$4, false);
        });
    }

    private void addBookSlotModel(MultiPartGenerator $$0, Condition.TerminalCondition $$1, VariantProperties.Rotation $$2, BooleanProperty $$32, ModelTemplate $$4, boolean $$5) {
        String $$6 = $$5 ? "_occupied" : "_empty";
        TextureMapping $$7 = new TextureMapping().put(TextureSlot.TEXTURE, TextureMapping.getBlockTexture(Blocks.CHISELED_BOOKSHELF, $$6));
        BookSlotModelCacheKey $$8 = new BookSlotModelCacheKey($$4, $$6);
        ResourceLocation $$9 = (ResourceLocation)CHISELED_BOOKSHELF_SLOT_MODEL_CACHE.computeIfAbsent((Object)$$8, $$3 -> $$4.createWithSuffix(Blocks.CHISELED_BOOKSHELF, $$6, $$7, this.modelOutput));
        $$0.with(Condition.and($$1, Condition.condition().term($$32, $$5)), Variant.variant().with(VariantProperties.MODEL, $$9).with(VariantProperties.Y_ROT, $$2));
    }

    private void createMagmaBlock() {
        this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock(Blocks.MAGMA_BLOCK, ModelTemplates.CUBE_ALL.create(Blocks.MAGMA_BLOCK, TextureMapping.cube(ModelLocationUtils.decorateBlockModelLocation("magma")), this.modelOutput)));
    }

    private void createShulkerBox(Block $$0) {
        this.createTrivialBlock($$0, TexturedModel.PARTICLE_ONLY);
        ModelTemplates.SHULKER_BOX_INVENTORY.create(ModelLocationUtils.getModelLocation($$0.asItem()), TextureMapping.particle($$0), this.modelOutput);
    }

    private void createGrowingPlant(Block $$0, Block $$1, TintState $$2) {
        this.createCrossBlock($$0, $$2);
        this.createCrossBlock($$1, $$2);
    }

    private void createBedItem(Block $$0, Block $$1) {
        ModelTemplates.BED_INVENTORY.create(ModelLocationUtils.getModelLocation($$0.asItem()), TextureMapping.particle($$1), this.modelOutput);
    }

    private void createInfestedStone() {
        ResourceLocation $$0 = ModelLocationUtils.getModelLocation(Blocks.STONE);
        ResourceLocation $$1 = ModelLocationUtils.getModelLocation(Blocks.STONE, "_mirrored");
        this.blockStateOutput.accept((Object)BlockModelGenerators.createRotatedVariant(Blocks.INFESTED_STONE, $$0, $$1));
        this.delegateItemModel(Blocks.INFESTED_STONE, $$0);
    }

    private void createInfestedDeepslate() {
        ResourceLocation $$0 = ModelLocationUtils.getModelLocation(Blocks.DEEPSLATE);
        ResourceLocation $$1 = ModelLocationUtils.getModelLocation(Blocks.DEEPSLATE, "_mirrored");
        this.blockStateOutput.accept((Object)BlockModelGenerators.createRotatedVariant(Blocks.INFESTED_DEEPSLATE, $$0, $$1).with(BlockModelGenerators.createRotatedPillar()));
        this.delegateItemModel(Blocks.INFESTED_DEEPSLATE, $$0);
    }

    private void createNetherRoots(Block $$0, Block $$1) {
        this.createCrossBlockWithDefaultItem($$0, TintState.NOT_TINTED);
        TextureMapping $$2 = TextureMapping.plant(TextureMapping.getBlockTexture($$0, "_pot"));
        ResourceLocation $$3 = TintState.NOT_TINTED.getCrossPot().create($$1, $$2, this.modelOutput);
        this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock($$1, $$3));
    }

    private void createRespawnAnchor() {
        ResourceLocation $$0 = TextureMapping.getBlockTexture(Blocks.RESPAWN_ANCHOR, "_bottom");
        ResourceLocation $$12 = TextureMapping.getBlockTexture(Blocks.RESPAWN_ANCHOR, "_top_off");
        ResourceLocation $$2 = TextureMapping.getBlockTexture(Blocks.RESPAWN_ANCHOR, "_top");
        ResourceLocation[] $$3 = new ResourceLocation[5];
        for (int $$4 = 0; $$4 < 5; ++$$4) {
            TextureMapping $$5 = new TextureMapping().put(TextureSlot.BOTTOM, $$0).put(TextureSlot.TOP, $$4 == 0 ? $$12 : $$2).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(Blocks.RESPAWN_ANCHOR, "_side" + $$4));
            $$3[$$4] = ModelTemplates.CUBE_BOTTOM_TOP.createWithSuffix(Blocks.RESPAWN_ANCHOR, "_" + $$4, $$5, this.modelOutput);
        }
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.RESPAWN_ANCHOR).with(PropertyDispatch.property(BlockStateProperties.RESPAWN_ANCHOR_CHARGES).generate((Function<Integer, Variant>)((Function)$$1 -> Variant.variant().with(VariantProperties.MODEL, $$3[$$1])))));
        this.delegateItemModel(Items.RESPAWN_ANCHOR, $$3[0]);
    }

    private Variant applyRotation(FrontAndTop $$0, Variant $$1) {
        switch ($$0) {
            case DOWN_NORTH: {
                return $$1.with(VariantProperties.X_ROT, VariantProperties.Rotation.R90);
            }
            case DOWN_SOUTH: {
                return $$1.with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180);
            }
            case DOWN_WEST: {
                return $$1.with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270);
            }
            case DOWN_EAST: {
                return $$1.with(VariantProperties.X_ROT, VariantProperties.Rotation.R90).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
            }
            case UP_NORTH: {
                return $$1.with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180);
            }
            case UP_SOUTH: {
                return $$1.with(VariantProperties.X_ROT, VariantProperties.Rotation.R270);
            }
            case UP_WEST: {
                return $$1.with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
            }
            case UP_EAST: {
                return $$1.with(VariantProperties.X_ROT, VariantProperties.Rotation.R270).with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270);
            }
            case NORTH_UP: {
                return $$1;
            }
            case SOUTH_UP: {
                return $$1.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180);
            }
            case WEST_UP: {
                return $$1.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270);
            }
            case EAST_UP: {
                return $$1.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
            }
        }
        throw new UnsupportedOperationException("Rotation " + $$0 + " can't be expressed with existing x and y values");
    }

    private void createJigsaw() {
        ResourceLocation $$02 = TextureMapping.getBlockTexture(Blocks.JIGSAW, "_top");
        ResourceLocation $$1 = TextureMapping.getBlockTexture(Blocks.JIGSAW, "_bottom");
        ResourceLocation $$2 = TextureMapping.getBlockTexture(Blocks.JIGSAW, "_side");
        ResourceLocation $$3 = TextureMapping.getBlockTexture(Blocks.JIGSAW, "_lock");
        TextureMapping $$4 = new TextureMapping().put(TextureSlot.DOWN, $$2).put(TextureSlot.WEST, $$2).put(TextureSlot.EAST, $$2).put(TextureSlot.PARTICLE, $$02).put(TextureSlot.NORTH, $$02).put(TextureSlot.SOUTH, $$1).put(TextureSlot.UP, $$3);
        ResourceLocation $$5 = ModelTemplates.CUBE_DIRECTIONAL.create(Blocks.JIGSAW, $$4, this.modelOutput);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.JIGSAW, Variant.variant().with(VariantProperties.MODEL, $$5)).with(PropertyDispatch.property(BlockStateProperties.ORIENTATION).generate((Function<FrontAndTop, Variant>)((Function)$$0 -> this.applyRotation((FrontAndTop)$$0, Variant.variant())))));
    }

    private void createPetrifiedOakSlab() {
        Block $$0 = Blocks.OAK_PLANKS;
        ResourceLocation $$1 = ModelLocationUtils.getModelLocation($$0);
        TexturedModel $$2 = TexturedModel.CUBE.get($$0);
        Block $$3 = Blocks.PETRIFIED_OAK_SLAB;
        ResourceLocation $$4 = ModelTemplates.SLAB_BOTTOM.create($$3, $$2.getMapping(), this.modelOutput);
        ResourceLocation $$5 = ModelTemplates.SLAB_TOP.create($$3, $$2.getMapping(), this.modelOutput);
        this.blockStateOutput.accept((Object)BlockModelGenerators.createSlab($$3, $$4, $$5, $$1));
    }

    public void run() {
        BlockFamilies.getAllFamilies().filter(BlockFamily::shouldGenerateModel).forEach($$0 -> this.family($$0.getBaseBlock()).generateFor((BlockFamily)$$0));
        this.family(Blocks.CUT_COPPER).generateFor(BlockFamilies.CUT_COPPER).fullBlockCopies(Blocks.WAXED_CUT_COPPER).generateFor(BlockFamilies.WAXED_CUT_COPPER);
        this.family(Blocks.EXPOSED_CUT_COPPER).generateFor(BlockFamilies.EXPOSED_CUT_COPPER).fullBlockCopies(Blocks.WAXED_EXPOSED_CUT_COPPER).generateFor(BlockFamilies.WAXED_EXPOSED_CUT_COPPER);
        this.family(Blocks.WEATHERED_CUT_COPPER).generateFor(BlockFamilies.WEATHERED_CUT_COPPER).fullBlockCopies(Blocks.WAXED_WEATHERED_CUT_COPPER).generateFor(BlockFamilies.WAXED_WEATHERED_CUT_COPPER);
        this.family(Blocks.OXIDIZED_CUT_COPPER).generateFor(BlockFamilies.OXIDIZED_CUT_COPPER).fullBlockCopies(Blocks.WAXED_OXIDIZED_CUT_COPPER).generateFor(BlockFamilies.WAXED_OXIDIZED_CUT_COPPER);
        this.createNonTemplateModelBlock(Blocks.AIR);
        this.createNonTemplateModelBlock(Blocks.CAVE_AIR, Blocks.AIR);
        this.createNonTemplateModelBlock(Blocks.VOID_AIR, Blocks.AIR);
        this.createNonTemplateModelBlock(Blocks.BEACON);
        this.createNonTemplateModelBlock(Blocks.CACTUS);
        this.createNonTemplateModelBlock(Blocks.BUBBLE_COLUMN, Blocks.WATER);
        this.createNonTemplateModelBlock(Blocks.DRAGON_EGG);
        this.createNonTemplateModelBlock(Blocks.DRIED_KELP_BLOCK);
        this.createNonTemplateModelBlock(Blocks.ENCHANTING_TABLE);
        this.createNonTemplateModelBlock(Blocks.FLOWER_POT);
        this.createSimpleFlatItemModel(Items.FLOWER_POT);
        this.createNonTemplateModelBlock(Blocks.HONEY_BLOCK);
        this.createNonTemplateModelBlock(Blocks.WATER);
        this.createNonTemplateModelBlock(Blocks.LAVA);
        this.createNonTemplateModelBlock(Blocks.SLIME_BLOCK);
        this.createSimpleFlatItemModel(Items.CHAIN);
        this.createCandleAndCandleCake(Blocks.WHITE_CANDLE, Blocks.WHITE_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.ORANGE_CANDLE, Blocks.ORANGE_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.MAGENTA_CANDLE, Blocks.MAGENTA_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.LIGHT_BLUE_CANDLE, Blocks.LIGHT_BLUE_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.YELLOW_CANDLE, Blocks.YELLOW_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.LIME_CANDLE, Blocks.LIME_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.PINK_CANDLE, Blocks.PINK_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.GRAY_CANDLE, Blocks.GRAY_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.LIGHT_GRAY_CANDLE, Blocks.LIGHT_GRAY_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.CYAN_CANDLE, Blocks.CYAN_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.PURPLE_CANDLE, Blocks.PURPLE_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.BLUE_CANDLE, Blocks.BLUE_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.BROWN_CANDLE, Blocks.BROWN_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.GREEN_CANDLE, Blocks.GREEN_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.RED_CANDLE, Blocks.RED_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.BLACK_CANDLE, Blocks.BLACK_CANDLE_CAKE);
        this.createCandleAndCandleCake(Blocks.CANDLE, Blocks.CANDLE_CAKE);
        this.createNonTemplateModelBlock(Blocks.POTTED_BAMBOO);
        this.createNonTemplateModelBlock(Blocks.POTTED_CACTUS);
        this.createNonTemplateModelBlock(Blocks.POWDER_SNOW);
        this.createNonTemplateModelBlock(Blocks.SPORE_BLOSSOM);
        this.createAzalea(Blocks.AZALEA);
        this.createAzalea(Blocks.FLOWERING_AZALEA);
        this.createPottedAzalea(Blocks.POTTED_AZALEA);
        this.createPottedAzalea(Blocks.POTTED_FLOWERING_AZALEA);
        this.createCaveVines();
        this.createFullAndCarpetBlocks(Blocks.MOSS_BLOCK, Blocks.MOSS_CARPET);
        this.createAirLikeBlock(Blocks.BARRIER, Items.BARRIER);
        this.createSimpleFlatItemModel(Items.BARRIER);
        this.createLightBlock();
        this.createAirLikeBlock(Blocks.STRUCTURE_VOID, Items.STRUCTURE_VOID);
        this.createSimpleFlatItemModel(Items.STRUCTURE_VOID);
        this.createAirLikeBlock(Blocks.MOVING_PISTON, TextureMapping.getBlockTexture(Blocks.PISTON, "_side"));
        this.createTrivialCube(Blocks.COAL_ORE);
        this.createTrivialCube(Blocks.DEEPSLATE_COAL_ORE);
        this.createTrivialCube(Blocks.COAL_BLOCK);
        this.createTrivialCube(Blocks.DIAMOND_ORE);
        this.createTrivialCube(Blocks.DEEPSLATE_DIAMOND_ORE);
        this.createTrivialCube(Blocks.DIAMOND_BLOCK);
        this.createTrivialCube(Blocks.EMERALD_ORE);
        this.createTrivialCube(Blocks.DEEPSLATE_EMERALD_ORE);
        this.createTrivialCube(Blocks.EMERALD_BLOCK);
        this.createTrivialCube(Blocks.GOLD_ORE);
        this.createTrivialCube(Blocks.NETHER_GOLD_ORE);
        this.createTrivialCube(Blocks.DEEPSLATE_GOLD_ORE);
        this.createTrivialCube(Blocks.GOLD_BLOCK);
        this.createTrivialCube(Blocks.IRON_ORE);
        this.createTrivialCube(Blocks.DEEPSLATE_IRON_ORE);
        this.createTrivialCube(Blocks.IRON_BLOCK);
        this.createTrivialBlock(Blocks.ANCIENT_DEBRIS, TexturedModel.COLUMN);
        this.createTrivialCube(Blocks.NETHERITE_BLOCK);
        this.createTrivialCube(Blocks.LAPIS_ORE);
        this.createTrivialCube(Blocks.DEEPSLATE_LAPIS_ORE);
        this.createTrivialCube(Blocks.LAPIS_BLOCK);
        this.createTrivialCube(Blocks.NETHER_QUARTZ_ORE);
        this.createTrivialCube(Blocks.REDSTONE_ORE);
        this.createTrivialCube(Blocks.DEEPSLATE_REDSTONE_ORE);
        this.createTrivialCube(Blocks.REDSTONE_BLOCK);
        this.createTrivialCube(Blocks.GILDED_BLACKSTONE);
        this.createTrivialCube(Blocks.BLUE_ICE);
        this.createTrivialCube(Blocks.CLAY);
        this.createTrivialCube(Blocks.COARSE_DIRT);
        this.createTrivialCube(Blocks.CRYING_OBSIDIAN);
        this.createTrivialCube(Blocks.END_STONE);
        this.createTrivialCube(Blocks.GLOWSTONE);
        this.createTrivialCube(Blocks.GRAVEL);
        this.createTrivialCube(Blocks.HONEYCOMB_BLOCK);
        this.createTrivialCube(Blocks.ICE);
        this.createTrivialBlock(Blocks.JUKEBOX, TexturedModel.CUBE_TOP);
        this.createTrivialBlock(Blocks.LODESTONE, TexturedModel.COLUMN);
        this.createTrivialBlock(Blocks.MELON, TexturedModel.COLUMN);
        this.createNonTemplateModelBlock(Blocks.MANGROVE_ROOTS);
        this.createNonTemplateModelBlock(Blocks.POTTED_MANGROVE_PROPAGULE);
        this.createTrivialCube(Blocks.NETHER_WART_BLOCK);
        this.createTrivialCube(Blocks.NOTE_BLOCK);
        this.createTrivialCube(Blocks.PACKED_ICE);
        this.createTrivialCube(Blocks.OBSIDIAN);
        this.createTrivialCube(Blocks.QUARTZ_BRICKS);
        this.createTrivialCube(Blocks.SEA_LANTERN);
        this.createTrivialCube(Blocks.SHROOMLIGHT);
        this.createTrivialCube(Blocks.SOUL_SAND);
        this.createTrivialCube(Blocks.SOUL_SOIL);
        this.createTrivialCube(Blocks.SPAWNER);
        this.createTrivialCube(Blocks.SPONGE);
        this.createTrivialBlock(Blocks.SEAGRASS, TexturedModel.SEAGRASS);
        this.createSimpleFlatItemModel(Items.SEAGRASS);
        this.createTrivialBlock(Blocks.TNT, TexturedModel.CUBE_TOP_BOTTOM);
        this.createTrivialBlock(Blocks.TARGET, TexturedModel.COLUMN);
        this.createTrivialCube(Blocks.WARPED_WART_BLOCK);
        this.createTrivialCube(Blocks.WET_SPONGE);
        this.createTrivialCube(Blocks.AMETHYST_BLOCK);
        this.createTrivialCube(Blocks.BUDDING_AMETHYST);
        this.createTrivialCube(Blocks.CALCITE);
        this.createTrivialCube(Blocks.TUFF);
        this.createTrivialCube(Blocks.DRIPSTONE_BLOCK);
        this.createTrivialCube(Blocks.RAW_IRON_BLOCK);
        this.createTrivialCube(Blocks.RAW_COPPER_BLOCK);
        this.createTrivialCube(Blocks.RAW_GOLD_BLOCK);
        this.createRotatedMirroredVariantBlock(Blocks.SCULK);
        this.createPetrifiedOakSlab();
        this.createTrivialCube(Blocks.COPPER_ORE);
        this.createTrivialCube(Blocks.DEEPSLATE_COPPER_ORE);
        this.createTrivialCube(Blocks.COPPER_BLOCK);
        this.createTrivialCube(Blocks.EXPOSED_COPPER);
        this.createTrivialCube(Blocks.WEATHERED_COPPER);
        this.createTrivialCube(Blocks.OXIDIZED_COPPER);
        this.copyModel(Blocks.COPPER_BLOCK, Blocks.WAXED_COPPER_BLOCK);
        this.copyModel(Blocks.EXPOSED_COPPER, Blocks.WAXED_EXPOSED_COPPER);
        this.copyModel(Blocks.WEATHERED_COPPER, Blocks.WAXED_WEATHERED_COPPER);
        this.copyModel(Blocks.OXIDIZED_COPPER, Blocks.WAXED_OXIDIZED_COPPER);
        this.createWeightedPressurePlate(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, Blocks.GOLD_BLOCK);
        this.createWeightedPressurePlate(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, Blocks.IRON_BLOCK);
        this.createAmethystClusters();
        this.createBookshelf();
        this.createChiseledBookshelf();
        this.createBrewingStand();
        this.createCakeBlock();
        this.createCampfires(Blocks.CAMPFIRE, Blocks.SOUL_CAMPFIRE);
        this.createCartographyTable();
        this.createCauldrons();
        this.createChorusFlower();
        this.createChorusPlant();
        this.createComposter();
        this.createDaylightDetector();
        this.createEndPortalFrame();
        this.createRotatableColumn(Blocks.END_ROD);
        this.createLightningRod();
        this.createFarmland();
        this.createFire();
        this.createSoulFire();
        this.createFrostedIce();
        this.createGrassBlocks();
        this.createCocoa();
        this.createDirtPath();
        this.createGrindstone();
        this.createHopper();
        this.createIronBars();
        this.createLever();
        this.createLilyPad();
        this.createNetherPortalBlock();
        this.createNetherrack();
        this.createObserver();
        this.createPistons();
        this.createPistonHeads();
        this.createScaffolding();
        this.createRedstoneTorch();
        this.createRedstoneLamp();
        this.createRepeater();
        this.createSeaPickle();
        this.createSmithingTable();
        this.createSnowBlocks();
        this.createStonecutter();
        this.createStructureBlock();
        this.createSweetBerryBush();
        this.createTripwire();
        this.createTripwireHook();
        this.createTurtleEgg();
        this.createMultiface(Blocks.VINE);
        this.createMultiface(Blocks.GLOW_LICHEN);
        this.createMultiface(Blocks.SCULK_VEIN);
        this.createMagmaBlock();
        this.createJigsaw();
        this.createSculkSensor();
        this.createSculkShrieker();
        this.createFrogspawnBlock();
        this.createMangrovePropagule();
        this.createMuddyMangroveRoots();
        this.createNonTemplateHorizontalBlock(Blocks.LADDER);
        this.createSimpleFlatItemModel(Blocks.LADDER);
        this.createNonTemplateHorizontalBlock(Blocks.LECTERN);
        this.createBigDripLeafBlock();
        this.createNonTemplateHorizontalBlock(Blocks.BIG_DRIPLEAF_STEM);
        this.createNormalTorch(Blocks.TORCH, Blocks.WALL_TORCH);
        this.createNormalTorch(Blocks.SOUL_TORCH, Blocks.SOUL_WALL_TORCH);
        this.createCraftingTableLike(Blocks.CRAFTING_TABLE, Blocks.OAK_PLANKS, (BiFunction<Block, Block, TextureMapping>)((BiFunction)TextureMapping::craftingTable));
        this.createCraftingTableLike(Blocks.FLETCHING_TABLE, Blocks.BIRCH_PLANKS, (BiFunction<Block, Block, TextureMapping>)((BiFunction)TextureMapping::fletchingTable));
        this.createNyliumBlock(Blocks.CRIMSON_NYLIUM);
        this.createNyliumBlock(Blocks.WARPED_NYLIUM);
        this.createDispenserBlock(Blocks.DISPENSER);
        this.createDispenserBlock(Blocks.DROPPER);
        this.createLantern(Blocks.LANTERN);
        this.createLantern(Blocks.SOUL_LANTERN);
        this.createAxisAlignedPillarBlockCustomModel(Blocks.CHAIN, ModelLocationUtils.getModelLocation(Blocks.CHAIN));
        this.createAxisAlignedPillarBlock(Blocks.BASALT, TexturedModel.COLUMN);
        this.createAxisAlignedPillarBlock(Blocks.POLISHED_BASALT, TexturedModel.COLUMN);
        this.createTrivialCube(Blocks.SMOOTH_BASALT);
        this.createAxisAlignedPillarBlock(Blocks.BONE_BLOCK, TexturedModel.COLUMN);
        this.createRotatedVariantBlock(Blocks.DIRT);
        this.createRotatedVariantBlock(Blocks.ROOTED_DIRT);
        this.createRotatedVariantBlock(Blocks.SAND);
        this.createRotatedVariantBlock(Blocks.RED_SAND);
        this.createRotatedMirroredVariantBlock(Blocks.BEDROCK);
        this.createTrivialBlock(Blocks.REINFORCED_DEEPSLATE, TexturedModel.CUBE_TOP_BOTTOM);
        this.createRotatedPillarWithHorizontalVariant(Blocks.HAY_BLOCK, TexturedModel.COLUMN, TexturedModel.COLUMN_HORIZONTAL);
        this.createRotatedPillarWithHorizontalVariant(Blocks.PURPUR_PILLAR, TexturedModel.COLUMN_ALT, TexturedModel.COLUMN_HORIZONTAL_ALT);
        this.createRotatedPillarWithHorizontalVariant(Blocks.QUARTZ_PILLAR, TexturedModel.COLUMN_ALT, TexturedModel.COLUMN_HORIZONTAL_ALT);
        this.createRotatedPillarWithHorizontalVariant(Blocks.OCHRE_FROGLIGHT, TexturedModel.COLUMN, TexturedModel.COLUMN_HORIZONTAL);
        this.createRotatedPillarWithHorizontalVariant(Blocks.VERDANT_FROGLIGHT, TexturedModel.COLUMN, TexturedModel.COLUMN_HORIZONTAL);
        this.createRotatedPillarWithHorizontalVariant(Blocks.PEARLESCENT_FROGLIGHT, TexturedModel.COLUMN, TexturedModel.COLUMN_HORIZONTAL);
        this.createHorizontallyRotatedBlock(Blocks.LOOM, TexturedModel.ORIENTABLE);
        this.createPumpkins();
        this.createBeeNest(Blocks.BEE_NEST, (Function<Block, TextureMapping>)((Function)TextureMapping::orientableCube));
        this.createBeeNest(Blocks.BEEHIVE, (Function<Block, TextureMapping>)((Function)TextureMapping::orientableCubeSameEnds));
        this.createCropBlock(Blocks.BEETROOTS, BlockStateProperties.AGE_3, 0, 1, 2, 3);
        this.createCropBlock(Blocks.CARROTS, BlockStateProperties.AGE_7, 0, 0, 1, 1, 2, 2, 2, 3);
        this.createCropBlock(Blocks.NETHER_WART, BlockStateProperties.AGE_3, 0, 1, 1, 2);
        this.createCropBlock(Blocks.POTATOES, BlockStateProperties.AGE_7, 0, 0, 1, 1, 2, 2, 2, 3);
        this.createCropBlock(Blocks.WHEAT, BlockStateProperties.AGE_7, 0, 1, 2, 3, 4, 5, 6, 7);
        this.blockEntityModels(ModelLocationUtils.decorateBlockModelLocation("banner"), Blocks.OAK_PLANKS).createWithCustomBlockItemModel(ModelTemplates.BANNER_INVENTORY, Blocks.WHITE_BANNER, Blocks.ORANGE_BANNER, Blocks.MAGENTA_BANNER, Blocks.LIGHT_BLUE_BANNER, Blocks.YELLOW_BANNER, Blocks.LIME_BANNER, Blocks.PINK_BANNER, Blocks.GRAY_BANNER, Blocks.LIGHT_GRAY_BANNER, Blocks.CYAN_BANNER, Blocks.PURPLE_BANNER, Blocks.BLUE_BANNER, Blocks.BROWN_BANNER, Blocks.GREEN_BANNER, Blocks.RED_BANNER, Blocks.BLACK_BANNER).createWithoutBlockItem(Blocks.WHITE_WALL_BANNER, Blocks.ORANGE_WALL_BANNER, Blocks.MAGENTA_WALL_BANNER, Blocks.LIGHT_BLUE_WALL_BANNER, Blocks.YELLOW_WALL_BANNER, Blocks.LIME_WALL_BANNER, Blocks.PINK_WALL_BANNER, Blocks.GRAY_WALL_BANNER, Blocks.LIGHT_GRAY_WALL_BANNER, Blocks.CYAN_WALL_BANNER, Blocks.PURPLE_WALL_BANNER, Blocks.BLUE_WALL_BANNER, Blocks.BROWN_WALL_BANNER, Blocks.GREEN_WALL_BANNER, Blocks.RED_WALL_BANNER, Blocks.BLACK_WALL_BANNER);
        this.blockEntityModels(ModelLocationUtils.decorateBlockModelLocation("bed"), Blocks.OAK_PLANKS).createWithoutBlockItem(Blocks.WHITE_BED, Blocks.ORANGE_BED, Blocks.MAGENTA_BED, Blocks.LIGHT_BLUE_BED, Blocks.YELLOW_BED, Blocks.LIME_BED, Blocks.PINK_BED, Blocks.GRAY_BED, Blocks.LIGHT_GRAY_BED, Blocks.CYAN_BED, Blocks.PURPLE_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.GREEN_BED, Blocks.RED_BED, Blocks.BLACK_BED);
        this.createBedItem(Blocks.WHITE_BED, Blocks.WHITE_WOOL);
        this.createBedItem(Blocks.ORANGE_BED, Blocks.ORANGE_WOOL);
        this.createBedItem(Blocks.MAGENTA_BED, Blocks.MAGENTA_WOOL);
        this.createBedItem(Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_BLUE_WOOL);
        this.createBedItem(Blocks.YELLOW_BED, Blocks.YELLOW_WOOL);
        this.createBedItem(Blocks.LIME_BED, Blocks.LIME_WOOL);
        this.createBedItem(Blocks.PINK_BED, Blocks.PINK_WOOL);
        this.createBedItem(Blocks.GRAY_BED, Blocks.GRAY_WOOL);
        this.createBedItem(Blocks.LIGHT_GRAY_BED, Blocks.LIGHT_GRAY_WOOL);
        this.createBedItem(Blocks.CYAN_BED, Blocks.CYAN_WOOL);
        this.createBedItem(Blocks.PURPLE_BED, Blocks.PURPLE_WOOL);
        this.createBedItem(Blocks.BLUE_BED, Blocks.BLUE_WOOL);
        this.createBedItem(Blocks.BROWN_BED, Blocks.BROWN_WOOL);
        this.createBedItem(Blocks.GREEN_BED, Blocks.GREEN_WOOL);
        this.createBedItem(Blocks.RED_BED, Blocks.RED_WOOL);
        this.createBedItem(Blocks.BLACK_BED, Blocks.BLACK_WOOL);
        this.blockEntityModels(ModelLocationUtils.decorateBlockModelLocation("skull"), Blocks.SOUL_SAND).createWithCustomBlockItemModel(ModelTemplates.SKULL_INVENTORY, Blocks.CREEPER_HEAD, Blocks.PLAYER_HEAD, Blocks.ZOMBIE_HEAD, Blocks.SKELETON_SKULL, Blocks.WITHER_SKELETON_SKULL, Blocks.PIGLIN_HEAD).create(Blocks.DRAGON_HEAD).createWithoutBlockItem(Blocks.CREEPER_WALL_HEAD, Blocks.DRAGON_WALL_HEAD, Blocks.PLAYER_WALL_HEAD, Blocks.ZOMBIE_WALL_HEAD, Blocks.SKELETON_WALL_SKULL, Blocks.WITHER_SKELETON_WALL_SKULL, Blocks.PIGLIN_WALL_HEAD);
        this.createShulkerBox(Blocks.SHULKER_BOX);
        this.createShulkerBox(Blocks.WHITE_SHULKER_BOX);
        this.createShulkerBox(Blocks.ORANGE_SHULKER_BOX);
        this.createShulkerBox(Blocks.MAGENTA_SHULKER_BOX);
        this.createShulkerBox(Blocks.LIGHT_BLUE_SHULKER_BOX);
        this.createShulkerBox(Blocks.YELLOW_SHULKER_BOX);
        this.createShulkerBox(Blocks.LIME_SHULKER_BOX);
        this.createShulkerBox(Blocks.PINK_SHULKER_BOX);
        this.createShulkerBox(Blocks.GRAY_SHULKER_BOX);
        this.createShulkerBox(Blocks.LIGHT_GRAY_SHULKER_BOX);
        this.createShulkerBox(Blocks.CYAN_SHULKER_BOX);
        this.createShulkerBox(Blocks.PURPLE_SHULKER_BOX);
        this.createShulkerBox(Blocks.BLUE_SHULKER_BOX);
        this.createShulkerBox(Blocks.BROWN_SHULKER_BOX);
        this.createShulkerBox(Blocks.GREEN_SHULKER_BOX);
        this.createShulkerBox(Blocks.RED_SHULKER_BOX);
        this.createShulkerBox(Blocks.BLACK_SHULKER_BOX);
        this.createTrivialBlock(Blocks.CONDUIT, TexturedModel.PARTICLE_ONLY);
        this.skipAutoItemBlock(Blocks.CONDUIT);
        this.blockEntityModels(ModelLocationUtils.decorateBlockModelLocation("chest"), Blocks.OAK_PLANKS).createWithoutBlockItem(Blocks.CHEST, Blocks.TRAPPED_CHEST);
        this.blockEntityModels(ModelLocationUtils.decorateBlockModelLocation("ender_chest"), Blocks.OBSIDIAN).createWithoutBlockItem(Blocks.ENDER_CHEST);
        this.blockEntityModels(Blocks.END_PORTAL, Blocks.OBSIDIAN).create(Blocks.END_PORTAL, Blocks.END_GATEWAY);
        this.createTrivialCube(Blocks.AZALEA_LEAVES);
        this.createTrivialCube(Blocks.FLOWERING_AZALEA_LEAVES);
        this.createTrivialCube(Blocks.WHITE_CONCRETE);
        this.createTrivialCube(Blocks.ORANGE_CONCRETE);
        this.createTrivialCube(Blocks.MAGENTA_CONCRETE);
        this.createTrivialCube(Blocks.LIGHT_BLUE_CONCRETE);
        this.createTrivialCube(Blocks.YELLOW_CONCRETE);
        this.createTrivialCube(Blocks.LIME_CONCRETE);
        this.createTrivialCube(Blocks.PINK_CONCRETE);
        this.createTrivialCube(Blocks.GRAY_CONCRETE);
        this.createTrivialCube(Blocks.LIGHT_GRAY_CONCRETE);
        this.createTrivialCube(Blocks.CYAN_CONCRETE);
        this.createTrivialCube(Blocks.PURPLE_CONCRETE);
        this.createTrivialCube(Blocks.BLUE_CONCRETE);
        this.createTrivialCube(Blocks.BROWN_CONCRETE);
        this.createTrivialCube(Blocks.GREEN_CONCRETE);
        this.createTrivialCube(Blocks.RED_CONCRETE);
        this.createTrivialCube(Blocks.BLACK_CONCRETE);
        this.createColoredBlockWithRandomRotations(TexturedModel.CUBE, Blocks.WHITE_CONCRETE_POWDER, Blocks.ORANGE_CONCRETE_POWDER, Blocks.MAGENTA_CONCRETE_POWDER, Blocks.LIGHT_BLUE_CONCRETE_POWDER, Blocks.YELLOW_CONCRETE_POWDER, Blocks.LIME_CONCRETE_POWDER, Blocks.PINK_CONCRETE_POWDER, Blocks.GRAY_CONCRETE_POWDER, Blocks.LIGHT_GRAY_CONCRETE_POWDER, Blocks.CYAN_CONCRETE_POWDER, Blocks.PURPLE_CONCRETE_POWDER, Blocks.BLUE_CONCRETE_POWDER, Blocks.BROWN_CONCRETE_POWDER, Blocks.GREEN_CONCRETE_POWDER, Blocks.RED_CONCRETE_POWDER, Blocks.BLACK_CONCRETE_POWDER);
        this.createTrivialCube(Blocks.TERRACOTTA);
        this.createTrivialCube(Blocks.WHITE_TERRACOTTA);
        this.createTrivialCube(Blocks.ORANGE_TERRACOTTA);
        this.createTrivialCube(Blocks.MAGENTA_TERRACOTTA);
        this.createTrivialCube(Blocks.LIGHT_BLUE_TERRACOTTA);
        this.createTrivialCube(Blocks.YELLOW_TERRACOTTA);
        this.createTrivialCube(Blocks.LIME_TERRACOTTA);
        this.createTrivialCube(Blocks.PINK_TERRACOTTA);
        this.createTrivialCube(Blocks.GRAY_TERRACOTTA);
        this.createTrivialCube(Blocks.LIGHT_GRAY_TERRACOTTA);
        this.createTrivialCube(Blocks.CYAN_TERRACOTTA);
        this.createTrivialCube(Blocks.PURPLE_TERRACOTTA);
        this.createTrivialCube(Blocks.BLUE_TERRACOTTA);
        this.createTrivialCube(Blocks.BROWN_TERRACOTTA);
        this.createTrivialCube(Blocks.GREEN_TERRACOTTA);
        this.createTrivialCube(Blocks.RED_TERRACOTTA);
        this.createTrivialCube(Blocks.BLACK_TERRACOTTA);
        this.createTrivialCube(Blocks.TINTED_GLASS);
        this.createGlassBlocks(Blocks.GLASS, Blocks.GLASS_PANE);
        this.createGlassBlocks(Blocks.WHITE_STAINED_GLASS, Blocks.WHITE_STAINED_GLASS_PANE);
        this.createGlassBlocks(Blocks.ORANGE_STAINED_GLASS, Blocks.ORANGE_STAINED_GLASS_PANE);
        this.createGlassBlocks(Blocks.MAGENTA_STAINED_GLASS, Blocks.MAGENTA_STAINED_GLASS_PANE);
        this.createGlassBlocks(Blocks.LIGHT_BLUE_STAINED_GLASS, Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
        this.createGlassBlocks(Blocks.YELLOW_STAINED_GLASS, Blocks.YELLOW_STAINED_GLASS_PANE);
        this.createGlassBlocks(Blocks.LIME_STAINED_GLASS, Blocks.LIME_STAINED_GLASS_PANE);
        this.createGlassBlocks(Blocks.PINK_STAINED_GLASS, Blocks.PINK_STAINED_GLASS_PANE);
        this.createGlassBlocks(Blocks.GRAY_STAINED_GLASS, Blocks.GRAY_STAINED_GLASS_PANE);
        this.createGlassBlocks(Blocks.LIGHT_GRAY_STAINED_GLASS, Blocks.LIGHT_GRAY_STAINED_GLASS_PANE);
        this.createGlassBlocks(Blocks.CYAN_STAINED_GLASS, Blocks.CYAN_STAINED_GLASS_PANE);
        this.createGlassBlocks(Blocks.PURPLE_STAINED_GLASS, Blocks.PURPLE_STAINED_GLASS_PANE);
        this.createGlassBlocks(Blocks.BLUE_STAINED_GLASS, Blocks.BLUE_STAINED_GLASS_PANE);
        this.createGlassBlocks(Blocks.BROWN_STAINED_GLASS, Blocks.BROWN_STAINED_GLASS_PANE);
        this.createGlassBlocks(Blocks.GREEN_STAINED_GLASS, Blocks.GREEN_STAINED_GLASS_PANE);
        this.createGlassBlocks(Blocks.RED_STAINED_GLASS, Blocks.RED_STAINED_GLASS_PANE);
        this.createGlassBlocks(Blocks.BLACK_STAINED_GLASS, Blocks.BLACK_STAINED_GLASS_PANE);
        this.createColoredBlockWithStateRotations(TexturedModel.GLAZED_TERRACOTTA, Blocks.WHITE_GLAZED_TERRACOTTA, Blocks.ORANGE_GLAZED_TERRACOTTA, Blocks.MAGENTA_GLAZED_TERRACOTTA, Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, Blocks.YELLOW_GLAZED_TERRACOTTA, Blocks.LIME_GLAZED_TERRACOTTA, Blocks.PINK_GLAZED_TERRACOTTA, Blocks.GRAY_GLAZED_TERRACOTTA, Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, Blocks.CYAN_GLAZED_TERRACOTTA, Blocks.PURPLE_GLAZED_TERRACOTTA, Blocks.BLUE_GLAZED_TERRACOTTA, Blocks.BROWN_GLAZED_TERRACOTTA, Blocks.GREEN_GLAZED_TERRACOTTA, Blocks.RED_GLAZED_TERRACOTTA, Blocks.BLACK_GLAZED_TERRACOTTA);
        this.createFullAndCarpetBlocks(Blocks.WHITE_WOOL, Blocks.WHITE_CARPET);
        this.createFullAndCarpetBlocks(Blocks.ORANGE_WOOL, Blocks.ORANGE_CARPET);
        this.createFullAndCarpetBlocks(Blocks.MAGENTA_WOOL, Blocks.MAGENTA_CARPET);
        this.createFullAndCarpetBlocks(Blocks.LIGHT_BLUE_WOOL, Blocks.LIGHT_BLUE_CARPET);
        this.createFullAndCarpetBlocks(Blocks.YELLOW_WOOL, Blocks.YELLOW_CARPET);
        this.createFullAndCarpetBlocks(Blocks.LIME_WOOL, Blocks.LIME_CARPET);
        this.createFullAndCarpetBlocks(Blocks.PINK_WOOL, Blocks.PINK_CARPET);
        this.createFullAndCarpetBlocks(Blocks.GRAY_WOOL, Blocks.GRAY_CARPET);
        this.createFullAndCarpetBlocks(Blocks.LIGHT_GRAY_WOOL, Blocks.LIGHT_GRAY_CARPET);
        this.createFullAndCarpetBlocks(Blocks.CYAN_WOOL, Blocks.CYAN_CARPET);
        this.createFullAndCarpetBlocks(Blocks.PURPLE_WOOL, Blocks.PURPLE_CARPET);
        this.createFullAndCarpetBlocks(Blocks.BLUE_WOOL, Blocks.BLUE_CARPET);
        this.createFullAndCarpetBlocks(Blocks.BROWN_WOOL, Blocks.BROWN_CARPET);
        this.createFullAndCarpetBlocks(Blocks.GREEN_WOOL, Blocks.GREEN_CARPET);
        this.createFullAndCarpetBlocks(Blocks.RED_WOOL, Blocks.RED_CARPET);
        this.createFullAndCarpetBlocks(Blocks.BLACK_WOOL, Blocks.BLACK_CARPET);
        this.createTrivialCube(Blocks.MUD);
        this.createTrivialCube(Blocks.PACKED_MUD);
        this.createPlant(Blocks.FERN, Blocks.POTTED_FERN, TintState.TINTED);
        this.createPlant(Blocks.DANDELION, Blocks.POTTED_DANDELION, TintState.NOT_TINTED);
        this.createPlant(Blocks.POPPY, Blocks.POTTED_POPPY, TintState.NOT_TINTED);
        this.createPlant(Blocks.BLUE_ORCHID, Blocks.POTTED_BLUE_ORCHID, TintState.NOT_TINTED);
        this.createPlant(Blocks.ALLIUM, Blocks.POTTED_ALLIUM, TintState.NOT_TINTED);
        this.createPlant(Blocks.AZURE_BLUET, Blocks.POTTED_AZURE_BLUET, TintState.NOT_TINTED);
        this.createPlant(Blocks.RED_TULIP, Blocks.POTTED_RED_TULIP, TintState.NOT_TINTED);
        this.createPlant(Blocks.ORANGE_TULIP, Blocks.POTTED_ORANGE_TULIP, TintState.NOT_TINTED);
        this.createPlant(Blocks.WHITE_TULIP, Blocks.POTTED_WHITE_TULIP, TintState.NOT_TINTED);
        this.createPlant(Blocks.PINK_TULIP, Blocks.POTTED_PINK_TULIP, TintState.NOT_TINTED);
        this.createPlant(Blocks.OXEYE_DAISY, Blocks.POTTED_OXEYE_DAISY, TintState.NOT_TINTED);
        this.createPlant(Blocks.CORNFLOWER, Blocks.POTTED_CORNFLOWER, TintState.NOT_TINTED);
        this.createPlant(Blocks.LILY_OF_THE_VALLEY, Blocks.POTTED_LILY_OF_THE_VALLEY, TintState.NOT_TINTED);
        this.createPlant(Blocks.WITHER_ROSE, Blocks.POTTED_WITHER_ROSE, TintState.NOT_TINTED);
        this.createPlant(Blocks.RED_MUSHROOM, Blocks.POTTED_RED_MUSHROOM, TintState.NOT_TINTED);
        this.createPlant(Blocks.BROWN_MUSHROOM, Blocks.POTTED_BROWN_MUSHROOM, TintState.NOT_TINTED);
        this.createPlant(Blocks.DEAD_BUSH, Blocks.POTTED_DEAD_BUSH, TintState.NOT_TINTED);
        this.createPointedDripstone();
        this.createMushroomBlock(Blocks.BROWN_MUSHROOM_BLOCK);
        this.createMushroomBlock(Blocks.RED_MUSHROOM_BLOCK);
        this.createMushroomBlock(Blocks.MUSHROOM_STEM);
        this.createCrossBlockWithDefaultItem(Blocks.GRASS, TintState.TINTED);
        this.createCrossBlock(Blocks.SUGAR_CANE, TintState.TINTED);
        this.createSimpleFlatItemModel(Items.SUGAR_CANE);
        this.createGrowingPlant(Blocks.KELP, Blocks.KELP_PLANT, TintState.TINTED);
        this.createSimpleFlatItemModel(Items.KELP);
        this.skipAutoItemBlock(Blocks.KELP_PLANT);
        this.createCrossBlock(Blocks.HANGING_ROOTS, TintState.NOT_TINTED);
        this.skipAutoItemBlock(Blocks.HANGING_ROOTS);
        this.skipAutoItemBlock(Blocks.CAVE_VINES_PLANT);
        this.createGrowingPlant(Blocks.WEEPING_VINES, Blocks.WEEPING_VINES_PLANT, TintState.NOT_TINTED);
        this.createGrowingPlant(Blocks.TWISTING_VINES, Blocks.TWISTING_VINES_PLANT, TintState.NOT_TINTED);
        this.createSimpleFlatItemModel(Blocks.WEEPING_VINES, "_plant");
        this.skipAutoItemBlock(Blocks.WEEPING_VINES_PLANT);
        this.createSimpleFlatItemModel(Blocks.TWISTING_VINES, "_plant");
        this.skipAutoItemBlock(Blocks.TWISTING_VINES_PLANT);
        this.createCrossBlockWithDefaultItem(Blocks.BAMBOO_SAPLING, TintState.TINTED, TextureMapping.cross(TextureMapping.getBlockTexture(Blocks.BAMBOO, "_stage0")));
        this.createBamboo();
        this.createCrossBlockWithDefaultItem(Blocks.COBWEB, TintState.NOT_TINTED);
        this.createDoublePlant(Blocks.LILAC, TintState.NOT_TINTED);
        this.createDoublePlant(Blocks.ROSE_BUSH, TintState.NOT_TINTED);
        this.createDoublePlant(Blocks.PEONY, TintState.NOT_TINTED);
        this.createDoublePlant(Blocks.TALL_GRASS, TintState.TINTED);
        this.createDoublePlant(Blocks.LARGE_FERN, TintState.TINTED);
        this.createSunflower();
        this.createTallSeagrass();
        this.createSmallDripleaf();
        this.createCoral(Blocks.TUBE_CORAL, Blocks.DEAD_TUBE_CORAL, Blocks.TUBE_CORAL_BLOCK, Blocks.DEAD_TUBE_CORAL_BLOCK, Blocks.TUBE_CORAL_FAN, Blocks.DEAD_TUBE_CORAL_FAN, Blocks.TUBE_CORAL_WALL_FAN, Blocks.DEAD_TUBE_CORAL_WALL_FAN);
        this.createCoral(Blocks.BRAIN_CORAL, Blocks.DEAD_BRAIN_CORAL, Blocks.BRAIN_CORAL_BLOCK, Blocks.DEAD_BRAIN_CORAL_BLOCK, Blocks.BRAIN_CORAL_FAN, Blocks.DEAD_BRAIN_CORAL_FAN, Blocks.BRAIN_CORAL_WALL_FAN, Blocks.DEAD_BRAIN_CORAL_WALL_FAN);
        this.createCoral(Blocks.BUBBLE_CORAL, Blocks.DEAD_BUBBLE_CORAL, Blocks.BUBBLE_CORAL_BLOCK, Blocks.DEAD_BUBBLE_CORAL_BLOCK, Blocks.BUBBLE_CORAL_FAN, Blocks.DEAD_BUBBLE_CORAL_FAN, Blocks.BUBBLE_CORAL_WALL_FAN, Blocks.DEAD_BUBBLE_CORAL_WALL_FAN);
        this.createCoral(Blocks.FIRE_CORAL, Blocks.DEAD_FIRE_CORAL, Blocks.FIRE_CORAL_BLOCK, Blocks.DEAD_FIRE_CORAL_BLOCK, Blocks.FIRE_CORAL_FAN, Blocks.DEAD_FIRE_CORAL_FAN, Blocks.FIRE_CORAL_WALL_FAN, Blocks.DEAD_FIRE_CORAL_WALL_FAN);
        this.createCoral(Blocks.HORN_CORAL, Blocks.DEAD_HORN_CORAL, Blocks.HORN_CORAL_BLOCK, Blocks.DEAD_HORN_CORAL_BLOCK, Blocks.HORN_CORAL_FAN, Blocks.DEAD_HORN_CORAL_FAN, Blocks.HORN_CORAL_WALL_FAN, Blocks.DEAD_HORN_CORAL_WALL_FAN);
        this.createStems(Blocks.MELON_STEM, Blocks.ATTACHED_MELON_STEM);
        this.createStems(Blocks.PUMPKIN_STEM, Blocks.ATTACHED_PUMPKIN_STEM);
        this.woodProvider(Blocks.MANGROVE_LOG).logWithHorizontal(Blocks.MANGROVE_LOG).wood(Blocks.MANGROVE_WOOD);
        this.woodProvider(Blocks.STRIPPED_MANGROVE_LOG).logWithHorizontal(Blocks.STRIPPED_MANGROVE_LOG).wood(Blocks.STRIPPED_MANGROVE_WOOD);
        this.createHangingSign(Blocks.STRIPPED_MANGROVE_LOG, Blocks.MANGROVE_HANGING_SIGN, Blocks.MANGROVE_WALL_HANGING_SIGN);
        this.createTrivialBlock(Blocks.MANGROVE_LEAVES, TexturedModel.LEAVES);
        this.woodProvider(Blocks.ACACIA_LOG).logWithHorizontal(Blocks.ACACIA_LOG).wood(Blocks.ACACIA_WOOD);
        this.woodProvider(Blocks.STRIPPED_ACACIA_LOG).logWithHorizontal(Blocks.STRIPPED_ACACIA_LOG).wood(Blocks.STRIPPED_ACACIA_WOOD);
        this.createHangingSign(Blocks.STRIPPED_ACACIA_LOG, Blocks.ACACIA_HANGING_SIGN, Blocks.ACACIA_WALL_HANGING_SIGN);
        this.createPlant(Blocks.ACACIA_SAPLING, Blocks.POTTED_ACACIA_SAPLING, TintState.NOT_TINTED);
        this.createTrivialBlock(Blocks.ACACIA_LEAVES, TexturedModel.LEAVES);
        this.woodProvider(Blocks.BIRCH_LOG).logWithHorizontal(Blocks.BIRCH_LOG).wood(Blocks.BIRCH_WOOD);
        this.woodProvider(Blocks.STRIPPED_BIRCH_LOG).logWithHorizontal(Blocks.STRIPPED_BIRCH_LOG).wood(Blocks.STRIPPED_BIRCH_WOOD);
        this.createHangingSign(Blocks.STRIPPED_BIRCH_LOG, Blocks.BIRCH_HANGING_SIGN, Blocks.BIRCH_WALL_HANGING_SIGN);
        this.createPlant(Blocks.BIRCH_SAPLING, Blocks.POTTED_BIRCH_SAPLING, TintState.NOT_TINTED);
        this.createTrivialBlock(Blocks.BIRCH_LEAVES, TexturedModel.LEAVES);
        this.woodProvider(Blocks.OAK_LOG).logWithHorizontal(Blocks.OAK_LOG).wood(Blocks.OAK_WOOD);
        this.woodProvider(Blocks.STRIPPED_OAK_LOG).logWithHorizontal(Blocks.STRIPPED_OAK_LOG).wood(Blocks.STRIPPED_OAK_WOOD);
        this.createHangingSign(Blocks.STRIPPED_OAK_LOG, Blocks.OAK_HANGING_SIGN, Blocks.OAK_WALL_HANGING_SIGN);
        this.createPlant(Blocks.OAK_SAPLING, Blocks.POTTED_OAK_SAPLING, TintState.NOT_TINTED);
        this.createTrivialBlock(Blocks.OAK_LEAVES, TexturedModel.LEAVES);
        this.woodProvider(Blocks.SPRUCE_LOG).logWithHorizontal(Blocks.SPRUCE_LOG).wood(Blocks.SPRUCE_WOOD);
        this.woodProvider(Blocks.STRIPPED_SPRUCE_LOG).logWithHorizontal(Blocks.STRIPPED_SPRUCE_LOG).wood(Blocks.STRIPPED_SPRUCE_WOOD);
        this.createHangingSign(Blocks.STRIPPED_SPRUCE_LOG, Blocks.SPRUCE_HANGING_SIGN, Blocks.SPRUCE_WALL_HANGING_SIGN);
        this.createPlant(Blocks.SPRUCE_SAPLING, Blocks.POTTED_SPRUCE_SAPLING, TintState.NOT_TINTED);
        this.createTrivialBlock(Blocks.SPRUCE_LEAVES, TexturedModel.LEAVES);
        this.woodProvider(Blocks.DARK_OAK_LOG).logWithHorizontal(Blocks.DARK_OAK_LOG).wood(Blocks.DARK_OAK_WOOD);
        this.woodProvider(Blocks.STRIPPED_DARK_OAK_LOG).logWithHorizontal(Blocks.STRIPPED_DARK_OAK_LOG).wood(Blocks.STRIPPED_DARK_OAK_WOOD);
        this.createHangingSign(Blocks.STRIPPED_DARK_OAK_LOG, Blocks.DARK_OAK_HANGING_SIGN, Blocks.DARK_OAK_WALL_HANGING_SIGN);
        this.createPlant(Blocks.DARK_OAK_SAPLING, Blocks.POTTED_DARK_OAK_SAPLING, TintState.NOT_TINTED);
        this.createTrivialBlock(Blocks.DARK_OAK_LEAVES, TexturedModel.LEAVES);
        this.woodProvider(Blocks.JUNGLE_LOG).logWithHorizontal(Blocks.JUNGLE_LOG).wood(Blocks.JUNGLE_WOOD);
        this.woodProvider(Blocks.STRIPPED_JUNGLE_LOG).logWithHorizontal(Blocks.STRIPPED_JUNGLE_LOG).wood(Blocks.STRIPPED_JUNGLE_WOOD);
        this.createHangingSign(Blocks.STRIPPED_JUNGLE_LOG, Blocks.JUNGLE_HANGING_SIGN, Blocks.JUNGLE_WALL_HANGING_SIGN);
        this.createPlant(Blocks.JUNGLE_SAPLING, Blocks.POTTED_JUNGLE_SAPLING, TintState.NOT_TINTED);
        this.createTrivialBlock(Blocks.JUNGLE_LEAVES, TexturedModel.LEAVES);
        this.woodProvider(Blocks.CRIMSON_STEM).log(Blocks.CRIMSON_STEM).wood(Blocks.CRIMSON_HYPHAE);
        this.woodProvider(Blocks.STRIPPED_CRIMSON_STEM).log(Blocks.STRIPPED_CRIMSON_STEM).wood(Blocks.STRIPPED_CRIMSON_HYPHAE);
        this.createHangingSign(Blocks.STRIPPED_CRIMSON_STEM, Blocks.CRIMSON_HANGING_SIGN, Blocks.CRIMSON_WALL_HANGING_SIGN);
        this.createPlant(Blocks.CRIMSON_FUNGUS, Blocks.POTTED_CRIMSON_FUNGUS, TintState.NOT_TINTED);
        this.createNetherRoots(Blocks.CRIMSON_ROOTS, Blocks.POTTED_CRIMSON_ROOTS);
        this.woodProvider(Blocks.WARPED_STEM).log(Blocks.WARPED_STEM).wood(Blocks.WARPED_HYPHAE);
        this.woodProvider(Blocks.STRIPPED_WARPED_STEM).log(Blocks.STRIPPED_WARPED_STEM).wood(Blocks.STRIPPED_WARPED_HYPHAE);
        this.createHangingSign(Blocks.STRIPPED_WARPED_STEM, Blocks.WARPED_HANGING_SIGN, Blocks.WARPED_WALL_HANGING_SIGN);
        this.createPlant(Blocks.WARPED_FUNGUS, Blocks.POTTED_WARPED_FUNGUS, TintState.NOT_TINTED);
        this.createNetherRoots(Blocks.WARPED_ROOTS, Blocks.POTTED_WARPED_ROOTS);
        this.woodProvider(Blocks.BAMBOO_BLOCK).logUVLocked(Blocks.BAMBOO_BLOCK);
        this.woodProvider(Blocks.STRIPPED_BAMBOO_BLOCK).logUVLocked(Blocks.STRIPPED_BAMBOO_BLOCK);
        this.createHangingSign(Blocks.BAMBOO_PLANKS, Blocks.BAMBOO_HANGING_SIGN, Blocks.BAMBOO_WALL_HANGING_SIGN);
        this.createCrossBlock(Blocks.NETHER_SPROUTS, TintState.NOT_TINTED);
        this.createSimpleFlatItemModel(Items.NETHER_SPROUTS);
        this.createDoor(Blocks.IRON_DOOR);
        this.createTrapdoor(Blocks.IRON_TRAPDOOR);
        this.createSmoothStoneSlab();
        this.createPassiveRail(Blocks.RAIL);
        this.createActiveRail(Blocks.POWERED_RAIL);
        this.createActiveRail(Blocks.DETECTOR_RAIL);
        this.createActiveRail(Blocks.ACTIVATOR_RAIL);
        this.createComparator();
        this.createCommandBlock(Blocks.COMMAND_BLOCK);
        this.createCommandBlock(Blocks.REPEATING_COMMAND_BLOCK);
        this.createCommandBlock(Blocks.CHAIN_COMMAND_BLOCK);
        this.createAnvil(Blocks.ANVIL);
        this.createAnvil(Blocks.CHIPPED_ANVIL);
        this.createAnvil(Blocks.DAMAGED_ANVIL);
        this.createBarrel();
        this.createBell();
        this.createFurnace(Blocks.FURNACE, TexturedModel.ORIENTABLE_ONLY_TOP);
        this.createFurnace(Blocks.BLAST_FURNACE, TexturedModel.ORIENTABLE_ONLY_TOP);
        this.createFurnace(Blocks.SMOKER, TexturedModel.ORIENTABLE);
        this.createRedstoneWire();
        this.createRespawnAnchor();
        this.createSculkCatalyst();
        this.copyModel(Blocks.CHISELED_STONE_BRICKS, Blocks.INFESTED_CHISELED_STONE_BRICKS);
        this.copyModel(Blocks.COBBLESTONE, Blocks.INFESTED_COBBLESTONE);
        this.copyModel(Blocks.CRACKED_STONE_BRICKS, Blocks.INFESTED_CRACKED_STONE_BRICKS);
        this.copyModel(Blocks.MOSSY_STONE_BRICKS, Blocks.INFESTED_MOSSY_STONE_BRICKS);
        this.createInfestedStone();
        this.copyModel(Blocks.STONE_BRICKS, Blocks.INFESTED_STONE_BRICKS);
        this.createInfestedDeepslate();
        SpawnEggItem.eggs().forEach($$0 -> this.delegateItemModel((Item)$$0, ModelLocationUtils.decorateItemModelLocation("template_spawn_egg")));
    }

    private void createLightBlock() {
        this.skipAutoItemBlock(Blocks.LIGHT);
        PropertyDispatch.C1<Integer> $$0 = PropertyDispatch.property(BlockStateProperties.LEVEL);
        for (int $$1 = 0; $$1 < 16; ++$$1) {
            String $$2 = String.format((Locale)Locale.ROOT, (String)"_%02d", (Object[])new Object[]{$$1});
            ResourceLocation $$3 = TextureMapping.getItemTexture(Items.LIGHT, $$2);
            $$0.select((Integer)$$1, Variant.variant().with(VariantProperties.MODEL, ModelTemplates.PARTICLE_ONLY.createWithSuffix(Blocks.LIGHT, $$2, TextureMapping.particle($$3), this.modelOutput)));
            ModelTemplates.FLAT_ITEM.create(ModelLocationUtils.getModelLocation(Items.LIGHT, $$2), TextureMapping.layer0($$3), this.modelOutput);
        }
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant(Blocks.LIGHT).with($$0));
    }

    private void createCandleAndCandleCake(Block $$0, Block $$1) {
        this.createSimpleFlatItemModel($$0.asItem());
        TextureMapping $$2 = TextureMapping.cube(TextureMapping.getBlockTexture($$0));
        TextureMapping $$3 = TextureMapping.cube(TextureMapping.getBlockTexture($$0, "_lit"));
        ResourceLocation $$4 = ModelTemplates.CANDLE.createWithSuffix($$0, "_one_candle", $$2, this.modelOutput);
        ResourceLocation $$5 = ModelTemplates.TWO_CANDLES.createWithSuffix($$0, "_two_candles", $$2, this.modelOutput);
        ResourceLocation $$6 = ModelTemplates.THREE_CANDLES.createWithSuffix($$0, "_three_candles", $$2, this.modelOutput);
        ResourceLocation $$7 = ModelTemplates.FOUR_CANDLES.createWithSuffix($$0, "_four_candles", $$2, this.modelOutput);
        ResourceLocation $$8 = ModelTemplates.CANDLE.createWithSuffix($$0, "_one_candle_lit", $$3, this.modelOutput);
        ResourceLocation $$9 = ModelTemplates.TWO_CANDLES.createWithSuffix($$0, "_two_candles_lit", $$3, this.modelOutput);
        ResourceLocation $$10 = ModelTemplates.THREE_CANDLES.createWithSuffix($$0, "_three_candles_lit", $$3, this.modelOutput);
        ResourceLocation $$11 = ModelTemplates.FOUR_CANDLES.createWithSuffix($$0, "_four_candles_lit", $$3, this.modelOutput);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant($$0).with(PropertyDispatch.properties(BlockStateProperties.CANDLES, BlockStateProperties.LIT).select((Integer)1, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$4)).select((Integer)2, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$5)).select((Integer)3, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$6)).select((Integer)4, (Boolean)false, Variant.variant().with(VariantProperties.MODEL, $$7)).select((Integer)1, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$8)).select((Integer)2, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$9)).select((Integer)3, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$10)).select((Integer)4, (Boolean)true, Variant.variant().with(VariantProperties.MODEL, $$11))));
        ResourceLocation $$12 = ModelTemplates.CANDLE_CAKE.create($$1, TextureMapping.candleCake($$0, false), this.modelOutput);
        ResourceLocation $$13 = ModelTemplates.CANDLE_CAKE.createWithSuffix($$1, "_lit", TextureMapping.candleCake($$0, true), this.modelOutput);
        this.blockStateOutput.accept((Object)MultiVariantGenerator.multiVariant($$1).with(BlockModelGenerators.createBooleanModelDispatch(BlockStateProperties.LIT, $$13, $$12)));
    }

    private /* synthetic */ Variant lambda$createCropBlock$12(int[] $$0, Int2ObjectMap $$1, Block $$22, Integer $$3) {
        int $$4 = $$0[$$3];
        ResourceLocation $$5 = (ResourceLocation)$$1.computeIfAbsent($$4, $$2 -> this.createSuffixedVariant($$22, "_stage" + $$4, ModelTemplates.CROP, (Function<ResourceLocation, TextureMapping>)((Function)TextureMapping::crop)));
        return Variant.variant().with(VariantProperties.MODEL, $$5);
    }

    @FunctionalInterface
    static interface BlockStateGeneratorSupplier {
        public BlockStateGenerator create(Block var1, ResourceLocation var2, TextureMapping var3, BiConsumer<ResourceLocation, Supplier<JsonElement>> var4);
    }

    class BlockFamilyProvider {
        private final TextureMapping mapping;
        private final Map<ModelTemplate, ResourceLocation> models = Maps.newHashMap();
        @Nullable
        private BlockFamily family;
        @Nullable
        private ResourceLocation fullBlock;

        public BlockFamilyProvider(TextureMapping $$0) {
            this.mapping = $$0;
        }

        public BlockFamilyProvider fullBlock(Block $$0, ModelTemplate $$1) {
            this.fullBlock = $$1.create($$0, this.mapping, BlockModelGenerators.this.modelOutput);
            if (BlockModelGenerators.this.fullBlockModelCustomGenerators.containsKey((Object)$$0)) {
                BlockModelGenerators.this.blockStateOutput.accept((Object)((BlockStateGeneratorSupplier)BlockModelGenerators.this.fullBlockModelCustomGenerators.get((Object)$$0)).create($$0, this.fullBlock, this.mapping, BlockModelGenerators.this.modelOutput));
            } else {
                BlockModelGenerators.this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock($$0, this.fullBlock));
            }
            return this;
        }

        public BlockFamilyProvider fullBlockCopies(Block ... $$0) {
            if (this.fullBlock == null) {
                throw new IllegalStateException("Full block not generated yet");
            }
            for (Block $$1 : $$0) {
                BlockModelGenerators.this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock($$1, this.fullBlock));
                BlockModelGenerators.this.delegateItemModel($$1, this.fullBlock);
            }
            return this;
        }

        public BlockFamilyProvider button(Block $$0) {
            ResourceLocation $$1 = ModelTemplates.BUTTON.create($$0, this.mapping, BlockModelGenerators.this.modelOutput);
            ResourceLocation $$2 = ModelTemplates.BUTTON_PRESSED.create($$0, this.mapping, BlockModelGenerators.this.modelOutput);
            BlockModelGenerators.this.blockStateOutput.accept((Object)BlockModelGenerators.createButton($$0, $$1, $$2));
            ResourceLocation $$3 = ModelTemplates.BUTTON_INVENTORY.create($$0, this.mapping, BlockModelGenerators.this.modelOutput);
            BlockModelGenerators.this.delegateItemModel($$0, $$3);
            return this;
        }

        public BlockFamilyProvider wall(Block $$0) {
            ResourceLocation $$1 = ModelTemplates.WALL_POST.create($$0, this.mapping, BlockModelGenerators.this.modelOutput);
            ResourceLocation $$2 = ModelTemplates.WALL_LOW_SIDE.create($$0, this.mapping, BlockModelGenerators.this.modelOutput);
            ResourceLocation $$3 = ModelTemplates.WALL_TALL_SIDE.create($$0, this.mapping, BlockModelGenerators.this.modelOutput);
            BlockModelGenerators.this.blockStateOutput.accept((Object)BlockModelGenerators.createWall($$0, $$1, $$2, $$3));
            ResourceLocation $$4 = ModelTemplates.WALL_INVENTORY.create($$0, this.mapping, BlockModelGenerators.this.modelOutput);
            BlockModelGenerators.this.delegateItemModel($$0, $$4);
            return this;
        }

        public BlockFamilyProvider customFence(Block $$0) {
            TextureMapping $$1 = TextureMapping.customParticle($$0);
            ResourceLocation $$2 = ModelTemplates.CUSTOM_FENCE_POST.create($$0, $$1, BlockModelGenerators.this.modelOutput);
            ResourceLocation $$3 = ModelTemplates.CUSTOM_FENCE_SIDE_NORTH.create($$0, $$1, BlockModelGenerators.this.modelOutput);
            ResourceLocation $$4 = ModelTemplates.CUSTOM_FENCE_SIDE_EAST.create($$0, $$1, BlockModelGenerators.this.modelOutput);
            ResourceLocation $$5 = ModelTemplates.CUSTOM_FENCE_SIDE_SOUTH.create($$0, $$1, BlockModelGenerators.this.modelOutput);
            ResourceLocation $$6 = ModelTemplates.CUSTOM_FENCE_SIDE_WEST.create($$0, $$1, BlockModelGenerators.this.modelOutput);
            BlockModelGenerators.this.blockStateOutput.accept((Object)BlockModelGenerators.createCustomFence($$0, $$2, $$3, $$4, $$5, $$6));
            ResourceLocation $$7 = ModelTemplates.CUSTOM_FENCE_INVENTORY.create($$0, $$1, BlockModelGenerators.this.modelOutput);
            BlockModelGenerators.this.delegateItemModel($$0, $$7);
            return this;
        }

        public BlockFamilyProvider fence(Block $$0) {
            ResourceLocation $$1 = ModelTemplates.FENCE_POST.create($$0, this.mapping, BlockModelGenerators.this.modelOutput);
            ResourceLocation $$2 = ModelTemplates.FENCE_SIDE.create($$0, this.mapping, BlockModelGenerators.this.modelOutput);
            BlockModelGenerators.this.blockStateOutput.accept((Object)BlockModelGenerators.createFence($$0, $$1, $$2));
            ResourceLocation $$3 = ModelTemplates.FENCE_INVENTORY.create($$0, this.mapping, BlockModelGenerators.this.modelOutput);
            BlockModelGenerators.this.delegateItemModel($$0, $$3);
            return this;
        }

        public BlockFamilyProvider customFenceGate(Block $$0) {
            TextureMapping $$1 = TextureMapping.customParticle($$0);
            ResourceLocation $$2 = ModelTemplates.CUSTOM_FENCE_GATE_OPEN.create($$0, $$1, BlockModelGenerators.this.modelOutput);
            ResourceLocation $$3 = ModelTemplates.CUSTOM_FENCE_GATE_CLOSED.create($$0, $$1, BlockModelGenerators.this.modelOutput);
            ResourceLocation $$4 = ModelTemplates.CUSTOM_FENCE_GATE_WALL_OPEN.create($$0, $$1, BlockModelGenerators.this.modelOutput);
            ResourceLocation $$5 = ModelTemplates.CUSTOM_FENCE_GATE_WALL_CLOSED.create($$0, $$1, BlockModelGenerators.this.modelOutput);
            BlockModelGenerators.this.blockStateOutput.accept((Object)BlockModelGenerators.createFenceGate($$0, $$2, $$3, $$4, $$5, false));
            return this;
        }

        public BlockFamilyProvider fenceGate(Block $$0) {
            ResourceLocation $$1 = ModelTemplates.FENCE_GATE_OPEN.create($$0, this.mapping, BlockModelGenerators.this.modelOutput);
            ResourceLocation $$2 = ModelTemplates.FENCE_GATE_CLOSED.create($$0, this.mapping, BlockModelGenerators.this.modelOutput);
            ResourceLocation $$3 = ModelTemplates.FENCE_GATE_WALL_OPEN.create($$0, this.mapping, BlockModelGenerators.this.modelOutput);
            ResourceLocation $$4 = ModelTemplates.FENCE_GATE_WALL_CLOSED.create($$0, this.mapping, BlockModelGenerators.this.modelOutput);
            BlockModelGenerators.this.blockStateOutput.accept((Object)BlockModelGenerators.createFenceGate($$0, $$1, $$2, $$3, $$4, true));
            return this;
        }

        public BlockFamilyProvider pressurePlate(Block $$0) {
            ResourceLocation $$1 = ModelTemplates.PRESSURE_PLATE_UP.create($$0, this.mapping, BlockModelGenerators.this.modelOutput);
            ResourceLocation $$2 = ModelTemplates.PRESSURE_PLATE_DOWN.create($$0, this.mapping, BlockModelGenerators.this.modelOutput);
            BlockModelGenerators.this.blockStateOutput.accept((Object)BlockModelGenerators.createPressurePlate($$0, $$1, $$2));
            return this;
        }

        public BlockFamilyProvider sign(Block $$0) {
            if (this.family == null) {
                throw new IllegalStateException("Family not defined");
            }
            Block $$1 = (Block)this.family.getVariants().get((Object)BlockFamily.Variant.WALL_SIGN);
            ResourceLocation $$2 = ModelTemplates.PARTICLE_ONLY.create($$0, this.mapping, BlockModelGenerators.this.modelOutput);
            BlockModelGenerators.this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock($$0, $$2));
            BlockModelGenerators.this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock($$1, $$2));
            BlockModelGenerators.this.createSimpleFlatItemModel($$0.asItem());
            BlockModelGenerators.this.skipAutoItemBlock($$1);
            return this;
        }

        public BlockFamilyProvider slab(Block $$0) {
            if (this.fullBlock == null) {
                throw new IllegalStateException("Full block not generated yet");
            }
            ResourceLocation $$1 = this.getOrCreateModel(ModelTemplates.SLAB_BOTTOM, $$0);
            ResourceLocation $$2 = this.getOrCreateModel(ModelTemplates.SLAB_TOP, $$0);
            BlockModelGenerators.this.blockStateOutput.accept((Object)BlockModelGenerators.createSlab($$0, $$1, $$2, this.fullBlock));
            BlockModelGenerators.this.delegateItemModel($$0, $$1);
            return this;
        }

        public BlockFamilyProvider stairs(Block $$0) {
            ResourceLocation $$1 = this.getOrCreateModel(ModelTemplates.STAIRS_INNER, $$0);
            ResourceLocation $$2 = this.getOrCreateModel(ModelTemplates.STAIRS_STRAIGHT, $$0);
            ResourceLocation $$3 = this.getOrCreateModel(ModelTemplates.STAIRS_OUTER, $$0);
            BlockModelGenerators.this.blockStateOutput.accept((Object)BlockModelGenerators.createStairs($$0, $$1, $$2, $$3));
            BlockModelGenerators.this.delegateItemModel($$0, $$2);
            return this;
        }

        private BlockFamilyProvider fullBlockVariant(Block $$0) {
            TexturedModel $$1 = (TexturedModel)BlockModelGenerators.this.texturedModels.getOrDefault((Object)$$0, (Object)TexturedModel.CUBE.get($$0));
            BlockModelGenerators.this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock($$0, $$1.create($$0, BlockModelGenerators.this.modelOutput)));
            return this;
        }

        private BlockFamilyProvider door(Block $$0) {
            BlockModelGenerators.this.createDoor($$0);
            return this;
        }

        private void trapdoor(Block $$0) {
            if (BlockModelGenerators.this.nonOrientableTrapdoor.contains((Object)$$0)) {
                BlockModelGenerators.this.createTrapdoor($$0);
            } else {
                BlockModelGenerators.this.createOrientableTrapdoor($$0);
            }
        }

        private ResourceLocation getOrCreateModel(ModelTemplate $$0, Block $$12) {
            return (ResourceLocation)this.models.computeIfAbsent((Object)$$0, $$1 -> $$1.create($$12, this.mapping, BlockModelGenerators.this.modelOutput));
        }

        public BlockFamilyProvider generateFor(BlockFamily $$02) {
            this.family = $$02;
            $$02.getVariants().forEach(($$0, $$1) -> {
                BiConsumer $$2 = (BiConsumer)SHAPE_CONSUMERS.get((Object)$$0);
                if ($$2 != null) {
                    $$2.accept((Object)this, $$1);
                }
            });
            return this;
        }
    }

    class WoodProvider {
        private final TextureMapping logMapping;

        public WoodProvider(TextureMapping $$0) {
            this.logMapping = $$0;
        }

        public WoodProvider wood(Block $$0) {
            TextureMapping $$1 = this.logMapping.copyAndUpdate(TextureSlot.END, this.logMapping.get(TextureSlot.SIDE));
            ResourceLocation $$2 = ModelTemplates.CUBE_COLUMN.create($$0, $$1, BlockModelGenerators.this.modelOutput);
            BlockModelGenerators.this.blockStateOutput.accept((Object)BlockModelGenerators.createAxisAlignedPillarBlock($$0, $$2));
            return this;
        }

        public WoodProvider log(Block $$0) {
            ResourceLocation $$1 = ModelTemplates.CUBE_COLUMN.create($$0, this.logMapping, BlockModelGenerators.this.modelOutput);
            BlockModelGenerators.this.blockStateOutput.accept((Object)BlockModelGenerators.createAxisAlignedPillarBlock($$0, $$1));
            return this;
        }

        public WoodProvider logWithHorizontal(Block $$0) {
            ResourceLocation $$1 = ModelTemplates.CUBE_COLUMN.create($$0, this.logMapping, BlockModelGenerators.this.modelOutput);
            ResourceLocation $$2 = ModelTemplates.CUBE_COLUMN_HORIZONTAL.create($$0, this.logMapping, BlockModelGenerators.this.modelOutput);
            BlockModelGenerators.this.blockStateOutput.accept((Object)BlockModelGenerators.createRotatedPillarWithHorizontalVariant($$0, $$1, $$2));
            return this;
        }

        public WoodProvider logUVLocked(Block $$0) {
            BlockModelGenerators.this.blockStateOutput.accept((Object)BlockModelGenerators.createPillarBlockUVLocked($$0, this.logMapping, BlockModelGenerators.this.modelOutput));
            return this;
        }
    }

    static enum TintState {
        TINTED,
        NOT_TINTED;


        public ModelTemplate getCross() {
            return this == TINTED ? ModelTemplates.TINTED_CROSS : ModelTemplates.CROSS;
        }

        public ModelTemplate getCrossPot() {
            return this == TINTED ? ModelTemplates.TINTED_FLOWER_POT_CROSS : ModelTemplates.FLOWER_POT_CROSS;
        }
    }

    class BlockEntityModelGenerator {
        private final ResourceLocation baseModel;

        public BlockEntityModelGenerator(ResourceLocation $$0, Block $$1) {
            this.baseModel = ModelTemplates.PARTICLE_ONLY.create($$0, TextureMapping.particle($$1), BlockModelGenerators.this.modelOutput);
        }

        public BlockEntityModelGenerator create(Block ... $$0) {
            for (Block $$1 : $$0) {
                BlockModelGenerators.this.blockStateOutput.accept((Object)BlockModelGenerators.createSimpleBlock($$1, this.baseModel));
            }
            return this;
        }

        public BlockEntityModelGenerator createWithoutBlockItem(Block ... $$0) {
            for (Block $$1 : $$0) {
                BlockModelGenerators.this.skipAutoItemBlock($$1);
            }
            return this.create($$0);
        }

        public BlockEntityModelGenerator createWithCustomBlockItemModel(ModelTemplate $$0, Block ... $$1) {
            for (Block $$2 : $$1) {
                $$0.create(ModelLocationUtils.getModelLocation($$2.asItem()), TextureMapping.particle($$2), BlockModelGenerators.this.modelOutput);
            }
            return this.create($$1);
        }
    }

    record BookSlotModelCacheKey(ModelTemplate template, String modelSuffix) {
    }
}