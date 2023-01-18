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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;

public class CompoundTagArgument
implements ArgumentType<CompoundTag> {
    private static final Collection<String> EXAMPLES = Arrays.asList((Object[])new String[]{"{}", "{foo=bar}"});

    private CompoundTagArgument() {
    }

    public static CompoundTagArgument compoundTag() {
        return new CompoundTagArgument();
    }

    public static <S> CompoundTag getCompoundTag(CommandContext<S> $$0, String $$1) {
        return (CompoundTag)$$0.getArgument($$1, CompoundTag.class);
    }

    public CompoundTag parse(StringReader $$0) throws CommandSyntaxException {
        return new TagParser($$0).readStruct();
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}