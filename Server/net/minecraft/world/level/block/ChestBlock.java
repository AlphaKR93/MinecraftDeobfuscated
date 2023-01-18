/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.floats.Float2FloatFunction
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Optional
 *  java.util.function.BiPredicate
 *  java.util.function.Function
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ChestBlock
extends AbstractChestBlock<ChestBlockEntity>
implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<ChestType> TYPE = BlockStateProperties.CHEST_TYPE;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final int EVENT_SET_OPEN_COUNT = 1;
    protected static final int AABB_OFFSET = 1;
    protected static final int AABB_HEIGHT = 14;
    protected static final VoxelShape NORTH_AABB = Block.box(1.0, 0.0, 0.0, 15.0, 14.0, 15.0);
    protected static final VoxelShape SOUTH_AABB = Block.box(1.0, 0.0, 1.0, 15.0, 14.0, 16.0);
    protected static final VoxelShape WEST_AABB = Block.box(0.0, 0.0, 1.0, 15.0, 14.0, 15.0);
    protected static final VoxelShape EAST_AABB = Block.box(1.0, 0.0, 1.0, 16.0, 14.0, 15.0);
    protected static final VoxelShape AABB = Block.box(1.0, 0.0, 1.0, 15.0, 14.0, 15.0);
    private static final DoubleBlockCombiner.Combiner<ChestBlockEntity, Optional<Container>> CHEST_COMBINER = new DoubleBlockCombiner.Combiner<ChestBlockEntity, Optional<Container>>(){

        @Override
        public Optional<Container> acceptDouble(ChestBlockEntity $$0, ChestBlockEntity $$1) {
            return Optional.of((Object)new CompoundContainer($$0, $$1));
        }

        @Override
        public Optional<Container> acceptSingle(ChestBlockEntity $$0) {
            return Optional.of((Object)$$0);
        }

        @Override
        public Optional<Container> acceptNone() {
            return Optional.empty();
        }
    };
    private static final DoubleBlockCombiner.Combiner<ChestBlockEntity, Optional<MenuProvider>> MENU_PROVIDER_COMBINER = new DoubleBlockCombiner.Combiner<ChestBlockEntity, Optional<MenuProvider>>(){

        @Override
        public Optional<MenuProvider> acceptDouble(final ChestBlockEntity $$0, final ChestBlockEntity $$1) {
            final CompoundContainer $$2 = new CompoundContainer($$0, $$1);
            return Optional.of((Object)new MenuProvider(){

                @Override
                @Nullable
                public AbstractContainerMenu createMenu(int $$02, Inventory $$12, Player $$22) {
                    if ($$0.canOpen($$22) && $$1.canOpen($$22)) {
                        $$0.unpackLootTable($$12.player);
                        $$1.unpackLootTable($$12.player);
                        return ChestMenu.sixRows($$02, $$12, $$2);
                    }
                    return null;
                }

                @Override
                public Component getDisplayName() {
                    if ($$0.hasCustomName()) {
                        return $$0.getDisplayName();
                    }
                    if ($$1.hasCustomName()) {
                        return $$1.getDisplayName();
                    }
                    return Component.translatable("container.chestDouble");
                }
            });
        }

        @Override
        public Optional<MenuProvider> acceptSingle(ChestBlockEntity $$0) {
            return Optional.of((Object)$$0);
        }

        @Override
        public Optional<MenuProvider> acceptNone() {
            return Optional.empty();
        }
    };

    protected ChestBlock(BlockBehaviour.Properties $$0, Supplier<BlockEntityType<? extends ChestBlockEntity>> $$1) {
        super($$0, $$1);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(TYPE, ChestType.SINGLE)).setValue(WATERLOGGED, false));
    }

    public static DoubleBlockCombiner.BlockType getBlockType(BlockState $$0) {
        ChestType $$1 = $$0.getValue(TYPE);
        if ($$1 == ChestType.SINGLE) {
            return DoubleBlockCombiner.BlockType.SINGLE;
        }
        if ($$1 == ChestType.RIGHT) {
            return DoubleBlockCombiner.BlockType.FIRST;
        }
        return DoubleBlockCombiner.BlockType.SECOND;
    }

    @Override
    public RenderShape getRenderShape(BlockState $$0) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            $$3.scheduleTick($$4, Fluids.WATER, Fluids.WATER.getTickDelay($$3));
        }
        if ($$2.is(this) && $$1.getAxis().isHorizontal()) {
            ChestType $$6 = $$2.getValue(TYPE);
            if ($$0.getValue(TYPE) == ChestType.SINGLE && $$6 != ChestType.SINGLE && $$0.getValue(FACING) == $$2.getValue(FACING) && ChestBlock.getConnectedDirection($$2) == $$1.getOpposite()) {
                return (BlockState)$$0.setValue(TYPE, $$6.getOpposite());
            }
        } else if (ChestBlock.getConnectedDirection($$0) == $$1) {
            return (BlockState)$$0.setValue(TYPE, ChestType.SINGLE);
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        if ($$0.getValue(TYPE) == ChestType.SINGLE) {
            return AABB;
        }
        switch (ChestBlock.getConnectedDirection($$0)) {
            default: {
                return NORTH_AABB;
            }
            case SOUTH: {
                return SOUTH_AABB;
            }
            case WEST: {
                return WEST_AABB;
            }
            case EAST: 
        }
        return EAST_AABB;
    }

    public static Direction getConnectedDirection(BlockState $$0) {
        Direction $$1 = $$0.getValue(FACING);
        return $$0.getValue(TYPE) == ChestType.LEFT ? $$1.getClockWise() : $$1.getCounterClockWise();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        Direction $$6;
        ChestType $$1 = ChestType.SINGLE;
        Direction $$2 = $$0.getHorizontalDirection().getOpposite();
        FluidState $$3 = $$0.getLevel().getFluidState($$0.getClickedPos());
        boolean $$4 = $$0.isSecondaryUseActive();
        Direction $$5 = $$0.getClickedFace();
        if ($$5.getAxis().isHorizontal() && $$4 && ($$6 = this.candidatePartnerFacing($$0, $$5.getOpposite())) != null && $$6.getAxis() != $$5.getAxis()) {
            $$2 = $$6;
            ChestType chestType = $$1 = $$2.getCounterClockWise() == $$5.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
        }
        if ($$1 == ChestType.SINGLE && !$$4) {
            if ($$2 == this.candidatePartnerFacing($$0, $$2.getClockWise())) {
                $$1 = ChestType.LEFT;
            } else if ($$2 == this.candidatePartnerFacing($$0, $$2.getCounterClockWise())) {
                $$1 = ChestType.RIGHT;
            }
        }
        return (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, $$2)).setValue(TYPE, $$1)).setValue(WATERLOGGED, $$3.getType() == Fluids.WATER);
    }

    @Override
    public FluidState getFluidState(BlockState $$0) {
        if ($$0.getValue(WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getSource(false);
        }
        return super.getFluidState($$0);
    }

    @Nullable
    private Direction candidatePartnerFacing(BlockPlaceContext $$0, Direction $$1) {
        BlockState $$2 = $$0.getLevel().getBlockState((BlockPos)$$0.getClickedPos().relative($$1));
        return $$2.is(this) && $$2.getValue(TYPE) == ChestType.SINGLE ? $$2.getValue(FACING) : null;
    }

    @Override
    public void setPlacedBy(Level $$0, BlockPos $$1, BlockState $$2, LivingEntity $$3, ItemStack $$4) {
        BlockEntity $$5;
        if ($$4.hasCustomHoverName() && ($$5 = $$0.getBlockEntity($$1)) instanceof ChestBlockEntity) {
            ((ChestBlockEntity)$$5).setCustomName($$4.getHoverName());
        }
    }

    @Override
    public void onRemove(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$0.is($$3.getBlock())) {
            return;
        }
        BlockEntity $$5 = $$1.getBlockEntity($$2);
        if ($$5 instanceof Container) {
            Containers.dropContents($$1, $$2, (Container)((Object)$$5));
            $$1.updateNeighbourForOutputSignal($$2, this);
        }
        super.onRemove($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        if ($$1.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        MenuProvider $$6 = this.getMenuProvider($$0, $$1, $$2);
        if ($$6 != null) {
            $$3.openMenu($$6);
            $$3.awardStat(this.getOpenChestStat());
            PiglinAi.angerNearbyPiglins($$3, true);
        }
        return InteractionResult.CONSUME;
    }

    protected Stat<ResourceLocation> getOpenChestStat() {
        return Stats.CUSTOM.get(Stats.OPEN_CHEST);
    }

    public BlockEntityType<? extends ChestBlockEntity> blockEntityType() {
        return (BlockEntityType)this.blockEntityType.get();
    }

    @Nullable
    public static Container getContainer(ChestBlock $$0, BlockState $$1, Level $$2, BlockPos $$3, boolean $$4) {
        return (Container)$$0.combine($$1, $$2, $$3, $$4).apply(CHEST_COMBINER).orElse(null);
    }

    @Override
    public DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> combine(BlockState $$02, Level $$12, BlockPos $$2, boolean $$3) {
        BiPredicate $$5;
        if ($$3) {
            BiPredicate $$4 = ($$0, $$1) -> false;
        } else {
            $$5 = ChestBlock::isChestBlockedAt;
        }
        return DoubleBlockCombiner.combineWithNeigbour((BlockEntityType)this.blockEntityType.get(), (Function<BlockState, DoubleBlockCombiner.BlockType>)((Function)ChestBlock::getBlockType), (Function<BlockState, Direction>)((Function)ChestBlock::getConnectedDirection), FACING, $$02, $$12, $$2, (BiPredicate<LevelAccessor, BlockPos>)$$5);
    }

    @Override
    @Nullable
    public MenuProvider getMenuProvider(BlockState $$0, Level $$1, BlockPos $$2) {
        return (MenuProvider)this.combine($$0, $$1, $$2, false).apply(MENU_PROVIDER_COMBINER).orElse(null);
    }

    public static DoubleBlockCombiner.Combiner<ChestBlockEntity, Float2FloatFunction> opennessCombiner(final LidBlockEntity $$0) {
        return new DoubleBlockCombiner.Combiner<ChestBlockEntity, Float2FloatFunction>(){

            @Override
            public Float2FloatFunction acceptDouble(ChestBlockEntity $$02, ChestBlockEntity $$1) {
                return $$2 -> Math.max((float)$$02.getOpenNess($$2), (float)$$1.getOpenNess($$2));
            }

            @Override
            public Float2FloatFunction acceptSingle(ChestBlockEntity $$02) {
                return $$02::getOpenNess;
            }

            @Override
            public Float2FloatFunction acceptNone() {
                return $$0::getOpenNess;
            }
        };
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new ChestBlockEntity($$0, $$1);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$0, BlockState $$1, BlockEntityType<T> $$2) {
        return $$0.isClientSide ? ChestBlock.createTickerHelper($$2, this.blockEntityType(), ChestBlockEntity::lidAnimateTick) : null;
    }

    public static boolean isChestBlockedAt(LevelAccessor $$0, BlockPos $$1) {
        return ChestBlock.isBlockedChestByBlock($$0, $$1) || ChestBlock.isCatSittingOnChest($$0, $$1);
    }

    private static boolean isBlockedChestByBlock(BlockGetter $$0, BlockPos $$1) {
        Vec3i $$2 = $$1.above();
        return $$0.getBlockState((BlockPos)$$2).isRedstoneConductor($$0, (BlockPos)$$2);
    }

    private static boolean isCatSittingOnChest(LevelAccessor $$0, BlockPos $$1) {
        List $$2 = $$0.getEntitiesOfClass(Cat.class, new AABB($$1.getX(), $$1.getY() + 1, $$1.getZ(), $$1.getX() + 1, $$1.getY() + 2, $$1.getZ() + 1));
        if (!$$2.isEmpty()) {
            for (Cat $$3 : $$2) {
                if (!$$3.isInSittingPose()) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState $$0) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState $$0, Level $$1, BlockPos $$2) {
        return AbstractContainerMenu.getRedstoneSignalFromContainer(ChestBlock.getContainer(this, $$0, $$1, $$2, false));
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
        $$0.add(FACING, TYPE, WATERLOGGED);
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        return false;
    }

    @Override
    public void tick(BlockState $$0, ServerLevel $$1, BlockPos $$2, RandomSource $$3) {
        BlockEntity $$4 = $$1.getBlockEntity($$2);
        if ($$4 instanceof ChestBlockEntity) {
            ((ChestBlockEntity)$$4).recheckOpen();
        }
    }
}