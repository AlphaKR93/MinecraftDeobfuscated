/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.function.IntFunction
 */
package net.minecraft.client;

import java.util.function.IntFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ByIdMap;

public enum NarratorStatus {
    OFF(0, "options.narrator.off"),
    ALL(1, "options.narrator.all"),
    CHAT(2, "options.narrator.chat"),
    SYSTEM(3, "options.narrator.system");

    private static final IntFunction<NarratorStatus> BY_ID;
    private final int id;
    private final Component name;

    private NarratorStatus(int $$0, String $$1) {
        this.id = $$0;
        this.name = Component.translatable($$1);
    }

    public int getId() {
        return this.id;
    }

    public Component getName() {
        return this.name;
    }

    public static NarratorStatus byId(int $$0) {
        return (NarratorStatus)((Object)BY_ID.apply($$0));
    }

    public boolean shouldNarrateChat() {
        return this == ALL || this == CHAT;
    }

    public boolean shouldNarrateSystem() {
        return this == ALL || this == SYSTEM;
    }

    static {
        BY_ID = ByIdMap.continuous(NarratorStatus::getId, NarratorStatus.values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    }
}