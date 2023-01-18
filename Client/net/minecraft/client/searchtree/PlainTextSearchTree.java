/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  java.util.Locale
 *  java.util.function.Function
 *  java.util.stream.Stream
 */
package net.minecraft.client.searchtree;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.client.searchtree.SuffixArray;

public interface PlainTextSearchTree<T> {
    public static <T> PlainTextSearchTree<T> empty() {
        return $$0 -> List.of();
    }

    public static <T> PlainTextSearchTree<T> create(List<T> $$0, Function<T, Stream<String>> $$1) {
        if ($$0.isEmpty()) {
            return PlainTextSearchTree.empty();
        }
        SuffixArray $$22 = new SuffixArray();
        for (Object $$3 : $$0) {
            ((Stream)$$1.apply($$3)).forEach($$2 -> $$22.add($$3, $$2.toLowerCase(Locale.ROOT)));
        }
        $$22.generate();
        return $$22::search;
    }

    public List<T> search(String var1);
}