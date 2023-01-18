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
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class EditServerScreen
extends Screen {
    private static final Component NAME_LABEL = Component.translatable("addServer.enterName");
    private static final Component IP_LABEL = Component.translatable("addServer.enterIp");
    private Button addButton;
    private final BooleanConsumer callback;
    private final ServerData serverData;
    private EditBox ipEdit;
    private EditBox nameEdit;
    private final Screen lastScreen;

    public EditServerScreen(Screen $$0, BooleanConsumer $$1, ServerData $$2) {
        super(Component.translatable("addServer.title"));
        this.lastScreen = $$0;
        this.callback = $$1;
        this.serverData = $$2;
    }

    @Override
    public void tick() {
        this.nameEdit.tick();
        this.ipEdit.tick();
    }

    @Override
    protected void init() {
        this.nameEdit = new EditBox(this.font, this.width / 2 - 100, 66, 200, 20, Component.translatable("addServer.enterName"));
        this.nameEdit.setValue(this.serverData.name);
        this.nameEdit.setResponder((Consumer<String>)((Consumer)$$0 -> this.updateAddButtonStatus()));
        this.addWidget(this.nameEdit);
        this.ipEdit = new EditBox(this.font, this.width / 2 - 100, 106, 200, 20, Component.translatable("addServer.enterIp"));
        this.ipEdit.setMaxLength(128);
        this.ipEdit.setValue(this.serverData.ip);
        this.ipEdit.setResponder((Consumer<String>)((Consumer)$$0 -> this.updateAddButtonStatus()));
        this.addWidget(this.ipEdit);
        this.addRenderableWidget(CycleButton.builder(ServerData.ServerPackStatus::getName).withValues(ServerData.ServerPackStatus.values()).withInitialValue(this.serverData.getResourcePackStatus()).create(this.width / 2 - 100, this.height / 4 + 72, 200, 20, Component.translatable("addServer.resourcePack"), ($$0, $$1) -> this.serverData.setResourcePackStatus((ServerData.ServerPackStatus)((Object)$$1))));
        this.addButton = this.addRenderableWidget(Button.builder(Component.translatable("addServer.add"), $$0 -> this.onAdd()).bounds(this.width / 2 - 100, this.height / 4 + 96 + 18, 200, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.callback.accept(false)).bounds(this.width / 2 - 100, this.height / 4 + 120 + 18, 200, 20).build());
        this.setInitialFocus(this.nameEdit);
        this.updateAddButtonStatus();
    }

    @Override
    public void resize(Minecraft $$0, int $$1, int $$2) {
        String $$3 = this.ipEdit.getValue();
        String $$4 = this.nameEdit.getValue();
        this.init($$0, $$1, $$2);
        this.ipEdit.setValue($$3);
        this.nameEdit.setValue($$4);
    }

    private void onAdd() {
        this.serverData.name = this.nameEdit.getValue();
        this.serverData.ip = this.ipEdit.getValue();
        this.callback.accept(true);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }

    private void updateAddButtonStatus() {
        this.addButton.active = ServerAddress.isValidAddress(this.ipEdit.getValue()) && !this.nameEdit.getValue().isEmpty();
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        EditServerScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 17, 0xFFFFFF);
        EditServerScreen.drawString($$0, this.font, NAME_LABEL, this.width / 2 - 100, 53, 0xA0A0A0);
        EditServerScreen.drawString($$0, this.font, IP_LABEL, this.width / 2 - 100, 94, 0xA0A0A0);
        this.nameEdit.render($$0, $$1, $$2, $$3);
        this.ipEdit.render($$0, $$1, $$2, $$3);
        super.render($$0, $$1, $$2, $$3);
    }
}