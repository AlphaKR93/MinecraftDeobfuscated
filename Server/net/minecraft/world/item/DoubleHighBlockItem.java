/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class DoubleHighBlockItem
extends BlockItem {
    public DoubleHighBlockItem(Block $$0, Item.Properties $$1) {
        super($$0, $$1);
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext $$0, BlockState $$1) {
        Vec3i $$3;
        Level $$2 = $$0.getLevel();
        BlockState $$4 = $$2.isWaterAt((BlockPos)($$3 = $$0.getClickedPos().above())) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
        $$2.setBlock((BlockPos)$$3, $$4, 27);
        return super.placeBlock($$0, $$1);
    }
}