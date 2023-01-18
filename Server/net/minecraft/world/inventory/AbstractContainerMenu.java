/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  com.google.common.collect.HashBasedTable
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  java.lang.Exception
 *  java.lang.IllegalArgumentException
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Short
 *  java.lang.UnsupportedOperationException
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Optional
 *  java.util.OptionalInt
 *  java.util.Set
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.world.inventory;

import com.google.common.base.Suppliers;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.ContainerSynchronizer;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.slf4j.Logger;

public abstract class AbstractContainerMenu {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final int SLOT_CLICKED_OUTSIDE = -999;
    public static final int QUICKCRAFT_TYPE_CHARITABLE = 0;
    public static final int QUICKCRAFT_TYPE_GREEDY = 1;
    public static final int QUICKCRAFT_TYPE_CLONE = 2;
    public static final int QUICKCRAFT_HEADER_START = 0;
    public static final int QUICKCRAFT_HEADER_CONTINUE = 1;
    public static final int QUICKCRAFT_HEADER_END = 2;
    public static final int CARRIED_SLOT_SIZE = Integer.MAX_VALUE;
    private final NonNullList<ItemStack> lastSlots = NonNullList.create();
    public final NonNullList<Slot> slots = NonNullList.create();
    private final List<DataSlot> dataSlots = Lists.newArrayList();
    private ItemStack carried = ItemStack.EMPTY;
    private final NonNullList<ItemStack> remoteSlots = NonNullList.create();
    private final IntList remoteDataSlots = new IntArrayList();
    private ItemStack remoteCarried = ItemStack.EMPTY;
    private int stateId;
    @Nullable
    private final MenuType<?> menuType;
    public final int containerId;
    private int quickcraftType = -1;
    private int quickcraftStatus;
    private final Set<Slot> quickcraftSlots = Sets.newHashSet();
    private final List<ContainerListener> containerListeners = Lists.newArrayList();
    @Nullable
    private ContainerSynchronizer synchronizer;
    private boolean suppressRemoteUpdates;

    protected AbstractContainerMenu(@Nullable MenuType<?> $$0, int $$1) {
        this.menuType = $$0;
        this.containerId = $$1;
    }

    protected static boolean stillValid(ContainerLevelAccess $$0, Player $$1, Block $$22) {
        return $$0.evaluate(($$2, $$3) -> {
            if (!$$2.getBlockState((BlockPos)$$3).is($$22)) {
                return false;
            }
            return $$1.distanceToSqr((double)$$3.getX() + 0.5, (double)$$3.getY() + 0.5, (double)$$3.getZ() + 0.5) <= 64.0;
        }, true);
    }

    public MenuType<?> getType() {
        if (this.menuType == null) {
            throw new UnsupportedOperationException("Unable to construct this menu by type");
        }
        return this.menuType;
    }

    protected static void checkContainerSize(Container $$0, int $$1) {
        int $$2 = $$0.getContainerSize();
        if ($$2 < $$1) {
            throw new IllegalArgumentException("Container size " + $$2 + " is smaller than expected " + $$1);
        }
    }

    protected static void checkContainerDataCount(ContainerData $$0, int $$1) {
        int $$2 = $$0.getCount();
        if ($$2 < $$1) {
            throw new IllegalArgumentException("Container data count " + $$2 + " is smaller than expected " + $$1);
        }
    }

    public boolean isValidSlotIndex(int $$0) {
        return $$0 == -1 || $$0 == -999 || $$0 < this.slots.size();
    }

    protected Slot addSlot(Slot $$0) {
        $$0.index = this.slots.size();
        this.slots.add($$0);
        this.lastSlots.add(ItemStack.EMPTY);
        this.remoteSlots.add(ItemStack.EMPTY);
        return $$0;
    }

    protected DataSlot addDataSlot(DataSlot $$0) {
        this.dataSlots.add((Object)$$0);
        this.remoteDataSlots.add(0);
        return $$0;
    }

