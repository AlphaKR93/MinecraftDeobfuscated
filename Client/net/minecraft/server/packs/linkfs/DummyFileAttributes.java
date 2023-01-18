/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.nio.file.attribute.BasicFileAttributes
 *  java.nio.file.attribute.FileTime
 *  javax.annotation.Nullable
 */
package net.minecraft.server.packs.linkfs;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import javax.annotation.Nullable;

abstract class DummyFileAttributes
implements BasicFileAttributes {
    private static final FileTime EPOCH = FileTime.fromMillis((long)0L);

    DummyFileAttributes() {
    }

    public FileTime lastModifiedTime() {
        return EPOCH;
    }

    public FileTime lastAccessTime() {
        return EPOCH;
    }

    public FileTime creationTime() {
        return EPOCH;
    }

    public boolean isSymbolicLink() {
        return false;
    }

    public boolean isOther() {
        return false;
    }

    public long size() {
        return 0L;
    }

    @Nullable
    public Object fileKey() {
        return null;
    }
}