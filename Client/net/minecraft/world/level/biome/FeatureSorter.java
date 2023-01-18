/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap
 *  it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
 *  java.lang.IllegalStateException
 *  java.lang.Math
 *  java.lang.Object
 *  java.util.ArrayList
 *  java.util.Collections
 *  java.util.Comparator
 *  java.util.List
 *  java.util.ListIterator
 *  java.util.Set
 *  java.util.TreeMap
 *  java.util.TreeSet
 *  java.util.function.Function
 *  java.util.function.ToIntFunction
 *  java.util.stream.Collectors
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.Graph;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.apache.commons.lang3.mutable.MutableInt;

public class FeatureSorter {
    public static <T> List<StepFeatureData> buildFeaturesPerStep(List<T> $$0, Function<T, List<HolderSet<PlacedFeature>>> $$12, boolean $$2) {
        Object2IntOpenHashMap $$3 = new Object2IntOpenHashMap();
        MutableInt $$4 = new MutableInt(0);
        record FeatureData(int featureIndex, int step, PlacedFeature feature) {
        }
        Comparator $$5 = Comparator.comparingInt(FeatureData::step).thenComparingInt(FeatureData::featureIndex);
        TreeMap $$6 = new TreeMap($$5);
        int $$7 = 0;
        for (Object $$8 : $$0) {
            ArrayList $$9 = Lists.newArrayList();
            List $$10 = (List)$$12.apply($$8);
            $$7 = Math.max((int)$$7, (int)$$10.size());
            for (int $$11 = 0; $$11 < $$10.size(); ++$$11) {
                for (Holder $$122 : (HolderSet)$$10.get($$11)) {
                    PlacedFeature $$13 = (PlacedFeature)((Object)$$122.value());
                    $$9.add((Object)new FeatureData($$3.computeIfAbsent((Object)$$13, $$1 -> $$4.getAndIncrement()), $$11, $$13));
                }
            }
            for (int $$14 = 0; $$14 < $$9.size(); ++$$14) {
                Set $$15 = (Set)$$6.computeIfAbsent((Object)((FeatureData)((Object)$$9.get($$14))), $$1 -> new TreeSet($$5));
                if ($$14 >= $$9.size() - 1) continue;
                $$15.add((Object)((FeatureData)((Object)$$9.get($$14 + 1))));
            }
        }
        TreeSet $$16 = new TreeSet($$5);
        TreeSet $$17 = new TreeSet($$5);
        ArrayList $$18 = Lists.newArrayList();
        for (FeatureData $$19 : $$6.keySet()) {
            if (!$$17.isEmpty()) {
                throw new IllegalStateException("You somehow broke the universe; DFS bork (iteration finished with non-empty in-progress vertex set");
            }
            if ($$16.contains((Object)$$19) || !Graph.depthFirstSearch($$6, $$16, $$17, arg_0 -> ((List)$$18).add(arg_0), $$19)) continue;
            if ($$2) {
                int $$21;
                ArrayList $$20 = new ArrayList($$0);
                do {
                    $$21 = $$20.size();
                    ListIterator $$22 = $$20.listIterator();
                    while ($$22.hasNext()) {
                        Object $$23 = $$22.next();
                        $$22.remove();
                        try {
                            FeatureSorter.buildFeaturesPerStep($$20, $$12, false);
                        }
                        catch (IllegalStateException $$24) {
                            continue;
                        }
                        $$22.add($$23);
                    }
                } while ($$21 != $$20.size());
                throw new IllegalStateException("Feature order cycle found, involved sources: " + (List)$$20);
            }
            throw new IllegalStateException("Feature order cycle found");
        }
        Collections.reverse((List)$$18);
        ImmutableList.Builder $$25 = ImmutableList.builder();
        int $$26 = 0;
        while ($$26 < $$7) {
            int $$27 = $$26++;
            List $$28 = (List)$$18.stream().filter($$1 -> $$1.step() == $$27).map(FeatureData::feature).collect(Collectors.toList());
            $$25.add((Object)new StepFeatureData((List<PlacedFeature>)$$28));
        }
        return $$25.build();
    }

    public record StepFeatureData(List<PlacedFeature> features, ToIntFunction<PlacedFeature> indexMapping) {
        StepFeatureData(List<PlacedFeature> $$02) {
            this($$02, Util.createIndexLookup($$02, $$0 -> new Object2IntOpenCustomHashMap($$0, Util.identityStrategy())));
        }
    }
}