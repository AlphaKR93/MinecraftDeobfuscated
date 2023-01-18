/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  java.lang.Float
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 */
package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableList;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.DismountHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class RespawnAnchorBlock
extends Block {
    public static final int MIN_CHARGES = 0;
    public static final int MAX_CHARGES = 4;
    public static final IntegerProperty CHARGE = BlockStateProperties.RESPAWN_ANCHOR_CHARGES;
    private static final ImmutableList<Vec3i> RESPAWN_HORIZONTAL_OFFSETS = ImmutableList.of((Object)new Vec3i(0, 0, -1), (Object)new Vec3i(-1, 0, 0), (Object)new Vec3i(0, 0, 1), (Object)new Vec3i(1, 0, 0), (Object)new Vec3i(-1, 0, -1), (Object)new Vec3i(1, 0, -1), (Object)new Vec3i(-1, 0, 1), (Object)new Vec3i(1, 0, 1));
    private static final ImmutableList<Vec3i> RESPAWN_OFFSETS = new ImmutableList.Builder().addAll(RESPAWN_HORIZONTAL_OFFSETS).addAll(RESPAWN_HORIZONTAL_OFFSETS.stream().map(Vec3i::below).iterator()).addAll(RESPAWN_HORIZONTAL_OFFSETS.stream().map(Vec3i::above).iterator()).add((Object)new Vec3i(0, 1, 0)).build();

    public RespawnAnchorBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(CHARGE, 0));
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        ItemStack $$6 = $$3.getItemInHand($$4);
        if ($$4 == InteractionHand.MAIN_HAND && !RespawnAnchorBlock.isRespawnFuel($$6) && RespawnAnchorBlock.isRespawnFuel($$3.getItemInHand(InteractionHand.OFF_HAND))) {
            return InteractionResult.PASS;
        }
        if (RespawnAnchorBlock.isRespawnFuel($$6) && RespawnAnchorBlock.canBeCharged($$0)) {
            RespawnAnchorBlock.charge($$1, $$2, $$0);
            if (!$$3.getAbilities().instabuild) {
                $$6.shrink(1);
            }
            return InteractionResult.sidedSuccess($$1.isClientSide);
        }
        if ($$0.getValue(CHARGE) == 0) {
            return InteractionResult.PASS;
        }
        if (RespawnAnchorBlock.canSetSpawn($$1)) {
            ServerPlayer $$7;
            if (!($$1.isClientSide || ($$7 = (ServerPlayer)$$3).getRespawnDimension() == $$1.dimension() && $$2.equals($$7.getRespawnPosition()))) {
                $$7.setRespawnPosition($$1.dimension(), $$2, 0.0f, false, true);
                $$1.playSound(null, (double)$$2.getX() + 0.5, (double)$$2.getY() + 0.5, (double)$$2.getZ() + 0.5, SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, SoundSource.BLOCKS, 1.0f, 1.0f);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }
        if (!$$1.isClientSide) {
            this.explode($$0, $$1, $$2);
        }
        return InteractionResult.sidedSuccess($$1.isClientSide);
    }

    private static boolean isRespawnFuel(ItemStack $$0) {
        return $$0.is(Items.GLOWSTONE);
    }

    private static boolean canBeCharged(BlockState $$0) {
        return $$0.getValue(CHARGE) < 4;
    }

    private static boolean isWaterThatWouldFlow(BlockPos $$0, Level $$1) {
        FluidState $$2 = $$1.getFluidState($$0);
        if (!$$2.is(FluidTags.WATER)) {
            return false;
        }
        if ($$2.isSource()) {
            return true;
        }
        float $$3 = $$2.getAmount();
        if ($$3 < 2.0f) {
            return false;
        }
        FluidState $$4 = $$1.getFluidState((BlockPos)$$0.below());
        return !$$4.is(FluidTags.WATER);
    }

    private void explode(BlockState $$0, Level $$12, final BlockPos $$2) {
        $$12.removeBlock($$2, false);
        boolean $$3 = Direction.Plane.HORIZONTAL.stream().map($$2::relative).anyMatch($$1 -> RespawnAnchorBlock.isWaterThatWouldFlow($$1, $$12));
        final boolean $$4 = $$3 || $$12.getFluidState((BlockPos)$$2.above()).is(FluidTags.WATER);
        ExplosionDamageCalculator $$5 = new ExplosionDamageCalculator(){

            @Override
            public Optional<Float> getBlockExplosionResistance(Explosion $$0, BlockGetter $$1, BlockPos $$22, BlockState $$3, FluidState $$42) {
                if ($$22.equals($$2) && $$4) {
                    return Optional.of((Object)Float.valueOf((float)Blocks.WATER.getExplosionResistance()));
                }
                return super.getBlockExplosionResistance($$0, $$1, $$22, $$3, $$42);
            }
        };
        Vec3 $$6 = $$2.getCenter();
        $$12.explode(null, DamageSource.badRespawnPointExplosion($$6), $$5, $$6, 5.0f, true, Level.ExplosionInteraction.BLOCK);
    }

    public static boolean canSetSpawn(Level $$0) {
        return $$0.dimensionType().respawnAnchorWorks();
    }

    public static void charge(Level $$0, BlockPos $$1, BlockState $$2) {
        $$0.setBlock($$1, (BlockState)$$2.setValue(CHARGE, $$2.getValue(CHARGE) + 1), 3);
        $$0.playSound(null, (double)$$1.getX() + 0.5, (double)$$1.getY() + 0.5, (double)$$1.getZ() + 0.5, SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.BLOCKS, 1.0f, 1.0f);
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        if ($$0.getValue(CHARGE) == 0) {
            return;
        }
        if ($$3.nextInt(100) == 0) {
            $$1.playSound(null, (double)$$2.getX() + 0.5, (double)$$2.getY() + 0.5, (double)$$2.getZ() + 0.5, SoundEvents.RESPAWN_ANCHOR_AMBIENT, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        double $$4 = (double)$$2.getX() + 0.5 + (0.5 - $$3.nextDouble());
        double $$5 = (double)$$2.getY() + 1.0;
        double $$6 = (double)$$2.getZ() + 0.5 + (0.5 - $$3.nextDouble());
        double $$7 = (double)$$3.nextFloat() * 0.04;
        $$1.addParticle(ParticleTypes.REVERSE_PORTAL, $$4, $$5, $$6, 0.0, $$7, 0.0);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(CHARGE);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState $$0) {
        return true;
    }

    public static int getScaledChargeLevel(BlockState $$0, int $$1) {
        return Mth.floor((float)($$0.getValue(CHARGE) - 0) / 4.0f * (float)$$1);
    }

    @Override
    public int getAnalogOutputSignal(BlockState $$0, Level $$1, BlockPos $$2) {
        return RespawnAnchorBlock.getScaledChargeLevel($$0, 15);
    }

    public static Optional<Vec3> findStandUpPosition(EntityType<?> $$0, CollisionGetter $$1, BlockPos $$2) {
        Optional<Vec3> $$3 = RespawnAnchorBlock.findStandUpPosition($$0, $$1, $$2, true);
        if ($$3.isPresent()) {
            return $$3;
        }
        return RespawnAnchorBlock.findStandUpPosition($$0, $$1, $$2, false);
    }

    private static Optional<Vec3> findStandUpPosition(EntityType<?> $$0, CollisionGetter $$1, BlockPos $$2, boolean $$3) {
        BlockPos.MutableBlockPos $$4 = new BlockPos.MutableBlockPos();
        for (Vec3i $$5 : RESPAWN_OFFSETS) {
            $$4.set($$2).move($$5);
            Vec3 $$6 = DismountHelper.findSafeDismountLocation($$0, $$1, $$4, $$3);
            if ($$6 == null) continue;
            return Optional.of((Object)$$6);
        }
        return Optional.empty();
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        return false;
    }
}