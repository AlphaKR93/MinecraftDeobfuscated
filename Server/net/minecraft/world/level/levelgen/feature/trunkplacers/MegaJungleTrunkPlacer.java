/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.function.BiConsumer
 */
package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.GiantTrunkPlacer;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;

public class MegaJungleTrunkPlacer
extends GiantTrunkPlacer {
    public static final Codec<MegaJungleTrunkPlacer> CODEC = RecordCodecBuilder.create($$0 -> MegaJungleTrunkPlacer.trunkPlacerParts($$0).apply((Applicative)$$0, MegaJungleTrunkPlacer::new));

    public MegaJungleTrunkPlacer(int $$0, int $$1, int $$2) {
        super($$0, $$1, $$2);
    }

    @Override
    protected TrunkPlacerType<?> type() {
        return TrunkPlacerType.MEGA_JUNGLE_TRUNK_PLACER;
    }

    @Override
    public List<FoliagePlacer.FoliageAttachment> placeTrunk(LevelSimulatedReader $$0, BiConsumer<BlockPos, BlockState> $$1, RandomSource $$2, int $$3, BlockPos $$4, TreeConfiguration $$5) {
        ArrayList $$6 = Lists.newArrayList();
        $$6.addAll(super.placeTrunk($$0, $$1, $$2, $$3, $$4, $$5));
        for (int $$7 = $$3 - 2 - $$2.nextInt(4); $$7 > $$3 / 2; $$7 -= 2 + $$2.nextInt(4)) {
            float $$8 = $$2.nextFloat() * ((float)Math.PI * 2);
            int $$9 = 0;
            int $$10 = 0;
            for (int $$11 = 0; $$11 < 5; ++$$11) {
                $$9 = (int)(1.5f + Mth.cos($$8) * (float)$$11);
                $$10 = (int)(1.5f + Mth.sin($$8) * (float)$$11);
                BlockPos $$12 = $$4.offset($$9, $$7 - 3 + $$11 / 2, $$10);
                this.placeLog($$0, $$1, $$2, $$12, $$5);
            }
            $$6.add((Object)new FoliagePlacer.FoliageAttachment($$4.offset($$9, $$7, $$10), -2, false));
        }
        return $$6;
    }
}