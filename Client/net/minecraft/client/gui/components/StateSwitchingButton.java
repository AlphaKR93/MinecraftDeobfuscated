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
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;

public class StateSwitchingButton
extends AbstractWidget {
    protected ResourceLocation resourceLocation;
    protected boolean isStateTriggered;
    protected int xTexStart;
    protected int yTexStart;
    protected int xDiffTex;
    protected int yDiffTex;

    public StateSwitchingButton(int $$0, int $$1, int $$2, int $$3, boolean $$4) {
        super($$0, $$1, $$2, $$3, CommonComponents.EMPTY);
        this.isStateTriggered = $$4;
    }

    public void initTextureValues(int $$0, int $$1, int $$2, int $$3, ResourceLocation $$4) {
        this.xTexStart = $$0;
        this.yTexStart = $$1;
        this.xDiffTex = $$2;
        this.yDiffTex = $$3;
        this.resourceLocation = $$4;
    }

    public void setStateTriggered(boolean $$0) {
        this.isStateTriggered = $$0;
    }

    public boolean isStateTriggered() {
        return this.isStateTriggered;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput $$0) {
        this.defaultButtonNarrationText($$0);
    }

    @Override
    public void renderWidget(PoseStack $$0, int $$1, int $$2, float $$3) {
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, this.resourceLocation);
        RenderSystem.disableDepthTest();
        int $$4 = this.xTexStart;
        int $$5 = this.yTexStart;
        if (this.isStateTriggered) {
            $$4 += this.xDiffTex;
        }
        if (this.isHoveredOrFocused()) {
            $$5 += this.yDiffTex;
        }
        this.blit($$0, this.getX(), this.getY(), $$4, $$5, this.width, this.height);
        RenderSystem.enableDepthTest();
    }
}