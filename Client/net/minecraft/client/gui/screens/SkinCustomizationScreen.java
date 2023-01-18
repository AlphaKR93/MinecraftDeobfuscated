/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.PlayerModelPart;

public class SkinCustomizationScreen
extends OptionsSubScreen {
    public SkinCustomizationScreen(Screen $$0, Options $$1) {
        super($$0, $$1, Component.translatable("options.skinCustomisation.title"));
    }

    @Override
    protected void init() {
        int $$02 = 0;
        for (PlayerModelPart $$12 : PlayerModelPart.values()) {
            this.addRenderableWidget(CycleButton.onOffBuilder(this.options.isModelPartEnabled($$12)).create(this.width / 2 - 155 + $$02 % 2 * 160, this.height / 6 + 24 * ($$02 >> 1), 150, 20, $$12.getName(), ($$1, $$2) -> this.options.toggleModelPart($$12, (boolean)$$2)));
            ++$$02;
        }
        this.addRenderableWidget(this.options.mainHand().createButton(this.options, this.width / 2 - 155 + $$02 % 2 * 160, this.height / 6 + 24 * ($$02 >> 1), 150));
        if (++$$02 % 2 == 1) {
            ++$$02;
        }
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.minecraft.setScreen(this.lastScreen)).bounds(this.width / 2 - 100, this.height / 6 + 24 * ($$02 >> 1), 200, 20).build());
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        SkinCustomizationScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render($$0, $$1, $$2, $$3);
    }
}