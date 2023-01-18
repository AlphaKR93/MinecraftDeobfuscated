/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.RuntimeException
 */
package net.minecraft.world.level.storage;

import net.minecraft.network.chat.Component;

public class LevelStorageException
extends RuntimeException {
    private final Component messageComponent;

    public LevelStorageException(Component $$0) {
        super($$0.getString());
        this.messageComponent = $$0;
    }

    public Component getMessageComponent() {
        return this.messageComponent;
    }
}