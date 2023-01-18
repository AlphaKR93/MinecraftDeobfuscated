/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Double
 *  java.lang.Object
 *  java.util.Optional
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.projectile;

import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class ProjectileUtil {
    public static HitResult getHitResult(Entity $$0, Predicate<Entity> $$1) {
        EntityHitResult $$7;
        Vec3 $$5;
        Vec3 $$2 = $$0.getDeltaMovement();
        Level $$3 = $$0.level;
        Vec3 $$4 = $$0.position();
        HitResult $$6 = $$3.clip(new ClipContext($$4, $$5 = $$4.add($$2), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, $$0));
        if (((HitResult)$$6).getType() != HitResult.Type.MISS) {
            $$5 = $$6.getLocation();
        }
        if (($$7 = ProjectileUtil.getEntityHitResult($$3, $$0, $$4, $$5, $$0.getBoundingBox().expandTowards($$0.getDeltaMovement()).inflate(1.0), $$1)) != null) {
            $$6 = $$7;
        }
        return $$6;
    }

    @Nullable
    public static EntityHitResult getEntityHitResult(Entity $$0, Vec3 $$1, Vec3 $$2, AABB $$3, Predicate<Entity> $$4, double $$5) {
        Level $$6 = $$0.level;
        double $$7 = $$5;
        Entity $$8 = null;
        Vec3 $$9 = null;
        for (Entity $$10 : $$6.getEntities($$0, $$3, $$4)) {
            Vec3 $$13;
            double $$14;
            AABB $$11 = $$10.getBoundingBox().inflate($$10.getPickRadius());
            Optional<Vec3> $$12 = $$11.clip($$1, $$2);
            if ($$11.contains($$1)) {
                if (!($$7 >= 0.0)) continue;
                $$8 = $$10;
                $$9 = (Vec3)$$12.orElse((Object)$$1);
                $$7 = 0.0;
                continue;
            }
            if (!$$12.isPresent() || !(($$14 = $$1.distanceToSqr($$13 = (Vec3)$$12.get())) < $$7) && $$7 != 0.0) continue;
            if ($$10.getRootVehicle() == $$0.getRootVehicle()) {
                if ($$7 != 0.0) continue;
                $$8 = $$10;
                $$9 = $$13;
                continue;
            }
            $$8 = $$10;
            $$9 = $$13;
            $$7 = $$14;
        }
        if ($$8 == null) {
            return null;
        }
        return new EntityHitResult($$8, $$9);
    }

    @Nullable
    public static EntityHitResult getEntityHitResult(Level $$0, Entity $$1, Vec3 $$2, Vec3 $$3, AABB $$4, Predicate<Entity> $$5) {
        return ProjectileUtil.getEntityHitResult($$0, $$1, $$2, $$3, $$4, $$5, 0.3f);
    }

    @Nullable
    public static EntityHitResult getEntityHitResult(Level $$0, Entity $$1, Vec3 $$2, Vec3 $$3, AABB $$4, Predicate<Entity> $$5, float $$6) {
        double $$7 = Double.MAX_VALUE;
        Entity $$8 = null;
        for (Entity $$9 : $$0.getEntities($$1, $$4, $$5)) {
            double $$12;
            AABB $$10 = $$9.getBoundingBox().inflate($$6);
            Optional<Vec3> $$11 = $$10.clip($$2, $$3);
            if (!$$11.isPresent() || !(($$12 = $$2.distanceToSqr((Vec3)$$11.get())) < $$7)) continue;
            $$8 = $$9;
            $$7 = $$12;
        }
        if ($$8 == null) {
            return null;
        }
        return new EntityHitResult($$8);
    }

    public static void rotateTowardsMovement(Entity $$0, float $$1) {
        Vec3 $$2 = $$0.getDeltaMovement();
        if ($$2.lengthSqr() == 0.0) {
            return;
        }
        double $$3 = $$2.horizontalDistance();
        $$0.setYRot((float)(Mth.atan2($$2.z, $$2.x) * 57.2957763671875) + 90.0f);
        $$0.setXRot((float)(Mth.atan2($$3, $$2.y) * 57.2957763671875) - 90.0f);
        while ($$0.getXRot() - $$0.xRotO < -180.0f) {
            $$0.xRotO -= 360.0f;
        }
        while ($$0.getXRot() - $$0.xRotO >= 180.0f) {
            $$0.xRotO += 360.0f;
        }
        while ($$0.getYRot() - $$0.yRotO < -180.0f) {
            $$0.yRotO -= 360.0f;
        }
        while ($$0.getYRot() - $$0.yRotO >= 180.0f) {
            $$0.yRotO += 360.0f;
        }
        $$0.setXRot(Mth.lerp($$1, $$0.xRotO, $$0.getXRot()));
        $$0.setYRot(Mth.lerp($$1, $$0.yRotO, $$0.getYRot()));
    }

    public static InteractionHand getWeaponHoldingHand(LivingEntity $$0, Item $$1) {
        return $$0.getMainHandItem().is($$1) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
    }

    public static AbstractArrow getMobArrow(LivingEntity $$0, ItemStack $$1, float $$2) {
        ArrowItem $$3 = (ArrowItem)($$1.getItem() instanceof ArrowItem ? $$1.getItem() : Items.ARROW);
        AbstractArrow $$4 = $$3.createArrow($$0.level, $$1, $$0);
        $$4.setEnchantmentEffectsFromEntity($$0, $$2);
        if ($$1.is(Items.TIPPED_ARROW) && $$4 instanceof Arrow) {
            ((Arrow)$$4).setEffectsFromItem($$1);
        }
        return $$4;
    }
}