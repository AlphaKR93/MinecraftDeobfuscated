/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class FireworkStarItem
extends Item {
    public FireworkStarItem(Item.Properties $$0) {
        super($$0);
    }

    @Override
    public void appendHoverText(ItemStack $$0, @Nullable Level $$1, List<Component> $$2, TooltipFlag $$3) {
        CompoundTag $$4 = $$0.getTagElement("Explosion");
        if ($$4 != null) {
            FireworkStarItem.appendHoverText($$4, $$2);
        }
    }

    public static void appendHoverText(CompoundTag $$0, List<Component> $$1) {
        int[] $$4;
        FireworkRocketItem.Shape $$2 = FireworkRocketItem.Shape.byId($$0.getByte("Type"));
        $$1.add((Object)Component.translatable("item.minecraft.firework_star.shape." + $$2.getName()).withStyle(ChatFormatting.GRAY));
        int[] $$3 = $$0.getIntArray("Colors");
        if ($$3.length > 0) {
            $$1.add((Object)FireworkStarItem.appendColors(Component.empty().withStyle(ChatFormatting.GRAY), $$3));
        }
        if (($$4 = $$0.getIntArray("FadeColors")).length > 0) {
            $$1.add((Object)FireworkStarItem.appendColors(Component.translatable("item.minecraft.firework_star.fade_to").append(CommonComponents.SPACE).withStyle(ChatFormatting.GRAY), $$4));
        }
        if ($$0.getBoolean("Trail")) {
            $$1.add((Object)Component.translatable("item.minecraft.firework_star.trail").withStyle(ChatFormatting.GRAY));
        }
        if ($$0.getBoolean("Flicker")) {
            $$1.add((Object)Component.translatable("item.minecraft.firework_star.flicker").withStyle(ChatFormatting.GRAY));
        }
    }

    private static Component appendColors(MutableComponent $$0, int[] $$1) {
        for (int $$2 = 0; $$2 < $$1.length; ++$$2) {
            if ($$2 > 0) {
                $$0.append(", ");
            }
            $$0.append(FireworkStarItem.getColorName($$1[$$2]));
        }
        return $$0;
    }

    private static Component getColorName(int $$0) {
        DyeColor $$1 = DyeColor.byFireworkColor($$0);
        if ($$1 == null) {
            return Component.translatable("item.minecraft.firework_star.custom_color");
        }
        return Component.translatable("item.minecraft.firework_star." + $$1.getName());
    }
}