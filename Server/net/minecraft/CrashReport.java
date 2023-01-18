/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.logging.LogUtils
 *  java.io.File
 *  java.io.FileOutputStream
 *  java.io.OutputStream
 *  java.io.OutputStreamWriter
 *  java.io.PrintWriter
 *  java.io.StringWriter
 *  java.io.Writer
 *  java.lang.NullPointerException
 *  java.lang.Object
 *  java.lang.OutOfMemoryError
 *  java.lang.StackOverflowError
 *  java.lang.StackTraceElement
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.System
 *  java.lang.Thread
 *  java.lang.Throwable
 *  java.nio.charset.StandardCharsets
 *  java.time.ZonedDateTime
 *  java.time.format.DateTimeFormatter
 *  java.time.temporal.TemporalAccessor
 *  java.util.List
 *  java.util.Locale
 *  java.util.concurrent.CompletionException
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.ArrayUtils
 *  org.slf4j.Logger
 */
package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletionException;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SystemReport;
import net.minecraft.Util;
import net.minecraft.util.MemoryReserve;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

public class CrashReport {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern((String)"yyyy-MM-dd HH:mm:ss", (Locale)Locale.ROOT);
    private final String title;
    private final Throwable exception;
    private final List<CrashReportCategory> details = Lists.newArrayList();
    private File saveFile;
    private boolean trackingStackTrace = true;
    private StackTraceElement[] uncategorizedStackTrace = new StackTraceElement[0];
    private final SystemReport systemReport = new SystemReport();

    public CrashReport(String $$0, Throwable $$1) {
        this.title = $$0;
        this.exception = $$1;
    }

    public String getTitle() {
        return this.title;
    }

    public Throwable getException() {
        return this.exception;
    }

    public String getDetails() {
        StringBuilder $$0 = new StringBuilder();
        this.getDetails($$0);
        return $$0.toString();
    }

