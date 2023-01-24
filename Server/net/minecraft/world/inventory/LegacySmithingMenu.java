/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Deprecated
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.function.BiConsumer
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.world.inventory;

import java.util.List;
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
import net.minecraft.world.item.crafting.LegacyUpgradeRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

@Deprecated(forRemoval=true)
public class LegacySmithingMenu
extends ItemCombinerMenu {
    private final Level level;
    public static final int INPUT_SLOT = 0;
    public static final int ADDITIONAL_SLOT = 1;
    public static final int RESULT_SLOT = 2;
    private static final int INPUT_SLOT_X_PLACEMENT = 27;
    private static final int ADDITIONAL_SLOT_X_PLACEMENT = 76;
    private static final int RESULT_SLOT_X_PLACEMENT = 134;
    private static final int SLOT_Y_PLACEMENT = 47;
    @Nullable
    private LegacyUpgradeRecipe selectedRecipe;
    private final List<LegacyUpgradeRecipe> recipes;

    public LegacySmithingMenu(int $$0, Inventory $$1) {
        this($$0, $$1, ContainerLevelAccess.NULL);
    }

    public LegacySmithingMenu(int $$02, Inventory $$1, ContainerLevelAccess $$2) {
        super(MenuType.LEGACY_SMITHING, $$02, $$1, $$2);
        this.level = $$1.player.level;
        this.recipes = this.level.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING).stream().filter($$0 -> $$0 instanceof LegacyUpgradeRecipe).map($$0 -> (LegacyUpgradeRecipe)$$0).toList();
    }

    @Override
    protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create().withSlot(0, 27, 47, (Predicate<ItemStack>)((Predicate)$$0 -> true)).withSlot(1, 76, 47, (Predicate<ItemStack>)((Predicate)$$0 -> true)).withResultSlot(2, 134, 47).build();
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
        List $$02 = this.level.getRecipeManager().getRecipesFor(RecipeType.SMITHING, this.inputSlots, this.level).stream().filter($$0 -> $$0 instanceof LegacyUpgradeRecipe).map($$0 -> (LegacyUpgradeRecipe)$$0).toList();
        if ($$02.isEmpty()) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
        } else {
            LegacyUpgradeRecipe $$1 = (LegacyUpgradeRecipe)$$02.get(0);
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
        return this.shouldQuickMoveToAdditionalSlot($$0) ? 1 : 0;
    }

    protected boolean shouldQuickMoveToAdditionalSlot(ItemStack $$0) {
        return this.recipes.stream().anyMatch($$1 -> $$1.isAdditionIngredient($$0));
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack $$0, Slot $$1) {
        return $$1.container != this.resultSlots && super.canTakeItemForPickAll($$0, $$1);
    }
}