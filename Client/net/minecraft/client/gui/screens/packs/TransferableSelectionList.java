/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.Objects
 *  java.util.function.Supplier
 */
package net.minecraft.client.gui.screens.packs;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.util.FormattedCharSequence;

public class TransferableSelectionList
extends ObjectSelectionList<PackEntry> {
    static final ResourceLocation ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/resource_packs.png");
    static final Component INCOMPATIBLE_TITLE = Component.translatable("pack.incompatible");
    static final Component INCOMPATIBLE_CONFIRM_TITLE = Component.translatable("pack.incompatible.confirm.title");
    private final Component title;
    final PackSelectionScreen screen;

    public TransferableSelectionList(Minecraft $$0, PackSelectionScreen $$1, int $$2, int $$3, Component $$4) {
        super($$0, $$2, $$3, 32, $$3 - 55 + 4, 36);
        this.screen = $$1;
        this.title = $$4;
        this.centerListVertically = false;
        Objects.requireNonNull((Object)$$0.font);
        this.setRenderHeader(true, (int)(9.0f * 1.5f));
    }

    @Override
    protected void renderHeader(PoseStack $$0, int $$1, int $$2) {
        MutableComponent $$3 = Component.empty().append(this.title).withStyle(ChatFormatting.UNDERLINE, ChatFormatting.BOLD);
        this.minecraft.font.draw($$0, $$3, (float)($$1 + this.width / 2 - this.minecraft.font.width($$3) / 2), (float)Math.min((int)(this.y0 + 3), (int)$$2), 0xFFFFFF);
    }

    @Override
    public int getRowWidth() {
        return this.width;
    }

    @Override
    protected int getScrollbarPosition() {
        return this.x1 - 6;
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if (this.getSelected() != null) {
            switch ($$0) {
                case 32: 
                case 257: {
                    ((PackEntry)this.getSelected()).keyboardSelection();
                    return true;
                }
            }
            if (Screen.hasShiftDown()) {
                switch ($$0) {
                    case 265: {
                        ((PackEntry)this.getSelected()).keyboardMoveUp();
                        return true;
                    }
                    case 264: {
                        ((PackEntry)this.getSelected()).keyboardMoveDown();
                        return true;
                    }
                }
            }
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    public static class PackEntry
    extends ObjectSelectionList.Entry<PackEntry> {
        private static final int ICON_OVERLAY_X_MOVE_RIGHT = 0;
        private static final int ICON_OVERLAY_X_MOVE_LEFT = 32;
        private static final int ICON_OVERLAY_X_MOVE_DOWN = 64;
        private static final int ICON_OVERLAY_X_MOVE_UP = 96;
        private static final int ICON_OVERLAY_Y_UNSELECTED = 0;
        private static final int ICON_OVERLAY_Y_SELECTED = 32;
        private static final int MAX_DESCRIPTION_WIDTH_PIXELS = 157;
        private static final int MAX_NAME_WIDTH_PIXELS = 157;
        private static final String TOO_LONG_NAME_SUFFIX = "...";
        private final TransferableSelectionList parent;
        protected final Minecraft minecraft;
        private final PackSelectionModel.Entry pack;
        private final FormattedCharSequence nameDisplayCache;
        private final MultiLineLabel descriptionDisplayCache;
        private final FormattedCharSequence incompatibleNameDisplayCache;
        private final MultiLineLabel incompatibleDescriptionDisplayCache;

        public PackEntry(Minecraft $$0, TransferableSelectionList $$1, PackSelectionModel.Entry $$2) {
            this.minecraft = $$0;
            this.pack = $$2;
            this.parent = $$1;
            this.nameDisplayCache = PackEntry.cacheName($$0, $$2.getTitle());
            this.descriptionDisplayCache = PackEntry.cacheDescription($$0, $$2.getExtendedDescription());
            this.incompatibleNameDisplayCache = PackEntry.cacheName($$0, INCOMPATIBLE_TITLE);
            this.incompatibleDescriptionDisplayCache = PackEntry.cacheDescription($$0, $$2.getCompatibility().getDescription());
        }

        private static FormattedCharSequence cacheName(Minecraft $$0, Component $$1) {
            int $$2 = $$0.font.width($$1);
            if ($$2 > 157) {
                FormattedText $$3 = FormattedText.composite($$0.font.substrByWidth($$1, 157 - $$0.font.width(TOO_LONG_NAME_SUFFIX)), FormattedText.of(TOO_LONG_NAME_SUFFIX));
                return Language.getInstance().getVisualOrder($$3);
            }
            return $$1.getVisualOrderText();
        }

        private static MultiLineLabel cacheDescription(Minecraft $$0, Component $$1) {
            return MultiLineLabel.create($$0.font, (FormattedText)$$1, 157, 2);
        }

        @Override
        public Component getNarration() {
            return Component.translatable("narrator.select", this.pack.getTitle());
        }

        @Override
        public void render(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, boolean $$8, float $$9) {
            PackCompatibility $$10 = this.pack.getCompatibility();
            if (!$$10.isCompatible()) {
                GuiComponent.fill($$0, $$3 - 1, $$2 - 1, $$3 + $$4 - 9, $$2 + $$5 + 1, -8978432);
            }
            RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
            RenderSystem.setShaderTexture(0, this.pack.getIconTexture());
            GuiComponent.blit($$0, $$3, $$2, 0.0f, 0.0f, 32, 32, 32, 32);
            FormattedCharSequence $$11 = this.nameDisplayCache;
            MultiLineLabel $$12 = this.descriptionDisplayCache;
            if (this.showHoverOverlay() && (this.minecraft.options.touchscreen().get().booleanValue() || $$8 || this.parent.getSelected() == this && this.parent.isFocused())) {
                RenderSystem.setShaderTexture(0, ICON_OVERLAY_LOCATION);
                GuiComponent.fill($$0, $$3, $$2, $$3 + 32, $$2 + 32, -1601138544);
                RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
                int $$13 = $$6 - $$3;
                int $$14 = $$7 - $$2;
                if (!this.pack.getCompatibility().isCompatible()) {
                    $$11 = this.incompatibleNameDisplayCache;
                    $$12 = this.incompatibleDescriptionDisplayCache;
                }
                if (this.pack.canSelect()) {
                    if ($$13 < 32) {
                        GuiComponent.blit($$0, $$3, $$2, 0.0f, 32.0f, 32, 32, 256, 256);
                    } else {
                        GuiComponent.blit($$0, $$3, $$2, 0.0f, 0.0f, 32, 32, 256, 256);
                    }
                } else {
                    if (this.pack.canUnselect()) {
                        if ($$13 < 16) {
                            GuiComponent.blit($$0, $$3, $$2, 32.0f, 32.0f, 32, 32, 256, 256);
                        } else {
                            GuiComponent.blit($$0, $$3, $$2, 32.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                    if (this.pack.canMoveUp()) {
                        if ($$13 < 32 && $$13 > 16 && $$14 < 16) {
                            GuiComponent.blit($$0, $$3, $$2, 96.0f, 32.0f, 32, 32, 256, 256);
                        } else {
                            GuiComponent.blit($$0, $$3, $$2, 96.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                    if (this.pack.canMoveDown()) {
                        if ($$13 < 32 && $$13 > 16 && $$14 > 16) {
                            GuiComponent.blit($$0, $$3, $$2, 64.0f, 32.0f, 32, 32, 256, 256);
                        } else {
                            GuiComponent.blit($$0, $$3, $$2, 64.0f, 0.0f, 32, 32, 256, 256);
                        }
                    }
                }
            }
            this.minecraft.font.drawShadow($$0, $$11, (float)($$3 + 32 + 2), (float)($$2 + 1), 0xFFFFFF);
            $$12.renderLeftAligned($$0, $$3 + 32 + 2, $$2 + 12, 10, 0x808080);
        }

        public String getPackId() {
            return this.pack.getId();
        }

        private boolean showHoverOverlay() {
            return !this.pack.isFixedPosition() || !this.pack.isRequired();
        }

        public void keyboardSelection() {
            if (this.pack.canSelect() && this.pack.getCompatibility().isCompatible()) {
                this.pack.select();
                this.parent.screen.updateFocus(this.pack, this.parent);
            } else if (this.pack.canUnselect()) {
                this.pack.unselect();
                this.parent.screen.updateFocus(this.pack, this.parent);
            }
        }

        public void keyboardMoveUp() {
            if (this.pack.canMoveUp()) {
                this.pack.moveUp();
            }
        }

        public void keyboardMoveDown() {
            if (this.pack.canMoveDown()) {
                this.pack.moveDown();
            }
        }

        @Override
        public boolean mouseClicked(double $$02, double $$1, int $$2) {
            double $$3 = $$02 - (double)this.parent.getRowLeft();
            double $$4 = $$1 - (double)this.parent.getRowTop(this.parent.children().indexOf((Object)this));
            if (this.showHoverOverlay() && $$3 <= 32.0) {
                this.parent.screen.clearSelected();
                if (this.pack.canSelect()) {
                    PackCompatibility $$5 = this.pack.getCompatibility();
                    if ($$5.isCompatible()) {
                        this.pack.select();
                    } else {
                        Component $$6 = $$5.getConfirmation();
                        this.minecraft.setScreen(new ConfirmScreen($$0 -> {
                            this.minecraft.setScreen(this.parent.screen);
                            if ($$0) {
                                this.pack.select();
                            }
                        }, INCOMPATIBLE_CONFIRM_TITLE, $$6));
                    }
                    return true;
                }
                if ($$3 < 16.0 && this.pack.canUnselect()) {
                    this.pack.unselect();
                    return true;
                }
                if ($$3 > 16.0 && $$4 < 16.0 && this.pack.canMoveUp()) {
                    this.pack.moveUp();
                    return true;
                }
                if ($$3 > 16.0 && $$4 > 16.0 && this.pack.canMoveDown()) {
                    this.pack.moveDown();
                    return true;
                }
            }
            return false;
        }
    }
}