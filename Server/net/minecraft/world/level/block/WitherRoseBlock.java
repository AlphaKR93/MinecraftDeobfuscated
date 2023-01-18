/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WitherRoseBlock
extends FlowerBlock {
    public WitherRoseBlock(MobEffect $$0, BlockBehaviour.Properties $$1) {
        super($$0, 8, $$1);
    }

    @Override
    protected boolean mayPlaceOn(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return super.mayPlaceOn($$0, $$1, $$2) || $$0.is(Blocks.NETHERRACK) || $$0.is(Blocks.SOUL_SAND) || $$0.is(Blocks.SOUL_SOIL);
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        VoxelShape $$4 = this.getShape($$0, $$1, $$2, CollisionContext.empty());
        Vec3 $$5 = $$4.bounds().getCenter();
        double $$6 = (double)$$2.getX() + $$5.x;
        double $$7 = (double)$$2.getZ() + $$5.z;
        for (int $$8 = 0; $$8 < 3; ++$$8) {
            if (!$$3.nextBoolean()) continue;
            $$1.addParticle(ParticleTypes.SMOKE, $$6 + $$3.nextDouble() / 5.0, (double)$$2.getY() + (0.5 - $$3.nextDouble()), $$7 + $$3.nextDouble() / 5.0, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3) {
        LivingEntity $$4;
        if ($$1.isClientSide || $$1.getDifficulty() == Difficulty.PEACEFUL) {
            return;
        }
        if ($$3 instanceof LivingEntity && !($$4 = (LivingEntity)$$3).isInvulnerableTo(DamageSource.WITHER)) {
            $$4.addEffect(new MobEffectInstance(MobEffects.WITHER, 40));
        }
    }
}