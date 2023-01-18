/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class NoteBlock
extends Block {
    public static final EnumProperty<NoteBlockInstrument> INSTRUMENT = BlockStateProperties.NOTEBLOCK_INSTRUMENT;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final IntegerProperty NOTE = BlockStateProperties.NOTE;
    public static final int NOTE_VOLUME = 3;

    public NoteBlock(BlockBehaviour.Properties $$0) {
        super($$0);
        this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(INSTRUMENT, NoteBlockInstrument.HARP)).setValue(NOTE, 0)).setValue(POWERED, false));
    }

    private static boolean isFeatureFlagEnabled(LevelAccessor $$0) {
        return $$0.enabledFeatures().contains(FeatureFlags.UPDATE_1_20);
    }

    private BlockState setInstrument(LevelAccessor $$0, BlockPos $$1, BlockState $$2) {
        if (NoteBlock.isFeatureFlagEnabled($$0)) {
            BlockState $$3 = $$0.getBlockState((BlockPos)$$1.above());
            return (BlockState)$$2.setValue(INSTRUMENT, (NoteBlockInstrument)NoteBlockInstrument.byStateAbove($$3).orElseGet(() -> NoteBlockInstrument.byStateBelow($$0.getBlockState((BlockPos)$$1.below()))));
        }
        return (BlockState)$$2.setValue(INSTRUMENT, NoteBlockInstrument.byStateBelow($$0.getBlockState((BlockPos)$$1.below())));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext $$0) {
        return this.setInstrument($$0.getLevel(), $$0.getClickedPos(), this.defaultBlockState());
    }

    @Override
    public BlockState updateShape(BlockState $$0, Direction $$1, BlockState $$2, LevelAccessor $$3, BlockPos $$4, BlockPos $$5) {
        boolean $$6;
        boolean bl = NoteBlock.isFeatureFlagEnabled($$3) ? $$1.getAxis() == Direction.Axis.Y : ($$6 = $$1 == Direction.DOWN);
        if ($$6) {
            return this.setInstrument($$3, $$4, $$0);
        }
        return super.updateShape($$0, $$1, $$2, $$3, $$4, $$5);
    }

    @Override
    public void neighborChanged(BlockState $$0, Level $$1, BlockPos $$2, Block $$3, BlockPos $$4, boolean $$5) {
        boolean $$6 = $$1.hasNeighborSignal($$2);
        if ($$6 != $$0.getValue(POWERED)) {
            if ($$6) {
                this.playNote(null, $$0, $$1, $$2);
            }
            $$1.setBlock($$2, (BlockState)$$0.setValue(POWERED, $$6), 3);
        }
    }

    private void playNote(@Nullable Entity $$0, BlockState $$1, Level $$2, BlockPos $$3) {
        if (!$$1.getValue(INSTRUMENT).requiresAirAbove() || $$2.getBlockState((BlockPos)$$3.above()).isAir()) {
            $$2.blockEvent($$3, this, 0, 0);
            $$2.gameEvent($$0, GameEvent.NOTE_BLOCK_PLAY, $$3);
        }
    }

    @Override
    public InteractionResult use(BlockState $$0, Level $$1, BlockPos $$2, Player $$3, InteractionHand $$4, BlockHitResult $$5) {
        if ($$1.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        $$0 = (BlockState)$$0.cycle(NOTE);
        $$1.setBlock($$2, $$0, 3);
        this.playNote($$3, $$0, $$1, $$2);
        $$3.awardStat(Stats.TUNE_NOTEBLOCK);
        return InteractionResult.CONSUME;
    }

    @Override
    public void attack(BlockState $$0, Level $$1, BlockPos $$2, Player $$3) {
        if ($$1.isClientSide) {
            return;
        }
        this.playNote($$3, $$0, $$1, $$2);
        $$3.awardStat(Stats.PLAY_NOTEBLOCK);
    }

    @Override
    public boolean triggerEvent(BlockState $$0, Level $$1, BlockPos $$2, int $$3, int $$4) {
        Holder<SoundEvent> $$11;
        float $$8;
        NoteBlockInstrument $$5 = $$0.getValue(INSTRUMENT);
        if ($$5.isTunable()) {
            int $$6 = $$0.getValue(NOTE);
            float $$7 = (float)Math.pow((double)2.0, (double)((double)($$6 - 12) / 12.0));
            $$1.addParticle(ParticleTypes.NOTE, (double)$$2.getX() + 0.5, (double)$$2.getY() + 1.2, (double)$$2.getZ() + 0.5, (double)$$6 / 24.0, 0.0, 0.0);
        } else {
            $$8 = 1.0f;
        }
        if ($$5.hasCustomSound()) {
            ResourceLocation $$9 = this.getCustomSoundId($$1, $$2);
            if ($$9 == null) {
                return false;
            }
            Holder<SoundEvent> $$10 = Holder.direct(SoundEvent.createVariableRangeEvent($$9));
        } else {
            $$11 = $$5.getSoundEvent();
        }
        $$1.playSeededSound(null, (double)$$2.getX() + 0.5, (double)$$2.getY() + 0.5, (double)$$2.getZ() + 0.5, $$11, SoundSource.RECORDS, 3.0f, $$8, $$1.random.nextLong());
        return true;
    }

    @Nullable
    private ResourceLocation getCustomSoundId(Level $$0, BlockPos $$1) {
        BlockEntity blockEntity = $$0.getBlockEntity((BlockPos)$$1.above());
        if (blockEntity instanceof SkullBlockEntity) {
            SkullBlockEntity $$2 = (SkullBlockEntity)blockEntity;
            return $$2.getNoteBlockSound();
        }
        return null;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> $$0) {
        $$0.add(INSTRUMENT, POWERED, NOTE);
    }
}