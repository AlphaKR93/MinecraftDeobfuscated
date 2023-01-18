/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameRules;

public class GameRuleCommand {
    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        final LiteralArgumentBuilder $$1 = (LiteralArgumentBuilder)Commands.literal("gamerule").requires($$0 -> $$0.hasPermission(2));
        GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor(){

            @Override
            public <T extends GameRules.Value<T>> void visit(GameRules.Key<T> $$0, GameRules.Type<T> $$12) {
                $$1.then(((LiteralArgumentBuilder)Commands.literal($$0.getId()).executes($$1 -> GameRuleCommand.queryRule((CommandSourceStack)$$1.getSource(), $$0))).then($$12.createArgument("value").executes($$1 -> GameRuleCommand.setRule((CommandContext<CommandSourceStack>)$$1, $$0))));
            }
        });
        $$02.register($$1);
    }

    static <T extends GameRules.Value<T>> int setRule(CommandContext<CommandSourceStack> $$0, GameRules.Key<T> $$1) {
        CommandSourceStack $$2 = (CommandSourceStack)$$0.getSource();
        T $$3 = $$2.getServer().getGameRules().getRule($$1);
        ((GameRules.Value)$$3).setFromArgument($$0, "value");
        $$2.sendSuccess(Component.translatable("commands.gamerule.set", $$1.getId(), ((GameRules.Value)$$3).toString()), true);
        return ((GameRules.Value)$$3).getCommandResult();
    }

    static <T extends GameRules.Value<T>> int queryRule(CommandSourceStack $$0, GameRules.Key<T> $$1) {
        T $$2 = $$0.getServer().getGameRules().getRule($$1);
        $$0.sendSuccess(Component.translatable("commands.gamerule.query", $$1.getId(), ((GameRules.Value)$$2).toString()), false);
        return ((GameRules.Value)$$2).getCommandResult();
    }
}