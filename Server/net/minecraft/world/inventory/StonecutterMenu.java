/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.util.List
 *  java.util.function.BiConsumer
 */
package net.minecraft.world.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class StonecutterMenu
extends AbstractContainerMenu {
    public static final int INPUT_SLOT = 0;
    public static final int RESULT_SLOT = 1;
    private static final int INV_SLOT_START = 2;
    private static final int INV_SLOT_END = 29;
    private static final int USE_ROW_SLOT_START = 29;
    private static final int USE_ROW_SLOT_END = 38;
    private final ContainerLevelAccess access;
    private final DataSlot selectedRecipeIndex = DataSlot.standalone();
    private final Level level;
    private List<StonecutterRecipe> recipes = Lists.newArrayList();
    private ItemStack input = ItemStack.EMPTY;
    long lastSoundTime;
    final Slot inputSlot;
    final Slot resultSlot;
    Runnable slotUpdateListener = () -> {};
    public final Container container = new SimpleContainer(1){

        @Override
        public void setChanged() {
            super.setChanged();
            StonecutterMenu.this.slotsChanged(this);
            StonecutterMenu.this.slotUpdateListener.run();
        }
    };
    final ResultContainer resultContainer = new ResultContainer();

    public StonecutterMenu(int $$0, Inventory $$1) {
        this($$0, $$1, ContainerLevelAccess.NULL);
    }

    public StonecutterMenu(int $$0, Inventory $$1, final ContainerLevelAccess $$2) {
        super(MenuType.STONECUTTER, $$0);
        this.access = $$2;
        this.level = $$1.player.level;
        this.inputSlot = this.addSlot(new Slot(this.container, 0, 20, 33));
        this.resultSlot = this.addSlot(new Slot(this.resultContainer, 1, 143, 33){

            @Override
            public boolean mayPlace(ItemStack $$0) {
                return false;
            }

            @Override
            public void onTake(Player $$02, ItemStack $$12) {
                $$12.onCraftedBy($$02.level, $$02, $$12.getCount());
                StonecutterMenu.this.resultContainer.awardUsedRecipes($$02);
                ItemStack $$22 = StonecutterMenu.this.inputSlot.remove(1);
                if (!$$22.isEmpty()) {
                    StonecutterMenu.this.setupResultSlot();
                }
                $$2.execute((BiConsumer<Level, BlockPos>)((BiConsumer)($$0, $$1) -> {
                    long $$22 = $$0.getGameTime();
                    if (StonecutterMenu.this.lastSoundTime != $$22) {
                        $$0.playSound(null, (BlockPos)$$1, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 1.0f, 1.0f);
                        StonecutterMenu.this.lastSoundTime = $$22;
                    }
                }));
                super.onTake($$02, $$12);
            }
        });
        for (int $$3 = 0; $$3 < 3; ++$$3) {
            for (int $$4 = 0; $$4 < 9; ++$$4) {
                this.addSlot(new Slot($$1, $$4 + $$3 * 9 + 9, 8 + $$4 * 18, 84 + $$3 * 18));
            }
        }
        for (int $$5 = 0; $$5 < 9; ++$$5) {
            this.addSlot(new Slot($$1, $$5, 8 + $$5 * 18, 142));
        }
        this.addDataSlot(this.selectedRecipeIndex);
    }

    public int getSelectedRecipeIndex() {
        return this.selectedRecipeIndex.get();
    }

    public List<StonecutterRecipe> getRecipes() {
        return this.recipes;
    }

    public int getNumRecipes() {
        return this.recipes.size();
    }

    public boolean hasInputItem() {
        return this.inputSlot.hasItem() && !this.recipes.isEmpty();
    }

    @Override
    public boolean stillValid(Player $$0) {
        return StonecutterMenu.stillValid(this.access, $$0, Blocks.STONECUTTER);
    }

    @Override
    public boolean clickMenuButton(Player $$0, int $$1) {
        if (this.isValidRecipeIndex($$1)) {
            this.selectedRecipeIndex.set($$1);
            this.setupResultSlot();
        }
        return true;
    }

    private boolean isValidRecipeIndex(int $$0) {
        return $$0 >= 0 && $$0 < this.recipes.size();
    }

    @Override
    public void slotsChanged(Container $$0) {
        ItemStack $$1 = this.inputSlot.getItem();
        if (!$$1.is(this.input.getItem())) {
            this.input = $$1.copy();
            this.setupRecipeList($$0, $$1);
        }
    }

    private void setupRecipeList(Container $$0, ItemStack $$1) {
        this.recipes.clear();
        this.selectedRecipeIndex.set(-1);
        this.resultSlot.set(ItemStack.EMPTY);
        if (!$$1.isEmpty()) {
            this.recipes = this.level.getRecipeManager().getRecipesFor(RecipeType.STONECUTTING, $$0, this.level);
        }
    }

    void setupResultSlot() {
        if (!this.recipes.isEmpty() && this.isValidRecipeIndex(this.selectedRecipeIndex.get())) {
            StonecutterRecipe $$0 = (StonecutterRecipe)this.recipes.get(this.selectedRecipeIndex.get());
            ItemStack $$1 = $$0.assemble(this.container, this.level.registryAccess());
            if ($$1.isItemEnabled(this.level.enabledFeatures())) {
                this.resultContainer.setRecipeUsed($$0);
                this.resultSlot.set($$1);
            } else {
                this.resultSlot.set(ItemStack.EMPTY);
            }
        } else {
            this.resultSlot.set(ItemStack.EMPTY);
        }
        this.broadcastChanges();
    }

    @Override
    public MenuType<?> getType() {
        return MenuType.STONECUTTER;
    }

    public void registerUpdateListener(Runnable $$0) {
        this.slotUpdateListener = $$0;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack $$0, Slot $$1) {
        return $$1.container != this.resultContainer && super.canTakeItemForPickAll($$0, $$1);
    }

    @Override
    public ItemStack quickMoveStack(Player $$0, int $$1) {
        ItemStack $$2 = ItemStack.EMPTY;
        Slot $$3 = (Slot)this.slots.get($$1);
        if ($$3 != null && $$3.hasItem()) {
            ItemStack $$4 = $$3.getItem();
            Item $$5 = $$4.getItem();
            $$2 = $$4.copy();
            if ($$1 == 1) {
                $$5.onCraftedBy($$4, $$0.level, $$0);
                if (!this.moveItemStackTo($$4, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
                $$3.onQuickCraft($$4, $$2);
            } else if ($$1 == 0 ? !this.moveItemStackTo($$4, 2, 38, false) : (this.level.getRecipeManager().getRecipeFor(RecipeType.STONECUTTING, new SimpleContainer($$4), this.level).isPresent() ? !this.moveItemStackTo($$4, 0, 1, false) : ($$1 >= 2 && $$1 < 29 ? !this.moveItemStackTo($$4, 29, 38, false) : $$1 >= 29 && $$1 < 38 && !this.moveItemStackTo($$4, 2, 29, false)))) {
                return ItemStack.EMPTY;
            }
            if ($$4.isEmpty()) {
                $$3.set(ItemStack.EMPTY);
            }
            $$3.setChanged();
            if ($$4.getCount() == $$2.getCount()) {
                return ItemStack.EMPTY;
            }
            $$3.onTake($$0, $$4);
            this.broadcastChanges();
        }
        return $$2;
    }

    @Override
    public void removed(Player $$0) {
        super.removed($$0);
        this.resultContainer.removeItemNoUpdate(1);
        this.access.execute((BiConsumer<Level, BlockPos>)((BiConsumer)($$1, $$2) -> this.clearContainer($$0, this.container)));
    }
}