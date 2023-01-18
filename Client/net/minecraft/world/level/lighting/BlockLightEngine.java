/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Integer
 *  java.lang.Long
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.world.level.lighting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.BlockLightSectionStorage;
import net.minecraft.world.level.lighting.LayerLightEngine;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableInt;

public final class BlockLightEngine
extends LayerLightEngine<BlockLightSectionStorage.BlockDataLayerStorageMap, BlockLightSectionStorage> {
    private static final Direction[] DIRECTIONS = Direction.values();
    private final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

    public BlockLightEngine(LightChunkGetter $$0) {
        super($$0, LightLayer.BLOCK, new BlockLightSectionStorage($$0));
    }

    private int getLightEmission(long $$0) {
        int $$1 = BlockPos.getX($$0);
        int $$2 = BlockPos.getY($$0);
        int $$3 = BlockPos.getZ($$0);
        BlockGetter $$4 = this.chunkSource.getChunkForLighting(SectionPos.blockToSectionCoord($$1), SectionPos.blockToSectionCoord($$3));
        if ($$4 != null) {
            return $$4.getLightEmission(this.pos.set($$1, $$2, $$3));
        }
        return 0;
    }

    @Override
    protected int computeLevelFromNeighbor(long $$0, long $$1, int $$2) {
        VoxelShape $$11;
        int $$5;
        int $$4;
        if ($$1 == Long.MAX_VALUE) {
            return 15;
        }
        if ($$0 == Long.MAX_VALUE) {
            return $$2 + 15 - this.getLightEmission($$1);
        }
        if ($$2 >= 15) {
            return $$2;
        }
        int $$3 = Integer.signum((int)(BlockPos.getX($$1) - BlockPos.getX($$0)));
        Direction $$6 = Direction.fromNormal($$3, $$4 = Integer.signum((int)(BlockPos.getY($$1) - BlockPos.getY($$0))), $$5 = Integer.signum((int)(BlockPos.getZ($$1) - BlockPos.getZ($$0))));
        if ($$6 == null) {
            return 15;
        }
        MutableInt $$7 = new MutableInt();
        BlockState $$8 = this.getStateAndOpacity($$1, $$7);
        if ($$7.getValue() >= 15) {
            return 15;
        }
        BlockState $$9 = this.getStateAndOpacity($$0, null);
        VoxelShape $$10 = this.getShape($$9, $$0, $$6);
        if (Shapes.faceShapeOccludes($$10, $$11 = this.getShape($$8, $$1, $$6.getOpposite()))) {
            return 15;
        }
        return $$2 + Math.max((int)1, (int)$$7.getValue());
    }

    @Override
    protected void checkNeighborsAfterUpdate(long $$0, int $$1, boolean $$2) {
        long $$3 = SectionPos.blockToSection($$0);
        for (Direction $$4 : DIRECTIONS) {
            long $$5 = BlockPos.offset($$0, $$4);
            long $$6 = SectionPos.blockToSection($$5);
            if ($$3 != $$6 && !((BlockLightSectionStorage)this.storage).storingLightForSection($$6)) continue;
            this.checkNeighbor($$0, $$5, $$1, $$2);
        }
    }

    @Override
    protected int getComputedLevel(long $$0, long $$1, int $$2) {
        int $$3 = $$2;
        if (Long.MAX_VALUE != $$1) {
            int $$4 = this.computeLevelFromNeighbor(Long.MAX_VALUE, $$0, 0);
            if ($$3 > $$4) {
                $$3 = $$4;
            }
            if ($$3 == 0) {
                return $$3;
            }
        }
        long $$5 = SectionPos.blockToSection($$0);
        DataLayer $$6 = ((BlockLightSectionStorage)this.storage).getDataLayer($$5, true);
        for (Direction $$7 : DIRECTIONS) {
            DataLayer $$11;
            long $$8 = BlockPos.offset($$0, $$7);
            if ($$8 == $$1) continue;
            long $$9 = SectionPos.blockToSection($$8);
            if ($$5 == $$9) {
                DataLayer $$10 = $$6;
            } else {
                $$11 = ((BlockLightSectionStorage)this.storage).getDataLayer($$9, true);
            }
            if ($$11 == null) continue;
            int $$12 = this.computeLevelFromNeighbor($$8, $$0, this.getLevel($$11, $$8));
            if ($$3 > $$12) {
                $$3 = $$12;
            }
            if ($$3 != 0) continue;
            return $$3;
        }
        return $$3;
    }

    @Override
    public void onBlockEmissionIncrease(BlockPos $$0, int $$1) {
        ((BlockLightSectionStorage)this.storage).runAllUpdates();
        this.checkEdge(Long.MAX_VALUE, $$0.asLong(), 15 - $$1, true);
    }
}