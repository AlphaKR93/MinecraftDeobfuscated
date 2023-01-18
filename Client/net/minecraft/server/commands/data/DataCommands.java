/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.arguments.DoubleArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.ArgumentBuilder
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.builder.RequiredArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collections
 *  java.util.Iterator
 *  java.util.List
 *  java.util.function.BiConsumer
 *  java.util.function.Function
 *  java.util.function.Supplier
 */
package net.minecraft.server.commands.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.NbtTagArgument;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.data.BlockDataAccessor;
import net.minecraft.server.commands.data.DataAccessor;
import net.minecraft.server.commands.data.EntityDataAccessor;
import net.minecraft.server.commands.data.StorageDataAccessor;
import net.minecraft.util.Mth;

public class DataCommands {
    private static final SimpleCommandExceptionType ERROR_MERGE_UNCHANGED = new SimpleCommandExceptionType((Message)Component.translatable("commands.data.merge.failed"));
    private static final DynamicCommandExceptionType ERROR_GET_NOT_NUMBER = new DynamicCommandExceptionType($$0 -> Component.translatable("commands.data.get.invalid", $$0));
    private static final DynamicCommandExceptionType ERROR_GET_NON_EXISTENT = new DynamicCommandExceptionType($$0 -> Component.translatable("commands.data.get.unknown", $$0));
    private static final SimpleCommandExceptionType ERROR_MULTIPLE_TAGS = new SimpleCommandExceptionType((Message)Component.translatable("commands.data.get.multiple"));
    private static final DynamicCommandExceptionType ERROR_EXPECTED_OBJECT = new DynamicCommandExceptionType($$0 -> Component.translatable("commands.data.modify.expected_object", $$0));
    public static final List<Function<String, DataProvider>> ALL_PROVIDERS = ImmutableList.of(EntityDataAccessor.PROVIDER, BlockDataAccessor.PROVIDER, StorageDataAccessor.PROVIDER);
    public static final List<DataProvider> TARGET_PROVIDERS = (List)ALL_PROVIDERS.stream().map($$0 -> (DataProvider)$$0.apply((Object)"target")).collect(ImmutableList.toImmutableList());
    public static final List<DataProvider> SOURCE_PROVIDERS = (List)ALL_PROVIDERS.stream().map($$0 -> (DataProvider)$$0.apply((Object)"source")).collect(ImmutableList.toImmutableList());

