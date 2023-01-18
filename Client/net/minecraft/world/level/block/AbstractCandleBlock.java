/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractCandleBlock
extends Block {
    public static final int LIGHT_PER_CANDLE = 3;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    protected AbstractCandleBlock(BlockBehaviour.Properties $$0) {
        super($$0);
    }

    protected abstract Iterable<Vec3> getParticleOffsets(BlockState var1);

    public static boolean isLit(BlockState $$0) {
        return $$0.hasProperty(LIT) && ($$0.is(BlockTags.CANDLES) || $$0.is(BlockTags.CANDLE_CAKES)) && $$0.getValue(LIT) != false;
    }

    @Override
    public void onProjectileHit(Level $$0, BlockState $$1, BlockHitResult $$2, Projectile $$3) {
        if (!$$0.isClientSide && $$3.isOnFire() && this.canBeLit($$1)) {
            AbstractCandleBlock.setLit($$0, $$1, $$2.getBlockPos(), true);
        }
    }

    protected boolean canBeLit(BlockState $$0) {
        return $$0.getValue(LIT) == false;
    }

    @Override
    public void animateTick(BlockState $$0, Level $$1, BlockPos $$2, RandomSource $$32) {
        if (!$$0.getValue(LIT).booleanValue()) {
            return;
        }
        this.getParticleOffsets($$0).forEach($$3 -> AbstractCandleBlock.addParticlesAndSound($$1, $$3.add($$2.getX(), $$2.getY(), $$2.getZ()), $$32));
    }

    private static void addParticlesAndSound(Level $$0, Vec3 $$1, RandomSource $$2) {
        float $$3 = $$2.nextFloat();
        if ($$3 < 0.3f) {
            $$0.addParticle(ParticleTypes.SMOKE, $$1.x, $$1.y, $$1.z, 0.0, 0.0, 0.0);
            if ($$3 < 0.17f) {
                $$0.playLocalSound($$1.x + 0.5, $$1.y + 0.5, $$1.z + 0.5, SoundEvents.CANDLE_AMBIENT, SoundSource.BLOCKS, 1.0f + $$2.nextFloat(), $$2.nextFloat() * 0.7f + 0.3f, false);
            }
        }
        $$0.addParticle(ParticleTypes.SMALL_FLAME, $$1.x, $$1.y, $$1.z, 0.0, 0.0, 0.0);
    }

    public static void extinguish(@Nullable Player $$0, BlockState $$1, LevelAccessor $$22, BlockPos $$3) {
        AbstractCandleBlock.setLit($$22, $$1, $$3, false);
        if ($$1.getBlock() instanceof AbstractCandleBlock) {
            ((AbstractCandleBlock)$$1.getBlock()).getParticleOffsets($$1).forEach($$2 -> $$22.addParticle(ParticleTypes.SMOKE, (double)$$3.getX() + $$2.x(), (double)$$3.getY() + $$2.y(), (double)$$3.getZ() + $$2.z(), 0.0, 0.1f, 0.0));
        }
        $$22.playSound(null, $$3, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1.0f, 1.0f);
        $$22.gameEvent((Entity)$$0, GameEvent.BLOCK_CHANGE, $$3);
    }

    private static void setLit(LevelAccessor $$0, BlockState $$1, BlockPos $$2, boolean $$3) {
        $$0.setBlock($$2, (BlockState)$$1.setValue(LIT, $$3), 11);
    }
}