/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.mojang.logging.LogUtils
 *  java.io.IOException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Map
 *  java.util.SortedMap
 *  java.util.TreeMap
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.resources.language;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.metadata.language.LanguageMetadataSection;
import net.minecraft.locale.Language;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.slf4j.Logger;

public class LanguageManager
implements ResourceManagerReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String DEFAULT_LANGUAGE_CODE = "en_us";
    private static final LanguageInfo DEFAULT_LANGUAGE = new LanguageInfo("US", "English", false);
    private Map<String, LanguageInfo> languages = ImmutableMap.of((Object)"en_us", (Object)((Object)DEFAULT_LANGUAGE));
    private String currentCode;

    public LanguageManager(String $$0) {
        this.currentCode = $$0;
    }

    private static Map<String, LanguageInfo> extractLanguages(Stream<PackResources> $$0) {
        HashMap $$1 = Maps.newHashMap();
        $$0.forEach(arg_0 -> LanguageManager.lambda$extractLanguages$0((Map)$$1, arg_0));
        return ImmutableMap.copyOf((Map)$$1);
    }

    @Override
    public void onResourceManagerReload(ResourceManager $$0) {
        LanguageInfo $$3;
        this.languages = LanguageManager.extractLanguages($$0.listPacks());
        ArrayList $$1 = new ArrayList(2);
        boolean $$2 = DEFAULT_LANGUAGE.bidirectional();
        $$1.add((Object)DEFAULT_LANGUAGE_CODE);
        if (!this.currentCode.equals((Object)DEFAULT_LANGUAGE_CODE) && ($$3 = (LanguageInfo)((Object)this.languages.get((Object)this.currentCode))) != null) {
            $$1.add((Object)this.currentCode);
            $$2 = $$3.bidirectional();
        }
        ClientLanguage $$4 = ClientLanguage.loadFrom($$0, (List<String>)$$1, $$2);
        I18n.setLanguage($$4);
        Language.inject($$4);
    }

    public void setSelected(String $$0) {
        this.currentCode = $$0;
    }

    public String getSelected() {
        return this.currentCode;
    }

    public SortedMap<String, LanguageInfo> getLanguages() {
        return new TreeMap(this.languages);
    }

    @Nullable
    public LanguageInfo getLanguage(String $$0) {
        return (LanguageInfo)((Object)this.languages.get((Object)$$0));
    }

    private static /* synthetic */ void lambda$extractLanguages$0(Map $$0, PackResources $$1) {
        try {
            LanguageMetadataSection $$2 = $$1.getMetadataSection(LanguageMetadataSection.TYPE);
            if ($$2 != null) {
                $$2.languages().forEach((arg_0, arg_1) -> ((Map)$$0).putIfAbsent(arg_0, arg_1));
            }
        }
        catch (IOException | RuntimeException $$3) {
            LOGGER.warn("Unable to parse language metadata section of resourcepack: {}", (Object)$$1.packId(), (Object)$$3);
        }
    }
}