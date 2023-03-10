/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public abstract class SimpleOptionsSubScreen
extends OptionsSubScreen {
    protected final OptionInstance<?>[] smallOptions;
    @Nullable
    private AbstractWidget narratorButton;
    protected OptionsList list;

    public SimpleOptionsSubScreen(Screen $$0, Options $$1, Component $$2, OptionInstance<?>[] $$3) {
        super($$0, $$1, $$2);
        this.smallOptions = $$3;
    }

    @Override
    protected void init() {
        this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.list.addSmall(this.smallOptions);
        this.addWidget(this.list);
        this.createFooter();
        this.narratorButton = this.list.findOption(this.options.narrator());
        if (this.narratorButton != null) {
            this.narratorButton.active = this.minecraft.getNarrator().isActive();
        }
    }

    protected void createFooter() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, $$0 -> this.minecraft.setScreen(this.lastScreen)).bounds(this.width / 2 - 100, this.height - 27, 200, 20).build());
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.basicListRender($$0, this.list, $$1, $$2, $$3);
    }

    public void updateNarratorButton() {
        if (this.narratorButton instanceof CycleButton) {
            ((CycleButton)this.narratorButton).setValue(this.options.narrator().get());
        }
    }
}