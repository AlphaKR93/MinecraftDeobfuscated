/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.FloatArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  java.lang.Float
 *  java.lang.Math
 *  java.lang.Object
 *  java.util.Collection
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class PlaySoundCommand {
    private static final SimpleCommandExceptionType ERROR_TOO_FAR = new SimpleCommandExceptionType((Message)Component.translatable("commands.playsound.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        RequiredArgumentBuilder $$1 = Commands.argument("sound", ResourceLocationArgument.id()).suggests(SuggestionProviders.AVAILABLE_SOUNDS);
        for (SoundSource $$2 : SoundSource.values()) {
            $$1.then(PlaySoundCommand.source($$2));
        }
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("playsound").requires($$0 -> $$0.hasPermission(2))).then((ArgumentBuilder)$$1));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> source(SoundSource $$0) {
        return (LiteralArgumentBuilder)Commands.literal($$0.getName()).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes($$1 -> PlaySoundCommand.playSound((CommandSourceStack)$$1.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$1, "targets"), ResourceLocationArgument.getId((CommandContext<CommandSourceStack>)$$1, "sound"), $$0, ((CommandSourceStack)$$1.getSource()).getPosition(), 1.0f, 1.0f, 0.0f))).then(((RequiredArgumentBuilder)Commands.argument("pos", Vec3Argument.vec3()).executes($$1 -> PlaySoundCommand.playSound((CommandSourceStack)$$1.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$1, "targets"), ResourceLocationArgument.getId((CommandContext<CommandSourceStack>)$$1, "sound"), $$0, Vec3Argument.getVec3((CommandContext<CommandSourceStack>)$$1, "pos"), 1.0f, 1.0f, 0.0f))).then(((RequiredArgumentBuilder)Commands.argument("volume", FloatArgumentType.floatArg((float)0.0f)).executes($$1 -> PlaySoundCommand.playSound((CommandSourceStack)$$1.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$1, "targets"), ResourceLocationArgument.getId((CommandContext<CommandSourceStack>)$$1, "sound"), $$0, Vec3Argument.getVec3((CommandContext<CommandSourceStack>)$$1, "pos"), ((Float)$$1.getArgument("volume", Float.class)).floatValue(), 1.0f, 0.0f))).then(((RequiredArgumentBuilder)Commands.argument("pitch", FloatArgumentType.floatArg((float)0.0f, (float)2.0f)).executes($$1 -> PlaySoundCommand.playSound((CommandSourceStack)$$1.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$1, "targets"), ResourceLocationArgument.getId((CommandContext<CommandSourceStack>)$$1, "sound"), $$0, Vec3Argument.getVec3((CommandContext<CommandSourceStack>)$$1, "pos"), ((Float)$$1.getArgument("volume", Float.class)).floatValue(), ((Float)$$1.getArgument("pitch", Float.class)).floatValue(), 0.0f))).then(Commands.argument("minVolume", FloatArgumentType.floatArg((float)0.0f, (float)1.0f)).executes($$1 -> PlaySoundCommand.playSound((CommandSourceStack)$$1.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$1, "targets"), ResourceLocationArgument.getId((CommandContext<CommandSourceStack>)$$1, "sound"), $$0, Vec3Argument.getVec3((CommandContext<CommandSourceStack>)$$1, "pos"), ((Float)$$1.getArgument("volume", Float.class)).floatValue(), ((Float)$$1.getArgument("pitch", Float.class)).floatValue(), ((Float)$$1.getArgument("minVolume", Float.class)).floatValue())))))));
    }

    private static int playSound(CommandSourceStack $$0, Collection<ServerPlayer> $$1, ResourceLocation $$2, SoundSource $$3, Vec3 $$4, float $$5, float $$6, float $$7) throws CommandSyntaxException {
        Holder<SoundEvent> $$8 = Holder.direct(SoundEvent.createVariableRangeEvent($$2));
        double $$9 = Mth.square($$8.value().getRange($$5));
        int $$10 = 0;
        long $$11 = $$0.getLevel().getRandom().nextLong();
        for (ServerPlayer $$12 : $$1) {
            double $$13 = $$4.x - $$12.getX();
            double $$14 = $$4.y - $$12.getY();
            double $$15 = $$4.z - $$12.getZ();
            double $$16 = $$13 * $$13 + $$14 * $$14 + $$15 * $$15;
            Vec3 $$17 = $$4;
            float $$18 = $$5;
            if ($$16 > $$9) {
                if ($$7 <= 0.0f) continue;
                double $$19 = Math.sqrt((double)$$16);
                $$17 = new Vec3($$12.getX() + $$13 / $$19 * 2.0, $$12.getY() + $$14 / $$19 * 2.0, $$12.getZ() + $$15 / $$19 * 2.0);
                $$18 = $$7;
            }
            $$12.connection.send(new ClientboundSoundPacket($$8, $$3, $$17.x(), $$17.y(), $$17.z(), $$18, $$6, $$11));
            ++$$10;
        }
        if ($$10 == 0) {
            throw ERROR_TOO_FAR.create();
        }
        if ($$1.size() == 1) {
            $$0.sendSuccess(Component.translatable("commands.playsound.success.single", $$2, ((ServerPlayer)$$1.iterator().next()).getDisplayName()), true);
        } else {
            $$0.sendSuccess(Component.translatable("commands.playsound.success.multiple", $$2, $$1.size()), true);
        }
        return $$10;
    }
}