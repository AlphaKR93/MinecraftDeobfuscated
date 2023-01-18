/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.EnumSet
 *  java.util.Set
 */
package net.minecraft.world.entity;

import java.util.EnumSet;
import java.util.Set;

public enum RelativeMovement {
    X(0),
    Y(1),
    Z(2),
    Y_ROT(3),
    X_ROT(4);

    public static final Set<RelativeMovement> ALL;
    public static final Set<RelativeMovement> ROTATION;
    private final int bit;

    private RelativeMovement(int $$0) {
        this.bit = $$0;
    }

    private int getMask() {
        return 1 << this.bit;
    }

    private boolean isSet(int $$0) {
        return ($$0 & this.getMask()) == this.getMask();
    }

    public static Set<RelativeMovement> unpack(int $$0) {
        EnumSet $$1 = EnumSet.noneOf(RelativeMovement.class);
        for (RelativeMovement $$2 : RelativeMovement.values()) {
            if (!$$2.isSet($$0)) continue;
            $$1.add((Object)$$2);
        }
        return $$1;
    }

    public static int pack(Set<RelativeMovement> $$0) {
        int $$1 = 0;
        for (RelativeMovement $$2 : $$0) {
            $$1 |= $$2.getMask();
        }
        return $$1;
    }

    static {
        ALL = Set.of((Object[])RelativeMovement.values());
        ROTATION = Set.of((Object)((Object)X_ROT), (Object)((Object)Y_ROT));
    }
}