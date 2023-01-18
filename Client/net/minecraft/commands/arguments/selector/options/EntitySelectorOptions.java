/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.mojang.brigadier.ImmutableStringReader
 *  com.mojang.brigadier.Message
 *  com.mojang.brigadier.StringReader
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.DynamicCommandExceptionType
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  com.mojang.brigadier.suggestion.Suggestions
 *  com.mojang.brigadier.suggestion.SuggestionsBuilder
 *  java.lang.Double
 *  java.lang.Float
 *  java.lang.Integer
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Arrays
 *  java.util.HashMap
 *  java.util.Locale
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Objects
 *  java.util.concurrent.CompletableFuture
 *  java.util.function.BiFunction
 *  java.util.function.Consumer
 *  java.util.function.Function
 *  java.util.function.Predicate
 *  java.util.stream.Stream
 */
package net.minecraft.commands.arguments.selector.options;

import com.google.common.collect.Maps;
import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.WrappedMinMaxBounds;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Team;

public class EntitySelectorOptions {
    private static final Map<String, Option> OPTIONS = Maps.newHashMap();
    public static final DynamicCommandExceptionType ERROR_UNKNOWN_OPTION = new DynamicCommandExceptionType($$0 -> Component.translatable("argument.entity.options.unknown", $$0));
    public static final DynamicCommandExceptionType ERROR_INAPPLICABLE_OPTION = new DynamicCommandExceptionType($$0 -> Component.translatable("argument.entity.options.inapplicable", $$0));
    public static final SimpleCommandExceptionType ERROR_RANGE_NEGATIVE = new SimpleCommandExceptionType((Message)Component.translatable("argument.entity.options.distance.negative"));
    public static final SimpleCommandExceptionType ERROR_LEVEL_NEGATIVE = new SimpleCommandExceptionType((Message)Component.translatable("argument.entity.options.level.negative"));
    public static final SimpleCommandExceptionType ERROR_LIMIT_TOO_SMALL = new SimpleCommandExceptionType((Message)Component.translatable("argument.entity.options.limit.toosmall"));
    public static final DynamicCommandExceptionType ERROR_SORT_UNKNOWN = new DynamicCommandExceptionType($$0 -> Component.translatable("argument.entity.options.sort.irreversible", $$0));
    public static final DynamicCommandExceptionType ERROR_GAME_MODE_INVALID = new DynamicCommandExceptionType($$0 -> Component.translatable("argument.entity.options.mode.invalid", $$0));
    public static final DynamicCommandExceptionType ERROR_ENTITY_TYPE_INVALID = new DynamicCommandExceptionType($$0 -> Component.translatable("argument.entity.options.type.invalid", $$0));

    private static void register(String $$0, Modifier $$1, Predicate<EntitySelectorParser> $$2, Component $$3) {
        OPTIONS.put((Object)$$0, (Object)new Option($$1, $$2, $$3));
    }

