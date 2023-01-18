/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandExceptionType
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.Optional
 *  java.util.concurrent.CompletableFuture
 *  java.util.stream.Stream
 */
package net.minecraft.gametest.framework;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class TestFunctionArgument
implements ArgumentType<TestFunction> {
    private static final Collection<String> EXAMPLES = Arrays.asList((Object[])new String[]{"techtests.piston", "techtests"});

    public TestFunction parse(StringReader $$0) throws CommandSyntaxException {
        String $$1 = $$0.readUnquotedString();
        Optional<TestFunction> $$2 = GameTestRegistry.findTestFunction($$1);
        if ($$2.isPresent()) {
            return (TestFunction)$$2.get();
        }
        MutableComponent $$3 = Component.literal("No such test: " + $$1);
        throw new CommandSyntaxException((CommandExceptionType)new SimpleCommandExceptionType((Message)$$3), (Message)$$3);
    }

    public static TestFunctionArgument testFunctionArgument() {
        return new TestFunctionArgument();
    }

    public static TestFunction getTestFunction(CommandContext<CommandSourceStack> $$0, String $$1) {
        return (TestFunction)$$0.getArgument($$1, TestFunction.class);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> $$0, SuggestionsBuilder $$1) {
        Stream $$2 = GameTestRegistry.getAllTestFunctions().stream().map(TestFunction::getTestName);
        return SharedSuggestionProvider.suggest((Stream<String>)$$2, $$1);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}