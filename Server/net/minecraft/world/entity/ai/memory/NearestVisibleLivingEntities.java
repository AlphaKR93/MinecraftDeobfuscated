/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.util.List
 *  java.util.Optional
 *  java.util.function.Predicate
 *  java.util.stream.Stream
 */
package net.minecraft.world.entity.ai.memory;

import com.google.common.collect.Iterables;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.sensing.Sensor;

public class NearestVisibleLivingEntities {
    private static final NearestVisibleLivingEntities EMPTY = new NearestVisibleLivingEntities();
    private final List<LivingEntity> nearbyEntities;
    private final Predicate<LivingEntity> lineOfSightTest;

    private NearestVisibleLivingEntities() {
        this.nearbyEntities = List.of();
        this.lineOfSightTest = $$0 -> false;
    }

    public NearestVisibleLivingEntities(LivingEntity $$0, List<LivingEntity> $$12) {
        this.nearbyEntities = $$12;
        Object2BooleanOpenHashMap $$22 = new Object2BooleanOpenHashMap($$12.size());
        Predicate $$3 = $$1 -> Sensor.isEntityTargetable($$0, $$1);
        this.lineOfSightTest = $$2 -> $$22.computeIfAbsent($$2, $$3);
    }

    public static NearestVisibleLivingEntities empty() {
        return EMPTY;
    }

    public Optional<LivingEntity> findClosest(Predicate<LivingEntity> $$0) {
        for (LivingEntity $$1 : this.nearbyEntities) {
            if (!$$0.test((Object)$$1) || !this.lineOfSightTest.test((Object)$$1)) continue;
            return Optional.of((Object)$$1);
        }
        return Optional.empty();
    }

    public Iterable<LivingEntity> findAll(Predicate<LivingEntity> $$0) {
        return Iterables.filter(this.nearbyEntities, $$1 -> $$0.test($$1) && this.lineOfSightTest.test($$1));
    }

    public Stream<LivingEntity> find(Predicate<LivingEntity> $$0) {
        return this.nearbyEntities.stream().filter($$1 -> $$0.test($$1) && this.lineOfSightTest.test($$1));
    }

    public boolean contains(LivingEntity $$0) {
        return this.nearbyEntities.contains((Object)$$0) && this.lineOfSightTest.test((Object)$$0);
    }

    public boolean contains(Predicate<LivingEntity> $$0) {
        for (LivingEntity $$1 : this.nearbyEntities) {
            if (!$$0.test((Object)$$1) || !this.lineOfSightTest.test((Object)$$1)) continue;
            return true;
        }
        return false;
    }
}