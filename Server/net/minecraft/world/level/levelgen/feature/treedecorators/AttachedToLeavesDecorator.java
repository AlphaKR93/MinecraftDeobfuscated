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
 *  java.util.HashSet
 *  java.util.List
 */
package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class AttachedToLeavesDecorator
extends TreeDecorator {
    public static final Codec<AttachedToLeavesDecorator> CODEC = RecordCodecBuilder.create($$02 -> $$02.group((App)Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("probability").forGetter($$0 -> Float.valueOf((float)$$0.probability)), (App)Codec.intRange((int)0, (int)16).fieldOf("exclusion_radius_xz").forGetter($$0 -> $$0.exclusionRadiusXZ), (App)Codec.intRange((int)0, (int)16).fieldOf("exclusion_radius_y").forGetter($$0 -> $$0.exclusionRadiusY), (App)BlockStateProvider.CODEC.fieldOf("block_provider").forGetter($$0 -> $$0.blockProvider), (App)Codec.intRange((int)1, (int)16).fieldOf("required_empty_blocks").forGetter($$0 -> $$0.requiredEmptyBlocks), (App)ExtraCodecs.nonEmptyList(Direction.CODEC.listOf()).fieldOf("directions").forGetter($$0 -> $$0.directions)).apply((Applicative)$$02, AttachedToLeavesDecorator::new));
    protected final float probability;
    protected final int exclusionRadiusXZ;
    protected final int exclusionRadiusY;
    protected final BlockStateProvider blockProvider;
    protected final int requiredEmptyBlocks;
    protected final List<Direction> directions;

    public AttachedToLeavesDecorator(float $$0, int $$1, int $$2, BlockStateProvider $$3, int $$4, List<Direction> $$5) {
        this.probability = $$0;
        this.exclusionRadiusXZ = $$1;
        this.exclusionRadiusY = $$2;
        this.blockProvider = $$3;
        this.requiredEmptyBlocks = $$4;
        this.directions = $$5;
    }

    @Override
    public void place(TreeDecorator.Context $$0) {
        HashSet $$1 = new HashSet();
        RandomSource $$2 = $$0.random();
        for (BlockPos $$3 : Util.shuffledCopy($$0.leaves(), $$2)) {
            Direction $$4;
            Vec3i $$5 = $$3.relative($$4 = Util.getRandom(this.directions, $$2));
            if ($$1.contains((Object)$$5) || !($$2.nextFloat() < this.probability) || !this.hasRequiredEmptyBlocks($$0, $$3, $$4)) continue;
            BlockPos $$6 = ((BlockPos)$$5).offset(-this.exclusionRadiusXZ, -this.exclusionRadiusY, -this.exclusionRadiusXZ);
            BlockPos $$7 = ((BlockPos)$$5).offset(this.exclusionRadiusXZ, this.exclusionRadiusY, this.exclusionRadiusXZ);
            for (BlockPos $$8 : BlockPos.betweenClosed($$6, $$7)) {
                $$1.add((Object)$$8.immutable());
            }
            $$0.setBlock((BlockPos)$$5, this.blockProvider.getState($$2, (BlockPos)$$5));
        }
    }

    private boolean hasRequiredEmptyBlocks(TreeDecorator.Context $$0, BlockPos $$1, Direction $$2) {
        for (int $$3 = 1; $$3 <= this.requiredEmptyBlocks; ++$$3) {
            BlockPos $$4 = $$1.relative($$2, $$3);
            if ($$0.isAir($$4)) continue;
            return false;
        }
        return true;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return TreeDecoratorType.ATTACHED_TO_LEAVES;
    }
}