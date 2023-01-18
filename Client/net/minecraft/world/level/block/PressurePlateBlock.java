/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 */
package net.minecraft.world.level.block;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;

public class PressurePlateBlock
extends BasePressurePlateBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    private final Sensitivity sensitivity;
    private final SoundEvent soundOff;
    private final SoundEvent soundOn;

    protected PressurePlateBlock(Sensitivity $$0, BlockBehaviour.Properties $$1, SoundEvent $$2, SoundEvent $$3) {
        super($$1);
        this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(POWERED, false));
        this.sensitivity = $$0;
        this.soundOff = $$2;
        this.soundOn = $$3;
    }

    @Override
    protected int getSignalForState(BlockState $$0) {
        return $$0.getValue(POWERED) != false ? 15 : 0;
    }

    @Override
    protected BlockState setSignalForState(BlockState $$0, int $$1) {
        return (BlockState)$$0.setValue(POWERED, $$1 > 0);
    }

    @Override
    protected void playOnSound(LevelAccessor $$0, BlockPos $$1) {
        $$0.playSound(null, $$1, this.soundOn, SoundSource.BLOCKS);
    }

    @Override
    protected void playOffSound(LevelAccessor $$0, BlockPos $$1) {
        $$0.playSound(null, $$1, this.soundOff, SoundSource.BLOCKS);
    }

    /*
     * WARNING - void declaration
     */
    @Override
    protected int getSignalStrength(Level $$0, BlockPos $$1) {
        void $$5;
        AABB $$2 = TOUCH_AABB.move($$1);
        switch (this.sensitivity) {
            case EVERYTHING: {
                List $$3 = $$0.getEntities(null, $$2);
                break;
            }
            case MOBS: {
                List $$4 = $$0.getEntitiesOfClass(LivingEntity.class, $$2);
                break;
            }
            default: {
                return 0;
            }
        }
        if (!$$5.isEmpty()) {
            for (Entity $$6 : $$5) {
                if ($$6.isIgnoringBlockTriggers()) continue;
                return 15;
            }
        }
        return 0;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(POWERED);
    }

    public static enum Sensitivity {
        EVERYTHING,
        MOBS;

    }
}