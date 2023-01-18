/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  java.lang.Object
 *  java.util.ArrayList
 *  java.util.Collections
 *  java.util.function.Predicate
 *  javax.annotation.Nullable
 */
package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.SetBlockCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class FillCommand {
    private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType(($$0, $$1) -> Component.translatable("commands.fill.toobig", $$0, $$1));
    static final BlockInput HOLLOW_CORE = new BlockInput(Blocks.AIR.defaultBlockState(), Collections.emptySet(), null);
    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType((Message)Component.translatable("commands.fill.failed"));

    public static void register(CommandDispatcher<CommandSourceStack> $$03, CommandBuildContext $$1) {
        $$03.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("fill").requires($$0 -> $$0.hasPermission(2))).then(Commands.argument("from", BlockPosArgument.blockPos()).then(Commands.argument("to", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("block", BlockStateArgument.block($$1)).executes($$0 -> FillCommand.fillBlocks((CommandSourceStack)$$0.getSource(), BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "from"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "to")), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)$$0, "block"), Mode.REPLACE, null))).then(((LiteralArgumentBuilder)Commands.literal("replace").executes($$0 -> FillCommand.fillBlocks((CommandSourceStack)$$0.getSource(), BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "from"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "to")), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)$$0, "block"), Mode.REPLACE, null))).then(Commands.argument("filter", BlockPredicateArgument.blockPredicate($$1)).executes($$0 -> FillCommand.fillBlocks((CommandSourceStack)$$0.getSource(), BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "from"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "to")), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)$$0, "block"), Mode.REPLACE, BlockPredicateArgument.getBlockPredicate((CommandContext<CommandSourceStack>)$$0, "filter")))))).then(Commands.literal("keep").executes($$02 -> FillCommand.fillBlocks((CommandSourceStack)$$02.getSource(), BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$02, "from"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$02, "to")), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)$$02, "block"), Mode.REPLACE, (Predicate<BlockInWorld>)((Predicate)$$0 -> $$0.getLevel().isEmptyBlock($$0.getPos())))))).then(Commands.literal("outline").executes($$0 -> FillCommand.fillBlocks((CommandSourceStack)$$0.getSource(), BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "from"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "to")), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)$$0, "block"), Mode.OUTLINE, null)))).then(Commands.literal("hollow").executes($$0 -> FillCommand.fillBlocks((CommandSourceStack)$$0.getSource(), BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "from"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "to")), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)$$0, "block"), Mode.HOLLOW, null)))).then(Commands.literal("destroy").executes($$0 -> FillCommand.fillBlocks((CommandSourceStack)$$0.getSource(), BoundingBox.fromCorners(BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "from"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "to")), BlockStateArgument.getBlock((CommandContext<CommandSourceStack>)$$0, "block"), Mode.DESTROY, null)))))));
    }

    private static int fillBlocks(CommandSourceStack $$0, BoundingBox $$1, BlockInput $$2, Mode $$3, @Nullable Predicate<BlockInWorld> $$4) throws CommandSyntaxException {
        int $$6;
        int $$5 = $$1.getXSpan() * $$1.getYSpan() * $$1.getZSpan();
        if ($$5 > ($$6 = $$0.getLevel().getGameRules().getInt(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT))) {
            throw ERROR_AREA_TOO_LARGE.create((Object)$$6, (Object)$$5);
        }
        ArrayList $$7 = Lists.newArrayList();
        ServerLevel $$8 = $$0.getLevel();
        int $$9 = 0;
        for (BlockPos $$10 : BlockPos.betweenClosed($$1.minX(), $$1.minY(), $$1.minZ(), $$1.maxX(), $$1.maxY(), $$1.maxZ())) {
            BlockInput $$11;
            if ($$4 != null && !$$4.test((Object)new BlockInWorld($$8, $$10, true)) || ($$11 = $$3.filter.filter($$1, $$10, $$2, $$8)) == null) continue;
            BlockEntity $$12 = $$8.getBlockEntity($$10);
            Clearable.tryClear($$12);
            if (!$$11.place($$8, $$10, 2)) continue;
            $$7.add((Object)$$10.immutable());
            ++$$9;
        }
        for (BlockPos $$13 : $$7) {
            Block $$14 = $$8.getBlockState($$13).getBlock();
            $$8.blockUpdated($$13, $$14);
        }
        if ($$9 == 0) {
            throw ERROR_FAILED.create();
        }
        $$0.sendSuccess(Component.translatable("commands.fill.success", $$9), true);
        return $$9;
    }

    static enum Mode {
        REPLACE(($$0, $$1, $$2, $$3) -> $$2),
        OUTLINE(($$0, $$1, $$2, $$3) -> {
            if ($$1.getX() == $$0.minX() || $$1.getX() == $$0.maxX() || $$1.getY() == $$0.minY() || $$1.getY() == $$0.maxY() || $$1.getZ() == $$0.minZ() || $$1.getZ() == $$0.maxZ()) {
                return $$2;
            }
            return null;
        }),
        HOLLOW(($$0, $$1, $$2, $$3) -> {
            if ($$1.getX() == $$0.minX() || $$1.getX() == $$0.maxX() || $$1.getY() == $$0.minY() || $$1.getY() == $$0.maxY() || $$1.getZ() == $$0.minZ() || $$1.getZ() == $$0.maxZ()) {
                return $$2;
            }
            return HOLLOW_CORE;
        }),
        DESTROY(($$0, $$1, $$2, $$3) -> {
            $$3.destroyBlock($$1, true);
            return $$2;
        });

        public final SetBlockCommand.Filter filter;

        private Mode(SetBlockCommand.Filter $$0) {
            this.filter = $$0;
        }
    }
}