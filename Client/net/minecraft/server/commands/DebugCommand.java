/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.logging.LogUtils
 *  java.io.BufferedWriter
 *  java.io.IOException
 *  java.io.PrintWriter
 *  java.io.UncheckedIOException
 *  java.io.Writer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.nio.charset.Charset
 *  java.nio.charset.StandardCharsets
 *  java.nio.file.Files
 *  java.nio.file.OpenOption
 *  java.nio.file.Path
 *  java.nio.file.attribute.FileAttribute
 *  java.util.Collection
 *  java.util.Locale
 *  net.minecraft.server.MinecraftServer
 *  org.slf4j.Logger
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.logging.LogUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;
import java.util.Locale;
import net.minecraft.Util;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.commands.FunctionCommand;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.profiling.ProfileResults;
import org.slf4j.Logger;

public class DebugCommand {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final SimpleCommandExceptionType ERROR_NOT_RUNNING = new SimpleCommandExceptionType((Message)Component.translatable("commands.debug.notRunning"));
    private static final SimpleCommandExceptionType ERROR_ALREADY_RUNNING = new SimpleCommandExceptionType((Message)Component.translatable("commands.debug.alreadyRunning"));

    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("debug").requires($$0 -> $$0.hasPermission(3))).then(Commands.literal("start").executes($$0 -> DebugCommand.start((CommandSourceStack)$$0.getSource())))).then(Commands.literal("stop").executes($$0 -> DebugCommand.stop((CommandSourceStack)$$0.getSource())))).then(((LiteralArgumentBuilder)Commands.literal("function").requires($$0 -> $$0.hasPermission(3))).then(Commands.argument("name", FunctionArgument.functions()).suggests(FunctionCommand.SUGGEST_FUNCTION).executes($$0 -> DebugCommand.traceFunction((CommandSourceStack)$$0.getSource(), FunctionArgument.getFunctions((CommandContext<CommandSourceStack>)$$0, "name"))))));
    }

    private static int start(CommandSourceStack $$0) throws CommandSyntaxException {
        MinecraftServer $$1 = $$0.getServer();
        if ($$1.isTimeProfilerRunning()) {
            throw ERROR_ALREADY_RUNNING.create();
        }
        $$1.startTimeProfiler();
        $$0.sendSuccess(Component.translatable("commands.debug.started"), true);
        return 0;
    }

    private static int stop(CommandSourceStack $$0) throws CommandSyntaxException {
        MinecraftServer $$1 = $$0.getServer();
        if (!$$1.isTimeProfilerRunning()) {
            throw ERROR_NOT_RUNNING.create();
        }
        ProfileResults $$2 = $$1.stopTimeProfiler();
        double $$3 = (double)$$2.getNanoDuration() / (double)TimeUtil.NANOSECONDS_PER_SECOND;
        double $$4 = (double)$$2.getTickDuration() / $$3;
        $$0.sendSuccess(Component.translatable("commands.debug.stopped", String.format((Locale)Locale.ROOT, (String)"%.2f", (Object[])new Object[]{$$3}), $$2.getTickDuration(), String.format((Locale)Locale.ROOT, (String)"%.2f", (Object[])new Object[]{$$4})), true);
        return (int)$$4;
    }

    private static int traceFunction(CommandSourceStack $$0, Collection<CommandFunction> $$1) {
        int $$2 = 0;
        MinecraftServer $$3 = $$0.getServer();
        String $$4 = "debug-trace-" + Util.getFilenameFormattedDateTime() + ".txt";
        try {
            Path $$5 = $$3.getFile("debug").toPath();
            Files.createDirectories((Path)$$5, (FileAttribute[])new FileAttribute[0]);
            try (BufferedWriter $$6 = Files.newBufferedWriter((Path)$$5.resolve($$4), (Charset)StandardCharsets.UTF_8, (OpenOption[])new OpenOption[0]);){
                PrintWriter $$7 = new PrintWriter((Writer)$$6);
                for (CommandFunction $$8 : $$1) {
                    $$7.println((Object)$$8.getId());
                    Tracer $$9 = new Tracer($$7);
                    $$2 += $$0.getServer().getFunctions().execute($$8, $$0.withSource($$9).withMaximumPermission(2), $$9);
                }
            }
        }
        catch (IOException | UncheckedIOException $$10) {
            LOGGER.warn("Tracing failed", $$10);
            $$0.sendFailure(Component.translatable("commands.debug.function.traceFailed"));
        }
        if ($$1.size() == 1) {
            $$0.sendSuccess(Component.translatable("commands.debug.function.success.single", $$2, ((CommandFunction)$$1.iterator().next()).getId(), $$4), true);
        } else {
            $$0.sendSuccess(Component.translatable("commands.debug.function.success.multiple", $$2, $$1.size(), $$4), true);
        }
        return $$2;
    }

    static class Tracer
    implements ServerFunctionManager.TraceCallbacks,
    CommandSource {
        public static final int INDENT_OFFSET = 1;
        private final PrintWriter output;
        private int lastIndent;
        private boolean waitingForResult;

        Tracer(PrintWriter $$0) {
            this.output = $$0;
        }

        private void indentAndSave(int $$0) {
            this.printIndent($$0);
            this.lastIndent = $$0;
        }

        private void printIndent(int $$0) {
            for (int $$1 = 0; $$1 < $$0 + 1; ++$$1) {
                this.output.write("    ");
            }
        }

        private void newLine() {
            if (this.waitingForResult) {
                this.output.println();
                this.waitingForResult = false;
            }
        }

        @Override
        public void onCommand(int $$0, String $$1) {
            this.newLine();
            this.indentAndSave($$0);
            this.output.print("[C] ");
            this.output.print($$1);
            this.waitingForResult = true;
        }

        @Override
        public void onReturn(int $$0, String $$1, int $$2) {
            if (this.waitingForResult) {
                this.output.print(" -> ");
                this.output.println($$2);
                this.waitingForResult = false;
            } else {
                this.indentAndSave($$0);
                this.output.print("[R = ");
                this.output.print($$2);
                this.output.print("] ");
                this.output.println($$1);
            }
        }

        @Override
        public void onCall(int $$0, ResourceLocation $$1, int $$2) {
            this.newLine();
            this.indentAndSave($$0);
            this.output.print("[F] ");
            this.output.print((Object)$$1);
            this.output.print(" size=");
            this.output.println($$2);
        }

        @Override
        public void onError(int $$0, String $$1) {
            this.newLine();
            this.indentAndSave($$0 + 1);
            this.output.print("[E] ");
            this.output.print($$1);
        }

        @Override
        public void sendSystemMessage(Component $$0) {
            this.newLine();
            this.printIndent(this.lastIndent + 1);
            this.output.print("[M] ");
            this.output.println($$0.getString());
        }

        @Override
        public boolean acceptsSuccess() {
            return true;
        }

        @Override
        public boolean acceptsFailure() {
            return true;
        }

        @Override
        public boolean shouldInformAdmins() {
            return false;
        }

        @Override
        public boolean alwaysAccepts() {
            return true;
        }
    }
}