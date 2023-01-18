/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.lighting;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.BlockLightEngine;
import net.minecraft.world.level.lighting.LayerLightEngine;
import net.minecraft.world.level.lighting.LayerLightEventListener;
import net.minecraft.world.level.lighting.LightEventListener;
import net.minecraft.world.level.lighting.SkyLightEngine;

public class LevelLightEngine
implements LightEventListener {
    public static final int MAX_SOURCE_LEVEL = 15;
    public static final int LIGHT_SECTION_PADDING = 1;
    protected final LevelHeightAccessor levelHeightAccessor;
    @Nullable
    private final LayerLightEngine<?, ?> blockEngine;
    @Nullable
    private final LayerLightEngine<?, ?> skyEngine;

    public LevelLightEngine(LightChunkGetter $$0, boolean $$1, boolean $$2) {
        this.levelHeightAccessor = $$0.getLevel();
        this.blockEngine = $$1 ? new BlockLightEngine($$0) : null;
        this.skyEngine = $$2 ? new SkyLightEngine($$0) : null;
    }

    @Override
    public void checkBlock(BlockPos $$0) {
        if (this.blockEngine != null) {
            this.blockEngine.checkBlock($$0);
        }
        if (this.skyEngine != null) {
            this.skyEngine.checkBlock($$0);
        }
    }

    @Override
    public void onBlockEmissionIncrease(BlockPos $$0, int $$1) {
        if (this.blockEngine != null) {
            this.blockEngine.onBlockEmissionIncrease($$0, $$1);
        }
    }

    @Override
    public boolean hasLightWork() {
        if (this.skyEngine != null && this.skyEngine.hasLightWork()) {
            return true;
        }
        return this.blockEngine != null && this.blockEngine.hasLightWork();
    }

    @Override
    public int runUpdates(int $$0, boolean $$1, boolean $$2) {
        if (this.blockEngine != null && this.skyEngine != null) {
            int $$3 = $$0 / 2;
            int $$4 = this.blockEngine.runUpdates($$3, $$1, $$2);
            int $$5 = $$0 - $$3 + $$4;
            int $$6 = this.skyEngine.runUpdates($$5, $$1, $$2);
            if ($$4 == 0 && $$6 > 0) {
                return this.blockEngine.runUpdates($$6, $$1, $$2);
            }
            return $$6;
        }
        if (this.blockEngine != null) {
            return this.blockEngine.runUpdates($$0, $$1, $$2);
        }
        if (this.skyEngine != null) {
            return this.skyEngine.runUpdates($$0, $$1, $$2);
        }
        return $$0;
    }

    @Override
    public void updateSectionStatus(SectionPos $$0, boolean $$1) {
        if (this.blockEngine != null) {
            this.blockEngine.updateSectionStatus($$0, $$1);
        }
        if (this.skyEngine != null) {
            this.skyEngine.updateSectionStatus($$0, $$1);
        }
    }

    @Override
    public void enableLightSources(ChunkPos $$0, boolean $$1) {
        if (this.blockEngine != null) {
            this.blockEngine.enableLightSources($$0, $$1);
        }
        if (this.skyEngine != null) {
            this.skyEngine.enableLightSources($$0, $$1);
        }
    }

    public LayerLightEventListener getLayerListener(LightLayer $$0) {
        if ($$0 == LightLayer.BLOCK) {
            if (this.blockEngine == null) {
                return LayerLightEventListener.DummyLightLayerEventListener.INSTANCE;
            }
            return this.blockEngine;
        }
        if (this.skyEngine == null) {
            return LayerLightEventListener.DummyLightLayerEventListener.INSTANCE;
        }
        return this.skyEngine;
    }

    public String getDebugData(LightLayer $$0, SectionPos $$1) {
        if ($$0 == LightLayer.BLOCK) {
            if (this.blockEngine != null) {
                return this.blockEngine.getDebugData($$1.asLong());
            }
        } else if (this.skyEngine != null) {
            return this.skyEngine.getDebugData($$1.asLong());
        }
        return "n/a";
    }

    public void queueSectionData(LightLayer $$0, SectionPos $$1, @Nullable DataLayer $$2, boolean $$3) {
        if ($$0 == LightLayer.BLOCK) {
            if (this.blockEngine != null) {
                this.blockEngine.queueSectionData($$1.asLong(), $$2, $$3);
            }
        } else if (this.skyEngine != null) {
            this.skyEngine.queueSectionData($$1.asLong(), $$2, $$3);
        }
    }

    public void retainData(ChunkPos $$0, boolean $$1) {
        if (this.blockEngine != null) {
            this.blockEngine.retainData($$0, $$1);
        }
        if (this.skyEngine != null) {
            this.skyEngine.retainData($$0, $$1);
        }
    }

    public int getRawBrightness(BlockPos $$0, int $$1) {
        int $$2 = this.skyEngine == null ? 0 : this.skyEngine.getLightValue($$0) - $$1;
        int $$3 = this.blockEngine == null ? 0 : this.blockEngine.getLightValue($$0);
        return Math.max((int)$$3, (int)$$2);
    }

    public int getLightSectionCount() {
        return this.levelHeightAccessor.getSectionsCount() + 2;
    }

    public int getMinLightSection() {
        return this.levelHeightAccessor.getMinSection() - 1;
    }

    public int getMaxLightSection() {
        return this.getMinLightSection() + this.getLightSectionCount();
    }
}