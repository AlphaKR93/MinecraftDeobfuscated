/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap
 *  java.lang.Object
 *  java.util.Collection
 *  java.util.List
 */
package net.minecraft.world.entity.schedule;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import java.util.Collection;
import java.util.List;
import net.minecraft.world.entity.schedule.Keyframe;

public class Timeline {
    private final List<Keyframe> keyframes = Lists.newArrayList();
    private int previousIndex;

    public ImmutableList<Keyframe> getKeyframes() {
        return ImmutableList.copyOf(this.keyframes);
    }

    public Timeline addKeyframe(int $$0, float $$1) {
        this.keyframes.add((Object)new Keyframe($$0, $$1));
        this.sortAndDeduplicateKeyframes();
        return this;
    }

    public Timeline addKeyframes(Collection<Keyframe> $$0) {
        this.keyframes.addAll($$0);
        this.sortAndDeduplicateKeyframes();
        return this;
    }

    private void sortAndDeduplicateKeyframes() {
        Int2ObjectAVLTreeMap $$0 = new Int2ObjectAVLTreeMap();
        this.keyframes.forEach(arg_0 -> Timeline.lambda$sortAndDeduplicateKeyframes$0((Int2ObjectSortedMap)$$0, arg_0));
        this.keyframes.clear();
        this.keyframes.addAll((Collection)$$0.values());
        this.previousIndex = 0;
    }

    public float getValueAt(int $$0) {
        Keyframe $$7;
        if (this.keyframes.size() <= 0) {
            return 0.0f;
        }
        Keyframe $$1 = (Keyframe)this.keyframes.get(this.previousIndex);
        Keyframe $$2 = (Keyframe)this.keyframes.get(this.keyframes.size() - 1);
        boolean $$3 = $$0 < $$1.getTimeStamp();
        int $$4 = $$3 ? 0 : this.previousIndex;
        float $$5 = $$3 ? $$2.getValue() : $$1.getValue();
        int $$6 = $$4;
        while ($$6 < this.keyframes.size() && ($$7 = (Keyframe)this.keyframes.get($$6)).getTimeStamp() <= $$0) {
            this.previousIndex = $$6++;
            $$5 = $$7.getValue();
        }
        return $$5;
    }

    private static /* synthetic */ void lambda$sortAndDeduplicateKeyframes$0(Int2ObjectSortedMap $$0, Keyframe $$1) {
        $$0.put($$1.getTimeStamp(), (Object)$$1);
    }
}