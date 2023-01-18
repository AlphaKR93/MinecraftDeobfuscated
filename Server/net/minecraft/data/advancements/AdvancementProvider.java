/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  java.lang.IllegalStateException
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.nio.file.Path
 *  java.util.ArrayList
 *  java.util.HashSet
 *  java.util.List
 *  java.util.Set
 *  java.util.concurrent.CompletableFuture
 *  java.util.function.Consumer
 */
package net.minecraft.data.advancements;

import com.google.gson.JsonElement;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementSubProvider;

public class AdvancementProvider
implements DataProvider {
    private final PackOutput.PathProvider pathProvider;
    private final List<AdvancementSubProvider> subProviders;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public AdvancementProvider(PackOutput $$0, CompletableFuture<HolderLookup.Provider> $$1, List<AdvancementSubProvider> $$2) {
        this.pathProvider = $$0.createPathProvider(PackOutput.Target.DATA_PACK, "advancements");
        this.subProviders = $$2;
        this.registries = $$1;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput $$0) {
        return this.registries.thenCompose($$1 -> {
            HashSet $$2 = new HashSet();
            ArrayList $$3 = new ArrayList();
            Consumer $$4 = arg_0 -> this.lambda$run$0((Set)$$2, (List)$$3, $$0, arg_0);
            for (AdvancementSubProvider $$5 : this.subProviders) {
                $$5.generate((HolderLookup.Provider)$$1, (Consumer<Advancement>)$$4);
            }
            return CompletableFuture.allOf((CompletableFuture[])((CompletableFuture[])$$3.toArray(CompletableFuture[]::new)));
        });
    }

    @Override
    public final String getName() {
        return "Advancements";
    }

    private /* synthetic */ void lambda$run$0(Set $$0, List $$1, CachedOutput $$2, Advancement $$3) {
        if (!$$0.add((Object)$$3.getId())) {
            throw new IllegalStateException("Duplicate advancement " + $$3.getId());
        }
        Path $$4 = this.pathProvider.json($$3.getId());
        $$1.add(DataProvider.saveStable($$2, (JsonElement)$$3.deconstruct().serializeToJson(), $$4));
    }
}