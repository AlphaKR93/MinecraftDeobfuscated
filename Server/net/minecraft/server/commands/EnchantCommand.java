/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collection
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class EnchantCommand {
    private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType($$0 -> Component.translatable("commands.enchant.failed.entity", $$0));
    private static final DynamicCommandExceptionType ERROR_NO_ITEM = new DynamicCommandExceptionType($$0 -> Component.translatable("commands.enchant.failed.itemless", $$0));
    private static final DynamicCommandExceptionType ERROR_INCOMPATIBLE = new DynamicCommandExceptionType($$0 -> Component.translatable("commands.enchant.failed.incompatible", $$0));
    private static final Dynamic2CommandExceptionType ERROR_LEVEL_TOO_HIGH = new Dynamic2CommandExceptionType(($$0, $$1) -> Component.translatable("commands.enchant.failed.level", $$0, $$1));
    private static final SimpleCommandExceptionType ERROR_NOTHING_HAPPENED = new SimpleCommandExceptionType((Message)Component.translatable("commands.enchant.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> $$02, CommandBuildContext $$1) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("enchant").requires($$0 -> $$0.hasPermission(2))).then(Commands.argument("targets", EntityArgument.entities()).then(((RequiredArgumentBuilder)Commands.argument("enchantment", ResourceArgument.resource($$1, Registries.ENCHANTMENT)).executes($$0 -> EnchantCommand.enchant((CommandSourceStack)$$0.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)$$0, "targets"), ResourceArgument.getEnchantment((CommandContext<CommandSourceStack>)$$0, "enchantment"), 1))).then(Commands.argument("level", IntegerArgumentType.integer((int)0)).executes($$0 -> EnchantCommand.enchant((CommandSourceStack)$$0.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)$$0, "targets"), ResourceArgument.getEnchantment((CommandContext<CommandSourceStack>)$$0, "enchantment"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"level")))))));
    }

    private static int enchant(CommandSourceStack $$0, Collection<? extends Entity> $$1, Holder<Enchantment> $$2, int $$3) throws CommandSyntaxException {
        Enchantment $$4 = $$2.value();
        if ($$3 > $$4.getMaxLevel()) {
            throw ERROR_LEVEL_TOO_HIGH.create((Object)$$3, (Object)$$4.getMaxLevel());
        }
        int $$5 = 0;
        for (Entity $$6 : $$1) {
            if ($$6 instanceof LivingEntity) {
                LivingEntity $$7 = (LivingEntity)$$6;
                ItemStack $$8 = $$7.getMainHandItem();
                if (!$$8.isEmpty()) {
                    if ($$4.canEnchant($$8) && EnchantmentHelper.isEnchantmentCompatible((Collection<Enchantment>)EnchantmentHelper.getEnchantments($$8).keySet(), $$4)) {
                        $$8.enchant($$4, $$3);
                        ++$$5;
                        continue;
                    }
                    if ($$1.size() != 1) continue;
                    throw ERROR_INCOMPATIBLE.create((Object)$$8.getItem().getName($$8).getString());
                }
                if ($$1.size() != 1) continue;
                throw ERROR_NO_ITEM.create((Object)$$7.getName().getString());
            }
            if ($$1.size() != 1) continue;
            throw ERROR_NOT_LIVING_ENTITY.create((Object)$$6.getName().getString());
        }
        if ($$5 == 0) {
            throw ERROR_NOTHING_HAPPENED.create();
        }
        if ($$1.size() == 1) {
            $$0.sendSuccess(Component.translatable("commands.enchant.success.single", $$4.getFullname($$3), ((Entity)$$1.iterator().next()).getDisplayName()), true);
        } else {
            $$0.sendSuccess(Component.translatable("commands.enchant.success.multiple", $$4.getFullname($$3), $$1.size()), true);
        }
        return $$5;
    }
}