/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.world.entity;

import net.minecraft.util.OptionEnum;

public enum HumanoidArm implements OptionEnum
{
    LEFT(0, "options.mainHand.left"),
    RIGHT(1, "options.mainHand.right");

    private final int id;
    private final String name;

    private HumanoidArm(int $$0, String $$1) {
        this.id = $$0;
        this.name = $$1;
    }

    public HumanoidArm getOpposite() {
        if (this == LEFT) {
            return RIGHT;
        }
        return LEFT;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getKey() {
        return this.name;
    }
}