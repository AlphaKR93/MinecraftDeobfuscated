/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class TrackingEmitter
extends NoRenderParticle {
    private final Entity entity;
    private int life;
    private final int lifeTime;
    private final ParticleOptions particleType;

    public TrackingEmitter(ClientLevel $$0, Entity $$1, ParticleOptions $$2) {
        this($$0, $$1, $$2, 3);
    }

    public TrackingEmitter(ClientLevel $$0, Entity $$1, ParticleOptions $$2, int $$3) {
        this($$0, $$1, $$2, $$3, $$1.getDeltaMovement());
    }

    private TrackingEmitter(ClientLevel $$0, Entity $$1, ParticleOptions $$2, int $$3, Vec3 $$4) {
        super($$0, $$1.getX(), $$1.getY(0.5), $$1.getZ(), $$4.x, $$4.y, $$4.z);
        this.entity = $$1;
        this.lifeTime = $$3;
        this.particleType = $$2;
        this.tick();
    }

    @Override
    public void tick() {
        for (int $$0 = 0; $$0 < 16; ++$$0) {
            double $$3;
            double $$2;
            double $$1 = this.random.nextFloat() * 2.0f - 1.0f;
            if ($$1 * $$1 + ($$2 = (double)(this.random.nextFloat() * 2.0f - 1.0f)) * $$2 + ($$3 = (double)(this.random.nextFloat() * 2.0f - 1.0f)) * $$3 > 1.0) continue;
            double $$4 = this.entity.getX($$1 / 4.0);
            double $$5 = this.entity.getY(0.5 + $$2 / 4.0);
            double $$6 = this.entity.getZ($$3 / 4.0);
            this.level.addParticle(this.particleType, false, $$4, $$5, $$6, $$1, $$2 + 0.2, $$3);
        }
        ++this.life;
        if (this.life >= this.lifeTime) {
            this.remove();
        }
    }
}