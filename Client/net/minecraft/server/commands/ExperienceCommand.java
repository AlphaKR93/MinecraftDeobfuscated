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
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.tree.CommandNode
 *  com.mojang.brigadier.tree.LiteralCommandNode
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collection
 *  java.util.function.BiConsumer
 *  java.util.function.BiPredicate
 *  java.util.function.ToIntFunction
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class ExperienceCommand {
    private static final SimpleCommandExceptionType ERROR_SET_POINTS_INVALID = new SimpleCommandExceptionType((Message)Component.translatable("commands.experience.set.points.invalid"));

    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        LiteralCommandNode $$1 = $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("experience").requires($$0 -> $$0.hasPermission(2))).then(Commands.literal("add").then(Commands.argument("targets", EntityArgument.players()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("amount", IntegerArgumentType.integer()).executes($$0 -> ExperienceCommand.addExperience((CommandSourceStack)$$0.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$0, "targets"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"amount"), Type.POINTS))).then(Commands.literal("points").executes($$0 -> ExperienceCommand.addExperience((CommandSourceStack)$$0.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$0, "targets"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"amount"), Type.POINTS)))).then(Commands.literal("levels").executes($$0 -> ExperienceCommand.addExperience((CommandSourceStack)$$0.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$0, "targets"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"amount"), Type.LEVELS))))))).then(Commands.literal("set").then(Commands.argument("targets", EntityArgument.players()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("amount", IntegerArgumentType.integer((int)0)).executes($$0 -> ExperienceCommand.setExperience((CommandSourceStack)$$0.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$0, "targets"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"amount"), Type.POINTS))).then(Commands.literal("points").executes($$0 -> ExperienceCommand.setExperience((CommandSourceStack)$$0.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$0, "targets"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"amount"), Type.POINTS)))).then(Commands.literal("levels").executes($$0 -> ExperienceCommand.setExperience((CommandSourceStack)$$0.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$0, "targets"), IntegerArgumentType.getInteger((CommandContext)$$0, (String)"amount"), Type.LEVELS))))))).then(Commands.literal("query").then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.player()).then(Commands.literal("points").executes($$0 -> ExperienceCommand.queryExperience((CommandSourceStack)$$0.getSource(), EntityArgument.getPlayer((CommandContext<CommandSourceStack>)$$0, "targets"), Type.POINTS)))).then(Commands.literal("levels").executes($$0 -> ExperienceCommand.queryExperience((CommandSourceStack)$$0.getSource(), EntityArgument.getPlayer((CommandContext<CommandSourceStack>)$$0, "targets"), Type.LEVELS))))));
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("xp").requires($$0 -> $$0.hasPermission(2))).redirect((CommandNode)$$1));
    }

    private static int queryExperience(CommandSourceStack $$0, ServerPlayer $$1, Type $$2) {
        int $$3 = $$2.query.applyAsInt((Object)$$1);
        $$0.sendSuccess(Component.translatable("commands.experience.query." + $$2.name, $$1.getDisplayName(), $$3), false);
        return $$3;
    }

    private static int addExperience(CommandSourceStack $$0, Collection<? extends ServerPlayer> $$1, int $$2, Type $$3) {
        for (ServerPlayer $$4 : $$1) {
            $$3.add.accept((Object)$$4, (Object)$$2);
        }
        if ($$1.size() == 1) {
            $$0.sendSuccess(Component.translatable("commands.experience.add." + $$3.name + ".success.single", $$2, ((ServerPlayer)$$1.iterator().next()).getDisplayName()), true);
        } else {
            $$0.sendSuccess(Component.translatable("commands.experience.add." + $$3.name + ".success.multiple", $$2, $$1.size()), true);
        }
        return $$1.size();
    }

    private static int setExperience(CommandSourceStack $$0, Collection<? extends ServerPlayer> $$1, int $$2, Type $$3) throws CommandSyntaxException {
        int $$4 = 0;
        for (ServerPlayer $$5 : $$1) {
            if (!$$3.set.test((Object)$$5, (Object)$$2)) continue;
            ++$$4;
        }
        if ($$4 == 0) {
            throw ERROR_SET_POINTS_INVALID.create();
        }
        if ($$1.size() == 1) {
            $$0.sendSuccess(Component.translatable("commands.experience.set." + $$3.name + ".success.single", $$2, ((ServerPlayer)$$1.iterator().next()).getDisplayName()), true);
        } else {
            $$0.sendSuccess(Component.translatable("commands.experience.set." + $$3.name + ".success.multiple", $$2, $$1.size()), true);
        }
        return $$1.size();
    }

    static enum Type {
        POINTS("points", (BiConsumer<ServerPlayer, Integer>)((BiConsumer)Player::giveExperiencePoints), (BiPredicate<ServerPlayer, Integer>)((BiPredicate)($$0, $$1) -> {
            if ($$1 >= $$0.getXpNeededForNextLevel()) {
                return false;
            }
            $$0.setExperiencePoints((int)$$1);
            return true;
        }), (ToIntFunction<ServerPlayer>)((ToIntFunction)$$0 -> Mth.floor($$0.experienceProgress * (float)$$0.getXpNeededForNextLevel()))),
        LEVELS("levels", (BiConsumer<ServerPlayer, Integer>)((BiConsumer)ServerPlayer::giveExperienceLevels), (BiPredicate<ServerPlayer, Integer>)((BiPredicate)($$0, $$1) -> {
            $$0.setExperienceLevels((int)$$1);
            return true;
        }), (ToIntFunction<ServerPlayer>)((ToIntFunction)$$0 -> $$0.experienceLevel));

        public final BiConsumer<ServerPlayer, Integer> add;
        public final BiPredicate<ServerPlayer, Integer> set;
        public final String name;
        final ToIntFunction<ServerPlayer> query;

        private Type(String $$0, BiConsumer<ServerPlayer, Integer> $$1, BiPredicate<ServerPlayer, Integer> $$2, ToIntFunction<ServerPlayer> $$3) {
            this.add = $$1;
            this.name = $$0;
            this.set = $$2;
            this.query = $$3;
        }
    }
}