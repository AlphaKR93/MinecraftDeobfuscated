/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.hash.Hashing
 *  com.google.common.hash.HashingOutputStream
 *  com.google.gson.JsonElement
 *  com.google.gson.stream.JsonWriter
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  java.io.ByteArrayOutputStream
 *  java.io.IOException
 *  java.io.OutputStream
 *  java.io.OutputStreamWriter
 *  java.io.Writer
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.lang.String
 *  java.nio.charset.StandardCharsets
 *  java.nio.file.Path
 *  java.util.Comparator
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.function.ToIntFunction
 *  org.slf4j.Logger
 */
package net.minecraft.data;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.ToIntFunction;
import net.minecraft.Util;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

public interface DataProvider {
    public static final ToIntFunction<String> FIXED_ORDER_FIELDS = (ToIntFunction)Util.make(new Object2IntOpenHashMap(), $$0 -> {
        $$0.put((Object)"type", 0);
        $$0.put((Object)"parent", 1);
        $$0.defaultReturnValue(2);
    });
    public static final Comparator<String> KEY_COMPARATOR = Comparator.comparingInt(FIXED_ORDER_FIELDS).thenComparing($$0 -> $$0);
    public static final Logger LOGGER = LogUtils.getLogger();

    public CompletableFuture<?> run(CachedOutput var1);

    public String getName();

    public static CompletableFuture<?> saveStable(CachedOutput $$0, JsonElement $$1, Path $$2) {
        return CompletableFuture.runAsync(() -> {
            try {
                ByteArrayOutputStream $$3 = new ByteArrayOutputStream();
                HashingOutputStream $$4 = new HashingOutputStream(Hashing.sha1(), (OutputStream)$$3);
                try (JsonWriter $$5 = new JsonWriter((Writer)new OutputStreamWriter((OutputStream)$$4, StandardCharsets.UTF_8));){
                    $$5.setSerializeNulls(false);
                    $$5.setIndent("  ");
                    GsonHelper.writeValue($$5, $$1, KEY_COMPARATOR);
                }
                $$0.writeIfNeeded($$2, $$3.toByteArray(), $$4.hash());
            }
            catch (IOException $$6) {
                LOGGER.error("Failed to save file to {}", (Object)$$2, (Object)$$6);
            }
        }, (Executor)Util.backgroundExecutor());
    }

    @FunctionalInterface
    public static interface Factory<T extends DataProvider> {
        public T create(PackOutput var1);
    }
}