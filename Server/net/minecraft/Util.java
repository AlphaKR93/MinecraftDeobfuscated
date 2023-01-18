/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Ticker
 *  com.google.common.collect.Iterators
 *  com.google.common.collect.Lists
 *  com.google.common.util.concurrent.ListeningExecutorService
 *  com.google.common.util.concurrent.MoreExecutors
 *  com.mojang.datafixers.DSL$TypeReference
 *  com.mojang.datafixers.DataFixUtils
 *  com.mojang.datafixers.types.Type
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DataResult$PartialResult
 *  it.unimi.dsi.fastutil.Hash$Strategy
 *  it.unimi.dsi.fastutil.ints.IntArrayList
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  java.io.File
 *  java.io.IOException
 *  java.lang.CharSequence
 *  java.lang.Character
 *  java.lang.Comparable
 *  java.lang.Exception
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.InterruptedException
 *  java.lang.Iterable
 *  java.lang.NumberFormatException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Process
 *  java.lang.Runnable
 *  java.lang.Runtime
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.System
 *  java.lang.Thread
 *  java.lang.Thread$UncaughtExceptionHandler
 *  java.lang.Throwable
 *  java.lang.Void
 *  java.lang.management.ManagementFactory
 *  java.lang.management.RuntimeMXBean
 *  java.net.MalformedURLException
 *  java.net.URI
 *  java.net.URISyntaxException
 *  java.net.URL
 *  java.nio.file.CopyOption
 *  java.nio.file.Files
 *  java.nio.file.LinkOption
 *  java.nio.file.Path
 *  java.nio.file.spi.FileSystemProvider
 *  java.security.AccessController
 *  java.security.PrivilegedActionException
 *  java.time.Duration
 *  java.time.Instant
 *  java.time.ZonedDateTime
 *  java.time.format.DateTimeFormatter
 *  java.time.temporal.Temporal
 *  java.time.temporal.TemporalAccessor
 *  java.util.ArrayList
 *  java.util.Arrays
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Optional
 *  java.util.UUID
 *  java.util.concurrent.BlockingQueue
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.CompletionException
 *  java.util.concurrent.CompletionStage
 *  java.util.concurrent.ConcurrentHashMap
 *  java.util.concurrent.Executor
 *  java.util.concurrent.ExecutorService
 *  java.util.concurrent.Executors
 *  java.util.concurrent.ForkJoinPool
 *  java.util.concurrent.ForkJoinWorkerThread
 *  java.util.concurrent.LinkedBlockingQueue
 *  java.util.concurrent.TimeUnit
 *  java.util.concurrent.atomic.AtomicInteger
 *  java.util.function.BiFunction
 *  java.util.function.BooleanSupplier
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  java.util.function.IntFunction
 *  java.util.function.Predicate
 *  java.util.function.Supplier
 *  java.util.function.ToIntFunction
 *  java.util.stream.Collector
 *  java.util.stream.Collectors
 *  java.util.stream.IntStream
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft;

import com.google.common.base.Ticker;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CharPredicate;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeSource;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.block.state.properties.Property;
import org.slf4j.Logger;

public class Util {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final int DEFAULT_MAX_THREADS = 255;
    private static final String MAX_THREADS_SYSTEM_PROPERTY = "max.bg.threads";
    private static final AtomicInteger WORKER_COUNT = new AtomicInteger(1);
    private static final ExecutorService BOOTSTRAP_EXECUTOR = Util.makeExecutor("Bootstrap");
    private static final ExecutorService BACKGROUND_EXECUTOR = Util.makeExecutor("Main");
    private static final ExecutorService IO_POOL = Util.makeIoExecutor();
    private static final DateTimeFormatter FILENAME_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern((String)"yyyy-MM-dd_HH.mm.ss", (Locale)Locale.ROOT);
    public static TimeSource.NanoTimeSource timeSource = System::nanoTime;
    public static final Ticker TICKER = new Ticker(){

        public long read() {
            return timeSource.getAsLong();
        }
    };
    public static final UUID NIL_UUID = new UUID(0L, 0L);
    public static final FileSystemProvider ZIP_FILE_SYSTEM_PROVIDER = (FileSystemProvider)FileSystemProvider.installedProviders().stream().filter($$0 -> $$0.getScheme().equalsIgnoreCase("jar")).findFirst().orElseThrow(() -> new IllegalStateException("No jar file system provider found"));
    private static Consumer<String> thePauser = $$0 -> {};

