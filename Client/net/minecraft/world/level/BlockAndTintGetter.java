/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.lighting.LevelLightEngine;

public interface BlockAndTintGetter
extends BlockGetter {
    public float getShade(Direction var1, boolean var2);

    public LevelLightEngine getLightEngine();

    public int getBlockTint(BlockPos var1, ColorResolver var2);

    default public int getBrightness(LightLayer $$0, BlockPos $$1) {
        return this.getLightEngine().getLayerListener($$0).getLightValue($$1);
    }

    default public int getRawBrightness(BlockPos $$0, int $$1) {
        return this.getLightEngine().getRawBrightness($$0, $$1);
    }

    default public boolean canSeeSky(BlockPos $$0) {
        return this.getBrightness(LightLayer.SKY, $$0) >= this.getMaxLightLevel();
    }
}