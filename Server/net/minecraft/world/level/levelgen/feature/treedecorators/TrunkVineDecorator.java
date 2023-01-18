/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class TrunkVineDecorator
extends TreeDecorator {
    public static final Codec<TrunkVineDecorator> CODEC = Codec.unit(() -> INSTANCE);
    public static final TrunkVineDecorator INSTANCE = new TrunkVineDecorator();

    @Override
    protected TreeDecoratorType<?> type() {
        return TreeDecoratorType.TRUNK_VINE;
    }

    @Override
    public void place(TreeDecorator.Context $$0) {
        RandomSource $$1 = $$0.random();
        $$0.logs().forEach($$2 -> {
            Vec3i $$6;
            Vec3i $$5;
            Vec3i $$4;
            Vec3i $$3;
            if ($$1.nextInt(3) > 0 && $$0.isAir((BlockPos)($$3 = $$2.west()))) {
                $$0.placeVine((BlockPos)$$3, VineBlock.EAST);
            }
            if ($$1.nextInt(3) > 0 && $$0.isAir((BlockPos)($$4 = $$2.east()))) {
                $$0.placeVine((BlockPos)$$4, VineBlock.WEST);
            }
            if ($$1.nextInt(3) > 0 && $$0.isAir((BlockPos)($$5 = $$2.north()))) {
                $$0.placeVine((BlockPos)$$5, VineBlock.SOUTH);
            }
            if ($$1.nextInt(3) > 0 && $$0.isAir((BlockPos)($$6 = $$2.south()))) {
                $$0.placeVine((BlockPos)$$6, VineBlock.NORTH);
            }
        });
    }
}