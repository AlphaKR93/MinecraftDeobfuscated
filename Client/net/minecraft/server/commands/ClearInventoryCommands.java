/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.function.Predicate
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ClearInventoryCommands {
    private static final DynamicCommandExceptionType ERROR_SINGLE = new DynamicCommandExceptionType($$0 -> Component.translatable("clear.failed.single", $$0));
    private static final DynamicCommandExceptionType ERROR_MULTIPLE = new DynamicCommandExceptionType($$0 -> Component.translatable("clear.failed.multiple", $$0));

    public static void register(CommandDispatcher<CommandSourceStack> $$03, CommandBuildContext $$1) {
        $$03.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("clear").requires($$0 -> $$0.hasPermission(2))).executes($$02 -> ClearInventoryCommands.clearInventory((CommandSourceStack)$$02.getSource(), (Collection<ServerPlayer>)Collections.singleton((Object)((CommandSourceStack)$$02.getSource()).getPlayerOrException()), (Predicate<ItemStack>)((Predicate)$$0 -> true), -1))).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes($$02 -> ClearInventoryCommands.clearInventory((CommandSourceStack)$$02.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$02, "targets"), (Predicate<ItemStack>)((Predicate)$$0 -> true), -1))).then(((RequiredArgumentBuilder)Commands.argument("item", ItemPredicateArgument.itemPredicate($$1)).executes($$0 -> ClearInventoryCommands.clearInventory((CommandSourceStack)$$0.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$0, "targets"), ItemPredicateArgument.getItemPredicate((CommandContext<CommandSourceStack>)$$0, "item"), -1))).then(Commands.argument("maxCount", IntegerArgumentType.integer((int)0)).executes($$0 -> ClearInventoryCommands.clearInventory((CommandSourceStack)$$0.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$0, "targets"), ItemPredicateArgument.getItemPredicate((CommandContext<CommandSourceStack>)$$0, "item"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"maxCount")))))));
    }

    private static int clearInventory(CommandSourceStack $$0, Collection<ServerPlayer> $$1, Predicate<ItemStack> $$2, int $$3) throws CommandSyntaxException {
        int $$4 = 0;
        for (ServerPlayer $$5 : $$1) {
            $$4 += $$5.getInventory().clearOrCountMatchingItems($$2, $$3, $$5.inventoryMenu.getCraftSlots());
            $$5.containerMenu.broadcastChanges();
            $$5.inventoryMenu.slotsChanged($$5.getInventory());
        }
        if ($$4 == 0) {
            if ($$1.size() == 1) {
                throw ERROR_SINGLE.create((Object)((ServerPlayer)$$1.iterator().next()).getName());
            }
            throw ERROR_MULTIPLE.create((Object)$$1.size());
        }
        if ($$3 == 0) {
            if ($$1.size() == 1) {
                $$0.sendSuccess(Component.translatable("commands.clear.test.single", $$4, ((ServerPlayer)$$1.iterator().next()).getDisplayName()), true);
            } else {
                $$0.sendSuccess(Component.translatable("commands.clear.test.multiple", $$4, $$1.size()), true);
            }
        } else if ($$1.size() == 1) {
            $$0.sendSuccess(Component.translatable("commands.clear.success.single", $$4, ((ServerPlayer)$$1.iterator().next()).getDisplayName()), true);
        } else {
            $$0.sendSuccess(Component.translatable("commands.clear.success.multiple", $$4, $$1.size()), true);
        }
        return $$4;
    }
}