    protected void addDataSlots(ContainerData $$0) {
        for (int $$1 = 0; $$1 < $$0.getCount(); ++$$1) {
            this.addDataSlot(DataSlot.forContainer($$0, $$1));
        }
    }

    public void addSlotListener(ContainerListener $$0) {
        if (this.containerListeners.contains((Object)$$0)) {
            return;
        }
        this.containerListeners.add((Object)$$0);
        this.broadcastChanges();
    }

    public void setSynchronizer(ContainerSynchronizer $$0) {
        this.synchronizer = $$0;
        this.sendAllDataToRemote();
    }

    public void sendAllDataToRemote() {
        int $$1 = this.slots.size();
        for (int $$0 = 0; $$0 < $$1; ++$$0) {
            this.remoteSlots.set($$0, this.slots.get($$0).getItem().copy());
        }
        this.remoteCarried = this.getCarried().copy();
        int $$3 = this.dataSlots.size();
        for (int $$2 = 0; $$2 < $$3; ++$$2) {
            this.remoteDataSlots.set($$2, ((DataSlot)this.dataSlots.get($$2)).get());
        }
        if (this.synchronizer != null) {
            this.synchronizer.sendInitialData(this, this.remoteSlots, this.remoteCarried, this.remoteDataSlots.toIntArray());
        }
    }

    public void removeSlotListener(ContainerListener $$0) {
        this.containerListeners.remove((Object)$$0);
    }

    public NonNullList<ItemStack> getItems() {
        NonNullList<ItemStack> $$0 = NonNullList.create();
        Iterator iterator = this.slots.iterator();
        while (iterator.hasNext()) {
            Slot $$1 = (Slot)iterator.next();
            $$0.add($$1.getItem());
        }
        return $$0;
    }

    public void broadcastChanges() {
        for (int $$0 = 0; $$0 < this.slots.size(); ++$$0) {
            ItemStack $$1 = this.slots.get($$0).getItem();
            com.google.common.base.Supplier $$2 = Suppliers.memoize($$1::copy);
            this.triggerSlotListeners($$0, $$1, (Supplier<ItemStack>)$$2);
            this.synchronizeSlotToRemote($$0, $$1, (Supplier<ItemStack>)$$2);
        }
        this.synchronizeCarriedToRemote();
        for (int $$3 = 0; $$3 < this.dataSlots.size(); ++$$3) {
            DataSlot $$4 = (DataSlot)this.dataSlots.get($$3);
            int $$5 = $$4.get();
            if ($$4.checkAndClearUpdateFlag()) {
                this.updateDataSlotListeners($$3, $$5);
            }
            this.synchronizeDataSlotToRemote($$3, $$5);
        }
    }

    public void broadcastFullState() {
        for (int $$0 = 0; $$0 < this.slots.size(); ++$$0) {
            ItemStack $$1 = this.slots.get($$0).getItem();
            this.triggerSlotListeners($$0, $$1, (Supplier<ItemStack>)((Supplier)$$1::copy));
        }
        for (int $$2 = 0; $$2 < this.dataSlots.size(); ++$$2) {
            DataSlot $$3 = (DataSlot)this.dataSlots.get($$2);
            if (!$$3.checkAndClearUpdateFlag()) continue;
            this.updateDataSlotListeners($$2, $$3.get());
        }
        this.sendAllDataToRemote();
    }

    private void updateDataSlotListeners(int $$0, int $$1) {
        for (ContainerListener $$2 : this.containerListeners) {
            $$2.dataChanged(this, $$0, $$1);
        }
    }

    private void triggerSlotListeners(int $$0, ItemStack $$1, Supplier<ItemStack> $$2) {
        ItemStack $$3 = this.lastSlots.get($$0);
        if (!ItemStack.matches($$3, $$1)) {
            ItemStack $$4 = (ItemStack)$$2.get();
            this.lastSlots.set($$0, $$4);
            for (ContainerListener $$5 : this.containerListeners) {
                $$5.slotChanged(this, $$0, $$4);
            }
        }
    }

