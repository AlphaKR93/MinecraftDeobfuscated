/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMaps
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.longs.LongIterator
 *  it.unimi.dsi.fastutil.longs.LongOpenHashSet
 *  it.unimi.dsi.fastutil.longs.LongSet
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  java.lang.Integer
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.SectionTracker;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.DataLayerStorageMap;
import net.minecraft.world.level.lighting.LayerLightEngine;

public abstract class LayerLightSectionStorage<M extends DataLayerStorageMap<M>>
extends SectionTracker {
    protected static final int LIGHT_AND_DATA = 0;
    protected static final int LIGHT_ONLY = 1;
    protected static final int EMPTY = 2;
    protected static final DataLayer EMPTY_DATA = new DataLayer();
    private static final Direction[] DIRECTIONS = Direction.values();
    private final LightLayer layer;
    private final LightChunkGetter chunkSource;
    protected final LongSet dataSectionSet = new LongOpenHashSet();
    protected final LongSet toMarkNoData = new LongOpenHashSet();
    protected final LongSet toMarkData = new LongOpenHashSet();
    protected volatile M visibleSectionData;
    protected final M updatingSectionData;
    protected final LongSet changedSections = new LongOpenHashSet();
    protected final LongSet sectionsAffectedByLightUpdates = new LongOpenHashSet();
    protected final Long2ObjectMap<DataLayer> queuedSections = Long2ObjectMaps.synchronize((Long2ObjectMap)new Long2ObjectOpenHashMap());
    private final LongSet untrustedSections = new LongOpenHashSet();
    private final LongSet columnsToRetainQueuedDataFor = new LongOpenHashSet();
    private final LongSet toRemove = new LongOpenHashSet();
    protected volatile boolean hasToRemove;

    protected LayerLightSectionStorage(LightLayer $$0, LightChunkGetter $$1, M $$2) {
        super(3, 16, 256);
        this.layer = $$0;
        this.chunkSource = $$1;
        this.updatingSectionData = $$2;
        this.visibleSectionData = ((DataLayerStorageMap)$$2).copy();
        ((DataLayerStorageMap)this.visibleSectionData).disableCache();
    }

    protected boolean storingLightForSection(long $$0) {
        return this.getDataLayer($$0, true) != null;
    }

    @Nullable
    protected DataLayer getDataLayer(long $$0, boolean $$1) {
        return this.getDataLayer($$1 ? this.updatingSectionData : this.visibleSectionData, $$0);
    }

    @Nullable
    protected DataLayer getDataLayer(M $$0, long $$1) {
        return ((DataLayerStorageMap)$$0).getLayer($$1);
    }

    @Nullable
    public DataLayer getDataLayerData(long $$0) {
        DataLayer $$1 = (DataLayer)this.queuedSections.get($$0);
        if ($$1 != null) {
            return $$1;
        }
        return this.getDataLayer($$0, false);
    }

    protected abstract int getLightValue(long var1);

    protected int getStoredLevel(long $$0) {
        long $$1 = SectionPos.blockToSection($$0);
        DataLayer $$2 = this.getDataLayer($$1, true);
        return $$2.get(SectionPos.sectionRelative(BlockPos.getX($$0)), SectionPos.sectionRelative(BlockPos.getY($$0)), SectionPos.sectionRelative(BlockPos.getZ($$0)));
    }

    protected void setStoredLevel(long $$0, int $$1) {
        long $$2 = SectionPos.blockToSection($$0);
        if (this.changedSections.add($$2)) {
            ((DataLayerStorageMap)this.updatingSectionData).copyDataLayer($$2);
        }
        DataLayer $$3 = this.getDataLayer($$2, true);
        $$3.set(SectionPos.sectionRelative(BlockPos.getX($$0)), SectionPos.sectionRelative(BlockPos.getY($$0)), SectionPos.sectionRelative(BlockPos.getZ($$0)), $$1);
        SectionPos.aroundAndAtBlockPos($$0, arg_0 -> ((LongSet)this.sectionsAffectedByLightUpdates).add(arg_0));
    }

    @Override
    protected int getLevel(long $$0) {
        if ($$0 == Long.MAX_VALUE) {
            return 2;
        }
        if (this.dataSectionSet.contains($$0)) {
            return 0;
        }
        if (!this.toRemove.contains($$0) && ((DataLayerStorageMap)this.updatingSectionData).hasLayer($$0)) {
            return 1;
        }
        return 2;
    }

    @Override
    protected int getLevelFromSource(long $$0) {
        if (this.toMarkNoData.contains($$0)) {
            return 2;
        }
        if (this.dataSectionSet.contains($$0) || this.toMarkData.contains($$0)) {
            return 0;
        }
        return 2;
    }

    @Override
    protected void setLevel(long $$0, int $$1) {
        int $$2 = this.getLevel($$0);
        if ($$2 != 0 && $$1 == 0) {
            this.dataSectionSet.add($$0);
            this.toMarkData.remove($$0);
        }
        if ($$2 == 0 && $$1 != 0) {
            this.dataSectionSet.remove($$0);
            this.toMarkNoData.remove($$0);
        }
        if ($$2 >= 2 && $$1 != 2) {
            if (this.toRemove.contains($$0)) {
                this.toRemove.remove($$0);
            } else {
                ((DataLayerStorageMap)this.updatingSectionData).setLayer($$0, this.createDataLayer($$0));
                this.changedSections.add($$0);
                this.onNodeAdded($$0);
                int $$3 = SectionPos.x($$0);
                int $$4 = SectionPos.y($$0);
                int $$5 = SectionPos.z($$0);
                for (int $$6 = -1; $$6 <= 1; ++$$6) {
                    for (int $$7 = -1; $$7 <= 1; ++$$7) {
                        for (int $$8 = -1; $$8 <= 1; ++$$8) {
                            this.sectionsAffectedByLightUpdates.add(SectionPos.asLong($$3 + $$7, $$4 + $$8, $$5 + $$6));
                        }
                    }
                }
            }
        }
        if ($$2 != 2 && $$1 >= 2) {
            this.toRemove.add($$0);
        }
        this.hasToRemove = !this.toRemove.isEmpty();
    }

    protected DataLayer createDataLayer(long $$0) {
        DataLayer $$1 = (DataLayer)this.queuedSections.get($$0);
        if ($$1 != null) {
            return $$1;
        }
        return new DataLayer();
    }

    protected void clearQueuedSectionBlocks(LayerLightEngine<?, ?> $$0, long $$12) {
        if ($$0.getQueueSize() == 0) {
            return;
        }
        if ($$0.getQueueSize() < 8192) {
            $$0.removeIf($$1 -> SectionPos.blockToSection($$1) == $$12);
            return;
        }
        int $$2 = SectionPos.sectionToBlockCoord(SectionPos.x($$12));
        int $$3 = SectionPos.sectionToBlockCoord(SectionPos.y($$12));
        int $$4 = SectionPos.sectionToBlockCoord(SectionPos.z($$12));
        for (int $$5 = 0; $$5 < 16; ++$$5) {
            for (int $$6 = 0; $$6 < 16; ++$$6) {
                for (int $$7 = 0; $$7 < 16; ++$$7) {
                    long $$8 = BlockPos.asLong($$2 + $$5, $$3 + $$6, $$4 + $$7);
                    $$0.removeFromQueue($$8);
                }
            }
        }
    }

    protected boolean hasInconsistencies() {
        return this.hasToRemove;
    }

    protected void markNewInconsistencies(LayerLightEngine<M, ?> $$0, boolean $$1, boolean $$2) {
        if (!this.hasInconsistencies() && this.queuedSections.isEmpty()) {
            return;
        }
        LongIterator longIterator = this.toRemove.iterator();
        while (longIterator.hasNext()) {
            long $$3 = (Long)longIterator.next();
            this.clearQueuedSectionBlocks($$0, $$3);
            DataLayer $$4 = (DataLayer)this.queuedSections.remove($$3);
            DataLayer $$5 = ((DataLayerStorageMap)this.updatingSectionData).removeLayer($$3);
            if (!this.columnsToRetainQueuedDataFor.contains(SectionPos.getZeroNode($$3))) continue;
            if ($$4 != null) {
                this.queuedSections.put($$3, (Object)$$4);
                continue;
            }
            if ($$5 == null) continue;
            this.queuedSections.put($$3, (Object)$$5);
        }
        ((DataLayerStorageMap)this.updatingSectionData).clearCache();
        longIterator = this.toRemove.iterator();
        while (longIterator.hasNext()) {
            long $$6 = (Long)longIterator.next();
            this.onNodeRemoved($$6);
        }
        this.toRemove.clear();
        this.hasToRemove = false;
        for (Long2ObjectMap.Entry $$7 : this.queuedSections.long2ObjectEntrySet()) {
            long $$8 = $$7.getLongKey();
            if (!this.storingLightForSection($$8)) continue;
            DataLayer $$9 = (DataLayer)$$7.getValue();
            if (((DataLayerStorageMap)this.updatingSectionData).getLayer($$8) == $$9) continue;
            this.clearQueuedSectionBlocks($$0, $$8);
            ((DataLayerStorageMap)this.updatingSectionData).setLayer($$8, $$9);
            this.changedSections.add($$8);
        }
        ((DataLayerStorageMap)this.updatingSectionData).clearCache();
        if (!$$2) {
            longIterator = this.queuedSections.keySet().iterator();
            while (longIterator.hasNext()) {
                long $$10 = (Long)longIterator.next();
                this.checkEdgesForSection($$0, $$10);
            }
        } else {
            longIterator = this.untrustedSections.iterator();
            while (longIterator.hasNext()) {
                long $$11 = (Long)longIterator.next();
                this.checkEdgesForSection($$0, $$11);
            }
        }
        this.untrustedSections.clear();
        ObjectIterator $$12 = this.queuedSections.long2ObjectEntrySet().iterator();
        while ($$12.hasNext()) {
            Long2ObjectMap.Entry $$13 = (Long2ObjectMap.Entry)$$12.next();
            long $$14 = $$13.getLongKey();
            if (!this.storingLightForSection($$14)) continue;
            $$12.remove();
        }
    }

    private void checkEdgesForSection(LayerLightEngine<M, ?> $$0, long $$1) {
        if (!this.storingLightForSection($$1)) {
            return;
        }
        int $$2 = SectionPos.sectionToBlockCoord(SectionPos.x($$1));
        int $$3 = SectionPos.sectionToBlockCoord(SectionPos.y($$1));
        int $$4 = SectionPos.sectionToBlockCoord(SectionPos.z($$1));
        for (Direction $$5 : DIRECTIONS) {
            long $$6 = SectionPos.offset($$1, $$5);
            if (this.queuedSections.containsKey($$6) || !this.storingLightForSection($$6)) continue;
            for (int $$7 = 0; $$7 < 16; ++$$7) {
                for (int $$8 = 0; $$8 < 16; ++$$8) {
                    long $$20;
                    long $$19;
                    switch ($$5) {
                        case DOWN: {
                            long $$9 = BlockPos.asLong($$2 + $$8, $$3, $$4 + $$7);
                            long $$10 = BlockPos.asLong($$2 + $$8, $$3 - 1, $$4 + $$7);
                            break;
                        }
                        case UP: {
                            long $$11 = BlockPos.asLong($$2 + $$8, $$3 + 16 - 1, $$4 + $$7);
                            long $$12 = BlockPos.asLong($$2 + $$8, $$3 + 16, $$4 + $$7);
                            break;
                        }
                        case NORTH: {
                            long $$13 = BlockPos.asLong($$2 + $$7, $$3 + $$8, $$4);
                            long $$14 = BlockPos.asLong($$2 + $$7, $$3 + $$8, $$4 - 1);
                            break;
                        }
                        case SOUTH: {
                            long $$15 = BlockPos.asLong($$2 + $$7, $$3 + $$8, $$4 + 16 - 1);
                            long $$16 = BlockPos.asLong($$2 + $$7, $$3 + $$8, $$4 + 16);
                            break;
                        }
                        case WEST: {
                            long $$17 = BlockPos.asLong($$2, $$3 + $$7, $$4 + $$8);
                            long $$18 = BlockPos.asLong($$2 - 1, $$3 + $$7, $$4 + $$8);
                            break;
                        }
                        default: {
                            $$19 = BlockPos.asLong($$2 + 16 - 1, $$3 + $$7, $$4 + $$8);
                            $$20 = BlockPos.asLong($$2 + 16, $$3 + $$7, $$4 + $$8);
                        }
                    }
                    $$0.checkEdge($$19, $$20, $$0.computeLevelFromNeighbor($$19, $$20, $$0.getLevel($$19)), false);
                    $$0.checkEdge($$20, $$19, $$0.computeLevelFromNeighbor($$20, $$19, $$0.getLevel($$20)), false);
                }
            }
        }
    }

    protected void onNodeAdded(long $$0) {
    }

    protected void onNodeRemoved(long $$0) {
    }

    protected void enableLightSources(long $$0, boolean $$1) {
    }

    public void retainData(long $$0, boolean $$1) {
        if ($$1) {
            this.columnsToRetainQueuedDataFor.add($$0);
        } else {
            this.columnsToRetainQueuedDataFor.remove($$0);
        }
    }

    protected void queueSectionData(long $$0, @Nullable DataLayer $$1, boolean $$2) {
        if ($$1 != null) {
            this.queuedSections.put($$0, (Object)$$1);
            if (!$$2) {
                this.untrustedSections.add($$0);
            }
        } else {
            this.queuedSections.remove($$0);
        }
    }

    protected void updateSectionStatus(long $$0, boolean $$1) {
        boolean $$2 = this.dataSectionSet.contains($$0);
        if (!$$2 && !$$1) {
            this.toMarkData.add($$0);
            this.checkEdge(Long.MAX_VALUE, $$0, 0, true);
        }
        if ($$2 && $$1) {
            this.toMarkNoData.add($$0);
            this.checkEdge(Long.MAX_VALUE, $$0, 2, false);
        }
    }

    protected void runAllUpdates() {
        if (this.hasWork()) {
            this.runUpdates(Integer.MAX_VALUE);
        }
    }

    protected void swapSectionMap() {
        if (!this.changedSections.isEmpty()) {
            Object $$0 = ((DataLayerStorageMap)this.updatingSectionData).copy();
            ((DataLayerStorageMap)$$0).disableCache();
            this.visibleSectionData = $$0;
            this.changedSections.clear();
        }
        if (!this.sectionsAffectedByLightUpdates.isEmpty()) {
            LongIterator $$1 = this.sectionsAffectedByLightUpdates.iterator();
            while ($$1.hasNext()) {
                long $$2 = $$1.nextLong();
                this.chunkSource.onLightUpdate(this.layer, SectionPos.of($$2));
            }
            this.sectionsAffectedByLightUpdates.clear();
        }
    }
}