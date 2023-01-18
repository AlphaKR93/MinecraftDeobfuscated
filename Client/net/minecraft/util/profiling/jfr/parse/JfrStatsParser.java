/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.util.Pair
 *  java.io.IOException
 *  java.io.UncheckedIOException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 *  java.nio.file.Path
 *  java.time.Duration
 *  java.time.Instant
 *  java.time.temporal.Temporal
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Map
 *  java.util.NoSuchElementException
 *  java.util.Spliterator
 *  java.util.Spliterators
 *  java.util.stream.Stream
 *  java.util.stream.StreamSupport
 *  javax.annotation.Nullable
 *  jdk.jfr.consumer.RecordedEvent
 *  jdk.jfr.consumer.RecordingFile
 */
package net.minecraft.util.profiling.jfr.parse;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;
import net.minecraft.util.profiling.jfr.parse.JfrStatsResult;
import net.minecraft.util.profiling.jfr.stats.ChunkGenStat;
import net.minecraft.util.profiling.jfr.stats.CpuLoadStat;
import net.minecraft.util.profiling.jfr.stats.FileIOStat;
import net.minecraft.util.profiling.jfr.stats.GcHeapStat;
import net.minecraft.util.profiling.jfr.stats.NetworkPacketSummary;
import net.minecraft.util.profiling.jfr.stats.ThreadAllocationStat;
import net.minecraft.util.profiling.jfr.stats.TickTimeStat;

public class JfrStatsParser {
    private Instant recordingStarted = Instant.EPOCH;
    private Instant recordingEnded = Instant.EPOCH;
    private final List<ChunkGenStat> chunkGenStats = Lists.newArrayList();
    private final List<CpuLoadStat> cpuLoadStat = Lists.newArrayList();
    private final Map<NetworkPacketSummary.PacketIdentification, MutableCountAndSize> receivedPackets = Maps.newHashMap();
    private final Map<NetworkPacketSummary.PacketIdentification, MutableCountAndSize> sentPackets = Maps.newHashMap();
    private final List<FileIOStat> fileWrites = Lists.newArrayList();
    private final List<FileIOStat> fileReads = Lists.newArrayList();
    private int garbageCollections;
    private Duration gcTotalDuration = Duration.ZERO;
    private final List<GcHeapStat> gcHeapStats = Lists.newArrayList();
    private final List<ThreadAllocationStat> threadAllocationStats = Lists.newArrayList();
    private final List<TickTimeStat> tickTimes = Lists.newArrayList();
    @Nullable
    private Duration worldCreationDuration = null;

    private JfrStatsParser(Stream<RecordedEvent> $$0) {
        this.capture($$0);
    }

    public static JfrStatsResult parse(Path $$0) {
        JfrStatsResult jfrStatsResult;
        final RecordingFile $$1 = new RecordingFile($$0);
        try {
            Iterator<RecordedEvent> $$2 = new Iterator<RecordedEvent>(){

                public boolean hasNext() {
                    return $$1.hasMoreEvents();
                }

                public RecordedEvent next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    try {
                        return $$1.readEvent();
                    }
                    catch (IOException $$0) {
                        throw new UncheckedIOException($$0);
                    }
                }
            };
            Stream $$3 = StreamSupport.stream((Spliterator)Spliterators.spliteratorUnknownSize((Iterator)$$2, (int)1297), (boolean)false);
            jfrStatsResult = new JfrStatsParser((Stream<RecordedEvent>)$$3).results();
        }
        catch (Throwable throwable) {
            try {
                try {
                    $$1.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException $$4) {
                throw new UncheckedIOException($$4);
            }
        }
        $$1.close();
        return jfrStatsResult;
    }

