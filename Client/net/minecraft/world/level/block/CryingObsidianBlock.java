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
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class CryingObsidianBlock
extends Block {
    public CryingObsidianBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        if ($$3.nextInt(5) != 0) {
            return;
        }
        Direction $$4 = Direction.getRandom($$3);
        if ($$4 == Direction.UP) {
            return;
        }
        Vec3i $$5 = $$2.relative($$4);
        BlockState $$6 = $$1.getBlockState((BlockPos)$$5);
        if ($$0.canOcclude() && $$6.isFaceSturdy($$1, (BlockPos)$$5, $$4.getOpposite())) {
            return;
        }
        double $$7 = $$4.getStepX() == 0 ? $$3.nextDouble() : 0.5 + (double)$$4.getStepX() * 0.6;
        double $$8 = $$4.getStepY() == 0 ? $$3.nextDouble() : 0.5 + (double)$$4.getStepY() * 0.6;
        double $$9 = $$4.getStepZ() == 0 ? $$3.nextDouble() : 0.5 + (double)$$4.getStepZ() * 0.6;
        $$1.addParticle(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, (double)$$2.getX() + $$7, (double)$$2.getY() + $$8, (double)$$2.getZ() + $$9, 0.0, 0.0, 0.0);
    }
}