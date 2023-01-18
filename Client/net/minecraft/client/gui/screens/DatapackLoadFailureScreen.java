/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

public class DatapackLoadFailureScreen
extends Screen {
    private MultiLineLabel message = MultiLineLabel.EMPTY;
    private final Runnable callback;

    public DatapackLoadFailureScreen(Runnable $$0) {
        super(Component.translatable("datapackFailure.title"));
        this.callback = $$0;
    }

    @Override
    protected void init() {
        super.init();
        this.message = MultiLineLabel.create(this.font, (FormattedText)this.getTitle(), this.width - 50);
        this.addRenderableWidget(Button.builder(Component.translatable("datapackFailure.safeMode"), $$0 -> this.callback.run()).bounds(this.width / 2 - 155, this.height / 6 + 96, 150, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("gui.toTitle"), $$0 -> this.minecraft.setScreen(null)).bounds(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20).build());
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        this.message.renderCentered($$0, this.width / 2, 70);
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}