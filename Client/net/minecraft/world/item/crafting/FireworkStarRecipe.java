/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.Map
 */
package net.minecraft.world.item.crafting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class FireworkStarRecipe
extends CustomRecipe {
    private static final Ingredient SHAPE_INGREDIENT = Ingredient.of(Items.FIRE_CHARGE, Items.FEATHER, Items.GOLD_NUGGET, Items.SKELETON_SKULL, Items.WITHER_SKELETON_SKULL, Items.CREEPER_HEAD, Items.PLAYER_HEAD, Items.DRAGON_HEAD, Items.ZOMBIE_HEAD, Items.PIGLIN_HEAD);
    private static final Ingredient TRAIL_INGREDIENT = Ingredient.of(Items.DIAMOND);
    private static final Ingredient FLICKER_INGREDIENT = Ingredient.of(Items.GLOWSTONE_DUST);
    private static final Map<Item, FireworkRocketItem.Shape> SHAPE_BY_ITEM = (Map)Util.make(Maps.newHashMap(), $$0 -> {
        $$0.put((Object)Items.FIRE_CHARGE, (Object)FireworkRocketItem.Shape.LARGE_BALL);
        $$0.put((Object)Items.FEATHER, (Object)FireworkRocketItem.Shape.BURST);
        $$0.put((Object)Items.GOLD_NUGGET, (Object)FireworkRocketItem.Shape.STAR);
        $$0.put((Object)Items.SKELETON_SKULL, (Object)FireworkRocketItem.Shape.CREEPER);
        $$0.put((Object)Items.WITHER_SKELETON_SKULL, (Object)FireworkRocketItem.Shape.CREEPER);
        $$0.put((Object)Items.CREEPER_HEAD, (Object)FireworkRocketItem.Shape.CREEPER);
        $$0.put((Object)Items.PLAYER_HEAD, (Object)FireworkRocketItem.Shape.CREEPER);
        $$0.put((Object)Items.DRAGON_HEAD, (Object)FireworkRocketItem.Shape.CREEPER);
        $$0.put((Object)Items.ZOMBIE_HEAD, (Object)FireworkRocketItem.Shape.CREEPER);
        $$0.put((Object)Items.PIGLIN_HEAD, (Object)FireworkRocketItem.Shape.CREEPER);
    });
    private static final Ingredient GUNPOWDER_INGREDIENT = Ingredient.of(Items.GUNPOWDER);

    public FireworkStarRecipe(ResourceLocation $$0, CraftingBookCategory $$1) {
        super($$0, $$1);
    }

    @Override
    public boolean matches(CraftingContainer $$0, Level $$1) {
        boolean $$2 = false;
        boolean $$3 = false;
        boolean $$4 = false;
        boolean $$5 = false;
        boolean $$6 = false;
        for (int $$7 = 0; $$7 < $$0.getContainerSize(); ++$$7) {
            ItemStack $$8 = $$0.getItem($$7);
            if ($$8.isEmpty()) continue;
            if (SHAPE_INGREDIENT.test($$8)) {
                if ($$4) {
                    return false;
                }
                $$4 = true;
                continue;
            }
            if (FLICKER_INGREDIENT.test($$8)) {
                if ($$6) {
                    return false;
                }
                $$6 = true;
                continue;
            }
            if (TRAIL_INGREDIENT.test($$8)) {
                if ($$5) {
                    return false;
                }
                $$5 = true;
                continue;
            }
            if (GUNPOWDER_INGREDIENT.test($$8)) {
                if ($$2) {
                    return false;
                }
                $$2 = true;
                continue;
            }
            if ($$8.getItem() instanceof DyeItem) {
                $$3 = true;
                continue;
            }
            return false;
        }
        return $$2 && $$3;
    }

    @Override
    public ItemStack assemble(CraftingContainer $$0, RegistryAccess $$1) {
        ItemStack $$2 = new ItemStack(Items.FIREWORK_STAR);
        CompoundTag $$3 = $$2.getOrCreateTagElement("Explosion");
        FireworkRocketItem.Shape $$4 = FireworkRocketItem.Shape.SMALL_BALL;
        ArrayList $$5 = Lists.newArrayList();
        for (int $$6 = 0; $$6 < $$0.getContainerSize(); ++$$6) {
            ItemStack $$7 = $$0.getItem($$6);
            if ($$7.isEmpty()) continue;
            if (SHAPE_INGREDIENT.test($$7)) {
                $$4 = (FireworkRocketItem.Shape)((Object)SHAPE_BY_ITEM.get((Object)$$7.getItem()));
                continue;
            }
            if (FLICKER_INGREDIENT.test($$7)) {
                $$3.putBoolean("Flicker", true);
                continue;
            }
            if (TRAIL_INGREDIENT.test($$7)) {
                $$3.putBoolean("Trail", true);
                continue;
            }
            if (!($$7.getItem() instanceof DyeItem)) continue;
            $$5.add((Object)((DyeItem)$$7.getItem()).getDyeColor().getFireworkColor());
        }
        $$3.putIntArray("Colors", (List<Integer>)$$5);
        $$3.putByte("Type", (byte)$$4.getId());
        return $$2;
    }

    @Override
    public boolean canCraftInDimensions(int $$0, int $$1) {
        return $$0 * $$1 >= 2;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess $$0) {
        return new ItemStack(Items.FIREWORK_STAR);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.FIREWORK_STAR;
    }
}