/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;

public interface LevelHeightAccessor {
    public int getHeight();

    public int getMinBuildHeight();

    default public int getMaxBuildHeight() {
        return this.getMinBuildHeight() + this.getHeight();
    }

    default public int getSectionsCount() {
        return this.getMaxSection() - this.getMinSection();
    }

    default public int getMinSection() {
        return SectionPos.blockToSectionCoord(this.getMinBuildHeight());
    }

    default public int getMaxSection() {
        return SectionPos.blockToSectionCoord(this.getMaxBuildHeight() - 1) + 1;
    }

    default public boolean isOutsideBuildHeight(BlockPos $$0) {
        return this.isOutsideBuildHeight($$0.getY());
    }

    default public boolean isOutsideBuildHeight(int $$0) {
        return $$0 < this.getMinBuildHeight() || $$0 >= this.getMaxBuildHeight();
    }

    default public int getSectionIndex(int $$0) {
        return this.getSectionIndexFromSectionY(SectionPos.blockToSectionCoord($$0));
    }

    default public int getSectionIndexFromSectionY(int $$0) {
        return $$0 - this.getMinSection();
    }

    default public int getSectionYFromSectionIndex(int $$0) {
        return $$0 + this.getMinSection();
    }

    public static LevelHeightAccessor create(final int $$0, final int $$1) {
        return new LevelHeightAccessor(){

            @Override
            public int getHeight() {
                return $$1;
            }

            @Override
            public int getMinBuildHeight() {
                return $$0;
            }
        };
    }
}