/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.CharSequence
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Map
 *  java.util.function.BiConsumer
 *  java.util.function.Predicate
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 */
package net.minecraft.world.inventory;

import com.mojang.logging.LogUtils;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class AnvilMenu
extends ItemCombinerMenu {
    public static final int INPUT_SLOT = 0;
    public static final int ADDITIONAL_SLOT = 1;
    public static final int RESULT_SLOT = 2;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final boolean DEBUG_COST = false;
    public static final int MAX_NAME_LENGTH = 50;
    private int repairItemCountCost;
    private String itemName;
    private final DataSlot cost = DataSlot.standalone();
    private static final int COST_FAIL = 0;
    private static final int COST_BASE = 1;
    private static final int COST_ADDED_BASE = 1;
    private static final int COST_REPAIR_MATERIAL = 1;
    private static final int COST_REPAIR_SACRIFICE = 2;
    private static final int COST_INCOMPATIBLE_PENALTY = 1;
    private static final int COST_RENAME = 1;
    private static final int INPUT_SLOT_X_PLACEMENT = 27;
    private static final int ADDITIONAL_SLOT_X_PLACEMENT = 76;
    private static final int RESULT_SLOT_X_PLACEMENT = 134;
    private static final int SLOT_Y_PLACEMENT = 47;

    public AnvilMenu(int $$0, Inventory $$1) {
        this($$0, $$1, ContainerLevelAccess.NULL);
    }

    public AnvilMenu(int $$0, Inventory $$1, ContainerLevelAccess $$2) {
        super(MenuType.ANVIL, $$0, $$1, $$2);
        this.addDataSlot(this.cost);
    }

    @Override
    protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create().withSlot(0, 27, 47, (Predicate<ItemStack>)((Predicate)$$0 -> true)).withSlot(1, 76, 47, (Predicate<ItemStack>)((Predicate)$$0 -> true)).withResultSlot(2, 134, 47).build();
    }

    @Override
    protected boolean isValidBlock(BlockState $$0) {
        return $$0.is(BlockTags.ANVIL);
    }

    @Override
    protected boolean mayPickup(Player $$0, boolean $$1) {
        return ($$0.getAbilities().instabuild || $$0.experienceLevel >= this.cost.get()) && this.cost.get() > 0;
    }

    @Override
    protected void onTake(Player $$0, ItemStack $$12) {
        if (!$$0.getAbilities().instabuild) {
            $$0.giveExperienceLevels(-this.cost.get());
        }
        this.inputSlots.setItem(0, ItemStack.EMPTY);
        if (this.repairItemCountCost > 0) {
            ItemStack $$22 = this.inputSlots.getItem(1);
            if (!$$22.isEmpty() && $$22.getCount() > this.repairItemCountCost) {
                $$22.shrink(this.repairItemCountCost);
                this.inputSlots.setItem(1, $$22);
            } else {
                this.inputSlots.setItem(1, ItemStack.EMPTY);
            }
        } else {
            this.inputSlots.setItem(1, ItemStack.EMPTY);
        }
        this.cost.set(0);
        this.access.execute((BiConsumer<Level, BlockPos>)((BiConsumer)($$1, $$2) -> {
            BlockState $$3 = $$1.getBlockState((BlockPos)$$2);
            if (!$$0.getAbilities().instabuild && $$3.is(BlockTags.ANVIL) && $$0.getRandom().nextFloat() < 0.12f) {
                BlockState $$4 = AnvilBlock.damage($$3);
                if ($$4 == null) {
                    $$1.removeBlock((BlockPos)$$2, false);
                    $$1.levelEvent(1029, $$2, 0);
                } else {
                    $$1.setBlock((BlockPos)$$2, $$4, 2);
                    $$1.levelEvent(1030, $$2, 0);
                }
            } else {
                $$1.levelEvent(1030, $$2, 0);
            }
        }));
    }

    @Override
    public void createResult() {
        ItemStack $$0 = this.inputSlots.getItem(0);
        this.cost.set(1);
        int $$1 = 0;
        int $$2 = 0;
        int $$3 = 0;
        if ($$0.isEmpty()) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            this.cost.set(0);
            return;
        }
        ItemStack $$4 = $$0.copy();
        ItemStack $$5 = this.inputSlots.getItem(1);
        Map<Enchantment, Integer> $$6 = EnchantmentHelper.getEnchantments($$4);
        $$2 += $$0.getBaseRepairCost() + ($$5.isEmpty() ? 0 : $$5.getBaseRepairCost());
        this.repairItemCountCost = 0;
        if (!$$5.isEmpty()) {
            boolean $$7;
            boolean bl = $$7 = $$5.is(Items.ENCHANTED_BOOK) && !EnchantedBookItem.getEnchantments($$5).isEmpty();
            if ($$4.isDamageableItem() && $$4.getItem().isValidRepairItem($$0, $$5)) {
                int $$9;
                int $$8 = Math.min((int)$$4.getDamageValue(), (int)($$4.getMaxDamage() / 4));
                if ($$8 <= 0) {
                    this.resultSlots.setItem(0, ItemStack.EMPTY);
                    this.cost.set(0);
                    return;
                }
                for ($$9 = 0; $$8 > 0 && $$9 < $$5.getCount(); ++$$9) {
                    int $$10 = $$4.getDamageValue() - $$8;
                    $$4.setDamageValue($$10);
                    ++$$1;
                    $$8 = Math.min((int)$$4.getDamageValue(), (int)($$4.getMaxDamage() / 4));
                }
                this.repairItemCountCost = $$9;
            } else {
                if (!($$7 || $$4.is($$5.getItem()) && $$4.isDamageableItem())) {
                    this.resultSlots.setItem(0, ItemStack.EMPTY);
                    this.cost.set(0);
                    return;
                }
                if ($$4.isDamageableItem() && !$$7) {
                    int $$11 = $$0.getMaxDamage() - $$0.getDamageValue();
                    int $$12 = $$5.getMaxDamage() - $$5.getDamageValue();
                    int $$13 = $$12 + $$4.getMaxDamage() * 12 / 100;
                    int $$14 = $$11 + $$13;
                    int $$15 = $$4.getMaxDamage() - $$14;
                    if ($$15 < 0) {
                        $$15 = 0;
                    }
                    if ($$15 < $$4.getDamageValue()) {
                        $$4.setDamageValue($$15);
                        $$1 += 2;
                    }
                }
                Map<Enchantment, Integer> $$16 = EnchantmentHelper.getEnchantments($$5);
                boolean $$17 = false;
                boolean $$18 = false;
                for (Enchantment $$19 : $$16.keySet()) {
                    int $$21;
                    if ($$19 == null) continue;
                    int $$20 = (Integer)$$6.getOrDefault((Object)$$19, (Object)0);
                    $$21 = $$20 == ($$21 = ((Integer)$$16.get((Object)$$19)).intValue()) ? $$21 + 1 : Math.max((int)$$21, (int)$$20);
                    boolean $$22 = $$19.canEnchant($$0);
                    if (this.player.getAbilities().instabuild || $$0.is(Items.ENCHANTED_BOOK)) {
                        $$22 = true;
                    }
                    for (Enchantment $$23 : $$6.keySet()) {
                        if ($$23 == $$19 || $$19.isCompatibleWith($$23)) continue;
                        $$22 = false;
                        ++$$1;
                    }
                    if (!$$22) {
                        $$18 = true;
                        continue;
                    }
                    $$17 = true;
                    if ($$21 > $$19.getMaxLevel()) {
                        $$21 = $$19.getMaxLevel();
                    }
                    $$6.put((Object)$$19, (Object)$$21);
                    int $$24 = 0;
                    switch ($$19.getRarity()) {
                        case COMMON: {
                            $$24 = 1;
                            break;
                        }
                        case UNCOMMON: {
                            $$24 = 2;
                            break;
                        }
                        case RARE: {
                            $$24 = 4;
                            break;
                        }
                        case VERY_RARE: {
                            $$24 = 8;
                        }
                    }
                    if ($$7) {
                        $$24 = Math.max((int)1, (int)($$24 / 2));
                    }
                    $$1 += $$24 * $$21;
                    if ($$0.getCount() <= 1) continue;
                    $$1 = 40;
                }
                if ($$18 && !$$17) {
                    this.resultSlots.setItem(0, ItemStack.EMPTY);
                    this.cost.set(0);
                    return;
                }
            }
        }
        if (StringUtils.isBlank((CharSequence)this.itemName)) {
            if ($$0.hasCustomHoverName()) {
                $$3 = 1;
                $$1 += $$3;
                $$4.resetHoverName();
            }
        } else if (!this.itemName.equals((Object)$$0.getHoverName().getString())) {
            $$3 = 1;
            $$1 += $$3;
            $$4.setHoverName(Component.literal(this.itemName));
        }
        this.cost.set($$2 + $$1);
        if ($$1 <= 0) {
            $$4 = ItemStack.EMPTY;
        }
        if ($$3 == $$1 && $$3 > 0 && this.cost.get() >= 40) {
            this.cost.set(39);
        }
        if (this.cost.get() >= 40 && !this.player.getAbilities().instabuild) {
            $$4 = ItemStack.EMPTY;
        }
        if (!$$4.isEmpty()) {
            int $$25 = $$4.getBaseRepairCost();
            if (!$$5.isEmpty() && $$25 < $$5.getBaseRepairCost()) {
                $$25 = $$5.getBaseRepairCost();
            }
            if ($$3 != $$1 || $$3 == 0) {
                $$25 = AnvilMenu.calculateIncreasedRepairCost($$25);
            }
            $$4.setRepairCost($$25);
            EnchantmentHelper.setEnchantments($$6, $$4);
        }
        this.resultSlots.setItem(0, $$4);
        this.broadcastChanges();
    }

    public static int calculateIncreasedRepairCost(int $$0) {
        return $$0 * 2 + 1;
    }

    public void setItemName(String $$0) {
        this.itemName = $$0;
        if (this.getSlot(2).hasItem()) {
            ItemStack $$1 = this.getSlot(2).getItem();
            if (StringUtils.isBlank((CharSequence)$$0)) {
                $$1.resetHoverName();
            } else {
                $$1.setHoverName(Component.literal(this.itemName));
            }
        }
        this.createResult();
    }

    public int getCost() {
        return this.cost.get();
    }
}