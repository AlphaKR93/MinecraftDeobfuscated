/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.logging.LogUtils
 *  java.io.File
 *  java.io.IOException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 *  java.nio.file.Path
 *  java.nio.file.Paths
 *  java.util.Locale
 *  java.util.function.Consumer
 *  org.apache.commons.io.FileUtils
 *  org.slf4j.Logger
 */
package net.minecraft.server.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.function.Consumer;
import net.minecraft.FileUtil;
import net.minecraft.SharedConstants;
import net.minecraft.SystemReport;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.FileZipper;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.profiling.EmptyProfileResults;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.metrics.storage.MetricsPersister;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

public class PerfCommand {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final SimpleCommandExceptionType ERROR_NOT_RUNNING = new SimpleCommandExceptionType((Message)Component.translatable("commands.perf.notRunning"));
    private static final SimpleCommandExceptionType ERROR_ALREADY_RUNNING = new SimpleCommandExceptionType((Message)Component.translatable("commands.perf.alreadyRunning"));

    public static void register(CommandDispatcher<CommandSourceStack> $$02) {
        $$02.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("perf").requires($$0 -> $$0.hasPermission(4))).then(Commands.literal("start").executes($$0 -> PerfCommand.startProfilingDedicatedServer((CommandSourceStack)$$0.getSource())))).then(Commands.literal("stop").executes($$0 -> PerfCommand.stopProfilingDedicatedServer((CommandSourceStack)$$0.getSource()))));
    }

    private static int startProfilingDedicatedServer(CommandSourceStack $$0) throws CommandSyntaxException {
        MinecraftServer $$12 = $$0.getServer();
        if ($$12.isRecordingMetrics()) {
            throw ERROR_ALREADY_RUNNING.create();
        }
        Consumer $$22 = $$1 -> PerfCommand.whenStopped($$0, $$1);
        Consumer $$3 = $$2 -> PerfCommand.saveResults($$0, $$2, $$12);
        $$12.startRecordingMetrics((Consumer<ProfileResults>)$$22, (Consumer<Path>)$$3);
        $$0.sendSuccess(Component.translatable("commands.perf.started"), false);
        return 0;
    }

    private static int stopProfilingDedicatedServer(CommandSourceStack $$0) throws CommandSyntaxException {
        MinecraftServer $$1 = $$0.getServer();
        if (!$$1.isRecordingMetrics()) {
            throw ERROR_NOT_RUNNING.create();
        }
        $$1.finishRecordingMetrics();
        return 0;
    }

    /*
     * WARNING - void declaration
     */
    private static void saveResults(CommandSourceStack $$0, Path $$1, MinecraftServer $$2) {
        void $$6;
        String $$3 = String.format((Locale)Locale.ROOT, (String)"%s-%s-%s", (Object[])new Object[]{Util.getFilenameFormattedDateTime(), $$2.getWorldData().getLevelName(), SharedConstants.getCurrentVersion().getId()});
        try {
            String $$4 = FileUtil.findAvailableName(MetricsPersister.PROFILING_RESULTS_DIR, $$3, ".zip");
        }
        catch (IOException $$5) {
            $$0.sendFailure(Component.translatable("commands.perf.reportFailed"));
            LOGGER.error("Failed to create report name", (Throwable)$$5);
            return;
        }
        try (FileZipper $$7 = new FileZipper(MetricsPersister.PROFILING_RESULTS_DIR.resolve((String)$$6));){
            $$7.add(Paths.get((String)"system.txt", (String[])new String[0]), $$2.fillSystemReport(new SystemReport()).toLineSeparatedString());
            $$7.add($$1);
        }
        try {
            FileUtils.forceDelete((File)$$1.toFile());
        }
        catch (IOException $$8) {
            LOGGER.warn("Failed to delete temporary profiling file {}", (Object)$$1, (Object)$$8);
        }
        $$0.sendSuccess(Component.translatable("commands.perf.reportSaved", $$6), false);
    }

    private static void whenStopped(CommandSourceStack $$0, ProfileResults $$1) {
        if ($$1 == EmptyProfileResults.EMPTY) {
            return;
        }
        int $$2 = $$1.getTickDuration();
        double $$3 = (double)$$1.getNanoDuration() / (double)TimeUtil.NANOSECONDS_PER_SECOND;
        $$0.sendSuccess(Component.translatable("commands.perf.stopped", String.format((Locale)Locale.ROOT, (String)"%.2f", (Object[])new Object[]{$$3}), $$2, String.format((Locale)Locale.ROOT, (String)"%.2f", (Object[])new Object[]{(double)$$2 / $$3})), false);
    }
}