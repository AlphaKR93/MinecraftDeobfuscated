/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Comparator
 *  java.util.List
 *  java.util.function.Function
 *  java.util.function.ToIntFunction
 *  java.util.stream.Stream
 */
package net.minecraft.client.searchtree;

import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.client.searchtree.IntersectionIterator;
import net.minecraft.client.searchtree.RefreshableSearchTree;
import net.minecraft.client.searchtree.ResourceLocationSearchTree;
import net.minecraft.resources.ResourceLocation;

public class IdSearchTree<T>
implements RefreshableSearchTree<T> {
    protected final Comparator<T> additionOrder;
    protected final ResourceLocationSearchTree<T> resourceLocationSearchTree;

    public IdSearchTree(Function<T, Stream<ResourceLocation>> $$0, List<T> $$1) {
        ToIntFunction<T> $$2 = Util.createIndexLookup($$1);
        this.additionOrder = Comparator.comparingInt($$2);
        this.resourceLocationSearchTree = ResourceLocationSearchTree.create($$1, $$0);
    }

    @Override
    public List<T> search(String $$0) {
        int $$1 = $$0.indexOf(58);
        if ($$1 == -1) {
            return this.searchPlainText($$0);
        }
        return this.searchResourceLocation($$0.substring(0, $$1).trim(), $$0.substring($$1 + 1).trim());
    }

    protected List<T> searchPlainText(String $$0) {
        return this.resourceLocationSearchTree.searchPath($$0);
    }

    protected List<T> searchResourceLocation(String $$0, String $$1) {
        List<T> $$2 = this.resourceLocationSearchTree.searchNamespace($$0);
        List<T> $$3 = this.resourceLocationSearchTree.searchPath($$1);
        return ImmutableList.copyOf(new IntersectionIterator<T>($$2.iterator(), $$3.iterator(), this.additionOrder));
    }
}