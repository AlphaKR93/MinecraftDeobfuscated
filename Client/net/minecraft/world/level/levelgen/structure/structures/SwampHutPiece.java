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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.ScatteredFeaturePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class SwampHutPiece
extends ScatteredFeaturePiece {
    private boolean spawnedWitch;
    private boolean spawnedCat;

    public SwampHutPiece(RandomSource $$0, int $$1, int $$2) {
        super(StructurePieceType.SWAMPLAND_HUT, $$1, 64, $$2, 7, 7, 9, SwampHutPiece.getRandomHorizontalDirection($$0));
    }

    public SwampHutPiece(CompoundTag $$0) {
        super(StructurePieceType.SWAMPLAND_HUT, $$0);
        this.spawnedWitch = $$0.getBoolean("Witch");
        this.spawnedCat = $$0.getBoolean("Cat");
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext $$0, CompoundTag $$1) {
        super.addAdditionalSaveData($$0, $$1);
        $$1.putBoolean("Witch", this.spawnedWitch);
        $$1.putBoolean("Cat", this.spawnedCat);
    }

    @Override
    public void postProcess(WorldGenLevel $$0, StructureManager $$1, ChunkGenerator $$2, RandomSource $$3, BoundingBox $$4, ChunkPos $$5, BlockPos $$6) {
        BlockPos.MutableBlockPos $$13;
        if (!this.updateAverageGroundHeight($$0, $$4, 0)) {
            return;
        }
        this.generateBox($$0, $$4, 1, 1, 1, 5, 1, 7, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
        this.generateBox($$0, $$4, 1, 4, 2, 5, 4, 7, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
        this.generateBox($$0, $$4, 2, 1, 0, 4, 1, 0, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
        this.generateBox($$0, $$4, 2, 2, 2, 3, 3, 2, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
        this.generateBox($$0, $$4, 1, 2, 3, 1, 3, 6, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
        this.generateBox($$0, $$4, 5, 2, 3, 5, 3, 6, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
        this.generateBox($$0, $$4, 2, 2, 7, 4, 3, 7, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
        this.generateBox($$0, $$4, 1, 0, 2, 1, 3, 2, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
        this.generateBox($$0, $$4, 5, 0, 2, 5, 3, 2, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
        this.generateBox($$0, $$4, 1, 0, 7, 1, 3, 7, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
        this.generateBox($$0, $$4, 5, 0, 7, 5, 3, 7, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
        this.placeBlock($$0, Blocks.OAK_FENCE.defaultBlockState(), 2, 3, 2, $$4);
        this.placeBlock($$0, Blocks.OAK_FENCE.defaultBlockState(), 3, 3, 7, $$4);
        this.placeBlock($$0, Blocks.AIR.defaultBlockState(), 1, 3, 4, $$4);
        this.placeBlock($$0, Blocks.AIR.defaultBlockState(), 5, 3, 4, $$4);
        this.placeBlock($$0, Blocks.AIR.defaultBlockState(), 5, 3, 5, $$4);
        this.placeBlock($$0, Blocks.POTTED_RED_MUSHROOM.defaultBlockState(), 1, 3, 5, $$4);
        this.placeBlock($$0, Blocks.CRAFTING_TABLE.defaultBlockState(), 3, 2, 6, $$4);
        this.placeBlock($$0, Blocks.CAULDRON.defaultBlockState(), 4, 2, 6, $$4);
        this.placeBlock($$0, Blocks.OAK_FENCE.defaultBlockState(), 1, 2, 1, $$4);
        this.placeBlock($$0, Blocks.OAK_FENCE.defaultBlockState(), 5, 2, 1, $$4);
        BlockState $$7 = (BlockState)Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);
        BlockState $$8 = (BlockState)Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.EAST);
        BlockState $$9 = (BlockState)Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.WEST);
        BlockState $$10 = (BlockState)Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);
        this.generateBox($$0, $$4, 0, 4, 1, 6, 4, 1, $$7, $$7, false);
        this.generateBox($$0, $$4, 0, 4, 2, 0, 4, 7, $$8, $$8, false);
        this.generateBox($$0, $$4, 6, 4, 2, 6, 4, 7, $$9, $$9, false);
        this.generateBox($$0, $$4, 0, 4, 8, 6, 4, 8, $$10, $$10, false);
        this.placeBlock($$0, (BlockState)$$7.setValue(StairBlock.SHAPE, StairsShape.OUTER_RIGHT), 0, 4, 1, $$4);
        this.placeBlock($$0, (BlockState)$$7.setValue(StairBlock.SHAPE, StairsShape.OUTER_LEFT), 6, 4, 1, $$4);
        this.placeBlock($$0, (BlockState)$$10.setValue(StairBlock.SHAPE, StairsShape.OUTER_LEFT), 0, 4, 8, $$4);
        this.placeBlock($$0, (BlockState)$$10.setValue(StairBlock.SHAPE, StairsShape.OUTER_RIGHT), 6, 4, 8, $$4);
        for (int $$11 = 2; $$11 <= 7; $$11 += 5) {
            for (int $$12 = 1; $$12 <= 5; $$12 += 4) {
                this.fillColumnDown($$0, Blocks.OAK_LOG.defaultBlockState(), $$12, -1, $$11, $$4);
            }
        }
        if (!this.spawnedWitch && $$4.isInside($$13 = this.getWorldPos(2, 2, 5))) {
            this.spawnedWitch = true;
            Witch $$14 = EntityType.WITCH.create($$0.getLevel());
            if ($$14 != null) {
                $$14.setPersistenceRequired();
                $$14.moveTo((double)$$13.getX() + 0.5, $$13.getY(), (double)$$13.getZ() + 0.5, 0.0f, 0.0f);
                $$14.finalizeSpawn($$0, $$0.getCurrentDifficultyAt($$13), MobSpawnType.STRUCTURE, null, null);
                $$0.addFreshEntityWithPassengers($$14);
            }
        }
        this.spawnCat($$0, $$4);
    }

    private void spawnCat(ServerLevelAccessor $$0, BoundingBox $$1) {
        BlockPos.MutableBlockPos $$2;
        if (!this.spawnedCat && $$1.isInside($$2 = this.getWorldPos(2, 2, 5))) {
            this.spawnedCat = true;
            Cat $$3 = EntityType.CAT.create($$0.getLevel());
            if ($$3 != null) {
                $$3.setPersistenceRequired();
                $$3.moveTo((double)$$2.getX() + 0.5, $$2.getY(), (double)$$2.getZ() + 0.5, 0.0f, 0.0f);
                $$3.finalizeSpawn($$0, $$0.getCurrentDifficultyAt($$2), MobSpawnType.STRUCTURE, null, null);
                $$0.addFreshEntityWithPassengers($$3);
            }
        }
    }
}