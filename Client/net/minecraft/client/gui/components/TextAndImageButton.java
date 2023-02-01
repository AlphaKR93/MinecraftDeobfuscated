/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 */
package net.minecraft.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public class TextAndImageButton
extends ImageButton {
    private static final int TEXT_OVERFLOW_PADDING = 5;
    private final int xOffset;
    private final int yOffset;
    private final int usedTextureWidth;
    private final int usedTextureHeight;

    TextAndImageButton(Component $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, int $$8, int $$9, ResourceLocation $$10, Button.OnPress $$11) {
        super(0, 0, 150, 20, $$1, $$2, $$5, $$10, $$8, $$9, $$11, $$0);
        this.xOffset = $$3;
        this.yOffset = $$4;
        this.usedTextureWidth = $$6;
        this.usedTextureHeight = $$7;
    }

    @Override
    public void renderWidget(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderButton($$0, $$1, $$2);
        this.renderTexture($$0, this.resourceLocation, this.getXOffset(), this.getYOffset(), this.xTexStart, this.yTexStart, this.yDiffTex, this.usedTextureWidth, this.usedTextureHeight, this.textureWidth, this.textureHeight);
    }

    @Override
    public void renderString(PoseStack $$0, Font $$1, int $$2, int $$3, int $$4) {
        int $$9;
        FormattedCharSequence $$5 = this.getMessage().getVisualOrderText();
        int $$6 = $$1.width($$5);
        int $$7 = $$2 - $$6 / 2;
        int $$8 = $$7 + $$6;
        if ($$8 >= ($$9 = this.getX() + this.width - this.usedTextureWidth - 5)) {
            $$7 -= $$8 - $$9;
        }
        TextAndImageButton.drawString($$0, $$1, $$5, $$7, $$3, $$4);
    }

    private int getXOffset() {
        return this.getX() + (this.width / 2 - this.usedTextureWidth / 2) + this.xOffset;
    }

    private int getYOffset() {
        return this.getY() + this.yOffset;
    }

    public static Builder builder(Component $$0, ResourceLocation $$1, Button.OnPress $$2) {
        return new Builder($$0, $$1, $$2);
    }

    public static class Builder {
        private final Component message;
        private final ResourceLocation resourceLocation;
        private final Button.OnPress onPress;
        private int xTexStart;
        private int yTexStart;
        private int yDiffTex;
        private int usedTextureWidth;
        private int usedTextureHeight;
        private int textureWidth;
        private int textureHeight;
        private int xOffset;
        private int yOffset;

        public Builder(Component $$0, ResourceLocation $$1, Button.OnPress $$2) {
            this.message = $$0;
            this.resourceLocation = $$1;
            this.onPress = $$2;
        }

        public Builder texStart(int $$0, int $$1) {
            this.xTexStart = $$0;
            this.yTexStart = $$1;
            return this;
        }

        public Builder offset(int $$0, int $$1) {
            this.xOffset = $$0;
            this.yOffset = $$1;
            return this;
        }

        public Builder yDiffTex(int $$0) {
            this.yDiffTex = $$0;
            return this;
        }

        public Builder usedTextureSize(int $$0, int $$1) {
            this.usedTextureWidth = $$0;
            this.usedTextureHeight = $$1;
            return this;
        }

        public Builder textureSize(int $$0, int $$1) {
            this.textureWidth = $$0;
            this.textureHeight = $$1;
            return this;
        }

        public TextAndImageButton build() {
            return new TextAndImageButton(this.message, this.xTexStart, this.yTexStart, this.xOffset, this.yOffset, this.yDiffTex, this.usedTextureWidth, this.usedTextureHeight, this.textureWidth, this.textureHeight, this.resourceLocation, this.onPress);
        }
    }
}