    public static <K, V> Collector<Map.Entry<? extends K, ? extends V>, ?, Map<K, V>> toMap() {
        return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    public static <T extends Comparable<T>> String getPropertyName(Property<T> $$0, Object $$1) {
        return $$0.getName((Comparable)$$1);
    }

    public static String makeDescriptionId(String $$0, @Nullable ResourceLocation $$1) {
        if ($$1 == null) {
            return $$0 + ".unregistered_sadface";
        }
        return $$0 + "." + $$1.getNamespace() + "." + $$1.getPath().replace('/', '.');
    }

    public static long getMillis() {
        return Util.getNanos() / 1000000L;
    }

    public static long getNanos() {
        return timeSource.getAsLong();
    }

    public static long getEpochMillis() {
        return Instant.now().toEpochMilli();
    }

    public static String getFilenameFormattedDateTime() {
        return FILENAME_DATE_TIME_FORMATTER.format((TemporalAccessor)ZonedDateTime.now());
    }

    private static ExecutorService makeExecutor(String $$0) {
        ForkJoinPool $$3;
        int $$12 = Mth.clamp(Runtime.getRuntime().availableProcessors() - 1, 1, Util.getMaxThreads());
        if ($$12 <= 0) {
            ListeningExecutorService $$2 = MoreExecutors.newDirectExecutorService();
        } else {
            $$3 = new ForkJoinPool($$12, $$1 -> {
                ForkJoinWorkerThread $$2 = new ForkJoinWorkerThread($$1){

                    protected void onTermination(Throwable $$0) {
                        if ($$0 != null) {
                            LOGGER.warn("{} died", (Object)this.getName(), (Object)$$0);
                        } else {
                            LOGGER.debug("{} shutdown", (Object)this.getName());
                        }
                        super.onTermination($$0);
                    }
                };
                $$2.setName("Worker-" + $$0 + "-" + WORKER_COUNT.getAndIncrement());
                return $$2;
            }, Util::onThreadException, true);
        }
        return $$3;
    }

    private static int getMaxThreads() {
        String $$0 = System.getProperty((String)MAX_THREADS_SYSTEM_PROPERTY);
        if ($$0 != null) {
            try {
                int $$1 = Integer.parseInt((String)$$0);
                if ($$1 >= 1 && $$1 <= 255) {
                    return $$1;
                }
                LOGGER.error("Wrong {} property value '{}'. Should be an integer value between 1 and {}.", new Object[]{MAX_THREADS_SYSTEM_PROPERTY, $$0, 255});
            }
            catch (NumberFormatException $$2) {
                LOGGER.error("Could not parse {} property value '{}'. Should be an integer value between 1 and {}.", new Object[]{MAX_THREADS_SYSTEM_PROPERTY, $$0, 255});
            }
        }
        return 255;
    }

    public static ExecutorService bootstrapExecutor() {
        return BOOTSTRAP_EXECUTOR;
    }

    public static ExecutorService backgroundExecutor() {
        return BACKGROUND_EXECUTOR;
    }

    public static ExecutorService ioPool() {
        return IO_POOL;
    }

    public static void shutdownExecutors() {
        Util.shutdownExecutor(BACKGROUND_EXECUTOR);
        Util.shutdownExecutor(IO_POOL);
    }

    private static void shutdownExecutor(ExecutorService $$0) {
        boolean $$3;
        $$0.shutdown();
        try {
            boolean $$1 = $$0.awaitTermination(3L, TimeUnit.SECONDS);
        }
        catch (InterruptedException $$2) {
            $$3 = false;
        }
        if (!$$3) {
            $$0.shutdownNow();
        }
    }

    private static ExecutorService makeIoExecutor() {
        return Executors.newCachedThreadPool($$0 -> {
            Thread $$1 = new Thread($$0);
            $$1.setName("IO-Worker-" + WORKER_COUNT.getAndIncrement());
            $$1.setUncaughtExceptionHandler(Util::onThreadException);
            return $$1;
        });
    }

    public static void throwAsRuntime(Throwable $$0) {
        throw $$0 instanceof RuntimeException ? (RuntimeException)$$0 : new RuntimeException($$0);
    }

    private static void onThreadException(Thread $$0, Throwable $$1) {
        Util.pauseInIde($$1);
        if ($$1 instanceof CompletionException) {
            $$1 = $$1.getCause();
        }
        if ($$1 instanceof ReportedException) {
            Bootstrap.realStdoutPrintln(((ReportedException)$$1).getReport().getFriendlyReport());
            System.exit((int)-1);
        }
        LOGGER.error(String.format((Locale)Locale.ROOT, (String)"Caught exception in thread %s", (Object[])new Object[]{$$0}), $$1);
    }

    @Nullable
    public static Type<?> fetchChoiceType(DSL.TypeReference $$0, String $$1) {
        if (!SharedConstants.CHECK_DATA_FIXER_SCHEMA) {
            return null;
        }
        return Util.doFetchChoiceType($$0, $$1);
    }

    @Nullable
    private static Type<?> doFetchChoiceType(DSL.TypeReference $$0, String $$1) {
        Type $$2;
        block2: {
            $$2 = null;
            try {
                $$2 = DataFixers.getDataFixer().getSchema(DataFixUtils.makeKey((int)SharedConstants.getCurrentVersion().getDataVersion().getVersion())).getChoiceType($$0, $$1);
            }
            catch (IllegalArgumentException $$3) {
                LOGGER.error("No data fixer registered for {}", (Object)$$1);
                if (!SharedConstants.IS_RUNNING_IN_IDE) break block2;
                throw $$3;
            }
        }
        return $$2;
    }

    public static Runnable wrapThreadWithTaskName(String $$0, Runnable $$1) {
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            return () -> {
                Thread $$2 = Thread.currentThread();
                String $$3 = $$2.getName();
                $$2.setName($$0);
                try {
                    $$1.run();
                }
                finally {
                    $$2.setName($$3);
                }
            };
        }
        return $$1;
    }

