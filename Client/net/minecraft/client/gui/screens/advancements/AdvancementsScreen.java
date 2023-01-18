/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Iterator
 *  java.util.Map
 *  java.util.Objects
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.advancements;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.gui.screens.advancements.AdvancementWidget;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;

public class AdvancementsScreen
extends Screen
implements ClientAdvancements.Listener {
    private static final ResourceLocation WINDOW_LOCATION = new ResourceLocation("textures/gui/advancements/window.png");
    private static final ResourceLocation TABS_LOCATION = new ResourceLocation("textures/gui/advancements/tabs.png");
    public static final int WINDOW_WIDTH = 252;
    public static final int WINDOW_HEIGHT = 140;
    private static final int WINDOW_INSIDE_X = 9;
    private static final int WINDOW_INSIDE_Y = 18;
    public static final int WINDOW_INSIDE_WIDTH = 234;
    public static final int WINDOW_INSIDE_HEIGHT = 113;
    private static final int WINDOW_TITLE_X = 8;
    private static final int WINDOW_TITLE_Y = 6;
    public static final int BACKGROUND_TILE_WIDTH = 16;
    public static final int BACKGROUND_TILE_HEIGHT = 16;
    public static final int BACKGROUND_TILE_COUNT_X = 14;
    public static final int BACKGROUND_TILE_COUNT_Y = 7;
    private static final Component VERY_SAD_LABEL = Component.translatable("advancements.sad_label");
    private static final Component NO_ADVANCEMENTS_LABEL = Component.translatable("advancements.empty");
    private static final Component TITLE = Component.translatable("gui.advancements");
    private final ClientAdvancements advancements;
    private final Map<Advancement, AdvancementTab> tabs = Maps.newLinkedHashMap();
    @Nullable
    private AdvancementTab selectedTab;
    private boolean isScrolling;

    public AdvancementsScreen(ClientAdvancements $$0) {
        super(GameNarrator.NO_TITLE);
        this.advancements = $$0;
    }

    @Override
    protected void init() {
        this.tabs.clear();
        this.selectedTab = null;
        this.advancements.setListener(this);
        if (this.selectedTab == null && !this.tabs.isEmpty()) {
            this.advancements.setSelectedTab(((AdvancementTab)this.tabs.values().iterator().next()).getAdvancement(), true);
        } else {
            this.advancements.setSelectedTab(this.selectedTab == null ? null : this.selectedTab.getAdvancement(), true);
        }
    }

    @Override
    public void removed() {
        this.advancements.setListener(null);
        ClientPacketListener $$0 = this.minecraft.getConnection();
        if ($$0 != null) {
            $$0.send(ServerboundSeenAdvancementsPacket.closedScreen());
        }
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        if ($$2 == 0) {
            int $$3 = (this.width - 252) / 2;
            int $$4 = (this.height - 140) / 2;
            for (AdvancementTab $$5 : this.tabs.values()) {
                if (!$$5.isMouseOver($$3, $$4, $$0, $$1)) continue;
                this.advancements.setSelectedTab($$5.getAdvancement(), true);
                break;
            }
        }
        return super.mouseClicked($$0, $$1, $$2);
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (this.minecraft.options.keyAdvancements.matches($$0, $$1)) {
            this.minecraft.setScreen(null);
            this.minecraft.mouseHandler.grabMouse();
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        int $$4 = (this.width - 252) / 2;
        int $$5 = (this.height - 140) / 2;
        this.renderBackground($$0);
        this.renderInside($$0, $$1, $$2, $$4, $$5);
        this.renderWindow($$0, $$4, $$5);
        this.renderTooltips($$0, $$1, $$2, $$4, $$5);
    }

    @Override
    public boolean mouseDragged(double $$0, double $$1, int $$2, double $$3, double $$4) {
        if ($$2 != 0) {
            this.isScrolling = false;
            return false;
        }
        if (!this.isScrolling) {
            this.isScrolling = true;
        } else if (this.selectedTab != null) {
            this.selectedTab.scroll($$3, $$4);
        }
        return true;
    }

    private void renderInside(PoseStack $$0, int $$1, int $$2, int $$3, int $$4) {
        AdvancementTab $$5 = this.selectedTab;
        if ($$5 == null) {
            AdvancementsScreen.fill($$0, $$3 + 9, $$4 + 18, $$3 + 9 + 234, $$4 + 18 + 113, -16777216);
            int $$6 = $$3 + 9 + 117;
            Objects.requireNonNull((Object)this.font);
            AdvancementsScreen.drawCenteredString($$0, this.font, NO_ADVANCEMENTS_LABEL, $$6, $$4 + 18 + 56 - 9 / 2, -1);
            Objects.requireNonNull((Object)this.font);
            AdvancementsScreen.drawCenteredString($$0, this.font, VERY_SAD_LABEL, $$6, $$4 + 18 + 113 - 9, -1);
            return;
        }
        PoseStack $$7 = RenderSystem.getModelViewStack();
        $$7.pushPose();
        $$7.translate($$3 + 9, $$4 + 18, 0.0f);
        RenderSystem.applyModelViewMatrix();
        $$5.drawContents($$0);
        $$7.popPose();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
    }

    public void renderWindow(PoseStack $$0, int $$1, int $$2) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, WINDOW_LOCATION);
        this.blit($$0, $$1, $$2, 0, 0, 252, 140);
        if (this.tabs.size() > 1) {
            RenderSystem.setShaderTexture(0, TABS_LOCATION);
            Iterator iterator = this.tabs.values().iterator();
            while (iterator.hasNext()) {
                AdvancementTab $$3;
                $$3.drawTab($$0, $$1, $$2, ($$3 = (AdvancementTab)iterator.next()) == this.selectedTab);
            }
            RenderSystem.defaultBlendFunc();
            for (AdvancementTab $$4 : this.tabs.values()) {
                $$4.drawIcon($$1, $$2, this.itemRenderer);
            }
            RenderSystem.disableBlend();
        }
        this.font.draw($$0, TITLE, (float)($$1 + 8), (float)($$2 + 6), 0x404040);
    }

    private void renderTooltips(PoseStack $$0, int $$1, int $$2, int $$3, int $$4) {
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        if (this.selectedTab != null) {
            PoseStack $$5 = RenderSystem.getModelViewStack();
            $$5.pushPose();
            $$5.translate($$3 + 9, $$4 + 18, 400.0f);
            RenderSystem.applyModelViewMatrix();
            RenderSystem.enableDepthTest();
            this.selectedTab.drawTooltips($$0, $$1 - $$3 - 9, $$2 - $$4 - 18, $$3, $$4);
            RenderSystem.disableDepthTest();
            $$5.popPose();
            RenderSystem.applyModelViewMatrix();
        }
        if (this.tabs.size() > 1) {
            for (AdvancementTab $$6 : this.tabs.values()) {
                if (!$$6.isMouseOver($$3, $$4, $$1, $$2)) continue;
                this.renderTooltip($$0, $$6.getTitle(), $$1, $$2);
            }
        }
    }

    @Override
    public void onAddAdvancementRoot(Advancement $$0) {
        AdvancementTab $$1 = AdvancementTab.create(this.minecraft, this, this.tabs.size(), $$0);
        if ($$1 == null) {
            return;
        }
        this.tabs.put((Object)$$0, (Object)$$1);
    }

    @Override
    public void onRemoveAdvancementRoot(Advancement $$0) {
    }

    @Override
    public void onAddAdvancementTask(Advancement $$0) {
        AdvancementTab $$1 = this.getTab($$0);
        if ($$1 != null) {
            $$1.addAdvancement($$0);
        }
    }

    @Override
    public void onRemoveAdvancementTask(Advancement $$0) {
    }

    @Override
    public void onUpdateAdvancementProgress(Advancement $$0, AdvancementProgress $$1) {
        AdvancementWidget $$2 = this.getAdvancementWidget($$0);
        if ($$2 != null) {
            $$2.setProgress($$1);
        }
    }

    @Override
    public void onSelectedTabChanged(@Nullable Advancement $$0) {
        this.selectedTab = (AdvancementTab)this.tabs.get((Object)$$0);
    }

    @Override
    public void onAdvancementsCleared() {
        this.tabs.clear();
        this.selectedTab = null;
    }

    @Nullable
    public AdvancementWidget getAdvancementWidget(Advancement $$0) {
        AdvancementTab $$1 = this.getTab($$0);
        return $$1 == null ? null : $$1.getWidget($$0);
    }

    @Nullable
    private AdvancementTab getTab(Advancement $$0) {
        while ($$0.getParent() != null) {
            $$0 = $$0.getParent();
        }
        return (AdvancementTab)this.tabs.get((Object)$$0);
    }
}