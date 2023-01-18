/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.level.lighting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;

public interface LightEventListener {
    public void checkBlock(BlockPos var1);

    public void onBlockEmissionIncrease(BlockPos var1, int var2);

    public boolean hasLightWork();

    public int runUpdates(int var1, boolean var2, boolean var3);

    default public void updateSectionStatus(BlockPos $$0, boolean $$1) {
        this.updateSectionStatus(SectionPos.of($$0), $$1);
    }

    public void updateSectionStatus(SectionPos var1, boolean var2);

    public void enableLightSources(ChunkPos var1, boolean var2);
}