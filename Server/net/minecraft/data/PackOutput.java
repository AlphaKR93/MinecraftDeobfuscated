/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.nio.file.Path
 */
package net.minecraft.data;

import java.nio.file.Path;
import net.minecraft.resources.ResourceLocation;

public class PackOutput {
    private final Path outputFolder;

    public PackOutput(Path $$0) {
        this.outputFolder = $$0;
    }

    public Path getOutputFolder() {
        return this.outputFolder;
    }

    public Path getOutputFolder(Target $$0) {
        return this.getOutputFolder().resolve($$0.directory);
    }

    public PathProvider createPathProvider(Target $$0, String $$1) {
        return new PathProvider(this, $$0, $$1);
    }

    public static enum Target {
        DATA_PACK("data"),
        RESOURCE_PACK("assets"),
        REPORTS("reports");

        final String directory;

        private Target(String $$0) {
            this.directory = $$0;
        }
    }

    public static class PathProvider {
        private final Path root;
        private final String kind;

        PathProvider(PackOutput $$0, Target $$1, String $$2) {
            this.root = $$0.getOutputFolder($$1);
            this.kind = $$2;
        }

        public Path file(ResourceLocation $$0, String $$1) {
            return this.root.resolve($$0.getNamespace()).resolve(this.kind).resolve($$0.getPath() + "." + $$1);
        }

        public Path json(ResourceLocation $$0) {
            return this.root.resolve($$0.getNamespace()).resolve(this.kind).resolve($$0.getPath() + ".json");
        }
    }
}