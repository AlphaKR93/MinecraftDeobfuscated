/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Comparator
 *  java.util.List
 *  java.util.Optional
 *  java.util.Set
 */
package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.item.ItemEntity;

public class NearestItemSensor
extends Sensor<Mob> {
    private static final long XZ_RANGE = 32L;
    private static final long Y_RANGE = 16L;
    public static final int MAX_DISTANCE_TO_WANTED_ITEM = 32;

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return ImmutableSet.of(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM);
    }

    @Override
    protected void doTick(ServerLevel $$02, Mob $$12) {
        Brain<?> $$2 = $$12.getBrain();
        List $$3 = $$02.getEntitiesOfClass(ItemEntity.class, $$12.getBoundingBox().inflate(32.0, 16.0, 32.0), $$0 -> true);
        $$3.sort(Comparator.comparingDouble($$12::distanceToSqr));
        Optional $$4 = $$3.stream().filter($$1 -> $$12.wantsToPickUp($$1.getItem())).filter($$1 -> $$1.closerThan($$12, 32.0)).filter($$12::hasLineOfSight).findFirst();
        $$2.setMemory(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM, $$4);
    }
}