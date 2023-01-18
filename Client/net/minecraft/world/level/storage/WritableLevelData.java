/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.level.storage;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.LevelData;

public interface WritableLevelData
extends LevelData {
    public void setXSpawn(int var1);

    public void setYSpawn(int var1);

    public void setZSpawn(int var1);

    public void setSpawnAngle(float var1);

    default public void setSpawn(BlockPos $$0, float $$1) {
        this.setXSpawn($$0.getX());
        this.setYSpawn($$0.getY());
        this.setZSpawn($$0.getZ());
        this.setSpawnAngle($$1);
    }
}