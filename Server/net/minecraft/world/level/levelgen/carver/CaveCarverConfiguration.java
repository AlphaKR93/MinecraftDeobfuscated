/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 */
package net.minecraft.world.level.levelgen.carver;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarverDebugSettings;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;

public class CaveCarverConfiguration
extends CarverConfiguration {
    public static final Codec<CaveCarverConfiguration> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)CarverConfiguration.CODEC.forGetter($$0 -> $$0), (App)FloatProvider.CODEC.fieldOf("horizontal_radius_multiplier").forGetter($$0 -> $$0.horizontalRadiusMultiplier), (App)FloatProvider.CODEC.fieldOf("vertical_radius_multiplier").forGetter($$0 -> $$0.verticalRadiusMultiplier), (App)FloatProvider.codec(-1.0f, 1.0f).fieldOf("floor_level").forGetter($$0 -> $$0.floorLevel)).apply((Applicative)$$02, CaveCarverConfiguration::new));
    public final FloatProvider horizontalRadiusMultiplier;
    public final FloatProvider verticalRadiusMultiplier;
    final FloatProvider floorLevel;

    public CaveCarverConfiguration(float $$0, HeightProvider $$1, FloatProvider $$2, VerticalAnchor $$3, CarverDebugSettings $$4, HolderSet<Block> $$5, FloatProvider $$6, FloatProvider $$7, FloatProvider $$8) {
        super($$0, $$1, $$2, $$3, $$4, $$5);
        this.horizontalRadiusMultiplier = $$6;
        this.verticalRadiusMultiplier = $$7;
        this.floorLevel = $$8;
    }

    public CaveCarverConfiguration(float $$0, HeightProvider $$1, FloatProvider $$2, VerticalAnchor $$3, HolderSet<Block> $$4, FloatProvider $$5, FloatProvider $$6, FloatProvider $$7) {
        this($$0, $$1, $$2, $$3, CarverDebugSettings.DEFAULT, $$4, $$5, $$6, $$7);
    }

    public CaveCarverConfiguration(CarverConfiguration $$0, FloatProvider $$1, FloatProvider $$2, FloatProvider $$3) {
        this($$0.probability, $$0.y, $$0.yScale, $$0.lavaLevel, $$0.debugSettings, $$0.replaceable, $$1, $$2, $$3);
    }
}