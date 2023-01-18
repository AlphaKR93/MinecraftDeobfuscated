/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WetSpongeBlock
extends Block {
    protected WetSpongeBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    public void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$1.dimensionType().ultraWarm()) {
            $$1.setBlock($$2, Blocks.SPONGE.defaultBlockState(), 3);
            $$1.levelEvent(2009, $$2, 0);
            $$1.playSound(null, $$2, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0f, (1.0f + $$1.getRandom().nextFloat() * 0.2f) * 0.7f);
        }
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        Direction $$4 = Direction.getRandom($$3);
        if ($$4 == Direction.UP) {
            return;
        }
        Vec3i $$5 = $$2.relative($$4);
        BlockState $$6 = $$1.getBlockState((BlockPos)$$5);
        if ($$0.canOcclude() && $$6.isFaceSturdy($$1, (BlockPos)$$5, $$4.getOpposite())) {
            return;
        }
        double $$7 = $$2.getX();
        double $$8 = $$2.getY();
        double $$9 = $$2.getZ();
        if ($$4 == Direction.DOWN) {
            $$8 -= 0.05;
            $$7 += $$3.nextDouble();
            $$9 += $$3.nextDouble();
        } else {
            $$8 += $$3.nextDouble() * 0.8;
            if ($$4.getAxis() == Direction.Axis.X) {
                $$9 += $$3.nextDouble();
                $$7 = $$4 == Direction.EAST ? ($$7 += 1.1) : ($$7 += 0.05);
            } else {
                $$7 += $$3.nextDouble();
                $$9 = $$4 == Direction.SOUTH ? ($$9 += 1.1) : ($$9 += 0.05);
            }
        }
        $$1.addParticle(ParticleTypes.DRIPPING_WATER, $$7, $$8, $$9, 0.0, 0.0, 0.0);
    }
}