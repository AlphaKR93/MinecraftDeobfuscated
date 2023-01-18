/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  java.lang.Object
 *  java.util.Collection
 *  javax.annotation.Nullable
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;

public class StopSoundCommand {
    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        RequiredArgumentBuilder $$12 = (RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes($$0 -> StopSoundCommand.stopSound((CommandSourceStack)$$0.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$0, "targets"), null, null))).then(Commands.literal("*").then(Commands.argument("sound", ResourceLocationArgument.id()).suggests(SuggestionProviders.AVAILABLE_SOUNDS).executes($$0 -> StopSoundCommand.stopSound((CommandSourceStack)$$0.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$0, "targets"), null, ResourceLocationArgument.getId((CommandContext<CommandSourceStack>)$$0, "sound")))));
        for (SoundSource $$2 : SoundSource.values()) {
            $$12.then(((LiteralArgumentBuilder)Commands.literal($$2.getName()).executes($$1 -> StopSoundCommand.stopSound((CommandSourceStack)$$1.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$1, "targets"), $$2, null))).then(Commands.argument("sound", ResourceLocationArgument.id()).suggests(SuggestionProviders.AVAILABLE_SOUNDS).executes($$1 -> StopSoundCommand.stopSound((CommandSourceStack)$$1.getSource(), EntityArgument.getPlayers((CommandContext<CommandSourceStack>)$$1, "targets"), $$2, ResourceLocationArgument.getId((CommandContext<CommandSourceStack>)$$1, "sound")))));
        }
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("stopsound").requires($$0 -> $$0.hasPermission(2))).then((ArgumentBuilder)$$12));
    }

    private static int stopSound(CommandSourceStack $$0, Collection<ServerPlayer> $$1, @Nullable SoundSource $$2, @Nullable ResourceLocation $$3) {
        ClientboundStopSoundPacket $$4 = new ClientboundStopSoundPacket($$3, $$2);
        for (ServerPlayer $$5 : $$1) {
            $$5.connection.send($$4);
        }
        if ($$2 != null) {
            if ($$3 != null) {
                $$0.sendSuccess(Component.translatable("commands.stopsound.success.source.sound", $$3, $$2.getName()), true);
            } else {
                $$0.sendSuccess(Component.translatable("commands.stopsound.success.source.any", $$2.getName()), true);
            }
        } else if ($$3 != null) {
            $$0.sendSuccess(Component.translatable("commands.stopsound.success.sourceless.sound", $$3), true);
        } else {
            $$0.sendSuccess(Component.translatable("commands.stopsound.success.sourceless.any"), true);
        }
        return $$1.size();
    }
}