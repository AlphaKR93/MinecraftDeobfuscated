/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.String
 *  java.util.Objects
 *  javax.annotation.Nullable
 */
package net.minecraft.stats;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.StatType;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class Stat<T>
extends ObjectiveCriteria {
    private final StatFormatter formatter;
    private final T value;
    private final StatType<T> type;

    protected Stat(StatType<T> $$0, T $$1, StatFormatter $$2) {
        super(Stat.buildName($$0, $$1));
        this.type = $$0;
        this.formatter = $$2;
        this.value = $$1;
    }

    public static <T> String buildName(StatType<T> $$0, T $$1) {
        return Stat.locationToKey(BuiltInRegistries.STAT_TYPE.getKey($$0)) + ":" + Stat.locationToKey($$0.getRegistry().getKey($$1));
    }

    private static <T> String locationToKey(@Nullable ResourceLocation $$0) {
        return $$0.toString().replace(':', '.');
    }

    public StatType<T> getType() {
        return this.type;
    }

    public T getValue() {
        return this.value;
    }

    public String format(int $$0) {
        return this.formatter.format($$0);
    }

    public boolean equals(Object $$0) {
        return this == $$0 || $$0 instanceof Stat && Objects.equals((Object)this.getName(), (Object)((Stat)$$0).getName());
    }

    public int hashCode() {
        return this.getName().hashCode();
    }

    public String toString() {
        return "Stat{name=" + this.getName() + ", formatter=" + this.formatter + "}";
    }
}