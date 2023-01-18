/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  java.lang.Float
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 *  java.util.Optional
 *  java.util.function.Function
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.player.Player;

public class FollowTemptation
extends Behavior<PathfinderMob> {
    public static final int TEMPTATION_COOLDOWN = 100;
    public static final double CLOSE_ENOUGH_DIST = 2.5;
    private final Function<LivingEntity, Float> speedModifier;

    public FollowTemptation(Function<LivingEntity, Float> $$0) {
        super((Map)Util.make(() -> {
            ImmutableMap.Builder $$0 = ImmutableMap.builder();
            $$0.put(MemoryModuleType.LOOK_TARGET, (Object)MemoryStatus.REGISTERED);
            $$0.put(MemoryModuleType.WALK_TARGET, (Object)MemoryStatus.REGISTERED);
            $$0.put(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, (Object)MemoryStatus.VALUE_ABSENT);
            $$0.put(MemoryModuleType.IS_TEMPTED, (Object)MemoryStatus.REGISTERED);
            $$0.put(MemoryModuleType.TEMPTING_PLAYER, (Object)MemoryStatus.VALUE_PRESENT);
            $$0.put(MemoryModuleType.BREED_TARGET, (Object)MemoryStatus.VALUE_ABSENT);
            $$0.put(MemoryModuleType.IS_PANICKING, (Object)MemoryStatus.VALUE_ABSENT);
            return $$0.build();
        }));
        this.speedModifier = $$0;
    }

    protected float getSpeedModifier(PathfinderMob $$0) {
        return ((Float)this.speedModifier.apply((Object)$$0)).floatValue();
    }

    private Optional<Player> getTemptingPlayer(PathfinderMob $$0) {
        return $$0.getBrain().getMemory(MemoryModuleType.TEMPTING_PLAYER);
    }

    @Override
    protected boolean timedOut(long $$0) {
        return false;
    }

    @Override
    protected boolean canStillUse(ServerLevel $$0, PathfinderMob $$1, long $$2) {
        return this.getTemptingPlayer($$1).isPresent() && !$$1.getBrain().hasMemoryValue(MemoryModuleType.BREED_TARGET) && !$$1.getBrain().hasMemoryValue(MemoryModuleType.IS_PANICKING);
    }

    @Override
    protected void start(ServerLevel $$0, PathfinderMob $$1, long $$2) {
        $$1.getBrain().setMemory(MemoryModuleType.IS_TEMPTED, true);
    }

    @Override
    protected void start(ServerLevel $$0, PathfinderMob $$1, long $$2) {
        Brain<?> $$3 = $$1.getBrain();
        $$3.setMemory(MemoryModuleType.TEMPTATION_COOLDOWN_TICKS, 100);
        $$3.setMemory(MemoryModuleType.IS_TEMPTED, false);
        $$3.eraseMemory(MemoryModuleType.WALK_TARGET);
        $$3.eraseMemory(MemoryModuleType.LOOK_TARGET);
    }

    @Override
    protected void start(ServerLevel $$0, PathfinderMob $$1, long $$2) {
        Player $$3 = (Player)this.getTemptingPlayer($$1).get();
        Brain<?> $$4 = $$1.getBrain();
        $$4.setMemory(MemoryModuleType.LOOK_TARGET, new EntityTracker($$3, true));
        if ($$1.distanceToSqr($$3) < 6.25) {
            $$4.eraseMemory(MemoryModuleType.WALK_TARGET);
        } else {
            $$4.setMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(new EntityTracker($$3, false), this.getSpeedModifier($$1), 2));
        }
    }
}