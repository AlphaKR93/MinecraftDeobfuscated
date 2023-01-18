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
import net.minecraft.resources.ResourceLocation;

public class ImageButton
extends Button {
    private final ResourceLocation resourceLocation;
    private final int xTexStart;
    private final int yTexStart;
    private final int yDiffTex;
    private final int textureWidth;
    private final int textureHeight;

    public ImageButton(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5, ResourceLocation $$6, Button.OnPress $$7) {
        this($$0, $$1, $$2, $$3, $$4, $$5, $$3, $$6, 256, 256, $$7);
    }

    public ImageButton(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, ResourceLocation $$7, Button.OnPress $$8) {
        this($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, 256, 256, $$8);
    }

    public ImageButton(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, ResourceLocation $$7, int $$8, int $$9, Button.OnPress $$10) {
        this($$0, $$1, $$2, $$3, $$4, $$5, $$6, $$7, $$8, $$9, $$10, CommonComponents.EMPTY);
    }

    public ImageButton(int $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, ResourceLocation $$7, int $$8, int $$9, Button.OnPress $$10, Component $$11) {
        super($$0, $$1, $$2, $$3, $$11, $$10, DEFAULT_NARRATION);
        this.textureWidth = $$8;
        this.textureHeight = $$9;
        this.xTexStart = $$4;
        this.yTexStart = $$5;
        this.yDiffTex = $$6;
        this.resourceLocation = $$7;
    }

    @Override
    public void renderButton(PoseStack $$0, int $$1, int $$2, float $$3) {
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, this.resourceLocation);
        int $$4 = this.yTexStart;
        if (!this.isActive()) {
            $$4 += this.yDiffTex * 2;
        } else if (this.isHoveredOrFocused()) {
            $$4 += this.yDiffTex;
        }
        RenderSystem.enableDepthTest();
        ImageButton.blit($$0, this.getX(), this.getY(), this.xTexStart, $$4, this.width, this.height, this.textureWidth, this.textureHeight);
    }
}