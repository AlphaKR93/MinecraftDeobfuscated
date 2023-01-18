/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.util.Pair
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item.alchemy;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;

public class PotionUtils {
    public static final String TAG_CUSTOM_POTION_EFFECTS = "CustomPotionEffects";
    public static final String TAG_CUSTOM_POTION_COLOR = "CustomPotionColor";
    public static final String TAG_POTION = "Potion";
    private static final int EMPTY_COLOR = 0xF800F8;
    private static final Component NO_EFFECT = Component.translatable("effect.none").withStyle(ChatFormatting.GRAY);

    public static List<MobEffectInstance> getMobEffects(ItemStack $$0) {
        return PotionUtils.getAllEffects($$0.getTag());
    }

    public static List<MobEffectInstance> getAllEffects(Potion $$0, Collection<MobEffectInstance> $$1) {
        ArrayList $$2 = Lists.newArrayList();
        $$2.addAll($$0.getEffects());
        $$2.addAll($$1);
        return $$2;
    }

    public static List<MobEffectInstance> getAllEffects(@Nullable CompoundTag $$0) {
        ArrayList $$1 = Lists.newArrayList();
        $$1.addAll(PotionUtils.getPotion($$0).getEffects());
        PotionUtils.getCustomEffects($$0, (List<MobEffectInstance>)$$1);
        return $$1;
    }

    public static List<MobEffectInstance> getCustomEffects(ItemStack $$0) {
        return PotionUtils.getCustomEffects($$0.getTag());
    }

    public static List<MobEffectInstance> getCustomEffects(@Nullable CompoundTag $$0) {
        ArrayList $$1 = Lists.newArrayList();
        PotionUtils.getCustomEffects($$0, (List<MobEffectInstance>)$$1);
        return $$1;
    }

    public static void getCustomEffects(@Nullable CompoundTag $$0, List<MobEffectInstance> $$1) {
        if ($$0 != null && $$0.contains(TAG_CUSTOM_POTION_EFFECTS, 9)) {
            ListTag $$2 = $$0.getList(TAG_CUSTOM_POTION_EFFECTS, 10);
            for (int $$3 = 0; $$3 < $$2.size(); ++$$3) {
                CompoundTag $$4 = $$2.getCompound($$3);
                MobEffectInstance $$5 = MobEffectInstance.load($$4);
                if ($$5 == null) continue;
                $$1.add((Object)$$5);
            }
        }
    }

    public static int getColor(ItemStack $$0) {
        CompoundTag $$1 = $$0.getTag();
        if ($$1 != null && $$1.contains(TAG_CUSTOM_POTION_COLOR, 99)) {
            return $$1.getInt(TAG_CUSTOM_POTION_COLOR);
        }
        return PotionUtils.getPotion($$0) == Potions.EMPTY ? 0xF800F8 : PotionUtils.getColor(PotionUtils.getMobEffects($$0));
    }

    public static int getColor(Potion $$0) {
        return $$0 == Potions.EMPTY ? 0xF800F8 : PotionUtils.getColor($$0.getEffects());
    }

    public static int getColor(Collection<MobEffectInstance> $$0) {
        int $$1 = 3694022;
        if ($$0.isEmpty()) {
            return 3694022;
        }
        float $$2 = 0.0f;
        float $$3 = 0.0f;
        float $$4 = 0.0f;
        int $$5 = 0;
        for (MobEffectInstance $$6 : $$0) {
            if (!$$6.isVisible()) continue;
            int $$7 = $$6.getEffect().getColor();
            int $$8 = $$6.getAmplifier() + 1;
            $$2 += (float)($$8 * ($$7 >> 16 & 0xFF)) / 255.0f;
            $$3 += (float)($$8 * ($$7 >> 8 & 0xFF)) / 255.0f;
            $$4 += (float)($$8 * ($$7 >> 0 & 0xFF)) / 255.0f;
            $$5 += $$8;
        }
        if ($$5 == 0) {
            return 0;
        }
        $$2 = $$2 / (float)$$5 * 255.0f;
        $$3 = $$3 / (float)$$5 * 255.0f;
        $$4 = $$4 / (float)$$5 * 255.0f;
        return (int)$$2 << 16 | (int)$$3 << 8 | (int)$$4;
    }

