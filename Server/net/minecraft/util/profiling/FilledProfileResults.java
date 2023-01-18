/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2LongMap
 *  it.unimi.dsi.fastutil.objects.Object2LongMaps
 *  java.io.BufferedWriter
 *  java.io.Writer
 *  java.lang.CharSequence
 *  java.lang.Exception
 *  java.lang.Float
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.lang.Throwable
 *  java.nio.charset.Charset
 *  java.nio.charset.StandardCharsets
 *  java.nio.file.Files
 *  java.nio.file.OpenOption
 *  java.nio.file.Path
 *  java.nio.file.attribute.FileAttribute
 *  java.util.ArrayList
 *  java.util.Collections
 *  java.util.Comparator
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.TreeMap
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.ObjectUtils
 *  org.slf4j.Logger
 */
package net.minecraft.util.profiling;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import java.io.BufferedWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.ProfilerPathEntry;
import net.minecraft.util.profiling.ResultField;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;

public class FilledProfileResults
implements ProfileResults {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ProfilerPathEntry EMPTY = new ProfilerPathEntry(){

        @Override
        public long getDuration() {
            return 0L;
        }

        @Override
        public long getMaxDuration() {
            return 0L;
        }

        @Override
        public long getCount() {
            return 0L;
        }

        @Override
        public Object2LongMap<String> getCounters() {
            return Object2LongMaps.emptyMap();
        }
    };
    private static final Splitter SPLITTER = Splitter.on((char)'\u001e');
    private static final Comparator<Map.Entry<String, CounterCollector>> COUNTER_ENTRY_COMPARATOR = Map.Entry.comparingByValue((Comparator)Comparator.comparingLong($$0 -> $$0.totalValue)).reversed();
    private final Map<String, ? extends ProfilerPathEntry> entries;
    private final long startTimeNano;
    private final int startTimeTicks;
    private final long endTimeNano;
    private final int endTimeTicks;
    private final int tickDuration;

    public FilledProfileResults(Map<String, ? extends ProfilerPathEntry> $$0, long $$1, int $$2, long $$3, int $$4) {
        this.entries = $$0;
        this.startTimeNano = $$1;
        this.startTimeTicks = $$2;
        this.endTimeNano = $$3;
        this.endTimeTicks = $$4;
        this.tickDuration = $$4 - $$2;
    }

    private ProfilerPathEntry getEntry(String $$0) {
        ProfilerPathEntry $$1 = (ProfilerPathEntry)this.entries.get((Object)$$0);
        return $$1 != null ? $$1 : EMPTY;
    }

    @Override
    public List<ResultField> getTimes(String $$0) {
        String $$1 = $$0;
        ProfilerPathEntry $$2 = this.getEntry("root");
        long $$3 = $$2.getDuration();
        ProfilerPathEntry $$4 = this.getEntry($$0);
        long $$5 = $$4.getDuration();
        long $$6 = $$4.getCount();
        ArrayList $$7 = Lists.newArrayList();
        if (!$$0.isEmpty()) {
            $$0 = $$0 + "\u001e";
        }
        long $$8 = 0L;
        for (String $$9 : this.entries.keySet()) {
            if (!FilledProfileResults.isDirectChild($$0, $$9)) continue;
            $$8 += this.getEntry($$9).getDuration();
        }
        float $$10 = $$8;
        if ($$8 < $$5) {
            $$8 = $$5;
        }
        if ($$3 < $$8) {
            $$3 = $$8;
        }
        for (String $$11 : this.entries.keySet()) {
            if (!FilledProfileResults.isDirectChild($$0, $$11)) continue;
            ProfilerPathEntry $$12 = this.getEntry($$11);
            long $$13 = $$12.getDuration();
            double $$14 = (double)$$13 * 100.0 / (double)$$8;
            double $$15 = (double)$$13 * 100.0 / (double)$$3;
            String $$16 = $$11.substring($$0.length());
            $$7.add((Object)new ResultField($$16, $$14, $$15, $$12.getCount()));
        }
        if ((float)$$8 > $$10) {
            $$7.add((Object)new ResultField("unspecified", (double)((float)$$8 - $$10) * 100.0 / (double)$$8, (double)((float)$$8 - $$10) * 100.0 / (double)$$3, $$6));
        }
        Collections.sort((List)$$7);
        $$7.add(0, (Object)new ResultField($$1, 100.0, (double)$$8 * 100.0 / (double)$$3, $$6));
        return $$7;
    }

    private static boolean isDirectChild(String $$0, String $$1) {
        return $$1.length() > $$0.length() && $$1.startsWith($$0) && $$1.indexOf(30, $$0.length() + 1) < 0;
    }

    private Map<String, CounterCollector> getCounterValues() {
        TreeMap $$0 = Maps.newTreeMap();
        this.entries.forEach((arg_0, arg_1) -> FilledProfileResults.lambda$getCounterValues$3((Map)$$0, arg_0, arg_1));
        return $$0;
    }

    @Override
    public long getStartTimeNano() {
        return this.startTimeNano;
    }

    @Override
    public int getStartTimeTicks() {
        return this.startTimeTicks;
    }

    @Override
    public long getEndTimeNano() {
        return this.endTimeNano;
    }

    @Override
    public int getEndTimeTicks() {
        return this.endTimeTicks;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean saveResults(Path $$0) {
        boolean bl;
        BufferedWriter $$1 = null;
        try {
            Files.createDirectories((Path)$$0.getParent(), (FileAttribute[])new FileAttribute[0]);
            $$1 = Files.newBufferedWriter((Path)$$0, (Charset)StandardCharsets.UTF_8, (OpenOption[])new OpenOption[0]);
            $$1.write(this.getProfilerResults(this.getNanoDuration(), this.getTickDuration()));
            bl = true;
        }
        catch (Throwable $$2) {
            boolean bl2;
            try {
                LOGGER.error("Could not save profiler results to {}", (Object)$$0, (Object)$$2);
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

    protected String getProfilerResults(long $$0, int $$1) {
        StringBuilder $$2 = new StringBuilder();
        $$2.append("---- Minecraft Profiler Results ----\n");
        $$2.append("// ");
        $$2.append(FilledProfileResults.getComment());
        $$2.append("\n\n");
        $$2.append("Version: ").append(SharedConstants.getCurrentVersion().getId()).append('\n');
        $$2.append("Time span: ").append($$0 / 1000000L).append(" ms\n");
        $$2.append("Tick span: ").append($$1).append(" ticks\n");
        $$2.append("// This is approximately ").append(String.format((Locale)Locale.ROOT, (String)"%.2f", (Object[])new Object[]{Float.valueOf((float)((float)$$1 / ((float)$$0 / 1.0E9f)))})).append(" ticks per second. It should be ").append(20).append(" ticks per second\n\n");
        $$2.append("--- BEGIN PROFILE DUMP ---\n\n");
        this.appendProfilerResults(0, "root", $$2);
        $$2.append("--- END PROFILE DUMP ---\n\n");
        Map<String, CounterCollector> $$3 = this.getCounterValues();
        if (!$$3.isEmpty()) {
            $$2.append("--- BEGIN COUNTER DUMP ---\n\n");
            this.appendCounters($$3, $$2, $$1);
            $$2.append("--- END COUNTER DUMP ---\n\n");
        }
        return $$2.toString();
    }

    @Override
    public String getProfilerResults() {
        StringBuilder $$0 = new StringBuilder();
        this.appendProfilerResults(0, "root", $$0);
        return $$0.toString();
    }

    private static StringBuilder indentLine(StringBuilder $$0, int $$1) {
        $$0.append(String.format((Locale)Locale.ROOT, (String)"[%02d] ", (Object[])new Object[]{$$1}));
        for (int $$2 = 0; $$2 < $$1; ++$$2) {
            $$0.append("|   ");
        }
        return $$0;
    }

    private void appendProfilerResults(int $$0, String $$1, StringBuilder $$22) {
        List<ResultField> $$32 = this.getTimes($$1);
        Object2LongMap<String> $$4 = ((ProfilerPathEntry)ObjectUtils.firstNonNull((Object[])new ProfilerPathEntry[]{(ProfilerPathEntry)this.entries.get((Object)$$1), EMPTY})).getCounters();
        $$4.forEach(($$2, $$3) -> FilledProfileResults.indentLine($$22, $$0).append('#').append($$2).append(' ').append($$3).append('/').append($$3 / (long)this.tickDuration).append('\n'));
        if ($$32.size() < 3) {
            return;
        }
        for (int $$5 = 1; $$5 < $$32.size(); ++$$5) {
            ResultField $$6 = (ResultField)$$32.get($$5);
            FilledProfileResults.indentLine($$22, $$0).append($$6.name).append('(').append($$6.count).append('/').append(String.format((Locale)Locale.ROOT, (String)"%.0f", (Object[])new Object[]{Float.valueOf((float)((float)$$6.count / (float)this.tickDuration))})).append(')').append(" - ").append(String.format((Locale)Locale.ROOT, (String)"%.2f", (Object[])new Object[]{$$6.percentage})).append("%/").append(String.format((Locale)Locale.ROOT, (String)"%.2f", (Object[])new Object[]{$$6.globalPercentage})).append("%\n");
            if ("unspecified".equals((Object)$$6.name)) continue;
            try {
                this.appendProfilerResults($$0 + 1, $$1 + "\u001e" + $$6.name, $$22);
                continue;
            }
            catch (Exception $$7) {
                $$22.append("[[ EXCEPTION ").append((Object)$$7).append(" ]]");
            }
        }
    }

    private void appendCounterResults(int $$0, String $$1, CounterCollector $$2, int $$32, StringBuilder $$4) {
        FilledProfileResults.indentLine($$4, $$0).append($$1).append(" total:").append($$2.selfValue).append('/').append($$2.totalValue).append(" average: ").append($$2.selfValue / (long)$$32).append('/').append($$2.totalValue / (long)$$32).append('\n');
        $$2.children.entrySet().stream().sorted(COUNTER_ENTRY_COMPARATOR).forEach($$3 -> this.appendCounterResults($$0 + 1, (String)$$3.getKey(), (CounterCollector)$$3.getValue(), $$32, $$4));
    }

    private void appendCounters(Map<String, CounterCollector> $$0, StringBuilder $$1, int $$22) {
        $$0.forEach(($$2, $$3) -> {
            $$1.append("-- Counter: ").append($$2).append(" --\n");
            this.appendCounterResults(0, "root", (CounterCollector)$$3.children.get((Object)"root"), $$22, $$1);
            $$1.append("\n\n");
        });
    }

    private static String getComment() {
        String[] $$0 = new String[]{"I'd Rather Be Surfing", "Shiny numbers!", "Am I not running fast enough? :(", "I'm working as hard as I can!", "Will I ever be good enough for you? :(", "Speedy. Zoooooom!", "Hello world", "40% better than a crash report.", "Now with extra numbers", "Now with less numbers", "Now with the same numbers", "You should add flames to things, it makes them go faster!", "Do you feel the need for... optimization?", "*cracks redstone whip*", "Maybe if you treated it better then it'll have more motivation to work faster! Poor server."};
        try {
            return $$0[(int)(Util.getNanos() % (long)$$0.length)];
        }
        catch (Throwable $$1) {
            return "Witty comment unavailable :(";
        }
    }

    @Override
    public int getTickDuration() {
        return this.tickDuration;
    }

    private static /* synthetic */ void lambda$getCounterValues$3(Map $$0, String $$1, ProfilerPathEntry $$22) {
        Object2LongMap<String> $$32 = $$22.getCounters();
        if (!$$32.isEmpty()) {
            List $$4 = SPLITTER.splitToList((CharSequence)$$1);
            $$32.forEach(($$2, $$3) -> ((CounterCollector)$$0.computeIfAbsent($$2, $$0 -> new CounterCollector())).addValue((Iterator<String>)$$4.iterator(), (long)$$3));
        }
    }

    static class CounterCollector {
        long selfValue;
        long totalValue;
        final Map<String, CounterCollector> children = Maps.newHashMap();

        CounterCollector() {
        }

        public void addValue(Iterator<String> $$02, long $$1) {
            this.totalValue += $$1;
            if (!$$02.hasNext()) {
                this.selfValue += $$1;
            } else {
                ((CounterCollector)this.children.computeIfAbsent((Object)((String)$$02.next()), $$0 -> new CounterCollector())).addValue($$02, $$1);
            }
        }
    }
}