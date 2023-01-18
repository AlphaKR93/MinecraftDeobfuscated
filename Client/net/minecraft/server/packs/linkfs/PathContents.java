/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.nio.file.Path
 *  java.util.Map
 */
package net.minecraft.server.packs.linkfs;

import java.nio.file.Path;
import java.util.Map;
import net.minecraft.server.packs.linkfs.LinkFSPath;

interface PathContents {
    public static final PathContents MISSING = new PathContents(){

        public String toString() {
            return "empty";
        }
    };
    public static final PathContents RELATIVE = new PathContents(){

        public String toString() {
            return "relative";
        }
    };

    public record DirectoryContents(Map<String, LinkFSPath> children) implements PathContents
    {
    }

    public record FileContents(Path contents) implements PathContents
    {
    }
}