/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  it.unimi.dsi.fastutil.objects.ObjectListIterator
 *  java.lang.Enum
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.System
 *  java.util.EnumSet
 *  java.util.Set
 *  java.util.function.Predicate
 *  java.util.function.Supplier
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.levelgen;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.util.BitStorage;
import net.minecraft.util.Mth;
import net.minecraft.util.SimpleBitStorage;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.slf4j.Logger;

public class Heightmap {
    private static final Logger LOGGER = LogUtils.getLogger();
    static final Predicate<BlockState> NOT_AIR = $$0 -> !$$0.isAir();
    static final Predicate<BlockState> MATERIAL_MOTION_BLOCKING = $$0 -> $$0.getMaterial().blocksMotion();
    private final BitStorage data;
    private final Predicate<BlockState> isOpaque;
    private final ChunkAccess chunk;

    public Heightmap(ChunkAccess $$0, Types $$1) {
        this.isOpaque = $$1.isOpaque();
        this.chunk = $$0;
        int $$2 = Mth.ceillog2($$0.getHeight() + 1);
        this.data = new SimpleBitStorage($$2, 256);
    }

    public static void primeHeightmaps(ChunkAccess $$0, Set<Types> $$1) {
        int $$2 = $$1.size();
        ObjectArrayList $$3 = new ObjectArrayList($$2);
        ObjectListIterator $$4 = $$3.iterator();
        int $$5 = $$0.getHighestSectionPosition() + 16;
        BlockPos.MutableBlockPos $$6 = new BlockPos.MutableBlockPos();
        for (int $$7 = 0; $$7 < 16; ++$$7) {
            block1: for (int $$8 = 0; $$8 < 16; ++$$8) {
                for (Types $$9 : $$1) {
                    $$3.add((Object)$$0.getOrCreateHeightmapUnprimed($$9));
                }
                for (int $$10 = $$5 - 1; $$10 >= $$0.getMinBuildHeight(); --$$10) {
                    $$6.set($$7, $$10, $$8);
                    BlockState $$11 = $$0.getBlockState($$6);
                    if ($$11.is(Blocks.AIR)) continue;
                    while ($$4.hasNext()) {
                        Heightmap $$12 = (Heightmap)$$4.next();
                        if (!$$12.isOpaque.test((Object)$$11)) continue;
                        $$12.setHeight($$7, $$8, $$10 + 1);
                        $$4.remove();
                    }
                    if ($$3.isEmpty()) continue block1;
                    $$4.back($$2);
                }
            }
        }
    }

    public boolean update(int $$0, int $$1, int $$2, BlockState $$3) {
        int $$4 = this.getFirstAvailable($$0, $$2);
        if ($$1 <= $$4 - 2) {
            return false;
        }
        if (this.isOpaque.test((Object)$$3)) {
            if ($$1 >= $$4) {
                this.setHeight($$0, $$2, $$1 + 1);
                return true;
            }
        } else if ($$4 - 1 == $$1) {
            BlockPos.MutableBlockPos $$5 = new BlockPos.MutableBlockPos();
            for (int $$6 = $$1 - 1; $$6 >= this.chunk.getMinBuildHeight(); --$$6) {
                $$5.set($$0, $$6, $$2);
                if (!this.isOpaque.test((Object)this.chunk.getBlockState($$5))) continue;
                this.setHeight($$0, $$2, $$6 + 1);
                return true;
            }
            this.setHeight($$0, $$2, this.chunk.getMinBuildHeight());
            return true;
        }
        return false;
    }

    public int getFirstAvailable(int $$0, int $$1) {
        return this.getFirstAvailable(Heightmap.getIndex($$0, $$1));
    }

    public int getHighestTaken(int $$0, int $$1) {
        return this.getFirstAvailable(Heightmap.getIndex($$0, $$1)) - 1;
    }

    private int getFirstAvailable(int $$0) {
        return this.data.get($$0) + this.chunk.getMinBuildHeight();
    }

    private void setHeight(int $$0, int $$1, int $$2) {
        this.data.set(Heightmap.getIndex($$0, $$1), $$2 - this.chunk.getMinBuildHeight());
    }

    public void setRawData(ChunkAccess $$0, Types $$1, long[] $$2) {
        long[] $$3 = this.data.getRaw();
        if ($$3.length == $$2.length) {
            System.arraycopy((Object)$$2, (int)0, (Object)$$3, (int)0, (int)$$2.length);
            return;
        }
        LOGGER.warn("Ignoring heightmap data for chunk " + $$0.getPos() + ", size does not match; expected: " + $$3.length + ", got: " + $$2.length);
        Heightmap.primeHeightmaps($$0, (Set<Types>)EnumSet.of((Enum)$$1));
    }

    public long[] getRawData() {
        return this.data.getRaw();
    }

    private static int getIndex(int $$0, int $$1) {
        return $$0 + $$1 * 16;
    }

    public static enum Types implements StringRepresentable
    {
        WORLD_SURFACE_WG("WORLD_SURFACE_WG", Usage.WORLDGEN, NOT_AIR),
        WORLD_SURFACE("WORLD_SURFACE", Usage.CLIENT, NOT_AIR),
        OCEAN_FLOOR_WG("OCEAN_FLOOR_WG", Usage.WORLDGEN, MATERIAL_MOTION_BLOCKING),
        OCEAN_FLOOR("OCEAN_FLOOR", Usage.LIVE_WORLD, MATERIAL_MOTION_BLOCKING),
        MOTION_BLOCKING("MOTION_BLOCKING", Usage.CLIENT, (Predicate<BlockState>)((Predicate)$$0 -> $$0.getMaterial().blocksMotion() || !$$0.getFluidState().isEmpty())),
        MOTION_BLOCKING_NO_LEAVES("MOTION_BLOCKING_NO_LEAVES", Usage.LIVE_WORLD, (Predicate<BlockState>)((Predicate)$$0 -> ($$0.getMaterial().blocksMotion() || !$$0.getFluidState().isEmpty()) && !($$0.getBlock() instanceof LeavesBlock)));

        public static final Codec<Types> CODEC;
        private final String serializationKey;
        private final Usage usage;
        private final Predicate<BlockState> isOpaque;

        private Types(String $$0, Usage $$1, Predicate<BlockState> $$2) {
            this.serializationKey = $$0;
            this.usage = $$1;
            this.isOpaque = $$2;
        }

        public String getSerializationKey() {
            return this.serializationKey;
        }

        public boolean sendToClient() {
            return this.usage == Usage.CLIENT;
        }

        public boolean keepAfterWorldgen() {
            return this.usage != Usage.WORLDGEN;
        }

        public Predicate<BlockState> isOpaque() {
            return this.isOpaque;
        }

        @Override
        public String getSerializedName() {
            return this.serializationKey;
        }

        static {
            CODEC = StringRepresentable.fromEnum((Supplier<E[]>)((Supplier)Types::values));
        }
    }

    public static enum Usage {
        WORLDGEN,
        LIVE_WORLD,
        CLIENT;

    }
}