    private void synchronizeSlotToRemote(int $$0, ItemStack $$1, Supplier<ItemStack> $$2) {
        if (this.suppressRemoteUpdates) {
            return;
        }
        ItemStack $$3 = this.remoteSlots.get($$0);
        if (!ItemStack.matches($$3, $$1)) {
            ItemStack $$4 = (ItemStack)$$2.get();
            this.remoteSlots.set($$0, $$4);
            if (this.synchronizer != null) {
                this.synchronizer.sendSlotChange(this, $$0, $$4);
            }
        }
    }

    private void synchronizeDataSlotToRemote(int $$0, int $$1) {
        if (this.suppressRemoteUpdates) {
            return;
        }
        int $$2 = this.remoteDataSlots.getInt($$0);
        if ($$2 != $$1) {
            this.remoteDataSlots.set($$0, $$1);
            if (this.synchronizer != null) {
                this.synchronizer.sendDataChange(this, $$0, $$1);
            }
        }
    }

    private void synchronizeCarriedToRemote() {
        if (this.suppressRemoteUpdates) {
            return;
        }
        if (!ItemStack.matches(this.getCarried(), this.remoteCarried)) {
            this.remoteCarried = this.getCarried().copy();
            if (this.synchronizer != null) {
                this.synchronizer.sendCarriedChange(this, this.remoteCarried);
            }
        }
    }

    public void setRemoteSlot(int $$0, ItemStack $$1) {
        this.remoteSlots.set($$0, $$1.copy());
    }

    public void setRemoteSlotNoCopy(int $$0, ItemStack $$1) {
        if ($$0 < 0 || $$0 >= this.remoteSlots.size()) {
            LOGGER.debug("Incorrect slot index: {} available slots: {}", (Object)$$0, (Object)this.remoteSlots.size());
            return;
        }
        this.remoteSlots.set($$0, $$1);
    }

    public void setRemoteCarried(ItemStack $$0) {
        this.remoteCarried = $$0.copy();
    }

    public boolean clickMenuButton(Player $$0, int $$1) {
        return false;
    }

    public Slot getSlot(int $$0) {
        return this.slots.get($$0);
    }

    public abstract ItemStack quickMoveStack(Player var1, int var2);

    public void clicked(int $$0, int $$1, ClickType $$2, Player $$3) {
        try {
            this.doClick($$0, $$1, $$2, $$3);
        }
        catch (Exception $$4) {
            CrashReport $$5 = CrashReport.forThrowable($$4, "Container click");
            CrashReportCategory $$6 = $$5.addCategory("Click info");
            $$6.setDetail("Menu Type", () -> this.menuType != null ? BuiltInRegistries.MENU.getKey(this.menuType).toString() : "<no type>");
            $$6.setDetail("Menu Class", () -> this.getClass().getCanonicalName());
            $$6.setDetail("Slot Count", this.slots.size());
            $$6.setDetail("Slot", $$0);
            $$6.setDetail("Button", $$1);
            $$6.setDetail("Type", (Object)$$2);
            throw new ReportedException($$5);
        }
    }

