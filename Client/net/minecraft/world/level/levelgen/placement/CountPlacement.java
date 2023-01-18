/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.placement.RepeatingPlacement;

public class CountPlacement
extends RepeatingPlacement {
    public static final Codec<CountPlacement> CODEC = IntProvider.codec(0, 256).fieldOf("count").xmap(CountPlacement::new, $$0 -> $$0.count).codec();
    private final IntProvider count;

    private CountPlacement(IntProvider $$0) {
        this.count = $$0;
    }

    public static CountPlacement of(IntProvider $$0) {
        return new CountPlacement($$0);
    }

    public static CountPlacement of(int $$0) {
        return CountPlacement.of(ConstantInt.of($$0));
    }

    @Override
    protected int count(RandomSource $$0, BlockPos $$1) {
        return this.count.sample($$0);
    }

    @Override
    public PlacementModifierType<?> type() {
        return PlacementModifierType.COUNT;
    }
}