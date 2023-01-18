/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.System
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.function.Function
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class WoodlandMansionPieces {
    public static void generateMansion(StructureTemplateManager $$0, BlockPos $$1, Rotation $$2, List<WoodlandMansionPiece> $$3, RandomSource $$4) {
        MansionGrid $$5 = new MansionGrid($$4);
        MansionPiecePlacer $$6 = new MansionPiecePlacer($$0, $$4);
        $$6.createMansion($$1, $$2, $$3, $$5);
    }

    public static void main(String[] $$0) {
        RandomSource $$1 = RandomSource.create();
        long $$2 = $$1.nextLong();
        System.out.println("Seed: " + $$2);
        $$1.setSeed($$2);
        MansionGrid $$3 = new MansionGrid($$1);
        $$3.print();
    }

    static class MansionGrid {
        private static final int DEFAULT_SIZE = 11;
        private static final int CLEAR = 0;
        private static final int CORRIDOR = 1;
        private static final int ROOM = 2;
        private static final int START_ROOM = 3;
        private static final int TEST_ROOM = 4;
        private static final int BLOCKED = 5;
        private static final int ROOM_1x1 = 65536;
        private static final int ROOM_1x2 = 131072;
        private static final int ROOM_2x2 = 262144;
        private static final int ROOM_ORIGIN_FLAG = 0x100000;
        private static final int ROOM_DOOR_FLAG = 0x200000;
        private static final int ROOM_STAIRS_FLAG = 0x400000;
        private static final int ROOM_CORRIDOR_FLAG = 0x800000;
        private static final int ROOM_TYPE_MASK = 983040;
        private static final int ROOM_ID_MASK = 65535;
        private final RandomSource random;
        final SimpleGrid baseGrid;
        final SimpleGrid thirdFloorGrid;
        final SimpleGrid[] floorRooms;
        final int entranceX;
        final int entranceY;

        public MansionGrid(RandomSource $$0) {
            this.random = $$0;
            int $$1 = 11;
            this.entranceX = 7;
            this.entranceY = 4;
            this.baseGrid = new SimpleGrid(11, 11, 5);
            this.baseGrid.set(this.entranceX, this.entranceY, this.entranceX + 1, this.entranceY + 1, 3);
            this.baseGrid.set(this.entranceX - 1, this.entranceY, this.entranceX - 1, this.entranceY + 1, 2);
            this.baseGrid.set(this.entranceX + 2, this.entranceY - 2, this.entranceX + 3, this.entranceY + 3, 5);
            this.baseGrid.set(this.entranceX + 1, this.entranceY - 2, this.entranceX + 1, this.entranceY - 1, 1);
            this.baseGrid.set(this.entranceX + 1, this.entranceY + 2, this.entranceX + 1, this.entranceY + 3, 1);
            this.baseGrid.set(this.entranceX - 1, this.entranceY - 1, 1);
            this.baseGrid.set(this.entranceX - 1, this.entranceY + 2, 1);
            this.baseGrid.set(0, 0, 11, 1, 5);
            this.baseGrid.set(0, 9, 11, 11, 5);
            this.recursiveCorridor(this.baseGrid, this.entranceX, this.entranceY - 2, Direction.WEST, 6);
            this.recursiveCorridor(this.baseGrid, this.entranceX, this.entranceY + 3, Direction.WEST, 6);
            this.recursiveCorridor(this.baseGrid, this.entranceX - 2, this.entranceY - 1, Direction.WEST, 3);
            this.recursiveCorridor(this.baseGrid, this.entranceX - 2, this.entranceY + 2, Direction.WEST, 3);
            while (this.cleanEdges(this.baseGrid)) {
            }
            this.floorRooms = new SimpleGrid[3];
            this.floorRooms[0] = new SimpleGrid(11, 11, 5);
            this.floorRooms[1] = new SimpleGrid(11, 11, 5);
            this.floorRooms[2] = new SimpleGrid(11, 11, 5);
            this.identifyRooms(this.baseGrid, this.floorRooms[0]);
            this.identifyRooms(this.baseGrid, this.floorRooms[1]);
            this.floorRooms[0].set(this.entranceX + 1, this.entranceY, this.entranceX + 1, this.entranceY + 1, 0x800000);
            this.floorRooms[1].set(this.entranceX + 1, this.entranceY, this.entranceX + 1, this.entranceY + 1, 0x800000);
            this.thirdFloorGrid = new SimpleGrid(this.baseGrid.width, this.baseGrid.height, 5);
            this.setupThirdFloor();
            this.identifyRooms(this.thirdFloorGrid, this.floorRooms[2]);
        }

        public static boolean isHouse(SimpleGrid $$0, int $$1, int $$2) {
            int $$3 = $$0.get($$1, $$2);
            return $$3 == 1 || $$3 == 2 || $$3 == 3 || $$3 == 4;
        }

        public boolean isRoomId(SimpleGrid $$0, int $$1, int $$2, int $$3, int $$4) {
            return (this.floorRooms[$$3].get($$1, $$2) & 0xFFFF) == $$4;
        }

        @Nullable
        public Direction get1x2RoomDirection(SimpleGrid $$0, int $$1, int $$2, int $$3, int $$4) {
            for (Direction $$5 : Direction.Plane.HORIZONTAL) {
                if (!this.isRoomId($$0, $$1 + $$5.getStepX(), $$2 + $$5.getStepZ(), $$3, $$4)) continue;
                return $$5;
            }
            return null;
        }

        private void recursiveCorridor(SimpleGrid $$0, int $$1, int $$2, Direction $$3, int $$4) {
            if ($$4 <= 0) {
                return;
            }
            $$0.set($$1, $$2, 1);
            $$0.setif($$1 + $$3.getStepX(), $$2 + $$3.getStepZ(), 0, 1);
            for (int $$5 = 0; $$5 < 8; ++$$5) {
                Direction $$6 = Direction.from2DDataValue(this.random.nextInt(4));
                if ($$6 == $$3.getOpposite() || $$6 == Direction.EAST && this.random.nextBoolean()) continue;
                int $$7 = $$1 + $$3.getStepX();
                int $$8 = $$2 + $$3.getStepZ();
                if ($$0.get($$7 + $$6.getStepX(), $$8 + $$6.getStepZ()) != 0 || $$0.get($$7 + $$6.getStepX() * 2, $$8 + $$6.getStepZ() * 2) != 0) continue;
                this.recursiveCorridor($$0, $$1 + $$3.getStepX() + $$6.getStepX(), $$2 + $$3.getStepZ() + $$6.getStepZ(), $$6, $$4 - 1);
                break;
            }
            Direction $$9 = $$3.getClockWise();
            Direction $$10 = $$3.getCounterClockWise();
            $$0.setif($$1 + $$9.getStepX(), $$2 + $$9.getStepZ(), 0, 2);
            $$0.setif($$1 + $$10.getStepX(), $$2 + $$10.getStepZ(), 0, 2);
            $$0.setif($$1 + $$3.getStepX() + $$9.getStepX(), $$2 + $$3.getStepZ() + $$9.getStepZ(), 0, 2);
            $$0.setif($$1 + $$3.getStepX() + $$10.getStepX(), $$2 + $$3.getStepZ() + $$10.getStepZ(), 0, 2);
            $$0.setif($$1 + $$3.getStepX() * 2, $$2 + $$3.getStepZ() * 2, 0, 2);
            $$0.setif($$1 + $$9.getStepX() * 2, $$2 + $$9.getStepZ() * 2, 0, 2);
            $$0.setif($$1 + $$10.getStepX() * 2, $$2 + $$10.getStepZ() * 2, 0, 2);
        }

        private boolean cleanEdges(SimpleGrid $$0) {
            boolean $$1 = false;
            for (int $$2 = 0; $$2 < $$0.height; ++$$2) {
                for (int $$3 = 0; $$3 < $$0.width; ++$$3) {
                    if ($$0.get($$3, $$2) != 0) continue;
                    int $$4 = 0;
                    $$4 += MansionGrid.isHouse($$0, $$3 + 1, $$2) ? 1 : 0;
                    $$4 += MansionGrid.isHouse($$0, $$3 - 1, $$2) ? 1 : 0;
                    $$4 += MansionGrid.isHouse($$0, $$3, $$2 + 1) ? 1 : 0;
                    if (($$4 += MansionGrid.isHouse($$0, $$3, $$2 - 1) ? 1 : 0) >= 3) {
                        $$0.set($$3, $$2, 2);
                        $$1 = true;
                        continue;
                    }
                    if ($$4 != 2) continue;
                    int $$5 = 0;
                    $$5 += MansionGrid.isHouse($$0, $$3 + 1, $$2 + 1) ? 1 : 0;
                    $$5 += MansionGrid.isHouse($$0, $$3 - 1, $$2 + 1) ? 1 : 0;
                    $$5 += MansionGrid.isHouse($$0, $$3 + 1, $$2 - 1) ? 1 : 0;
                    if (($$5 += MansionGrid.isHouse($$0, $$3 - 1, $$2 - 1) ? 1 : 0) > 1) continue;
                    $$0.set($$3, $$2, 2);
                    $$1 = true;
                }
            }
            return $$1;
        }

        private void setupThirdFloor() {
            ArrayList $$0 = Lists.newArrayList();
            SimpleGrid $$1 = this.floorRooms[1];
            for (int $$2 = 0; $$2 < this.thirdFloorGrid.height; ++$$2) {
                for (int $$3 = 0; $$3 < this.thirdFloorGrid.width; ++$$3) {
                    int $$4 = $$1.get($$3, $$2);
                    int $$5 = $$4 & 0xF0000;
                    if ($$5 != 131072 || ($$4 & 0x200000) != 0x200000) continue;
                    $$0.add(new Tuple<Integer, Integer>($$3, $$2));
                }
            }
            if ($$0.isEmpty()) {
                this.thirdFloorGrid.set(0, 0, this.thirdFloorGrid.width, this.thirdFloorGrid.height, 5);
                return;
            }
            Tuple $$6 = (Tuple)$$0.get(this.random.nextInt($$0.size()));
            int $$7 = $$1.get((Integer)$$6.getA(), (Integer)$$6.getB());
            $$1.set((Integer)$$6.getA(), (Integer)$$6.getB(), $$7 | 0x400000);
            Direction $$8 = this.get1x2RoomDirection(this.baseGrid, (Integer)$$6.getA(), (Integer)$$6.getB(), 1, $$7 & 0xFFFF);
            int $$9 = (Integer)$$6.getA() + $$8.getStepX();
            int $$10 = (Integer)$$6.getB() + $$8.getStepZ();
            for (int $$11 = 0; $$11 < this.thirdFloorGrid.height; ++$$11) {
                for (int $$12 = 0; $$12 < this.thirdFloorGrid.width; ++$$12) {
                    if (!MansionGrid.isHouse(this.baseGrid, $$12, $$11)) {
                        this.thirdFloorGrid.set($$12, $$11, 5);
                        continue;
                    }
                    if ($$12 == (Integer)$$6.getA() && $$11 == (Integer)$$6.getB()) {
                        this.thirdFloorGrid.set($$12, $$11, 3);
                        continue;
                    }
                    if ($$12 != $$9 || $$11 != $$10) continue;
                    this.thirdFloorGrid.set($$12, $$11, 3);
                    this.floorRooms[2].set($$12, $$11, 0x800000);
                }
            }
            ArrayList $$13 = Lists.newArrayList();
            for (Direction $$14 : Direction.Plane.HORIZONTAL) {
                if (this.thirdFloorGrid.get($$9 + $$14.getStepX(), $$10 + $$14.getStepZ()) != 0) continue;
                $$13.add((Object)$$14);
            }
            if ($$13.isEmpty()) {
                this.thirdFloorGrid.set(0, 0, this.thirdFloorGrid.width, this.thirdFloorGrid.height, 5);
                $$1.set((Integer)$$6.getA(), (Integer)$$6.getB(), $$7);
                return;
            }
            Direction $$15 = (Direction)$$13.get(this.random.nextInt($$13.size()));
            this.recursiveCorridor(this.thirdFloorGrid, $$9 + $$15.getStepX(), $$10 + $$15.getStepZ(), $$15, 4);
            while (this.cleanEdges(this.thirdFloorGrid)) {
            }
        }

        private void identifyRooms(SimpleGrid $$0, SimpleGrid $$1) {
            ObjectArrayList $$2 = new ObjectArrayList();
            for (int $$3 = 0; $$3 < $$0.height; ++$$3) {
                for (int $$4 = 0; $$4 < $$0.width; ++$$4) {
                    if ($$0.get($$4, $$3) != 2) continue;
                    $$2.add(new Tuple<Integer, Integer>($$4, $$3));
                }
            }
            Util.shuffle($$2, this.random);
            int $$5 = 10;
            for (Tuple $$6 : $$2) {
                int $$8;
                int $$7 = (Integer)$$6.getA();
                if ($$1.get($$7, $$8 = ((Integer)$$6.getB()).intValue()) != 0) continue;
                int $$9 = $$7;
                int $$10 = $$7;
                int $$11 = $$8;
                int $$12 = $$8;
                int $$13 = 65536;
                if ($$1.get($$7 + 1, $$8) == 0 && $$1.get($$7, $$8 + 1) == 0 && $$1.get($$7 + 1, $$8 + 1) == 0 && $$0.get($$7 + 1, $$8) == 2 && $$0.get($$7, $$8 + 1) == 2 && $$0.get($$7 + 1, $$8 + 1) == 2) {
                    ++$$10;
                    ++$$12;
                    $$13 = 262144;
                } else if ($$1.get($$7 - 1, $$8) == 0 && $$1.get($$7, $$8 + 1) == 0 && $$1.get($$7 - 1, $$8 + 1) == 0 && $$0.get($$7 - 1, $$8) == 2 && $$0.get($$7, $$8 + 1) == 2 && $$0.get($$7 - 1, $$8 + 1) == 2) {
                    --$$9;
                    ++$$12;
                    $$13 = 262144;
                } else if ($$1.get($$7 - 1, $$8) == 0 && $$1.get($$7, $$8 - 1) == 0 && $$1.get($$7 - 1, $$8 - 1) == 0 && $$0.get($$7 - 1, $$8) == 2 && $$0.get($$7, $$8 - 1) == 2 && $$0.get($$7 - 1, $$8 - 1) == 2) {
                    --$$9;
                    --$$11;
                    $$13 = 262144;
                } else if ($$1.get($$7 + 1, $$8) == 0 && $$0.get($$7 + 1, $$8) == 2) {
                    ++$$10;
                    $$13 = 131072;
                } else if ($$1.get($$7, $$8 + 1) == 0 && $$0.get($$7, $$8 + 1) == 2) {
                    ++$$12;
                    $$13 = 131072;
                } else if ($$1.get($$7 - 1, $$8) == 0 && $$0.get($$7 - 1, $$8) == 2) {
                    --$$9;
                    $$13 = 131072;
                } else if ($$1.get($$7, $$8 - 1) == 0 && $$0.get($$7, $$8 - 1) == 2) {
                    --$$11;
                    $$13 = 131072;
                }
                int $$14 = this.random.nextBoolean() ? $$9 : $$10;
                int $$15 = this.random.nextBoolean() ? $$11 : $$12;
                int $$16 = 0x200000;
                if (!$$0.edgesTo($$14, $$15, 1)) {
                    $$14 = $$14 == $$9 ? $$10 : $$9;
                    int n = $$15 = $$15 == $$11 ? $$12 : $$11;
                    if (!$$0.edgesTo($$14, $$15, 1)) {
                        int n2 = $$15 = $$15 == $$11 ? $$12 : $$11;
                        if (!$$0.edgesTo($$14, $$15, 1)) {
                            $$14 = $$14 == $$9 ? $$10 : $$9;
                            int n3 = $$15 = $$15 == $$11 ? $$12 : $$11;
                            if (!$$0.edgesTo($$14, $$15, 1)) {
                                $$16 = 0;
                                $$14 = $$9;
                                $$15 = $$11;
                            }
                        }
                    }
                }
                for (int $$17 = $$11; $$17 <= $$12; ++$$17) {
                    for (int $$18 = $$9; $$18 <= $$10; ++$$18) {
                        if ($$18 == $$14 && $$17 == $$15) {
                            $$1.set($$18, $$17, 0x100000 | $$16 | $$13 | $$5);
                            continue;
                        }
                        $$1.set($$18, $$17, $$13 | $$5);
                    }
                }
                ++$$5;
            }
        }

        public void print() {
            for (int $$0 = 0; $$0 < 2; ++$$0) {
                SimpleGrid $$1 = $$0 == 0 ? this.baseGrid : this.thirdFloorGrid;
                for (int $$2 = 0; $$2 < $$1.height; ++$$2) {
                    for (int $$3 = 0; $$3 < $$1.width; ++$$3) {
                        int $$4 = $$1.get($$3, $$2);
                        if ($$4 == 1) {
                            System.out.print("+");
                            continue;
                        }
                        if ($$4 == 4) {
                            System.out.print("x");
                            continue;
                        }
                        if ($$4 == 2) {
                            System.out.print("X");
                            continue;
                        }
                        if ($$4 == 3) {
                            System.out.print("O");
                            continue;
                        }
                        if ($$4 == 5) {
                            System.out.print("#");
                            continue;
                        }
                        System.out.print(" ");
                    }
                    System.out.println("");
                }
                System.out.println("");
            }
        }
    }

    static class MansionPiecePlacer {
        private final StructureTemplateManager structureTemplateManager;
        private final RandomSource random;
        private int startX;
        private int startY;

        public MansionPiecePlacer(StructureTemplateManager $$0, RandomSource $$1) {
            this.structureTemplateManager = $$0;
            this.random = $$1;
        }

        public void createMansion(BlockPos $$0, Rotation $$1, List<WoodlandMansionPiece> $$2, MansionGrid $$3) {
            PlacementData $$4 = new PlacementData();
            $$4.position = $$0;
            $$4.rotation = $$1;
            $$4.wallType = "wall_flat";
            PlacementData $$5 = new PlacementData();
            this.entrance($$2, $$4);
            $$5.position = $$4.position.above(8);
            $$5.rotation = $$4.rotation;
            $$5.wallType = "wall_window";
            if (!$$2.isEmpty()) {
                // empty if block
            }
            SimpleGrid $$6 = $$3.baseGrid;
            SimpleGrid $$7 = $$3.thirdFloorGrid;
            this.startX = $$3.entranceX + 1;
            this.startY = $$3.entranceY + 1;
            int $$8 = $$3.entranceX + 1;
            int $$9 = $$3.entranceY;
            this.traverseOuterWalls($$2, $$4, $$6, Direction.SOUTH, this.startX, this.startY, $$8, $$9);
            this.traverseOuterWalls($$2, $$5, $$6, Direction.SOUTH, this.startX, this.startY, $$8, $$9);
            PlacementData $$10 = new PlacementData();
            $$10.position = $$4.position.above(19);
            $$10.rotation = $$4.rotation;
            $$10.wallType = "wall_window";
            boolean $$11 = false;
            for (int $$12 = 0; $$12 < $$7.height && !$$11; ++$$12) {
                for (int $$13 = $$7.width - 1; $$13 >= 0 && !$$11; --$$13) {
                    if (!MansionGrid.isHouse($$7, $$13, $$12)) continue;
                    $$10.position = $$10.position.relative($$1.rotate(Direction.SOUTH), 8 + ($$12 - this.startY) * 8);
                    $$10.position = $$10.position.relative($$1.rotate(Direction.EAST), ($$13 - this.startX) * 8);
                    this.traverseWallPiece($$2, $$10);
                    this.traverseOuterWalls($$2, $$10, $$7, Direction.SOUTH, $$13, $$12, $$13, $$12);
                    $$11 = true;
                }
            }
            this.createRoof($$2, (BlockPos)$$0.above(16), $$1, $$6, $$7);
            this.createRoof($$2, (BlockPos)$$0.above(27), $$1, $$7, null);
            if (!$$2.isEmpty()) {
                // empty if block
            }
            FloorRoomCollection[] $$14 = new FloorRoomCollection[]{new FirstFloorRoomCollection(), new SecondFloorRoomCollection(), new ThirdFloorRoomCollection()};
            for (int $$15 = 0; $$15 < 3; ++$$15) {
                Vec3i $$16 = $$0.above(8 * $$15 + ($$15 == 2 ? 3 : 0));
                SimpleGrid $$17 = $$3.floorRooms[$$15];
                SimpleGrid $$18 = $$15 == 2 ? $$7 : $$6;
                String $$19 = $$15 == 0 ? "carpet_south_1" : "carpet_south_2";
                String $$20 = $$15 == 0 ? "carpet_west_1" : "carpet_west_2";
                for (int $$21 = 0; $$21 < $$18.height; ++$$21) {
                    for (int $$22 = 0; $$22 < $$18.width; ++$$22) {
                        if ($$18.get($$22, $$21) != 1) continue;
                        BlockPos $$23 = ((BlockPos)$$16).relative($$1.rotate(Direction.SOUTH), 8 + ($$21 - this.startY) * 8);
                        $$23 = $$23.relative($$1.rotate(Direction.EAST), ($$22 - this.startX) * 8);
                        $$2.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, "corridor_floor", $$23, $$1));
                        if ($$18.get($$22, $$21 - 1) == 1 || ($$17.get($$22, $$21 - 1) & 0x800000) == 0x800000) {
                            $$2.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, "carpet_north", (BlockPos)$$23.relative($$1.rotate(Direction.EAST), 1).above(), $$1));
                        }
                        if ($$18.get($$22 + 1, $$21) == 1 || ($$17.get($$22 + 1, $$21) & 0x800000) == 0x800000) {
                            $$2.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, "carpet_east", (BlockPos)$$23.relative($$1.rotate(Direction.SOUTH), 1).relative($$1.rotate(Direction.EAST), 5).above(), $$1));
                        }
                        if ($$18.get($$22, $$21 + 1) == 1 || ($$17.get($$22, $$21 + 1) & 0x800000) == 0x800000) {
                            $$2.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, $$19, $$23.relative($$1.rotate(Direction.SOUTH), 5).relative($$1.rotate(Direction.WEST), 1), $$1));
                        }
                        if ($$18.get($$22 - 1, $$21) != 1 && ($$17.get($$22 - 1, $$21) & 0x800000) != 0x800000) continue;
                        $$2.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, $$20, $$23.relative($$1.rotate(Direction.WEST), 1).relative($$1.rotate(Direction.NORTH), 1), $$1));
                    }
                }
                String $$24 = $$15 == 0 ? "indoors_wall_1" : "indoors_wall_2";
                String $$25 = $$15 == 0 ? "indoors_door_1" : "indoors_door_2";
                ArrayList $$26 = Lists.newArrayList();
                for (int $$27 = 0; $$27 < $$18.height; ++$$27) {
                    for (int $$28 = 0; $$28 < $$18.width; ++$$28) {
                        boolean $$29;
                        boolean bl = $$29 = $$15 == 2 && $$18.get($$28, $$27) == 3;
                        if ($$18.get($$28, $$27) != 2 && !$$29) continue;
                        int $$30 = $$17.get($$28, $$27);
                        int $$31 = $$30 & 0xF0000;
                        int $$32 = $$30 & 0xFFFF;
                        $$29 = $$29 && ($$30 & 0x800000) == 0x800000;
                        $$26.clear();
                        if (($$30 & 0x200000) == 0x200000) {
                            for (Direction $$33 : Direction.Plane.HORIZONTAL) {
                                if ($$18.get($$28 + $$33.getStepX(), $$27 + $$33.getStepZ()) != 1) continue;
                                $$26.add((Object)$$33);
                            }
                        }
                        Direction $$34 = null;
                        if (!$$26.isEmpty()) {
                            $$34 = (Direction)$$26.get(this.random.nextInt($$26.size()));
                        } else if (($$30 & 0x100000) == 0x100000) {
                            $$34 = Direction.UP;
                        }
                        BlockPos $$35 = ((BlockPos)$$16).relative($$1.rotate(Direction.SOUTH), 8 + ($$27 - this.startY) * 8);
                        $$35 = $$35.relative($$1.rotate(Direction.EAST), -1 + ($$28 - this.startX) * 8);
                        if (MansionGrid.isHouse($$18, $$28 - 1, $$27) && !$$3.isRoomId($$18, $$28 - 1, $$27, $$15, $$32)) {
                            $$2.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, $$34 == Direction.WEST ? $$25 : $$24, $$35, $$1));
                        }
                        if ($$18.get($$28 + 1, $$27) == 1 && !$$29) {
                            BlockPos $$36 = $$35.relative($$1.rotate(Direction.EAST), 8);
                            $$2.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, $$34 == Direction.EAST ? $$25 : $$24, $$36, $$1));
                        }
                        if (MansionGrid.isHouse($$18, $$28, $$27 + 1) && !$$3.isRoomId($$18, $$28, $$27 + 1, $$15, $$32)) {
                            BlockPos $$37 = $$35.relative($$1.rotate(Direction.SOUTH), 7);
                            $$37 = $$37.relative($$1.rotate(Direction.EAST), 7);
                            $$2.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, $$34 == Direction.SOUTH ? $$25 : $$24, $$37, $$1.getRotated(Rotation.CLOCKWISE_90)));
                        }
                        if ($$18.get($$28, $$27 - 1) == 1 && !$$29) {
                            BlockPos $$38 = $$35.relative($$1.rotate(Direction.NORTH), 1);
                            $$38 = $$38.relative($$1.rotate(Direction.EAST), 7);
                            $$2.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, $$34 == Direction.NORTH ? $$25 : $$24, $$38, $$1.getRotated(Rotation.CLOCKWISE_90)));
                        }
                        if ($$31 == 65536) {
                            this.addRoom1x1($$2, $$35, $$1, $$34, $$14[$$15]);
                            continue;
                        }
                        if ($$31 == 131072 && $$34 != null) {
                            Direction $$39 = $$3.get1x2RoomDirection($$18, $$28, $$27, $$15, $$32);
                            boolean $$40 = ($$30 & 0x400000) == 0x400000;
                            this.addRoom1x2($$2, $$35, $$1, $$39, $$34, $$14[$$15], $$40);
                            continue;
                        }
                        if ($$31 == 262144 && $$34 != null && $$34 != Direction.UP) {
                            Direction $$41 = $$34.getClockWise();
                            if (!$$3.isRoomId($$18, $$28 + $$41.getStepX(), $$27 + $$41.getStepZ(), $$15, $$32)) {
                                $$41 = $$41.getOpposite();
                            }
                            this.addRoom2x2($$2, $$35, $$1, $$41, $$34, $$14[$$15]);
                            continue;
                        }
                        if ($$31 != 262144 || $$34 != Direction.UP) continue;
                        this.addRoom2x2Secret($$2, $$35, $$1, $$14[$$15]);
                    }
                }
            }
        }

        private void traverseOuterWalls(List<WoodlandMansionPiece> $$0, PlacementData $$1, SimpleGrid $$2, Direction $$3, int $$4, int $$5, int $$6, int $$7) {
            int $$8 = $$4;
            int $$9 = $$5;
            Direction $$10 = $$3;
            do {
                if (!MansionGrid.isHouse($$2, $$8 + $$3.getStepX(), $$9 + $$3.getStepZ())) {
                    this.traverseTurn($$0, $$1);
                    $$3 = $$3.getClockWise();
                    if ($$8 == $$6 && $$9 == $$7 && $$10 == $$3) continue;
                    this.traverseWallPiece($$0, $$1);
                    continue;
                }
                if (MansionGrid.isHouse($$2, $$8 + $$3.getStepX(), $$9 + $$3.getStepZ()) && MansionGrid.isHouse($$2, $$8 + $$3.getStepX() + $$3.getCounterClockWise().getStepX(), $$9 + $$3.getStepZ() + $$3.getCounterClockWise().getStepZ())) {
                    this.traverseInnerTurn($$0, $$1);
                    $$8 += $$3.getStepX();
                    $$9 += $$3.getStepZ();
                    $$3 = $$3.getCounterClockWise();
                    continue;
                }
                if (($$8 += $$3.getStepX()) == $$6 && ($$9 += $$3.getStepZ()) == $$7 && $$10 == $$3) continue;
                this.traverseWallPiece($$0, $$1);
            } while ($$8 != $$6 || $$9 != $$7 || $$10 != $$3);
        }

        private void createRoof(List<WoodlandMansionPiece> $$0, BlockPos $$1, Rotation $$2, SimpleGrid $$3, @Nullable SimpleGrid $$4) {
            for (int $$5 = 0; $$5 < $$3.height; ++$$5) {
                for (int $$6 = 0; $$6 < $$3.width; ++$$6) {
                    boolean $$8;
                    BlockPos $$7 = $$1;
                    $$7 = $$7.relative($$2.rotate(Direction.SOUTH), 8 + ($$5 - this.startY) * 8);
                    $$7 = $$7.relative($$2.rotate(Direction.EAST), ($$6 - this.startX) * 8);
                    boolean bl = $$8 = $$4 != null && MansionGrid.isHouse($$4, $$6, $$5);
                    if (!MansionGrid.isHouse($$3, $$6, $$5) || $$8) continue;
                    $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, "roof", (BlockPos)$$7.above(3), $$2));
                    if (!MansionGrid.isHouse($$3, $$6 + 1, $$5)) {
                        BlockPos $$9 = $$7.relative($$2.rotate(Direction.EAST), 6);
                        $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, "roof_front", $$9, $$2));
                    }
                    if (!MansionGrid.isHouse($$3, $$6 - 1, $$5)) {
                        BlockPos $$10 = $$7.relative($$2.rotate(Direction.EAST), 0);
                        $$10 = $$10.relative($$2.rotate(Direction.SOUTH), 7);
                        $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, "roof_front", $$10, $$2.getRotated(Rotation.CLOCKWISE_180)));
                    }
                    if (!MansionGrid.isHouse($$3, $$6, $$5 - 1)) {
                        BlockPos $$11 = $$7.relative($$2.rotate(Direction.WEST), 1);
                        $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, "roof_front", $$11, $$2.getRotated(Rotation.COUNTERCLOCKWISE_90)));
                    }
                    if (MansionGrid.isHouse($$3, $$6, $$5 + 1)) continue;
                    BlockPos $$12 = $$7.relative($$2.rotate(Direction.EAST), 6);
                    $$12 = $$12.relative($$2.rotate(Direction.SOUTH), 6);
                    $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, "roof_front", $$12, $$2.getRotated(Rotation.CLOCKWISE_90)));
                }
            }
            if ($$4 != null) {
                for (int $$13 = 0; $$13 < $$3.height; ++$$13) {
                    for (int $$14 = 0; $$14 < $$3.width; ++$$14) {
                        BlockPos $$15 = $$1;
                        $$15 = $$15.relative($$2.rotate(Direction.SOUTH), 8 + ($$13 - this.startY) * 8);
                        $$15 = $$15.relative($$2.rotate(Direction.EAST), ($$14 - this.startX) * 8);
                        boolean $$16 = MansionGrid.isHouse($$4, $$14, $$13);
                        if (!MansionGrid.isHouse($$3, $$14, $$13) || !$$16) continue;
                        if (!MansionGrid.isHouse($$3, $$14 + 1, $$13)) {
                            BlockPos $$17 = $$15.relative($$2.rotate(Direction.EAST), 7);
                            $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, "small_wall", $$17, $$2));
                        }
                        if (!MansionGrid.isHouse($$3, $$14 - 1, $$13)) {
                            BlockPos $$18 = $$15.relative($$2.rotate(Direction.WEST), 1);
                            $$18 = $$18.relative($$2.rotate(Direction.SOUTH), 6);
                            $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, "small_wall", $$18, $$2.getRotated(Rotation.CLOCKWISE_180)));
                        }
                        if (!MansionGrid.isHouse($$3, $$14, $$13 - 1)) {
                            BlockPos $$19 = $$15.relative($$2.rotate(Direction.WEST), 0);
                            $$19 = $$19.relative($$2.rotate(Direction.NORTH), 1);
                            $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, "small_wall", $$19, $$2.getRotated(Rotation.COUNTERCLOCKWISE_90)));
                        }
                        if (!MansionGrid.isHouse($$3, $$14, $$13 + 1)) {
                            BlockPos $$20 = $$15.relative($$2.rotate(Direction.EAST), 6);
                            $$20 = $$20.relative($$2.rotate(Direction.SOUTH), 7);
                            $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, "small_wall", $$20, $$2.getRotated(Rotation.CLOCKWISE_90)));
                        }
                        if (!MansionGrid.isHouse($$3, $$14 + 1, $$13)) {
                            if (!MansionGrid.isHouse($$3, $$14, $$13 - 1)) {
                                BlockPos $$21 = $$15.relative($$2.rotate(Direction.EAST), 7);
                                $$21 = $$21.relative($$2.rotate(Direction.NORTH), 2);
                                $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, "small_wall_corner", $$21, $$2));
                            }
                            if (!MansionGrid.isHouse($$3, $$14, $$13 + 1)) {
                                BlockPos $$22 = $$15.relative($$2.rotate(Direction.EAST), 8);
                                $$22 = $$22.relative($$2.rotate(Direction.SOUTH), 7);
                                $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, "small_wall_corner", $$22, $$2.getRotated(Rotation.CLOCKWISE_90)));
                            }
                        }
                        if (MansionGrid.isHouse($$3, $$14 - 1, $$13)) continue;
                        if (!MansionGrid.isHouse($$3, $$14, $$13 - 1)) {
                            BlockPos $$23 = $$15.relative($$2.rotate(Direction.WEST), 2);
                            $$23 = $$23.relative($$2.rotate(Direction.NORTH), 1);
                            $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, "small_wall_corner", $$23, $$2.getRotated(Rotation.COUNTERCLOCKWISE_90)));
                        }
                        if (MansionGrid.isHouse($$3, $$14, $$13 + 1)) continue;
                        BlockPos $$24 = $$15.relative($$2.rotate(Direction.WEST), 1);
                        $$24 = $$24.relative($$2.rotate(Direction.SOUTH), 8);
                        $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, "small_wall_corner", $$24, $$2.getRotated(Rotation.CLOCKWISE_180)));
                    }
                }
            }
            for (int $$25 = 0; $$25 < $$3.height; ++$$25) {
                for (int $$26 = 0; $$26 < $$3.width; ++$$26) {
                    boolean $$28;
                    BlockPos $$27 = $$1;
                    $$27 = $$27.relative($$2.rotate(Direction.SOUTH), 8 + ($$25 - this.startY) * 8);
                    $$27 = $$27.relative($$2.rotate(Direction.EAST), ($$26 - this.startX) * 8);
                    boolean bl = $$28 = $$4 != null && MansionGrid.isHouse($$4, $$26, $$25);
                    if (!MansionGrid.isHouse($$3, $$26, $$25) || $$28) continue;
                    if (!MansionGrid.isHouse($$3, $$26 + 1, $$25)) {
                        BlockPos $$29 = $$27.relative($$2.rotate(Direction.EAST), 6);
                        if (!MansionGrid.isHouse($$3, $$26, $$25 + 1)) {
                            BlockPos $$30 = $$29.relative($$2.rotate(Direction.SOUTH), 6);
                            $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, "roof_corner", $$30, $$2));
                        } else if (MansionGrid.isHouse($$3, $$26 + 1, $$25 + 1)) {
                            BlockPos $$31 = $$29.relative($$2.rotate(Direction.SOUTH), 5);
                            $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, "roof_inner_corner", $$31, $$2));
                        }
                        if (!MansionGrid.isHouse($$3, $$26, $$25 - 1)) {
                            $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, "roof_corner", $$29, $$2.getRotated(Rotation.COUNTERCLOCKWISE_90)));
                        } else if (MansionGrid.isHouse($$3, $$26 + 1, $$25 - 1)) {
                            BlockPos $$32 = $$27.relative($$2.rotate(Direction.EAST), 9);
                            $$32 = $$32.relative($$2.rotate(Direction.NORTH), 2);
                            $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, "roof_inner_corner", $$32, $$2.getRotated(Rotation.CLOCKWISE_90)));
                        }
                    }
                    if (MansionGrid.isHouse($$3, $$26 - 1, $$25)) continue;
                    BlockPos $$33 = $$27.relative($$2.rotate(Direction.EAST), 0);
                    $$33 = $$33.relative($$2.rotate(Direction.SOUTH), 0);
                    if (!MansionGrid.isHouse($$3, $$26, $$25 + 1)) {
                        BlockPos $$34 = $$33.relative($$2.rotate(Direction.SOUTH), 6);
                        $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, "roof_corner", $$34, $$2.getRotated(Rotation.CLOCKWISE_90)));
                    } else if (MansionGrid.isHouse($$3, $$26 - 1, $$25 + 1)) {
                        BlockPos $$35 = $$33.relative($$2.rotate(Direction.SOUTH), 8);
                        $$35 = $$35.relative($$2.rotate(Direction.WEST), 3);
                        $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, "roof_inner_corner", $$35, $$2.getRotated(Rotation.COUNTERCLOCKWISE_90)));
                    }
                    if (!MansionGrid.isHouse($$3, $$26, $$25 - 1)) {
                        $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, "roof_corner", $$33, $$2.getRotated(Rotation.CLOCKWISE_180)));
                        continue;
                    }
                    if (!MansionGrid.isHouse($$3, $$26 - 1, $$25 - 1)) continue;
                    BlockPos $$36 = $$33.relative($$2.rotate(Direction.SOUTH), 1);
                    $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, "roof_inner_corner", $$36, $$2.getRotated(Rotation.CLOCKWISE_180)));
                }
            }
        }

        private void entrance(List<WoodlandMansionPiece> $$0, PlacementData $$1) {
            Direction $$2 = $$1.rotation.rotate(Direction.WEST);
            $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, "entrance", $$1.position.relative($$2, 9), $$1.rotation));
            $$1.position = $$1.position.relative($$1.rotation.rotate(Direction.SOUTH), 16);
        }

        private void traverseWallPiece(List<WoodlandMansionPiece> $$0, PlacementData $$1) {
            $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, $$1.wallType, $$1.position.relative($$1.rotation.rotate(Direction.EAST), 7), $$1.rotation));
            $$1.position = $$1.position.relative($$1.rotation.rotate(Direction.SOUTH), 8);
        }

        private void traverseTurn(List<WoodlandMansionPiece> $$0, PlacementData $$1) {
            $$1.position = $$1.position.relative($$1.rotation.rotate(Direction.SOUTH), -1);
            $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, "wall_corner", $$1.position, $$1.rotation));
            $$1.position = $$1.position.relative($$1.rotation.rotate(Direction.SOUTH), -7);
            $$1.position = $$1.position.relative($$1.rotation.rotate(Direction.WEST), -6);
            $$1.rotation = $$1.rotation.getRotated(Rotation.CLOCKWISE_90);
        }

        private void traverseInnerTurn(List<WoodlandMansionPiece> $$0, PlacementData $$1) {
            $$1.position = $$1.position.relative($$1.rotation.rotate(Direction.SOUTH), 6);
            $$1.position = $$1.position.relative($$1.rotation.rotate(Direction.EAST), 8);
            $$1.rotation = $$1.rotation.getRotated(Rotation.COUNTERCLOCKWISE_90);
        }

        private void addRoom1x1(List<WoodlandMansionPiece> $$0, BlockPos $$1, Rotation $$2, Direction $$3, FloorRoomCollection $$4) {
            Rotation $$5 = Rotation.NONE;
            String $$6 = $$4.get1x1(this.random);
            if ($$3 != Direction.EAST) {
                if ($$3 == Direction.NORTH) {
                    $$5 = $$5.getRotated(Rotation.COUNTERCLOCKWISE_90);
                } else if ($$3 == Direction.WEST) {
                    $$5 = $$5.getRotated(Rotation.CLOCKWISE_180);
                } else if ($$3 == Direction.SOUTH) {
                    $$5 = $$5.getRotated(Rotation.CLOCKWISE_90);
                } else {
                    $$6 = $$4.get1x1Secret(this.random);
                }
            }
            BlockPos $$7 = StructureTemplate.getZeroPositionWithTransform(new BlockPos(1, 0, 0), Mirror.NONE, $$5, 7, 7);
            $$5 = $$5.getRotated($$2);
            $$7 = $$7.rotate($$2);
            BlockPos $$8 = $$1.offset($$7.getX(), 0, $$7.getZ());
            $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, $$6, $$8, $$5));
        }

        private void addRoom1x2(List<WoodlandMansionPiece> $$0, BlockPos $$1, Rotation $$2, Direction $$3, Direction $$4, FloorRoomCollection $$5, boolean $$6) {
            if ($$4 == Direction.EAST && $$3 == Direction.SOUTH) {
                BlockPos $$7 = $$1.relative($$2.rotate(Direction.EAST), 1);
                $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, $$5.get1x2SideEntrance(this.random, $$6), $$7, $$2));
            } else if ($$4 == Direction.EAST && $$3 == Direction.NORTH) {
                BlockPos $$8 = $$1.relative($$2.rotate(Direction.EAST), 1);
                $$8 = $$8.relative($$2.rotate(Direction.SOUTH), 6);
                $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, $$5.get1x2SideEntrance(this.random, $$6), $$8, $$2, Mirror.LEFT_RIGHT));
            } else if ($$4 == Direction.WEST && $$3 == Direction.NORTH) {
                BlockPos $$9 = $$1.relative($$2.rotate(Direction.EAST), 7);
                $$9 = $$9.relative($$2.rotate(Direction.SOUTH), 6);
                $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, $$5.get1x2SideEntrance(this.random, $$6), $$9, $$2.getRotated(Rotation.CLOCKWISE_180)));
            } else if ($$4 == Direction.WEST && $$3 == Direction.SOUTH) {
                BlockPos $$10 = $$1.relative($$2.rotate(Direction.EAST), 7);
                $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, $$5.get1x2SideEntrance(this.random, $$6), $$10, $$2, Mirror.FRONT_BACK));
            } else if ($$4 == Direction.SOUTH && $$3 == Direction.EAST) {
                BlockPos $$11 = $$1.relative($$2.rotate(Direction.EAST), 1);
                $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, $$5.get1x2SideEntrance(this.random, $$6), $$11, $$2.getRotated(Rotation.CLOCKWISE_90), Mirror.LEFT_RIGHT));
            } else if ($$4 == Direction.SOUTH && $$3 == Direction.WEST) {
                BlockPos $$12 = $$1.relative($$2.rotate(Direction.EAST), 7);
                $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, $$5.get1x2SideEntrance(this.random, $$6), $$12, $$2.getRotated(Rotation.CLOCKWISE_90)));
            } else if ($$4 == Direction.NORTH && $$3 == Direction.WEST) {
                BlockPos $$13 = $$1.relative($$2.rotate(Direction.EAST), 7);
                $$13 = $$13.relative($$2.rotate(Direction.SOUTH), 6);
                $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, $$5.get1x2SideEntrance(this.random, $$6), $$13, $$2.getRotated(Rotation.CLOCKWISE_90), Mirror.FRONT_BACK));
            } else if ($$4 == Direction.NORTH && $$3 == Direction.EAST) {
                BlockPos $$14 = $$1.relative($$2.rotate(Direction.EAST), 1);
                $$14 = $$14.relative($$2.rotate(Direction.SOUTH), 6);
                $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, $$5.get1x2SideEntrance(this.random, $$6), $$14, $$2.getRotated(Rotation.COUNTERCLOCKWISE_90)));
            } else if ($$4 == Direction.SOUTH && $$3 == Direction.NORTH) {
                BlockPos $$15 = $$1.relative($$2.rotate(Direction.EAST), 1);
                $$15 = $$15.relative($$2.rotate(Direction.NORTH), 8);
                $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, $$5.get1x2FrontEntrance(this.random, $$6), $$15, $$2));
            } else if ($$4 == Direction.NORTH && $$3 == Direction.SOUTH) {
                BlockPos $$16 = $$1.relative($$2.rotate(Direction.EAST), 7);
                $$16 = $$16.relative($$2.rotate(Direction.SOUTH), 14);
                $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, $$5.get1x2FrontEntrance(this.random, $$6), $$16, $$2.getRotated(Rotation.CLOCKWISE_180)));
            } else if ($$4 == Direction.WEST && $$3 == Direction.EAST) {
                BlockPos $$17 = $$1.relative($$2.rotate(Direction.EAST), 15);
                $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, $$5.get1x2FrontEntrance(this.random, $$6), $$17, $$2.getRotated(Rotation.CLOCKWISE_90)));
            } else if ($$4 == Direction.EAST && $$3 == Direction.WEST) {
                BlockPos $$18 = $$1.relative($$2.rotate(Direction.WEST), 7);
                $$18 = $$18.relative($$2.rotate(Direction.SOUTH), 6);
                $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, $$5.get1x2FrontEntrance(this.random, $$6), $$18, $$2.getRotated(Rotation.COUNTERCLOCKWISE_90)));
            } else if ($$4 == Direction.UP && $$3 == Direction.EAST) {
                BlockPos $$19 = $$1.relative($$2.rotate(Direction.EAST), 15);
                $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, $$5.get1x2Secret(this.random), $$19, $$2.getRotated(Rotation.CLOCKWISE_90)));
            } else if ($$4 == Direction.UP && $$3 == Direction.SOUTH) {
                BlockPos $$20 = $$1.relative($$2.rotate(Direction.EAST), 1);
                $$20 = $$20.relative($$2.rotate(Direction.NORTH), 0);
                $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, $$5.get1x2Secret(this.random), $$20, $$2));
            }
        }

        private void addRoom2x2(List<WoodlandMansionPiece> $$0, BlockPos $$1, Rotation $$2, Direction $$3, Direction $$4, FloorRoomCollection $$5) {
            int $$6 = 0;
            int $$7 = 0;
            Rotation $$8 = $$2;
            Mirror $$9 = Mirror.NONE;
            if ($$4 == Direction.EAST && $$3 == Direction.SOUTH) {
                $$6 = -7;
            } else if ($$4 == Direction.EAST && $$3 == Direction.NORTH) {
                $$6 = -7;
                $$7 = 6;
                $$9 = Mirror.LEFT_RIGHT;
            } else if ($$4 == Direction.NORTH && $$3 == Direction.EAST) {
                $$6 = 1;
                $$7 = 14;
                $$8 = $$2.getRotated(Rotation.COUNTERCLOCKWISE_90);
            } else if ($$4 == Direction.NORTH && $$3 == Direction.WEST) {
                $$6 = 7;
                $$7 = 14;
                $$8 = $$2.getRotated(Rotation.COUNTERCLOCKWISE_90);
                $$9 = Mirror.LEFT_RIGHT;
            } else if ($$4 == Direction.SOUTH && $$3 == Direction.WEST) {
                $$6 = 7;
                $$7 = -8;
                $$8 = $$2.getRotated(Rotation.CLOCKWISE_90);
            } else if ($$4 == Direction.SOUTH && $$3 == Direction.EAST) {
                $$6 = 1;
                $$7 = -8;
                $$8 = $$2.getRotated(Rotation.CLOCKWISE_90);
                $$9 = Mirror.LEFT_RIGHT;
            } else if ($$4 == Direction.WEST && $$3 == Direction.NORTH) {
                $$6 = 15;
                $$7 = 6;
                $$8 = $$2.getRotated(Rotation.CLOCKWISE_180);
            } else if ($$4 == Direction.WEST && $$3 == Direction.SOUTH) {
                $$6 = 15;
                $$9 = Mirror.FRONT_BACK;
            }
            BlockPos $$10 = $$1.relative($$2.rotate(Direction.EAST), $$6);
            $$10 = $$10.relative($$2.rotate(Direction.SOUTH), $$7);
            $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, $$5.get2x2(this.random), $$10, $$8, $$9));
        }

        private void addRoom2x2Secret(List<WoodlandMansionPiece> $$0, BlockPos $$1, Rotation $$2, FloorRoomCollection $$3) {
            BlockPos $$4 = $$1.relative($$2.rotate(Direction.EAST), 1);
            $$0.add((Object)new WoodlandMansionPiece(this.structureTemplateManager, $$3.get2x2Secret(this.random), $$4, $$2, Mirror.NONE));
        }
    }

    static class ThirdFloorRoomCollection
    extends SecondFloorRoomCollection {
        ThirdFloorRoomCollection() {
        }
    }

    static class SecondFloorRoomCollection
    extends FloorRoomCollection {
        SecondFloorRoomCollection() {
        }

        @Override
        public String get1x1(RandomSource $$0) {
            return "1x1_b" + ($$0.nextInt(4) + 1);
        }

        @Override
        public String get1x1Secret(RandomSource $$0) {
            return "1x1_as" + ($$0.nextInt(4) + 1);
        }

        @Override
        public String get1x2SideEntrance(RandomSource $$0, boolean $$1) {
            if ($$1) {
                return "1x2_c_stairs";
            }
            return "1x2_c" + ($$0.nextInt(4) + 1);
        }

        @Override
        public String get1x2FrontEntrance(RandomSource $$0, boolean $$1) {
            if ($$1) {
                return "1x2_d_stairs";
            }
            return "1x2_d" + ($$0.nextInt(5) + 1);
        }

        @Override
        public String get1x2Secret(RandomSource $$0) {
            return "1x2_se" + ($$0.nextInt(1) + 1);
        }

        @Override
        public String get2x2(RandomSource $$0) {
            return "2x2_b" + ($$0.nextInt(5) + 1);
        }

        @Override
        public String get2x2Secret(RandomSource $$0) {
            return "2x2_s1";
        }
    }

    static class FirstFloorRoomCollection
    extends FloorRoomCollection {
        FirstFloorRoomCollection() {
        }

        @Override
        public String get1x1(RandomSource $$0) {
            return "1x1_a" + ($$0.nextInt(5) + 1);
        }

        @Override
        public String get1x1Secret(RandomSource $$0) {
            return "1x1_as" + ($$0.nextInt(4) + 1);
        }

        @Override
        public String get1x2SideEntrance(RandomSource $$0, boolean $$1) {
            return "1x2_a" + ($$0.nextInt(9) + 1);
        }

        @Override
        public String get1x2FrontEntrance(RandomSource $$0, boolean $$1) {
            return "1x2_b" + ($$0.nextInt(5) + 1);
        }

        @Override
        public String get1x2Secret(RandomSource $$0) {
            return "1x2_s" + ($$0.nextInt(2) + 1);
        }

        @Override
        public String get2x2(RandomSource $$0) {
            return "2x2_a" + ($$0.nextInt(4) + 1);
        }

        @Override
        public String get2x2Secret(RandomSource $$0) {
            return "2x2_s1";
        }
    }

    static abstract class FloorRoomCollection {
        FloorRoomCollection() {
        }

        public abstract String get1x1(RandomSource var1);

        public abstract String get1x1Secret(RandomSource var1);

        public abstract String get1x2SideEntrance(RandomSource var1, boolean var2);

        public abstract String get1x2FrontEntrance(RandomSource var1, boolean var2);

        public abstract String get1x2Secret(RandomSource var1);

        public abstract String get2x2(RandomSource var1);

        public abstract String get2x2Secret(RandomSource var1);
    }

    static class SimpleGrid {
        private final int[][] grid;
        final int width;
        final int height;
        private final int valueIfOutside;

        public SimpleGrid(int $$0, int $$1, int $$2) {
            this.width = $$0;
            this.height = $$1;
            this.valueIfOutside = $$2;
            this.grid = new int[$$0][$$1];
        }

        public void set(int $$0, int $$1, int $$2) {
            if ($$0 >= 0 && $$0 < this.width && $$1 >= 0 && $$1 < this.height) {
                this.grid[$$0][$$1] = $$2;
            }
        }

        public void set(int $$0, int $$1, int $$2, int $$3, int $$4) {
            for (int $$5 = $$1; $$5 <= $$3; ++$$5) {
                for (int $$6 = $$0; $$6 <= $$2; ++$$6) {
                    this.set($$6, $$5, $$4);
                }
            }
        }

        public int get(int $$0, int $$1) {
            if ($$0 >= 0 && $$0 < this.width && $$1 >= 0 && $$1 < this.height) {
                return this.grid[$$0][$$1];
            }
            return this.valueIfOutside;
        }

        public void setif(int $$0, int $$1, int $$2, int $$3) {
            if (this.get($$0, $$1) == $$2) {
                this.set($$0, $$1, $$3);
            }
        }

        public boolean edgesTo(int $$0, int $$1, int $$2) {
            return this.get($$0 - 1, $$1) == $$2 || this.get($$0 + 1, $$1) == $$2 || this.get($$0, $$1 + 1) == $$2 || this.get($$0, $$1 - 1) == $$2;
        }
    }

    static class PlacementData {
        public Rotation rotation;
        public BlockPos position;
        public String wallType;

        PlacementData() {
        }
    }

    public static class WoodlandMansionPiece
    extends TemplateStructurePiece {
        public WoodlandMansionPiece(StructureTemplateManager $$0, String $$1, BlockPos $$2, Rotation $$3) {
            this($$0, $$1, $$2, $$3, Mirror.NONE);
        }

        public WoodlandMansionPiece(StructureTemplateManager $$0, String $$1, BlockPos $$2, Rotation $$3, Mirror $$4) {
            super(StructurePieceType.WOODLAND_MANSION_PIECE, 0, $$0, WoodlandMansionPiece.makeLocation($$1), $$1, WoodlandMansionPiece.makeSettings($$4, $$3), $$2);
        }

        public WoodlandMansionPiece(StructureTemplateManager $$0, CompoundTag $$12) {
            super(StructurePieceType.WOODLAND_MANSION_PIECE, $$12, $$0, (Function<ResourceLocation, StructurePlaceSettings>)((Function)$$1 -> WoodlandMansionPiece.makeSettings(Mirror.valueOf($$12.getString("Mi")), Rotation.valueOf($$12.getString("Rot")))));
        }

        @Override
        protected ResourceLocation makeTemplateLocation() {
            return WoodlandMansionPiece.makeLocation(this.templateName);
        }

        private static ResourceLocation makeLocation(String $$0) {
            return new ResourceLocation("woodland_mansion/" + $$0);
        }

        private static StructurePlaceSettings makeSettings(Mirror $$0, Rotation $$1) {
            return new StructurePlaceSettings().setIgnoreEntities(true).setRotation($$1).setMirror($$0).addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
        }

        @Override
        protected void addAdditionalSaveData(StructurePieceSerializationContext $$0, CompoundTag $$1) {
            super.addAdditionalSaveData($$0, $$1);
            $$1.putString("Rot", this.placeSettings.getRotation().name());
            $$1.putString("Mi", this.placeSettings.getMirror().name());
        }

        /*
         * Exception decompiling
         */
        @Override
        protected void handleDataMarker(String $$0, BlockPos $$1, ServerLevelAccessor $$2, RandomSource $$3, BoundingBox $$4) {
            /*
             * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
             * 
             * org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.SwitchStringRewriter$TooOptimisticMatchException
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.SwitchStringRewriter.getString(SwitchStringRewriter.java:404)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.SwitchStringRewriter.access$600(SwitchStringRewriter.java:53)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.SwitchStringRewriter$SwitchStringMatchResultCollector.collectMatches(SwitchStringRewriter.java:368)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.matchutil.ResetAfterTest.match(ResetAfterTest.java:24)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.matchutil.KleeneN.match(KleeneN.java:24)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.matchutil.MatchSequence.match(MatchSequence.java:26)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.matchutil.ResetAfterTest.match(ResetAfterTest.java:23)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.SwitchStringRewriter.rewriteComplex(SwitchStringRewriter.java:201)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.SwitchStringRewriter.rewrite(SwitchStringRewriter.java:73)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:881)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
             *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
             *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseInnerClassesPass1(ClassFile.java:923)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1035)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
             *     at cuchaz.enigma.source.cfr.CfrSource.ensureDecompiled(CfrSource.java:81)
             *     at cuchaz.enigma.source.cfr.CfrSource.asString(CfrSource.java:50)
             *     at cuchaz.enigma.EnigmaProject$JarExport.decompileClass(EnigmaProject.java:298)
             *     at cuchaz.enigma.EnigmaProject$JarExport.lambda$decompileStream$1(EnigmaProject.java:274)
             *     at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:197)
             *     at java.base/java.util.AbstractList$RandomAccessSpliterator.forEachRemaining(AbstractList.java:722)
             *     at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:509)
             *     at java.base/java.util.stream.ForEachOps$ForEachTask.compute(ForEachOps.java:290)
             *     at java.base/java.util.concurrent.CountedCompleter.exec(CountedCompleter.java:754)
             *     at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:387)
             *     at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1311)
             *     at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1840)
             *     at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1806)
             *     at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:177)
             */
            throw new IllegalStateException("Decompilation failed");
        }
    }
}