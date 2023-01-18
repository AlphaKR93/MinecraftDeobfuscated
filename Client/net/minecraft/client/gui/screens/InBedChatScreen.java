/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;

public class InBedChatScreen
extends ChatScreen {
    private Button leaveBedButton;

    public InBedChatScreen() {
        super("");
    }

    @Override
    protected void init() {
        super.init();
        this.leaveBedButton = Button.builder(Component.translatable("multiplayer.stopSleeping"), $$0 -> this.sendWakeUp()).bounds(this.width / 2 - 100, this.height - 40, 200, 20).build();
        this.addRenderableWidget(this.leaveBedButton);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        if (!this.minecraft.getChatStatus().isChatAllowed(this.minecraft.isLocalServer())) {
            this.leaveBedButton.render($$0, $$1, $$2, $$3);
            return;
        }
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    public void onClose() {
        this.sendWakeUp();
    }

    @Override
    public boolean charTyped(char $$0, int $$1) {
        if (!this.minecraft.getChatStatus().isChatAllowed(this.minecraft.isLocalServer())) {
            return true;
        }
        return super.charTyped($$0, $$1);
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 256) {
            this.sendWakeUp();
        }
        if (!this.minecraft.getChatStatus().isChatAllowed(this.minecraft.isLocalServer())) {
            return true;
        }
        if ($$0 == 257 || $$0 == 335) {
            if (this.handleChatInput(this.input.getValue(), true)) {
                this.minecraft.setScreen(null);
                this.input.setValue("");
                this.minecraft.gui.getChat().resetChatScroll();
            }
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    private void sendWakeUp() {
        ClientPacketListener $$0 = this.minecraft.player.connection;
        $$0.send(new ServerboundPlayerCommandPacket(this.minecraft.player, ServerboundPlayerCommandPacket.Action.STOP_SLEEPING));
    }

    public void onPlayerWokeUp() {
        if (this.input.getValue().isEmpty()) {
            this.minecraft.setScreen(null);
        } else {
            this.minecraft.setScreen(new ChatScreen(this.input.getValue()));
        }
    }
}