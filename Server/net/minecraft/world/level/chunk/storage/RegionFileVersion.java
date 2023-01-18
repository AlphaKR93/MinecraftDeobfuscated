/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  java.io.BufferedOutputStream
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.OutputStream
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.util.zip.DeflaterOutputStream
 *  java.util.zip.GZIPInputStream
 *  java.util.zip.GZIPOutputStream
 *  java.util.zip.InflaterInputStream
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.chunk.storage;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;
import javax.annotation.Nullable;
import net.minecraft.util.FastBufferedInputStream;

public class RegionFileVersion {
    private static final Int2ObjectMap<RegionFileVersion> VERSIONS = new Int2ObjectOpenHashMap();
    public static final RegionFileVersion VERSION_GZIP = RegionFileVersion.register(new RegionFileVersion(1, $$0 -> new FastBufferedInputStream((InputStream)new GZIPInputStream($$0)), $$0 -> new BufferedOutputStream((OutputStream)new GZIPOutputStream($$0))));
    public static final RegionFileVersion VERSION_DEFLATE = RegionFileVersion.register(new RegionFileVersion(2, $$0 -> new FastBufferedInputStream((InputStream)new InflaterInputStream($$0)), $$0 -> new BufferedOutputStream((OutputStream)new DeflaterOutputStream($$0))));
    public static final RegionFileVersion VERSION_NONE = RegionFileVersion.register(new RegionFileVersion(3, $$0 -> $$0, $$0 -> $$0));
    private final int id;
    private final StreamWrapper<InputStream> inputWrapper;
    private final StreamWrapper<OutputStream> outputWrapper;

    private RegionFileVersion(int $$0, StreamWrapper<InputStream> $$1, StreamWrapper<OutputStream> $$2) {
        this.id = $$0;
        this.inputWrapper = $$1;
        this.outputWrapper = $$2;
    }

    private static RegionFileVersion register(RegionFileVersion $$0) {
        VERSIONS.put($$0.id, (Object)$$0);
        return $$0;
    }

    @Nullable
    public static RegionFileVersion fromId(int $$0) {
        return (RegionFileVersion)VERSIONS.get($$0);
    }

    public static boolean isValidVersion(int $$0) {
        return VERSIONS.containsKey($$0);
    }

    public int getId() {
        return this.id;
    }

    public OutputStream wrap(OutputStream $$0) throws IOException {
        return this.outputWrapper.wrap($$0);
    }

    public InputStream wrap(InputStream $$0) throws IOException {
        return this.inputWrapper.wrap($$0);
    }

    @FunctionalInterface
    static interface StreamWrapper<O> {
        public O wrap(O var1) throws IOException;
    }
}