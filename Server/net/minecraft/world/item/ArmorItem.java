/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMultimap
 *  com.google.common.collect.ImmutableMultimap$Builder
 *  com.google.common.collect.Multimap
 *  java.lang.Enum
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.EnumMap
 *  java.util.List
 *  java.util.UUID
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 *  net.minecraft.world.level.Level
 */
package net.minecraft.world.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Vec3i;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Wearable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

public class ArmorItem
extends Item
implements Wearable {
    private static final EnumMap<Type, UUID> ARMOR_MODIFIER_UUID_PER_TYPE = Util.make(new EnumMap(Type.class), $$0 -> {
        $$0.put((Enum)Type.BOOTS, (Object)UUID.fromString((String)"845DB27C-C624-495F-8C9F-6020A9A58B6B"));
        $$0.put((Enum)Type.LEGGINGS, (Object)UUID.fromString((String)"D8499B04-0E66-4726-AB29-64469D734E0D"));
        $$0.put((Enum)Type.CHESTPLATE, (Object)UUID.fromString((String)"9F3D476D-C118-4544-8365-64846904B48E"));
        $$0.put((Enum)Type.HELMET, (Object)UUID.fromString((String)"2AD3F246-FEE1-4E67-B886-69FD380BB150"));
    });
    public static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior(){

        @Override
        protected ItemStack execute(BlockSource $$0, ItemStack $$1) {
            return ArmorItem.dispenseArmor($$0, $$1) ? $$1 : super.execute($$0, $$1);
        }
    };
    protected final Type type;
    private final int defense;
    private final float toughness;
    protected final float knockbackResistance;
    protected final ArmorMaterial material;
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public static boolean dispenseArmor(BlockSource $$0, ItemStack $$1) {
        Vec3i $$2 = $$0.getPos().relative($$0.getBlockState().getValue(DispenserBlock.FACING));
        List $$3 = $$0.getLevel().getEntitiesOfClass(LivingEntity.class, new AABB((BlockPos)$$2), EntitySelector.NO_SPECTATORS.and((Predicate)new EntitySelector.MobCanWearArmorEntitySelector($$1)));
        if ($$3.isEmpty()) {
            return false;
        }
        LivingEntity $$4 = (LivingEntity)$$3.get(0);
        EquipmentSlot $$5 = Mob.getEquipmentSlotForItem($$1);
        ItemStack $$6 = $$1.split(1);
        $$4.setItemSlot($$5, $$6);
        if ($$4 instanceof Mob) {
            ((Mob)$$4).setDropChance($$5, 2.0f);
            ((Mob)$$4).setPersistenceRequired();
        }
        return true;
    }

    public ArmorItem(ArmorMaterial $$0, Type $$1, Item.Properties $$2) {
        super($$2.defaultDurability($$0.getDurabilityForType($$1)));
        this.material = $$0;
        this.type = $$1;
        this.defense = $$0.getDefenseForType($$1);
        this.toughness = $$0.getToughness();
        this.knockbackResistance = $$0.getKnockbackResistance();
        DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
        ImmutableMultimap.Builder $$3 = ImmutableMultimap.builder();
        UUID $$4 = (UUID)ARMOR_MODIFIER_UUID_PER_TYPE.get((Object)$$1);
        $$3.put((Object)Attributes.ARMOR, (Object)new AttributeModifier($$4, "Armor modifier", (double)this.defense, AttributeModifier.Operation.ADDITION));
        $$3.put((Object)Attributes.ARMOR_TOUGHNESS, (Object)new AttributeModifier($$4, "Armor toughness", (double)this.toughness, AttributeModifier.Operation.ADDITION));
        if ($$0 == ArmorMaterials.NETHERITE) {
            $$3.put((Object)Attributes.KNOCKBACK_RESISTANCE, (Object)new AttributeModifier($$4, "Armor knockback resistance", (double)this.knockbackResistance, AttributeModifier.Operation.ADDITION));
        }
        this.defaultModifiers = $$3.build();
    }

    public Type getType() {
        return this.type;
    }

    public EquipmentSlot getSlot() {
        return this.type.getSlot();
    }

    @Override
    public int getEnchantmentValue() {
        return this.material.getEnchantmentValue();
    }

    public ArmorMaterial getMaterial() {
        return this.material;
    }

    @Override
    public boolean isValidRepairItem(ItemStack $$0, ItemStack $$1) {
        return this.material.getRepairIngredient().test($$1) || super.isValidRepairItem($$0, $$1);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level $$0, Player $$1, InteractionHand $$2) {
        return this.swapWithEquipmentSlot(this, $$0, $$1, $$2);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot $$0) {
        if ($$0 == this.type.getSlot()) {
            return this.defaultModifiers;
        }
        return super.getDefaultAttributeModifiers($$0);
    }

    public int getDefense() {
        return this.defense;
    }

    public float getToughness() {
        return this.toughness;
    }

    @Override
    @Nullable
    public SoundEvent getEquipSound() {
        return this.getMaterial().getEquipSound();
    }

    public static enum Type {
        HELMET(EquipmentSlot.HEAD, "helmet"),
        CHESTPLATE(EquipmentSlot.CHEST, "chestplate"),
        LEGGINGS(EquipmentSlot.LEGS, "leggings"),
        BOOTS(EquipmentSlot.FEET, "boots");

        private final EquipmentSlot slot;
        private final String name;

        private Type(EquipmentSlot $$0, String $$1) {
            this.slot = $$0;
            this.name = $$1;
        }

        public EquipmentSlot getSlot() {
            return this.slot;
        }

        public String getName() {
            return this.name;
        }
    }
}