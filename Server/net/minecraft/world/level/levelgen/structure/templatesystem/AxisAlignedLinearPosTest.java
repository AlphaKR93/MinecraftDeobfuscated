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
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;

public class AxisAlignedLinearPosTest
extends PosRuleTest {
    public static final Codec<AxisAlignedLinearPosTest> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.FLOAT.fieldOf("min_chance").orElse((Object)Float.valueOf((float)0.0f)).forGetter($$0 -> Float.valueOf((float)$$0.minChance)), (App)Codec.FLOAT.fieldOf("max_chance").orElse((Object)Float.valueOf((float)0.0f)).forGetter($$0 -> Float.valueOf((float)$$0.maxChance)), (App)Codec.INT.fieldOf("min_dist").orElse((Object)0).forGetter($$0 -> $$0.minDist), (App)Codec.INT.fieldOf("max_dist").orElse((Object)0).forGetter($$0 -> $$0.maxDist), (App)Direction.Axis.CODEC.fieldOf("axis").orElse((Object)Direction.Axis.Y).forGetter($$0 -> $$0.axis)).apply((Applicative)$$02, AxisAlignedLinearPosTest::new));
    private final float minChance;
    private final float maxChance;
    private final int minDist;
    private final int maxDist;
    private final Direction.Axis axis;

    public AxisAlignedLinearPosTest(float $$0, float $$1, int $$2, int $$3, Direction.Axis $$4) {
        if ($$2 >= $$3) {
            throw new IllegalArgumentException("Invalid range: [" + $$2 + "," + $$3 + "]");
        }
        this.minChance = $$0;
        this.maxChance = $$1;
        this.minDist = $$2;
        this.maxDist = $$3;
        this.axis = $$4;
    }

    @Override
    public boolean test(BlockPos $$0, BlockPos $$1, BlockPos $$2, RandomSource $$3) {
        Direction $$4 = Direction.get(Direction.AxisDirection.POSITIVE, this.axis);
        float $$5 = Math.abs((int)(($$1.getX() - $$2.getX()) * $$4.getStepX()));
        float $$6 = Math.abs((int)(($$1.getY() - $$2.getY()) * $$4.getStepY()));
        float $$7 = Math.abs((int)(($$1.getZ() - $$2.getZ()) * $$4.getStepZ()));
        int $$8 = (int)($$5 + $$6 + $$7);
        float $$9 = $$3.nextFloat();
        return $$9 <= Mth.clampedLerp(this.minChance, this.maxChance, Mth.inverseLerp($$8, this.minDist, this.maxDist));
    }

    @Override
    protected PosRuleTestType<?> getType() {
        return PosRuleTestType.AXIS_ALIGNED_LINEAR_POS_TEST;
    }
}