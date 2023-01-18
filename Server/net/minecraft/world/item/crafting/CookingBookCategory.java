/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.function.Supplier
 */
package net.minecraft.world.item.crafting;

import java.util.function.Supplier;
import net.minecraft.util.StringRepresentable;

public enum CookingBookCategory implements StringRepresentable
{
    FOOD("food"),
    BLOCKS("blocks"),
    MISC("misc");

    public static final StringRepresentable.EnumCodec<CookingBookCategory> CODEC;
    private final String name;

    private CookingBookCategory(String $$0) {
        this.name = $$0;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    static {
        CODEC = StringRepresentable.fromEnum((Supplier<E[]>)((Supplier)CookingBookCategory::values));
    }
}