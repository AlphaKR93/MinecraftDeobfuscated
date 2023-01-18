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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.monster.warden.Warden;

public class Digging<E extends Warden>
extends Behavior<E> {
    public Digging(int $$0) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.ATTACK_TARGET, (Object)((Object)MemoryStatus.VALUE_ABSENT), MemoryModuleType.WALK_TARGET, (Object)((Object)MemoryStatus.VALUE_ABSENT)), $$0);
    }

    @Override
    protected boolean canStillUse(ServerLevel $$0, E $$1, long $$2) {
        return ((Entity)$$1).getRemovalReason() == null;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel $$0, E $$1) {
        return ((Entity)$$1).isOnGround() || ((Entity)$$1).isInWater() || ((Entity)$$1).isInLava();
    }

    @Override
    protected void stop(ServerLevel $$0, E $$1, long $$2) {
        if (((Entity)$$1).isOnGround()) {
            ((Entity)$$1).setPose(Pose.DIGGING);
            ((Entity)$$1).playSound(SoundEvents.WARDEN_DIG, 5.0f, 1.0f);
        } else {
            ((Entity)$$1).playSound(SoundEvents.WARDEN_AGITATED, 5.0f, 1.0f);
            this.stop($$0, $$1, $$2);
        }
    }

    @Override
    protected void stop(ServerLevel $$0, E $$1, long $$2) {
        if (((Entity)$$1).getRemovalReason() == null) {
            ((Entity)$$1).remove(Entity.RemovalReason.DISCARDED);
        }
    }
}