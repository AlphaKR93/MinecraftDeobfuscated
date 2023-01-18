/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Comparator
 *  java.util.List
 *  java.util.Map
 *  java.util.Map$Entry
 *  java.util.stream.Collectors
 */
package net.minecraft.world.entity.schedule;

import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.ScheduleBuilder;
import net.minecraft.world.entity.schedule.Timeline;

public class Schedule {
    public static final int WORK_START_TIME = 2000;
    public static final int TOTAL_WORK_TIME = 7000;
    public static final Schedule EMPTY = Schedule.register("empty").changeActivityAt(0, Activity.IDLE).build();
    public static final Schedule SIMPLE = Schedule.register("simple").changeActivityAt(5000, Activity.WORK).changeActivityAt(11000, Activity.REST).build();
    public static final Schedule VILLAGER_BABY = Schedule.register("villager_baby").changeActivityAt(10, Activity.IDLE).changeActivityAt(3000, Activity.PLAY).changeActivityAt(6000, Activity.IDLE).changeActivityAt(10000, Activity.PLAY).changeActivityAt(12000, Activity.REST).build();
    public static final Schedule VILLAGER_DEFAULT = Schedule.register("villager_default").changeActivityAt(10, Activity.IDLE).changeActivityAt(2000, Activity.WORK).changeActivityAt(9000, Activity.MEET).changeActivityAt(11000, Activity.IDLE).changeActivityAt(12000, Activity.REST).build();
    private final Map<Activity, Timeline> timelines = Maps.newHashMap();

    protected static ScheduleBuilder register(String $$0) {
        Schedule $$1 = Registry.register(BuiltInRegistries.SCHEDULE, $$0, new Schedule());
        return new ScheduleBuilder($$1);
    }

    protected void ensureTimelineExistsFor(Activity $$0) {
        if (!this.timelines.containsKey((Object)$$0)) {
            this.timelines.put((Object)$$0, (Object)new Timeline());
        }
    }

    protected Timeline getTimelineFor(Activity $$0) {
        return (Timeline)this.timelines.get((Object)$$0);
    }

    protected List<Timeline> getAllTimelinesExceptFor(Activity $$0) {
        return (List)this.timelines.entrySet().stream().filter($$1 -> $$1.getKey() != $$0).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public Activity getActivityAt(int $$0) {
        return (Activity)this.timelines.entrySet().stream().max(Comparator.comparingDouble($$1 -> ((Timeline)$$1.getValue()).getValueAt($$0))).map(Map.Entry::getKey).orElse((Object)Activity.IDLE);
    }
}