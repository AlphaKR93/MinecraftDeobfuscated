/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.lighting;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.lighting.LightEventListener;

public interface LayerLightEventListener
extends LightEventListener {
    @Nullable
    public DataLayer getDataLayerData(SectionPos var1);

    public int getLightValue(BlockPos var1);

    public static enum DummyLightLayerEventListener implements LayerLightEventListener
    {
        INSTANCE;


        @Override
        @Nullable
        public DataLayer getDataLayerData(SectionPos $$0) {
            return null;
        }

        @Override
        public int getLightValue(BlockPos $$0) {
            return 0;
        }

        @Override
        public void checkBlock(BlockPos $$0) {
        }

        @Override
        public void onBlockEmissionIncrease(BlockPos $$0, int $$1) {
        }

        @Override
        public boolean hasLightWork() {
            return false;
        }

        @Override
        public int runUpdates(int $$0, boolean $$1, boolean $$2) {
            return $$0;
        }

        @Override
        public void updateSectionStatus(SectionPos $$0, boolean $$1) {
        }

        @Override
        public void enableLightSources(ChunkPos $$0, boolean $$1) {
        }
    }
}