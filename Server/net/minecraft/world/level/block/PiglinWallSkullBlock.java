/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 */
package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.WallSkullBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PiglinWallSkullBlock
extends WallSkullBlock {
    private static final Map<Direction, VoxelShape> AABBS = Maps.immutableEnumMap((Map)Map.of((Object)Direction.NORTH, (Object)Block.box(3.0, 4.0, 8.0, 13.0, 12.0, 16.0), (Object)Direction.SOUTH, (Object)Block.box(3.0, 4.0, 0.0, 13.0, 12.0, 8.0), (Object)Direction.EAST, (Object)Block.box(0.0, 4.0, 3.0, 8.0, 12.0, 13.0), (Object)Direction.WEST, (Object)Block.box(8.0, 4.0, 3.0, 16.0, 12.0, 13.0)));

    public PiglinWallSkullBlock(BlockBehaviour.Properties $$0) {
        super(SkullBlock.Types.PIGLIN, $$0);
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return (VoxelShape)AABBS.get((Object)$$0.getValue(FACING));
    }
}