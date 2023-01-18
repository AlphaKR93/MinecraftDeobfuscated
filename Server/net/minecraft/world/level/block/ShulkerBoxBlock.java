/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Enum
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Map
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShulkerBoxBlock
extends BaseEntityBlock {
    private static final float OPEN_AABB_SIZE = 1.0f;
    private static final VoxelShape UP_OPEN_AABB = Block.box(0.0, 15.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape DOWN_OPEN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
    private static final VoxelShape WES_OPEN_AABB = Block.box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
    private static final VoxelShape EAST_OPEN_AABB = Block.box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    private static final VoxelShape NORTH_OPEN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
    private static final VoxelShape SOUTH_OPEN_AABB = Block.box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
    private static final Map<Direction, VoxelShape> OPEN_SHAPE_BY_DIRECTION = (Map)Util.make(Maps.newEnumMap(Direction.class), $$0 -> {
        $$0.put((Enum)Direction.NORTH, (Object)NORTH_OPEN_AABB);
        $$0.put((Enum)Direction.EAST, (Object)EAST_OPEN_AABB);
        $$0.put((Enum)Direction.SOUTH, (Object)SOUTH_OPEN_AABB);
        $$0.put((Enum)Direction.WEST, (Object)WES_OPEN_AABB);
        $$0.put((Enum)Direction.UP, (Object)UP_OPEN_AABB);
        $$0.put((Enum)Direction.DOWN, (Object)DOWN_OPEN_AABB);
    });
    public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;
    public static final ResourceLocation CONTENTS = new ResourceLocation("contents");
    @Nullable
    private final DyeColor color;

    public ShulkerBoxBlock(@Nullable DyeColor $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.color = $$0;
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.UP));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new ShulkerBoxBlockEntity(this.color, $$0, $$1);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$0, BlockState $$1, BlockEntityType<T> $$2) {
        return ShulkerBoxBlock.createTickerHelper($$2, BlockEntityType.SHULKER_BOX, ShulkerBoxBlockEntity::tick);
    }

    @Override
    public RenderShape getRenderShape(BlockState $$0) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        if ($$1.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if ($$3.isSpectator()) {
            return InteractionResult.CONSUME;
        }
        BlockEntity $$6 = $$1.getBlockEntity($$2);
        if ($$6 instanceof ShulkerBoxBlockEntity) {
            ShulkerBoxBlockEntity $$7 = (ShulkerBoxBlockEntity)$$6;
            if (ShulkerBoxBlock.canOpen($$0, $$1, $$2, $$7)) {
                $$3.openMenu($$7);
                $$3.awardStat(Stats.OPEN_SHULKER_BOX);
                PiglinAi.angerNearbyPiglins($$3, true);
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    private static boolean canOpen(BlockState $$0, Level $$1, BlockPos $$2, ShulkerBoxBlockEntity $$3) {
        if ($$3.getAnimationStatus() != ShulkerBoxBlockEntity.AnimationStatus.CLOSED) {
            return true;
        }
        AABB $$4 = Shulker.getProgressDeltaAabb($$0.getValue(FACING), 0.0f, 0.5f).move($$2).deflate(1.0E-6);
        return $$1.noCollision($$4);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        return (BlockState)this.defaultBlockState().setValue(FACING, $$0.getClickedFace());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(FACING);
    }

    @Override
    public void playerWillDestroy(Level $$0, BlockPos $$1, BlockState $$2, Player $$3) {
        BlockEntity $$4 = $$0.getBlockEntity($$1);
        if ($$4 instanceof ShulkerBoxBlockEntity) {
            ShulkerBoxBlockEntity $$5 = (ShulkerBoxBlockEntity)$$4;
            if (!$$0.isClientSide && $$3.isCreative() && !$$5.isEmpty()) {
                ItemStack $$6 = ShulkerBoxBlock.getColoredItemStack(this.getColor());
                $$4.saveToItem($$6);
                if ($$5.hasCustomName()) {
                    $$6.setHoverName($$5.getCustomName());
                }
                ItemEntity $$7 = new ItemEntity($$0, (double)$$1.getX() + 0.5, (double)$$1.getY() + 0.5, (double)$$1.getZ() + 0.5, $$6);
                $$7.setDefaultPickUpDelay();
                $$0.addFreshEntity($$7);
            } else {
                $$5.unpackLootTable($$3);
            }
        }
        super.playerWillDestroy($$0, $$1, $$2, $$3);
    }

    @Override
    public List<ItemStack> getDrops(BlockState $$0, LootContext.Builder $$12) {
        BlockEntity $$22 = $$12.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if ($$22 instanceof ShulkerBoxBlockEntity) {
            ShulkerBoxBlockEntity $$3 = (ShulkerBoxBlockEntity)$$22;
            $$12 = $$12.withDynamicDrop(CONTENTS, ($$1, $$2) -> {
                for (int $$3 = 0; $$3 < $$3.getContainerSize(); ++$$3) {
                    $$2.accept((Object)$$3.getItem($$3));
                }
            });
        }
        return super.getDrops($$0, $$12);
    }

    @Override
    public void setPlacedBy(Level $$0, BlockPos $$1, BlockState $$2, LivingEntity $$3, ItemStack $$4) {
        BlockEntity $$5;
        if ($$4.hasCustomHoverName() && ($$5 = $$0.getBlockEntity($$1)) instanceof ShulkerBoxBlockEntity) {
            ((ShulkerBoxBlockEntity)$$5).setCustomName($$4.getHoverName());
        }
    }

    @Override
    public void onRemove(BlockState $$0, Level $$1, BlockPos $$2, BlockState $$3, boolean $$4) {
        if ($$0.is($$3.getBlock())) {
            return;
        }
        BlockEntity $$5 = $$1.getBlockEntity($$2);
        if ($$5 instanceof ShulkerBoxBlockEntity) {
            $$1.updateNeighbourForOutputSignal($$2, $$0.getBlock());
        }
        super.onRemove($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public void appendHoverText(ItemStack $$0, @Nullable BlockGetter $$1, List<Component> $$2, TooltipFlag $$3) {
        super.appendHoverText($$0, $$1, $$2, $$3);
        CompoundTag $$4 = BlockItem.getBlockEntityData($$0);
        if ($$4 != null) {
            if ($$4.contains("LootTable", 8)) {
                $$2.add((Object)Component.literal("???????"));
            }
            if ($$4.contains("Items", 9)) {
                NonNullList<ItemStack> $$5 = NonNullList.withSize(27, ItemStack.EMPTY);
                ContainerHelper.loadAllItems($$4, $$5);
                int $$6 = 0;
                int $$7 = 0;
                Iterator iterator = $$5.iterator();
                while (iterator.hasNext()) {
                    ItemStack $$8 = (ItemStack)iterator.next();
                    if ($$8.isEmpty()) continue;
                    ++$$7;
                    if ($$6 > 4) continue;
                    ++$$6;
                    MutableComponent $$9 = $$8.getHoverName().copy();
                    $$9.append(" x").append(String.valueOf((int)$$8.getCount()));
                    $$2.add((Object)$$9);
                }
                if ($$7 - $$6 > 0) {
                    $$2.add((Object)Component.translatable("container.shulkerBox.more", $$7 - $$6).withStyle(ChatFormatting.ITALIC));
                }
            }
        }
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState $$0) {
        return PushReaction.DESTROY;
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        ShulkerBoxBlockEntity $$4;
        BlockEntity $$3 = $$1.getBlockEntity($$2);
        if ($$3 instanceof ShulkerBoxBlockEntity && !($$4 = (ShulkerBoxBlockEntity)$$3).isClosed()) {
            return (VoxelShape)OPEN_SHAPE_BY_DIRECTION.get((Object)$$0.getValue(FACING).getOpposite());
        }
        return Shapes.block();
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        BlockEntity $$4 = $$1.getBlockEntity($$2);
        if ($$4 instanceof ShulkerBoxBlockEntity) {
            return Shapes.create(((ShulkerBoxBlockEntity)$$4).getBoundingBox($$0));
        }
        return Shapes.block();
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState $$0) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState $$0, Level $$1, BlockPos $$2) {
        return AbstractContainerMenu.getRedstoneSignalFromContainer((Container)((Object)$$1.getBlockEntity($$2)));
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter $$0, BlockPos $$12, BlockState $$2) {
        ItemStack $$3 = super.getCloneItemStack($$0, $$12, $$2);
        $$0.getBlockEntity($$12, BlockEntityType.SHULKER_BOX).ifPresent($$1 -> $$1.saveToItem($$3));
        return $$3;
    }

    @Nullable
    public static DyeColor getColorFromItem(Item $$0) {
        return ShulkerBoxBlock.getColorFromBlock(Block.byItem($$0));
    }

    @Nullable
    public static DyeColor getColorFromBlock(Block $$0) {
        if ($$0 instanceof ShulkerBoxBlock) {
            return ((ShulkerBoxBlock)$$0).getColor();
        }
        return null;
    }

    public static Block getBlockByColor(@Nullable DyeColor $$0) {
        if ($$0 == null) {
            return Blocks.SHULKER_BOX;
        }
        switch ($$0) {
            case WHITE: {
                return Blocks.WHITE_SHULKER_BOX;
            }
            case ORANGE: {
                return Blocks.ORANGE_SHULKER_BOX;
            }
            case MAGENTA: {
                return Blocks.MAGENTA_SHULKER_BOX;
            }
            case LIGHT_BLUE: {
                return Blocks.LIGHT_BLUE_SHULKER_BOX;
            }
            case YELLOW: {
                return Blocks.YELLOW_SHULKER_BOX;
            }
            case LIME: {
                return Blocks.LIME_SHULKER_BOX;
            }
            case PINK: {
                return Blocks.PINK_SHULKER_BOX;
            }
            case GRAY: {
                return Blocks.GRAY_SHULKER_BOX;
            }
            case LIGHT_GRAY: {
                return Blocks.LIGHT_GRAY_SHULKER_BOX;
            }
            case CYAN: {
                return Blocks.CYAN_SHULKER_BOX;
            }
            default: {
                return Blocks.PURPLE_SHULKER_BOX;
            }
            case BLUE: {
                return Blocks.BLUE_SHULKER_BOX;
            }
            case BROWN: {
                return Blocks.BROWN_SHULKER_BOX;
            }
            case GREEN: {
                return Blocks.GREEN_SHULKER_BOX;
            }
            case RED: {
                return Blocks.RED_SHULKER_BOX;
            }
            case BLACK: 
        }
        return Blocks.BLACK_SHULKER_BOX;
    }

    @Nullable
    public DyeColor getColor() {
        return this.color;
    }

    public static ItemStack getColoredItemStack(@Nullable DyeColor $$0) {
        return new ItemStack(ShulkerBoxBlock.getBlockByColor($$0));
    }

    @Override
    public BlockState rotate(BlockState $$0, Rotation $$1) {
        return (BlockState)$$0.setValue(FACING, $$1.rotate($$0.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState $$0, Mirror $$1) {
        return $$0.rotate($$1.getRotation($$0.getValue(FACING)));
    }
}