    public static void bootStrap() {
        if (!OPTIONS.isEmpty()) {
            return;
        }
        EntitySelectorOptions.register("name", $$0 -> {
            int $$1 = $$0.getReader().getCursor();
            boolean $$22 = $$0.shouldInvertValue();
            String $$3 = $$0.getReader().readString();
            if ($$0.hasNameNotEquals() && !$$22) {
                $$0.getReader().setCursor($$1);
                throw ERROR_INAPPLICABLE_OPTION.createWithContext((ImmutableStringReader)$$0.getReader(), (Object)"name");
            }
            if ($$22) {
                $$0.setHasNameNotEquals(true);
            } else {
                $$0.setHasNameEquals(true);
            }
            $$0.addPredicate((Predicate<Entity>)((Predicate)$$2 -> $$2.getName().getString().equals((Object)$$3) != $$22));
        }, (Predicate<EntitySelectorParser>)((Predicate)$$0 -> !$$0.hasNameEquals()), Component.translatable("argument.entity.options.name.description"));
        EntitySelectorOptions.register("distance", $$0 -> {
            int $$1 = $$0.getReader().getCursor();
            MinMaxBounds.Doubles $$2 = MinMaxBounds.Doubles.fromReader($$0.getReader());
            if ($$2.getMin() != null && (Double)$$2.getMin() < 0.0 || $$2.getMax() != null && (Double)$$2.getMax() < 0.0) {
                $$0.getReader().setCursor($$1);
                throw ERROR_RANGE_NEGATIVE.createWithContext((ImmutableStringReader)$$0.getReader());
            }
            $$0.setDistance($$2);
            $$0.setWorldLimited();
        }, (Predicate<EntitySelectorParser>)((Predicate)$$0 -> $$0.getDistance().isAny()), Component.translatable("argument.entity.options.distance.description"));
        EntitySelectorOptions.register("level", $$0 -> {
            int $$1 = $$0.getReader().getCursor();
            MinMaxBounds.Ints $$2 = MinMaxBounds.Ints.fromReader($$0.getReader());
            if ($$2.getMin() != null && (Integer)$$2.getMin() < 0 || $$2.getMax() != null && (Integer)$$2.getMax() < 0) {
                $$0.getReader().setCursor($$1);
                throw ERROR_LEVEL_NEGATIVE.createWithContext((ImmutableStringReader)$$0.getReader());
            }
            $$0.setLevel($$2);
            $$0.setIncludesEntities(false);
        }, (Predicate<EntitySelectorParser>)((Predicate)$$0 -> $$0.getLevel().isAny()), Component.translatable("argument.entity.options.level.description"));
        EntitySelectorOptions.register("x", $$0 -> {
            $$0.setWorldLimited();
            $$0.setX($$0.getReader().readDouble());
        }, (Predicate<EntitySelectorParser>)((Predicate)$$0 -> $$0.getX() == null), Component.translatable("argument.entity.options.x.description"));
        EntitySelectorOptions.register("y", $$0 -> {
            $$0.setWorldLimited();
            $$0.setY($$0.getReader().readDouble());
        }, (Predicate<EntitySelectorParser>)((Predicate)$$0 -> $$0.getY() == null), Component.translatable("argument.entity.options.y.description"));
        EntitySelectorOptions.register("z", $$0 -> {
            $$0.setWorldLimited();
            $$0.setZ($$0.getReader().readDouble());
        }, (Predicate<EntitySelectorParser>)((Predicate)$$0 -> $$0.getZ() == null), Component.translatable("argument.entity.options.z.description"));
        EntitySelectorOptions.register("dx", $$0 -> {
            $$0.setWorldLimited();
            $$0.setDeltaX($$0.getReader().readDouble());
        }, (Predicate<EntitySelectorParser>)((Predicate)$$0 -> $$0.getDeltaX() == null), Component.translatable("argument.entity.options.dx.description"));
        EntitySelectorOptions.register("dy", $$0 -> {
            $$0.setWorldLimited();
            $$0.setDeltaY($$0.getReader().readDouble());
        }, (Predicate<EntitySelectorParser>)((Predicate)$$0 -> $$0.getDeltaY() == null), Component.translatable("argument.entity.options.dy.description"));
        EntitySelectorOptions.register("dz", $$0 -> {
            $$0.setWorldLimited();
            $$0.setDeltaZ($$0.getReader().readDouble());
        }, (Predicate<EntitySelectorParser>)((Predicate)$$0 -> $$0.getDeltaZ() == null), Component.translatable("argument.entity.options.dz.description"));
        EntitySelectorOptions.register("x_rotation", $$0 -> $$0.setRotX(WrappedMinMaxBounds.fromReader($$0.getReader(), true, (Function<Float, Float>)((Function)Mth::wrapDegrees))), (Predicate<EntitySelectorParser>)((Predicate)$$0 -> $$0.getRotX() == WrappedMinMaxBounds.ANY), Component.translatable("argument.entity.options.x_rotation.description"));
        EntitySelectorOptions.register("y_rotation", $$0 -> $$0.setRotY(WrappedMinMaxBounds.fromReader($$0.getReader(), true, (Function<Float, Float>)((Function)Mth::wrapDegrees))), (Predicate<EntitySelectorParser>)((Predicate)$$0 -> $$0.getRotY() == WrappedMinMaxBounds.ANY), Component.translatable("argument.entity.options.y_rotation.description"));
        EntitySelectorOptions.register("limit", $$0 -> {
            int $$1 = $$0.getReader().getCursor();
            int $$2 = $$0.getReader().readInt();
            if ($$2 < 1) {
                $$0.getReader().setCursor($$1);
                throw ERROR_LIMIT_TOO_SMALL.createWithContext((ImmutableStringReader)$$0.getReader());
            }
            $$0.setMaxResults($$2);
            $$0.setLimited(true);
        }, (Predicate<EntitySelectorParser>)((Predicate)$$0 -> !$$0.isCurrentEntity() && !$$0.isLimited()), Component.translatable("argument.entity.options.limit.description"));
        EntitySelectorOptions.register("sort", $$0 -> {
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
        }, (Predicate<EntitySelectorParser>)((Predicate)$$0 -> !$$0.isCurrentEntity() && !$$0.isSorted()), Component.translatable("argument.entity.options.sort.description"));
        EntitySelectorOptions.register("gamemode", $$0 -> {
            $$0.setSuggestions((BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>>)((BiFunction)($$1, $$2) -> {
                String $$3 = $$1.getRemaining().toLowerCase(Locale.ROOT);
                boolean $$4 = !$$0.hasGamemodeNotEquals();
                boolean $$5 = true;
                if (!$$3.isEmpty()) {
                    if ($$3.charAt(0) == '!') {
                        $$4 = false;
                        $$3 = $$3.substring(1);
                    } else {
                        $$5 = false;
                    }
                }
                for (GameType $$6 : GameType.values()) {
                    if (!$$6.getName().toLowerCase(Locale.ROOT).startsWith($$3)) continue;
                    if ($$5) {
                        $$1.suggest("!" + $$6.getName());
                    }
                    if (!$$4) continue;
                    $$1.suggest($$6.getName());
                }
                return $$1.buildFuture();
            }));
            int $$12 = $$0.getReader().getCursor();
            boolean $$22 = $$0.shouldInvertValue();
            if ($$0.hasGamemodeNotEquals() && !$$22) {
                $$0.getReader().setCursor($$12);
                throw ERROR_INAPPLICABLE_OPTION.createWithContext((ImmutableStringReader)$$0.getReader(), (Object)"gamemode");
            }
            String $$3 = $$0.getReader().readUnquotedString();
            GameType $$4 = GameType.byName($$3, null);
            if ($$4 == null) {
                $$0.getReader().setCursor($$12);
                throw ERROR_GAME_MODE_INVALID.createWithContext((ImmutableStringReader)$$0.getReader(), (Object)$$3);
            }
            $$0.setIncludesEntities(false);
            $$0.addPredicate((Predicate<Entity>)((Predicate)$$2 -> {
                if (!($$2 instanceof ServerPlayer)) {
                    return false;
                }
                GameType $$3 = ((ServerPlayer)$$2).gameMode.getGameModeForPlayer();
                return $$22 ? $$3 != $$4 : $$3 == $$4;
            }));
            if ($$22) {
                $$0.setHasGamemodeNotEquals(true);
            } else {
                $$0.setHasGamemodeEquals(true);
            }
        }, (Predicate<EntitySelectorParser>)((Predicate)$$0 -> !$$0.hasGamemodeEquals()), Component.translatable("argument.entity.options.gamemode.description"));
        EntitySelectorOptions.register("team", $$0 -> {
            boolean $$1 = $$0.shouldInvertValue();
            String $$22 = $$0.getReader().readUnquotedString();
            $$0.addPredicate((Predicate<Entity>)((Predicate)$$2 -> {
                if (!($$2 instanceof LivingEntity)) {
                    return false;
                }
                Team $$3 = $$2.getTeam();
                String $$4 = $$3 == null ? "" : $$3.getName();
                return $$4.equals((Object)$$22) != $$1;
            }));
            if ($$1) {
                $$0.setHasTeamNotEquals(true);
            } else {
                $$0.setHasTeamEquals(true);
            }
        }, (Predicate<EntitySelectorParser>)((Predicate)$$0 -> !$$0.hasTeamEquals()), Component.translatable("argument.entity.options.team.description"));
        EntitySelectorOptions.register("type", $$0 -> {
            $$0.setSuggestions((BiFunction<SuggestionsBuilder, Consumer<SuggestionsBuilder>, CompletableFuture<Suggestions>>)((BiFunction)($$1, $$2) -> {
                SharedSuggestionProvider.suggestResource((Iterable<ResourceLocation>)BuiltInRegistries.ENTITY_TYPE.keySet(), $$1, String.valueOf((char)'!'));
                SharedSuggestionProvider.suggestResource((Stream<ResourceLocation>)BuiltInRegistries.ENTITY_TYPE.getTagNames().map(TagKey::location), $$1, "!#");
                if (!$$0.isTypeLimitedInversely()) {
                    SharedSuggestionProvider.suggestResource((Iterable<ResourceLocation>)BuiltInRegistries.ENTITY_TYPE.keySet(), $$1);
                    SharedSuggestionProvider.suggestResource((Stream<ResourceLocation>)BuiltInRegistries.ENTITY_TYPE.getTagNames().map(TagKey::location), $$1, String.valueOf((char)'#'));
                }
                return $$1.buildFuture();
            }));
            int $$12 = $$0.getReader().getCursor();
            boolean $$22 = $$0.shouldInvertValue();
            if ($$0.isTypeLimitedInversely() && !$$22) {
                $$0.getReader().setCursor($$12);
                throw ERROR_INAPPLICABLE_OPTION.createWithContext((ImmutableStringReader)$$0.getReader(), (Object)"type");
            }
            if ($$22) {
                $$0.setTypeLimitedInversely();
            }
            if ($$0.isTag()) {
                TagKey<EntityType<?>> $$3 = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.read($$0.getReader()));
                $$0.addPredicate((Predicate<Entity>)((Predicate)$$2 -> $$2.getType().is($$3) != $$22));
            } else {
                ResourceLocation $$4 = ResourceLocation.read($$0.getReader());
                EntityType $$5 = (EntityType)BuiltInRegistries.ENTITY_TYPE.getOptional($$4).orElseThrow(() -> {
                    $$0.getReader().setCursor($$12);
                    return ERROR_ENTITY_TYPE_INVALID.createWithContext((ImmutableStringReader)$$0.getReader(), (Object)$$4.toString());
                });
                if (Objects.equals(EntityType.PLAYER, (Object)$$5) && !$$22) {
                    $$0.setIncludesEntities(false);
                }
                $$0.addPredicate((Predicate<Entity>)((Predicate)$$2 -> Objects.equals((Object)$$5, $$2.getType()) != $$22));
                if (!$$22) {
                    $$0.limitToType($$5);
                }
            }
        }, (Predicate<EntitySelectorParser>)((Predicate)$$0 -> !$$0.isTypeLimited()), Component.translatable("argument.entity.options.type.description"));
        EntitySelectorOptions.register("tag", $$0 -> {
            boolean $$1 = $$0.shouldInvertValue();
            String $$22 = $$0.getReader().readUnquotedString();
            $$0.addPredicate((Predicate<Entity>)((Predicate)$$2 -> {
                if ("".equals((Object)$$22)) {
                    return $$2.getTags().isEmpty() != $$1;
                }
                return $$2.getTags().contains((Object)$$22) != $$1;
            }));
        }, (Predicate<EntitySelectorParser>)((Predicate)$$0 -> true), Component.translatable("argument.entity.options.tag.description"));
        EntitySelectorOptions.register("nbt", $$0 -> {
            boolean $$1 = $$0.shouldInvertValue();
            CompoundTag $$22 = new TagParser($$0.getReader()).readStruct();
            $$0.addPredicate((Predicate<Entity>)((Predicate)$$2 -> {
                ItemStack $$4;
                CompoundTag $$3 = $$2.saveWithoutId(new CompoundTag());
                if ($$2 instanceof ServerPlayer && !($$4 = ((ServerPlayer)$$2).getInventory().getSelected()).isEmpty()) {
                    $$3.put("SelectedItem", $$4.save(new CompoundTag()));
                }
                return NbtUtils.compareNbt($$22, $$3, true) != $$1;
            }));
        }, (Predicate<EntitySelectorParser>)((Predicate)$$0 -> true), Component.translatable("argument.entity.options.nbt.description"));
        EntitySelectorOptions.register("scores", $$0 -> {
            StringReader $$1 = $$0.getReader();
            HashMap $$2 = Maps.newHashMap();
            $$1.expect('{');
            $$1.skipWhitespace();
            while ($$1.canRead() && $$1.peek() != '}') {
                $$1.skipWhitespace();
                String $$3 = $$1.readUnquotedString();
                $$1.skipWhitespace();
                $$1.expect('=');
                $$1.skipWhitespace();
                MinMaxBounds.Ints $$4 = MinMaxBounds.Ints.fromReader($$1);
                $$2.put((Object)$$3, (Object)$$4);
                $$1.skipWhitespace();
                if (!$$1.canRead() || $$1.peek() != ',') continue;
                $$1.skip();
            }
            $$1.expect('}');
            if (!$$2.isEmpty()) {
                $$0.addPredicate((Predicate<Entity>)((Predicate)arg_0 -> EntitySelectorOptions.lambda$bootStrap$52((Map)$$2, arg_0)));
            }
            $$0.setHasScores(true);
        }, (Predicate<EntitySelectorParser>)((Predicate)$$0 -> !$$0.hasScores()), Component.translatable("argument.entity.options.scores.description"));
        EntitySelectorOptions.register("advancements", $$0 -> {
            StringReader $$12 = $$0.getReader();
            HashMap $$2 = Maps.newHashMap();
            $$12.expect('{');
            $$12.skipWhitespace();
            while ($$12.canRead() && $$12.peek() != '}') {
                $$12.skipWhitespace();
                ResourceLocation $$3 = ResourceLocation.read($$12);
                $$12.skipWhitespace();
                $$12.expect('=');
                $$12.skipWhitespace();
                if ($$12.canRead() && $$12.peek() == '{') {
                    HashMap $$4 = Maps.newHashMap();
                    $$12.skipWhitespace();
                    $$12.expect('{');
                    $$12.skipWhitespace();
                    while ($$12.canRead() && $$12.peek() != '}') {
                        $$12.skipWhitespace();
                        String $$5 = $$12.readUnquotedString();
                        $$12.skipWhitespace();
                        $$12.expect('=');
                        $$12.skipWhitespace();
                        boolean $$6 = $$12.readBoolean();
                        $$4.put((Object)$$5, $$1 -> $$1.isDone() == $$6);
                        $$12.skipWhitespace();
                        if (!$$12.canRead() || $$12.peek() != ',') continue;
                        $$12.skip();
                    }
                    $$12.skipWhitespace();
                    $$12.expect('}');
                    $$12.skipWhitespace();
                    $$2.put((Object)$$3, arg_0 -> EntitySelectorOptions.lambda$bootStrap$56((Map)$$4, arg_0));
                } else {
                    boolean $$7 = $$12.readBoolean();
                    $$2.put((Object)$$3, $$1 -> $$1.isDone() == $$7);
                }
                $$12.skipWhitespace();
                if (!$$12.canRead() || $$12.peek() != ',') continue;
                $$12.skip();
            }
            $$12.expect('}');
            if (!$$2.isEmpty()) {
                $$0.addPredicate((Predicate<Entity>)((Predicate)arg_0 -> EntitySelectorOptions.lambda$bootStrap$58((Map)$$2, arg_0)));
                $$0.setIncludesEntities(false);
            }
            $$0.setHasAdvancements(true);
        }, (Predicate<EntitySelectorParser>)((Predicate)$$0 -> !$$0.hasAdvancements()), Component.translatable("argument.entity.options.advancements.description"));
        EntitySelectorOptions.register("predicate", $$0 -> {
            boolean $$1 = $$0.shouldInvertValue();
            ResourceLocation $$22 = ResourceLocation.read($$0.getReader());
            $$0.addPredicate((Predicate<Entity>)((Predicate)$$2 -> {
                if (!($$2.level instanceof ServerLevel)) {
                    return false;
                }
                ServerLevel $$3 = (ServerLevel)$$2.level;
                LootItemCondition $$4 = $$3.getServer().getPredicateManager().get($$22);
                if ($$4 == null) {
                    return false;
                }
                LootContext $$5 = new LootContext.Builder($$3).withParameter(LootContextParams.THIS_ENTITY, $$2).withParameter(LootContextParams.ORIGIN, $$2.position()).create(LootContextParamSets.SELECTOR);
                return $$1 ^ $$4.test($$5);
            }));
        }, (Predicate<EntitySelectorParser>)((Predicate)$$0 -> true), Component.translatable("argument.entity.options.predicate.description"));
    }

    public static Modifier get(EntitySelectorParser $$0, String $$1, int $$2) throws CommandSyntaxException {
        Option $$3 = (Option)((Object)OPTIONS.get((Object)$$1));
        if ($$3 != null) {
            if ($$3.canUse.test((Object)$$0)) {
                return $$3.modifier;
            }
            throw ERROR_INAPPLICABLE_OPTION.createWithContext((ImmutableStringReader)$$0.getReader(), (Object)$$1);
        }
        $$0.getReader().setCursor($$2);
        throw ERROR_UNKNOWN_OPTION.createWithContext((ImmutableStringReader)$$0.getReader(), (Object)$$1);
    }

    public static void suggestNames(EntitySelectorParser $$0, SuggestionsBuilder $$1) {
        String $$2 = $$1.getRemaining().toLowerCase(Locale.ROOT);
        for (Map.Entry $$3 : OPTIONS.entrySet()) {
            if (!((Option)((Object)$$3.getValue())).canUse.test((Object)$$0) || !((String)$$3.getKey()).toLowerCase(Locale.ROOT).startsWith($$2)) continue;
            $$1.suggest((String)$$3.getKey() + "=", (Message)((Option)((Object)$$3.getValue())).description);
        }
    }

    private static /* synthetic */ boolean lambda$bootStrap$58(Map $$0, Entity $$1) {
        if (!($$1 instanceof ServerPlayer)) {
            return false;
        }
        ServerPlayer $$2 = (ServerPlayer)$$1;
        PlayerAdvancements $$3 = $$2.getAdvancements();
        ServerAdvancementManager $$4 = $$2.getServer().getAdvancements();
        for (Map.Entry $$5 : $$0.entrySet()) {
            Advancement $$6 = $$4.getAdvancement((ResourceLocation)$$5.getKey());
            if ($$6 != null && ((Predicate)$$5.getValue()).test((Object)$$3.getOrStartProgress($$6))) continue;
            return false;
        }
        return true;
    }

    private static /* synthetic */ boolean lambda$bootStrap$56(Map $$0, AdvancementProgress $$1) {
        for (Map.Entry $$2 : $$0.entrySet()) {
            CriterionProgress $$3 = $$1.getCriterion((String)$$2.getKey());
            if ($$3 != null && ((Predicate)$$2.getValue()).test((Object)$$3)) continue;
            return false;
        }
        return true;
    }

    private static /* synthetic */ boolean lambda$bootStrap$52(Map $$0, Entity $$1) {
        ServerScoreboard $$2 = $$1.getServer().getScoreboard();
        String $$3 = $$1.getScoreboardName();
        for (Map.Entry $$4 : $$0.entrySet()) {
            Objective $$5 = $$2.getObjective((String)$$4.getKey());
            if ($$5 == null) {
                return false;
            }
            if (!$$2.hasPlayerScore($$3, $$5)) {
                return false;
            }
            Score $$6 = $$2.getOrCreatePlayerScore($$3, $$5);
            int $$7 = $$6.getScore();
            if (((MinMaxBounds.Ints)$$4.getValue()).matches($$7)) continue;
            return false;
        }
        return true;
    }

    private static /* synthetic */ CompletableFuture lambda$bootStrap$30(SuggestionsBuilder $$0, Consumer $$1) {
        return SharedSuggestionProvider.suggest((Iterable<String>)Arrays.asList((Object[])new String[]{"nearest", "furthest", "random", "arbitrary"}), $$0);
    }

    record Option(Modifier modifier, Predicate<EntitySelectorParser> canUse, Component description) {
    }

    public static interface Modifier {
        public void handle(EntitySelectorParser var1) throws CommandSyntaxException;
    }
}