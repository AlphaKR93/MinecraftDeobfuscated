/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  java.lang.FunctionalInterface
 *  java.lang.Integer
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.Iterator
 *  java.util.LinkedHashMap
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.mutable.MutableFloat
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.world.item.enchantment;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.SweepingEdgeEnchantment;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

public class EnchantmentHelper {
    private static final String TAG_ENCH_ID = "id";
    private static final String TAG_ENCH_LEVEL = "lvl";
    private static final float SWIFT_SNEAK_EXTRA_FACTOR = 0.15f;

    public static CompoundTag storeEnchantment(@Nullable ResourceLocation $$0, int $$1) {
        CompoundTag $$2 = new CompoundTag();
        $$2.putString(TAG_ENCH_ID, String.valueOf((Object)$$0));
        $$2.putShort(TAG_ENCH_LEVEL, (short)$$1);
        return $$2;
    }

    public static void setEnchantmentLevel(CompoundTag $$0, int $$1) {
        $$0.putShort(TAG_ENCH_LEVEL, (short)$$1);
    }

    public static int getEnchantmentLevel(CompoundTag $$0) {
        return Mth.clamp($$0.getInt(TAG_ENCH_LEVEL), 0, 255);
    }

    @Nullable
    public static ResourceLocation getEnchantmentId(CompoundTag $$0) {
        return ResourceLocation.tryParse($$0.getString(TAG_ENCH_ID));
    }

    @Nullable
    public static ResourceLocation getEnchantmentId(Enchantment $$0) {
        return BuiltInRegistries.ENCHANTMENT.getKey($$0);
    }

    public static int getItemEnchantmentLevel(Enchantment $$0, ItemStack $$1) {
        if ($$1.isEmpty()) {
            return 0;
        }
        ResourceLocation $$2 = EnchantmentHelper.getEnchantmentId($$0);
        ListTag $$3 = $$1.getEnchantmentTags();
        for (int $$4 = 0; $$4 < $$3.size(); ++$$4) {
            CompoundTag $$5 = $$3.getCompound($$4);
            ResourceLocation $$6 = EnchantmentHelper.getEnchantmentId($$5);
            if ($$6 == null || !$$6.equals($$2)) continue;
            return EnchantmentHelper.getEnchantmentLevel($$5);
        }
        return 0;
    }

    public static Map<Enchantment, Integer> getEnchantments(ItemStack $$0) {
        ListTag $$1 = $$0.is(Items.ENCHANTED_BOOK) ? EnchantedBookItem.getEnchantments($$0) : $$0.getEnchantmentTags();
        return EnchantmentHelper.deserializeEnchantments($$1);
    }

    public static Map<Enchantment, Integer> deserializeEnchantments(ListTag $$0) {
        LinkedHashMap $$1 = Maps.newLinkedHashMap();
        for (int $$2 = 0; $$2 < $$0.size(); ++$$2) {
            CompoundTag $$3 = $$0.getCompound($$2);
            BuiltInRegistries.ENCHANTMENT.getOptional(EnchantmentHelper.getEnchantmentId($$3)).ifPresent(arg_0 -> EnchantmentHelper.lambda$deserializeEnchantments$0((Map)$$1, $$3, arg_0));
        }
        return $$1;
    }

    public static void setEnchantments(Map<Enchantment, Integer> $$0, ItemStack $$1) {
        ListTag $$2 = new ListTag();
        for (Map.Entry $$3 : $$0.entrySet()) {
            Enchantment $$4 = (Enchantment)$$3.getKey();
            if ($$4 == null) continue;
            int $$5 = (Integer)$$3.getValue();
            $$2.add(EnchantmentHelper.storeEnchantment(EnchantmentHelper.getEnchantmentId($$4), $$5));
            if (!$$1.is(Items.ENCHANTED_BOOK)) continue;
            EnchantedBookItem.addEnchantment($$1, new EnchantmentInstance($$4, $$5));
        }
        if ($$2.isEmpty()) {
            $$1.removeTagKey("Enchantments");
        } else if (!$$1.is(Items.ENCHANTED_BOOK)) {
            $$1.addTagElement("Enchantments", $$2);
        }
    }

