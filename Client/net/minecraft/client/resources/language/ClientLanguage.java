/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.mojang.logging.LogUtils
 *  java.io.IOException
 *  java.io.InputStream
 *  java.lang.Exception
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.function.BiConsumer
 *  org.slf4j.Logger
 */
package net.minecraft.client.resources.language;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.client.resources.language.FormattedBidiReorder;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FormattedCharSequence;
import org.slf4j.Logger;

public class ClientLanguage
extends Language {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final Map<String, String> storage;
    private final boolean defaultRightToLeft;

    private ClientLanguage(Map<String, String> $$0, boolean $$1) {
        this.storage = $$0;
        this.defaultRightToLeft = $$1;
    }

    public static ClientLanguage loadFrom(ResourceManager $$0, List<String> $$1, boolean $$2) {
        HashMap $$3 = Maps.newHashMap();
        for (String $$4 : $$1) {
            String $$5 = String.format((Locale)Locale.ROOT, (String)"lang/%s.json", (Object[])new Object[]{$$4});
            for (String $$6 : $$0.getNamespaces()) {
                try {
                    ResourceLocation $$7 = new ResourceLocation($$6, $$5);
                    ClientLanguage.appendFrom($$4, $$0.getResourceStack($$7), (Map<String, String>)$$3);
                }
                catch (Exception $$8) {
                    LOGGER.warn("Skipped language file: {}:{} ({})", new Object[]{$$6, $$5, $$8.toString()});
                }
            }
        }
        return new ClientLanguage((Map<String, String>)ImmutableMap.copyOf((Map)$$3), $$2);
    }

    private static void appendFrom(String $$0, List<Resource> $$1, Map<String, String> $$2) {
        for (Resource $$3 : $$1) {
            try {
                InputStream $$4 = $$3.open();
                try {
                    Language.loadFromJson($$4, (BiConsumer<String, String>)((BiConsumer)(arg_0, arg_1) -> $$2.put(arg_0, arg_1)));
                }
                finally {
                    if ($$4 == null) continue;
                    $$4.close();
                }
            }
            catch (IOException $$5) {
                LOGGER.warn("Failed to load translations for {} from pack {}", new Object[]{$$0, $$3.sourcePackId(), $$5});
            }
        }
    }

    @Override
    public String getOrDefault(String $$0, String $$1) {
        return (String)this.storage.getOrDefault((Object)$$0, (Object)$$1);
    }

    @Override
    public boolean has(String $$0) {
        return this.storage.containsKey((Object)$$0);
    }

    @Override
    public boolean isDefaultRightToLeft() {
        return this.defaultRightToLeft;
    }

    @Override
    public FormattedCharSequence getVisualOrder(FormattedText $$0) {
        return FormattedBidiReorder.reorder($$0, this.defaultRightToLeft);
    }
}