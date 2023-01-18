/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.suggestion.SuggestionProvider
 *  java.lang.Object
 *  java.util.Collection
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ServerFunctionManager;

public class FunctionCommand {
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_FUNCTION = ($$0, $$1) -> {
        ServerFunctionManager $$2 = ((CommandSourceStack)$$0.getSource()).getServer().getFunctions();
        SharedSuggestionProvider.suggestResource($$2.getTagNames(), $$1, "#");
        return SharedSuggestionProvider.suggestResource($$2.getFunctionNames(), $$1);
    };

    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("function").requires($$0 -> $$0.hasPermission(2))).then(Commands.argument("name", FunctionArgument.functions()).suggests(SUGGEST_FUNCTION).executes($$0 -> FunctionCommand.runFunction((CommandSourceStack)$$0.getSource(), FunctionArgument.getFunctions((CommandContext<CommandSourceStack>)$$0, "name")))));
    }

    private static int runFunction(CommandSourceStack $$0, Collection<CommandFunction> $$1) {
        int $$2 = 0;
        for (CommandFunction $$3 : $$1) {
            $$2 += $$0.getServer().getFunctions().execute($$3, $$0.withSuppressedOutput().withMaximumPermission(2));
        }
        if ($$1.size() == 1) {
            $$0.sendSuccess(Component.translatable("commands.function.success.single", $$2, ((CommandFunction)$$1.iterator().next()).getId()), true);
        } else {
            $$0.sendSuccess(Component.translatable("commands.function.success.multiple", $$2, $$1.size()), true);
        }
        return $$2;
    }
}