    private static void runIterationOnItem(EnchantmentVisitor $$0, ItemStack $$1) {
        if ($$1.isEmpty()) {
            return;
        }
        ListTag $$22 = $$1.getEnchantmentTags();
        for (int $$3 = 0; $$3 < $$22.size(); ++$$3) {
            CompoundTag $$4 = $$22.getCompound($$3);
            BuiltInRegistries.ENCHANTMENT.getOptional(EnchantmentHelper.getEnchantmentId($$4)).ifPresent($$2 -> $$0.accept((Enchantment)$$2, EnchantmentHelper.getEnchantmentLevel($$4)));
        }
    }

    private static void runIterationOnInventory(EnchantmentVisitor $$0, Iterable<ItemStack> $$1) {
        for (ItemStack $$2 : $$1) {
            EnchantmentHelper.runIterationOnItem($$0, $$2);
        }
    }

    public static int getDamageProtection(Iterable<ItemStack> $$0, DamageSource $$1) {
        MutableInt $$22 = new MutableInt();
        EnchantmentHelper.runIterationOnInventory(($$2, $$3) -> $$22.add($$2.getDamageProtection($$3, $$1)), $$0);
        return $$22.intValue();
    }

    public static float getDamageBonus(ItemStack $$0, MobType $$1) {
        MutableFloat $$22 = new MutableFloat();
        EnchantmentHelper.runIterationOnItem(($$2, $$3) -> $$22.add($$2.getDamageBonus($$3, $$1)), $$0);
        return $$22.floatValue();
    }

