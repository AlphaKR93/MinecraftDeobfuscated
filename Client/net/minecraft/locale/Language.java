/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.logging.LogUtils
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.InputStreamReader
 *  java.io.Reader
 *  java.lang.CharSequence
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.nio.charset.StandardCharsets
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Optional
 *  java.util.function.BiConsumer
 *  java.util.regex.Pattern
 *  org.slf4j.Logger
 */
package net.minecraft.locale;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.StringDecomposer;
import org.slf4j.Logger;

public abstract class Language {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();
    private static final Pattern UNSUPPORTED_FORMAT_PATTERN = Pattern.compile((String)"%(\\d+\\$)?[\\d.]*[df]");
    public static final String DEFAULT = "en_us";
    private static volatile Language instance = Language.loadDefault();

    private static Language loadDefault() {
        ImmutableMap.Builder $$0 = ImmutableMap.builder();
        BiConsumer $$1 = (arg_0, arg_1) -> ((ImmutableMap.Builder)$$0).put(arg_0, arg_1);
        String $$2 = "/assets/minecraft/lang/en_us.json";
        try (InputStream $$3 = Language.class.getResourceAsStream("/assets/minecraft/lang/en_us.json");){
            Language.loadFromJson($$3, (BiConsumer<String, String>)$$1);
        }
        catch (JsonParseException | IOException $$4) {
            LOGGER.error("Couldn't read strings from {}", (Object)"/assets/minecraft/lang/en_us.json", (Object)$$4);
        }
        ImmutableMap $$5 = $$0.build();
        return new Language((Map)$$5){
            final /* synthetic */ Map val$storage;
            {
                this.val$storage = map;
            }

            @Override
            public String getOrDefault(String $$0, String $$1) {
                return (String)this.val$storage.getOrDefault((Object)$$0, (Object)$$1);
            }

            @Override
            public boolean has(String $$0) {
                return this.val$storage.containsKey((Object)$$0);
            }

            @Override
            public boolean isDefaultRightToLeft() {
                return false;
            }

            @Override
            public FormattedCharSequence getVisualOrder(FormattedText $$0) {
                return $$12 -> $$0.visit(($$1, $$2) -> StringDecomposer.iterateFormatted($$2, $$1, $$12) ? Optional.empty() : FormattedText.STOP_ITERATION, Style.EMPTY).isPresent();
            }
        };
    }

    public static void loadFromJson(InputStream $$0, BiConsumer<String, String> $$1) {
        JsonObject $$2 = (JsonObject)GSON.fromJson((Reader)new InputStreamReader($$0, StandardCharsets.UTF_8), JsonObject.class);
        for (Map.Entry $$3 : $$2.entrySet()) {
            String $$4 = UNSUPPORTED_FORMAT_PATTERN.matcher((CharSequence)GsonHelper.convertToString((JsonElement)$$3.getValue(), (String)$$3.getKey())).replaceAll("%$1s");
            $$1.accept((Object)((String)$$3.getKey()), (Object)$$4);
        }
    }

    public static Language getInstance() {
        return instance;
    }

    public static void inject(Language $$0) {
        instance = $$0;
    }

    public String getOrDefault(String $$0) {
        return this.getOrDefault($$0, $$0);
    }

    public abstract String getOrDefault(String var1, String var2);

    public abstract boolean has(String var1);

    public abstract boolean isDefaultRightToLeft();

    public abstract FormattedCharSequence getVisualOrder(FormattedText var1);

    public List<FormattedCharSequence> getVisualOrder(List<FormattedText> $$0) {
        return (List)$$0.stream().map(this::getVisualOrder).collect(ImmutableList.toImmutableList());
    }
}