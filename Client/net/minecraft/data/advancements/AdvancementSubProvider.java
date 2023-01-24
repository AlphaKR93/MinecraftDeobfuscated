/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.function.Consumer
 */
package net.minecraft.data.advancements;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

public interface AdvancementSubProvider {
    public static Advancement createPlaceholder(String $$0) {
        return Advancement.Builder.advancement().build(new ResourceLocation($$0));
    }

    public void generate(HolderLookup.Provider var1, Consumer<Advancement> var2);
}