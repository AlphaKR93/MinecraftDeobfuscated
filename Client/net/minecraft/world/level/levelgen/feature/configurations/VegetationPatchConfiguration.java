/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Float
 *  java.lang.Object
 */
package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class VegetationPatchConfiguration
implements FeatureConfiguration {
    public static final Codec<VegetationPatchConfiguration> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)TagKey.hashedCodec(Registries.BLOCK).fieldOf("replaceable").forGetter($$0 -> $$0.replaceable), (App)BlockStateProvider.CODEC.fieldOf("ground_state").forGetter($$0 -> $$0.groundState), (App)PlacedFeature.CODEC.fieldOf("vegetation_feature").forGetter($$0 -> $$0.vegetationFeature), (App)CaveSurface.CODEC.fieldOf("surface").forGetter($$0 -> $$0.surface), (App)IntProvider.codec(1, 128).fieldOf("depth").forGetter($$0 -> $$0.depth), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("extra_bottom_block_chance").forGetter($$0 -> Float.valueOf((float)$$0.extraBottomBlockChance)), (App)Codec.intRange((int)1, (int)256).fieldOf("vertical_range").forGetter($$0 -> $$0.verticalRange), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("vegetation_chance").forGetter($$0 -> Float.valueOf((float)$$0.vegetationChance)), (App)IntProvider.CODEC.fieldOf("xz_radius").forGetter($$0 -> $$0.xzRadius), (App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("extra_edge_column_chance").forGetter($$0 -> Float.valueOf((float)$$0.extraEdgeColumnChance))).apply((Applicative)$$02, VegetationPatchConfiguration::new));
    public final TagKey<Block> replaceable;
    public final BlockStateProvider groundState;
    public final Holder<PlacedFeature> vegetationFeature;
    public final CaveSurface surface;
    public final IntProvider depth;
    public final float extraBottomBlockChance;
    public final int verticalRange;
    public final float vegetationChance;
    public final IntProvider xzRadius;
    public final float extraEdgeColumnChance;

    public VegetationPatchConfiguration(TagKey<Block> $$0, BlockStateProvider $$1, Holder<PlacedFeature> $$2, CaveSurface $$3, IntProvider $$4, float $$5, int $$6, float $$7, IntProvider $$8, float $$9) {
        this.replaceable = $$0;
        this.groundState = $$1;
        this.vegetationFeature = $$2;
        this.surface = $$3;
        this.depth = $$4;
        this.extraBottomBlockChance = $$5;
        this.verticalRange = $$6;
        this.vegetationChance = $$7;
        this.xzRadius = $$8;
        this.extraEdgeColumnChance = $$9;
    }
}