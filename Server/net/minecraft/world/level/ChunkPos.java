/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Spliterator
 *  java.util.Spliterators$AbstractSpliterator
 *  java.util.function.Consumer
 *  java.util.stream.Stream
 *  java.util.stream.StreamSupport
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;

public class ChunkPos {
    private static final int SAFETY_MARGIN = 1056;
    public static final long INVALID_CHUNK_POS = ChunkPos.asLong(1875066, 1875066);
    public static final ChunkPos ZERO = new ChunkPos(0, 0);
    private static final long COORD_BITS = 32L;
    private static final long COORD_MASK = 0xFFFFFFFFL;
    private static final int REGION_BITS = 5;
    public static final int REGION_SIZE = 32;
    private static final int REGION_MASK = 31;
    public static final int REGION_MAX_INDEX = 31;
    public final int x;
    public final int z;
    private static final int HASH_A = 1664525;
    private static final int HASH_C = 1013904223;
    private static final int HASH_Z_XOR = -559038737;

    public ChunkPos(int $$0, int $$1) {
        this.x = $$0;
        this.z = $$1;
    }

    public ChunkPos(BlockPos $$0) {
        this.x = SectionPos.blockToSectionCoord($$0.getX());
        this.z = SectionPos.blockToSectionCoord($$0.getZ());
    }

    public ChunkPos(long $$0) {
        this.x = (int)$$0;
        this.z = (int)($$0 >> 32);
    }

    public static ChunkPos minFromRegion(int $$0, int $$1) {
        return new ChunkPos($$0 << 5, $$1 << 5);
    }

    public static ChunkPos maxFromRegion(int $$0, int $$1) {
        return new ChunkPos(($$0 << 5) + 31, ($$1 << 5) + 31);
    }

    public long toLong() {
        return ChunkPos.asLong(this.x, this.z);
    }

    public static long asLong(int $$0, int $$1) {
        return (long)$$0 & 0xFFFFFFFFL | ((long)$$1 & 0xFFFFFFFFL) << 32;
    }

    public static long asLong(BlockPos $$0) {
        return ChunkPos.asLong(SectionPos.blockToSectionCoord($$0.getX()), SectionPos.blockToSectionCoord($$0.getZ()));
    }

    public static int getX(long $$0) {
        return (int)($$0 & 0xFFFFFFFFL);
    }

    public static int getZ(long $$0) {
        return (int)($$0 >>> 32 & 0xFFFFFFFFL);
    }

    public int hashCode() {
        return ChunkPos.hash(this.x, this.z);
    }

    public static int hash(int $$0, int $$1) {
        int $$2 = 1664525 * $$0 + 1013904223;
        int $$3 = 1664525 * ($$1 ^ 0xDEADBEEF) + 1013904223;
        return $$2 ^ $$3;
    }

    public boolean equals(Object $$0) {
        if (this == $$0) {
            return true;
        }
        if ($$0 instanceof ChunkPos) {
            ChunkPos $$1 = (ChunkPos)$$0;
            return this.x == $$1.x && this.z == $$1.z;
        }
        return false;
    }

    public int getMiddleBlockX() {
        return this.getBlockX(8);
    }

    public int getMiddleBlockZ() {
        return this.getBlockZ(8);
    }

    public int getMinBlockX() {
        return SectionPos.sectionToBlockCoord(this.x);
    }

    public int getMinBlockZ() {
        return SectionPos.sectionToBlockCoord(this.z);
    }

    public int getMaxBlockX() {
        return this.getBlockX(15);
    }

    public int getMaxBlockZ() {
        return this.getBlockZ(15);
    }

    public int getRegionX() {
        return this.x >> 5;
    }

    public int getRegionZ() {
        return this.z >> 5;
    }

    public int getRegionLocalX() {
        return this.x & 0x1F;
    }

    public int getRegionLocalZ() {
        return this.z & 0x1F;
    }

    public BlockPos getBlockAt(int $$0, int $$1, int $$2) {
        return new BlockPos(this.getBlockX($$0), $$1, this.getBlockZ($$2));
    }

    public int getBlockX(int $$0) {
        return SectionPos.sectionToBlockCoord(this.x, $$0);
    }

    public int getBlockZ(int $$0) {
        return SectionPos.sectionToBlockCoord(this.z, $$0);
    }

    public BlockPos getMiddleBlockPosition(int $$0) {
        return new BlockPos(this.getMiddleBlockX(), $$0, this.getMiddleBlockZ());
    }

    public String toString() {
        return "[" + this.x + ", " + this.z + "]";
    }

    public BlockPos getWorldPosition() {
        return new BlockPos(this.getMinBlockX(), 0, this.getMinBlockZ());
    }

    public int getChessboardDistance(ChunkPos $$0) {
        return Math.max((int)Math.abs((int)(this.x - $$0.x)), (int)Math.abs((int)(this.z - $$0.z)));
    }

    public static Stream<ChunkPos> rangeClosed(ChunkPos $$0, int $$1) {
        return ChunkPos.rangeClosed(new ChunkPos($$0.x - $$1, $$0.z - $$1), new ChunkPos($$0.x + $$1, $$0.z + $$1));
    }

    public static Stream<ChunkPos> rangeClosed(final ChunkPos $$0, final ChunkPos $$1) {
        int $$2 = Math.abs((int)($$0.x - $$1.x)) + 1;
        int $$3 = Math.abs((int)($$0.z - $$1.z)) + 1;
        final int $$4 = $$0.x < $$1.x ? 1 : -1;
        final int $$5 = $$0.z < $$1.z ? 1 : -1;
        return StreamSupport.stream((Spliterator)new Spliterators.AbstractSpliterator<ChunkPos>((long)($$2 * $$3), 64){
            @Nullable
            private ChunkPos pos;

            public boolean tryAdvance(Consumer<? super ChunkPos> $$02) {
                if (this.pos == null) {
                    this.pos = $$0;
                } else {
                    int $$12 = this.pos.x;
                    int $$2 = this.pos.z;
                    if ($$12 == $$1.x) {
                        if ($$2 == $$1.z) {
                            return false;
                        }
                        this.pos = new ChunkPos($$0.x, $$2 + $$5);
                    } else {
                        this.pos = new ChunkPos($$12 + $$4, $$2);
                    }
                }
                $$02.accept((Object)this.pos);
                return true;
            }
        }, (boolean)false);
    }
}