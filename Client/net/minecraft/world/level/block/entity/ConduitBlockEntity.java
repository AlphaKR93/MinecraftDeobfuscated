/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.UUID
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.block.entity;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ConduitBlockEntity
extends BlockEntity {
    private static final int BLOCK_REFRESH_RATE = 2;
    private static final int EFFECT_DURATION = 13;
    private static final float ROTATION_SPEED = -0.0375f;
    private static final int MIN_ACTIVE_SIZE = 16;
    private static final int MIN_KILL_SIZE = 42;
    private static final int KILL_RANGE = 8;
    private static final Block[] VALID_BLOCKS = new Block[]{Blocks.PRISMARINE, Blocks.PRISMARINE_BRICKS, Blocks.SEA_LANTERN, Blocks.DARK_PRISMARINE};
    public int tickCount;
    private float activeRotation;
    private boolean isActive;
    private boolean isHunting;
    private final List<BlockPos> effectBlocks = Lists.newArrayList();
    @Nullable
    private LivingEntity destroyTarget;
    @Nullable
    private UUID destroyTargetUUID;
    private long nextAmbientSoundActivation;

    public ConduitBlockEntity(BlockPos $$0, BlockState $$1) {
        super(BlockEntityType.CONDUIT, $$0, $$1);
    }

    @Override
    public void load(CompoundTag $$0) {
        super.load($$0);
        this.destroyTargetUUID = $$0.hasUUID("Target") ? $$0.getUUID("Target") : null;
    }

    @Override
    protected void saveAdditional(CompoundTag $$0) {
        super.saveAdditional($$0);
        if (this.destroyTarget != null) {
            $$0.putUUID("Target", this.destroyTarget.getUUID());
        }
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public static void clientTick(Level $$0, BlockPos $$1, BlockState $$2, ConduitBlockEntity $$3) {
        ++$$3.tickCount;
        long $$4 = $$0.getGameTime();
        List<BlockPos> $$5 = $$3.effectBlocks;
        if ($$4 % 40L == 0L) {
            $$3.isActive = ConduitBlockEntity.updateShape($$0, $$1, $$5);
            ConduitBlockEntity.updateHunting($$3, $$5);
        }
        ConduitBlockEntity.updateClientTarget($$0, $$1, $$3);
        ConduitBlockEntity.animationTick($$0, $$1, $$5, $$3.destroyTarget, $$3.tickCount);
        if ($$3.isActive()) {
            $$3.activeRotation += 1.0f;
        }
    }

    public static void serverTick(Level $$0, BlockPos $$1, BlockState $$2, ConduitBlockEntity $$3) {
        ++$$3.tickCount;
        long $$4 = $$0.getGameTime();
        List<BlockPos> $$5 = $$3.effectBlocks;
        if ($$4 % 40L == 0L) {
            boolean $$6 = ConduitBlockEntity.updateShape($$0, $$1, $$5);
            if ($$6 != $$3.isActive) {
                SoundEvent $$7 = $$6 ? SoundEvents.CONDUIT_ACTIVATE : SoundEvents.CONDUIT_DEACTIVATE;
                $$0.playSound(null, $$1, $$7, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
            $$3.isActive = $$6;
            ConduitBlockEntity.updateHunting($$3, $$5);
            if ($$6) {
                ConduitBlockEntity.applyEffects($$0, $$1, $$5);
                ConduitBlockEntity.updateDestroyTarget($$0, $$1, $$2, $$5, $$3);
            }
        }
        if ($$3.isActive()) {
            if ($$4 % 80L == 0L) {
                $$0.playSound(null, $$1, SoundEvents.CONDUIT_AMBIENT, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
            if ($$4 > $$3.nextAmbientSoundActivation) {
                $$3.nextAmbientSoundActivation = $$4 + 60L + (long)$$0.getRandom().nextInt(40);
                $$0.playSound(null, $$1, SoundEvents.CONDUIT_AMBIENT_SHORT, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
        }
    }

    private static void updateHunting(ConduitBlockEntity $$0, List<BlockPos> $$1) {
        $$0.setHunting($$1.size() >= 42);
    }

    private static boolean updateShape(Level $$0, BlockPos $$1, List<BlockPos> $$2) {
        $$2.clear();
        for (int $$3 = -1; $$3 <= 1; ++$$3) {
            for (int $$4 = -1; $$4 <= 1; ++$$4) {
                for (int $$5 = -1; $$5 <= 1; ++$$5) {
                    BlockPos $$6 = $$1.offset($$3, $$4, $$5);
                    if ($$0.isWaterAt($$6)) continue;
                    return false;
                }
            }
        }
        for (int $$7 = -2; $$7 <= 2; ++$$7) {
            for (int $$8 = -2; $$8 <= 2; ++$$8) {
                for (int $$9 = -2; $$9 <= 2; ++$$9) {
                    int $$10 = Math.abs((int)$$7);
                    int $$11 = Math.abs((int)$$8);
                    int $$12 = Math.abs((int)$$9);
                    if ($$10 <= 1 && $$11 <= 1 && $$12 <= 1 || ($$7 != 0 || $$11 != 2 && $$12 != 2) && ($$8 != 0 || $$10 != 2 && $$12 != 2) && ($$9 != 0 || $$10 != 2 && $$11 != 2)) continue;
                    BlockPos $$13 = $$1.offset($$7, $$8, $$9);
                    BlockState $$14 = $$0.getBlockState($$13);
                    for (Block $$15 : VALID_BLOCKS) {
                        if (!$$14.is($$15)) continue;
                        $$2.add((Object)$$13);
                    }
                }
            }
        }
        return $$2.size() >= 16;
    }

    private static void applyEffects(Level $$0, BlockPos $$1, List<BlockPos> $$2) {
        int $$7;
        int $$6;
        int $$3 = $$2.size();
        int $$4 = $$3 / 7 * 16;
        int $$5 = $$1.getX();
        AABB $$8 = new AABB($$5, $$6 = $$1.getY(), $$7 = $$1.getZ(), $$5 + 1, $$6 + 1, $$7 + 1).inflate($$4).expandTowards(0.0, $$0.getHeight(), 0.0);
        List $$9 = $$0.getEntitiesOfClass(Player.class, $$8);
        if ($$9.isEmpty()) {
            return;
        }
        for (Player $$10 : $$9) {
            if (!$$1.closerThan($$10.blockPosition(), $$4) || !$$10.isInWaterOrRain()) continue;
            $$10.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 260, 0, true, true));
        }
    }

    private static void updateDestroyTarget(Level $$02, BlockPos $$1, BlockState $$2, List<BlockPos> $$3, ConduitBlockEntity $$4) {
        LivingEntity $$5 = $$4.destroyTarget;
        int $$6 = $$3.size();
        if ($$6 < 42) {
            $$4.destroyTarget = null;
        } else if ($$4.destroyTarget == null && $$4.destroyTargetUUID != null) {
            $$4.destroyTarget = ConduitBlockEntity.findDestroyTarget($$02, $$1, $$4.destroyTargetUUID);
            $$4.destroyTargetUUID = null;
        } else if ($$4.destroyTarget == null) {
            List $$7 = $$02.getEntitiesOfClass(LivingEntity.class, ConduitBlockEntity.getDestroyRangeAABB($$1), $$0 -> $$0 instanceof Enemy && $$0.isInWaterOrRain());
            if (!$$7.isEmpty()) {
                $$4.destroyTarget = (LivingEntity)$$7.get($$02.random.nextInt($$7.size()));
            }
        } else if (!$$4.destroyTarget.isAlive() || !$$1.closerThan($$4.destroyTarget.blockPosition(), 8.0)) {
            $$4.destroyTarget = null;
        }
        if ($$4.destroyTarget != null) {
            $$02.playSound(null, $$4.destroyTarget.getX(), $$4.destroyTarget.getY(), $$4.destroyTarget.getZ(), SoundEvents.CONDUIT_ATTACK_TARGET, SoundSource.BLOCKS, 1.0f, 1.0f);
            $$4.destroyTarget.hurt(DamageSource.MAGIC, 4.0f);
        }
        if ($$5 != $$4.destroyTarget) {
            $$02.sendBlockUpdated($$1, $$2, $$2, 2);
        }
    }

    private static void updateClientTarget(Level $$0, BlockPos $$1, ConduitBlockEntity $$2) {
        if ($$2.destroyTargetUUID == null) {
            $$2.destroyTarget = null;
        } else if ($$2.destroyTarget == null || !$$2.destroyTarget.getUUID().equals((Object)$$2.destroyTargetUUID)) {
            $$2.destroyTarget = ConduitBlockEntity.findDestroyTarget($$0, $$1, $$2.destroyTargetUUID);
            if ($$2.destroyTarget == null) {
                $$2.destroyTargetUUID = null;
            }
        }
    }

    private static AABB getDestroyRangeAABB(BlockPos $$0) {
        int $$1 = $$0.getX();
        int $$2 = $$0.getY();
        int $$3 = $$0.getZ();
        return new AABB($$1, $$2, $$3, $$1 + 1, $$2 + 1, $$3 + 1).inflate(8.0);
    }

    @Nullable
    private static LivingEntity findDestroyTarget(Level $$0, BlockPos $$12, UUID $$2) {
        List $$3 = $$0.getEntitiesOfClass(LivingEntity.class, ConduitBlockEntity.getDestroyRangeAABB($$12), $$1 -> $$1.getUUID().equals((Object)$$2));
        if ($$3.size() == 1) {
            return (LivingEntity)$$3.get(0);
        }
        return null;
    }

    private static void animationTick(Level $$0, BlockPos $$1, List<BlockPos> $$2, @Nullable Entity $$3, int $$4) {
        RandomSource $$5 = $$0.random;
        double $$6 = Mth.sin((float)($$4 + 35) * 0.1f) / 2.0f + 0.5f;
        $$6 = ($$6 * $$6 + $$6) * (double)0.3f;
        Vec3 $$7 = new Vec3((double)$$1.getX() + 0.5, (double)$$1.getY() + 1.5 + $$6, (double)$$1.getZ() + 0.5);
        for (BlockPos $$8 : $$2) {
            if ($$5.nextInt(50) != 0) continue;
            Vec3i $$9 = $$8.subtract($$1);
            float $$10 = -0.5f + $$5.nextFloat() + (float)$$9.getX();
            float $$11 = -2.0f + $$5.nextFloat() + (float)$$9.getY();
            float $$12 = -0.5f + $$5.nextFloat() + (float)$$9.getZ();
            $$0.addParticle(ParticleTypes.NAUTILUS, $$7.x, $$7.y, $$7.z, $$10, $$11, $$12);
        }
        if ($$3 != null) {
            Vec3 $$13 = new Vec3($$3.getX(), $$3.getEyeY(), $$3.getZ());
            float $$14 = (-0.5f + $$5.nextFloat()) * (3.0f + $$3.getBbWidth());
            float $$15 = -1.0f + $$5.nextFloat() * $$3.getBbHeight();
            float $$16 = (-0.5f + $$5.nextFloat()) * (3.0f + $$3.getBbWidth());
            Vec3 $$17 = new Vec3($$14, $$15, $$16);
            $$0.addParticle(ParticleTypes.NAUTILUS, $$13.x, $$13.y, $$13.z, $$17.x, $$17.y, $$17.z);
        }
    }

    public boolean isActive() {
        return this.isActive;
    }

    public boolean isHunting() {
        return this.isHunting;
    }

    private void setHunting(boolean $$0) {
        this.isHunting = $$0;
    }

    public float getActiveRotation(float $$0) {
        return (this.activeRotation + $$0) * -0.0375f;
    }
}