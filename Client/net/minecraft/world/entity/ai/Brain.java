/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableSet
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  com.mojang.datafixers.util.Pair
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.MapCodec
 *  com.mojang.serialization.MapLike
 *  com.mojang.serialization.RecordBuilder
 *  it.unimi.dsi.fastutil.objects.ObjectArrayList
 *  java.lang.Deprecated
 *  java.lang.IllegalStateException
 *  java.lang.Integer
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Collection
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.Optional
 *  java.util.Set
 *  java.util.function.Supplier
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.slf4j.Logger
 */
package net.minecraft.world.entity.ai;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.ExpirableValue;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

public class Brain<E extends LivingEntity> {
    static final Logger LOGGER = LogUtils.getLogger();
    private final Supplier<Codec<Brain<E>>> codec;
    private static final int SCHEDULE_UPDATE_DELAY = 20;
    private final Map<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> memories = Maps.newHashMap();
    private final Map<SensorType<? extends Sensor<? super E>>, Sensor<? super E>> sensors = Maps.newLinkedHashMap();
    private final Map<Integer, Map<Activity, Set<BehaviorControl<? super E>>>> availableBehaviorsByPriority = Maps.newTreeMap();
    private Schedule schedule = Schedule.EMPTY;
    private final Map<Activity, Set<Pair<MemoryModuleType<?>, MemoryStatus>>> activityRequirements = Maps.newHashMap();
    private final Map<Activity, Set<MemoryModuleType<?>>> activityMemoriesToEraseWhenStopped = Maps.newHashMap();
    private Set<Activity> coreActivities = Sets.newHashSet();
    private final Set<Activity> activeActivities = Sets.newHashSet();
    private Activity defaultActivity = Activity.IDLE;
    private long lastScheduleUpdate = -9999L;

    public static <E extends LivingEntity> Provider<E> provider(Collection<? extends MemoryModuleType<?>> $$0, Collection<? extends SensorType<? extends Sensor<? super E>>> $$1) {
        return new Provider($$0, $$1);
    }

