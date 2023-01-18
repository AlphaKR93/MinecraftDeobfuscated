/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.Collection
 */
package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;

public class NbtTagArgument
implements ArgumentType<Tag> {
    private static final Collection<String> EXAMPLES = Arrays.asList((Object[])new String[]{"0", "0b", "0l", "0.0", "\"foo\"", "{foo=bar}", "[0]"});

    private NbtTagArgument() {
    }

    public static NbtTagArgument nbtTag() {
        return new NbtTagArgument();
    }

    public static <S> Tag getNbtTag(CommandContext<S> $$0, String $$1) {
        return (Tag)$$0.getArgument($$1, Tag.class);
    }

    public Tag parse(StringReader $$0) throws CommandSyntaxException {
        return new TagParser($$0).readValue();
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}