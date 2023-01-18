/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.datafixers.util.Pair
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.util.List
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BannerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatterns;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class LoomScreen
extends AbstractContainerScreen<LoomMenu> {
    private static final ResourceLocation BG_LOCATION = new ResourceLocation("textures/gui/container/loom.png");
    private static final int PATTERN_COLUMNS = 4;
    private static final int PATTERN_ROWS = 4;
    private static final int SCROLLER_WIDTH = 12;
    private static final int SCROLLER_HEIGHT = 15;
    private static final int PATTERN_IMAGE_SIZE = 14;
    private static final int SCROLLER_FULL_HEIGHT = 56;
    private static final int PATTERNS_X = 60;
    private static final int PATTERNS_Y = 13;
    private ModelPart flag;
    @Nullable
    private List<Pair<Holder<BannerPattern>, DyeColor>> resultBannerPatterns;
    private ItemStack bannerStack = ItemStack.EMPTY;
    private ItemStack dyeStack = ItemStack.EMPTY;
    private ItemStack patternStack = ItemStack.EMPTY;
    private boolean displayPatterns;
    private boolean hasMaxPatterns;
    private float scrollOffs;
    private boolean scrolling;
    private int startRow;

    public LoomScreen(LoomMenu $$0, Inventory $$1, Component $$2) {
        super($$0, $$1, $$2);
        $$0.registerUpdateListener(this::containerChanged);
        this.titleLabelY -= 2;
    }

    @Override
    protected void init() {
        super.init();
        this.flag = this.minecraft.getEntityModels().bakeLayer(ModelLayers.BANNER).getChild("flag");
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        super.render($$0, $$1, $$2, $$3);
        this.renderTooltip($$0, $$1, $$2);
    }

    private int totalRowCount() {
        return Mth.positiveCeilDiv(((LoomMenu)this.menu).getSelectablePatterns().size(), 4);
    }

    @Override
    protected void renderBg(PoseStack $$0, float $$1, int $$2, int $$3) {
        this.renderBackground($$0);
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, BG_LOCATION);
        int $$4 = this.leftPos;
        int $$5 = this.topPos;
        this.blit($$0, $$4, $$5, 0, 0, this.imageWidth, this.imageHeight);
        Slot $$6 = ((LoomMenu)this.menu).getBannerSlot();
        Slot $$7 = ((LoomMenu)this.menu).getDyeSlot();
        Slot $$8 = ((LoomMenu)this.menu).getPatternSlot();
        Slot $$9 = ((LoomMenu)this.menu).getResultSlot();
        if (!$$6.hasItem()) {
            this.blit($$0, $$4 + $$6.x, $$5 + $$6.y, this.imageWidth, 0, 16, 16);
        }
        if (!$$7.hasItem()) {
            this.blit($$0, $$4 + $$7.x, $$5 + $$7.y, this.imageWidth + 16, 0, 16, 16);
        }
        if (!$$8.hasItem()) {
            this.blit($$0, $$4 + $$8.x, $$5 + $$8.y, this.imageWidth + 32, 0, 16, 16);
        }
        int $$10 = (int)(41.0f * this.scrollOffs);
        this.blit($$0, $$4 + 119, $$5 + 13 + $$10, 232 + (this.displayPatterns ? 0 : 12), 0, 12, 15);
        Lighting.setupForFlatItems();
        if (this.resultBannerPatterns != null && !this.hasMaxPatterns) {
            MultiBufferSource.BufferSource $$11 = this.minecraft.renderBuffers().bufferSource();
            $$0.pushPose();
            $$0.translate($$4 + 139, $$5 + 52, 0.0f);
            $$0.scale(24.0f, -24.0f, 1.0f);
            $$0.translate(0.5f, 0.5f, 0.5f);
            float $$12 = 0.6666667f;
            $$0.scale(0.6666667f, -0.6666667f, -0.6666667f);
            this.flag.xRot = 0.0f;
            this.flag.y = -32.0f;
            BannerRenderer.renderPatterns($$0, $$11, 0xF000F0, OverlayTexture.NO_OVERLAY, this.flag, ModelBakery.BANNER_BASE, true, this.resultBannerPatterns);
            $$0.popPose();
            $$11.endBatch();
        } else if (this.hasMaxPatterns) {
            this.blit($$0, $$4 + $$9.x - 2, $$5 + $$9.y - 2, this.imageWidth, 17, 17, 16);
        }
        if (this.displayPatterns) {
            int $$13 = $$4 + 60;
            int $$14 = $$5 + 13;
            List<Holder<BannerPattern>> $$15 = ((LoomMenu)this.menu).getSelectablePatterns();
            block0: for (int $$16 = 0; $$16 < 4; ++$$16) {
                for (int $$17 = 0; $$17 < 4; ++$$17) {
                    int $$25;
                    boolean $$22;
                    int $$18 = $$16 + this.startRow;
                    int $$19 = $$18 * 4 + $$17;
                    if ($$19 >= $$15.size()) break block0;
                    RenderSystem.setShaderTexture(0, BG_LOCATION);
                    int $$20 = $$13 + $$17 * 14;
                    int $$21 = $$14 + $$16 * 14;
                    boolean bl = $$22 = $$2 >= $$20 && $$3 >= $$21 && $$2 < $$20 + 14 && $$3 < $$21 + 14;
                    if ($$19 == ((LoomMenu)this.menu).getSelectedBannerPatternIndex()) {
                        int $$23 = this.imageHeight + 14;
                    } else if ($$22) {
                        int $$24 = this.imageHeight + 28;
                    } else {
                        $$25 = this.imageHeight;
                    }
                    this.blit($$0, $$20, $$21, 0, $$25, 14, 14);
                    this.renderPattern((Holder)$$15.get($$19), $$20, $$21);
                }
            }
        }
        Lighting.setupFor3DItems();
    }

    private void renderPattern(Holder<BannerPattern> $$0, int $$1, int $$2) {
        CompoundTag $$3 = new CompoundTag();
        ListTag $$4 = new BannerPattern.Builder().addPattern(BannerPatterns.BASE, DyeColor.GRAY).addPattern($$0, DyeColor.WHITE).toListTag();
        $$3.put("Patterns", $$4);
        ItemStack $$5 = new ItemStack(Items.GRAY_BANNER);
        BlockItem.setBlockEntityData($$5, BlockEntityType.BANNER, $$3);
        PoseStack $$6 = new PoseStack();
        $$6.pushPose();
        $$6.translate((float)$$1 + 0.5f, $$2 + 16, 0.0f);
        $$6.scale(6.0f, -6.0f, 1.0f);
        $$6.translate(0.5f, 0.5f, 0.0f);
        $$6.translate(0.5f, 0.5f, 0.5f);
        float $$7 = 0.6666667f;
        $$6.scale(0.6666667f, -0.6666667f, -0.6666667f);
        MultiBufferSource.BufferSource $$8 = this.minecraft.renderBuffers().bufferSource();
        this.flag.xRot = 0.0f;
        this.flag.y = -32.0f;
        List<Pair<Holder<BannerPattern>, DyeColor>> $$9 = BannerBlockEntity.createPatterns(DyeColor.GRAY, BannerBlockEntity.getItemPatterns($$5));
        BannerRenderer.renderPatterns($$6, $$8, 0xF000F0, OverlayTexture.NO_OVERLAY, this.flag, ModelBakery.BANNER_BASE, true, $$9);
        $$6.popPose();
        $$8.endBatch();
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        this.scrolling = false;
        if (this.displayPatterns) {
            int $$3 = this.leftPos + 60;
            int $$4 = this.topPos + 13;
            for (int $$5 = 0; $$5 < 4; ++$$5) {
                for (int $$6 = 0; $$6 < 4; ++$$6) {
                    double $$7 = $$0 - (double)($$3 + $$6 * 14);
                    double $$8 = $$1 - (double)($$4 + $$5 * 14);
                    int $$9 = $$5 + this.startRow;
                    int $$10 = $$9 * 4 + $$6;
                    if (!($$7 >= 0.0) || !($$8 >= 0.0) || !($$7 < 14.0) || !($$8 < 14.0) || !((LoomMenu)this.menu).clickMenuButton(this.minecraft.player, $$10)) continue;
                    Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0f));
                    this.minecraft.gameMode.handleInventoryButtonClick(((LoomMenu)this.menu).containerId, $$10);
                    return true;
                }
            }
            $$3 = this.leftPos + 119;
            $$4 = this.topPos + 9;
            if ($$0 >= (double)$$3 && $$0 < (double)($$3 + 12) && $$1 >= (double)$$4 && $$1 < (double)($$4 + 56)) {
                this.scrolling = true;
            }
        }
        return super.mouseClicked($$0, $$1, $$2);
    }

    @Override
    public boolean mouseDragged(double $$0, double $$1, int $$2, double $$3, double $$4) {
        int $$5 = this.totalRowCount() - 4;
        if (this.scrolling && this.displayPatterns && $$5 > 0) {
            int $$6 = this.topPos + 13;
            int $$7 = $$6 + 56;
            this.scrollOffs = ((float)$$1 - (float)$$6 - 7.5f) / ((float)($$7 - $$6) - 15.0f);
            this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0f, 1.0f);
            this.startRow = Math.max((int)((int)((double)(this.scrollOffs * (float)$$5) + 0.5)), (int)0);
            return true;
        }
        return super.mouseDragged($$0, $$1, $$2, $$3, $$4);
    }

    @Override
    public boolean mouseScrolled(double $$0, double $$1, double $$2) {
        int $$3 = this.totalRowCount() - 4;
        if (this.displayPatterns && $$3 > 0) {
            float $$4 = (float)$$2 / (float)$$3;
            this.scrollOffs = Mth.clamp(this.scrollOffs - $$4, 0.0f, 1.0f);
            this.startRow = Math.max((int)((int)(this.scrollOffs * (float)$$3 + 0.5f)), (int)0);
        }
        return true;
    }

    @Override
    protected boolean hasClickedOutside(double $$0, double $$1, int $$2, int $$3, int $$4) {
        return $$0 < (double)$$2 || $$1 < (double)$$3 || $$0 >= (double)($$2 + this.imageWidth) || $$1 >= (double)($$3 + this.imageHeight);
    }

    private void containerChanged() {
        ItemStack $$0 = ((LoomMenu)this.menu).getResultSlot().getItem();
        this.resultBannerPatterns = $$0.isEmpty() ? null : BannerBlockEntity.createPatterns(((BannerItem)$$0.getItem()).getColor(), BannerBlockEntity.getItemPatterns($$0));
        ItemStack $$1 = ((LoomMenu)this.menu).getBannerSlot().getItem();
        ItemStack $$2 = ((LoomMenu)this.menu).getDyeSlot().getItem();
        ItemStack $$3 = ((LoomMenu)this.menu).getPatternSlot().getItem();
        CompoundTag $$4 = BlockItem.getBlockEntityData($$1);
        boolean bl = this.hasMaxPatterns = $$4 != null && $$4.contains("Patterns", 9) && !$$1.isEmpty() && $$4.getList("Patterns", 10).size() >= 6;
        if (this.hasMaxPatterns) {
            this.resultBannerPatterns = null;
        }
        if (!(ItemStack.matches($$1, this.bannerStack) && ItemStack.matches($$2, this.dyeStack) && ItemStack.matches($$3, this.patternStack))) {
            boolean bl2 = this.displayPatterns = !$$1.isEmpty() && !$$2.isEmpty() && !this.hasMaxPatterns && !((LoomMenu)this.menu).getSelectablePatterns().isEmpty();
        }
        if (this.startRow >= this.totalRowCount()) {
            this.startRow = 0;
            this.scrollOffs = 0.0f;
        }
        this.bannerStack = $$1.copy();
        this.dyeStack = $$2.copy();
        this.patternStack = $$3.copy();
    }
}