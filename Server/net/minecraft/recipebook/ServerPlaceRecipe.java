/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.ints.IntList
 *  it.unimi.dsi.fastutil.ints.IntListIterator
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.Iterator
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.recipebook;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.ArrayList;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket;
import net.minecraft.recipebook.PlaceRecipe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.slf4j.Logger;

public class ServerPlaceRecipe<C extends Container>
implements PlaceRecipe<Integer> {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final StackedContents stackedContents = new StackedContents();
    protected Inventory inventory;
    protected RecipeBookMenu<C> menu;

    public ServerPlaceRecipe(RecipeBookMenu<C> $$0) {
        this.menu = $$0;
    }

    public void recipeClicked(ServerPlayer $$0, @Nullable Recipe<C> $$1, boolean $$2) {
        if ($$1 == null || !$$0.getRecipeBook().contains($$1)) {
            return;
        }
        this.inventory = $$0.getInventory();
        if (!this.testClearGrid() && !$$0.isCreative()) {
            return;
        }
        this.stackedContents.clear();
        $$0.getInventory().fillStackedContents(this.stackedContents);
        this.menu.fillCraftSlotsStackedContents(this.stackedContents);
        if (this.stackedContents.canCraft($$1, null)) {
            this.handleRecipeClicked($$1, $$2);
        } else {
            this.clearGrid(true);
            $$0.connection.send(new ClientboundPlaceGhostRecipePacket($$0.containerMenu.containerId, $$1));
        }
        $$0.getInventory().setChanged();
    }

    protected void clearGrid(boolean $$0) {
        for (int $$1 = 0; $$1 < this.menu.getSize(); ++$$1) {
            if (!this.menu.shouldMoveToInventory($$1)) continue;
            ItemStack $$2 = this.menu.getSlot($$1).getItem().copy();
            this.inventory.placeItemBackInInventory($$2, false);
            this.menu.getSlot($$1).set($$2);
        }
        this.menu.clearCraftingContent();
    }

    protected void handleRecipeClicked(Recipe<C> $$0, boolean $$1) {
        int $$6;
        IntArrayList $$7;
        boolean $$2 = this.menu.recipeMatches($$0);
        int $$3 = this.stackedContents.getBiggestCraftableStack($$0, null);
        if ($$2) {
            for (int $$4 = 0; $$4 < this.menu.getGridHeight() * this.menu.getGridWidth() + 1; ++$$4) {
                ItemStack $$5;
                if ($$4 == this.menu.getResultSlotIndex() || ($$5 = this.menu.getSlot($$4).getItem()).isEmpty() || Math.min((int)$$3, (int)$$5.getMaxStackSize()) >= $$5.getCount() + 1) continue;
                return;
            }
        }
        if (this.stackedContents.canCraft($$0, (IntList)($$7 = new IntArrayList()), $$6 = this.getStackSize($$1, $$3, $$2))) {
            int $$8 = $$6;
            IntListIterator intListIterator = $$7.iterator();
            while (intListIterator.hasNext()) {
                int $$9 = (Integer)intListIterator.next();
                int $$10 = StackedContents.fromStackingIndex($$9).getMaxStackSize();
                if ($$10 >= $$8) continue;
                $$8 = $$10;
            }
            $$6 = $$8;
            if (this.stackedContents.canCraft($$0, (IntList)$$7, $$6)) {
                this.clearGrid(false);
                this.placeRecipe(this.menu.getGridWidth(), this.menu.getGridHeight(), this.menu.getResultSlotIndex(), $$0, (Iterator)$$7.iterator(), $$6);
            }
        }
    }

    @Override
    public void addItemToSlot(Iterator<Integer> $$0, int $$1, int $$2, int $$3, int $$4) {
        Slot $$5 = this.menu.getSlot($$1);
        ItemStack $$6 = StackedContents.fromStackingIndex((Integer)$$0.next());
        if (!$$6.isEmpty()) {
            for (int $$7 = 0; $$7 < $$2; ++$$7) {
                this.moveItemToGrid($$5, $$6);
            }
        }
    }

    protected int getStackSize(boolean $$0, int $$1, boolean $$2) {
        int $$3 = 1;
        if ($$0) {
            $$3 = $$1;
        } else if ($$2) {
            $$3 = 64;
            for (int $$4 = 0; $$4 < this.menu.getGridWidth() * this.menu.getGridHeight() + 1; ++$$4) {
                ItemStack $$5;
                if ($$4 == this.menu.getResultSlotIndex() || ($$5 = this.menu.getSlot($$4).getItem()).isEmpty() || $$3 <= $$5.getCount()) continue;
                $$3 = $$5.getCount();
            }
            if ($$3 < 64) {
                ++$$3;
            }
        }
        return $$3;
    }

    protected void moveItemToGrid(Slot $$0, ItemStack $$1) {
        int $$2 = this.inventory.findSlotMatchingUnusedItem($$1);
        if ($$2 == -1) {
            return;
        }
        ItemStack $$3 = this.inventory.getItem($$2).copy();
        if ($$3.isEmpty()) {
            return;
        }
        if ($$3.getCount() > 1) {
            this.inventory.removeItem($$2, 1);
        } else {
            this.inventory.removeItemNoUpdate($$2);
        }
        $$3.setCount(1);
        if ($$0.getItem().isEmpty()) {
            $$0.set($$3);
        } else {
            $$0.getItem().grow(1);
        }
    }

    private boolean testClearGrid() {
        ArrayList $$0 = Lists.newArrayList();
        int $$1 = this.getAmountOfFreeSlotsInInventory();
        for (int $$2 = 0; $$2 < this.menu.getGridWidth() * this.menu.getGridHeight() + 1; ++$$2) {
            ItemStack $$3;
            if ($$2 == this.menu.getResultSlotIndex() || ($$3 = this.menu.getSlot($$2).getItem().copy()).isEmpty()) continue;
            int $$4 = this.inventory.getSlotWithRemainingSpace($$3);
            if ($$4 == -1 && $$0.size() <= $$1) {
                for (ItemStack $$5 : $$0) {
                    if (!$$5.sameItem($$3) || $$5.getCount() == $$5.getMaxStackSize() || $$5.getCount() + $$3.getCount() > $$5.getMaxStackSize()) continue;
                    $$5.grow($$3.getCount());
                    $$3.setCount(0);
                    break;
                }
                if ($$3.isEmpty()) continue;
                if ($$0.size() < $$1) {
                    $$0.add((Object)$$3);
                    continue;
                }
                return false;
            }
            if ($$4 != -1) continue;
            return false;
        }
        return true;
    }

    private int getAmountOfFreeSlotsInInventory() {
        int $$0 = 0;
        Iterator iterator = this.inventory.items.iterator();
        while (iterator.hasNext()) {
            ItemStack $$1 = (ItemStack)iterator.next();
            if (!$$1.isEmpty()) continue;
            ++$$0;
        }
        return $$0;
    }
}