    public static float getSweepingDamageRatio(LivingEntity $$0) {
        int $$1 = EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, $$0);
        if ($$1 > 0) {
            return SweepingEdgeEnchantment.getSweepingDamageRatio($$1);
        }
        return 0.0f;
    }

    public static void doPostHurtEffects(LivingEntity $$0, Entity $$1) {
        EnchantmentVisitor $$22 = ($$2, $$3) -> $$2.doPostHurt($$0, $$1, $$3);
        if ($$0 != null) {
            EnchantmentHelper.runIterationOnInventory($$22, $$0.getAllSlots());
        }
        if ($$1 instanceof Player) {
            EnchantmentHelper.runIterationOnItem($$22, $$0.getMainHandItem());
        }
    }

    public static void doPostDamageEffects(LivingEntity $$0, Entity $$1) {
        EnchantmentVisitor $$22 = ($$2, $$3) -> $$2.doPostAttack($$0, $$1, $$3);
        if ($$0 != null) {
            EnchantmentHelper.runIterationOnInventory($$22, $$0.getAllSlots());
        }
        if ($$0 instanceof Player) {
            EnchantmentHelper.runIterationOnItem($$22, $$0.getMainHandItem());
        }
    }

    public static int getEnchantmentLevel(Enchantment $$0, LivingEntity $$1) {
        Collection $$2 = $$0.getSlotItems($$1).values();
        if ($$2 == null) {
            return 0;
        }
        int $$3 = 0;
        for (ItemStack $$4 : $$2) {
            int $$5 = EnchantmentHelper.getItemEnchantmentLevel($$0, $$4);
            if ($$5 <= $$3) continue;
            $$3 = $$5;
        }
        return $$3;
    }

    public static float getSneakingSpeedBonus(LivingEntity $$0) {
        return (float)EnchantmentHelper.getEnchantmentLevel(Enchantments.SWIFT_SNEAK, $$0) * 0.15f;
    }

    public static int getKnockbackBonus(LivingEntity $$0) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.KNOCKBACK, $$0);
    }

    public static int getFireAspect(LivingEntity $$0) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, $$0);
    }

    public static int getRespiration(LivingEntity $$0) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.RESPIRATION, $$0);
    }

    public static int getDepthStrider(LivingEntity $$0) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.DEPTH_STRIDER, $$0);
    }

    public static int getBlockEfficiency(LivingEntity $$0) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.BLOCK_EFFICIENCY, $$0);
    }

    public static int getFishingLuckBonus(ItemStack $$0) {
        return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FISHING_LUCK, $$0);
    }

    public static int getFishingSpeedBonus(ItemStack $$0) {
        return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FISHING_SPEED, $$0);
    }

    public static int getMobLooting(LivingEntity $$0) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.MOB_LOOTING, $$0);
    }

    public static boolean hasAquaAffinity(LivingEntity $$0) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.AQUA_AFFINITY, $$0) > 0;
    }

    public static boolean hasFrostWalker(LivingEntity $$0) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.FROST_WALKER, $$0) > 0;
    }

    public static boolean hasSoulSpeed(LivingEntity $$0) {
        return EnchantmentHelper.getEnchantmentLevel(Enchantments.SOUL_SPEED, $$0) > 0;
    }

    public static boolean hasBindingCurse(ItemStack $$0) {
        return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BINDING_CURSE, $$0) > 0;
    }

    public static boolean hasVanishingCurse(ItemStack $$0) {
        return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.VANISHING_CURSE, $$0) > 0;
    }

    public static int getLoyalty(ItemStack $$0) {
        return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.LOYALTY, $$0);
    }

    public static int getRiptide(ItemStack $$0) {
        return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.RIPTIDE, $$0);
    }

    public static boolean hasChanneling(ItemStack $$0) {
        return EnchantmentHelper.getItemEnchantmentLevel(Enchantments.CHANNELING, $$0) > 0;
    }

    @Nullable
    public static Map.Entry<EquipmentSlot, ItemStack> getRandomItemWith(Enchantment $$02, LivingEntity $$1) {
        return EnchantmentHelper.getRandomItemWith($$02, $$1, (Predicate<ItemStack>)((Predicate)$$0 -> true));
    }

    @Nullable
    public static Map.Entry<EquipmentSlot, ItemStack> getRandomItemWith(Enchantment $$0, LivingEntity $$1, Predicate<ItemStack> $$2) {
        Map<EquipmentSlot, ItemStack> $$3 = $$0.getSlotItems($$1);
        if ($$3.isEmpty()) {
            return null;
        }
        ArrayList $$4 = Lists.newArrayList();
        for (Map.Entry $$5 : $$3.entrySet()) {
            ItemStack $$6 = (ItemStack)$$5.getValue();
            if ($$6.isEmpty() || EnchantmentHelper.getItemEnchantmentLevel($$0, $$6) <= 0 || !$$2.test((Object)$$6)) continue;
            $$4.add((Object)$$5);
        }
        return $$4.isEmpty() ? null : (Map.Entry)$$4.get($$1.getRandom().nextInt($$4.size()));
    }

    public static int getEnchantmentCost(RandomSource $$0, int $$1, int $$2, ItemStack $$3) {
        Item $$4 = $$3.getItem();
        int $$5 = $$4.getEnchantmentValue();
        if ($$5 <= 0) {
            return 0;
        }
        if ($$2 > 15) {
            $$2 = 15;
        }
        int $$6 = $$0.nextInt(8) + 1 + ($$2 >> 1) + $$0.nextInt($$2 + 1);
        if ($$1 == 0) {
            return Math.max((int)($$6 / 3), (int)1);
        }
        if ($$1 == 1) {
            return $$6 * 2 / 3 + 1;
        }
        return Math.max((int)$$6, (int)($$2 * 2));
    }

    public static ItemStack enchantItem(RandomSource $$0, ItemStack $$1, int $$2, boolean $$3) {
        List<EnchantmentInstance> $$4 = EnchantmentHelper.selectEnchantment($$0, $$1, $$2, $$3);
        boolean $$5 = $$1.is(Items.BOOK);
        if ($$5) {
            $$1 = new ItemStack(Items.ENCHANTED_BOOK);
        }
        for (EnchantmentInstance $$6 : $$4) {
            if ($$5) {
                EnchantedBookItem.addEnchantment($$1, $$6);
                continue;
            }
            $$1.enchant($$6.enchantment, $$6.level);
        }
        return $$1;
    }

    public static List<EnchantmentInstance> selectEnchantment(RandomSource $$0, ItemStack $$1, int $$2, boolean $$3) {
        ArrayList $$4 = Lists.newArrayList();
        Item $$5 = $$1.getItem();
        int $$6 = $$5.getEnchantmentValue();
        if ($$6 <= 0) {
            return $$4;
        }
        $$2 += 1 + $$0.nextInt($$6 / 4 + 1) + $$0.nextInt($$6 / 4 + 1);
        float $$7 = ($$0.nextFloat() + $$0.nextFloat() - 1.0f) * 0.15f;
        List<EnchantmentInstance> $$8 = EnchantmentHelper.getAvailableEnchantmentResults($$2 = Mth.clamp(Math.round((float)((float)$$2 + (float)$$2 * $$7)), 1, Integer.MAX_VALUE), $$1, $$3);
        if (!$$8.isEmpty()) {
            WeightedRandom.getRandomItem($$0, $$8).ifPresent(arg_0 -> ((List)$$4).add(arg_0));
            while ($$0.nextInt(50) <= $$2) {
                if (!$$4.isEmpty()) {
                    EnchantmentHelper.filterCompatibleEnchantments($$8, (EnchantmentInstance)Util.lastOf($$4));
                }
                if ($$8.isEmpty()) break;
                WeightedRandom.getRandomItem($$0, $$8).ifPresent(arg_0 -> ((List)$$4).add(arg_0));
                $$2 /= 2;
            }
        }
        return $$4;
    }

    public static void filterCompatibleEnchantments(List<EnchantmentInstance> $$0, EnchantmentInstance $$1) {
        Iterator $$2 = $$0.iterator();
        while ($$2.hasNext()) {
            if ($$1.enchantment.isCompatibleWith(((EnchantmentInstance)$$2.next()).enchantment)) continue;
            $$2.remove();
        }
    }

    public static boolean isEnchantmentCompatible(Collection<Enchantment> $$0, Enchantment $$1) {
        for (Enchantment $$2 : $$0) {
            if ($$2.isCompatibleWith($$1)) continue;
            return false;
        }
        return true;
    }

    public static List<EnchantmentInstance> getAvailableEnchantmentResults(int $$0, ItemStack $$1, boolean $$2) {
        ArrayList $$3 = Lists.newArrayList();
        Item $$4 = $$1.getItem();
        boolean $$5 = $$1.is(Items.BOOK);
        block0: for (Enchantment $$6 : BuiltInRegistries.ENCHANTMENT) {
            if ($$6.isTreasureOnly() && !$$2 || !$$6.isDiscoverable() || !$$6.category.canEnchant($$4) && !$$5) continue;
            for (int $$7 = $$6.getMaxLevel(); $$7 > $$6.getMinLevel() - 1; --$$7) {
                if ($$0 < $$6.getMinCost($$7) || $$0 > $$6.getMaxCost($$7)) continue;
                $$3.add((Object)new EnchantmentInstance($$6, $$7));
                continue block0;
            }
        }
        return $$3;
    }

    private static /* synthetic */ void lambda$deserializeEnchantments$0(Map $$0, CompoundTag $$1, Enchantment $$2) {
        $$0.put((Object)$$2, (Object)EnchantmentHelper.getEnchantmentLevel($$1));
    }

    @FunctionalInterface
    static interface EnchantmentVisitor {
        public void accept(Enchantment var1, int var2);
    }
}