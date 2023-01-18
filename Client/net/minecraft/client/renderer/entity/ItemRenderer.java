/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.util.List
 *  java.util.Set
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.client.renderer.entity;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.math.MatrixUtil;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;

public class ItemRenderer
implements ResourceManagerReloadListener {
    public static final ResourceLocation ENCHANT_GLINT_LOCATION = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private static final Set<Item> IGNORED = Sets.newHashSet((Object[])new Item[]{Items.AIR});
    private static final int GUI_SLOT_CENTER_X = 8;
    private static final int GUI_SLOT_CENTER_Y = 8;
    public static final int ITEM_COUNT_BLIT_OFFSET = 200;
    public static final float COMPASS_FOIL_UI_SCALE = 0.5f;
    public static final float COMPASS_FOIL_FIRST_PERSON_SCALE = 0.75f;
    public static final float COMPASS_FOIL_TEXTURE_SCALE = 0.0078125f;
    private static final ModelResourceLocation TRIDENT_MODEL = ModelResourceLocation.vanilla("trident", "inventory");
    public static final ModelResourceLocation TRIDENT_IN_HAND_MODEL = ModelResourceLocation.vanilla("trident_in_hand", "inventory");
    private static final ModelResourceLocation SPYGLASS_MODEL = ModelResourceLocation.vanilla("spyglass", "inventory");
    public static final ModelResourceLocation SPYGLASS_IN_HAND_MODEL = ModelResourceLocation.vanilla("spyglass_in_hand", "inventory");
    public float blitOffset;
    private final ItemModelShaper itemModelShaper;
    private final TextureManager textureManager;
    private final ItemColors itemColors;
    private final BlockEntityWithoutLevelRenderer blockEntityRenderer;

    public ItemRenderer(TextureManager $$0, ModelManager $$1, ItemColors $$2, BlockEntityWithoutLevelRenderer $$3) {
        this.textureManager = $$0;
        this.itemModelShaper = new ItemModelShaper($$1);
        this.blockEntityRenderer = $$3;
        for (Item $$4 : BuiltInRegistries.ITEM) {
            if (IGNORED.contains((Object)$$4)) continue;
            this.itemModelShaper.register($$4, new ModelResourceLocation(BuiltInRegistries.ITEM.getKey($$4), "inventory"));
        }
        this.itemColors = $$2;
    }

    public ItemModelShaper getItemModelShaper() {
        return this.itemModelShaper;
    }

    private void renderModelLists(BakedModel $$0, ItemStack $$1, int $$2, int $$3, PoseStack $$4, VertexConsumer $$5) {
        RandomSource $$6 = RandomSource.create();
        long $$7 = 42L;
        for (Direction $$8 : Direction.values()) {
            $$6.setSeed(42L);
            this.renderQuadList($$4, $$5, $$0.getQuads(null, $$8, $$6), $$1, $$2, $$3);
        }
        $$6.setSeed(42L);
        this.renderQuadList($$4, $$5, $$0.getQuads(null, null, $$6), $$1, $$2, $$3);
    }

    public void render(ItemStack $$0, ItemTransforms.TransformType $$1, boolean $$2, PoseStack $$3, MultiBufferSource $$4, int $$5, int $$6, BakedModel $$7) {
        boolean $$8;
        if ($$0.isEmpty()) {
            return;
        }
        $$3.pushPose();
        boolean bl = $$8 = $$1 == ItemTransforms.TransformType.GUI || $$1 == ItemTransforms.TransformType.GROUND || $$1 == ItemTransforms.TransformType.FIXED;
        if ($$8) {
            if ($$0.is(Items.TRIDENT)) {
                $$7 = this.itemModelShaper.getModelManager().getModel(TRIDENT_MODEL);
            } else if ($$0.is(Items.SPYGLASS)) {
                $$7 = this.itemModelShaper.getModelManager().getModel(SPYGLASS_MODEL);
            }
        }
        $$7.getTransforms().getTransform($$1).apply($$2, $$3);
        $$3.translate(-0.5f, -0.5f, -0.5f);
        if ($$7.isCustomRenderer() || $$0.is(Items.TRIDENT) && !$$8) {
            this.blockEntityRenderer.renderByItem($$0, $$1, $$3, $$4, $$5, $$6);
        } else {
            VertexConsumer $$17;
            boolean $$11;
            if ($$1 != ItemTransforms.TransformType.GUI && !$$1.firstPerson() && $$0.getItem() instanceof BlockItem) {
                Block $$9 = ((BlockItem)$$0.getItem()).getBlock();
                boolean $$10 = !($$9 instanceof HalfTransparentBlock) && !($$9 instanceof StainedGlassPaneBlock);
            } else {
                $$11 = true;
            }
            RenderType $$12 = ItemBlockRenderTypes.getRenderType($$0, $$11);
            if ($$0.is(ItemTags.COMPASSES) && $$0.hasFoil()) {
                $$3.pushPose();
                PoseStack.Pose $$13 = $$3.last();
                if ($$1 == ItemTransforms.TransformType.GUI) {
                    MatrixUtil.mulComponentWise($$13.pose(), 0.5f);
                } else if ($$1.firstPerson()) {
                    MatrixUtil.mulComponentWise($$13.pose(), 0.75f);
                }
                if ($$11) {
                    VertexConsumer $$14 = ItemRenderer.getCompassFoilBufferDirect($$4, $$12, $$13);
                } else {
                    VertexConsumer $$15 = ItemRenderer.getCompassFoilBuffer($$4, $$12, $$13);
                }
                $$3.popPose();
            } else if ($$11) {
                VertexConsumer $$16 = ItemRenderer.getFoilBufferDirect($$4, $$12, true, $$0.hasFoil());
            } else {
                $$17 = ItemRenderer.getFoilBuffer($$4, $$12, true, $$0.hasFoil());
            }
            this.renderModelLists($$7, $$0, $$5, $$6, $$3, $$17);
        }
        $$3.popPose();
    }

    public static VertexConsumer getArmorFoilBuffer(MultiBufferSource $$0, RenderType $$1, boolean $$2, boolean $$3) {
        if ($$3) {
            return VertexMultiConsumer.create($$0.getBuffer($$2 ? RenderType.armorGlint() : RenderType.armorEntityGlint()), $$0.getBuffer($$1));
        }
        return $$0.getBuffer($$1);
    }

    public static VertexConsumer getCompassFoilBuffer(MultiBufferSource $$0, RenderType $$1, PoseStack.Pose $$2) {
        return VertexMultiConsumer.create((VertexConsumer)new SheetedDecalTextureGenerator($$0.getBuffer(RenderType.glint()), $$2.pose(), $$2.normal(), 0.0078125f), $$0.getBuffer($$1));
    }

    public static VertexConsumer getCompassFoilBufferDirect(MultiBufferSource $$0, RenderType $$1, PoseStack.Pose $$2) {
        return VertexMultiConsumer.create((VertexConsumer)new SheetedDecalTextureGenerator($$0.getBuffer(RenderType.glintDirect()), $$2.pose(), $$2.normal(), 0.0078125f), $$0.getBuffer($$1));
    }

    public static VertexConsumer getFoilBuffer(MultiBufferSource $$0, RenderType $$1, boolean $$2, boolean $$3) {
        if ($$3) {
            if (Minecraft.useShaderTransparency() && $$1 == Sheets.translucentItemSheet()) {
                return VertexMultiConsumer.create($$0.getBuffer(RenderType.glintTranslucent()), $$0.getBuffer($$1));
            }
            return VertexMultiConsumer.create($$0.getBuffer($$2 ? RenderType.glint() : RenderType.entityGlint()), $$0.getBuffer($$1));
        }
        return $$0.getBuffer($$1);
    }

    public static VertexConsumer getFoilBufferDirect(MultiBufferSource $$0, RenderType $$1, boolean $$2, boolean $$3) {
        if ($$3) {
            return VertexMultiConsumer.create($$0.getBuffer($$2 ? RenderType.glintDirect() : RenderType.entityGlintDirect()), $$0.getBuffer($$1));
        }
        return $$0.getBuffer($$1);
    }

    private void renderQuadList(PoseStack $$0, VertexConsumer $$1, List<BakedQuad> $$2, ItemStack $$3, int $$4, int $$5) {
        boolean $$6 = !$$3.isEmpty();
        PoseStack.Pose $$7 = $$0.last();
        for (BakedQuad $$8 : $$2) {
            int $$9 = -1;
            if ($$6 && $$8.isTinted()) {
                $$9 = this.itemColors.getColor($$3, $$8.getTintIndex());
            }
            float $$10 = (float)($$9 >> 16 & 0xFF) / 255.0f;
            float $$11 = (float)($$9 >> 8 & 0xFF) / 255.0f;
            float $$12 = (float)($$9 & 0xFF) / 255.0f;
            $$1.putBulkData($$7, $$8, $$10, $$11, $$12, $$4, $$5);
        }
    }

    public BakedModel getModel(ItemStack $$0, @Nullable Level $$1, @Nullable LivingEntity $$2, int $$3) {
        BakedModel $$6;
        if ($$0.is(Items.TRIDENT)) {
            BakedModel $$4 = this.itemModelShaper.getModelManager().getModel(TRIDENT_IN_HAND_MODEL);
        } else if ($$0.is(Items.SPYGLASS)) {
            BakedModel $$5 = this.itemModelShaper.getModelManager().getModel(SPYGLASS_IN_HAND_MODEL);
        } else {
            $$6 = this.itemModelShaper.getItemModel($$0);
        }
        ClientLevel $$7 = $$1 instanceof ClientLevel ? (ClientLevel)$$1 : null;
        BakedModel $$8 = $$6.getOverrides().resolve($$6, $$0, $$7, $$2, $$3);
        return $$8 == null ? this.itemModelShaper.getModelManager().getMissingModel() : $$8;
    }

    public void renderStatic(ItemStack $$0, ItemTransforms.TransformType $$1, int $$2, int $$3, PoseStack $$4, MultiBufferSource $$5, int $$6) {
        this.renderStatic(null, $$0, $$1, false, $$4, $$5, null, $$2, $$3, $$6);
    }

    public void renderStatic(@Nullable LivingEntity $$0, ItemStack $$1, ItemTransforms.TransformType $$2, boolean $$3, PoseStack $$4, MultiBufferSource $$5, @Nullable Level $$6, int $$7, int $$8, int $$9) {
        if ($$1.isEmpty()) {
            return;
        }
        BakedModel $$10 = this.getModel($$1, $$6, $$0, $$9);
        this.render($$1, $$2, $$3, $$4, $$5, $$7, $$8, $$10);
    }

    public void renderGuiItem(ItemStack $$0, int $$1, int $$2) {
        this.renderGuiItem($$0, $$1, $$2, this.getModel($$0, null, null, 0));
    }

    protected void renderGuiItem(ItemStack $$0, int $$1, int $$2, BakedModel $$3) {
        boolean $$7;
        this.textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        PoseStack $$4 = RenderSystem.getModelViewStack();
        $$4.pushPose();
        $$4.translate($$1, $$2, 100.0f + this.blitOffset);
        $$4.translate(8.0f, 8.0f, 0.0f);
        $$4.scale(1.0f, -1.0f, 1.0f);
        $$4.scale(16.0f, 16.0f, 16.0f);
        RenderSystem.applyModelViewMatrix();
        PoseStack $$5 = new PoseStack();
        MultiBufferSource.BufferSource $$6 = Minecraft.getInstance().renderBuffers().bufferSource();
        boolean bl = $$7 = !$$3.usesBlockLight();
        if ($$7) {
            Lighting.setupForFlatItems();
        }
        this.render($$0, ItemTransforms.TransformType.GUI, false, $$5, $$6, 0xF000F0, OverlayTexture.NO_OVERLAY, $$3);
        $$6.endBatch();
        RenderSystem.enableDepthTest();
        if ($$7) {
            Lighting.setupFor3DItems();
        }
        $$4.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    public void renderAndDecorateItem(ItemStack $$0, int $$1, int $$2) {
        this.tryRenderGuiItem(Minecraft.getInstance().player, $$0, $$1, $$2, 0);
    }

    public void renderAndDecorateItem(ItemStack $$0, int $$1, int $$2, int $$3) {
        this.tryRenderGuiItem(Minecraft.getInstance().player, $$0, $$1, $$2, $$3);
    }

    public void renderAndDecorateItem(ItemStack $$0, int $$1, int $$2, int $$3, int $$4) {
        this.tryRenderGuiItem(Minecraft.getInstance().player, $$0, $$1, $$2, $$3, $$4);
    }

    public void renderAndDecorateFakeItem(ItemStack $$0, int $$1, int $$2) {
        this.tryRenderGuiItem(null, $$0, $$1, $$2, 0);
    }

    public void renderAndDecorateItem(LivingEntity $$0, ItemStack $$1, int $$2, int $$3, int $$4) {
        this.tryRenderGuiItem($$0, $$1, $$2, $$3, $$4);
    }

    private void tryRenderGuiItem(@Nullable LivingEntity $$0, ItemStack $$1, int $$2, int $$3, int $$4) {
        this.tryRenderGuiItem($$0, $$1, $$2, $$3, $$4, 0);
    }

    private void tryRenderGuiItem(@Nullable LivingEntity $$0, ItemStack $$1, int $$2, int $$3, int $$4, int $$5) {
        if ($$1.isEmpty()) {
            return;
        }
        BakedModel $$6 = this.getModel($$1, null, $$0, $$4);
        this.blitOffset = $$6.isGui3d() ? this.blitOffset + 50.0f + (float)$$5 : this.blitOffset + 50.0f;
        try {
            this.renderGuiItem($$1, $$2, $$3, $$6);
        }
        catch (Throwable $$7) {
            CrashReport $$8 = CrashReport.forThrowable($$7, "Rendering item");
            CrashReportCategory $$9 = $$8.addCategory("Item being rendered");
            $$9.setDetail("Item Type", () -> String.valueOf((Object)$$1.getItem()));
            $$9.setDetail("Item Damage", () -> String.valueOf((int)$$1.getDamageValue()));
            $$9.setDetail("Item NBT", () -> String.valueOf((Object)$$1.getTag()));
            $$9.setDetail("Item Foil", () -> String.valueOf((boolean)$$1.hasFoil()));
            throw new ReportedException($$8);
        }
        this.blitOffset = $$6.isGui3d() ? this.blitOffset - 50.0f - (float)$$5 : this.blitOffset - 50.0f;
    }

    public void renderGuiItemDecorations(Font $$0, ItemStack $$1, int $$2, int $$3) {
        this.renderGuiItemDecorations($$0, $$1, $$2, $$3, null);
    }

    public void renderGuiItemDecorations(Font $$0, ItemStack $$1, int $$2, int $$3, @Nullable String $$4) {
        LocalPlayer $$12;
        float $$13;
        if ($$1.isEmpty()) {
            return;
        }
        PoseStack $$5 = new PoseStack();
        if ($$1.getCount() != 1 || $$4 != null) {
            String $$6 = $$4 == null ? String.valueOf((int)$$1.getCount()) : $$4;
            $$5.translate(0.0f, 0.0f, this.blitOffset + 200.0f);
            MultiBufferSource.BufferSource $$7 = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            $$0.drawInBatch($$6, (float)($$2 + 19 - 2 - $$0.width($$6)), (float)($$3 + 6 + 3), 0xFFFFFF, true, $$5.last().pose(), (MultiBufferSource)$$7, false, 0, 0xF000F0);
            $$7.endBatch();
        }
        if ($$1.isBarVisible()) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.disableBlend();
            Tesselator $$8 = Tesselator.getInstance();
            BufferBuilder $$9 = $$8.getBuilder();
            int $$10 = $$1.getBarWidth();
            int $$11 = $$1.getBarColor();
            this.fillRect($$9, $$2 + 2, $$3 + 13, 13, 2, 0, 0, 0, 255);
            this.fillRect($$9, $$2 + 2, $$3 + 13, $$10, 1, $$11 >> 16 & 0xFF, $$11 >> 8 & 0xFF, $$11 & 0xFF, 255);
            RenderSystem.enableBlend();
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
        }
        float f = $$13 = ($$12 = Minecraft.getInstance().player) == null ? 0.0f : $$12.getCooldowns().getCooldownPercent($$1.getItem(), Minecraft.getInstance().getFrameTime());
        if ($$13 > 0.0f) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            Tesselator $$14 = Tesselator.getInstance();
            BufferBuilder $$15 = $$14.getBuilder();
            this.fillRect($$15, $$2, $$3 + Mth.floor(16.0f * (1.0f - $$13)), 16, Mth.ceil(16.0f * $$13), 255, 255, 255, 127);
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();
        }
    }

    private void fillRect(BufferBuilder $$0, int $$1, int $$2, int $$3, int $$4, int $$5, int $$6, int $$7, int $$8) {
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionColorShader));
        $$0.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        $$0.vertex($$1 + 0, $$2 + 0, 0.0).color($$5, $$6, $$7, $$8).endVertex();
        $$0.vertex($$1 + 0, $$2 + $$4, 0.0).color($$5, $$6, $$7, $$8).endVertex();
        $$0.vertex($$1 + $$3, $$2 + $$4, 0.0).color($$5, $$6, $$7, $$8).endVertex();
        $$0.vertex($$1 + $$3, $$2 + 0, 0.0).color($$5, $$6, $$7, $$8).endVertex();
        BufferUploader.drawWithShader($$0.end());
    }

    @Override
    public void onResourceManagerReload(ResourceManager $$0) {
        this.itemModelShaper.rebuildCache();
    }
}