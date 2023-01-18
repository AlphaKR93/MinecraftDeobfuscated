/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.io.File
 *  java.io.IOException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.nio.file.DirectoryStream
 *  java.nio.file.FileSystem
 *  java.nio.file.FileSystems
 *  java.nio.file.Files
 *  java.nio.file.LinkOption
 *  java.nio.file.NoSuchFileException
 *  java.nio.file.Path
 *  java.nio.file.attribute.BasicFileAttributes
 *  java.util.function.BiConsumer
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.packs.repository;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.linkfs.LinkFileSystem;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import org.slf4j.Logger;

public class FolderRepositorySource
implements RepositorySource {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Path folder;
    private final PackType packType;
    private final PackSource packSource;

    public FolderRepositorySource(Path $$0, PackType $$1, PackSource $$2) {
        this.folder = $$0;
        this.packType = $$1;
        this.packSource = $$2;
    }

    private static String nameFromPath(Path $$0) {
        return $$0.getFileName().toString();
    }

    @Override
    public void loadPacks(Consumer<Pack> $$0) {
        try {
            FileUtil.createDirectoriesSafe(this.folder);
            FolderRepositorySource.discoverPacks(this.folder, false, (BiConsumer<Path, Pack.ResourcesSupplier>)((BiConsumer)($$1, $$2) -> {
                String $$3 = FolderRepositorySource.nameFromPath($$1);
                Pack $$4 = Pack.readMetaAndCreate("file/" + $$3, Component.literal($$3), false, $$2, this.packType, Pack.Position.TOP, this.packSource);
                if ($$4 != null) {
                    $$0.accept((Object)$$4);
                }
            }));
        }
        catch (IOException $$12) {
            LOGGER.warn("Failed to list packs in {}", (Object)this.folder, (Object)$$12);
        }
    }

    public static void discoverPacks(Path $$0, boolean $$1, BiConsumer<Path, Pack.ResourcesSupplier> $$2) throws IOException {
        try (DirectoryStream $$3 = Files.newDirectoryStream((Path)$$0);){
            for (Path $$4 : $$3) {
                Pack.ResourcesSupplier $$5 = FolderRepositorySource.detectPackResources($$4, $$1);
                if ($$5 == null) continue;
                $$2.accept((Object)$$4, (Object)$$5);
            }
        }
    }

    /*
     * WARNING - void declaration
     */
    @Nullable
    public static Pack.ResourcesSupplier detectPackResources(Path $$0, boolean $$1) {
        FileSystem $$6;
        void $$5;
        try {
            BasicFileAttributes $$22 = Files.readAttributes((Path)$$0, BasicFileAttributes.class, (LinkOption[])new LinkOption[0]);
        }
        catch (NoSuchFileException $$3) {
            return null;
        }
        catch (IOException $$4) {
            LOGGER.warn("Failed to read properties of '{}', ignoring", (Object)$$0, (Object)$$4);
            return null;
        }
        if ($$5.isDirectory() && Files.isRegularFile((Path)$$0.resolve("pack.mcmeta"), (LinkOption[])new LinkOption[0])) {
            return $$2 -> new PathPackResources($$2, $$0, $$1);
        }
        if ($$5.isRegularFile() && $$0.getFileName().toString().endsWith(".zip") && (($$6 = $$0.getFileSystem()) == FileSystems.getDefault() || $$6 instanceof LinkFileSystem)) {
            File $$7 = $$0.toFile();
            return $$2 -> new FilePackResources($$2, $$7, $$1);
        }
        LOGGER.info("Found non-pack entry '{}', ignoring", (Object)$$0);
        return null;
    }
}