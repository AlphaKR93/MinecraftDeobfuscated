/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Long
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Locale
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.world.level.lighting;

import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LayerLightEngine;
import net.minecraft.world.level.lighting.SkyLightSectionStorage;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.mutable.MutableInt;

public final class SkyLightEngine
extends LayerLightEngine<SkyLightSectionStorage.SkyDataLayerStorageMap, SkyLightSectionStorage> {
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};

    public SkyLightEngine(LightChunkGetter $$0) {
        super($$0, LightLayer.SKY, new SkyLightSectionStorage($$0));
    }

    @Override
    protected int computeLevelFromNeighbor(long $$0, long $$1, int $$2) {
        boolean $$19;
        VoxelShape $$17;
        int $$13;
        int $$12;
        if ($$1 == Long.MAX_VALUE || $$0 == Long.MAX_VALUE) {
            return 15;
        }
        if ($$2 >= 15) {
            return $$2;
        }
        MutableInt $$3 = new MutableInt();
        BlockState $$4 = this.getStateAndOpacity($$1, $$3);
        if ($$3.getValue() >= 15) {
            return 15;
        }
        int $$5 = BlockPos.getX($$0);
        int $$6 = BlockPos.getY($$0);
        int $$7 = BlockPos.getZ($$0);
        int $$8 = BlockPos.getX($$1);
        int $$9 = BlockPos.getY($$1);
        int $$10 = BlockPos.getZ($$1);
        int $$11 = Integer.signum((int)($$8 - $$5));
        Direction $$14 = Direction.fromNormal($$11, $$12 = Integer.signum((int)($$9 - $$6)), $$13 = Integer.signum((int)($$10 - $$7)));
        if ($$14 == null) {
            throw new IllegalStateException(String.format((Locale)Locale.ROOT, (String)"Light was spread in illegal direction %d, %d, %d", (Object[])new Object[]{$$11, $$12, $$13}));
        }
        BlockState $$15 = this.getStateAndOpacity($$0, null);
        VoxelShape $$16 = this.getShape($$15, $$0, $$14);
        if (Shapes.faceShapeOccludes($$16, $$17 = this.getShape($$4, $$1, $$14.getOpposite()))) {
            return 15;
        }
        boolean $$18 = $$5 == $$8 && $$7 == $$10;
        boolean bl = $$19 = $$18 && $$6 > $$9;
        if ($$19 && $$2 == 0 && $$3.getValue() == 0) {
            return 0;
        }
        return $$2 + Math.max((int)1, (int)$$3.getValue());
    }

    @Override
    protected void checkNeighborsAfterUpdate(long $$0, int $$1, boolean $$2) {
        long $$12;
        long $$13;
        int $$9;
        long $$3 = SectionPos.blockToSection($$0);
        int $$4 = BlockPos.getY($$0);
        int $$5 = SectionPos.sectionRelative($$4);
        int $$6 = SectionPos.blockToSectionCoord($$4);
        if ($$5 != 0) {
            boolean $$7 = false;
        } else {
            int $$8 = 0;
            while (!((SkyLightSectionStorage)this.storage).storingLightForSection(SectionPos.offset($$3, 0, -$$8 - 1, 0)) && ((SkyLightSectionStorage)this.storage).hasSectionsBelow($$6 - $$8 - 1)) {
                ++$$8;
            }
            $$9 = $$8;
        }
        long $$10 = BlockPos.offset($$0, 0, -1 - $$9 * 16, 0);
        long $$11 = SectionPos.blockToSection($$10);
        if ($$3 == $$11 || ((SkyLightSectionStorage)this.storage).storingLightForSection($$11)) {
            this.checkNeighbor($$0, $$10, $$1, $$2);
        }
        if ($$3 == ($$13 = SectionPos.blockToSection($$12 = BlockPos.offset($$0, Direction.UP))) || ((SkyLightSectionStorage)this.storage).storingLightForSection($$13)) {
            this.checkNeighbor($$0, $$12, $$1, $$2);
        }
        block1: for (Direction $$14 : HORIZONTALS) {
            int $$15 = 0;
            do {
                long $$16;
                long $$17;
                if ($$3 == ($$17 = SectionPos.blockToSection($$16 = BlockPos.offset($$0, $$14.getStepX(), -$$15, $$14.getStepZ())))) {
                    this.checkNeighbor($$0, $$16, $$1, $$2);
                    continue block1;
                }
                if (!((SkyLightSectionStorage)this.storage).storingLightForSection($$17)) continue;
                long $$18 = BlockPos.offset($$0, 0, -$$15, 0);
                this.checkNeighbor($$18, $$16, $$1, $$2);
            } while (++$$15 <= $$9 * 16);
        }
    }

    @Override
    protected int getComputedLevel(long $$0, long $$1, int $$2) {
        int $$3 = $$2;
        long $$4 = SectionPos.blockToSection($$0);
        DataLayer $$5 = ((SkyLightSectionStorage)this.storage).getDataLayer($$4, true);
        for (Direction $$6 : DIRECTIONS) {
            int $$12;
            DataLayer $$10;
            long $$7 = BlockPos.offset($$0, $$6);
            if ($$7 == $$1) continue;
            long $$8 = SectionPos.blockToSection($$7);
            if ($$4 == $$8) {
                DataLayer $$9 = $$5;
            } else {
                $$10 = ((SkyLightSectionStorage)this.storage).getDataLayer($$8, true);
            }
            if ($$10 != null) {
                int $$11 = this.getLevel($$10, $$7);
            } else {
                if ($$6 == Direction.DOWN) continue;
                $$12 = 15 - ((SkyLightSectionStorage)this.storage).getLightValue($$7, true);
            }
            int $$13 = this.computeLevelFromNeighbor($$7, $$0, $$12);
            if ($$3 > $$13) {
                $$3 = $$13;
            }
            if ($$3 != 0) continue;
            return $$3;
        }
        return $$3;
    }

    @Override
    protected void checkNode(long $$0) {
        ((SkyLightSectionStorage)this.storage).runAllUpdates();
        long $$1 = SectionPos.blockToSection($$0);
        if (((SkyLightSectionStorage)this.storage).storingLightForSection($$1)) {
            super.checkNode($$0);
        } else {
            $$0 = BlockPos.getFlatIndex($$0);
            while (!((SkyLightSectionStorage)this.storage).storingLightForSection($$1) && !((SkyLightSectionStorage)this.storage).isAboveData($$1)) {
                $$1 = SectionPos.offset($$1, Direction.UP);
                $$0 = BlockPos.offset($$0, 0, 16, 0);
            }
            if (((SkyLightSectionStorage)this.storage).storingLightForSection($$1)) {
                super.checkNode($$0);
            }
        }
    }

    @Override
    public String getDebugData(long $$0) {
        return super.getDebugData($$0) + (((SkyLightSectionStorage)this.storage).isAboveData($$0) ? "*" : "");
    }
}