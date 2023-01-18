/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  net.minecraft.world.level.Level
 */
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class MyceliumBlock
extends SpreadingSnowyDirtBlock {
    public MyceliumBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        super.animateTick($$0, $$1, $$2, $$3);
        if ($$3.nextInt(10) == 0) {
            $$1.addParticle((ParticleOptions)ParticleTypes.MYCELIUM, (double)$$2.getX() + $$3.nextDouble(), (double)$$2.getY() + 1.1, (double)$$2.getZ() + $$3.nextDouble(), 0.0, 0.0, 0.0);
        }
    }
}