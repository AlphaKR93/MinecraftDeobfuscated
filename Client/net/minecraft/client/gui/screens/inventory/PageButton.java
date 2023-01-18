/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Supplier
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.sounds.SoundEvents;

public class PageButton
extends Button {
    private final boolean isForward;
    private final boolean playTurnSound;

    public PageButton(int $$0, int $$1, boolean $$2, Button.OnPress $$3, boolean $$4) {
        super($$0, $$1, 23, 13, CommonComponents.EMPTY, $$3, DEFAULT_NARRATION);
        this.isForward = $$2;
        this.playTurnSound = $$4;
    }

    @Override
    public void renderButton(PoseStack $$0, int $$1, int $$2, float $$3) {
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, BookViewScreen.BOOK_LOCATION);
        int $$4 = 0;
        int $$5 = 192;
        if (this.isHoveredOrFocused()) {
            $$4 += 23;
        }
        if (!this.isForward) {
            $$5 += 13;
        }
        this.blit($$0, this.getX(), this.getY(), $$4, $$5, 23, 13);
    }

    @Override
    public void playDownSound(SoundManager $$0) {
        if (this.playTurnSound) {
            $$0.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0f));
        }
    }
}