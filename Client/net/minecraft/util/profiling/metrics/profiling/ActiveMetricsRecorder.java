/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.nio.file.Path
 *  java.time.Instant
 *  java.util.Collection
 *  java.util.HashSet
 *  java.util.List
 *  java.util.Map
 *  java.util.Set
 *  java.util.concurrent.Executor
 *  java.util.concurrent.TimeUnit
 *  java.util.function.Consumer
 *  java.util.function.LongSupplier
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.util.profiling.metrics.profiling;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.util.profiling.ActiveProfiler;
import net.minecraft.util.profiling.ContinuousProfiler;
import net.minecraft.util.profiling.EmptyProfileResults;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.profiling.ProfileCollector;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.metrics.MetricSampler;
import net.minecraft.util.profiling.metrics.MetricsSamplerProvider;
import net.minecraft.util.profiling.metrics.profiling.MetricsRecorder;
import net.minecraft.util.profiling.metrics.storage.MetricsPersister;
import net.minecraft.util.profiling.metrics.storage.RecordedDeviation;

public class ActiveMetricsRecorder
implements MetricsRecorder {
    public static final int PROFILING_MAX_DURATION_SECONDS = 10;
    @Nullable
    private static Consumer<Path> globalOnReportFinished = null;
    private final Map<MetricSampler, List<RecordedDeviation>> deviationsBySampler = new Object2ObjectOpenHashMap();
    private final ContinuousProfiler taskProfiler;
    private final Executor ioExecutor;
    private final MetricsPersister metricsPersister;
    private final Consumer<ProfileResults> onProfilingEnd;
    private final Consumer<Path> onReportFinished;
    private final MetricsSamplerProvider metricsSamplerProvider;
    private final LongSupplier wallTimeSource;
    private final long deadlineNano;
    private int currentTick;
    private ProfileCollector singleTickProfiler;
    private volatile boolean killSwitch;
    private Set<MetricSampler> thisTickSamplers = ImmutableSet.of();

    private ActiveMetricsRecorder(MetricsSamplerProvider $$0, LongSupplier $$1, Executor $$2, MetricsPersister $$3, Consumer<ProfileResults> $$4, Consumer<Path> $$5) {
        this.metricsSamplerProvider = $$0;
        this.wallTimeSource = $$1;
        this.taskProfiler = new ContinuousProfiler($$1, () -> this.currentTick);
        this.ioExecutor = $$2;
        this.metricsPersister = $$3;
        this.onProfilingEnd = $$4;
        this.onReportFinished = globalOnReportFinished == null ? $$5 : $$5.andThen(globalOnReportFinished);
        this.deadlineNano = $$1.getAsLong() + TimeUnit.NANOSECONDS.convert(10L, TimeUnit.SECONDS);
        this.singleTickProfiler = new ActiveProfiler(this.wallTimeSource, () -> this.currentTick, false);
        this.taskProfiler.enable();
    }

    public static ActiveMetricsRecorder createStarted(MetricsSamplerProvider $$0, LongSupplier $$1, Executor $$2, MetricsPersister $$3, Consumer<ProfileResults> $$4, Consumer<Path> $$5) {
        return new ActiveMetricsRecorder($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public synchronized void end() {
        if (!this.isRecording()) {
            return;
        }
        this.killSwitch = true;
    }

    @Override
    public synchronized void cancel() {
        if (!this.isRecording()) {
            return;
        }
        this.singleTickProfiler = InactiveProfiler.INSTANCE;
        this.onProfilingEnd.accept((Object)EmptyProfileResults.EMPTY);
        this.cleanup((Collection<MetricSampler>)this.thisTickSamplers);
    }

    @Override
    public void startTick() {
        this.verifyStarted();
        this.thisTickSamplers = this.metricsSamplerProvider.samplers((Supplier<ProfileCollector>)((Supplier)() -> this.singleTickProfiler));
        for (MetricSampler $$0 : this.thisTickSamplers) {
            $$0.onStartTick();
        }
        ++this.currentTick;
    }

    @Override
    public void endTick() {
        this.verifyStarted();
        if (this.currentTick == 0) {
            return;
        }
        for (MetricSampler $$02 : this.thisTickSamplers) {
            $$02.onEndTick(this.currentTick);
            if (!$$02.triggersThreshold()) continue;
            RecordedDeviation $$1 = new RecordedDeviation(Instant.now(), this.currentTick, this.singleTickProfiler.getResults());
            ((List)this.deviationsBySampler.computeIfAbsent((Object)$$02, $$0 -> Lists.newArrayList())).add((Object)$$1);
        }
        if (this.killSwitch || this.wallTimeSource.getAsLong() > this.deadlineNano) {
            this.killSwitch = false;
            ProfileResults $$2 = this.taskProfiler.getResults();
            this.singleTickProfiler = InactiveProfiler.INSTANCE;
            this.onProfilingEnd.accept((Object)$$2);
            this.scheduleSaveResults($$2);
            return;
        }
        this.singleTickProfiler = new ActiveProfiler(this.wallTimeSource, () -> this.currentTick, false);
    }

    @Override
    public boolean isRecording() {
        return this.taskProfiler.isEnabled();
    }

    @Override
    public ProfilerFiller getProfiler() {
        return ProfilerFiller.tee(this.taskProfiler.getFiller(), this.singleTickProfiler);
    }

    private void verifyStarted() {
        if (!this.isRecording()) {
            throw new IllegalStateException("Not started!");
        }
    }

    private void scheduleSaveResults(ProfileResults $$0) {
        HashSet $$1 = new HashSet(this.thisTickSamplers);
        this.ioExecutor.execute(() -> {
            Path $$2 = this.metricsPersister.saveReports((Set<MetricSampler>)$$1, this.deviationsBySampler, $$0);
            this.cleanup((Collection<MetricSampler>)$$1);
            this.onReportFinished.accept((Object)$$2);
        });
    }

    private void cleanup(Collection<MetricSampler> $$0) {
        for (MetricSampler $$1 : $$0) {
            $$1.onFinished();
        }
        this.deviationsBySampler.clear();
        this.taskProfiler.disable();
    }

    public static void registerGlobalCompletionCallback(Consumer<Path> $$0) {
        globalOnReportFinished = $$0;
    }
}