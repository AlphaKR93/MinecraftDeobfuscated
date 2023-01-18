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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class NetherPortalBlock
extends Block {
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    protected static final int AABB_OFFSET = 2;
    protected static final VoxelShape X_AXIS_AABB = Block.box(0.0, 0.0, 6.0, 16.0, 16.0, 10.0);
    protected static final VoxelShape Z_AXIS_AABB = Block.box(6.0, 0.0, 0.0, 10.0, 16.0, 16.0);

    public NetherPortalBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AXIS, Direction.Axis.X));
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        switch ($$0.getValue(AXIS)) {
            case Z: {
                return Z_AXIS_AABB;
            }
        }
        return X_AXIS_AABB;
    }

    @Override
    public void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        if ($$1.dimensionType().natural() && $$1.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && $$3.nextInt(2000) < $$1.getDifficulty().getId()) {
            ZombifiedPiglin $$4;
            while ($$1.getBlockState((BlockPos)$$2).is(this)) {
                $$2 = ((BlockPos)$$2).below();
            }
            if ($$1.getBlockState((BlockPos)$$2).isValidSpawn($$1, (BlockPos)$$2, EntityType.ZOMBIFIED_PIGLIN) && ($$4 = EntityType.ZOMBIFIED_PIGLIN.spawn($$1, (BlockPos)((BlockPos)$$2).above(), MobSpawnType.STRUCTURE)) != null) {
                $$4.setPortalCooldown();
            }
        }
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        boolean $$8;
        Direction.Axis $$6 = $$1.getAxis();
        Direction.Axis $$7 = $$0.getValue(AXIS);
        boolean bl = $$8 = $$7 != $$6 && $$6.isHorizontal();
        if ($$8 || $$2.is(this) || new PortalShape($$3, $$4, $$7).isComplete()) {
            return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3) {
        if ($$3.canChangeDimensions()) {
            $$3.handleInsidePortal($$2);
        }
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        if ($$3.nextInt(100) == 0) {
            $$1.playLocalSound((double)$$2.getX() + 0.5, (double)$$2.getY() + 0.5, (double)$$2.getZ() + 0.5, SoundEvents.PORTAL_AMBIENT, SoundSource.BLOCKS, 0.5f, $$3.nextFloat() * 0.4f + 0.8f, false);
        }
        for (int $$4 = 0; $$4 < 4; ++$$4) {
            double $$5 = (double)$$2.getX() + $$3.nextDouble();
            double $$6 = (double)$$2.getY() + $$3.nextDouble();
            double $$7 = (double)$$2.getZ() + $$3.nextDouble();
            double $$8 = ((double)$$3.nextFloat() - 0.5) * 0.5;
            double $$9 = ((double)$$3.nextFloat() - 0.5) * 0.5;
            double $$10 = ((double)$$3.nextFloat() - 0.5) * 0.5;
            int $$11 = $$3.nextInt(2) * 2 - 1;
            if ($$1.getBlockState((BlockPos)$$2.west()).is(this) || $$1.getBlockState((BlockPos)$$2.east()).is(this)) {
                $$7 = (double)$$2.getZ() + 0.5 + 0.25 * (double)$$11;
                $$10 = $$3.nextFloat() * 2.0f * (float)$$11;
            } else {
                $$5 = (double)$$2.getX() + 0.5 + 0.25 * (double)$$11;
                $$8 = $$3.nextFloat() * 2.0f * (float)$$11;
            }
            $$1.addParticle(ParticleTypes.PORTAL, $$5, $$6, $$7, $$8, $$9, $$10);
        }
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter $$0, BlockPos $$1, BlockState $$2) {
        return ItemStack.EMPTY;
    }

    @Override
    public BlockState rotate(BlockState $$0, Rotation $$1) {
        switch ($$1) {
            case COUNTERCLOCKWISE_90: 
            case CLOCKWISE_90: {
                switch ($$0.getValue(AXIS)) {
                    case X: {
                        return (BlockState)$$0.setValue(AXIS, Direction.Axis.Z);
                    }
                    case Z: {
                        return (BlockState)$$0.setValue(AXIS, Direction.Axis.X);
                    }
                }
                return $$0;
            }
        }
        return $$0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(AXIS);
    }
}