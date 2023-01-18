/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.mojang.logging.LogUtils
 *  java.io.IOException
 *  java.lang.Class
 *  java.lang.Exception
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Throwable
 *  java.net.URI
 *  java.net.URL
 *  java.nio.file.FileSystemAlreadyExistsException
 *  java.nio.file.FileSystemNotFoundException
 *  java.nio.file.FileSystems
 *  java.nio.file.Files
 *  java.nio.file.LinkOption
 *  java.nio.file.Path
 *  java.nio.file.Paths
 *  java.util.ArrayList
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.Collections
 *  java.util.EnumMap
 *  java.util.Enumeration
 *  java.util.HashSet
 *  java.util.LinkedHashSet
 *  java.util.List
 *  java.util.Map
 *  java.util.Set
 *  java.util.function.Consumer
 *  org.slf4j.Logger
 */
package net.minecraft.server.packs;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.server.packs.BuiltInMetadata;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import org.slf4j.Logger;

public class VanillaPackResourcesBuilder {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static Consumer<VanillaPackResourcesBuilder> developmentConfig = $$0 -> {};
    private static final Map<PackType, Path> ROOT_DIR_BY_TYPE = (Map)Util.make(() -> {
        Class<VanillaPackResources> clazz = VanillaPackResources.class;
        synchronized (VanillaPackResources.class) {
            ImmutableMap.Builder $$0 = ImmutableMap.builder();
            for (PackType $$1 : PackType.values()) {
                String $$2 = "/" + $$1.getDirectory() + "/.mcassetsroot";
                URL $$3 = VanillaPackResources.class.getResource($$2);
                if ($$3 == null) {
                    LOGGER.error("File {} does not exist in classpath", (Object)$$2);
                    continue;
                }
                try {
                    URI $$4 = $$3.toURI();
                    String $$5 = $$4.getScheme();
                    if (!"jar".equals((Object)$$5) && !"file".equals((Object)$$5)) {
                        LOGGER.warn("Assets URL '{}' uses unexpected schema", (Object)$$4);
                    }
                    Path $$6 = VanillaPackResourcesBuilder.safeGetPath($$4);
                    $$0.put((Object)$$1, (Object)$$6.getParent());
                }
                catch (Exception $$7) {
                    LOGGER.error("Couldn't resolve path to vanilla assets", (Throwable)$$7);
                }
            }
            // ** MonitorExit[var0] (shouldn't be in output)
            return $$0.build();
        }
    });
    private final Set<Path> rootPaths = new LinkedHashSet();
    private final Map<PackType, Set<Path>> pathsForType = new EnumMap(PackType.class);
    private BuiltInMetadata metadata = BuiltInMetadata.of();
    private final Set<String> namespaces = new HashSet();

    private static Path safeGetPath(URI $$0) throws IOException {
        try {
            return Paths.get((URI)$$0);
        }
        catch (FileSystemNotFoundException fileSystemNotFoundException) {
        }
        catch (Throwable $$1) {
            LOGGER.warn("Unable to get path for: {}", (Object)$$0, (Object)$$1);
        }
        try {
            FileSystems.newFileSystem((URI)$$0, (Map)Collections.emptyMap());
        }
        catch (FileSystemAlreadyExistsException fileSystemAlreadyExistsException) {
            // empty catch block
        }
        return Paths.get((URI)$$0);
    }

    private boolean validateDirPath(Path $$0) {
        if (!Files.exists((Path)$$0, (LinkOption[])new LinkOption[0])) {
            return false;
        }
        if (!Files.isDirectory((Path)$$0, (LinkOption[])new LinkOption[0])) {
            throw new IllegalArgumentException("Path " + $$0.toAbsolutePath() + " is not directory");
        }
        return true;
    }

    private void pushRootPath(Path $$0) {
        if (this.validateDirPath($$0)) {
            this.rootPaths.add((Object)$$0);
        }
    }

    private void pushPathForType(PackType $$02, Path $$1) {
        if (this.validateDirPath($$1)) {
            ((Set)this.pathsForType.computeIfAbsent((Object)$$02, $$0 -> new LinkedHashSet())).add((Object)$$1);
        }
    }

    public VanillaPackResourcesBuilder pushJarResources() {
        ROOT_DIR_BY_TYPE.forEach(($$0, $$1) -> {
            this.pushRootPath($$1.getParent());
            this.pushPathForType((PackType)((Object)$$0), (Path)$$1);
        });
        return this;
    }

    public VanillaPackResourcesBuilder pushClasspathResources(PackType $$0, Class<?> $$1) {
        Enumeration $$2 = null;
        try {
            $$2 = $$1.getClassLoader().getResources($$0.getDirectory() + "/");
        }
        catch (IOException iOException) {
            // empty catch block
        }
        while ($$2 != null && $$2.hasMoreElements()) {
            URL $$3 = (URL)$$2.nextElement();
            try {
                URI $$4 = $$3.toURI();
                if (!"file".equals((Object)$$4.getScheme())) continue;
                Path $$5 = Paths.get((URI)$$4);
                this.pushRootPath($$5.getParent());
                this.pushPathForType($$0, $$5);
            }
            catch (Exception $$6) {
                LOGGER.error("Failed to extract path from {}", (Object)$$3, (Object)$$6);
            }
        }
        return this;
    }

    public VanillaPackResourcesBuilder applyDevelopmentConfig() {
        developmentConfig.accept((Object)this);
        return this;
    }

    public VanillaPackResourcesBuilder pushUniversalPath(Path $$0) {
        this.pushRootPath($$0);
        for (PackType $$1 : PackType.values()) {
            this.pushPathForType($$1, $$0.resolve($$1.getDirectory()));
        }
        return this;
    }

    public VanillaPackResourcesBuilder pushAssetPath(PackType $$0, Path $$1) {
        this.pushRootPath($$1);
        this.pushPathForType($$0, $$1);
        return this;
    }

    public VanillaPackResourcesBuilder setMetadata(BuiltInMetadata $$0) {
        this.metadata = $$0;
        return this;
    }

    public VanillaPackResourcesBuilder exposeNamespace(String ... $$0) {
        this.namespaces.addAll((Collection)Arrays.asList((Object[])$$0));
        return this;
    }

    public VanillaPackResources build() {
        EnumMap $$0 = new EnumMap(PackType.class);
        for (PackType $$1 : PackType.values()) {
            List<Path> $$2 = VanillaPackResourcesBuilder.copyAndReverse((Collection<Path>)((Collection)this.pathsForType.getOrDefault((Object)$$1, (Object)Set.of())));
            $$0.put((Object)$$1, $$2);
        }
        return new VanillaPackResources(this.metadata, (Set<String>)Set.copyOf(this.namespaces), VanillaPackResourcesBuilder.copyAndReverse(this.rootPaths), (Map<PackType, List<Path>>)$$0);
    }

    private static List<Path> copyAndReverse(Collection<Path> $$0) {
        ArrayList $$1 = new ArrayList($$0);
        Collections.reverse((List)$$1);
        return List.copyOf((Collection)$$1);
    }
}