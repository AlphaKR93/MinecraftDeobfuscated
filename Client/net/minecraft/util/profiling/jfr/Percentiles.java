/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.math.Quantiles
 *  com.google.common.math.Quantiles$ScaleAndIndexes
 *  it.unimi.dsi.fastutil.ints.Int2DoubleRBTreeMap
 *  it.unimi.dsi.fastutil.ints.Int2DoubleSortedMap
 *  it.unimi.dsi.fastutil.ints.Int2DoubleSortedMaps
 *  java.lang.Double
 *  java.lang.Integer
 *  java.lang.Object
 *  java.util.Comparator
 *  java.util.Map
 */
package net.minecraft.util.profiling.jfr;

import com.google.common.math.Quantiles;
import it.unimi.dsi.fastutil.ints.Int2DoubleRBTreeMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleSortedMap;
import it.unimi.dsi.fastutil.ints.Int2DoubleSortedMaps;
import java.util.Comparator;
import java.util.Map;
import net.minecraft.Util;

public class Percentiles {
    public static final Quantiles.ScaleAndIndexes DEFAULT_INDEXES = Quantiles.scale((int)100).indexes(new int[]{50, 75, 90, 99});

    private Percentiles() {
    }

    public static Map<Integer, Double> evaluate(long[] $$0) {
        return $$0.length == 0 ? Map.of() : Percentiles.sorted((Map<Integer, Double>)DEFAULT_INDEXES.compute($$0));
    }

    public static Map<Integer, Double> evaluate(double[] $$0) {
        return $$0.length == 0 ? Map.of() : Percentiles.sorted((Map<Integer, Double>)DEFAULT_INDEXES.compute($$0));
    }

    private static Map<Integer, Double> sorted(Map<Integer, Double> $$0) {
        Int2DoubleSortedMap $$12 = (Int2DoubleSortedMap)Util.make(new Int2DoubleRBTreeMap(Comparator.reverseOrder()), $$1 -> $$1.putAll($$0));
        return Int2DoubleSortedMaps.unmodifiable((Int2DoubleSortedMap)$$12);
    }
}