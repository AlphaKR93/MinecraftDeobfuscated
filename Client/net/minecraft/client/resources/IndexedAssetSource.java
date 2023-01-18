/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Splitter
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.logging.LogUtils
 *  java.io.BufferedReader
 *  java.io.IOException
 *  java.io.Reader
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.String
 *  java.nio.charset.Charset
 *  java.nio.charset.StandardCharsets
 *  java.nio.file.Files
 *  java.nio.file.Path
 *  java.util.List
 *  java.util.Map$Entry
 *  org.slf4j.Logger
 */
package net.minecraft.client.resources;

import com.google.common.base.Splitter;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import net.minecraft.server.packs.linkfs.LinkFileSystem;
import net.minecraft.util.GsonHelper;
import org.slf4j.Logger;

public class IndexedAssetSource {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Splitter PATH_SPLITTER = Splitter.on((char)'/');

    public static Path createIndexFs(Path $$0, String $$1) {
        Path $$2 = $$0.resolve("objects");
        LinkFileSystem.Builder $$3 = LinkFileSystem.builder();
        Path $$4 = $$0.resolve("indexes/" + $$1 + ".json");
        try (BufferedReader $$5 = Files.newBufferedReader((Path)$$4, (Charset)StandardCharsets.UTF_8);){
            JsonObject $$6 = GsonHelper.parse((Reader)$$5);
            JsonObject $$7 = GsonHelper.getAsJsonObject($$6, "objects", null);
            if ($$7 != null) {
                for (Map.Entry $$8 : $$7.entrySet()) {
                    JsonObject $$9 = (JsonObject)$$8.getValue();
                    String $$10 = (String)$$8.getKey();
                    List $$11 = PATH_SPLITTER.splitToList((CharSequence)$$10);
                    String $$12 = GsonHelper.getAsString($$9, "hash");
                    Path $$13 = $$2.resolve($$12.substring(0, 2) + "/" + $$12);
                    $$3.put((List<String>)$$11, $$13);
                }
            }
        }
        catch (JsonParseException $$14) {
            LOGGER.error("Unable to parse resource index file: {}", (Object)$$4);
        }
        catch (IOException $$15) {
            LOGGER.error("Can't open the resource index file: {}", (Object)$$4);
        }
        return $$3.build("index-" + $$1).getPath("/", new String[0]);
    }
}