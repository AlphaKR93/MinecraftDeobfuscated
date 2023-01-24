/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.HashMap
 *  java.util.Map
 */
package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

public class RepairItemRecipe
extends CustomRecipe {
    public RepairItemRecipe(ResourceLocation $$0, CraftingBookCategory $$1) {
        super($$0, $$1);
    }

    @Override
    public boolean matches(CraftingContainer $$0, Level $$1) {
        ArrayList $$2 = Lists.newArrayList();
        for (int $$3 = 0; $$3 < $$0.getContainerSize(); ++$$3) {
            ItemStack $$5;
            ItemStack $$4 = $$0.getItem($$3);
            if ($$4.isEmpty()) continue;
            $$2.add((Object)$$4);
            if ($$2.size() <= 1 || $$4.is(($$5 = (ItemStack)$$2.get(0)).getItem()) && $$5.getCount() == 1 && $$4.getCount() == 1 && $$5.getItem().canBeDepleted()) continue;
            return false;
        }
        return $$2.size() == 2;
    }

    @Override
    public ItemStack assemble(CraftingContainer $$0, RegistryAccess $$1) {
        ItemStack $$7;
        ItemStack $$6;
        ArrayList $$2 = Lists.newArrayList();
        for (int $$3 = 0; $$3 < $$0.getContainerSize(); ++$$3) {
            ItemStack $$5;
            ItemStack $$4 = $$0.getItem($$3);
            if ($$4.isEmpty()) continue;
            $$2.add((Object)$$4);
            if ($$2.size() <= 1 || $$4.is(($$5 = (ItemStack)$$2.get(0)).getItem()) && $$5.getCount() == 1 && $$4.getCount() == 1 && $$5.getItem().canBeDepleted()) continue;
            return ItemStack.EMPTY;
        }
        if ($$2.size() == 2 && ($$6 = (ItemStack)$$2.get(0)).is(($$7 = (ItemStack)$$2.get(1)).getItem()) && $$6.getCount() == 1 && $$7.getCount() == 1 && $$6.getItem().canBeDepleted()) {
            Item $$8 = $$6.getItem();
            int $$9 = $$8.getMaxDamage() - $$6.getDamageValue();
            int $$10 = $$8.getMaxDamage() - $$7.getDamageValue();
            int $$11 = $$9 + $$10 + $$8.getMaxDamage() * 5 / 100;
            int $$12 = $$8.getMaxDamage() - $$11;
            if ($$12 < 0) {
                $$12 = 0;
            }
            ItemStack $$13 = new ItemStack($$6.getItem());
            $$13.setDamageValue($$12);
            HashMap $$14 = Maps.newHashMap();
            Map<Enchantment, Integer> $$15 = EnchantmentHelper.getEnchantments($$6);
            Map<Enchantment, Integer> $$16 = EnchantmentHelper.getEnchantments($$7);
            BuiltInRegistries.ENCHANTMENT.stream().filter(Enchantment::isCurse).forEach(arg_0 -> RepairItemRecipe.lambda$assemble$0($$15, $$16, (Map)$$14, arg_0));
            if (!$$14.isEmpty()) {
                EnchantmentHelper.setEnchantments((Map<Enchantment, Integer>)$$14, $$13);
            }
            return $$13;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int $$0, int $$1) {
        return $$0 * $$1 >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.REPAIR_ITEM;
    }

    private static /* synthetic */ void lambda$assemble$0(Map $$0, Map $$1, Map $$2, Enchantment $$3) {
        int $$4 = Math.max((int)((Integer)$$0.getOrDefault((Object)$$3, (Object)0)), (int)((Integer)$$1.getOrDefault((Object)$$3, (Object)0)));
        if ($$4 > 0) {
            $$2.put((Object)$$3, (Object)$$4);
        }
    }
}