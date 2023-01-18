/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Float
 *  java.lang.IllegalArgumentException
 *  java.lang.IllegalStateException
 *  java.lang.Long
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.ThreadLocal
 *  java.util.ArrayList
 *  java.util.Arrays
 *  java.util.Comparator
 *  java.util.Iterator
 *  java.util.List
 *  java.util.Locale
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.biome;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.QuartPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;

public class Climate {
    private static final boolean DEBUG_SLOW_BIOME_SEARCH = false;
    private static final float QUANTIZATION_FACTOR = 10000.0f;
    @VisibleForTesting
    protected static final int PARAMETER_COUNT = 7;

    public static TargetPoint target(float $$0, float $$1, float $$2, float $$3, float $$4, float $$5) {
        return new TargetPoint(Climate.quantizeCoord($$0), Climate.quantizeCoord($$1), Climate.quantizeCoord($$2), Climate.quantizeCoord($$3), Climate.quantizeCoord($$4), Climate.quantizeCoord($$5));
    }

    public static ParameterPoint parameters(float $$0, float $$1, float $$2, float $$3, float $$4, float $$5, float $$6) {
        return new ParameterPoint(Parameter.point($$0), Parameter.point($$1), Parameter.point($$2), Parameter.point($$3), Parameter.point($$4), Parameter.point($$5), Climate.quantizeCoord($$6));
    }

    public static ParameterPoint parameters(Parameter $$0, Parameter $$1, Parameter $$2, Parameter $$3, Parameter $$4, Parameter $$5, float $$6) {
        return new ParameterPoint($$0, $$1, $$2, $$3, $$4, $$5, Climate.quantizeCoord($$6));
    }

    public static long quantizeCoord(float $$0) {
        return (long)($$0 * 10000.0f);
    }

    public static float unquantizeCoord(long $$0) {
        return (float)$$0 / 10000.0f;
    }

    public static Sampler empty() {
        DensityFunction $$0 = DensityFunctions.zero();
        return new Sampler($$0, $$0, $$0, $$0, $$0, $$0, (List<ParameterPoint>)List.of());
    }

    public static BlockPos findSpawnPosition(List<ParameterPoint> $$0, Sampler $$1) {
        return new SpawnFinder($$0, (Sampler)$$1).result.location();
    }

    public record TargetPoint(long temperature, long humidity, long continentalness, long erosion, long depth, long weirdness) {
        @VisibleForTesting
        protected long[] toParameterArray() {
            return new long[]{this.temperature, this.humidity, this.continentalness, this.erosion, this.depth, this.weirdness, 0L};
        }
    }

