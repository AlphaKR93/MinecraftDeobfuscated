/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.io.BufferedReader
 *  java.io.IOException
 *  java.io.InputStreamReader
 *  java.io.Reader
 *  java.lang.Class
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.net.SocketAddress
 *  java.net.URL
 *  java.nio.file.Path
 *  java.nio.file.Paths
 *  java.text.ParseException
 *  java.time.Instant
 *  java.time.ZoneId
 *  java.time.format.DateTimeFormatter
 *  java.time.format.DateTimeFormatterBuilder
 *  java.time.temporal.TemporalAccessor
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.concurrent.ConcurrentHashMap
 *  javax.annotation.Nullable
 *  jdk.jfr.Configuration
 *  jdk.jfr.Event
 *  jdk.jfr.FlightRecorder
 *  jdk.jfr.FlightRecorderListener
 *  jdk.jfr.Recording
 *  jdk.jfr.RecordingState
 *  org.slf4j.Logger
 */
package net.minecraft.util.profiling.jfr;

import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.SocketAddress;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import jdk.jfr.Configuration;
import jdk.jfr.Event;
import jdk.jfr.FlightRecorder;
import jdk.jfr.FlightRecorderListener;
import jdk.jfr.Recording;
import jdk.jfr.RecordingState;
import net.minecraft.FileUtil;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.profiling.jfr.Environment;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.profiling.jfr.SummaryReporter;
import net.minecraft.util.profiling.jfr.callback.ProfiledDuration;
import net.minecraft.util.profiling.jfr.event.ChunkGenerationEvent;
import net.minecraft.util.profiling.jfr.event.NetworkSummaryEvent;
import net.minecraft.util.profiling.jfr.event.PacketReceivedEvent;
import net.minecraft.util.profiling.jfr.event.PacketSentEvent;
import net.minecraft.util.profiling.jfr.event.ServerTickTimeEvent;
import net.minecraft.util.profiling.jfr.event.WorldLoadFinishedEvent;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;

