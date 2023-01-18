/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Integer
 *  java.lang.Math
 *  java.lang.Object
 *  java.util.Map
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.advancements;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.advancements.AdvancementTabType;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class AdvancementTab
extends GuiComponent {
    private final Minecraft minecraft;
    private final AdvancementsScreen screen;
    private final AdvancementTabType type;
    private final int index;
    private final Advancement advancement;
    private final DisplayInfo display;
    private final ItemStack icon;
    private final Component title;
    private final AdvancementWidget root;
    private final Map<Advancement, AdvancementWidget> widgets = Maps.newLinkedHashMap();
    private double scrollX;
    private double scrollY;
    private int minX = Integer.MAX_VALUE;
    private int minY = Integer.MAX_VALUE;
    private int maxX = Integer.MIN_VALUE;
    private int maxY = Integer.MIN_VALUE;
    private float fade;
    private boolean centered;

    public AdvancementTab(Minecraft $$0, AdvancementsScreen $$1, AdvancementTabType $$2, int $$3, Advancement $$4, DisplayInfo $$5) {
        this.minecraft = $$0;
        this.screen = $$1;
        this.type = $$2;
        this.index = $$3;
        this.advancement = $$4;
        this.display = $$5;
        this.icon = $$5.getIcon();
        this.title = $$5.getTitle();
        this.root = new AdvancementWidget(this, $$0, $$4, $$5);
        this.addWidget(this.root, $$4);
    }

    public AdvancementTabType getType() {
        return this.type;
    }

    public int getIndex() {
        return this.index;
    }

    public Advancement getAdvancement() {
        return this.advancement;
    }

    public Component getTitle() {
        return this.title;
    }

    public DisplayInfo getDisplay() {
        return this.display;
    }

    public void drawTab(PoseStack $$0, int $$1, int $$2, boolean $$3) {
        this.type.draw($$0, this, $$1, $$2, $$3, this.index);
    }

    public void drawIcon(int $$0, int $$1, ItemRenderer $$2) {
        this.type.drawIcon($$0, $$1, this.index, $$2, this.icon);
    }

    public void drawContents(PoseStack $$0) {
        if (!this.centered) {
            this.scrollX = 117 - (this.maxX + this.minX) / 2;
            this.scrollY = 56 - (this.maxY + this.minY) / 2;
            this.centered = true;
        }
        $$0.pushPose();
        $$0.translate(0.0f, 0.0f, 950.0f);
        RenderSystem.enableDepthTest();
        RenderSystem.colorMask(false, false, false, false);
        AdvancementTab.fill($$0, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        $$0.translate(0.0f, 0.0f, -950.0f);
        RenderSystem.depthFunc(518);
        AdvancementTab.fill($$0, 234, 113, 0, 0, -16777216);
        RenderSystem.depthFunc(515);
        ResourceLocation $$1 = this.display.getBackground();
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        if ($$1 != null) {
            RenderSystem.setShaderTexture(0, $$1);
        } else {
            RenderSystem.setShaderTexture(0, TextureManager.INTENTIONAL_MISSING_TEXTURE);
        }
        int $$2 = Mth.floor(this.scrollX);
        int $$3 = Mth.floor(this.scrollY);
        int $$4 = $$2 % 16;
        int $$5 = $$3 % 16;
        for (int $$6 = -1; $$6 <= 15; ++$$6) {
            for (int $$7 = -1; $$7 <= 8; ++$$7) {
                AdvancementTab.blit($$0, $$4 + 16 * $$6, $$5 + 16 * $$7, 0.0f, 0.0f, 16, 16, 16, 16);
            }
        }
        this.root.drawConnectivity($$0, $$2, $$3, true);
        this.root.drawConnectivity($$0, $$2, $$3, false);
        this.root.draw($$0, $$2, $$3);
        RenderSystem.depthFunc(518);
        $$0.translate(0.0f, 0.0f, -950.0f);
        RenderSystem.colorMask(false, false, false, false);
        AdvancementTab.fill($$0, 4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.depthFunc(515);
        $$0.popPose();
    }

    public void drawTooltips(PoseStack $$0, int $$1, int $$2, int $$3, int $$4) {
        $$0.pushPose();
        $$0.translate(0.0f, 0.0f, -200.0f);
        AdvancementTab.fill($$0, 0, 0, 234, 113, Mth.floor(this.fade * 255.0f) << 24);
        boolean $$5 = false;
        int $$6 = Mth.floor(this.scrollX);
        int $$7 = Mth.floor(this.scrollY);
        if ($$1 > 0 && $$1 < 234 && $$2 > 0 && $$2 < 113) {
            for (AdvancementWidget $$8 : this.widgets.values()) {
                if (!$$8.isMouseOver($$6, $$7, $$1, $$2)) continue;
                $$5 = true;
                $$8.drawHover($$0, $$6, $$7, this.fade, $$3, $$4);
                break;
            }
        }
        $$0.popPose();
        this.fade = $$5 ? Mth.clamp(this.fade + 0.02f, 0.0f, 0.3f) : Mth.clamp(this.fade - 0.04f, 0.0f, 1.0f);
    }

    public boolean isMouseOver(int $$0, int $$1, double $$2, double $$3) {
        return this.type.isMouseOver($$0, $$1, this.index, $$2, $$3);
    }

    @Nullable
    public static AdvancementTab create(Minecraft $$0, AdvancementsScreen $$1, int $$2, Advancement $$3) {
        if ($$3.getDisplay() == null) {
            return null;
        }
        for (AdvancementTabType $$4 : AdvancementTabType.values()) {
            if ($$2 >= $$4.getMax()) {
                $$2 -= $$4.getMax();
                continue;
            }
            return new AdvancementTab($$0, $$1, $$4, $$2, $$3, $$3.getDisplay());
        }
        return null;
    }

    public void scroll(double $$0, double $$1) {
        if (this.maxX - this.minX > 234) {
            this.scrollX = Mth.clamp(this.scrollX + $$0, (double)(-(this.maxX - 234)), 0.0);
        }
        if (this.maxY - this.minY > 113) {
            this.scrollY = Mth.clamp(this.scrollY + $$1, (double)(-(this.maxY - 113)), 0.0);
        }
    }

    public void addAdvancement(Advancement $$0) {
        if ($$0.getDisplay() == null) {
            return;
        }
        AdvancementWidget $$1 = new AdvancementWidget(this, this.minecraft, $$0, $$0.getDisplay());
        this.addWidget($$1, $$0);
    }

    private void addWidget(AdvancementWidget $$0, Advancement $$1) {
        this.widgets.put((Object)$$1, (Object)$$0);
        int $$2 = $$0.getX();
        int $$3 = $$2 + 28;
        int $$4 = $$0.getY();
        int $$5 = $$4 + 27;
        this.minX = Math.min((int)this.minX, (int)$$2);
        this.maxX = Math.max((int)this.maxX, (int)$$3);
        this.minY = Math.min((int)this.minY, (int)$$4);
        this.maxY = Math.max((int)this.maxY, (int)$$5);
        for (AdvancementWidget $$6 : this.widgets.values()) {
            $$6.attachToParent();
        }
    }

    @Nullable
    public AdvancementWidget getWidget(Advancement $$0) {
        return (AdvancementWidget)this.widgets.get((Object)$$0);
    }

    public AdvancementsScreen getScreen() {
        return this.screen;
    }
}