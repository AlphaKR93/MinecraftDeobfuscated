/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.System
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class ReceivingLevelScreen
extends Screen {
    private static final Component DOWNLOADING_TERRAIN_TEXT = Component.translatable("multiplayer.downloadingTerrain");
    private static final long CHUNK_LOADING_START_WAIT_LIMIT_MS = 30000L;
    private boolean loadingPacketsReceived = false;
    private boolean oneTickSkipped = false;
    private final long createdAt = System.currentTimeMillis();

    public ReceivingLevelScreen() {
        super(GameNarrator.NO_TITLE);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderDirtBackground(0);
        ReceivingLevelScreen.drawCenteredString($$0, this.font, DOWNLOADING_TERRAIN_TEXT, this.width / 2, this.height / 2 - 50, 0xFFFFFF);
        super.render($$0, $$1, $$2, $$3);
    }

    @Override
    public void tick() {
        if (System.currentTimeMillis() > this.createdAt + 30000L) {
            this.onClose();
            return;
        }
        if (this.oneTickSkipped) {
            boolean $$1;
            if (this.minecraft.player == null) {
                return;
            }
            BlockPos $$0 = this.minecraft.player.blockPosition();
            boolean bl = $$1 = this.minecraft.level != null && this.minecraft.level.isOutsideBuildHeight($$0.getY());
            if ($$1 || this.minecraft.levelRenderer.isChunkCompiled($$0) || this.minecraft.player.isSpectator() || !this.minecraft.player.isAlive()) {
                this.onClose();
            }
        } else {
            this.oneTickSkipped = this.loadingPacketsReceived;
        }
    }

    public void loadingPacketsReceived() {
        this.loadingPacketsReceived = true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}