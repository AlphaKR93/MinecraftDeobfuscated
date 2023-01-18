/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 *  java.util.function.Predicate
 */
package net.minecraft.world.level.block;

import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class PowderSnowCauldronBlock
extends LayeredCauldronBlock {
    public PowderSnowCauldronBlock(BlockBehaviour.Properties $$0, Predicate<Biome.Precipitation> $$1, Map<Item, CauldronInteraction> $$2) {
        super($$0, $$1, $$2);
    }

    @Override
    protected void handleEntityOnFireInside(BlockState $$0, Level $$1, BlockPos $$2) {
        PowderSnowCauldronBlock.lowerFillLevel((BlockState)Blocks.WATER_CAULDRON.defaultBlockState().setValue(LEVEL, $$0.getValue(LEVEL)), $$1, $$2);
    }
}