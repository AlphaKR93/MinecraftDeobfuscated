/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 */
package net.minecraft.world.entity.ai.behavior.warden;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.phys.Vec3;

public class SonicBoom
extends Behavior<Warden> {
    private static final int DISTANCE_XZ = 15;
    private static final int DISTANCE_Y = 20;
    private static final double KNOCKBACK_VERTICAL = 0.5;
    private static final double KNOCKBACK_HORIZONTAL = 2.5;
    public static final int COOLDOWN = 40;
    private static final int TICKS_BEFORE_PLAYING_SOUND = Mth.ceil(34.0);
    private static final int DURATION = Mth.ceil(60.0f);

    public SonicBoom() {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, (Object)((Object)MemoryStatus.VALUE_PRESENT), MemoryModuleType.SONIC_BOOM_COOLDOWN, (Object)((Object)MemoryStatus.VALUE_ABSENT), MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN, (Object)((Object)MemoryStatus.REGISTERED), MemoryModuleType.SONIC_BOOM_SOUND_DELAY, (Object)((Object)MemoryStatus.REGISTERED)), DURATION);
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel $$0, Warden $$1) {
        return $$1.closerThan((Entity)$$1.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).get(), 15.0, 20.0);
    }

    @Override
    protected boolean canStillUse(ServerLevel $$0, Warden $$1, long $$2) {
        return true;
    }

    @Override
    protected void stop(ServerLevel $$0, Warden $$1, long $$2) {
        $$1.getBrain().setMemoryWithExpiry(MemoryModuleType.ATTACK_COOLING_DOWN, true, DURATION);
        $$1.getBrain().setMemoryWithExpiry(MemoryModuleType.SONIC_BOOM_SOUND_DELAY, Unit.INSTANCE, TICKS_BEFORE_PLAYING_SOUND);
        $$0.broadcastEntityEvent($$1, (byte)62);
        $$1.playSound(SoundEvents.WARDEN_SONIC_CHARGE, 3.0f, 1.0f);
    }

    @Override
    protected void tick(ServerLevel $$0, Warden $$12, long $$22) {
        $$12.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).ifPresent($$1 -> $$12.getLookControl().setLookAt($$1.position()));
        if ($$12.getBrain().hasMemoryValue(MemoryModuleType.SONIC_BOOM_SOUND_DELAY) || $$12.getBrain().hasMemoryValue(MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN)) {
            return;
        }
        $$12.getBrain().setMemoryWithExpiry(MemoryModuleType.SONIC_BOOM_SOUND_COOLDOWN, Unit.INSTANCE, DURATION - TICKS_BEFORE_PLAYING_SOUND);
        $$12.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET).filter($$12::canTargetEntity).filter($$1 -> $$12.closerThan((Entity)$$1, 15.0, 20.0)).ifPresent($$2 -> {
            Vec3 $$3 = $$12.position().add(0.0, 1.6f, 0.0);
            Vec3 $$4 = $$2.getEyePosition().subtract($$3);
            Vec3 $$5 = $$4.normalize();
            for (int $$6 = 1; $$6 < Mth.floor($$4.length()) + 7; ++$$6) {
                Vec3 $$7 = $$3.add($$5.scale($$6));
                $$0.sendParticles(ParticleTypes.SONIC_BOOM, $$7.x, $$7.y, $$7.z, 1, 0.0, 0.0, 0.0, 0.0);
            }
            $$12.playSound(SoundEvents.WARDEN_SONIC_BOOM, 3.0f, 1.0f);
            $$2.hurt(DamageSource.sonicBoom($$12), 10.0f);
            double $$8 = 0.5 * (1.0 - $$2.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            double $$9 = 2.5 * (1.0 - $$2.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            $$2.push($$5.x() * $$9, $$5.y() * $$8, $$5.z() * $$9);
        });
    }

    @Override
    protected void stop(ServerLevel $$0, Warden $$1, long $$2) {
        SonicBoom.setCooldown($$1, 40);
    }

    public static void setCooldown(LivingEntity $$0, int $$1) {
        $$0.getBrain().setMemoryWithExpiry(MemoryModuleType.SONIC_BOOM_COOLDOWN, Unit.INSTANCE, $$1);
    }
}