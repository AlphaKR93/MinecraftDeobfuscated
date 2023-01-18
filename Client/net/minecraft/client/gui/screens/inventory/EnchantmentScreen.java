/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.List
 *  java.util.function.Supplier
 *  net.minecraft.world.item.ItemStack
 *  org.joml.Matrix4f
 */
package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EnchantmentNames;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.joml.Matrix4f;

public class EnchantmentScreen
extends AbstractContainerScreen<EnchantmentMenu> {
    private static final ResourceLocation ENCHANTING_TABLE_LOCATION = new ResourceLocation("textures/gui/container/enchanting_table.png");
    private static final ResourceLocation ENCHANTING_BOOK_LOCATION = new ResourceLocation("textures/entity/enchanting_table_book.png");
    private final RandomSource random = RandomSource.create();
    private BookModel bookModel;
    public int time;
    public float flip;
    public float oFlip;
    public float flipT;
    public float flipA;
    public float open;
    public float oOpen;
    private ItemStack last = ItemStack.EMPTY;

    public EnchantmentScreen(EnchantmentMenu $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2);
    }

    @Override
    protected void init() {
        super.init();
        this.bookModel = new BookModel(this.minecraft.getEntityModels().bakeLayer(ModelLayers.BOOK));
    }

    @Override
    public void containerTick() {
        super.containerTick();
        this.tickBook();
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        int $$3 = (this.width - this.imageWidth) / 2;
        int $$4 = (this.height - this.imageHeight) / 2;
        for (int $$5 = 0; $$5 < 3; ++$$5) {
            double $$6 = $$0 - (double)($$3 + 60);
            double $$7 = $$1 - (double)($$4 + 14 + 19 * $$5);
            if (!($$6 >= 0.0) || !($$7 >= 0.0) || !($$6 < 108.0) || !($$7 < 19.0) || !((EnchantmentMenu)this.menu).clickMenuButton(this.minecraft.player, $$5)) continue;
            this.minecraft.gameMode.handleInventoryButtonClick(((EnchantmentMenu)this.menu).containerId, $$5);
            return true;
        }
        return super.mouseClicked($$0, $$1, $$2);
    }

    @Override
    protected void renderBg(PoseStack $$0, float $$1, int $$2, int $$3) {
        Lighting.setupForFlatItems();
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, ENCHANTING_TABLE_LOCATION);
        int $$4 = (this.width - this.imageWidth) / 2;
        int $$5 = (this.height - this.imageHeight) / 2;
        this.blit($$0, $$4, $$5, 0, 0, this.imageWidth, this.imageHeight);
        int $$6 = (int)this.minecraft.getWindow().getGuiScale();
        RenderSystem.viewport((this.width - 320) / 2 * $$6, (this.height - 240) / 2 * $$6, 320 * $$6, 240 * $$6);
        Matrix4f $$7 = new Matrix4f().translation(-0.34f, 0.23f, 0.0f).perspective(1.5707964f, 1.3333334f, 9.0f, 80.0f);
        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix($$7);
        $$0.pushPose();
        $$0.setIdentity();
        $$0.translate(0.0f, 3.3f, 1984.0f);
        float $$8 = 5.0f;
        $$0.scale(5.0f, 5.0f, 5.0f);
        $$0.mulPose(Axis.ZP.rotationDegrees(180.0f));
        $$0.mulPose(Axis.XP.rotationDegrees(20.0f));
        float $$9 = Mth.lerp($$1, this.oOpen, this.open);
        $$0.translate((1.0f - $$9) * 0.2f, (1.0f - $$9) * 0.1f, (1.0f - $$9) * 0.25f);
        float $$10 = -(1.0f - $$9) * 90.0f - 90.0f;
        $$0.mulPose(Axis.YP.rotationDegrees($$10));
        $$0.mulPose(Axis.XP.rotationDegrees(180.0f));
        float $$11 = Mth.lerp($$1, this.oFlip, this.flip) + 0.25f;
        float $$12 = Mth.lerp($$1, this.oFlip, this.flip) + 0.75f;
        $$11 = ($$11 - (float)Mth.fastFloor($$11)) * 1.6f - 0.3f;
        $$12 = ($$12 - (float)Mth.fastFloor($$12)) * 1.6f - 0.3f;
        if ($$11 < 0.0f) {
            $$11 = 0.0f;
        }
        if ($$12 < 0.0f) {
            $$12 = 0.0f;
        }
        if ($$11 > 1.0f) {
            $$11 = 1.0f;
        }
        if ($$12 > 1.0f) {
            $$12 = 1.0f;
        }
        this.bookModel.setupAnim(0.0f, $$11, $$12, $$9);
        MultiBufferSource.BufferSource $$13 = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        VertexConsumer $$14 = $$13.getBuffer(this.bookModel.renderType(ENCHANTING_BOOK_LOCATION));
        this.bookModel.renderToBuffer($$0, $$14, 0xF000F0, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        $$13.endBatch();
        $$0.popPose();
        RenderSystem.viewport(0, 0, this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
        RenderSystem.restoreProjectionMatrix();
        Lighting.setupFor3DItems();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        EnchantmentNames.getInstance().initSeed(((EnchantmentMenu)this.menu).getEnchantmentSeed());
        int $$15 = ((EnchantmentMenu)this.menu).getGoldCount();
        for (int $$16 = 0; $$16 < 3; ++$$16) {
            int $$17 = $$4 + 60;
            int $$18 = $$17 + 20;
            this.setBlitOffset(0);
            RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
            RenderSystem.setShaderTexture(0, ENCHANTING_TABLE_LOCATION);
            int $$19 = ((EnchantmentMenu)this.menu).costs[$$16];
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            if ($$19 == 0) {
                this.blit($$0, $$17, $$5 + 14 + 19 * $$16, 0, 185, 108, 19);
                continue;
            }
            String $$20 = "" + $$19;
            int $$21 = 86 - this.font.width($$20);
            FormattedText $$22 = EnchantmentNames.getInstance().getRandomName(this.font, $$21);
            int $$23 = 6839882;
            if (!($$15 >= $$16 + 1 && this.minecraft.player.experienceLevel >= $$19 || this.minecraft.player.getAbilities().instabuild)) {
                this.blit($$0, $$17, $$5 + 14 + 19 * $$16, 0, 185, 108, 19);
                this.blit($$0, $$17 + 1, $$5 + 15 + 19 * $$16, 16 * $$16, 239, 16, 16);
                this.font.drawWordWrap($$22, $$18, $$5 + 16 + 19 * $$16, $$21, ($$23 & 0xFEFEFE) >> 1);
                $$23 = 4226832;
            } else {
                int $$24 = $$2 - ($$4 + 60);
                int $$25 = $$3 - ($$5 + 14 + 19 * $$16);
                if ($$24 >= 0 && $$25 >= 0 && $$24 < 108 && $$25 < 19) {
                    this.blit($$0, $$17, $$5 + 14 + 19 * $$16, 0, 204, 108, 19);
                    $$23 = 0xFFFF80;
                } else {
                    this.blit($$0, $$17, $$5 + 14 + 19 * $$16, 0, 166, 108, 19);
                }
                this.blit($$0, $$17 + 1, $$5 + 15 + 19 * $$16, 16 * $$16, 223, 16, 16);
                this.font.drawWordWrap($$22, $$18, $$5 + 16 + 19 * $$16, $$21, $$23);
                $$23 = 8453920;
            }
            this.font.drawShadow($$0, $$20, (float)($$18 + 86 - this.font.width($$20)), (float)($$5 + 16 + 19 * $$16 + 7), $$23);
        }
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        $$3 = this.minecraft.getFrameTime();
        this.renderBackground($$0);
        super.render($$0, $$1, $$2, $$3);
        this.renderTooltip($$0, $$1, $$2);
        boolean $$4 = this.minecraft.player.getAbilities().instabuild;
        int $$5 = ((EnchantmentMenu)this.menu).getGoldCount();
        for (int $$6 = 0; $$6 < 3; ++$$6) {
            int $$7 = ((EnchantmentMenu)this.menu).costs[$$6];
            Enchantment $$8 = Enchantment.byId(((EnchantmentMenu)this.menu).enchantClue[$$6]);
            int $$9 = ((EnchantmentMenu)this.menu).levelClue[$$6];
            int $$10 = $$6 + 1;
            if (!this.isHovering(60, 14 + 19 * $$6, 108, 17, $$1, $$2) || $$7 <= 0 || $$9 < 0 || $$8 == null) continue;
            ArrayList $$11 = Lists.newArrayList();
            $$11.add((Object)Component.translatable("container.enchant.clue", $$8.getFullname($$9)).withStyle(ChatFormatting.WHITE));
            if (!$$4) {
                $$11.add((Object)CommonComponents.EMPTY);
                if (this.minecraft.player.experienceLevel < $$7) {
                    $$11.add((Object)Component.translatable("container.enchant.level.requirement", ((EnchantmentMenu)this.menu).costs[$$6]).withStyle(ChatFormatting.RED));
                } else {
                    MutableComponent $$15;
                    MutableComponent $$13;
                    if ($$10 == 1) {
                        MutableComponent $$12 = Component.translatable("container.enchant.lapis.one");
                    } else {
                        $$13 = Component.translatable("container.enchant.lapis.many", $$10);
                    }
                    $$11.add((Object)$$13.withStyle($$5 >= $$10 ? ChatFormatting.GRAY : ChatFormatting.RED));
                    if ($$10 == 1) {
                        MutableComponent $$14 = Component.translatable("container.enchant.level.one");
                    } else {
                        $$15 = Component.translatable("container.enchant.level.many", $$10);
                    }
                    $$11.add((Object)$$15.withStyle(ChatFormatting.GRAY));
                }
            }
            this.renderComponentTooltip($$0, (List<Component>)$$11, $$1, $$2);
            break;
        }
    }

    public void tickBook() {
        ItemStack $$0 = ((EnchantmentMenu)this.menu).getSlot(0).getItem();
        if (!ItemStack.matches((ItemStack)$$0, (ItemStack)this.last)) {
            this.last = $$0;
            do {
                this.flipT += (float)(this.random.nextInt(4) - this.random.nextInt(4));
            } while (this.flip <= this.flipT + 1.0f && this.flip >= this.flipT - 1.0f);
        }
        ++this.time;
        this.oFlip = this.flip;
        this.oOpen = this.open;
        boolean $$1 = false;
        for (int $$2 = 0; $$2 < 3; ++$$2) {
            if (((EnchantmentMenu)this.menu).costs[$$2] == 0) continue;
            $$1 = true;
        }
        this.open = $$1 ? (this.open += 0.2f) : (this.open -= 0.2f);
        this.open = Mth.clamp(this.open, 0.0f, 1.0f);
        float $$3 = (this.flipT - this.flip) * 0.4f;
        float $$4 = 0.2f;
        $$3 = Mth.clamp($$3, -0.2f, 0.2f);
        this.flipA += ($$3 - this.flipA) * 0.9f;
        this.flip += this.flipA;
    }
}