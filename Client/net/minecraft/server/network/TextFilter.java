/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.List
 *  java.util.concurrent.CompletableFuture
 */
package net.minecraft.server.network;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.server.network.FilteredText;

public interface TextFilter {
    public static final TextFilter DUMMY = new TextFilter(){

        @Override
        public void join() {
        }

        @Override
        public void leave() {
        }

        @Override
        public CompletableFuture<FilteredText> processStreamMessage(String $$0) {
            return CompletableFuture.completedFuture((Object)((Object)FilteredText.passThrough($$0)));
        }

        @Override
        public CompletableFuture<List<FilteredText>> processMessageBundle(List<String> $$0) {
            return CompletableFuture.completedFuture((Object)((List)$$0.stream().map(FilteredText::passThrough).collect(ImmutableList.toImmutableList())));
        }
    };

    public void join();

    public void leave();

    public CompletableFuture<FilteredText> processStreamMessage(String var1);

    public CompletableFuture<List<FilteredText>> processMessageBundle(List<String> var1);
}