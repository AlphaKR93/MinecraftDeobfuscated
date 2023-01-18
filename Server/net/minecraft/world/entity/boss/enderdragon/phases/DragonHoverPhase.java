/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.phys.Vec3;

public class DragonHoverPhase
extends AbstractDragonPhaseInstance {
    @Nullable
    private Vec3 targetLocation;

    public DragonHoverPhase(EnderDragon $$0) {
        super($$0);
    }

    @Override
    public void doServerTick() {
        if (this.targetLocation == null) {
            this.targetLocation = this.dragon.position();
        }
    }

    @Override
    public boolean isSitting() {
        return true;
    }

    @Override
    public void begin() {
        this.targetLocation = null;
    }

    @Override
    public float getFlySpeed() {
        return 1.0f;
    }

    @Override
    @Nullable
    public Vec3 getFlyTargetLocation() {
        return this.targetLocation;
    }

    public EnderDragonPhase<DragonHoverPhase> getPhase() {
        return EnderDragonPhase.HOVERING;
    }
}