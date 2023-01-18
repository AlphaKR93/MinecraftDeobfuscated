/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Maps
 *  com.mojang.datafixers.DSL
 *  com.mojang.datafixers.DataFix
 *  com.mojang.datafixers.TypeRewriteRule
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.schemas.Schema
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicLike
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.OptionalDynamic
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.HashMap
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Optional
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 *  org.apache.commons.lang3.math.NumberUtils
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicLike;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.datafix.fixes.References;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;

public class WorldGenSettingsFix
extends DataFix {
    private static final String VILLAGE = "minecraft:village";
    private static final String DESERT_PYRAMID = "minecraft:desert_pyramid";
    private static final String IGLOO = "minecraft:igloo";
    private static final String JUNGLE_TEMPLE = "minecraft:jungle_pyramid";
    private static final String SWAMP_HUT = "minecraft:swamp_hut";
    private static final String PILLAGER_OUTPOST = "minecraft:pillager_outpost";
    private static final String END_CITY = "minecraft:endcity";
    private static final String WOODLAND_MANSION = "minecraft:mansion";
    private static final String OCEAN_MONUMENT = "minecraft:monument";
    private static final ImmutableMap<String, StructureFeatureConfiguration> DEFAULTS = ImmutableMap.builder().put((Object)"minecraft:village", (Object)new StructureFeatureConfiguration(32, 8, 10387312)).put((Object)"minecraft:desert_pyramid", (Object)new StructureFeatureConfiguration(32, 8, 14357617)).put((Object)"minecraft:igloo", (Object)new StructureFeatureConfiguration(32, 8, 14357618)).put((Object)"minecraft:jungle_pyramid", (Object)new StructureFeatureConfiguration(32, 8, 14357619)).put((Object)"minecraft:swamp_hut", (Object)new StructureFeatureConfiguration(32, 8, 14357620)).put((Object)"minecraft:pillager_outpost", (Object)new StructureFeatureConfiguration(32, 8, 165745296)).put((Object)"minecraft:monument", (Object)new StructureFeatureConfiguration(32, 5, 10387313)).put((Object)"minecraft:endcity", (Object)new StructureFeatureConfiguration(20, 11, 10387313)).put((Object)"minecraft:mansion", (Object)new StructureFeatureConfiguration(80, 20, 10387319)).build();

    public WorldGenSettingsFix(Schema $$0) {
        super($$0, true);
    }

    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("WorldGenSettings building", this.getInputSchema().getType(References.WORLD_GEN_SETTINGS), $$0 -> $$0.update(DSL.remainderFinder(), WorldGenSettingsFix::fix));
    }

    private static <T> Dynamic<T> noise(long $$0, DynamicLike<T> $$1, Dynamic<T> $$2, Dynamic<T> $$3) {
        return $$1.createMap((Map)ImmutableMap.of((Object)$$1.createString("type"), (Object)$$1.createString("minecraft:noise"), (Object)$$1.createString("biome_source"), $$3, (Object)$$1.createString("seed"), (Object)$$1.createLong($$0), (Object)$$1.createString("settings"), $$2));
    }

    private static <T> Dynamic<T> vanillaBiomeSource(Dynamic<T> $$0, long $$1, boolean $$2, boolean $$3) {
        ImmutableMap.Builder $$4 = ImmutableMap.builder().put((Object)$$0.createString("type"), (Object)$$0.createString("minecraft:vanilla_layered")).put((Object)$$0.createString("seed"), (Object)$$0.createLong($$1)).put((Object)$$0.createString("large_biomes"), (Object)$$0.createBoolean($$3));
        if ($$2) {
            $$4.put((Object)$$0.createString("legacy_biome_init_layer"), (Object)$$0.createBoolean($$2));
        }
        return $$0.createMap((Map)$$4.build());
    }

    /*
     * Exception decompiling
     */
    private static <T> Dynamic<T> fix(Dynamic<T> $$0) {
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
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
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
    }

    protected static <T> Dynamic<T> defaultOverworld(Dynamic<T> $$0, long $$1) {
        return WorldGenSettingsFix.noise($$1, $$0, $$0.createString("minecraft:overworld"), WorldGenSettingsFix.vanillaBiomeSource($$0, $$1, false, false));
    }

    protected static <T> T vanillaLevels(Dynamic<T> $$0, long $$1, Dynamic<T> $$2, boolean $$3) {
        DynamicOps $$4 = $$0.getOps();
        return (T)$$4.createMap((Map)ImmutableMap.of((Object)$$4.createString("minecraft:overworld"), (Object)$$4.createMap((Map)ImmutableMap.of((Object)$$4.createString("type"), (Object)$$4.createString("minecraft:overworld" + ($$3 ? "_caves" : "")), (Object)$$4.createString("generator"), (Object)$$2.getValue())), (Object)$$4.createString("minecraft:the_nether"), (Object)$$4.createMap((Map)ImmutableMap.of((Object)$$4.createString("type"), (Object)$$4.createString("minecraft:the_nether"), (Object)$$4.createString("generator"), (Object)WorldGenSettingsFix.noise($$1, $$0, $$0.createString("minecraft:nether"), $$0.createMap((Map)ImmutableMap.of((Object)$$0.createString("type"), (Object)$$0.createString("minecraft:multi_noise"), (Object)$$0.createString("seed"), (Object)$$0.createLong($$1), (Object)$$0.createString("preset"), (Object)$$0.createString("minecraft:nether")))).getValue())), (Object)$$4.createString("minecraft:the_end"), (Object)$$4.createMap((Map)ImmutableMap.of((Object)$$4.createString("type"), (Object)$$4.createString("minecraft:the_end"), (Object)$$4.createString("generator"), (Object)WorldGenSettingsFix.noise($$1, $$0, $$0.createString("minecraft:end"), $$0.createMap((Map)ImmutableMap.of((Object)$$0.createString("type"), (Object)$$0.createString("minecraft:the_end"), (Object)$$0.createString("seed"), (Object)$$0.createLong($$1)))).getValue()))));
    }

    private static <T> Map<Dynamic<T>, Dynamic<T>> fixFlatStructures(DynamicOps<T> $$0, OptionalDynamic<T> $$12) {
        MutableInt $$2 = new MutableInt(32);
        MutableInt $$3 = new MutableInt(3);
        MutableInt $$4 = new MutableInt(128);
        MutableBoolean $$5 = new MutableBoolean(false);
        HashMap $$6 = Maps.newHashMap();
        if (!$$12.result().isPresent()) {
            $$5.setTrue();
            $$6.put((Object)VILLAGE, (Object)((StructureFeatureConfiguration)DEFAULTS.get((Object)VILLAGE)));
        }
        $$12.get("structures").flatMap(Dynamic::getMapValues).result().ifPresent(arg_0 -> WorldGenSettingsFix.lambda$fixFlatStructures$10($$5, $$2, $$3, $$4, (Map)$$6, arg_0));
        ImmutableMap.Builder $$7 = ImmutableMap.builder();
        $$7.put((Object)$$12.createString("structures"), (Object)$$12.createMap((Map)$$6.entrySet().stream().collect(Collectors.toMap($$1 -> $$12.createString((String)$$1.getKey()), $$1 -> ((StructureFeatureConfiguration)$$1.getValue()).serialize($$0)))));
        if ($$5.isTrue()) {
            $$7.put((Object)$$12.createString("stronghold"), (Object)$$12.createMap((Map)ImmutableMap.of((Object)$$12.createString("distance"), (Object)$$12.createInt($$2.getValue().intValue()), (Object)$$12.createString("spread"), (Object)$$12.createInt($$3.getValue().intValue()), (Object)$$12.createString("count"), (Object)$$12.createInt($$4.getValue().intValue()))));
        }
        return $$7.build();
    }

    private static int getInt(String $$0, int $$1) {
        return NumberUtils.toInt((String)$$0, (int)$$1);
    }

    private static int getInt(String $$0, int $$1, int $$2) {
        return Math.max((int)$$2, (int)WorldGenSettingsFix.getInt($$0, $$1));
    }

    private static void setSpacing(Map<String, StructureFeatureConfiguration> $$0, String $$1, String $$2, int $$3) {
        StructureFeatureConfiguration $$4 = (StructureFeatureConfiguration)$$0.getOrDefault((Object)$$1, (Object)((StructureFeatureConfiguration)DEFAULTS.get((Object)$$1)));
        int $$5 = WorldGenSettingsFix.getInt($$2, $$4.spacing, $$3);
        $$0.put((Object)$$1, (Object)new StructureFeatureConfiguration($$5, $$4.separation, $$4.salt));
    }

    private static /* synthetic */ void lambda$fixFlatStructures$10(MutableBoolean $$0, MutableInt $$1, MutableInt $$2, MutableInt $$3, Map $$4, Map $$52) {
        $$52.forEach(($$5, $$6) -> $$6.getMapValues().result().ifPresent($$62 -> $$62.forEach(($$6, $$7) -> {
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
             *     at org.benf.cfr.reader.bytecode.analysis.structured.statement.StructuredExpressionStatement.rewriteExpressions(StructuredExpressionStatement.java:70)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewrite(LambdaRewriter.java:89)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.rewriteLambdas(Op04StructuredStatement.java:1137)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:912)
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
             *     at org.benf.cfr.reader.bytecode.analysis.structured.statement.StructuredExpressionStatement.rewriteExpressions(StructuredExpressionStatement.java:70)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.op4rewriters.LambdaRewriter.rewrite(LambdaRewriter.java:89)
             *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.rewriteLambdas(Op04StructuredStatement.java:1137)
             *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:912)
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
             *     at org.benf.cfr.reader.bytecode.analysis.structured.statement.StructuredExpressionStatement.rewriteExpressions(StructuredExpressionStatement.java:70)
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
        })));
    }

    private static /* synthetic */ void lambda$fix$6(ImmutableMap.Builder $$0, DynamicOps $$1, String $$2) {
        $$0.put($$1.createString("legacy_custom_options"), $$1.createString($$2));
    }

    private static /* synthetic */ Optional lambda$fix$5(Dynamic $$0) {
        return $$0.asString().result();
    }

    private static /* synthetic */ Dynamic lambda$fix$4(Dynamic $$0) {
        return $$0.createMap((Map)ImmutableMap.of((Object)$$0.createString("type"), (Object)$$0.createString("minecraft:fixed")));
    }

    private static /* synthetic */ Dynamic lambda$fix$3(Dynamic $$0) {
        return $$0.createList(Stream.of((Object[])new Dynamic[]{$$0.createMap((Map)ImmutableMap.of((Object)$$0.createString("height"), (Object)$$0.createInt(1), (Object)$$0.createString("block"), (Object)$$0.createString("minecraft:bedrock"))), $$0.createMap((Map)ImmutableMap.of((Object)$$0.createString("height"), (Object)$$0.createInt(2), (Object)$$0.createString("block"), (Object)$$0.createString("minecraft:dirt"))), $$0.createMap((Map)ImmutableMap.of((Object)$$0.createString("height"), (Object)$$0.createInt(1), (Object)$$0.createString("block"), (Object)$$0.createString("minecraft:grass_block")))}));
    }

    private static /* synthetic */ Optional lambda$fix$2(Optional $$0, Dynamic $$1) {
        if ($$0.equals((Object)Optional.of((Object)"customized"))) {
            return $$1.get("generatorOptions").asString().result();
        }
        return Optional.empty();
    }

    private static /* synthetic */ String lambda$fix$1(String $$0) {
        return $$0.toLowerCase(Locale.ROOT);
    }

    static final class StructureFeatureConfiguration {
        public static final Codec<StructureFeatureConfiguration> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.INT.fieldOf("spacing").forGetter($$0 -> $$0.spacing), (App)Codec.INT.fieldOf("separation").forGetter($$0 -> $$0.separation), (App)Codec.INT.fieldOf("salt").forGetter($$0 -> $$0.salt)).apply((Applicative)$$02, StructureFeatureConfiguration::new));
        final int spacing;
        final int separation;
        final int salt;

        public StructureFeatureConfiguration(int $$0, int $$1, int $$2) {
            this.spacing = $$0;
            this.separation = $$1;
            this.salt = $$2;
        }

        public <T> Dynamic<T> serialize(DynamicOps<T> $$0) {
            return new Dynamic($$0, CODEC.encodeStart($$0, (Object)this).result().orElse($$0.emptyMap()));
        }
    }
}