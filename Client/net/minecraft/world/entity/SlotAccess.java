/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Predicate
 *  net.minecraft.world.entity.LivingEntity
 */
package net.minecraft.world.entity;

import java.util.function.Predicate;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public interface SlotAccess {
    public static final SlotAccess NULL = new SlotAccess(){

        @Override
        public ItemStack get() {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean set(ItemStack $$0) {
            return false;
        }
    };

    public static SlotAccess forContainer(final Container $$0, final int $$1, final Predicate<ItemStack> $$2) {
        return new SlotAccess(){

            @Override
            public ItemStack get() {
                return $$0.getItem($$1);
            }

            @Override
            public boolean set(ItemStack $$02) {
                if (!$$2.test((Object)$$02)) {
                    return false;
                }
                $$0.setItem($$1, $$02);
                return true;
            }
        };
    }

    public static SlotAccess forContainer(Container $$02, int $$1) {
        return SlotAccess.forContainer($$02, $$1, (Predicate<ItemStack>)((Predicate)$$0 -> true));
    }

    public static SlotAccess forEquipmentSlot(final LivingEntity $$0, final EquipmentSlot $$1, final Predicate<ItemStack> $$2) {
        return new SlotAccess(){

            @Override
            public ItemStack get() {
                return $$0.getItemBySlot($$1);
            }

            @Override
            public boolean set(ItemStack $$02) {
                if (!$$2.test((Object)$$02)) {
                    return false;
                }
                $$0.setItemSlot($$1, $$02);
                return true;
            }
        };
    }

    public static SlotAccess forEquipmentSlot(LivingEntity $$02, EquipmentSlot $$1) {
        return SlotAccess.forEquipmentSlot($$02, $$1, (Predicate<ItemStack>)((Predicate)$$0 -> true));
    }

    public ItemStack get();

    public boolean set(ItemStack var1);
}