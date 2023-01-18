/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue
 *  java.lang.Math
 *  java.lang.Object
 *  java.util.BitSet
 *  java.util.EnumSet
 *  java.util.Set
 */
package net.minecraft.client.renderer.chunk;

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.Util;
import net.minecraft.client.renderer.chunk.VisibilitySet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class VisGraph {
    private static final int SIZE_IN_BITS = 4;
    private static final int LEN = 16;
    private static final int MASK = 15;
    private static final int SIZE = 4096;
    private static final int X_SHIFT = 0;
    private static final int Z_SHIFT = 4;
    private static final int Y_SHIFT = 8;
    private static final int DX = (int)Math.pow((double)16.0, (double)0.0);
    private static final int DZ = (int)Math.pow((double)16.0, (double)1.0);
    private static final int DY = (int)Math.pow((double)16.0, (double)2.0);
    private static final int INVALID_INDEX = -1;
    private static final Direction[] DIRECTIONS = Direction.values();
    private final BitSet bitSet = new BitSet(4096);
    private static final int[] INDEX_OF_EDGES = Util.make(new int[1352], $$0 -> {
        boolean $$1 = false;
        int $$2 = 15;
        int $$3 = 0;
        for (int $$4 = 0; $$4 < 16; ++$$4) {
            for (int $$5 = 0; $$5 < 16; ++$$5) {
                for (int $$6 = 0; $$6 < 16; ++$$6) {
                    if ($$4 != 0 && $$4 != 15 && $$5 != 0 && $$5 != 15 && $$6 != 0 && $$6 != 15) continue;
                    $$0[$$3++] = VisGraph.getIndex($$4, $$5, $$6);
                }
            }
        }
    });
    private int empty = 4096;

    public void setOpaque(BlockPos $$0) {
        this.bitSet.set(VisGraph.getIndex($$0), true);
        --this.empty;
    }

    private static int getIndex(BlockPos $$0) {
        return VisGraph.getIndex($$0.getX() & 0xF, $$0.getY() & 0xF, $$0.getZ() & 0xF);
    }

    private static int getIndex(int $$0, int $$1, int $$2) {
        return $$0 << 0 | $$1 << 8 | $$2 << 4;
    }

    public VisibilitySet resolve() {
        VisibilitySet $$0 = new VisibilitySet();
        if (4096 - this.empty < 256) {
            $$0.setAll(true);
        } else if (this.empty == 0) {
            $$0.setAll(false);
        } else {
            for (int $$1 : INDEX_OF_EDGES) {
                if (this.bitSet.get($$1)) continue;
                $$0.add(this.floodFill($$1));
            }
        }
        return $$0;
    }

    private Set<Direction> floodFill(int $$0) {
        EnumSet $$1 = EnumSet.noneOf(Direction.class);
        IntArrayFIFOQueue $$2 = new IntArrayFIFOQueue();
        $$2.enqueue($$0);
        this.bitSet.set($$0, true);
        while (!$$2.isEmpty()) {
            int $$3 = $$2.dequeueInt();
            this.addEdges($$3, (Set<Direction>)$$1);
            for (Direction $$4 : DIRECTIONS) {
                int $$5 = this.getNeighborIndexAtFace($$3, $$4);
                if ($$5 < 0 || this.bitSet.get($$5)) continue;
                this.bitSet.set($$5, true);
                $$2.enqueue($$5);
            }
        }
        return $$1;
    }

    private void addEdges(int $$0, Set<Direction> $$1) {
        int $$2 = $$0 >> 0 & 0xF;
        if ($$2 == 0) {
            $$1.add((Object)Direction.WEST);
        } else if ($$2 == 15) {
            $$1.add((Object)Direction.EAST);
        }
        int $$3 = $$0 >> 8 & 0xF;
        if ($$3 == 0) {
            $$1.add((Object)Direction.DOWN);
        } else if ($$3 == 15) {
            $$1.add((Object)Direction.UP);
        }
        int $$4 = $$0 >> 4 & 0xF;
        if ($$4 == 0) {
            $$1.add((Object)Direction.NORTH);
        } else if ($$4 == 15) {
            $$1.add((Object)Direction.SOUTH);
        }
    }

    private int getNeighborIndexAtFace(int $$0, Direction $$1) {
        switch ($$1) {
            case DOWN: {
                if (($$0 >> 8 & 0xF) == 0) {
                    return -1;
                }
                return $$0 - DY;
            }
            case UP: {
                if (($$0 >> 8 & 0xF) == 15) {
                    return -1;
                }
                return $$0 + DY;
            }
            case NORTH: {
                if (($$0 >> 4 & 0xF) == 0) {
                    return -1;
                }
                return $$0 - DZ;
            }
            case SOUTH: {
                if (($$0 >> 4 & 0xF) == 15) {
                    return -1;
                }
                return $$0 + DZ;
            }
            case WEST: {
                if (($$0 >> 0 & 0xF) == 0) {
                    return -1;
                }
                return $$0 - DX;
            }
            case EAST: {
                if (($$0 >> 0 & 0xF) == 15) {
                    return -1;
                }
                return $$0 + DX;
            }
        }
        return -1;
    }
}