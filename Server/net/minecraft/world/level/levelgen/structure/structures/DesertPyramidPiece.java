/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.levelgen.structure.structures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.ScatteredFeaturePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class DesertPyramidPiece
extends ScatteredFeaturePiece {
    public static final int WIDTH = 21;
    public static final int DEPTH = 21;
    private final boolean[] hasPlacedChest = new boolean[4];

    public DesertPyramidPiece(RandomSource $$0, int $$1, int $$2) {
        super(StructurePieceType.DESERT_PYRAMID_PIECE, $$1, 64, $$2, 21, 15, 21, DesertPyramidPiece.getRandomHorizontalDirection($$0));
    }

    public DesertPyramidPiece(CompoundTag $$0) {
        super(StructurePieceType.DESERT_PYRAMID_PIECE, $$0);
        this.hasPlacedChest[0] = $$0.getBoolean("hasPlacedChest0");
        this.hasPlacedChest[1] = $$0.getBoolean("hasPlacedChest1");
        this.hasPlacedChest[2] = $$0.getBoolean("hasPlacedChest2");
        this.hasPlacedChest[3] = $$0.getBoolean("hasPlacedChest3");
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext $$0, CompoundTag $$1) {
        super.addAdditionalSaveData($$0, $$1);
        $$1.putBoolean("hasPlacedChest0", this.hasPlacedChest[0]);
        $$1.putBoolean("hasPlacedChest1", this.hasPlacedChest[1]);
        $$1.putBoolean("hasPlacedChest2", this.hasPlacedChest[2]);
        $$1.putBoolean("hasPlacedChest3", this.hasPlacedChest[3]);
    }

    @Override
    public void postProcess(WorldGenLevel $$0, StructureManager $$1, ChunkGenerator $$2, RandomSource $$3, BoundingBox $$4, ChunkPos $$5, BlockPos $$6) {
        if (!this.updateHeightPositionToLowestGroundHeight($$0, -$$3.nextInt(3))) {
            return;
        }
        this.generateBox($$0, $$4, 0, -4, 0, this.width - 1, 0, this.depth - 1, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
        for (int $$7 = 1; $$7 <= 9; ++$$7) {
            this.generateBox($$0, $$4, $$7, $$7, $$7, this.width - 1 - $$7, $$7, this.depth - 1 - $$7, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
            this.generateBox($$0, $$4, $$7 + 1, $$7, $$7 + 1, this.width - 2 - $$7, $$7, this.depth - 2 - $$7, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        }
        for (int $$8 = 0; $$8 < this.width; ++$$8) {
            for (int $$9 = 0; $$9 < this.depth; ++$$9) {
                int $$10 = -5;
                this.fillColumnDown($$0, Blocks.SANDSTONE.defaultBlockState(), $$8, -5, $$9, $$4);
            }
        }
        BlockState $$11 = (BlockState)Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);
        BlockState $$12 = (BlockState)Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);
        BlockState $$13 = (BlockState)Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.EAST);
        BlockState $$14 = (BlockState)Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.WEST);
        this.generateBox($$0, $$4, 0, 0, 0, 4, 9, 4, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        this.generateBox($$0, $$4, 1, 10, 1, 3, 10, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
        this.placeBlock($$0, $$11, 2, 10, 0, $$4);
        this.placeBlock($$0, $$12, 2, 10, 4, $$4);
        this.placeBlock($$0, $$13, 0, 10, 2, $$4);
        this.placeBlock($$0, $$14, 4, 10, 2, $$4);
        this.generateBox($$0, $$4, this.width - 5, 0, 0, this.width - 1, 9, 4, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        this.generateBox($$0, $$4, this.width - 4, 10, 1, this.width - 2, 10, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
        this.placeBlock($$0, $$11, this.width - 3, 10, 0, $$4);
        this.placeBlock($$0, $$12, this.width - 3, 10, 4, $$4);
        this.placeBlock($$0, $$13, this.width - 5, 10, 2, $$4);
        this.placeBlock($$0, $$14, this.width - 1, 10, 2, $$4);
        this.generateBox($$0, $$4, 8, 0, 0, 12, 4, 4, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        this.generateBox($$0, $$4, 9, 1, 0, 11, 3, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), 9, 1, 1, $$4);
        this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), 9, 2, 1, $$4);
        this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), 9, 3, 1, $$4);
        this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), 10, 3, 1, $$4);
        this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), 11, 3, 1, $$4);
        this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), 11, 2, 1, $$4);
        this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), 11, 1, 1, $$4);
        this.generateBox($$0, $$4, 4, 1, 1, 8, 3, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        this.generateBox($$0, $$4, 4, 1, 2, 8, 2, 2, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        this.generateBox($$0, $$4, 12, 1, 1, 16, 3, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        this.generateBox($$0, $$4, 12, 1, 2, 16, 2, 2, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        this.generateBox($$0, $$4, 5, 4, 5, this.width - 6, 4, this.depth - 6, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
        this.generateBox($$0, $$4, 9, 4, 9, 11, 4, 11, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        this.generateBox($$0, $$4, 8, 1, 8, 8, 3, 8, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
        this.generateBox($$0, $$4, 12, 1, 8, 12, 3, 8, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
        this.generateBox($$0, $$4, 8, 1, 12, 8, 3, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
        this.generateBox($$0, $$4, 12, 1, 12, 12, 3, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
        this.generateBox($$0, $$4, 1, 1, 5, 4, 4, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
        this.generateBox($$0, $$4, this.width - 5, 1, 5, this.width - 2, 4, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
        this.generateBox($$0, $$4, 6, 7, 9, 6, 7, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
        this.generateBox($$0, $$4, this.width - 7, 7, 9, this.width - 7, 7, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
        this.generateBox($$0, $$4, 5, 5, 9, 5, 7, 11, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
        this.generateBox($$0, $$4, this.width - 6, 5, 9, this.width - 6, 7, 11, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
        this.placeBlock($$0, Blocks.AIR.defaultBlockState(), 5, 5, 10, $$4);
        this.placeBlock($$0, Blocks.AIR.defaultBlockState(), 5, 6, 10, $$4);
        this.placeBlock($$0, Blocks.AIR.defaultBlockState(), 6, 6, 10, $$4);
        this.placeBlock($$0, Blocks.AIR.defaultBlockState(), this.width - 6, 5, 10, $$4);
        this.placeBlock($$0, Blocks.AIR.defaultBlockState(), this.width - 6, 6, 10, $$4);
        this.placeBlock($$0, Blocks.AIR.defaultBlockState(), this.width - 7, 6, 10, $$4);
        this.generateBox($$0, $$4, 2, 4, 4, 2, 6, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        this.generateBox($$0, $$4, this.width - 3, 4, 4, this.width - 3, 6, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        this.placeBlock($$0, $$11, 2, 4, 5, $$4);
        this.placeBlock($$0, $$11, 2, 3, 4, $$4);
        this.placeBlock($$0, $$11, this.width - 3, 4, 5, $$4);
        this.placeBlock($$0, $$11, this.width - 3, 3, 4, $$4);
        this.generateBox($$0, $$4, 1, 1, 3, 2, 2, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
        this.generateBox($$0, $$4, this.width - 3, 1, 3, this.width - 2, 2, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
        this.placeBlock($$0, Blocks.SANDSTONE.defaultBlockState(), 1, 1, 2, $$4);
        this.placeBlock($$0, Blocks.SANDSTONE.defaultBlockState(), this.width - 2, 1, 2, $$4);
        this.placeBlock($$0, Blocks.SANDSTONE_SLAB.defaultBlockState(), 1, 2, 2, $$4);
        this.placeBlock($$0, Blocks.SANDSTONE_SLAB.defaultBlockState(), this.width - 2, 2, 2, $$4);
        this.placeBlock($$0, $$14, 2, 1, 2, $$4);
        this.placeBlock($$0, $$13, this.width - 3, 1, 2, $$4);
        this.generateBox($$0, $$4, 4, 3, 5, 4, 3, 17, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
        this.generateBox($$0, $$4, this.width - 5, 3, 5, this.width - 5, 3, 17, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
        this.generateBox($$0, $$4, 3, 1, 5, 4, 2, 16, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        this.generateBox($$0, $$4, this.width - 6, 1, 5, this.width - 5, 2, 16, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        for (int $$15 = 5; $$15 <= 17; $$15 += 2) {
            this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), 4, 1, $$15, $$4);
            this.placeBlock($$0, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 4, 2, $$15, $$4);
            this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), this.width - 5, 1, $$15, $$4);
            this.placeBlock($$0, Blocks.CHISELED_SANDSTONE.defaultBlockState(), this.width - 5, 2, $$15, $$4);
        }
        this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 7, $$4);
        this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 8, $$4);
        this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 9, 0, 9, $$4);
        this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 11, 0, 9, $$4);
        this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 8, 0, 10, $$4);
        this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 12, 0, 10, $$4);
        this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 7, 0, 10, $$4);
        this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 13, 0, 10, $$4);
        this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 9, 0, 11, $$4);
        this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 11, 0, 11, $$4);
        this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 12, $$4);
        this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 13, $$4);
        this.placeBlock($$0, Blocks.BLUE_TERRACOTTA.defaultBlockState(), 10, 0, 10, $$4);
        for (int $$16 = 0; $$16 <= this.width - 1; $$16 += this.width - 1) {
            this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), $$16, 2, 1, $$4);
            this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), $$16, 2, 2, $$4);
            this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), $$16, 2, 3, $$4);
            this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), $$16, 3, 1, $$4);
            this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), $$16, 3, 2, $$4);
            this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), $$16, 3, 3, $$4);
            this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), $$16, 4, 1, $$4);
            this.placeBlock($$0, Blocks.CHISELED_SANDSTONE.defaultBlockState(), $$16, 4, 2, $$4);
            this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), $$16, 4, 3, $$4);
            this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), $$16, 5, 1, $$4);
            this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), $$16, 5, 2, $$4);
            this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), $$16, 5, 3, $$4);
            this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), $$16, 6, 1, $$4);
            this.placeBlock($$0, Blocks.CHISELED_SANDSTONE.defaultBlockState(), $$16, 6, 2, $$4);
            this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), $$16, 6, 3, $$4);
            this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), $$16, 7, 1, $$4);
            this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), $$16, 7, 2, $$4);
            this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), $$16, 7, 3, $$4);
            this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), $$16, 8, 1, $$4);
            this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), $$16, 8, 2, $$4);
            this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), $$16, 8, 3, $$4);
        }
        for (int $$17 = 2; $$17 <= this.width - 3; $$17 += this.width - 3 - 2) {
            this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), $$17 - 1, 2, 0, $$4);
            this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), $$17, 2, 0, $$4);
            this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), $$17 + 1, 2, 0, $$4);
            this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), $$17 - 1, 3, 0, $$4);
            this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), $$17, 3, 0, $$4);
            this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), $$17 + 1, 3, 0, $$4);
            this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), $$17 - 1, 4, 0, $$4);
            this.placeBlock($$0, Blocks.CHISELED_SANDSTONE.defaultBlockState(), $$17, 4, 0, $$4);
            this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), $$17 + 1, 4, 0, $$4);
            this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), $$17 - 1, 5, 0, $$4);
            this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), $$17, 5, 0, $$4);
            this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), $$17 + 1, 5, 0, $$4);
            this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), $$17 - 1, 6, 0, $$4);
            this.placeBlock($$0, Blocks.CHISELED_SANDSTONE.defaultBlockState(), $$17, 6, 0, $$4);
            this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), $$17 + 1, 6, 0, $$4);
            this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), $$17 - 1, 7, 0, $$4);
            this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), $$17, 7, 0, $$4);
            this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), $$17 + 1, 7, 0, $$4);
            this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), $$17 - 1, 8, 0, $$4);
            this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), $$17, 8, 0, $$4);
            this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), $$17 + 1, 8, 0, $$4);
        }
        this.generateBox($$0, $$4, 8, 4, 0, 12, 6, 0, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
        this.placeBlock($$0, Blocks.AIR.defaultBlockState(), 8, 6, 0, $$4);
        this.placeBlock($$0, Blocks.AIR.defaultBlockState(), 12, 6, 0, $$4);
        this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 9, 5, 0, $$4);
        this.placeBlock($$0, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 10, 5, 0, $$4);
        this.placeBlock($$0, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 11, 5, 0, $$4);
        this.generateBox($$0, $$4, 8, -14, 8, 12, -11, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
        this.generateBox($$0, $$4, 8, -10, 8, 12, -10, 12, Blocks.CHISELED_SANDSTONE.defaultBlockState(), Blocks.CHISELED_SANDSTONE.defaultBlockState(), false);
        this.generateBox($$0, $$4, 8, -9, 8, 12, -9, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
        this.generateBox($$0, $$4, 8, -8, 8, 12, -1, 12, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
        this.generateBox($$0, $$4, 9, -11, 9, 11, -1, 11, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        this.placeBlock($$0, Blocks.STONE_PRESSURE_PLATE.defaultBlockState(), 10, -11, 10, $$4);
        this.generateBox($$0, $$4, 9, -13, 9, 11, -13, 11, Blocks.TNT.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
        this.placeBlock($$0, Blocks.AIR.defaultBlockState(), 8, -11, 10, $$4);
        this.placeBlock($$0, Blocks.AIR.defaultBlockState(), 8, -10, 10, $$4);
        this.placeBlock($$0, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 7, -10, 10, $$4);
        this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), 7, -11, 10, $$4);
        this.placeBlock($$0, Blocks.AIR.defaultBlockState(), 12, -11, 10, $$4);
        this.placeBlock($$0, Blocks.AIR.defaultBlockState(), 12, -10, 10, $$4);
        this.placeBlock($$0, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 13, -10, 10, $$4);
        this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), 13, -11, 10, $$4);
        this.placeBlock($$0, Blocks.AIR.defaultBlockState(), 10, -11, 8, $$4);
        this.placeBlock($$0, Blocks.AIR.defaultBlockState(), 10, -10, 8, $$4);
        this.placeBlock($$0, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 10, -10, 7, $$4);
        this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), 10, -11, 7, $$4);
        this.placeBlock($$0, Blocks.AIR.defaultBlockState(), 10, -11, 12, $$4);
        this.placeBlock($$0, Blocks.AIR.defaultBlockState(), 10, -10, 12, $$4);
        this.placeBlock($$0, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 10, -10, 13, $$4);
        this.placeBlock($$0, Blocks.CUT_SANDSTONE.defaultBlockState(), 10, -11, 13, $$4);
        for (Direction $$18 : Direction.Plane.HORIZONTAL) {
            if (this.hasPlacedChest[$$18.get2DDataValue()]) continue;
            int $$19 = $$18.getStepX() * 2;
            int $$20 = $$18.getStepZ() * 2;
            this.hasPlacedChest[$$18.get2DDataValue()] = this.createChest($$0, $$4, $$3, 10 + $$19, -11, 10 + $$20, BuiltInLootTables.DESERT_PYRAMID);
        }
    }
}