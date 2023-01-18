/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.function.Function
 */
package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.structures.OceanRuinStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class OceanRuinPieces {
    private static final ResourceLocation[] WARM_RUINS = new ResourceLocation[]{new ResourceLocation("underwater_ruin/warm_1"), new ResourceLocation("underwater_ruin/warm_2"), new ResourceLocation("underwater_ruin/warm_3"), new ResourceLocation("underwater_ruin/warm_4"), new ResourceLocation("underwater_ruin/warm_5"), new ResourceLocation("underwater_ruin/warm_6"), new ResourceLocation("underwater_ruin/warm_7"), new ResourceLocation("underwater_ruin/warm_8")};
    private static final ResourceLocation[] RUINS_BRICK = new ResourceLocation[]{new ResourceLocation("underwater_ruin/brick_1"), new ResourceLocation("underwater_ruin/brick_2"), new ResourceLocation("underwater_ruin/brick_3"), new ResourceLocation("underwater_ruin/brick_4"), new ResourceLocation("underwater_ruin/brick_5"), new ResourceLocation("underwater_ruin/brick_6"), new ResourceLocation("underwater_ruin/brick_7"), new ResourceLocation("underwater_ruin/brick_8")};
    private static final ResourceLocation[] RUINS_CRACKED = new ResourceLocation[]{new ResourceLocation("underwater_ruin/cracked_1"), new ResourceLocation("underwater_ruin/cracked_2"), new ResourceLocation("underwater_ruin/cracked_3"), new ResourceLocation("underwater_ruin/cracked_4"), new ResourceLocation("underwater_ruin/cracked_5"), new ResourceLocation("underwater_ruin/cracked_6"), new ResourceLocation("underwater_ruin/cracked_7"), new ResourceLocation("underwater_ruin/cracked_8")};
    private static final ResourceLocation[] RUINS_MOSSY = new ResourceLocation[]{new ResourceLocation("underwater_ruin/mossy_1"), new ResourceLocation("underwater_ruin/mossy_2"), new ResourceLocation("underwater_ruin/mossy_3"), new ResourceLocation("underwater_ruin/mossy_4"), new ResourceLocation("underwater_ruin/mossy_5"), new ResourceLocation("underwater_ruin/mossy_6"), new ResourceLocation("underwater_ruin/mossy_7"), new ResourceLocation("underwater_ruin/mossy_8")};
    private static final ResourceLocation[] BIG_RUINS_BRICK = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_brick_1"), new ResourceLocation("underwater_ruin/big_brick_2"), new ResourceLocation("underwater_ruin/big_brick_3"), new ResourceLocation("underwater_ruin/big_brick_8")};
    private static final ResourceLocation[] BIG_RUINS_MOSSY = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_mossy_1"), new ResourceLocation("underwater_ruin/big_mossy_2"), new ResourceLocation("underwater_ruin/big_mossy_3"), new ResourceLocation("underwater_ruin/big_mossy_8")};
    private static final ResourceLocation[] BIG_RUINS_CRACKED = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_cracked_1"), new ResourceLocation("underwater_ruin/big_cracked_2"), new ResourceLocation("underwater_ruin/big_cracked_3"), new ResourceLocation("underwater_ruin/big_cracked_8")};
    private static final ResourceLocation[] BIG_WARM_RUINS = new ResourceLocation[]{new ResourceLocation("underwater_ruin/big_warm_4"), new ResourceLocation("underwater_ruin/big_warm_5"), new ResourceLocation("underwater_ruin/big_warm_6"), new ResourceLocation("underwater_ruin/big_warm_7")};

    private static ResourceLocation getSmallWarmRuin(RandomSource $$0) {
        return Util.getRandom(WARM_RUINS, $$0);
    }

    private static ResourceLocation getBigWarmRuin(RandomSource $$0) {
        return Util.getRandom(BIG_WARM_RUINS, $$0);
    }

    public static void addPieces(StructureTemplateManager $$0, BlockPos $$1, Rotation $$2, StructurePieceAccessor $$3, RandomSource $$4, OceanRuinStructure $$5) {
        boolean $$6 = $$4.nextFloat() <= $$5.largeProbability;
        float $$7 = $$6 ? 0.9f : 0.8f;
        OceanRuinPieces.addPiece($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
        if ($$6 && $$4.nextFloat() <= $$5.clusterProbability) {
            OceanRuinPieces.addClusterRuins($$0, $$4, $$2, $$1, $$5, $$3);
        }
    }

    private static void addClusterRuins(StructureTemplateManager $$0, RandomSource $$1, Rotation $$2, BlockPos $$3, OceanRuinStructure $$4, StructurePieceAccessor $$5) {
        BlockPos $$6 = new BlockPos($$3.getX(), 90, $$3.getZ());
        Vec3i $$7 = StructureTemplate.transform(new BlockPos(15, 0, 15), Mirror.NONE, $$2, BlockPos.ZERO).offset($$6);
        BoundingBox $$8 = BoundingBox.fromCorners($$6, $$7);
        BlockPos $$9 = new BlockPos(Math.min((int)$$6.getX(), (int)$$7.getX()), $$6.getY(), Math.min((int)$$6.getZ(), (int)$$7.getZ()));
        List<BlockPos> $$10 = OceanRuinPieces.allPositions($$1, $$9);
        int $$11 = Mth.nextInt($$1, 4, 8);
        for (int $$12 = 0; $$12 < $$11; ++$$12) {
            Rotation $$15;
            Vec3i $$16;
            int $$13;
            BlockPos $$14;
            BoundingBox $$17;
            if ($$10.isEmpty() || ($$17 = BoundingBox.fromCorners($$14 = (BlockPos)$$10.remove($$13 = $$1.nextInt($$10.size())), $$16 = StructureTemplate.transform(new BlockPos(5, 0, 6), Mirror.NONE, $$15 = Rotation.getRandom($$1), BlockPos.ZERO).offset($$14))).intersects($$8)) continue;
            OceanRuinPieces.addPiece($$0, $$14, $$15, $$5, $$1, $$4, false, 0.8f);
        }
    }

    private static List<BlockPos> allPositions(RandomSource $$0, BlockPos $$1) {
        ArrayList $$2 = Lists.newArrayList();
        $$2.add((Object)$$1.offset(-16 + Mth.nextInt($$0, 1, 8), 0, 16 + Mth.nextInt($$0, 1, 7)));
        $$2.add((Object)$$1.offset(-16 + Mth.nextInt($$0, 1, 8), 0, Mth.nextInt($$0, 1, 7)));
        $$2.add((Object)$$1.offset(-16 + Mth.nextInt($$0, 1, 8), 0, -16 + Mth.nextInt($$0, 4, 8)));
        $$2.add((Object)$$1.offset(Mth.nextInt($$0, 1, 7), 0, 16 + Mth.nextInt($$0, 1, 7)));
        $$2.add((Object)$$1.offset(Mth.nextInt($$0, 1, 7), 0, -16 + Mth.nextInt($$0, 4, 6)));
        $$2.add((Object)$$1.offset(16 + Mth.nextInt($$0, 1, 7), 0, 16 + Mth.nextInt($$0, 3, 8)));
        $$2.add((Object)$$1.offset(16 + Mth.nextInt($$0, 1, 7), 0, Mth.nextInt($$0, 1, 7)));
        $$2.add((Object)$$1.offset(16 + Mth.nextInt($$0, 1, 7), 0, -16 + Mth.nextInt($$0, 4, 8)));
        return $$2;
    }

    private static void addPiece(StructureTemplateManager $$0, BlockPos $$1, Rotation $$2, StructurePieceAccessor $$3, RandomSource $$4, OceanRuinStructure $$5, boolean $$6, float $$7) {
        switch ($$5.biomeTemp) {
            default: {
                ResourceLocation $$8 = $$6 ? OceanRuinPieces.getBigWarmRuin($$4) : OceanRuinPieces.getSmallWarmRuin($$4);
                $$3.addPiece(new OceanRuinPiece($$0, $$8, $$1, $$2, $$7, $$5.biomeTemp, $$6));
                break;
            }
            case COLD: {
                ResourceLocation[] $$9 = $$6 ? BIG_RUINS_BRICK : RUINS_BRICK;
                ResourceLocation[] $$10 = $$6 ? BIG_RUINS_CRACKED : RUINS_CRACKED;
                ResourceLocation[] $$11 = $$6 ? BIG_RUINS_MOSSY : RUINS_MOSSY;
                int $$12 = $$4.nextInt($$9.length);
                $$3.addPiece(new OceanRuinPiece($$0, $$9[$$12], $$1, $$2, $$7, $$5.biomeTemp, $$6));
                $$3.addPiece(new OceanRuinPiece($$0, $$10[$$12], $$1, $$2, 0.7f, $$5.biomeTemp, $$6));
                $$3.addPiece(new OceanRuinPiece($$0, $$11[$$12], $$1, $$2, 0.5f, $$5.biomeTemp, $$6));
            }
        }
    }

    public static class OceanRuinPiece
    extends TemplateStructurePiece {
        private final OceanRuinStructure.Type biomeType;
        private final float integrity;
        private final boolean isLarge;

        public OceanRuinPiece(StructureTemplateManager $$0, ResourceLocation $$1, BlockPos $$2, Rotation $$3, float $$4, OceanRuinStructure.Type $$5, boolean $$6) {
            super(StructurePieceType.OCEAN_RUIN, 0, $$0, $$1, $$1.toString(), OceanRuinPiece.makeSettings($$3), $$2);
            this.integrity = $$4;
            this.biomeType = $$5;
            this.isLarge = $$6;
        }

        public OceanRuinPiece(StructureTemplateManager $$0, CompoundTag $$12) {
            super(StructurePieceType.OCEAN_RUIN, $$12, $$0, (Function<ResourceLocation, StructurePlaceSettings>)((Function)$$1 -> OceanRuinPiece.makeSettings(Rotation.valueOf($$12.getString("Rot")))));
            this.integrity = $$12.getFloat("Integrity");
            this.biomeType = OceanRuinStructure.Type.valueOf($$12.getString("BiomeType"));
            this.isLarge = $$12.getBoolean("IsLarge");
        }

        private static StructurePlaceSettings makeSettings(Rotation $$0) {
            return new StructurePlaceSettings().setRotation($$0).setMirror(Mirror.NONE).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext $$0, CompoundTag $$1) {
            super.addAdditionalSaveData($$0, $$1);
            $$1.putString("Rot", this.placeSettings.getRotation().name());
            $$1.putFloat("Integrity", this.integrity);
            $$1.putString("BiomeType", this.biomeType.toString());
            $$1.putBoolean("IsLarge", this.isLarge);
        }

        @Override
        protected void handleDataMarker(String $$0, BlockPos $$1, ServerLevelAccessor $$2, RandomSource $$3, BoundingBox $$4) {
            Drowned $$6;
            if ("chest".equals((Object)$$0)) {
                $$2.setBlock($$1, (BlockState)Blocks.CHEST.defaultBlockState().setValue(ChestBlock.WATERLOGGED, $$2.getFluidState($$1).is(FluidTags.WATER)), 2);
                BlockEntity $$5 = $$2.getBlockEntity($$1);
                if ($$5 instanceof ChestBlockEntity) {
                    ((ChestBlockEntity)$$5).setLootTable(this.isLarge ? BuiltInLootTables.UNDERWATER_RUIN_BIG : BuiltInLootTables.UNDERWATER_RUIN_SMALL, $$3.nextLong());
                }
            } else if ("drowned".equals((Object)$$0) && ($$6 = EntityType.DROWNED.create($$2.getLevel())) != null) {
                $$6.setPersistenceRequired();
                $$6.moveTo($$1, 0.0f, 0.0f);
                $$6.finalizeSpawn($$2, $$2.getCurrentDifficultyAt($$1), MobSpawnType.STRUCTURE, null, null);
                $$2.addFreshEntityWithPassengers($$6);
                if ($$1.getY() > $$2.getSeaLevel()) {
                    $$2.setBlock($$1, Blocks.AIR.defaultBlockState(), 2);
                } else {
                    $$2.setBlock($$1, Blocks.WATER.defaultBlockState(), 2);
                }
            }
        }

        @Override
        public void postProcess(WorldGenLevel $$0, StructureManager $$1, ChunkGenerator $$2, RandomSource $$3, BoundingBox $$4, ChunkPos $$5, BlockPos $$6) {
            this.placeSettings.clearProcessors().addProcessor(new BlockRotProcessor(this.integrity)).addProcessor(BlockIgnoreProcessor.STRUCTURE_AND_AIR);
            int $$7 = $$0.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, this.templatePosition.getX(), this.templatePosition.getZ());
            this.templatePosition = new BlockPos(this.templatePosition.getX(), $$7, this.templatePosition.getZ());
            Vec3i $$8 = StructureTemplate.transform(new BlockPos(this.template.getSize().getX() - 1, 0, this.template.getSize().getZ() - 1), Mirror.NONE, this.placeSettings.getRotation(), BlockPos.ZERO).offset(this.templatePosition);
            this.templatePosition = new BlockPos(this.templatePosition.getX(), this.getHeight(this.templatePosition, $$0, (BlockPos)$$8), this.templatePosition.getZ());
            super.postProcess($$0, $$1, $$2, $$3, $$4, $$5, $$6);
        }

        private int getHeight(BlockPos $$0, BlockGetter $$1, BlockPos $$2) {
            int $$3 = $$0.getY();
            int $$4 = 512;
            int $$5 = $$3 - 1;
            int $$6 = 0;
            for (BlockPos $$7 : BlockPos.betweenClosed($$0, $$2)) {
                int $$8 = $$7.getX();
                int $$9 = $$7.getZ();
                int $$10 = $$0.getY() - 1;
                BlockPos.MutableBlockPos $$11 = new BlockPos.MutableBlockPos($$8, $$10, $$9);
                BlockState $$12 = $$1.getBlockState($$11);
                FluidState $$13 = $$1.getFluidState($$11);
                while (($$12.isAir() || $$13.is(FluidTags.WATER) || $$12.is(BlockTags.ICE)) && $$10 > $$1.getMinBuildHeight() + 1) {
                    $$11.set($$8, --$$10, $$9);
                    $$12 = $$1.getBlockState($$11);
                    $$13 = $$1.getFluidState($$11);
                }
                $$4 = Math.min((int)$$4, (int)$$10);
                if ($$10 >= $$5 - 2) continue;
                ++$$6;
            }
            int $$14 = Math.abs((int)($$0.getX() - $$2.getX()));
            if ($$5 - $$4 > 2 && $$6 > $$14 - 2) {
                $$3 = $$4 + 1;
            }
            return $$3;
        }
    }
}