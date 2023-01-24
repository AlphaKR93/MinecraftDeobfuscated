/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 *  net.minecraft.world.item.Item
 */
package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FlowerPotBlock
extends Block {
    private static final Map<Block, Block> POTTED_BY_CONTENT = Maps.newHashMap();
    public static final float AABB_SIZE = 3.0f;
    protected static final VoxelShape SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);
    private final Block content;

    public FlowerPotBlock(Block $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.content = $$0;
        POTTED_BY_CONTENT.put((Object)$$0, (Object)this);
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
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        boolean $$10;
        ItemStack $$6 = $$3.getItemInHand($$4);
        Item $$7 = $$6.getItem();
        BlockState $$8 = ($$7 instanceof BlockItem ? (Block)POTTED_BY_CONTENT.getOrDefault((Object)((BlockItem)$$7).getBlock(), (Object)Blocks.AIR) : Blocks.AIR).defaultBlockState();
        boolean $$9 = $$8.is(Blocks.AIR);
        if ($$9 != ($$10 = this.isEmpty())) {
            if ($$10) {
                $$1.setBlock($$2, $$8, 3);
                $$3.awardStat(Stats.POT_FLOWER);
                if (!$$3.getAbilities().instabuild) {
                    $$6.shrink(1);
                }
            } else {
                ItemStack $$11 = new ItemStack(this.content);
                if ($$6.isEmpty()) {
                    $$3.setItemInHand($$4, $$11);
                } else if (!$$3.addItem($$11)) {
                    $$3.drop($$11, false);
                }
                $$1.setBlock($$2, Blocks.FLOWER_POT.defaultBlockState(), 3);
            }
            $$1.gameEvent($$3, GameEvent.BLOCK_CHANGE, $$2);
            return InteractionResult.sidedSuccess($$1.isClientSide);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter $$0, BlockPos $$1, BlockState $$2) {
        if (this.isEmpty()) {
            return super.getCloneItemStack($$0, $$1, $$2);
        }
        return new ItemStack(this.content);
    }

    private boolean isEmpty() {
        return this.content == Blocks.AIR;
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        if ($$1 == Direction.DOWN && !$$0.canSurvive($$3, $$4)) {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    public Block getContent() {
        return this.content;
    }

    @Override
    public boolean isPathfindable(BlockState $$0, BlockGetter $$1, BlockPos $$2, PathComputationType $$3) {
        return false;
    }
}