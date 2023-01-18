/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;

public class Stray
extends AbstractSkeleton {
    public Stray(EntityType<? extends Stray> $$0, Level $$1) {
        super((EntityType<? extends AbstractSkeleton>)$$0, $$1);
    }

    public static boolean checkStraySpawnRules(EntityType<Stray> $$0, ServerLevelAccessor $$1, MobSpawnType $$2, BlockPos $$3, RandomSource $$4) {
        Vec3i $$5 = $$3;
        while ($$1.getBlockState((BlockPos)($$5 = ((BlockPos)$$5).above())).is(Blocks.POWDER_SNOW)) {
        }
        return Stray.checkMonsterSpawnRules($$0, $$1, $$2, $$3, $$4) && ($$2 == MobSpawnType.SPAWNER || $$1.canSeeSky((BlockPos)((BlockPos)$$5).below()));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.STRAY_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource $$0) {
        return SoundEvents.STRAY_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.STRAY_DEATH;
    }

    @Override
    SoundEvent getStepSound() {
        return SoundEvents.STRAY_STEP;
    }

    @Override
    protected AbstractArrow getArrow(ItemStack $$0, float $$1) {
        AbstractArrow $$2 = super.getArrow($$0, $$1);
        if ($$2 instanceof Arrow) {
            ((Arrow)$$2).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600));
        }
        return $$2;
    }
}