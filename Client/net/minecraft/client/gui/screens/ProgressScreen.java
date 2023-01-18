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
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ProgressListener;

public class ProgressScreen
extends Screen
implements ProgressListener {
    @Nullable
    private Component header;
    @Nullable
    private Component stage;
    private int progress;
    private boolean stop;
    private final boolean clearScreenAfterStop;

    public ProgressScreen(boolean $$0) {
        super(GameNarrator.NO_TITLE);
        this.clearScreenAfterStop = $$0;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void progressStartNoAbort(Component $$0) {
        this.progressStart($$0);
    }

    @Override
    public void progressStart(Component $$0) {
        this.header = $$0;
        this.progressStage(Component.translatable("progress.working"));
    }

    @Override
    public void progressStage(Component $$0) {
        this.stage = $$0;
        this.progressStagePercentage(0);
    }

    @Override
    public void progressStagePercentage(int $$0) {
        this.progress = $$0;
    }

    @Override
    public void stop() {
        this.stop = true;
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        if (this.stop) {
            if (this.clearScreenAfterStop) {
                this.minecraft.setScreen(null);
            }
            return;
        }
        this.renderBackground($$0);
        if (this.header != null) {
            ProgressScreen.drawCenteredString($$0, this.font, this.header, this.width / 2, 70, 0xFFFFFF);
        }
        if (this.stage != null && this.progress != 0) {
            ProgressScreen.drawCenteredString($$0, this.font, Component.empty().append(this.stage).append(" " + this.progress + "%"), this.width / 2, 90, 0xFFFFFF);
        }
        super.render($$0, $$1, $$2, $$3);
    }
}