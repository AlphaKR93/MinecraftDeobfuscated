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
import net.minecraft.data.advancements.packs.VanillaAdventureAdvancements;
import net.minecraft.data.advancements.packs.VanillaHusbandryAdvancements;
import net.minecraft.data.advancements.packs.VanillaNetherAdvancements;
import net.minecraft.data.advancements.packs.VanillaStoryAdvancements;
import net.minecraft.data.advancements.packs.VanillaTheEndAdvancements;

public class VanillaAdvancementProvider {
    public static AdvancementProvider create(PackOutput $$0, CompletableFuture<HolderLookup.Provider> $$1) {
        return new AdvancementProvider($$0, $$1, (List<AdvancementSubProvider>)List.of((Object)new VanillaTheEndAdvancements(), (Object)new VanillaHusbandryAdvancements(), (Object)new VanillaAdventureAdvancements(), (Object)new VanillaNetherAdvancements(), (Object)new VanillaStoryAdvancements()));
    }
}