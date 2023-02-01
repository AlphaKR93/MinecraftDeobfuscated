/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.client.gui.narration;

import net.minecraft.client.gui.components.TabOrderedElement;
import net.minecraft.client.gui.narration.NarrationSupplier;

public interface NarratableEntry
extends TabOrderedElement,
NarrationSupplier {
    public NarrationPriority narrationPriority();

    default public boolean isActive() {
        return true;
    }

    public static enum NarrationPriority {
        NONE,
        HOVERED,
        FOCUSED;


        public boolean isTerminal() {
            return this == FOCUSED;
        }
    }
}