    public static void register(CommandDispatcher<CommandSourceStack> $$03) {
        LiteralArgumentBuilder $$1 = (LiteralArgumentBuilder)Commands.literal("data").requires($$0 -> $$0.hasPermission(2));
        for (DataProvider $$2 : TARGET_PROVIDERS) {
            ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)$$1.then($$2.wrap((ArgumentBuilder<CommandSourceStack, ?>)Commands.literal("merge"), (Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>>)((Function)$$12 -> $$12.then(Commands.argument("nbt", CompoundTagArgument.compoundTag()).executes($$1 -> DataCommands.mergeData((CommandSourceStack)$$1.getSource(), $$2.access((CommandContext<CommandSourceStack>)$$1), CompoundTagArgument.getCompoundTag($$1, "nbt")))))))).then($$2.wrap((ArgumentBuilder<CommandSourceStack, ?>)Commands.literal("get"), (Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>>)((Function)$$12 -> $$12.executes($$1 -> DataCommands.getData((CommandSourceStack)$$1.getSource(), $$2.access((CommandContext<CommandSourceStack>)$$1))).then(((RequiredArgumentBuilder)Commands.argument("path", NbtPathArgument.nbtPath()).executes($$1 -> DataCommands.getData((CommandSourceStack)$$1.getSource(), $$2.access((CommandContext<CommandSourceStack>)$$1), NbtPathArgument.getPath((CommandContext<CommandSourceStack>)$$1, "path")))).then(Commands.argument("scale", DoubleArgumentType.doubleArg()).executes($$1 -> DataCommands.getNumeric((CommandSourceStack)$$1.getSource(), $$2.access((CommandContext<CommandSourceStack>)$$1), NbtPathArgument.getPath((CommandContext<CommandSourceStack>)$$1, "path"), DoubleArgumentType.getDouble((CommandContext)$$1, (String)"scale"))))))))).then($$2.wrap((ArgumentBuilder<CommandSourceStack, ?>)Commands.literal("remove"), (Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>>)((Function)$$12 -> $$12.then(Commands.argument("path", NbtPathArgument.nbtPath()).executes($$1 -> DataCommands.removeData((CommandSourceStack)$$1.getSource(), $$2.access((CommandContext<CommandSourceStack>)$$1), NbtPathArgument.getPath((CommandContext<CommandSourceStack>)$$1, "path")))))))).then(DataCommands.decorateModification(($$02, $$12) -> $$02.then(Commands.literal("insert").then(Commands.argument("index", IntegerArgumentType.integer()).then($$12.create(($$0, $$1, $$2, $$3) -> $$2.insert(IntegerArgumentType.getInteger((CommandContext)$$0, (String)"index"), $$1, (List<Tag>)$$3))))).then(Commands.literal("prepend").then($$12.create(($$0, $$1, $$2, $$3) -> $$2.insert(0, $$1, (List<Tag>)$$3)))).then(Commands.literal("append").then($$12.create(($$0, $$1, $$2, $$3) -> $$2.insert(-1, $$1, (List<Tag>)$$3)))).then(Commands.literal("set").then($$12.create(($$0, $$1, $$2, $$3) -> $$2.set($$1, (Tag)Iterables.getLast((Iterable)$$3))))).then(Commands.literal("merge").then($$12.create(($$0, $$1, $$2, $$3) -> {
                CompoundTag $$4 = new CompoundTag();
                for (Tag $$5 : $$3) {
                    if (NbtPathArgument.NbtPath.isTooDeep($$5, 0)) {
                        throw NbtPathArgument.ERROR_DATA_TOO_DEEP.create();
                    }
                    if ($$5 instanceof CompoundTag) {
                        CompoundTag $$6 = (CompoundTag)$$5;
                        $$4.merge($$6);
                        continue;
                    }
                    throw ERROR_EXPECTED_OBJECT.create((Object)$$5);
                }
                List<Tag> $$7 = $$2.getOrCreate($$1, (Supplier<Tag>)((Supplier)CompoundTag::new));
                int $$8 = 0;
                for (Tag $$9 : $$7) {
                    void $$11;
                    if (!($$9 instanceof CompoundTag)) {
                        throw ERROR_EXPECTED_OBJECT.create((Object)$$9);
                    }
                    CompoundTag $$10 = (CompoundTag)$$9;
                    CompoundTag $$12 = $$11.copy();
                    $$11.merge($$4);
                    $$8 += $$12.equals($$11) ? 0 : 1;
                }
                return $$8;
            })))));
        }
        $$03.register($$1);
    }

    private static ArgumentBuilder<CommandSourceStack, ?> decorateModification(BiConsumer<ArgumentBuilder<CommandSourceStack, ?>, DataManipulatorDecorator> $$0) {
        LiteralArgumentBuilder<CommandSourceStack> $$1 = Commands.literal("modify");
        for (DataProvider $$2 : TARGET_PROVIDERS) {
            $$2.wrap((ArgumentBuilder<CommandSourceStack, ?>)$$1, (Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>>)((Function)$$22 -> {
                RequiredArgumentBuilder<CommandSourceStack, NbtPathArgument.NbtPath> $$3 = Commands.argument("targetPath", NbtPathArgument.nbtPath());
                for (DataProvider $$4 : SOURCE_PROVIDERS) {
                    $$0.accept($$3, $$2 -> $$4.wrap((ArgumentBuilder<CommandSourceStack, ?>)Commands.literal("from"), (Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>>)((Function)$$32 -> $$32.executes($$3 -> {
                        List $$4 = Collections.singletonList((Object)$$4.access((CommandContext<CommandSourceStack>)$$3).getData());
                        return DataCommands.manipulateData((CommandContext<CommandSourceStack>)$$3, $$2, $$2, (List<Tag>)$$4);
                    }).then(Commands.argument("sourcePath", NbtPathArgument.nbtPath()).executes($$3 -> {
                        DataAccessor $$4 = $$4.access((CommandContext<CommandSourceStack>)$$3);
                        NbtPathArgument.NbtPath $$5 = NbtPathArgument.getPath((CommandContext<CommandSourceStack>)$$3, "sourcePath");
                        List<Tag> $$6 = $$5.get($$4.getData());
                        return DataCommands.manipulateData((CommandContext<CommandSourceStack>)$$3, $$2, $$2, $$6);
                    })))));
                }
                $$0.accept($$3, $$1 -> Commands.literal("value").then(Commands.argument("value", NbtTagArgument.nbtTag()).executes($$2 -> {
                    List $$3 = Collections.singletonList((Object)NbtTagArgument.getNbtTag($$2, "value"));
                    return DataCommands.manipulateData((CommandContext<CommandSourceStack>)$$2, $$2, $$1, (List<Tag>)$$3);
                })));
                return $$22.then($$3);
            }));
        }
        return $$1;
    }

    private static int manipulateData(CommandContext<CommandSourceStack> $$0, DataProvider $$1, DataManipulator $$2, List<Tag> $$3) throws CommandSyntaxException {
        DataAccessor $$4 = $$1.access($$0);
        NbtPathArgument.NbtPath $$5 = NbtPathArgument.getPath($$0, "targetPath");
        CompoundTag $$6 = $$4.getData();
        int $$7 = $$2.modify($$0, $$6, $$5, $$3);
        if ($$7 == 0) {
            throw ERROR_MERGE_UNCHANGED.create();
        }
        $$4.setData($$6);
        ((CommandSourceStack)$$0.getSource()).sendSuccess($$4.getModifiedSuccess(), true);
        return $$7;
    }

    private static int removeData(CommandSourceStack $$0, DataAccessor $$1, NbtPathArgument.NbtPath $$2) throws CommandSyntaxException {
        CompoundTag $$3 = $$1.getData();
        int $$4 = $$2.remove($$3);
        if ($$4 == 0) {
            throw ERROR_MERGE_UNCHANGED.create();
        }
        $$1.setData($$3);
        $$0.sendSuccess($$1.getModifiedSuccess(), true);
        return $$4;
    }

    private static Tag getSingleTag(NbtPathArgument.NbtPath $$0, DataAccessor $$1) throws CommandSyntaxException {
        List<Tag> $$2 = $$0.get($$1.getData());
        Iterator $$3 = $$2.iterator();
        Tag $$4 = (Tag)$$3.next();
        if ($$3.hasNext()) {
            throw ERROR_MULTIPLE_TAGS.create();
        }
        return $$4;
    }

    /*
     * WARNING - void declaration
     */
    private static int getData(CommandSourceStack $$0, DataAccessor $$1, NbtPathArgument.NbtPath $$2) throws CommandSyntaxException {
        void $$8;
        Tag $$3 = DataCommands.getSingleTag($$2, $$1);
        if ($$3 instanceof NumericTag) {
            int $$4 = Mth.floor(((NumericTag)$$3).getAsDouble());
        } else if ($$3 instanceof CollectionTag) {
            int $$5 = ((CollectionTag)$$3).size();
        } else if ($$3 instanceof CompoundTag) {
            int $$6 = ((CompoundTag)$$3).size();
        } else if ($$3 instanceof StringTag) {
            int $$7 = $$3.getAsString().length();
        } else {
            throw ERROR_GET_NON_EXISTENT.create((Object)$$2.toString());
        }
        $$0.sendSuccess($$1.getPrintSuccess($$3), false);
        return (int)$$8;
    }

    private static int getNumeric(CommandSourceStack $$0, DataAccessor $$1, NbtPathArgument.NbtPath $$2, double $$3) throws CommandSyntaxException {
        Tag $$4 = DataCommands.getSingleTag($$2, $$1);
        if (!($$4 instanceof NumericTag)) {
            throw ERROR_GET_NOT_NUMBER.create((Object)$$2.toString());
        }
        int $$5 = Mth.floor(((NumericTag)$$4).getAsDouble() * $$3);
        $$0.sendSuccess($$1.getPrintSuccess($$2, $$3, $$5), false);
        return $$5;
    }

    private static int getData(CommandSourceStack $$0, DataAccessor $$1) throws CommandSyntaxException {
        $$0.sendSuccess($$1.getPrintSuccess($$1.getData()), false);
        return 1;
    }

    private static int mergeData(CommandSourceStack $$0, DataAccessor $$1, CompoundTag $$2) throws CommandSyntaxException {
        CompoundTag $$3 = $$1.getData();
        if (NbtPathArgument.NbtPath.isTooDeep($$2, 0)) {
            throw NbtPathArgument.ERROR_DATA_TOO_DEEP.create();
        }
        CompoundTag $$4 = $$3.copy().merge($$2);
        if ($$3.equals($$4)) {
            throw ERROR_MERGE_UNCHANGED.create();
        }
        $$1.setData($$4);
        $$0.sendSuccess($$1.getModifiedSuccess(), true);
        return 1;
    }

    public static interface DataProvider {
        public DataAccessor access(CommandContext<CommandSourceStack> var1) throws CommandSyntaxException;

        public ArgumentBuilder<CommandSourceStack, ?> wrap(ArgumentBuilder<CommandSourceStack, ?> var1, Function<ArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>> var2);
    }

    static interface DataManipulator {
        public int modify(CommandContext<CommandSourceStack> var1, CompoundTag var2, NbtPathArgument.NbtPath var3, List<Tag> var4) throws CommandSyntaxException;
    }

    static interface DataManipulatorDecorator {
        public ArgumentBuilder<CommandSourceStack, ?> create(DataManipulator var1);
    }
}