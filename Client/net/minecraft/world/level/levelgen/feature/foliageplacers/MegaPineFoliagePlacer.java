/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.BiConsumer
 */
package net.minecraft.world.level.levelgen.feature.foliageplacers;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;

public class MegaPineFoliagePlacer
extends FoliagePlacer {
    public static final Codec<MegaPineFoliagePlacer> CODEC = RecordCodecBuilder.create($$02 -> MegaPineFoliagePlacer.foliagePlacerParts($$02).and((App)IntProvider.codec(0, 24).fieldOf("crown_height").forGetter($$0 -> $$0.crownHeight)).apply((Applicative)$$02, MegaPineFoliagePlacer::new));
    private final IntProvider crownHeight;

    public MegaPineFoliagePlacer(IntProvider $$0, IntProvider $$1, IntProvider $$2) {
        super($$0, $$1);
        this.crownHeight = $$2;
    }

    @Override
    protected FoliagePlacerType<?> type() {
        return FoliagePlacerType.MEGA_PINE_FOLIAGE_PLACER;
    }

    @Override
    protected void createFoliage(LevelSimulatedReader $$0, BiConsumer<BlockPos, BlockState> $$1, RandomSource $$2, TreeConfiguration $$3, int $$4, FoliagePlacer.FoliageAttachment $$5, int $$6, int $$7, int $$8) {
        BlockPos $$9 = $$5.pos();
        int $$10 = 0;
        for (int $$11 = $$9.getY() - $$6 + $$8; $$11 <= $$9.getY() + $$8; ++$$11) {
            int $$15;
            int $$12 = $$9.getY() - $$11;
            int $$13 = $$7 + $$5.radiusOffset() + Mth.floor((float)$$12 / (float)$$6 * 3.5f);
            if ($$12 > 0 && $$13 == $$10 && ($$11 & 1) == 0) {
                int $$14 = $$13 + 1;
            } else {
                $$15 = $$13;
            }
            this.placeLeavesRow($$0, $$1, $$2, $$3, new BlockPos($$9.getX(), $$11, $$9.getZ()), $$15, 0, $$5.doubleTrunk());
            $$10 = $$13;
        }
    }

    @Override
    public int foliageHeight(RandomSource $$0, int $$1, TreeConfiguration $$2) {
        return this.crownHeight.sample($$0);
    }

    @Override
    protected boolean shouldSkipLocation(RandomSource $$0, int $$1, int $$2, int $$3, int $$4, boolean $$5) {
        if ($$1 + $$3 >= 7) {
            return true;
        }
        return $$1 * $$1 + $$3 * $$3 > $$4 * $$4;
    }
}