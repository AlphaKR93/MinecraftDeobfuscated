/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Map
 *  java.util.Optional
 */
package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;

public class WorkAtPoi
extends Behavior<Villager> {
    private static final int CHECK_COOLDOWN = 300;
    private static final double DISTANCE = 1.73;
    private long lastCheck;

    public WorkAtPoi() {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.JOB_SITE, (Object)((Object)MemoryStatus.VALUE_PRESENT), MemoryModuleType.LOOK_TARGET, (Object)((Object)MemoryStatus.REGISTERED)));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel $$0, Villager $$1) {
        if ($$0.getGameTime() - this.lastCheck < 300L) {
            return false;
        }
        if ($$0.random.nextInt(2) != 0) {
            return false;
        }
        this.lastCheck = $$0.getGameTime();
        GlobalPos $$2 = (GlobalPos)$$1.getBrain().getMemory(MemoryModuleType.JOB_SITE).get();
        return $$2.dimension() == $$0.dimension() && $$2.pos().closerToCenterThan($$1.position(), 1.73);
    }

    @Override
    protected void start(ServerLevel $$0, Villager $$12, long $$2) {
        Brain<Villager> $$3 = $$12.getBrain();
        $$3.setMemory(MemoryModuleType.LAST_WORKED_AT_POI, $$2);
        $$3.getMemory(MemoryModuleType.JOB_SITE).ifPresent($$1 -> $$3.setMemory(MemoryModuleType.LOOK_TARGET, new BlockPosTracker($$1.pos())));
        $$12.playWorkSound();
        this.useWorkstation($$0, $$12);
        if ($$12.shouldRestock()) {
            $$12.restock();
        }
    }

    protected void useWorkstation(ServerLevel $$0, Villager $$1) {
    }

    @Override
    protected boolean canStillUse(ServerLevel $$0, Villager $$1, long $$2) {
        Optional<GlobalPos> $$3 = $$1.getBrain().getMemory(MemoryModuleType.JOB_SITE);
        if (!$$3.isPresent()) {
            return false;
        }
        GlobalPos $$4 = (GlobalPos)$$3.get();
        return $$4.dimension() == $$0.dimension() && $$4.pos().closerToCenterThan($$1.position(), 1.73);
    }
}