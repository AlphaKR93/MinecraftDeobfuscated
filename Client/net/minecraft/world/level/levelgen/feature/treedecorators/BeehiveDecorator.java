/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  java.lang.Float
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Collections
 *  java.util.List
 *  java.util.Optional
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 */
package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;

public class BeehiveDecorator
extends TreeDecorator {
    public static final Codec<BeehiveDecorator> CODEC = Codec.floatRange((float)0.0f, (float)1.0f).fieldOf("probability").xmap(BeehiveDecorator::new, $$0 -> Float.valueOf((float)$$0.probability)).codec();
    private static final Direction WORLDGEN_FACING = Direction.SOUTH;
    private static final Direction[] SPAWN_DIRECTIONS = (Direction[])Direction.Plane.HORIZONTAL.stream().filter($$0 -> $$0 != WORLDGEN_FACING.getOpposite()).toArray(Direction[]::new);
    private final float probability;

    public BeehiveDecorator(float $$0) {
        this.probability = $$0;
    }

    @Override
    protected TreeDecoratorType<?> type() {
        return TreeDecoratorType.BEEHIVE;
    }

    @Override
    public void place(TreeDecorator.Context $$02) {
        RandomSource $$12 = $$02.random();
        if ($$12.nextFloat() >= this.probability) {
            return;
        }
        ObjectArrayList<BlockPos> $$2 = $$02.leaves();
        ObjectArrayList<BlockPos> $$3 = $$02.logs();
        int $$4 = !$$2.isEmpty() ? Math.max((int)(((BlockPos)$$2.get(0)).getY() - 1), (int)(((BlockPos)$$3.get(0)).getY() + 1)) : Math.min((int)(((BlockPos)$$3.get(0)).getY() + 1 + $$12.nextInt(3)), (int)((BlockPos)$$3.get($$3.size() - 1)).getY());
        List $$5 = (List)$$3.stream().filter($$1 -> $$1.getY() == $$4).flatMap($$0 -> Stream.of((Object[])SPAWN_DIRECTIONS).map($$0::relative)).collect(Collectors.toList());
        if ($$5.isEmpty()) {
            return;
        }
        Collections.shuffle((List)$$5);
        Optional $$6 = $$5.stream().filter($$1 -> $$02.isAir((BlockPos)$$1) && $$02.isAir((BlockPos)$$1.relative(WORLDGEN_FACING))).findFirst();
        if ($$6.isEmpty()) {
            return;
        }
        $$02.setBlock((BlockPos)$$6.get(), (BlockState)Blocks.BEE_NEST.defaultBlockState().setValue(BeehiveBlock.FACING, WORLDGEN_FACING));
        $$02.level().getBlockEntity((BlockPos)$$6.get(), BlockEntityType.BEEHIVE).ifPresent($$1 -> {
            int $$2 = 2 + $$12.nextInt(2);
            for (int $$3 = 0; $$3 < $$2; ++$$3) {
                CompoundTag $$4 = new CompoundTag();
                $$4.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(EntityType.BEE).toString());
                $$1.storeBee($$4, $$12.nextInt(599), false);
            }
        });
    }
}