    private void doClick(int $$0, int $$1, ClickType $$22, Player $$32) {
        block39: {
            block50: {
                block46: {
                    ItemStack $$29;
                    ItemStack $$28;
                    Slot $$27;
                    Inventory $$4;
                    block49: {
                        block48: {
                            block47: {
                                block44: {
                                    ClickAction $$16;
                                    block45: {
                                        block43: {
                                            block37: {
                                                block42: {
                                                    ItemStack $$7;
                                                    block41: {
                                                        block40: {
                                                            block38: {
                                                                $$4 = $$32.getInventory();
                                                                if ($$22 != ClickType.QUICK_CRAFT) break block37;
                                                                int $$5 = this.quickcraftStatus;
                                                                this.quickcraftStatus = AbstractContainerMenu.getQuickcraftHeader($$1);
                                                                if ($$5 == 1 && this.quickcraftStatus == 2 || $$5 == this.quickcraftStatus) break block38;
                                                                this.resetQuickCraft();
                                                                break block39;
                                                            }
                                                            if (!this.getCarried().isEmpty()) break block40;
                                                            this.resetQuickCraft();
                                                            break block39;
                                                        }
                                                        if (this.quickcraftStatus != 0) break block41;
                                                        this.quickcraftType = AbstractContainerMenu.getQuickcraftType($$1);
                                                        if (AbstractContainerMenu.isValidQuickcraftType(this.quickcraftType, $$32)) {
                                                            this.quickcraftStatus = 1;
                                                            this.quickcraftSlots.clear();
                                                        } else {
                                                            this.resetQuickCraft();
                                                        }
                                                        break block39;
                                                    }
                                                    if (this.quickcraftStatus != 1) break block42;
                                                    Slot $$6 = this.slots.get($$0);
                                                    if (!AbstractContainerMenu.canItemQuickReplace($$6, $$7 = this.getCarried(), true) || !$$6.mayPlace($$7) || this.quickcraftType != 2 && $$7.getCount() <= this.quickcraftSlots.size() || !this.canDragTo($$6)) break block39;
                                                    this.quickcraftSlots.add((Object)$$6);
                                                    break block39;
                                                }
                                                if (this.quickcraftStatus == 2) {
                                                    if (!this.quickcraftSlots.isEmpty()) {
                                                        if (this.quickcraftSlots.size() == 1) {
                                                            int $$8 = ((Slot)this.quickcraftSlots.iterator().next()).index;
                                                            this.resetQuickCraft();
                                                            this.doClick($$8, this.quickcraftType, ClickType.PICKUP, $$32);
                                                            return;
                                                        }
                                                        ItemStack $$9 = this.getCarried().copy();
                                                        int $$10 = this.getCarried().getCount();
                                                        for (Slot $$11 : this.quickcraftSlots) {
                                                            ItemStack $$12 = this.getCarried();
                                                            if ($$11 == null || !AbstractContainerMenu.canItemQuickReplace($$11, $$12, true) || !$$11.mayPlace($$12) || this.quickcraftType != 2 && $$12.getCount() < this.quickcraftSlots.size() || !this.canDragTo($$11)) continue;
                                                            ItemStack $$13 = $$9.copy();
                                                            int $$14 = $$11.hasItem() ? $$11.getItem().getCount() : 0;
                                                            AbstractContainerMenu.getQuickCraftSlotCount(this.quickcraftSlots, this.quickcraftType, $$13, $$14);
                                                            int $$15 = Math.min((int)$$13.getMaxStackSize(), (int)$$11.getMaxStackSize($$13));
                                                            if ($$13.getCount() > $$15) {
                                                                $$13.setCount($$15);
                                                            }
                                                            $$10 -= $$13.getCount() - $$14;
                                                            $$11.set($$13);
                                                        }
                                                        $$9.setCount($$10);
                                                        this.setCarried($$9);
                                                    }
                                                    this.resetQuickCraft();
                                                } else {
                                                    this.resetQuickCraft();
                                                }
                                                break block39;
                                            }
                                            if (this.quickcraftStatus == 0) break block43;
                                            this.resetQuickCraft();
                                            break block39;
                                        }
                                        if ($$22 != ClickType.PICKUP && $$22 != ClickType.QUICK_MOVE || $$1 != 0 && $$1 != 1) break block44;
                                        ClickAction clickAction = $$16 = $$1 == 0 ? ClickAction.PRIMARY : ClickAction.SECONDARY;
                                        if ($$0 != -999) break block45;
                                        if (this.getCarried().isEmpty()) break block39;
                                        if ($$16 == ClickAction.PRIMARY) {
                                            $$32.drop(this.getCarried(), true);
                                            this.setCarried(ItemStack.EMPTY);
                                        } else {
                                            $$32.drop(this.getCarried().split(1), true);
                                        }
                                        break block39;
                                    }
                                    if ($$22 == ClickType.QUICK_MOVE) {
                                        if ($$0 < 0) {
                                            return;
                                        }
                                        Slot $$17 = this.slots.get($$0);
                                        if (!$$17.mayPickup($$32)) {
                                            return;
                                        }
                                        ItemStack $$18 = this.quickMoveStack($$32, $$0);
                                        while (!$$18.isEmpty() && ItemStack.isSame($$17.getItem(), $$18)) {
                                            $$18 = this.quickMoveStack($$32, $$0);
                                        }
                                    } else {
                                        if ($$0 < 0) {
                                            return;
                                        }
                                        Slot $$19 = this.slots.get($$0);
                                        ItemStack $$20 = $$19.getItem();
                                        ItemStack $$21 = this.getCarried();
                                        $$32.updateTutorialInventoryAction($$21, $$19.getItem(), $$16);
                                        if (!this.tryItemClickBehaviourOverride($$32, $$16, $$19, $$20, $$21)) {
                                            if ($$20.isEmpty()) {
                                                if (!$$21.isEmpty()) {
                                                    int $$222 = $$16 == ClickAction.PRIMARY ? $$21.getCount() : 1;
                                                    this.setCarried($$19.safeInsert($$21, $$222));
                                                }
                                            } else if ($$19.mayPickup($$32)) {
                                                if ($$21.isEmpty()) {
                                                    int $$23 = $$16 == ClickAction.PRIMARY ? $$20.getCount() : ($$20.getCount() + 1) / 2;
                                                    Optional<ItemStack> $$24 = $$19.tryRemove($$23, Integer.MAX_VALUE, $$32);
                                                    $$24.ifPresent($$2 -> {
                                                        this.setCarried((ItemStack)$$2);
                                                        $$19.onTake($$32, (ItemStack)$$2);
                                                    });
                                                } else if ($$19.mayPlace($$21)) {
                                                    if (ItemStack.isSameItemSameTags($$20, $$21)) {
                                                        int $$25 = $$16 == ClickAction.PRIMARY ? $$21.getCount() : 1;
                                                        this.setCarried($$19.safeInsert($$21, $$25));
                                                    } else if ($$21.getCount() <= $$19.getMaxStackSize($$21)) {
                                                        this.setCarried($$20);
                                                        $$19.set($$21);
                                                    }
                                                } else if (ItemStack.isSameItemSameTags($$20, $$21)) {
                                                    Optional<ItemStack> $$26 = $$19.tryRemove($$20.getCount(), $$21.getMaxStackSize() - $$21.getCount(), $$32);
                                                    $$26.ifPresent($$3 -> {
                                                        $$21.grow($$3.getCount());
                                                        $$19.onTake($$32, (ItemStack)$$3);
                                                    });
                                                }
                                            }
                                        }
                                        $$19.setChanged();
                                    }
                                    break block39;
                                }
                                if ($$22 != ClickType.SWAP) break block46;
                                $$27 = this.slots.get($$0);
                                $$28 = $$4.getItem($$1);
                                $$29 = $$27.getItem();
                                if ($$28.isEmpty() && $$29.isEmpty()) break block39;
                                if (!$$28.isEmpty()) break block47;
                                if (!$$27.mayPickup($$32)) break block39;
                                $$4.setItem($$1, $$29);
                                $$27.onSwapCraft($$29.getCount());
                                $$27.set(ItemStack.EMPTY);
                                $$27.onTake($$32, $$29);
                                break block39;
                            }
                            if (!$$29.isEmpty()) break block48;
                            if (!$$27.mayPlace($$28)) break block39;
                            int $$30 = $$27.getMaxStackSize($$28);
                            if ($$28.getCount() > $$30) {
                                $$27.set($$28.split($$30));
                            } else {
                                $$4.setItem($$1, ItemStack.EMPTY);
                                $$27.set($$28);
                            }
                            break block39;
                        }
                        if (!$$27.mayPickup($$32) || !$$27.mayPlace($$28)) break block39;
                        int $$31 = $$27.getMaxStackSize($$28);
                        if ($$28.getCount() <= $$31) break block49;
                        $$27.set($$28.split($$31));
                        $$27.onTake($$32, $$29);
                        if ($$4.add($$29)) break block39;
                        $$32.drop($$29, true);
                        break block39;
                    }
                    $$4.setItem($$1, $$29);
                    $$27.set($$28);
                    $$27.onTake($$32, $$29);
                    break block39;
                }
                if ($$22 != ClickType.CLONE || !$$32.getAbilities().instabuild || !this.getCarried().isEmpty() || $$0 < 0) break block50;
                Slot $$322 = this.slots.get($$0);
                if (!$$322.hasItem()) break block39;
                ItemStack $$33 = $$322.getItem().copy();
                $$33.setCount($$33.getMaxStackSize());
                this.setCarried($$33);
                break block39;
            }
            if ($$22 == ClickType.THROW && this.getCarried().isEmpty() && $$0 >= 0) {
                Slot $$34 = this.slots.get($$0);
                int $$35 = $$1 == 0 ? 1 : $$34.getItem().getCount();
                ItemStack $$36 = $$34.safeTake($$35, Integer.MAX_VALUE, $$32);
                $$32.drop($$36, true);
            } else if ($$22 == ClickType.PICKUP_ALL && $$0 >= 0) {
                Slot $$37 = this.slots.get($$0);
                ItemStack $$38 = this.getCarried();
                if (!($$38.isEmpty() || $$37.hasItem() && $$37.mayPickup($$32))) {
                    int $$39 = $$1 == 0 ? 0 : this.slots.size() - 1;
                    int $$40 = $$1 == 0 ? 1 : -1;
                    for (int $$41 = 0; $$41 < 2; ++$$41) {
                        for (int $$42 = $$39; $$42 >= 0 && $$42 < this.slots.size() && $$38.getCount() < $$38.getMaxStackSize(); $$42 += $$40) {
                            Slot $$43 = this.slots.get($$42);
                            if (!$$43.hasItem() || !AbstractContainerMenu.canItemQuickReplace($$43, $$38, true) || !$$43.mayPickup($$32) || !this.canTakeItemForPickAll($$38, $$43)) continue;
                            ItemStack $$44 = $$43.getItem();
                            if ($$41 == 0 && $$44.getCount() == $$44.getMaxStackSize()) continue;
                            ItemStack $$45 = $$43.safeTake($$44.getCount(), $$38.getMaxStackSize() - $$38.getCount(), $$32);
                            $$38.grow($$45.getCount());
                        }
                    }
                }
            }
        }
    }

