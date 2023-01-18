/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMaps
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.function.Predicate
 *  java.util.function.ToIntFunction
 */
package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CandleBlock
extends AbstractCandleBlock
implements SimpleWaterloggedBlock {
    public static final int MIN_CANDLES = 1;
    public static final int MAX_CANDLES = 4;
    public static final IntegerProperty CANDLES = BlockStateProperties.CANDLES;
    public static final BooleanProperty LIT = AbstractCandleBlock.LIT;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final ToIntFunction<BlockState> LIGHT_EMISSION = $$0 -> $$0.getValue(LIT) != false ? 3 * $$0.getValue(CANDLES) : 0;
    private static final Int2ObjectMap<List<Vec3>> PARTICLE_OFFSETS = (Int2ObjectMap)Util.make(() -> {
        Int2ObjectOpenHashMap $$0 = new Int2ObjectOpenHashMap();
        $$0.defaultReturnValue((Object)ImmutableList.of());
        $$0.put(1, (Object)ImmutableList.of((Object)new Vec3(0.5, 0.5, 0.5)));
        $$0.put(2, (Object)ImmutableList.of((Object)new Vec3(0.375, 0.44, 0.5), (Object)new Vec3(0.625, 0.5, 0.44)));
        $$0.put(3, (Object)ImmutableList.of((Object)new Vec3(0.5, 0.313, 0.625), (Object)new Vec3(0.375, 0.44, 0.5), (Object)new Vec3(0.56, 0.5, 0.44)));
        $$0.put(4, (Object)ImmutableList.of((Object)new Vec3(0.44, 0.313, 0.56), (Object)new Vec3(0.625, 0.44, 0.56), (Object)new Vec3(0.375, 0.44, 0.375), (Object)new Vec3(0.56, 0.5, 0.375)));
        return Int2ObjectMaps.unmodifiable((Int2ObjectMap)$$0);
    });
    private static final VoxelShape ONE_AABB = Block.box(7.0, 0.0, 7.0, 9.0, 6.0, 9.0);
    private static final VoxelShape TWO_AABB = Block.box(5.0, 0.0, 6.0, 11.0, 6.0, 9.0);
    private static final VoxelShape THREE_AABB = Block.box(5.0, 0.0, 6.0, 10.0, 6.0, 11.0);
    private static final VoxelShape FOUR_AABB = Block.box(5.0, 0.0, 5.0, 11.0, 6.0, 10.0);

    public CandleBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(CANDLES, 1)).setValue(LIT, false)).setValue(WATERLOGGED, false));
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        if ($$3.getAbilities().mayBuild && $$3.getItemInHand($$4).isEmpty() && $$0.getValue(LIT).booleanValue()) {
            CandleBlock.extinguish($$3, $$0, $$1, $$2);
            return InteractionResult.sidedSuccess($$1.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean canBeReplaced(BlockState $$0, BlockPlaceContext $$1) {
        if (!$$1.isSecondaryUseActive() && $$1.getItemInHand().getItem() == this.asItem() && $$0.getValue(CANDLES) < 4) {
            return true;
        }
        return super.canBeReplaced($$0, $$1);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        BlockState $$1 = $$0.getLevel().getBlockState($$0.getClickedPos());
        if ($$1.is(this)) {
            return (BlockState)$$1.cycle(CANDLES);
        }
        FluidState $$2 = $$0.getLevel().getFluidState($$0.getClickedPos());
        boolean $$3 = $$2.getType() == Fluids.WATER;
        return (BlockState)super.getStateForPlacement($$0).setValue(WATERLOGGED, $$3);
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
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        switch ($$0.getValue(CANDLES)) {
            default: {
                return ONE_AABB;
            }
            case 2: {
                return TWO_AABB;
            }
            case 3: {
                return THREE_AABB;
            }
            case 4: 
        }
        return FOUR_AABB;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(CANDLES, LIT, WATERLOGGED);
    }

    @Override
    public boolean placeLiquid(LevelAccessor $$0, BlockPos $$1, BlockState $$2, FluidState $$3) {
        if ($$2.getValue(WATERLOGGED).booleanValue() || $$3.getType() != Fluids.WATER) {
            return false;
        }
        BlockState $$4 = (BlockState)$$2.setValue(WATERLOGGED, true);
        if ($$2.getValue(LIT).booleanValue()) {
            CandleBlock.extinguish(null, $$4, $$0, $$1);
        } else {
            $$0.setBlock($$1, $$4, 3);
        }
        $$0.scheduleTick($$1, $$3.getType(), $$3.getType().getTickDelay($$0));
        return true;
    }

    public static boolean canLight(BlockState $$02) {
        return $$02.is(BlockTags.CANDLES, (Predicate<BlockBehaviour.BlockStateBase>)((Predicate)$$0 -> $$0.hasProperty(LIT) && $$0.hasProperty(WATERLOGGED))) && $$02.getValue(LIT) == false && $$02.getValue(WATERLOGGED) == false;
    }

    @Override
    protected Iterable<Vec3> getParticleOffsets(BlockState $$0) {
        return (Iterable)PARTICLE_OFFSETS.get($$0.getValue(CANDLES).intValue());
    }

    @Override
    protected boolean canBeLit(BlockState $$0) {
        return $$0.getValue(WATERLOGGED) == false && super.canBeLit($$0);
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        return Block.canSupportCenter($$1, (BlockPos)$$2.below(), Direction.UP);
    }
}