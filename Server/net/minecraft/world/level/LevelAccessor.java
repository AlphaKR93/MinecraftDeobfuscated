/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.CommonLevelAccessor;
import net.minecraft.world.level.LevelTimeAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.redstone.NeighborUpdater;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.ScheduledTick;
import net.minecraft.world.ticks.TickPriority;

public interface LevelAccessor
extends CommonLevelAccessor,
LevelTimeAccess {
    @Override
    default public long dayTime() {
        return this.getLevelData().getDayTime();
    }

    public long nextSubTickCount();

    public LevelTickAccess<Block> getBlockTicks();

    private <T> ScheduledTick<T> createTick(BlockPos $$0, T $$1, int $$2, TickPriority $$3) {
        return new ScheduledTick<T>($$1, $$0, this.getLevelData().getGameTime() + (long)$$2, $$3, this.nextSubTickCount());
    }

    private <T> ScheduledTick<T> createTick(BlockPos $$0, T $$1, int $$2) {
        return new ScheduledTick<T>($$1, $$0, this.getLevelData().getGameTime() + (long)$$2, this.nextSubTickCount());
    }

    default public void scheduleTick(BlockPos $$0, Block $$1, int $$2, TickPriority $$3) {
        this.getBlockTicks().schedule(this.createTick($$0, $$1, $$2, $$3));
    }

    default public void scheduleTick(BlockPos $$0, Block $$1, int $$2) {
        this.getBlockTicks().schedule(this.createTick($$0, $$1, $$2));
    }

    public LevelTickAccess<Fluid> getFluidTicks();

    default public void scheduleTick(BlockPos $$0, Fluid $$1, int $$2, TickPriority $$3) {
        this.getFluidTicks().schedule(this.createTick($$0, $$1, $$2, $$3));
    }

    default public void scheduleTick(BlockPos $$0, Fluid $$1, int $$2) {
        this.getFluidTicks().schedule(this.createTick($$0, $$1, $$2));
    }

    public LevelData getLevelData();

    public DifficultyInstance getCurrentDifficultyAt(BlockPos var1);

    @Nullable
    public MinecraftServer getServer();

    default public Difficulty getDifficulty() {
        return this.getLevelData().getDifficulty();
    }

    public ChunkSource getChunkSource();

    @Override
    default public boolean hasChunk(int $$0, int $$1) {
        return this.getChunkSource().hasChunk($$0, $$1);
    }

    public RandomSource getRandom();

    default public void blockUpdated(BlockPos $$0, Block $$1) {
    }

    default public void neighborShapeChanged(Direction $$0, BlockState $$1, BlockPos $$2, BlockPos $$3, int $$4, int $$5) {
        NeighborUpdater.executeShapeUpdate(this, $$0, $$1, $$2, $$3, $$4, $$5 - 1);
    }

    default public void playSound(@Nullable Player $$0, BlockPos $$1, SoundEvent $$2, SoundSource $$3) {
        this.playSound($$0, $$1, $$2, $$3, 1.0f, 1.0f);
    }

    public void playSound(@Nullable Player var1, BlockPos var2, SoundEvent var3, SoundSource var4, float var5, float var6);

    public void addParticle(ParticleOptions var1, double var2, double var4, double var6, double var8, double var10, double var12);

    public void levelEvent(@Nullable Player var1, int var2, BlockPos var3, int var4);

    default public void levelEvent(int $$0, BlockPos $$1, int $$2) {
        this.levelEvent(null, $$0, $$1, $$2);
    }

    public void gameEvent(GameEvent var1, Vec3 var2, GameEvent.Context var3);

    default public void gameEvent(@Nullable Entity $$0, GameEvent $$1, Vec3 $$2) {
        this.gameEvent($$1, $$2, new GameEvent.Context($$0, null));
    }

    default public void gameEvent(@Nullable Entity $$0, GameEvent $$1, BlockPos $$2) {
        this.gameEvent($$1, $$2, new GameEvent.Context($$0, null));
    }

    default public void gameEvent(GameEvent $$0, BlockPos $$1, GameEvent.Context $$2) {
        this.gameEvent($$0, Vec3.atCenterOf($$1), $$2);
    }
}