/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Throwable
 */
package net.minecraft.network.chat;

import net.minecraft.network.chat.Component;

public class ThrowingComponent
extends Exception {
    private final Component component;

    public ThrowingComponent(Component $$0) {
        super($$0.getString());
        this.component = $$0;
    }

    public ThrowingComponent(Component $$0, Throwable $$1) {
        super($$0.getString(), $$1);
        this.component = $$0;
    }

    public Component getComponent() {
        return this.component;
    }
}