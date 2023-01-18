/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.function.BiConsumer
 *  java.util.stream.Collectors
 */
package net.minecraft.world.inventory;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class GrindstoneMenu
extends AbstractContainerMenu {
    public static final int MAX_NAME_LENGTH = 35;
    public static final int INPUT_SLOT = 0;
    public static final int ADDITIONAL_SLOT = 1;
    public static final int RESULT_SLOT = 2;
    private static final int INV_SLOT_START = 3;
    private static final int INV_SLOT_END = 30;
    private static final int USE_ROW_SLOT_START = 30;
    private static final int USE_ROW_SLOT_END = 39;
    private final Container resultSlots = new ResultContainer();
    final Container repairSlots = new SimpleContainer(2){

        @Override
        public void setChanged() {
            super.setChanged();
            GrindstoneMenu.this.slotsChanged(this);
        }
    };
    private final ContainerLevelAccess access;

    public GrindstoneMenu(int $$0, Inventory $$1) {
        this($$0, $$1, ContainerLevelAccess.NULL);
    }

    public GrindstoneMenu(int $$0, Inventory $$1, final ContainerLevelAccess $$2) {
        super(MenuType.GRINDSTONE, $$0);
        this.access = $$2;
        this.addSlot(new Slot(this.repairSlots, 0, 49, 19){

            @Override
            public boolean mayPlace(ItemStack $$0) {
                return $$0.isDamageableItem() || $$0.is(Items.ENCHANTED_BOOK) || $$0.isEnchanted();
            }
        });
        this.addSlot(new Slot(this.repairSlots, 1, 49, 40){

            @Override
            public boolean mayPlace(ItemStack $$0) {
                return $$0.isDamageableItem() || $$0.is(Items.ENCHANTED_BOOK) || $$0.isEnchanted();
            }
        });
        this.addSlot(new Slot(this.resultSlots, 2, 129, 34){

            @Override
            public boolean mayPlace(ItemStack $$0) {
                return false;
            }

            @Override
            public void onTake(Player $$02, ItemStack $$12) {
                $$2.execute((BiConsumer<Level, BlockPos>)((BiConsumer)($$0, $$1) -> {
                    if ($$0 instanceof ServerLevel) {
                        ExperienceOrb.award((ServerLevel)$$0, Vec3.atCenterOf($$1), this.getExperienceAmount((Level)$$0));
                    }
                    $$0.levelEvent(1042, $$1, 0);
                }));
                GrindstoneMenu.this.repairSlots.setItem(0, ItemStack.EMPTY);
                GrindstoneMenu.this.repairSlots.setItem(1, ItemStack.EMPTY);
            }

            private int getExperienceAmount(Level $$0) {
                int $$1 = 0;
                $$1 += this.getExperienceFromItem(GrindstoneMenu.this.repairSlots.getItem(0));
                if (($$1 += this.getExperienceFromItem(GrindstoneMenu.this.repairSlots.getItem(1))) > 0) {
                    int $$22 = (int)Math.ceil((double)((double)$$1 / 2.0));
                    return $$22 + $$0.random.nextInt($$22);
                }
                return 0;
            }

            private int getExperienceFromItem(ItemStack $$0) {
                int $$1 = 0;
                Map<Enchantment, Integer> $$22 = EnchantmentHelper.getEnchantments($$0);
                for (Map.Entry $$3 : $$22.entrySet()) {
                    Enchantment $$4 = (Enchantment)$$3.getKey();
                    Integer $$5 = (Integer)$$3.getValue();
                    if ($$4.isCurse()) continue;
                    $$1 += $$4.getMinCost($$5);
                }
                return $$1;
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
    }

    @Override
    public void slotsChanged(Container $$0) {
        super.slotsChanged($$0);
        if ($$0 == this.repairSlots) {
            this.createResult();
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    private void createResult() {
        boolean $$3;
        ItemStack $$0 = this.repairSlots.getItem(0);
        ItemStack $$1 = this.repairSlots.getItem(1);
        boolean $$2 = !$$0.isEmpty() || !$$1.isEmpty();
        boolean bl = $$3 = !$$0.isEmpty() && !$$1.isEmpty();
        if ($$2) {
            ItemStack $$14;
            int $$13;
            boolean $$4;
            boolean bl2 = $$4 = !$$0.isEmpty() && !$$0.is(Items.ENCHANTED_BOOK) && !$$0.isEnchanted() || !$$1.isEmpty() && !$$1.is(Items.ENCHANTED_BOOK) && !$$1.isEnchanted();
            if ($$0.getCount() > 1 || $$1.getCount() > 1 || !$$3 && $$4) {
                this.resultSlots.setItem(0, ItemStack.EMPTY);
                this.broadcastChanges();
                return;
            }
            int $$5 = 1;
            if ($$3) {
                if (!$$0.is($$1.getItem())) {
                    this.resultSlots.setItem(0, ItemStack.EMPTY);
                    this.broadcastChanges();
                    return;
                }
                Item $$6 = $$0.getItem();
                int $$7 = $$6.getMaxDamage() - $$0.getDamageValue();
                int $$8 = $$6.getMaxDamage() - $$1.getDamageValue();
                int $$9 = $$7 + $$8 + $$6.getMaxDamage() * 5 / 100;
                int $$10 = Math.max((int)($$6.getMaxDamage() - $$9), (int)0);
                ItemStack $$11 = this.mergeEnchants($$0, $$1);
                if (!$$11.isDamageableItem()) {
                    if (!ItemStack.matches($$0, $$1)) {
                        this.resultSlots.setItem(0, ItemStack.EMPTY);
                        this.broadcastChanges();
                        return;
                    }
                    $$5 = 2;
                }
            } else {
                boolean $$12 = !$$0.isEmpty();
                $$13 = $$12 ? $$0.getDamageValue() : $$1.getDamageValue();
                $$14 = $$12 ? $$0 : $$1;
            }
            this.resultSlots.setItem(0, this.removeNonCurses($$14, $$13, $$5));
        } else {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
        }
        this.broadcastChanges();
    }

    private ItemStack mergeEnchants(ItemStack $$0, ItemStack $$1) {
        ItemStack $$2 = $$0.copy();
        Map<Enchantment, Integer> $$3 = EnchantmentHelper.getEnchantments($$1);
        for (Map.Entry $$4 : $$3.entrySet()) {
            Enchantment $$5 = (Enchantment)$$4.getKey();
            if ($$5.isCurse() && EnchantmentHelper.getItemEnchantmentLevel($$5, $$2) != 0) continue;
            $$2.enchant($$5, (Integer)$$4.getValue());
        }
        return $$2;
    }

    private ItemStack removeNonCurses(ItemStack $$02, int $$1, int $$2) {
        ItemStack $$3 = $$02.copy();
        $$3.removeTagKey("Enchantments");
        $$3.removeTagKey("StoredEnchantments");
        if ($$1 > 0) {
            $$3.setDamageValue($$1);
        } else {
            $$3.removeTagKey("Damage");
        }
        $$3.setCount($$2);
        Map $$4 = (Map)EnchantmentHelper.getEnchantments($$02).entrySet().stream().filter($$0 -> ((Enchantment)$$0.getKey()).isCurse()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        EnchantmentHelper.setEnchantments((Map<Enchantment, Integer>)$$4, $$3);
        $$3.setRepairCost(0);
        if ($$3.is(Items.ENCHANTED_BOOK) && $$4.size() == 0) {
            $$3 = new ItemStack(Items.BOOK);
            if ($$02.hasCustomHoverName()) {
                $$3.setHoverName($$02.getHoverName());
            }
        }
        for (int $$5 = 0; $$5 < $$4.size(); ++$$5) {
            $$3.setRepairCost(AnvilMenu.calculateIncreasedRepairCost($$3.getBaseRepairCost()));
        }
        return $$3;
    }

    @Override
    public void removed(Player $$0) {
        super.removed($$0);
        this.access.execute((BiConsumer<Level, BlockPos>)((BiConsumer)($$1, $$2) -> this.clearContainer($$0, this.repairSlots)));
    }

    @Override
    public boolean stillValid(Player $$0) {
        return GrindstoneMenu.stillValid(this.access, $$0, Blocks.GRINDSTONE);
    }

    @Override
    public ItemStack quickMoveStack(Player $$0, int $$1) {
        ItemStack $$2 = ItemStack.EMPTY;
        Slot $$3 = (Slot)this.slots.get($$1);
        if ($$3 != null && $$3.hasItem()) {
            ItemStack $$4 = $$3.getItem();
            $$2 = $$4.copy();
            ItemStack $$5 = this.repairSlots.getItem(0);
            ItemStack $$6 = this.repairSlots.getItem(1);
            if ($$1 == 2) {
                if (!this.moveItemStackTo($$4, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                $$3.onQuickCraft($$4, $$2);
            } else if ($$1 == 0 || $$1 == 1 ? !this.moveItemStackTo($$4, 3, 39, false) : ($$5.isEmpty() || $$6.isEmpty() ? !this.moveItemStackTo($$4, 0, 2, false) : ($$1 >= 3 && $$1 < 30 ? !this.moveItemStackTo($$4, 30, 39, false) : $$1 >= 30 && $$1 < 39 && !this.moveItemStackTo($$4, 3, 30, false)))) {
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