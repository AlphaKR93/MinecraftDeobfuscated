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
 *  java.util.Collection
 *  java.util.LinkedList
 *  java.util.List
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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.ticks.LevelTicks;

public class CloneCommands {
    private static final int MAX_CLONE_AREA = 32768;
    private static final SimpleCommandExceptionType ERROR_OVERLAP = new SimpleCommandExceptionType((Message)Component.translatable("commands.clone.overlap"));
    private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType(($$0, $$1) -> Component.translatable("commands.clone.toobig", $$0, $$1));
    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType((Message)Component.translatable("commands.clone.failed"));
    public static final Predicate<BlockInWorld> FILTER_AIR = $$0 -> !$$0.getState().isAir();

    public static void register(CommandDispatcher<CommandSourceStack> $$03, CommandBuildContext $$1) {
        $$03.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("clone").requires($$0 -> $$0.hasPermission(2))).then(Commands.argument("begin", BlockPosArgument.blockPos()).then(Commands.argument("end", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("destination", BlockPosArgument.blockPos()).executes($$02 -> CloneCommands.clone((CommandSourceStack)$$02.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$02, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$02, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$02, "destination"), (Predicate<BlockInWorld>)((Predicate)$$0 -> true), Mode.NORMAL))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("replace").executes($$02 -> CloneCommands.clone((CommandSourceStack)$$02.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$02, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$02, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$02, "destination"), (Predicate<BlockInWorld>)((Predicate)$$0 -> true), Mode.NORMAL))).then(Commands.literal("force").executes($$02 -> CloneCommands.clone((CommandSourceStack)$$02.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$02, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$02, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$02, "destination"), (Predicate<BlockInWorld>)((Predicate)$$0 -> true), Mode.FORCE)))).then(Commands.literal("move").executes($$02 -> CloneCommands.clone((CommandSourceStack)$$02.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$02, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$02, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$02, "destination"), (Predicate<BlockInWorld>)((Predicate)$$0 -> true), Mode.MOVE)))).then(Commands.literal("normal").executes($$02 -> CloneCommands.clone((CommandSourceStack)$$02.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$02, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$02, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$02, "destination"), (Predicate<BlockInWorld>)((Predicate)$$0 -> true), Mode.NORMAL))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("masked").executes($$0 -> CloneCommands.clone((CommandSourceStack)$$0.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "destination"), FILTER_AIR, Mode.NORMAL))).then(Commands.literal("force").executes($$0 -> CloneCommands.clone((CommandSourceStack)$$0.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "destination"), FILTER_AIR, Mode.FORCE)))).then(Commands.literal("move").executes($$0 -> CloneCommands.clone((CommandSourceStack)$$0.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "destination"), FILTER_AIR, Mode.MOVE)))).then(Commands.literal("normal").executes($$0 -> CloneCommands.clone((CommandSourceStack)$$0.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "destination"), FILTER_AIR, Mode.NORMAL))))).then(Commands.literal("filtered").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("filter", BlockPredicateArgument.blockPredicate($$1)).executes($$0 -> CloneCommands.clone((CommandSourceStack)$$0.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "destination"), BlockPredicateArgument.getBlockPredicate((CommandContext<CommandSourceStack>)$$0, "filter"), Mode.NORMAL))).then(Commands.literal("force").executes($$0 -> CloneCommands.clone((CommandSourceStack)$$0.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "destination"), BlockPredicateArgument.getBlockPredicate((CommandContext<CommandSourceStack>)$$0, "filter"), Mode.FORCE)))).then(Commands.literal("move").executes($$0 -> CloneCommands.clone((CommandSourceStack)$$0.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "destination"), BlockPredicateArgument.getBlockPredicate((CommandContext<CommandSourceStack>)$$0, "filter"), Mode.MOVE)))).then(Commands.literal("normal").executes($$0 -> CloneCommands.clone((CommandSourceStack)$$0.getSource(), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "begin"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "end"), BlockPosArgument.getLoadedBlockPos((CommandContext<CommandSourceStack>)$$0, "destination"), BlockPredicateArgument.getBlockPredicate((CommandContext<CommandSourceStack>)$$0, "filter"), Mode.NORMAL)))))))));
    }

    private static int clone(CommandSourceStack $$0, BlockPos $$1, BlockPos $$2, BlockPos $$3, Predicate<BlockInWorld> $$4, Mode $$5) throws CommandSyntaxException {
        BoundingBox $$6 = BoundingBox.fromCorners($$1, $$2);
        Vec3i $$7 = $$3.offset($$6.getLength());
        BoundingBox $$8 = BoundingBox.fromCorners($$3, $$7);
        if (!$$5.canOverlap() && $$8.intersects($$6)) {
            throw ERROR_OVERLAP.create();
        }
        int $$9 = $$6.getXSpan() * $$6.getYSpan() * $$6.getZSpan();
        if ($$9 > 32768) {
            throw ERROR_AREA_TOO_LARGE.create((Object)32768, (Object)$$9);
        }
        ServerLevel $$10 = $$0.getLevel();
        if (!$$10.hasChunksAt($$1, $$2) || !$$10.hasChunksAt($$3, (BlockPos)$$7)) {
            throw BlockPosArgument.ERROR_NOT_LOADED.create();
        }
        ArrayList $$11 = Lists.newArrayList();
        ArrayList $$12 = Lists.newArrayList();
        ArrayList $$13 = Lists.newArrayList();
        LinkedList $$14 = Lists.newLinkedList();
        BlockPos $$15 = new BlockPos($$8.minX() - $$6.minX(), $$8.minY() - $$6.minY(), $$8.minZ() - $$6.minZ());
        for (int $$16 = $$6.minZ(); $$16 <= $$6.maxZ(); ++$$16) {
            for (int $$17 = $$6.minY(); $$17 <= $$6.maxY(); ++$$17) {
                for (int $$18 = $$6.minX(); $$18 <= $$6.maxX(); ++$$18) {
                    BlockPos $$19 = new BlockPos($$18, $$17, $$16);
                    Vec3i $$20 = $$19.offset($$15);
                    BlockInWorld $$21 = new BlockInWorld($$10, $$19, false);
                    BlockState $$22 = $$21.getState();
                    if (!$$4.test((Object)$$21)) continue;
                    BlockEntity $$23 = $$10.getBlockEntity($$19);
                    if ($$23 != null) {
                        CompoundTag $$24 = $$23.saveWithoutMetadata();
                        $$12.add((Object)new CloneBlockInfo((BlockPos)$$20, $$22, $$24));
                        $$14.addLast((Object)$$19);
                        continue;
                    }
                    if ($$22.isSolidRender($$10, $$19) || $$22.isCollisionShapeFullBlock($$10, $$19)) {
                        $$11.add((Object)new CloneBlockInfo((BlockPos)$$20, $$22, null));
                        $$14.addLast((Object)$$19);
                        continue;
                    }
                    $$13.add((Object)new CloneBlockInfo((BlockPos)$$20, $$22, null));
                    $$14.addFirst((Object)$$19);
                }
            }
        }
        if ($$5 == Mode.MOVE) {
            for (BlockPos $$25 : $$14) {
                BlockEntity $$26 = $$10.getBlockEntity($$25);
                Clearable.tryClear($$26);
                $$10.setBlock($$25, Blocks.BARRIER.defaultBlockState(), 2);
            }
            for (BlockPos $$27 : $$14) {
                $$10.setBlock($$27, Blocks.AIR.defaultBlockState(), 3);
            }
        }
        ArrayList $$28 = Lists.newArrayList();
        $$28.addAll((Collection)$$11);
        $$28.addAll((Collection)$$12);
        $$28.addAll((Collection)$$13);
        List $$29 = Lists.reverse((List)$$28);
        for (CloneBlockInfo $$30 : $$29) {
            BlockEntity $$31 = $$10.getBlockEntity($$30.pos);
            Clearable.tryClear($$31);
            $$10.setBlock($$30.pos, Blocks.BARRIER.defaultBlockState(), 2);
        }
        int $$32 = 0;
        for (CloneBlockInfo $$33 : $$28) {
            if (!$$10.setBlock($$33.pos, $$33.state, 2)) continue;
            ++$$32;
        }
        for (CloneBlockInfo $$34 : $$12) {
            BlockEntity $$35 = $$10.getBlockEntity($$34.pos);
            if ($$34.tag != null && $$35 != null) {
                $$35.load($$34.tag);
                $$35.setChanged();
            }
            $$10.setBlock($$34.pos, $$34.state, 2);
        }
        for (CloneBlockInfo $$36 : $$29) {
            $$10.blockUpdated($$36.pos, $$36.state.getBlock());
        }
        ((LevelTicks)$$10.getBlockTicks()).copyArea($$6, $$15);
        if ($$32 == 0) {
            throw ERROR_FAILED.create();
        }
        $$0.sendSuccess(Component.translatable("commands.clone.success", $$32), true);
        return $$32;
    }

    static enum Mode {
        FORCE(true),
        MOVE(true),
        NORMAL(false);

        private final boolean canOverlap;

        private Mode(boolean $$0) {
            this.canOverlap = $$0;
        }

        public boolean canOverlap() {
            return this.canOverlap;
        }
    }

    static class CloneBlockInfo {
        public final BlockPos pos;
        public final BlockState state;
        @Nullable
        public final CompoundTag tag;

        public CloneBlockInfo(BlockPos $$0, BlockState $$1, @Nullable CompoundTag $$2) {
            this.pos = $$0;
            this.state = $$1;
            this.tag = $$2;
        }
    }
}