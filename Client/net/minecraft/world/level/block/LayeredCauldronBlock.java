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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CauldronBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class LayeredCauldronBlock
extends AbstractCauldronBlock {
    public static final int MIN_FILL_LEVEL = 1;
    public static final int MAX_FILL_LEVEL = 3;
    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_CAULDRON;
    private static final int BASE_CONTENT_HEIGHT = 6;
    private static final double HEIGHT_PER_LEVEL = 3.0;
    public static final Predicate<Biome.Precipitation> RAIN = $$0 -> $$0 == Biome.Precipitation.RAIN;
    public static final Predicate<Biome.Precipitation> SNOW = $$0 -> $$0 == Biome.Precipitation.SNOW;
    private final Predicate<Biome.Precipitation> fillPredicate;

    public LayeredCauldronBlock(BlockBehaviour.Properties $$0, Predicate<Biome.Precipitation> $$1, Map<Item, CauldronInteraction> $$2) {
        super($$0, $$2);
        this.fillPredicate = $$1;
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LEVEL, 1));
    }

    @Override
    public boolean isFull(BlockState $$0) {
        return $$0.getValue(LEVEL) == 3;
    }

    @Override
    protected boolean canReceiveStalactiteDrip(Fluid $$0) {
        return $$0 == Fluids.WATER && this.fillPredicate == RAIN;
    }

    @Override
    protected double getContentHeight(BlockState $$0) {
        return (6.0 + (double)$$0.getValue(LEVEL).intValue() * 3.0) / 16.0;
    }

    @Override
    public void entityInside(BlockState $$0, Level $$1, BlockPos $$2, Entity $$3) {
        if (!$$1.isClientSide && $$3.isOnFire() && this.isEntityInsideContent($$0, $$2, $$3)) {
            $$3.clearFire();
            if ($$3.mayInteract($$1, $$2)) {
                this.handleEntityOnFireInside($$0, $$1, $$2);
            }
        }
    }

    protected void handleEntityOnFireInside(BlockState $$0, Level $$1, BlockPos $$2) {
        LayeredCauldronBlock.lowerFillLevel($$0, $$1, $$2);
    }

    public static void lowerFillLevel(BlockState $$0, Level $$1, BlockPos $$2) {
        int $$3 = $$0.getValue(LEVEL) - 1;
        BlockState $$4 = $$3 == 0 ? Blocks.CAULDRON.defaultBlockState() : (BlockState)$$0.setValue(LEVEL, $$3);
        $$1.setBlockAndUpdate($$2, $$4);
        $$1.gameEvent(GameEvent.BLOCK_CHANGE, $$2, GameEvent.Context.of($$4));
    }

    @Override
    public void handlePrecipitation(BlockState $$0, Level $$1, BlockPos $$2, Biome.Precipitation $$3) {
        if (!CauldronBlock.shouldHandlePrecipitation($$1, $$3) || $$0.getValue(LEVEL) == 3 || !this.fillPredicate.test((Object)$$3)) {
            return;
        }
        BlockState $$4 = (BlockState)$$0.cycle(LEVEL);
        $$1.setBlockAndUpdate($$2, $$4);
        $$1.gameEvent(GameEvent.BLOCK_CHANGE, $$2, GameEvent.Context.of($$4));
    }

    @Override
    public int getAnalogOutputSignal(BlockState $$0, Level $$1, BlockPos $$2) {
        return $$0.getValue(LEVEL);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(LEVEL);
    }

    @Override
    protected void receiveStalactiteDrip(BlockState $$0, Level $$1, BlockPos $$2, Fluid $$3) {
        if (this.isFull($$0)) {
            return;
        }
        BlockState $$4 = (BlockState)$$0.setValue(LEVEL, $$0.getValue(LEVEL) + 1);
        $$1.setBlockAndUpdate($$2, $$4);
        $$1.gameEvent(GameEvent.BLOCK_CHANGE, $$2, GameEvent.Context.of($$4));
        $$1.levelEvent(1047, $$2, 0);
    }
}