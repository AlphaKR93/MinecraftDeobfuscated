/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Arrays
 *  java.util.Iterator
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.entity;

import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BrewingStandBlockEntity
extends BaseContainerBlockEntity
implements WorldlyContainer {
    private static final int INGREDIENT_SLOT = 3;
    private static final int FUEL_SLOT = 4;
    private static final int[] SLOTS_FOR_UP = new int[]{3};
    private static final int[] SLOTS_FOR_DOWN = new int[]{0, 1, 2, 3};
    private static final int[] SLOTS_FOR_SIDES = new int[]{0, 1, 2, 4};
    public static final int FUEL_USES = 20;
    public static final int DATA_BREW_TIME = 0;
    public static final int DATA_FUEL_USES = 1;
    public static final int NUM_DATA_VALUES = 2;
    private NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);
    int brewTime;
    private boolean[] lastPotionCount;
    private Item ingredient;
    int fuel;
    protected final ContainerData dataAccess = new ContainerData(){

        @Override
        public int get(int $$0) {
            switch ($$0) {
                case 0: {
                    return BrewingStandBlockEntity.this.brewTime;
                }
                case 1: {
                    return BrewingStandBlockEntity.this.fuel;
                }
            }
            return 0;
        }

        @Override
        public void set(int $$0, int $$1) {
            switch ($$0) {
                case 0: {
                    BrewingStandBlockEntity.this.brewTime = $$1;
                    break;
                }
                case 1: {
                    BrewingStandBlockEntity.this.fuel = $$1;
                }
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    };

    public BrewingStandBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.BREWING_STAND, $$0, $$1);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.brewing");
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    public boolean isEmpty() {
        Iterator iterator = this.items.iterator();
        while (iterator.hasNext()) {
            ItemStack $$0 = (ItemStack)iterator.next();
            if ($$0.isEmpty()) continue;
            return false;
        }
        return true;
    }

    public static void serverTick(Level $$0, BlockPos $$1, BlockState $$2, BrewingStandBlockEntity $$3) {
        ItemStack $$4 = $$3.items.get(4);
        if ($$3.fuel <= 0 && $$4.is(Items.BLAZE_POWDER)) {
            $$3.fuel = 20;
            $$4.shrink(1);
            BrewingStandBlockEntity.setChanged($$0, $$1, $$2);
        }
        boolean $$5 = BrewingStandBlockEntity.isBrewable($$3.items);
        boolean $$6 = $$3.brewTime > 0;
        ItemStack $$7 = $$3.items.get(3);
        if ($$6) {
            boolean $$8;
            --$$3.brewTime;
            boolean bl = $$8 = $$3.brewTime == 0;
            if ($$8 && $$5) {
                BrewingStandBlockEntity.doBrew($$0, $$1, $$3.items);
                BrewingStandBlockEntity.setChanged($$0, $$1, $$2);
            } else if (!$$5 || !$$7.is($$3.ingredient)) {
                $$3.brewTime = 0;
                BrewingStandBlockEntity.setChanged($$0, $$1, $$2);
            }
        } else if ($$5 && $$3.fuel > 0) {
            --$$3.fuel;
            $$3.brewTime = 400;
            $$3.ingredient = $$7.getItem();
            BrewingStandBlockEntity.setChanged($$0, $$1, $$2);
        }
        boolean[] $$9 = $$3.getPotionBits();
        if (!Arrays.equals((boolean[])$$9, (boolean[])$$3.lastPotionCount)) {
            $$3.lastPotionCount = $$9;
            BlockState $$10 = $$2;
            if (!($$10.getBlock() instanceof BrewingStandBlock)) {
                return;
            }
            for (int $$11 = 0; $$11 < BrewingStandBlock.HAS_BOTTLE.length; ++$$11) {
                $$10 = (BlockState)$$10.setValue(BrewingStandBlock.HAS_BOTTLE[$$11], $$9[$$11]);
            }
            $$0.setBlock($$1, $$10, 2);
        }
    }

    private boolean[] getPotionBits() {
        boolean[] $$0 = new boolean[3];
        for (int $$1 = 0; $$1 < 3; ++$$1) {
            if (this.items.get($$1).isEmpty()) continue;
            $$0[$$1] = true;
        }
        return $$0;
    }

    private static boolean isBrewable(NonNullList<ItemStack> $$0) {
        ItemStack $$1 = $$0.get(3);
        if ($$1.isEmpty()) {
            return false;
        }
        if (!PotionBrewing.isIngredient($$1)) {
            return false;
        }
        for (int $$2 = 0; $$2 < 3; ++$$2) {
            ItemStack $$3 = $$0.get($$2);
            if ($$3.isEmpty() || !PotionBrewing.hasMix($$3, $$1)) continue;
            return true;
        }
        return false;
    }

    private static void doBrew(Level $$0, BlockPos $$1, NonNullList<ItemStack> $$2) {
        ItemStack $$3 = $$2.get(3);
        for (int $$4 = 0; $$4 < 3; ++$$4) {
            $$2.set($$4, PotionBrewing.mix($$3, $$2.get($$4)));
        }
        $$3.shrink(1);
        if ($$3.getItem().hasCraftingRemainingItem()) {
            ItemStack $$5 = new ItemStack($$3.getItem().getCraftingRemainingItem());
            if ($$3.isEmpty()) {
                $$3 = $$5;
            } else {
                Containers.dropItemStack($$0, $$1.getX(), $$1.getY(), $$1.getZ(), $$5);
            }
        }
        $$2.set(3, $$3);
        $$0.levelEvent(1035, $$1, 0);
    }

    @Override
    public void load(CompoundTag $$0) {
        super.load($$0);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems($$0, this.items);
        this.brewTime = $$0.getShort("BrewTime");
        this.fuel = $$0.getByte("Fuel");
    }

    @Override
    protected void saveAdditional(CompoundTag $$0) {
        super.saveAdditional($$0);
        $$0.putShort("BrewTime", (short)this.brewTime);
        ContainerHelper.saveAllItems($$0, this.items);
        $$0.putByte("Fuel", (byte)this.fuel);
    }

    @Override
    public ItemStack getItem(int $$0) {
        if ($$0 >= 0 && $$0 < this.items.size()) {
            return this.items.get($$0);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItem(int $$0, int $$1) {
        return ContainerHelper.removeItem(this.items, $$0, $$1);
    }

    @Override
    public ItemStack removeItemNoUpdate(int $$0) {
        return ContainerHelper.takeItem(this.items, $$0);
    }

    @Override
    public void setItem(int $$0, ItemStack $$1) {
        if ($$0 >= 0 && $$0 < this.items.size()) {
            this.items.set($$0, $$1);
        }
    }

    @Override
    public boolean stillValid(Player $$0) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        }
        return !($$0.distanceToSqr((double)this.worldPosition.getX() + 0.5, (double)this.worldPosition.getY() + 0.5, (double)this.worldPosition.getZ() + 0.5) > 64.0);
    }

    @Override
    public boolean canPlaceItem(int $$0, ItemStack $$1) {
        if ($$0 == 3) {
            return PotionBrewing.isIngredient($$1);
        }
        if ($$0 == 4) {
            return $$1.is(Items.BLAZE_POWDER);
        }
        return ($$1.is(Items.POTION) || $$1.is(Items.SPLASH_POTION) || $$1.is(Items.LINGERING_POTION) || $$1.is(Items.GLASS_BOTTLE)) && this.getItem($$0).isEmpty();
    }

    @Override
    public int[] getSlotsForFace(Direction $$0) {
        if ($$0 == Direction.UP) {
            return SLOTS_FOR_UP;
        }
        if ($$0 == Direction.DOWN) {
            return SLOTS_FOR_DOWN;
        }
        return SLOTS_FOR_SIDES;
    }

    @Override
    public boolean canPlaceItemThroughFace(int $$0, ItemStack $$1, @Nullable Direction $$2) {
        return this.canPlaceItem($$0, $$1);
    }

    @Override
    public boolean canTakeItemThroughFace(int $$0, ItemStack $$1, Direction $$2) {
        if ($$0 == 3) {
            return $$1.is(Items.GLASS_BOTTLE);
        }
        return true;
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    protected AbstractContainerMenu createMenu(int $$0, Inventory $$1) {
        return new BrewingStandMenu($$0, $$1, this, this.dataAccess);
    }
}