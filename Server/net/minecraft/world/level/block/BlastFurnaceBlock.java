/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class BlastFurnaceBlock
extends AbstractFurnaceBlock {
    protected BlastFurnaceBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos $$0, BlockState $$1) {
        return new BlastFurnaceBlockEntity($$0, $$1);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level $$0, BlockState $$1, BlockEntityType<T> $$2) {
        return BlastFurnaceBlock.createFurnaceTicker($$0, $$2, BlockEntityType.BLAST_FURNACE);
    }

    @Override
    protected void openContainer(Level $$0, BlockPos $$1, Player $$2) {
        BlockEntity $$3 = $$0.getBlockEntity($$1);
        if ($$3 instanceof BlastFurnaceBlockEntity) {
            $$2.openMenu((MenuProvider)((Object)$$3));
            $$2.awardStat(Stats.INTERACT_WITH_BLAST_FURNACE);
        }
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$3) {
        if (!$$0.getValue(LIT).booleanValue()) {
            return;
        }
        double $$4 = (double)$$2.getX() + 0.5;
        double $$5 = $$2.getY();
        double $$6 = (double)$$2.getZ() + 0.5;
        if ($$3.nextDouble() < 0.1) {
            $$1.playLocalSound($$4, $$5, $$6, SoundEvents.BLASTFURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0f, 1.0f, false);
        }
        Direction $$7 = $$0.getValue(FACING);
        Direction.Axis $$8 = $$7.getAxis();
        double $$9 = 0.52;
        double $$10 = $$3.nextDouble() * 0.6 - 0.3;
        double $$11 = $$8 == Direction.Axis.X ? (double)$$7.getStepX() * 0.52 : $$10;
        double $$12 = $$3.nextDouble() * 9.0 / 16.0;
        double $$13 = $$8 == Direction.Axis.Z ? (double)$$7.getStepZ() * 0.52 : $$10;
        $$1.addParticle(ParticleTypes.SMOKE, $$4 + $$11, $$5 + $$12, $$6 + $$13, 0.0, 0.0, 0.0);
    }
}