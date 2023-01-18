/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Throwable
 *  java.util.Iterator
 *  java.util.List
 *  java.util.function.Predicate
 */
package net.minecraft.world.entity.player;

import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Nameable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class Inventory
implements Container,
Nameable {
    public static final int POP_TIME_DURATION = 5;
    public static final int INVENTORY_SIZE = 36;
    private static final int SELECTION_SIZE = 9;
    public static final int SLOT_OFFHAND = 40;
    public static final int NOT_FOUND_INDEX = -1;
    public static final int[] ALL_ARMOR_SLOTS = new int[]{0, 1, 2, 3};
    public static final int[] HELMET_SLOT_ONLY = new int[]{3};
    public final NonNullList<ItemStack> items = NonNullList.withSize(36, ItemStack.EMPTY);
    public final NonNullList<ItemStack> armor = NonNullList.withSize(4, ItemStack.EMPTY);
    public final NonNullList<ItemStack> offhand = NonNullList.withSize(1, ItemStack.EMPTY);
    private final List<NonNullList<ItemStack>> compartments = ImmutableList.of(this.items, this.armor, this.offhand);
    public int selected;
    public final Player player;
    private int timesChanged;

    public Inventory(Player $$0) {
        this.player = $$0;
    }

    public ItemStack getSelected() {
        if (Inventory.isHotbarSlot(this.selected)) {
            return this.items.get(this.selected);
        }
        return ItemStack.EMPTY;
    }

    public static int getSelectionSize() {
        return 9;
    }

    private boolean hasRemainingSpaceForItem(ItemStack $$0, ItemStack $$1) {
        return !$$0.isEmpty() && ItemStack.isSameItemSameTags($$0, $$1) && $$0.isStackable() && $$0.getCount() < $$0.getMaxStackSize() && $$0.getCount() < this.getMaxStackSize();
    }

    public int getFreeSlot() {
        for (int $$0 = 0; $$0 < this.items.size(); ++$$0) {
            if (!this.items.get($$0).isEmpty()) continue;
            return $$0;
        }
        return -1;
    }

    public void setPickedItem(ItemStack $$0) {
        int $$1 = this.findSlotMatchingItem($$0);
        if (Inventory.isHotbarSlot($$1)) {
            this.selected = $$1;
            return;
        }
        if ($$1 == -1) {
            int $$2;
            this.selected = this.getSuitableHotbarSlot();
            if (!this.items.get(this.selected).isEmpty() && ($$2 = this.getFreeSlot()) != -1) {
                this.items.set($$2, this.items.get(this.selected));
            }
            this.items.set(this.selected, $$0);
        } else {
            this.pickSlot($$1);
        }
    }

    public void pickSlot(int $$0) {
        this.selected = this.getSuitableHotbarSlot();
        ItemStack $$1 = this.items.get(this.selected);
        this.items.set(this.selected, this.items.get($$0));
        this.items.set($$0, $$1);
    }

    public static boolean isHotbarSlot(int $$0) {
        return $$0 >= 0 && $$0 < 9;
    }

    public int findSlotMatchingItem(ItemStack $$0) {
        for (int $$1 = 0; $$1 < this.items.size(); ++$$1) {
            if (this.items.get($$1).isEmpty() || !ItemStack.isSameItemSameTags($$0, this.items.get($$1))) continue;
            return $$1;
        }
        return -1;
    }

    public int findSlotMatchingUnusedItem(ItemStack $$0) {
        for (int $$1 = 0; $$1 < this.items.size(); ++$$1) {
            ItemStack $$2 = this.items.get($$1);
            if (this.items.get($$1).isEmpty() || !ItemStack.isSameItemSameTags($$0, this.items.get($$1)) || this.items.get($$1).isDamaged() || $$2.isEnchanted() || $$2.hasCustomHoverName()) continue;
            return $$1;
        }
        return -1;
    }

    public int getSuitableHotbarSlot() {
        for (int $$0 = 0; $$0 < 9; ++$$0) {
            int $$1 = (this.selected + $$0) % 9;
            if (!this.items.get($$1).isEmpty()) continue;
            return $$1;
        }
        for (int $$2 = 0; $$2 < 9; ++$$2) {
            int $$3 = (this.selected + $$2) % 9;
            if (this.items.get($$3).isEnchanted()) continue;
            return $$3;
        }
        return this.selected;
    }

    public void swapPaint(double $$0) {
        int $$1 = (int)Math.signum((double)$$0);
        this.selected -= $$1;
        while (this.selected < 0) {
            this.selected += 9;
        }
        while (this.selected >= 9) {
            this.selected -= 9;
        }
    }

    public int clearOrCountMatchingItems(Predicate<ItemStack> $$0, int $$1, Container $$2) {
        int $$3 = 0;
        boolean $$4 = $$1 == 0;
        $$3 += ContainerHelper.clearOrCountMatchingItems(this, $$0, $$1 - $$3, $$4);
        $$3 += ContainerHelper.clearOrCountMatchingItems($$2, $$0, $$1 - $$3, $$4);
        ItemStack $$5 = this.player.containerMenu.getCarried();
        $$3 += ContainerHelper.clearOrCountMatchingItems($$5, $$0, $$1 - $$3, $$4);
        if ($$5.isEmpty()) {
            this.player.containerMenu.setCarried(ItemStack.EMPTY);
        }
        return $$3;
    }

    private int addResource(ItemStack $$0) {
        int $$1 = this.getSlotWithRemainingSpace($$0);
        if ($$1 == -1) {
            $$1 = this.getFreeSlot();
        }
        if ($$1 == -1) {
            return $$0.getCount();
        }
        return this.addResource($$1, $$0);
    }

    private int addResource(int $$0, ItemStack $$1) {
        int $$5;
        Item $$2 = $$1.getItem();
        int $$3 = $$1.getCount();
        ItemStack $$4 = this.getItem($$0);
        if ($$4.isEmpty()) {
            $$4 = new ItemStack($$2, 0);
            if ($$1.hasTag()) {
                $$4.setTag($$1.getTag().copy());
            }
            this.setItem($$0, $$4);
        }
        if (($$5 = $$3) > $$4.getMaxStackSize() - $$4.getCount()) {
            $$5 = $$4.getMaxStackSize() - $$4.getCount();
        }
        if ($$5 > this.getMaxStackSize() - $$4.getCount()) {
            $$5 = this.getMaxStackSize() - $$4.getCount();
        }
        if ($$5 == 0) {
            return $$3;
        }
        $$4.grow($$5);
        $$4.setPopTime(5);
        return $$3 -= $$5;
    }

    public int getSlotWithRemainingSpace(ItemStack $$0) {
        if (this.hasRemainingSpaceForItem(this.getItem(this.selected), $$0)) {
            return this.selected;
        }
        if (this.hasRemainingSpaceForItem(this.getItem(40), $$0)) {
            return 40;
        }
        for (int $$1 = 0; $$1 < this.items.size(); ++$$1) {
            if (!this.hasRemainingSpaceForItem(this.items.get($$1), $$0)) continue;
            return $$1;
        }
        return -1;
    }

    public void tick() {
        for (NonNullList $$0 : this.compartments) {
            for (int $$1 = 0; $$1 < $$0.size(); ++$$1) {
                if (((ItemStack)$$0.get($$1)).isEmpty()) continue;
                ((ItemStack)$$0.get($$1)).inventoryTick(this.player.level, this.player, $$1, this.selected == $$1);
            }
        }
    }

    public boolean add(ItemStack $$0) {
        return this.add(-1, $$0);
    }

    public boolean add(int $$0, ItemStack $$1) {
        if ($$1.isEmpty()) {
            return false;
        }
        try {
            if (!$$1.isDamaged()) {
                int $$2;
                do {
                    $$2 = $$1.getCount();
                    if ($$0 == -1) {
                        $$1.setCount(this.addResource($$1));
                        continue;
                    }
                    $$1.setCount(this.addResource($$0, $$1));
                } while (!$$1.isEmpty() && $$1.getCount() < $$2);
                if ($$1.getCount() == $$2 && this.player.getAbilities().instabuild) {
                    $$1.setCount(0);
                    return true;
                }
                return $$1.getCount() < $$2;
            }
            if ($$0 == -1) {
                $$0 = this.getFreeSlot();
            }
            if ($$0 >= 0) {
                this.items.set($$0, $$1.copy());
                this.items.get($$0).setPopTime(5);
                $$1.setCount(0);
                return true;
            }
            if (this.player.getAbilities().instabuild) {
                $$1.setCount(0);
                return true;
            }
            return false;
        }
        catch (Throwable $$3) {
            CrashReport $$4 = CrashReport.forThrowable($$3, "Adding item to inventory");
            CrashReportCategory $$5 = $$4.addCategory("Item being added");
            $$5.setDetail("Item ID", Item.getId($$1.getItem()));
            $$5.setDetail("Item data", $$1.getDamageValue());
            $$5.setDetail("Item name", () -> $$1.getHoverName().getString());
            throw new ReportedException($$4);
        }
    }

    public void placeItemBackInInventory(ItemStack $$0) {
        this.placeItemBackInInventory($$0, true);
    }

    public void placeItemBackInInventory(ItemStack $$0, boolean $$1) {
        while (!$$0.isEmpty()) {
            int $$2 = this.getSlotWithRemainingSpace($$0);
            if ($$2 == -1) {
                $$2 = this.getFreeSlot();
            }
            if ($$2 == -1) {
                this.player.drop($$0, false);
                break;
            }
            int $$3 = $$0.getMaxStackSize() - this.getItem($$2).getCount();
            if (!this.add($$2, $$0.split($$3)) || !$$1 || !(this.player instanceof ServerPlayer)) continue;
            ((ServerPlayer)this.player).connection.send(new ClientboundContainerSetSlotPacket(-2, 0, $$2, this.getItem($$2)));
        }
    }

    @Override
    public ItemStack removeItem(int $$0, int $$1) {
        NonNullList $$2 = null;
        for (NonNullList $$3 : this.compartments) {
            if ($$0 < $$3.size()) {
                $$2 = $$3;
                break;
            }
            $$0 -= $$3.size();
        }
        if ($$2 != null && !((ItemStack)$$2.get($$0)).isEmpty()) {
            return ContainerHelper.removeItem((List<ItemStack>)$$2, $$0, $$1);
        }
        return ItemStack.EMPTY;
    }

    public void removeItem(ItemStack $$0) {
        block0: for (NonNullList $$1 : this.compartments) {
            for (int $$2 = 0; $$2 < $$1.size(); ++$$2) {
                if ($$1.get($$2) != $$0) continue;
                $$1.set($$2, ItemStack.EMPTY);
                continue block0;
            }
        }
    }

    @Override
    public ItemStack removeItemNoUpdate(int $$0) {
        NonNullList $$1 = null;
        for (NonNullList $$2 : this.compartments) {
            if ($$0 < $$2.size()) {
                $$1 = $$2;
                break;
            }
            $$0 -= $$2.size();
        }
        if ($$1 != null && !((ItemStack)$$1.get($$0)).isEmpty()) {
            ItemStack $$3 = (ItemStack)$$1.get($$0);
            $$1.set($$0, ItemStack.EMPTY);
            return $$3;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int $$0, ItemStack $$1) {
        NonNullList $$2 = null;
        for (NonNullList $$3 : this.compartments) {
            if ($$0 < $$3.size()) {
                $$2 = $$3;
                break;
            }
            $$0 -= $$3.size();
        }
        if ($$2 != null) {
            $$2.set($$0, $$1);
        }
    }

    public float getDestroySpeed(BlockState $$0) {
        return this.items.get(this.selected).getDestroySpeed($$0);
    }

    public ListTag save(ListTag $$0) {
        for (int $$1 = 0; $$1 < this.items.size(); ++$$1) {
            if (this.items.get($$1).isEmpty()) continue;
            CompoundTag $$2 = new CompoundTag();
            $$2.putByte("Slot", (byte)$$1);
            this.items.get($$1).save($$2);
            $$0.add($$2);
        }
        for (int $$3 = 0; $$3 < this.armor.size(); ++$$3) {
            if (this.armor.get($$3).isEmpty()) continue;
            CompoundTag $$4 = new CompoundTag();
            $$4.putByte("Slot", (byte)($$3 + 100));
            this.armor.get($$3).save($$4);
            $$0.add($$4);
        }
        for (int $$5 = 0; $$5 < this.offhand.size(); ++$$5) {
            if (this.offhand.get($$5).isEmpty()) continue;
            CompoundTag $$6 = new CompoundTag();
            $$6.putByte("Slot", (byte)($$5 + 150));
            this.offhand.get($$5).save($$6);
            $$0.add($$6);
        }
        return $$0;
    }

    public void load(ListTag $$0) {
        this.items.clear();
        this.armor.clear();
        this.offhand.clear();
        for (int $$1 = 0; $$1 < $$0.size(); ++$$1) {
            CompoundTag $$2 = $$0.getCompound($$1);
            int $$3 = $$2.getByte("Slot") & 0xFF;
            ItemStack $$4 = ItemStack.of($$2);
            if ($$4.isEmpty()) continue;
            if ($$3 >= 0 && $$3 < this.items.size()) {
                this.items.set($$3, $$4);
                continue;
            }
            if ($$3 >= 100 && $$3 < this.armor.size() + 100) {
                this.armor.set($$3 - 100, $$4);
                continue;
            }
            if ($$3 < 150 || $$3 >= this.offhand.size() + 150) continue;
            this.offhand.set($$3 - 150, $$4);
        }
    }

    @Override
    public int getContainerSize() {
        return this.items.size() + this.armor.size() + this.offhand.size();
    }

    @Override
    public boolean isEmpty() {
        Iterator iterator = this.items.iterator();
        while (iterator.hasNext()) {
            ItemStack $$0 = (ItemStack)iterator.next();
            if ($$0.isEmpty()) continue;
            return false;
        }
        iterator = this.armor.iterator();
        while (iterator.hasNext()) {
            ItemStack $$1 = (ItemStack)iterator.next();
            if ($$1.isEmpty()) continue;
            return false;
        }
        iterator = this.offhand.iterator();
        while (iterator.hasNext()) {
            ItemStack $$2 = (ItemStack)iterator.next();
            if ($$2.isEmpty()) continue;
            return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int $$0) {
        NonNullList $$1 = null;
        for (NonNullList $$2 : this.compartments) {
            if ($$0 < $$2.size()) {
                $$1 = $$2;
                break;
            }
            $$0 -= $$2.size();
        }
        return $$1 == null ? ItemStack.EMPTY : (ItemStack)$$1.get($$0);
    }

    @Override
    public Component getName() {
        return Component.translatable("container.inventory");
    }

    public ItemStack getArmor(int $$0) {
        return this.armor.get($$0);
    }

    public void hurtArmor(DamageSource $$0, float $$12, int[] $$2) {
        if ($$12 <= 0.0f) {
            return;
        }
        if (($$12 /= 4.0f) < 1.0f) {
            $$12 = 1.0f;
        }
        for (int $$3 : $$2) {
            ItemStack $$4 = this.armor.get($$3);
            if ($$0.isFire() && $$4.getItem().isFireResistant() || !($$4.getItem() instanceof ArmorItem)) continue;
            $$4.hurtAndBreak((int)$$12, this.player, $$1 -> $$1.broadcastBreakEvent(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, $$3)));
        }
    }

    public void dropAll() {
        for (List $$0 : this.compartments) {
            for (int $$1 = 0; $$1 < $$0.size(); ++$$1) {
                ItemStack $$2 = (ItemStack)$$0.get($$1);
                if ($$2.isEmpty()) continue;
                this.player.drop($$2, true, false);
                $$0.set($$1, (Object)ItemStack.EMPTY);
            }
        }
    }

    @Override
    public void setChanged() {
        ++this.timesChanged;
    }

    public int getTimesChanged() {
        return this.timesChanged;
    }

    @Override
    public boolean stillValid(Player $$0) {
        if (this.player.isRemoved()) {
            return false;
        }
        return !($$0.distanceToSqr(this.player) > 64.0);
    }

    public boolean contains(ItemStack $$0) {
        for (List $$1 : this.compartments) {
            for (ItemStack $$2 : $$1) {
                if ($$2.isEmpty() || !ItemStack.isSameItemSameTags($$2, $$0)) continue;
                return true;
            }
        }
        return false;
    }

    public boolean contains(TagKey<Item> $$0) {
        for (List $$1 : this.compartments) {
            for (ItemStack $$2 : $$1) {
                if ($$2.isEmpty() || !$$2.is($$0)) continue;
                return true;
            }
        }
        return false;
    }

    public void replaceWith(Inventory $$0) {
        for (int $$1 = 0; $$1 < this.getContainerSize(); ++$$1) {
            this.setItem($$1, $$0.getItem($$1));
        }
        this.selected = $$0.selected;
    }

    @Override
    public void clearContent() {
        for (List $$0 : this.compartments) {
            $$0.clear();
        }
    }

    public void fillStackedContents(StackedContents $$0) {
        Iterator iterator = this.items.iterator();
        while (iterator.hasNext()) {
            ItemStack $$1 = (ItemStack)iterator.next();
            $$0.accountSimpleStack($$1);
        }
    }

    public ItemStack removeFromSelected(boolean $$0) {
        ItemStack $$1 = this.getSelected();
        if ($$1.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return this.removeItem(this.selected, $$0 ? $$1.getCount() : 1);
    }
}