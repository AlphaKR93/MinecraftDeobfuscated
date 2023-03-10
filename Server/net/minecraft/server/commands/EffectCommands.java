/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.BoolArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collection
 *  javax.annotation.Nullable
 */
package net.minecraft.server.commands;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class EffectCommands {
    private static final SimpleCommandExceptionType ERROR_GIVE_FAILED = new SimpleCommandExceptionType((Message)Component.translatable("commands.effect.give.failed"));
    private static final SimpleCommandExceptionType ERROR_CLEAR_EVERYTHING_FAILED = new SimpleCommandExceptionType((Message)Component.translatable("commands.effect.clear.everything.failed"));
    private static final SimpleCommandExceptionType ERROR_CLEAR_SPECIFIC_FAILED = new SimpleCommandExceptionType((Message)Component.translatable("commands.effect.clear.specific.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> $$02, CommandBuildContext $$1) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("effect").requires($$0 -> $$0.hasPermission(2))).then(((LiteralArgumentBuilder)Commands.literal("clear").executes($$0 -> EffectCommands.clearEffects((CommandSourceStack)$$0.getSource(), (Collection<? extends Entity>)ImmutableList.of((Object)((CommandSourceStack)$$0.getSource()).getEntityOrException())))).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.entities()).executes($$0 -> EffectCommands.clearEffects((CommandSourceStack)$$0.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)$$0, "targets")))).then(Commands.argument("effect", ResourceArgument.resource($$1, Registries.MOB_EFFECT)).executes($$0 -> EffectCommands.clearEffect((CommandSourceStack)$$0.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)$$0, "targets"), ResourceArgument.getMobEffect((CommandContext<CommandSourceStack>)$$0, "effect"))))))).then(Commands.literal("give").then(Commands.argument("targets", EntityArgument.entities()).then(((RequiredArgumentBuilder)Commands.argument("effect", ResourceArgument.resource($$1, Registries.MOB_EFFECT)).executes($$0 -> EffectCommands.giveEffect((CommandSourceStack)$$0.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)$$0, "targets"), ResourceArgument.getMobEffect((CommandContext<CommandSourceStack>)$$0, "effect"), null, 0, true))).then(((RequiredArgumentBuilder)Commands.argument("seconds", IntegerArgumentType.integer((int)1, (int)1000000)).executes($$0 -> EffectCommands.giveEffect((CommandSourceStack)$$0.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)$$0, "targets"), ResourceArgument.getMobEffect((CommandContext<CommandSourceStack>)$$0, "effect"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"seconds"), 0, true))).then(((RequiredArgumentBuilder)Commands.argument("amplifier", IntegerArgumentType.integer((int)0, (int)255)).executes($$0 -> EffectCommands.giveEffect((CommandSourceStack)$$0.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)$$0, "targets"), ResourceArgument.getMobEffect((CommandContext<CommandSourceStack>)$$0, "effect"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"seconds"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"amplifier"), true))).then(Commands.argument("hideParticles", BoolArgumentType.bool()).executes($$0 -> EffectCommands.giveEffect((CommandSourceStack)$$0.getSource(), EntityArgument.getEntities((CommandContext<CommandSourceStack>)$$0, "targets"), ResourceArgument.getMobEffect((CommandContext<CommandSourceStack>)$$0, "effect"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"seconds"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"amplifier"), !BoolArgumentType.getBool((CommandContext)$$0, (String)"hideParticles"))))))))));
    }

    private static int giveEffect(CommandSourceStack $$0, Collection<? extends Entity> $$1, Holder<MobEffect> $$2, @Nullable Integer $$3, int $$4, boolean $$5) throws CommandSyntaxException {
        int $$11;
        MobEffect $$6 = $$2.value();
        int $$7 = 0;
        if ($$3 != null) {
            if ($$6.isInstantenous()) {
                int $$8 = $$3;
            } else {
                int $$9 = $$3 * 20;
            }
        } else if ($$6.isInstantenous()) {
            boolean $$10 = true;
        } else {
            $$11 = 600;
        }
        for (Entity $$12 : $$1) {
            MobEffectInstance $$13;
            if (!($$12 instanceof LivingEntity) || !((LivingEntity)$$12).addEffect($$13 = new MobEffectInstance($$6, $$11, $$4, false, $$5), $$0.getEntity())) continue;
            ++$$7;
        }
        if ($$7 == 0) {
            throw ERROR_GIVE_FAILED.create();
        }
        if ($$1.size() == 1) {
            $$0.sendSuccess(Component.translatable("commands.effect.give.success.single", $$6.getDisplayName(), ((Entity)$$1.iterator().next()).getDisplayName(), $$11 / 20), true);
        } else {
            $$0.sendSuccess(Component.translatable("commands.effect.give.success.multiple", $$6.getDisplayName(), $$1.size(), $$11 / 20), true);
        }
        return $$7;
    }

    private static int clearEffects(CommandSourceStack $$0, Collection<? extends Entity> $$1) throws CommandSyntaxException {
        int $$2 = 0;
        for (Entity $$3 : $$1) {
            if (!($$3 instanceof LivingEntity) || !((LivingEntity)$$3).removeAllEffects()) continue;
            ++$$2;
        }
        if ($$2 == 0) {
            throw ERROR_CLEAR_EVERYTHING_FAILED.create();
        }
        if ($$1.size() == 1) {
            $$0.sendSuccess(Component.translatable("commands.effect.clear.everything.success.single", ((Entity)$$1.iterator().next()).getDisplayName()), true);
        } else {
            $$0.sendSuccess(Component.translatable("commands.effect.clear.everything.success.multiple", $$1.size()), true);
        }
        return $$2;
    }

    private static int clearEffect(CommandSourceStack $$0, Collection<? extends Entity> $$1, Holder<MobEffect> $$2) throws CommandSyntaxException {
        MobEffect $$3 = $$2.value();
        int $$4 = 0;
        for (Entity $$5 : $$1) {
            if (!($$5 instanceof LivingEntity) || !((LivingEntity)$$5).removeEffect($$3)) continue;
            ++$$4;
        }
        if ($$4 == 0) {
            throw ERROR_CLEAR_SPECIFIC_FAILED.create();
        }
        if ($$1.size() == 1) {
            $$0.sendSuccess(Component.translatable("commands.effect.clear.specific.success.single", $$3.getDisplayName(), ((Entity)$$1.iterator().next()).getDisplayName()), true);
        } else {
            $$0.sendSuccess(Component.translatable("commands.effect.clear.specific.success.multiple", $$3.getDisplayName(), $$1.size()), true);
        }
        return $$4;
    }
}