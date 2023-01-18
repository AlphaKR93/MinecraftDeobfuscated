/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Collections
 *  java.util.List
 *  java.util.Optional
 */
package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LiquidBlock
extends Block
implements BucketPickup {
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL;
    protected final FlowingFluid fluid;
    private final List<FluidState> stateCache;
    public static final VoxelShape STABLE_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
    public static final ImmutableList<Direction> POSSIBLE_FLOW_DIRECTIONS = ImmutableList.of((Object)Direction.DOWN, (Object)Direction.SOUTH, (Object)Direction.NORTH, (Object)Direction.EAST, (Object)Direction.WEST);

    protected LiquidBlock(FlowingFluid $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.fluid = $$0;
        this.stateCache = Lists.newArrayList();
        this.stateCache.add((Object)$$0.getSource(false));
        for (int $$2 = 1; $$2 < 8; ++$$2) {
            this.stateCache.add((Object)$$0.getFlowing(8 - $$2, false));
        }
        this.stateCache.add((Object)$$0.getFlowing(8, true));
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LEVEL, 0));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        if ($$3.isAbove(STABLE_SHAPE, $$2, true) && $$0.getValue(LEVEL) == 0 && $$3.canStandOnFluid($$1.getFluidState((BlockPos)$$2.above()), $$0.getFluidState())) {
            return STABLE_SHAPE;
        }
        return Shapes.empty();
    }

    @Override
    public boolean isRandomlyTicking(BlockState $$0) {
        return $$0.getFluidState().isRandomlyTicking();
    }

    @Override
    public void randomTick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        $$0.getFluidState().randomTick($$1, $$2, $$3);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return false;
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        return !this.fluid.is(FluidTags.LAVA);
    }

    @Override
    public FluidState getFluidState(BlockState $$0) {
        int $$1 = $$0.getValue(LEVEL);
        return (FluidState)this.stateCache.get(Math.min((int)$$1, (int)8));
    }

    @Override
    public boolean skipRendering(BlockState $$0, BlockState $$1, Direction $$2) {
        return $$1.getFluidState().getType().isSame(this.fluid);
    }

    @Override
    public RenderShape getRenderShape(BlockState $$0) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public List<ItemStack> getDrops(BlockState $$0, LootContext.Builder $$1) {
        return Collections.emptyList();
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return Shapes.empty();
    }

    @Override
    public void onPlace(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if (this.shouldSpreadLiquid($$1, $$2, $$0)) {
            $$1.scheduleTick($$2, $$0.getFluidState().getType(), this.fluid.getTickDelay($$1));
        }
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$0.getFluidState().isSource() || $$2.getFluidState().isSource()) {
            $$3.scheduleTick($$4, $$0.getFluidState().getType(), this.fluid.getTickDelay($$3));
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public void neighborChanged(BlockState $$0, Level $$1, BlockPos $$2, Block $$3, BlockPos $$4, boolean $$5) {
        if (this.shouldSpreadLiquid($$1, $$2, $$0)) {
            $$1.scheduleTick($$2, $$0.getFluidState().getType(), this.fluid.getTickDelay($$1));
        }
    }

    private boolean shouldSpreadLiquid(Level $$0, BlockPos $$1, BlockState $$2) {
        if (this.fluid.is(FluidTags.LAVA)) {
            boolean $$3 = $$0.getBlockState((BlockPos)$$1.below()).is(Blocks.SOUL_SOIL);
            for (Direction $$4 : POSSIBLE_FLOW_DIRECTIONS) {
                Vec3i $$5 = $$1.relative($$4.getOpposite());
                if ($$0.getFluidState((BlockPos)$$5).is(FluidTags.WATER)) {
                    Block $$6 = $$0.getFluidState($$1).isSource() ? Blocks.OBSIDIAN : Blocks.COBBLESTONE;
                    $$0.setBlockAndUpdate($$1, $$6.defaultBlockState());
                    this.fizz($$0, $$1);
                    return false;
                }
                if (!$$3 || !$$0.getBlockState((BlockPos)$$5).is(Blocks.BLUE_ICE)) continue;
                $$0.setBlockAndUpdate($$1, Blocks.BASALT.defaultBlockState());
                this.fizz($$0, $$1);
                return false;
            }
        }
        return true;
    }

    private void fizz(LevelAccessor $$0, BlockPos $$1) {
        $$0.levelEvent(1501, $$1, 0);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(LEVEL);
    }

    @Override
    public ItemStack pickupBlock(LevelAccessor $$0, BlockPos $$1, BlockState $$2) {
        if ($$2.getValue(LEVEL) == 0) {
            $$0.setBlock($$1, Blocks.AIR.defaultBlockState(), 11);
            return new ItemStack(this.fluid.getBucket());
        }
        return ItemStack.EMPTY;
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return this.fluid.getPickupSound();
    }
}