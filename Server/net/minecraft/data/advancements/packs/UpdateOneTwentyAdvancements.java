/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Arrays
 *  java.util.List
 *  java.util.function.Consumer
 *  java.util.stream.Collectors
 */
package net.minecraft.data.advancements.packs;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.advancements.packs.VanillaHusbandryAdvancements;
import net.minecraft.world.entity.EntityType;

public class UpdateOneTwentyAdvancements
extends VanillaHusbandryAdvancements {
    @Override
    public void generate(HolderLookup.Provider $$0, Consumer<Advancement> $$1) {
        Advancement $$2 = this.createRoot($$1);
        Advancement $$3 = this.createBreedAnAnimalAdvancement($$2, $$1);
        this.createBreedAllAnimalsAdvancement($$3, $$1);
    }

    @Override
    public EntityType<?>[] getBreedableAnimals() {
        Object[] $$0 = super.getBreedableAnimals();
        List $$1 = (List)Arrays.stream((Object[])$$0).collect(Collectors.toList());
        $$1.add(EntityType.CAMEL);
        return (EntityType[])$$1.toArray($$0);
    }
}