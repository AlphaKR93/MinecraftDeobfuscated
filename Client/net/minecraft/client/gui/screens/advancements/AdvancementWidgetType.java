/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.client.gui.screens.advancements;

public enum AdvancementWidgetType {
    OBTAINED(0),
    UNOBTAINED(1);

    private final int y;

    private AdvancementWidgetType(int $$0) {
        this.y = $$0;
    }

    public int getIndex() {
        return this.y;
    }
}