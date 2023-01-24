/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Enum
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.EnumMap
 *  java.util.function.Supplier
 */
package net.minecraft.world.item;

import java.util.EnumMap;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public enum ArmorMaterials implements StringRepresentable,
ArmorMaterial
{
    LEATHER("leather", 5, (EnumMap<ArmorItem.Type, Integer>)Util.make(new EnumMap(ArmorItem.Type.class), $$0 -> {
        $$0.put((Enum)ArmorItem.Type.BOOTS, (Object)1);
        $$0.put((Enum)ArmorItem.Type.LEGGINGS, (Object)2);
        $$0.put((Enum)ArmorItem.Type.CHESTPLATE, (Object)3);
        $$0.put((Enum)ArmorItem.Type.HELMET, (Object)1);
    }), 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, (Supplier<Ingredient>)((Supplier)() -> Ingredient.of(Items.LEATHER)), false),
    CHAIN("chainmail", 15, (EnumMap<ArmorItem.Type, Integer>)Util.make(new EnumMap(ArmorItem.Type.class), $$0 -> {
        $$0.put((Enum)ArmorItem.Type.BOOTS, (Object)1);
        $$0.put((Enum)ArmorItem.Type.LEGGINGS, (Object)4);
        $$0.put((Enum)ArmorItem.Type.CHESTPLATE, (Object)5);
        $$0.put((Enum)ArmorItem.Type.HELMET, (Object)2);
    }), 12, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0f, 0.0f, (Supplier<Ingredient>)((Supplier)() -> Ingredient.of(Items.IRON_INGOT)), true),
    IRON("iron", 15, (EnumMap<ArmorItem.Type, Integer>)Util.make(new EnumMap(ArmorItem.Type.class), $$0 -> {
        $$0.put((Enum)ArmorItem.Type.BOOTS, (Object)2);
        $$0.put((Enum)ArmorItem.Type.LEGGINGS, (Object)5);
        $$0.put((Enum)ArmorItem.Type.CHESTPLATE, (Object)6);
        $$0.put((Enum)ArmorItem.Type.HELMET, (Object)2);
    }), 9, SoundEvents.ARMOR_EQUIP_IRON, 0.0f, 0.0f, (Supplier<Ingredient>)((Supplier)() -> Ingredient.of(Items.IRON_INGOT)), true),
    GOLD("gold", 7, (EnumMap<ArmorItem.Type, Integer>)Util.make(new EnumMap(ArmorItem.Type.class), $$0 -> {
        $$0.put((Enum)ArmorItem.Type.BOOTS, (Object)1);
        $$0.put((Enum)ArmorItem.Type.LEGGINGS, (Object)3);
        $$0.put((Enum)ArmorItem.Type.CHESTPLATE, (Object)5);
        $$0.put((Enum)ArmorItem.Type.HELMET, (Object)2);
    }), 25, SoundEvents.ARMOR_EQUIP_GOLD, 0.0f, 0.0f, (Supplier<Ingredient>)((Supplier)() -> Ingredient.of(Items.GOLD_INGOT)), true),
    DIAMOND("diamond", 33, (EnumMap<ArmorItem.Type, Integer>)Util.make(new EnumMap(ArmorItem.Type.class), $$0 -> {
        $$0.put((Enum)ArmorItem.Type.BOOTS, (Object)3);
        $$0.put((Enum)ArmorItem.Type.LEGGINGS, (Object)6);
        $$0.put((Enum)ArmorItem.Type.CHESTPLATE, (Object)8);
        $$0.put((Enum)ArmorItem.Type.HELMET, (Object)3);
    }), 10, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0f, 0.0f, (Supplier<Ingredient>)((Supplier)() -> Ingredient.of(Items.DIAMOND)), true),
    TURTLE("turtle", 25, (EnumMap<ArmorItem.Type, Integer>)Util.make(new EnumMap(ArmorItem.Type.class), $$0 -> {
        $$0.put((Enum)ArmorItem.Type.BOOTS, (Object)2);
        $$0.put((Enum)ArmorItem.Type.LEGGINGS, (Object)5);
        $$0.put((Enum)ArmorItem.Type.CHESTPLATE, (Object)6);
        $$0.put((Enum)ArmorItem.Type.HELMET, (Object)2);
    }), 9, SoundEvents.ARMOR_EQUIP_TURTLE, 0.0f, 0.0f, (Supplier<Ingredient>)((Supplier)() -> Ingredient.of(Items.SCUTE)), true),
    NETHERITE("netherite", 37, (EnumMap<ArmorItem.Type, Integer>)Util.make(new EnumMap(ArmorItem.Type.class), $$0 -> {
        $$0.put((Enum)ArmorItem.Type.BOOTS, (Object)3);
        $$0.put((Enum)ArmorItem.Type.LEGGINGS, (Object)6);
        $$0.put((Enum)ArmorItem.Type.CHESTPLATE, (Object)8);
        $$0.put((Enum)ArmorItem.Type.HELMET, (Object)3);
    }), 15, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0f, 0.1f, (Supplier<Ingredient>)((Supplier)() -> Ingredient.of(Items.NETHERITE_INGOT)), true);

    public static final StringRepresentable.EnumCodec<ArmorMaterials> CODEC;
    private static final EnumMap<ArmorItem.Type, Integer> HEALTH_FUNCTION_FOR_TYPE;
    private final String name;
    private final int durabilityMultiplier;
    private final EnumMap<ArmorItem.Type, Integer> protectionFunctionForType;
    private final int enchantmentValue;
    private final SoundEvent sound;
    private final float toughness;
    private final float knockbackResistance;
    private final LazyLoadedValue<Ingredient> repairIngredient;
    private final boolean canHaveTrims;

    private ArmorMaterials(String $$0, int $$1, EnumMap<ArmorItem.Type, Integer> $$2, int $$3, SoundEvent $$4, float $$5, float $$6, Supplier<Ingredient> $$7, boolean $$8) {
        this.name = $$0;
        this.durabilityMultiplier = $$1;
        this.protectionFunctionForType = $$2;
        this.enchantmentValue = $$3;
        this.sound = $$4;
        this.toughness = $$5;
        this.knockbackResistance = $$6;
        this.repairIngredient = new LazyLoadedValue<Ingredient>($$7);
        this.canHaveTrims = $$8;
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type $$0) {
        return (Integer)HEALTH_FUNCTION_FOR_TYPE.get((Object)$$0) * this.durabilityMultiplier;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type $$0) {
        return (Integer)this.protectionFunctionForType.get((Object)$$0);
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.sound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }

    @Override
    public boolean canHaveTrims() {
        return this.canHaveTrims;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    static {
        CODEC = StringRepresentable.fromEnum((Supplier<E[]>)((Supplier)ArmorMaterials::values));
        HEALTH_FUNCTION_FOR_TYPE = Util.make(new EnumMap(ArmorItem.Type.class), $$0 -> {
            $$0.put((Enum)ArmorItem.Type.BOOTS, (Object)13);
            $$0.put((Enum)ArmorItem.Type.LEGGINGS, (Object)15);
            $$0.put((Enum)ArmorItem.Type.CHESTPLATE, (Object)16);
            $$0.put((Enum)ArmorItem.Type.HELMET, (Object)11);
        });
    }
}