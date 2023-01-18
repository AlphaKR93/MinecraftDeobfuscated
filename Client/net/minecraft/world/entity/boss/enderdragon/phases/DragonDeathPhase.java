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
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.AbstractDragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.phys.Vec3;

public class DragonDeathPhase
extends AbstractDragonPhaseInstance {
    @Nullable
    private Vec3 targetLocation;
    private int time;

    public DragonDeathPhase(EnderDragon $$0) {
        super($$0);
    }

    @Override
    public void doClientTick() {
        if (this.time++ % 10 == 0) {
            float $$0 = (this.dragon.getRandom().nextFloat() - 0.5f) * 8.0f;
            float $$1 = (this.dragon.getRandom().nextFloat() - 0.5f) * 4.0f;
            float $$2 = (this.dragon.getRandom().nextFloat() - 0.5f) * 8.0f;
            this.dragon.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.dragon.getX() + (double)$$0, this.dragon.getY() + 2.0 + (double)$$1, this.dragon.getZ() + (double)$$2, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void doServerTick() {
        double $$1;
        ++this.time;
        if (this.targetLocation == null) {
            BlockPos $$0 = this.dragon.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, EndPodiumFeature.END_PODIUM_LOCATION);
            this.targetLocation = Vec3.atBottomCenterOf($$0);
        }
        if (($$1 = this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ())) < 100.0 || $$1 > 22500.0 || this.dragon.horizontalCollision || this.dragon.verticalCollision) {
            this.dragon.setHealth(0.0f);
        } else {
            this.dragon.setHealth(1.0f);
        }
    }

    @Override
    public void begin() {
        this.targetLocation = null;
        this.time = 0;
    }

    @Override
    public float getFlySpeed() {
        return 3.0f;
    }

    @Override
    @Nullable
    public Vec3 getFlyTargetLocation() {
        return this.targetLocation;
    }

    public EnderDragonPhase<DragonDeathPhase> getPhase() {
        return EnderDragonPhase.DYING;
    }
}