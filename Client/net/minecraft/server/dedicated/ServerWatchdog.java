/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Streams
 *  com.mojang.logging.LogUtils
 *  java.io.File
 *  java.lang.CharSequence
 *  java.lang.Error
 *  java.lang.Float
 *  java.lang.InterruptedException
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.Runtime
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.System
 *  java.lang.Thread
 *  java.lang.Throwable
 *  java.lang.management.ManagementFactory
 *  java.lang.management.ThreadInfo
 *  java.lang.management.ThreadMXBean
 *  java.util.Locale
 *  java.util.Timer
 *  java.util.TimerTask
 *  java.util.stream.Collectors
 *  org.slf4j.Logger
 */
package net.minecraft.server.dedicated;

import com.google.common.collect.Streams;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.Util;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.level.GameRules;
import org.slf4j.Logger;

public class ServerWatchdog
implements Runnable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final long MAX_SHUTDOWN_TIME = 10000L;
    private static final int SHUTDOWN_STATUS = 1;
    private final DedicatedServer server;
    private final long maxTickTime;

    public ServerWatchdog(DedicatedServer $$0) {
        this.server = $$0;
        this.maxTickTime = $$0.getMaxTickLength();
    }

    public void run() {
        while (this.server.isRunning()) {
            long $$0 = this.server.getNextTickTime();
            long $$1 = Util.getMillis();
            long $$2 = $$1 - $$0;
            if ($$2 > this.maxTickTime) {
                LOGGER.error(LogUtils.FATAL_MARKER, "A single server tick took {} seconds (should be max {})", (Object)String.format((Locale)Locale.ROOT, (String)"%.2f", (Object[])new Object[]{Float.valueOf((float)((float)$$2 / 1000.0f))}), (Object)String.format((Locale)Locale.ROOT, (String)"%.2f", (Object[])new Object[]{Float.valueOf((float)0.05f)}));
                LOGGER.error(LogUtils.FATAL_MARKER, "Considering it to be crashed, server will forcibly shutdown.");
                ThreadMXBean $$3 = ManagementFactory.getThreadMXBean();
                ThreadInfo[] $$4 = $$3.dumpAllThreads(true, true);
                StringBuilder $$5 = new StringBuilder();
                Error $$6 = new Error("Watchdog");
                for (ThreadInfo $$7 : $$4) {
                    if ($$7.getThreadId() == this.server.getRunningThread().getId()) {
                        $$6.setStackTrace($$7.getStackTrace());
                    }
                    $$5.append((Object)$$7);
                    $$5.append("\n");
                }
                CrashReport $$8 = new CrashReport("Watching Server", (Throwable)$$6);
                this.server.fillSystemReport($$8.getSystemReport());
                CrashReportCategory $$9 = $$8.addCategory("Thread Dump");
                $$9.setDetail("Threads", $$5);
                CrashReportCategory $$10 = $$8.addCategory("Performance stats");
                $$10.setDetail("Random tick rate", () -> this.server.getWorldData().getGameRules().getRule(GameRules.RULE_RANDOMTICKING).toString());
                $$10.setDetail("Level stats", () -> (String)Streams.stream(this.server.getAllLevels()).map($$0 -> $$0.dimension() + ": " + $$0.getWatchdogStats()).collect(Collectors.joining((CharSequence)",\n")));
                Bootstrap.realStdoutPrintln("Crash report:\n" + $$8.getFriendlyReport());
                File $$11 = new File(new File(this.server.getServerDirectory(), "crash-reports"), "crash-" + Util.getFilenameFormattedDateTime() + "-server.txt");
                if ($$8.saveToFile($$11)) {
                    LOGGER.error("This crash report has been saved to: {}", (Object)$$11.getAbsolutePath());
                } else {
                    LOGGER.error("We were unable to save this crash report to disk.");
                }
                this.exit();
            }
            try {
                Thread.sleep((long)($$0 + this.maxTickTime - $$1));
            }
            catch (InterruptedException interruptedException) {}
        }
    }

    private void exit() {
        try {
            Timer $$0 = new Timer();
            $$0.schedule(new TimerTask(){

                public void run() {
                    Runtime.getRuntime().halt(1);
                }
            }, 10000L);
            System.exit((int)1);
        }
        catch (Throwable $$1) {
            Runtime.getRuntime().halt(1);
        }
    }
}