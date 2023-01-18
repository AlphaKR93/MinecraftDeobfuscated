/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 */
package net.minecraft.world.level.block.entity;

import net.minecraft.util.Mth;

public class ChestLidController {
    private boolean shouldBeOpen;
    private float openness;
    private float oOpenness;

    public void tickLid() {
        this.oOpenness = this.openness;
        float $$0 = 0.1f;
        if (!this.shouldBeOpen && this.openness > 0.0f) {
            this.openness = Math.max((float)(this.openness - 0.1f), (float)0.0f);
        } else if (this.shouldBeOpen && this.openness < 1.0f) {
            this.openness = Math.min((float)(this.openness + 0.1f), (float)1.0f);
        }
    }

    public float getOpenness(float $$0) {
        return Mth.lerp($$0, this.oOpenness, this.openness);
    }

    public void shouldBeOpen(boolean $$0) {
        this.shouldBeOpen = $$0;
    }
}