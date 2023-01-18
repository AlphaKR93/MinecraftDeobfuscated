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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RodBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class LightningRodBlock
extends RodBlock
implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private static final int ACTIVATION_TICKS = 8;
    public static final int RANGE = 128;
    private static final int SPARK_CYCLE = 200;

    public LightningRodBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.UP)).setValue(WATERLOGGED, false)).setValue(POWERED, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        FluidState $$1 = $$0.getLevel().getFluidState($$0.getClickedPos());
        boolean $$2 = $$1.getType() == Fluids.WATER;
        return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, $$0.getClickedFace())).setValue(WATERLOGGED, $$2);
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$3.scheduleTick($$4, Fluids.WATER, Fluids.WATER.getTickDelay($$3));
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }

    @Override
    public int getSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        return $$0.getValue(POWERED) != false ? 15 : 0;
    }

    @Override
    public int getDirectSignal(BlockState $$0, BlockGetter $$1, BlockPos $$2, Direction $$3) {
        if ($$0.getValue(POWERED).booleanValue() && $$0.getValue(FACING) == $$3) {
            return 15;
        }
        return 0;
    }

    public void onLightningStrike(BlockState $$0, Level $$1, BlockPos $$2) {
        $$1.setBlock($$2, (BlockState)$$0.setValue(POWERED, true), 3);
        this.updateNeighbours($$0, $$1, $$2);
        $$1.scheduleTick($$2, this, 8);
        $$1.levelEvent(3002, $$2, $$0.getValue(FACING).getAxis().ordinal());
    }

    private void updateNeighbours(BlockState $$0, Level $$1, BlockPos $$2) {
        $$1.updateNeighborsAt((BlockPos)$$2.relative($$0.getValue(FACING).getOpposite()), this);
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        $$1.setBlock($$2, (BlockState)$$0.setValue(POWERED, false), 3);
        this.updateNeighbours($$0, $$1, $$2);
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        if (!$$1.isThundering() || (long)$$1.random.nextInt(200) > $$1.getGameTime() % 200L || $$2.getY() != $$1.getHeight(Heightmap.Types.WORLD_SURFACE, $$2.getX(), $$2.getZ()) - 1) {
            return;
        }
        ParticleUtils.spawnParticlesAlongAxis($$0.getValue(FACING).getAxis(), $$1, $$2, 0.125, ParticleTypes.ELECTRIC_SPARK, UniformInt.of(1, 2));
    }

    @Override
    public void onRemove(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$0.is($$3.getBlock())) {
            return;
        }
        if ($$0.getValue(POWERED).booleanValue()) {
            this.updateNeighbours($$0, $$1, $$2);
        }
        super.onRemove($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$0.is($$3.getBlock())) {
            return;
        }
        if ($$0.getValue(POWERED).booleanValue() && !$$1.getBlockTicks().hasScheduledTick($$2, this)) {
            $$1.setBlock($$2, (BlockState)$$0.setValue(POWERED, false), 18);
        }
    }

    @Override
    public void onProjectileHit(Level $$0, BlockState $$1, BlockHitResult $$2, Projectile $$3) {
        BlockPos $$4;
        if ($$0.isThundering() && $$3 instanceof ThrownTrident && ((ThrownTrident)$$3).isChanneling() && $$0.canSeeSky($$4 = $$2.getBlockPos())) {
            LightningBolt $$5 = EntityType.LIGHTNING_BOLT.create($$0);
            if ($$5 != null) {
                $$5.moveTo(Vec3.atBottomCenterOf($$4.above()));
                Entity $$6 = $$3.getOwner();
                $$5.setCause($$6 instanceof ServerPlayer ? (ServerPlayer)$$6 : null);
                $$0.addFreshEntity($$5);
            }
            $$0.playSound(null, $$4, SoundEvents.TRIDENT_THUNDER, SoundSource.WEATHER, 5.0f, 1.0f);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(FACING, POWERED, WATERLOGGED);
    }

    @Override
    public boolean isSignalSource(BlockState $$0) {
        return true;
    }
}