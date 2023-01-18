/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.util;

public class FrameTimer {
    public static final int LOGGING_LENGTH = 240;
    private final long[] loggedTimes = new long[240];
    private int logStart;
    private int logLength;
    private int logEnd;

    public void logFrameDuration(long $$0) {
        this.loggedTimes[this.logEnd] = $$0;
        ++this.logEnd;
        if (this.logEnd == 240) {
            this.logEnd = 0;
        }
        if (this.logLength < 240) {
            this.logStart = 0;
            ++this.logLength;
        } else {
            this.logStart = this.wrapIndex(this.logEnd + 1);
        }
    }

    public long getAverageDuration(int $$0) {
        int $$1 = (this.logStart + $$0) % 240;
        long $$3 = 0L;
        for (int $$2 = this.logStart; $$2 != $$1; ++$$2) {
            $$3 += this.loggedTimes[$$2];
        }
        return $$3 / (long)$$0;
    }

    public int scaleAverageDurationTo(int $$0, int $$1) {
        return this.scaleSampleTo(this.getAverageDuration($$0), $$1, 60);
    }

    public int scaleSampleTo(long $$0, int $$1, int $$2) {
        double $$3 = (double)$$0 / (double)(1000000000L / (long)$$2);
        return (int)($$3 * (double)$$1);
    }

    public int getLogStart() {
        return this.logStart;
    }

    public int getLogEnd() {
        return this.logEnd;
    }

    public int wrapIndex(int $$0) {
        return $$0 % 240;
    }

    public long[] getLog() {
        return this.loggedTimes;
    }
}