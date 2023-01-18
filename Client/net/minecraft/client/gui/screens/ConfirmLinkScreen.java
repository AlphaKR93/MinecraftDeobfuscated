/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ConfirmLinkScreen
extends ConfirmScreen {
    private static final Component COPY_BUTTON_TEXT = Component.translatable("chat.copy");
    private static final Component WARNING_TEXT = Component.translatable("chat.link.warning");
    private final String url;
    private final boolean showWarning;

    public ConfirmLinkScreen(BooleanConsumer $$0, String $$1, boolean $$2) {
        this($$0, ConfirmLinkScreen.confirmMessage($$2), Component.literal($$1), $$1, $$2 ? CommonComponents.GUI_CANCEL : CommonComponents.GUI_NO, $$2);
    }

    public ConfirmLinkScreen(BooleanConsumer $$0, Component $$1, String $$2, boolean $$3) {
        this($$0, $$1, $$2, $$3 ? CommonComponents.GUI_CANCEL : CommonComponents.GUI_NO, $$3);
    }

    public ConfirmLinkScreen(BooleanConsumer $$0, Component $$1, String $$2, Component $$3, boolean $$4) {
        this($$0, $$1, ConfirmLinkScreen.confirmMessage($$4, $$2), $$2, $$3, $$4);
    }

    public ConfirmLinkScreen(BooleanConsumer $$0, Component $$1, Component $$2, String $$3, Component $$4, boolean $$5) {
        super($$0, $$1, $$2);
        this.yesButton = $$5 ? Component.translatable("chat.link.open") : CommonComponents.GUI_YES;
        this.noButton = $$4;
        this.showWarning = !$$5;
        this.url = $$3;
    }

    protected static MutableComponent confirmMessage(boolean $$0, String $$1) {
        return ConfirmLinkScreen.confirmMessage($$0).append(CommonComponents.SPACE).append(Component.literal($$1));
    }

    protected static MutableComponent confirmMessage(boolean $$0) {
        return Component.translatable($$0 ? "chat.link.confirmTrusted" : "chat.link.confirm");
    }

    @Override
    protected void addButtons(int $$02) {
        this.addRenderableWidget(Button.builder(this.yesButton, $$0 -> this.callback.accept(true)).bounds(this.width / 2 - 50 - 105, $$02, 100, 20).build());
        this.addRenderableWidget(Button.builder(COPY_BUTTON_TEXT, $$0 -> {
            this.copyToClipboard();
            this.callback.accept(false);
        }).bounds(this.width / 2 - 50, $$02, 100, 20).build());
        this.addRenderableWidget(Button.builder(this.noButton, $$0 -> this.callback.accept(false)).bounds(this.width / 2 - 50 + 105, $$02, 100, 20).build());
    }

    public void copyToClipboard() {
        this.minecraft.keyboardHandler.setClipboard(this.url);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        if (this.showWarning) {
            ConfirmLinkScreen.drawCenteredString($$0, this.font, WARNING_TEXT, this.width / 2, 110, 0xFFCCCC);
        }
    }
}