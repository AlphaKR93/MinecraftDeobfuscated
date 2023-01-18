/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.hash.HashCode
 *  com.google.common.hash.Hashing
 *  com.mojang.logging.LogUtils
 *  java.io.BufferedReader
 *  java.io.BufferedWriter
 *  java.io.IOException
 *  java.lang.CharSequence
 *  java.lang.Exception
 *  java.lang.FunctionalInterface
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.nio.charset.Charset
 *  java.nio.charset.StandardCharsets
 *  java.nio.file.FileVisitOption
 *  java.nio.file.Files
 *  java.nio.file.LinkOption
 *  java.nio.file.OpenOption
 *  java.nio.file.Path
 *  java.nio.file.attribute.FileAttribute
 *  java.time.LocalDateTime
 *  java.time.format.DateTimeFormatter
 *  java.time.temporal.TemporalAccessor
 *  java.util.Collection
 *  java.util.HashMap
 *  java.util.HashSet
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Objects
 *  java.util.Set
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.ConcurrentHashMap
 *  java.util.concurrent.ConcurrentMap
 *  java.util.concurrent.atomic.AtomicInteger
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.mutable.MutableInt
 *  org.slf4j.Logger
 */
package net.minecraft.data;

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.WorldVersion;
import net.minecraft.data.CachedOutput;
import org.apache.commons.lang3.mutable.MutableInt;
import org.slf4j.Logger;

public class HashCache {
    static final Logger LOGGER = LogUtils.getLogger();
    private static final String HEADER_MARKER = "// ";
    private final Path rootDir;
    private final Path cacheDir;
    private final String versionId;
    private final Map<String, ProviderCache> caches;
    private final Set<String> cachesToWrite = new HashSet();
    private final Set<Path> cachePaths = new HashSet();
    private final int initialCount;
    private int writes;

    private Path getProviderCachePath(String $$0) {
        return this.cacheDir.resolve(Hashing.sha1().hashString((CharSequence)$$0, StandardCharsets.UTF_8).toString());
    }

    public HashCache(Path $$0, Collection<String> $$1, WorldVersion $$2) throws IOException {
        this.versionId = $$2.getName();
        this.rootDir = $$0;
        this.cacheDir = $$0.resolve(".cache");
        Files.createDirectories((Path)this.cacheDir, (FileAttribute[])new FileAttribute[0]);
        HashMap $$3 = new HashMap();
        int $$4 = 0;
        for (String $$5 : $$1) {
            Path $$6 = this.getProviderCachePath($$5);
            this.cachePaths.add((Object)$$6);
            ProviderCache $$7 = HashCache.readCache($$0, $$6);
            $$3.put((Object)$$5, (Object)$$7);
            $$4 += $$7.count();
        }
        this.caches = $$3;
        this.initialCount = $$4;
    }

    private static ProviderCache readCache(Path $$0, Path $$1) {
        if (Files.isReadable((Path)$$1)) {
            try {
                return ProviderCache.load($$0, $$1);
            }
            catch (Exception $$2) {
                LOGGER.warn("Failed to parse cache {}, discarding", (Object)$$1, (Object)$$2);
            }
        }
        return new ProviderCache("unknown", (ImmutableMap<Path, HashCode>)ImmutableMap.of());
    }

    public boolean shouldRunInThisVersion(String $$0) {
        ProviderCache $$1 = (ProviderCache)((Object)this.caches.get((Object)$$0));
        return $$1 == null || !$$1.version.equals((Object)this.versionId);
    }

    public CompletableFuture<UpdateResult> generateUpdate(String $$0, UpdateFunction $$12) {
        ProviderCache $$2 = (ProviderCache)((Object)this.caches.get((Object)$$0));
        if ($$2 == null) {
            throw new IllegalStateException("Provider not registered: " + $$0);
        }
        CacheUpdater $$3 = new CacheUpdater($$0, this.versionId, $$2);
        return $$12.update($$3).thenApply($$1 -> $$3.close());
    }

    public void applyUpdate(UpdateResult $$0) {
        this.caches.put((Object)$$0.providerId(), (Object)$$0.cache());
        this.cachesToWrite.add((Object)$$0.providerId());
        this.writes += $$0.writes();
    }

    public void purgeStaleAndWrite() throws IOException {
        HashSet $$0 = new HashSet();
        this.caches.forEach((arg_0, arg_1) -> this.lambda$purgeStaleAndWrite$1((Set)$$0, arg_0, arg_1));
        $$0.add((Object)this.rootDir.resolve("version.json"));
        MutableInt $$1 = new MutableInt();
        MutableInt $$2 = new MutableInt();
        try (Stream $$3 = Files.walk((Path)this.rootDir, (FileVisitOption[])new FileVisitOption[0]);){
            $$3.forEach(arg_0 -> this.lambda$purgeStaleAndWrite$2($$1, (Set)$$0, $$2, arg_0));
        }
        LOGGER.info("Caching: total files: {}, old count: {}, new count: {}, removed stale: {}, written: {}", new Object[]{$$1, this.initialCount, $$0.size(), $$2, this.writes});
    }

