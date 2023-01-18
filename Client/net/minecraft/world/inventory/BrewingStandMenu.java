/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.inventory;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;

public class BrewingStandMenu
extends AbstractContainerMenu {
    private static final int BOTTLE_SLOT_START = 0;
    private static final int BOTTLE_SLOT_END = 2;
    private static final int INGREDIENT_SLOT = 3;
    private static final int FUEL_SLOT = 4;
    private static final int SLOT_COUNT = 5;
    private static final int DATA_COUNT = 2;
    private static final int INV_SLOT_START = 5;
    private static final int INV_SLOT_END = 32;
    private static final int USE_ROW_SLOT_START = 32;
    private static final int USE_ROW_SLOT_END = 41;
    private final Container brewingStand;
    private final ContainerData brewingStandData;
    private final Slot ingredientSlot;

    public BrewingStandMenu(int $$0, Inventory $$1) {
        this($$0, $$1, new SimpleContainer(5), new SimpleContainerData(2));
    }

    public BrewingStandMenu(int $$0, Inventory $$1, Container $$2, ContainerData $$3) {
        super(MenuType.BREWING_STAND, $$0);
        BrewingStandMenu.checkContainerSize($$2, 5);
        BrewingStandMenu.checkContainerDataCount($$3, 2);
        this.brewingStand = $$2;
        this.brewingStandData = $$3;
        this.addSlot(new PotionSlot($$2, 0, 56, 51));
        this.addSlot(new PotionSlot($$2, 1, 79, 58));
        this.addSlot(new PotionSlot($$2, 2, 102, 51));
        this.ingredientSlot = this.addSlot(new IngredientsSlot($$2, 3, 79, 17));
        this.addSlot(new FuelSlot($$2, 4, 17, 17));
        this.addDataSlots($$3);
        for (int $$4 = 0; $$4 < 3; ++$$4) {
            for (int $$5 = 0; $$5 < 9; ++$$5) {
                this.addSlot(new Slot($$1, $$5 + $$4 * 9 + 9, 8 + $$5 * 18, 84 + $$4 * 18));
            }
        }
        for (int $$6 = 0; $$6 < 9; ++$$6) {
            this.addSlot(new Slot($$1, $$6, 8 + $$6 * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player $$0) {
        return this.brewingStand.stillValid($$0);
    }

    @Override
    public ItemStack quickMoveStack(Player $$0, int $$1) {
        ItemStack $$2 = ItemStack.EMPTY;
        Slot $$3 = (Slot)this.slots.get($$1);
        if ($$3 != null && $$3.hasItem()) {
            ItemStack $$4 = $$3.getItem();
            $$2 = $$4.copy();
            if ($$1 >= 0 && $$1 <= 2 || $$1 == 3 || $$1 == 4) {
                if (!this.moveItemStackTo($$4, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
                $$3.onQuickCraft($$4, $$2);
            } else if (FuelSlot.mayPlaceItem($$2) ? this.moveItemStackTo($$4, 4, 5, false) || this.ingredientSlot.mayPlace($$4) && !this.moveItemStackTo($$4, 3, 4, false) : (this.ingredientSlot.mayPlace($$4) ? !this.moveItemStackTo($$4, 3, 4, false) : (PotionSlot.mayPlaceItem($$2) && $$2.getCount() == 1 ? !this.moveItemStackTo($$4, 0, 3, false) : ($$1 >= 5 && $$1 < 32 ? !this.moveItemStackTo($$4, 32, 41, false) : ($$1 >= 32 && $$1 < 41 ? !this.moveItemStackTo($$4, 5, 32, false) : !this.moveItemStackTo($$4, 5, 41, false)))))) {
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

    public int getFuel() {
        return this.brewingStandData.get(1);
    }

    public int getBrewingTicks() {
        return this.brewingStandData.get(0);
    }

    static class PotionSlot
    extends Slot {
        public PotionSlot(Container $$0, int $$1, int $$2, int $$3) {
            super($$0, $$1, $$2, $$3);
        }

        @Override
        public boolean mayPlace(ItemStack $$0) {
            return PotionSlot.mayPlaceItem($$0);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public void onTake(Player $$0, ItemStack $$1) {
            Potion $$2 = PotionUtils.getPotion($$1);
            if ($$0 instanceof ServerPlayer) {
                CriteriaTriggers.BREWED_POTION.trigger((ServerPlayer)$$0, $$2);
            }
            super.onTake($$0, $$1);
        }

        public static boolean mayPlaceItem(ItemStack $$0) {
            return $$0.is(Items.POTION) || $$0.is(Items.SPLASH_POTION) || $$0.is(Items.LINGERING_POTION) || $$0.is(Items.GLASS_BOTTLE);
        }
    }

    static class IngredientsSlot
    extends Slot {
        public IngredientsSlot(Container $$0, int $$1, int $$2, int $$3) {
            super($$0, $$1, $$2, $$3);
        }

        @Override
        public boolean mayPlace(ItemStack $$0) {
            return PotionBrewing.isIngredient($$0);
        }

        @Override
        public int getMaxStackSize() {
            return 64;
        }
    }

    static class FuelSlot
    extends Slot {
        public FuelSlot(Container $$0, int $$1, int $$2, int $$3) {
            super($$0, $$1, $$2, $$3);
        }

        @Override
        public boolean mayPlace(ItemStack $$0) {
            return FuelSlot.mayPlaceItem($$0);
        }

        public static boolean mayPlaceItem(ItemStack $$0) {
            return $$0.is(Items.BLAZE_POWDER);
        }

        @Override
        public int getMaxStackSize() {
            return 64;
        }
    }
}