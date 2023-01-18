/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.Iterator
 *  java.util.function.Supplier
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Iterator;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public class MerchantScreen
extends AbstractContainerScreen<MerchantMenu> {
    private static final ResourceLocation VILLAGER_LOCATION = new ResourceLocation("textures/gui/container/villager2.png");
    private static final int TEXTURE_WIDTH = 512;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int MERCHANT_MENU_PART_X = 99;
    private static final int PROGRESS_BAR_X = 136;
    private static final int PROGRESS_BAR_Y = 16;
    private static final int SELL_ITEM_1_X = 5;
    private static final int SELL_ITEM_2_X = 35;
    private static final int BUY_ITEM_X = 68;
    private static final int LABEL_Y = 6;
    private static final int NUMBER_OF_OFFER_BUTTONS = 7;
    private static final int TRADE_BUTTON_X = 5;
    private static final int TRADE_BUTTON_HEIGHT = 20;
    private static final int TRADE_BUTTON_WIDTH = 89;
    private static final int SCROLLER_HEIGHT = 27;
    private static final int SCROLLER_WIDTH = 6;
    private static final int SCROLL_BAR_HEIGHT = 139;
    private static final int SCROLL_BAR_TOP_POS_Y = 18;
    private static final int SCROLL_BAR_START_X = 94;
    private static final Component TRADES_LABEL = Component.translatable("merchant.trades");
    private static final Component LEVEL_SEPARATOR = Component.literal(" - ");
    private static final Component DEPRECATED_TOOLTIP = Component.translatable("merchant.deprecated");
    private int shopItem;
    private final TradeOfferButton[] tradeOfferButtons = new TradeOfferButton[7];
    int scrollOff;
    private boolean isDragging;

    public MerchantScreen(MerchantMenu $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2);
        this.imageWidth = 276;
        this.inventoryLabelX = 107;
    }

    private void postButtonClick() {
        ((MerchantMenu)this.menu).setSelectionHint(this.shopItem);
        ((MerchantMenu)this.menu).tryMoveItems(this.shopItem);
        this.minecraft.getConnection().send(new ServerboundSelectTradePacket(this.shopItem));
    }

    @Override
    protected void init() {
        super.init();
        int $$02 = (this.width - this.imageWidth) / 2;
        int $$1 = (this.height - this.imageHeight) / 2;
        int $$2 = $$1 + 16 + 2;
        for (int $$3 = 0; $$3 < 7; ++$$3) {
            this.tradeOfferButtons[$$3] = this.addRenderableWidget(new TradeOfferButton($$02 + 5, $$2, $$3, $$0 -> {
                if ($$0 instanceof TradeOfferButton) {
                    this.shopItem = ((TradeOfferButton)$$0).getIndex() + this.scrollOff;
                    this.postButtonClick();
                }
            }));
            $$2 += 20;
        }
    }

    @Override
    protected void renderLabels(PoseStack $$0, int $$1, int $$2) {
        int $$3 = ((MerchantMenu)this.menu).getTraderLevel();
        if ($$3 > 0 && $$3 <= 5 && ((MerchantMenu)this.menu).showProgressBar()) {
            MutableComponent $$4 = this.title.copy().append(LEVEL_SEPARATOR).append(Component.translatable("merchant.level." + $$3));
            int $$5 = this.font.width($$4);
            int $$6 = 49 + this.imageWidth / 2 - $$5 / 2;
            this.font.draw($$0, $$4, (float)$$6, 6.0f, 0x404040);
        } else {
            this.font.draw($$0, this.title, (float)(49 + this.imageWidth / 2 - this.font.width(this.title) / 2), 6.0f, 0x404040);
        }
        this.font.draw($$0, this.playerInventoryTitle, (float)this.inventoryLabelX, (float)this.inventoryLabelY, 0x404040);
        int $$7 = this.font.width(TRADES_LABEL);
        this.font.draw($$0, TRADES_LABEL, (float)(5 - $$7 / 2 + 48), 6.0f, 0x404040);
    }

    @Override
    protected void renderBg(PoseStack $$0, float $$1, int $$2, int $$3) {
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, VILLAGER_LOCATION);
        int $$4 = (this.width - this.imageWidth) / 2;
        int $$5 = (this.height - this.imageHeight) / 2;
        MerchantScreen.blit($$0, $$4, $$5, this.getBlitOffset(), 0.0f, 0.0f, this.imageWidth, this.imageHeight, 512, 256);
        MerchantOffers $$6 = ((MerchantMenu)this.menu).getOffers();
        if (!$$6.isEmpty()) {
            int $$7 = this.shopItem;
            if ($$7 < 0 || $$7 >= $$6.size()) {
                return;
            }
            MerchantOffer $$8 = (MerchantOffer)$$6.get($$7);
            if ($$8.isOutOfStock()) {
                RenderSystem.setShaderTexture(0, VILLAGER_LOCATION);
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                MerchantScreen.blit($$0, this.leftPos + 83 + 99, this.topPos + 35, this.getBlitOffset(), 311.0f, 0.0f, 28, 21, 512, 256);
            }
        }
    }

    private void renderProgressBar(PoseStack $$0, int $$1, int $$2, MerchantOffer $$3) {
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, VILLAGER_LOCATION);
        int $$4 = ((MerchantMenu)this.menu).getTraderLevel();
        int $$5 = ((MerchantMenu)this.menu).getTraderXp();
        if ($$4 >= 5) {
            return;
        }
        MerchantScreen.blit($$0, $$1 + 136, $$2 + 16, this.getBlitOffset(), 0.0f, 186.0f, 102, 5, 512, 256);
        int $$6 = VillagerData.getMinXpPerLevel($$4);
        if ($$5 < $$6 || !VillagerData.canLevelUp($$4)) {
            return;
        }
        int $$7 = 100;
        float $$8 = 100.0f / (float)(VillagerData.getMaxXpPerLevel($$4) - $$6);
        int $$9 = Math.min((int)Mth.floor($$8 * (float)($$5 - $$6)), (int)100);
        MerchantScreen.blit($$0, $$1 + 136, $$2 + 16, this.getBlitOffset(), 0.0f, 191.0f, $$9 + 1, 5, 512, 256);
        int $$10 = ((MerchantMenu)this.menu).getFutureTraderXp();
        if ($$10 > 0) {
            int $$11 = Math.min((int)Mth.floor((float)$$10 * $$8), (int)(100 - $$9));
            MerchantScreen.blit($$0, $$1 + 136 + $$9 + 1, $$2 + 16 + 1, this.getBlitOffset(), 2.0f, 182.0f, $$11, 3, 512, 256);
        }
    }

    private void renderScroller(PoseStack $$0, int $$1, int $$2, MerchantOffers $$3) {
        int $$4 = $$3.size() + 1 - 7;
        if ($$4 > 1) {
            int $$5 = 139 - (27 + ($$4 - 1) * 139 / $$4);
            int $$6 = 1 + $$5 / $$4 + 139 / $$4;
            int $$7 = 113;
            int $$8 = Math.min((int)113, (int)(this.scrollOff * $$6));
            if (this.scrollOff == $$4 - 1) {
                $$8 = 113;
            }
            MerchantScreen.blit($$0, $$1 + 94, $$2 + 18 + $$8, this.getBlitOffset(), 0.0f, 199.0f, 6, 27, 512, 256);
        } else {
            MerchantScreen.blit($$0, $$1 + 94, $$2 + 18, this.getBlitOffset(), 6.0f, 199.0f, 6, 27, 512, 256);
        }
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        super.render($$0, $$1, $$2, $$3);
        MerchantOffers $$4 = ((MerchantMenu)this.menu).getOffers();
        if (!$$4.isEmpty()) {
            int $$5 = (this.width - this.imageWidth) / 2;
            int $$6 = (this.height - this.imageHeight) / 2;
            int $$7 = $$6 + 16 + 1;
            int $$8 = $$5 + 5 + 5;
            RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
            RenderSystem.setShaderTexture(0, VILLAGER_LOCATION);
            this.renderScroller($$0, $$5, $$6, $$4);
            int $$9 = 0;
            Iterator iterator = $$4.iterator();
            while (iterator.hasNext()) {
                MerchantOffer $$10 = (MerchantOffer)iterator.next();
                if (this.canScroll($$4.size()) && ($$9 < this.scrollOff || $$9 >= 7 + this.scrollOff)) {
                    ++$$9;
                    continue;
                }
                ItemStack $$11 = $$10.getBaseCostA();
                ItemStack $$12 = $$10.getCostA();
                ItemStack $$13 = $$10.getCostB();
                ItemStack $$14 = $$10.getResult();
                this.itemRenderer.blitOffset = 100.0f;
                int $$15 = $$7 + 2;
                this.renderAndDecorateCostA($$0, $$12, $$11, $$8, $$15);
                if (!$$13.isEmpty()) {
                    this.itemRenderer.renderAndDecorateFakeItem($$13, $$5 + 5 + 35, $$15);
                    this.itemRenderer.renderGuiItemDecorations(this.font, $$13, $$5 + 5 + 35, $$15);
                }
                this.renderButtonArrows($$0, $$10, $$5, $$15);
                this.itemRenderer.renderAndDecorateFakeItem($$14, $$5 + 5 + 68, $$15);
                this.itemRenderer.renderGuiItemDecorations(this.font, $$14, $$5 + 5 + 68, $$15);
                this.itemRenderer.blitOffset = 0.0f;
                $$7 += 20;
                ++$$9;
            }
            int $$16 = this.shopItem;
            MerchantOffer $$17 = (MerchantOffer)$$4.get($$16);
            if (((MerchantMenu)this.menu).showProgressBar()) {
                this.renderProgressBar($$0, $$5, $$6, $$17);
            }
            if ($$17.isOutOfStock() && this.isHovering(186, 35, 22, 21, $$1, $$2) && ((MerchantMenu)this.menu).canRestock()) {
                this.renderTooltip($$0, DEPRECATED_TOOLTIP, $$1, $$2);
            }
            for (TradeOfferButton $$18 : this.tradeOfferButtons) {
                if ($$18.isHoveredOrFocused()) {
                    $$18.renderToolTip($$0, $$1, $$2);
                }
                $$18.visible = $$18.index < ((MerchantMenu)this.menu).getOffers().size();
            }
            RenderSystem.enableDepthTest();
        }
        this.renderTooltip($$0, $$1, $$2);
    }

    private void renderButtonArrows(PoseStack $$0, MerchantOffer $$1, int $$2, int $$3) {
        RenderSystem.enableBlend();
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, VILLAGER_LOCATION);
        if ($$1.isOutOfStock()) {
            MerchantScreen.blit($$0, $$2 + 5 + 35 + 20, $$3 + 3, this.getBlitOffset(), 25.0f, 171.0f, 10, 9, 512, 256);
        } else {
            MerchantScreen.blit($$0, $$2 + 5 + 35 + 20, $$3 + 3, this.getBlitOffset(), 15.0f, 171.0f, 10, 9, 512, 256);
        }
    }

    private void renderAndDecorateCostA(PoseStack $$0, ItemStack $$1, ItemStack $$2, int $$3, int $$4) {
        this.itemRenderer.renderAndDecorateFakeItem($$1, $$3, $$4);
        if ($$2.getCount() == $$1.getCount()) {
            this.itemRenderer.renderGuiItemDecorations(this.font, $$1, $$3, $$4);
        } else {
            this.itemRenderer.renderGuiItemDecorations(this.font, $$2, $$3, $$4, $$2.getCount() == 1 ? "1" : null);
            this.itemRenderer.renderGuiItemDecorations(this.font, $$1, $$3 + 14, $$4, $$1.getCount() == 1 ? "1" : null);
            RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
            RenderSystem.setShaderTexture(0, VILLAGER_LOCATION);
            this.setBlitOffset(this.getBlitOffset() + 300);
            MerchantScreen.blit($$0, $$3 + 7, $$4 + 12, this.getBlitOffset(), 0.0f, 176.0f, 9, 2, 512, 256);
            this.setBlitOffset(this.getBlitOffset() - 300);
        }
    }

    private boolean canScroll(int $$0) {
        return $$0 > 7;
    }

    @Override
    public boolean mouseScrolled(double $$0, double $$1, double $$2) {
        int $$3 = ((MerchantMenu)this.menu).getOffers().size();
        if (this.canScroll($$3)) {
            int $$4 = $$3 - 7;
            this.scrollOff = Mth.clamp((int)((double)this.scrollOff - $$2), 0, $$4);
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double $$0, double $$1, int $$2, double $$3, double $$4) {
        int $$5 = ((MerchantMenu)this.menu).getOffers().size();
        if (this.isDragging) {
            int $$6 = this.topPos + 18;
            int $$7 = $$6 + 139;
            int $$8 = $$5 - 7;
            float $$9 = ((float)$$1 - (float)$$6 - 13.5f) / ((float)($$7 - $$6) - 27.0f);
            $$9 = $$9 * (float)$$8 + 0.5f;
            this.scrollOff = Mth.clamp((int)$$9, 0, $$8);
            return true;
        }
        return super.mouseDragged($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        this.isDragging = false;
        int $$3 = (this.width - this.imageWidth) / 2;
        int $$4 = (this.height - this.imageHeight) / 2;
        if (this.canScroll(((MerchantMenu)this.menu).getOffers().size()) && $$0 > (double)($$3 + 94) && $$0 < (double)($$3 + 94 + 6) && $$1 > (double)($$4 + 18) && $$1 <= (double)($$4 + 18 + 139 + 1)) {
            this.isDragging = true;
        }
        return super.mouseClicked($$0, $$1, $$2);
    }

    class TradeOfferButton
    extends Button {
        final int index;

        public TradeOfferButton(int $$0, int $$1, int $$2, Button.OnPress $$3) {
            super($$0, $$1, 89, 20, CommonComponents.EMPTY, $$3, DEFAULT_NARRATION);
            this.index = $$2;
            this.visible = false;
        }

        public int getIndex() {
            return this.index;
        }

        public void renderToolTip(PoseStack $$0, int $$1, int $$2) {
            if (this.isHovered && ((MerchantMenu)MerchantScreen.this.menu).getOffers().size() > this.index + MerchantScreen.this.scrollOff) {
                if ($$1 < this.getX() + 20) {
                    ItemStack $$3 = ((MerchantOffer)((MerchantMenu)MerchantScreen.this.menu).getOffers().get(this.index + MerchantScreen.this.scrollOff)).getCostA();
                    MerchantScreen.this.renderTooltip($$0, $$3, $$1, $$2);
                } else if ($$1 < this.getX() + 50 && $$1 > this.getX() + 30) {
                    ItemStack $$4 = ((MerchantOffer)((MerchantMenu)MerchantScreen.this.menu).getOffers().get(this.index + MerchantScreen.this.scrollOff)).getCostB();
                    if (!$$4.isEmpty()) {
                        MerchantScreen.this.renderTooltip($$0, $$4, $$1, $$2);
                    }
                } else if ($$1 > this.getX() + 65) {
                    ItemStack $$5 = ((MerchantOffer)((MerchantMenu)MerchantScreen.this.menu).getOffers().get(this.index + MerchantScreen.this.scrollOff)).getResult();
                    MerchantScreen.this.renderTooltip($$0, $$5, $$1, $$2);
                }
            }
        }
    }
}