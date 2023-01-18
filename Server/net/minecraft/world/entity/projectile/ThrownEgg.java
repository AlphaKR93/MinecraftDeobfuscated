/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.entity.projectile;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ThrownEgg
extends ThrowableItemProjectile {
    public ThrownEgg(EntityType<? extends ThrownEgg> $$0, Level $$1) {
        super((EntityType<? extends ThrowableItemProjectile>)$$0, $$1);
    }

    public ThrownEgg(Level $$0, LivingEntity $$1) {
        super((EntityType<? extends ThrowableItemProjectile>)EntityType.EGG, $$1, $$0);
    }

    public ThrownEgg(Level $$0, double $$1, double $$2, double $$3) {
        super((EntityType<? extends ThrowableItemProjectile>)EntityType.EGG, $$1, $$2, $$3, $$0);
    }

    @Override
    public void handleEntityEvent(byte $$0) {
        if ($$0 == 3) {
            double $$1 = 0.08;
            for (int $$2 = 0; $$2 < 8; ++$$2) {
                this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem()), this.getX(), this.getY(), this.getZ(), ((double)this.random.nextFloat() - 0.5) * 0.08, ((double)this.random.nextFloat() - 0.5) * 0.08, ((double)this.random.nextFloat() - 0.5) * 0.08);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult $$0) {
        super.onHitEntity($$0);
        $$0.getEntity().hurt(DamageSource.thrown(this, this.getOwner()), 0.0f);
    }

    @Override
    protected void onHit(HitResult $$0) {
        super.onHit($$0);
        if (!this.level.isClientSide) {
            if (this.random.nextInt(8) == 0) {
                int $$1 = 1;
                if (this.random.nextInt(32) == 0) {
                    $$1 = 4;
                }
                for (int $$2 = 0; $$2 < $$1; ++$$2) {
                    Chicken $$3 = EntityType.CHICKEN.create(this.level);
                    if ($$3 == null) continue;
                    $$3.setAge(-24000);
                    $$3.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0f);
                    this.level.addFreshEntity($$3);
                }
            }
            this.level.broadcastEntityEvent(this, (byte)3);
            this.discard();
        }
    }

    @Override
    protected Item getDefaultItem() {
        return Items.EGG;
    }
}