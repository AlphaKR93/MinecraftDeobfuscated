/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;

public class EnchantedBookItem
extends Item {
    public static final String TAG_STORED_ENCHANTMENTS = "StoredEnchantments";

    public EnchantedBookItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public boolean isFoil(ItemStack $$0) {
        return true;
    }

    @Override
    public boolean isEnchantable(ItemStack $$0) {
        return false;
    }

    public static ListTag getEnchantments(ItemStack $$0) {
        CompoundTag $$1 = $$0.getTag();
        if ($$1 != null) {
            return $$1.getList(TAG_STORED_ENCHANTMENTS, 10);
        }
        return new ListTag();
    }

    @Override
    public void appendHoverText(ItemStack $$0, @Nullable Level $$1, List<Component> $$2, TooltipFlag $$3) {
        super.appendHoverText($$0, $$1, $$2, $$3);
        ItemStack.appendEnchantmentNames($$2, EnchantedBookItem.getEnchantments($$0));
    }

    public static void addEnchantment(ItemStack $$0, EnchantmentInstance $$1) {
        ListTag $$2 = EnchantedBookItem.getEnchantments($$0);
        boolean $$3 = true;
        ResourceLocation $$4 = EnchantmentHelper.getEnchantmentId($$1.enchantment);
        for (int $$5 = 0; $$5 < $$2.size(); ++$$5) {
            CompoundTag $$6 = $$2.getCompound($$5);
            ResourceLocation $$7 = EnchantmentHelper.getEnchantmentId($$6);
            if ($$7 == null || !$$7.equals($$4)) continue;
            if (EnchantmentHelper.getEnchantmentLevel($$6) < $$1.level) {
                EnchantmentHelper.setEnchantmentLevel($$6, $$1.level);
            }
            $$3 = false;
            break;
        }
        if ($$3) {
            $$2.add(EnchantmentHelper.storeEnchantment($$4, $$1.level));
        }
        $$0.getOrCreateTag().put(TAG_STORED_ENCHANTMENTS, $$2);
    }

    public static ItemStack createForEnchantment(EnchantmentInstance $$0) {
        ItemStack $$1 = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantedBookItem.addEnchantment($$1, $$0);
        return $$1;
    }
}