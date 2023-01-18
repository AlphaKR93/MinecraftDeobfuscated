/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  java.lang.Object
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class SetBlockCommand {
    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType((Message)Component.translatable("commands.setblock.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> $$03, CommandBuildContext $$1) {
        $$03.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("setblock").requires($$0 -> $$0.hasPermission(2))).then(Commands.argument("pos", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("block", BlockStateArgument.block($$1)).executes($$0 -> SetBlockCommand.setBlock((CommandSourceStack)$$0.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "pos"), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)$$0, "block"), Mode.REPLACE, null))).then(Commands.literal("destroy").executes($$0 -> SetBlockCommand.setBlock((CommandSourceStack)$$0.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "pos"), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)$$0, "block"), Mode.DESTROY, null)))).then(Commands.literal("keep").executes($$02 -> SetBlockCommand.setBlock((CommandSourceStack)$$02.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$02, "pos"), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)$$02, "block"), Mode.REPLACE, (Predicate<BlockInWorld>)((Predicate)$$0 -> $$0.getLevel().isEmptyBlock($$0.getPos())))))).then(Commands.literal("replace").executes($$0 -> SetBlockCommand.setBlock((CommandSourceStack)$$0.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "pos"), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)$$0, "block"), Mode.REPLACE, null))))));
    }

    private static int setBlock(CommandSourceStack $$0, BlockPos $$1, BlockInput $$2, Mode $$3, @Nullable Predicate<BlockInWorld> $$4) throws CommandSyntaxException {
        boolean $$8;
        ServerLevel $$5 = $$0.getLevel();
        if ($$4 != null && !$$4.test((Object)new BlockInWorld($$5, $$1, true))) {
            throw ERROR_FAILED.create();
        }
        if ($$3 == Mode.DESTROY) {
            $$5.destroyBlock($$1, true);
            boolean $$6 = !$$2.getState().isAir() || !$$5.getBlockState($$1).isAir();
        } else {
            BlockEntity $$7 = $$5.getBlockEntity($$1);
            Clearable.tryClear($$7);
            $$8 = true;
        }
        if ($$8 && !$$2.place($$5, $$1, 2)) {
            throw ERROR_FAILED.create();
        }
        $$5.blockUpdated($$1, $$2.getState().getBlock());
        $$0.sendSuccess(Component.translatable("commands.setblock.success", $$1.getX(), $$1.getY(), $$1.getZ()), true);
        return 1;
    }

    public static enum Mode {
        REPLACE,
        DESTROY;

    }

    public static interface Filter {
        @Nullable
        public BlockInput filter(BoundingBox var1, BlockPos var2, BlockInput var3, ServerLevel var4);
    }
}