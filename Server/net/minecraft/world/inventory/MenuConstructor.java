/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.FunctionalInterface
 *  java.lang.Object
 *  javax.annotation.Nullable
 *  net.minecraft.world.entity.player.Player
 */
package net.minecraft.world.inventory;

import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

@FunctionalInterface
public interface MenuConstructor {
    @Nullable
    public AbstractContainerMenu createMenu(int var1, Inventory var2, Player var3);
}