/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.ticks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.ticks.TickAccess;

public interface LevelTickAccess<T>
extends TickAccess<T> {
    public boolean willTickThisTick(BlockPos var1, T var2);
}