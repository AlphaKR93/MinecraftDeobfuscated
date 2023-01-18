/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.level;

import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.dimension.DimensionType;

public interface LevelTimeAccess
extends LevelReader {
    public long dayTime();

    default public float getMoonBrightness() {
        return DimensionType.MOON_BRIGHTNESS_PER_PHASE[this.dimensionType().moonPhase(this.dayTime())];
    }

    default public float getTimeOfDay(float $$0) {
        return this.dimensionType().timeOfDay(this.dayTime());
    }

    default public int getMoonPhase() {
        return this.dimensionType().moonPhase(this.dayTime());
    }
}