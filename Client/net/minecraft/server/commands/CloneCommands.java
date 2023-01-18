/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.lang.String
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
import com.mojang.brigadier.builder.ArgumentBuilder;
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
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.ticks.LevelTicks;

public class CloneCommands {
    private static final SimpleCommandExceptionType ERROR_OVERLAP = new SimpleCommandExceptionType((Message)Component.translatable("commands.clone.overlap"));
    private static final Dynamic2CommandExceptionType ERROR_AREA_TOO_LARGE = new Dynamic2CommandExceptionType(($$0, $$1) -> Component.translatable("commands.clone.toobig", $$0, $$1));
    private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType((Message)Component.translatable("commands.clone.failed"));
    public static final Predicate<BlockInWorld> FILTER_AIR = $$0 -> !$$0.getState().isAir();

    public static void register(CommandDispatcher<CommandSourceStack> $$02, CommandBuildContext $$1) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("clone").requires($$0 -> $$0.hasPermission(2))).then(CloneCommands.beginEndDestinationAndModeSuffix($$1, $$0 -> ((CommandSourceStack)$$0.getSource()).getLevel()))).then(Commands.literal("from").then(Commands.argument("sourceDimension", DimensionArgument.dimension()).then(CloneCommands.beginEndDestinationAndModeSuffix($$1, $$0 -> DimensionArgument.getDimension((CommandContext<CommandSourceStack>)$$0, "sourceDimension"))))));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> beginEndDestinationAndModeSuffix(CommandBuildContext $$02, CommandFunction<CommandContext<CommandSourceStack>, ServerLevel> $$1) {
        return Commands.argument("begin", BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)Commands.argument("end", BlockPosArgument.blockPos()).then(CloneCommands.destinationAndModeSuffix($$02, $$1, $$0 -> ((CommandSourceStack)$$0.getSource()).getLevel()))).then(Commands.literal("to").then(Commands.argument("targetDimension", DimensionArgument.dimension()).then(CloneCommands.destinationAndModeSuffix($$02, $$1, $$0 -> DimensionArgument.getDimension((CommandContext<CommandSourceStack>)$$0, "targetDimension"))))));
    }

    private static DimensionAndPosition getLoadedDimensionAndPosition(CommandContext<CommandSourceStack> $$0, ServerLevel $$1, String $$2) throws CommandSyntaxException {
        BlockPos $$3 = BlockPosArgument.getLoadedBlockPos($$0, $$1, $$2);
        return new DimensionAndPosition($$1, $$3);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> destinationAndModeSuffix(CommandBuildContext $$03, CommandFunction<CommandContext<CommandSourceStack>, ServerLevel> $$12, CommandFunction<CommandContext<CommandSourceStack>, ServerLevel> $$2) {
        CommandFunction<CommandContext<CommandSourceStack>, DimensionAndPosition> $$32 = $$1 -> CloneCommands.getLoadedDimensionAndPosition((CommandContext<CommandSourceStack>)$$1, (ServerLevel)$$12.apply((CommandContext<CommandSourceStack>)$$1), "begin");
        CommandFunction<CommandContext<CommandSourceStack>, DimensionAndPosition> $$4 = $$1 -> CloneCommands.getLoadedDimensionAndPosition((CommandContext<CommandSourceStack>)$$1, (ServerLevel)$$12.apply((CommandContext<CommandSourceStack>)$$1), "end");
        CommandFunction<CommandContext<CommandSourceStack>, DimensionAndPosition> $$5 = $$1 -> CloneCommands.getLoadedDimensionAndPosition((CommandContext<CommandSourceStack>)$$1, (ServerLevel)$$2.apply((CommandContext<CommandSourceStack>)$$1), "destination");
        return ((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("destination", BlockPosArgument.blockPos()).executes($$3 -> CloneCommands.clone((CommandSourceStack)$$3.getSource(), (DimensionAndPosition)((Object)((Object)$$32.apply($$3))), (DimensionAndPosition)((Object)((Object)$$4.apply($$3))), (DimensionAndPosition)((Object)((Object)$$5.apply($$3))), (Predicate<BlockInWorld>)((Predicate)$$0 -> true), Mode.NORMAL))).then(CloneCommands.wrapWithCloneMode($$32, $$4, $$5, $$02 -> $$0 -> true, Commands.literal("replace").executes($$3 -> CloneCommands.clone((CommandSourceStack)$$3.getSource(), (DimensionAndPosition)((Object)((Object)$$32.apply($$3))), (DimensionAndPosition)((Object)((Object)$$4.apply($$3))), (DimensionAndPosition)((Object)((Object)$$5.apply($$3))), (Predicate<BlockInWorld>)((Predicate)$$0 -> true), Mode.NORMAL))))).then(CloneCommands.wrapWithCloneMode($$32, $$4, $$5, $$0 -> FILTER_AIR, Commands.literal("masked").executes($$3 -> CloneCommands.clone((CommandSourceStack)$$3.getSource(), (DimensionAndPosition)((Object)((Object)$$32.apply($$3))), (DimensionAndPosition)((Object)((Object)$$4.apply($$3))), (DimensionAndPosition)((Object)((Object)$$5.apply($$3))), FILTER_AIR, Mode.NORMAL))))).then(Commands.literal("filtered").then(CloneCommands.wrapWithCloneMode($$32, $$4, $$5, $$0 -> BlockPredicateArgument.getBlockPredicate((CommandContext<CommandSourceStack>)$$0, "filter"), Commands.argument("filter", BlockPredicateArgument.blockPredicate($$03)).executes($$3 -> CloneCommands.clone((CommandSourceStack)$$3.getSource(), (DimensionAndPosition)((Object)((Object)$$32.apply($$3))), (DimensionAndPosition)((Object)((Object)$$4.apply($$3))), (DimensionAndPosition)((Object)((Object)$$5.apply($$3))), BlockPredicateArgument.getBlockPredicate((CommandContext<CommandSourceStack>)$$3, "filter"), Mode.NORMAL)))));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> wrapWithCloneMode(CommandFunction<CommandContext<CommandSourceStack>, DimensionAndPosition> $$0, CommandFunction<CommandContext<CommandSourceStack>, DimensionAndPosition> $$1, CommandFunction<CommandContext<CommandSourceStack>, DimensionAndPosition> $$2, CommandFunction<CommandContext<CommandSourceStack>, Predicate<BlockInWorld>> $$3, ArgumentBuilder<CommandSourceStack, ?> $$42) {
        return $$42.then(Commands.literal("force").executes($$4 -> CloneCommands.clone((CommandSourceStack)$$4.getSource(), (DimensionAndPosition)((Object)((Object)$$0.apply($$4))), (DimensionAndPosition)((Object)((Object)$$1.apply($$4))), (DimensionAndPosition)((Object)((Object)$$2.apply($$4))), (Predicate<BlockInWorld>)((Predicate)$$3.apply($$4)), Mode.FORCE))).then(Commands.literal("move").executes($$4 -> CloneCommands.clone((CommandSourceStack)$$4.getSource(), (DimensionAndPosition)((Object)((Object)$$0.apply($$4))), (DimensionAndPosition)((Object)((Object)$$1.apply($$4))), (DimensionAndPosition)((Object)((Object)$$2.apply($$4))), (Predicate<BlockInWorld>)((Predicate)$$3.apply($$4)), Mode.MOVE))).then(Commands.literal("normal").executes($$4 -> CloneCommands.clone((CommandSourceStack)$$4.getSource(), (DimensionAndPosition)((Object)((Object)$$0.apply($$4))), (DimensionAndPosition)((Object)((Object)$$1.apply($$4))), (DimensionAndPosition)((Object)((Object)$$2.apply($$4))), (Predicate<BlockInWorld>)((Predicate)$$3.apply($$4)), Mode.NORMAL)));
    }

    private static int clone(CommandSourceStack $$0, DimensionAndPosition $$1, DimensionAndPosition $$2, DimensionAndPosition $$3, Predicate<BlockInWorld> $$4, Mode $$5) throws CommandSyntaxException {
        int $$15;
        BlockPos $$6 = $$1.position();
        BlockPos $$7 = $$2.position();
        BoundingBox $$8 = BoundingBox.fromCorners($$6, $$7);
        BlockPos $$9 = $$3.position();
        Vec3i $$10 = $$9.offset($$8.getLength());
        BoundingBox $$11 = BoundingBox.fromCorners($$9, $$10);
        ServerLevel $$12 = $$1.dimension();
        ServerLevel $$13 = $$3.dimension();
        if (!$$5.canOverlap() && $$12 == $$13 && $$11.intersects($$8)) {
            throw ERROR_OVERLAP.create();
        }
        int $$14 = $$8.getXSpan() * $$8.getYSpan() * $$8.getZSpan();
        if ($$14 > ($$15 = $$0.getLevel().getGameRules().getInt(GameRules.RULE_COMMAND_MODIFICATION_BLOCK_LIMIT))) {
            throw ERROR_AREA_TOO_LARGE.create((Object)$$15, (Object)$$14);
        }
        if (!$$12.hasChunksAt($$6, $$7) || !$$13.hasChunksAt($$9, (BlockPos)$$10)) {
            throw BlockPosArgument.ERROR_NOT_LOADED.create();
        }
        ArrayList $$16 = Lists.newArrayList();
        ArrayList $$17 = Lists.newArrayList();
        ArrayList $$18 = Lists.newArrayList();
        LinkedList $$19 = Lists.newLinkedList();
        BlockPos $$20 = new BlockPos($$11.minX() - $$8.minX(), $$11.minY() - $$8.minY(), $$11.minZ() - $$8.minZ());
        for (int $$21 = $$8.minZ(); $$21 <= $$8.maxZ(); ++$$21) {
            for (int $$22 = $$8.minY(); $$22 <= $$8.maxY(); ++$$22) {
                for (int $$23 = $$8.minX(); $$23 <= $$8.maxX(); ++$$23) {
                    BlockPos $$24 = new BlockPos($$23, $$22, $$21);
                    Vec3i $$25 = $$24.offset($$20);
                    BlockInWorld $$26 = new BlockInWorld($$12, $$24, false);
                    BlockState $$27 = $$26.getState();
                    if (!$$4.test((Object)$$26)) continue;
                    BlockEntity $$28 = $$12.getBlockEntity($$24);
                    if ($$28 != null) {
                        CompoundTag $$29 = $$28.saveWithoutMetadata();
                        $$17.add((Object)new CloneBlockInfo((BlockPos)$$25, $$27, $$29));
                        $$19.addLast((Object)$$24);
                        continue;
                    }
                    if ($$27.isSolidRender($$12, $$24) || $$27.isCollisionShapeFullBlock($$12, $$24)) {
                        $$16.add((Object)new CloneBlockInfo((BlockPos)$$25, $$27, null));
                        $$19.addLast((Object)$$24);
                        continue;
                    }
                    $$18.add((Object)new CloneBlockInfo((BlockPos)$$25, $$27, null));
                    $$19.addFirst((Object)$$24);
                }
            }
        }
        if ($$5 == Mode.MOVE) {
            for (BlockPos $$30 : $$19) {
                BlockEntity $$31 = $$12.getBlockEntity($$30);
                Clearable.tryClear($$31);
                $$12.setBlock($$30, Blocks.BARRIER.defaultBlockState(), 2);
            }
            for (BlockPos $$32 : $$19) {
                $$12.setBlock($$32, Blocks.AIR.defaultBlockState(), 3);
            }
        }
        ArrayList $$33 = Lists.newArrayList();
        $$33.addAll((Collection)$$16);
        $$33.addAll((Collection)$$17);
        $$33.addAll((Collection)$$18);
        List $$34 = Lists.reverse((List)$$33);
        for (CloneBlockInfo $$35 : $$34) {
            BlockEntity $$36 = $$13.getBlockEntity($$35.pos);
            Clearable.tryClear($$36);
            $$13.setBlock($$35.pos, Blocks.BARRIER.defaultBlockState(), 2);
        }
        int $$37 = 0;
        for (CloneBlockInfo $$38 : $$33) {
            if (!$$13.setBlock($$38.pos, $$38.state, 2)) continue;
            ++$$37;
        }
        for (CloneBlockInfo $$39 : $$17) {
            BlockEntity $$40 = $$13.getBlockEntity($$39.pos);
            if ($$39.tag != null && $$40 != null) {
                $$40.load($$39.tag);
                $$40.setChanged();
            }
            $$13.setBlock($$39.pos, $$39.state, 2);
        }
        for (CloneBlockInfo $$41 : $$34) {
            $$13.blockUpdated($$41.pos, $$41.state.getBlock());
        }
        ((LevelTicks)$$13.getBlockTicks()).copyAreaFrom($$12.getBlockTicks(), $$8, $$20);
        if ($$37 == 0) {
            throw ERROR_FAILED.create();
        }
        $$0.sendSuccess(Component.translatable("commands.clone.success", $$37), true);
        return $$37;
    }

    @FunctionalInterface
    static interface CommandFunction<T, R> {
        public R apply(T var1) throws CommandSyntaxException;
    }

    record DimensionAndPosition(ServerLevel dimension, BlockPos position) {
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