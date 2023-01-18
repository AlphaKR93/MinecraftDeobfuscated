/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class GenericDirtMessageScreen
extends Screen {
    public GenericDirtMessageScreen(Component $$0) {
        super($$0);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderDirtBackground($$0);
        GenericDirtMessageScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 70, 0xFFFFFF);
        super.render($$0, $$1, $$2, $$3);
    }
}