/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 */
package net.minecraft.world.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.RecipeType;

public class FurnaceMenu
extends AbstractFurnaceMenu {
    public FurnaceMenu(int $$0, Inventory $$1) {
        super(MenuType.FURNACE, RecipeType.SMELTING, RecipeBookType.FURNACE, $$0, $$1);
    }

    public FurnaceMenu(int $$0, Inventory $$1, Container $$2, ContainerData $$3) {
        super(MenuType.FURNACE, RecipeType.SMELTING, RecipeBookType.FURNACE, $$0, $$1, $$2, $$3);
    }
}