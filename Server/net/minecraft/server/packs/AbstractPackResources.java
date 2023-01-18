/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  java.io.BufferedReader
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.InputStreamReader
 *  java.io.Reader
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.nio.charset.StandardCharsets
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.server.packs;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

public abstract class AbstractPackResources
implements PackResources {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final String name;
    private final boolean isBuiltin;

    protected AbstractPackResources(String $$0, boolean $$1) {
        this.name = $$0;
        this.isBuiltin = $$1;
    }

    @Override
    @Nullable
    public <T> T getMetadataSection(MetadataSectionSerializer<T> $$0) throws IOException {
        IoSupplier $$1 = this.getRootResource(new String[]{"pack.mcmeta"});
        if ($$1 == null) {
            return null;
        }
        try (InputStream $$2 = (InputStream)$$1.get();){
            T t = AbstractPackResources.getMetadataFromStream($$0, $$2);
            return t;
        }
    }

    /*
     * WARNING - void declaration
     */
    @Nullable
    public static <T> T getMetadataFromStream(MetadataSectionSerializer<T> $$0, InputStream $$1) {
        void $$6;
        try (BufferedReader $$2 = new BufferedReader((Reader)new InputStreamReader($$1, StandardCharsets.UTF_8));){
            JsonObject $$3 = GsonHelper.parse((Reader)$$2);
        }
        catch (Exception $$5) {
            LOGGER.error("Couldn't load {} metadata", (Object)$$0.getMetadataSectionName(), (Object)$$5);
            return null;
        }
        if (!$$6.has($$0.getMetadataSectionName())) {
            return null;
        }
        try {
            return $$0.fromJson(GsonHelper.getAsJsonObject((JsonObject)$$6, $$0.getMetadataSectionName()));
        }
        catch (Exception $$7) {
            LOGGER.error("Couldn't load {} metadata", (Object)$$0.getMetadataSectionName(), (Object)$$7);
            return null;
        }
    }

    @Override
    public String packId() {
        return this.name;
    }

    @Override
    public boolean isBuiltin() {
        return this.isBuiltin;
    }
}