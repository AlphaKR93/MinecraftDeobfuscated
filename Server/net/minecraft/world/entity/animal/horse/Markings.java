/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.function.IntFunction
 */
package net.minecraft.world.entity.animal.horse;

import java.util.function.IntFunction;
import net.minecraft.util.ByIdMap;

public enum Markings {
    NONE(0),
    WHITE(1),
    WHITE_FIELD(2),
    WHITE_DOTS(3),
    BLACK_DOTS(4);

    private static final IntFunction<Markings> BY_ID;
    private final int id;

    private Markings(int $$0) {
        this.id = $$0;
    }

    public int getId() {
        return this.id;
    }

    public static Markings byId(int $$0) {
        return (Markings)((Object)BY_ID.apply($$0));
    }

    static {
        BY_ID = ByIdMap.continuous(Markings::getId, Markings.values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    }
}