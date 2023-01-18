/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class WeightedPressurePlateBlock
extends BasePressurePlateBlock {
    public static final IntegerProperty POWER = BlockStateProperties.POWER;
    private final int maxWeight;
    private final SoundEvent soundOff;
    private final SoundEvent soundOn;

    protected WeightedPressurePlateBlock(int $$0, BlockBehaviour.Properties $$1, SoundEvent $$2, SoundEvent $$3) {
        super($$1);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(POWER, 0));
        this.maxWeight = $$0;
        this.soundOff = $$2;
        this.soundOn = $$3;
    }

    @Override
    protected int getSignalStrength(Level $$0, BlockPos $$1) {
        int $$2 = Math.min((int)$$0.getEntitiesOfClass(Entity.class, TOUCH_AABB.move($$1)).size(), (int)this.maxWeight);
        if ($$2 > 0) {
            float $$3 = (float)Math.min((int)this.maxWeight, (int)$$2) / (float)this.maxWeight;
            return Mth.ceil($$3 * 15.0f);
        }
        return 0;
    }

    @Override
    protected void playOnSound(LevelAccessor $$0, BlockPos $$1) {
        $$0.playSound(null, $$1, this.soundOn, SoundSource.BLOCKS);
    }

    @Override
    protected void playOffSound(LevelAccessor $$0, BlockPos $$1) {
        $$0.playSound(null, $$1, this.soundOff, SoundSource.BLOCKS);
    }

    @Override
    protected int getSignalForState(BlockState $$0) {
        return $$0.getValue(POWER);
    }

    @Override
    protected BlockState setSignalForState(BlockState $$0, int $$1) {
        return (BlockState)$$0.setValue(POWER, $$1);
    }

    @Override
    protected int getPressedTime() {
        return 10;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(POWER);
    }
}