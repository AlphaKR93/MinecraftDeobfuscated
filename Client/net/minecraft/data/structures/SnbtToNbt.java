/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.hash.HashCode
 *  com.google.common.hash.Hashing
 *  com.google.common.hash.HashingOutputStream
 *  com.mojang.logging.LogUtils
 *  java.io.BufferedReader
 *  java.io.ByteArrayOutputStream
 *  java.io.IOException
 *  java.io.OutputStream
 *  java.io.Reader
 *  java.lang.Exception
 *  java.lang.FunctionalInterface
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.Throwable
 *  java.nio.file.FileVisitOption
 *  java.nio.file.Files
 *  java.nio.file.Path
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 */
package net.minecraft.data.structures;

import com.google.common.collect.Lists;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.structures.NbtToSnbt;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class SnbtToNbt
implements DataProvider {
    @Nullable
    private static final Path DUMP_SNBT_TO = null;
    private static final Logger LOGGER = LogUtils.getLogger();
    private final PackOutput output;
    private final Iterable<Path> inputFolders;
    private final List<Filter> filters = Lists.newArrayList();

    public SnbtToNbt(PackOutput $$0, Iterable<Path> $$1) {
        this.output = $$0;
        this.inputFolders = $$1;
    }

    public SnbtToNbt addFilter(Filter $$0) {
        this.filters.add((Object)$$0);
        return this;
    }

    private CompoundTag applyFilters(String $$0, CompoundTag $$1) {
        CompoundTag $$2 = $$1;
        for (Filter $$3 : this.filters) {
            $$2 = $$3.apply($$0, $$2);
        }
        return $$2;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput $$02) {
        Path $$1 = this.output.getOutputFolder();
        ArrayList $$2 = Lists.newArrayList();
        for (Path $$3 : this.inputFolders) {
            $$2.add((Object)CompletableFuture.supplyAsync(() -> {
                CompletableFuture completableFuture;
                block8: {
                    Stream $$32 = Files.walk((Path)$$3, (FileVisitOption[])new FileVisitOption[0]);
                    try {
                        completableFuture = CompletableFuture.allOf((CompletableFuture[])((CompletableFuture[])$$32.filter($$0 -> $$0.toString().endsWith(".snbt")).map($$3 -> CompletableFuture.runAsync(() -> {
                            TaskResult $$4 = this.readStructure((Path)$$3, this.getName($$3, (Path)$$3));
                            this.storeStructureIfChanged($$02, $$4, $$1);
                        }, (Executor)Util.backgroundExecutor())).toArray(CompletableFuture[]::new)));
                        if ($$32 == null) break block8;
                    }
                    catch (Throwable throwable) {
                        try {
                            if ($$32 != null) {
                                try {
                                    $$32.close();
                                }
                                catch (Throwable throwable2) {
                                    throwable.addSuppressed(throwable2);
                                }
                            }
                            throw throwable;
                        }
                        catch (Exception $$4) {
                            throw new RuntimeException("Failed to read structure input directory, aborting", (Throwable)$$4);
                        }
                    }
                    $$32.close();
                }
                return completableFuture;
            }, (Executor)Util.backgroundExecutor()).thenCompose($$0 -> $$0));
        }
        return Util.sequenceFailFast($$2);
    }

    @Override
    public final String getName() {
        return "SNBT -> NBT";
    }

    private String getName(Path $$0, Path $$1) {
        String $$2 = $$0.relativize($$1).toString().replaceAll("\\\\", "/");
        return $$2.substring(0, $$2.length() - ".snbt".length());
    }

    private TaskResult readStructure(Path $$0, String $$1) {
        TaskResult taskResult;
        block10: {
            BufferedReader $$2 = Files.newBufferedReader((Path)$$0);
            try {
                String $$10;
                String $$3 = IOUtils.toString((Reader)$$2);
                CompoundTag $$4 = this.applyFilters($$1, NbtUtils.snbtToStructure($$3));
                ByteArrayOutputStream $$5 = new ByteArrayOutputStream();
                HashingOutputStream $$6 = new HashingOutputStream(Hashing.sha1(), (OutputStream)$$5);
                NbtIo.writeCompressed($$4, (OutputStream)$$6);
                byte[] $$7 = $$5.toByteArray();
                HashCode $$8 = $$6.hash();
                if (DUMP_SNBT_TO != null) {
                    String $$9 = NbtUtils.structureToSnbt($$4);
                } else {
                    $$10 = null;
                }
                taskResult = new TaskResult($$1, $$7, $$10, $$8);
                if ($$2 == null) break block10;
            }
            catch (Throwable throwable) {
                try {
                    if ($$2 != null) {
                        try {
                            $$2.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (Throwable $$11) {
                    throw new StructureConversionException($$0, $$11);
                }
            }
            $$2.close();
        }
        return taskResult;
    }

    private void storeStructureIfChanged(CachedOutput $$0, TaskResult $$1, Path $$2) {
        if ($$1.snbtPayload != null) {
            Path $$3 = DUMP_SNBT_TO.resolve($$1.name + ".snbt");
            try {
                NbtToSnbt.writeSnbt(CachedOutput.NO_CACHE, $$3, $$1.snbtPayload);
            }
            catch (IOException $$4) {
                LOGGER.error("Couldn't write structure SNBT {} at {}", new Object[]{$$1.name, $$3, $$4});
            }
        }
        Path $$5 = $$2.resolve($$1.name + ".nbt");
        try {
            $$0.writeIfNeeded($$5, $$1.payload, $$1.hash);
        }
        catch (IOException $$6) {
            LOGGER.error("Couldn't write structure {} at {}", new Object[]{$$1.name, $$5, $$6});
        }
    }

    @FunctionalInterface
    public static interface Filter {
        public CompoundTag apply(String var1, CompoundTag var2);
    }

    record TaskResult(String name, byte[] payload, @Nullable String snbtPayload, HashCode hash) {
    }

    static class StructureConversionException
    extends RuntimeException {
        public StructureConversionException(Path $$0, Throwable $$1) {
            super($$0.toAbsolutePath().toString(), $$1);
        }
    }
}