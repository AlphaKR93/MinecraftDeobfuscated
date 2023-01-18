/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.function.Consumer
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class DirectJoinServerScreen
extends Screen {
    private static final Component ENTER_IP_LABEL = Component.translatable("addServer.enterIp");
    private Button selectButton;
    private final ServerData serverData;
    private EditBox ipEdit;
    private final BooleanConsumer callback;
    private final Screen lastScreen;

    public DirectJoinServerScreen(Screen $$0, BooleanConsumer $$1, ServerData $$2) {
        super(Component.translatable("selectServer.direct"));
        this.lastScreen = $$0;
        this.serverData = $$2;
        this.callback = $$1;
    }

    @Override
    public void tick() {
        this.ipEdit.tick();
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (this.selectButton.active && this.getFocused() == this.ipEdit && ($$0 == 257 || $$0 == 335)) {
            this.onSelect();
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    @Override
    protected void init() {
        this.ipEdit = new EditBox(this.font, this.width / 2 - 100, 116, 200, 20, Component.translatable("addServer.enterIp"));
        this.ipEdit.setMaxLength(128);
        this.ipEdit.setValue(this.minecraft.options.lastMpIp);
        this.ipEdit.setResponder((Consumer<String>)((Consumer)$$0 -> this.updateSelectButtonStatus()));
        this.addWidget(this.ipEdit);
        this.selectButton = this.addRenderableWidget(Button.builder(Component.translatable("selectServer.select"), $$0 -> this.onSelect()).bounds(this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.callback.accept(false)).bounds(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20).build());
        this.setInitialFocus(this.ipEdit);
        this.updateSelectButtonStatus();
    }

    @Override
    public void resize(Minecraft $$0, int $$1, int $$2) {
        String $$3 = this.ipEdit.getValue();
        this.init($$0, $$1, $$2);
        this.ipEdit.setValue($$3);
    }

    private void onSelect() {
        this.serverData.ip = this.ipEdit.getValue();
        this.callback.accept(true);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    public void removed() {
        this.minecraft.options.lastMpIp = this.ipEdit.getValue();
        this.minecraft.options.save();
    }

    private void updateSelectButtonStatus() {
        this.selectButton.active = ServerAddress.isValidAddress(this.ipEdit.getValue());
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        DirectJoinServerScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        DirectJoinServerScreen.drawString($$0, this.font, ENTER_IP_LABEL, this.width / 2 - 100, 100, 0xA0A0A0);
        this.ipEdit.render($$0, $$1, $$2, $$3);
        super.render($$0, $$1, $$2, $$3);
    }
}