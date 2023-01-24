/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Optional
 *  java.util.function.BiConsumer
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.inventory;

import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SmithingMenu
extends ItemCombinerMenu {
    public static final int TEMPLATE_SLOT = 0;
    public static final int BASE_SLOT = 1;
    public static final int ADDITIONAL_SLOT = 2;
    public static final int RESULT_SLOT = 3;
    public static final int TEMPLATE_SLOT_X_PLACEMENT = 8;
    public static final int BASE_SLOT_X_PLACEMENT = 26;
    public static final int ADDITIONAL_SLOT_X_PLACEMENT = 44;
    private static final int RESULT_SLOT_X_PLACEMENT = 98;
    public static final int SLOT_Y_PLACEMENT = 48;
    private final Level level;
    @Nullable
    private SmithingRecipe selectedRecipe;
    private final List<SmithingRecipe> recipes;

    public SmithingMenu(int $$0, Inventory $$1) {
        this($$0, $$1, ContainerLevelAccess.NULL);
    }

    public SmithingMenu(int $$0, Inventory $$1, ContainerLevelAccess $$2) {
        super(MenuType.SMITHING, $$0, $$1, $$2);
        this.level = $$1.player.level;
        this.recipes = this.level.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING);
    }

    @Override
    protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create().withSlot(0, 8, 48, (Predicate<ItemStack>)((Predicate)$$0 -> this.recipes.stream().anyMatch($$1 -> $$1.isTemplateIngredient((ItemStack)$$0)))).withSlot(1, 26, 48, (Predicate<ItemStack>)((Predicate)$$0 -> this.recipes.stream().anyMatch($$1 -> $$1.isBaseIngredient((ItemStack)$$0) && $$1.isTemplateIngredient(((Slot)this.slots.get(0)).getItem())))).withSlot(2, 44, 48, (Predicate<ItemStack>)((Predicate)$$0 -> this.recipes.stream().anyMatch($$1 -> $$1.isAdditionIngredient((ItemStack)$$0) && $$1.isTemplateIngredient(((Slot)this.slots.get(0)).getItem())))).withResultSlot(3, 98, 48).build();
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
        this.shrinkStackInSlot(2);
        this.access.execute((BiConsumer<Level, BlockPos>)((BiConsumer)($$0, $$1) -> $$0.levelEvent(1044, $$1, 0)));
    }

    private void shrinkStackInSlot(int $$0) {
        ItemStack $$1 = this.inputSlots.getItem($$0);
        $$1.shrink(1);
        this.inputSlots.setItem($$0, $$1);
    }

    @Override
    public void createResult() {
        List<SmithingRecipe> $$0 = this.level.getRecipeManager().getRecipesFor(RecipeType.SMITHING, this.inputSlots, this.level);
        if ($$0.isEmpty()) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
        } else {
            SmithingRecipe $$1 = (SmithingRecipe)$$0.get(0);
            ItemStack $$2 = $$1.assemble(this.inputSlots, this.level.registryAccess());
            if ($$2.isItemEnabled(this.level.enabledFeatures())) {
                this.selectedRecipe = $$1;
                this.resultSlots.setRecipeUsed($$1);
                this.resultSlots.setItem(0, $$2);
            }
        }
    }

    @Override
    public int getSlotToQuickMoveTo(ItemStack $$0) {
        return (Integer)((Optional)this.recipes.stream().map($$1 -> SmithingMenu.findSlotMatchingIngredient($$1, $$0)).filter(Optional::isPresent).findFirst().orElse((Object)Optional.of((Object)0))).get();
    }

    private static Optional<Integer> findSlotMatchingIngredient(SmithingRecipe $$0, ItemStack $$1) {
        if ($$0.isTemplateIngredient($$1)) {
            return Optional.of((Object)0);
        }
        if ($$0.isBaseIngredient($$1)) {
            return Optional.of((Object)1);
        }
        if ($$0.isAdditionIngredient($$1)) {
            return Optional.of((Object)2);
        }
        return Optional.empty();
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack $$0, Slot $$1) {
        return $$1.container != this.resultSlots && super.canTakeItemForPickAll($$0, $$1);
    }

    @Override
    public boolean canMoveIntoInputSlots(ItemStack $$0) {
        return this.recipes.stream().map($$1 -> SmithingMenu.findSlotMatchingIngredient($$1, $$0)).anyMatch(Optional::isPresent);
    }
}