    private boolean tryItemClickBehaviourOverride(Player $$0, ClickAction $$1, Slot $$2, ItemStack $$3, ItemStack $$4) {
        FeatureFlagSet $$5 = $$0.getLevel().enabledFeatures();
        if ($$4.isItemEnabled($$5) && $$4.overrideStackedOnOther($$2, $$1, $$0)) {
            return true;
        }
        return $$3.isItemEnabled($$5) && $$3.overrideOtherStackedOnMe($$4, $$2, $$1, $$0, this.createCarriedSlotAccess());
    }

    private SlotAccess createCarriedSlotAccess() {
        return new SlotAccess(){

            @Override
            public ItemStack get() {
                return AbstractContainerMenu.this.getCarried();
            }

            @Override
            public boolean set(ItemStack $$0) {
                AbstractContainerMenu.this.setCarried($$0);
                return true;
            }
        };
    }

    public boolean canTakeItemForPickAll(ItemStack $$0, Slot $$1) {
        return true;
    }

    public void removed(Player $$0) {
        ItemStack $$1;
        if ($$0 instanceof ServerPlayer && !($$1 = this.getCarried()).isEmpty()) {
            if (!$$0.isAlive() || ((ServerPlayer)$$0).hasDisconnected()) {
                $$0.drop($$1, false);
            } else {
                $$0.getInventory().placeItemBackInInventory($$1);
            }
            this.setCarried(ItemStack.EMPTY);
        }
    }

