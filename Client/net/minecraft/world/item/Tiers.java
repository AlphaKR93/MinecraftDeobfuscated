/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Supplier
 */
package net.minecraft.world.item;

import java.util.function.Supplier;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public enum Tiers implements Tier
{
    WOOD(0, 59, 2.0f, 0.0f, 15, (Supplier<Ingredient>)((Supplier)() -> Ingredient.of(ItemTags.PLANKS))),
    STONE(1, 131, 4.0f, 1.0f, 5, (Supplier<Ingredient>)((Supplier)() -> Ingredient.of(ItemTags.STONE_TOOL_MATERIALS))),
    IRON(2, 250, 6.0f, 2.0f, 14, (Supplier<Ingredient>)((Supplier)() -> Ingredient.of(Items.IRON_INGOT))),
    DIAMOND(3, 1561, 8.0f, 3.0f, 10, (Supplier<Ingredient>)((Supplier)() -> Ingredient.of(Items.DIAMOND))),
    GOLD(0, 32, 12.0f, 0.0f, 22, (Supplier<Ingredient>)((Supplier)() -> Ingredient.of(Items.GOLD_INGOT))),
    NETHERITE(4, 2031, 9.0f, 4.0f, 15, (Supplier<Ingredient>)((Supplier)() -> Ingredient.of(Items.NETHERITE_INGOT)));

    private final int level;
    private final int uses;
    private final float speed;
    private final float damage;
    private final int enchantmentValue;
    private final LazyLoadedValue<Ingredient> repairIngredient;

    private Tiers(int $$0, int $$1, float $$2, float $$3, int $$4, Supplier<Ingredient> $$5) {
        this.level = $$0;
        this.uses = $$1;
        this.speed = $$2;
        this.damage = $$3;
        this.enchantmentValue = $$4;
        this.repairIngredient = new LazyLoadedValue<Ingredient>($$5);
    }

    @Override
    public int getUses() {
        return this.uses;
    }

    @Override
    public float getSpeed() {
        return this.speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return this.damage;
    }

    @Override
    public int getLevel() {
        return this.level;
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}