    public static Potion getPotion(ItemStack $$0) {
        return PotionUtils.getPotion($$0.getTag());
    }

    public static Potion getPotion(@Nullable CompoundTag $$0) {
        if ($$0 == null) {
            return Potions.EMPTY;
        }
        return Potion.byName($$0.getString(TAG_POTION));
    }

    public static ItemStack setPotion(ItemStack $$0, Potion $$1) {
        ResourceLocation $$2 = BuiltInRegistries.POTION.getKey($$1);
        if ($$1 == Potions.EMPTY) {
            $$0.removeTagKey(TAG_POTION);
        } else {
            $$0.getOrCreateTag().putString(TAG_POTION, $$2.toString());
        }
        return $$0;
    }

    public static ItemStack setCustomEffects(ItemStack $$0, Collection<MobEffectInstance> $$1) {
        if ($$1.isEmpty()) {
            return $$0;
        }
        CompoundTag $$2 = $$0.getOrCreateTag();
        ListTag $$3 = $$2.getList(TAG_CUSTOM_POTION_EFFECTS, 9);
        for (MobEffectInstance $$4 : $$1) {
            $$3.add($$4.save(new CompoundTag()));
        }
        $$2.put(TAG_CUSTOM_POTION_EFFECTS, $$3);
        return $$0;
    }

    public static void addPotionTooltip(ItemStack $$0, List<Component> $$1, float $$2) {
        PotionUtils.addPotionTooltip(PotionUtils.getMobEffects($$0), $$1, $$2);
    }

    public static void addPotionTooltip(List<MobEffectInstance> $$0, List<Component> $$1, float $$2) {
        ArrayList $$3 = Lists.newArrayList();
        if ($$0.isEmpty()) {
            $$1.add((Object)NO_EFFECT);
        } else {
            for (MobEffectInstance $$4 : $$0) {
                MutableComponent $$5 = Component.translatable($$4.getDescriptionId());
                MobEffect $$6 = $$4.getEffect();
                Map<Attribute, AttributeModifier> $$7 = $$6.getAttributeModifiers();
                if (!$$7.isEmpty()) {
                    for (Map.Entry $$8 : $$7.entrySet()) {
                        AttributeModifier $$9 = (AttributeModifier)$$8.getValue();
                        AttributeModifier $$10 = new AttributeModifier($$9.getName(), $$6.getAttributeModifierValue($$4.getAmplifier(), $$9), $$9.getOperation());
                        $$3.add((Object)new Pair((Object)((Attribute)$$8.getKey()), (Object)$$10));
                    }
                }
                if ($$4.getAmplifier() > 0) {
                    $$5 = Component.translatable("potion.withAmplifier", $$5, Component.translatable("potion.potency." + $$4.getAmplifier()));
                }
                if ($$4.getDuration() > 20) {
                    $$5 = Component.translatable("potion.withDuration", $$5, MobEffectUtil.formatDuration($$4, $$2));
                }
                $$1.add((Object)$$5.withStyle($$6.getCategory().getTooltipFormatting()));
            }
        }
        if (!$$3.isEmpty()) {
            $$1.add((Object)CommonComponents.EMPTY);
            $$1.add((Object)Component.translatable("potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));
            for (Pair $$11 : $$3) {
                double $$15;
                AttributeModifier $$12 = (AttributeModifier)$$11.getSecond();
                double $$13 = $$12.getAmount();
                if ($$12.getOperation() == AttributeModifier.Operation.MULTIPLY_BASE || $$12.getOperation() == AttributeModifier.Operation.MULTIPLY_TOTAL) {
                    double $$14 = $$12.getAmount() * 100.0;
                } else {
                    $$15 = $$12.getAmount();
                }
                if ($$13 > 0.0) {
                    $$1.add((Object)Component.translatable("attribute.modifier.plus." + $$12.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format($$15), Component.translatable(((Attribute)$$11.getFirst()).getDescriptionId())).withStyle(ChatFormatting.BLUE));
                    continue;
                }
                if (!($$13 < 0.0)) continue;
                $$1.add((Object)Component.translatable("attribute.modifier.take." + $$12.getOperation().toValue(), ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format($$15 *= -1.0), Component.translatable(((Attribute)$$11.getFirst()).getDescriptionId())).withStyle(ChatFormatting.RED));
            }
        }
    }
}