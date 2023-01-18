/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.commands.arguments.coordinates;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public interface Coordinates {
    public Vec3 getPosition(CommandSourceStack var1);

    public Vec2 getRotation(CommandSourceStack var1);

    default public BlockPos getBlockPos(CommandSourceStack $$0) {
        return new BlockPos(this.getPosition($$0));
    }

    public boolean isXRelative();

    public boolean isYRelative();

    public boolean isZRelative();
}