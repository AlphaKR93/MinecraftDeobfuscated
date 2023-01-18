/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 *  java.util.Optional
 *  java.util.function.Predicate
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.animal.Animal;

public class AnimalMakeLove
extends Behavior<Animal> {
    private static final int BREED_RANGE = 3;
    private static final int MIN_DURATION = 60;
    private static final int MAX_DURATION = 110;
    private final EntityType<? extends Animal> partnerType;
    private final float speedModifier;
    private long spawnChildAtTime;

    public AnimalMakeLove(EntityType<? extends Animal> $$0, float $$1) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES, (Object)((Object)MemoryStatus.VALUE_PRESENT), MemoryModuleType.BREED_TARGET, (Object)((Object)MemoryStatus.VALUE_ABSENT), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryStatus.REGISTERED), MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryStatus.REGISTERED)), 110);
        this.partnerType = $$0;
        this.speedModifier = $$1;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel $$0, Animal $$1) {
        return $$1.isInLove() && this.findValidBreedPartner($$1).isPresent();
    }

    @Override
    protected void start(ServerLevel $$0, Animal $$1, long $$2) {
        Animal $$3 = (Animal)this.findValidBreedPartner($$1).get();
        $$1.getBrain().setMemory(MemoryModuleType.BREED_TARGET, $$3);
        $$3.getBrain().setMemory(MemoryModuleType.BREED_TARGET, $$1);
        BehaviorUtils.lockGazeAndWalkToEachOther($$1, $$3, this.speedModifier);
        int $$4 = 60 + $$1.getRandom().nextInt(50);
        this.spawnChildAtTime = $$2 + (long)$$4;
    }

    @Override
    protected boolean canStillUse(ServerLevel $$0, Animal $$1, long $$2) {
        if (!this.hasBreedTargetOfRightType($$1)) {
            return false;
        }
        Animal $$3 = this.getBreedTarget($$1);
        return $$3.isAlive() && $$1.canMate($$3) && BehaviorUtils.entityIsVisible($$1.getBrain(), $$3) && $$2 <= this.spawnChildAtTime;
    }

    @Override
    protected void tick(ServerLevel $$0, Animal $$1, long $$2) {
        Animal $$3 = this.getBreedTarget($$1);
        BehaviorUtils.lockGazeAndWalkToEachOther($$1, $$3, this.speedModifier);
        if (!$$1.closerThan($$3, 3.0)) {
            return;
        }
        if ($$2 >= this.spawnChildAtTime) {
            $$1.spawnChildFromBreeding($$0, $$3);
            $$1.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
            $$3.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
        }
    }

    @Override
    protected void start(ServerLevel $$0, Animal $$1, long $$2) {
        $$1.getBrain().eraseMemory(MemoryModuleType.BREED_TARGET);
        $$1.getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        $$1.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
        this.spawnChildAtTime = 0L;
    }

    private Animal getBreedTarget(Animal $$0) {
        return (Animal)$$0.getBrain().getMemory(MemoryModuleType.BREED_TARGET).get();
    }

    private boolean hasBreedTargetOfRightType(Animal $$0) {
        Brain<AgeableMob> $$1 = $$0.getBrain();
        return $$1.hasMemoryValue(MemoryModuleType.BREED_TARGET) && ((AgeableMob)$$1.getMemory(MemoryModuleType.BREED_TARGET).get()).getType() == this.partnerType;
    }

    private Optional<? extends Animal> findValidBreedPartner(Animal $$0) {
        return ((NearestVisibleLivingEntities)$$0.getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES).get()).findClosest((Predicate<LivingEntity>)((Predicate)$$1 -> {
            Animal $$2;
            return $$1.getType() == this.partnerType && $$1 instanceof Animal && $$0.canMate($$2 = (Animal)$$1);
        })).map(arg_0 -> Animal.class.cast(arg_0));
    }
}