/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.item.ItemStack;

public class LecternScreen
extends BookViewScreen
implements MenuAccess<LecternMenu> {
    private final LecternMenu menu;
    private final ContainerListener listener = new ContainerListener(){

        @Override
        public void slotChanged(AbstractContainerMenu $$0, int $$1, ItemStack $$2) {
            LecternScreen.this.bookChanged();
        }

        @Override
        public void dataChanged(AbstractContainerMenu $$0, int $$1, int $$2) {
            if ($$1 == 0) {
                LecternScreen.this.pageChanged();
            }
        }
    };

    public LecternScreen(LecternMenu $$0, Inventory $$1, Component $$2) {
        this.menu = $$0;
    }

    @Override
    public LecternMenu getMenu() {
        return this.menu;
    }

    @Override
    protected void init() {
        super.init();
        this.menu.addSlotListener(this.listener);
    }

    @Override
    public void onClose() {
        this.minecraft.player.closeContainer();
        super.onClose();
    }

    @Override
    public void removed() {
        super.removed();
        this.menu.removeSlotListener(this.listener);
    }

    @Override
    protected void createMenuControls() {
        if (this.minecraft.player.mayBuild()) {
            this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.onClose()).bounds(this.width / 2 - 100, 196, 98, 20).build());
            this.addRenderableWidget(Button.builder(Component.translatable("lectern.take_book"), $$0 -> this.sendButtonClick(3)).bounds(this.width / 2 + 2, 196, 98, 20).build());
        } else {
            super.createMenuControls();
        }
    }

    @Override
    protected void pageBack() {
        this.sendButtonClick(1);
    }

    @Override
    protected void pageForward() {
        this.sendButtonClick(2);
    }

    @Override
    protected boolean forcePage(int $$0) {
        if ($$0 != this.menu.getPage()) {
            this.sendButtonClick(100 + $$0);
            return true;
        }
        return false;
    }

    private void sendButtonClick(int $$0) {
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, $$0);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    void bookChanged() {
        ItemStack $$0 = this.menu.getBook();
        this.setBookAccess(BookViewScreen.BookAccess.fromItem($$0));
    }

    void pageChanged() {
        this.setPage(this.menu.getPage());
    }

    @Override
    protected void closeScreen() {
        this.minecraft.player.closeContainer();
    }
}