    public record ParameterPoint(Parameter temperature, Parameter humidity, Parameter continentalness, Parameter erosion, Parameter depth, Parameter weirdness, long offset) {
        public static final Codec<ParameterPoint> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Parameter.CODEC.fieldOf("temperature").forGetter($$0 -> $$0.temperature), (App)Parameter.CODEC.fieldOf("humidity").forGetter($$0 -> $$0.humidity), (App)Parameter.CODEC.fieldOf("continentalness").forGetter($$0 -> $$0.continentalness), (App)Parameter.CODEC.fieldOf("erosion").forGetter($$0 -> $$0.erosion), (App)Parameter.CODEC.fieldOf("depth").forGetter($$0 -> $$0.depth), (App)Parameter.CODEC.fieldOf("weirdness").forGetter($$0 -> $$0.weirdness), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("offset").xmap(Climate::quantizeCoord, Climate::unquantizeCoord).forGetter($$0 -> $$0.offset)).apply((Applicative)$$02, ParameterPoint::new));

        long fitness(TargetPoint $$0) {
            return Mth.square(this.temperature.distance($$0.temperature)) + Mth.square(this.humidity.distance($$0.humidity)) + Mth.square(this.continentalness.distance($$0.continentalness)) + Mth.square(this.erosion.distance($$0.erosion)) + Mth.square(this.depth.distance($$0.depth)) + Mth.square(this.weirdness.distance($$0.weirdness)) + Mth.square(this.offset);
        }

        protected List<Parameter> parameterSpace() {
            return ImmutableList.of((Object)((Object)this.temperature), (Object)((Object)this.humidity), (Object)((Object)this.continentalness), (Object)((Object)this.erosion), (Object)((Object)this.depth), (Object)((Object)this.weirdness), (Object)((Object)new Parameter(this.offset, this.offset)));
        }
    }

    public record Parameter(long min, long max) {
        public static final Codec<Parameter> CODEC = ExtraCodecs.intervalCodec(Codec.floatRange((float)-2.0f, (float)2.0f), "min", "max", ($$0, $$1) -> {
            if ($$0.compareTo($$1) > 0) {
                return DataResult.error((String)("Cannon construct interval, min > max (" + $$0 + " > " + $$1 + ")"));
            }
            return DataResult.success((Object)((Object)new Parameter(Climate.quantizeCoord($$0.floatValue()), Climate.quantizeCoord($$1.floatValue()))));
        }, $$0 -> Float.valueOf((float)Climate.unquantizeCoord($$0.min())), $$0 -> Float.valueOf((float)Climate.unquantizeCoord($$0.max())));

        public static Parameter point(float $$0) {
            return Parameter.span($$0, $$0);
        }

        public static Parameter span(float $$0, float $$1) {
            if ($$0 > $$1) {
                throw new IllegalArgumentException("min > max: " + $$0 + " " + $$1);
            }
            return new Parameter(Climate.quantizeCoord($$0), Climate.quantizeCoord($$1));
        }

        public static Parameter span(Parameter $$0, Parameter $$1) {
            if ($$0.min() > $$1.max()) {
                throw new IllegalArgumentException("min > max: " + $$0 + " " + $$1);
            }
            return new Parameter($$0.min(), $$1.max());
        }

        public String toString() {
            return this.min == this.max ? String.format((Locale)Locale.ROOT, (String)"%d", (Object[])new Object[]{this.min}) : String.format((Locale)Locale.ROOT, (String)"[%d-%d]", (Object[])new Object[]{this.min, this.max});
        }

        public long distance(long $$0) {
            long $$1 = $$0 - this.max;
            long $$2 = this.min - $$0;
            if ($$1 > 0L) {
                return $$1;
            }
            return Math.max((long)$$2, (long)0L);
        }

        public long distance(Parameter $$0) {
            long $$1 = $$0.min() - this.max;
            long $$2 = this.min - $$0.max();
            if ($$1 > 0L) {
                return $$1;
            }
            return Math.max((long)$$2, (long)0L);
        }

        public Parameter span(@Nullable Parameter $$0) {
            return $$0 == null ? this : new Parameter(Math.min((long)this.min, (long)$$0.min()), Math.max((long)this.max, (long)$$0.max()));
        }
    }

    public record Sampler(DensityFunction temperature, DensityFunction humidity, DensityFunction continentalness, DensityFunction erosion, DensityFunction depth, DensityFunction weirdness, List<ParameterPoint> spawnTarget) {
        public TargetPoint sample(int $$0, int $$1, int $$2) {
            int $$3 = QuartPos.toBlock($$0);
            int $$4 = QuartPos.toBlock($$1);
            int $$5 = QuartPos.toBlock($$2);
            DensityFunction.SinglePointContext $$6 = new DensityFunction.SinglePointContext($$3, $$4, $$5);
            return Climate.target((float)this.temperature.compute($$6), (float)this.humidity.compute($$6), (float)this.continentalness.compute($$6), (float)this.erosion.compute($$6), (float)this.depth.compute($$6), (float)this.weirdness.compute($$6));
        }

        public BlockPos findSpawnPosition() {
            if (this.spawnTarget.isEmpty()) {
                return BlockPos.ZERO;
            }
            return Climate.findSpawnPosition(this.spawnTarget, this);
        }
    }

    static class SpawnFinder {
        Result result;

        SpawnFinder(List<ParameterPoint> $$0, Sampler $$1) {
            this.result = SpawnFinder.getSpawnPositionAndFitness($$0, $$1, 0, 0);
            this.radialSearch($$0, $$1, 2048.0f, 512.0f);
            this.radialSearch($$0, $$1, 512.0f, 32.0f);
        }

        private void radialSearch(List<ParameterPoint> $$0, Sampler $$1, float $$2, float $$3) {
            float $$4 = 0.0f;
            float $$5 = $$3;
            BlockPos $$6 = this.result.location();
            while ($$5 <= $$2) {
                int $$8;
                int $$7 = $$6.getX() + (int)(Math.sin((double)$$4) * (double)$$5);
                Result $$9 = SpawnFinder.getSpawnPositionAndFitness($$0, $$1, $$7, $$8 = $$6.getZ() + (int)(Math.cos((double)$$4) * (double)$$5));
                if ($$9.fitness() < this.result.fitness()) {
                    this.result = $$9;
                }
                if (!((double)($$4 += $$3 / $$5) > Math.PI * 2)) continue;
                $$4 = 0.0f;
                $$5 += $$3;
            }
        }

        private static Result getSpawnPositionAndFitness(List<ParameterPoint> $$0, Sampler $$1, int $$2, int $$3) {
            double $$4 = Mth.square(2500.0);
            int $$5 = 2;
            long $$6 = (long)((double)Mth.square(10000.0f) * Math.pow((double)((double)(Mth.square((long)$$2) + Mth.square((long)$$3)) / $$4), (double)2.0));
            TargetPoint $$7 = $$1.sample(QuartPos.fromBlock($$2), 0, QuartPos.fromBlock($$3));
            TargetPoint $$8 = new TargetPoint($$7.temperature(), $$7.humidity(), $$7.continentalness(), $$7.erosion(), 0L, $$7.weirdness());
            long $$9 = Long.MAX_VALUE;
            for (ParameterPoint $$10 : $$0) {
                $$9 = Math.min((long)$$9, (long)$$10.fitness($$8));
            }
            return new Result(new BlockPos($$2, 0, $$3), $$6 + $$9);
        }

        record Result(BlockPos location, long fitness) {
        }
    }

    public static class ParameterList<T> {
        private final List<Pair<ParameterPoint, T>> values;
        private final RTree<T> index;

        public ParameterList(List<Pair<ParameterPoint, T>> $$0) {
            this.values = $$0;
            this.index = RTree.create($$0);
        }

        public List<Pair<ParameterPoint, T>> values() {
            return this.values;
        }

        public T findValue(TargetPoint $$0) {
            return this.findValueIndex($$0);
        }

        @VisibleForTesting
        public T findValueBruteForce(TargetPoint $$0) {
            Iterator $$1 = this.values().iterator();
            Pair $$2 = (Pair)$$1.next();
            long $$3 = ((ParameterPoint)((Object)$$2.getFirst())).fitness($$0);
            Object $$4 = $$2.getSecond();
            while ($$1.hasNext()) {
                Pair $$5 = (Pair)$$1.next();
                long $$6 = ((ParameterPoint)((Object)$$5.getFirst())).fitness($$0);
                if ($$6 >= $$3) continue;
                $$3 = $$6;
                $$4 = $$5.getSecond();
            }
            return (T)$$4;
        }

        public T findValueIndex(TargetPoint $$0) {
            return this.findValueIndex($$0, RTree.Node::distance);
        }

        protected T findValueIndex(TargetPoint $$0, DistanceMetric<T> $$1) {
            return this.index.search($$0, $$1);
        }
    }

    protected static final class RTree<T> {
        private static final int CHILDREN_PER_NODE = 6;
        private final Node<T> root;
        private final ThreadLocal<Leaf<T>> lastResult = new ThreadLocal();

        private RTree(Node<T> $$0) {
            this.root = $$0;
        }

        public static <T> RTree<T> create(List<Pair<ParameterPoint, T>> $$02) {
            if ($$02.isEmpty()) {
                throw new IllegalArgumentException("Need at least one value to build the search tree.");
            }
            int $$1 = ((ParameterPoint)((Object)((Pair)$$02.get(0)).getFirst())).parameterSpace().size();
            if ($$1 != 7) {
                throw new IllegalStateException("Expecting parameter space to be 7, got " + $$1);
            }
            List $$2 = (List)$$02.stream().map($$0 -> new Leaf<Object>((ParameterPoint)((Object)((Object)$$0.getFirst())), $$0.getSecond())).collect(Collectors.toCollection(ArrayList::new));
            return new RTree<T>(RTree.build($$1, $$2));
        }

        private static <T> Node<T> build(int $$0, List<? extends Node<T>> $$12) {
            if ($$12.isEmpty()) {
                throw new IllegalStateException("Need at least one child to build a node");
            }
            if ($$12.size() == 1) {
                return (Node)$$12.get(0);
            }
            if ($$12.size() <= 6) {
                $$12.sort(Comparator.comparingLong($$1 -> {
                    long $$2 = 0L;
                    for (int $$3 = 0; $$3 < $$0; ++$$3) {
                        Parameter $$4 = $$1.parameterSpace[$$3];
                        $$2 += Math.abs((long)(($$4.min() + $$4.max()) / 2L));
                    }
                    return $$2;
                }));
                return new SubTree($$12);
            }
            long $$2 = Long.MAX_VALUE;
            int $$3 = -1;
            List<SubTree<T>> $$4 = null;
            for (int $$5 = 0; $$5 < $$0; ++$$5) {
                RTree.sort($$12, $$0, $$5, false);
                List<SubTree<T>> $$6 = RTree.bucketize($$12);
                long $$7 = 0L;
                for (SubTree $$8 : $$6) {
                    $$7 += RTree.cost($$8.parameterSpace);
                }
                if ($$2 <= $$7) continue;
                $$2 = $$7;
                $$3 = $$5;
                $$4 = $$6;
            }
            RTree.sort($$4, $$0, $$3, true);
            return new SubTree((List)$$4.stream().map($$1 -> RTree.build($$0, Arrays.asList((Object[])$$1.children))).collect(Collectors.toList()));
        }

        private static <T> void sort(List<? extends Node<T>> $$0, int $$1, int $$2, boolean $$3) {
            Comparator $$4 = RTree.comparator($$2, $$3);
            for (int $$5 = 1; $$5 < $$1; ++$$5) {
                $$4 = $$4.thenComparing(RTree.comparator(($$2 + $$5) % $$1, $$3));
            }
            $$0.sort($$4);
        }

        private static <T> Comparator<Node<T>> comparator(int $$0, boolean $$1) {
            return Comparator.comparingLong($$2 -> {
                Parameter $$3 = $$2.parameterSpace[$$0];
                long $$4 = ($$3.min() + $$3.max()) / 2L;
                return $$1 ? Math.abs((long)$$4) : $$4;
            });
        }

        private static <T> List<SubTree<T>> bucketize(List<? extends Node<T>> $$0) {
            ArrayList $$1 = Lists.newArrayList();
            ArrayList $$2 = Lists.newArrayList();
            int $$3 = (int)Math.pow((double)6.0, (double)Math.floor((double)(Math.log((double)((double)$$0.size() - 0.01)) / Math.log((double)6.0))));
            for (Node $$4 : $$0) {
                $$2.add((Object)$$4);
                if ($$2.size() < $$3) continue;
                $$1.add(new SubTree($$2));
                $$2 = Lists.newArrayList();
            }
            if (!$$2.isEmpty()) {
                $$1.add(new SubTree($$2));
            }
            return $$1;
        }

        private static long cost(Parameter[] $$0) {
            long $$1 = 0L;
            for (Parameter $$2 : $$0) {
                $$1 += Math.abs((long)($$2.max() - $$2.min()));
            }
            return $$1;
        }

        static <T> List<Parameter> buildParameterSpace(List<? extends Node<T>> $$0) {
            if ($$0.isEmpty()) {
                throw new IllegalArgumentException("SubTree needs at least one child");
            }
            int $$1 = 7;
            ArrayList $$2 = Lists.newArrayList();
            for (int $$3 = 0; $$3 < 7; ++$$3) {
                $$2.add(null);
            }
            for (Node $$4 : $$0) {
                for (int $$5 = 0; $$5 < 7; ++$$5) {
                    $$2.set($$5, (Object)$$4.parameterSpace[$$5].span((Parameter)((Object)$$2.get($$5))));
                }
            }
            return $$2;
        }

        public T search(TargetPoint $$0, DistanceMetric<T> $$1) {
            long[] $$2 = $$0.toParameterArray();
            Leaf<T> $$3 = this.root.search($$2, (Leaf)this.lastResult.get(), $$1);
            this.lastResult.set($$3);
            return $$3.value;
        }

        static abstract class Node<T> {
            protected final Parameter[] parameterSpace;

            protected Node(List<Parameter> $$0) {
                this.parameterSpace = (Parameter[])$$0.toArray((Object[])new Parameter[0]);
            }

            protected abstract Leaf<T> search(long[] var1, @Nullable Leaf<T> var2, DistanceMetric<T> var3);

            protected long distance(long[] $$0) {
                long $$1 = 0L;
                for (int $$2 = 0; $$2 < 7; ++$$2) {
                    $$1 += Mth.square(this.parameterSpace[$$2].distance($$0[$$2]));
                }
                return $$1;
            }

            public String toString() {
                return Arrays.toString((Object[])this.parameterSpace);
            }
        }

        static final class SubTree<T>
        extends Node<T> {
            final Node<T>[] children;

            protected SubTree(List<? extends Node<T>> $$0) {
                this(RTree.buildParameterSpace($$0), $$0);
            }

            protected SubTree(List<Parameter> $$0, List<? extends Node<T>> $$1) {
                super($$0);
                this.children = (Node[])$$1.toArray((Object[])new Node[0]);
            }

            @Override
            protected Leaf<T> search(long[] $$0, @Nullable Leaf<T> $$1, DistanceMetric<T> $$2) {
                long $$3 = $$1 == null ? Long.MAX_VALUE : $$2.distance($$1, $$0);
                Leaf<T> $$4 = $$1;
                for (Node<T> $$5 : this.children) {
                    long $$8;
                    long $$6 = $$2.distance($$5, $$0);
                    if ($$3 <= $$6) continue;
                    Leaf<T> $$7 = $$5.search($$0, $$4, $$2);
                    long l = $$8 = $$5 == $$7 ? $$6 : $$2.distance($$7, $$0);
                    if ($$3 <= $$8) continue;
                    $$3 = $$8;
                    $$4 = $$7;
                }
                return $$4;
            }
        }

        static final class Leaf<T>
        extends Node<T> {
            final T value;

            Leaf(ParameterPoint $$0, T $$1) {
                super($$0.parameterSpace());
                this.value = $$1;
            }

            @Override
            protected Leaf<T> search(long[] $$0, @Nullable Leaf<T> $$1, DistanceMetric<T> $$2) {
                return this;
            }
        }
    }

    static interface DistanceMetric<T> {
        public long distance(RTree.Node<T> var1, long[] var2);
    }
}