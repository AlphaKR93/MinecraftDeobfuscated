/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Iterable
 *  java.lang.Object
 *  java.lang.String
 *  java.util.IdentityHashMap
 *  java.util.Iterator
 *  java.util.Map
 *  javax.annotation.Nullable
 */
package net.minecraft.stats;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatFormatter;

public class StatType<T>
implements Iterable<Stat<T>> {
    private final Registry<T> registry;
    private final Map<T, Stat<T>> map = new IdentityHashMap();
    @Nullable
    private Component displayName;

    public StatType(Registry<T> $$0) {
        this.registry = $$0;
    }

    public boolean contains(T $$0) {
        return this.map.containsKey($$0);
    }

    public Stat<T> get(T $$0, StatFormatter $$12) {
        return (Stat)this.map.computeIfAbsent($$0, $$1 -> new Stat<Object>(this, $$1, $$12));
    }

    public Registry<T> getRegistry() {
        return this.registry;
    }

    public Iterator<Stat<T>> iterator() {
        return this.map.values().iterator();
    }

    public Stat<T> get(T $$0) {
        return this.get($$0, StatFormatter.DEFAULT);
    }

    public String getTranslationKey() {
        return "stat_type." + BuiltInRegistries.STAT_TYPE.getKey(this).toString().replace(':', '.');
    }

    public Component getDisplayName() {
        if (this.displayName == null) {
            this.displayName = Component.translatable(this.getTranslationKey());
        }
        return this.displayName;
    }
}