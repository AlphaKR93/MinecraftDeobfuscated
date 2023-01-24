/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Optional
 *  java.util.function.BiConsumer
 */
package net.minecraft.world.inventory;

import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class CraftingMenu
extends RecipeBookMenu<CraftingContainer> {
    public static final int RESULT_SLOT = 0;
    private static final int CRAFT_SLOT_START = 1;
    private static final int CRAFT_SLOT_END = 10;
    private static final int INV_SLOT_START = 10;
    private static final int INV_SLOT_END = 37;
    private static final int USE_ROW_SLOT_START = 37;
    private static final int USE_ROW_SLOT_END = 46;
    private final CraftingContainer craftSlots = new CraftingContainer(this, 3, 3);
    private final ResultContainer resultSlots = new ResultContainer();
    private final ContainerLevelAccess access;
    private final Player player;

    public CraftingMenu(int $$0, Inventory $$1) {
        this($$0, $$1, ContainerLevelAccess.NULL);
    }

    public CraftingMenu(int $$0, Inventory $$1, ContainerLevelAccess $$2) {
        super(MenuType.CRAFTING, $$0);
        this.access = $$2;
        this.player = $$1.player;
        this.addSlot(new ResultSlot($$1.player, this.craftSlots, this.resultSlots, 0, 124, 35));
        for (int $$3 = 0; $$3 < 3; ++$$3) {
            for (int $$4 = 0; $$4 < 3; ++$$4) {
                this.addSlot(new Slot(this.craftSlots, $$4 + $$3 * 3, 30 + $$4 * 18, 17 + $$3 * 18));
            }
        }
        for (int $$5 = 0; $$5 < 3; ++$$5) {
            for (int $$6 = 0; $$6 < 9; ++$$6) {
                this.addSlot(new Slot($$1, $$6 + $$5 * 9 + 9, 8 + $$6 * 18, 84 + $$5 * 18));
            }
        }
        for (int $$7 = 0; $$7 < 9; ++$$7) {
            this.addSlot(new Slot($$1, $$7, 8 + $$7 * 18, 142));
        }
    }

    protected static void slotChangedCraftingGrid(AbstractContainerMenu $$0, Level $$1, Player $$2, CraftingContainer $$3, ResultContainer $$4) {
        ItemStack $$9;
        CraftingRecipe $$8;
        if ($$1.isClientSide) {
            return;
        }
        ServerPlayer $$5 = (ServerPlayer)$$2;
        ItemStack $$6 = ItemStack.EMPTY;
        Optional<CraftingRecipe> $$7 = $$1.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, $$3, $$1);
        if ($$7.isPresent() && $$4.setRecipeUsed($$1, $$5, $$8 = (CraftingRecipe)$$7.get()) && ($$9 = $$8.assemble($$3, $$1.registryAccess())).isItemEnabled($$1.enabledFeatures())) {
            $$6 = $$9;
        }
        $$4.setItem(0, $$6);
        $$0.setRemoteSlot(0, $$6);
        $$5.connection.send(new ClientboundContainerSetSlotPacket($$0.containerId, $$0.incrementStateId(), 0, $$6));
    }

    @Override
    public void slotsChanged(Container $$02) {
        this.access.execute((BiConsumer<Level, BlockPos>)((BiConsumer)($$0, $$1) -> CraftingMenu.slotChangedCraftingGrid(this, $$0, this.player, this.craftSlots, this.resultSlots)));
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedContents $$0) {
        this.craftSlots.fillStackedContents($$0);
    }

    @Override
    public void clearCraftingContent() {
        this.craftSlots.clearContent();
        this.resultSlots.clearContent();
    }

    @Override
    public boolean recipeMatches(Recipe<? super CraftingContainer> $$0) {
        return $$0.matches(this.craftSlots, this.player.level);
    }

    @Override
    public void removed(Player $$0) {
        super.removed($$0);
        this.access.execute((BiConsumer<Level, BlockPos>)((BiConsumer)($$1, $$2) -> this.clearContainer($$0, this.craftSlots)));
    }

    @Override
    public boolean stillValid(Player $$0) {
        return CraftingMenu.stillValid(this.access, $$0, Blocks.CRAFTING_TABLE);
    }

    @Override
    public ItemStack quickMoveStack(Player $$0, int $$1) {
        ItemStack $$22 = ItemStack.EMPTY;
        Slot $$32 = (Slot)this.slots.get($$1);
        if ($$32 != null && $$32.hasItem()) {
            ItemStack $$4 = $$32.getItem();
            $$22 = $$4.copy();
            if ($$1 == 0) {
                this.access.execute((BiConsumer<Level, BlockPos>)((BiConsumer)($$2, $$3) -> $$4.getItem().onCraftedBy($$4, (Level)$$2, $$0)));
                if (!this.moveItemStackTo($$4, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }
                $$32.onQuickCraft($$4, $$22);
            } else if ($$1 >= 10 && $$1 < 46 ? !this.moveItemStackTo($$4, 1, 10, false) && ($$1 < 37 ? !this.moveItemStackTo($$4, 37, 46, false) : !this.moveItemStackTo($$4, 10, 37, false)) : !this.moveItemStackTo($$4, 10, 46, false)) {
                return ItemStack.EMPTY;
            }
            if ($$4.isEmpty()) {
                $$32.set(ItemStack.EMPTY);
            } else {
                $$32.setChanged();
            }
            if ($$4.getCount() == $$22.getCount()) {
                return ItemStack.EMPTY;
            }
            $$32.onTake($$0, $$4);
            if ($$1 == 0) {
                $$0.drop($$4, false);
            }
        }
        return $$22;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack $$0, Slot $$1) {
        return $$1.container != this.resultSlots && super.canTakeItemForPickAll($$0, $$1);
    }

    @Override
    public int getResultSlotIndex() {
        return 0;
    }

    @Override
    public int getGridWidth() {
        return this.craftSlots.getWidth();
    }

    @Override
    public int getGridHeight() {
        return this.craftSlots.getHeight();
    }

    @Override
    public int getSize() {
        return 10;
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return RecipeBookType.CRAFTING;
    }

    @Override
    public boolean shouldMoveToInventory(int $$0) {
        return $$0 != this.getResultSlotIndex();
    }
}