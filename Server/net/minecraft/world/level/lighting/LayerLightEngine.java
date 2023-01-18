/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Long
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Arrays
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.world.level.lighting;

import java.util.Arrays;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.DataLayerStorageMap;
import net.minecraft.world.level.lighting.DynamicGraphMinFixedPoint;
import net.minecraft.world.level.lighting.LayerLightEventListener;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableInt;

public abstract class LayerLightEngine<M extends DataLayerStorageMap<M>, S extends LayerLightSectionStorage<M>>
extends DynamicGraphMinFixedPoint
implements LayerLightEventListener {
    public static final long SELF_SOURCE = Long.MAX_VALUE;
    private static final Direction[] DIRECTIONS = Direction.values();
    protected final LightChunkGetter chunkSource;
    protected final LightLayer layer;
    protected final S storage;
    private boolean runningLightUpdates;
    protected final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
    private static final int CACHE_SIZE = 2;
    private final long[] lastChunkPos = new long[2];
    private final BlockGetter[] lastChunk = new BlockGetter[2];

    public LayerLightEngine(LightChunkGetter $$0, LightLayer $$1, S $$2) {
        super(16, 256, 8192);
        this.chunkSource = $$0;
        this.layer = $$1;
        this.storage = $$2;
        this.clearCache();
    }

    @Override
    protected void checkNode(long $$0) {
        ((LayerLightSectionStorage)this.storage).runAllUpdates();
        if (((LayerLightSectionStorage)this.storage).storingLightForSection(SectionPos.blockToSection($$0))) {
            super.checkNode($$0);
        }
    }

    @Nullable
    private BlockGetter getChunk(int $$0, int $$1) {
        long $$2 = ChunkPos.asLong($$0, $$1);
        for (int $$3 = 0; $$3 < 2; ++$$3) {
            if ($$2 != this.lastChunkPos[$$3]) continue;
            return this.lastChunk[$$3];
        }
        BlockGetter $$4 = this.chunkSource.getChunkForLighting($$0, $$1);
        for (int $$5 = 1; $$5 > 0; --$$5) {
            this.lastChunkPos[$$5] = this.lastChunkPos[$$5 - 1];
            this.lastChunk[$$5] = this.lastChunk[$$5 - 1];
        }
        this.lastChunkPos[0] = $$2;
        this.lastChunk[0] = $$4;
        return $$4;
    }

    private void clearCache() {
        Arrays.fill((long[])this.lastChunkPos, (long)ChunkPos.INVALID_CHUNK_POS);
        Arrays.fill((Object[])this.lastChunk, null);
    }

    protected BlockState getStateAndOpacity(long $$0, @Nullable MutableInt $$1) {
        boolean $$6;
        int $$3;
        if ($$0 == Long.MAX_VALUE) {
            if ($$1 != null) {
                $$1.setValue(0);
            }
            return Blocks.AIR.defaultBlockState();
        }
        int $$2 = SectionPos.blockToSectionCoord(BlockPos.getX($$0));
        BlockGetter $$4 = this.getChunk($$2, $$3 = SectionPos.blockToSectionCoord(BlockPos.getZ($$0)));
        if ($$4 == null) {
            if ($$1 != null) {
                $$1.setValue(16);
            }
            return Blocks.BEDROCK.defaultBlockState();
        }
        this.pos.set($$0);
        BlockState $$5 = $$4.getBlockState(this.pos);
        boolean bl = $$6 = $$5.canOcclude() && $$5.useShapeForLightOcclusion();
        if ($$1 != null) {
            $$1.setValue($$5.getLightBlock(this.chunkSource.getLevel(), this.pos));
        }
        return $$6 ? $$5 : Blocks.AIR.defaultBlockState();
    }

    protected VoxelShape getShape(BlockState $$0, long $$1, Direction $$2) {
        return $$0.canOcclude() ? $$0.getFaceOcclusionShape(this.chunkSource.getLevel(), this.pos.set($$1), $$2) : Shapes.empty();
    }

    public static int getLightBlockInto(BlockGetter $$0, BlockState $$1, BlockPos $$2, BlockState $$3, BlockPos $$4, Direction $$5, int $$6) {
        VoxelShape $$10;
        boolean $$8;
        boolean $$7 = $$1.canOcclude() && $$1.useShapeForLightOcclusion();
        boolean bl = $$8 = $$3.canOcclude() && $$3.useShapeForLightOcclusion();
        if (!$$7 && !$$8) {
            return $$6;
        }
        VoxelShape $$9 = $$7 ? $$1.getOcclusionShape($$0, $$2) : Shapes.empty();
        VoxelShape voxelShape = $$10 = $$8 ? $$3.getOcclusionShape($$0, $$4) : Shapes.empty();
        if (Shapes.mergedFaceOccludes($$9, $$10, $$5)) {
            return 16;
        }
        return $$6;
    }

    @Override
    protected boolean isSource(long $$0) {
        return $$0 == Long.MAX_VALUE;
    }

    @Override
    protected int getComputedLevel(long $$0, long $$1, int $$2) {
        return 0;
    }

    @Override
    protected int getLevel(long $$0) {
        if ($$0 == Long.MAX_VALUE) {
            return 0;
        }
        return 15 - ((LayerLightSectionStorage)this.storage).getStoredLevel($$0);
    }

    protected int getLevel(DataLayer $$0, long $$1) {
        return 15 - $$0.get(SectionPos.sectionRelative(BlockPos.getX($$1)), SectionPos.sectionRelative(BlockPos.getY($$1)), SectionPos.sectionRelative(BlockPos.getZ($$1)));
    }

    @Override
    protected void setLevel(long $$0, int $$1) {
        ((LayerLightSectionStorage)this.storage).setStoredLevel($$0, Math.min((int)15, (int)(15 - $$1)));
    }

    @Override
    protected int computeLevelFromNeighbor(long $$0, long $$1, int $$2) {
        return 0;
    }

    @Override
    public boolean hasLightWork() {
        return this.hasWork() || ((DynamicGraphMinFixedPoint)this.storage).hasWork() || ((LayerLightSectionStorage)this.storage).hasInconsistencies();
    }

    @Override
    public int runUpdates(int $$0, boolean $$1, boolean $$2) {
        if (!this.runningLightUpdates) {
            if (((DynamicGraphMinFixedPoint)this.storage).hasWork() && ($$0 = ((DynamicGraphMinFixedPoint)this.storage).runUpdates($$0)) == 0) {
                return $$0;
            }
            ((LayerLightSectionStorage)this.storage).markNewInconsistencies(this, $$1, $$2);
        }
        this.runningLightUpdates = true;
        if (this.hasWork()) {
            $$0 = this.runUpdates($$0);
            this.clearCache();
            if ($$0 == 0) {
                return $$0;
            }
        }
        this.runningLightUpdates = false;
        ((LayerLightSectionStorage)this.storage).swapSectionMap();
        return $$0;
    }

    protected void queueSectionData(long $$0, @Nullable DataLayer $$1, boolean $$2) {
        ((LayerLightSectionStorage)this.storage).queueSectionData($$0, $$1, $$2);
    }

    @Override
    @Nullable
    public DataLayer getDataLayerData(SectionPos $$0) {
        return ((LayerLightSectionStorage)this.storage).getDataLayerData($$0.asLong());
    }

    @Override
    public int getLightValue(BlockPos $$0) {
        return ((LayerLightSectionStorage)this.storage).getLightValue($$0.asLong());
    }

    public String getDebugData(long $$0) {
        return "" + ((LayerLightSectionStorage)this.storage).getLevel($$0);
    }

    @Override
    public void checkBlock(BlockPos $$0) {
        long $$1 = $$0.asLong();
        this.checkNode($$1);
        for (Direction $$2 : DIRECTIONS) {
            this.checkNode(BlockPos.offset($$1, $$2));
        }
    }

    @Override
    public void onBlockEmissionIncrease(BlockPos $$0, int $$1) {
    }

    @Override
    public void updateSectionStatus(SectionPos $$0, boolean $$1) {
        ((LayerLightSectionStorage)this.storage).updateSectionStatus($$0.asLong(), $$1);
    }

    @Override
    public void enableLightSources(ChunkPos $$0, boolean $$1) {
        long $$2 = SectionPos.getZeroNode(SectionPos.asLong($$0.x, 0, $$0.z));
        ((LayerLightSectionStorage)this.storage).enableLightSources($$2, $$1);
    }

    public void retainData(ChunkPos $$0, boolean $$1) {
        long $$2 = SectionPos.getZeroNode(SectionPos.asLong($$0.x, 0, $$0.z));
        ((LayerLightSectionStorage)this.storage).retainData($$2, $$1);
    }
}