    protected void clearContainer(Player $$0, Container $$1) {
        if (!$$0.isAlive() || $$0 instanceof ServerPlayer && ((ServerPlayer)$$0).hasDisconnected()) {
            for (int $$2 = 0; $$2 < $$1.getContainerSize(); ++$$2) {
                $$0.drop($$1.removeItemNoUpdate($$2), false);
            }
            return;
        }
        for (int $$3 = 0; $$3 < $$1.getContainerSize(); ++$$3) {
            Inventory $$4 = $$0.getInventory();
            if (!($$4.player instanceof ServerPlayer)) continue;
            $$4.placeItemBackInInventory($$1.removeItemNoUpdate($$3));
        }
    }

    public void slotsChanged(Container $$0) {
        this.broadcastChanges();
    }

    public void setItem(int $$0, int $$1, ItemStack $$2) {
        this.getSlot($$0).set($$2);
        this.stateId = $$1;
    }

    public void initializeContents(int $$0, List<ItemStack> $$1, ItemStack $$2) {
        for (int $$3 = 0; $$3 < $$1.size(); ++$$3) {
            this.getSlot($$3).initialize((ItemStack)$$1.get($$3));
        }
        this.carried = $$2;
        this.stateId = $$0;
    }

    public void setData(int $$0, int $$1) {
        ((DataSlot)this.dataSlots.get($$0)).set($$1);
    }

