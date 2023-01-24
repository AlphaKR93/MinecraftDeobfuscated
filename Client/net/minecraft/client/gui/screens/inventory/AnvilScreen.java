/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.function.Consumer
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class AnvilScreen
extends ItemCombinerScreen<AnvilMenu> {
    private static final ResourceLocation ANVIL_LOCATION = new ResourceLocation("textures/gui/container/anvil.png");
    private static final Component TOO_EXPENSIVE_TEXT = Component.translatable("container.repair.expensive");
    private EditBox name;
    private final Player player;

    public AnvilScreen(AnvilMenu $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2, ANVIL_LOCATION);
        this.player = $$1.player;
        this.titleLabelX = 60;
    }

    @Override
    public void containerTick() {
        super.containerTick();
        this.name.tick();
    }

    @Override
    protected void subInit() {
        int $$0 = (this.width - this.imageWidth) / 2;
        int $$1 = (this.height - this.imageHeight) / 2;
        this.name = new EditBox(this.font, $$0 + 62, $$1 + 24, 103, 12, Component.translatable("container.repair"));
        this.name.setCanLoseFocus(false);
        this.name.setTextColor(-1);
        this.name.setTextColorUneditable(-1);
        this.name.setBordered(false);
        this.name.setMaxLength(50);
        this.name.setResponder((Consumer<String>)((Consumer)this::onNameChanged));
        this.name.setValue("");
        this.addWidget(this.name);
        this.setInitialFocus(this.name);
        this.name.setEditable(false);
    }

    @Override
    public void resize(Minecraft $$0, int $$1, int $$2) {
        String $$3 = this.name.getValue();
        this.init($$0, $$1, $$2);
        this.name.setValue($$3);
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 256) {
            this.minecraft.player.closeContainer();
        }
        if (this.name.keyPressed($$0, $$1, $$2) || this.name.canConsumeInput()) {
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    private void onNameChanged(String $$0) {
        if ($$0.isEmpty()) {
            return;
        }
        String $$1 = $$0;
        Slot $$2 = ((AnvilMenu)this.menu).getSlot(0);
        if ($$2 != null && $$2.hasItem() && !$$2.getItem().hasCustomHoverName() && $$1.equals((Object)$$2.getItem().getHoverName().getString())) {
            $$1 = "";
        }
        ((AnvilMenu)this.menu).setItemName($$1);
        this.minecraft.player.connection.send(new ServerboundRenameItemPacket($$1));
    }

    @Override
    protected void renderLabels(PoseStack $$0, int $$1, int $$2) {
        RenderSystem.disableBlend();
        super.renderLabels($$0, $$1, $$2);
        int $$3 = ((AnvilMenu)this.menu).getCost();
        if ($$3 > 0) {
            MutableComponent $$7;
            int $$4 = 8453920;
            if ($$3 >= 40 && !this.minecraft.player.getAbilities().instabuild) {
                Component $$5 = TOO_EXPENSIVE_TEXT;
                $$4 = 0xFF6060;
            } else if (!((AnvilMenu)this.menu).getSlot(2).hasItem()) {
                Object $$6 = null;
            } else {
                $$7 = Component.translatable("container.repair.cost", $$3);
                if (!((AnvilMenu)this.menu).getSlot(2).mayPickup(this.player)) {
                    $$4 = 0xFF6060;
                }
            }
            if ($$7 != null) {
                int $$8 = this.imageWidth - 8 - this.font.width($$7) - 2;
                int $$9 = 69;
                AnvilScreen.fill($$0, $$8 - 2, 67, this.imageWidth - 8, 79, 0x4F000000);
                this.font.drawShadow($$0, $$7, (float)$$8, 69.0f, $$4);
            }
        }
    }

    @Override
    protected void renderBg(PoseStack $$0, float $$1, int $$2, int $$3) {
        super.renderBg($$0, $$1, $$2, $$3);
        this.blit($$0, this.leftPos + 59, this.topPos + 20, 0, this.imageHeight + (((AnvilMenu)this.menu).getSlot(0).hasItem() ? 0 : 16), 110, 16);
    }

    @Override
    public void renderFg(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.name.render($$0, $$1, $$2, $$3);
    }

    @Override
    protected void renderErrorIcon(PoseStack $$0, int $$1, int $$2) {
        if ((((AnvilMenu)this.menu).getSlot(0).hasItem() || ((AnvilMenu)this.menu).getSlot(1).hasItem()) && !((AnvilMenu)this.menu).getSlot(((AnvilMenu)this.menu).getResultSlot()).hasItem()) {
            this.blit($$0, $$1 + 99, $$2 + 45, this.imageWidth, 0, 28, 21);
        }
    }

    @Override
    public void slotChanged(AbstractContainerMenu $$0, int $$1, ItemStack $$2) {
        if ($$1 == 0) {
            this.name.setValue($$2.isEmpty() ? "" : $$2.getHoverName().getString());
            this.name.setEditable(!$$2.isEmpty());
            this.setFocused(this.name);
        }
    }
}