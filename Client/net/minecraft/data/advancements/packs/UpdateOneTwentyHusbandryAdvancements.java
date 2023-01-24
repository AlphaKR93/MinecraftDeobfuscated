/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Consumer
 *  java.util.stream.Stream
 */
package net.minecraft.data.advancements.packs;

import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.data.advancements.packs.VanillaHusbandryAdvancements;
import net.minecraft.world.entity.EntityType;

public class UpdateOneTwentyHusbandryAdvancements
implements AdvancementSubProvider {
    @Override
    public void generate(HolderLookup.Provider $$0, Consumer<Advancement> $$1) {
        Advancement $$2 = AdvancementSubProvider.createPlaceholder("husbandry/breed_an_animal");
        Stream $$3 = Stream.concat((Stream)VanillaHusbandryAdvancements.BREEDABLE_ANIMALS.stream(), (Stream)Stream.of(EntityType.CAMEL));
        VanillaHusbandryAdvancements.createBreedAllAnimalsAdvancement($$2, $$1, $$3, VanillaHusbandryAdvancements.INDIRECTLY_BREEDABLE_ANIMALS.stream());
    }
}