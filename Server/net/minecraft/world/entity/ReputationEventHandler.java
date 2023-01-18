/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.ReputationEventType;

public interface ReputationEventHandler {
    public void onReputationEventFrom(ReputationEventType var1, Entity var2);
}