/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.String
 */
package net.minecraft.world.entity;

public enum EquipmentSlot {
    MAINHAND(Type.HAND, 0, 0, "mainhand"),
    OFFHAND(Type.HAND, 1, 5, "offhand"),
    FEET(Type.ARMOR, 0, 1, "feet"),
    LEGS(Type.ARMOR, 1, 2, "legs"),
    CHEST(Type.ARMOR, 2, 3, "chest"),
    HEAD(Type.ARMOR, 3, 4, "head");

    private final Type type;
    private final int index;
    private final int filterFlag;
    private final String name;

    private EquipmentSlot(Type $$0, int $$1, int $$2, String $$3) {
        this.type = $$0;
        this.index = $$1;
        this.filterFlag = $$2;
        this.name = $$3;
    }

    public Type getType() {
        return this.type;
    }

    public int getIndex() {
        return this.index;
    }

    public int getIndex(int $$0) {
        return $$0 + this.index;
    }

    public int getFilterFlag() {
        return this.filterFlag;
    }

    public String getName() {
        return this.name;
    }

    public boolean isArmor() {
        return this.type == Type.ARMOR;
    }

    public static EquipmentSlot byName(String $$0) {
        for (EquipmentSlot $$1 : EquipmentSlot.values()) {
            if (!$$1.getName().equals((Object)$$0)) continue;
            return $$1;
        }
        throw new IllegalArgumentException("Invalid slot '" + $$0 + "'");
    }

    public static EquipmentSlot byTypeAndIndex(Type $$0, int $$1) {
        for (EquipmentSlot $$2 : EquipmentSlot.values()) {
            if ($$2.getType() != $$0 || $$2.getIndex() != $$1) continue;
            return $$2;
        }
        throw new IllegalArgumentException("Invalid slot '" + $$0 + "': " + $$1);
    }

    public static enum Type {
        HAND,
        ARMOR;

    }
}