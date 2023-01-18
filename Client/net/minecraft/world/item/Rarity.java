/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.item;

import net.minecraft.ChatFormatting;

public enum Rarity {
    COMMON(ChatFormatting.WHITE),
    UNCOMMON(ChatFormatting.YELLOW),
    RARE(ChatFormatting.AQUA),
    EPIC(ChatFormatting.LIGHT_PURPLE);

    public final ChatFormatting color;

    private Rarity(ChatFormatting $$0) {
        this.color = $$0;
    }
}