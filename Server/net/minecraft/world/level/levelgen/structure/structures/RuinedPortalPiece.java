/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Float
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.function.Function
 *  java.util.function.Supplier
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.AlwaysTrueTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlackstoneReplaceProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockAgeProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.LavaSubmergedBlockProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorRule;
import net.minecraft.world.level.levelgen.structure.templatesystem.ProtectedBlockProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.RandomBlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.slf4j.Logger;

public class RuinedPortalPiece
extends TemplateStructurePiece {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final float PROBABILITY_OF_GOLD_GONE = 0.3f;
    private static final float PROBABILITY_OF_MAGMA_INSTEAD_OF_NETHERRACK = 0.07f;
    private static final float PROBABILITY_OF_MAGMA_INSTEAD_OF_LAVA = 0.2f;
    private final VerticalPlacement verticalPlacement;
    private final Properties properties;

    public RuinedPortalPiece(StructureTemplateManager $$0, BlockPos $$1, VerticalPlacement $$2, Properties $$3, ResourceLocation $$4, StructureTemplate $$5, Rotation $$6, Mirror $$7, BlockPos $$8) {
        super(StructurePieceType.RUINED_PORTAL, 0, $$0, $$4, $$4.toString(), RuinedPortalPiece.makeSettings($$7, $$6, $$2, $$8, $$3), $$1);
        this.verticalPlacement = $$2;
        this.properties = $$3;
    }

    public RuinedPortalPiece(StructureTemplateManager $$0, CompoundTag $$1) {
        super(StructurePieceType.RUINED_PORTAL, $$1, $$0, (Function<ResourceLocation, StructurePlaceSettings>)((Function)$$2 -> RuinedPortalPiece.makeSettings($$0, $$1, $$2)));
        this.verticalPlacement = VerticalPlacement.byName($$1.getString("VerticalPlacement"));
        this.properties = (Properties)Properties.CODEC.parse(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)$$1.get("Properties"))).getOrThrow(true, arg_0 -> ((Logger)LOGGER).error(arg_0));
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext $$0, CompoundTag $$12) {
        super.addAdditionalSaveData($$0, $$12);
        $$12.putString("Rotation", this.placeSettings.getRotation().name());
        $$12.putString("Mirror", this.placeSettings.getMirror().name());
        $$12.putString("VerticalPlacement", this.verticalPlacement.getName());
        Properties.CODEC.encodeStart((DynamicOps)NbtOps.INSTANCE, (Object)this.properties).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).ifPresent($$1 -> $$12.put("Properties", (Tag)$$1));
    }

    private static StructurePlaceSettings makeSettings(StructureTemplateManager $$0, CompoundTag $$1, ResourceLocation $$2) {
        StructureTemplate $$3 = $$0.getOrCreate($$2);
        BlockPos $$4 = new BlockPos($$3.getSize().getX() / 2, 0, $$3.getSize().getZ() / 2);
        return RuinedPortalPiece.makeSettings(Mirror.valueOf($$1.getString("Mirror")), Rotation.valueOf($$1.getString("Rotation")), VerticalPlacement.byName($$1.getString("VerticalPlacement")), $$4, (Properties)Properties.CODEC.parse(new Dynamic((DynamicOps)NbtOps.INSTANCE, (Object)$$1.get("Properties"))).getOrThrow(true, arg_0 -> ((Logger)LOGGER).error(arg_0)));
    }

    private static StructurePlaceSettings makeSettings(Mirror $$0, Rotation $$1, VerticalPlacement $$2, BlockPos $$3, Properties $$4) {
        BlockIgnoreProcessor $$5 = $$4.airPocket ? BlockIgnoreProcessor.STRUCTURE_BLOCK : BlockIgnoreProcessor.STRUCTURE_AND_AIR;
        ArrayList $$6 = Lists.newArrayList();
        $$6.add((Object)RuinedPortalPiece.getBlockReplaceRule(Blocks.GOLD_BLOCK, 0.3f, Blocks.AIR));
        $$6.add((Object)RuinedPortalPiece.getLavaProcessorRule($$2, $$4));
        if (!$$4.cold) {
            $$6.add((Object)RuinedPortalPiece.getBlockReplaceRule(Blocks.NETHERRACK, 0.07f, Blocks.MAGMA_BLOCK));
        }
        StructurePlaceSettings $$7 = new StructurePlaceSettings().setRotation($$1).setMirror($$0).setRotationPivot($$3).addProcessor($$5).addProcessor(new RuleProcessor((List<? extends ProcessorRule>)$$6)).addProcessor(new BlockAgeProcessor($$4.mossiness)).addProcessor(new ProtectedBlockProcessor(BlockTags.FEATURES_CANNOT_REPLACE)).addProcessor(new LavaSubmergedBlockProcessor());
        if ($$4.replaceWithBlackstone) {
            $$7.addProcessor(BlackstoneReplaceProcessor.INSTANCE);
        }
        return $$7;
    }

    private static ProcessorRule getLavaProcessorRule(VerticalPlacement $$0, Properties $$1) {
        if ($$0 == VerticalPlacement.ON_OCEAN_FLOOR) {
            return RuinedPortalPiece.getBlockReplaceRule(Blocks.LAVA, Blocks.MAGMA_BLOCK);
        }
        if ($$1.cold) {
            return RuinedPortalPiece.getBlockReplaceRule(Blocks.LAVA, Blocks.NETHERRACK);
        }
        return RuinedPortalPiece.getBlockReplaceRule(Blocks.LAVA, 0.2f, Blocks.MAGMA_BLOCK);
    }

    @Override
    public void postProcess(WorldGenLevel $$0, StructureManager $$1, ChunkGenerator $$22, RandomSource $$3, BoundingBox $$4, ChunkPos $$5, BlockPos $$6) {
        BoundingBox $$7 = this.template.getBoundingBox(this.placeSettings, this.templatePosition);
        if (!$$4.isInside($$7.getCenter())) {
            return;
        }
        $$4.encapsulate($$7);
        super.postProcess($$0, $$1, $$22, $$3, $$4, $$5, $$6);
        this.spreadNetherrack($$3, $$0);
        this.addNetherrackDripColumnsBelowPortal($$3, $$0);
        if (this.properties.vines || this.properties.overgrown) {
            BlockPos.betweenClosedStream(this.getBoundingBox()).forEach($$2 -> {
                if (this.properties.vines) {
                    this.maybeAddVines($$3, $$0, (BlockPos)$$2);
                }
                if (this.properties.overgrown) {
                    this.maybeAddLeavesAbove($$3, $$0, (BlockPos)$$2);
                }
            });
        }
    }

    @Override
    protected void handleDataMarker(String $$0, BlockPos $$1, ServerLevelAccessor $$2, RandomSource $$3, BoundingBox $$4) {
    }

    private void maybeAddVines(RandomSource $$0, LevelAccessor $$1, BlockPos $$2) {
        BlockState $$3 = $$1.getBlockState($$2);
        if ($$3.isAir() || $$3.is(Blocks.VINE)) {
            return;
        }
        Direction $$4 = RuinedPortalPiece.getRandomHorizontalDirection($$0);
        Vec3i $$5 = $$2.relative($$4);
        BlockState $$6 = $$1.getBlockState((BlockPos)$$5);
        if (!$$6.isAir()) {
            return;
        }
        if (!Block.isFaceFull($$3.getCollisionShape($$1, $$2), $$4)) {
            return;
        }
        BooleanProperty $$7 = VineBlock.getPropertyForFace($$4.getOpposite());
        $$1.setBlock((BlockPos)$$5, (BlockState)Blocks.VINE.defaultBlockState().setValue($$7, true), 3);
    }

    private void maybeAddLeavesAbove(RandomSource $$0, LevelAccessor $$1, BlockPos $$2) {
        if ($$0.nextFloat() < 0.5f && $$1.getBlockState($$2).is(Blocks.NETHERRACK) && $$1.getBlockState((BlockPos)$$2.above()).isAir()) {
            $$1.setBlock((BlockPos)$$2.above(), (BlockState)Blocks.JUNGLE_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true), 3);
        }
    }

    private void addNetherrackDripColumnsBelowPortal(RandomSource $$0, LevelAccessor $$1) {
        for (int $$2 = this.boundingBox.minX() + 1; $$2 < this.boundingBox.maxX(); ++$$2) {
            for (int $$3 = this.boundingBox.minZ() + 1; $$3 < this.boundingBox.maxZ(); ++$$3) {
                BlockPos $$4 = new BlockPos($$2, this.boundingBox.minY(), $$3);
                if (!$$1.getBlockState($$4).is(Blocks.NETHERRACK)) continue;
                this.addNetherrackDripColumn($$0, $$1, (BlockPos)$$4.below());
            }
        }
    }

    private void addNetherrackDripColumn(RandomSource $$0, LevelAccessor $$1, BlockPos $$2) {
        BlockPos.MutableBlockPos $$3 = $$2.mutable();
        this.placeNetherrackOrMagma($$0, $$1, $$3);
        for (int $$4 = 8; $$4 > 0 && $$0.nextFloat() < 0.5f; --$$4) {
            $$3.move(Direction.DOWN);
            this.placeNetherrackOrMagma($$0, $$1, $$3);
        }
    }

    private void spreadNetherrack(RandomSource $$0, LevelAccessor $$1) {
        boolean $$2 = this.verticalPlacement == VerticalPlacement.ON_LAND_SURFACE || this.verticalPlacement == VerticalPlacement.ON_OCEAN_FLOOR;
        BlockPos $$3 = this.boundingBox.getCenter();
        int $$4 = $$3.getX();
        int $$5 = $$3.getZ();
        float[] $$6 = new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.9f, 0.9f, 0.8f, 0.7f, 0.6f, 0.4f, 0.2f};
        int $$7 = $$6.length;
        int $$8 = (this.boundingBox.getXSpan() + this.boundingBox.getZSpan()) / 2;
        int $$9 = $$0.nextInt(Math.max((int)1, (int)(8 - $$8 / 2)));
        int $$10 = 3;
        BlockPos.MutableBlockPos $$11 = BlockPos.ZERO.mutable();
        for (int $$12 = $$4 - $$7; $$12 <= $$4 + $$7; ++$$12) {
            for (int $$13 = $$5 - $$7; $$13 <= $$5 + $$7; ++$$13) {
                int $$14 = Math.abs((int)($$12 - $$4)) + Math.abs((int)($$13 - $$5));
                int $$15 = Math.max((int)0, (int)($$14 + $$9));
                if ($$15 >= $$7) continue;
                float $$16 = $$6[$$15];
                if (!($$0.nextDouble() < (double)$$16)) continue;
                int $$17 = RuinedPortalPiece.getSurfaceY($$1, $$12, $$13, this.verticalPlacement);
                int $$18 = $$2 ? $$17 : Math.min((int)this.boundingBox.minY(), (int)$$17);
                $$11.set($$12, $$18, $$13);
                if (Math.abs((int)($$18 - this.boundingBox.minY())) > 3 || !this.canBlockBeReplacedByNetherrackOrMagma($$1, $$11)) continue;
                this.placeNetherrackOrMagma($$0, $$1, $$11);
                if (this.properties.overgrown) {
                    this.maybeAddLeavesAbove($$0, $$1, $$11);
                }
                this.addNetherrackDripColumn($$0, $$1, (BlockPos)$$11.below());
            }
        }
    }

    private boolean canBlockBeReplacedByNetherrackOrMagma(LevelAccessor $$0, BlockPos $$1) {
        BlockState $$2 = $$0.getBlockState($$1);
        return !$$2.is(Blocks.AIR) && !$$2.is(Blocks.OBSIDIAN) && !$$2.is(BlockTags.FEATURES_CANNOT_REPLACE) && (this.verticalPlacement == VerticalPlacement.IN_NETHER || !$$2.is(Blocks.LAVA));
    }

    private void placeNetherrackOrMagma(RandomSource $$0, LevelAccessor $$1, BlockPos $$2) {
        if (!this.properties.cold && $$0.nextFloat() < 0.07f) {
            $$1.setBlock($$2, Blocks.MAGMA_BLOCK.defaultBlockState(), 3);
        } else {
            $$1.setBlock($$2, Blocks.NETHERRACK.defaultBlockState(), 3);
        }
    }

    private static int getSurfaceY(LevelAccessor $$0, int $$1, int $$2, VerticalPlacement $$3) {
        return $$0.getHeight(RuinedPortalPiece.getHeightMapType($$3), $$1, $$2) - 1;
    }

    public static Heightmap.Types getHeightMapType(VerticalPlacement $$0) {
        return $$0 == VerticalPlacement.ON_OCEAN_FLOOR ? Heightmap.Types.OCEAN_FLOOR_WG : Heightmap.Types.WORLD_SURFACE_WG;
    }

    private static ProcessorRule getBlockReplaceRule(Block $$0, float $$1, Block $$2) {
        return new ProcessorRule(new RandomBlockMatchTest($$0, $$1), AlwaysTrueTest.INSTANCE, $$2.defaultBlockState());
    }

    private static ProcessorRule getBlockReplaceRule(Block $$0, Block $$1) {
        return new ProcessorRule(new BlockMatchTest($$0), AlwaysTrueTest.INSTANCE, $$1.defaultBlockState());
    }

    public static enum VerticalPlacement implements StringRepresentable
    {
        ON_LAND_SURFACE("on_land_surface"),
        PARTLY_BURIED("partly_buried"),
        ON_OCEAN_FLOOR("on_ocean_floor"),
        IN_MOUNTAIN("in_mountain"),
        UNDERGROUND("underground"),
        IN_NETHER("in_nether");

        public static final StringRepresentable.EnumCodec<VerticalPlacement> CODEC;
        private final String name;

        private VerticalPlacement(String $$0) {
            this.name = $$0;
        }

        public String getName() {
            return this.name;
        }

        public static VerticalPlacement byName(String $$0) {
            return CODEC.byName($$0);
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        static {
            CODEC = StringRepresentable.fromEnum((Supplier<E[]>)((Supplier)VerticalPlacement::values));
        }
    }

    public static class Properties {
        public static final Codec<Properties> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.BOOL.fieldOf("cold").forGetter($$0 -> $$0.cold), (App)Codec.FLOAT.fieldOf("mossiness").forGetter($$0 -> Float.valueOf((float)$$0.mossiness)), (App)Codec.BOOL.fieldOf("air_pocket").forGetter($$0 -> $$0.airPocket), (App)Codec.BOOL.fieldOf("overgrown").forGetter($$0 -> $$0.overgrown), (App)Codec.BOOL.fieldOf("vines").forGetter($$0 -> $$0.vines), (App)Codec.BOOL.fieldOf("replace_with_blackstone").forGetter($$0 -> $$0.replaceWithBlackstone)).apply((Applicative)$$02, Properties::new));
        public boolean cold;
        public float mossiness;
        public boolean airPocket;
        public boolean overgrown;
        public boolean vines;
        public boolean replaceWithBlackstone;

        public Properties() {
        }

        public Properties(boolean $$0, float $$1, boolean $$2, boolean $$3, boolean $$4, boolean $$5) {
            this.cold = $$0;
            this.mossiness = $$1;
            this.airPocket = $$2;
            this.overgrown = $$3;
            this.vines = $$4;
            this.replaceWithBlackstone = $$5;
        }
    }
}