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
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EndPortalBlock
extends BaseEntityBlock {
    protected static final VoxelShape SHAPE = Block.box(0.0, 6.0, 0.0, 16.0, 12.0, 16.0);

    protected EndPortalBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new TheEndPortalBlockEntity($$0, $$1);
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE;
    }

    @Override
    public void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3) {
        if ($$1 instanceof ServerLevel && !$$3.isPassenger() && !$$3.isVehicle() && $$3.canChangeDimensions() && Shapes.joinIsNotEmpty(Shapes.create($$3.getBoundingBox().move(-$$2.getX(), -$$2.getY(), -$$2.getZ())), $$0.getShape($$1, $$2), BooleanOp.AND)) {
            ResourceKey<Level> $$4 = $$1.dimension() == Level.END ? Level.OVERWORLD : Level.END;
            ServerLevel $$5 = ((ServerLevel)$$1).getServer().getLevel($$4);
            if ($$5 == null) {
                return;
            }
            $$3.changeDimension($$5);
        }
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        double $$4 = (double)$$2.getX() + $$3.nextDouble();
        double $$5 = (double)$$2.getY() + 0.8;
        double $$6 = (double)$$2.getZ() + $$3.nextDouble();
        $$1.addParticle(ParticleTypes.SMOKE, $$4, $$5, $$6, 0.0, 0.0, 0.0);
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