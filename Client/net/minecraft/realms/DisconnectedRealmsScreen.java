/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Objects
 */
package net.minecraft.realms;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.realms.RealmsScreen;

public class DisconnectedRealmsScreen
extends RealmsScreen {
    private final Component reason;
    private MultiLineLabel message = MultiLineLabel.EMPTY;
    private final Screen parent;
    private int textHeight;

    public DisconnectedRealmsScreen(Screen $$0, Component $$1, Component $$2) {
        super($$1);
        this.parent = $$0;
        this.reason = $$2;
    }

    @Override
    public void init() {
        Minecraft $$0 = Minecraft.getInstance();
        $$0.setConnectedToRealms(false);
        $$0.getDownloadedPackSource().clearServerPack();
        this.message = MultiLineLabel.create(this.font, (FormattedText)this.reason, this.width - 50);
        int n = this.message.getLineCount();
        Objects.requireNonNull((Object)this.font);
        this.textHeight = n * 9;
        Button.Builder builder = Button.builder(CommonComponents.GUI_BACK, $$1 -> $$0.setScreen(this.parent));
        int n2 = this.width / 2 - 100;
        int n3 = this.height / 2 + this.textHeight / 2;
        Objects.requireNonNull((Object)this.font);
        this.addRenderableWidget(builder.bounds(n2, n3 + 9, 200, 20).build());
    }

    @Override
    public Component getNarrationMessage() {
        return Component.empty().append(this.title).append(": ").append(this.reason);
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(this.parent);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        int n = this.width / 2;
        int n2 = this.height / 2 - this.textHeight / 2;
        Objects.requireNonNull((Object)this.font);
        DisconnectedRealmsScreen.drawCenteredString($$0, this.font, this.title, n, n2 - 9 * 2, 0xAAAAAA);
        this.message.renderCentered($$0, this.width / 2, this.height / 2 - this.textHeight / 2);
        super.render($$0, $$1, $$2, $$3);
    }
}