/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  java.lang.CharSequence
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.UUID
 *  java.util.regex.Matcher
 *  java.util.regex.Pattern
 */
package net.minecraft.commands.arguments;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

public class UuidArgument
implements ArgumentType<UUID> {
    public static final SimpleCommandExceptionType ERROR_INVALID_UUID = new SimpleCommandExceptionType((Message)Component.translatable("argument.uuid.invalid"));
    private static final Collection<String> EXAMPLES = Arrays.asList((Object[])new String[]{"dd12be42-52a9-4a91-a8a1-11c01849e498"});
    private static final Pattern ALLOWED_CHARACTERS = Pattern.compile((String)"^([-A-Fa-f0-9]+)");

    public static UUID getUuid(CommandContext<CommandSourceStack> $$0, String $$1) {
        return (UUID)$$0.getArgument($$1, UUID.class);
    }

    public static UuidArgument uuid() {
        return new UuidArgument();
    }

    public UUID parse(StringReader $$0) throws CommandSyntaxException {
        String $$1 = $$0.getRemaining();
        Matcher $$2 = ALLOWED_CHARACTERS.matcher((CharSequence)$$1);
        if ($$2.find()) {
            String $$3 = $$2.group(1);
            try {
                UUID $$4 = UUID.fromString((String)$$3);
                $$0.setCursor($$0.getCursor() + $$3.length());
                return $$4;
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        throw ERROR_INVALID_UUID.create();
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}