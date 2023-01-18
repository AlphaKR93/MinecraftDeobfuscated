/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.BiConsumer
 */
package net.minecraft.world.inventory;

import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class CartographyTableMenu
extends AbstractContainerMenu {
    public static final int MAP_SLOT = 0;
    public static final int ADDITIONAL_SLOT = 1;
    public static final int RESULT_SLOT = 2;
    private static final int INV_SLOT_START = 3;
    private static final int INV_SLOT_END = 30;
    private static final int USE_ROW_SLOT_START = 30;
    private static final int USE_ROW_SLOT_END = 39;
    private final ContainerLevelAccess access;
    long lastSoundTime;
    public final Container container = new SimpleContainer(2){

        @Override
        public void setChanged() {
            CartographyTableMenu.this.slotsChanged(this);
            super.setChanged();
        }
    };
    private final ResultContainer resultContainer = new ResultContainer(){

        @Override
        public void setChanged() {
            CartographyTableMenu.this.slotsChanged(this);
            super.setChanged();
        }
    };

    public CartographyTableMenu(int $$0, Inventory $$1) {
        this($$0, $$1, ContainerLevelAccess.NULL);
    }

    public CartographyTableMenu(int $$0, Inventory $$1, final ContainerLevelAccess $$2) {
        super(MenuType.CARTOGRAPHY_TABLE, $$0);
        this.access = $$2;
        this.addSlot(new Slot(this.container, 0, 15, 15){

            @Override
            public boolean mayPlace(ItemStack $$0) {
                return $$0.is(Items.FILLED_MAP);
            }
        });
        this.addSlot(new Slot(this.container, 1, 15, 52){

            @Override
            public boolean mayPlace(ItemStack $$0) {
                return $$0.is(Items.PAPER) || $$0.is(Items.MAP) || $$0.is(Items.GLASS_PANE);
            }
        });
        this.addSlot(new Slot(this.resultContainer, 2, 145, 39){

            @Override
            public boolean mayPlace(ItemStack $$0) {
                return false;
            }

            @Override
            public void onTake(Player $$02, ItemStack $$12) {
                ((Slot)CartographyTableMenu.this.slots.get(0)).remove(1);
                ((Slot)CartographyTableMenu.this.slots.get(1)).remove(1);
                $$12.getItem().onCraftedBy($$12, $$02.level, $$02);
                $$2.execute((BiConsumer<Level, BlockPos>)((BiConsumer)($$0, $$1) -> {
                    long $$22 = $$0.getGameTime();
                    if (CartographyTableMenu.this.lastSoundTime != $$22) {
                        $$0.playSound(null, (BlockPos)$$1, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundSource.BLOCKS, 1.0f, 1.0f);
                        CartographyTableMenu.this.lastSoundTime = $$22;
                    }
                }));
                super.onTake($$02, $$12);
            }
        });
        for (int $$3 = 0; $$3 < 3; ++$$3) {
            for (int $$4 = 0; $$4 < 9; ++$$4) {
                this.addSlot(new Slot($$1, $$4 + $$3 * 9 + 9, 8 + $$4 * 18, 84 + $$3 * 18));
            }
        }
        for (int $$5 = 0; $$5 < 9; ++$$5) {
            this.addSlot(new Slot($$1, $$5, 8 + $$5 * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player $$0) {
        return CartographyTableMenu.stillValid(this.access, $$0, Blocks.CARTOGRAPHY_TABLE);
    }

    @Override
    public void slotsChanged(Container $$0) {
        ItemStack $$1 = this.container.getItem(0);
        ItemStack $$2 = this.container.getItem(1);
        ItemStack $$3 = this.resultContainer.getItem(2);
        if (!$$3.isEmpty() && ($$1.isEmpty() || $$2.isEmpty())) {
            this.resultContainer.removeItemNoUpdate(2);
        } else if (!$$1.isEmpty() && !$$2.isEmpty()) {
            this.setupResultSlot($$1, $$2, $$3);
        }
    }

    private void setupResultSlot(ItemStack $$0, ItemStack $$1, ItemStack $$2) {
        this.access.execute((BiConsumer<Level, BlockPos>)((BiConsumer)($$3, $$4) -> {
            void $$9;
            MapItemSavedData $$5 = MapItem.getSavedData($$0, $$3);
            if ($$5 == null) {
                return;
            }
            if ($$1.is(Items.PAPER) && !$$5.locked && $$5.scale < 4) {
                ItemStack $$6 = $$0.copy();
                $$6.setCount(1);
                $$6.getOrCreateTag().putInt("map_scale_direction", 1);
                this.broadcastChanges();
            } else if ($$1.is(Items.GLASS_PANE) && !$$5.locked) {
                ItemStack $$7 = $$0.copy();
                $$7.setCount(1);
                $$7.getOrCreateTag().putBoolean("map_to_lock", true);
                this.broadcastChanges();
            } else if ($$1.is(Items.MAP)) {
                ItemStack $$8 = $$0.copy();
                $$8.setCount(2);
                this.broadcastChanges();
            } else {
                this.resultContainer.removeItemNoUpdate(2);
                this.broadcastChanges();
                return;
            }
            if (!ItemStack.matches((ItemStack)$$9, $$2)) {
                this.resultContainer.setItem(2, (ItemStack)$$9);
                this.broadcastChanges();
            }
        }));
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack $$0, Slot $$1) {
        return $$1.container != this.resultContainer && super.canTakeItemForPickAll($$0, $$1);
    }

    @Override
    public ItemStack quickMoveStack(Player $$0, int $$1) {
        ItemStack $$2 = ItemStack.EMPTY;
        Slot $$3 = (Slot)this.slots.get($$1);
        if ($$3 != null && $$3.hasItem()) {
            ItemStack $$4 = $$3.getItem();
            $$2 = $$4.copy();
            if ($$1 == 2) {
                $$4.getItem().onCraftedBy($$4, $$0.level, $$0);
                if (!this.moveItemStackTo($$4, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                $$3.onQuickCraft($$4, $$2);
            } else if ($$1 == 1 || $$1 == 0 ? !this.moveItemStackTo($$4, 3, 39, false) : ($$4.is(Items.FILLED_MAP) ? !this.moveItemStackTo($$4, 0, 1, false) : ($$4.is(Items.PAPER) || $$4.is(Items.MAP) || $$4.is(Items.GLASS_PANE) ? !this.moveItemStackTo($$4, 1, 2, false) : ($$1 >= 3 && $$1 < 30 ? !this.moveItemStackTo($$4, 30, 39, false) : $$1 >= 30 && $$1 < 39 && !this.moveItemStackTo($$4, 3, 30, false))))) {
                return ItemStack.EMPTY;
            }
            if ($$4.isEmpty()) {
                $$3.set(ItemStack.EMPTY);
            }
            $$3.setChanged();
            if ($$4.getCount() == $$2.getCount()) {
                return ItemStack.EMPTY;
            }
            $$3.onTake($$0, $$4);
            this.broadcastChanges();
        }
        return $$2;
    }

    @Override
    public void removed(Player $$0) {
        super.removed($$0);
        this.resultContainer.removeItemNoUpdate(2);
        this.access.execute((BiConsumer<Level, BlockPos>)((BiConsumer)($$1, $$2) -> this.clearContainer($$0, this.container)));
    }
}