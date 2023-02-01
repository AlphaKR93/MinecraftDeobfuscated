/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.entity.projectile;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ThrownPotion
extends ThrowableItemProjectile
implements ItemSupplier {
    public static final double SPLASH_RANGE = 4.0;
    private static final double SPLASH_RANGE_SQ = 16.0;
    public static final Predicate<LivingEntity> WATER_SENSITIVE_OR_ON_FIRE = $$0 -> $$0.isSensitiveToWater() || $$0.isOnFire();

    public ThrownPotion(EntityType<? extends ThrownPotion> $$0, Level $$1) {
        super((EntityType<? extends ThrowableItemProjectile>)$$0, $$1);
    }

    public ThrownPotion(Level $$0, LivingEntity $$1) {
        super((EntityType<? extends ThrowableItemProjectile>)EntityType.POTION, $$1, $$0);
    }

    public ThrownPotion(Level $$0, double $$1, double $$2, double $$3) {
        super((EntityType<? extends ThrowableItemProjectile>)EntityType.POTION, $$1, $$2, $$3, $$0);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.SPLASH_POTION;
    }

    @Override
    protected float getGravity() {
        return 0.05f;
    }

    @Override
    protected void onHitBlock(BlockHitResult $$0) {
        super.onHitBlock($$0);
        if (this.level.isClientSide) {
            return;
        }
        ItemStack $$1 = this.getItem();
        Potion $$2 = PotionUtils.getPotion($$1);
        List<MobEffectInstance> $$3 = PotionUtils.getMobEffects($$1);
        boolean $$4 = $$2 == Potions.WATER && $$3.isEmpty();
        Direction $$5 = $$0.getDirection();
        BlockPos $$6 = $$0.getBlockPos();
        Vec3i $$7 = $$6.relative($$5);
        if ($$4) {
            this.dowseFire((BlockPos)$$7);
            this.dowseFire((BlockPos)((BlockPos)$$7).relative($$5.getOpposite()));
            for (Direction $$8 : Direction.Plane.HORIZONTAL) {
                this.dowseFire((BlockPos)((BlockPos)$$7).relative($$8));
            }
        }
    }

    @Override
    protected void onHit(HitResult $$0) {
        boolean $$4;
        super.onHit($$0);
        if (this.level.isClientSide) {
            return;
        }
        ItemStack $$1 = this.getItem();
        Potion $$2 = PotionUtils.getPotion($$1);
        List<MobEffectInstance> $$3 = PotionUtils.getMobEffects($$1);
        boolean bl = $$4 = $$2 == Potions.WATER && $$3.isEmpty();
        if ($$4) {
            this.applyWater();
        } else if (!$$3.isEmpty()) {
            if (this.isLingering()) {
                this.makeAreaOfEffectCloud($$1, $$2);
            } else {
                this.applySplash($$3, $$0.getType() == HitResult.Type.ENTITY ? ((EntityHitResult)$$0).getEntity() : null);
            }
        }
        int $$5 = $$2.hasInstantEffects() ? 2007 : 2002;
        this.level.levelEvent($$5, this.blockPosition(), PotionUtils.getColor($$1));
        this.discard();
    }

    private void applyWater() {
        AABB $$0 = this.getBoundingBox().inflate(4.0, 2.0, 4.0);
        List $$1 = this.level.getEntitiesOfClass(LivingEntity.class, $$0, WATER_SENSITIVE_OR_ON_FIRE);
        for (LivingEntity $$2 : $$1) {
            double $$3 = this.distanceToSqr($$2);
            if (!($$3 < 16.0)) continue;
            if ($$2.isSensitiveToWater()) {
                $$2.hurt(DamageSource.indirectMagic(this, this.getOwner()), 1.0f);
            }
            if (!$$2.isOnFire() || !$$2.isAlive()) continue;
            $$2.extinguishFire();
        }
        List $$4 = this.level.getEntitiesOfClass(Axolotl.class, $$0);
        for (Axolotl $$5 : $$4) {
            $$5.rehydrate();
        }
    }

    private void applySplash(List<MobEffectInstance> $$0, @Nullable Entity $$12) {
        AABB $$2 = this.getBoundingBox().inflate(4.0, 2.0, 4.0);
        List $$3 = this.level.getEntitiesOfClass(LivingEntity.class, $$2);
        if (!$$3.isEmpty()) {
            Entity $$4 = this.getEffectSource();
            for (LivingEntity $$5 : $$3) {
                double $$8;
                double $$6;
                if (!$$5.isAffectedByPotions() || !(($$6 = this.distanceToSqr($$5)) < 16.0)) continue;
                if ($$5 == $$12) {
                    double $$7 = 1.0;
                } else {
                    $$8 = 1.0 - Math.sqrt((double)$$6) / 4.0;
                }
                for (MobEffectInstance $$9 : $$0) {
                    MobEffect $$10 = $$9.getEffect();
                    if ($$10.isInstantenous()) {
                        $$10.applyInstantenousEffect(this, this.getOwner(), $$5, $$9.getAmplifier(), $$8);
                        continue;
                    }
                    int $$11 = $$9.mapDuration($$1 -> (int)($$8 * (double)$$1 + 0.5));
                    MobEffectInstance $$122 = new MobEffectInstance($$10, $$11, $$9.getAmplifier(), $$9.isAmbient(), $$9.isVisible());
                    if ($$122.endsWithin(20)) continue;
                    $$5.addEffect($$122, $$4);
                }
            }
        }
    }

    private void makeAreaOfEffectCloud(ItemStack $$0, Potion $$1) {
        AreaEffectCloud $$2 = new AreaEffectCloud(this.level, this.getX(), this.getY(), this.getZ());
        Entity $$3 = this.getOwner();
        if ($$3 instanceof LivingEntity) {
            $$2.setOwner((LivingEntity)$$3);
        }
        $$2.setRadius(3.0f);
        $$2.setRadiusOnUse(-0.5f);
        $$2.setWaitTime(10);
        $$2.setRadiusPerTick(-$$2.getRadius() / (float)$$2.getDuration());
        $$2.setPotion($$1);
        for (MobEffectInstance $$4 : PotionUtils.getCustomEffects($$0)) {
            $$2.addEffect(new MobEffectInstance($$4));
        }
        CompoundTag $$5 = $$0.getTag();
        if ($$5 != null && $$5.contains("CustomPotionColor", 99)) {
            $$2.setFixedColor($$5.getInt("CustomPotionColor"));
        }
        this.level.addFreshEntity($$2);
    }

    private boolean isLingering() {
        return this.getItem().is(Items.LINGERING_POTION);
    }

    private void dowseFire(BlockPos $$0) {
        BlockState $$1 = this.level.getBlockState($$0);
        if ($$1.is(BlockTags.FIRE)) {
            this.level.removeBlock($$0, false);
        } else if (AbstractCandleBlock.isLit($$1)) {
            AbstractCandleBlock.extinguish(null, $$1, this.level, $$0);
        } else if (CampfireBlock.isLitCampfire($$1)) {
            this.level.levelEvent(null, 1009, $$0, 0);
            CampfireBlock.dowse(this.getOwner(), this.level, $$0, $$1);
            this.level.setBlockAndUpdate($$0, (BlockState)$$1.setValue(CampfireBlock.LIT, false));
        }
    }
}