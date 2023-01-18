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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CandleCakeBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CakeBlock
extends Block {
    public static final int MAX_BITES = 6;
    public static final IntegerProperty BITES = BlockStateProperties.BITES;
    public static final int FULL_CAKE_SIGNAL = CakeBlock.getOutputSignal(0);
    protected static final float AABB_OFFSET = 1.0f;
    protected static final float AABB_SIZE_PER_BITE = 2.0f;
    protected static final VoxelShape[] SHAPE_BY_BITE = new VoxelShape[]{Block.box(1.0, 0.0, 1.0, 15.0, 8.0, 15.0), Block.box(3.0, 0.0, 1.0, 15.0, 8.0, 15.0), Block.box(5.0, 0.0, 1.0, 15.0, 8.0, 15.0), Block.box(7.0, 0.0, 1.0, 15.0, 8.0, 15.0), Block.box(9.0, 0.0, 1.0, 15.0, 8.0, 15.0), Block.box(11.0, 0.0, 1.0, 15.0, 8.0, 15.0), Block.box(13.0, 0.0, 1.0, 15.0, 8.0, 15.0)};

    protected CakeBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(BITES, 0));
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return SHAPE_BY_BITE[$$0.getValue(BITES)];
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        Block $$8;
        ItemStack $$6 = $$3.getItemInHand($$4);
        Item $$7 = $$6.getItem();
        if ($$6.is(ItemTags.CANDLES) && $$0.getValue(BITES) == 0 && ($$8 = Block.byItem($$7)) instanceof CandleBlock) {
            if (!$$3.isCreative()) {
                $$6.shrink(1);
            }
            $$1.playSound(null, $$2, SoundEvents.CAKE_ADD_CANDLE, SoundSource.BLOCKS, 1.0f, 1.0f);
            $$1.setBlockAndUpdate($$2, CandleCakeBlock.byCandle($$8));
            $$1.gameEvent($$3, GameEvent.BLOCK_CHANGE, $$2);
            $$3.awardStat(Stats.ITEM_USED.get($$7));
            return InteractionResult.SUCCESS;
        }
        if ($$1.isClientSide) {
            if (CakeBlock.eat($$1, $$2, $$0, $$3).consumesAction()) {
                return InteractionResult.SUCCESS;
            }
            if ($$6.isEmpty()) {
                return InteractionResult.CONSUME;
            }
        }
        return CakeBlock.eat($$1, $$2, $$0, $$3);
    }

    protected static InteractionResult eat(LevelAccessor $$0, BlockPos $$1, BlockState $$2, Player $$3) {
        if (!$$3.canEat(false)) {
            return InteractionResult.PASS;
        }
        $$3.awardStat(Stats.EAT_CAKE_SLICE);
        $$3.getFoodData().eat(2, 0.1f);
        int $$4 = $$2.getValue(BITES);
        $$0.gameEvent((Entity)$$3, GameEvent.EAT, $$1);
        if ($$4 < 6) {
            $$0.setBlock($$1, (BlockState)$$2.setValue(BITES, $$4 + 1), 3);
        } else {
            $$0.removeBlock($$1, false);
            $$0.gameEvent((Entity)$$3, GameEvent.BLOCK_DESTROY, $$1);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$1 == Direction.DOWN && !$$0.canSurvive($$3, $$4)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public boolean canSurvive(BlockState $$0, LevelReader $$1, BlockPos $$2) {
        return $$1.getBlockState((BlockPos)$$2.below()).getMaterial().isSolid();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(BITES);
    }

    @Override
    public int getAnalogOutputSignal(BlockState $$0, Level $$1, BlockPos $$2) {
        return CakeBlock.getOutputSignal($$0.getValue(BITES));
    }

    public static int getOutputSignal(int $$0) {
        return (7 - $$0) * 2;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState $$0) {
        return true;
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        return false;
    }
}