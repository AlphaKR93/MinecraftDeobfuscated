/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.io.BufferedReader
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.InputStreamReader
 *  java.io.Reader
 *  java.lang.Object
 *  java.lang.String
 *  java.nio.charset.StandardCharsets
 *  javax.annotation.Nullable
 */
package net.minecraft.server.packs.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.ResourceMetadata;

public class Resource {
    private final PackResources source;
    private final IoSupplier<InputStream> streamSupplier;
    private final IoSupplier<ResourceMetadata> metadataSupplier;
    @Nullable
    private ResourceMetadata cachedMetadata;

    public Resource(PackResources $$0, IoSupplier<InputStream> $$1, IoSupplier<ResourceMetadata> $$2) {
        this.source = $$0;
        this.streamSupplier = $$1;
        this.metadataSupplier = $$2;
    }

    public Resource(PackResources $$0, IoSupplier<InputStream> $$1) {
        this.source = $$0;
        this.streamSupplier = $$1;
        this.metadataSupplier = ResourceMetadata.EMPTY_SUPPLIER;
        this.cachedMetadata = ResourceMetadata.EMPTY;
    }

    public PackResources source() {
        return this.source;
    }

    public String sourcePackId() {
        return this.source.packId();
    }

    public boolean isBuiltin() {
        return this.source.isBuiltin();
    }

    public InputStream open() throws IOException {
        return this.streamSupplier.get();
    }

    public BufferedReader openAsReader() throws IOException {
        return new BufferedReader((Reader)new InputStreamReader(this.open(), StandardCharsets.UTF_8));
    }

    public ResourceMetadata metadata() throws IOException {
        if (this.cachedMetadata == null) {
            this.cachedMetadata = this.metadataSupplier.get();
        }
        return this.cachedMetadata;
    }
}