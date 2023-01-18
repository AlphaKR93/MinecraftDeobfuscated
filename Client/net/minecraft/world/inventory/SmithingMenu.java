/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.function.BiConsumer
 *  javax.annotation.Nullable
 */
package net.minecraft.world.inventory;

import java.util.List;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SmithingMenu
extends ItemCombinerMenu {
    private final Level level;
    @Nullable
    private UpgradeRecipe selectedRecipe;
    private final List<UpgradeRecipe> recipes;

    public SmithingMenu(int $$0, Inventory $$1) {
        this($$0, $$1, ContainerLevelAccess.NULL);
    }

    public SmithingMenu(int $$0, Inventory $$1, ContainerLevelAccess $$2) {
        super(MenuType.SMITHING, $$0, $$1, $$2);
        this.level = $$1.player.level;
        this.recipes = this.level.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING);
    }

    @Override
    protected boolean isValidBlock(BlockState $$0) {
        return $$0.is(Blocks.SMITHING_TABLE);
    }

    @Override
    protected boolean mayPickup(Player $$0, boolean $$1) {
        return this.selectedRecipe != null && this.selectedRecipe.matches(this.inputSlots, this.level);
    }

    @Override
    protected void onTake(Player $$02, ItemStack $$12) {
        $$12.onCraftedBy($$02.level, $$02, $$12.getCount());
        this.resultSlots.awardUsedRecipes($$02);
        this.shrinkStackInSlot(0);
        this.shrinkStackInSlot(1);
        this.access.execute((BiConsumer<Level, BlockPos>)((BiConsumer)($$0, $$1) -> $$0.levelEvent(1044, $$1, 0)));
    }

    private void shrinkStackInSlot(int $$0) {
        ItemStack $$1 = this.inputSlots.getItem($$0);
        $$1.shrink(1);
        this.inputSlots.setItem($$0, $$1);
    }

    @Override
    public void createResult() {
        List<UpgradeRecipe> $$0 = this.level.getRecipeManager().getRecipesFor(RecipeType.SMITHING, this.inputSlots, this.level);
        if ($$0.isEmpty()) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
        } else {
            UpgradeRecipe $$1 = (UpgradeRecipe)$$0.get(0);
            ItemStack $$2 = $$1.assemble(this.inputSlots);
            if ($$2.isItemEnabled(this.level.enabledFeatures())) {
                this.selectedRecipe = $$1;
                this.resultSlots.setRecipeUsed($$1);
                this.resultSlots.setItem(0, $$2);
            }
        }
    }

    @Override
    protected boolean shouldQuickMoveToAdditionalSlot(ItemStack $$0) {
        return this.recipes.stream().anyMatch($$1 -> $$1.isAdditionIngredient($$0));
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack $$0, Slot $$1) {
        return $$1.container != this.resultSlots && super.canTakeItemForPickAll($$0, $$1);
    }
}