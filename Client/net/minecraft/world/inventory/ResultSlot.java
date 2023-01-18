/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.inventory;

import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

public class ResultSlot
extends Slot {
    private final CraftingContainer craftSlots;
    private final Player player;
    private int removeCount;

    public ResultSlot(Player $$0, CraftingContainer $$1, Container $$2, int $$3, int $$4, int $$5) {
        super($$2, $$3, $$4, $$5);
        this.player = $$0;
        this.craftSlots = $$1;
    }

    @Override
    public boolean mayPlace(ItemStack $$0) {
        return false;
    }

    @Override
    public ItemStack remove(int $$0) {
        if (this.hasItem()) {
            this.removeCount += Math.min((int)$$0, (int)this.getItem().getCount());
        }
        return super.remove($$0);
    }

    @Override
    protected void onQuickCraft(ItemStack $$0, int $$1) {
        this.removeCount += $$1;
        this.checkTakeAchievements($$0);
    }

    @Override
    protected void onSwapCraft(int $$0) {
        this.removeCount += $$0;
    }

    @Override
    protected void checkTakeAchievements(ItemStack $$0) {
        if (this.removeCount > 0) {
            $$0.onCraftedBy(this.player.level, this.player, this.removeCount);
        }
        if (this.container instanceof RecipeHolder) {
            ((RecipeHolder)((Object)this.container)).awardUsedRecipes(this.player);
        }
        this.removeCount = 0;
    }

    @Override
    public void onTake(Player $$0, ItemStack $$1) {
        this.checkTakeAchievements($$1);
        NonNullList<ItemStack> $$2 = $$0.level.getRecipeManager().getRemainingItemsFor(RecipeType.CRAFTING, this.craftSlots, $$0.level);
        for (int $$3 = 0; $$3 < $$2.size(); ++$$3) {
            ItemStack $$4 = this.craftSlots.getItem($$3);
            ItemStack $$5 = $$2.get($$3);
            if (!$$4.isEmpty()) {
                this.craftSlots.removeItem($$3, 1);
                $$4 = this.craftSlots.getItem($$3);
            }
            if ($$5.isEmpty()) continue;
            if ($$4.isEmpty()) {
                this.craftSlots.setItem($$3, $$5);
                continue;
            }
            if (ItemStack.isSame($$4, $$5) && ItemStack.tagMatches($$4, $$5)) {
                $$5.grow($$4.getCount());
                this.craftSlots.setItem($$3, $$5);
                continue;
            }
            if (this.player.getInventory().add($$5)) continue;
            this.player.drop($$5, false);
        }
    }
}