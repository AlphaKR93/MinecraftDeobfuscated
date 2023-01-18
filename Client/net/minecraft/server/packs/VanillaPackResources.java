/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.io.IOException
 *  java.io.InputStream
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.nio.file.Files
 *  java.nio.file.LinkOption
 *  java.nio.file.Path
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Map
 *  java.util.Optional
 *  java.util.Set
 *  java.util.function.BiConsumer
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.packs;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.BuiltInMetadata;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.slf4j.Logger;

public class VanillaPackResources
implements PackResources {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final BuiltInMetadata metadata;
    private final Set<String> namespaces;
    private final List<Path> rootPaths;
    private final Map<PackType, List<Path>> pathsForType;

    VanillaPackResources(BuiltInMetadata $$0, Set<String> $$1, List<Path> $$2, Map<PackType, List<Path>> $$3) {
        this.metadata = $$0;
        this.namespaces = $$1;
        this.rootPaths = $$2;
        this.pathsForType = $$3;
    }

    @Override
    @Nullable
    public IoSupplier<InputStream> getRootResource(String ... $$0) {
        FileUtil.validatePath($$0);
        List $$1 = List.of((Object[])$$0);
        for (Path $$2 : this.rootPaths) {
            Path $$3 = FileUtil.resolvePath($$2, (List<String>)$$1);
            if (!Files.exists((Path)$$3, (LinkOption[])new LinkOption[0]) || !PathPackResources.validatePath($$3)) continue;
            return IoSupplier.create($$3);
        }
        return null;
    }

    public void listRawPaths(PackType $$0, ResourceLocation $$12, Consumer<Path> $$2) {
        FileUtil.decomposePath($$12.getPath()).get().ifLeft($$3 -> {
            String $$4 = $$12.getNamespace();
            for (Path $$5 : (List)this.pathsForType.get((Object)$$0)) {
                Path $$6 = $$5.resolve($$4);
                $$2.accept((Object)FileUtil.resolvePath($$6, (List<String>)$$3));
            }
        }).ifRight($$1 -> LOGGER.error("Invalid path {}: {}", (Object)$$12, (Object)$$1.message()));
    }

    @Override
    public void listResources(PackType $$0, String $$12, String $$2, PackResources.ResourceOutput $$32) {
        FileUtil.decomposePath($$2).get().ifLeft($$3 -> {
            List $$4 = (List)this.pathsForType.get((Object)$$0);
            int $$5 = $$4.size();
            if ($$5 == 1) {
                VanillaPackResources.getResources($$32, $$12, (Path)$$4.get(0), (List<String>)$$3);
            } else if ($$5 > 1) {
                HashMap $$6 = new HashMap();
                for (int $$7 = 0; $$7 < $$5 - 1; ++$$7) {
                    VanillaPackResources.getResources((arg_0, arg_1) -> ((Map)$$6).putIfAbsent(arg_0, arg_1), $$12, (Path)$$4.get($$7), (List<String>)$$3);
                }
                Path $$8 = (Path)$$4.get($$5 - 1);
                if ($$6.isEmpty()) {
                    VanillaPackResources.getResources($$32, $$12, $$8, (List<String>)$$3);
                } else {
                    VanillaPackResources.getResources((arg_0, arg_1) -> ((Map)$$6).putIfAbsent(arg_0, arg_1), $$12, $$8, (List<String>)$$3);
                    $$6.forEach((BiConsumer)$$32);
                }
            }
        }).ifRight($$1 -> LOGGER.error("Invalid path {}: {}", (Object)$$2, (Object)$$1.message()));
    }

    private static void getResources(PackResources.ResourceOutput $$0, String $$1, Path $$2, List<String> $$3) {
        Path $$4 = $$2.resolve($$1);
        PathPackResources.listPath($$1, $$4, $$3, $$0);
    }

    @Override
    @Nullable
    public IoSupplier<InputStream> getResource(PackType $$0, ResourceLocation $$12) {
        return (IoSupplier)FileUtil.decomposePath($$12.getPath()).get().map($$2 -> {
            String $$3 = $$12.getNamespace();
            for (Path $$4 : (List)this.pathsForType.get((Object)$$0)) {
                Path $$5 = FileUtil.resolvePath($$4.resolve($$3), (List<String>)$$2);
                if (!Files.exists((Path)$$5, (LinkOption[])new LinkOption[0]) || !PathPackResources.validatePath($$5)) continue;
                return IoSupplier.create($$5);
            }
            return null;
        }, $$1 -> {
            LOGGER.error("Invalid path {}: {}", (Object)$$12, (Object)$$1.message());
            return null;
        });
    }

    @Override
    public Set<String> getNamespaces(PackType $$0) {
        return this.namespaces;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    @Nullable
    public <T> T getMetadataSection(MetadataSectionSerializer<T> $$0) {
        IoSupplier<InputStream> $$1 = this.getRootResource("pack.mcmeta");
        if ($$1 == null) return this.metadata.get($$0);
        try (InputStream $$2 = $$1.get();){
            T $$3 = AbstractPackResources.getMetadataFromStream($$0, $$2);
            if ($$3 == null) return this.metadata.get($$0);
            T t = $$3;
            return t;
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return this.metadata.get($$0);
    }

    @Override
    public String packId() {
        return "vanilla";
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public void close() {
    }

    public ResourceProvider asProvider() {
        return $$02 -> Optional.ofNullable(this.getResource(PackType.CLIENT_RESOURCES, $$02)).map($$0 -> new Resource(this, (IoSupplier<InputStream>)$$0));
    }
}