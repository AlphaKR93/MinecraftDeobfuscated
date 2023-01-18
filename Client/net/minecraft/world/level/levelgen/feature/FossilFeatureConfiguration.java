/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.util.List
 */
package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

public class FossilFeatureConfiguration
implements FeatureConfiguration {
    public static final Codec<FossilFeatureConfiguration> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)ResourceLocation.CODEC.listOf().fieldOf("fossil_structures").forGetter($$0 -> $$0.fossilStructures), (App)ResourceLocation.CODEC.listOf().fieldOf("overlay_structures").forGetter($$0 -> $$0.overlayStructures), (App)StructureProcessorType.LIST_CODEC.fieldOf("fossil_processors").forGetter($$0 -> $$0.fossilProcessors), (App)StructureProcessorType.LIST_CODEC.fieldOf("overlay_processors").forGetter($$0 -> $$0.overlayProcessors), (App)Codec.intRange((int)0, (int)7).fieldOf("max_empty_corners_allowed").forGetter($$0 -> $$0.maxEmptyCornersAllowed)).apply((Applicative)$$02, FossilFeatureConfiguration::new));
    public final List<ResourceLocation> fossilStructures;
    public final List<ResourceLocation> overlayStructures;
    public final Holder<StructureProcessorList> fossilProcessors;
    public final Holder<StructureProcessorList> overlayProcessors;
    public final int maxEmptyCornersAllowed;

    public FossilFeatureConfiguration(List<ResourceLocation> $$0, List<ResourceLocation> $$1, Holder<StructureProcessorList> $$2, Holder<StructureProcessorList> $$3, int $$4) {
        if ($$0.isEmpty()) {
            throw new IllegalArgumentException("Fossil structure lists need at least one entry");
        }
        if ($$0.size() != $$1.size()) {
            throw new IllegalArgumentException("Fossil structure lists must be equal lengths");
        }
        this.fossilStructures = $$0;
        this.overlayStructures = $$1;
        this.fossilProcessors = $$2;
        this.overlayProcessors = $$3;
        this.maxEmptyCornersAllowed = $$4;
    }
}