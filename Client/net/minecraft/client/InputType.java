/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.client;

public enum InputType {
    NONE,
    MOUSE,
    KEYBOARD_OTHER,
    KEYBOARD_TAB;


    public boolean isMouse() {
        return this == MOUSE;
    }

    public boolean isKeyboard() {
        return this == KEYBOARD_OTHER || this == KEYBOARD_TAB;
    }
}