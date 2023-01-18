/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.world.entity.ai.attributes;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class Attributes {
    public static final Attribute MAX_HEALTH = Attributes.register("generic.max_health", new RangedAttribute("attribute.name.generic.max_health", 20.0, 1.0, 1024.0).setSyncable(true));
    public static final Attribute FOLLOW_RANGE = Attributes.register("generic.follow_range", new RangedAttribute("attribute.name.generic.follow_range", 32.0, 0.0, 2048.0));
    public static final Attribute KNOCKBACK_RESISTANCE = Attributes.register("generic.knockback_resistance", new RangedAttribute("attribute.name.generic.knockback_resistance", 0.0, 0.0, 1.0));
    public static final Attribute MOVEMENT_SPEED = Attributes.register("generic.movement_speed", new RangedAttribute("attribute.name.generic.movement_speed", 0.7f, 0.0, 1024.0).setSyncable(true));
    public static final Attribute FLYING_SPEED = Attributes.register("generic.flying_speed", new RangedAttribute("attribute.name.generic.flying_speed", 0.4f, 0.0, 1024.0).setSyncable(true));
    public static final Attribute ATTACK_DAMAGE = Attributes.register("generic.attack_damage", new RangedAttribute("attribute.name.generic.attack_damage", 2.0, 0.0, 2048.0));
    public static final Attribute ATTACK_KNOCKBACK = Attributes.register("generic.attack_knockback", new RangedAttribute("attribute.name.generic.attack_knockback", 0.0, 0.0, 5.0));
    public static final Attribute ATTACK_SPEED = Attributes.register("generic.attack_speed", new RangedAttribute("attribute.name.generic.attack_speed", 4.0, 0.0, 1024.0).setSyncable(true));
    public static final Attribute ARMOR = Attributes.register("generic.armor", new RangedAttribute("attribute.name.generic.armor", 0.0, 0.0, 30.0).setSyncable(true));
    public static final Attribute ARMOR_TOUGHNESS = Attributes.register("generic.armor_toughness", new RangedAttribute("attribute.name.generic.armor_toughness", 0.0, 0.0, 20.0).setSyncable(true));
    public static final Attribute LUCK = Attributes.register("generic.luck", new RangedAttribute("attribute.name.generic.luck", 0.0, -1024.0, 1024.0).setSyncable(true));
    public static final Attribute SPAWN_REINFORCEMENTS_CHANCE = Attributes.register("zombie.spawn_reinforcements", new RangedAttribute("attribute.name.zombie.spawn_reinforcements", 0.0, 0.0, 1.0));
    public static final Attribute JUMP_STRENGTH = Attributes.register("horse.jump_strength", new RangedAttribute("attribute.name.horse.jump_strength", 0.7, 0.0, 2.0).setSyncable(true));

    private static Attribute register(String $$0, Attribute $$1) {
        return Registry.register(BuiltInRegistries.ATTRIBUTE, $$0, $$1);
    }
}