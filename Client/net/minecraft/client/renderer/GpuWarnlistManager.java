/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Lists
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.google.gson.JsonSyntaxException
 *  com.mojang.logging.LogUtils
 *  java.io.BufferedReader
 *  java.io.IOException
 *  java.io.Reader
 *  java.lang.CharSequence
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.regex.Matcher
 *  java.util.regex.Pattern
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlUtil;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public class GpuWarnlistManager
extends SimplePreparableReloadListener<Preparations> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation GPU_WARNLIST_LOCATION = new ResourceLocation("gpu_warnlist.json");
    private ImmutableMap<String, String> warnings = ImmutableMap.of();
    private boolean showWarning;
    private boolean warningDismissed;
    private boolean skipFabulous;

    public boolean hasWarnings() {
        return !this.warnings.isEmpty();
    }

    public boolean willShowWarning() {
        return this.hasWarnings() && !this.warningDismissed;
    }

    public void showWarning() {
        this.showWarning = true;
    }

    public void dismissWarning() {
        this.warningDismissed = true;
    }

    public void dismissWarningAndSkipFabulous() {
        this.warningDismissed = true;
        this.skipFabulous = true;
    }

    public boolean isShowingWarning() {
        return this.showWarning && !this.warningDismissed;
    }

    public boolean isSkippingFabulous() {
        return this.skipFabulous;
    }

    public void resetWarnings() {
        this.showWarning = false;
        this.warningDismissed = false;
        this.skipFabulous = false;
    }

    @Nullable
    public String getRendererWarnings() {
        return (String)this.warnings.get((Object)"renderer");
    }

    @Nullable
    public String getVersionWarnings() {
        return (String)this.warnings.get((Object)"version");
    }

    @Nullable
    public String getVendorWarnings() {
        return (String)this.warnings.get((Object)"vendor");
    }

    @Nullable
    public String getAllWarnings() {
        StringBuilder $$0 = new StringBuilder();
        this.warnings.forEach(($$1, $$2) -> $$0.append($$1).append(": ").append($$2));
        return $$0.length() == 0 ? null : $$0.toString();
    }

    @Override
    protected Preparations prepare(ResourceManager $$0, ProfilerFiller $$1) {
        ArrayList $$2 = Lists.newArrayList();
        ArrayList $$3 = Lists.newArrayList();
        ArrayList $$4 = Lists.newArrayList();
        $$1.startTick();
        JsonObject $$5 = GpuWarnlistManager.parseJson($$0, $$1);
        if ($$5 != null) {
            $$1.push("compile_regex");
            GpuWarnlistManager.compilePatterns($$5.getAsJsonArray("renderer"), (List<Pattern>)$$2);
            GpuWarnlistManager.compilePatterns($$5.getAsJsonArray("version"), (List<Pattern>)$$3);
            GpuWarnlistManager.compilePatterns($$5.getAsJsonArray("vendor"), (List<Pattern>)$$4);
            $$1.pop();
        }
        $$1.endTick();
        return new Preparations((List<Pattern>)$$2, (List<Pattern>)$$3, (List<Pattern>)$$4);
    }

    @Override
    protected void apply(Preparations $$0, ResourceManager $$1, ProfilerFiller $$2) {
        this.warnings = $$0.apply();
    }

    private static void compilePatterns(JsonArray $$0, List<Pattern> $$12) {
        $$0.forEach($$1 -> $$12.add((Object)Pattern.compile((String)$$1.getAsString(), (int)2)));
    }

    @Nullable
    private static JsonObject parseJson(ResourceManager $$0, ProfilerFiller $$1) {
        $$1.push("parse_json");
        JsonObject $$2 = null;
        try (BufferedReader $$3 = $$0.openAsReader(GPU_WARNLIST_LOCATION);){
            $$2 = JsonParser.parseReader((Reader)$$3).getAsJsonObject();
        }
        catch (JsonSyntaxException | IOException $$4) {
            LOGGER.warn("Failed to load GPU warnlist");
        }
        $$1.pop();
        return $$2;
    }

    protected static final class Preparations {
        private final List<Pattern> rendererPatterns;
        private final List<Pattern> versionPatterns;
        private final List<Pattern> vendorPatterns;

        Preparations(List<Pattern> $$0, List<Pattern> $$1, List<Pattern> $$2) {
            this.rendererPatterns = $$0;
            this.versionPatterns = $$1;
            this.vendorPatterns = $$2;
        }

        private static String matchAny(List<Pattern> $$0, String $$1) {
            ArrayList $$2 = Lists.newArrayList();
            for (Pattern $$3 : $$0) {
                Matcher $$4 = $$3.matcher((CharSequence)$$1);
                while ($$4.find()) {
                    $$2.add((Object)$$4.group());
                }
            }
            return String.join((CharSequence)", ", (Iterable)$$2);
        }

        ImmutableMap<String, String> apply() {
            String $$3;
            String $$2;
            ImmutableMap.Builder $$0 = new ImmutableMap.Builder();
            String $$1 = Preparations.matchAny(this.rendererPatterns, GlUtil.getRenderer());
            if (!$$1.isEmpty()) {
                $$0.put((Object)"renderer", (Object)$$1);
            }
            if (!($$2 = Preparations.matchAny(this.versionPatterns, GlUtil.getOpenGLVersion())).isEmpty()) {
                $$0.put((Object)"version", (Object)$$2);
            }
            if (!($$3 = Preparations.matchAny(this.vendorPatterns, GlUtil.getVendor())).isEmpty()) {
                $$0.put((Object)"vendor", (Object)$$3);
            }
            return $$0.build();
        }
    }
}