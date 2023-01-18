/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Map
 *  java.util.Optional
 *  java.util.function.Function
 *  java.util.function.ToDoubleFunction
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.phys.Vec3;

public class RamTarget
extends Behavior<Goat> {
    public static final int TIME_OUT_DURATION = 200;
    public static final float RAM_SPEED_FORCE_FACTOR = 1.65f;
    private final Function<Goat, UniformInt> getTimeBetweenRams;
    private final TargetingConditions ramTargeting;
    private final float speed;
    private final ToDoubleFunction<Goat> getKnockbackForce;
    private Vec3 ramDirection;
    private final Function<Goat, SoundEvent> getImpactSound;
    private final Function<Goat, SoundEvent> getHornBreakSound;

    public RamTarget(Function<Goat, UniformInt> $$0, TargetingConditions $$1, float $$2, ToDoubleFunction<Goat> $$3, Function<Goat, SoundEvent> $$4, Function<Goat, SoundEvent> $$5) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.RAM_COOLDOWN_TICKS, (Object)((Object)MemoryStatus.VALUE_ABSENT), MemoryModuleType.RAM_TARGET, (Object)((Object)MemoryStatus.VALUE_PRESENT)), 200);
        this.getTimeBetweenRams = $$0;
        this.ramTargeting = $$1;
        this.speed = $$2;
        this.getKnockbackForce = $$3;
        this.getImpactSound = $$4;
        this.getHornBreakSound = $$5;
        this.ramDirection = Vec3.ZERO;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel $$0, Goat $$1) {
        return $$1.getBrain().hasMemoryValue(MemoryModuleType.RAM_TARGET);
    }

    @Override
    protected boolean canStillUse(ServerLevel $$0, Goat $$1, long $$2) {
        return $$1.getBrain().hasMemoryValue(MemoryModuleType.RAM_TARGET);
    }

    @Override
    protected void start(ServerLevel $$0, Goat $$1, long $$2) {
        BlockPos $$3 = $$1.blockPosition();
        Brain<Goat> $$4 = $$1.getBrain();
        Vec3 $$5 = (Vec3)$$4.getMemory(MemoryModuleType.RAM_TARGET).get();
        this.ramDirection = new Vec3((double)$$3.getX() - $$5.x(), 0.0, (double)$$3.getZ() - $$5.z()).normalize();
        $$4.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget($$5, this.speed, 0));
    }

    @Override
    protected void tick(ServerLevel $$0, Goat $$1, long $$2) {
        List $$3 = $$0.getNearbyEntities(LivingEntity.class, this.ramTargeting, $$1, $$1.getBoundingBox());
        Brain<Goat> $$4 = $$1.getBrain();
        if (!$$3.isEmpty()) {
            LivingEntity $$5 = (LivingEntity)$$3.get(0);
            $$5.hurt(DamageSource.mobAttack($$1).setNoAggro(), (float)$$1.getAttributeValue(Attributes.ATTACK_DAMAGE));
            int $$6 = $$1.hasEffect(MobEffects.MOVEMENT_SPEED) ? $$1.getEffect(MobEffects.MOVEMENT_SPEED).getAmplifier() + 1 : 0;
            int $$7 = $$1.hasEffect(MobEffects.MOVEMENT_SLOWDOWN) ? $$1.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() + 1 : 0;
            float $$8 = 0.25f * (float)($$6 - $$7);
            float $$9 = Mth.clamp($$1.getSpeed() * 1.65f, 0.2f, 3.0f) + $$8;
            float $$10 = $$5.isDamageSourceBlocked(DamageSource.mobAttack($$1)) ? 0.5f : 1.0f;
            $$5.knockback((double)($$10 * $$9) * this.getKnockbackForce.applyAsDouble((Object)$$1), this.ramDirection.x(), this.ramDirection.z());
            this.finishRam($$0, $$1);
            $$0.playSound(null, $$1, (SoundEvent)this.getImpactSound.apply((Object)$$1), SoundSource.NEUTRAL, 1.0f, 1.0f);
        } else if (this.hasRammedHornBreakingBlock($$0, $$1)) {
            $$0.playSound(null, $$1, (SoundEvent)this.getImpactSound.apply((Object)$$1), SoundSource.NEUTRAL, 1.0f, 1.0f);
            boolean $$11 = $$1.dropHorn();
            if ($$11) {
                $$0.playSound(null, $$1, (SoundEvent)this.getHornBreakSound.apply((Object)$$1), SoundSource.NEUTRAL, 1.0f, 1.0f);
            }
            this.finishRam($$0, $$1);
        } else {
            boolean $$14;
            Optional<WalkTarget> $$12 = $$4.getMemory(MemoryModuleType.WALK_TARGET);
            Optional<Vec3> $$13 = $$4.getMemory(MemoryModuleType.RAM_TARGET);
            boolean bl = $$14 = $$12.isEmpty() || $$13.isEmpty() || ((WalkTarget)$$12.get()).getTarget().currentPosition().closerThan((Position)$$13.get(), 0.25);
            if ($$14) {
                this.finishRam($$0, $$1);
            }
        }
    }

    private boolean hasRammedHornBreakingBlock(ServerLevel $$0, Goat $$1) {
        Vec3 $$2 = $$1.getDeltaMovement().multiply(1.0, 0.0, 1.0).normalize();
        BlockPos $$3 = new BlockPos($$1.position().add($$2));
        return $$0.getBlockState($$3).is(BlockTags.SNAPS_GOAT_HORN) || $$0.getBlockState((BlockPos)$$3.above()).is(BlockTags.SNAPS_GOAT_HORN);
    }

    protected void finishRam(ServerLevel $$0, Goat $$1) {
        $$0.broadcastEntityEvent($$1, (byte)59);
        $$1.getBrain().setMemory(MemoryModuleType.RAM_COOLDOWN_TICKS, ((UniformInt)this.getTimeBetweenRams.apply((Object)$$1)).sample($$0.random));
        $$1.getBrain().eraseMemory(MemoryModuleType.RAM_TARGET);
    }
}