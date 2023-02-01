/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  java.lang.Boolean
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.schedule.Activity;

public class GoToPotentialJobSite
extends Behavior<Villager> {
    private static final int TICKS_UNTIL_TIMEOUT = 1200;
    final float speedModifier;

    public GoToPotentialJobSite(float $$0) {
        super((Map<MemoryModuleType<?>, MemoryStatus>)ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, (Object)((Object)MemoryStatus.VALUE_PRESENT)), 1200);
        this.speedModifier = $$0;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel $$02, Villager $$1) {
        return (Boolean)$$1.getBrain().getActiveNonCoreActivity().map($$0 -> $$0 == Activity.IDLE || $$0 == Activity.WORK || $$0 == Activity.PLAY).orElse((Object)true);
    }

    @Override
    protected boolean canStillUse(ServerLevel $$0, Villager $$1, long $$2) {
        return $$1.getBrain().hasMemoryValue(MemoryModuleType.POTENTIAL_JOB_SITE);
    }

    @Override
    protected void stop(ServerLevel $$0, Villager $$1, long $$2) {
        BehaviorUtils.setWalkAndLookTargetMemories((LivingEntity)$$1, ((GlobalPos)$$1.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get()).pos(), this.speedModifier, 1);
    }

    @Override
    protected void stop(ServerLevel $$0, Villager $$12, long $$2) {
        Optional<GlobalPos> $$3 = $$12.getBrain().getMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
        $$3.ifPresent($$1 -> {
            BlockPos $$2 = $$1.pos();
            ServerLevel $$3 = $$0.getServer().getLevel($$1.dimension());
            if ($$3 == null) {
                return;
            }
            PoiManager $$4 = $$3.getPoiManager();
            if ($$4.exists($$2, (Predicate<Holder<PoiType>>)((Predicate)$$0 -> true))) {
                $$4.release($$2);
            }
            DebugPackets.sendPoiTicketCountPacket($$0, $$2);
        });
        $$12.getBrain().eraseMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
    }
}