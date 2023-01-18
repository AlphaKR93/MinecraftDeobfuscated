/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Object
 *  java.lang.Override
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.components.spectator;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.gui.spectator.SpectatorMenuListener;
import net.minecraft.client.gui.spectator.categories.SpectatorPage;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SpectatorGui
extends GuiComponent
implements SpectatorMenuListener {
    private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
    public static final ResourceLocation SPECTATOR_LOCATION = new ResourceLocation("textures/gui/spectator_widgets.png");
    private static final long FADE_OUT_DELAY = 5000L;
    private static final long FADE_OUT_TIME = 2000L;
    private final Minecraft minecraft;
    private long lastSelectionTime;
    @Nullable
    private SpectatorMenu menu;

    public SpectatorGui(Minecraft $$0) {
        this.minecraft = $$0;
    }

    public void onHotbarSelected(int $$0) {
        this.lastSelectionTime = Util.getMillis();
        if (this.menu != null) {
            this.menu.selectSlot($$0);
        } else {
            this.menu = new SpectatorMenu(this);
        }
    }

    private float getHotbarAlpha() {
        long $$0 = this.lastSelectionTime - Util.getMillis() + 5000L;
        return Mth.clamp((float)$$0 / 2000.0f, 0.0f, 1.0f);
    }

    public void renderHotbar(PoseStack $$0) {
        if (this.menu == null) {
            return;
        }
        float $$1 = this.getHotbarAlpha();
        if ($$1 <= 0.0f) {
            this.menu.exit();
            return;
        }
        int $$2 = this.minecraft.getWindow().getGuiScaledWidth() / 2;
        int $$3 = this.getBlitOffset();
        this.setBlitOffset(-90);
        int $$4 = Mth.floor((float)this.minecraft.getWindow().getGuiScaledHeight() - 22.0f * $$1);
        SpectatorPage $$5 = this.menu.getCurrentPage();
        this.renderPage($$0, $$1, $$2, $$4, $$5);
        this.setBlitOffset($$3);
    }

    protected void renderPage(PoseStack $$0, float $$1, int $$2, int $$3, SpectatorPage $$4) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, $$1);
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        this.blit($$0, $$2 - 91, $$3, 0, 0, 182, 22);
        if ($$4.getSelectedSlot() >= 0) {
            this.blit($$0, $$2 - 91 - 1 + $$4.getSelectedSlot() * 20, $$3 - 1, 0, 22, 24, 22);
        }
        for (int $$5 = 0; $$5 < 9; ++$$5) {
            this.renderSlot($$0, $$5, this.minecraft.getWindow().getGuiScaledWidth() / 2 - 90 + $$5 * 20 + 2, $$3 + 3, $$1, $$4.getItem($$5));
        }
        RenderSystem.disableBlend();
    }

    private void renderSlot(PoseStack $$0, int $$1, int $$2, float $$3, float $$4, SpectatorMenuItem $$5) {
        RenderSystem.setShaderTexture(0, SPECTATOR_LOCATION);
        if ($$5 != SpectatorMenu.EMPTY_SLOT) {
            int $$6 = (int)($$4 * 255.0f);
            $$0.pushPose();
            $$0.translate($$2, $$3, 0.0f);
            float $$7 = $$5.isEnabled() ? 1.0f : 0.25f;
            RenderSystem.setShaderColor($$7, $$7, $$7, $$4);
            $$5.renderIcon($$0, $$7, $$6);
            $$0.popPose();
            if ($$6 > 3 && $$5.isEnabled()) {
                Component $$8 = this.minecraft.options.keyHotbarSlots[$$1].getTranslatedKeyMessage();
                this.minecraft.font.drawShadow($$0, $$8, (float)($$2 + 19 - 2 - this.minecraft.font.width($$8)), $$3 + 6.0f + 3.0f, 0xFFFFFF + ($$6 << 24));
            }
        }
    }

    public void renderTooltip(PoseStack $$0) {
        int $$1 = (int)(this.getHotbarAlpha() * 255.0f);
        if ($$1 > 3 && this.menu != null) {
            Component $$3;
            SpectatorMenuItem $$2 = this.menu.getSelectedItem();
            Component component = $$3 = $$2 == SpectatorMenu.EMPTY_SLOT ? this.menu.getSelectedCategory().getPrompt() : $$2.getName();
            if ($$3 != null) {
                int $$4 = (this.minecraft.getWindow().getGuiScaledWidth() - this.minecraft.font.width($$3)) / 2;
                int $$5 = this.minecraft.getWindow().getGuiScaledHeight() - 35;
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                this.minecraft.font.drawShadow($$0, $$3, (float)$$4, (float)$$5, 0xFFFFFF + ($$1 << 24));
                RenderSystem.disableBlend();
            }
        }
    }

    @Override
    public void onSpectatorMenuClosed(SpectatorMenu $$0) {
        this.menu = null;
        this.lastSelectionTime = 0L;
    }

    public boolean isMenuActive() {
        return this.menu != null;
    }

    public void onMouseScrolled(int $$0) {
        int $$1;
        for ($$1 = this.menu.getSelectedSlot() + $$0; !($$1 < 0 || $$1 > 8 || this.menu.getItem($$1) != SpectatorMenu.EMPTY_SLOT && this.menu.getItem($$1).isEnabled()); $$1 += $$0) {
        }
        if ($$1 >= 0 && $$1 <= 8) {
            this.menu.selectSlot($$1);
            this.lastSelectionTime = Util.getMillis();
        }
    }

    public void onMouseMiddleClick() {
        this.lastSelectionTime = Util.getMillis();
        if (this.isMenuActive()) {
            int $$0 = this.menu.getSelectedSlot();
            if ($$0 != -1) {
                this.menu.selectSlot($$0);
            }
        } else {
            this.menu = new SpectatorMenu(this);
        }
    }
}