/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.logging.LogUtils
 *  java.io.IOException
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Map
 *  java.util.SortedSet
 *  java.util.stream.Stream
 *  org.slf4j.Logger
 */
package net.minecraft.client.resources.language;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.stream.Stream;
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
    private static final LanguageInfo DEFAULT_LANGUAGE = new LanguageInfo("en_us", "US", "English", false);
    private Map<String, LanguageInfo> languages = ImmutableMap.of((Object)"en_us", (Object)DEFAULT_LANGUAGE);
    private String currentCode;
    private LanguageInfo currentLanguage = DEFAULT_LANGUAGE;

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
        this.languages = LanguageManager.extractLanguages($$0.listPacks());
        LanguageInfo $$1 = (LanguageInfo)this.languages.getOrDefault((Object)DEFAULT_LANGUAGE_CODE, (Object)DEFAULT_LANGUAGE);
        this.currentLanguage = (LanguageInfo)this.languages.getOrDefault((Object)this.currentCode, (Object)$$1);
        ArrayList $$2 = Lists.newArrayList((Object[])new LanguageInfo[]{$$1});
        if (this.currentLanguage != $$1) {
            $$2.add((Object)this.currentLanguage);
        }
        ClientLanguage $$3 = ClientLanguage.loadFrom($$0, (List<LanguageInfo>)$$2);
        I18n.setLanguage($$3);
        Language.inject($$3);
    }

    public void setSelected(LanguageInfo $$0) {
        this.currentCode = $$0.getCode();
        this.currentLanguage = $$0;
    }

    public LanguageInfo getSelected() {
        return this.currentLanguage;
    }

    public SortedSet<LanguageInfo> getLanguages() {
        return Sets.newTreeSet((Iterable)this.languages.values());
    }

    public LanguageInfo getLanguage(String $$0) {
        return (LanguageInfo)this.languages.get((Object)$$0);
    }

    private static /* synthetic */ void lambda$extractLanguages$0(Map $$0, PackResources $$1) {
        try {
            LanguageMetadataSection $$2 = $$1.getMetadataSection(LanguageMetadataSection.SERIALIZER);
            if ($$2 != null) {
                for (LanguageInfo $$3 : $$2.getLanguages()) {
                    $$0.putIfAbsent((Object)$$3.getCode(), (Object)$$3);
                }
            }
        }
        catch (IOException | RuntimeException $$4) {
            LOGGER.warn("Unable to parse language metadata section of resourcepack: {}", (Object)$$1.packId(), (Object)$$4);
        }
    }
}