    public abstract boolean stillValid(Player var1);

    protected boolean moveItemStackTo(ItemStack $$0, int $$1, int $$2, boolean $$3) {
        boolean $$4 = false;
        int $$5 = $$1;
        if ($$3) {
            $$5 = $$2 - 1;
        }
        if ($$0.isStackable()) {
            while (!$$0.isEmpty() && ($$3 ? $$5 >= $$1 : $$5 < $$2)) {
                Slot $$6 = this.slots.get($$5);
                ItemStack $$7 = $$6.getItem();
                if (!$$7.isEmpty() && ItemStack.isSameItemSameTags($$0, $$7)) {
                    int $$8 = $$7.getCount() + $$0.getCount();
                    if ($$8 <= $$0.getMaxStackSize()) {
                        $$0.setCount(0);
                        $$7.setCount($$8);
                        $$6.setChanged();
                        $$4 = true;
                    } else if ($$7.getCount() < $$0.getMaxStackSize()) {
                        $$0.shrink($$0.getMaxStackSize() - $$7.getCount());
                        $$7.setCount($$0.getMaxStackSize());
                        $$6.setChanged();
                        $$4 = true;
                    }
                }
                if ($$3) {
                    --$$5;
                    continue;
                }
                ++$$5;
            }
        }
        if (!$$0.isEmpty()) {
            $$5 = $$3 ? $$2 - 1 : $$1;
            while ($$3 ? $$5 >= $$1 : $$5 < $$2) {
                Slot $$9 = this.slots.get($$5);
                ItemStack $$10 = $$9.getItem();
                if ($$10.isEmpty() && $$9.mayPlace($$0)) {
                    if ($$0.getCount() > $$9.getMaxStackSize()) {
                        $$9.set($$0.split($$9.getMaxStackSize()));
                    } else {
                        $$9.set($$0.split($$0.getCount()));
                    }
                    $$9.setChanged();
                    $$4 = true;
                    break;
                }
                if ($$3) {
                    --$$5;
                    continue;
                }
                ++$$5;
            }
        }
        return $$4;
    }

    public static int getQuickcraftType(int $$0) {
        return $$0 >> 2 & 3;
    }

    public static int getQuickcraftHeader(int $$0) {
        return $$0 & 3;
    }

    public static int getQuickcraftMask(int $$0, int $$1) {
        return $$0 & 3 | ($$1 & 3) << 2;
    }

    public static boolean isValidQuickcraftType(int $$0, Player $$1) {
        if ($$0 == 0) {
            return true;
        }
        if ($$0 == 1) {
            return true;
        }
        return $$0 == 2 && $$1.getAbilities().instabuild;
    }

