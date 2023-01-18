/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.level.portal;

import net.minecraft.world.phys.Vec3;

public class PortalInfo {
    public final Vec3 pos;
    public final Vec3 speed;
    public final float yRot;
    public final float xRot;

    public PortalInfo(Vec3 $$0, Vec3 $$1, float $$2, float $$3) {
        this.pos = $$0;
        this.speed = $$1;
        this.yRot = $$2;
        this.xRot = $$3;
    }
}