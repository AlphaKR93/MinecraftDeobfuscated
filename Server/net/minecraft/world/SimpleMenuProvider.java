/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.world;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;

public final class SimpleMenuProvider
implements MenuProvider {
    private final Component title;
    private final MenuConstructor menuConstructor;

    public SimpleMenuProvider(MenuConstructor $$0, Component $$1) {
        this.menuConstructor = $$0;
        this.title = $$1;
    }

    @Override
    public Component getDisplayName() {
        return this.title;
    }

    @Override
    public AbstractContainerMenu createMenu(int $$0, Inventory $$1, Player $$2) {
        return this.menuConstructor.createMenu($$0, $$1, $$2);
    }
}