    public void getDetails(StringBuilder $$0) {
        if (!(this.uncategorizedStackTrace != null && this.uncategorizedStackTrace.length > 0 || this.details.isEmpty())) {
            this.uncategorizedStackTrace = (StackTraceElement[])ArrayUtils.subarray((Object[])((CrashReportCategory)this.details.get(0)).getStacktrace(), (int)0, (int)1);
        }
        if (this.uncategorizedStackTrace != null && this.uncategorizedStackTrace.length > 0) {
            $$0.append("-- Head --\n");
            $$0.append("Thread: ").append(Thread.currentThread().getName()).append("\n");
            $$0.append("Stacktrace:\n");
            for (StackTraceElement $$1 : this.uncategorizedStackTrace) {
                $$0.append("\t").append("at ").append((Object)$$1);
                $$0.append("\n");
            }
            $$0.append("\n");
        }
        for (CrashReportCategory $$2 : this.details) {
            $$2.getDetails($$0);
            $$0.append("\n\n");
        }
        this.systemReport.appendToCrashReportString($$0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getExceptionMessage() {
        String string;
        StringWriter $$0 = null;
        PrintWriter $$1 = null;
        Throwable $$2 = this.exception;
        if ($$2.getMessage() == null) {
            if ($$2 instanceof NullPointerException) {
                $$2 = new NullPointerException(this.title);
            } else if ($$2 instanceof StackOverflowError) {
                $$2 = new StackOverflowError(this.title);
            } else if ($$2 instanceof OutOfMemoryError) {
                $$2 = new OutOfMemoryError(this.title);
            }
            $$2.setStackTrace(this.exception.getStackTrace());
        }
        try {
            $$0 = new StringWriter();
            $$1 = new PrintWriter((Writer)$$0);
            $$2.printStackTrace($$1);
            string = $$0.toString();
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly((Writer)$$0);
            IOUtils.closeQuietly($$1);
            throw throwable;
        }
        IOUtils.closeQuietly((Writer)$$0);
        IOUtils.closeQuietly((Writer)$$1);
        return string;
    }

    public String getFriendlyReport() {
        StringBuilder $$0 = new StringBuilder();
        $$0.append("---- Minecraft Crash Report ----\n");
        $$0.append("// ");
        $$0.append(CrashReport.getErrorComment());
        $$0.append("\n\n");
        $$0.append("Time: ");
        $$0.append(DATE_TIME_FORMATTER.format((TemporalAccessor)ZonedDateTime.now()));
        $$0.append("\n");
        $$0.append("Description: ");
        $$0.append(this.title);
        $$0.append("\n\n");
        $$0.append(this.getExceptionMessage());
        $$0.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");
        for (int $$1 = 0; $$1 < 87; ++$$1) {
            $$0.append("-");
        }
        $$0.append("\n\n");
        this.getDetails($$0);
        return $$0.toString();
    }

    public File getSaveFile() {
        return this.saveFile;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean saveToFile(File $$0) {
        boolean bl;
        if (this.saveFile != null) {
            return false;
        }
        if ($$0.getParentFile() != null) {
            $$0.getParentFile().mkdirs();
        }
        OutputStreamWriter $$1 = null;
        try {
            $$1 = new OutputStreamWriter((OutputStream)new FileOutputStream($$0), StandardCharsets.UTF_8);
            $$1.write(this.getFriendlyReport());
            this.saveFile = $$0;
            bl = true;
        }
        catch (Throwable $$2) {
            boolean bl2;
            try {
                LOGGER.error("Could not save crash report to {}", (Object)$$0, (Object)$$2);
                bl2 = false;
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly($$1);
                throw throwable;
            }
            IOUtils.closeQuietly((Writer)$$1);
            return bl2;
        }
        IOUtils.closeQuietly((Writer)$$1);
        return bl;
    }

    public SystemReport getSystemReport() {
        return this.systemReport;
    }

    public CrashReportCategory addCategory(String $$0) {
        return this.addCategory($$0, 1);
    }

    public CrashReportCategory addCategory(String $$0, int $$1) {
        CrashReportCategory $$2 = new CrashReportCategory($$0);
        if (this.trackingStackTrace) {
            int $$3 = $$2.fillInStackTrace($$1);
            StackTraceElement[] $$4 = this.exception.getStackTrace();
            StackTraceElement $$5 = null;
            StackTraceElement $$6 = null;
            int $$7 = $$4.length - $$3;
            if ($$7 < 0) {
                System.out.println("Negative index in crash report handler (" + $$4.length + "/" + $$3 + ")");
            }
            if ($$4 != null && 0 <= $$7 && $$7 < $$4.length) {
                $$5 = $$4[$$7];
                if ($$4.length + 1 - $$3 < $$4.length) {
                    $$6 = $$4[$$4.length + 1 - $$3];
                }
            }
            this.trackingStackTrace = $$2.validateStackTrace($$5, $$6);
            if ($$4 != null && $$4.length >= $$3 && 0 <= $$7 && $$7 < $$4.length) {
                this.uncategorizedStackTrace = new StackTraceElement[$$7];
                System.arraycopy((Object)$$4, (int)0, (Object)this.uncategorizedStackTrace, (int)0, (int)this.uncategorizedStackTrace.length);
            } else {
                this.trackingStackTrace = false;
            }
        }
        this.details.add((Object)$$2);
        return $$2;
    }

    private static String getErrorComment() {
        String[] $$0 = new String[]{"Who set us up the TNT?", "Everything's going to plan. No, really, that was supposed to happen.", "Uh... Did I do that?", "Oops.", "Why did you do that?", "I feel sad now :(", "My bad.", "I'm sorry, Dave.", "I let you down. Sorry :(", "On the bright side, I bought you a teddy bear!", "Daisy, daisy...", "Oh - I know what I did wrong!", "Hey, that tickles! Hehehe!", "I blame Dinnerbone.", "You should try our sister game, Minceraft!", "Don't be sad. I'll do better next time, I promise!", "Don't be sad, have a hug! <3", "I just don't know what went wrong :(", "Shall we play a game?", "Quite honestly, I wouldn't worry myself about that.", "I bet Cylons wouldn't have this problem.", "Sorry :(", "Surprise! Haha. Well, this is awkward.", "Would you like a cupcake?", "Hi. I'm Minecraft, and I'm a crashaholic.", "Ooh. Shiny.", "This doesn't make any sense!", "Why is it breaking :(", "Don't do that.", "Ouch. That hurt :(", "You're mean.", "This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]", "There are four lights!", "But it works on my machine."};
        try {
            return $$0[(int)(Util.getNanos() % (long)$$0.length)];
        }
        catch (Throwable $$1) {
            return "Witty comment unavailable :(";
        }
    }

    public static CrashReport forThrowable(Throwable $$0, String $$1) {
        CrashReport $$3;
        while ($$0 instanceof CompletionException && $$0.getCause() != null) {
            $$0 = $$0.getCause();
        }
        if ($$0 instanceof ReportedException) {
            CrashReport $$2 = ((ReportedException)((Object)$$0)).getReport();
        } else {
            $$3 = new CrashReport($$1, $$0);
        }
        return $$3;
    }

    public static void preload() {
        MemoryReserve.allocate();
        new CrashReport("Don't panic!", new Throwable()).getFriendlyReport();
    }
}