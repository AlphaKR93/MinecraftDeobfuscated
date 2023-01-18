/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 *  java.util.function.BiConsumer
 *  javax.annotation.Nullable
 */
package net.minecraft.world.inventory;

import java.util.Optional;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class BeaconMenu
extends AbstractContainerMenu {
    private static final int PAYMENT_SLOT = 0;
    private static final int SLOT_COUNT = 1;
    private static final int DATA_COUNT = 3;
    private static final int INV_SLOT_START = 1;
    private static final int INV_SLOT_END = 28;
    private static final int USE_ROW_SLOT_START = 28;
    private static final int USE_ROW_SLOT_END = 37;
    private final Container beacon = new SimpleContainer(1){

        @Override
        public boolean canPlaceItem(int $$0, ItemStack $$1) {
            return $$1.is(ItemTags.BEACON_PAYMENT_ITEMS);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    };
    private final PaymentSlot paymentSlot;
    private final ContainerLevelAccess access;
    private final ContainerData beaconData;

    public BeaconMenu(int $$0, Container $$1) {
        this($$0, $$1, new SimpleContainerData(3), ContainerLevelAccess.NULL);
    }

    public BeaconMenu(int $$0, Container $$1, ContainerData $$2, ContainerLevelAccess $$3) {
        super(MenuType.BEACON, $$0);
        BeaconMenu.checkContainerDataCount($$2, 3);
        this.beaconData = $$2;
        this.access = $$3;
        this.paymentSlot = new PaymentSlot(this.beacon, 0, 136, 110);
        this.addSlot(this.paymentSlot);
        this.addDataSlots($$2);
        int $$4 = 36;
        int $$5 = 137;
        for (int $$6 = 0; $$6 < 3; ++$$6) {
            for (int $$7 = 0; $$7 < 9; ++$$7) {
                this.addSlot(new Slot($$1, $$7 + $$6 * 9 + 9, 36 + $$7 * 18, 137 + $$6 * 18));
            }
        }
        for (int $$8 = 0; $$8 < 9; ++$$8) {
            this.addSlot(new Slot($$1, $$8, 36 + $$8 * 18, 195));
        }
    }

    @Override
    public void removed(Player $$0) {
        super.removed($$0);
        if ($$0.level.isClientSide) {
            return;
        }
        ItemStack $$1 = this.paymentSlot.remove(this.paymentSlot.getMaxStackSize());
        if (!$$1.isEmpty()) {
            $$0.drop($$1, false);
        }
    }

    @Override
    public boolean stillValid(Player $$0) {
        return BeaconMenu.stillValid(this.access, $$0, Blocks.BEACON);
    }

    @Override
    public void setData(int $$0, int $$1) {
        super.setData($$0, $$1);
        this.broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(Player $$0, int $$1) {
        ItemStack $$2 = ItemStack.EMPTY;
        Slot $$3 = (Slot)this.slots.get($$1);
        if ($$3 != null && $$3.hasItem()) {
            ItemStack $$4 = $$3.getItem();
            $$2 = $$4.copy();
            if ($$1 == 0) {
                if (!this.moveItemStackTo($$4, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
                $$3.onQuickCraft($$4, $$2);
            } else if (!this.paymentSlot.hasItem() && this.paymentSlot.mayPlace($$4) && $$4.getCount() == 1 ? !this.moveItemStackTo($$4, 0, 1, false) : ($$1 >= 1 && $$1 < 28 ? !this.moveItemStackTo($$4, 28, 37, false) : ($$1 >= 28 && $$1 < 37 ? !this.moveItemStackTo($$4, 1, 28, false) : !this.moveItemStackTo($$4, 1, 37, false)))) {
                return ItemStack.EMPTY;
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

    public int getLevels() {
        return this.beaconData.get(0);
    }

    @Nullable
    public MobEffect getPrimaryEffect() {
        return MobEffect.byId(this.beaconData.get(1));
    }

    @Nullable
    public MobEffect getSecondaryEffect() {
        return MobEffect.byId(this.beaconData.get(2));
    }

    public void updateEffects(Optional<MobEffect> $$0, Optional<MobEffect> $$1) {
        if (this.paymentSlot.hasItem()) {
            this.beaconData.set(1, (Integer)$$0.map(MobEffect::getId).orElse((Object)-1));
            this.beaconData.set(2, (Integer)$$1.map(MobEffect::getId).orElse((Object)-1));
            this.paymentSlot.remove(1);
            this.access.execute((BiConsumer<Level, BlockPos>)((BiConsumer)Level::blockEntityChanged));
        }
    }

    public boolean hasPayment() {
        return !this.beacon.getItem(0).isEmpty();
    }

    class PaymentSlot
    extends Slot {
        public PaymentSlot(Container $$0, int $$1, int $$2, int $$3) {
            super($$0, $$1, $$2, $$3);
        }

        @Override
        public boolean mayPlace(ItemStack $$0) {
            return $$0.is(ItemTags.BEACON_PAYMENT_ITEMS);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}