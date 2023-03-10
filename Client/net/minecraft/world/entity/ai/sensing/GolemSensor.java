/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.Optional
 *  java.util.Set
 */
package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;

public class GolemSensor
extends Sensor<LivingEntity> {
    private static final int GOLEM_SCAN_RATE = 200;
    private static final int MEMORY_TIME_TO_LIVE = 600;

    public GolemSensor() {
        this(200);
    }

    public GolemSensor(int $$0) {
        super($$0);
    }

    @Override
    protected void doTick(ServerLevel $$0, LivingEntity $$1) {
        GolemSensor.checkForNearbyGolem($$1);
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_LIVING_ENTITIES);
    }

    public static void checkForNearbyGolem(LivingEntity $$02) {
        Optional<List<LivingEntity>> $$1 = $$02.getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES);
        if (!$$1.isPresent()) {
            return;
        }
        boolean $$2 = ((List)$$1.get()).stream().anyMatch($$0 -> $$0.getType().equals(EntityType.IRON_GOLEM));
        if ($$2) {
            GolemSensor.golemDetected($$02);
        }
    }

    public static void golemDetected(LivingEntity $$0) {
        $$0.getBrain().setMemoryWithExpiry(MemoryModuleType.GOLEM_DETECTED_RECENTLY, true, 600L);
    }
}