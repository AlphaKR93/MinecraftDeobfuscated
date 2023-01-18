/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Class
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 */
package net.minecraft.world.level.timers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.timers.TimerQueue;

@FunctionalInterface
public interface TimerCallback<T> {
    public void handle(T var1, TimerQueue<T> var2, long var3);

    public static abstract class Serializer<T, C extends TimerCallback<T>> {
        private final ResourceLocation id;
        private final Class<?> cls;

        public Serializer(ResourceLocation $$0, Class<?> $$1) {
            this.id = $$0;
            this.cls = $$1;
        }

        public ResourceLocation getId() {
            return this.id;
        }

        public Class<?> getCls() {
            return this.cls;
        }

        public abstract void serialize(CompoundTag var1, C var2);

        public abstract C deserialize(CompoundTag var1);
    }
}