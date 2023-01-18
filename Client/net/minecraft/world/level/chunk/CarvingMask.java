/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.BitSet
 *  java.util.stream.Stream
 */
package net.minecraft.world.level.chunk;

import java.util.BitSet;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

public class CarvingMask {
    private final int minY;
    private final BitSet mask;
    private Mask additionalMask = ($$0, $$1, $$2) -> false;

    public CarvingMask(int $$02, int $$12) {
        this.minY = $$12;
        this.mask = new BitSet(256 * $$02);
    }

    public void setAdditionalMask(Mask $$0) {
        this.additionalMask = $$0;
    }

    public CarvingMask(long[] $$02, int $$12) {
        this.minY = $$12;
        this.mask = BitSet.valueOf((long[])$$02);
    }

    private int getIndex(int $$0, int $$1, int $$2) {
        return $$0 & 0xF | ($$2 & 0xF) << 4 | $$1 - this.minY << 8;
    }

    public void set(int $$0, int $$1, int $$2) {
        this.mask.set(this.getIndex($$0, $$1, $$2));
    }

    public boolean get(int $$0, int $$1, int $$2) {
        return this.additionalMask.test($$0, $$1, $$2) || this.mask.get(this.getIndex($$0, $$1, $$2));
    }

    public Stream<BlockPos> stream(ChunkPos $$0) {
        return this.mask.stream().mapToObj($$1 -> {
            int $$2 = $$1 & 0xF;
            int $$3 = $$1 >> 4 & 0xF;
            int $$4 = $$1 >> 8;
            return $$0.getBlockAt($$2, $$4 + this.minY, $$3);
        });
    }

    public long[] toArray() {
        return this.mask.toLongArray();
    }

    public static interface Mask {
        public boolean test(int var1, int var2, int var3);
    }
}