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
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DragonEggBlock
extends FallingBlock {
    protected static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

    public DragonEggBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        this.teleport($$0, $$1, $$2);
        return InteractionResult.sidedSuccess($$1.isClientSide);
    }

    @Override
    public void attack(BlockState $$0, Level $$1, BlockPos $$2, Player $$3) {
        this.teleport($$0, $$1, $$2);
    }

    private void teleport(BlockState $$0, Level $$1, BlockPos $$2) {
        WorldBorder $$3 = $$1.getWorldBorder();
        for (int $$4 = 0; $$4 < 1000; ++$$4) {
            BlockPos $$5 = $$2.offset($$1.random.nextInt(16) - $$1.random.nextInt(16), $$1.random.nextInt(8) - $$1.random.nextInt(8), $$1.random.nextInt(16) - $$1.random.nextInt(16));
            if (!$$1.getBlockState($$5).isAir() || !$$3.isWithinBounds($$5)) continue;
            if ($$1.isClientSide) {
                for (int $$6 = 0; $$6 < 128; ++$$6) {
                    double $$7 = $$1.random.nextDouble();
                    float $$8 = ($$1.random.nextFloat() - 0.5f) * 0.2f;
                    float $$9 = ($$1.random.nextFloat() - 0.5f) * 0.2f;
                    float $$10 = ($$1.random.nextFloat() - 0.5f) * 0.2f;
                    double $$11 = Mth.lerp($$7, (double)$$5.getX(), (double)$$2.getX()) + ($$1.random.nextDouble() - 0.5) + 0.5;
                    double $$12 = Mth.lerp($$7, (double)$$5.getY(), (double)$$2.getY()) + $$1.random.nextDouble() - 0.5;
                    double $$13 = Mth.lerp($$7, (double)$$5.getZ(), (double)$$2.getZ()) + ($$1.random.nextDouble() - 0.5) + 0.5;
                    $$1.addParticle(ParticleTypes.PORTAL, $$11, $$12, $$13, $$8, $$9, $$10);
                }
            } else {
                $$1.setBlock($$5, $$0, 2);
                $$1.removeBlock($$2, false);
            }
            return;
        }
    }

    @Override
    protected int getDelayAfterPlace() {
        return 5;
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        return false;
    }
}