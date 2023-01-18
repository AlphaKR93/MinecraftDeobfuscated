/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.client.renderer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.biome.Biome;

public class BiomeColors {
    public static final ColorResolver GRASS_COLOR_RESOLVER = Biome::getGrassColor;
    public static final ColorResolver FOLIAGE_COLOR_RESOLVER = ($$0, $$1, $$2) -> $$0.getFoliageColor();
    public static final ColorResolver WATER_COLOR_RESOLVER = ($$0, $$1, $$2) -> $$0.getWaterColor();

    private static int getAverageColor(BlockAndTintGetter $$0, BlockPos $$1, ColorResolver $$2) {
        return $$0.getBlockTint($$1, $$2);
    }

    public static int getAverageGrassColor(BlockAndTintGetter $$0, BlockPos $$1) {
        return BiomeColors.getAverageColor($$0, $$1, GRASS_COLOR_RESOLVER);
    }

    public static int getAverageFoliageColor(BlockAndTintGetter $$0, BlockPos $$1) {
        return BiomeColors.getAverageColor($$0, $$1, FOLIAGE_COLOR_RESOLVER);
    }

    public static int getAverageWaterColor(BlockAndTintGetter $$0, BlockPos $$1) {
        return BiomeColors.getAverageColor($$0, $$1, WATER_COLOR_RESOLVER);
    }
}