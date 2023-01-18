/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 */
package net.minecraft.world.item;

import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface DyeableLeatherItem {
    public static final String TAG_COLOR = "color";
    public static final String TAG_DISPLAY = "display";
    public static final int DEFAULT_LEATHER_COLOR = 10511680;

    default public boolean hasCustomColor(ItemStack $$0) {
        CompoundTag $$1 = $$0.getTagElement(TAG_DISPLAY);
        return $$1 != null && $$1.contains(TAG_COLOR, 99);
    }

    default public int getColor(ItemStack $$0) {
        CompoundTag $$1 = $$0.getTagElement(TAG_DISPLAY);
        if ($$1 != null && $$1.contains(TAG_COLOR, 99)) {
            return $$1.getInt(TAG_COLOR);
        }
        return 10511680;
    }

    default public void clearColor(ItemStack $$0) {
        CompoundTag $$1 = $$0.getTagElement(TAG_DISPLAY);
        if ($$1 != null && $$1.contains(TAG_COLOR)) {
            $$1.remove(TAG_COLOR);
        }
    }

    default public void setColor(ItemStack $$0, int $$1) {
        $$0.getOrCreateTagElement(TAG_DISPLAY).putInt(TAG_COLOR, $$1);
    }

    public static ItemStack dyeArmor(ItemStack $$0, List<DyeItem> $$1) {
        ItemStack $$2 = ItemStack.EMPTY;
        int[] $$3 = new int[3];
        int $$4 = 0;
        int $$5 = 0;
        DyeableLeatherItem $$6 = null;
        Item $$7 = $$0.getItem();
        if ($$7 instanceof DyeableLeatherItem) {
            $$6 = (DyeableLeatherItem)((Object)$$7);
            $$2 = $$0.copy();
            $$2.setCount(1);
            if ($$6.hasCustomColor($$0)) {
                int $$8 = $$6.getColor($$2);
                float $$9 = (float)($$8 >> 16 & 0xFF) / 255.0f;
                float $$10 = (float)($$8 >> 8 & 0xFF) / 255.0f;
                float $$11 = (float)($$8 & 0xFF) / 255.0f;
                $$4 += (int)(Math.max((float)$$9, (float)Math.max((float)$$10, (float)$$11)) * 255.0f);
                $$3[0] = $$3[0] + (int)($$9 * 255.0f);
                $$3[1] = $$3[1] + (int)($$10 * 255.0f);
                $$3[2] = $$3[2] + (int)($$11 * 255.0f);
                ++$$5;
            }
            for (DyeItem $$12 : $$1) {
                float[] $$13 = $$12.getDyeColor().getTextureDiffuseColors();
                int $$14 = (int)($$13[0] * 255.0f);
                int $$15 = (int)($$13[1] * 255.0f);
                int $$16 = (int)($$13[2] * 255.0f);
                $$4 += Math.max((int)$$14, (int)Math.max((int)$$15, (int)$$16));
                $$3[0] = $$3[0] + $$14;
                $$3[1] = $$3[1] + $$15;
                $$3[2] = $$3[2] + $$16;
                ++$$5;
            }
        }
        if ($$6 == null) {
            return ItemStack.EMPTY;
        }
        int $$17 = $$3[0] / $$5;
        int $$18 = $$3[1] / $$5;
        int $$19 = $$3[2] / $$5;
        float $$20 = (float)$$4 / (float)$$5;
        float $$21 = Math.max((int)$$17, (int)Math.max((int)$$18, (int)$$19));
        $$17 = (int)((float)$$17 * $$20 / $$21);
        $$18 = (int)((float)$$18 * $$20 / $$21);
        $$19 = (int)((float)$$19 * $$20 / $$21);
        int $$22 = $$17;
        $$22 = ($$22 << 8) + $$18;
        $$22 = ($$22 << 8) + $$19;
        $$6.setColor($$2, $$22);
        return $$2;
    }
}