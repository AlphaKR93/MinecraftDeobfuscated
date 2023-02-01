/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Splitter
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.google.common.io.Files
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParser
 *  com.google.gson.reflect.TypeToken
 *  com.google.gson.stream.JsonReader
 *  com.mojang.datafixers.util.Either
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  java.io.BufferedReader
 *  java.io.File
 *  java.io.FileOutputStream
 *  java.io.OutputStream
 *  java.io.OutputStreamWriter
 *  java.io.PrintWriter
 *  java.io.Reader
 *  java.io.StringReader
 *  java.io.Writer
 *  java.lang.Boolean
 *  java.lang.CharSequence
 *  java.lang.Double
 *  java.lang.Enum
 *  java.lang.Exception
 *  java.lang.Float
 *  java.lang.IncompatibleClassChangeError
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.NumberFormatException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runtime
 *  java.lang.RuntimeException
 *  java.lang.String
 *  java.lang.System
 *  java.lang.Throwable
 *  java.nio.charset.Charset
 *  java.nio.charset.StandardCharsets
 *  java.util.ArrayList
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.EnumMap
 *  java.util.EnumSet
 *  java.util.Iterator
 *  java.util.LinkedHashSet
 *  java.util.List
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Objects
 *  java.util.Optional
 *  java.util.Set
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.ArrayUtils
 *  org.slf4j.Logger
 */
package net.minecraft.client;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.VideoMode;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.CameraType;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.PrioritizeChunkUpdates;
import net.minecraft.client.ToggleKeyMapping;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.GpuWarnlistManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ServerboundClientInformationPacket;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

