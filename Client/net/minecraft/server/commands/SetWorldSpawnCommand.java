/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  java.lang.Float
 *  java.lang.Object
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.AngleArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class SetWorldSpawnCommand {
    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("setworldspawn").requires($$0 -> $$0.hasPermission(2))).executes($$0 -> SetWorldSpawnCommand.setSpawn((CommandSourceStack)$$0.getSource(), new BlockPos(((CommandSourceStack)$$0.getSource()).getPosition()), 0.0f))).then(((RequiredArgumentBuilder)Commands.argument("pos", BlockPosArgument.blockPos()).executes($$0 -> SetWorldSpawnCommand.setSpawn((CommandSourceStack)$$0.getSource(), BlockPosArgument.getSpawnablePos((CommandContext<CommandSourceStack>)$$0, "pos"), 0.0f))).then(Commands.argument("angle", AngleArgument.angle()).executes($$0 -> SetWorldSpawnCommand.setSpawn((CommandSourceStack)$$0.getSource(), BlockPosArgument.getSpawnablePos((CommandContext<CommandSourceStack>)$$0, "pos"), AngleArgument.getAngle((CommandContext<CommandSourceStack>)$$0, "angle"))))));
    }

    private static int setSpawn(CommandSourceStack $$0, BlockPos $$1, float $$2) {
        $$0.getLevel().setDefaultSpawnPos($$1, $$2);
        $$0.sendSuccess(Component.translatable("commands.setworldspawn.success", $$1.getX(), $$1.getY(), $$1.getZ(), Float.valueOf((float)$$2)), true);
        return 1;
    }
}