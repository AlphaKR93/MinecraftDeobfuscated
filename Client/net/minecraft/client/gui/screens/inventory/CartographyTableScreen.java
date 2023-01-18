/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.lang.Integer
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class CartographyTableScreen
extends AbstractContainerScreen<CartographyTableMenu> {
    private static final ResourceLocation BG_LOCATION = new ResourceLocation("textures/gui/container/cartography_table.png");

    public CartographyTableScreen(CartographyTableMenu $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2);
        this.titleLabelY -= 2;
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        this.renderTooltip($$0, $$1, $$2);
    }

    @Override
    protected void renderBg(PoseStack $$0, float $$1, int $$2, int $$3) {
        MapItemSavedData $$15;
        Integer $$14;
        this.renderBackground($$0);
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, BG_LOCATION);
        int $$4 = this.leftPos;
        int $$5 = this.topPos;
        this.blit($$0, $$4, $$5, 0, 0, this.imageWidth, this.imageHeight);
        ItemStack $$6 = ((CartographyTableMenu)this.menu).getSlot(1).getItem();
        boolean $$7 = $$6.is(Items.MAP);
        boolean $$8 = $$6.is(Items.PAPER);
        boolean $$9 = $$6.is(Items.GLASS_PANE);
        ItemStack $$10 = ((CartographyTableMenu)this.menu).getSlot(0).getItem();
        boolean $$11 = false;
        if ($$10.is(Items.FILLED_MAP)) {
            Integer $$12 = MapItem.getMapId($$10);
            MapItemSavedData $$13 = MapItem.getSavedData($$12, (Level)this.minecraft.level);
            if ($$13 != null) {
                if ($$13.locked) {
                    $$11 = true;
                    if ($$8 || $$9) {
                        this.blit($$0, $$4 + 35, $$5 + 31, this.imageWidth + 50, 132, 28, 21);
                    }
                }
                if ($$8 && $$13.scale >= 4) {
                    $$11 = true;
                    this.blit($$0, $$4 + 35, $$5 + 31, this.imageWidth + 50, 132, 28, 21);
                }
            }
        } else {
            $$14 = null;
            $$15 = null;
        }
        this.renderResultingMap($$0, $$14, $$15, $$7, $$8, $$9, $$11);
    }

    private void renderResultingMap(PoseStack $$0, @Nullable Integer $$1, @Nullable MapItemSavedData $$2, boolean $$3, boolean $$4, boolean $$5, boolean $$6) {
        int $$7 = this.leftPos;
        int $$8 = this.topPos;
        if ($$4 && !$$6) {
            this.blit($$0, $$7 + 67, $$8 + 13, this.imageWidth, 66, 66, 66);
            this.renderMap($$0, $$1, $$2, $$7 + 85, $$8 + 31, 0.226f);
        } else if ($$3) {
            this.blit($$0, $$7 + 67 + 16, $$8 + 13, this.imageWidth, 132, 50, 66);
            this.renderMap($$0, $$1, $$2, $$7 + 86, $$8 + 16, 0.34f);
            RenderSystem.setShaderTexture(0, BG_LOCATION);
            $$0.pushPose();
            $$0.translate(0.0f, 0.0f, 1.0f);
            this.blit($$0, $$7 + 67, $$8 + 13 + 16, this.imageWidth, 132, 50, 66);
            this.renderMap($$0, $$1, $$2, $$7 + 70, $$8 + 32, 0.34f);
            $$0.popPose();
        } else if ($$5) {
            this.blit($$0, $$7 + 67, $$8 + 13, this.imageWidth, 0, 66, 66);
            this.renderMap($$0, $$1, $$2, $$7 + 71, $$8 + 17, 0.45f);
            RenderSystem.setShaderTexture(0, BG_LOCATION);
            $$0.pushPose();
            $$0.translate(0.0f, 0.0f, 1.0f);
            this.blit($$0, $$7 + 66, $$8 + 12, 0, this.imageHeight, 66, 66);
            $$0.popPose();
        } else {
            this.blit($$0, $$7 + 67, $$8 + 13, this.imageWidth, 0, 66, 66);
            this.renderMap($$0, $$1, $$2, $$7 + 71, $$8 + 17, 0.45f);
        }
    }

    private void renderMap(PoseStack $$0, @Nullable Integer $$1, @Nullable MapItemSavedData $$2, int $$3, int $$4, float $$5) {
        if ($$1 != null && $$2 != null) {
            $$0.pushPose();
            $$0.translate($$3, $$4, 1.0f);
            $$0.scale($$5, $$5, 1.0f);
            MultiBufferSource.BufferSource $$6 = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            this.minecraft.gameRenderer.getMapRenderer().render($$0, $$6, $$1, $$2, true, 0xF000F0);
            $$6.endBatch();
            $$0.popPose();
        }
    }
}