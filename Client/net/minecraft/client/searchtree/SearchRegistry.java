/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.HashMap
 *  java.util.List
 *  java.util.Map
 *  java.util.function.Function
 */
package net.minecraft.client.searchtree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import net.minecraft.client.searchtree.RefreshableSearchTree;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.item.ItemStack;

public class SearchRegistry
implements ResourceManagerReloadListener {
    public static final Key<ItemStack> CREATIVE_NAMES = new Key();
    public static final Key<ItemStack> CREATIVE_TAGS = new Key();
    public static final Key<RecipeCollection> RECIPE_COLLECTIONS = new Key();
    private final Map<Key<?>, TreeEntry<?>> searchTrees = new HashMap();

    @Override
    public void onResourceManagerReload(ResourceManager $$0) {
        for (TreeEntry $$1 : this.searchTrees.values()) {
            $$1.refresh();
        }
    }

    public <T> void register(Key<T> $$0, TreeBuilderSupplier<T> $$1) {
        this.searchTrees.put($$0, new TreeEntry<T>($$1));
    }

    private <T> TreeEntry<T> getSupplier(Key<T> $$0) {
        TreeEntry $$1 = (TreeEntry)this.searchTrees.get($$0);
        if ($$1 == null) {
            throw new IllegalStateException("Tree builder not registered");
        }
        return $$1;
    }

    public <T> void populate(Key<T> $$0, List<T> $$1) {
        this.getSupplier($$0).populate($$1);
    }

    public <T> SearchTree<T> getTree(Key<T> $$0) {
        return this.getSupplier($$0).tree;
    }

    static class TreeEntry<T> {
        private final TreeBuilderSupplier<T> factory;
        RefreshableSearchTree<T> tree = RefreshableSearchTree.empty();

        TreeEntry(TreeBuilderSupplier<T> $$0) {
            this.factory = $$0;
        }

        void populate(List<T> $$0) {
            this.tree = (RefreshableSearchTree)this.factory.apply($$0);
            this.tree.refresh();
        }

        void refresh() {
            this.tree.refresh();
        }
    }

    public static interface TreeBuilderSupplier<T>
    extends Function<List<T>, RefreshableSearchTree<T>> {
    }

    public static class Key<T> {
    }
}