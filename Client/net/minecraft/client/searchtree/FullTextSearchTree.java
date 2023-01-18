/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.function.Function
 *  java.util.stream.Stream
 */
package net.minecraft.client.searchtree;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.client.searchtree.IdSearchTree;
import net.minecraft.client.searchtree.IntersectionIterator;
import net.minecraft.client.searchtree.MergingUniqueIterator;
import net.minecraft.client.searchtree.PlainTextSearchTree;
import net.minecraft.resources.ResourceLocation;

public class FullTextSearchTree<T>
extends IdSearchTree<T> {
    private final List<T> contents;
    private final Function<T, Stream<String>> filler;
    private PlainTextSearchTree<T> plainTextSearchTree = PlainTextSearchTree.empty();

    public FullTextSearchTree(Function<T, Stream<String>> $$0, Function<T, Stream<ResourceLocation>> $$1, List<T> $$2) {
        super($$1, $$2);
        this.contents = $$2;
        this.filler = $$0;
    }

    @Override
    public void refresh() {
        super.refresh();
        this.plainTextSearchTree = PlainTextSearchTree.create(this.contents, this.filler);
    }

    @Override
    protected List<T> searchPlainText(String $$0) {
        return this.plainTextSearchTree.search($$0);
    }

    @Override
    protected List<T> searchResourceLocation(String $$0, String $$1) {
        List $$2 = this.resourceLocationSearchTree.searchNamespace($$0);
        List $$3 = this.resourceLocationSearchTree.searchPath($$1);
        List<T> $$4 = this.plainTextSearchTree.search($$1);
        MergingUniqueIterator $$5 = new MergingUniqueIterator($$3.iterator(), $$4.iterator(), this.additionOrder);
        return ImmutableList.copyOf(new IntersectionIterator($$2.iterator(), $$5, this.additionOrder));
    }
}