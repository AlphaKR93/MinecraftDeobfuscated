/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.world.damagesource;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;

public class PointDamageSource
extends DamageSource {
    private final Vec3 damageSourcePosition;

    public PointDamageSource(String $$0, Vec3 $$1) {
        super($$0);
        this.damageSourcePosition = $$1;
    }

    @Override
    public Vec3 getSourcePosition() {
        return this.damageSourcePosition;
    }
}