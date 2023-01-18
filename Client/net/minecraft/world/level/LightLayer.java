/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.level;

public enum LightLayer {
    SKY(15),
    BLOCK(0);

    public final int surrounding;

    private LightLayer(int $$0) {
        this.surrounding = $$0;
    }
}