public class Options {
    static final Logger LOGGER = LogUtils.getLogger();
    static final Gson GSON = new Gson();
    private static final TypeToken<List<String>> RESOURCE_PACK_TYPE = new TypeToken<List<String>>(){};
    public static final int RENDER_DISTANCE_TINY = 2;
    public static final int RENDER_DISTANCE_SHORT = 4;
    public static final int RENDER_DISTANCE_NORMAL = 8;
    public static final int RENDER_DISTANCE_FAR = 12;
    public static final int RENDER_DISTANCE_REALLY_FAR = 16;
    public static final int RENDER_DISTANCE_EXTREME = 32;
    private static final Splitter OPTION_SPLITTER = Splitter.on((char)':').limit(2);
    private static final float DEFAULT_VOLUME = 1.0f;
    public static final String DEFAULT_SOUND_DEVICE = "";
    private static final Component ACCESSIBILITY_TOOLTIP_DARK_MOJANG_BACKGROUND = Component.translatable("options.darkMojangStudiosBackgroundColor.tooltip");
    private final OptionInstance<Boolean> darkMojangStudiosBackground = OptionInstance.createBoolean("options.darkMojangStudiosBackgroundColor", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_DARK_MOJANG_BACKGROUND), false);
    private static final Component ACCESSIBILITY_TOOLTIP_HIDE_LIGHTNING_FLASHES = Component.translatable("options.hideLightningFlashes.tooltip");
    private final OptionInstance<Boolean> hideLightningFlash = OptionInstance.createBoolean("options.hideLightningFlashes", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_HIDE_LIGHTNING_FLASHES), false);
    private final OptionInstance<Double> sensitivity = new OptionInstance<Double>("options.sensitivity", OptionInstance.noTooltip(), ($$0, $$1) -> {
        if ($$1 == 0.0) {
            return Options.genericValueLabel($$0, Component.translatable("options.sensitivity.min"));
        }
        if ($$1 == 1.0) {
            return Options.genericValueLabel($$0, Component.translatable("options.sensitivity.max"));
        }
        return Options.percentValueLabel($$0, 2.0 * $$1);
    }, OptionInstance.UnitDouble.INSTANCE, 0.5, $$0 -> {});
    private final OptionInstance<Integer> renderDistance;
    private final OptionInstance<Integer> simulationDistance;
    private int serverRenderDistance = 0;
    private final OptionInstance<Double> entityDistanceScaling = new OptionInstance<Double>("options.entityDistanceScaling", OptionInstance.noTooltip(), Options::percentValueLabel, new OptionInstance.IntRange(2, 20).xmap($$0 -> (double)$$0 / 4.0, $$0 -> (int)($$0 * 4.0)), Codec.doubleRange((double)0.5, (double)5.0), 1.0, $$0 -> {});
    public static final int UNLIMITED_FRAMERATE_CUTOFF = 260;
    private final OptionInstance<Integer> framerateLimit = new OptionInstance<Integer>("options.framerateLimit", OptionInstance.noTooltip(), ($$0, $$1) -> {
        if ($$1 == 260) {
            return Options.genericValueLabel($$0, Component.translatable("options.framerateLimit.max"));
        }
        return Options.genericValueLabel($$0, Component.translatable("options.framerate", $$1));
    }, new OptionInstance.IntRange(1, 26).xmap($$0 -> $$0 * 10, $$0 -> $$0 / 10), Codec.intRange((int)10, (int)260), 120, $$0 -> Minecraft.getInstance().getWindow().setFramerateLimit((int)$$0));
    private final OptionInstance<CloudStatus> cloudStatus = new OptionInstance<CloudStatus>("options.renderClouds", OptionInstance.noTooltip(), OptionInstance.forOptionEnum(), new OptionInstance.Enum(Arrays.asList((Object[])CloudStatus.values()), Codec.either((Codec)Codec.BOOL, (Codec)Codec.STRING).xmap($$02 -> (CloudStatus)$$02.map($$0 -> $$0 != false ? CloudStatus.FANCY : CloudStatus.OFF, $$0 -> {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.SwitchStringRewriter$TooOptimisticMatchException
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.SwitchStringRewriter.getString(SwitchStringRewriter.java:404)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.SwitchStringRewriter.access$600(SwitchStringRewriter.java:53)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.SwitchStringRewriter$SwitchStringMatchResultCollector.collectMatches(SwitchStringRewriter.java:368)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.matchutil.ResetAfterTest.match(ResetAfterTest.java:24)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.matchutil.KleeneN.match(KleeneN.java:24)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.matchutil.MatchSequence.match(MatchSequence.java:26)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.matchutil.ResetAfterTest.match(ResetAfterTest.java:23)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.SwitchStringRewriter.rewriteComplex(SwitchStringRewriter.java:201)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.SwitchStringRewriter.rewrite(SwitchStringRewriter.java:73)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:881)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.getAnalysis(Method.java:520)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteDynamicExpression(LambdaRewriter.java:352)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteDynamicExpression(LambdaRewriter.java:168)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:106)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.rewriters.ExpressionRewriterHelper.applyForwards(ExpressionRewriterHelper.java:12)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriterToArgs(AbstractMemberFunctionInvokation.java:101)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractMemberFunctionInvokation.applyExpressionRewriter(AbstractMemberFunctionInvokation.java:88)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:104)
         *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.CastExpression.applyExpressionRewriter(CastExpression.java:128)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewriteExpression(LambdaRewriter.java:104)
         *     at org.benf.cfr.reader.bytecode.analysis.structured.statement.StructuredReturn.rewriteExpressions(StructuredReturn.java:99)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewrite(LambdaRewriter.java:89)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.rewriteLambdas(Op04StructuredStatement.java:1137)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:912)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1050)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at cuchaz.enigma.source.cfr.CfrSource.ensureDecompiled(CfrSource.java:81)
         *     at cuchaz.enigma.source.cfr.CfrSource.asString(CfrSource.java:50)
         *     at cuchaz.enigma.EnigmaProject$JarExport.decompileClass(EnigmaProject.java:298)
         *     at cuchaz.enigma.EnigmaProject$JarExport.lambda$decompileStream$1(EnigmaProject.java:274)
         *     at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:197)
         *     at java.base/java.util.AbstractList$RandomAccessSpliterator.forEachRemaining(AbstractList.java:722)
         *     at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:509)
         *     at java.base/java.util.stream.ForEachOps$ForEachTask.compute(ForEachOps.java:290)
         *     at java.base/java.util.concurrent.CountedCompleter.exec(CountedCompleter.java:754)
         *     at java.base/java.util.concurrent.ForkJoinTask.doExec(ForkJoinTask.java:387)
         *     at java.base/java.util.concurrent.ForkJoinPool$WorkQueue.topLevelExec(ForkJoinPool.java:1311)
         *     at java.base/java.util.concurrent.ForkJoinPool.scan(ForkJoinPool.java:1840)
         *     at java.base/java.util.concurrent.ForkJoinPool.runWorker(ForkJoinPool.java:1806)
         *     at java.base/java.util.concurrent.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:177)
         */
        throw new IllegalStateException("Decompilation failed");
    }), $$0 -> Either.right((Object)(switch ($$0) {
        default -> throw new IncompatibleClassChangeError();
        case CloudStatus.FANCY -> "true";
        case CloudStatus.FAST -> "fast";
        case CloudStatus.OFF -> "false";
    })))), CloudStatus.FANCY, $$0 -> {
        RenderTarget $$1;
        if (Minecraft.useShaderTransparency() && ($$1 = Minecraft.getInstance().levelRenderer.getCloudsTarget()) != null) {
            $$1.clear(Minecraft.ON_OSX);
        }
    });
    private static final Component GRAPHICS_TOOLTIP_FAST = Component.translatable("options.graphics.fast.tooltip");
    private static final Component GRAPHICS_TOOLTIP_FABULOUS = Component.translatable("options.graphics.fabulous.tooltip", Component.translatable("options.graphics.fabulous").withStyle(ChatFormatting.ITALIC));
    private static final Component GRAPHICS_TOOLTIP_FANCY = Component.translatable("options.graphics.fancy.tooltip");
    private final OptionInstance<GraphicsStatus> graphicsMode = new OptionInstance<GraphicsStatus>("options.graphics", $$0 -> switch ($$0) {
        default -> throw new IncompatibleClassChangeError();
        case GraphicsStatus.FANCY -> Tooltip.create(GRAPHICS_TOOLTIP_FANCY);
        case GraphicsStatus.FAST -> Tooltip.create(GRAPHICS_TOOLTIP_FAST);
        case GraphicsStatus.FABULOUS -> Tooltip.create(GRAPHICS_TOOLTIP_FABULOUS);
    }, ($$0, $$1) -> {
        MutableComponent $$2 = Component.translatable($$1.getKey());
        if ($$1 == GraphicsStatus.FABULOUS) {
            return $$2.withStyle(ChatFormatting.ITALIC);
        }
        return $$2;
    }, new OptionInstance.AltEnum<GraphicsStatus>(Arrays.asList((Object[])GraphicsStatus.values()), (List)Stream.of((Object[])GraphicsStatus.values()).filter($$0 -> $$0 != GraphicsStatus.FABULOUS).collect(Collectors.toList()), () -> Minecraft.getInstance().isRunning() && Minecraft.getInstance().getGpuWarnlistManager().isSkippingFabulous(), ($$0, $$1) -> {
        Minecraft $$2 = Minecraft.getInstance();
        GpuWarnlistManager $$3 = $$2.getGpuWarnlistManager();
        if ($$1 == GraphicsStatus.FABULOUS && $$3.willShowWarning()) {
            $$3.showWarning();
            return;
        }
        $$0.set($$1);
        $$2.levelRenderer.allChanged();
    }, Codec.INT.xmap(GraphicsStatus::byId, GraphicsStatus::getId)), GraphicsStatus.FANCY, $$0 -> {});
    private final OptionInstance<Boolean> ambientOcclusion = OptionInstance.createBoolean("options.ao", true, (Consumer<Boolean>)((Consumer)$$0 -> Minecraft.getInstance().levelRenderer.allChanged()));
    private static final Component PRIORITIZE_CHUNK_TOOLTIP_NONE = Component.translatable("options.prioritizeChunkUpdates.none.tooltip");
    private static final Component PRIORITIZE_CHUNK_TOOLTIP_PLAYER_AFFECTED = Component.translatable("options.prioritizeChunkUpdates.byPlayer.tooltip");
    private static final Component PRIORITIZE_CHUNK_TOOLTIP_NEARBY = Component.translatable("options.prioritizeChunkUpdates.nearby.tooltip");
    private final OptionInstance<PrioritizeChunkUpdates> prioritizeChunkUpdates = new OptionInstance<PrioritizeChunkUpdates>("options.prioritizeChunkUpdates", $$0 -> switch ($$0) {
        default -> throw new IncompatibleClassChangeError();
        case PrioritizeChunkUpdates.NONE -> Tooltip.create(PRIORITIZE_CHUNK_TOOLTIP_NONE);
        case PrioritizeChunkUpdates.PLAYER_AFFECTED -> Tooltip.create(PRIORITIZE_CHUNK_TOOLTIP_PLAYER_AFFECTED);
        case PrioritizeChunkUpdates.NEARBY -> Tooltip.create(PRIORITIZE_CHUNK_TOOLTIP_NEARBY);
    }, OptionInstance.forOptionEnum(), new OptionInstance.Enum(Arrays.asList((Object[])PrioritizeChunkUpdates.values()), Codec.INT.xmap(PrioritizeChunkUpdates::byId, PrioritizeChunkUpdates::getId)), PrioritizeChunkUpdates.NONE, $$0 -> {});
    public List<String> resourcePacks = Lists.newArrayList();
    public List<String> incompatibleResourcePacks = Lists.newArrayList();
    private final OptionInstance<ChatVisiblity> chatVisibility = new OptionInstance<ChatVisiblity>("options.chat.visibility", OptionInstance.noTooltip(), OptionInstance.forOptionEnum(), new OptionInstance.Enum(Arrays.asList((Object[])ChatVisiblity.values()), Codec.INT.xmap(ChatVisiblity::byId, ChatVisiblity::getId)), ChatVisiblity.FULL, $$0 -> {});
    private final OptionInstance<Double> chatOpacity = new OptionInstance<Double>("options.chat.opacity", OptionInstance.noTooltip(), ($$0, $$1) -> Options.percentValueLabel($$0, $$1 * 0.9 + 0.1), OptionInstance.UnitDouble.INSTANCE, 1.0, $$0 -> Minecraft.getInstance().gui.getChat().rescaleChat());
    private final OptionInstance<Double> chatLineSpacing = new OptionInstance<Double>("options.chat.line_spacing", OptionInstance.noTooltip(), Options::percentValueLabel, OptionInstance.UnitDouble.INSTANCE, 0.0, $$0 -> {});
    private final OptionInstance<Double> textBackgroundOpacity = new OptionInstance<Double>("options.accessibility.text_background_opacity", OptionInstance.noTooltip(), Options::percentValueLabel, OptionInstance.UnitDouble.INSTANCE, 0.5, $$0 -> Minecraft.getInstance().gui.getChat().rescaleChat());
    private final OptionInstance<Double> panoramaSpeed = new OptionInstance<Double>("options.accessibility.panorama_speed", OptionInstance.noTooltip(), Options::percentValueLabel, OptionInstance.UnitDouble.INSTANCE, 1.0, $$0 -> {});
    @Nullable
    public String fullscreenVideoModeString;
    public boolean hideServerAddress;
    public boolean advancedItemTooltips;
    public boolean pauseOnLostFocus = true;
    private final Set<PlayerModelPart> modelParts = EnumSet.allOf(PlayerModelPart.class);
    private final OptionInstance<HumanoidArm> mainHand = new OptionInstance<HumanoidArm>("options.mainHand", OptionInstance.noTooltip(), OptionInstance.forOptionEnum(), new OptionInstance.Enum(Arrays.asList((Object[])HumanoidArm.values()), Codec.STRING.xmap($$0 -> "left".equals($$0) ? HumanoidArm.LEFT : HumanoidArm.RIGHT, $$0 -> $$0 == HumanoidArm.LEFT ? "left" : "right")), HumanoidArm.RIGHT, $$0 -> this.broadcastOptions());
    public int overrideWidth;
    public int overrideHeight;
    public boolean heldItemTooltips = true;
    private final OptionInstance<Double> chatScale = new OptionInstance<Double>("options.chat.scale", OptionInstance.noTooltip(), ($$0, $$1) -> {
        if ($$1 == 0.0) {
            return CommonComponents.optionStatus($$0, false);
        }
        return Options.percentValueLabel($$0, $$1);
    }, OptionInstance.UnitDouble.INSTANCE, 1.0, $$0 -> Minecraft.getInstance().gui.getChat().rescaleChat());
    private final OptionInstance<Double> chatWidth = new OptionInstance<Double>("options.chat.width", OptionInstance.noTooltip(), ($$0, $$1) -> Options.pixelValueLabel($$0, ChatComponent.getWidth($$1)), OptionInstance.UnitDouble.INSTANCE, 1.0, $$0 -> Minecraft.getInstance().gui.getChat().rescaleChat());
    private final OptionInstance<Double> chatHeightUnfocused = new OptionInstance<Double>("options.chat.height.unfocused", OptionInstance.noTooltip(), ($$0, $$1) -> Options.pixelValueLabel($$0, ChatComponent.getHeight($$1)), OptionInstance.UnitDouble.INSTANCE, ChatComponent.defaultUnfocusedPct(), $$0 -> Minecraft.getInstance().gui.getChat().rescaleChat());
    private final OptionInstance<Double> chatHeightFocused = new OptionInstance<Double>("options.chat.height.focused", OptionInstance.noTooltip(), ($$0, $$1) -> Options.pixelValueLabel($$0, ChatComponent.getHeight($$1)), OptionInstance.UnitDouble.INSTANCE, 1.0, $$0 -> Minecraft.getInstance().gui.getChat().rescaleChat());
    private final OptionInstance<Double> chatDelay = new OptionInstance<Double>("options.chat.delay_instant", OptionInstance.noTooltip(), ($$0, $$1) -> {
        if ($$1 <= 0.0) {
            return Component.translatable("options.chat.delay_none");
        }
        return Component.translatable("options.chat.delay", String.format((Locale)Locale.ROOT, (String)"%.1f", (Object[])new Object[]{$$1}));
    }, new OptionInstance.IntRange(0, 60).xmap($$0 -> (double)$$0 / 10.0, $$0 -> (int)($$0 * 10.0)), Codec.doubleRange((double)0.0, (double)6.0), 0.0, $$0 -> Minecraft.getInstance().getChatListener().setMessageDelay((double)$$0));
    private static final Component ACCESSIBILITY_TOOLTIP_NOTIFICATION_DISPLAY_TIME = Component.translatable("options.notifications.display_time.tooltip");
    private final OptionInstance<Double> notificationDisplayTime = new OptionInstance<Double>("options.notifications.display_time", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_NOTIFICATION_DISPLAY_TIME), ($$0, $$1) -> Options.genericValueLabel($$0, Component.translatable("options.multiplier", $$1)), new OptionInstance.IntRange(5, 100).xmap($$0 -> (double)$$0 / 10.0, $$0 -> (int)($$0 * 10.0)), Codec.doubleRange((double)0.5, (double)10.0), 1.0, $$0 -> {});
    private final OptionInstance<Integer> mipmapLevels = new OptionInstance<Integer>("options.mipmapLevels", OptionInstance.noTooltip(), ($$0, $$1) -> {
        if ($$1 == 0) {
            return CommonComponents.optionStatus($$0, false);
        }
        return Options.genericValueLabel($$0, $$1);
    }, new OptionInstance.IntRange(0, 4), 4, $$0 -> {});
    public boolean useNativeTransport = true;
    private final OptionInstance<AttackIndicatorStatus> attackIndicator = new OptionInstance<AttackIndicatorStatus>("options.attackIndicator", OptionInstance.noTooltip(), OptionInstance.forOptionEnum(), new OptionInstance.Enum(Arrays.asList((Object[])AttackIndicatorStatus.values()), Codec.INT.xmap(AttackIndicatorStatus::byId, AttackIndicatorStatus::getId)), AttackIndicatorStatus.CROSSHAIR, $$0 -> {});
    public TutorialSteps tutorialStep = TutorialSteps.MOVEMENT;
    public boolean joinedFirstServer = false;
    public boolean hideBundleTutorial = false;
    private final OptionInstance<Integer> biomeBlendRadius = new OptionInstance<Integer>("options.biomeBlendRadius", OptionInstance.noTooltip(), ($$0, $$1) -> {
        int $$2 = $$1 * 2 + 1;
        return Options.genericValueLabel($$0, Component.translatable("options.biomeBlendRadius." + $$2));
    }, new OptionInstance.IntRange(0, 7), 2, $$0 -> Minecraft.getInstance().levelRenderer.allChanged());
    private final OptionInstance<Double> mouseWheelSensitivity = new OptionInstance<Double>("options.mouseWheelSensitivity", OptionInstance.noTooltip(), ($$0, $$1) -> Options.genericValueLabel($$0, Component.literal(String.format((Locale)Locale.ROOT, (String)"%.2f", (Object[])new Object[]{$$1}))), new OptionInstance.IntRange(-200, 100).xmap(Options::logMouse, Options::unlogMouse), Codec.doubleRange((double)Options.logMouse(-200), (double)Options.logMouse(100)), Options.logMouse(0), $$0 -> {});
    private final OptionInstance<Boolean> rawMouseInput = OptionInstance.createBoolean("options.rawMouseInput", true, (Consumer<Boolean>)((Consumer)$$0 -> {
        Window $$1 = Minecraft.getInstance().getWindow();
        if ($$1 != null) {
            $$1.updateRawMouseInput((boolean)$$0);
        }
    }));
    public int glDebugVerbosity = 1;
    private final OptionInstance<Boolean> autoJump = OptionInstance.createBoolean("options.autoJump", false);
    private final OptionInstance<Boolean> operatorItemsTab = OptionInstance.createBoolean("options.operatorItemsTab", false);
    private final OptionInstance<Boolean> autoSuggestions = OptionInstance.createBoolean("options.autoSuggestCommands", true);
    private final OptionInstance<Boolean> chatColors = OptionInstance.createBoolean("options.chat.color", true);
    private final OptionInstance<Boolean> chatLinks = OptionInstance.createBoolean("options.chat.links", true);
    private final OptionInstance<Boolean> chatLinksPrompt = OptionInstance.createBoolean("options.chat.links.prompt", true);
    private final OptionInstance<Boolean> enableVsync = OptionInstance.createBoolean("options.vsync", true, (Consumer<Boolean>)((Consumer)$$0 -> {
        if (Minecraft.getInstance().getWindow() != null) {
            Minecraft.getInstance().getWindow().updateVsync((boolean)$$0);
        }
    }));
    private final OptionInstance<Boolean> entityShadows = OptionInstance.createBoolean("options.entityShadows", true);
    private final OptionInstance<Boolean> forceUnicodeFont = OptionInstance.createBoolean("options.forceUnicodeFont", false, (Consumer<Boolean>)((Consumer)$$0 -> {
        Minecraft $$1 = Minecraft.getInstance();
        if ($$1.getWindow() != null) {
            $$1.selectMainFont((boolean)$$0);
            $$1.resizeDisplay();
        }
    }));
    private final OptionInstance<Boolean> invertYMouse = OptionInstance.createBoolean("options.invertMouse", false);
    private final OptionInstance<Boolean> discreteMouseScroll = OptionInstance.createBoolean("options.discrete_mouse_scroll", false);
    private final OptionInstance<Boolean> realmsNotifications = OptionInstance.createBoolean("options.realmsNotifications", true);
    private static final Component ALLOW_SERVER_LISTING_TOOLTIP = Component.translatable("options.allowServerListing.tooltip");
    private final OptionInstance<Boolean> allowServerListing = OptionInstance.createBoolean("options.allowServerListing", OptionInstance.cachedConstantTooltip(ALLOW_SERVER_LISTING_TOOLTIP), true, (Consumer<Boolean>)((Consumer)$$0 -> this.broadcastOptions()));
    private final OptionInstance<Boolean> reducedDebugInfo = OptionInstance.createBoolean("options.reducedDebugInfo", false);
    private final Map<SoundSource, OptionInstance<Double>> soundSourceVolumes = (Map)Util.make(new EnumMap(SoundSource.class), $$0 -> {
        for (SoundSource $$1 : SoundSource.values()) {
            $$0.put((Enum)$$1, this.createSoundSliderOptionInstance("soundCategory." + $$1.getName(), $$1));
        }
    });
    private final OptionInstance<Boolean> showSubtitles = OptionInstance.createBoolean("options.showSubtitles", false);
    private static final Component DIRECTIONAL_AUDIO_TOOLTIP_ON = Component.translatable("options.directionalAudio.on.tooltip");
    private static final Component DIRECTIONAL_AUDIO_TOOLTIP_OFF = Component.translatable("options.directionalAudio.off.tooltip");
    private final OptionInstance<Boolean> directionalAudio = OptionInstance.createBoolean("options.directionalAudio", $$0 -> $$0 != false ? Tooltip.create(DIRECTIONAL_AUDIO_TOOLTIP_ON) : Tooltip.create(DIRECTIONAL_AUDIO_TOOLTIP_OFF), false, (Consumer<Boolean>)((Consumer)$$0 -> {
        SoundManager $$1 = Minecraft.getInstance().getSoundManager();
        $$1.reload();
        $$1.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }));
    private final OptionInstance<Boolean> backgroundForChatOnly = new OptionInstance<Boolean>("options.accessibility.text_background", OptionInstance.noTooltip(), ($$0, $$1) -> $$1 != false ? Component.translatable("options.accessibility.text_background.chat") : Component.translatable("options.accessibility.text_background.everywhere"), OptionInstance.BOOLEAN_VALUES, true, $$0 -> {});
    private final OptionInstance<Boolean> touchscreen = OptionInstance.createBoolean("options.touchscreen", false);
    private final OptionInstance<Boolean> fullscreen = OptionInstance.createBoolean("options.fullscreen", false, (Consumer<Boolean>)((Consumer)$$0 -> {
        Minecraft $$1 = Minecraft.getInstance();
        if ($$1.getWindow() != null && $$1.getWindow().isFullscreen() != $$0.booleanValue()) {
            $$1.getWindow().toggleFullScreen();
            this.fullscreen().set($$1.getWindow().isFullscreen());
        }
    }));
    private final OptionInstance<Boolean> bobView = OptionInstance.createBoolean("options.viewBobbing", true);
    private static final Component MOVEMENT_TOGGLE = Component.translatable("options.key.toggle");
    private static final Component MOVEMENT_HOLD = Component.translatable("options.key.hold");
    private final OptionInstance<Boolean> toggleCrouch = new OptionInstance<Boolean>("key.sneak", OptionInstance.noTooltip(), ($$0, $$1) -> $$1 != false ? MOVEMENT_TOGGLE : MOVEMENT_HOLD, OptionInstance.BOOLEAN_VALUES, false, $$0 -> {});
    private final OptionInstance<Boolean> toggleSprint = new OptionInstance<Boolean>("key.sprint", OptionInstance.noTooltip(), ($$0, $$1) -> $$1 != false ? MOVEMENT_TOGGLE : MOVEMENT_HOLD, OptionInstance.BOOLEAN_VALUES, false, $$0 -> {});
    public boolean skipMultiplayerWarning;
    public boolean skipRealms32bitWarning;
    private static final Component CHAT_TOOLTIP_HIDE_MATCHED_NAMES = Component.translatable("options.hideMatchedNames.tooltip");
    private final OptionInstance<Boolean> hideMatchedNames = OptionInstance.createBoolean("options.hideMatchedNames", OptionInstance.cachedConstantTooltip(CHAT_TOOLTIP_HIDE_MATCHED_NAMES), true);
    private final OptionInstance<Boolean> showAutosaveIndicator = OptionInstance.createBoolean("options.autosaveIndicator", true);
    private static final Component CHAT_TOOLTIP_ONLY_SHOW_SECURE = Component.translatable("options.onlyShowSecureChat.tooltip");
    private final OptionInstance<Boolean> onlyShowSecureChat = OptionInstance.createBoolean("options.onlyShowSecureChat", OptionInstance.cachedConstantTooltip(CHAT_TOOLTIP_ONLY_SHOW_SECURE), false);
    public final KeyMapping keyUp = new KeyMapping("key.forward", 87, "key.categories.movement");
    public final KeyMapping keyLeft = new KeyMapping("key.left", 65, "key.categories.movement");
    public final KeyMapping keyDown = new KeyMapping("key.back", 83, "key.categories.movement");
    public final KeyMapping keyRight = new KeyMapping("key.right", 68, "key.categories.movement");
    public final KeyMapping keyJump = new KeyMapping("key.jump", 32, "key.categories.movement");
    public final KeyMapping keyShift = new ToggleKeyMapping("key.sneak", 340, "key.categories.movement", this.toggleCrouch::get);
    public final KeyMapping keySprint = new ToggleKeyMapping("key.sprint", 341, "key.categories.movement", this.toggleSprint::get);
    public final KeyMapping keyInventory = new KeyMapping("key.inventory", 69, "key.categories.inventory");
    public final KeyMapping keySwapOffhand = new KeyMapping("key.swapOffhand", 70, "key.categories.inventory");
    public final KeyMapping keyDrop = new KeyMapping("key.drop", 81, "key.categories.inventory");
    public final KeyMapping keyUse = new KeyMapping("key.use", InputConstants.Type.MOUSE, 1, "key.categories.gameplay");
    public final KeyMapping keyAttack = new KeyMapping("key.attack", InputConstants.Type.MOUSE, 0, "key.categories.gameplay");
    public final KeyMapping keyPickItem = new KeyMapping("key.pickItem", InputConstants.Type.MOUSE, 2, "key.categories.gameplay");
    public final KeyMapping keyChat = new KeyMapping("key.chat", 84, "key.categories.multiplayer");
    public final KeyMapping keyPlayerList = new KeyMapping("key.playerlist", 258, "key.categories.multiplayer");
    public final KeyMapping keyCommand = new KeyMapping("key.command", 47, "key.categories.multiplayer");
    public final KeyMapping keySocialInteractions = new KeyMapping("key.socialInteractions", 80, "key.categories.multiplayer");
    public final KeyMapping keyScreenshot = new KeyMapping("key.screenshot", 291, "key.categories.misc");
    public final KeyMapping keyTogglePerspective = new KeyMapping("key.togglePerspective", 294, "key.categories.misc");
    public final KeyMapping keySmoothCamera = new KeyMapping("key.smoothCamera", InputConstants.UNKNOWN.getValue(), "key.categories.misc");
    public final KeyMapping keyFullscreen = new KeyMapping("key.fullscreen", 300, "key.categories.misc");
    public final KeyMapping keySpectatorOutlines = new KeyMapping("key.spectatorOutlines", InputConstants.UNKNOWN.getValue(), "key.categories.misc");
    public final KeyMapping keyAdvancements = new KeyMapping("key.advancements", 76, "key.categories.misc");
    public final KeyMapping[] keyHotbarSlots = new KeyMapping[]{new KeyMapping("key.hotbar.1", 49, "key.categories.inventory"), new KeyMapping("key.hotbar.2", 50, "key.categories.inventory"), new KeyMapping("key.hotbar.3", 51, "key.categories.inventory"), new KeyMapping("key.hotbar.4", 52, "key.categories.inventory"), new KeyMapping("key.hotbar.5", 53, "key.categories.inventory"), new KeyMapping("key.hotbar.6", 54, "key.categories.inventory"), new KeyMapping("key.hotbar.7", 55, "key.categories.inventory"), new KeyMapping("key.hotbar.8", 56, "key.categories.inventory"), new KeyMapping("key.hotbar.9", 57, "key.categories.inventory")};
    public final KeyMapping keySaveHotbarActivator = new KeyMapping("key.saveToolbarActivator", 67, "key.categories.creative");
    public final KeyMapping keyLoadHotbarActivator = new KeyMapping("key.loadToolbarActivator", 88, "key.categories.creative");
    public final KeyMapping[] keyMappings = (KeyMapping[])ArrayUtils.addAll((Object[])new KeyMapping[]{this.keyAttack, this.keyUse, this.keyUp, this.keyLeft, this.keyDown, this.keyRight, this.keyJump, this.keyShift, this.keySprint, this.keyDrop, this.keyInventory, this.keyChat, this.keyPlayerList, this.keyPickItem, this.keyCommand, this.keySocialInteractions, this.keyScreenshot, this.keyTogglePerspective, this.keySmoothCamera, this.keyFullscreen, this.keySpectatorOutlines, this.keySwapOffhand, this.keySaveHotbarActivator, this.keyLoadHotbarActivator, this.keyAdvancements}, (Object[])this.keyHotbarSlots);
    protected Minecraft minecraft;
    private final File optionsFile;
    public boolean hideGui;
    private CameraType cameraType = CameraType.FIRST_PERSON;
    public boolean renderDebug;
    public boolean renderDebugCharts;
    public boolean renderFpsChart;
    public String lastMpIp = "";
    public boolean smoothCamera;
    private final OptionInstance<Integer> fov = new OptionInstance<Integer>("options.fov", OptionInstance.noTooltip(), ($$0, $$1) -> switch ($$1) {
        case 70 -> Options.genericValueLabel($$0, Component.translatable("options.fov.min"));
        case 110 -> Options.genericValueLabel($$0, Component.translatable("options.fov.max"));
        default -> Options.genericValueLabel($$0, $$1);
    }, new OptionInstance.IntRange(30, 110), Codec.DOUBLE.xmap($$0 -> (int)($$0 * 40.0 + 70.0), $$0 -> ((double)$$0.intValue() - 70.0) / 40.0), 70, $$0 -> Minecraft.getInstance().levelRenderer.needsUpdate());
    private static final MutableComponent TELEMETRY_TOOLTIP = Component.translatable("options.telemetry.button.tooltip", Component.translatable("options.telemetry.state.minimal"), Component.translatable("options.telemetry.state.all"));
    private final OptionInstance<Boolean> telemetryOptInExtra = OptionInstance.createBoolean("options.telemetry.button", OptionInstance.cachedConstantTooltip(TELEMETRY_TOOLTIP), ($$0, $$1) -> {
        Minecraft $$2 = Minecraft.getInstance();
        if (!$$2.allowsTelemetry()) {
            return Component.translatable("options.telemetry.state.none");
        }
        if ($$1.booleanValue() && $$2.extraTelemetryAvailable()) {
            return Component.translatable("options.telemetry.state.all");
        }
        return Component.translatable("options.telemetry.state.minimal");
    }, false, (Consumer<Boolean>)((Consumer)$$0 -> {}));
    private static final Component ACCESSIBILITY_TOOLTIP_SCREEN_EFFECT = Component.translatable("options.screenEffectScale.tooltip");
    private final OptionInstance<Double> screenEffectScale = new OptionInstance<Double>("options.screenEffectScale", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_SCREEN_EFFECT), ($$0, $$1) -> {
        if ($$1 == 0.0) {
            return Options.genericValueLabel($$0, CommonComponents.OPTION_OFF);
        }
        return Options.percentValueLabel($$0, $$1);
    }, OptionInstance.UnitDouble.INSTANCE, 1.0, $$0 -> {});
    private static final Component ACCESSIBILITY_TOOLTIP_FOV_EFFECT = Component.translatable("options.fovEffectScale.tooltip");
    private final OptionInstance<Double> fovEffectScale = new OptionInstance<Double>("options.fovEffectScale", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_FOV_EFFECT), ($$0, $$1) -> {
        if ($$1 == 0.0) {
            return Options.genericValueLabel($$0, CommonComponents.OPTION_OFF);
        }
        return Options.percentValueLabel($$0, $$1);
    }, OptionInstance.UnitDouble.INSTANCE.xmap(Mth::square, Math::sqrt), Codec.doubleRange((double)0.0, (double)1.0), 1.0, $$0 -> {});
    private static final Component ACCESSIBILITY_TOOLTIP_DARKNESS_EFFECT = Component.translatable("options.darknessEffectScale.tooltip");
    private final OptionInstance<Double> darknessEffectScale = new OptionInstance<Double>("options.darknessEffectScale", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_DARKNESS_EFFECT), ($$0, $$1) -> {
        if ($$1 == 0.0) {
            return Options.genericValueLabel($$0, CommonComponents.OPTION_OFF);
        }
        return Options.percentValueLabel($$0, $$1);
    }, OptionInstance.UnitDouble.INSTANCE.xmap(Mth::square, Math::sqrt), 1.0, $$0 -> {});
    private static final Component ACCESSIBILITY_TOOLTIP_GLINT_SPEED = Component.translatable("options.glintSpeed.tooltip");
    private final OptionInstance<Double> glintSpeed = new OptionInstance<Double>("options.glintSpeed", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_GLINT_SPEED), ($$0, $$1) -> {
        if ($$1 == 0.0) {
            return Options.genericValueLabel($$0, CommonComponents.OPTION_OFF);
        }
        return Options.percentValueLabel($$0, $$1);
    }, OptionInstance.UnitDouble.INSTANCE, 0.5, $$0 -> {});
    private static final Component ACCESSIBILITY_TOOLTIP_GLINT_STRENGTH = Component.translatable("options.glintStrength.tooltip");
    private final OptionInstance<Double> glintStrength = new OptionInstance<Double>("options.glintStrength", OptionInstance.cachedConstantTooltip(ACCESSIBILITY_TOOLTIP_GLINT_STRENGTH), ($$0, $$1) -> {
        if ($$1 == 0.0) {
            return Options.genericValueLabel($$0, CommonComponents.OPTION_OFF);
        }
        return Options.percentValueLabel($$0, $$1);
    }, OptionInstance.UnitDouble.INSTANCE, 1.0, RenderSystem::setShaderGlintAlpha);
    private final OptionInstance<Double> gamma = new OptionInstance<Double>("options.gamma", OptionInstance.noTooltip(), ($$0, $$1) -> {
        int $$2 = (int)($$1 * 100.0);
        if ($$2 == 0) {
            return Options.genericValueLabel($$0, Component.translatable("options.gamma.min"));
        }
        if ($$2 == 50) {
            return Options.genericValueLabel($$0, Component.translatable("options.gamma.default"));
        }
        if ($$2 == 100) {
            return Options.genericValueLabel($$0, Component.translatable("options.gamma.max"));
        }
        return Options.genericValueLabel($$0, $$2);
    }, OptionInstance.UnitDouble.INSTANCE, 0.5, $$0 -> {});
    private final OptionInstance<Integer> guiScale = new OptionInstance<Integer>("options.guiScale", OptionInstance.noTooltip(), ($$0, $$1) -> $$1 == 0 ? Component.translatable("options.guiScale.auto") : Component.literal(Integer.toString((int)$$1)), new OptionInstance.ClampingLazyMaxIntRange(0, () -> {
        Minecraft $$0 = Minecraft.getInstance();
        if (!$$0.isRunning()) {
            return 0x7FFFFFFE;
        }
        return $$0.getWindow().calculateScale(0, $$0.isEnforceUnicode());
    }), 0, $$0 -> {});
    private final OptionInstance<ParticleStatus> particles = new OptionInstance<ParticleStatus>("options.particles", OptionInstance.noTooltip(), OptionInstance.forOptionEnum(), new OptionInstance.Enum(Arrays.asList((Object[])ParticleStatus.values()), Codec.INT.xmap(ParticleStatus::byId, ParticleStatus::getId)), ParticleStatus.ALL, $$0 -> {});
    private final OptionInstance<NarratorStatus> narrator = new OptionInstance<NarratorStatus>("options.narrator", OptionInstance.noTooltip(), ($$0, $$1) -> {
        if (this.minecraft.getNarrator().isActive()) {
            return $$1.getName();
        }
        return Component.translatable("options.narrator.notavailable");
    }, new OptionInstance.Enum(Arrays.asList((Object[])NarratorStatus.values()), Codec.INT.xmap(NarratorStatus::byId, NarratorStatus::getId)), NarratorStatus.OFF, $$0 -> this.minecraft.getNarrator().updateNarratorStatus((NarratorStatus)((Object)$$0)));
    public String languageCode = "en_us";
    private final OptionInstance<String> soundDevice = new OptionInstance<String>("options.audioDevice", OptionInstance.noTooltip(), ($$0, $$1) -> {
        if (DEFAULT_SOUND_DEVICE.equals($$1)) {
            return Component.translatable("options.audioDevice.default");
        }
        if ($$1.startsWith("OpenAL Soft on ")) {
            return Component.literal($$1.substring(SoundEngine.OPEN_AL_SOFT_PREFIX_LENGTH));
        }
        return Component.literal($$1);
    }, new OptionInstance.LazyEnum(() -> Stream.concat((Stream)Stream.of((Object)DEFAULT_SOUND_DEVICE), (Stream)Minecraft.getInstance().getSoundManager().getAvailableSoundDevices().stream()).toList(), $$0 -> {
        if (!Minecraft.getInstance().isRunning() || $$0 == DEFAULT_SOUND_DEVICE || Minecraft.getInstance().getSoundManager().getAvailableSoundDevices().contains($$0)) {
            return Optional.of((Object)$$0);
        }
        return Optional.empty();
    }, Codec.STRING), "", $$0 -> {
        SoundManager $$1 = Minecraft.getInstance().getSoundManager();
        $$1.reload();
        $$1.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    });
    public boolean onboardAccessibility = true;
    public boolean syncWrites;

    public OptionInstance<Boolean> darkMojangStudiosBackground() {
        return this.darkMojangStudiosBackground;
    }

    public OptionInstance<Boolean> hideLightningFlash() {
        return this.hideLightningFlash;
    }

    public OptionInstance<Double> sensitivity() {
        return this.sensitivity;
    }

    public OptionInstance<Integer> renderDistance() {
        return this.renderDistance;
    }

    public OptionInstance<Integer> simulationDistance() {
        return this.simulationDistance;
    }

    public OptionInstance<Double> entityDistanceScaling() {
        return this.entityDistanceScaling;
    }

    public OptionInstance<Integer> framerateLimit() {
        return this.framerateLimit;
    }

    public OptionInstance<CloudStatus> cloudStatus() {
        return this.cloudStatus;
    }

    public OptionInstance<GraphicsStatus> graphicsMode() {
        return this.graphicsMode;
    }

    public OptionInstance<Boolean> ambientOcclusion() {
        return this.ambientOcclusion;
    }

    public OptionInstance<PrioritizeChunkUpdates> prioritizeChunkUpdates() {
        return this.prioritizeChunkUpdates;
    }

    public OptionInstance<ChatVisiblity> chatVisibility() {
        return this.chatVisibility;
    }

    public OptionInstance<Double> chatOpacity() {
        return this.chatOpacity;
    }

    public OptionInstance<Double> chatLineSpacing() {
        return this.chatLineSpacing;
    }

    public OptionInstance<Double> textBackgroundOpacity() {
        return this.textBackgroundOpacity;
    }

    public OptionInstance<Double> panoramaSpeed() {
        return this.panoramaSpeed;
    }

    public OptionInstance<HumanoidArm> mainHand() {
        return this.mainHand;
    }

    public OptionInstance<Double> chatScale() {
        return this.chatScale;
    }

    public OptionInstance<Double> chatWidth() {
        return this.chatWidth;
    }

    public OptionInstance<Double> chatHeightUnfocused() {
        return this.chatHeightUnfocused;
    }

    public OptionInstance<Double> chatHeightFocused() {
        return this.chatHeightFocused;
    }

    public OptionInstance<Double> chatDelay() {
        return this.chatDelay;
    }

    public OptionInstance<Double> notificationDisplayTime() {
        return this.notificationDisplayTime;
    }

    public OptionInstance<Integer> mipmapLevels() {
        return this.mipmapLevels;
    }

    public OptionInstance<AttackIndicatorStatus> attackIndicator() {
        return this.attackIndicator;
    }

    public OptionInstance<Integer> biomeBlendRadius() {
        return this.biomeBlendRadius;
    }

    private static double logMouse(int $$0) {
        return Math.pow((double)10.0, (double)((double)$$0 / 100.0));
    }

    private static int unlogMouse(double $$0) {
        return Mth.floor(Math.log10((double)$$0) * 100.0);
    }

    public OptionInstance<Double> mouseWheelSensitivity() {
        return this.mouseWheelSensitivity;
    }

    public OptionInstance<Boolean> rawMouseInput() {
        return this.rawMouseInput;
    }

    public OptionInstance<Boolean> autoJump() {
        return this.autoJump;
    }

    public OptionInstance<Boolean> operatorItemsTab() {
        return this.operatorItemsTab;
    }

    public OptionInstance<Boolean> autoSuggestions() {
        return this.autoSuggestions;
    }

    public OptionInstance<Boolean> chatColors() {
        return this.chatColors;
    }

    public OptionInstance<Boolean> chatLinks() {
        return this.chatLinks;
    }

    public OptionInstance<Boolean> chatLinksPrompt() {
        return this.chatLinksPrompt;
    }

    public OptionInstance<Boolean> enableVsync() {
        return this.enableVsync;
    }

    public OptionInstance<Boolean> entityShadows() {
        return this.entityShadows;
    }

    public OptionInstance<Boolean> forceUnicodeFont() {
        return this.forceUnicodeFont;
    }

    public OptionInstance<Boolean> invertYMouse() {
        return this.invertYMouse;
    }

    public OptionInstance<Boolean> discreteMouseScroll() {
        return this.discreteMouseScroll;
    }

    public OptionInstance<Boolean> realmsNotifications() {
        return this.realmsNotifications;
    }

    public OptionInstance<Boolean> allowServerListing() {
        return this.allowServerListing;
    }

    public OptionInstance<Boolean> reducedDebugInfo() {
        return this.reducedDebugInfo;
    }

    public final float getSoundSourceVolume(SoundSource $$0) {
        return this.getSoundSourceOptionInstance($$0).get().floatValue();
    }

    public final OptionInstance<Double> getSoundSourceOptionInstance(SoundSource $$0) {
        return (OptionInstance)Objects.requireNonNull((Object)((OptionInstance)this.soundSourceVolumes.get((Object)$$0)));
    }

    private OptionInstance<Double> createSoundSliderOptionInstance(String $$02, SoundSource $$12) {
        return new OptionInstance<Double>($$02, OptionInstance.noTooltip(), ($$0, $$1) -> {
            if ($$1 == 0.0) {
                return Options.genericValueLabel($$0, CommonComponents.OPTION_OFF);
            }
            return Options.percentValueLabel($$0, $$1);
        }, OptionInstance.UnitDouble.INSTANCE, 1.0, $$1 -> Minecraft.getInstance().getSoundManager().updateSourceVolume($$12, $$1.floatValue()));
    }

    public OptionInstance<Boolean> showSubtitles() {
        return this.showSubtitles;
    }

    public OptionInstance<Boolean> directionalAudio() {
        return this.directionalAudio;
    }

    public OptionInstance<Boolean> backgroundForChatOnly() {
        return this.backgroundForChatOnly;
    }

    public OptionInstance<Boolean> touchscreen() {
        return this.touchscreen;
    }

    public OptionInstance<Boolean> fullscreen() {
        return this.fullscreen;
    }

    public OptionInstance<Boolean> bobView() {
        return this.bobView;
    }

    public OptionInstance<Boolean> toggleCrouch() {
        return this.toggleCrouch;
    }

    public OptionInstance<Boolean> toggleSprint() {
        return this.toggleSprint;
    }

    public OptionInstance<Boolean> hideMatchedNames() {
        return this.hideMatchedNames;
    }

    public OptionInstance<Boolean> showAutosaveIndicator() {
        return this.showAutosaveIndicator;
    }

    public OptionInstance<Boolean> onlyShowSecureChat() {
        return this.onlyShowSecureChat;
    }

    public OptionInstance<Integer> fov() {
        return this.fov;
    }

    public OptionInstance<Boolean> telemetryOptInExtra() {
        return this.telemetryOptInExtra;
    }

    public OptionInstance<Double> screenEffectScale() {
        return this.screenEffectScale;
    }

    public OptionInstance<Double> fovEffectScale() {
        return this.fovEffectScale;
    }

    public OptionInstance<Double> darknessEffectScale() {
        return this.darknessEffectScale;
    }

    public OptionInstance<Double> glintSpeed() {
        return this.glintSpeed;
    }

    public OptionInstance<Double> glintStrength() {
        return this.glintStrength;
    }

    public OptionInstance<Double> gamma() {
        return this.gamma;
    }

    public OptionInstance<Integer> guiScale() {
        return this.guiScale;
    }

    public OptionInstance<ParticleStatus> particles() {
        return this.particles;
    }

    public OptionInstance<NarratorStatus> narrator() {
        return this.narrator;
    }

    public OptionInstance<String> soundDevice() {
        return this.soundDevice;
    }

    public Options(Minecraft $$03, File $$12) {
        this.minecraft = $$03;
        this.optionsFile = new File($$12, "options.txt");
        boolean $$2 = $$03.is64Bit();
        boolean $$3 = $$2 && Runtime.getRuntime().maxMemory() >= 1000000000L;
        this.renderDistance = new OptionInstance<Integer>("options.renderDistance", OptionInstance.noTooltip(), ($$0, $$1) -> Options.genericValueLabel($$0, Component.translatable("options.chunks", $$1)), new OptionInstance.IntRange(2, $$3 ? 32 : 16), $$2 ? 12 : 8, $$0 -> Minecraft.getInstance().levelRenderer.needsUpdate());
        this.simulationDistance = new OptionInstance<Integer>("options.simulationDistance", OptionInstance.noTooltip(), ($$0, $$1) -> Options.genericValueLabel($$0, Component.translatable("options.chunks", $$1)), new OptionInstance.IntRange(5, $$3 ? 32 : 16), $$2 ? 12 : 8, $$0 -> {});
        this.syncWrites = Util.getPlatform() == Util.OS.WINDOWS;
        this.load();
    }

    public float getBackgroundOpacity(float $$0) {
        return this.backgroundForChatOnly.get() != false ? $$0 : this.textBackgroundOpacity().get().floatValue();
    }

    public int getBackgroundColor(float $$0) {
        return (int)(this.getBackgroundOpacity($$0) * 255.0f) << 24 & 0xFF000000;
    }

    public int getBackgroundColor(int $$0) {
        return this.backgroundForChatOnly.get() != false ? $$0 : (int)(this.textBackgroundOpacity.get() * 255.0) << 24 & 0xFF000000;
    }

    public void setKey(KeyMapping $$0, InputConstants.Key $$1) {
        $$0.setKey($$1);
        this.save();
    }

    private void processOptions(FieldAccess $$0) {
        $$0.process("autoJump", this.autoJump);
        $$0.process("operatorItemsTab", this.operatorItemsTab);
        $$0.process("autoSuggestions", this.autoSuggestions);
        $$0.process("chatColors", this.chatColors);
        $$0.process("chatLinks", this.chatLinks);
        $$0.process("chatLinksPrompt", this.chatLinksPrompt);
        $$0.process("enableVsync", this.enableVsync);
        $$0.process("entityShadows", this.entityShadows);
        $$0.process("forceUnicodeFont", this.forceUnicodeFont);
        $$0.process("discrete_mouse_scroll", this.discreteMouseScroll);
        $$0.process("invertYMouse", this.invertYMouse);
        $$0.process("realmsNotifications", this.realmsNotifications);
        $$0.process("reducedDebugInfo", this.reducedDebugInfo);
        $$0.process("showSubtitles", this.showSubtitles);
        $$0.process("directionalAudio", this.directionalAudio);
        $$0.process("touchscreen", this.touchscreen);
        $$0.process("fullscreen", this.fullscreen);
        $$0.process("bobView", this.bobView);
        $$0.process("toggleCrouch", this.toggleCrouch);
        $$0.process("toggleSprint", this.toggleSprint);
        $$0.process("darkMojangStudiosBackground", this.darkMojangStudiosBackground);
        $$0.process("hideLightningFlashes", this.hideLightningFlash);
        $$0.process("mouseSensitivity", this.sensitivity);
        $$0.process("fov", this.fov);
        $$0.process("screenEffectScale", this.screenEffectScale);
        $$0.process("fovEffectScale", this.fovEffectScale);
        $$0.process("darknessEffectScale", this.darknessEffectScale);
        $$0.process("glintSpeed", this.glintSpeed);
        $$0.process("glintStrength", this.glintStrength);
        $$0.process("gamma", this.gamma);
        $$0.process("renderDistance", this.renderDistance);
        $$0.process("simulationDistance", this.simulationDistance);
        $$0.process("entityDistanceScaling", this.entityDistanceScaling);
        $$0.process("guiScale", this.guiScale);
        $$0.process("particles", this.particles);
        $$0.process("maxFps", this.framerateLimit);
        $$0.process("graphicsMode", this.graphicsMode);
        $$0.process("ao", this.ambientOcclusion);
        $$0.process("prioritizeChunkUpdates", this.prioritizeChunkUpdates);
        $$0.process("biomeBlendRadius", this.biomeBlendRadius);
        $$0.process("renderClouds", this.cloudStatus);
        this.resourcePacks = $$0.process("resourcePacks", this.resourcePacks, Options::readPackList, arg_0 -> ((Gson)GSON).toJson(arg_0));
        this.incompatibleResourcePacks = $$0.process("incompatibleResourcePacks", this.incompatibleResourcePacks, Options::readPackList, arg_0 -> ((Gson)GSON).toJson(arg_0));
        this.lastMpIp = $$0.process("lastServer", this.lastMpIp);
        this.languageCode = $$0.process("lang", this.languageCode);
        $$0.process("soundDevice", this.soundDevice);
        $$0.process("chatVisibility", this.chatVisibility);
        $$0.process("chatOpacity", this.chatOpacity);
        $$0.process("chatLineSpacing", this.chatLineSpacing);
        $$0.process("textBackgroundOpacity", this.textBackgroundOpacity);
        $$0.process("backgroundForChatOnly", this.backgroundForChatOnly);
        this.hideServerAddress = $$0.process("hideServerAddress", this.hideServerAddress);
        this.advancedItemTooltips = $$0.process("advancedItemTooltips", this.advancedItemTooltips);
        this.pauseOnLostFocus = $$0.process("pauseOnLostFocus", this.pauseOnLostFocus);
        this.overrideWidth = $$0.process("overrideWidth", this.overrideWidth);
        this.overrideHeight = $$0.process("overrideHeight", this.overrideHeight);
        this.heldItemTooltips = $$0.process("heldItemTooltips", this.heldItemTooltips);
        $$0.process("chatHeightFocused", this.chatHeightFocused);
        $$0.process("chatDelay", this.chatDelay);
        $$0.process("chatHeightUnfocused", this.chatHeightUnfocused);
        $$0.process("chatScale", this.chatScale);
        $$0.process("chatWidth", this.chatWidth);
        $$0.process("notificationDisplayTime", this.notificationDisplayTime);
        $$0.process("mipmapLevels", this.mipmapLevels);
        this.useNativeTransport = $$0.process("useNativeTransport", this.useNativeTransport);
        $$0.process("mainHand", this.mainHand);
        $$0.process("attackIndicator", this.attackIndicator);
        $$0.process("narrator", this.narrator);
        this.tutorialStep = $$0.process("tutorialStep", this.tutorialStep, TutorialSteps::getByName, TutorialSteps::getName);
        $$0.process("mouseWheelSensitivity", this.mouseWheelSensitivity);
        $$0.process("rawMouseInput", this.rawMouseInput);
        this.glDebugVerbosity = $$0.process("glDebugVerbosity", this.glDebugVerbosity);
        this.skipMultiplayerWarning = $$0.process("skipMultiplayerWarning", this.skipMultiplayerWarning);
        this.skipRealms32bitWarning = $$0.process("skipRealms32bitWarning", this.skipRealms32bitWarning);
        $$0.process("hideMatchedNames", this.hideMatchedNames);
        this.joinedFirstServer = $$0.process("joinedFirstServer", this.joinedFirstServer);
        this.hideBundleTutorial = $$0.process("hideBundleTutorial", this.hideBundleTutorial);
        this.syncWrites = $$0.process("syncChunkWrites", this.syncWrites);
        $$0.process("showAutosaveIndicator", this.showAutosaveIndicator);
        $$0.process("allowServerListing", this.allowServerListing);
        $$0.process("onlyShowSecureChat", this.onlyShowSecureChat);
        $$0.process("panoramaScrollSpeed", this.panoramaSpeed);
        $$0.process("telemetryOptInExtra", this.telemetryOptInExtra);
        this.onboardAccessibility = $$0.process("onboardAccessibility", this.onboardAccessibility);
        for (KeyMapping keyMapping : this.keyMappings) {
            String $$3;
            String $$2 = keyMapping.saveString();
            if ($$2.equals((Object)($$3 = $$0.process("key_" + keyMapping.getName(), $$2)))) continue;
            keyMapping.setKey(InputConstants.getKey($$3));
        }
        for (SoundSource soundSource : SoundSource.values()) {
            $$0.process("soundCategory_" + soundSource.getName(), (OptionInstance)this.soundSourceVolumes.get((Object)soundSource));
        }
        for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
            boolean $$6 = this.modelParts.contains((Object)playerModelPart);
            boolean $$7 = $$0.process("modelPart_" + playerModelPart.getId(), $$6);
            if ($$7 == $$6) continue;
            this.setModelPart(playerModelPart, $$7);
        }
    }

    public void load() {
        try {
            if (!this.optionsFile.exists()) {
                return;
            }
            CompoundTag $$0 = new CompoundTag();
            try (BufferedReader $$12 = Files.newReader((File)this.optionsFile, (Charset)Charsets.UTF_8);){
                $$12.lines().forEach($$1 -> {
                    try {
                        Iterator $$2 = OPTION_SPLITTER.split((CharSequence)$$1).iterator();
                        $$0.putString((String)$$2.next(), (String)$$2.next());
                    }
                    catch (Exception $$3) {
                        LOGGER.warn("Skipping bad option: {}", $$1);
                    }
                });
            }
            final CompoundTag $$2 = this.dataFix($$0);
            if (!$$2.contains("graphicsMode") && $$2.contains("fancyGraphics")) {
                if (Options.isTrue($$2.getString("fancyGraphics"))) {
                    this.graphicsMode.set(GraphicsStatus.FANCY);
                } else {
                    this.graphicsMode.set(GraphicsStatus.FAST);
                }
            }
            this.processOptions(new FieldAccess(){

                @Nullable
                private String getValueOrNull(String $$0) {
                    return $$2.contains($$0) ? $$2.getString($$0) : null;
                }

                @Override
                public <T> void process(String $$0, OptionInstance<T> $$1) {
                    String $$22 = this.getValueOrNull($$0);
                    if ($$22 != null) {
                        JsonReader $$3 = new JsonReader((Reader)new StringReader($$22.isEmpty() ? "\"\"" : $$22));
                        JsonElement $$4 = JsonParser.parseReader((JsonReader)$$3);
                        DataResult $$5 = $$1.codec().parse((DynamicOps)JsonOps.INSTANCE, (Object)$$4);
                        $$5.error().ifPresent($$2 -> LOGGER.error("Error parsing option value " + $$22 + " for option " + $$1 + ": " + $$2.message()));
                        $$5.result().ifPresent($$1::set);
                    }
                }

                @Override
                public int process(String $$0, int $$1) {
                    String $$22 = this.getValueOrNull($$0);
                    if ($$22 != null) {
                        try {
                            return Integer.parseInt((String)$$22);
                        }
                        catch (NumberFormatException $$3) {
                            LOGGER.warn("Invalid integer value for option {} = {}", new Object[]{$$0, $$22, $$3});
                        }
                    }
                    return $$1;
                }

                @Override
                public boolean process(String $$0, boolean $$1) {
                    String $$22 = this.getValueOrNull($$0);
                    return $$22 != null ? Options.isTrue($$22) : $$1;
                }

                @Override
                public String process(String $$0, String $$1) {
                    return (String)MoreObjects.firstNonNull((Object)this.getValueOrNull($$0), (Object)$$1);
                }

                @Override
                public float process(String $$0, float $$1) {
                    String $$22 = this.getValueOrNull($$0);
                    if ($$22 != null) {
                        if (Options.isTrue($$22)) {
                            return 1.0f;
                        }
                        if (Options.isFalse($$22)) {
                            return 0.0f;
                        }
                        try {
                            return Float.parseFloat((String)$$22);
                        }
                        catch (NumberFormatException $$3) {
                            LOGGER.warn("Invalid floating point value for option {} = {}", new Object[]{$$0, $$22, $$3});
                        }
                    }
                    return $$1;
                }

                @Override
                public <T> T process(String $$0, T $$1, Function<String, T> $$22, Function<T, String> $$3) {
                    String $$4 = this.getValueOrNull($$0);
                    return (T)($$4 == null ? $$1 : $$22.apply((Object)$$4));
                }
            });
            if ($$2.contains("fullscreenResolution")) {
                this.fullscreenVideoModeString = $$2.getString("fullscreenResolution");
            }
            if (this.minecraft.getWindow() != null) {
                this.minecraft.getWindow().setFramerateLimit(this.framerateLimit.get());
            }
            KeyMapping.resetMapping();
        }
        catch (Exception $$3) {
            LOGGER.error("Failed to load options", (Throwable)$$3);
        }
    }

    static boolean isTrue(String $$0) {
        return "true".equals((Object)$$0);
    }

    static boolean isFalse(String $$0) {
        return "false".equals((Object)$$0);
    }

    private CompoundTag dataFix(CompoundTag $$0) {
        int $$1 = 0;
        try {
            $$1 = Integer.parseInt((String)$$0.getString("version"));
        }
        catch (RuntimeException runtimeException) {
            // empty catch block
        }
        return DataFixTypes.OPTIONS.updateToCurrentVersion(this.minecraft.getFixerUpper(), $$0, $$1);
    }

    public void save() {
        try (final PrintWriter $$0 = new PrintWriter((Writer)new OutputStreamWriter((OutputStream)new FileOutputStream(this.optionsFile), StandardCharsets.UTF_8));){
            $$0.println("version:" + SharedConstants.getCurrentVersion().getDataVersion().getVersion());
            this.processOptions(new FieldAccess(){

                public void writePrefix(String $$02) {
                    $$0.print($$02);
                    $$0.print(':');
                }

                @Override
                public <T> void process(String $$02, OptionInstance<T> $$12) {
                    DataResult $$22 = $$12.codec().encodeStart((DynamicOps)JsonOps.INSTANCE, $$12.get());
                    $$22.error().ifPresent($$1 -> LOGGER.error("Error saving option " + $$12 + ": " + $$1));
                    $$22.result().ifPresent($$2 -> {
                        this.writePrefix($$02);
                        $$0.println(GSON.toJson($$2));
                    });
                }

                @Override
                public int process(String $$02, int $$1) {
                    this.writePrefix($$02);
                    $$0.println($$1);
                    return $$1;
                }

                @Override
                public boolean process(String $$02, boolean $$1) {
                    this.writePrefix($$02);
                    $$0.println($$1);
                    return $$1;
                }

                @Override
                public String process(String $$02, String $$1) {
                    this.writePrefix($$02);
                    $$0.println($$1);
                    return $$1;
                }

                @Override
                public float process(String $$02, float $$1) {
                    this.writePrefix($$02);
                    $$0.println($$1);
                    return $$1;
                }

                @Override
                public <T> T process(String $$02, T $$1, Function<String, T> $$2, Function<T, String> $$3) {
                    this.writePrefix($$02);
                    $$0.println((String)$$3.apply($$1));
                    return $$1;
                }
            });
            if (this.minecraft.getWindow().getPreferredFullscreenVideoMode().isPresent()) {
                $$0.println("fullscreenResolution:" + ((VideoMode)this.minecraft.getWindow().getPreferredFullscreenVideoMode().get()).write());
            }
        }
        catch (Exception $$1) {
            LOGGER.error("Failed to save options", (Throwable)$$1);
        }
        this.broadcastOptions();
    }

    public void broadcastOptions() {
        if (this.minecraft.player != null) {
            int $$0 = 0;
            for (PlayerModelPart $$1 : this.modelParts) {
                $$0 |= $$1.getMask();
            }
            this.minecraft.player.connection.send(new ServerboundClientInformationPacket(this.languageCode, this.renderDistance.get(), this.chatVisibility.get(), this.chatColors.get(), $$0, this.mainHand.get(), this.minecraft.isTextFilteringEnabled(), this.allowServerListing.get()));
        }
    }

    private void setModelPart(PlayerModelPart $$0, boolean $$1) {
        if ($$1) {
            this.modelParts.add((Object)$$0);
        } else {
            this.modelParts.remove((Object)$$0);
        }
    }

    public boolean isModelPartEnabled(PlayerModelPart $$0) {
        return this.modelParts.contains((Object)$$0);
    }

    public void toggleModelPart(PlayerModelPart $$0, boolean $$1) {
        this.setModelPart($$0, $$1);
        this.broadcastOptions();
    }

    public CloudStatus getCloudsType() {
        if (this.getEffectiveRenderDistance() >= 4) {
            return this.cloudStatus.get();
        }
        return CloudStatus.OFF;
    }

    public boolean useNativeTransport() {
        return this.useNativeTransport;
    }

    public void loadSelectedResourcePacks(PackRepository $$0) {
        LinkedHashSet $$1 = Sets.newLinkedHashSet();
        Iterator $$2 = this.resourcePacks.iterator();
        while ($$2.hasNext()) {
            String $$3 = (String)$$2.next();
            Pack $$4 = $$0.getPack($$3);
            if ($$4 == null && !$$3.startsWith("file/")) {
                $$4 = $$0.getPack("file/" + $$3);
            }
            if ($$4 == null) {
                LOGGER.warn("Removed resource pack {} from options because it doesn't seem to exist anymore", (Object)$$3);
                $$2.remove();
                continue;
            }
            if (!$$4.getCompatibility().isCompatible() && !this.incompatibleResourcePacks.contains((Object)$$3)) {
                LOGGER.warn("Removed resource pack {} from options because it is no longer compatible", (Object)$$3);
                $$2.remove();
                continue;
            }
            if ($$4.getCompatibility().isCompatible() && this.incompatibleResourcePacks.contains((Object)$$3)) {
                LOGGER.info("Removed resource pack {} from incompatibility list because it's now compatible", (Object)$$3);
                this.incompatibleResourcePacks.remove((Object)$$3);
                continue;
            }
            $$1.add((Object)$$4.getId());
        }
        $$0.setSelected((Collection<String>)$$1);
    }

    public CameraType getCameraType() {
        return this.cameraType;
    }

    public void setCameraType(CameraType $$0) {
        this.cameraType = $$0;
    }

    private static List<String> readPackList(String $$0) {
        ArrayList $$1 = GsonHelper.fromNullableJson(GSON, $$0, RESOURCE_PACK_TYPE);
        return $$1 != null ? $$1 : Lists.newArrayList();
    }

    public File getFile() {
        return this.optionsFile;
    }

    public String dumpOptionsForReport() {
        Stream $$02 = Stream.builder().add((Object)Pair.of((Object)"ao", (Object)this.ambientOcclusion.get())).add((Object)Pair.of((Object)"biomeBlendRadius", (Object)this.biomeBlendRadius.get())).add((Object)Pair.of((Object)"enableVsync", (Object)this.enableVsync.get())).add((Object)Pair.of((Object)"entityDistanceScaling", (Object)this.entityDistanceScaling.get())).add((Object)Pair.of((Object)"entityShadows", (Object)this.entityShadows.get())).add((Object)Pair.of((Object)"forceUnicodeFont", (Object)this.forceUnicodeFont.get())).add((Object)Pair.of((Object)"fov", (Object)this.fov.get())).add((Object)Pair.of((Object)"fovEffectScale", (Object)this.fovEffectScale.get())).add((Object)Pair.of((Object)"darknessEffectScale", (Object)this.darknessEffectScale.get())).add((Object)Pair.of((Object)"glintSpeed", (Object)this.glintSpeed.get())).add((Object)Pair.of((Object)"glintStrength", (Object)this.glintStrength.get())).add((Object)Pair.of((Object)"prioritizeChunkUpdates", (Object)this.prioritizeChunkUpdates.get())).add((Object)Pair.of((Object)"fullscreen", (Object)this.fullscreen.get())).add((Object)Pair.of((Object)"fullscreenResolution", (Object)String.valueOf((Object)this.fullscreenVideoModeString))).add((Object)Pair.of((Object)"gamma", (Object)this.gamma.get())).add((Object)Pair.of((Object)"glDebugVerbosity", (Object)this.glDebugVerbosity)).add((Object)Pair.of((Object)"graphicsMode", (Object)this.graphicsMode.get())).add((Object)Pair.of((Object)"guiScale", (Object)this.guiScale.get())).add((Object)Pair.of((Object)"maxFps", (Object)this.framerateLimit.get())).add((Object)Pair.of((Object)"mipmapLevels", (Object)this.mipmapLevels.get())).add((Object)Pair.of((Object)"narrator", (Object)((Object)this.narrator.get()))).add((Object)Pair.of((Object)"overrideHeight", (Object)this.overrideHeight)).add((Object)Pair.of((Object)"overrideWidth", (Object)this.overrideWidth)).add((Object)Pair.of((Object)"particles", (Object)this.particles.get())).add((Object)Pair.of((Object)"reducedDebugInfo", (Object)this.reducedDebugInfo.get())).add((Object)Pair.of((Object)"renderClouds", (Object)this.cloudStatus.get())).add((Object)Pair.of((Object)"renderDistance", (Object)this.renderDistance.get())).add((Object)Pair.of((Object)"simulationDistance", (Object)this.simulationDistance.get())).add((Object)Pair.of((Object)"resourcePacks", this.resourcePacks)).add((Object)Pair.of((Object)"screenEffectScale", (Object)this.screenEffectScale.get())).add((Object)Pair.of((Object)"syncChunkWrites", (Object)this.syncWrites)).add((Object)Pair.of((Object)"useNativeTransport", (Object)this.useNativeTransport)).add((Object)Pair.of((Object)"soundDevice", (Object)this.soundDevice.get())).build();
        return (String)$$02.map($$0 -> (String)$$0.getFirst() + ": " + $$0.getSecond()).collect(Collectors.joining((CharSequence)System.lineSeparator()));
    }

    public void setServerRenderDistance(int $$0) {
        this.serverRenderDistance = $$0;
    }

    public int getEffectiveRenderDistance() {
        return this.serverRenderDistance > 0 ? Math.min((int)this.renderDistance.get(), (int)this.serverRenderDistance) : this.renderDistance.get();
    }

    private static Component pixelValueLabel(Component $$0, int $$1) {
        return Component.translatable("options.pixel_value", $$0, $$1);
    }

    private static Component percentValueLabel(Component $$0, double $$1) {
        return Component.translatable("options.percent_value", $$0, (int)($$1 * 100.0));
    }

    public static Component genericValueLabel(Component $$0, Component $$1) {
        return Component.translatable("options.generic_value", $$0, $$1);
    }

    public static Component genericValueLabel(Component $$0, int $$1) {
        return Options.genericValueLabel($$0, Component.literal(Integer.toString((int)$$1)));
    }

    static interface FieldAccess {
        public <T> void process(String var1, OptionInstance<T> var2);

        public int process(String var1, int var2);

        public boolean process(String var1, boolean var2);

        public String process(String var1, String var2);

        public float process(String var1, float var2);

        public <T> T process(String var1, T var2, Function<String, T> var3, Function<T, String> var4);
    }
}