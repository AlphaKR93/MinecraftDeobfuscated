/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.io.BufferedWriter
 *  java.io.IOException
 *  java.io.UncheckedIOException
 *  java.io.Writer
 *  java.lang.Exception
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.String
 *  java.nio.charset.Charset
 *  java.nio.charset.StandardCharsets
 *  java.nio.file.Files
 *  java.nio.file.OpenOption
 *  java.nio.file.Path
 *  java.nio.file.Paths
 *  java.nio.file.attribute.FileAttribute
 *  java.time.ZoneId
 *  java.time.format.DateTimeFormatter
 *  java.time.temporal.TemporalAccessor
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Set
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 */
package net.minecraft.util.profiling.metrics.storage;

import com.mojang.logging.LogUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CsvOutput;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.util.profiling.metrics.MetricSampler;
import net.minecraft.util.profiling.metrics.storage.RecordedDeviation;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class MetricsPersister {
    public static final Path PROFILING_RESULTS_DIR = Paths.get((String)"debug/profiling", (String[])new String[0]);
    public static final String METRICS_DIR_NAME = "metrics";
    public static final String DEVIATIONS_DIR_NAME = "deviations";
    public static final String PROFILING_RESULT_FILENAME = "profiling.txt";
    private static final Logger LOGGER = LogUtils.getLogger();
    private final String rootFolderName;

    public MetricsPersister(String $$0) {
        this.rootFolderName = $$0;
    }

    public Path saveReports(Set<MetricSampler> $$0, Map<MetricSampler, List<RecordedDeviation>> $$1, ProfileResults $$2) {
        try {
            Files.createDirectories((Path)PROFILING_RESULTS_DIR, (FileAttribute[])new FileAttribute[0]);
        }
        catch (IOException $$3) {
            throw new UncheckedIOException($$3);
        }
        try {
            Path $$4 = Files.createTempDirectory((String)"minecraft-profiling", (FileAttribute[])new FileAttribute[0]);
            $$4.toFile().deleteOnExit();
            Files.createDirectories((Path)PROFILING_RESULTS_DIR, (FileAttribute[])new FileAttribute[0]);
            Path $$5 = $$4.resolve(this.rootFolderName);
            Path $$6 = $$5.resolve(METRICS_DIR_NAME);
            this.saveMetrics($$0, $$6);
            if (!$$1.isEmpty()) {
                this.saveDeviations($$1, $$5.resolve(DEVIATIONS_DIR_NAME));
            }
            this.saveProfilingTaskExecutionResult($$2, $$5);
            return $$4;
        }
        catch (IOException $$7) {
            throw new UncheckedIOException($$7);
        }
    }

    private void saveMetrics(Set<MetricSampler> $$0, Path $$12) {
        if ($$0.isEmpty()) {
            throw new IllegalArgumentException("Expected at least one sampler to persist");
        }
        Map $$22 = (Map)$$0.stream().collect(Collectors.groupingBy(MetricSampler::getCategory));
        $$22.forEach(($$1, $$2) -> this.saveCategory((MetricCategory)((Object)$$1), (List<MetricSampler>)$$2, $$12));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void saveCategory(MetricCategory $$0, List<MetricSampler> $$12, Path $$2) {
        Path $$3 = $$2.resolve(Util.sanitizeName($$0.getDescription(), ResourceLocation::validPathChar) + ".csv");
        BufferedWriter $$4 = null;
        try {
            Files.createDirectories((Path)$$3.getParent(), (FileAttribute[])new FileAttribute[0]);
            $$4 = Files.newBufferedWriter((Path)$$3, (Charset)StandardCharsets.UTF_8, (OpenOption[])new OpenOption[0]);
            CsvOutput.Builder $$5 = CsvOutput.builder();
            $$5.addColumn("@tick");
            for (MetricSampler $$6 : $$12) {
                $$5.addColumn($$6.getName());
            }
            CsvOutput $$7 = $$5.build((Writer)$$4);
            List $$8 = (List)$$12.stream().map(MetricSampler::result).collect(Collectors.toList());
            int $$9 = $$8.stream().mapToInt(MetricSampler.SamplerResult::getFirstTick).summaryStatistics().getMin();
            int $$10 = $$8.stream().mapToInt(MetricSampler.SamplerResult::getLastTick).summaryStatistics().getMax();
            for (int $$11 = $$9; $$11 <= $$10; ++$$11) {
                int $$122 = $$11;
                Stream $$13 = $$8.stream().map($$1 -> String.valueOf((double)$$1.valueAtTick($$122)));
                Object[] $$14 = Stream.concat((Stream)Stream.of((Object)String.valueOf((int)$$11)), (Stream)$$13).toArray(String[]::new);
                $$7.writeRow($$14);
            }
            LOGGER.info("Flushed metrics to {}", (Object)$$3);
            IOUtils.closeQuietly((Writer)$$4);
        }
        catch (Exception $$15) {
            LOGGER.error("Could not save profiler results to {}", (Object)$$3, (Object)$$15);
        }
        finally {
            IOUtils.closeQuietly($$4);
        }
    }

    private void saveDeviations(Map<MetricSampler, List<RecordedDeviation>> $$0, Path $$1) {
        DateTimeFormatter $$22 = DateTimeFormatter.ofPattern((String)"yyyy-MM-dd_HH.mm.ss.SSS", (Locale)Locale.UK).withZone(ZoneId.systemDefault());
        $$0.forEach(($$2, $$32) -> $$32.forEach($$3 -> {
            String $$4 = $$22.format((TemporalAccessor)$$3.timestamp);
            Path $$5 = $$1.resolve(Util.sanitizeName($$2.getName(), ResourceLocation::validPathChar)).resolve(String.format((Locale)Locale.ROOT, (String)"%d@%s.txt", (Object[])new Object[]{$$3.tick, $$4}));
            $$3.profilerResultAtTick.saveResults($$5);
        }));
    }

    private void saveProfilingTaskExecutionResult(ProfileResults $$0, Path $$1) {
        $$0.saveResults($$1.resolve(PROFILING_RESULT_FILENAME));
    }
}