    public static <E extends LivingEntity> Codec<Brain<E>> codec(final Collection<? extends MemoryModuleType<?>> $$0, final Collection<? extends SensorType<? extends Sensor<? super E>>> $$1) {
        final MutableObject $$2 = new MutableObject();
        $$2.setValue((Object)new MapCodec<Brain<E>>(){

            public <T> Stream<T> keys(DynamicOps<T> $$02) {
                return $$0.stream().flatMap($$0 -> $$0.getCodec().map($$1 -> BuiltInRegistries.MEMORY_MODULE_TYPE.getKey((MemoryModuleType<?>)$$0)).stream()).map($$1 -> $$02.createString($$1.toString()));
            }

            public <T> DataResult<Brain<E>> decode(DynamicOps<T> $$02, MapLike<T> $$12) {
                MutableObject $$22 = new MutableObject((Object)DataResult.success((Object)ImmutableList.builder()));
                $$12.entries().forEach($$22 -> {
                    DataResult $$3 = BuiltInRegistries.MEMORY_MODULE_TYPE.byNameCodec().parse($$02, $$22.getFirst());
                    DataResult $$4 = $$3.flatMap($$2 -> this.captureRead((MemoryModuleType)$$2, $$02, (Object)$$22.getSecond()));
                    $$22.setValue((Object)((DataResult)$$22.getValue()).apply2(ImmutableList.Builder::add, $$4));
                });
                ImmutableList $$3 = (ImmutableList)((DataResult)$$22.getValue()).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).map(ImmutableList.Builder::build).orElseGet(ImmutableList::of);
                return DataResult.success(new Brain($$0, $$1, $$3, () -> ((MutableObject)$$2).getValue()));
            }

            private <T, U> DataResult<MemoryValue<U>> captureRead(MemoryModuleType<U> $$02, DynamicOps<T> $$12, T $$22) {
                return ((DataResult)$$02.getCodec().map(DataResult::success).orElseGet(() -> DataResult.error((String)("No codec for memory: " + $$02)))).flatMap($$2 -> $$2.parse($$12, $$22)).map($$1 -> new MemoryValue($$02, Optional.of((Object)$$1)));
            }

            public <T> RecordBuilder<T> encode(Brain<E> $$02, DynamicOps<T> $$12, RecordBuilder<T> $$22) {
                $$02.memories().forEach($$2 -> $$2.serialize($$12, $$22));
                return $$22;
            }
        }.fieldOf("memories").codec());
        return (Codec)$$2.getValue();
    }

    public Brain(Collection<? extends MemoryModuleType<?>> $$0, Collection<? extends SensorType<? extends Sensor<? super E>>> $$1, ImmutableList<MemoryValue<?>> $$2, Supplier<Codec<Brain<E>>> $$3) {
        this.codec = $$3;
        for (MemoryModuleType $$4 : $$0) {
            this.memories.put((Object)$$4, (Object)Optional.empty());
        }
        for (SensorType $$5 : $$1) {
            this.sensors.put((Object)$$5, $$5.create());
        }
        for (Sensor $$6 : this.sensors.values()) {
            for (MemoryModuleType $$7 : $$6.requires()) {
                this.memories.put((Object)$$7, (Object)Optional.empty());
            }
        }
        for (MemoryValue $$8 : $$2) {
            $$8.setMemoryInternal(this);
        }
    }

    public <T> DataResult<T> serializeStart(DynamicOps<T> $$0) {
        return ((Codec)this.codec.get()).encodeStart($$0, (Object)this);
    }

    Stream<MemoryValue<?>> memories() {
        return this.memories.entrySet().stream().map($$0 -> MemoryValue.createUnchecked((MemoryModuleType)$$0.getKey(), (Optional)$$0.getValue()));
    }

    public boolean hasMemoryValue(MemoryModuleType<?> $$0) {
        return this.checkMemory($$0, MemoryStatus.VALUE_PRESENT);
    }

    public <U> void eraseMemory(MemoryModuleType<U> $$0) {
        this.setMemory($$0, Optional.empty());
    }

    public <U> void setMemory(MemoryModuleType<U> $$0, @Nullable U $$1) {
        this.setMemory($$0, Optional.ofNullable($$1));
    }

    public <U> void setMemoryWithExpiry(MemoryModuleType<U> $$0, U $$1, long $$2) {
        this.setMemoryInternal($$0, Optional.of(ExpirableValue.of($$1, $$2)));
    }

    public <U> void setMemory(MemoryModuleType<U> $$0, Optional<? extends U> $$1) {
        this.setMemoryInternal($$0, $$1.map(ExpirableValue::of));
    }

    <U> void setMemoryInternal(MemoryModuleType<U> $$0, Optional<? extends ExpirableValue<?>> $$1) {
        if (this.memories.containsKey($$0)) {
            if ($$1.isPresent() && this.isEmptyCollection(((ExpirableValue)$$1.get()).getValue())) {
                this.eraseMemory($$0);
            } else {
                this.memories.put($$0, $$1);
            }
        }
    }

    public <U> Optional<U> getMemory(MemoryModuleType<U> $$0) {
        Optional $$1 = (Optional)this.memories.get($$0);
        if ($$1 == null) {
            throw new IllegalStateException("Unregistered memory fetched: " + $$0);
        }
        return $$1.map(ExpirableValue::getValue);
    }

    @Nullable
    public <U> Optional<U> getMemoryInternal(MemoryModuleType<U> $$0) {
        Optional $$1 = (Optional)this.memories.get($$0);
        if ($$1 == null) {
            return null;
        }
        return $$1.map(ExpirableValue::getValue);
    }

    public <U> long getTimeUntilExpiry(MemoryModuleType<U> $$0) {
        Optional $$1 = (Optional)this.memories.get($$0);
        return (Long)$$1.map(ExpirableValue::getTimeToLive).orElse((Object)0L);
    }

    @Deprecated
    @VisibleForDebug
    public Map<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> getMemories() {
        return this.memories;
    }

    public <U> boolean isMemoryValue(MemoryModuleType<U> $$0, U $$12) {
        if (!this.hasMemoryValue($$0)) {
            return false;
        }
        return this.getMemory($$0).filter($$1 -> $$1.equals($$12)).isPresent();
    }

    public boolean checkMemory(MemoryModuleType<?> $$0, MemoryStatus $$1) {
        Optional $$2 = (Optional)this.memories.get($$0);
        if ($$2 == null) {
            return false;
        }
        return $$1 == MemoryStatus.REGISTERED || $$1 == MemoryStatus.VALUE_PRESENT && $$2.isPresent() || $$1 == MemoryStatus.VALUE_ABSENT && !$$2.isPresent();
    }

    public Schedule getSchedule() {
        return this.schedule;
    }

    public void setSchedule(Schedule $$0) {
        this.schedule = $$0;
    }

    public void setCoreActivities(Set<Activity> $$0) {
        this.coreActivities = $$0;
    }

    @Deprecated
    @VisibleForDebug
    public Set<Activity> getActiveActivities() {
        return this.activeActivities;
    }

    @Deprecated
    @VisibleForDebug
    public List<BehaviorControl<? super E>> getRunningBehaviors() {
        ObjectArrayList $$0 = new ObjectArrayList();
        for (Map $$1 : this.availableBehaviorsByPriority.values()) {
            for (Set $$2 : $$1.values()) {
                for (BehaviorControl $$3 : $$2) {
                    if ($$3.getStatus() != Behavior.Status.RUNNING) continue;
                    $$0.add((Object)$$3);
                }
            }
        }
        return $$0;
    }

    public void useDefaultActivity() {
        this.setActiveActivity(this.defaultActivity);
    }

    public Optional<Activity> getActiveNonCoreActivity() {
        for (Activity $$0 : this.activeActivities) {
            if (this.coreActivities.contains((Object)$$0)) continue;
            return Optional.of((Object)$$0);
        }
        return Optional.empty();
    }

    public void setActiveActivityIfPossible(Activity $$0) {
        if (this.activityRequirementsAreMet($$0)) {
            this.setActiveActivity($$0);
        } else {
            this.useDefaultActivity();
        }
    }

    private void setActiveActivity(Activity $$0) {
        if (this.isActive($$0)) {
            return;
        }
        this.eraseMemoriesForOtherActivitesThan($$0);
        this.activeActivities.clear();
        this.activeActivities.addAll(this.coreActivities);
        this.activeActivities.add((Object)$$0);
    }

    private void eraseMemoriesForOtherActivitesThan(Activity $$0) {
        for (Activity $$1 : this.activeActivities) {
            Set $$2;
            if ($$1 == $$0 || ($$2 = (Set)this.activityMemoriesToEraseWhenStopped.get((Object)$$1)) == null) continue;
            for (MemoryModuleType $$3 : $$2) {
                this.eraseMemory($$3);
            }
        }
    }

    public void updateActivityFromSchedule(long $$0, long $$1) {
        if ($$1 - this.lastScheduleUpdate > 20L) {
            this.lastScheduleUpdate = $$1;
            Activity $$2 = this.getSchedule().getActivityAt((int)($$0 % 24000L));
            if (!this.activeActivities.contains((Object)$$2)) {
                this.setActiveActivityIfPossible($$2);
            }
        }
    }

    public void setActiveActivityToFirstValid(List<Activity> $$0) {
        for (Activity $$1 : $$0) {
            if (!this.activityRequirementsAreMet($$1)) continue;
            this.setActiveActivity($$1);
            break;
        }
    }

    public void setDefaultActivity(Activity $$0) {
        this.defaultActivity = $$0;
    }

    public void addActivity(Activity $$0, int $$1, ImmutableList<? extends BehaviorControl<? super E>> $$2) {
        this.addActivity($$0, this.createPriorityPairs($$1, $$2));
    }

    public void addActivityAndRemoveMemoryWhenStopped(Activity $$0, int $$1, ImmutableList<? extends BehaviorControl<? super E>> $$2, MemoryModuleType<?> $$3) {
        ImmutableSet $$4 = ImmutableSet.of((Object)Pair.of($$3, (Object)((Object)MemoryStatus.VALUE_PRESENT)));
        ImmutableSet $$5 = ImmutableSet.of($$3);
        this.addActivityAndRemoveMemoriesWhenStopped($$0, (ImmutableList<? extends Pair<Integer, ? extends BehaviorControl<? super E>>>)this.createPriorityPairs($$1, $$2), (Set<Pair<MemoryModuleType<?>, MemoryStatus>>)$$4, (Set<MemoryModuleType<?>>)$$5);
    }

    public void addActivity(Activity $$0, ImmutableList<? extends Pair<Integer, ? extends BehaviorControl<? super E>>> $$1) {
        this.addActivityAndRemoveMemoriesWhenStopped($$0, $$1, (Set<Pair<MemoryModuleType<?>, MemoryStatus>>)ImmutableSet.of(), (Set<MemoryModuleType<?>>)Sets.newHashSet());
    }

    public void addActivityWithConditions(Activity $$0, ImmutableList<? extends Pair<Integer, ? extends BehaviorControl<? super E>>> $$1, Set<Pair<MemoryModuleType<?>, MemoryStatus>> $$2) {
        this.addActivityAndRemoveMemoriesWhenStopped($$0, $$1, $$2, (Set<MemoryModuleType<?>>)Sets.newHashSet());
    }

    public void addActivityAndRemoveMemoriesWhenStopped(Activity $$02, ImmutableList<? extends Pair<Integer, ? extends BehaviorControl<? super E>>> $$1, Set<Pair<MemoryModuleType<?>, MemoryStatus>> $$2, Set<MemoryModuleType<?>> $$3) {
        this.activityRequirements.put((Object)$$02, $$2);
        if (!$$3.isEmpty()) {
            this.activityMemoriesToEraseWhenStopped.put((Object)$$02, $$3);
        }
        for (Pair $$4 : $$1) {
            ((Set)((Map)this.availableBehaviorsByPriority.computeIfAbsent((Object)((Integer)$$4.getFirst()), $$0 -> Maps.newHashMap())).computeIfAbsent((Object)$$02, $$0 -> Sets.newLinkedHashSet())).add((Object)((BehaviorControl)$$4.getSecond()));
        }
    }

    @VisibleForTesting
    public void removeAllBehaviors() {
        this.availableBehaviorsByPriority.clear();
    }

    public boolean isActive(Activity $$0) {
        return this.activeActivities.contains((Object)$$0);
    }

    public Brain<E> copyWithoutBehaviors() {
        Brain<E> $$0 = new Brain<E>((Collection<MemoryModuleType<?>>)this.memories.keySet(), this.sensors.keySet(), (ImmutableList<MemoryValue<?>>)ImmutableList.of(), this.codec);
        for (Map.Entry $$1 : this.memories.entrySet()) {
            MemoryModuleType $$2 = (MemoryModuleType)$$1.getKey();
            if (!((Optional)$$1.getValue()).isPresent()) continue;
            $$0.memories.put((Object)$$2, (Object)((Optional)$$1.getValue()));
        }
        return $$0;
    }

    public void tick(ServerLevel $$0, E $$1) {
        this.forgetOutdatedMemories();
        this.tickSensors($$0, $$1);
        this.startEachNonRunningBehavior($$0, $$1);
        this.tickEachRunningBehavior($$0, $$1);
    }

    private void tickSensors(ServerLevel $$0, E $$1) {
        for (Sensor $$2 : this.sensors.values()) {
            $$2.tick($$0, $$1);
        }
    }

    private void forgetOutdatedMemories() {
        for (Map.Entry $$0 : this.memories.entrySet()) {
            if (!((Optional)$$0.getValue()).isPresent()) continue;
            ExpirableValue $$1 = (ExpirableValue)((Optional)$$0.getValue()).get();
            if ($$1.hasExpired()) {
                this.eraseMemory((MemoryModuleType)$$0.getKey());
            }
            $$1.tick();
        }
    }

    public void stopAll(ServerLevel $$0, E $$1) {
        long $$2 = ((LivingEntity)$$1).level.getGameTime();
        for (BehaviorControl $$3 : this.getRunningBehaviors()) {
            $$3.doStop($$0, $$1, $$2);
        }
    }

    private void startEachNonRunningBehavior(ServerLevel $$0, E $$1) {
        long $$2 = $$0.getGameTime();
        for (Map $$3 : this.availableBehaviorsByPriority.values()) {
            for (Map.Entry $$4 : $$3.entrySet()) {
                Activity $$5 = (Activity)$$4.getKey();
                if (!this.activeActivities.contains((Object)$$5)) continue;
                Set $$6 = (Set)$$4.getValue();
                for (BehaviorControl $$7 : $$6) {
                    if ($$7.getStatus() != Behavior.Status.STOPPED) continue;
                    $$7.tryStart($$0, $$1, $$2);
                }
            }
        }
    }

    private void tickEachRunningBehavior(ServerLevel $$0, E $$1) {
        long $$2 = $$0.getGameTime();
        for (BehaviorControl $$3 : this.getRunningBehaviors()) {
            $$3.tickOrStop($$0, $$1, $$2);
        }
    }

    private boolean activityRequirementsAreMet(Activity $$0) {
        if (!this.activityRequirements.containsKey((Object)$$0)) {
            return false;
        }
        for (Pair $$1 : (Set)this.activityRequirements.get((Object)$$0)) {
            MemoryStatus $$3;
            MemoryModuleType $$2 = (MemoryModuleType)$$1.getFirst();
            if (this.checkMemory($$2, $$3 = (MemoryStatus)((Object)$$1.getSecond()))) continue;
            return false;
        }
        return true;
    }

    private boolean isEmptyCollection(Object $$0) {
        return $$0 instanceof Collection && ((Collection)$$0).isEmpty();
    }

    ImmutableList<? extends Pair<Integer, ? extends BehaviorControl<? super E>>> createPriorityPairs(int $$0, ImmutableList<? extends BehaviorControl<? super E>> $$1) {
        int $$2 = $$0;
        ImmutableList.Builder $$3 = ImmutableList.builder();
        for (BehaviorControl $$4 : $$1) {
            $$3.add((Object)Pair.of((Object)$$2++, (Object)$$4));
        }
        return $$3.build();
    }

    public static final class Provider<E extends LivingEntity> {
        private final Collection<? extends MemoryModuleType<?>> memoryTypes;
        private final Collection<? extends SensorType<? extends Sensor<? super E>>> sensorTypes;
        private final Codec<Brain<E>> codec;

        Provider(Collection<? extends MemoryModuleType<?>> $$0, Collection<? extends SensorType<? extends Sensor<? super E>>> $$1) {
            this.memoryTypes = $$0;
            this.sensorTypes = $$1;
            this.codec = Brain.codec($$0, $$1);
        }

        public Brain<E> makeBrain(Dynamic<?> $$0) {
            return (Brain)this.codec.parse($$0).resultOrPartial(arg_0 -> ((Logger)LOGGER).error(arg_0)).orElseGet(() -> new Brain(this.memoryTypes, this.sensorTypes, ImmutableList.of(), () -> this.codec));
        }
    }

    static final class MemoryValue<U> {
        private final MemoryModuleType<U> type;
        private final Optional<? extends ExpirableValue<U>> value;

        static <U> MemoryValue<U> createUnchecked(MemoryModuleType<U> $$0, Optional<? extends ExpirableValue<?>> $$1) {
            return new MemoryValue<U>($$0, $$1);
        }

        MemoryValue(MemoryModuleType<U> $$0, Optional<? extends ExpirableValue<U>> $$1) {
            this.type = $$0;
            this.value = $$1;
        }

        void setMemoryInternal(Brain<?> $$0) {
            $$0.setMemoryInternal(this.type, this.value);
        }

        public <T> void serialize(DynamicOps<T> $$0, RecordBuilder<T> $$1) {
            this.type.getCodec().ifPresent($$2 -> this.value.ifPresent($$3 -> $$1.add(BuiltInRegistries.MEMORY_MODULE_TYPE.byNameCodec().encodeStart($$0, this.type), $$2.encodeStart($$0, $$3))));
        }
    }
}