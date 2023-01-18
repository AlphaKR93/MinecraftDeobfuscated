/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.Collections
 */
package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class FunctionArgument
implements ArgumentType<Result> {
    private static final Collection<String> EXAMPLES = Arrays.asList((Object[])new String[]{"foo", "foo:bar", "#foo"});
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_TAG = new DynamicCommandExceptionType($$0 -> Component.translatable("arguments.function.tag.unknown", $$0));
    private static final DynamicCommandExceptionType ERROR_UNKNOWN_FUNCTION = new DynamicCommandExceptionType($$0 -> Component.translatable("arguments.function.unknown", $$0));

    public static FunctionArgument functions() {
        return new FunctionArgument();
    }

    public Result parse(StringReader $$0) throws CommandSyntaxException {
        if ($$0.canRead() && $$0.peek() == '#') {
            $$0.skip();
            final ResourceLocation $$1 = ResourceLocation.read($$0);
            return new Result(){

                @Override
                public Collection<CommandFunction> create(CommandContext<CommandSourceStack> $$0) throws CommandSyntaxException {
                    return FunctionArgument.getFunctionTag($$0, $$1);
                }

                @Override
                public Pair<ResourceLocation, Either<CommandFunction, Collection<CommandFunction>>> unwrap(CommandContext<CommandSourceStack> $$0) throws CommandSyntaxException {
                    return Pair.of((Object)$$1, (Object)Either.right(FunctionArgument.getFunctionTag($$0, $$1)));
                }
            };
        }
        final ResourceLocation $$2 = ResourceLocation.read($$0);
        return new Result(){

            @Override
            public Collection<CommandFunction> create(CommandContext<CommandSourceStack> $$0) throws CommandSyntaxException {
                return Collections.singleton((Object)FunctionArgument.getFunction($$0, $$2));
            }

            @Override
            public Pair<ResourceLocation, Either<CommandFunction, Collection<CommandFunction>>> unwrap(CommandContext<CommandSourceStack> $$0) throws CommandSyntaxException {
                return Pair.of((Object)$$2, (Object)Either.left((Object)FunctionArgument.getFunction($$0, $$2)));
            }
        };
    }

    static CommandFunction getFunction(CommandContext<CommandSourceStack> $$0, ResourceLocation $$1) throws CommandSyntaxException {
        return (CommandFunction)((CommandSourceStack)$$0.getSource()).getServer().getFunctions().get($$1).orElseThrow(() -> ERROR_UNKNOWN_FUNCTION.create((Object)$$1.toString()));
    }

    static Collection<CommandFunction> getFunctionTag(CommandContext<CommandSourceStack> $$0, ResourceLocation $$1) throws CommandSyntaxException {
        Collection<CommandFunction> $$2 = ((CommandSourceStack)$$0.getSource()).getServer().getFunctions().getTag($$1);
        if ($$2 == null) {
            throw ERROR_UNKNOWN_TAG.create((Object)$$1.toString());
        }
        return $$2;
    }

    public static Collection<CommandFunction> getFunctions(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
        return ((Result)$$0.getArgument($$1, Result.class)).create($$0);
    }

    public static Pair<ResourceLocation, Either<CommandFunction, Collection<CommandFunction>>> getFunctionOrTag(CommandContext<CommandSourceStack> $$0, String $$1) throws CommandSyntaxException {
        return ((Result)$$0.getArgument($$1, Result.class)).unwrap($$0);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static interface Result {
        public Collection<CommandFunction> create(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException;

        public Pair<ResourceLocation, Either<CommandFunction, Collection<CommandFunction>>> unwrap(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException;
    }
}