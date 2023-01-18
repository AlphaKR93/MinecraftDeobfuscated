/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMaps
 *  java.lang.Object
 *  java.lang.UnsupportedOperationException
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.entity;

import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;

public class EntityTickList {
    private Int2ObjectMap<Entity> active = new Int2ObjectLinkedOpenHashMap();
    private Int2ObjectMap<Entity> passive = new Int2ObjectLinkedOpenHashMap();
    @Nullable
    private Int2ObjectMap<Entity> iterated;

    private void ensureActiveIsNotIterated() {
        if (this.iterated == this.active) {
            this.passive.clear();
            for (Int2ObjectMap.Entry $$0 : Int2ObjectMaps.fastIterable(this.active)) {
                this.passive.put($$0.getIntKey(), (Object)((Entity)$$0.getValue()));
            }
            Int2ObjectMap<Entity> $$1 = this.active;
            this.active = this.passive;
            this.passive = $$1;
        }
    }

    public void add(Entity $$0) {
        this.ensureActiveIsNotIterated();
        this.active.put($$0.getId(), (Object)$$0);
    }

    public void remove(Entity $$0) {
        this.ensureActiveIsNotIterated();
        this.active.remove($$0.getId());
    }

    public boolean contains(Entity $$0) {
        return this.active.containsKey($$0.getId());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void forEach(Consumer<Entity> $$0) {
        if (this.iterated != null) {
            throw new UnsupportedOperationException("Only one concurrent iteration supported");
        }
        this.iterated = this.active;
        try {
            for (Entity $$1 : this.active.values()) {
                $$0.accept((Object)$$1);
            }
        }
        finally {
            this.iterated = null;
        }
    }
}