/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  java.lang.Object
 *  java.util.Collection
 */
package net.minecraft.server.commands;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

public class KillCommand {
    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("kill").requires($$0 -> $$0.hasPermission(2))).executes($$0 -> KillCommand.kill((CommandSourceStack)$$0.getSource(), (Collection<? extends Entity>)ImmutableList.of((Object)((CommandSourceStack)$$0.getSource()).getEntityOrException())))).then(Commands.argument("targets", EntityArgument.entities()).executes($$0 -> KillCommand.kill((CommandSourceStack)$$0.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)$$0, "targets")))));
    }

    private static int kill(CommandSourceStack $$0, Collection<? extends Entity> $$1) {
        for (Entity $$2 : $$1) {
            $$2.kill();
        }
        if ($$1.size() == 1) {
            $$0.sendSuccess(Component.translatable("commands.kill.success.single", ((Entity)$$1.iterator().next()).getDisplayName()), true);
        } else {
            $$0.sendSuccess(Component.translatable("commands.kill.success.multiple", $$1.size()), true);
        }
        return $$1.size();
    }
}