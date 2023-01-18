/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Wearable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class ElytraItem
extends Item
implements Wearable {
    public ElytraItem(Item.Properties $$0) {
        super($$0);
        DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
    }

    public static boolean isFlyEnabled(ItemStack $$0) {
        return $$0.getDamageValue() < $$0.getMaxDamage() - 1;
    }

    @Override
    public boolean isValidRepairItem(ItemStack $$0, ItemStack $$1) {
        return $$1.is(Items.PHANTOM_MEMBRANE);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level $$0, Player $$1, InteractionHand $$2) {
        ItemStack $$3 = $$1.getItemInHand($$2);
        EquipmentSlot $$4 = Mob.getEquipmentSlotForItem($$3);
        ItemStack $$5 = $$1.getItemBySlot($$4);
        if ($$5.isEmpty()) {
            $$1.setItemSlot($$4, $$3.copy());
            if (!$$0.isClientSide()) {
                $$1.awardStat(Stats.ITEM_USED.get(this));
            }
            $$3.setCount(0);
            return InteractionResultHolder.sidedSuccess($$3, $$0.isClientSide());
        }
        return InteractionResultHolder.fail($$3);
    }

    @Override
    @Nullable
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_ELYTRA;
    }
}