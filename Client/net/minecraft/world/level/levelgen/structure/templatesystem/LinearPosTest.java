/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.codecs.RecordCodecBuilder
 *  java.lang.Float
 *  java.lang.IllegalArgumentException
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;

public class LinearPosTest
extends PosRuleTest {
    public static final Codec<LinearPosTest> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.FLOAT.fieldOf("min_chance").orElse((Object)Float.valueOf((float)0.0f)).forGetter($$0 -> Float.valueOf((float)$$0.minChance)), (App)Codec.FLOAT.fieldOf("max_chance").orElse((Object)Float.valueOf((float)0.0f)).forGetter($$0 -> Float.valueOf((float)$$0.maxChance)), (App)Codec.INT.fieldOf("min_dist").orElse((Object)0).forGetter($$0 -> $$0.minDist), (App)Codec.INT.fieldOf("max_dist").orElse((Object)0).forGetter($$0 -> $$0.maxDist)).apply((Applicative)$$02, LinearPosTest::new));
    private final float minChance;
    private final float maxChance;
    private final int minDist;
    private final int maxDist;

    public LinearPosTest(float $$0, float $$1, int $$2, int $$3) {
        if ($$2 >= $$3) {
            throw new IllegalArgumentException("Invalid range: [" + $$2 + "," + $$3 + "]");
        }
        this.minChance = $$0;
        this.maxChance = $$1;
        this.minDist = $$2;
        this.maxDist = $$3;
    }

    @Override
    public boolean test(BlockPos $$0, BlockPos $$1, BlockPos $$2, RandomSource $$3) {
        int $$4 = $$1.distManhattan($$2);
        float $$5 = $$3.nextFloat();
        return $$5 <= Mth.clampedLerp(this.minChance, this.maxChance, Mth.inverseLerp($$4, this.minDist, this.maxDist));
    }

    @Override
    protected PosRuleTestType<?> getType() {
        return PosRuleTestType.LINEAR_POS_TEST;
    }
}