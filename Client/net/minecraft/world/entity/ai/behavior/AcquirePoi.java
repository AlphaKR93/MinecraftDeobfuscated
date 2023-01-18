/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.kinds.App
 *  com.mojang.datafixers.kinds.Applicative
 *  com.mojang.datafixers.util.Pair
 *  it.unimi.dsi.fastutil.longs.Long2ObjectMap
 *  it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap
 *  java.lang.Byte
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.HashSet
 *  java.util.Optional
 *  java.util.Set
 *  java.util.function.BiPredicate
 *  java.util.function.Predicate
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.mutable.MutableLong
 */
package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.pathfinder.Path;
import org.apache.commons.lang3.mutable.MutableLong;

public class AcquirePoi {
    public static final int SCAN_RANGE = 48;

    public static BehaviorControl<PathfinderMob> create(Predicate<Holder<PoiType>> $$0, MemoryModuleType<GlobalPos> $$1, boolean $$2, Optional<Byte> $$3) {
        return AcquirePoi.create($$0, $$1, $$1, $$2, $$3);
    }

    public static BehaviorControl<PathfinderMob> create(Predicate<Holder<PoiType>> $$0, MemoryModuleType<GlobalPos> $$1, MemoryModuleType<GlobalPos> $$22, boolean $$3, Optional<Byte> $$4) {
        int $$5 = 5;
        int $$6 = 20;
        MutableLong $$7 = new MutableLong(0L);
        Long2ObjectOpenHashMap $$8 = new Long2ObjectOpenHashMap();
        OneShot<PathfinderMob> $$9 = BehaviorBuilder.create(arg_0 -> AcquirePoi.lambda$create$8($$22, $$3, $$7, (Long2ObjectMap)$$8, $$0, $$4, arg_0));
        if ($$22 == $$1) {
            return $$9;
        }
        return BehaviorBuilder.create($$2 -> $$2.group($$2.absent($$1)).apply((Applicative)$$2, $$1 -> $$9));
    }

    @Nullable
    public static Path findPathToPois(Mob $$0, Set<Pair<Holder<PoiType>, BlockPos>> $$1) {
        if ($$1.isEmpty()) {
            return null;
        }
        HashSet $$2 = new HashSet();
        int $$3 = 1;
        for (Pair $$4 : $$1) {
            $$3 = Math.max((int)$$3, (int)((PoiType)((Object)((Holder)$$4.getFirst()).value())).validRange());
            $$2.add((Object)((BlockPos)$$4.getSecond()));
        }
        return $$0.getNavigation().createPath((Set<BlockPos>)$$2, $$3);
    }

    private static /* synthetic */ App lambda$create$8(MemoryModuleType $$0, boolean $$1, MutableLong $$2, Long2ObjectMap $$3, Predicate $$4, Optional $$52, BehaviorBuilder.Instance $$6) {
        return $$6.group($$6.absent($$0)).apply((Applicative)$$6, $$5 -> ($$6, $$7, $$82) -> {
            if ($$1 && $$7.isBaby()) {
                return false;
            }
            if ($$2.getValue() == 0L) {
                $$2.setValue($$6.getGameTime() + (long)$$6.random.nextInt(20));
                return false;
            }
            if ($$6.getGameTime() < $$2.getValue()) {
                return false;
            }
            $$2.setValue($$82 + 20L + (long)$$6.getRandom().nextInt(20));
            PoiManager $$9 = $$6.getPoiManager();
            $$3.long2ObjectEntrySet().removeIf($$1 -> !((JitteredLinearRetry)$$1.getValue()).isStillValid($$82));
            Predicate $$10 = $$2 -> {
                JitteredLinearRetry $$3 = (JitteredLinearRetry)$$3.get($$2.asLong());
                if ($$3 == null) {
                    return true;
                }
                if (!$$3.shouldRetry($$82)) {
                    return false;
                }
                $$3.markAttempt($$82);
                return true;
            };
            Set $$11 = (Set)$$9.findAllClosestFirstWithType((Predicate<Holder<PoiType>>)$$4, (Predicate<BlockPos>)$$10, $$7.blockPosition(), 48, PoiManager.Occupancy.HAS_SPACE).limit(5L).collect(Collectors.toSet());
            Path $$122 = AcquirePoi.findPathToPois($$7, (Set<Pair<Holder<PoiType>, BlockPos>>)$$11);
            if ($$122 != null && $$122.canReach()) {
                BlockPos $$13 = $$122.getTarget();
                $$9.getType($$13).ifPresent($$8 -> {
                    $$9.take((Predicate<Holder<PoiType>>)$$4, (BiPredicate<Holder<PoiType>, BlockPos>)((BiPredicate)($$1, $$2) -> $$2.equals($$13)), $$13, 1);
                    $$5.set(GlobalPos.of($$6.dimension(), $$13));
                    $$52.ifPresent($$2 -> $$6.broadcastEntityEvent($$7, (byte)$$2));
                    $$3.clear();
                    DebugPackets.sendPoiTicketCountPacket($$6, $$13);
                });
            } else {
                for (Pair $$14 : $$11) {
                    $$3.computeIfAbsent(((BlockPos)$$14.getSecond()).asLong(), $$2 -> new JitteredLinearRetry($$0.random, $$82));
                }
            }
            return true;
        });
    }

    static class JitteredLinearRetry {
        private static final int MIN_INTERVAL_INCREASE = 40;
        private static final int MAX_INTERVAL_INCREASE = 80;
        private static final int MAX_RETRY_PATHFINDING_INTERVAL = 400;
        private final RandomSource random;
        private long previousAttemptTimestamp;
        private long nextScheduledAttemptTimestamp;
        private int currentDelay;

        JitteredLinearRetry(RandomSource $$0, long $$1) {
            this.random = $$0;
            this.markAttempt($$1);
        }

        public void markAttempt(long $$0) {
            this.previousAttemptTimestamp = $$0;
            int $$1 = this.currentDelay + this.random.nextInt(40) + 40;
            this.currentDelay = Math.min((int)$$1, (int)400);
            this.nextScheduledAttemptTimestamp = $$0 + (long)this.currentDelay;
        }

        public boolean isStillValid(long $$0) {
            return $$0 - this.previousAttemptTimestamp < 400L;
        }

        public boolean shouldRetry(long $$0) {
            return $$0 >= this.nextScheduledAttemptTimestamp;
        }

        public String toString() {
            return "RetryMarker{, previousAttemptAt=" + this.previousAttemptTimestamp + ", nextScheduledAttemptAt=" + this.nextScheduledAttemptTimestamp + ", currentDelay=" + this.currentDelay + "}";
        }
    }
}