/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Character
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Objects
 *  java.util.function.Consumer
 *  java.util.function.Supplier
 *  org.joml.Matrix4f
 */
package net.minecraft.client.gui.components;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.components.MultilineTextField;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.joml.Matrix4f;

public class MultiLineEditBox
extends AbstractScrollWidget {
    private static final int CURSOR_INSERT_WIDTH = 1;
    private static final int CURSOR_INSERT_COLOR = -3092272;
    private static final String CURSOR_APPEND_CHARACTER = "_";
    private static final int TEXT_COLOR = -2039584;
    private static final int PLACEHOLDER_TEXT_COLOR = -857677600;
    private final Font font;
    private final Component placeholder;
    private final MultilineTextField textField;
    private int frame;

    public MultiLineEditBox(Font $$0, int $$1, int $$2, int $$3, int $$4, Component $$5, Component $$6) {
        super($$1, $$2, $$3, $$4, $$6);
        this.font = $$0;
        this.placeholder = $$5;
        this.textField = new MultilineTextField($$0, $$3 - this.totalInnerPadding());
        this.textField.setCursorListener(this::scrollToCursor);
    }

    public void setCharacterLimit(int $$0) {
        this.textField.setCharacterLimit($$0);
    }

    public void setValueListener(Consumer<String> $$0) {
        this.textField.setValueListener($$0);
    }

    public void setValue(String $$0) {
        this.textField.setValue($$0);
    }

    public String getValue() {
        return this.textField.value();
    }

    public void tick() {
        ++this.frame;
    }

    @Override
    public void updateWidgetNarration(NarrationElementOutput $$0) {
        $$0.add(NarratedElementType.TITLE, (Component)Component.translatable("gui.narrate.editBox", this.getMessage(), this.getValue()));
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        if (super.mouseClicked($$0, $$1, $$2)) {
            return true;
        }
        if (this.withinContentAreaPoint($$0, $$1) && $$2 == 0) {
            this.textField.setSelecting(Screen.hasShiftDown());
            this.seekCursorScreen($$0, $$1);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double $$0, double $$1, int $$2, double $$3, double $$4) {
        if (super.mouseDragged($$0, $$1, $$2, $$3, $$4)) {
            return true;
        }
        if (this.withinContentAreaPoint($$0, $$1) && $$2 == 0) {
            this.textField.setSelecting(true);
            this.seekCursorScreen($$0, $$1);
            this.textField.setSelecting(Screen.hasShiftDown());
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        return this.textField.keyPressed($$0);
    }

    @Override
    public boolean charTyped(char $$0, int $$1) {
        if (!(this.visible && this.isFocused() && SharedConstants.isAllowedChatCharacter($$0))) {
            return false;
        }
        this.textField.insertText(Character.toString((char)$$0));
        return true;
    }

    @Override
    protected void renderContents(PoseStack $$0, int $$1, int $$2, float $$3) {
        String $$4 = this.textField.value();
        if ($$4.isEmpty() && !this.isFocused()) {
            this.font.drawWordWrap(this.placeholder, this.getX() + this.innerPadding(), this.getY() + this.innerPadding(), this.width - this.totalInnerPadding(), -857677600);
            return;
        }
        int $$5 = this.textField.cursor();
        boolean $$6 = this.isFocused() && this.frame / 6 % 2 == 0;
        boolean $$7 = $$5 < $$4.length();
        int $$8 = 0;
        int $$9 = 0;
        int $$10 = this.getY() + this.innerPadding();
        for (MultilineTextField.StringView $$11 : this.textField.iterateLines()) {
            Objects.requireNonNull((Object)this.font);
            boolean $$12 = this.withinContentAreaTopBottom($$10, $$10 + 9);
            if ($$6 && $$7 && $$5 >= $$11.beginIndex() && $$5 <= $$11.endIndex()) {
                if ($$12) {
                    $$8 = this.font.drawShadow($$0, $$4.substring($$11.beginIndex(), $$5), (float)(this.getX() + this.innerPadding()), (float)$$10, -2039584) - 1;
                    Objects.requireNonNull((Object)this.font);
                    GuiComponent.fill($$0, $$8, $$10 - 1, $$8 + 1, $$10 + 1 + 9, -3092272);
                    this.font.drawShadow($$0, $$4.substring($$5, $$11.endIndex()), (float)$$8, (float)$$10, -2039584);
                }
            } else {
                if ($$12) {
                    $$8 = this.font.drawShadow($$0, $$4.substring($$11.beginIndex(), $$11.endIndex()), (float)(this.getX() + this.innerPadding()), (float)$$10, -2039584) - 1;
                }
                $$9 = $$10;
            }
            Objects.requireNonNull((Object)this.font);
            $$10 += 9;
        }
        if ($$6 && !$$7) {
            Objects.requireNonNull((Object)this.font);
            if (this.withinContentAreaTopBottom($$9, $$9 + 9)) {
                this.font.drawShadow($$0, CURSOR_APPEND_CHARACTER, (float)$$8, (float)$$9, -3092272);
            }
        }
        if (this.textField.hasSelection()) {
            MultilineTextField.StringView $$13 = this.textField.getSelected();
            int $$14 = this.getX() + this.innerPadding();
            $$10 = this.getY() + this.innerPadding();
            for (MultilineTextField.StringView $$15 : this.textField.iterateLines()) {
                if ($$13.beginIndex() > $$15.endIndex()) {
                    Objects.requireNonNull((Object)this.font);
                    $$10 += 9;
                    continue;
                }
                if ($$15.beginIndex() > $$13.endIndex()) break;
                Objects.requireNonNull((Object)this.font);
                if (this.withinContentAreaTopBottom($$10, $$10 + 9)) {
                    int $$18;
                    int $$16 = this.font.width($$4.substring($$15.beginIndex(), Math.max((int)$$13.beginIndex(), (int)$$15.beginIndex())));
                    if ($$13.endIndex() > $$15.endIndex()) {
                        int $$17 = this.width - this.innerPadding();
                    } else {
                        $$18 = this.font.width($$4.substring($$15.beginIndex(), $$13.endIndex()));
                    }
                    Objects.requireNonNull((Object)this.font);
                    this.renderHighlight($$0, $$14 + $$16, $$10, $$14 + $$18, $$10 + 9);
                }
                Objects.requireNonNull((Object)this.font);
                $$10 += 9;
            }
        }
    }

    @Override
    protected void renderDecorations(PoseStack $$0) {
        super.renderDecorations($$0);
        if (this.textField.hasCharacterLimit()) {
            int $$1 = this.textField.characterLimit();
            MutableComponent $$2 = Component.translatable("gui.multiLineEditBox.character_limit", this.textField.value().length(), $$1);
            MultiLineEditBox.drawString($$0, this.font, $$2, this.getX() + this.width - this.font.width($$2), this.getY() + this.height + 4, 0xA0A0A0);
        }
    }

    @Override
    public int getInnerHeight() {
        Objects.requireNonNull((Object)this.font);
        return 9 * this.textField.getLineCount();
    }

    @Override
    protected boolean scrollbarVisible() {
        return (double)this.textField.getLineCount() > this.getDisplayableLineCount();
    }

    @Override
    protected double scrollRate() {
        Objects.requireNonNull((Object)this.font);
        return 9.0 / 2.0;
    }

    private void renderHighlight(PoseStack $$0, int $$1, int $$2, int $$3, int $$4) {
        Matrix4f $$5 = $$0.last().pose();
        Tesselator $$6 = Tesselator.getInstance();
        BufferBuilder $$7 = $$6.getBuilder();
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionShader));
        RenderSystem.setShaderColor(0.0f, 0.0f, 1.0f, 1.0f);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        $$7.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        $$7.vertex($$5, $$1, $$4, 0.0f).endVertex();
        $$7.vertex($$5, $$3, $$4, 0.0f).endVertex();
        $$7.vertex($$5, $$3, $$2, 0.0f).endVertex();
        $$7.vertex($$5, $$1, $$2, 0.0f).endVertex();
        $$6.end();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }

    private void scrollToCursor() {
        double $$0 = this.scrollAmount();
        Objects.requireNonNull((Object)this.font);
        MultilineTextField.StringView $$1 = this.textField.getLineView((int)($$0 / 9.0));
        if (this.textField.cursor() <= $$1.beginIndex()) {
            int n = this.textField.getLineAtCursor();
            Objects.requireNonNull((Object)this.font);
            $$0 = n * 9;
        } else {
            double d = $$0 + (double)this.height;
            Objects.requireNonNull((Object)this.font);
            MultilineTextField.StringView $$2 = this.textField.getLineView((int)(d / 9.0) - 1);
            if (this.textField.cursor() > $$2.endIndex()) {
                int n = this.textField.getLineAtCursor();
                Objects.requireNonNull((Object)this.font);
                int n2 = n * 9 - this.height;
                Objects.requireNonNull((Object)this.font);
                $$0 = n2 + 9 + this.totalInnerPadding();
            }
        }
        this.setScrollAmount($$0);
    }

    private double getDisplayableLineCount() {
        double d = this.height - this.totalInnerPadding();
        Objects.requireNonNull((Object)this.font);
        return d / 9.0;
    }

    private void seekCursorScreen(double $$0, double $$1) {
        double $$2 = $$0 - (double)this.getX() - (double)this.innerPadding();
        double $$3 = $$1 - (double)this.getY() - (double)this.innerPadding() + this.scrollAmount();
        this.textField.seekCursorToPoint($$2, $$3);
    }
}