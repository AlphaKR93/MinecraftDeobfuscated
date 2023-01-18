/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.FishingRodItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.item.Wearable;
import net.minecraft.world.level.block.Block;

/*
 * Uses 'sealed' constructs - enablewith --sealed true
 */
public enum EnchantmentCategory {
    ARMOR{

        @Override
        public boolean canEnchant(Item $$0) {
            return $$0 instanceof ArmorItem;
        }
    }
    ,
    ARMOR_FEET{

        @Override
        public boolean canEnchant(Item $$0) {
            return $$0 instanceof ArmorItem && ((ArmorItem)$$0).getSlot() == EquipmentSlot.FEET;
        }
    }
    ,
    ARMOR_LEGS{

        @Override
        public boolean canEnchant(Item $$0) {
            return $$0 instanceof ArmorItem && ((ArmorItem)$$0).getSlot() == EquipmentSlot.LEGS;
        }
    }
    ,
    ARMOR_CHEST{

        @Override
        public boolean canEnchant(Item $$0) {
            return $$0 instanceof ArmorItem && ((ArmorItem)$$0).getSlot() == EquipmentSlot.CHEST;
        }
    }
    ,
    ARMOR_HEAD{

        @Override
        public boolean canEnchant(Item $$0) {
            return $$0 instanceof ArmorItem && ((ArmorItem)$$0).getSlot() == EquipmentSlot.HEAD;
        }
    }
    ,
    WEAPON{

        @Override
        public boolean canEnchant(Item $$0) {
            return $$0 instanceof SwordItem;
        }
    }
    ,
    DIGGER{

        @Override
        public boolean canEnchant(Item $$0) {
            return $$0 instanceof DiggerItem;
        }
    }
    ,
    FISHING_ROD{

        @Override
        public boolean canEnchant(Item $$0) {
            return $$0 instanceof FishingRodItem;
        }
    }
    ,
    TRIDENT{

        @Override
        public boolean canEnchant(Item $$0) {
            return $$0 instanceof TridentItem;
        }
    }
    ,
    BREAKABLE{

        @Override
        public boolean canEnchant(Item $$0) {
            return $$0.canBeDepleted();
        }
    }
    ,
    BOW{

        @Override
        public boolean canEnchant(Item $$0) {
            return $$0 instanceof BowItem;
        }
    }
    ,
    WEARABLE{

        @Override
        public boolean canEnchant(Item $$0) {
            return $$0 instanceof Wearable || Block.byItem($$0) instanceof Wearable;
        }
    }
    ,
    CROSSBOW{

        @Override
        public boolean canEnchant(Item $$0) {
            return $$0 instanceof CrossbowItem;
        }
    }
    ,
    VANISHABLE{

        @Override
        public boolean canEnchant(Item $$0) {
            return $$0 instanceof Vanishable || Block.byItem($$0) instanceof Vanishable || BREAKABLE.canEnchant($$0);
        }
    };


    public abstract boolean canEnchant(Item var1);
}