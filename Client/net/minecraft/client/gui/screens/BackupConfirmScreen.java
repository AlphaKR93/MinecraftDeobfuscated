/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Objects
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;

public class BackupConfirmScreen
extends Screen {
    private final Screen lastScreen;
    protected final Listener listener;
    private final Component description;
    private final boolean promptForCacheErase;
    private MultiLineLabel message = MultiLineLabel.EMPTY;
    protected int id;
    private Checkbox eraseCache;

    public BackupConfirmScreen(Screen $$0, Listener $$1, Component $$2, Component $$3, boolean $$4) {
        super($$2);
        this.lastScreen = $$0;
        this.listener = $$1;
        this.description = $$3;
        this.promptForCacheErase = $$4;
    }

    @Override
    protected void init() {
        super.init();
        this.message = MultiLineLabel.create(this.font, (FormattedText)this.description, this.width - 50);
        int n = this.message.getLineCount() + 1;
        Objects.requireNonNull((Object)this.font);
        int $$02 = n * 9;
        this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.backupJoinConfirmButton"), $$0 -> this.listener.proceed(true, this.eraseCache.selected())).bounds(this.width / 2 - 155, 100 + $$02, 150, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("selectWorld.backupJoinSkipButton"), $$0 -> this.listener.proceed(false, this.eraseCache.selected())).bounds(this.width / 2 - 155 + 160, 100 + $$02, 150, 20).build());
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.minecraft.setScreen(this.lastScreen)).bounds(this.width / 2 - 155 + 80, 124 + $$02, 150, 20).build());
        this.eraseCache = new Checkbox(this.width / 2 - 155 + 80, 76 + $$02, 150, 20, Component.translatable("selectWorld.backupEraseCache"), false);
        if (this.promptForCacheErase) {
            this.addRenderableWidget(this.eraseCache);
        }
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        BackupConfirmScreen.drawCenteredString($$0, this.font, this.title, this.width / 2, 50, 0xFFFFFF);
        this.message.renderCentered($$0, this.width / 2, 70);
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 256) {
            this.minecraft.setScreen(this.lastScreen);
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    public static interface Listener {
        public void proceed(boolean var1, boolean var2);
    }
}