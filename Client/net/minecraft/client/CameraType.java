/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.client;

public enum CameraType {
    FIRST_PERSON(true, false),
    THIRD_PERSON_BACK(false, false),
    THIRD_PERSON_FRONT(false, true);

    private static final CameraType[] VALUES;
    private final boolean firstPerson;
    private final boolean mirrored;

    private CameraType(boolean $$0, boolean $$1) {
        this.firstPerson = $$0;
        this.mirrored = $$1;
    }

    public boolean isFirstPerson() {
        return this.firstPerson;
    }

    public boolean isMirrored() {
        return this.mirrored;
    }

    public CameraType cycle() {
        return VALUES[(this.ordinal() + 1) % VALUES.length];
    }

    static {
        VALUES = CameraType.values();
    }
}