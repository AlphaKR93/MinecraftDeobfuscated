/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.level.entity;

public interface LevelCallback<T> {
    public void onCreated(T var1);

    public void onDestroyed(T var1);

    public void onTickingStart(T var1);

    public void onTickingEnd(T var1);

    public void onTrackingStart(T var1);

    public void onTrackingEnd(T var1);

    public void onSectionChange(T var1);
}