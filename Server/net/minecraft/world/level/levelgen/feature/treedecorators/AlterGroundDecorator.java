/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.serialization.Codec
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.ArrayList
 */
package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class AlterGroundDecorator
extends TreeDecorator {
    public static final Codec<AlterGroundDecorator> CODEC = BlockStateProvider.CODEC.fieldOf("provider").xmap(AlterGroundDecorator::new, $$0 -> $$0.provider).codec();
    private final BlockStateProvider provider;

    public AlterGroundDecorator(BlockStateProvider $$0) {
        this.provider = $$0;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return TreeDecoratorType.ALTER_GROUND;
    }

    @Override
    public void place(TreeDecorator.Context $$0) {
        ArrayList $$12 = Lists.newArrayList();
        ObjectArrayList<BlockPos> $$2 = $$0.roots();
        ObjectArrayList<BlockPos> $$3 = $$0.logs();
        if ($$2.isEmpty()) {
            $$12.addAll($$3);
        } else if (!$$3.isEmpty() && ((BlockPos)$$2.get(0)).getY() == ((BlockPos)$$3.get(0)).getY()) {
            $$12.addAll($$3);
            $$12.addAll($$2);
        } else {
            $$12.addAll($$2);
        }
        if ($$12.isEmpty()) {
            return;
        }
        int $$4 = ((BlockPos)$$12.get(0)).getY();
        $$12.stream().filter($$1 -> $$1.getY() == $$4).forEach($$1 -> {
            this.placeCircle($$0, (BlockPos)((BlockPos)$$1.west()).north());
            this.placeCircle($$0, (BlockPos)((BlockPos)$$1.east(2)).north());
            this.placeCircle($$0, (BlockPos)((BlockPos)$$1.west()).south(2));
            this.placeCircle($$0, (BlockPos)((BlockPos)$$1.east(2)).south(2));
            for (int $$2 = 0; $$2 < 5; ++$$2) {
                int $$3 = $$0.random().nextInt(64);
                int $$4 = $$3 % 8;
                int $$5 = $$3 / 8;
                if ($$4 != 0 && $$4 != 7 && $$5 != 0 && $$5 != 7) continue;
                this.placeCircle($$0, $$1.offset(-3 + $$4, 0, -3 + $$5));
            }
        });
    }

    private void placeCircle(TreeDecorator.Context $$0, BlockPos $$1) {
        for (int $$2 = -2; $$2 <= 2; ++$$2) {
            for (int $$3 = -2; $$3 <= 2; ++$$3) {
                if (Math.abs((int)$$2) == 2 && Math.abs((int)$$3) == 2) continue;
                this.placeBlockAt($$0, $$1.offset($$2, 0, $$3));
            }
        }
    }

    private void placeBlockAt(TreeDecorator.Context $$0, BlockPos $$1) {
        for (int $$2 = 2; $$2 >= -3; --$$2) {
            Vec3i $$3 = $$1.above($$2);
            if (Feature.isGrassOrDirt($$0.level(), (BlockPos)$$3)) {
                $$0.setBlock((BlockPos)$$3, this.provider.getState($$0.random(), $$1));
                break;
            }
            if (!$$0.isAir((BlockPos)$$3) && $$2 < 0) break;
        }
    }
}