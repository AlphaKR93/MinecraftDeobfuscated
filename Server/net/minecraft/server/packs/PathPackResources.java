/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  java.io.IOException
 *  java.io.InputStream
 *  java.lang.Integer
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.nio.file.DirectoryStream
 *  java.nio.file.FileVisitOption
 *  java.nio.file.Files
 *  java.nio.file.LinkOption
 *  java.nio.file.NoSuchFileException
 *  java.nio.file.Path
 *  java.util.HashSet
 *  java.util.List
 *  java.util.Locale
 *  java.util.Set
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.packs;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;
import org.slf4j.Logger;

public class PathPackResources
extends AbstractPackResources {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Joiner PATH_JOINER = Joiner.on((String)"/");
    private final Path root;

    public PathPackResources(String $$0, Path $$1, boolean $$2) {
        super($$0, $$2);
        this.root = $$1;
    }

    @Override
    @Nullable
    public IoSupplier<InputStream> getRootResource(String ... $$0) {
        FileUtil.validatePath($$0);
        Path $$1 = FileUtil.resolvePath(this.root, (List<String>)List.of((Object[])$$0));
        if (Files.exists((Path)$$1, (LinkOption[])new LinkOption[0])) {
            return IoSupplier.create($$1);
        }
        return null;
    }

    public static boolean validatePath(Path $$0) {
        return true;
    }

    @Override
    @Nullable
    public IoSupplier<InputStream> getResource(PackType $$0, ResourceLocation $$1) {
        Path $$2 = this.root.resolve($$0.getDirectory()).resolve($$1.getNamespace());
        return PathPackResources.getResource($$1, $$2);
    }

    public static IoSupplier<InputStream> getResource(ResourceLocation $$0, Path $$12) {
        return (IoSupplier)FileUtil.decomposePath($$0.getPath()).get().map($$1 -> {
            Path $$2 = FileUtil.resolvePath($$12, (List<String>)$$1);
            return PathPackResources.returnFileIfExists($$2);
        }, $$1 -> {
            LOGGER.error("Invalid path {}: {}", (Object)$$0, (Object)$$1.message());
            return null;
        });
    }

    @Nullable
    private static IoSupplier<InputStream> returnFileIfExists(Path $$0) {
        if (Files.exists((Path)$$0, (LinkOption[])new LinkOption[0]) && PathPackResources.validatePath($$0)) {
            return IoSupplier.create($$0);
        }
        return null;
    }

    @Override
    public void listResources(PackType $$0, String $$12, String $$2, PackResources.ResourceOutput $$32) {
        FileUtil.decomposePath($$2).get().ifLeft($$3 -> {
            Path $$4 = this.root.resolve($$0.getDirectory()).resolve($$12);
            PathPackResources.listPath($$12, $$4, (List<String>)$$3, $$32);
        }).ifRight($$1 -> LOGGER.error("Invalid path {}: {}", (Object)$$2, (Object)$$1.message()));
    }

    public static void listPath(String $$02, Path $$12, List<String> $$2, PackResources.ResourceOutput $$32) {
        Path $$4 = FileUtil.resolvePath($$12, $$2);
        try (Stream $$52 = Files.find((Path)$$4, (int)Integer.MAX_VALUE, ($$0, $$1) -> $$1.isRegularFile(), (FileVisitOption[])new FileVisitOption[0]);){
            $$52.forEach($$3 -> {
                String $$4 = PATH_JOINER.join((Iterable)$$12.relativize($$3));
                ResourceLocation $$5 = ResourceLocation.tryBuild($$02, $$4);
                if ($$5 == null) {
                    Util.logAndPauseIfInIde(String.format((Locale)Locale.ROOT, (String)"Invalid path in pack: %s:%s, ignoring", (Object[])new Object[]{$$02, $$4}));
                } else {
                    $$32.accept($$5, IoSupplier.create($$3));
                }
            });
        }
        catch (NoSuchFileException $$52) {
        }
        catch (IOException $$6) {
            LOGGER.error("Failed to list path {}", (Object)$$4, (Object)$$6);
        }
    }

    @Override
    public Set<String> getNamespaces(PackType $$0) {
        HashSet $$1 = Sets.newHashSet();
        Path $$2 = this.root.resolve($$0.getDirectory());
        try (DirectoryStream $$32 = Files.newDirectoryStream((Path)$$2);){
            for (Path $$4 : $$32) {
                String $$5 = $$4.getFileName().toString();
                if ($$5.equals((Object)$$5.toLowerCase(Locale.ROOT))) {
                    $$1.add((Object)$$5);
                    continue;
                }
                LOGGER.warn("Ignored non-lowercase namespace: {} in {}", (Object)$$5, (Object)this.root);
            }
        }
        catch (NoSuchFileException $$32) {
        }
        catch (IOException $$6) {
            LOGGER.error("Failed to list path {}", (Object)$$2, (Object)$$6);
        }
        return $$1;
    }

    @Override
    public void close() {
    }
}