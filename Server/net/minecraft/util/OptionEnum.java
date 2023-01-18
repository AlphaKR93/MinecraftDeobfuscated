/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.util;

import net.minecraft.network.chat.Component;

public interface OptionEnum {
    public int getId();

    public String getKey();

    default public Component getCaption() {
        return Component.translatable(this.getKey());
    }
}