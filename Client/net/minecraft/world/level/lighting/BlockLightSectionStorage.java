/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.lighting;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.DataLayerStorageMap;
import net.minecraft.world.level.lighting.LayerLightSectionStorage;

public class BlockLightSectionStorage
extends LayerLightSectionStorage<BlockDataLayerStorageMap> {
    protected BlockLightSectionStorage(LightChunkGetter $$0) {
        super(LightLayer.BLOCK, $$0, new BlockDataLayerStorageMap((Long2ObjectOpenHashMap<DataLayer>)new Long2ObjectOpenHashMap()));
    }

    @Override
    protected int getLightValue(long $$0) {
        long $$1 = SectionPos.blockToSection($$0);
        DataLayer $$2 = this.getDataLayer($$1, false);
        if ($$2 == null) {
            return 0;
        }
        return $$2.get(SectionPos.sectionRelative(BlockPos.getX($$0)), SectionPos.sectionRelative(BlockPos.getY($$0)), SectionPos.sectionRelative(BlockPos.getZ($$0)));
    }

    protected static final class BlockDataLayerStorageMap
    extends DataLayerStorageMap<BlockDataLayerStorageMap> {
        public BlockDataLayerStorageMap(Long2ObjectOpenHashMap<DataLayer> $$0) {
            super($$0);
        }

        @Override
        public BlockDataLayerStorageMap copy() {
            return new BlockDataLayerStorageMap((Long2ObjectOpenHashMap<DataLayer>)this.map.clone());
        }
    }
}