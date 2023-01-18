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
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collection
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

public class GiveCommand {
    public static final int MAX_ALLOWED_ITEMSTACKS = 100;

    public static void register(CommandDispatcher<CommandSourceStack> $$02, CommandBuildContext $$1) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("give").requires($$0 -> $$0.hasPermission(2))).then(Commands.argument("targets", EntityArgument.players()).then(((RequiredArgumentBuilder)Commands.argument("item", ItemArgument.item($$1)).executes($$0 -> GiveCommand.giveItem((CommandSourceStack)$$0.getSource(), ItemArgument.getItem($$0, "item"), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$0, "targets"), 1))).then(Commands.argument("count", IntegerArgumentType.integer((int)1)).executes($$0 -> GiveCommand.giveItem((CommandSourceStack)$$0.getSource(), ItemArgument.getItem($$0, "item"), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$0, "targets"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"count")))))));
    }

    private static int giveItem(CommandSourceStack $$0, ItemInput $$1, Collection<ServerPlayer> $$2, int $$3) throws CommandSyntaxException {
        int $$4 = $$1.getItem().getMaxStackSize();
        int $$5 = $$4 * 100;
        if ($$3 > $$5) {
            $$0.sendFailure(Component.translatable("commands.give.failed.toomanyitems", $$5, $$1.createItemStack($$3, false).getDisplayName()));
            return 0;
        }
        for (ServerPlayer $$6 : $$2) {
            int $$7 = $$3;
            while ($$7 > 0) {
                int $$8 = Math.min((int)$$4, (int)$$7);
                $$7 -= $$8;
                ItemStack $$9 = $$1.createItemStack($$8, false);
                boolean $$10 = $$6.getInventory().add($$9);
                if (!$$10 || !$$9.isEmpty()) {
                    ItemEntity $$11 = $$6.drop($$9, false);
                    if ($$11 == null) continue;
                    $$11.setNoPickUpDelay();
                    $$11.setOwner($$6.getUUID());
                    continue;
                }
                $$9.setCount(1);
                ItemEntity $$12 = $$6.drop($$9, false);
                if ($$12 != null) {
                    $$12.makeFakeItem();
                }
                $$6.level.playSound(null, $$6.getX(), $$6.getY(), $$6.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2f, (($$6.getRandom().nextFloat() - $$6.getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
                $$6.containerMenu.broadcastChanges();
            }
        }
        if ($$2.size() == 1) {
            $$0.sendSuccess(Component.translatable("commands.give.success.single", $$3, $$1.createItemStack($$3, false).getDisplayName(), ((ServerPlayer)$$2.iterator().next()).getDisplayName()), true);
        } else {
            $$0.sendSuccess(Component.translatable("commands.give.success.single", $$3, $$1.createItemStack($$3, false).getDisplayName(), $$2.size()), true);
        }
        return $$2.size();
    }
}