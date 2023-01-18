/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.util.List
 *  java.util.function.Predicate
 */
package net.minecraft.world.item.alchemy;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class PotionBrewing {
    public static final int BREWING_TIME_SECONDS = 20;
    private static final List<Mix<Potion>> POTION_MIXES = Lists.newArrayList();
    private static final List<Mix<Item>> CONTAINER_MIXES = Lists.newArrayList();
    private static final List<Ingredient> ALLOWED_CONTAINERS = Lists.newArrayList();
    private static final Predicate<ItemStack> ALLOWED_CONTAINER = $$0 -> {
        for (Ingredient $$1 : ALLOWED_CONTAINERS) {
            if (!$$1.test((ItemStack)$$0)) continue;
            return true;
        }
        return false;
    };

    public static boolean isIngredient(ItemStack $$0) {
        return PotionBrewing.isContainerIngredient($$0) || PotionBrewing.isPotionIngredient($$0);
    }

    protected static boolean isContainerIngredient(ItemStack $$0) {
        int $$2 = CONTAINER_MIXES.size();
        for (int $$1 = 0; $$1 < $$2; ++$$1) {
            if (!((Mix)PotionBrewing.CONTAINER_MIXES.get((int)$$1)).ingredient.test($$0)) continue;
            return true;
        }
        return false;
    }

    protected static boolean isPotionIngredient(ItemStack $$0) {
        int $$2 = POTION_MIXES.size();
        for (int $$1 = 0; $$1 < $$2; ++$$1) {
            if (!((Mix)PotionBrewing.POTION_MIXES.get((int)$$1)).ingredient.test($$0)) continue;
            return true;
        }
        return false;
    }

    public static boolean isBrewablePotion(Potion $$0) {
        int $$2 = POTION_MIXES.size();
        for (int $$1 = 0; $$1 < $$2; ++$$1) {
            if (((Mix)PotionBrewing.POTION_MIXES.get((int)$$1)).to != $$0) continue;
            return true;
        }
        return false;
    }

    public static boolean hasMix(ItemStack $$0, ItemStack $$1) {
        if (!ALLOWED_CONTAINER.test((Object)$$0)) {
            return false;
        }
        return PotionBrewing.hasContainerMix($$0, $$1) || PotionBrewing.hasPotionMix($$0, $$1);
    }

    protected static boolean hasContainerMix(ItemStack $$0, ItemStack $$1) {
        Item $$2 = $$0.getItem();
        int $$4 = CONTAINER_MIXES.size();
        for (int $$3 = 0; $$3 < $$4; ++$$3) {
            Mix $$5 = (Mix)CONTAINER_MIXES.get($$3);
            if ($$5.from != $$2 || !$$5.ingredient.test($$1)) continue;
            return true;
        }
        return false;
    }

    protected static boolean hasPotionMix(ItemStack $$0, ItemStack $$1) {
        Potion $$2 = PotionUtils.getPotion($$0);
        int $$4 = POTION_MIXES.size();
        for (int $$3 = 0; $$3 < $$4; ++$$3) {
            Mix $$5 = (Mix)POTION_MIXES.get($$3);
            if ($$5.from != $$2 || !$$5.ingredient.test($$1)) continue;
            return true;
        }
        return false;
    }

    public static ItemStack mix(ItemStack $$0, ItemStack $$1) {
        if (!$$1.isEmpty()) {
            Potion $$2 = PotionUtils.getPotion($$1);
            Item $$3 = $$1.getItem();
            int $$5 = CONTAINER_MIXES.size();
            for (int $$4 = 0; $$4 < $$5; ++$$4) {
                Mix $$6 = (Mix)CONTAINER_MIXES.get($$4);
                if ($$6.from != $$3 || !$$6.ingredient.test($$0)) continue;
                return PotionUtils.setPotion(new ItemStack((ItemLike)$$6.to), $$2);
            }
            int $$8 = POTION_MIXES.size();
            for (int $$7 = 0; $$7 < $$8; ++$$7) {
                Mix $$9 = (Mix)POTION_MIXES.get($$7);
                if ($$9.from != $$2 || !$$9.ingredient.test($$0)) continue;
                return PotionUtils.setPotion(new ItemStack($$3), (Potion)$$9.to);
            }
        }
        return $$1;
    }

    public static void bootStrap() {
        PotionBrewing.addContainer(Items.POTION);
        PotionBrewing.addContainer(Items.SPLASH_POTION);
        PotionBrewing.addContainer(Items.LINGERING_POTION);
        PotionBrewing.addContainerRecipe(Items.POTION, Items.GUNPOWDER, Items.SPLASH_POTION);
        PotionBrewing.addContainerRecipe(Items.SPLASH_POTION, Items.DRAGON_BREATH, Items.LINGERING_POTION);
        PotionBrewing.addMix(Potions.WATER, Items.GLISTERING_MELON_SLICE, Potions.MUNDANE);
        PotionBrewing.addMix(Potions.WATER, Items.GHAST_TEAR, Potions.MUNDANE);
        PotionBrewing.addMix(Potions.WATER, Items.RABBIT_FOOT, Potions.MUNDANE);
        PotionBrewing.addMix(Potions.WATER, Items.BLAZE_POWDER, Potions.MUNDANE);
        PotionBrewing.addMix(Potions.WATER, Items.SPIDER_EYE, Potions.MUNDANE);
        PotionBrewing.addMix(Potions.WATER, Items.SUGAR, Potions.MUNDANE);
        PotionBrewing.addMix(Potions.WATER, Items.MAGMA_CREAM, Potions.MUNDANE);
        PotionBrewing.addMix(Potions.WATER, Items.GLOWSTONE_DUST, Potions.THICK);
        PotionBrewing.addMix(Potions.WATER, Items.REDSTONE, Potions.MUNDANE);
        PotionBrewing.addMix(Potions.WATER, Items.NETHER_WART, Potions.AWKWARD);
        PotionBrewing.addMix(Potions.AWKWARD, Items.GOLDEN_CARROT, Potions.NIGHT_VISION);
        PotionBrewing.addMix(Potions.NIGHT_VISION, Items.REDSTONE, Potions.LONG_NIGHT_VISION);
        PotionBrewing.addMix(Potions.NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.INVISIBILITY);
        PotionBrewing.addMix(Potions.LONG_NIGHT_VISION, Items.FERMENTED_SPIDER_EYE, Potions.LONG_INVISIBILITY);
        PotionBrewing.addMix(Potions.INVISIBILITY, Items.REDSTONE, Potions.LONG_INVISIBILITY);
        PotionBrewing.addMix(Potions.AWKWARD, Items.MAGMA_CREAM, Potions.FIRE_RESISTANCE);
        PotionBrewing.addMix(Potions.FIRE_RESISTANCE, Items.REDSTONE, Potions.LONG_FIRE_RESISTANCE);
        PotionBrewing.addMix(Potions.AWKWARD, Items.RABBIT_FOOT, Potions.LEAPING);
        PotionBrewing.addMix(Potions.LEAPING, Items.REDSTONE, Potions.LONG_LEAPING);
        PotionBrewing.addMix(Potions.LEAPING, Items.GLOWSTONE_DUST, Potions.STRONG_LEAPING);
        PotionBrewing.addMix(Potions.LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
        PotionBrewing.addMix(Potions.LONG_LEAPING, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
        PotionBrewing.addMix(Potions.SLOWNESS, Items.REDSTONE, Potions.LONG_SLOWNESS);
        PotionBrewing.addMix(Potions.SLOWNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SLOWNESS);
        PotionBrewing.addMix(Potions.AWKWARD, Items.TURTLE_HELMET, Potions.TURTLE_MASTER);
        PotionBrewing.addMix(Potions.TURTLE_MASTER, Items.REDSTONE, Potions.LONG_TURTLE_MASTER);
        PotionBrewing.addMix(Potions.TURTLE_MASTER, Items.GLOWSTONE_DUST, Potions.STRONG_TURTLE_MASTER);
        PotionBrewing.addMix(Potions.SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.SLOWNESS);
        PotionBrewing.addMix(Potions.LONG_SWIFTNESS, Items.FERMENTED_SPIDER_EYE, Potions.LONG_SLOWNESS);
        PotionBrewing.addMix(Potions.AWKWARD, Items.SUGAR, Potions.SWIFTNESS);
        PotionBrewing.addMix(Potions.SWIFTNESS, Items.REDSTONE, Potions.LONG_SWIFTNESS);
        PotionBrewing.addMix(Potions.SWIFTNESS, Items.GLOWSTONE_DUST, Potions.STRONG_SWIFTNESS);
        PotionBrewing.addMix(Potions.AWKWARD, Items.PUFFERFISH, Potions.WATER_BREATHING);
        PotionBrewing.addMix(Potions.WATER_BREATHING, Items.REDSTONE, Potions.LONG_WATER_BREATHING);
        PotionBrewing.addMix(Potions.AWKWARD, Items.GLISTERING_MELON_SLICE, Potions.HEALING);
        PotionBrewing.addMix(Potions.HEALING, Items.GLOWSTONE_DUST, Potions.STRONG_HEALING);
        PotionBrewing.addMix(Potions.HEALING, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
        PotionBrewing.addMix(Potions.STRONG_HEALING, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
        PotionBrewing.addMix(Potions.HARMING, Items.GLOWSTONE_DUST, Potions.STRONG_HARMING);
        PotionBrewing.addMix(Potions.POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
        PotionBrewing.addMix(Potions.LONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.HARMING);
        PotionBrewing.addMix(Potions.STRONG_POISON, Items.FERMENTED_SPIDER_EYE, Potions.STRONG_HARMING);
        PotionBrewing.addMix(Potions.AWKWARD, Items.SPIDER_EYE, Potions.POISON);
        PotionBrewing.addMix(Potions.POISON, Items.REDSTONE, Potions.LONG_POISON);
        PotionBrewing.addMix(Potions.POISON, Items.GLOWSTONE_DUST, Potions.STRONG_POISON);
        PotionBrewing.addMix(Potions.AWKWARD, Items.GHAST_TEAR, Potions.REGENERATION);
        PotionBrewing.addMix(Potions.REGENERATION, Items.REDSTONE, Potions.LONG_REGENERATION);
        PotionBrewing.addMix(Potions.REGENERATION, Items.GLOWSTONE_DUST, Potions.STRONG_REGENERATION);
        PotionBrewing.addMix(Potions.AWKWARD, Items.BLAZE_POWDER, Potions.STRENGTH);
        PotionBrewing.addMix(Potions.STRENGTH, Items.REDSTONE, Potions.LONG_STRENGTH);
        PotionBrewing.addMix(Potions.STRENGTH, Items.GLOWSTONE_DUST, Potions.STRONG_STRENGTH);
        PotionBrewing.addMix(Potions.WATER, Items.FERMENTED_SPIDER_EYE, Potions.WEAKNESS);
        PotionBrewing.addMix(Potions.WEAKNESS, Items.REDSTONE, Potions.LONG_WEAKNESS);
        PotionBrewing.addMix(Potions.AWKWARD, Items.PHANTOM_MEMBRANE, Potions.SLOW_FALLING);
        PotionBrewing.addMix(Potions.SLOW_FALLING, Items.REDSTONE, Potions.LONG_SLOW_FALLING);
    }

    private static void addContainerRecipe(Item $$0, Item $$1, Item $$2) {
        if (!($$0 instanceof PotionItem)) {
            throw new IllegalArgumentException("Expected a potion, got: " + BuiltInRegistries.ITEM.getKey($$0));
        }
        if (!($$2 instanceof PotionItem)) {
            throw new IllegalArgumentException("Expected a potion, got: " + BuiltInRegistries.ITEM.getKey($$2));
        }
        CONTAINER_MIXES.add(new Mix<Item>($$0, Ingredient.of($$1), $$2));
    }

    private static void addContainer(Item $$0) {
        if (!($$0 instanceof PotionItem)) {
            throw new IllegalArgumentException("Expected a potion, got: " + BuiltInRegistries.ITEM.getKey($$0));
        }
        ALLOWED_CONTAINERS.add((Object)Ingredient.of($$0));
    }

    private static void addMix(Potion $$0, Item $$1, Potion $$2) {
        POTION_MIXES.add(new Mix<Potion>($$0, Ingredient.of($$1), $$2));
    }

    static class Mix<T> {
        final T from;
        final Ingredient ingredient;
        final T to;

        public Mix(T $$0, Ingredient $$1, T $$2) {
            this.from = $$0;
            this.ingredient = $$1;
            this.to = $$2;
        }
    }
}