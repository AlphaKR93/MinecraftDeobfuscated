/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Map
 *  javax.annotation.Nullable
 */
package net.minecraft.commands;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.network.chat.PlayerChatMessage;

public interface CommandSigningContext {
    public static final CommandSigningContext ANONYMOUS = new CommandSigningContext(){

        @Override
        @Nullable
        public PlayerChatMessage getArgument(String $$0) {
            return null;
        }
    };

    @Nullable
    public PlayerChatMessage getArgument(String var1);

    public record SignedArguments(Map<String, PlayerChatMessage> arguments) implements CommandSigningContext
    {
        @Override
        @Nullable
        public PlayerChatMessage getArgument(String $$0) {
            return (PlayerChatMessage)((Object)this.arguments.get((Object)$$0));
        }
    }
}