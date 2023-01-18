/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Object
 *  java.util.Iterator
 *  java.util.Map
 *  java.util.Map$Entry
 */
package net.minecraft.world.item;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;

public class ItemCooldowns {
    private final Map<Item, CooldownInstance> cooldowns = Maps.newHashMap();
    private int tickCount;

    public boolean isOnCooldown(Item $$0) {
        return this.getCooldownPercent($$0, 0.0f) > 0.0f;
    }

    public float getCooldownPercent(Item $$0, float $$1) {
        CooldownInstance $$2 = (CooldownInstance)this.cooldowns.get((Object)$$0);
        if ($$2 != null) {
            float $$3 = $$2.endTime - $$2.startTime;
            float $$4 = (float)$$2.endTime - ((float)this.tickCount + $$1);
            return Mth.clamp($$4 / $$3, 0.0f, 1.0f);
        }
        return 0.0f;
    }

    public void tick() {
        ++this.tickCount;
        if (!this.cooldowns.isEmpty()) {
            Iterator $$0 = this.cooldowns.entrySet().iterator();
            while ($$0.hasNext()) {
                Map.Entry $$1 = (Map.Entry)$$0.next();
                if (((CooldownInstance)$$1.getValue()).endTime > this.tickCount) continue;
                $$0.remove();
                this.onCooldownEnded((Item)$$1.getKey());
            }
        }
    }

    public void addCooldown(Item $$0, int $$1) {
        this.cooldowns.put((Object)$$0, (Object)new CooldownInstance(this.tickCount, this.tickCount + $$1));
        this.onCooldownStarted($$0, $$1);
    }

    public void removeCooldown(Item $$0) {
        this.cooldowns.remove((Object)$$0);
        this.onCooldownEnded($$0);
    }

    protected void onCooldownStarted(Item $$0, int $$1) {
    }

    protected void onCooldownEnded(Item $$0) {
    }

    static class CooldownInstance {
        final int startTime;
        final int endTime;

        CooldownInstance(int $$0, int $$1) {
            this.startTime = $$0;
            this.endTime = $$1;
        }
    }
}