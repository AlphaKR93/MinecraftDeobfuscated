/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Object
 *  java.lang.StackTraceElement
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.System
 *  java.lang.Thread
 *  java.lang.Throwable
 *  java.util.List
 *  java.util.Locale
 *  javax.annotation.Nullable
 */
package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.CrashReportDetail;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class CrashReportCategory {
    private final String title;
    private final List<Entry> entries = Lists.newArrayList();
    private StackTraceElement[] stackTrace = new StackTraceElement[0];

    public CrashReportCategory(String $$0) {
        this.title = $$0;
    }

    public static String formatLocation(LevelHeightAccessor $$0, double $$1, double $$2, double $$3) {
        return String.format((Locale)Locale.ROOT, (String)"%.2f,%.2f,%.2f - %s", (Object[])new Object[]{$$1, $$2, $$3, CrashReportCategory.formatLocation($$0, new BlockPos($$1, $$2, $$3))});
    }

    public static String formatLocation(LevelHeightAccessor $$0, BlockPos $$1) {
        return CrashReportCategory.formatLocation($$0, $$1.getX(), $$1.getY(), $$1.getZ());
    }

    public static String formatLocation(LevelHeightAccessor $$0, int $$1, int $$2, int $$3) {
        StringBuilder $$4 = new StringBuilder();
        try {
            $$4.append(String.format((Locale)Locale.ROOT, (String)"World: (%d,%d,%d)", (Object[])new Object[]{$$1, $$2, $$3}));
        }
        catch (Throwable $$5) {
            $$4.append("(Error finding world loc)");
        }
        $$4.append(", ");
        try {
            int $$6 = SectionPos.blockToSectionCoord($$1);
            int $$7 = SectionPos.blockToSectionCoord($$2);
            int $$8 = SectionPos.blockToSectionCoord($$3);
            int $$9 = $$1 & 0xF;
            int $$10 = $$2 & 0xF;
            int $$11 = $$3 & 0xF;
            int $$12 = SectionPos.sectionToBlockCoord($$6);
            int $$13 = $$0.getMinBuildHeight();
            int $$14 = SectionPos.sectionToBlockCoord($$8);
            int $$15 = SectionPos.sectionToBlockCoord($$6 + 1) - 1;
            int $$16 = $$0.getMaxBuildHeight() - 1;
            int $$17 = SectionPos.sectionToBlockCoord($$8 + 1) - 1;
            $$4.append(String.format((Locale)Locale.ROOT, (String)"Section: (at %d,%d,%d in %d,%d,%d; chunk contains blocks %d,%d,%d to %d,%d,%d)", (Object[])new Object[]{$$9, $$10, $$11, $$6, $$7, $$8, $$12, $$13, $$14, $$15, $$16, $$17}));
        }
        catch (Throwable $$18) {
            $$4.append("(Error finding chunk loc)");
        }
        $$4.append(", ");
        try {
            int $$19 = $$1 >> 9;
            int $$20 = $$3 >> 9;
            int $$21 = $$19 << 5;
            int $$22 = $$20 << 5;
            int $$23 = ($$19 + 1 << 5) - 1;
            int $$24 = ($$20 + 1 << 5) - 1;
            int $$25 = $$19 << 9;
            int $$26 = $$0.getMinBuildHeight();
            int $$27 = $$20 << 9;
            int $$28 = ($$19 + 1 << 9) - 1;
            int $$29 = $$0.getMaxBuildHeight() - 1;
            int $$30 = ($$20 + 1 << 9) - 1;
            $$4.append(String.format((Locale)Locale.ROOT, (String)"Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,%d,%d to %d,%d,%d)", (Object[])new Object[]{$$19, $$20, $$21, $$22, $$23, $$24, $$25, $$26, $$27, $$28, $$29, $$30}));
        }
        catch (Throwable $$31) {
            $$4.append("(Error finding world loc)");
        }
        return $$4.toString();
    }

    public CrashReportCategory setDetail(String $$0, CrashReportDetail<String> $$1) {
        try {
            this.setDetail($$0, $$1.call());
        }
        catch (Throwable $$2) {
            this.setDetailError($$0, $$2);
        }
        return this;
    }

    public CrashReportCategory setDetail(String $$0, Object $$1) {
        this.entries.add((Object)new Entry($$0, $$1));
        return this;
    }

    public void setDetailError(String $$0, Throwable $$1) {
        this.setDetail($$0, (Object)$$1);
    }

    public int fillInStackTrace(int $$0) {
        StackTraceElement[] $$1 = Thread.currentThread().getStackTrace();
        if ($$1.length <= 0) {
            return 0;
        }
        this.stackTrace = new StackTraceElement[$$1.length - 3 - $$0];
        System.arraycopy((Object)$$1, (int)(3 + $$0), (Object)this.stackTrace, (int)0, (int)this.stackTrace.length);
        return this.stackTrace.length;
    }

    public boolean validateStackTrace(StackTraceElement $$0, StackTraceElement $$1) {
        if (this.stackTrace.length == 0 || $$0 == null) {
            return false;
        }
        StackTraceElement $$2 = this.stackTrace[0];
        if (!($$2.isNativeMethod() == $$0.isNativeMethod() && $$2.getClassName().equals((Object)$$0.getClassName()) && $$2.getFileName().equals((Object)$$0.getFileName()) && $$2.getMethodName().equals((Object)$$0.getMethodName()))) {
            return false;
        }
        if ($$1 != null != this.stackTrace.length > 1) {
            return false;
        }
        if ($$1 != null && !this.stackTrace[1].equals((Object)$$1)) {
            return false;
        }
        this.stackTrace[0] = $$0;
        return true;
    }

    public void trimStacktrace(int $$0) {
        StackTraceElement[] $$1 = new StackTraceElement[this.stackTrace.length - $$0];
        System.arraycopy((Object)this.stackTrace, (int)0, (Object)$$1, (int)0, (int)$$1.length);
        this.stackTrace = $$1;
    }

    public void getDetails(StringBuilder $$0) {
        $$0.append("-- ").append(this.title).append(" --\n");
        $$0.append("Details:");
        for (Entry $$1 : this.entries) {
            $$0.append("\n\t");
            $$0.append($$1.getKey());
            $$0.append(": ");
            $$0.append($$1.getValue());
        }
        if (this.stackTrace != null && this.stackTrace.length > 0) {
            $$0.append("\nStacktrace:");
            for (StackTraceElement $$2 : this.stackTrace) {
                $$0.append("\n\tat ");
                $$0.append((Object)$$2);
            }
        }
    }

    public StackTraceElement[] getStacktrace() {
        return this.stackTrace;
    }

    public static void populateBlockDetails(CrashReportCategory $$0, LevelHeightAccessor $$1, BlockPos $$2, @Nullable BlockState $$3) {
        if ($$3 != null) {
            $$0.setDetail("Block", $$3::toString);
        }
        $$0.setDetail("Block location", () -> CrashReportCategory.formatLocation($$1, $$2));
    }

    static class Entry {
        private final String key;
        private final String value;

        public Entry(String $$0, @Nullable Object $$1) {
            this.key = $$0;
            if ($$1 == null) {
                this.value = "~~NULL~~";
            } else if ($$1 instanceof Throwable) {
                Throwable $$2 = (Throwable)$$1;
                this.value = "~~ERROR~~ " + $$2.getClass().getSimpleName() + ": " + $$2.getMessage();
            } else {
                this.value = $$1.toString();
            }
        }

        public String getKey() {
            return this.key;
        }

        public String getValue() {
            return this.value;
        }
    }
}