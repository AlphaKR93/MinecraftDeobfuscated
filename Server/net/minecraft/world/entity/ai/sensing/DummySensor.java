/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Set
 */
package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;

public class DummySensor
extends Sensor<LivingEntity> {
    @Override
    protected void doTick(ServerLevel $$0, LivingEntity $$1) {
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of();
    }
}