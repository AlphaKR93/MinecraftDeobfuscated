/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item.crafting;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class FireworkRocketRecipe
extends CustomRecipe {
    private static final Ingredient PAPER_INGREDIENT = Ingredient.of(Items.PAPER);
    private static final Ingredient GUNPOWDER_INGREDIENT = Ingredient.of(Items.GUNPOWDER);
    private static final Ingredient STAR_INGREDIENT = Ingredient.of(Items.FIREWORK_STAR);

    public FireworkRocketRecipe(ResourceLocation $$0, CraftingBookCategory $$1) {
        super($$0, $$1);
    }

    @Override
    public boolean matches(CraftingContainer $$0, Level $$1) {
        boolean $$2 = false;
        int $$3 = 0;
        for (int $$4 = 0; $$4 < $$0.getContainerSize(); ++$$4) {
            ItemStack $$5 = $$0.getItem($$4);
            if ($$5.isEmpty()) continue;
            if (PAPER_INGREDIENT.test($$5)) {
                if ($$2) {
                    return false;
                }
                $$2 = true;
                continue;
            }
            if (!(GUNPOWDER_INGREDIENT.test($$5) ? ++$$3 > 3 : !STAR_INGREDIENT.test($$5))) continue;
            return false;
        }
        return $$2 && $$3 >= 1;
    }

    @Override
    public ItemStack assemble(CraftingContainer $$0) {
        ItemStack $$1 = new ItemStack(Items.FIREWORK_ROCKET, 3);
        CompoundTag $$2 = $$1.getOrCreateTagElement("Fireworks");
        ListTag $$3 = new ListTag();
        int $$4 = 0;
        for (int $$5 = 0; $$5 < $$0.getContainerSize(); ++$$5) {
            CompoundTag $$7;
            ItemStack $$6 = $$0.getItem($$5);
            if ($$6.isEmpty()) continue;
            if (GUNPOWDER_INGREDIENT.test($$6)) {
                ++$$4;
                continue;
            }
            if (!STAR_INGREDIENT.test($$6) || ($$7 = $$6.getTagElement("Explosion")) == null) continue;
            $$3.add($$7);
        }
        $$2.putByte("Flight", (byte)$$4);
        if (!$$3.isEmpty()) {
            $$2.put("Explosions", $$3);
        }
        return $$1;
    }

    @Override
    public boolean canCraftInDimensions(int $$0, int $$1) {
        return $$0 * $$1 >= 2;
    }

    @Override
    public ItemStack getResultItem() {
        return new ItemStack(Items.FIREWORK_ROCKET);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.FIREWORK_ROCKET;
    }
}