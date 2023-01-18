/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.TheEndGatewayBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

public class EndGatewayBlock
extends BaseEntityBlock {
    protected EndGatewayBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new TheEndGatewayBlockEntity($$0, $$1);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$0, BlockState $$1, BlockEntityType<T> $$2) {
        return EndGatewayBlock.createTickerHelper($$2, BlockEntityType.END_GATEWAY, $$0.isClientSide ? TheEndGatewayBlockEntity::beamAnimationTick : TheEndGatewayBlockEntity::teleportTick);
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        BlockEntity $$4 = $$1.getBlockEntity($$2);
        if (!($$4 instanceof TheEndGatewayBlockEntity)) {
            return;
        }
        int $$5 = ((TheEndGatewayBlockEntity)$$4).getParticleAmount();
        for (int $$6 = 0; $$6 < $$5; ++$$6) {
            double $$7 = (double)$$2.getX() + $$3.nextDouble();
            double $$8 = (double)$$2.getY() + $$3.nextDouble();
            double $$9 = (double)$$2.getZ() + $$3.nextDouble();
            double $$10 = ($$3.nextDouble() - 0.5) * 0.5;
            double $$11 = ($$3.nextDouble() - 0.5) * 0.5;
            double $$12 = ($$3.nextDouble() - 0.5) * 0.5;
            int $$13 = $$3.nextInt(2) * 2 - 1;
            if ($$3.nextBoolean()) {
                $$9 = (double)$$2.getZ() + 0.5 + 0.25 * (double)$$13;
                $$12 = $$3.nextFloat() * 2.0f * (float)$$13;
            } else {
                $$7 = (double)$$2.getX() + 0.5 + 0.25 * (double)$$13;
                $$10 = $$3.nextFloat() * 2.0f * (float)$$13;
            }
            $$1.addParticle(ParticleTypes.PORTAL, $$7, $$8, $$9, $$10, $$11, $$12);
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter $$0, BlockPos $$1, BlockState $$2) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canBeReplaced(BlockState $$0, Fluid $$1) {
        return false;
    }
}