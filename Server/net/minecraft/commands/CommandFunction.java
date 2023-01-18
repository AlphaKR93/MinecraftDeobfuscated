/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.ParseResults
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  java.lang.FunctionalInterface
 *  java.lang.IllegalArgumentException
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Deque
 *  java.util.List
 *  java.util.Optional
 *  javax.annotation.Nullable
 */
package net.minecraft.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerFunctionManager;

public class CommandFunction {
    private final Entry[] entries;
    final ResourceLocation id;

    public CommandFunction(ResourceLocation $$0, Entry[] $$1) {
        this.id = $$0;
        this.entries = $$1;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public Entry[] getEntries() {
        return this.entries;
    }

    public static CommandFunction fromLines(ResourceLocation $$0, CommandDispatcher<CommandSourceStack> $$1, CommandSourceStack $$2, List<String> $$3) {
        ArrayList $$4 = Lists.newArrayListWithCapacity((int)$$3.size());
        for (int $$5 = 0; $$5 < $$3.size(); ++$$5) {
            int $$6 = $$5 + 1;
            String $$7 = ((String)$$3.get($$5)).trim();
            StringReader $$8 = new StringReader($$7);
            if (!$$8.canRead() || $$8.peek() == '#') continue;
            if ($$8.peek() == '/') {
                $$8.skip();
                if ($$8.peek() == '/') {
                    throw new IllegalArgumentException("Unknown or invalid command '" + $$7 + "' on line " + $$6 + " (if you intended to make a comment, use '#' not '//')");
                }
                String $$9 = $$8.readUnquotedString();
                throw new IllegalArgumentException("Unknown or invalid command '" + $$7 + "' on line " + $$6 + " (did you mean '" + $$9 + "'? Do not use a preceding forwards slash.)");
            }
            try {
                ParseResults $$10 = $$1.parse($$8, (Object)$$2);
                if ($$10.getReader().canRead()) {
                    throw Commands.getParseException($$10);
                }
                $$4.add((Object)new CommandEntry((ParseResults<CommandSourceStack>)$$10));
                continue;
            }
            catch (CommandSyntaxException $$11) {
                throw new IllegalArgumentException("Whilst parsing command on line " + $$6 + ": " + $$11.getMessage());
            }
        }
        return new CommandFunction($$0, (Entry[])$$4.toArray((Object[])new Entry[0]));
    }

    @FunctionalInterface
    public static interface Entry {
        public void execute(ServerFunctionManager var1, CommandSourceStack var2, Deque<ServerFunctionManager.QueuedCommand> var3, int var4, int var5, @Nullable ServerFunctionManager.TraceCallbacks var6) throws CommandSyntaxException;
    }

    public static class CommandEntry
    implements Entry {
        private final ParseResults<CommandSourceStack> parse;

        public CommandEntry(ParseResults<CommandSourceStack> $$0) {
            this.parse = $$0;
        }

        @Override
        public void execute(ServerFunctionManager $$0, CommandSourceStack $$1, Deque<ServerFunctionManager.QueuedCommand> $$2, int $$3, int $$4, @Nullable ServerFunctionManager.TraceCallbacks $$5) throws CommandSyntaxException {
            if ($$5 != null) {
                String $$6 = this.parse.getReader().getString();
                $$5.onCommand($$4, $$6);
                int $$7 = this.execute($$0, $$1);
                $$5.onReturn($$4, $$6, $$7);
            } else {
                this.execute($$0, $$1);
            }
        }

        private int execute(ServerFunctionManager $$0, CommandSourceStack $$12) throws CommandSyntaxException {
            return $$0.getDispatcher().execute(Commands.mapSource(this.parse, $$1 -> $$12));
        }

        public String toString() {
            return this.parse.getReader().getString();
        }
    }

    public static class CacheableFunction {
        public static final CacheableFunction NONE = new CacheableFunction((ResourceLocation)null);
        @Nullable
        private final ResourceLocation id;
        private boolean resolved;
        private Optional<CommandFunction> function = Optional.empty();

        public CacheableFunction(@Nullable ResourceLocation $$0) {
            this.id = $$0;
        }

        public CacheableFunction(CommandFunction $$0) {
            this.resolved = true;
            this.id = null;
            this.function = Optional.of((Object)$$0);
        }

        public Optional<CommandFunction> get(ServerFunctionManager $$0) {
            if (!this.resolved) {
                if (this.id != null) {
                    this.function = $$0.get(this.id);
                }
                this.resolved = true;
            }
            return this.function;
        }

        @Nullable
        public ResourceLocation getId() {
            return (ResourceLocation)this.function.map($$0 -> $$0.id).orElse((Object)this.id);
        }
    }

    public static class FunctionEntry
    implements Entry {
        private final CacheableFunction function;

        public FunctionEntry(CommandFunction $$0) {
            this.function = new CacheableFunction($$0);
        }

        @Override
        public void execute(ServerFunctionManager $$0, CommandSourceStack $$1, Deque<ServerFunctionManager.QueuedCommand> $$2, int $$3, int $$4, @Nullable ServerFunctionManager.TraceCallbacks $$52) {
            Util.ifElse(this.function.get($$0), $$5 -> {
                Entry[] $$6 = $$5.getEntries();
                if ($$52 != null) {
                    $$52.onCall($$4, $$5.getId(), $$6.length);
                }
                int $$7 = $$3 - $$2.size();
                int $$8 = Math.min((int)$$6.length, (int)$$7);
                for (int $$9 = $$8 - 1; $$9 >= 0; --$$9) {
                    $$2.addFirst((Object)new ServerFunctionManager.QueuedCommand($$1, $$4 + 1, $$6[$$9]));
                }
            }, () -> {
                if ($$52 != null) {
                    $$52.onCall($$4, this.function.getId(), -1);
                }
            });
        }

        public String toString() {
            return "function " + this.function.getId();
        }
    }
}