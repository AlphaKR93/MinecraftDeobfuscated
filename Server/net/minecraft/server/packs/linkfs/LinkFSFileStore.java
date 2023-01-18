/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.io.IOException
 *  java.lang.Class
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.UnsupportedOperationException
 *  java.nio.file.FileStore
 *  java.nio.file.attribute.BasicFileAttributeView
 *  java.nio.file.attribute.FileAttributeView
 *  java.nio.file.attribute.FileStoreAttributeView
 *  javax.annotation.Nullable
 */
package net.minecraft.server.packs.linkfs;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;
import javax.annotation.Nullable;

class LinkFSFileStore
extends FileStore {
    private final String name;

    public LinkFSFileStore(String $$0) {
        this.name = $$0;
    }

    public String name() {
        return this.name;
    }

    public String type() {
        return "index";
    }

    public boolean isReadOnly() {
        return true;
    }

    public long getTotalSpace() {
        return 0L;
    }

    public long getUsableSpace() {
        return 0L;
    }

    public long getUnallocatedSpace() {
        return 0L;
    }

    public boolean supportsFileAttributeView(Class<? extends FileAttributeView> $$0) {
        return $$0 == BasicFileAttributeView.class;
    }

    public boolean supportsFileAttributeView(String $$0) {
        return "basic".equals((Object)$$0);
    }

    @Nullable
    public <V extends FileStoreAttributeView> V getFileStoreAttributeView(Class<V> $$0) {
        return null;
    }

    public Object getAttribute(String $$0) throws IOException {
        throw new UnsupportedOperationException();
    }
}