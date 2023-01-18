/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.level.entity;

import net.minecraft.server.level.ChunkHolder;

public enum Visibility {
    HIDDEN(false, false),
    TRACKED(true, false),
    TICKING(true, true);

    private final boolean accessible;
    private final boolean ticking;

    private Visibility(boolean $$0, boolean $$1) {
        this.accessible = $$0;
        this.ticking = $$1;
    }

    public boolean isTicking() {
        return this.ticking;
    }

    public boolean isAccessible() {
        return this.accessible;
    }

    public static Visibility fromFullChunkStatus(ChunkHolder.FullChunkStatus $$0) {
        if ($$0.isOrAfter(ChunkHolder.FullChunkStatus.ENTITY_TICKING)) {
            return TICKING;
        }
        if ($$0.isOrAfter(ChunkHolder.FullChunkStatus.BORDER)) {
            return TRACKED;
        }
        return HIDDEN;
    }
}