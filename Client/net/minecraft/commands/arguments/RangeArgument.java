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
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandSourceStack;

public interface RangeArgument<T extends MinMaxBounds<?>>
extends ArgumentType<T> {
    public static Ints intRange() {
        return new Ints();
    }

    public static Floats floatRange() {
        return new Floats();
    }

    public static class Ints
    implements RangeArgument<MinMaxBounds.Ints> {
        private static final Collection<String> EXAMPLES = Arrays.asList((Object[])new String[]{"0..5", "0", "-5", "-100..", "..100"});

        public static MinMaxBounds.Ints getRange(CommandContext<CommandSourceStack> $$0, String $$1) {
            return (MinMaxBounds.Ints)$$0.getArgument($$1, MinMaxBounds.Ints.class);
        }

        public MinMaxBounds.Ints parse(StringReader $$0) throws CommandSyntaxException {
            return MinMaxBounds.Ints.fromReader($$0);
        }

        public Collection<String> getExamples() {
            return EXAMPLES;
        }
    }

    public static class Floats
    implements RangeArgument<MinMaxBounds.Doubles> {
        private static final Collection<String> EXAMPLES = Arrays.asList((Object[])new String[]{"0..5.2", "0", "-5.4", "-100.76..", "..100"});

        public static MinMaxBounds.Doubles getRange(CommandContext<CommandSourceStack> $$0, String $$1) {
            return (MinMaxBounds.Doubles)$$0.getArgument($$1, MinMaxBounds.Doubles.class);
        }

        public MinMaxBounds.Doubles parse(StringReader $$0) throws CommandSyntaxException {
            return MinMaxBounds.Doubles.fromReader($$0);
        }

        public Collection<String> getExamples() {
            return EXAMPLES;
        }
    }
}