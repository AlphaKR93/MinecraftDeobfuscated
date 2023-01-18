/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  net.minecraft.world.entity.LivingEntity
 */
package net.minecraft.world.entity.projectile;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class ThrownExperienceBottle
extends ThrowableItemProjectile {
    public ThrownExperienceBottle(EntityType<? extends ThrownExperienceBottle> $$0, Level $$1) {
        super((EntityType<? extends ThrowableItemProjectile>)$$0, $$1);
    }

    public ThrownExperienceBottle(Level $$0, LivingEntity $$1) {
        super((EntityType<? extends ThrowableItemProjectile>)EntityType.EXPERIENCE_BOTTLE, $$1, $$0);
    }

    public ThrownExperienceBottle(Level $$0, double $$1, double $$2, double $$3) {
        super((EntityType<? extends ThrowableItemProjectile>)EntityType.EXPERIENCE_BOTTLE, $$1, $$2, $$3, $$0);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.EXPERIENCE_BOTTLE;
    }

    @Override
    protected float getGravity() {
        return 0.07f;
    }

    @Override
    protected void onHit(HitResult $$0) {
        super.onHit($$0);
        if (this.level instanceof ServerLevel) {
            this.level.levelEvent(2002, this.blockPosition(), PotionUtils.getColor(Potions.WATER));
            int $$1 = 3 + this.level.random.nextInt(5) + this.level.random.nextInt(5);
            ExperienceOrb.award((ServerLevel)this.level, this.position(), $$1);
            this.discard();
        }
    }
}