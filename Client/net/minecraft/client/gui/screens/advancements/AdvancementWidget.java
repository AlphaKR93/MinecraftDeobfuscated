/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Float
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.util.List
 *  java.util.Objects
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.advancements;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidgetType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

public class AdvancementWidget
extends GuiComponent {
    private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/advancements/widgets.png");
    private static final int HEIGHT = 26;
    private static final int BOX_X = 0;
    private static final int BOX_WIDTH = 200;
    private static final int FRAME_WIDTH = 26;
    private static final int ICON_X = 8;
    private static final int ICON_Y = 5;
    private static final int ICON_WIDTH = 26;
    private static final int TITLE_PADDING_LEFT = 3;
    private static final int TITLE_PADDING_RIGHT = 5;
    private static final int TITLE_X = 32;
    private static final int TITLE_Y = 9;
    private static final int TITLE_MAX_WIDTH = 163;
    private static final int[] TEST_SPLIT_OFFSETS = new int[]{0, 10, -10, 25, -25};
    private final AdvancementTab tab;
    private final Advancement advancement;
    private final DisplayInfo display;
    private final FormattedCharSequence title;
    private final int width;
    private final List<FormattedCharSequence> description;
    private final Minecraft minecraft;
    @Nullable
    private AdvancementWidget parent;
    private final List<AdvancementWidget> children = Lists.newArrayList();
    @Nullable
    private AdvancementProgress progress;
    private final int x;
    private final int y;

    public AdvancementWidget(AdvancementTab $$0, Minecraft $$1, Advancement $$2, DisplayInfo $$3) {
        this.tab = $$0;
        this.advancement = $$2;
        this.display = $$3;
        this.minecraft = $$1;
        this.title = Language.getInstance().getVisualOrder($$1.font.substrByWidth($$3.getTitle(), 163));
        this.x = Mth.floor($$3.getX() * 28.0f);
        this.y = Mth.floor($$3.getY() * 27.0f);
        int $$4 = $$2.getMaxCriteraRequired();
        int $$5 = String.valueOf((int)$$4).length();
        int $$6 = $$4 > 1 ? $$1.font.width("  ") + $$1.font.width("0") * $$5 * 2 + $$1.font.width("/") : 0;
        int $$7 = 29 + $$1.font.width(this.title) + $$6;
        this.description = Language.getInstance().getVisualOrder(this.findOptimalLines(ComponentUtils.mergeStyles($$3.getDescription().copy(), Style.EMPTY.withColor($$3.getFrame().getChatColor())), $$7));
        for (FormattedCharSequence $$8 : this.description) {
            $$7 = Math.max((int)$$7, (int)$$1.font.width($$8));
        }
        this.width = $$7 + 3 + 5;
    }

    private static float getMaxWidth(StringSplitter $$0, List<FormattedText> $$1) {
        return (float)$$1.stream().mapToDouble($$0::stringWidth).max().orElse(0.0);
    }

    private List<FormattedText> findOptimalLines(Component $$0, int $$1) {
        StringSplitter $$2 = this.minecraft.font.getSplitter();
        List<FormattedText> $$3 = null;
        float $$4 = Float.MAX_VALUE;
        for (int $$5 : TEST_SPLIT_OFFSETS) {
            List<FormattedText> $$6 = $$2.splitLines($$0, $$1 - $$5, Style.EMPTY);
            float $$7 = Math.abs((float)(AdvancementWidget.getMaxWidth($$2, $$6) - (float)$$1));
            if ($$7 <= 10.0f) {
                return $$6;
            }
            if (!($$7 < $$4)) continue;
            $$4 = $$7;
            $$3 = $$6;
        }
        return $$3;
    }

    @Nullable
    private AdvancementWidget getFirstVisibleParent(Advancement $$0) {
        while (($$0 = $$0.getParent()) != null && $$0.getDisplay() == null) {
        }
        if ($$0 == null || $$0.getDisplay() == null) {
            return null;
        }
        return this.tab.getWidget($$0);
    }

    public void drawConnectivity(PoseStack $$0, int $$1, int $$2, boolean $$3) {
        if (this.parent != null) {
            int $$9;
            int $$4 = $$1 + this.parent.x + 13;
            int $$5 = $$1 + this.parent.x + 26 + 4;
            int $$6 = $$2 + this.parent.y + 13;
            int $$7 = $$1 + this.x + 13;
            int $$8 = $$2 + this.y + 13;
            int n = $$9 = $$3 ? -16777216 : -1;
            if ($$3) {
                this.hLine($$0, $$5, $$4, $$6 - 1, $$9);
                this.hLine($$0, $$5 + 1, $$4, $$6, $$9);
                this.hLine($$0, $$5, $$4, $$6 + 1, $$9);
                this.hLine($$0, $$7, $$5 - 1, $$8 - 1, $$9);
                this.hLine($$0, $$7, $$5 - 1, $$8, $$9);
                this.hLine($$0, $$7, $$5 - 1, $$8 + 1, $$9);
                this.vLine($$0, $$5 - 1, $$8, $$6, $$9);
                this.vLine($$0, $$5 + 1, $$8, $$6, $$9);
            } else {
                this.hLine($$0, $$5, $$4, $$6, $$9);
                this.hLine($$0, $$7, $$5, $$8, $$9);
                this.vLine($$0, $$5, $$8, $$6, $$9);
            }
        }
        for (AdvancementWidget $$10 : this.children) {
            $$10.drawConnectivity($$0, $$1, $$2, $$3);
        }
    }

    public void draw(PoseStack $$0, int $$1, int $$2) {
        if (!this.display.isHidden() || this.progress != null && this.progress.isDone()) {
            AdvancementWidgetType $$5;
            float $$3;
            float f = $$3 = this.progress == null ? 0.0f : this.progress.getPercent();
            if ($$3 >= 1.0f) {
                AdvancementWidgetType $$4 = AdvancementWidgetType.OBTAINED;
            } else {
                $$5 = AdvancementWidgetType.UNOBTAINED;
            }
            RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
            RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
            this.blit($$0, $$1 + this.x + 3, $$2 + this.y, this.display.getFrame().getTexture(), 128 + $$5.getIndex() * 26, 26, 26);
            this.minecraft.getItemRenderer().renderAndDecorateFakeItem(this.display.getIcon(), $$1 + this.x + 8, $$2 + this.y + 5);
        }
        for (AdvancementWidget $$6 : this.children) {
            $$6.draw($$0, $$1, $$2);
        }
    }

    public int getWidth() {
        return this.width;
    }

    public void setProgress(AdvancementProgress $$0) {
        this.progress = $$0;
    }

    public void addChild(AdvancementWidget $$0) {
        this.children.add((Object)$$0);
    }

    public void drawHover(PoseStack $$0, int $$1, int $$2, float $$3, int $$4, int $$5) {
        int $$27;
        AdvancementWidgetType $$23;
        AdvancementWidgetType $$22;
        AdvancementWidgetType $$21;
        boolean $$6 = $$4 + $$1 + this.x + this.width + 26 >= this.tab.getScreen().width;
        String $$7 = this.progress == null ? null : this.progress.getProgressText();
        int $$8 = $$7 == null ? 0 : this.minecraft.font.width($$7);
        int n = this.description.size();
        Objects.requireNonNull((Object)this.minecraft.font);
        boolean $$9 = 113 - $$2 - this.y - 26 <= 6 + n * 9;
        float $$10 = this.progress == null ? 0.0f : this.progress.getPercent();
        int $$11 = Mth.floor($$10 * (float)this.width);
        if ($$10 >= 1.0f) {
            $$11 = this.width / 2;
            AdvancementWidgetType $$12 = AdvancementWidgetType.OBTAINED;
            AdvancementWidgetType $$13 = AdvancementWidgetType.OBTAINED;
            AdvancementWidgetType $$14 = AdvancementWidgetType.OBTAINED;
        } else if ($$11 < 2) {
            $$11 = this.width / 2;
            AdvancementWidgetType $$15 = AdvancementWidgetType.UNOBTAINED;
            AdvancementWidgetType $$16 = AdvancementWidgetType.UNOBTAINED;
            AdvancementWidgetType $$17 = AdvancementWidgetType.UNOBTAINED;
        } else if ($$11 > this.width - 2) {
            $$11 = this.width / 2;
            AdvancementWidgetType $$18 = AdvancementWidgetType.OBTAINED;
            AdvancementWidgetType $$19 = AdvancementWidgetType.OBTAINED;
            AdvancementWidgetType $$20 = AdvancementWidgetType.UNOBTAINED;
        } else {
            $$21 = AdvancementWidgetType.OBTAINED;
            $$22 = AdvancementWidgetType.UNOBTAINED;
            $$23 = AdvancementWidgetType.UNOBTAINED;
        }
        int $$24 = this.width - $$11;
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.enableBlend();
        int $$25 = $$2 + this.y;
        if ($$6) {
            int $$26 = $$1 + this.x - this.width + 26 + 6;
        } else {
            $$27 = $$1 + this.x;
        }
        int n2 = this.description.size();
        Objects.requireNonNull((Object)this.minecraft.font);
        int $$28 = 32 + n2 * 9;
        if (!this.description.isEmpty()) {
            if ($$9) {
                this.render9Sprite($$0, $$27, $$25 + 26 - $$28, this.width, $$28, 10, 200, 26, 0, 52);
            } else {
                this.render9Sprite($$0, $$27, $$25, this.width, $$28, 10, 200, 26, 0, 52);
            }
        }
        this.blit($$0, $$27, $$25, 0, $$21.getIndex() * 26, $$11, 26);
        this.blit($$0, $$27 + $$11, $$25, 200 - $$24, $$22.getIndex() * 26, $$24, 26);
        this.blit($$0, $$1 + this.x + 3, $$2 + this.y, this.display.getFrame().getTexture(), 128 + $$23.getIndex() * 26, 26, 26);
        if ($$6) {
            this.minecraft.font.drawShadow($$0, this.title, (float)($$27 + 5), (float)($$2 + this.y + 9), -1);
            if ($$7 != null) {
                this.minecraft.font.drawShadow($$0, $$7, (float)($$1 + this.x - $$8), (float)($$2 + this.y + 9), -1);
            }
        } else {
            this.minecraft.font.drawShadow($$0, this.title, (float)($$1 + this.x + 32), (float)($$2 + this.y + 9), -1);
            if ($$7 != null) {
                this.minecraft.font.drawShadow($$0, $$7, (float)($$1 + this.x + this.width - $$8 - 5), (float)($$2 + this.y + 9), -1);
            }
        }
        if ($$9) {
            for (int $$29 = 0; $$29 < this.description.size(); ++$$29) {
                Font font = this.minecraft.font;
                FormattedCharSequence formattedCharSequence = (FormattedCharSequence)this.description.get($$29);
                float f = $$27 + 5;
                Objects.requireNonNull((Object)this.minecraft.font);
                font.draw($$0, formattedCharSequence, f, (float)($$25 + 26 - $$28 + 7 + $$29 * 9), -5592406);
            }
        } else {
            for (int $$30 = 0; $$30 < this.description.size(); ++$$30) {
                Font font = this.minecraft.font;
                FormattedCharSequence formattedCharSequence = (FormattedCharSequence)this.description.get($$30);
                float f = $$27 + 5;
                Objects.requireNonNull((Object)this.minecraft.font);
                font.draw($$0, formattedCharSequence, f, (float)($$2 + this.y + 9 + 17 + $$30 * 9), -5592406);
            }
        }
        this.minecraft.getItemRenderer().renderAndDecorateFakeItem(this.display.getIcon(), $$1 + this.x + 8, $$2 + this.y + 5);
    }

    protected void render9Sprite(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, int $$8, int $$9) {
        this.blit($$0, $$1, $$2, $$8, $$9, $$5, $$5);
        this.renderRepeating($$0, $$1 + $$5, $$2, $$3 - $$5 - $$5, $$5, $$8 + $$5, $$9, $$6 - $$5 - $$5, $$7);
        this.blit($$0, $$1 + $$3 - $$5, $$2, $$8 + $$6 - $$5, $$9, $$5, $$5);
        this.blit($$0, $$1, $$2 + $$4 - $$5, $$8, $$9 + $$7 - $$5, $$5, $$5);
        this.renderRepeating($$0, $$1 + $$5, $$2 + $$4 - $$5, $$3 - $$5 - $$5, $$5, $$8 + $$5, $$9 + $$7 - $$5, $$6 - $$5 - $$5, $$7);
        this.blit($$0, $$1 + $$3 - $$5, $$2 + $$4 - $$5, $$8 + $$6 - $$5, $$9 + $$7 - $$5, $$5, $$5);
        this.renderRepeating($$0, $$1, $$2 + $$5, $$5, $$4 - $$5 - $$5, $$8, $$9 + $$5, $$6, $$7 - $$5 - $$5);
        this.renderRepeating($$0, $$1 + $$5, $$2 + $$5, $$3 - $$5 - $$5, $$4 - $$5 - $$5, $$8 + $$5, $$9 + $$5, $$6 - $$5 - $$5, $$7 - $$5 - $$5);
        this.renderRepeating($$0, $$1 + $$3 - $$5, $$2 + $$5, $$5, $$4 - $$5 - $$5, $$8 + $$6 - $$5, $$9 + $$5, $$6, $$7 - $$5 - $$5);
    }

    protected void renderRepeating(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, int $$8) {
        for (int $$9 = 0; $$9 < $$3; $$9 += $$7) {
            int $$10 = $$1 + $$9;
            int $$11 = Math.min((int)$$7, (int)($$3 - $$9));
            for (int $$12 = 0; $$12 < $$4; $$12 += $$8) {
                int $$13 = $$2 + $$12;
                int $$14 = Math.min((int)$$8, (int)($$4 - $$12));
                this.blit($$0, $$10, $$13, $$5, $$6, $$11, $$14);
            }
        }
    }

    public boolean isMouseOver(int $$0, int $$1, int $$2, int $$3) {
        if (this.display.isHidden() && (this.progress == null || !this.progress.isDone())) {
            return false;
        }
        int $$4 = $$0 + this.x;
        int $$5 = $$4 + 26;
        int $$6 = $$1 + this.y;
        int $$7 = $$6 + 26;
        return $$2 >= $$4 && $$2 <= $$5 && $$3 >= $$6 && $$3 <= $$7;
    }

    public void attachToParent() {
        if (this.parent == null && this.advancement.getParent() != null) {
            this.parent = this.getFirstVisibleParent(this.advancement);
            if (this.parent != null) {
                this.parent.addChild(this);
            }
        }
    }

    public int getY() {
        return this.y;
    }

    public int getX() {
        return this.x;
    }
}