    public static <V> Supplier<V> wrapThreadWithTaskName(String $$0, Supplier<V> $$1) {
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            return () -> {
                Thread $$2 = Thread.currentThread();
                String $$3 = $$2.getName();
                $$2.setName($$0);
                try {
                    Object object = $$1.get();
                    return object;
                }
                finally {
                    $$2.setName($$3);
                }
            };
        }
        return $$1;
    }

    public static OS getPlatform() {
        String $$0 = System.getProperty((String)"os.name").toLowerCase(Locale.ROOT);
        if ($$0.contains((CharSequence)"win")) {
            return OS.WINDOWS;
        }
        if ($$0.contains((CharSequence)"mac")) {
            return OS.OSX;
        }
        if ($$0.contains((CharSequence)"solaris")) {
            return OS.SOLARIS;
        }
        if ($$0.contains((CharSequence)"sunos")) {
            return OS.SOLARIS;
        }
        if ($$0.contains((CharSequence)"linux")) {
            return OS.LINUX;
        }
        if ($$0.contains((CharSequence)"unix")) {
            return OS.LINUX;
        }
        return OS.UNKNOWN;
    }

    public static Stream<String> getVmArguments() {
        RuntimeMXBean $$02 = ManagementFactory.getRuntimeMXBean();
        return $$02.getInputArguments().stream().filter($$0 -> $$0.startsWith("-X"));
    }

    public static <T> T lastOf(List<T> $$0) {
        return (T)$$0.get($$0.size() - 1);
    }

    public static <T> T findNextInIterable(Iterable<T> $$0, @Nullable T $$1) {
        Iterator $$2 = $$0.iterator();
        Object $$3 = $$2.next();
        if ($$1 != null) {
            Object $$4 = $$3;
            while (true) {
                if ($$4 == $$1) {
                    if (!$$2.hasNext()) break;
                    return (T)$$2.next();
                }
                if (!$$2.hasNext()) continue;
                $$4 = $$2.next();
            }
        }
        return (T)$$3;
    }

    public static <T> T findPreviousInIterable(Iterable<T> $$0, @Nullable T $$1) {
        Iterator $$2 = $$0.iterator();
        Object $$3 = null;
        while ($$2.hasNext()) {
            Object $$4 = $$2.next();
            if ($$4 == $$1) {
                if ($$3 != null) break;
                $$3 = $$2.hasNext() ? Iterators.getLast((Iterator)$$2) : $$1;
                break;
            }
            $$3 = $$4;
        }
        return (T)$$3;
    }

    public static <T> T make(Supplier<T> $$0) {
        return (T)$$0.get();
    }

    public static <T> T make(T $$0, Consumer<T> $$1) {
        $$1.accept($$0);
        return $$0;
    }

    @Nullable
    public static <T, R> R mapNullable(@Nullable T $$0, Function<T, R> $$1) {
        if ($$0 == null) {
            return null;
        }
        return (R)$$1.apply($$0);
    }

    public static <T, R> R mapNullable(@Nullable T $$0, Function<T, R> $$1, R $$2) {
        if ($$0 == null) {
            return $$2;
        }
        return (R)$$1.apply($$0);
    }

    public static <K> Hash.Strategy<K> identityStrategy() {
        return IdentityStrategy.INSTANCE;
    }

    public static <V> CompletableFuture<List<V>> sequence(List<? extends CompletableFuture<V>> $$0) {
        if ($$0.isEmpty()) {
            return CompletableFuture.completedFuture((Object)List.of());
        }
        if ($$0.size() == 1) {
            return ((CompletableFuture)$$0.get(0)).thenApply(List::of);
        }
        CompletableFuture $$12 = CompletableFuture.allOf((CompletableFuture[])((CompletableFuture[])$$0.toArray((Object[])new CompletableFuture[0])));
        return $$12.thenApply($$1 -> $$0.stream().map(CompletableFuture::join).toList());
    }

    public static <V> CompletableFuture<List<V>> sequenceFailFast(List<? extends CompletableFuture<? extends V>> $$0) {
        CompletableFuture $$1 = new CompletableFuture();
        return Util.fallibleSequence($$0, (Consumer<Throwable>)((Consumer)arg_0 -> ((CompletableFuture)$$1).completeExceptionally(arg_0))).applyToEither((CompletionStage)$$1, Function.identity());
    }

    public static <V> CompletableFuture<List<V>> sequenceFailFastAndCancel(List<? extends CompletableFuture<? extends V>> $$0) {
        CompletableFuture $$1 = new CompletableFuture();
        return Util.fallibleSequence($$0, (Consumer<Throwable>)((Consumer)$$2 -> {
            for (CompletableFuture $$3 : $$0) {
                $$3.cancel(true);
            }
            $$1.completeExceptionally($$2);
        })).applyToEither((CompletionStage)$$1, Function.identity());
    }

    private static <V> CompletableFuture<List<V>> fallibleSequence(List<? extends CompletableFuture<? extends V>> $$0, Consumer<Throwable> $$1) {
        ArrayList $$2 = Lists.newArrayListWithCapacity((int)$$0.size());
        CompletableFuture[] $$3 = new CompletableFuture[$$0.size()];
        $$0.forEach(arg_0 -> Util.lambda$fallibleSequence$11((List)$$2, $$3, $$1, arg_0));
        return CompletableFuture.allOf((CompletableFuture[])$$3).thenApply(arg_0 -> Util.lambda$fallibleSequence$12((List)$$2, arg_0));
    }

    public static <T> Optional<T> ifElse(Optional<T> $$0, Consumer<T> $$1, Runnable $$2) {
        if ($$0.isPresent()) {
            $$1.accept($$0.get());
        } else {
            $$2.run();
        }
        return $$0;
    }

    public static <T> Supplier<T> name(Supplier<T> $$0, Supplier<String> $$1) {
        return $$0;
    }

    public static Runnable name(Runnable $$0, Supplier<String> $$1) {
        return $$0;
    }

    public static void logAndPauseIfInIde(String $$0) {
        LOGGER.error($$0);
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            Util.doPause($$0);
        }
    }

    public static void logAndPauseIfInIde(String $$0, Throwable $$1) {
        LOGGER.error($$0, $$1);
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            Util.doPause($$0);
        }
    }

    public static <T extends Throwable> T pauseInIde(T $$0) {
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            LOGGER.error("Trying to throw a fatal exception, pausing in IDE", $$0);
            Util.doPause($$0.getMessage());
        }
        return $$0;
    }

    public static void setPause(Consumer<String> $$0) {
        thePauser = $$0;
    }

    private static void doPause(String $$0) {
        boolean $$2;
        Instant $$1 = Instant.now();
        LOGGER.warn("Did you remember to set a breakpoint here?");
        boolean bl = $$2 = Duration.between((Temporal)$$1, (Temporal)Instant.now()).toMillis() > 500L;
        if (!$$2) {
            thePauser.accept((Object)$$0);
        }
    }

    public static String describeError(Throwable $$0) {
        if ($$0.getCause() != null) {
            return Util.describeError($$0.getCause());
        }
        if ($$0.getMessage() != null) {
            return $$0.getMessage();
        }
        return $$0.toString();
    }

    public static <T> T getRandom(T[] $$0, RandomSource $$1) {
        return $$0[$$1.nextInt($$0.length)];
    }

    public static int getRandom(int[] $$0, RandomSource $$1) {
        return $$0[$$1.nextInt($$0.length)];
    }

    public static <T> T getRandom(List<T> $$0, RandomSource $$1) {
        return (T)$$0.get($$1.nextInt($$0.size()));
    }

    public static <T> Optional<T> getRandomSafe(List<T> $$0, RandomSource $$1) {
        if ($$0.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(Util.getRandom($$0, $$1));
    }

    private static BooleanSupplier createRenamer(final Path $$0, final Path $$1) {
        return new BooleanSupplier(){

            public boolean getAsBoolean() {
                try {
                    Files.move((Path)$$0, (Path)$$1, (CopyOption[])new CopyOption[0]);
                    return true;
                }
                catch (IOException $$02) {
                    LOGGER.error("Failed to rename", (Throwable)$$02);
                    return false;
                }
            }

            public String toString() {
                return "rename " + $$0 + " to " + $$1;
            }
        };
    }

    private static BooleanSupplier createDeleter(final Path $$0) {
        return new BooleanSupplier(){

            public boolean getAsBoolean() {
                try {
                    Files.deleteIfExists((Path)$$0);
                    return true;
                }
                catch (IOException $$02) {
                    LOGGER.warn("Failed to delete", (Throwable)$$02);
                    return false;
                }
            }

            public String toString() {
                return "delete old " + $$0;
            }
        };
    }

    private static BooleanSupplier createFileDeletedCheck(final Path $$0) {
        return new BooleanSupplier(){

            public boolean getAsBoolean() {
                return !Files.exists((Path)$$0, (LinkOption[])new LinkOption[0]);
            }

            public String toString() {
                return "verify that " + $$0 + " is deleted";
            }
        };
    }

    private static BooleanSupplier createFileCreatedCheck(final Path $$0) {
        return new BooleanSupplier(){

            public boolean getAsBoolean() {
                return Files.isRegularFile((Path)$$0, (LinkOption[])new LinkOption[0]);
            }

            public String toString() {
                return "verify that " + $$0 + " is present";
            }
        };
    }

    private static boolean executeInSequence(BooleanSupplier ... $$0) {
        for (BooleanSupplier $$1 : $$0) {
            if ($$1.getAsBoolean()) continue;
            LOGGER.warn("Failed to execute {}", (Object)$$1);
            return false;
        }
        return true;
    }

    private static boolean runWithRetries(int $$0, String $$1, BooleanSupplier ... $$2) {
        for (int $$3 = 0; $$3 < $$0; ++$$3) {
            if (Util.executeInSequence($$2)) {
                return true;
            }
            LOGGER.error("Failed to {}, retrying {}/{}", new Object[]{$$1, $$3, $$0});
        }
        LOGGER.error("Failed to {}, aborting, progress might be lost", (Object)$$1);
        return false;
    }

    public static void safeReplaceFile(File $$0, File $$1, File $$2) {
        Util.safeReplaceFile($$0.toPath(), $$1.toPath(), $$2.toPath());
    }

    public static void safeReplaceFile(Path $$0, Path $$1, Path $$2) {
        Util.safeReplaceOrMoveFile($$0, $$1, $$2, false);
    }

    public static void safeReplaceOrMoveFile(File $$0, File $$1, File $$2, boolean $$3) {
        Util.safeReplaceOrMoveFile($$0.toPath(), $$1.toPath(), $$2.toPath(), $$3);
    }

    public static void safeReplaceOrMoveFile(Path $$0, Path $$1, Path $$2, boolean $$3) {
        int $$4 = 10;
        if (Files.exists((Path)$$0, (LinkOption[])new LinkOption[0]) && !Util.runWithRetries(10, "create backup " + $$2, Util.createDeleter($$2), Util.createRenamer($$0, $$2), Util.createFileCreatedCheck($$2))) {
            return;
        }
        if (!Util.runWithRetries(10, "remove old " + $$0, Util.createDeleter($$0), Util.createFileDeletedCheck($$0))) {
            return;
        }
        if (!Util.runWithRetries(10, "replace " + $$0 + " with " + $$1, Util.createRenamer($$1, $$0), Util.createFileCreatedCheck($$0)) && !$$3) {
            Util.runWithRetries(10, "restore " + $$0 + " from " + $$2, Util.createRenamer($$2, $$0), Util.createFileCreatedCheck($$0));
        }
    }

    public static int offsetByCodepoints(String $$0, int $$1, int $$2) {
        int $$3 = $$0.length();
        if ($$2 >= 0) {
            for (int $$4 = 0; $$1 < $$3 && $$4 < $$2; ++$$4) {
                if (!Character.isHighSurrogate((char)$$0.charAt($$1++)) || $$1 >= $$3 || !Character.isLowSurrogate((char)$$0.charAt($$1))) continue;
                ++$$1;
            }
        } else {
            for (int $$5 = $$2; $$1 > 0 && $$5 < 0; ++$$5) {
                if (!Character.isLowSurrogate((char)$$0.charAt(--$$1)) || $$1 <= 0 || !Character.isHighSurrogate((char)$$0.charAt($$1 - 1))) continue;
                --$$1;
            }
        }
        return $$1;
    }

    public static Consumer<String> prefix(String $$0, Consumer<String> $$1) {
        return $$2 -> $$1.accept((Object)($$0 + $$2));
    }

    public static DataResult<int[]> fixedSize(IntStream $$0, int $$1) {
        int[] $$2 = $$0.limit((long)($$1 + 1)).toArray();
        if ($$2.length != $$1) {
            String $$3 = "Input is not a list of " + $$1 + " ints";
            if ($$2.length >= $$1) {
                return DataResult.error((String)$$3, (Object)Arrays.copyOf((int[])$$2, (int)$$1));
            }
            return DataResult.error((String)$$3);
        }
        return DataResult.success((Object)$$2);
    }

    public static <T> DataResult<List<T>> fixedSize(List<T> $$0, int $$1) {
        if ($$0.size() != $$1) {
            String $$2 = "Input is not a list of " + $$1 + " elements";
            if ($$0.size() >= $$1) {
                return DataResult.error((String)$$2, (Object)$$0.subList(0, $$1));
            }
            return DataResult.error((String)$$2);
        }
        return DataResult.success($$0);
    }

    public static void startTimerHackThread() {
        Thread $$0 = new Thread("Timer hack thread"){

            public void run() {
                try {
                    while (true) {
                        Thread.sleep((long)Integer.MAX_VALUE);
                    }
                }
                catch (InterruptedException $$0) {
                    LOGGER.warn("Timer hack thread interrupted, that really should not happen");
                    return;
                }
            }
        };
        $$0.setDaemon(true);
        $$0.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new DefaultUncaughtExceptionHandler(LOGGER));
        $$0.start();
    }

    public static void copyBetweenDirs(Path $$0, Path $$1, Path $$2) throws IOException {
        Path $$3 = $$0.relativize($$2);
        Path $$4 = $$1.resolve($$3);
        Files.copy((Path)$$2, (Path)$$4, (CopyOption[])new CopyOption[0]);
    }

    public static String sanitizeName(String $$0, CharPredicate $$12) {
        return (String)$$0.toLowerCase(Locale.ROOT).chars().mapToObj($$1 -> $$12.test((char)$$1) ? Character.toString((char)((char)$$1)) : "_").collect(Collectors.joining());
    }

    public static <T, R> Function<T, R> memoize(final Function<T, R> $$0) {
        return new Function<T, R>(){
            private final Map<T, R> cache = new ConcurrentHashMap();

            public R apply(T $$02) {
                return this.cache.computeIfAbsent($$02, $$0);
            }

            public String toString() {
                return "memoize/1[function=" + $$0 + ", size=" + this.cache.size() + "]";
            }
        };
    }

    public static <T, U, R> BiFunction<T, U, R> memoize(final BiFunction<T, U, R> $$0) {
        return new BiFunction<T, U, R>(){
            private final Map<Pair<T, U>, R> cache = new ConcurrentHashMap();

            public R apply(T $$02, U $$12) {
                return this.cache.computeIfAbsent((Object)Pair.of($$02, $$12), $$1 -> $$0.apply($$1.getFirst(), $$1.getSecond()));
            }

            public String toString() {
                return "memoize/2[function=" + $$0 + ", size=" + this.cache.size() + "]";
            }
        };
    }

    public static <T> List<T> toShuffledList(Stream<T> $$0, RandomSource $$1) {
        ObjectArrayList $$2 = (ObjectArrayList)$$0.collect(ObjectArrayList.toList());
        Util.shuffle($$2, $$1);
        return $$2;
    }

    public static IntArrayList toShuffledList(IntStream $$0, RandomSource $$1) {
        int $$3;
        IntArrayList $$2 = IntArrayList.wrap((int[])$$0.toArray());
        for (int $$4 = $$3 = $$2.size(); $$4 > 1; --$$4) {
            int $$5 = $$1.nextInt($$4);
            $$2.set($$4 - 1, $$2.set($$5, $$2.getInt($$4 - 1)));
        }
        return $$2;
    }

    public static <T> List<T> shuffledCopy(T[] $$0, RandomSource $$1) {
        ObjectArrayList $$2 = new ObjectArrayList((Object[])$$0);
        Util.shuffle($$2, $$1);
        return $$2;
    }

    public static <T> List<T> shuffledCopy(ObjectArrayList<T> $$0, RandomSource $$1) {
        ObjectArrayList $$2 = new ObjectArrayList($$0);
        Util.shuffle($$2, $$1);
        return $$2;
    }

    public static <T> void shuffle(ObjectArrayList<T> $$0, RandomSource $$1) {
        int $$2;
        for (int $$3 = $$2 = $$0.size(); $$3 > 1; --$$3) {
            int $$4 = $$1.nextInt($$3);
            $$0.set($$3 - 1, $$0.set($$4, $$0.get($$3 - 1)));
        }
    }

    public static <T> CompletableFuture<T> blockUntilDone(Function<Executor, CompletableFuture<T>> $$0) {
        return Util.blockUntilDone($$0, CompletableFuture::isDone);
    }

    public static <T> T blockUntilDone(Function<Executor, T> $$0, Predicate<T> $$1) {
        int $$6;
        LinkedBlockingQueue $$2 = new LinkedBlockingQueue();
        Object $$3 = $$0.apply(arg_0 -> ((BlockingQueue)$$2).add(arg_0));
        while (!$$1.test($$3)) {
            try {
                Runnable $$4 = (Runnable)$$2.poll(100L, TimeUnit.MILLISECONDS);
                if ($$4 == null) continue;
                $$4.run();
            }
            catch (InterruptedException $$5) {
                LOGGER.warn("Interrupted wait");
                break;
            }
        }
        if (($$6 = $$2.size()) > 0) {
            LOGGER.warn("Tasks left in queue: {}", (Object)$$6);
        }
        return (T)$$3;
    }

    public static <T> ToIntFunction<T> createIndexLookup(List<T> $$0) {
        return Util.createIndexLookup($$0, Object2IntOpenHashMap::new);
    }

    public static <T> ToIntFunction<T> createIndexLookup(List<T> $$0, IntFunction<Object2IntMap<T>> $$1) {
        Object2IntMap $$2 = (Object2IntMap)$$1.apply($$0.size());
        for (int $$3 = 0; $$3 < $$0.size(); ++$$3) {
            $$2.put($$0.get($$3), $$3);
        }
        return $$2;
    }

    public static <T, E extends Exception> T getOrThrow(DataResult<T> $$0, Function<String, E> $$1) throws E {
        Optional $$2 = $$0.error();
        if ($$2.isPresent()) {
            throw (Exception)$$1.apply((Object)((DataResult.PartialResult)$$2.get()).message());
        }
        return (T)$$0.result().orElseThrow();
    }

    private static /* synthetic */ List lambda$fallibleSequence$12(List $$0, Void $$1) {
        return $$0;
    }

    private static /* synthetic */ void lambda$fallibleSequence$11(List $$0, CompletableFuture[] $$1, Consumer $$2, CompletableFuture $$32) {
        int $$42 = $$0.size();
        $$0.add(null);
        $$1[$$42] = $$32.whenComplete(($$3, $$4) -> {
            if ($$4 != null) {
                $$2.accept($$4);
            } else {
                $$0.set($$42, $$3);
            }
        });
    }

    /*
     * Uses 'sealed' constructs - enablewith --sealed true
     */
    public static enum OS {
        LINUX("linux"),
        SOLARIS("solaris"),
        WINDOWS("windows"){

            @Override
            protected String[] getOpenUrlArguments(URL $$0) {
                return new String[]{"rundll32", "url.dll,FileProtocolHandler", $$0.toString()};
            }
        }
        ,
        OSX("mac"){

            @Override
            protected String[] getOpenUrlArguments(URL $$0) {
                return new String[]{"open", $$0.toString()};
            }
        }
        ,
        UNKNOWN("unknown");

        private final String telemetryName;

        OS(String $$0) {
            this.telemetryName = $$0;
        }

        public void openUrl(URL $$0) {
            try {
                Process $$1 = (Process)AccessController.doPrivileged(() -> Runtime.getRuntime().exec(this.getOpenUrlArguments($$0)));
                $$1.getInputStream().close();
                $$1.getErrorStream().close();
                $$1.getOutputStream().close();
            }
            catch (IOException | PrivilegedActionException $$2) {
                LOGGER.error("Couldn't open url '{}'", (Object)$$0, (Object)$$2);
            }
        }

        public void openUri(URI $$0) {
            try {
                this.openUrl($$0.toURL());
            }
            catch (MalformedURLException $$1) {
                LOGGER.error("Couldn't open uri '{}'", (Object)$$0, (Object)$$1);
            }
        }

        public void openFile(File $$0) {
            try {
                this.openUrl($$0.toURI().toURL());
            }
            catch (MalformedURLException $$1) {
                LOGGER.error("Couldn't open file '{}'", (Object)$$0, (Object)$$1);
            }
        }

        protected String[] getOpenUrlArguments(URL $$0) {
            String $$1 = $$0.toString();
            if ("file".equals((Object)$$0.getProtocol())) {
                $$1 = $$1.replace((CharSequence)"file:", (CharSequence)"file://");
            }
            return new String[]{"xdg-open", $$1};
        }

        public void openUri(String $$0) {
            try {
                this.openUrl(new URI($$0).toURL());
            }
            catch (IllegalArgumentException | MalformedURLException | URISyntaxException $$1) {
                LOGGER.error("Couldn't open uri '{}'", (Object)$$0, (Object)$$1);
            }
        }

        public String telemetryName() {
            return this.telemetryName;
        }
    }

    static enum IdentityStrategy implements Hash.Strategy<Object>
    {
        INSTANCE;


        public int hashCode(Object $$0) {
            return System.identityHashCode((Object)$$0);
        }

        public boolean equals(Object $$0, Object $$1) {
            return $$0 == $$1;
        }
    }
}