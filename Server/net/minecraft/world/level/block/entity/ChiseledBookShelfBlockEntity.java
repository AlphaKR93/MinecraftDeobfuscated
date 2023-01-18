/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Objects
 *  java.util.function.Predicate
 *  org.slf4j.Logger
 */
package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.slf4j.Logger;

public class ChiseledBookShelfBlockEntity
extends BlockEntity
implements Container {
    public static final int MAX_BOOKS_IN_STORAGE = 6;
    private static final Logger LOGGER = LogUtils.getLogger();
    private final NonNullList<ItemStack> items = NonNullList.withSize(6, ItemStack.EMPTY);
    private int lastInteractedSlot = -1;

    public ChiseledBookShelfBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.CHISELED_BOOKSHELF, $$0, $$1);
    }

    private void updateState(int $$0) {
        if ($$0 < 0 || $$0 >= 6) {
            LOGGER.error("Expected slot 0-5, got {}", (Object)$$0);
            return;
        }
        this.lastInteractedSlot = $$0;
        BlockState $$1 = this.getBlockState();
        for (int $$2 = 0; $$2 < ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.size(); ++$$2) {
            boolean $$3 = !this.getItem($$2).isEmpty();
            BooleanProperty $$4 = (BooleanProperty)ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.get($$2);
            $$1 = (BlockState)$$1.setValue($$4, $$3);
        }
        ((Level)Objects.requireNonNull((Object)this.level)).setBlock(this.worldPosition, $$1, 3);
    }

    @Override
    public void load(CompoundTag $$0) {
        this.items.clear();
        ContainerHelper.loadAllItems($$0, this.items);
        this.lastInteractedSlot = $$0.getInt("last_interacted_slot");
    }

    @Override
    protected void saveAdditional(CompoundTag $$0) {
        ContainerHelper.saveAllItems($$0, this.items, true);
        $$0.putInt("last_interacted_slot", this.lastInteractedSlot);
    }

    public int count() {
        return (int)this.items.stream().filter(Predicate.not(ItemStack::isEmpty)).count();
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public int getContainerSize() {
        return 6;
    }

    @Override
    public boolean isEmpty() {
        return this.items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int $$0) {
        return this.items.get($$0);
    }

    @Override
    public ItemStack removeItem(int $$0, int $$1) {
        ItemStack $$2 = (ItemStack)Objects.requireNonNullElse((Object)this.items.get($$0), (Object)ItemStack.EMPTY);
        this.items.set($$0, ItemStack.EMPTY);
        if (!$$2.isEmpty()) {
            this.updateState($$0);
        }
        return $$2;
    }

    @Override
    public ItemStack removeItemNoUpdate(int $$0) {
        return this.removeItem($$0, 1);
    }

    @Override
    public void setItem(int $$0, ItemStack $$1) {
        if ($$1.is(ItemTags.BOOKSHELF_BOOKS)) {
            this.items.set($$0, $$1);
            this.updateState($$0);
        }
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean stillValid(Player $$0) {
        if (this.level == null) {
            return false;
        }
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        }
        return !($$0.distanceToSqr((double)this.worldPosition.getX() + 0.5, (double)this.worldPosition.getY() + 0.5, (double)this.worldPosition.getZ() + 0.5) > 64.0);
    }

    @Override
    public boolean canPlaceItem(int $$0, ItemStack $$1) {
        return $$1.is(ItemTags.BOOKSHELF_BOOKS) && this.getItem($$0).isEmpty();
    }

    public int getLastInteractedSlot() {
        return this.lastInteractedSlot;
    }
}