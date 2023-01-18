/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.FurnaceFuelSlot;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;

public abstract class AbstractFurnaceMenu
extends RecipeBookMenu<Container> {
    public static final int INGREDIENT_SLOT = 0;
    public static final int FUEL_SLOT = 1;
    public static final int RESULT_SLOT = 2;
    public static final int SLOT_COUNT = 3;
    public static final int DATA_COUNT = 4;
    private static final int INV_SLOT_START = 3;
    private static final int INV_SLOT_END = 30;
    private static final int USE_ROW_SLOT_START = 30;
    private static final int USE_ROW_SLOT_END = 39;
    private final Container container;
    private final ContainerData data;
    protected final Level level;
    private final RecipeType<? extends AbstractCookingRecipe> recipeType;
    private final RecipeBookType recipeBookType;

    protected AbstractFurnaceMenu(MenuType<?> $$0, RecipeType<? extends AbstractCookingRecipe> $$1, RecipeBookType $$2, int $$3, Inventory $$4) {
        this($$0, $$1, $$2, $$3, $$4, new SimpleContainer(3), new SimpleContainerData(4));
    }

    protected AbstractFurnaceMenu(MenuType<?> $$0, RecipeType<? extends AbstractCookingRecipe> $$1, RecipeBookType $$2, int $$3, Inventory $$4, Container $$5, ContainerData $$6) {
        super($$0, $$3);
        this.recipeType = $$1;
        this.recipeBookType = $$2;
        AbstractFurnaceMenu.checkContainerSize($$5, 3);
        AbstractFurnaceMenu.checkContainerDataCount($$6, 4);
        this.container = $$5;
        this.data = $$6;
        this.level = $$4.player.level;
        this.addSlot(new Slot($$5, 0, 56, 17));
        this.addSlot(new FurnaceFuelSlot(this, $$5, 1, 56, 53));
        this.addSlot(new FurnaceResultSlot($$4.player, $$5, 2, 116, 35));
        for (int $$7 = 0; $$7 < 3; ++$$7) {
            for (int $$8 = 0; $$8 < 9; ++$$8) {
                this.addSlot(new Slot($$4, $$8 + $$7 * 9 + 9, 8 + $$8 * 18, 84 + $$7 * 18));
            }
        }
        for (int $$9 = 0; $$9 < 9; ++$$9) {
            this.addSlot(new Slot($$4, $$9, 8 + $$9 * 18, 142));
        }
        this.addDataSlots($$6);
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedContents $$0) {
        if (this.container instanceof StackedContentsCompatible) {
            ((StackedContentsCompatible)((Object)this.container)).fillStackedContents($$0);
        }
    }

    @Override
    public void clearCraftingContent() {
        this.getSlot(0).set(ItemStack.EMPTY);
        this.getSlot(2).set(ItemStack.EMPTY);
    }

    @Override
    public boolean recipeMatches(Recipe<? super Container> $$0) {
        return $$0.matches(this.container, this.level);
    }

    @Override
    public int getResultSlotIndex() {
        return 2;
    }

    @Override
    public int getGridWidth() {
        return 1;
    }

    @Override
    public int getGridHeight() {
        return 1;
    }

    @Override
    public int getSize() {
        return 3;
    }

    @Override
    public boolean stillValid(Player $$0) {
        return this.container.stillValid($$0);
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
            } else if ($$1 == 1 || $$1 == 0 ? !this.moveItemStackTo($$4, 3, 39, false) : (this.canSmelt($$4) ? !this.moveItemStackTo($$4, 0, 1, false) : (this.isFuel($$4) ? !this.moveItemStackTo($$4, 1, 2, false) : ($$1 >= 3 && $$1 < 30 ? !this.moveItemStackTo($$4, 30, 39, false) : $$1 >= 30 && $$1 < 39 && !this.moveItemStackTo($$4, 3, 30, false))))) {
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

    protected boolean canSmelt(ItemStack $$0) {
        return this.level.getRecipeManager().getRecipeFor(this.recipeType, new SimpleContainer($$0), this.level).isPresent();
    }

    protected boolean isFuel(ItemStack $$0) {
        return AbstractFurnaceBlockEntity.isFuel($$0);
    }

    public int getBurnProgress() {
        int $$0 = this.data.get(2);
        int $$1 = this.data.get(3);
        if ($$1 == 0 || $$0 == 0) {
            return 0;
        }
        return $$0 * 24 / $$1;
    }

    public int getLitProgress() {
        int $$0 = this.data.get(1);
        if ($$0 == 0) {
            $$0 = 200;
        }
        return this.data.get(0) * 13 / $$0;
    }

    public boolean isLit() {
        return this.data.get(0) > 0;
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return this.recipeBookType;
    }

    @Override
    public boolean shouldMoveToInventory(int $$0) {
        return $$0 != 1;
    }
}