/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.FunctionalInterface
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  org.apache.commons.lang3.mutable.MutableInt
 */
package net.minecraft.world.level.block.entity;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.mutable.MutableInt;

public class BellBlockEntity
extends BlockEntity {
    private static final int DURATION = 50;
    private static final int GLOW_DURATION = 60;
    private static final int MIN_TICKS_BETWEEN_SEARCHES = 60;
    private static final int MAX_RESONATION_TICKS = 40;
    private static final int TICKS_BEFORE_RESONATION = 5;
    private static final int SEARCH_RADIUS = 48;
    private static final int HEAR_BELL_RADIUS = 32;
    private static final int HIGHLIGHT_RAIDERS_RADIUS = 48;
    private long lastRingTimestamp;
    public int ticks;
    public boolean shaking;
    public Direction clickDirection;
    private List<LivingEntity> nearbyEntities;
    private boolean resonating;
    private int resonationTicks;

    public BellBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.BELL, $$0, $$1);
    }

    @Override
    public boolean triggerEvent(int $$0, int $$1) {
        if ($$0 == 1) {
            this.updateEntities();
            this.resonationTicks = 0;
            this.clickDirection = Direction.from3DDataValue($$1);
            this.ticks = 0;
            this.shaking = true;
            return true;
        }
        return super.triggerEvent($$0, $$1);
    }

    private static void tick(Level $$0, BlockPos $$1, BlockState $$2, BellBlockEntity $$3, ResonationEndAction $$4) {
        if ($$3.shaking) {
            ++$$3.ticks;
        }
        if ($$3.ticks >= 50) {
            $$3.shaking = false;
            $$3.ticks = 0;
        }
        if ($$3.ticks >= 5 && $$3.resonationTicks == 0 && BellBlockEntity.areRaidersNearby($$1, $$3.nearbyEntities)) {
            $$3.resonating = true;
            $$0.playSound(null, $$1, SoundEvents.BELL_RESONATE, SoundSource.BLOCKS, 1.0f, 1.0f);
        }
        if ($$3.resonating) {
            if ($$3.resonationTicks < 40) {
                ++$$3.resonationTicks;
            } else {
                $$4.run($$0, $$1, $$3.nearbyEntities);
                $$3.resonating = false;
            }
        }
    }

    public static void clientTick(Level $$0, BlockPos $$1, BlockState $$2, BellBlockEntity $$3) {
        BellBlockEntity.tick($$0, $$1, $$2, $$3, BellBlockEntity::showBellParticles);
    }

    public static void serverTick(Level $$0, BlockPos $$1, BlockState $$2, BellBlockEntity $$3) {
        BellBlockEntity.tick($$0, $$1, $$2, $$3, BellBlockEntity::makeRaidersGlow);
    }

    public void onHit(Direction $$0) {
        BlockPos $$1 = this.getBlockPos();
        this.clickDirection = $$0;
        if (this.shaking) {
            this.ticks = 0;
        } else {
            this.shaking = true;
        }
        this.level.blockEvent($$1, this.getBlockState().getBlock(), 1, $$0.get3DDataValue());
    }

    private void updateEntities() {
        BlockPos $$0 = this.getBlockPos();
        if (this.level.getGameTime() > this.lastRingTimestamp + 60L || this.nearbyEntities == null) {
            this.lastRingTimestamp = this.level.getGameTime();
            AABB $$1 = new AABB($$0).inflate(48.0);
            this.nearbyEntities = this.level.getEntitiesOfClass(LivingEntity.class, $$1);
        }
        if (!this.level.isClientSide) {
            for (LivingEntity $$2 : this.nearbyEntities) {
                if (!$$2.isAlive() || $$2.isRemoved() || !$$0.closerToCenterThan($$2.position(), 32.0)) continue;
                $$2.getBrain().setMemory(MemoryModuleType.HEARD_BELL_TIME, this.level.getGameTime());
            }
        }
    }

    private static boolean areRaidersNearby(BlockPos $$0, List<LivingEntity> $$1) {
        for (LivingEntity $$2 : $$1) {
            if (!$$2.isAlive() || $$2.isRemoved() || !$$0.closerToCenterThan($$2.position(), 32.0) || !$$2.getType().is(EntityTypeTags.RAIDERS)) continue;
            return true;
        }
        return false;
    }

    private static void makeRaidersGlow(Level $$0, BlockPos $$12, List<LivingEntity> $$2) {
        $$2.stream().filter($$1 -> BellBlockEntity.isRaiderWithinRange($$12, $$1)).forEach(BellBlockEntity::glow);
    }

    private static void showBellParticles(Level $$0, BlockPos $$12, List<LivingEntity> $$2) {
        MutableInt $$3 = new MutableInt(16700985);
        int $$42 = (int)$$2.stream().filter($$1 -> $$12.closerToCenterThan($$1.position(), 48.0)).count();
        $$2.stream().filter($$1 -> BellBlockEntity.isRaiderWithinRange($$12, $$1)).forEach($$4 -> {
            float $$5 = 1.0f;
            double $$6 = Math.sqrt((double)(($$4.getX() - (double)$$12.getX()) * ($$4.getX() - (double)$$12.getX()) + ($$4.getZ() - (double)$$12.getZ()) * ($$4.getZ() - (double)$$12.getZ())));
            double $$7 = (double)((float)$$12.getX() + 0.5f) + 1.0 / $$6 * ($$4.getX() - (double)$$12.getX());
            double $$8 = (double)((float)$$12.getZ() + 0.5f) + 1.0 / $$6 * ($$4.getZ() - (double)$$12.getZ());
            int $$9 = Mth.clamp(($$42 - 21) / -2, 3, 15);
            for (int $$10 = 0; $$10 < $$9; ++$$10) {
                int $$11 = $$3.addAndGet(5);
                double $$12 = (double)FastColor.ARGB32.red($$11) / 255.0;
                double $$13 = (double)FastColor.ARGB32.green($$11) / 255.0;
                double $$14 = (double)FastColor.ARGB32.blue($$11) / 255.0;
                $$0.addParticle(ParticleTypes.ENTITY_EFFECT, $$7, (float)$$12.getY() + 0.5f, $$8, $$12, $$13, $$14);
            }
        });
    }

    private static boolean isRaiderWithinRange(BlockPos $$0, LivingEntity $$1) {
        return $$1.isAlive() && !$$1.isRemoved() && $$0.closerToCenterThan($$1.position(), 48.0) && $$1.getType().is(EntityTypeTags.RAIDERS);
    }

    private static void glow(LivingEntity $$0) {
        $$0.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60));
    }

    @FunctionalInterface
    static interface ResonationEndAction {
        public void run(Level var1, BlockPos var2, List<LivingEntity> var3);
    }
}