    protected void resetQuickCraft() {
        this.quickcraftStatus = 0;
        this.quickcraftSlots.clear();
    }

    public static boolean canItemQuickReplace(@Nullable Slot $$0, ItemStack $$1, boolean $$2) {
        boolean $$3;
        boolean bl = $$3 = $$0 == null || !$$0.hasItem();
        if (!$$3 && ItemStack.isSameItemSameTags($$1, $$0.getItem())) {
            return $$0.getItem().getCount() + ($$2 ? 0 : $$1.getCount()) <= $$1.getMaxStackSize();
        }
        return $$3;
    }

    public static void getQuickCraftSlotCount(Set<Slot> $$0, int $$1, ItemStack $$2, int $$3) {
        switch ($$1) {
            case 0: {
                $$2.setCount(Mth.floor((float)$$2.getCount() / (float)$$0.size()));
                break;
            }
            case 1: {
                $$2.setCount(1);
                break;
            }
            case 2: {
                $$2.setCount($$2.getItem().getMaxStackSize());
            }
        }
        $$2.grow($$3);
    }

    public boolean canDragTo(Slot $$0) {
        return true;
    }

    public static int getRedstoneSignalFromBlockEntity(@Nullable BlockEntity $$0) {
        if ($$0 instanceof Container) {
            return AbstractContainerMenu.getRedstoneSignalFromContainer((Container)((Object)$$0));
        }
        return 0;
    }

    public static int getRedstoneSignalFromContainer(@Nullable Container $$0) {
        if ($$0 == null) {
            return 0;
        }
        int $$1 = 0;
        float $$2 = 0.0f;
        for (int $$3 = 0; $$3 < $$0.getContainerSize(); ++$$3) {
            ItemStack $$4 = $$0.getItem($$3);
            if ($$4.isEmpty()) continue;
            $$2 += (float)$$4.getCount() / (float)Math.min((int)$$0.getMaxStackSize(), (int)$$4.getMaxStackSize());
            ++$$1;
        }
        return Mth.floor(($$2 /= (float)$$0.getContainerSize()) * 14.0f) + ($$1 > 0 ? 1 : 0);
    }

    public void setCarried(ItemStack $$0) {
        this.carried = $$0;
    }

    public ItemStack getCarried() {
        return this.carried;
    }

    public void suppressRemoteUpdates() {
        this.suppressRemoteUpdates = true;
    }

    public void resumeRemoteUpdates() {
        this.suppressRemoteUpdates = false;
    }

    public void transferState(AbstractContainerMenu $$0) {
        HashBasedTable $$1 = HashBasedTable.create();
        for (int $$2 = 0; $$2 < $$0.slots.size(); ++$$2) {
            Slot $$3 = $$0.slots.get($$2);
            $$1.put((Object)$$3.container, (Object)$$3.getContainerSlot(), (Object)$$2);
        }
        for (int $$4 = 0; $$4 < this.slots.size(); ++$$4) {
            Slot $$5 = this.slots.get($$4);
            Integer $$6 = (Integer)$$1.get((Object)$$5.container, (Object)$$5.getContainerSlot());
            if ($$6 == null) continue;
            this.lastSlots.set($$4, $$0.lastSlots.get($$6));
            this.remoteSlots.set($$4, $$0.remoteSlots.get($$6));
        }
    }

    public OptionalInt findSlot(Container $$0, int $$1) {
        for (int $$2 = 0; $$2 < this.slots.size(); ++$$2) {
            Slot $$3 = this.slots.get($$2);
            if ($$3.container != $$0 || $$1 != $$3.getContainerSlot()) continue;
            return OptionalInt.of((int)$$2);
        }
        return OptionalInt.empty();
    }

    public int getStateId() {
        return this.stateId;
    }

    public int incrementStateId() {
        this.stateId = this.stateId + 1 & Short.MAX_VALUE;
        return this.stateId;
    }
}