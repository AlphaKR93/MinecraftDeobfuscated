/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.function.IntFunction
 */
package net.minecraft.world.entity.player;

import java.util.function.IntFunction;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.OptionEnum;

public enum ChatVisiblity implements OptionEnum
{
    FULL(0, "options.chat.visibility.full"),
    SYSTEM(1, "options.chat.visibility.system"),
    HIDDEN(2, "options.chat.visibility.hidden");

    private static final IntFunction<ChatVisiblity> BY_ID;
    private final int id;
    private final String key;

    private ChatVisiblity(int $$0, String $$1) {
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

    public static ChatVisiblity byId(int $$0) {
        return (ChatVisiblity)BY_ID.apply($$0);
    }

    static {
        BY_ID = ByIdMap.continuous(ChatVisiblity::getId, ChatVisiblity.values(), ByIdMap.OutOfBoundsStrategy.WRAP);
    }
}