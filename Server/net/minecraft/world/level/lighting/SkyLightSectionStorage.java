/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  java.lang.Integer
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.System
 *  java.util.Arrays
 */
package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.DataLayerStorageMap;
import net.minecraft.world.level.lighting.LayerLightEngine;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;

public class SkyLightSectionStorage
extends LayerLightSectionStorage<SkyDataLayerStorageMap> {
    private static final Direction[] HORIZONTALS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
    private final LongSet sectionsWithSources = new LongOpenHashSet();
    private final LongSet sectionsToAddSourcesTo = new LongOpenHashSet();
    private final LongSet sectionsToRemoveSourcesFrom = new LongOpenHashSet();
    private final LongSet columnsWithSkySources = new LongOpenHashSet();
    private volatile boolean hasSourceInconsistencies;

    protected SkyLightSectionStorage(LightChunkGetter $$0) {
        super(LightLayer.SKY, $$0, new SkyDataLayerStorageMap((Long2ObjectOpenHashMap<DataLayer>)new Long2ObjectOpenHashMap(), new Long2IntOpenHashMap(), Integer.MAX_VALUE));
    }

    @Override
    protected int getLightValue(long $$0) {
        return this.getLightValue($$0, false);
    }

    protected int getLightValue(long $$0, boolean $$1) {
        long $$2 = SectionPos.blockToSection($$0);
        int $$3 = SectionPos.y($$2);
        SkyDataLayerStorageMap $$4 = $$1 ? (SkyDataLayerStorageMap)this.updatingSectionData : (SkyDataLayerStorageMap)this.visibleSectionData;
        int $$5 = $$4.topSections.get(SectionPos.getZeroNode($$2));
        if ($$5 == $$4.currentLowestY || $$3 >= $$5) {
            if ($$1 && !this.lightOnInSection($$2)) {
                return 0;
            }
            return 15;
        }
        DataLayer $$6 = this.getDataLayer($$4, $$2);
        if ($$6 == null) {
            $$0 = BlockPos.getFlatIndex($$0);
            while ($$6 == null) {
                if (++$$3 >= $$5) {
                    return 15;
                }
                $$0 = BlockPos.offset($$0, 0, 16, 0);
                $$2 = SectionPos.offset($$2, Direction.UP);
                $$6 = this.getDataLayer($$4, $$2);
            }
        }
        return $$6.get(SectionPos.sectionRelative(BlockPos.getX($$0)), SectionPos.sectionRelative(BlockPos.getY($$0)), SectionPos.sectionRelative(BlockPos.getZ($$0)));
    }

    @Override
    protected void onNodeAdded(long $$0) {
        long $$2;
        int $$3;
        int $$1 = SectionPos.y($$0);
        if (((SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY > $$1) {
            ((SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY = $$1;
            ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.defaultReturnValue(((SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY);
        }
        if (($$3 = ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.get($$2 = SectionPos.getZeroNode($$0))) < $$1 + 1) {
            ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.put($$2, $$1 + 1);
            if (this.columnsWithSkySources.contains($$2)) {
                this.queueAddSource($$0);
                if ($$3 > ((SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY) {
                    long $$4 = SectionPos.asLong(SectionPos.x($$0), $$3 - 1, SectionPos.z($$0));
                    this.queueRemoveSource($$4);
                }
                this.recheckInconsistencyFlag();
            }
        }
    }

    private void queueRemoveSource(long $$0) {
        this.sectionsToRemoveSourcesFrom.add($$0);
        this.sectionsToAddSourcesTo.remove($$0);
    }

    private void queueAddSource(long $$0) {
        this.sectionsToAddSourcesTo.add($$0);
        this.sectionsToRemoveSourcesFrom.remove($$0);
    }

    private void recheckInconsistencyFlag() {
        this.hasSourceInconsistencies = !this.sectionsToAddSourcesTo.isEmpty() || !this.sectionsToRemoveSourcesFrom.isEmpty();
    }

    @Override
    protected void onNodeRemoved(long $$0) {
        long $$1 = SectionPos.getZeroNode($$0);
        boolean $$2 = this.columnsWithSkySources.contains($$1);
        if ($$2) {
            this.queueRemoveSource($$0);
        }
        int $$3 = SectionPos.y($$0);
        if (((SkyDataLayerStorageMap)this.updatingSectionData).topSections.get($$1) == $$3 + 1) {
            long $$4 = $$0;
            while (!this.storingLightForSection($$4) && this.hasSectionsBelow($$3)) {
                --$$3;
                $$4 = SectionPos.offset($$4, Direction.DOWN);
            }
            if (this.storingLightForSection($$4)) {
                ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.put($$1, $$3 + 1);
                if ($$2) {
                    this.queueAddSource($$4);
                }
            } else {
                ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.remove($$1);
            }
        }
        if ($$2) {
            this.recheckInconsistencyFlag();
        }
    }

    @Override
    protected void enableLightSources(long $$0, boolean $$1) {
        this.runAllUpdates();
        if ($$1 && this.columnsWithSkySources.add($$0)) {
            int $$2 = ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.get($$0);
            if ($$2 != ((SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY) {
                long $$3 = SectionPos.asLong(SectionPos.x($$0), $$2 - 1, SectionPos.z($$0));
                this.queueAddSource($$3);
                this.recheckInconsistencyFlag();
            }
        } else if (!$$1) {
            this.columnsWithSkySources.remove($$0);
        }
    }

    @Override
    protected boolean hasInconsistencies() {
        return super.hasInconsistencies() || this.hasSourceInconsistencies;
    }

    @Override
    protected DataLayer createDataLayer(long $$0) {
        DataLayer $$4;
        DataLayer $$1 = (DataLayer)this.queuedSections.get($$0);
        if ($$1 != null) {
            return $$1;
        }
        long $$2 = SectionPos.offset($$0, Direction.UP);
        int $$3 = ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.get(SectionPos.getZeroNode($$0));
        if ($$3 == ((SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY || SectionPos.y($$2) >= $$3) {
            return new DataLayer();
        }
        while (($$4 = this.getDataLayer($$2, true)) == null) {
            $$2 = SectionPos.offset($$2, Direction.UP);
        }
        return SkyLightSectionStorage.repeatFirstLayer($$4);
    }

    private static DataLayer repeatFirstLayer(DataLayer $$0) {
        if ($$0.isEmpty()) {
            return new DataLayer();
        }
        byte[] $$1 = $$0.getData();
        byte[] $$2 = new byte[2048];
        for (int $$3 = 0; $$3 < 16; ++$$3) {
            System.arraycopy((Object)$$1, (int)0, (Object)$$2, (int)($$3 * 128), (int)128);
        }
        return new DataLayer($$2);
    }

    @Override
    protected void markNewInconsistencies(LayerLightEngine<SkyDataLayerStorageMap, ?> $$0, boolean $$1, boolean $$2) {
        LongIterator longIterator;
        super.markNewInconsistencies($$0, $$1, $$2);
        if (!$$1) {
            return;
        }
        if (!this.sectionsToAddSourcesTo.isEmpty()) {
            longIterator = this.sectionsToAddSourcesTo.iterator();
            while (longIterator.hasNext()) {
                long $$3 = (Long)longIterator.next();
                int $$4 = this.getLevel($$3);
                if ($$4 == 2 || this.sectionsToRemoveSourcesFrom.contains($$3) || !this.sectionsWithSources.add($$3)) continue;
                if ($$4 == 1) {
                    this.clearQueuedSectionBlocks($$0, $$3);
                    if (this.changedSections.add($$3)) {
                        ((SkyDataLayerStorageMap)this.updatingSectionData).copyDataLayer($$3);
                    }
                    Arrays.fill((byte[])this.getDataLayer($$3, true).getData(), (byte)-1);
                    int $$5 = SectionPos.sectionToBlockCoord(SectionPos.x($$3));
                    int $$6 = SectionPos.sectionToBlockCoord(SectionPos.y($$3));
                    int $$7 = SectionPos.sectionToBlockCoord(SectionPos.z($$3));
                    for (Direction $$8 : HORIZONTALS) {
                        long $$9 = SectionPos.offset($$3, $$8);
                        if (!this.sectionsToRemoveSourcesFrom.contains($$9) && (this.sectionsWithSources.contains($$9) || this.sectionsToAddSourcesTo.contains($$9)) || !this.storingLightForSection($$9)) continue;
                        for (int $$10 = 0; $$10 < 16; ++$$10) {
                            for (int $$11 = 0; $$11 < 16; ++$$11) {
                                long $$19;
                                long $$18;
                                switch ($$8) {
                                    case NORTH: {
                                        long $$12 = BlockPos.asLong($$5 + $$10, $$6 + $$11, $$7);
                                        long $$13 = BlockPos.asLong($$5 + $$10, $$6 + $$11, $$7 - 1);
                                        break;
                                    }
                                    case SOUTH: {
                                        long $$14 = BlockPos.asLong($$5 + $$10, $$6 + $$11, $$7 + 16 - 1);
                                        long $$15 = BlockPos.asLong($$5 + $$10, $$6 + $$11, $$7 + 16);
                                        break;
                                    }
                                    case WEST: {
                                        long $$16 = BlockPos.asLong($$5, $$6 + $$10, $$7 + $$11);
                                        long $$17 = BlockPos.asLong($$5 - 1, $$6 + $$10, $$7 + $$11);
                                        break;
                                    }
                                    default: {
                                        $$18 = BlockPos.asLong($$5 + 16 - 1, $$6 + $$10, $$7 + $$11);
                                        $$19 = BlockPos.asLong($$5 + 16, $$6 + $$10, $$7 + $$11);
                                    }
                                }
                                $$0.checkEdge($$18, $$19, $$0.computeLevelFromNeighbor($$18, $$19, 0), true);
                            }
                        }
                    }
                    for (int $$20 = 0; $$20 < 16; ++$$20) {
                        for (int $$21 = 0; $$21 < 16; ++$$21) {
                            long $$22 = BlockPos.asLong(SectionPos.sectionToBlockCoord(SectionPos.x($$3), $$20), SectionPos.sectionToBlockCoord(SectionPos.y($$3)), SectionPos.sectionToBlockCoord(SectionPos.z($$3), $$21));
                            long $$23 = BlockPos.asLong(SectionPos.sectionToBlockCoord(SectionPos.x($$3), $$20), SectionPos.sectionToBlockCoord(SectionPos.y($$3)) - 1, SectionPos.sectionToBlockCoord(SectionPos.z($$3), $$21));
                            $$0.checkEdge($$22, $$23, $$0.computeLevelFromNeighbor($$22, $$23, 0), true);
                        }
                    }
                    continue;
                }
                for (int $$24 = 0; $$24 < 16; ++$$24) {
                    for (int $$25 = 0; $$25 < 16; ++$$25) {
                        long $$26 = BlockPos.asLong(SectionPos.sectionToBlockCoord(SectionPos.x($$3), $$24), SectionPos.sectionToBlockCoord(SectionPos.y($$3), 15), SectionPos.sectionToBlockCoord(SectionPos.z($$3), $$25));
                        $$0.checkEdge(Long.MAX_VALUE, $$26, 0, true);
                    }
                }
            }
        }
        this.sectionsToAddSourcesTo.clear();
        if (!this.sectionsToRemoveSourcesFrom.isEmpty()) {
            longIterator = this.sectionsToRemoveSourcesFrom.iterator();
            while (longIterator.hasNext()) {
                long $$27 = (Long)longIterator.next();
                if (!this.sectionsWithSources.remove($$27) || !this.storingLightForSection($$27)) continue;
                for (int $$28 = 0; $$28 < 16; ++$$28) {
                    for (int $$29 = 0; $$29 < 16; ++$$29) {
                        long $$30 = BlockPos.asLong(SectionPos.sectionToBlockCoord(SectionPos.x($$27), $$28), SectionPos.sectionToBlockCoord(SectionPos.y($$27), 15), SectionPos.sectionToBlockCoord(SectionPos.z($$27), $$29));
                        $$0.checkEdge(Long.MAX_VALUE, $$30, 15, false);
                    }
                }
            }
        }
        this.sectionsToRemoveSourcesFrom.clear();
        this.hasSourceInconsistencies = false;
    }

    protected boolean hasSectionsBelow(int $$0) {
        return $$0 >= ((SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY;
    }

    protected boolean isAboveData(long $$0) {
        long $$1 = SectionPos.getZeroNode($$0);
        int $$2 = ((SkyDataLayerStorageMap)this.updatingSectionData).topSections.get($$1);
        return $$2 == ((SkyDataLayerStorageMap)this.updatingSectionData).currentLowestY || SectionPos.y($$0) >= $$2;
    }

    protected boolean lightOnInSection(long $$0) {
        long $$1 = SectionPos.getZeroNode($$0);
        return this.columnsWithSkySources.contains($$1);
    }

    protected static final class SkyDataLayerStorageMap
    extends DataLayerStorageMap<SkyDataLayerStorageMap> {
        int currentLowestY;
        final Long2IntOpenHashMap topSections;

        public SkyDataLayerStorageMap(Long2ObjectOpenHashMap<DataLayer> $$0, Long2IntOpenHashMap $$1, int $$2) {
            super($$0);
            this.topSections = $$1;
            $$1.defaultReturnValue($$2);
            this.currentLowestY = $$2;
        }

        @Override
        public SkyDataLayerStorageMap copy() {
            return new SkyDataLayerStorageMap((Long2ObjectOpenHashMap<DataLayer>)this.map.clone(), this.topSections.clone(), this.currentLowestY);
        }
    }
}