    private JfrStatsResult results() {
        Duration $$0 = Duration.between((Temporal)this.recordingStarted, (Temporal)this.recordingEnded);
        return new JfrStatsResult(this.recordingStarted, this.recordingEnded, $$0, this.worldCreationDuration, this.tickTimes, this.cpuLoadStat, GcHeapStat.summary($$0, this.gcHeapStats, this.gcTotalDuration, this.garbageCollections), ThreadAllocationStat.summary(this.threadAllocationStats), JfrStatsParser.collectPacketStats($$0, this.receivedPackets), JfrStatsParser.collectPacketStats($$0, this.sentPackets), FileIOStat.summary($$0, this.fileWrites), FileIOStat.summary($$0, this.fileReads), this.chunkGenStats);
    }

    private void capture(Stream<RecordedEvent> $$02) {
        $$02.forEach($$0 -> {
            /*
             * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
             * 
             * org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.SwitchStringRewriter$TooOptimisticMatchException
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.SwitchStringRewriter.getString(SwitchStringRewriter.java:404)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.SwitchStringRewriter.access$600(SwitchStringRewriter.java:53)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.SwitchStringRewriter$SwitchStringMatchResultCollector.collectMatches(SwitchStringRewriter.java:368)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.matchutil.ResetAfterTest.match(ResetAfterTest.java:24)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.matchutil.KleeneN.match(KleeneN.java:24)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.matchutil.MatchSequence.match(MatchSequence.java:26)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.matchutil.ResetAfterTest.match(ResetAfterTest.java:23)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.SwitchStringRewriter.rewriteComplex(SwitchStringRewriter.java:201)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.SwitchStringRewriter.rewrite(SwitchStringRewriter.java:73)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:881)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
             *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
             *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1050)
             *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
             *     at cuchaz.enigma.source.cfr.CfrSource.ensureDecompiled(CfrSource.java:81)
             *     at cuchaz.enigma.source.cfr.CfrSource.asString(CfrSource.java:50)
             *     at cuchaz.enigma.EnigmaProject$JarExport.decompileClass(EnigmaProject.java:298)
             *     at cuchaz.enigma.EnigmaProject$JarExport.lambda$decompileStream$1(EnigmaProject.java:274)
             *     at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:197)
             *     at java.base/java.util.AbstractList$RandomAccessSpliterator.forEachRemaining(AbstractList.java:722)
             *     at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:509)
             *     at java.base/java.util.stream.ForEachOps$ForEachTask.compute(ForEachOps.java:290)
             *     at java.base/java.util.concurrent.CountedCompleter.exec(CountedCompleter.java:754)
             *     at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:387)
             *     at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1311)
             *     at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1840)
             *     at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1806)
             *     at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:177)
             */
            throw new IllegalStateException("Decompilation failed");
        });
    }

    private void incrementPacket(RecordedEvent $$02, int $$1, Map<NetworkPacketSummary.PacketIdentification, MutableCountAndSize> $$2) {
        ((MutableCountAndSize)$$2.computeIfAbsent((Object)NetworkPacketSummary.PacketIdentification.from($$02), $$0 -> new MutableCountAndSize())).increment($$1);
    }

    private void appendFileIO(RecordedEvent $$0, List<FileIOStat> $$1, String $$2) {
        $$1.add((Object)new FileIOStat($$0.getDuration(), $$0.getString("path"), $$0.getLong($$2)));
    }

    private static NetworkPacketSummary collectPacketStats(Duration $$02, Map<NetworkPacketSummary.PacketIdentification, MutableCountAndSize> $$1) {
        List $$2 = $$1.entrySet().stream().map($$0 -> Pair.of((Object)((Object)((NetworkPacketSummary.PacketIdentification)((Object)((Object)$$0.getKey())))), (Object)((Object)((MutableCountAndSize)$$0.getValue()).toCountAndSize()))).toList();
        return new NetworkPacketSummary($$02, (List<Pair<NetworkPacketSummary.PacketIdentification, NetworkPacketSummary.PacketCountAndSize>>)$$2);
    }

    public static final class MutableCountAndSize {
        private long count;
        private long totalSize;

        public void increment(int $$0) {
            this.totalSize += (long)$$0;
            ++this.count;
        }

        public NetworkPacketSummary.PacketCountAndSize toCountAndSize() {
            return new NetworkPacketSummary.PacketCountAndSize(this.count, this.totalSize);
        }
    }
}