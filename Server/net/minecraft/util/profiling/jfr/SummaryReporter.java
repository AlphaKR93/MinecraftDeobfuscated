/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.Throwable
 *  java.nio.file.Files
 *  java.nio.file.OpenOption
 *  java.nio.file.Path
 *  java.nio.file.StandardOpenOption
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 */
package net.minecraft.util.profiling.jfr;

import com.mojang.logging.LogUtils;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.profiling.jfr.parse.JfrStatsParser;
import net.minecraft.util.profiling.jfr.parse.JfrStatsResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

public class SummaryReporter {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Runnable onDeregistration;

    protected SummaryReporter(Runnable $$0) {
        this.onDeregistration = $$0;
    }

    /*
     * WARNING - void declaration
     */
    public void recordingStopped(@Nullable Path $$0) {
        if ($$0 == null) {
            return;
        }
        this.onDeregistration.run();
        SummaryReporter.infoWithFallback((Supplier<String>)((Supplier)() -> "Dumped flight recorder profiling to " + $$0));
        try {
            JfrStatsResult $$1 = JfrStatsParser.parse($$0);
        }
        catch (Throwable $$2) {
            SummaryReporter.warnWithFallback((Supplier<String>)((Supplier)() -> "Failed to parse JFR recording"), $$2);
            return;
        }
        try {
            void $$3;
            SummaryReporter.infoWithFallback((Supplier<String>)((Supplier)((JfrStatsResult)$$3)::asJson));
            Path $$4 = $$0.resolveSibling("jfr-report-" + StringUtils.substringBefore((String)$$0.getFileName().toString(), (String)".jfr") + ".json");
            Files.writeString((Path)$$4, (CharSequence)$$3.asJson(), (OpenOption[])new OpenOption[]{StandardOpenOption.CREATE});
            SummaryReporter.infoWithFallback((Supplier<String>)((Supplier)() -> "Dumped recording summary to " + $$4));
        }
        catch (Throwable $$5) {
            SummaryReporter.warnWithFallback((Supplier<String>)((Supplier)() -> "Failed to output JFR report"), $$5);
        }
    }

    private static void infoWithFallback(Supplier<String> $$0) {
        if (LogUtils.isLoggerActive()) {
            LOGGER.info((String)$$0.get());
        } else {
            Bootstrap.realStdoutPrintln((String)$$0.get());
        }
    }

    private static void warnWithFallback(Supplier<String> $$0, Throwable $$1) {
        if (LogUtils.isLoggerActive()) {
            LOGGER.warn((String)$$0.get(), $$1);
        } else {
            Bootstrap.realStdoutPrintln((String)$$0.get());
            $$1.printStackTrace(Bootstrap.STDOUT);
        }
    }
}