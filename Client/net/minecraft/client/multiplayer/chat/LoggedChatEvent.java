/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.function.Supplier
 */
package net.minecraft.client.multiplayer.chat;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.client.multiplayer.chat.LoggedChatMessage;
import net.minecraft.util.StringRepresentable;

public interface LoggedChatEvent {
    public static final Codec<LoggedChatEvent> CODEC = StringRepresentable.fromEnum((Supplier<E[]>)((Supplier)Type::values)).dispatch(LoggedChatEvent::type, Type::codec);

    public Type type();

    public static enum Type implements StringRepresentable
    {
        PLAYER("player", (Supplier<Codec<? extends LoggedChatEvent>>)((Supplier)() -> LoggedChatMessage.Player.CODEC)),
        SYSTEM("system", (Supplier<Codec<? extends LoggedChatEvent>>)((Supplier)() -> LoggedChatMessage.System.CODEC));

        private final String serializedName;
        private final Supplier<Codec<? extends LoggedChatEvent>> codec;

        private Type(String $$0, Supplier<Codec<? extends LoggedChatEvent>> $$1) {
            this.serializedName = $$0;
            this.codec = $$1;
        }

        private Codec<? extends LoggedChatEvent> codec() {
            return (Codec)this.codec.get();
        }

        @Override
        public String getSerializedName() {
            return this.serializedName;
        }
    }
}