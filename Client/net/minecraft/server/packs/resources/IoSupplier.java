/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.io.IOException
 *  java.io.InputStream
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  java.nio.file.Files
 *  java.nio.file.OpenOption
 *  java.nio.file.Path
 *  java.util.zip.ZipEntry
 *  java.util.zip.ZipFile
 */
package net.minecraft.server.packs.resources;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@FunctionalInterface
public interface IoSupplier<T> {
    public static IoSupplier<InputStream> create(Path $$0) {
        return () -> Files.newInputStream((Path)$$0, (OpenOption[])new OpenOption[0]);
    }

    public static IoSupplier<InputStream> create(ZipFile $$0, ZipEntry $$1) {
        return () -> $$0.getInputStream($$1);
    }

    public T get() throws IOException;
}