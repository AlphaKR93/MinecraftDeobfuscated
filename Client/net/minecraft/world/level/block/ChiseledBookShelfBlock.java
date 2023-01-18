/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Boolean
 *  java.lang.IncompatibleClassChangeError
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Optional
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraft.world.level.block;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class ChiseledBookShelfBlock
extends BaseEntityBlock {
    private static final int MAX_BOOKS_IN_STORAGE = 6;
    public static final int BOOKS_PER_ROW = 3;
    public static final List<BooleanProperty> SLOT_OCCUPIED_PROPERTIES = List.of((Object)BlockStateProperties.CHISELED_BOOKSHELF_SLOT_0_OCCUPIED, (Object)BlockStateProperties.CHISELED_BOOKSHELF_SLOT_1_OCCUPIED, (Object)BlockStateProperties.CHISELED_BOOKSHELF_SLOT_2_OCCUPIED, (Object)BlockStateProperties.CHISELED_BOOKSHELF_SLOT_3_OCCUPIED, (Object)BlockStateProperties.CHISELED_BOOKSHELF_SLOT_4_OCCUPIED, (Object)BlockStateProperties.CHISELED_BOOKSHELF_SLOT_5_OCCUPIED);

    public ChiseledBookShelfBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        BlockState $$1 = (BlockState)((BlockState)this.stateDefinition.any()).setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH);
        for (BooleanProperty $$2 : SLOT_OCCUPIED_PROPERTIES) {
            $$1 = (BlockState)$$1.setValue($$2, false);
        }
        this.registerDefaultState($$1);
    }

    @Override
    public RenderShape getRenderShape(BlockState $$0) {
        return RenderShape.MODEL;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        void $$7;
        BlockEntity blockEntity = $$1.getBlockEntity($$2);
        if (!(blockEntity instanceof ChiseledBookShelfBlockEntity)) {
            return InteractionResult.PASS;
        }
        ChiseledBookShelfBlockEntity $$6 = (ChiseledBookShelfBlockEntity)blockEntity;
        Optional<Vec2> $$8 = ChiseledBookShelfBlock.getRelativeHitCoordinatesForBlockFace($$5, $$0.getValue(HorizontalDirectionalBlock.FACING));
        if ($$8.isEmpty()) {
            return InteractionResult.PASS;
        }
        int $$9 = ChiseledBookShelfBlock.getHitSlot((Vec2)$$8.get());
        if (((Boolean)$$0.getValue((Property)SLOT_OCCUPIED_PROPERTIES.get($$9))).booleanValue()) {
            ChiseledBookShelfBlock.removeBook($$1, $$2, $$3, (ChiseledBookShelfBlockEntity)$$7, $$9);
            return InteractionResult.sidedSuccess($$1.isClientSide);
        }
        ItemStack $$10 = $$3.getItemInHand($$4);
        if ($$10.is(ItemTags.BOOKSHELF_BOOKS)) {
            ChiseledBookShelfBlock.addBook($$1, $$2, $$3, (ChiseledBookShelfBlockEntity)$$7, $$10, $$9);
            return InteractionResult.sidedSuccess($$1.isClientSide);
        }
        return InteractionResult.CONSUME;
    }

    private static Optional<Vec2> getRelativeHitCoordinatesForBlockFace(BlockHitResult $$0, Direction $$1) {
        Direction $$2 = $$0.getDirection();
        if ($$1 != $$2) {
            return Optional.empty();
        }
        Vec3i $$3 = $$0.getBlockPos().relative($$2);
        Vec3 $$4 = $$0.getLocation().subtract($$3.getX(), $$3.getY(), $$3.getZ());
        double $$5 = $$4.x();
        double $$6 = $$4.y();
        double $$7 = $$4.z();
        return switch ($$2) {
            default -> throw new IncompatibleClassChangeError();
            case Direction.NORTH -> Optional.of((Object)new Vec2((float)(1.0 - $$5), (float)$$6));
            case Direction.SOUTH -> Optional.of((Object)new Vec2((float)$$5, (float)$$6));
            case Direction.WEST -> Optional.of((Object)new Vec2((float)$$7, (float)$$6));
            case Direction.EAST -> Optional.of((Object)new Vec2((float)(1.0 - $$7), (float)$$6));
            case Direction.DOWN, Direction.UP -> Optional.empty();
        };
    }

    private static int getHitSlot(Vec2 $$0) {
        int $$1 = $$0.y >= 0.5f ? 0 : 1;
        int $$2 = ChiseledBookShelfBlock.getSection($$0.x);
        return $$2 + $$1 * 3;
    }

    private static int getSection(float $$0) {
        float $$1 = 0.0625f;
        float $$2 = 0.375f;
        if ($$0 < 0.375f) {
            return 0;
        }
        float $$3 = 0.6875f;
        if ($$0 < 0.6875f) {
            return 1;
        }
        return 2;
    }

    private static void addBook(Level $$0, BlockPos $$1, Player $$2, ChiseledBookShelfBlockEntity $$3, ItemStack $$4, int $$5) {
        if ($$0.isClientSide) {
            return;
        }
        $$2.awardStat(Stats.ITEM_USED.get($$4.getItem()));
        SoundEvent $$6 = $$4.is(Items.ENCHANTED_BOOK) ? SoundEvents.CHISELED_BOOKSHELF_INSERT_ENCHANTED : SoundEvents.CHISELED_BOOKSHELF_INSERT;
        $$3.setItem($$5, $$4.split(1));
        $$0.playSound(null, $$1, $$6, SoundSource.BLOCKS, 1.0f, 1.0f);
        if ($$2.isCreative()) {
            $$4.grow(1);
        }
        $$0.gameEvent($$2, GameEvent.BLOCK_CHANGE, $$1);
    }

    private static void removeBook(Level $$0, BlockPos $$1, Player $$2, ChiseledBookShelfBlockEntity $$3, int $$4) {
        if ($$0.isClientSide) {
            return;
        }
        ItemStack $$5 = $$3.removeItem($$4, 1);
        SoundEvent $$6 = $$5.is(Items.ENCHANTED_BOOK) ? SoundEvents.CHISELED_BOOKSHELF_PICKUP_ENCHANTED : SoundEvents.CHISELED_BOOKSHELF_PICKUP;
        $$0.playSound(null, $$1, $$6, SoundSource.BLOCKS, 1.0f, 1.0f);
        if (!$$2.getInventory().add($$5)) {
            $$2.drop($$5, false);
        }
        $$0.gameEvent($$2, GameEvent.BLOCK_CHANGE, $$1);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new ChiseledBookShelfBlockEntity($$0, $$1);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(HorizontalDirectionalBlock.FACING);
        SLOT_OCCUPIED_PROPERTIES.forEach($$1 -> $$0.add((Property<?>)$$1));
    }

    @Override
    public void onRemove(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        ChiseledBookShelfBlockEntity $$6;
        if ($$0.is($$3.getBlock())) {
            return;
        }
        BlockEntity $$5 = $$1.getBlockEntity($$2);
        if ($$5 instanceof ChiseledBookShelfBlockEntity && !($$6 = (ChiseledBookShelfBlockEntity)$$5).isEmpty()) {
            for (int $$7 = 0; $$7 < 6; ++$$7) {
                ItemStack $$8 = $$6.getItem($$7);
                if ($$8.isEmpty()) continue;
                Containers.dropItemStack($$1, $$2.getX(), $$2.getY(), $$2.getZ(), $$8);
            }
            $$6.clearContent();
            $$1.updateNeighbourForOutputSignal($$2, this);
        }
        super.onRemove($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        return (BlockState)this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, $$0.getHorizontalDirection().getOpposite());
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState $$0) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState $$0, Level $$1, BlockPos $$2) {
        if ($$1.isClientSide()) {
            return 0;
        }
        BlockEntity blockEntity = $$1.getBlockEntity($$2);
        if (blockEntity instanceof ChiseledBookShelfBlockEntity) {
            ChiseledBookShelfBlockEntity $$3 = (ChiseledBookShelfBlockEntity)blockEntity;
            return $$3.getLastInteractedSlot() + 1;
        }
        return 0;
    }
}