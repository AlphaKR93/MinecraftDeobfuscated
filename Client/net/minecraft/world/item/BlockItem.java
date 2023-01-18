/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Comparable
 *  java.lang.Deprecated
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.Map
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;

public class BlockItem
extends Item {
    public static final String BLOCK_ENTITY_TAG = "BlockEntityTag";
    public static final String BLOCK_STATE_TAG = "BlockStateTag";
    @Deprecated
    private final Block block;

    public BlockItem(Block $$0, Item.Properties $$1) {
        super($$1);
        this.block = $$0;
    }

    @Override
    public InteractionResult useOn(UseOnContext $$0) {
        InteractionResult $$1 = this.place(new BlockPlaceContext($$0));
        if (!$$1.consumesAction() && this.isEdible()) {
            InteractionResult $$2 = this.use($$0.getLevel(), $$0.getPlayer(), $$0.getHand()).getResult();
            return $$2 == InteractionResult.CONSUME ? InteractionResult.CONSUME_PARTIAL : $$2;
        }
        return $$1;
    }

    public InteractionResult place(BlockPlaceContext $$0) {
        if (!this.getBlock().isEnabled($$0.getLevel().enabledFeatures())) {
            return InteractionResult.FAIL;
        }
        if (!$$0.canPlace()) {
            return InteractionResult.FAIL;
        }
        BlockPlaceContext $$1 = this.updatePlacementContext($$0);
        if ($$1 == null) {
            return InteractionResult.FAIL;
        }
        BlockState $$2 = this.getPlacementState($$1);
        if ($$2 == null) {
            return InteractionResult.FAIL;
        }
        if (!this.placeBlock($$1, $$2)) {
            return InteractionResult.FAIL;
        }
        BlockPos $$3 = $$1.getClickedPos();
        Level $$4 = $$1.getLevel();
        Player $$5 = $$1.getPlayer();
        ItemStack $$6 = $$1.getItemInHand();
        BlockState $$7 = $$4.getBlockState($$3);
        if ($$7.is($$2.getBlock())) {
            $$7 = this.updateBlockStateFromTag($$3, $$4, $$6, $$7);
            this.updateCustomBlockEntityTag($$3, $$4, $$5, $$6, $$7);
            $$7.getBlock().setPlacedBy($$4, $$3, $$7, $$5, $$6);
            if ($$5 instanceof ServerPlayer) {
                CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)$$5, $$3, $$6);
            }
        }
        SoundType $$8 = $$7.getSoundType();
        $$4.playSound($$5, $$3, this.getPlaceSound($$7), SoundSource.BLOCKS, ($$8.getVolume() + 1.0f) / 2.0f, $$8.getPitch() * 0.8f);
        $$4.gameEvent(GameEvent.BLOCK_PLACE, $$3, GameEvent.Context.of($$5, $$7));
        if ($$5 == null || !$$5.getAbilities().instabuild) {
            $$6.shrink(1);
        }
        return InteractionResult.sidedSuccess($$4.isClientSide);
    }

    protected SoundEvent getPlaceSound(BlockState $$0) {
        return $$0.getSoundType().getPlaceSound();
    }

    @Nullable
    public BlockPlaceContext updatePlacementContext(BlockPlaceContext $$0) {
        return $$0;
    }

    protected boolean updateCustomBlockEntityTag(BlockPos $$0, Level $$1, @Nullable Player $$2, ItemStack $$3, BlockState $$4) {
        return BlockItem.updateCustomBlockEntityTag($$1, $$2, $$0, $$3);
    }

    @Nullable
    protected BlockState getPlacementState(BlockPlaceContext $$0) {
        BlockState $$1 = this.getBlock().getStateForPlacement($$0);
        return $$1 != null && this.canPlace($$0, $$1) ? $$1 : null;
    }

    private BlockState updateBlockStateFromTag(BlockPos $$0, Level $$1, ItemStack $$2, BlockState $$3) {
        BlockState $$4 = $$3;
        CompoundTag $$5 = $$2.getTag();
        if ($$5 != null) {
            CompoundTag $$6 = $$5.getCompound(BLOCK_STATE_TAG);
            StateDefinition<Block, BlockState> $$7 = $$4.getBlock().getStateDefinition();
            for (String $$8 : $$6.getAllKeys()) {
                Property<?> $$9 = $$7.getProperty($$8);
                if ($$9 == null) continue;
                String $$10 = $$6.get($$8).getAsString();
                $$4 = BlockItem.updateState($$4, $$9, $$10);
            }
        }
        if ($$4 != $$3) {
            $$1.setBlock($$0, $$4, 2);
        }
        return $$4;
    }

    private static <T extends Comparable<T>> BlockState updateState(BlockState $$0, Property<T> $$1, String $$22) {
        return (BlockState)$$1.getValue($$22).map($$2 -> (BlockState)$$0.setValue($$1, $$2)).orElse((Object)$$0);
    }

    protected boolean canPlace(BlockPlaceContext $$0, BlockState $$1) {
        Player $$2 = $$0.getPlayer();
        CollisionContext $$3 = $$2 == null ? CollisionContext.empty() : CollisionContext.of($$2);
        return (!this.mustSurvive() || $$1.canSurvive($$0.getLevel(), $$0.getClickedPos())) && $$0.getLevel().isUnobstructed($$1, $$0.getClickedPos(), $$3);
    }

    protected boolean mustSurvive() {
        return true;
    }

    protected boolean placeBlock(BlockPlaceContext $$0, BlockState $$1) {
        return $$0.getLevel().setBlock($$0.getClickedPos(), $$1, 11);
    }

    public static boolean updateCustomBlockEntityTag(Level $$0, @Nullable Player $$1, BlockPos $$2, ItemStack $$3) {
        BlockEntity $$6;
        MinecraftServer $$4 = $$0.getServer();
        if ($$4 == null) {
            return false;
        }
        CompoundTag $$5 = BlockItem.getBlockEntityData($$3);
        if ($$5 != null && ($$6 = $$0.getBlockEntity($$2)) != null) {
            if (!($$0.isClientSide || !$$6.onlyOpCanSetNbt() || $$1 != null && $$1.canUseGameMasterBlocks())) {
                return false;
            }
            CompoundTag $$7 = $$6.saveWithoutMetadata();
            CompoundTag $$8 = $$7.copy();
            $$7.merge($$5);
            if (!$$7.equals($$8)) {
                $$6.load($$7);
                $$6.setChanged();
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDescriptionId() {
        return this.getBlock().getDescriptionId();
    }

    @Override
    public void appendHoverText(ItemStack $$0, @Nullable Level $$1, List<Component> $$2, TooltipFlag $$3) {
        super.appendHoverText($$0, $$1, $$2, $$3);
        this.getBlock().appendHoverText($$0, $$1, $$2, $$3);
    }

    public Block getBlock() {
        return this.block;
    }

    public void registerBlocks(Map<Block, Item> $$0, Item $$1) {
        $$0.put((Object)this.getBlock(), (Object)$$1);
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return !(this.block instanceof ShulkerBoxBlock);
    }

    @Override
    public void onDestroyed(ItemEntity $$0) {
        ItemStack $$1;
        CompoundTag $$2;
        if (this.block instanceof ShulkerBoxBlock && ($$2 = BlockItem.getBlockEntityData($$1 = $$0.getItem())) != null && $$2.contains("Items", 9)) {
            ListTag $$3 = $$2.getList("Items", 10);
            ItemUtils.onContainerDestroyed($$0, (Stream<ItemStack>)$$3.stream().map(arg_0 -> CompoundTag.class.cast(arg_0)).map(ItemStack::of));
        }
    }

    @Nullable
    public static CompoundTag getBlockEntityData(ItemStack $$0) {
        return $$0.getTagElement(BLOCK_ENTITY_TAG);
    }

    public static void setBlockEntityData(ItemStack $$0, BlockEntityType<?> $$1, CompoundTag $$2) {
        if ($$2.isEmpty()) {
            $$0.removeTagKey(BLOCK_ENTITY_TAG);
        } else {
            BlockEntity.addEntityType($$2, $$1);
            $$0.addTagElement(BLOCK_ENTITY_TAG, $$2);
        }
    }

    @Override
    public FeatureFlagSet requiredFeatures() {
        return this.getBlock().requiredFeatures();
    }
}