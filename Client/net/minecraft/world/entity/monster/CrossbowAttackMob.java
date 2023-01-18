/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  javax.annotation.Nullable
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package net.minecraft.world.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public interface CrossbowAttackMob
extends RangedAttackMob {
    public void setChargingCrossbow(boolean var1);

    public void shootCrossbowProjectile(LivingEntity var1, ItemStack var2, Projectile var3, float var4);

    @Nullable
    public LivingEntity getTarget();

    public void onCrossbowAttackPerformed();

    default public void performCrossbowAttack(LivingEntity $$0, float $$1) {
        InteractionHand $$2 = ProjectileUtil.getWeaponHoldingHand($$0, Items.CROSSBOW);
        ItemStack $$3 = $$0.getItemInHand($$2);
        if ($$0.isHolding(Items.CROSSBOW)) {
            CrossbowItem.performShooting($$0.level, $$0, $$2, $$3, $$1, 14 - $$0.level.getDifficulty().getId() * 4);
        }
        this.onCrossbowAttackPerformed();
    }

    default public void shootCrossbowProjectile(LivingEntity $$0, LivingEntity $$1, Projectile $$2, float $$3, float $$4) {
        double $$5 = $$1.getX() - $$0.getX();
        double $$6 = $$1.getZ() - $$0.getZ();
        double $$7 = Math.sqrt((double)($$5 * $$5 + $$6 * $$6));
        double $$8 = $$1.getY(0.3333333333333333) - $$2.getY() + $$7 * (double)0.2f;
        Vector3f $$9 = this.getProjectileShotVector($$0, new Vec3($$5, $$8, $$6), $$3);
        $$2.shoot($$9.x(), $$9.y(), $$9.z(), $$4, 14 - $$0.level.getDifficulty().getId() * 4);
        $$0.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0f, 1.0f / ($$0.getRandom().nextFloat() * 0.4f + 0.8f));
    }

    default public Vector3f getProjectileShotVector(LivingEntity $$0, Vec3 $$1, float $$2) {
        Vector3f $$3 = $$1.toVector3f().normalize();
        Vector3f $$4 = new Vector3f((Vector3fc)$$3).cross((Vector3fc)new Vector3f(0.0f, 1.0f, 0.0f));
        if ((double)$$4.lengthSquared() <= 1.0E-7) {
            Vec3 $$5 = $$0.getUpVector(1.0f);
            $$4 = new Vector3f((Vector3fc)$$3).cross((Vector3fc)$$5.toVector3f());
        }
        Vector3f $$6 = new Vector3f((Vector3fc)$$3).rotateAxis(1.5707964f, $$4.x, $$4.y, $$4.z);
        return new Vector3f((Vector3fc)$$3).rotateAxis($$2 * ((float)Math.PI / 180), $$6.x, $$6.y, $$6.z);
    }
}