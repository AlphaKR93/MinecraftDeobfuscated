/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  java.lang.Boolean
 *  java.lang.Enum
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 */
package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PipeBlock
extends Block {
    private static final Direction[] DIRECTIONS = Direction.values();
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty UP = BlockStateProperties.UP;
    public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = ImmutableMap.copyOf((Map)((Map)Util.make(Maps.newEnumMap(Direction.class), $$0 -> {
        $$0.put((Enum)Direction.NORTH, (Object)NORTH);
        $$0.put((Enum)Direction.EAST, (Object)EAST);
        $$0.put((Enum)Direction.SOUTH, (Object)SOUTH);
        $$0.put((Enum)Direction.WEST, (Object)WEST);
        $$0.put((Enum)Direction.UP, (Object)UP);
        $$0.put((Enum)Direction.DOWN, (Object)DOWN);
    })));
    protected final VoxelShape[] shapeByIndex;

    protected PipeBlock(float $$0, BlockBehaviour.Properties $$1) {
        super($$1);
        this.shapeByIndex = this.makeShapes($$0);
    }

    private VoxelShape[] makeShapes(float $$0) {
        float $$1 = 0.5f - $$0;
        float $$2 = 0.5f + $$0;
        VoxelShape $$3 = Block.box($$1 * 16.0f, $$1 * 16.0f, $$1 * 16.0f, $$2 * 16.0f, $$2 * 16.0f, $$2 * 16.0f);
        VoxelShape[] $$4 = new VoxelShape[DIRECTIONS.length];
        for (int $$5 = 0; $$5 < DIRECTIONS.length; ++$$5) {
            Direction $$6 = DIRECTIONS[$$5];
            $$4[$$5] = Shapes.box(0.5 + Math.min((double)(-$$0), (double)((double)$$6.getStepX() * 0.5)), 0.5 + Math.min((double)(-$$0), (double)((double)$$6.getStepY() * 0.5)), 0.5 + Math.min((double)(-$$0), (double)((double)$$6.getStepZ() * 0.5)), 0.5 + Math.max((double)$$0, (double)((double)$$6.getStepX() * 0.5)), 0.5 + Math.max((double)$$0, (double)((double)$$6.getStepY() * 0.5)), 0.5 + Math.max((double)$$0, (double)((double)$$6.getStepZ() * 0.5)));
        }
        VoxelShape[] $$7 = new VoxelShape[64];
        for (int $$8 = 0; $$8 < 64; ++$$8) {
            VoxelShape $$9 = $$3;
            for (int $$10 = 0; $$10 < DIRECTIONS.length; ++$$10) {
                if (($$8 & 1 << $$10) == 0) continue;
                $$9 = Shapes.or($$9, $$4[$$10]);
            }
            $$7[$$8] = $$9;
        }
        return $$7;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState $$0, BlockGetter $$1, BlockPos $$2) {
        return false;
    }

    @Override
    public VoxelShape getShape(BlockState $$0, BlockGetter $$1, BlockPos $$2, CollisionContext $$3) {
        return this.shapeByIndex[this.getAABBIndex($$0)];
    }

    protected int getAABBIndex(BlockState $$0) {
        int $$1 = 0;
        for (int $$2 = 0; $$2 < DIRECTIONS.length; ++$$2) {
            if (!((Boolean)$$0.getValue((Property)PROPERTY_BY_DIRECTION.get((Object)DIRECTIONS[$$2]))).booleanValue()) continue;
            $$1 |= 1 << $$2;
        }
        return $$1;
    }
}