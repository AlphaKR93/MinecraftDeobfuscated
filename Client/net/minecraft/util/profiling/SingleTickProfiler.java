/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.io.File
 *  java.lang.Object
 *  java.lang.String
 *  java.util.function.LongSupplier
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.util.profiling;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.util.function.LongSupplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.profiling.ActiveProfiler;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.profiling.ProfileCollector;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public class SingleTickProfiler {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final LongSupplier realTime;
    private final long saveThreshold;
    private int tick;
    private final File location;
    private ProfileCollector profiler = InactiveProfiler.INSTANCE;

    public SingleTickProfiler(LongSupplier $$0, String $$1, long $$2) {
        this.realTime = $$0;
        this.location = new File("debug", $$1);
        this.saveThreshold = $$2;
    }

    public ProfilerFiller startTick() {
        this.profiler = new ActiveProfiler(this.realTime, () -> this.tick, false);
        ++this.tick;
        return this.profiler;
    }

    public void endTick() {
        if (this.profiler == InactiveProfiler.INSTANCE) {
            return;
        }
        ProfileResults $$0 = this.profiler.getResults();
        this.profiler = InactiveProfiler.INSTANCE;
        if ($$0.getNanoDuration() >= this.saveThreshold) {
            File $$1 = new File(this.location, "tick-results-" + Util.getFilenameFormattedDateTime() + ".txt");
            $$0.saveResults($$1.toPath());
            LOGGER.info("Recorded long tick -- wrote info to: {}", (Object)$$1.getAbsolutePath());
        }
    }

    @Nullable
    public static SingleTickProfiler createTickProfiler(String $$0) {
        return null;
    }

    public static ProfilerFiller decorateFiller(ProfilerFiller $$0, @Nullable SingleTickProfiler $$1) {
        if ($$1 != null) {
            return ProfilerFiller.tee($$1.startTick(), $$0);
        }
        return $$0;
    }
}