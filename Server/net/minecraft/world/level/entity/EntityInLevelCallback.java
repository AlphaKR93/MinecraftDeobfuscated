/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world.level.entity;

import net.minecraft.world.entity.Entity;

public interface EntityInLevelCallback {
    public static final EntityInLevelCallback NULL = new EntityInLevelCallback(){

        @Override
        public void onMove() {
        }

        @Override
        public void onRemove(Entity.RemovalReason $$0) {
        }
    };

    public void onMove();

    public void onRemove(Entity.RemovalReason var1);
}