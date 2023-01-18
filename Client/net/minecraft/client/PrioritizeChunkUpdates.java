/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.function.IntFunction
 */
package net.minecraft.client;

import java.util.function.IntFunction;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.OptionEnum;

public enum PrioritizeChunkUpdates implements OptionEnum
{
    NONE(0, "options.prioritizeChunkUpdates.none"),
    PLAYER_AFFECTED(1, "options.prioritizeChunkUpdates.byPlayer"),
    NEARBY(2, "options.prioritizeChunkUpdates.nearby");

    private static final IntFunction<PrioritizeChunkUpdates> BY_ID;
    private final int id;
    private final String key;

    private PrioritizeChunkUpdates(int $$0, String $$1) {
        this.id = $$0;
        this.key = $$1;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    public static PrioritizeChunkUpdates byId(int $$0) {
        return (PrioritizeChunkUpdates)BY_ID.apply($$0);
    }

    static {
        BY_ID = ByIdMap.continuous(PrioritizeChunkUpdates::getId, PrioritizeChunkUpdates.values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    }
}