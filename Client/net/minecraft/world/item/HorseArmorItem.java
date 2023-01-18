/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.world.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class HorseArmorItem
extends Item {
    private static final String TEX_FOLDER = "textures/entity/horse/";
    private final int protection;
    private final String texture;

    public HorseArmorItem(int $$0, String $$1, Item.Properties $$2) {
        super($$2);
        this.protection = $$0;
        this.texture = "textures/entity/horse/armor/horse_armor_" + $$1 + ".png";
    }

    public ResourceLocation getTexture() {
        return new ResourceLocation(this.texture);
    }

    public int getProtection() {
        return this.protection;
    }
}