    private /* synthetic */ void lambda$purgeStaleAndWrite$2(MutableInt $$0, Set $$1, MutableInt $$2, Path $$3) {
        if (Files.isDirectory((Path)$$3, (LinkOption[])new LinkOption[0])) {
            return;
        }
        if (this.cachePaths.contains((Object)$$3)) {
            return;
        }
        $$0.increment();
        if ($$1.contains((Object)$$3)) {
            return;
        }
        try {
            Files.delete((Path)$$3);
        }
        catch (IOException $$4) {
            LOGGER.warn("Failed to delete file {}", (Object)$$3, (Object)$$4);
        }
        $$2.increment();
    }

    private /* synthetic */ void lambda$purgeStaleAndWrite$1(Set $$0, String $$1, ProviderCache $$2) {
        if (this.cachesToWrite.contains((Object)$$1)) {
            Path $$3 = this.getProviderCachePath($$1);
            $$2.save(this.rootDir, $$3, DateTimeFormatter.ISO_LOCAL_DATE_TIME.format((TemporalAccessor)LocalDateTime.now()) + "\t" + $$1);
        }
        $$0.addAll((Collection)$$2.data().keySet());
    }

    record ProviderCache(String version, ImmutableMap<Path, HashCode> data) {
        @Nullable
        public HashCode get(Path $$0) {
            return (HashCode)this.data.get((Object)$$0);
        }

        public int count() {
            return this.data.size();
        }

        public static ProviderCache load(Path $$0, Path $$1) throws IOException {
            try (BufferedReader $$22 = Files.newBufferedReader((Path)$$1, (Charset)StandardCharsets.UTF_8);){
                String $$3 = $$22.readLine();
                if (!$$3.startsWith(HashCache.HEADER_MARKER)) {
                    throw new IllegalStateException("Missing cache file header");
                }
                String[] $$4 = $$3.substring(HashCache.HEADER_MARKER.length()).split("\t", 2);
                String $$5 = $$4[0];
                ImmutableMap.Builder $$6 = ImmutableMap.builder();
                $$22.lines().forEach($$2 -> {
                    int $$3 = $$2.indexOf(32);
                    $$6.put((Object)$$0.resolve($$2.substring($$3 + 1)), (Object)HashCode.fromString((String)$$2.substring(0, $$3)));
                });
                ProviderCache providerCache = new ProviderCache($$5, (ImmutableMap<Path, HashCode>)$$6.build());
                return providerCache;
            }
        }

        public void save(Path $$0, Path $$1, String $$2) {
            try (BufferedWriter $$3 = Files.newBufferedWriter((Path)$$1, (Charset)StandardCharsets.UTF_8, (OpenOption[])new OpenOption[0]);){
                $$3.write(HashCache.HEADER_MARKER);
                $$3.write(this.version);
                $$3.write(9);
                $$3.write($$2);
                $$3.newLine();
                for (Map.Entry $$4 : this.data.entrySet()) {
                    $$3.write(((HashCode)$$4.getValue()).toString());
                    $$3.write(32);
                    $$3.write($$0.relativize((Path)$$4.getKey()).toString());
                    $$3.newLine();
                }
            }
            catch (IOException $$5) {
                LOGGER.warn("Unable write cachefile {}: {}", (Object)$$1, (Object)$$5);
            }
        }
    }

    class CacheUpdater
    implements CachedOutput {
        private final String provider;
        private final ProviderCache oldCache;
        private final ProviderCacheBuilder newCache;
        private final AtomicInteger writes = new AtomicInteger();
        private volatile boolean closed;

        CacheUpdater(String $$0, String $$1, ProviderCache $$2) {
            this.provider = $$0;
            this.oldCache = $$2;
            this.newCache = new ProviderCacheBuilder($$1);
        }

        private boolean shouldWrite(Path $$0, HashCode $$1) {
            return !Objects.equals((Object)this.oldCache.get($$0), (Object)$$1) || !Files.exists((Path)$$0, (LinkOption[])new LinkOption[0]);
        }

        @Override
        public void writeIfNeeded(Path $$0, byte[] $$1, HashCode $$2) throws IOException {
            if (this.closed) {
                throw new IllegalStateException("Cannot write to cache as it has already been closed");
            }
            if (this.shouldWrite($$0, $$2)) {
                this.writes.incrementAndGet();
                Files.createDirectories((Path)$$0.getParent(), (FileAttribute[])new FileAttribute[0]);
                Files.write((Path)$$0, (byte[])$$1, (OpenOption[])new OpenOption[0]);
            }
            this.newCache.put($$0, $$2);
        }

        public UpdateResult close() {
            this.closed = true;
            return new UpdateResult(this.provider, this.newCache.build(), this.writes.get());
        }
    }

    @FunctionalInterface
    public static interface UpdateFunction {
        public CompletableFuture<?> update(CachedOutput var1);
    }

    public record UpdateResult(String providerId, ProviderCache cache, int writes) {
    }

    record ProviderCacheBuilder(String version, ConcurrentMap<Path, HashCode> data) {
        ProviderCacheBuilder(String $$0) {
            this($$0, (ConcurrentMap<Path, HashCode>)new ConcurrentHashMap());
        }

        public void put(Path $$0, HashCode $$1) {
            this.data.put((Object)$$0, (Object)$$1);
        }

        public ProviderCache build() {
            return new ProviderCache(this.version, (ImmutableMap<Path, HashCode>)ImmutableMap.copyOf(this.data));
        }
    }
}