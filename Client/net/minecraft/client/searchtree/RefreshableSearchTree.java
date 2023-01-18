/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.List
 */
package net.minecraft.client.searchtree;

import java.util.List;
import net.minecraft.client.searchtree.SearchTree;

public interface RefreshableSearchTree<T>
extends SearchTree<T> {
    public static <T> RefreshableSearchTree<T> empty() {
        return $$0 -> List.of();
    }

    default public void refresh() {
    }
}