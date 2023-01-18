/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.inventory;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.ClientSideMerchant;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.inventory.MerchantResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public class MerchantMenu
extends AbstractContainerMenu {
    protected static final int PAYMENT1_SLOT = 0;
    protected static final int PAYMENT2_SLOT = 1;
    protected static final int RESULT_SLOT = 2;
    private static final int INV_SLOT_START = 3;
    private static final int INV_SLOT_END = 30;
    private static final int USE_ROW_SLOT_START = 30;
    private static final int USE_ROW_SLOT_END = 39;
    private static final int SELLSLOT1_X = 136;
    private static final int SELLSLOT2_X = 162;
    private static final int BUYSLOT_X = 220;
    private static final int ROW_Y = 37;
    private final Merchant trader;
    private final MerchantContainer tradeContainer;
    private int merchantLevel;
    private boolean showProgressBar;
    private boolean canRestock;

    public MerchantMenu(int $$0, Inventory $$1) {
        this($$0, $$1, new ClientSideMerchant($$1.player));
    }

    public MerchantMenu(int $$0, Inventory $$1, Merchant $$2) {
        super(MenuType.MERCHANT, $$0);
        this.trader = $$2;
        this.tradeContainer = new MerchantContainer($$2);
        this.addSlot(new Slot(this.tradeContainer, 0, 136, 37));
        this.addSlot(new Slot(this.tradeContainer, 1, 162, 37));
        this.addSlot(new MerchantResultSlot($$1.player, $$2, this.tradeContainer, 2, 220, 37));
        for (int $$3 = 0; $$3 < 3; ++$$3) {
            for (int $$4 = 0; $$4 < 9; ++$$4) {
                this.addSlot(new Slot($$1, $$4 + $$3 * 9 + 9, 108 + $$4 * 18, 84 + $$3 * 18));
            }
        }
        for (int $$5 = 0; $$5 < 9; ++$$5) {
            this.addSlot(new Slot($$1, $$5, 108 + $$5 * 18, 142));
        }
    }

    public void setShowProgressBar(boolean $$0) {
        this.showProgressBar = $$0;
    }

    @Override
    public void slotsChanged(Container $$0) {
        this.tradeContainer.updateSellItem();
        super.slotsChanged($$0);
    }

    public void setSelectionHint(int $$0) {
        this.tradeContainer.setSelectionHint($$0);
    }

    @Override
    public boolean stillValid(Player $$0) {
        return this.trader.getTradingPlayer() == $$0;
    }

    public int getTraderXp() {
        return this.trader.getVillagerXp();
    }

    public int getFutureTraderXp() {
        return this.tradeContainer.getFutureXp();
    }

    public void setXp(int $$0) {
        this.trader.overrideXp($$0);
    }

    public int getTraderLevel() {
        return this.merchantLevel;
    }

    public void setMerchantLevel(int $$0) {
        this.merchantLevel = $$0;
    }

    public void setCanRestock(boolean $$0) {
        this.canRestock = $$0;
    }

    public boolean canRestock() {
        return this.canRestock;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack $$0, Slot $$1) {
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
                this.playTradeSound();
            } else if ($$1 == 0 || $$1 == 1 ? !this.moveItemStackTo($$4, 3, 39, false) : ($$1 >= 3 && $$1 < 30 ? !this.moveItemStackTo($$4, 30, 39, false) : $$1 >= 30 && $$1 < 39 && !this.moveItemStackTo($$4, 3, 30, false))) {
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

    private void playTradeSound() {
        if (!this.trader.isClientSide()) {
            Entity $$0 = (Entity)((Object)this.trader);
            $$0.getLevel().playLocalSound($$0.getX(), $$0.getY(), $$0.getZ(), this.trader.getNotifyTradeSound(), SoundSource.NEUTRAL, 1.0f, 1.0f, false);
        }
    }

    @Override
    public void removed(Player $$0) {
        super.removed($$0);
        this.trader.setTradingPlayer(null);
        if (this.trader.isClientSide()) {
            return;
        }
        if (!$$0.isAlive() || $$0 instanceof ServerPlayer && ((ServerPlayer)$$0).hasDisconnected()) {
            ItemStack $$1 = this.tradeContainer.removeItemNoUpdate(0);
            if (!$$1.isEmpty()) {
                $$0.drop($$1, false);
            }
            if (!($$1 = this.tradeContainer.removeItemNoUpdate(1)).isEmpty()) {
                $$0.drop($$1, false);
            }
        } else if ($$0 instanceof ServerPlayer) {
            $$0.getInventory().placeItemBackInInventory(this.tradeContainer.removeItemNoUpdate(0));
            $$0.getInventory().placeItemBackInInventory(this.tradeContainer.removeItemNoUpdate(1));
        }
    }

    public void tryMoveItems(int $$0) {
        ItemStack $$2;
        if ($$0 < 0 || this.getOffers().size() <= $$0) {
            return;
        }
        ItemStack $$1 = this.tradeContainer.getItem(0);
        if (!$$1.isEmpty()) {
            if (!this.moveItemStackTo($$1, 3, 39, true)) {
                return;
            }
            this.tradeContainer.setItem(0, $$1);
        }
        if (!($$2 = this.tradeContainer.getItem(1)).isEmpty()) {
            if (!this.moveItemStackTo($$2, 3, 39, true)) {
                return;
            }
            this.tradeContainer.setItem(1, $$2);
        }
        if (this.tradeContainer.getItem(0).isEmpty() && this.tradeContainer.getItem(1).isEmpty()) {
            ItemStack $$3 = ((MerchantOffer)this.getOffers().get($$0)).getCostA();
            this.moveFromInventoryToPaymentSlot(0, $$3);
            ItemStack $$4 = ((MerchantOffer)this.getOffers().get($$0)).getCostB();
            this.moveFromInventoryToPaymentSlot(1, $$4);
        }
    }

    private void moveFromInventoryToPaymentSlot(int $$0, ItemStack $$1) {
        if (!$$1.isEmpty()) {
            for (int $$2 = 3; $$2 < 39; ++$$2) {
                ItemStack $$3 = ((Slot)this.slots.get($$2)).getItem();
                if ($$3.isEmpty() || !ItemStack.isSameItemSameTags($$1, $$3)) continue;
                ItemStack $$4 = this.tradeContainer.getItem($$0);
                int $$5 = $$4.isEmpty() ? 0 : $$4.getCount();
                int $$6 = Math.min((int)($$1.getMaxStackSize() - $$5), (int)$$3.getCount());
                ItemStack $$7 = $$3.copy();
                int $$8 = $$5 + $$6;
                $$3.shrink($$6);
                $$7.setCount($$8);
                this.tradeContainer.setItem($$0, $$7);
                if ($$8 >= $$1.getMaxStackSize()) break;
            }
        }
    }

    public void setOffers(MerchantOffers $$0) {
        this.trader.overrideOffers($$0);
    }

    public MerchantOffers getOffers() {
        return this.trader.getOffers();
    }

    public boolean showProgressBar() {
        return this.showProgressBar;
    }
}