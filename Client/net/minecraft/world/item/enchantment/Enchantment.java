/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Object
 *  java.lang.String
 *  java.util.EnumMap
 *  java.util.Map
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item.enchantment;

import com.google.common.collect.Maps;
import java.util.EnumMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public abstract class Enchantment {
    private final EquipmentSlot[] slots;
    private final Rarity rarity;
    public final EnchantmentCategory category;
    @Nullable
    protected String descriptionId;

    @Nullable
    public static Enchantment byId(int $$0) {
        return (Enchantment)BuiltInRegistries.ENCHANTMENT.byId($$0);
    }

    protected Enchantment(Rarity $$0, EnchantmentCategory $$1, EquipmentSlot[] $$2) {
        this.rarity = $$0;
        this.category = $$1;
        this.slots = $$2;
    }

    public Map<EquipmentSlot, ItemStack> getSlotItems(LivingEntity $$0) {
        EnumMap $$1 = Maps.newEnumMap(EquipmentSlot.class);
        for (EquipmentSlot $$2 : this.slots) {
            ItemStack $$3 = $$0.getItemBySlot($$2);
            if ($$3.isEmpty()) continue;
            $$1.put((Object)$$2, (Object)$$3);
        }
        return $$1;
    }

    public Rarity getRarity() {
        return this.rarity;
    }

    public int getMinLevel() {
        return 1;
    }

    public int getMaxLevel() {
        return 1;
    }

    public int getMinCost(int $$0) {
        return 1 + $$0 * 10;
    }

    public int getMaxCost(int $$0) {
        return this.getMinCost($$0) + 5;
    }

    public int getDamageProtection(int $$0, DamageSource $$1) {
        return 0;
    }

    public float getDamageBonus(int $$0, MobType $$1) {
        return 0.0f;
    }

    public final boolean isCompatibleWith(Enchantment $$0) {
        return this.checkCompatibility($$0) && $$0.checkCompatibility(this);
    }

    protected boolean checkCompatibility(Enchantment $$0) {
        return this != $$0;
    }

    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("enchantment", BuiltInRegistries.ENCHANTMENT.getKey(this));
        }
        return this.descriptionId;
    }

    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }

    public Component getFullname(int $$0) {
        MutableComponent $$1 = Component.translatable(this.getDescriptionId());
        if (this.isCurse()) {
            $$1.withStyle(ChatFormatting.RED);
        } else {
            $$1.withStyle(ChatFormatting.GRAY);
        }
        if ($$0 != 1 || this.getMaxLevel() != 1) {
            $$1.append(" ").append(Component.translatable("enchantment.level." + $$0));
        }
        return $$1;
    }

    public boolean canEnchant(ItemStack $$0) {
        return this.category.canEnchant($$0.getItem());
    }

    public void doPostAttack(LivingEntity $$0, Entity $$1, int $$2) {
    }

    public void doPostHurt(LivingEntity $$0, Entity $$1, int $$2) {
    }

    public boolean isTreasureOnly() {
        return false;
    }

    public boolean isCurse() {
        return false;
    }

    public boolean isTradeable() {
        return true;
    }

    public boolean isDiscoverable() {
        return true;
    }

    public static enum Rarity {
        COMMON(10),
        UNCOMMON(5),
        RARE(2),
        VERY_RARE(1);

        private final int weight;

        private Rarity(int $$0) {
            this.weight = $$0;
        }

        public int getWeight() {
            return this.weight;
        }
    }
}