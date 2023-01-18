/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Queues
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  java.lang.Exception
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collection
 *  java.util.Deque
 *  java.util.List
 *  java.util.Optional
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionLibrary;
import net.minecraft.world.level.GameRules;

public class ServerFunctionManager {
    private static final Component NO_RECURSIVE_TRACES = Component.translatable("commands.debug.function.noRecursion");
    private static final ResourceLocation TICK_FUNCTION_TAG = new ResourceLocation("tick");
    private static final ResourceLocation LOAD_FUNCTION_TAG = new ResourceLocation("load");
    final MinecraftServer server;
    @Nullable
    private ExecutionContext context;
    private List<CommandFunction> ticking = ImmutableList.of();
    private boolean postReload;
    private ServerFunctionLibrary library;

    public ServerFunctionManager(MinecraftServer $$0, ServerFunctionLibrary $$1) {
        this.server = $$0;
        this.library = $$1;
        this.postReload($$1);
    }

    public int getCommandLimit() {
        return this.server.getGameRules().getInt(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH);
    }

    public CommandDispatcher<CommandSourceStack> getDispatcher() {
        return this.server.getCommands().getDispatcher();
    }

    public void tick() {
        if (this.postReload) {
            this.postReload = false;
            Collection<CommandFunction> $$0 = this.library.getTag(LOAD_FUNCTION_TAG);
            this.executeTagFunctions($$0, LOAD_FUNCTION_TAG);
        }
        this.executeTagFunctions((Collection<CommandFunction>)this.ticking, TICK_FUNCTION_TAG);
    }

    private void executeTagFunctions(Collection<CommandFunction> $$0, ResourceLocation $$1) {
        this.server.getProfiler().push((Supplier<String>)((Supplier)$$1::toString));
        for (CommandFunction $$2 : $$0) {
            this.execute($$2, this.getGameLoopSender());
        }
        this.server.getProfiler().pop();
    }

    public int execute(CommandFunction $$0, CommandSourceStack $$1) {
        return this.execute($$0, $$1, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int execute(CommandFunction $$0, CommandSourceStack $$1, @Nullable TraceCallbacks $$2) {
        if (this.context != null) {
            if ($$2 != null) {
                this.context.reportError(NO_RECURSIVE_TRACES.getString());
                return 0;
            }
            this.context.delayFunctionCall($$0, $$1);
            return 0;
        }
        try {
            this.context = new ExecutionContext($$2);
            int n = this.context.runTopCommand($$0, $$1);
            return n;
        }
        finally {
            this.context = null;
        }
    }

    public void replaceLibrary(ServerFunctionLibrary $$0) {
        this.library = $$0;
        this.postReload($$0);
    }

    private void postReload(ServerFunctionLibrary $$0) {
        this.ticking = ImmutableList.copyOf($$0.getTag(TICK_FUNCTION_TAG));
        this.postReload = true;
    }

    public CommandSourceStack getGameLoopSender() {
        return this.server.createCommandSourceStack().withPermission(2).withSuppressedOutput();
    }

    public Optional<CommandFunction> get(ResourceLocation $$0) {
        return this.library.getFunction($$0);
    }

    public Collection<CommandFunction> getTag(ResourceLocation $$0) {
        return this.library.getTag($$0);
    }

    public Iterable<ResourceLocation> getFunctionNames() {
        return this.library.getFunctions().keySet();
    }

    public Iterable<ResourceLocation> getTagNames() {
        return this.library.getAvailableTags();
    }

    public static interface TraceCallbacks {
        public void onCommand(int var1, String var2);

        public void onReturn(int var1, String var2, int var3);

        public void onError(int var1, String var2);

        public void onCall(int var1, ResourceLocation var2, int var3);
    }

    class ExecutionContext {
        private int depth;
        @Nullable
        private final TraceCallbacks tracer;
        private final Deque<QueuedCommand> commandQueue = Queues.newArrayDeque();
        private final List<QueuedCommand> nestedCalls = Lists.newArrayList();

        ExecutionContext(TraceCallbacks $$0) {
            this.tracer = $$0;
        }

        void delayFunctionCall(CommandFunction $$0, CommandSourceStack $$1) {
            int $$2 = ServerFunctionManager.this.getCommandLimit();
            if (this.commandQueue.size() + this.nestedCalls.size() < $$2) {
                this.nestedCalls.add((Object)new QueuedCommand($$1, this.depth, new CommandFunction.FunctionEntry($$0)));
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        int runTopCommand(CommandFunction $$0, CommandSourceStack $$1) {
            int $$2 = ServerFunctionManager.this.getCommandLimit();
            int $$3 = 0;
            CommandFunction.Entry[] $$4 = $$0.getEntries();
            for (int $$5 = $$4.length - 1; $$5 >= 0; --$$5) {
                this.commandQueue.push((Object)new QueuedCommand($$1, 0, $$4[$$5]));
            }
            while (!this.commandQueue.isEmpty()) {
                try {
                    QueuedCommand $$6 = (QueuedCommand)this.commandQueue.removeFirst();
                    ServerFunctionManager.this.server.getProfiler().push((Supplier<String>)((Supplier)$$6::toString));
                    this.depth = $$6.depth;
                    $$6.execute(ServerFunctionManager.this, this.commandQueue, $$2, this.tracer);
                    if (!this.nestedCalls.isEmpty()) {
                        Lists.reverse(this.nestedCalls).forEach(arg_0 -> this.commandQueue.addFirst(arg_0));
                        this.nestedCalls.clear();
                    }
                }
                finally {
                    ServerFunctionManager.this.server.getProfiler().pop();
                }
                if (++$$3 < $$2) continue;
                return $$3;
            }
            return $$3;
        }

        public void reportError(String $$0) {
            if (this.tracer != null) {
                this.tracer.onError(this.depth, $$0);
            }
        }
    }

    public static class QueuedCommand {
        private final CommandSourceStack sender;
        final int depth;
        private final CommandFunction.Entry entry;

        public QueuedCommand(CommandSourceStack $$0, int $$1, CommandFunction.Entry $$2) {
            this.sender = $$0;
            this.depth = $$1;
            this.entry = $$2;
        }

        public void execute(ServerFunctionManager $$0, Deque<QueuedCommand> $$1, int $$2, @Nullable TraceCallbacks $$3) {
            block4: {
                try {
                    this.entry.execute($$0, this.sender, $$1, $$2, this.depth, $$3);
                }
                catch (CommandSyntaxException $$4) {
                    if ($$3 != null) {
                        $$3.onError(this.depth, $$4.getRawMessage().getString());
                    }
                }
                catch (Exception $$5) {
                    if ($$3 == null) break block4;
                    $$3.onError(this.depth, $$5.getMessage());
                }
            }
        }

        public String toString() {
            return this.entry.toString();
        }
    }
}