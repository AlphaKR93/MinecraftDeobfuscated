/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  java.io.Closeable
 *  java.io.File
 *  java.io.IOException
 *  java.io.InputStream
 *  java.lang.CharSequence
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.ArrayList
 *  java.util.Enumeration
 *  java.util.HashSet
 *  java.util.Locale
 *  java.util.Set
 *  java.util.zip.ZipEntry
 *  java.util.zip.ZipFile
 *  javax.annotation.Nullable
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 */
package net.minecraft.server.packs;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

public class FilePackResources
extends AbstractPackResources {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Splitter SPLITTER = Splitter.on((char)'/').omitEmptyStrings().limit(3);
    private final File file;
    @Nullable
    private ZipFile zipFile;
    private boolean failedToLoad;

    public FilePackResources(String $$0, File $$1, boolean $$2) {
        super($$0, $$2);
        this.file = $$1;
    }

    @Nullable
    private ZipFile getOrCreateZipFile() {
        if (this.failedToLoad) {
            return null;
        }
        if (this.zipFile == null) {
            try {
                this.zipFile = new ZipFile(this.file);
            }
            catch (IOException $$0) {
                LOGGER.error("Failed to open pack {}", (Object)this.file, (Object)$$0);
                this.failedToLoad = true;
                return null;
            }
        }
        return this.zipFile;
    }

    private static String getPathFromLocation(PackType $$0, ResourceLocation $$1) {
        return String.format((Locale)Locale.ROOT, (String)"%s/%s/%s", (Object[])new Object[]{$$0.getDirectory(), $$1.getNamespace(), $$1.getPath()});
    }

    @Override
    @Nullable
    public IoSupplier<InputStream> getRootResource(String ... $$0) {
        return this.getResource(String.join((CharSequence)"/", (CharSequence[])$$0));
    }

    @Override
    public IoSupplier<InputStream> getResource(PackType $$0, ResourceLocation $$1) {
        return this.getResource(FilePackResources.getPathFromLocation($$0, $$1));
    }

    @Nullable
    private IoSupplier<InputStream> getResource(String $$0) {
        ZipFile $$1 = this.getOrCreateZipFile();
        if ($$1 == null) {
            return null;
        }
        ZipEntry $$2 = $$1.getEntry($$0);
        if ($$2 == null) {
            return null;
        }
        return IoSupplier.create($$1, $$2);
    }

    @Override
    public Set<String> getNamespaces(PackType $$0) {
        ZipFile $$1 = this.getOrCreateZipFile();
        if ($$1 == null) {
            return Set.of();
        }
        Enumeration $$2 = $$1.entries();
        HashSet $$3 = Sets.newHashSet();
        while ($$2.hasMoreElements()) {
            ArrayList $$6;
            ZipEntry $$4 = (ZipEntry)$$2.nextElement();
            String $$5 = $$4.getName();
            if (!$$5.startsWith($$0.getDirectory() + "/") || ($$6 = Lists.newArrayList((Iterable)SPLITTER.split((CharSequence)$$5))).size() <= 1) continue;
            String $$7 = (String)$$6.get(1);
            if ($$7.equals((Object)$$7.toLowerCase(Locale.ROOT))) {
                $$3.add((Object)$$7);
                continue;
            }
            LOGGER.warn("Ignored non-lowercase namespace: {} in {}", (Object)$$7, (Object)this.file);
        }
        return $$3;
    }

    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    @Override
    public void close() {
        if (this.zipFile != null) {
            IOUtils.closeQuietly((Closeable)this.zipFile);
            this.zipFile = null;
        }
    }

    @Override
    public void listResources(PackType $$0, String $$1, String $$2, PackResources.ResourceOutput $$3) {
        ZipFile $$4 = this.getOrCreateZipFile();
        if ($$4 == null) {
            return;
        }
        Enumeration $$5 = $$4.entries();
        String $$6 = $$0.getDirectory() + "/" + $$1 + "/";
        String $$7 = $$6 + $$2 + "/";
        while ($$5.hasMoreElements()) {
            String $$9;
            ZipEntry $$8 = (ZipEntry)$$5.nextElement();
            if ($$8.isDirectory() || !($$9 = $$8.getName()).startsWith($$7)) continue;
            String $$10 = $$9.substring($$6.length());
            ResourceLocation $$11 = ResourceLocation.tryBuild($$1, $$10);
            if ($$11 != null) {
                $$3.accept($$11, IoSupplier.create($$4, $$8));
                continue;
            }
            LOGGER.warn("Invalid path in datapack: {}:{}, ignoring", (Object)$$1, (Object)$$10);
        }
    }
}