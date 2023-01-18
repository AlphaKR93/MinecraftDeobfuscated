/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.BiConsumer
 *  javax.annotation.Nullable
 */
package net.minecraft.world.inventory;

import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class ItemCombinerMenu
extends AbstractContainerMenu {
    public static final int INPUT_SLOT = 0;
    public static final int ADDITIONAL_SLOT = 1;
    public static final int RESULT_SLOT = 2;
    private static final int INV_SLOT_START = 3;
    private static final int INV_SLOT_END = 30;
    private static final int USE_ROW_SLOT_START = 30;
    private static final int USE_ROW_SLOT_END = 39;
    protected final ResultContainer resultSlots = new ResultContainer();
    protected final Container inputSlots = new SimpleContainer(2){

        @Override
        public void setChanged() {
            super.setChanged();
            ItemCombinerMenu.this.slotsChanged(this);
        }
    };
    protected final ContainerLevelAccess access;
    protected final Player player;

    protected abstract boolean mayPickup(Player var1, boolean var2);

    protected abstract void onTake(Player var1, ItemStack var2);

    protected abstract boolean isValidBlock(BlockState var1);

    public ItemCombinerMenu(@Nullable MenuType<?> $$0, int $$1, Inventory $$2, ContainerLevelAccess $$3) {
        super($$0, $$1);
        this.access = $$3;
        this.player = $$2.player;
        this.addSlot(new Slot(this.inputSlots, 0, 27, 47));
        this.addSlot(new Slot(this.inputSlots, 1, 76, 47));
        this.addSlot(new Slot(this.resultSlots, 2, 134, 47){

            @Override
            public boolean mayPlace(ItemStack $$0) {
                return false;
            }

            @Override
            public boolean mayPickup(Player $$0) {
                return ItemCombinerMenu.this.mayPickup($$0, this.hasItem());
            }

            @Override
            public void onTake(Player $$0, ItemStack $$1) {
                ItemCombinerMenu.this.onTake($$0, $$1);
            }
        });
        for (int $$4 = 0; $$4 < 3; ++$$4) {
            for (int $$5 = 0; $$5 < 9; ++$$5) {
                this.addSlot(new Slot($$2, $$5 + $$4 * 9 + 9, 8 + $$5 * 18, 84 + $$4 * 18));
            }
        }
        for (int $$6 = 0; $$6 < 9; ++$$6) {
            this.addSlot(new Slot($$2, $$6, 8 + $$6 * 18, 142));
        }
    }

    public abstract void createResult();

    @Override
    public void slotsChanged(Container $$0) {
        super.slotsChanged($$0);
        if ($$0 == this.inputSlots) {
            this.createResult();
        }
    }

    @Override
    public void removed(Player $$0) {
        super.removed($$0);
        this.access.execute((BiConsumer<Level, BlockPos>)((BiConsumer)($$1, $$2) -> this.clearContainer($$0, this.inputSlots)));
    }

    @Override
    public boolean stillValid(Player $$0) {
        return this.access.evaluate(($$1, $$2) -> {
            if (!this.isValidBlock($$1.getBlockState((BlockPos)$$2))) {
                return false;
            }
            return $$0.distanceToSqr((double)$$2.getX() + 0.5, (double)$$2.getY() + 0.5, (double)$$2.getZ() + 0.5) <= 64.0;
        }, true);
    }

    protected boolean shouldQuickMoveToAdditionalSlot(ItemStack $$0) {
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player $$0, int $$1) {
        ItemStack $$2 = ItemStack.EMPTY;
        Slot $$3 = (Slot)this.slots.get($$1);
        if ($$3 != null && $$3.hasItem()) {
            ItemStack $$4 = $$3.getItem();
            $$2 = $$4.copy();
            if ($$1 == 2) {
                if (!this.moveItemStackTo($$4, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                $$3.onQuickCraft($$4, $$2);
            } else if ($$1 == 0 || $$1 == 1) {
                if (!this.moveItemStackTo($$4, 3, 39, false)) {
                    return ItemStack.EMPTY;
                }
            } else if ($$1 >= 3 && $$1 < 39) {
                int $$5;
                int n = $$5 = this.shouldQuickMoveToAdditionalSlot($$2) ? 1 : 0;
                if (!this.moveItemStackTo($$4, $$5, 2, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if ($$4.isEmpty()) {
                $$3.set(ItemStack.EMPTY);
            } else {
                $$3.setChanged();
            }
            if ($$4.getCount() == $$2.getCount()) {
                return ItemStack.EMPTY;
            }
            $$3.onTake($$0, $$4);
        }
        return $$2;
    }
}