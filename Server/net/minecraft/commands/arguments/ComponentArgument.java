/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.Collection
 */
package net.minecraft.commands.arguments;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ComponentArgument
implements ArgumentType<Component> {
    private static final Collection<String> EXAMPLES = Arrays.asList((Object[])new String[]{"\"hello world\"", "\"\"", "\"{\"text\":\"hello world\"}", "[\"\"]"});
    public static final DynamicCommandExceptionType ERROR_INVALID_JSON = new DynamicCommandExceptionType($$0 -> Component.translatable("argument.component.invalid", $$0));

    private ComponentArgument() {
    }

    public static Component getComponent(CommandContext<CommandSourceStack> $$0, String $$1) {
        return (Component)$$0.getArgument($$1, Component.class);
    }

    public static ComponentArgument textComponent() {
        return new ComponentArgument();
    }

    public Component parse(StringReader $$0) throws CommandSyntaxException {
        try {
            MutableComponent $$1 = Component.Serializer.fromJson($$0);
            if ($$1 == null) {
                throw ERROR_INVALID_JSON.createWithContext((ImmutableStringReader)$$0, (Object)"empty");
            }
            return $$1;
        }
        catch (Exception $$2) {
            String $$3 = $$2.getCause() != null ? $$2.getCause().getMessage() : $$2.getMessage();
            throw ERROR_INVALID_JSON.createWithContext((ImmutableStringReader)$$0, (Object)$$3);
        }
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}