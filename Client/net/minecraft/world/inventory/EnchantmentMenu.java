/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.function.BiConsumer
 */
package net.minecraft.world.inventory;

import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnchantmentTableBlock;

public class EnchantmentMenu
extends AbstractContainerMenu {
    private final Container enchantSlots = new SimpleContainer(2){

        @Override
        public void setChanged() {
            super.setChanged();
            EnchantmentMenu.this.slotsChanged(this);
        }
    };
    private final ContainerLevelAccess access;
    private final RandomSource random = RandomSource.create();
    private final DataSlot enchantmentSeed = DataSlot.standalone();
    public final int[] costs = new int[3];
    public final int[] enchantClue = new int[]{-1, -1, -1};
    public final int[] levelClue = new int[]{-1, -1, -1};

    public EnchantmentMenu(int $$0, Inventory $$1) {
        this($$0, $$1, ContainerLevelAccess.NULL);
    }

    public EnchantmentMenu(int $$0, Inventory $$1, ContainerLevelAccess $$2) {
        super(MenuType.ENCHANTMENT, $$0);
        this.access = $$2;
        this.addSlot(new Slot(this.enchantSlots, 0, 15, 47){

            @Override
            public boolean mayPlace(ItemStack $$0) {
                return true;
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        this.addSlot(new Slot(this.enchantSlots, 1, 35, 47){

            @Override
            public boolean mayPlace(ItemStack $$0) {
                return $$0.is(Items.LAPIS_LAZULI);
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
        this.addDataSlot(DataSlot.shared(this.costs, 0));
        this.addDataSlot(DataSlot.shared(this.costs, 1));
        this.addDataSlot(DataSlot.shared(this.costs, 2));
        this.addDataSlot(this.enchantmentSeed).set($$1.player.getEnchantmentSeed());
        this.addDataSlot(DataSlot.shared(this.enchantClue, 0));
        this.addDataSlot(DataSlot.shared(this.enchantClue, 1));
        this.addDataSlot(DataSlot.shared(this.enchantClue, 2));
        this.addDataSlot(DataSlot.shared(this.levelClue, 0));
        this.addDataSlot(DataSlot.shared(this.levelClue, 1));
        this.addDataSlot(DataSlot.shared(this.levelClue, 2));
    }

    @Override
    public void slotsChanged(Container $$0) {
        if ($$0 == this.enchantSlots) {
            ItemStack $$12 = $$0.getItem(0);
            if ($$12.isEmpty() || !$$12.isEnchantable()) {
                for (int $$22 = 0; $$22 < 3; ++$$22) {
                    this.costs[$$22] = 0;
                    this.enchantClue[$$22] = -1;
                    this.levelClue[$$22] = -1;
                }
            } else {
                this.access.execute((BiConsumer<Level, BlockPos>)((BiConsumer)($$1, $$2) -> {
                    int $$3 = 0;
                    for (BlockPos $$4 : EnchantmentTableBlock.BOOKSHELF_OFFSETS) {
                        if (!EnchantmentTableBlock.isValidBookShelf($$1, $$2, $$4)) continue;
                        ++$$3;
                    }
                    this.random.setSeed(this.enchantmentSeed.get());
                    for (int $$5 = 0; $$5 < 3; ++$$5) {
                        this.costs[$$5] = EnchantmentHelper.getEnchantmentCost(this.random, $$5, $$3, $$12);
                        this.enchantClue[$$5] = -1;
                        this.levelClue[$$5] = -1;
                        if (this.costs[$$5] >= $$5 + 1) continue;
                        this.costs[$$5] = 0;
                    }
                    for (int $$6 = 0; $$6 < 3; ++$$6) {
                        List<EnchantmentInstance> $$7;
                        if (this.costs[$$6] <= 0 || ($$7 = this.getEnchantmentList($$12, $$6, this.costs[$$6])) == null || $$7.isEmpty()) continue;
                        EnchantmentInstance $$8 = (EnchantmentInstance)$$7.get(this.random.nextInt($$7.size()));
                        this.enchantClue[$$6] = BuiltInRegistries.ENCHANTMENT.getId($$8.enchantment);
                        this.levelClue[$$6] = $$8.level;
                    }
                    this.broadcastChanges();
                }));
            }
        }
    }

    @Override
    public boolean clickMenuButton(Player $$0, int $$1) {
        if ($$1 < 0 || $$1 >= this.costs.length) {
            Util.logAndPauseIfInIde($$0.getName() + " pressed invalid button id: " + $$1);
            return false;
        }
        ItemStack $$2 = this.enchantSlots.getItem(0);
        ItemStack $$3 = this.enchantSlots.getItem(1);
        int $$4 = $$1 + 1;
        if (($$3.isEmpty() || $$3.getCount() < $$4) && !$$0.getAbilities().instabuild) {
            return false;
        }
        if (this.costs[$$1] > 0 && !$$2.isEmpty() && ($$0.experienceLevel >= $$4 && $$0.experienceLevel >= this.costs[$$1] || $$0.getAbilities().instabuild)) {
            this.access.execute((BiConsumer<Level, BlockPos>)((BiConsumer)($$5, $$6) -> {
                ItemStack $$7 = $$2;
                List<EnchantmentInstance> $$8 = this.getEnchantmentList($$7, $$1, this.costs[$$1]);
                if (!$$8.isEmpty()) {
                    $$0.onEnchantmentPerformed($$7, $$4);
                    boolean $$9 = $$7.is(Items.BOOK);
                    if ($$9) {
                        $$7 = new ItemStack(Items.ENCHANTED_BOOK);
                        CompoundTag $$10 = $$2.getTag();
                        if ($$10 != null) {
                            $$7.setTag($$10.copy());
                        }
                        this.enchantSlots.setItem(0, $$7);
                    }
                    for (int $$11 = 0; $$11 < $$8.size(); ++$$11) {
                        EnchantmentInstance $$12 = (EnchantmentInstance)$$8.get($$11);
                        if ($$9) {
                            EnchantedBookItem.addEnchantment($$7, $$12);
                            continue;
                        }
                        $$7.enchant($$12.enchantment, $$12.level);
                    }
                    if (!$$2.getAbilities().instabuild) {
                        $$3.shrink($$4);
                        if ($$3.isEmpty()) {
                            this.enchantSlots.setItem(1, ItemStack.EMPTY);
                        }
                    }
                    $$0.awardStat(Stats.ENCHANT_ITEM);
                    if ($$0 instanceof ServerPlayer) {
                        CriteriaTriggers.ENCHANTED_ITEM.trigger((ServerPlayer)$$0, $$7, $$4);
                    }
                    this.enchantSlots.setChanged();
                    this.enchantmentSeed.set($$0.getEnchantmentSeed());
                    this.slotsChanged(this.enchantSlots);
                    $$5.playSound(null, (BlockPos)$$6, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS, 1.0f, $$5.random.nextFloat() * 0.1f + 0.9f);
                }
            }));
            return true;
        }
        return false;
    }

    private List<EnchantmentInstance> getEnchantmentList(ItemStack $$0, int $$1, int $$2) {
        this.random.setSeed(this.enchantmentSeed.get() + $$1);
        List<EnchantmentInstance> $$3 = EnchantmentHelper.selectEnchantment(this.random, $$0, $$2, false);
        if ($$0.is(Items.BOOK) && $$3.size() > 1) {
            $$3.remove(this.random.nextInt($$3.size()));
        }
        return $$3;
    }

    public int getGoldCount() {
        ItemStack $$0 = this.enchantSlots.getItem(1);
        if ($$0.isEmpty()) {
            return 0;
        }
        return $$0.getCount();
    }

    public int getEnchantmentSeed() {
        return this.enchantmentSeed.get();
    }

    @Override
    public void removed(Player $$0) {
        super.removed($$0);
        this.access.execute((BiConsumer<Level, BlockPos>)((BiConsumer)($$1, $$2) -> this.clearContainer($$0, this.enchantSlots)));
    }

    @Override
    public boolean stillValid(Player $$0) {
        return EnchantmentMenu.stillValid(this.access, $$0, Blocks.ENCHANTING_TABLE);
    }

    @Override
    public ItemStack quickMoveStack(Player $$0, int $$1) {
        ItemStack $$2 = ItemStack.EMPTY;
        Slot $$3 = (Slot)this.slots.get($$1);
        if ($$3 != null && $$3.hasItem()) {
            ItemStack $$4 = $$3.getItem();
            $$2 = $$4.copy();
            if ($$1 == 0) {
                if (!this.moveItemStackTo($$4, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if ($$1 == 1) {
                if (!this.moveItemStackTo($$4, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }
            } else if ($$4.is(Items.LAPIS_LAZULI)) {
                if (!this.moveItemStackTo($$4, 1, 2, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!((Slot)this.slots.get(0)).hasItem() && ((Slot)this.slots.get(0)).mayPlace($$4)) {
                ItemStack $$5 = $$4.copy();
                $$5.setCount(1);
                $$4.shrink(1);
                ((Slot)this.slots.get(0)).set($$5);
            } else {
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
}