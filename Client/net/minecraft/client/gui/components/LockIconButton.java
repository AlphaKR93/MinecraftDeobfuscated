/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Supplier
 */
package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class LockIconButton
extends Button {
    private boolean locked;

    public LockIconButton(int $$0, int $$1, Button.OnPress $$2) {
        super($$0, $$1, 20, 20, Component.translatable("narrator.button.difficulty_lock"), $$2, DEFAULT_NARRATION);
    }

    @Override
    protected MutableComponent createNarrationMessage() {
        return CommonComponents.joinForNarration(super.createNarrationMessage(), this.isLocked() ? Component.translatable("narrator.button.difficulty_lock.locked") : Component.translatable("narrator.button.difficulty_lock.unlocked"));
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(boolean $$0) {
        this.locked = $$0;
    }

    @Override
    public void renderButton(PoseStack $$0, int $$1, int $$2, float $$3) {
        Icon $$6;
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, Button.WIDGETS_LOCATION);
        if (!this.active) {
            Icon $$4 = this.locked ? Icon.LOCKED_DISABLED : Icon.UNLOCKED_DISABLED;
        } else if (this.isHoveredOrFocused()) {
            Icon $$5 = this.locked ? Icon.LOCKED_HOVER : Icon.UNLOCKED_HOVER;
        } else {
            $$6 = this.locked ? Icon.LOCKED : Icon.UNLOCKED;
        }
        this.blit($$0, this.getX(), this.getY(), $$6.getX(), $$6.getY(), this.width, this.height);
    }

    static enum Icon {
        LOCKED(0, 146),
        LOCKED_HOVER(0, 166),
        LOCKED_DISABLED(0, 186),
        UNLOCKED(20, 146),
        UNLOCKED_HOVER(20, 166),
        UNLOCKED_DISABLED(20, 186);

        private final int x;
        private final int y;

        private Icon(int $$0, int $$1) {
            this.x = $$0;
            this.y = $$1;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }
    }
}