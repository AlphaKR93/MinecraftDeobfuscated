/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.function.IntFunction
 *  java.util.function.Supplier
 */
package net.minecraft.world.entity.animal.horse;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public enum Variant implements StringRepresentable
{
    WHITE(0, "white"),
    CREAMY(1, "creamy"),
    CHESTNUT(2, "chestnut"),
    BROWN(3, "brown"),
    BLACK(4, "black"),
    GRAY(5, "gray"),
    DARK_BROWN(6, "dark_brown");

    public static final Codec<Variant> CODEC;
    private static final IntFunction<Variant> BY_ID;
    private final int id;
    private final String name;

    private Variant(int $$0, String $$1) {
        this.id = $$0;
        this.name = $$1;
    }

    public int getId() {
        return this.id;
    }

    public static Variant byId(int $$0) {
        return (Variant)BY_ID.apply($$0);
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    static {
        CODEC = StringRepresentable.fromEnum((Supplier<E[]>)((Supplier)Variant::values));
        BY_ID = ByIdMap.continuous(Variant::getId, Variant.values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    }
}