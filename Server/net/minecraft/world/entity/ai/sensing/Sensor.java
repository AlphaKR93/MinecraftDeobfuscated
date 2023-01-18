/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.util.Set
 */
package net.minecraft.world.entity.ai.sensing;

import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

public abstract class Sensor<E extends LivingEntity> {
    private static final RandomSource RANDOM = RandomSource.createThreadSafe();
    private static final int DEFAULT_SCAN_RATE = 20;
    protected static final int TARGETING_RANGE = 16;
    private static final TargetingConditions TARGET_CONDITIONS = TargetingConditions.forNonCombat().range(16.0);
    private static final TargetingConditions TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING = TargetingConditions.forNonCombat().range(16.0).ignoreInvisibilityTesting();
    private static final TargetingConditions ATTACK_TARGET_CONDITIONS = TargetingConditions.forCombat().range(16.0);
    private static final TargetingConditions ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING = TargetingConditions.forCombat().range(16.0).ignoreInvisibilityTesting();
    private static final TargetingConditions ATTACK_TARGET_CONDITIONS_IGNORE_LINE_OF_SIGHT = TargetingConditions.forCombat().range(16.0).ignoreLineOfSight();
    private static final TargetingConditions ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_AND_LINE_OF_SIGHT = TargetingConditions.forCombat().range(16.0).ignoreLineOfSight().ignoreInvisibilityTesting();
    private final int scanRate;
    private long timeToTick;

    public Sensor(int $$0) {
        this.scanRate = $$0;
        this.timeToTick = RANDOM.nextInt($$0);
    }

    public Sensor() {
        this(20);
    }

    public final void tick(ServerLevel $$0, E $$1) {
        if (--this.timeToTick <= 0L) {
            this.timeToTick = this.scanRate;
            this.doTick($$0, $$1);
        }
    }

    protected abstract void doTick(ServerLevel var1, E var2);

    public abstract Set<MemoryModuleType<?>> requires();

    public static boolean isEntityTargetable(LivingEntity $$0, LivingEntity $$1) {
        if ($$0.getBrain().isMemoryValue(MemoryModuleType.ATTACK_TARGET, $$1)) {
            return TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING.test($$0, $$1);
        }
        return TARGET_CONDITIONS.test($$0, $$1);
    }

    public static boolean isEntityAttackable(LivingEntity $$0, LivingEntity $$1) {
        if ($$0.getBrain().isMemoryValue(MemoryModuleType.ATTACK_TARGET, $$1)) {
            return ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_TESTING.test($$0, $$1);
        }
        return ATTACK_TARGET_CONDITIONS.test($$0, $$1);
    }

    public static boolean isEntityAttackableIgnoringLineOfSight(LivingEntity $$0, LivingEntity $$1) {
        if ($$0.getBrain().isMemoryValue(MemoryModuleType.ATTACK_TARGET, $$1)) {
            return ATTACK_TARGET_CONDITIONS_IGNORE_INVISIBILITY_AND_LINE_OF_SIGHT.test($$0, $$1);
        }
        return ATTACK_TARGET_CONDITIONS_IGNORE_LINE_OF_SIGHT.test($$0, $$1);
    }
}