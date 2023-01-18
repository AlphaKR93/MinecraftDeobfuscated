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
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class OptionsSubScreen
extends Screen {
    protected final Screen lastScreen;
    protected final Options options;

    public OptionsSubScreen(Screen $$0, Options $$1, Component $$2) {
        super($$2);
        this.lastScreen = $$0;
        this.options = $$1;
    }

    @Override
    public void removed() {
        this.minecraft.options.save();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    protected void basicListRender(PoseStack $$0, OptionsList $$1, int $$2, int $$3, float $$4) {
        this.renderBackground($$0);
        $$1.render($$0, $$2, $$3, $$4);
        OptionsSubScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        super.render($$0, $$2, $$3, $$4);
    }
}