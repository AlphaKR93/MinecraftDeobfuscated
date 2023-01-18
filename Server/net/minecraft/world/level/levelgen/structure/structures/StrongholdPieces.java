/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Class
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class StrongholdPieces {
    private static final int SMALL_DOOR_WIDTH = 3;
    private static final int SMALL_DOOR_HEIGHT = 3;
    private static final int MAX_DEPTH = 50;
    private static final int LOWEST_Y_POSITION = 10;
    private static final boolean CHECK_AIR = true;
    public static final int MAGIC_START_Y = 64;
    private static final PieceWeight[] STRONGHOLD_PIECE_WEIGHTS = new PieceWeight[]{new PieceWeight(Straight.class, 40, 0), new PieceWeight(PrisonHall.class, 5, 5), new PieceWeight(LeftTurn.class, 20, 0), new PieceWeight(RightTurn.class, 20, 0), new PieceWeight(RoomCrossing.class, 10, 6), new PieceWeight(StraightStairsDown.class, 5, 5), new PieceWeight(StairsDown.class, 5, 5), new PieceWeight(FiveCrossing.class, 5, 4), new PieceWeight(ChestCorridor.class, 5, 4), new PieceWeight(Library.class, 10, 2){

        @Override
        public boolean doPlace(int $$0) {
            return super.doPlace($$0) && $$0 > 4;
        }
    }, new PieceWeight(PortalRoom.class, 20, 1){

        @Override
        public boolean doPlace(int $$0) {
            return super.doPlace($$0) && $$0 > 5;
        }
    }};
    private static List<PieceWeight> currentPieces;
    static Class<? extends StrongholdPiece> imposedPiece;
    private static int totalWeight;
    static final SmoothStoneSelector SMOOTH_STONE_SELECTOR;

    public static void resetPieces() {
        currentPieces = Lists.newArrayList();
        for (PieceWeight $$0 : STRONGHOLD_PIECE_WEIGHTS) {
            $$0.placeCount = 0;
            currentPieces.add((Object)$$0);
        }
        imposedPiece = null;
    }

    private static boolean updatePieceWeight() {
        boolean $$0 = false;
        totalWeight = 0;
        for (PieceWeight $$1 : currentPieces) {
            if ($$1.maxPlaceCount > 0 && $$1.placeCount < $$1.maxPlaceCount) {
                $$0 = true;
            }
            totalWeight += $$1.weight;
        }
        return $$0;
    }

    private static StrongholdPiece findAndCreatePieceFactory(Class<? extends StrongholdPiece> $$0, StructurePieceAccessor $$1, RandomSource $$2, int $$3, int $$4, int $$5, @Nullable Direction $$6, int $$7) {
        StrongholdPiece $$8 = null;
        if ($$0 == Straight.class) {
            $$8 = Straight.createPiece($$1, $$2, $$3, $$4, $$5, $$6, $$7);
        } else if ($$0 == PrisonHall.class) {
            $$8 = PrisonHall.createPiece($$1, $$2, $$3, $$4, $$5, $$6, $$7);
        } else if ($$0 == LeftTurn.class) {
            $$8 = LeftTurn.createPiece($$1, $$2, $$3, $$4, $$5, $$6, $$7);
        } else if ($$0 == RightTurn.class) {
            $$8 = RightTurn.createPiece($$1, $$2, $$3, $$4, $$5, $$6, $$7);
        } else if ($$0 == RoomCrossing.class) {
            $$8 = RoomCrossing.createPiece($$1, $$2, $$3, $$4, $$5, $$6, $$7);
        } else if ($$0 == StraightStairsDown.class) {
            $$8 = StraightStairsDown.createPiece($$1, $$2, $$3, $$4, $$5, $$6, $$7);
        } else if ($$0 == StairsDown.class) {
            $$8 = StairsDown.createPiece($$1, $$2, $$3, $$4, $$5, $$6, $$7);
        } else if ($$0 == FiveCrossing.class) {
            $$8 = FiveCrossing.createPiece($$1, $$2, $$3, $$4, $$5, $$6, $$7);
        } else if ($$0 == ChestCorridor.class) {
            $$8 = ChestCorridor.createPiece($$1, $$2, $$3, $$4, $$5, $$6, $$7);
        } else if ($$0 == Library.class) {
            $$8 = Library.createPiece($$1, $$2, $$3, $$4, $$5, $$6, $$7);
        } else if ($$0 == PortalRoom.class) {
            $$8 = PortalRoom.createPiece($$1, $$3, $$4, $$5, $$6, $$7);
        }
        return $$8;
    }

    private static StrongholdPiece generatePieceFromSmallDoor(StartPiece $$0, StructurePieceAccessor $$1, RandomSource $$2, int $$3, int $$4, int $$5, Direction $$6, int $$7) {
        if (!StrongholdPieces.updatePieceWeight()) {
            return null;
        }
        if (imposedPiece != null) {
            StrongholdPiece $$8 = StrongholdPieces.findAndCreatePieceFactory(imposedPiece, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
            imposedPiece = null;
            if ($$8 != null) {
                return $$8;
            }
        }
        int $$9 = 0;
        block0: while ($$9 < 5) {
            ++$$9;
            int $$10 = $$2.nextInt(totalWeight);
            for (PieceWeight $$11 : currentPieces) {
                if (($$10 -= $$11.weight) >= 0) continue;
                if (!$$11.doPlace($$7) || $$11 == $$0.previousPiece) continue block0;
                StrongholdPiece $$12 = StrongholdPieces.findAndCreatePieceFactory($$11.pieceClass, $$1, $$2, $$3, $$4, $$5, $$6, $$7);
                if ($$12 == null) continue;
                ++$$11.placeCount;
                $$0.previousPiece = $$11;
                if (!$$11.isValid()) {
                    currentPieces.remove((Object)$$11);
                }
                return $$12;
            }
        }
        BoundingBox $$13 = FillerCorridor.findPieceBox($$1, $$2, $$3, $$4, $$5, $$6);
        if ($$13 != null && $$13.minY() > 1) {
            return new FillerCorridor($$7, $$13, $$6);
        }
        return null;
    }

    static StructurePiece generateAndAddPiece(StartPiece $$0, StructurePieceAccessor $$1, RandomSource $$2, int $$3, int $$4, int $$5, @Nullable Direction $$6, int $$7) {
        if ($$7 > 50) {
            return null;
        }
        if (Math.abs((int)($$3 - $$0.getBoundingBox().minX())) > 112 || Math.abs((int)($$5 - $$0.getBoundingBox().minZ())) > 112) {
            return null;
        }
        StrongholdPiece $$8 = StrongholdPieces.generatePieceFromSmallDoor($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7 + 1);
        if ($$8 != null) {
            $$1.addPiece($$8);
            $$0.pendingChildren.add((Object)$$8);
        }
        return $$8;
    }

    static {
        SMOOTH_STONE_SELECTOR = new SmoothStoneSelector();
    }

    static class PieceWeight {
        public final Class<? extends StrongholdPiece> pieceClass;
        public final int weight;
        public int placeCount;
        public final int maxPlaceCount;

        public PieceWeight(Class<? extends StrongholdPiece> $$0, int $$1, int $$2) {
            this.pieceClass = $$0;
            this.weight = $$1;
            this.maxPlaceCount = $$2;
        }

        public boolean doPlace(int $$0) {
            return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
        }

        public boolean isValid() {
            return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
        }
    }

    public static class Straight
    extends StrongholdPiece {
        private static final int WIDTH = 5;
        private static final int HEIGHT = 5;
        private static final int DEPTH = 7;
        private final boolean leftChild;
        private final boolean rightChild;

        public Straight(int $$0, RandomSource $$1, BoundingBox $$2, Direction $$3) {
            super(StructurePieceType.STRONGHOLD_STRAIGHT, $$0, $$2);
            this.setOrientation($$3);
            this.entryDoor = this.randomSmallDoor($$1);
            this.leftChild = $$1.nextInt(2) == 0;
            this.rightChild = $$1.nextInt(2) == 0;
        }

        public Straight(CompoundTag $$0) {
            super(StructurePieceType.STRONGHOLD_STRAIGHT, $$0);
            this.leftChild = $$0.getBoolean("Left");
            this.rightChild = $$0.getBoolean("Right");
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext $$0, CompoundTag $$1) {
            super.addAdditionalSaveData($$0, $$1);
            $$1.putBoolean("Left", this.leftChild);
            $$1.putBoolean("Right", this.rightChild);
        }

        @Override
        public void addChildren(StructurePiece $$0, StructurePieceAccessor $$1, RandomSource $$2) {
            this.generateSmallDoorChildForward((StartPiece)$$0, $$1, $$2, 1, 1);
            if (this.leftChild) {
                this.generateSmallDoorChildLeft((StartPiece)$$0, $$1, $$2, 1, 2);
            }
            if (this.rightChild) {
                this.generateSmallDoorChildRight((StartPiece)$$0, $$1, $$2, 1, 2);
            }
        }

        public static Straight createPiece(StructurePieceAccessor $$0, RandomSource $$1, int $$2, int $$3, int $$4, Direction $$5, int $$6) {
            BoundingBox $$7 = BoundingBox.orientBox($$2, $$3, $$4, -1, -1, 0, 5, 5, 7, $$5);
            if (!Straight.isOkBox($$7) || $$0.findCollisionPiece($$7) != null) {
                return null;
            }
            return new Straight($$6, $$1, $$7, $$5);
        }

        @Override
        public void postProcess(WorldGenLevel $$0, StructureManager $$1, ChunkGenerator $$2, RandomSource $$3, BoundingBox $$4, ChunkPos $$5, BlockPos $$6) {
            this.generateBox($$0, $$4, 0, 0, 0, 4, 4, 6, true, $$3, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor($$0, $$3, $$4, this.entryDoor, 1, 1, 0);
            this.generateSmallDoor($$0, $$3, $$4, StrongholdPiece.SmallDoorType.OPENING, 1, 1, 6);
            BlockState $$7 = (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.EAST);
            BlockState $$8 = (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.WEST);
            this.maybeGenerateBlock($$0, $$4, $$3, 0.1f, 1, 2, 1, $$7);
            this.maybeGenerateBlock($$0, $$4, $$3, 0.1f, 3, 2, 1, $$8);
            this.maybeGenerateBlock($$0, $$4, $$3, 0.1f, 1, 2, 5, $$7);
            this.maybeGenerateBlock($$0, $$4, $$3, 0.1f, 3, 2, 5, $$8);
            if (this.leftChild) {
                this.generateBox($$0, $$4, 0, 1, 2, 0, 3, 4, CAVE_AIR, CAVE_AIR, false);
            }
            if (this.rightChild) {
                this.generateBox($$0, $$4, 4, 1, 2, 4, 3, 4, CAVE_AIR, CAVE_AIR, false);
            }
        }
    }

    public static class PrisonHall
    extends StrongholdPiece {
        protected static final int WIDTH = 9;
        protected static final int HEIGHT = 5;
        protected static final int DEPTH = 11;

        public PrisonHall(int $$0, RandomSource $$1, BoundingBox $$2, Direction $$3) {
            super(StructurePieceType.STRONGHOLD_PRISON_HALL, $$0, $$2);
            this.setOrientation($$3);
            this.entryDoor = this.randomSmallDoor($$1);
        }

        public PrisonHall(CompoundTag $$0) {
            super(StructurePieceType.STRONGHOLD_PRISON_HALL, $$0);
        }

        @Override
        public void addChildren(StructurePiece $$0, StructurePieceAccessor $$1, RandomSource $$2) {
            this.generateSmallDoorChildForward((StartPiece)$$0, $$1, $$2, 1, 1);
        }

        public static PrisonHall createPiece(StructurePieceAccessor $$0, RandomSource $$1, int $$2, int $$3, int $$4, Direction $$5, int $$6) {
            BoundingBox $$7 = BoundingBox.orientBox($$2, $$3, $$4, -1, -1, 0, 9, 5, 11, $$5);
            if (!PrisonHall.isOkBox($$7) || $$0.findCollisionPiece($$7) != null) {
                return null;
            }
            return new PrisonHall($$6, $$1, $$7, $$5);
        }

        @Override
        public void postProcess(WorldGenLevel $$0, StructureManager $$1, ChunkGenerator $$2, RandomSource $$3, BoundingBox $$4, ChunkPos $$5, BlockPos $$6) {
            this.generateBox($$0, $$4, 0, 0, 0, 8, 4, 10, true, $$3, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor($$0, $$3, $$4, this.entryDoor, 1, 1, 0);
            this.generateBox($$0, $$4, 1, 1, 10, 3, 3, 10, CAVE_AIR, CAVE_AIR, false);
            this.generateBox($$0, $$4, 4, 1, 1, 4, 3, 1, false, $$3, SMOOTH_STONE_SELECTOR);
            this.generateBox($$0, $$4, 4, 1, 3, 4, 3, 3, false, $$3, SMOOTH_STONE_SELECTOR);
            this.generateBox($$0, $$4, 4, 1, 7, 4, 3, 7, false, $$3, SMOOTH_STONE_SELECTOR);
            this.generateBox($$0, $$4, 4, 1, 9, 4, 3, 9, false, $$3, SMOOTH_STONE_SELECTOR);
            for (int $$7 = 1; $$7 <= 3; ++$$7) {
                this.placeBlock($$0, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true), 4, $$7, 4, $$4);
                this.placeBlock($$0, (BlockState)((BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true)).setValue(IronBarsBlock.EAST, true), 4, $$7, 5, $$4);
                this.placeBlock($$0, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true), 4, $$7, 6, $$4);
                this.placeBlock($$0, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, true)).setValue(IronBarsBlock.EAST, true), 5, $$7, 5, $$4);
                this.placeBlock($$0, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, true)).setValue(IronBarsBlock.EAST, true), 6, $$7, 5, $$4);
                this.placeBlock($$0, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, true)).setValue(IronBarsBlock.EAST, true), 7, $$7, 5, $$4);
            }
            this.placeBlock($$0, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true), 4, 3, 2, $$4);
            this.placeBlock($$0, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true), 4, 3, 8, $$4);
            BlockState $$8 = (BlockState)Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.FACING, Direction.WEST);
            BlockState $$9 = (BlockState)((BlockState)Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.FACING, Direction.WEST)).setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER);
            this.placeBlock($$0, $$8, 4, 1, 2, $$4);
            this.placeBlock($$0, $$9, 4, 2, 2, $$4);
            this.placeBlock($$0, $$8, 4, 1, 8, $$4);
            this.placeBlock($$0, $$9, 4, 2, 8, $$4);
        }
    }

    public static class LeftTurn
    extends Turn {
        public LeftTurn(int $$0, RandomSource $$1, BoundingBox $$2, Direction $$3) {
            super(StructurePieceType.STRONGHOLD_LEFT_TURN, $$0, $$2);
            this.setOrientation($$3);
            this.entryDoor = this.randomSmallDoor($$1);
        }

        public LeftTurn(CompoundTag $$0) {
            super(StructurePieceType.STRONGHOLD_LEFT_TURN, $$0);
        }

        @Override
        public void addChildren(StructurePiece $$0, StructurePieceAccessor $$1, RandomSource $$2) {
            Direction $$3 = this.getOrientation();
            if ($$3 == Direction.NORTH || $$3 == Direction.EAST) {
                this.generateSmallDoorChildLeft((StartPiece)$$0, $$1, $$2, 1, 1);
            } else {
                this.generateSmallDoorChildRight((StartPiece)$$0, $$1, $$2, 1, 1);
            }
        }

        public static LeftTurn createPiece(StructurePieceAccessor $$0, RandomSource $$1, int $$2, int $$3, int $$4, Direction $$5, int $$6) {
            BoundingBox $$7 = BoundingBox.orientBox($$2, $$3, $$4, -1, -1, 0, 5, 5, 5, $$5);
            if (!LeftTurn.isOkBox($$7) || $$0.findCollisionPiece($$7) != null) {
                return null;
            }
            return new LeftTurn($$6, $$1, $$7, $$5);
        }

        @Override
        public void postProcess(WorldGenLevel $$0, StructureManager $$1, ChunkGenerator $$2, RandomSource $$3, BoundingBox $$4, ChunkPos $$5, BlockPos $$6) {
            this.generateBox($$0, $$4, 0, 0, 0, 4, 4, 4, true, $$3, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor($$0, $$3, $$4, this.entryDoor, 1, 1, 0);
            Direction $$7 = this.getOrientation();
            if ($$7 == Direction.NORTH || $$7 == Direction.EAST) {
                this.generateBox($$0, $$4, 0, 1, 1, 0, 3, 3, CAVE_AIR, CAVE_AIR, false);
            } else {
                this.generateBox($$0, $$4, 4, 1, 1, 4, 3, 3, CAVE_AIR, CAVE_AIR, false);
            }
        }
    }

    public static class RightTurn
    extends Turn {
        public RightTurn(int $$0, RandomSource $$1, BoundingBox $$2, Direction $$3) {
            super(StructurePieceType.STRONGHOLD_RIGHT_TURN, $$0, $$2);
            this.setOrientation($$3);
            this.entryDoor = this.randomSmallDoor($$1);
        }

        public RightTurn(CompoundTag $$0) {
            super(StructurePieceType.STRONGHOLD_RIGHT_TURN, $$0);
        }

        @Override
        public void addChildren(StructurePiece $$0, StructurePieceAccessor $$1, RandomSource $$2) {
            Direction $$3 = this.getOrientation();
            if ($$3 == Direction.NORTH || $$3 == Direction.EAST) {
                this.generateSmallDoorChildRight((StartPiece)$$0, $$1, $$2, 1, 1);
            } else {
                this.generateSmallDoorChildLeft((StartPiece)$$0, $$1, $$2, 1, 1);
            }
        }

        public static RightTurn createPiece(StructurePieceAccessor $$0, RandomSource $$1, int $$2, int $$3, int $$4, Direction $$5, int $$6) {
            BoundingBox $$7 = BoundingBox.orientBox($$2, $$3, $$4, -1, -1, 0, 5, 5, 5, $$5);
            if (!RightTurn.isOkBox($$7) || $$0.findCollisionPiece($$7) != null) {
                return null;
            }
            return new RightTurn($$6, $$1, $$7, $$5);
        }

        @Override
        public void postProcess(WorldGenLevel $$0, StructureManager $$1, ChunkGenerator $$2, RandomSource $$3, BoundingBox $$4, ChunkPos $$5, BlockPos $$6) {
            this.generateBox($$0, $$4, 0, 0, 0, 4, 4, 4, true, $$3, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor($$0, $$3, $$4, this.entryDoor, 1, 1, 0);
            Direction $$7 = this.getOrientation();
            if ($$7 == Direction.NORTH || $$7 == Direction.EAST) {
                this.generateBox($$0, $$4, 4, 1, 1, 4, 3, 3, CAVE_AIR, CAVE_AIR, false);
            } else {
                this.generateBox($$0, $$4, 0, 1, 1, 0, 3, 3, CAVE_AIR, CAVE_AIR, false);
            }
        }
    }

    public static class RoomCrossing
    extends StrongholdPiece {
        protected static final int WIDTH = 11;
        protected static final int HEIGHT = 7;
        protected static final int DEPTH = 11;
        protected final int type;

        public RoomCrossing(int $$0, RandomSource $$1, BoundingBox $$2, Direction $$3) {
            super(StructurePieceType.STRONGHOLD_ROOM_CROSSING, $$0, $$2);
            this.setOrientation($$3);
            this.entryDoor = this.randomSmallDoor($$1);
            this.type = $$1.nextInt(5);
        }

        public RoomCrossing(CompoundTag $$0) {
            super(StructurePieceType.STRONGHOLD_ROOM_CROSSING, $$0);
            this.type = $$0.getInt("Type");
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext $$0, CompoundTag $$1) {
            super.addAdditionalSaveData($$0, $$1);
            $$1.putInt("Type", this.type);
        }

        @Override
        public void addChildren(StructurePiece $$0, StructurePieceAccessor $$1, RandomSource $$2) {
            this.generateSmallDoorChildForward((StartPiece)$$0, $$1, $$2, 4, 1);
            this.generateSmallDoorChildLeft((StartPiece)$$0, $$1, $$2, 1, 4);
            this.generateSmallDoorChildRight((StartPiece)$$0, $$1, $$2, 1, 4);
        }

        public static RoomCrossing createPiece(StructurePieceAccessor $$0, RandomSource $$1, int $$2, int $$3, int $$4, Direction $$5, int $$6) {
            BoundingBox $$7 = BoundingBox.orientBox($$2, $$3, $$4, -4, -1, 0, 11, 7, 11, $$5);
            if (!RoomCrossing.isOkBox($$7) || $$0.findCollisionPiece($$7) != null) {
                return null;
            }
            return new RoomCrossing($$6, $$1, $$7, $$5);
        }

        @Override
        public void postProcess(WorldGenLevel $$0, StructureManager $$1, ChunkGenerator $$2, RandomSource $$3, BoundingBox $$4, ChunkPos $$5, BlockPos $$6) {
            this.generateBox($$0, $$4, 0, 0, 0, 10, 6, 10, true, $$3, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor($$0, $$3, $$4, this.entryDoor, 4, 1, 0);
            this.generateBox($$0, $$4, 4, 1, 10, 6, 3, 10, CAVE_AIR, CAVE_AIR, false);
            this.generateBox($$0, $$4, 0, 1, 4, 0, 3, 6, CAVE_AIR, CAVE_AIR, false);
            this.generateBox($$0, $$4, 10, 1, 4, 10, 3, 6, CAVE_AIR, CAVE_AIR, false);
            switch (this.type) {
                default: {
                    break;
                }
                case 0: {
                    this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 5, 1, 5, $$4);
                    this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 5, 2, 5, $$4);
                    this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 5, 3, 5, $$4);
                    this.placeBlock($$0, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.WEST), 4, 3, 5, $$4);
                    this.placeBlock($$0, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.EAST), 6, 3, 5, $$4);
                    this.placeBlock($$0, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.SOUTH), 5, 3, 4, $$4);
                    this.placeBlock($$0, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.NORTH), 5, 3, 6, $$4);
                    this.placeBlock($$0, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 4, 1, 4, $$4);
                    this.placeBlock($$0, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 4, 1, 5, $$4);
                    this.placeBlock($$0, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 4, 1, 6, $$4);
                    this.placeBlock($$0, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 6, 1, 4, $$4);
                    this.placeBlock($$0, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 6, 1, 5, $$4);
                    this.placeBlock($$0, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 6, 1, 6, $$4);
                    this.placeBlock($$0, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 5, 1, 4, $$4);
                    this.placeBlock($$0, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 5, 1, 6, $$4);
                    break;
                }
                case 1: {
                    for (int $$7 = 0; $$7 < 5; ++$$7) {
                        this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 3, 1, 3 + $$7, $$4);
                        this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 7, 1, 3 + $$7, $$4);
                        this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 3 + $$7, 1, 3, $$4);
                        this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 3 + $$7, 1, 7, $$4);
                    }
                    this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 5, 1, 5, $$4);
                    this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 5, 2, 5, $$4);
                    this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 5, 3, 5, $$4);
                    this.placeBlock($$0, Blocks.WATER.defaultBlockState(), 5, 4, 5, $$4);
                    break;
                }
                case 2: {
                    for (int $$8 = 1; $$8 <= 9; ++$$8) {
                        this.placeBlock($$0, Blocks.COBBLESTONE.defaultBlockState(), 1, 3, $$8, $$4);
                        this.placeBlock($$0, Blocks.COBBLESTONE.defaultBlockState(), 9, 3, $$8, $$4);
                    }
                    for (int $$9 = 1; $$9 <= 9; ++$$9) {
                        this.placeBlock($$0, Blocks.COBBLESTONE.defaultBlockState(), $$9, 3, 1, $$4);
                        this.placeBlock($$0, Blocks.COBBLESTONE.defaultBlockState(), $$9, 3, 9, $$4);
                    }
                    this.placeBlock($$0, Blocks.COBBLESTONE.defaultBlockState(), 5, 1, 4, $$4);
                    this.placeBlock($$0, Blocks.COBBLESTONE.defaultBlockState(), 5, 1, 6, $$4);
                    this.placeBlock($$0, Blocks.COBBLESTONE.defaultBlockState(), 5, 3, 4, $$4);
                    this.placeBlock($$0, Blocks.COBBLESTONE.defaultBlockState(), 5, 3, 6, $$4);
                    this.placeBlock($$0, Blocks.COBBLESTONE.defaultBlockState(), 4, 1, 5, $$4);
                    this.placeBlock($$0, Blocks.COBBLESTONE.defaultBlockState(), 6, 1, 5, $$4);
                    this.placeBlock($$0, Blocks.COBBLESTONE.defaultBlockState(), 4, 3, 5, $$4);
                    this.placeBlock($$0, Blocks.COBBLESTONE.defaultBlockState(), 6, 3, 5, $$4);
                    for (int $$10 = 1; $$10 <= 3; ++$$10) {
                        this.placeBlock($$0, Blocks.COBBLESTONE.defaultBlockState(), 4, $$10, 4, $$4);
                        this.placeBlock($$0, Blocks.COBBLESTONE.defaultBlockState(), 6, $$10, 4, $$4);
                        this.placeBlock($$0, Blocks.COBBLESTONE.defaultBlockState(), 4, $$10, 6, $$4);
                        this.placeBlock($$0, Blocks.COBBLESTONE.defaultBlockState(), 6, $$10, 6, $$4);
                    }
                    this.placeBlock($$0, Blocks.TORCH.defaultBlockState(), 5, 3, 5, $$4);
                    for (int $$11 = 2; $$11 <= 8; ++$$11) {
                        this.placeBlock($$0, Blocks.OAK_PLANKS.defaultBlockState(), 2, 3, $$11, $$4);
                        this.placeBlock($$0, Blocks.OAK_PLANKS.defaultBlockState(), 3, 3, $$11, $$4);
                        if ($$11 <= 3 || $$11 >= 7) {
                            this.placeBlock($$0, Blocks.OAK_PLANKS.defaultBlockState(), 4, 3, $$11, $$4);
                            this.placeBlock($$0, Blocks.OAK_PLANKS.defaultBlockState(), 5, 3, $$11, $$4);
                            this.placeBlock($$0, Blocks.OAK_PLANKS.defaultBlockState(), 6, 3, $$11, $$4);
                        }
                        this.placeBlock($$0, Blocks.OAK_PLANKS.defaultBlockState(), 7, 3, $$11, $$4);
                        this.placeBlock($$0, Blocks.OAK_PLANKS.defaultBlockState(), 8, 3, $$11, $$4);
                    }
                    BlockState $$12 = (BlockState)Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.WEST);
                    this.placeBlock($$0, $$12, 9, 1, 3, $$4);
                    this.placeBlock($$0, $$12, 9, 2, 3, $$4);
                    this.placeBlock($$0, $$12, 9, 3, 3, $$4);
                    this.createChest($$0, $$4, $$3, 3, 4, 8, BuiltInLootTables.STRONGHOLD_CROSSING);
                }
            }
        }
    }

    public static class StraightStairsDown
    extends StrongholdPiece {
        private static final int WIDTH = 5;
        private static final int HEIGHT = 11;
        private static final int DEPTH = 8;

        public StraightStairsDown(int $$0, RandomSource $$1, BoundingBox $$2, Direction $$3) {
            super(StructurePieceType.STRONGHOLD_STRAIGHT_STAIRS_DOWN, $$0, $$2);
            this.setOrientation($$3);
            this.entryDoor = this.randomSmallDoor($$1);
        }

        public StraightStairsDown(CompoundTag $$0) {
            super(StructurePieceType.STRONGHOLD_STRAIGHT_STAIRS_DOWN, $$0);
        }

        @Override
        public void addChildren(StructurePiece $$0, StructurePieceAccessor $$1, RandomSource $$2) {
            this.generateSmallDoorChildForward((StartPiece)$$0, $$1, $$2, 1, 1);
        }

        public static StraightStairsDown createPiece(StructurePieceAccessor $$0, RandomSource $$1, int $$2, int $$3, int $$4, Direction $$5, int $$6) {
            BoundingBox $$7 = BoundingBox.orientBox($$2, $$3, $$4, -1, -7, 0, 5, 11, 8, $$5);
            if (!StraightStairsDown.isOkBox($$7) || $$0.findCollisionPiece($$7) != null) {
                return null;
            }
            return new StraightStairsDown($$6, $$1, $$7, $$5);
        }

        @Override
        public void postProcess(WorldGenLevel $$0, StructureManager $$1, ChunkGenerator $$2, RandomSource $$3, BoundingBox $$4, ChunkPos $$5, BlockPos $$6) {
            this.generateBox($$0, $$4, 0, 0, 0, 4, 10, 7, true, $$3, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor($$0, $$3, $$4, this.entryDoor, 1, 7, 0);
            this.generateSmallDoor($$0, $$3, $$4, StrongholdPiece.SmallDoorType.OPENING, 1, 1, 7);
            BlockState $$7 = (BlockState)Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);
            for (int $$8 = 0; $$8 < 6; ++$$8) {
                this.placeBlock($$0, $$7, 1, 6 - $$8, 1 + $$8, $$4);
                this.placeBlock($$0, $$7, 2, 6 - $$8, 1 + $$8, $$4);
                this.placeBlock($$0, $$7, 3, 6 - $$8, 1 + $$8, $$4);
                if ($$8 >= 5) continue;
                this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 1, 5 - $$8, 1 + $$8, $$4);
                this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 2, 5 - $$8, 1 + $$8, $$4);
                this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 3, 5 - $$8, 1 + $$8, $$4);
            }
        }
    }

    public static class StairsDown
    extends StrongholdPiece {
        private static final int WIDTH = 5;
        private static final int HEIGHT = 11;
        private static final int DEPTH = 5;
        private final boolean isSource;

        public StairsDown(StructurePieceType $$0, int $$1, int $$2, int $$3, Direction $$4) {
            super($$0, $$1, StairsDown.makeBoundingBox($$2, 64, $$3, $$4, 5, 11, 5));
            this.isSource = true;
            this.setOrientation($$4);
            this.entryDoor = StrongholdPiece.SmallDoorType.OPENING;
        }

        public StairsDown(int $$0, RandomSource $$1, BoundingBox $$2, Direction $$3) {
            super(StructurePieceType.STRONGHOLD_STAIRS_DOWN, $$0, $$2);
            this.isSource = false;
            this.setOrientation($$3);
            this.entryDoor = this.randomSmallDoor($$1);
        }

        public StairsDown(StructurePieceType $$0, CompoundTag $$1) {
            super($$0, $$1);
            this.isSource = $$1.getBoolean("Source");
        }

        public StairsDown(CompoundTag $$0) {
            this(StructurePieceType.STRONGHOLD_STAIRS_DOWN, $$0);
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext $$0, CompoundTag $$1) {
            super.addAdditionalSaveData($$0, $$1);
            $$1.putBoolean("Source", this.isSource);
        }

        @Override
        public void addChildren(StructurePiece $$0, StructurePieceAccessor $$1, RandomSource $$2) {
            if (this.isSource) {
                imposedPiece = FiveCrossing.class;
            }
            this.generateSmallDoorChildForward((StartPiece)$$0, $$1, $$2, 1, 1);
        }

        public static StairsDown createPiece(StructurePieceAccessor $$0, RandomSource $$1, int $$2, int $$3, int $$4, Direction $$5, int $$6) {
            BoundingBox $$7 = BoundingBox.orientBox($$2, $$3, $$4, -1, -7, 0, 5, 11, 5, $$5);
            if (!StairsDown.isOkBox($$7) || $$0.findCollisionPiece($$7) != null) {
                return null;
            }
            return new StairsDown($$6, $$1, $$7, $$5);
        }

        @Override
        public void postProcess(WorldGenLevel $$0, StructureManager $$1, ChunkGenerator $$2, RandomSource $$3, BoundingBox $$4, ChunkPos $$5, BlockPos $$6) {
            this.generateBox($$0, $$4, 0, 0, 0, 4, 10, 4, true, $$3, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor($$0, $$3, $$4, this.entryDoor, 1, 7, 0);
            this.generateSmallDoor($$0, $$3, $$4, StrongholdPiece.SmallDoorType.OPENING, 1, 1, 4);
            this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 2, 6, 1, $$4);
            this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 1, 5, 1, $$4);
            this.placeBlock($$0, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 6, 1, $$4);
            this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 1, 5, 2, $$4);
            this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 1, 4, 3, $$4);
            this.placeBlock($$0, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 5, 3, $$4);
            this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 2, 4, 3, $$4);
            this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 3, 3, 3, $$4);
            this.placeBlock($$0, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 3, 4, 3, $$4);
            this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 3, 3, 2, $$4);
            this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 3, 2, 1, $$4);
            this.placeBlock($$0, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 3, 3, 1, $$4);
            this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 2, 2, 1, $$4);
            this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 1, 1, 1, $$4);
            this.placeBlock($$0, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 2, 1, $$4);
            this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 1, 1, 2, $$4);
            this.placeBlock($$0, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 1, 3, $$4);
        }
    }

    public static class FiveCrossing
    extends StrongholdPiece {
        protected static final int WIDTH = 10;
        protected static final int HEIGHT = 9;
        protected static final int DEPTH = 11;
        private final boolean leftLow;
        private final boolean leftHigh;
        private final boolean rightLow;
        private final boolean rightHigh;

        public FiveCrossing(int $$0, RandomSource $$1, BoundingBox $$2, Direction $$3) {
            super(StructurePieceType.STRONGHOLD_FIVE_CROSSING, $$0, $$2);
            this.setOrientation($$3);
            this.entryDoor = this.randomSmallDoor($$1);
            this.leftLow = $$1.nextBoolean();
            this.leftHigh = $$1.nextBoolean();
            this.rightLow = $$1.nextBoolean();
            this.rightHigh = $$1.nextInt(3) > 0;
        }

        public FiveCrossing(CompoundTag $$0) {
            super(StructurePieceType.STRONGHOLD_FIVE_CROSSING, $$0);
            this.leftLow = $$0.getBoolean("leftLow");
            this.leftHigh = $$0.getBoolean("leftHigh");
            this.rightLow = $$0.getBoolean("rightLow");
            this.rightHigh = $$0.getBoolean("rightHigh");
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext $$0, CompoundTag $$1) {
            super.addAdditionalSaveData($$0, $$1);
            $$1.putBoolean("leftLow", this.leftLow);
            $$1.putBoolean("leftHigh", this.leftHigh);
            $$1.putBoolean("rightLow", this.rightLow);
            $$1.putBoolean("rightHigh", this.rightHigh);
        }

        @Override
        public void addChildren(StructurePiece $$0, StructurePieceAccessor $$1, RandomSource $$2) {
            int $$3 = 3;
            int $$4 = 5;
            Direction $$5 = this.getOrientation();
            if ($$5 == Direction.WEST || $$5 == Direction.NORTH) {
                $$3 = 8 - $$3;
                $$4 = 8 - $$4;
            }
            this.generateSmallDoorChildForward((StartPiece)$$0, $$1, $$2, 5, 1);
            if (this.leftLow) {
                this.generateSmallDoorChildLeft((StartPiece)$$0, $$1, $$2, $$3, 1);
            }
            if (this.leftHigh) {
                this.generateSmallDoorChildLeft((StartPiece)$$0, $$1, $$2, $$4, 7);
            }
            if (this.rightLow) {
                this.generateSmallDoorChildRight((StartPiece)$$0, $$1, $$2, $$3, 1);
            }
            if (this.rightHigh) {
                this.generateSmallDoorChildRight((StartPiece)$$0, $$1, $$2, $$4, 7);
            }
        }

        public static FiveCrossing createPiece(StructurePieceAccessor $$0, RandomSource $$1, int $$2, int $$3, int $$4, Direction $$5, int $$6) {
            BoundingBox $$7 = BoundingBox.orientBox($$2, $$3, $$4, -4, -3, 0, 10, 9, 11, $$5);
            if (!FiveCrossing.isOkBox($$7) || $$0.findCollisionPiece($$7) != null) {
                return null;
            }
            return new FiveCrossing($$6, $$1, $$7, $$5);
        }

        @Override
        public void postProcess(WorldGenLevel $$0, StructureManager $$1, ChunkGenerator $$2, RandomSource $$3, BoundingBox $$4, ChunkPos $$5, BlockPos $$6) {
            this.generateBox($$0, $$4, 0, 0, 0, 9, 8, 10, true, $$3, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor($$0, $$3, $$4, this.entryDoor, 4, 3, 0);
            if (this.leftLow) {
                this.generateBox($$0, $$4, 0, 3, 1, 0, 5, 3, CAVE_AIR, CAVE_AIR, false);
            }
            if (this.rightLow) {
                this.generateBox($$0, $$4, 9, 3, 1, 9, 5, 3, CAVE_AIR, CAVE_AIR, false);
            }
            if (this.leftHigh) {
                this.generateBox($$0, $$4, 0, 5, 7, 0, 7, 9, CAVE_AIR, CAVE_AIR, false);
            }
            if (this.rightHigh) {
                this.generateBox($$0, $$4, 9, 5, 7, 9, 7, 9, CAVE_AIR, CAVE_AIR, false);
            }
            this.generateBox($$0, $$4, 5, 1, 10, 7, 3, 10, CAVE_AIR, CAVE_AIR, false);
            this.generateBox($$0, $$4, 1, 2, 1, 8, 2, 6, false, $$3, SMOOTH_STONE_SELECTOR);
            this.generateBox($$0, $$4, 4, 1, 5, 4, 4, 9, false, $$3, SMOOTH_STONE_SELECTOR);
            this.generateBox($$0, $$4, 8, 1, 5, 8, 4, 9, false, $$3, SMOOTH_STONE_SELECTOR);
            this.generateBox($$0, $$4, 1, 4, 7, 3, 4, 9, false, $$3, SMOOTH_STONE_SELECTOR);
            this.generateBox($$0, $$4, 1, 3, 5, 3, 3, 6, false, $$3, SMOOTH_STONE_SELECTOR);
            this.generateBox($$0, $$4, 1, 3, 4, 3, 3, 4, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
            this.generateBox($$0, $$4, 1, 4, 6, 3, 4, 6, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
            this.generateBox($$0, $$4, 5, 1, 7, 7, 1, 8, false, $$3, SMOOTH_STONE_SELECTOR);
            this.generateBox($$0, $$4, 5, 1, 9, 7, 1, 9, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
            this.generateBox($$0, $$4, 5, 2, 7, 7, 2, 7, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
            this.generateBox($$0, $$4, 4, 5, 7, 4, 5, 9, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
            this.generateBox($$0, $$4, 8, 5, 7, 8, 5, 9, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
            this.generateBox($$0, $$4, 5, 5, 7, 7, 5, 9, (BlockState)Blocks.SMOOTH_STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE), (BlockState)Blocks.SMOOTH_STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE), false);
            this.placeBlock($$0, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.SOUTH), 6, 5, 6, $$4);
        }
    }

    public static class ChestCorridor
    extends StrongholdPiece {
        private static final int WIDTH = 5;
        private static final int HEIGHT = 5;
        private static final int DEPTH = 7;
        private boolean hasPlacedChest;

        public ChestCorridor(int $$0, RandomSource $$1, BoundingBox $$2, Direction $$3) {
            super(StructurePieceType.STRONGHOLD_CHEST_CORRIDOR, $$0, $$2);
            this.setOrientation($$3);
            this.entryDoor = this.randomSmallDoor($$1);
        }

        public ChestCorridor(CompoundTag $$0) {
            super(StructurePieceType.STRONGHOLD_CHEST_CORRIDOR, $$0);
            this.hasPlacedChest = $$0.getBoolean("Chest");
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext $$0, CompoundTag $$1) {
            super.addAdditionalSaveData($$0, $$1);
            $$1.putBoolean("Chest", this.hasPlacedChest);
        }

        @Override
        public void addChildren(StructurePiece $$0, StructurePieceAccessor $$1, RandomSource $$2) {
            this.generateSmallDoorChildForward((StartPiece)$$0, $$1, $$2, 1, 1);
        }

        public static ChestCorridor createPiece(StructurePieceAccessor $$0, RandomSource $$1, int $$2, int $$3, int $$4, Direction $$5, int $$6) {
            BoundingBox $$7 = BoundingBox.orientBox($$2, $$3, $$4, -1, -1, 0, 5, 5, 7, $$5);
            if (!ChestCorridor.isOkBox($$7) || $$0.findCollisionPiece($$7) != null) {
                return null;
            }
            return new ChestCorridor($$6, $$1, $$7, $$5);
        }

        @Override
        public void postProcess(WorldGenLevel $$0, StructureManager $$1, ChunkGenerator $$2, RandomSource $$3, BoundingBox $$4, ChunkPos $$5, BlockPos $$6) {
            this.generateBox($$0, $$4, 0, 0, 0, 4, 4, 6, true, $$3, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor($$0, $$3, $$4, this.entryDoor, 1, 1, 0);
            this.generateSmallDoor($$0, $$3, $$4, StrongholdPiece.SmallDoorType.OPENING, 1, 1, 6);
            this.generateBox($$0, $$4, 3, 1, 2, 3, 1, 4, Blocks.STONE_BRICKS.defaultBlockState(), Blocks.STONE_BRICKS.defaultBlockState(), false);
            this.placeBlock($$0, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 1, 1, $$4);
            this.placeBlock($$0, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 1, 5, $$4);
            this.placeBlock($$0, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 2, 2, $$4);
            this.placeBlock($$0, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 2, 4, $$4);
            for (int $$7 = 2; $$7 <= 4; ++$$7) {
                this.placeBlock($$0, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 2, 1, $$7, $$4);
            }
            if (!this.hasPlacedChest && $$4.isInside(this.getWorldPos(3, 2, 3))) {
                this.hasPlacedChest = true;
                this.createChest($$0, $$4, $$3, 3, 2, 3, BuiltInLootTables.STRONGHOLD_CORRIDOR);
            }
        }
    }

    public static class Library
    extends StrongholdPiece {
        protected static final int WIDTH = 14;
        protected static final int HEIGHT = 6;
        protected static final int TALL_HEIGHT = 11;
        protected static final int DEPTH = 15;
        private final boolean isTall;

        public Library(int $$0, RandomSource $$1, BoundingBox $$2, Direction $$3) {
            super(StructurePieceType.STRONGHOLD_LIBRARY, $$0, $$2);
            this.setOrientation($$3);
            this.entryDoor = this.randomSmallDoor($$1);
            this.isTall = $$2.getYSpan() > 6;
        }

        public Library(CompoundTag $$0) {
            super(StructurePieceType.STRONGHOLD_LIBRARY, $$0);
            this.isTall = $$0.getBoolean("Tall");
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext $$0, CompoundTag $$1) {
            super.addAdditionalSaveData($$0, $$1);
            $$1.putBoolean("Tall", this.isTall);
        }

        public static Library createPiece(StructurePieceAccessor $$0, RandomSource $$1, int $$2, int $$3, int $$4, Direction $$5, int $$6) {
            BoundingBox $$7 = BoundingBox.orientBox($$2, $$3, $$4, -4, -1, 0, 14, 11, 15, $$5);
            if (!(Library.isOkBox($$7) && $$0.findCollisionPiece($$7) == null || Library.isOkBox($$7 = BoundingBox.orientBox($$2, $$3, $$4, -4, -1, 0, 14, 6, 15, $$5)) && $$0.findCollisionPiece($$7) == null)) {
                return null;
            }
            return new Library($$6, $$1, $$7, $$5);
        }

        @Override
        public void postProcess(WorldGenLevel $$0, StructureManager $$1, ChunkGenerator $$2, RandomSource $$3, BoundingBox $$4, ChunkPos $$5, BlockPos $$6) {
            int $$7 = 11;
            if (!this.isTall) {
                $$7 = 6;
            }
            this.generateBox($$0, $$4, 0, 0, 0, 13, $$7 - 1, 14, true, $$3, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor($$0, $$3, $$4, this.entryDoor, 4, 1, 0);
            this.generateMaybeBox($$0, $$4, $$3, 0.07f, 2, 1, 1, 11, 4, 13, Blocks.COBWEB.defaultBlockState(), Blocks.COBWEB.defaultBlockState(), false, false);
            boolean $$8 = true;
            int $$9 = 12;
            for (int $$10 = 1; $$10 <= 13; ++$$10) {
                if (($$10 - 1) % 4 == 0) {
                    this.generateBox($$0, $$4, 1, 1, $$10, 1, 4, $$10, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
                    this.generateBox($$0, $$4, 12, 1, $$10, 12, 4, $$10, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
                    this.placeBlock($$0, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.EAST), 2, 3, $$10, $$4);
                    this.placeBlock($$0, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.WEST), 11, 3, $$10, $$4);
                    if (!this.isTall) continue;
                    this.generateBox($$0, $$4, 1, 6, $$10, 1, 9, $$10, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
                    this.generateBox($$0, $$4, 12, 6, $$10, 12, 9, $$10, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
                    continue;
                }
                this.generateBox($$0, $$4, 1, 1, $$10, 1, 4, $$10, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
                this.generateBox($$0, $$4, 12, 1, $$10, 12, 4, $$10, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
                if (!this.isTall) continue;
                this.generateBox($$0, $$4, 1, 6, $$10, 1, 9, $$10, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
                this.generateBox($$0, $$4, 12, 6, $$10, 12, 9, $$10, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
            }
            for (int $$11 = 3; $$11 < 12; $$11 += 2) {
                this.generateBox($$0, $$4, 3, 1, $$11, 4, 3, $$11, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
                this.generateBox($$0, $$4, 6, 1, $$11, 7, 3, $$11, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
                this.generateBox($$0, $$4, 9, 1, $$11, 10, 3, $$11, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
            }
            if (this.isTall) {
                this.generateBox($$0, $$4, 1, 5, 1, 3, 5, 13, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
                this.generateBox($$0, $$4, 10, 5, 1, 12, 5, 13, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
                this.generateBox($$0, $$4, 4, 5, 1, 9, 5, 2, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
                this.generateBox($$0, $$4, 4, 5, 12, 9, 5, 13, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
                this.placeBlock($$0, Blocks.OAK_PLANKS.defaultBlockState(), 9, 5, 11, $$4);
                this.placeBlock($$0, Blocks.OAK_PLANKS.defaultBlockState(), 8, 5, 11, $$4);
                this.placeBlock($$0, Blocks.OAK_PLANKS.defaultBlockState(), 9, 5, 10, $$4);
                BlockState $$12 = (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true)).setValue(FenceBlock.EAST, true);
                BlockState $$13 = (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.SOUTH, true);
                this.generateBox($$0, $$4, 3, 6, 3, 3, 6, 11, $$13, $$13, false);
                this.generateBox($$0, $$4, 10, 6, 3, 10, 6, 9, $$13, $$13, false);
                this.generateBox($$0, $$4, 4, 6, 2, 9, 6, 2, $$12, $$12, false);
                this.generateBox($$0, $$4, 4, 6, 12, 7, 6, 12, $$12, $$12, false);
                this.placeBlock($$0, (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.EAST, true), 3, 6, 2, $$4);
                this.placeBlock($$0, (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, true)).setValue(FenceBlock.EAST, true), 3, 6, 12, $$4);
                this.placeBlock($$0, (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.WEST, true), 10, 6, 2, $$4);
                for (int $$14 = 0; $$14 <= 2; ++$$14) {
                    this.placeBlock($$0, (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, true)).setValue(FenceBlock.WEST, true), 8 + $$14, 6, 12 - $$14, $$4);
                    if ($$14 == 2) continue;
                    this.placeBlock($$0, (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.EAST, true), 8 + $$14, 6, 11 - $$14, $$4);
                }
                BlockState $$15 = (BlockState)Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.SOUTH);
                this.placeBlock($$0, $$15, 10, 1, 13, $$4);
                this.placeBlock($$0, $$15, 10, 2, 13, $$4);
                this.placeBlock($$0, $$15, 10, 3, 13, $$4);
                this.placeBlock($$0, $$15, 10, 4, 13, $$4);
                this.placeBlock($$0, $$15, 10, 5, 13, $$4);
                this.placeBlock($$0, $$15, 10, 6, 13, $$4);
                this.placeBlock($$0, $$15, 10, 7, 13, $$4);
                int $$16 = 7;
                int $$17 = 7;
                BlockState $$18 = (BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, true);
                this.placeBlock($$0, $$18, 6, 9, 7, $$4);
                BlockState $$19 = (BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true);
                this.placeBlock($$0, $$19, 7, 9, 7, $$4);
                this.placeBlock($$0, $$18, 6, 8, 7, $$4);
                this.placeBlock($$0, $$19, 7, 8, 7, $$4);
                BlockState $$20 = (BlockState)((BlockState)$$13.setValue(FenceBlock.WEST, true)).setValue(FenceBlock.EAST, true);
                this.placeBlock($$0, $$20, 6, 7, 7, $$4);
                this.placeBlock($$0, $$20, 7, 7, 7, $$4);
                this.placeBlock($$0, $$18, 5, 7, 7, $$4);
                this.placeBlock($$0, $$19, 8, 7, 7, $$4);
                this.placeBlock($$0, (BlockState)$$18.setValue(FenceBlock.NORTH, true), 6, 7, 6, $$4);
                this.placeBlock($$0, (BlockState)$$18.setValue(FenceBlock.SOUTH, true), 6, 7, 8, $$4);
                this.placeBlock($$0, (BlockState)$$19.setValue(FenceBlock.NORTH, true), 7, 7, 6, $$4);
                this.placeBlock($$0, (BlockState)$$19.setValue(FenceBlock.SOUTH, true), 7, 7, 8, $$4);
                BlockState $$21 = Blocks.TORCH.defaultBlockState();
                this.placeBlock($$0, $$21, 5, 8, 7, $$4);
                this.placeBlock($$0, $$21, 8, 8, 7, $$4);
                this.placeBlock($$0, $$21, 6, 8, 6, $$4);
                this.placeBlock($$0, $$21, 6, 8, 8, $$4);
                this.placeBlock($$0, $$21, 7, 8, 6, $$4);
                this.placeBlock($$0, $$21, 7, 8, 8, $$4);
            }
            this.createChest($$0, $$4, $$3, 3, 3, 5, BuiltInLootTables.STRONGHOLD_LIBRARY);
            if (this.isTall) {
                this.placeBlock($$0, CAVE_AIR, 12, 9, 1, $$4);
                this.createChest($$0, $$4, $$3, 12, 8, 1, BuiltInLootTables.STRONGHOLD_LIBRARY);
            }
        }
    }

    public static class PortalRoom
    extends StrongholdPiece {
        protected static final int WIDTH = 11;
        protected static final int HEIGHT = 8;
        protected static final int DEPTH = 16;
        private boolean hasPlacedSpawner;

        public PortalRoom(int $$0, BoundingBox $$1, Direction $$2) {
            super(StructurePieceType.STRONGHOLD_PORTAL_ROOM, $$0, $$1);
            this.setOrientation($$2);
        }

        public PortalRoom(CompoundTag $$0) {
            super(StructurePieceType.STRONGHOLD_PORTAL_ROOM, $$0);
            this.hasPlacedSpawner = $$0.getBoolean("Mob");
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext $$0, CompoundTag $$1) {
            super.addAdditionalSaveData($$0, $$1);
            $$1.putBoolean("Mob", this.hasPlacedSpawner);
        }

        @Override
        public void addChildren(StructurePiece $$0, StructurePieceAccessor $$1, RandomSource $$2) {
            if ($$0 != null) {
                ((StartPiece)$$0).portalRoomPiece = this;
            }
        }

        public static PortalRoom createPiece(StructurePieceAccessor $$0, int $$1, int $$2, int $$3, Direction $$4, int $$5) {
            BoundingBox $$6 = BoundingBox.orientBox($$1, $$2, $$3, -4, -1, 0, 11, 8, 16, $$4);
            if (!PortalRoom.isOkBox($$6) || $$0.findCollisionPiece($$6) != null) {
                return null;
            }
            return new PortalRoom($$5, $$6, $$4);
        }

        @Override
        public void postProcess(WorldGenLevel $$0, StructureManager $$1, ChunkGenerator $$2, RandomSource $$3, BoundingBox $$4, ChunkPos $$5, BlockPos $$6) {
            BlockPos.MutableBlockPos $$22;
            this.generateBox($$0, $$4, 0, 0, 0, 10, 7, 15, false, $$3, SMOOTH_STONE_SELECTOR);
            this.generateSmallDoor($$0, $$3, $$4, StrongholdPiece.SmallDoorType.GRATES, 4, 1, 0);
            int $$7 = 6;
            this.generateBox($$0, $$4, 1, 6, 1, 1, 6, 14, false, $$3, SMOOTH_STONE_SELECTOR);
            this.generateBox($$0, $$4, 9, 6, 1, 9, 6, 14, false, $$3, SMOOTH_STONE_SELECTOR);
            this.generateBox($$0, $$4, 2, 6, 1, 8, 6, 2, false, $$3, SMOOTH_STONE_SELECTOR);
            this.generateBox($$0, $$4, 2, 6, 14, 8, 6, 14, false, $$3, SMOOTH_STONE_SELECTOR);
            this.generateBox($$0, $$4, 1, 1, 1, 2, 1, 4, false, $$3, SMOOTH_STONE_SELECTOR);
            this.generateBox($$0, $$4, 8, 1, 1, 9, 1, 4, false, $$3, SMOOTH_STONE_SELECTOR);
            this.generateBox($$0, $$4, 1, 1, 1, 1, 1, 3, Blocks.LAVA.defaultBlockState(), Blocks.LAVA.defaultBlockState(), false);
            this.generateBox($$0, $$4, 9, 1, 1, 9, 1, 3, Blocks.LAVA.defaultBlockState(), Blocks.LAVA.defaultBlockState(), false);
            this.generateBox($$0, $$4, 3, 1, 8, 7, 1, 12, false, $$3, SMOOTH_STONE_SELECTOR);
            this.generateBox($$0, $$4, 4, 1, 9, 6, 1, 11, Blocks.LAVA.defaultBlockState(), Blocks.LAVA.defaultBlockState(), false);
            BlockState $$8 = (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true);
            BlockState $$9 = (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, true)).setValue(IronBarsBlock.EAST, true);
            for (int $$10 = 3; $$10 < 14; $$10 += 2) {
                this.generateBox($$0, $$4, 0, 3, $$10, 0, 4, $$10, $$8, $$8, false);
                this.generateBox($$0, $$4, 10, 3, $$10, 10, 4, $$10, $$8, $$8, false);
            }
            for (int $$11 = 2; $$11 < 9; $$11 += 2) {
                this.generateBox($$0, $$4, $$11, 3, 15, $$11, 4, 15, $$9, $$9, false);
            }
            BlockState $$12 = (BlockState)Blocks.STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);
            this.generateBox($$0, $$4, 4, 1, 5, 6, 1, 7, false, $$3, SMOOTH_STONE_SELECTOR);
            this.generateBox($$0, $$4, 4, 2, 6, 6, 2, 7, false, $$3, SMOOTH_STONE_SELECTOR);
            this.generateBox($$0, $$4, 4, 3, 7, 6, 3, 7, false, $$3, SMOOTH_STONE_SELECTOR);
            for (int $$13 = 4; $$13 <= 6; ++$$13) {
                this.placeBlock($$0, $$12, $$13, 1, 4, $$4);
                this.placeBlock($$0, $$12, $$13, 2, 5, $$4);
                this.placeBlock($$0, $$12, $$13, 3, 6, $$4);
            }
            BlockState $$14 = (BlockState)Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.NORTH);
            BlockState $$15 = (BlockState)Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.SOUTH);
            BlockState $$16 = (BlockState)Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.EAST);
            BlockState $$17 = (BlockState)Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.WEST);
            boolean $$18 = true;
            boolean[] $$19 = new boolean[12];
            for (int $$20 = 0; $$20 < $$19.length; ++$$20) {
                $$19[$$20] = $$3.nextFloat() > 0.9f;
                $$18 &= $$19[$$20];
            }
            this.placeBlock($$0, (BlockState)$$14.setValue(EndPortalFrameBlock.HAS_EYE, $$19[0]), 4, 3, 8, $$4);
            this.placeBlock($$0, (BlockState)$$14.setValue(EndPortalFrameBlock.HAS_EYE, $$19[1]), 5, 3, 8, $$4);
            this.placeBlock($$0, (BlockState)$$14.setValue(EndPortalFrameBlock.HAS_EYE, $$19[2]), 6, 3, 8, $$4);
            this.placeBlock($$0, (BlockState)$$15.setValue(EndPortalFrameBlock.HAS_EYE, $$19[3]), 4, 3, 12, $$4);
            this.placeBlock($$0, (BlockState)$$15.setValue(EndPortalFrameBlock.HAS_EYE, $$19[4]), 5, 3, 12, $$4);
            this.placeBlock($$0, (BlockState)$$15.setValue(EndPortalFrameBlock.HAS_EYE, $$19[5]), 6, 3, 12, $$4);
            this.placeBlock($$0, (BlockState)$$16.setValue(EndPortalFrameBlock.HAS_EYE, $$19[6]), 3, 3, 9, $$4);
            this.placeBlock($$0, (BlockState)$$16.setValue(EndPortalFrameBlock.HAS_EYE, $$19[7]), 3, 3, 10, $$4);
            this.placeBlock($$0, (BlockState)$$16.setValue(EndPortalFrameBlock.HAS_EYE, $$19[8]), 3, 3, 11, $$4);
            this.placeBlock($$0, (BlockState)$$17.setValue(EndPortalFrameBlock.HAS_EYE, $$19[9]), 7, 3, 9, $$4);
            this.placeBlock($$0, (BlockState)$$17.setValue(EndPortalFrameBlock.HAS_EYE, $$19[10]), 7, 3, 10, $$4);
            this.placeBlock($$0, (BlockState)$$17.setValue(EndPortalFrameBlock.HAS_EYE, $$19[11]), 7, 3, 11, $$4);
            if ($$18) {
                BlockState $$21 = Blocks.END_PORTAL.defaultBlockState();
                this.placeBlock($$0, $$21, 4, 3, 9, $$4);
                this.placeBlock($$0, $$21, 5, 3, 9, $$4);
                this.placeBlock($$0, $$21, 6, 3, 9, $$4);
                this.placeBlock($$0, $$21, 4, 3, 10, $$4);
                this.placeBlock($$0, $$21, 5, 3, 10, $$4);
                this.placeBlock($$0, $$21, 6, 3, 10, $$4);
                this.placeBlock($$0, $$21, 4, 3, 11, $$4);
                this.placeBlock($$0, $$21, 5, 3, 11, $$4);
                this.placeBlock($$0, $$21, 6, 3, 11, $$4);
            }
            if (!this.hasPlacedSpawner && $$4.isInside($$22 = this.getWorldPos(5, 3, 6))) {
                this.hasPlacedSpawner = true;
                $$0.setBlock($$22, Blocks.SPAWNER.defaultBlockState(), 2);
                BlockEntity $$23 = $$0.getBlockEntity($$22);
                if ($$23 instanceof SpawnerBlockEntity) {
                    SpawnerBlockEntity $$24 = (SpawnerBlockEntity)$$23;
                    $$24.setEntityId(EntityType.SILVERFISH, $$3);
                }
            }
        }
    }

    static abstract class StrongholdPiece
    extends StructurePiece {
        protected SmallDoorType entryDoor = SmallDoorType.OPENING;

        protected StrongholdPiece(StructurePieceType $$0, int $$1, BoundingBox $$2) {
            super($$0, $$1, $$2);
        }

        public StrongholdPiece(StructurePieceType $$0, CompoundTag $$1) {
            super($$0, $$1);
            this.entryDoor = SmallDoorType.valueOf($$1.getString("EntryDoor"));
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext $$0, CompoundTag $$1) {
            $$1.putString("EntryDoor", this.entryDoor.name());
        }

        protected void generateSmallDoor(WorldGenLevel $$0, RandomSource $$1, BoundingBox $$2, SmallDoorType $$3, int $$4, int $$5, int $$6) {
            switch ($$3) {
                case OPENING: {
                    this.generateBox($$0, $$2, $$4, $$5, $$6, $$4 + 3 - 1, $$5 + 3 - 1, $$6, CAVE_AIR, CAVE_AIR, false);
                    break;
                }
                case WOOD_DOOR: {
                    this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), $$4, $$5, $$6, $$2);
                    this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), $$4, $$5 + 1, $$6, $$2);
                    this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), $$4, $$5 + 2, $$6, $$2);
                    this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), $$4 + 1, $$5 + 2, $$6, $$2);
                    this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), $$4 + 2, $$5 + 2, $$6, $$2);
                    this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), $$4 + 2, $$5 + 1, $$6, $$2);
                    this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), $$4 + 2, $$5, $$6, $$2);
                    this.placeBlock($$0, Blocks.OAK_DOOR.defaultBlockState(), $$4 + 1, $$5, $$6, $$2);
                    this.placeBlock($$0, (BlockState)Blocks.OAK_DOOR.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), $$4 + 1, $$5 + 1, $$6, $$2);
                    break;
                }
                case GRATES: {
                    this.placeBlock($$0, Blocks.CAVE_AIR.defaultBlockState(), $$4 + 1, $$5, $$6, $$2);
                    this.placeBlock($$0, Blocks.CAVE_AIR.defaultBlockState(), $$4 + 1, $$5 + 1, $$6, $$2);
                    this.placeBlock($$0, (BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, true), $$4, $$5, $$6, $$2);
                    this.placeBlock($$0, (BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, true), $$4, $$5 + 1, $$6, $$2);
                    this.placeBlock($$0, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.EAST, true)).setValue(IronBarsBlock.WEST, true), $$4, $$5 + 2, $$6, $$2);
                    this.placeBlock($$0, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.EAST, true)).setValue(IronBarsBlock.WEST, true), $$4 + 1, $$5 + 2, $$6, $$2);
                    this.placeBlock($$0, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.EAST, true)).setValue(IronBarsBlock.WEST, true), $$4 + 2, $$5 + 2, $$6, $$2);
                    this.placeBlock($$0, (BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.EAST, true), $$4 + 2, $$5 + 1, $$6, $$2);
                    this.placeBlock($$0, (BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.EAST, true), $$4 + 2, $$5, $$6, $$2);
                    break;
                }
                case IRON_DOOR: {
                    this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), $$4, $$5, $$6, $$2);
                    this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), $$4, $$5 + 1, $$6, $$2);
                    this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), $$4, $$5 + 2, $$6, $$2);
                    this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), $$4 + 1, $$5 + 2, $$6, $$2);
                    this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), $$4 + 2, $$5 + 2, $$6, $$2);
                    this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), $$4 + 2, $$5 + 1, $$6, $$2);
                    this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), $$4 + 2, $$5, $$6, $$2);
                    this.placeBlock($$0, Blocks.IRON_DOOR.defaultBlockState(), $$4 + 1, $$5, $$6, $$2);
                    this.placeBlock($$0, (BlockState)Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), $$4 + 1, $$5 + 1, $$6, $$2);
                    this.placeBlock($$0, (BlockState)Blocks.STONE_BUTTON.defaultBlockState().setValue(ButtonBlock.FACING, Direction.NORTH), $$4 + 2, $$5 + 1, $$6 + 1, $$2);
                    this.placeBlock($$0, (BlockState)Blocks.STONE_BUTTON.defaultBlockState().setValue(ButtonBlock.FACING, Direction.SOUTH), $$4 + 2, $$5 + 1, $$6 - 1, $$2);
                }
            }
        }

        protected SmallDoorType randomSmallDoor(RandomSource $$0) {
            int $$1 = $$0.nextInt(5);
            switch ($$1) {
                default: {
                    return SmallDoorType.OPENING;
                }
                case 2: {
                    return SmallDoorType.WOOD_DOOR;
                }
                case 3: {
                    return SmallDoorType.GRATES;
                }
                case 4: 
            }
            return SmallDoorType.IRON_DOOR;
        }

        @Nullable
        protected StructurePiece generateSmallDoorChildForward(StartPiece $$0, StructurePieceAccessor $$1, RandomSource $$2, int $$3, int $$4) {
            Direction $$5 = this.getOrientation();
            if ($$5 != null) {
                switch ($$5) {
                    case NORTH: {
                        return StrongholdPieces.generateAndAddPiece($$0, $$1, $$2, this.boundingBox.minX() + $$3, this.boundingBox.minY() + $$4, this.boundingBox.minZ() - 1, $$5, this.getGenDepth());
                    }
                    case SOUTH: {
                        return StrongholdPieces.generateAndAddPiece($$0, $$1, $$2, this.boundingBox.minX() + $$3, this.boundingBox.minY() + $$4, this.boundingBox.maxZ() + 1, $$5, this.getGenDepth());
                    }
                    case WEST: {
                        return StrongholdPieces.generateAndAddPiece($$0, $$1, $$2, this.boundingBox.minX() - 1, this.boundingBox.minY() + $$4, this.boundingBox.minZ() + $$3, $$5, this.getGenDepth());
                    }
                    case EAST: {
                        return StrongholdPieces.generateAndAddPiece($$0, $$1, $$2, this.boundingBox.maxX() + 1, this.boundingBox.minY() + $$4, this.boundingBox.minZ() + $$3, $$5, this.getGenDepth());
                    }
                }
            }
            return null;
        }

        @Nullable
        protected StructurePiece generateSmallDoorChildLeft(StartPiece $$0, StructurePieceAccessor $$1, RandomSource $$2, int $$3, int $$4) {
            Direction $$5 = this.getOrientation();
            if ($$5 != null) {
                switch ($$5) {
                    case NORTH: {
                        return StrongholdPieces.generateAndAddPiece($$0, $$1, $$2, this.boundingBox.minX() - 1, this.boundingBox.minY() + $$3, this.boundingBox.minZ() + $$4, Direction.WEST, this.getGenDepth());
                    }
                    case SOUTH: {
                        return StrongholdPieces.generateAndAddPiece($$0, $$1, $$2, this.boundingBox.minX() - 1, this.boundingBox.minY() + $$3, this.boundingBox.minZ() + $$4, Direction.WEST, this.getGenDepth());
                    }
                    case WEST: {
                        return StrongholdPieces.generateAndAddPiece($$0, $$1, $$2, this.boundingBox.minX() + $$4, this.boundingBox.minY() + $$3, this.boundingBox.minZ() - 1, Direction.NORTH, this.getGenDepth());
                    }
                    case EAST: {
                        return StrongholdPieces.generateAndAddPiece($$0, $$1, $$2, this.boundingBox.minX() + $$4, this.boundingBox.minY() + $$3, this.boundingBox.minZ() - 1, Direction.NORTH, this.getGenDepth());
                    }
                }
            }
            return null;
        }

        @Nullable
        protected StructurePiece generateSmallDoorChildRight(StartPiece $$0, StructurePieceAccessor $$1, RandomSource $$2, int $$3, int $$4) {
            Direction $$5 = this.getOrientation();
            if ($$5 != null) {
                switch ($$5) {
                    case NORTH: {
                        return StrongholdPieces.generateAndAddPiece($$0, $$1, $$2, this.boundingBox.maxX() + 1, this.boundingBox.minY() + $$3, this.boundingBox.minZ() + $$4, Direction.EAST, this.getGenDepth());
                    }
                    case SOUTH: {
                        return StrongholdPieces.generateAndAddPiece($$0, $$1, $$2, this.boundingBox.maxX() + 1, this.boundingBox.minY() + $$3, this.boundingBox.minZ() + $$4, Direction.EAST, this.getGenDepth());
                    }
                    case WEST: {
                        return StrongholdPieces.generateAndAddPiece($$0, $$1, $$2, this.boundingBox.minX() + $$4, this.boundingBox.minY() + $$3, this.boundingBox.maxZ() + 1, Direction.SOUTH, this.getGenDepth());
                    }
                    case EAST: {
                        return StrongholdPieces.generateAndAddPiece($$0, $$1, $$2, this.boundingBox.minX() + $$4, this.boundingBox.minY() + $$3, this.boundingBox.maxZ() + 1, Direction.SOUTH, this.getGenDepth());
                    }
                }
            }
            return null;
        }

        protected static boolean isOkBox(BoundingBox $$0) {
            return $$0 != null && $$0.minY() > 10;
        }

        protected static enum SmallDoorType {
            OPENING,
            WOOD_DOOR,
            GRATES,
            IRON_DOOR;

        }
    }

    public static class StartPiece
    extends StairsDown {
        public PieceWeight previousPiece;
        @Nullable
        public PortalRoom portalRoomPiece;
        public final List<StructurePiece> pendingChildren = Lists.newArrayList();

        public StartPiece(RandomSource $$0, int $$1, int $$2) {
            super(StructurePieceType.STRONGHOLD_START, 0, $$1, $$2, StartPiece.getRandomHorizontalDirection($$0));
        }

        public StartPiece(CompoundTag $$0) {
            super(StructurePieceType.STRONGHOLD_START, $$0);
        }

        @Override
        public BlockPos getLocatorPosition() {
            if (this.portalRoomPiece != null) {
                return this.portalRoomPiece.getLocatorPosition();
            }
            return super.getLocatorPosition();
        }
    }

    public static class FillerCorridor
    extends StrongholdPiece {
        private final int steps;

        public FillerCorridor(int $$0, BoundingBox $$1, Direction $$2) {
            super(StructurePieceType.STRONGHOLD_FILLER_CORRIDOR, $$0, $$1);
            this.setOrientation($$2);
            this.steps = $$2 == Direction.NORTH || $$2 == Direction.SOUTH ? $$1.getZSpan() : $$1.getXSpan();
        }

        public FillerCorridor(CompoundTag $$0) {
            super(StructurePieceType.STRONGHOLD_FILLER_CORRIDOR, $$0);
            this.steps = $$0.getInt("Steps");
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext $$0, CompoundTag $$1) {
            super.addAdditionalSaveData($$0, $$1);
            $$1.putInt("Steps", this.steps);
        }

        public static BoundingBox findPieceBox(StructurePieceAccessor $$0, RandomSource $$1, int $$2, int $$3, int $$4, Direction $$5) {
            int $$6 = 3;
            BoundingBox $$7 = BoundingBox.orientBox($$2, $$3, $$4, -1, -1, 0, 5, 5, 4, $$5);
            StructurePiece $$8 = $$0.findCollisionPiece($$7);
            if ($$8 == null) {
                return null;
            }
            if ($$8.getBoundingBox().minY() == $$7.minY()) {
                for (int $$9 = 2; $$9 >= 1; --$$9) {
                    $$7 = BoundingBox.orientBox($$2, $$3, $$4, -1, -1, 0, 5, 5, $$9, $$5);
                    if ($$8.getBoundingBox().intersects($$7)) continue;
                    return BoundingBox.orientBox($$2, $$3, $$4, -1, -1, 0, 5, 5, $$9 + 1, $$5);
                }
            }
            return null;
        }

        @Override
        public void postProcess(WorldGenLevel $$0, StructureManager $$1, ChunkGenerator $$2, RandomSource $$3, BoundingBox $$4, ChunkPos $$5, BlockPos $$6) {
            for (int $$7 = 0; $$7 < this.steps; ++$$7) {
                this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 0, 0, $$7, $$4);
                this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 1, 0, $$7, $$4);
                this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 2, 0, $$7, $$4);
                this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 3, 0, $$7, $$4);
                this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 4, 0, $$7, $$4);
                for (int $$8 = 1; $$8 <= 3; ++$$8) {
                    this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 0, $$8, $$7, $$4);
                    this.placeBlock($$0, Blocks.CAVE_AIR.defaultBlockState(), 1, $$8, $$7, $$4);
                    this.placeBlock($$0, Blocks.CAVE_AIR.defaultBlockState(), 2, $$8, $$7, $$4);
                    this.placeBlock($$0, Blocks.CAVE_AIR.defaultBlockState(), 3, $$8, $$7, $$4);
                    this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 4, $$8, $$7, $$4);
                }
                this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 0, 4, $$7, $$4);
                this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 1, 4, $$7, $$4);
                this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 2, 4, $$7, $$4);
                this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 3, 4, $$7, $$4);
                this.placeBlock($$0, Blocks.STONE_BRICKS.defaultBlockState(), 4, 4, $$7, $$4);
            }
        }
    }

    static class SmoothStoneSelector
    extends StructurePiece.BlockSelector {
        SmoothStoneSelector() {
        }

        @Override
        public void next(RandomSource $$0, int $$1, int $$2, int $$3, boolean $$4) {
            float $$5;
            this.next = $$4 ? (($$5 = $$0.nextFloat()) < 0.2f ? Blocks.CRACKED_STONE_BRICKS.defaultBlockState() : ($$5 < 0.5f ? Blocks.MOSSY_STONE_BRICKS.defaultBlockState() : ($$5 < 0.55f ? Blocks.INFESTED_STONE_BRICKS.defaultBlockState() : Blocks.STONE_BRICKS.defaultBlockState()))) : Blocks.CAVE_AIR.defaultBlockState();
        }
    }

    public static abstract class Turn
    extends StrongholdPiece {
        protected static final int WIDTH = 5;
        protected static final int HEIGHT = 5;
        protected static final int DEPTH = 5;

        protected Turn(StructurePieceType $$0, int $$1, BoundingBox $$2) {
            super($$0, $$1, $$2);
        }

        public Turn(StructurePieceType $$0, CompoundTag $$1) {
            super($$0, $$1);
        }
    }
}