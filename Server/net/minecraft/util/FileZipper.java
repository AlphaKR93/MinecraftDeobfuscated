/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.mojang.logging.LogUtils
 *  java.io.Closeable
 *  java.io.File
 *  java.io.IOException
 *  java.io.UncheckedIOException
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.String
 *  java.nio.charset.StandardCharsets
 *  java.nio.file.CopyOption
 *  java.nio.file.FileSystem
 *  java.nio.file.FileVisitOption
 *  java.nio.file.Files
 *  java.nio.file.LinkOption
 *  java.nio.file.OpenOption
 *  java.nio.file.Path
 *  java.nio.file.attribute.FileAttribute
 *  java.util.List
 *  java.util.Map
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 *  org.slf4j.Logger
 */
package net.minecraft.util;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import org.slf4j.Logger;

public class FileZipper
implements Closeable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Path outputFile;
    private final Path tempFile;
    private final FileSystem fs;

    public FileZipper(Path $$0) {
        this.outputFile = $$0;
        this.tempFile = $$0.resolveSibling($$0.getFileName().toString() + "_tmp");
        try {
            this.fs = Util.ZIP_FILE_SYSTEM_PROVIDER.newFileSystem(this.tempFile, (Map)ImmutableMap.of((Object)"create", (Object)"true"));
        }
        catch (IOException $$1) {
            throw new UncheckedIOException($$1);
        }
    }

    public void add(Path $$0, String $$1) {
        try {
            Path $$2 = this.fs.getPath(File.separator, new String[0]);
            Path $$3 = $$2.resolve($$0.toString());
            Files.createDirectories((Path)$$3.getParent(), (FileAttribute[])new FileAttribute[0]);
            Files.write((Path)$$3, (byte[])$$1.getBytes(StandardCharsets.UTF_8), (OpenOption[])new OpenOption[0]);
        }
        catch (IOException $$4) {
            throw new UncheckedIOException($$4);
        }
    }

    public void add(Path $$0, File $$1) {
        try {
            Path $$2 = this.fs.getPath(File.separator, new String[0]);
            Path $$3 = $$2.resolve($$0.toString());
            Files.createDirectories((Path)$$3.getParent(), (FileAttribute[])new FileAttribute[0]);
            Files.copy((Path)$$1.toPath(), (Path)$$3, (CopyOption[])new CopyOption[0]);
        }
        catch (IOException $$4) {
            throw new UncheckedIOException($$4);
        }
    }

    public void add(Path $$02) {
        try {
            Path $$12 = this.fs.getPath(File.separator, new String[0]);
            if (Files.isRegularFile((Path)$$02, (LinkOption[])new LinkOption[0])) {
                Path $$2 = $$12.resolve($$02.getParent().relativize($$02).toString());
                Files.copy((Path)$$2, (Path)$$02, (CopyOption[])new CopyOption[0]);
                return;
            }
            try (Stream $$3 = Files.find((Path)$$02, (int)Integer.MAX_VALUE, ($$0, $$1) -> $$1.isRegularFile(), (FileVisitOption[])new FileVisitOption[0]);){
                for (Path $$4 : (List)$$3.collect(Collectors.toList())) {
                    Path $$5 = $$12.resolve($$02.relativize($$4).toString());
                    Files.createDirectories((Path)$$5.getParent(), (FileAttribute[])new FileAttribute[0]);
                    Files.copy((Path)$$4, (Path)$$5, (CopyOption[])new CopyOption[0]);
                }
            }
        }
        catch (IOException $$6) {
            throw new UncheckedIOException($$6);
        }
    }

    public void close() {
        try {
            this.fs.close();
            Files.move((Path)this.tempFile, (Path)this.outputFile, (CopyOption[])new CopyOption[0]);
            LOGGER.info("Compressed to {}", (Object)this.outputFile);
        }
        catch (IOException $$0) {
            throw new UncheckedIOException($$0);
        }
    }
}