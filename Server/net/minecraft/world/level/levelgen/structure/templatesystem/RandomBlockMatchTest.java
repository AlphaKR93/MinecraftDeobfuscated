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
 *  java.lang.Override
 */
package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;

public class RandomBlockMatchTest
extends RuleTest {
    public static final Codec<RandomBlockMatchTest> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter($$0 -> $$0.block), (App)Codec.FLOAT.fieldOf("probability").forGetter($$0 -> Float.valueOf((float)$$0.probability))).apply((Applicative)$$02, RandomBlockMatchTest::new));
    private final Block block;
    private final float probability;

    public RandomBlockMatchTest(Block $$0, float $$1) {
        this.block = $$0;
        this.probability = $$1;
    }

    @Override
    public boolean test(BlockState $$0, RandomSource $$1) {
        return $$0.is(this.block) && $$1.nextFloat() < this.probability;
    }

    @Override
    protected RuleTestType<?> getType() {
        return RuleTestType.RANDOM_BLOCK_TEST;
    }
}