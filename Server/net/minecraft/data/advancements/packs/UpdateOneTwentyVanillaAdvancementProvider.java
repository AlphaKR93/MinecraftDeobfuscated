/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.List
 *  java.util.concurrent.CompletableFuture
 */
package net.minecraft.data.advancements.packs;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.data.advancements.packs.UpdateOneTwentyHusbandryAdvancements;

public class UpdateOneTwentyVanillaAdvancementProvider {
    public static AdvancementProvider create(PackOutput $$0, CompletableFuture<HolderLookup.Provider> $$1) {
        return new AdvancementProvider($$0, $$1, (List<AdvancementSubProvider>)List.of((Object)new UpdateOneTwentyHusbandryAdvancements()));
    }
}