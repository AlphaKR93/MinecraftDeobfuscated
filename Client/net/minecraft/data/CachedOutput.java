/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.hash.HashCode
 *  java.io.IOException
 *  java.lang.Object
 *  java.nio.file.Files
 *  java.nio.file.OpenOption
 *  java.nio.file.Path
 *  java.nio.file.attribute.FileAttribute
 */
package net.minecraft.data;

import com.google.common.hash.HashCode;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;

public interface CachedOutput {
    public static final CachedOutput NO_CACHE = ($$0, $$1, $$2) -> {
        Files.createDirectories((Path)$$0.getParent(), (FileAttribute[])new FileAttribute[0]);
        Files.write((Path)$$0, (byte[])$$1, (OpenOption[])new OpenOption[0]);
    };

    public void writeIfNeeded(Path var1, byte[] var2, HashCode var3) throws IOException;
}