public class JfrProfiler
implements JvmProfiler {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String ROOT_CATEGORY = "Minecraft";
    public static final String WORLD_GEN_CATEGORY = "World Generation";
    public static final String TICK_CATEGORY = "Ticking";
    public static final String NETWORK_CATEGORY = "Network";
    private static final List<Class<? extends Event>> CUSTOM_EVENTS = List.of(ChunkGenerationEvent.class, PacketReceivedEvent.class, PacketSentEvent.class, NetworkSummaryEvent.class, ServerTickTimeEvent.class, WorldLoadFinishedEvent.class);
    private static final String FLIGHT_RECORDER_CONFIG = "/flightrecorder-config.jfc";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd-HHmmss").toFormatter().withZone(ZoneId.systemDefault());
    private static final JfrProfiler INSTANCE = new JfrProfiler();
    @Nullable
    Recording recording;
    private float currentAverageTickTime;
    private final Map<String, NetworkSummaryEvent.SumAggregation> networkTrafficByAddress = new ConcurrentHashMap();

    private JfrProfiler() {
        CUSTOM_EVENTS.forEach(FlightRecorder::register);
        FlightRecorder.addPeriodicEvent(ServerTickTimeEvent.class, () -> new ServerTickTimeEvent(this.currentAverageTickTime).commit());
        FlightRecorder.addPeriodicEvent(NetworkSummaryEvent.class, () -> {
            Iterator $$0 = this.networkTrafficByAddress.values().iterator();
            while ($$0.hasNext()) {
                ((NetworkSummaryEvent.SumAggregation)$$0.next()).commitEvent();
                $$0.remove();
            }
        });
    }

    public static JfrProfiler getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean start(Environment $$0) {
        boolean bl;
        URL $$1 = JfrProfiler.class.getResource(FLIGHT_RECORDER_CONFIG);
        if ($$1 == null) {
            LOGGER.warn("Could not find default flight recorder config at {}", (Object)FLIGHT_RECORDER_CONFIG);
            return false;
        }
        BufferedReader $$2 = new BufferedReader((Reader)new InputStreamReader($$1.openStream()));
        try {
            bl = this.start((Reader)$$2, $$0);
        }
        catch (Throwable throwable) {
            try {
                try {
                    $$2.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException $$3) {
                LOGGER.warn("Failed to start flight recorder using configuration at {}", (Object)$$1, (Object)$$3);
                return false;
            }
        }
        $$2.close();
        return bl;
    }

    @Override
    public Path stop() {
        if (this.recording == null) {
            throw new IllegalStateException("Not currently profiling");
        }
        this.networkTrafficByAddress.clear();
        Path $$0 = this.recording.getDestination();
        this.recording.stop();
        return $$0;
    }

    @Override
    public boolean isRunning() {
        return this.recording != null;
    }

    @Override
    public boolean isAvailable() {
        return FlightRecorder.isAvailable();
    }

    private boolean start(Reader $$0, Environment $$1) {
        if (this.isRunning()) {
            LOGGER.warn("Profiling already in progress");
            return false;
        }
        try {
            Configuration $$22 = Configuration.create((Reader)$$0);
            String $$3 = DATE_TIME_FORMATTER.format((TemporalAccessor)Instant.now());
            this.recording = Util.make(new Recording($$22), $$2 -> {
                CUSTOM_EVENTS.forEach(arg_0 -> ((Recording)$$2).enable(arg_0));
                $$2.setDumpOnExit(true);
                $$2.setToDisk(true);
                $$2.setName(String.format((Locale)Locale.ROOT, (String)"%s-%s-%s", (Object[])new Object[]{$$1.getDescription(), SharedConstants.getCurrentVersion().getName(), $$3}));
            });
            Path $$4 = Paths.get((String)String.format((Locale)Locale.ROOT, (String)"debug/%s-%s.jfr", (Object[])new Object[]{$$1.getDescription(), $$3}), (String[])new String[0]);
            FileUtil.createDirectoriesSafe($$4.getParent());
            this.recording.setDestination($$4);
            this.recording.start();
            this.setupSummaryListener();
        }
        catch (IOException | ParseException $$5) {
            LOGGER.warn("Failed to start jfr profiling", $$5);
            return false;
        }
        LOGGER.info("Started flight recorder profiling id({}):name({}) - will dump to {} on exit or stop command", new Object[]{this.recording.getId(), this.recording.getName(), this.recording.getDestination()});
        return true;
    }

    private void setupSummaryListener() {
        FlightRecorder.addListener((FlightRecorderListener)new FlightRecorderListener(){
            final SummaryReporter summaryReporter = new SummaryReporter(() -> {
                JfrProfiler.this.recording = null;
            });

            public void recordingStateChanged(Recording $$0) {
                if ($$0 != JfrProfiler.this.recording || $$0.getState() != RecordingState.STOPPED) {
                    return;
                }
                this.summaryReporter.recordingStopped($$0.getDestination());
                FlightRecorder.removeListener((FlightRecorderListener)this);
            }
        });
    }

    @Override
    public void onServerTick(float $$0) {
        if (ServerTickTimeEvent.TYPE.isEnabled()) {
            this.currentAverageTickTime = $$0;
        }
    }

    @Override
    public void onPacketReceived(int $$0, int $$1, SocketAddress $$2, int $$3) {
        if (PacketReceivedEvent.TYPE.isEnabled()) {
            new PacketReceivedEvent($$0, $$1, $$2, $$3).commit();
        }
        if (NetworkSummaryEvent.TYPE.isEnabled()) {
            this.networkStatFor($$2).trackReceivedPacket($$3);
        }
    }

    @Override
    public void onPacketSent(int $$0, int $$1, SocketAddress $$2, int $$3) {
        if (PacketSentEvent.TYPE.isEnabled()) {
            new PacketSentEvent($$0, $$1, $$2, $$3).commit();
        }
        if (NetworkSummaryEvent.TYPE.isEnabled()) {
            this.networkStatFor($$2).trackSentPacket($$3);
        }
    }

    private NetworkSummaryEvent.SumAggregation networkStatFor(SocketAddress $$0) {
        return (NetworkSummaryEvent.SumAggregation)this.networkTrafficByAddress.computeIfAbsent((Object)$$0.toString(), NetworkSummaryEvent.SumAggregation::new);
    }

    @Override
    @Nullable
    public ProfiledDuration onWorldLoadedStarted() {
        if (!WorldLoadFinishedEvent.TYPE.isEnabled()) {
            return null;
        }
        WorldLoadFinishedEvent $$0 = new WorldLoadFinishedEvent();
        $$0.begin();
        return () -> ((WorldLoadFinishedEvent)$$0).commit();
    }

    @Override
    @Nullable
    public ProfiledDuration onChunkGenerate(ChunkPos $$0, ResourceKey<Level> $$1, String $$2) {
        if (!ChunkGenerationEvent.TYPE.isEnabled()) {
            return null;
        }
        ChunkGenerationEvent $$3 = new ChunkGenerationEvent($$0, $$1, $$2);
        $$3.begin();
        return () -> ((ChunkGenerationEvent)$$3).commit();
    }
}