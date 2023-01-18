/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  java.lang.Float
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.NearestVisibleLivingEntitySensor;

public class VillagerHostilesSensor
extends NearestVisibleLivingEntitySensor {
    private static final ImmutableMap<EntityType<?>, Float> ACCEPTABLE_DISTANCE_FROM_HOSTILES = ImmutableMap.builder().put(EntityType.DROWNED, (Object)Float.valueOf((float)8.0f)).put(EntityType.EVOKER, (Object)Float.valueOf((float)12.0f)).put(EntityType.HUSK, (Object)Float.valueOf((float)8.0f)).put(EntityType.ILLUSIONER, (Object)Float.valueOf((float)12.0f)).put(EntityType.PILLAGER, (Object)Float.valueOf((float)15.0f)).put(EntityType.RAVAGER, (Object)Float.valueOf((float)12.0f)).put(EntityType.VEX, (Object)Float.valueOf((float)8.0f)).put(EntityType.VINDICATOR, (Object)Float.valueOf((float)10.0f)).put(EntityType.ZOGLIN, (Object)Float.valueOf((float)10.0f)).put(EntityType.ZOMBIE, (Object)Float.valueOf((float)8.0f)).put(EntityType.ZOMBIE_VILLAGER, (Object)Float.valueOf((float)8.0f)).build();

    @Override
    protected boolean isMatchingEntity(LivingEntity $$0, LivingEntity $$1) {
        return this.isHostile($$1) && this.isClose($$0, $$1);
    }

    private boolean isClose(LivingEntity $$0, LivingEntity $$1) {
        float $$2 = ((Float)ACCEPTABLE_DISTANCE_FROM_HOSTILES.get($$1.getType())).floatValue();
        return $$1.distanceToSqr($$0) <= (double)($$2 * $$2);
    }

    @Override
    protected MemoryModuleType<LivingEntity> getMemory() {
        return MemoryModuleType.NEAREST_HOSTILE;
    }

    private boolean isHostile(LivingEntity $$0) {
        return ACCEPTABLE_DISTANCE_FROM_HOSTILES.containsKey($$0.getType());
    }
}