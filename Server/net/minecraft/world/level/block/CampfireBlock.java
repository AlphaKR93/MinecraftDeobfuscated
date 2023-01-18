/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CampfireBlock
extends BaseEntityBlock
implements SimpleWaterloggedBlock {
    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 7.0, 16.0);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final BooleanProperty SIGNAL_FIRE = BlockStateProperties.SIGNAL_FIRE;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape VIRTUAL_FENCE_POST = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
    private static final int SMOKE_DISTANCE = 5;
    private final boolean spawnParticles;
    private final int fireDamage;

    public CampfireBlock(boolean $$0, int $$1, BlockBehaviour.Properties $$2) {
        super($$2);
        this.spawnParticles = $$0;
        this.fireDamage = $$1;
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(LIT, true)).setValue(SIGNAL_FIRE, false)).setValue(WATERLOGGED, false)).setValue(FACING, Direction.NORTH));
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        ItemStack $$8;
        CampfireBlockEntity $$7;
        Optional<CampfireCookingRecipe> $$9;
        BlockEntity $$6 = $$1.getBlockEntity($$2);
        if ($$6 instanceof CampfireBlockEntity && ($$9 = ($$7 = (CampfireBlockEntity)$$6).getCookableRecipe($$8 = $$3.getItemInHand($$4))).isPresent()) {
            if (!$$1.isClientSide && $$7.placeFood($$3, $$3.getAbilities().instabuild ? $$8.copy() : $$8, ((CampfireCookingRecipe)$$9.get()).getCookingTime())) {
                $$3.awardStat(Stats.INTERACT_WITH_CAMPFIRE);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3) {
        if ($$0.getValue(LIT).booleanValue() && $$3 instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)$$3)) {
            $$3.hurt(DamageSource.IN_FIRE, this.fireDamage);
        }
        super.entityInside($$0, $$1, $$2, $$3);
    }

    @Override
    public void onRemove(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$0.is($$3.getBlock())) {
            return;
        }
        BlockEntity $$5 = $$1.getBlockEntity($$2);
        if ($$5 instanceof CampfireBlockEntity) {
            Containers.dropContents($$1, $$2, ((CampfireBlockEntity)$$5).getItems());
        }
        super.onRemove($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        BlockPos $$2;
        Level $$1 = $$0.getLevel();
        boolean $$3 = $$1.getFluidState($$2 = $$0.getClickedPos()).getType() == Fluids.WATER;
        return (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(WATERLOGGED, $$3)).setValue(SIGNAL_FIRE, this.isSmokeSource($$1.getBlockState((BlockPos)$$2.below())))).setValue(LIT, !$$3)).setValue(FACING, $$0.getHorizontalDirection());
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$3.scheduleTick($$4, Fluids.WATER, Fluids.WATER.getTickDelay($$3));
        }
        if ($$1 == Direction.DOWN) {
            return (BlockState)$$0.setValue(SIGNAL_FIRE, this.isSmokeSource($$2));
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    private boolean isSmokeSource(BlockState $$0) {
        return $$0.is(Blocks.HAY_BLOCK);
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState $$0) {
        return RenderShape.MODEL;
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        if (!$$0.getValue(LIT).booleanValue()) {
            return;
        }
        if ($$3.nextInt(10) == 0) {
            $$1.playLocalSound((double)$$2.getX() + 0.5, (double)$$2.getY() + 0.5, (double)$$2.getZ() + 0.5, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5f + $$3.nextFloat(), $$3.nextFloat() * 0.7f + 0.6f, false);
        }
        if (this.spawnParticles && $$3.nextInt(5) == 0) {
            for (int $$4 = 0; $$4 < $$3.nextInt(1) + 1; ++$$4) {
                $$1.addParticle(ParticleTypes.LAVA, (double)$$2.getX() + 0.5, (double)$$2.getY() + 0.5, (double)$$2.getZ() + 0.5, $$3.nextFloat() / 2.0f, 5.0E-5, $$3.nextFloat() / 2.0f);
            }
        }
    }

    public static void dowse(@Nullable Entity $$0, LevelAccessor $$1, BlockPos $$2, BlockState $$3) {
        BlockEntity $$5;
        if ($$1.isClientSide()) {
            for (int $$4 = 0; $$4 < 20; ++$$4) {
                CampfireBlock.makeParticles((Level)$$1, $$2, $$3.getValue(SIGNAL_FIRE), true);
            }
        }
        if (($$5 = $$1.getBlockEntity($$2)) instanceof CampfireBlockEntity) {
            ((CampfireBlockEntity)$$5).dowse();
        }
        $$1.gameEvent($$0, GameEvent.BLOCK_CHANGE, $$2);
    }

    @Override
    public boolean placeLiquid(LevelAccessor $$0, BlockPos $$1, BlockState $$2, FluidState $$3) {
        if (!$$2.getValue(BlockStateProperties.WATERLOGGED).booleanValue() && $$3.getType() == Fluids.WATER) {
            boolean $$4 = $$2.getValue(LIT);
            if ($$4) {
                if (!$$0.isClientSide()) {
                    $$0.playSound(null, $$1, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
                CampfireBlock.dowse(null, $$0, $$1, $$2);
            }
            $$0.setBlock($$1, (BlockState)((BlockState)$$2.setValue(WATERLOGGED, true)).setValue(LIT, false), 3);
            $$0.scheduleTick($$1, $$3.getType(), $$3.getType().getTickDelay($$0));
            return true;
        }
        return false;
    }

    @Override
    public void onProjectileHit(Level $$0, BlockState $$1, BlockHitResult $$2, Projectile $$3) {
        BlockPos $$4 = $$2.getBlockPos();
        if (!$$0.isClientSide && $$3.isOnFire() && $$3.mayInteract($$0, $$4) && !$$1.getValue(LIT).booleanValue() && !$$1.getValue(WATERLOGGED).booleanValue()) {
            $$0.setBlock($$4, (BlockState)$$1.setValue(BlockStateProperties.LIT, true), 11);
        }
    }

    public static void makeParticles(Level $$0, BlockPos $$1, boolean $$2, boolean $$3) {
        RandomSource $$4 = $$0.getRandom();
        SimpleParticleType $$5 = $$2 ? ParticleTypes.CAMPFIRE_SIGNAL_SMOKE : ParticleTypes.CAMPFIRE_COSY_SMOKE;
        $$0.addAlwaysVisibleParticle($$5, true, (double)$$1.getX() + 0.5 + $$4.nextDouble() / 3.0 * (double)($$4.nextBoolean() ? 1 : -1), (double)$$1.getY() + $$4.nextDouble() + $$4.nextDouble(), (double)$$1.getZ() + 0.5 + $$4.nextDouble() / 3.0 * (double)($$4.nextBoolean() ? 1 : -1), 0.0, 0.07, 0.0);
        if ($$3) {
            $$0.addParticle(ParticleTypes.SMOKE, (double)$$1.getX() + 0.5 + $$4.nextDouble() / 4.0 * (double)($$4.nextBoolean() ? 1 : -1), (double)$$1.getY() + 0.4, (double)$$1.getZ() + 0.5 + $$4.nextDouble() / 4.0 * (double)($$4.nextBoolean() ? 1 : -1), 0.0, 0.005, 0.0);
        }
    }

    public static boolean isSmokeyPos(Level $$0, BlockPos $$1) {
        for (int $$2 = 1; $$2 <= 5; ++$$2) {
            Vec3i $$3 = $$1.below($$2);
            BlockState $$4 = $$0.getBlockState((BlockPos)$$3);
            if (CampfireBlock.isLitCampfire($$4)) {
                return true;
            }
            boolean $$5 = Shapes.joinIsNotEmpty(VIRTUAL_FENCE_POST, $$4.getCollisionShape($$0, $$1, CollisionContext.empty()), BooleanOp.AND);
            if (!$$5) continue;
            BlockState $$6 = $$0.getBlockState((BlockPos)((BlockPos)$$3).below());
            return CampfireBlock.isLitCampfire($$6);
        }
        return false;
    }

    public static boolean isLitCampfire(BlockState $$0) {
        return $$0.hasProperty(LIT) && $$0.is(BlockTags.CAMPFIRES) && $$0.getValue(LIT) != false;
    }

    @Override
    public FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }

    @Override
    public BlockState rotate(BlockState $$0, Rotation $$1) {
        return (BlockState)$$0.setValue(FACING, $$1.rotate($$0.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState $$0, Mirror $$1) {
        return $$0.rotate($$1.getRotation($$0.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(LIT, SIGNAL_FIRE, WATERLOGGED, FACING);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new CampfireBlockEntity($$0, $$1);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$0, BlockState $$1, BlockEntityType<T> $$2) {
        if ($$0.isClientSide) {
            if ($$1.getValue(LIT).booleanValue()) {
                return CampfireBlock.createTickerHelper($$2, BlockEntityType.CAMPFIRE, CampfireBlockEntity::particleTick);
            }
        } else {
            if ($$1.getValue(LIT).booleanValue()) {
                return CampfireBlock.createTickerHelper($$2, BlockEntityType.CAMPFIRE, CampfireBlockEntity::cookTick);
            }
            return CampfireBlock.createTickerHelper($$2, BlockEntityType.CAMPFIRE, CampfireBlockEntity::cooldownTick);
        }
        return null;
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        return false;
    }

    public static boolean canLight(BlockState $$02) {
        return $$02.is(BlockTags.CAMPFIRES, (Predicate<BlockBehaviour.BlockStateBase>)((Predicate)$$0 -> $$0.hasProperty(WATERLOGGED) && $$0.hasProperty(LIT))) && $$02.getValue(WATERLOGGED) == false && $$02.getValue(LIT) == false;
    }
}