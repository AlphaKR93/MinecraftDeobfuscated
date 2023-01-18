/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Ordering
 *  com.mojang.datafixers.util.Pair
 *  java.lang.Integer
 *  java.lang.Iterable
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Runnable
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.List
 *  java.util.Objects
 *  java.util.function.Supplier
 *  java.util.stream.Collectors
 *  javax.annotation.Nullable
 */
package net.minecraft.client.gui;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.gui.components.SubtitleOverlay;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringUtil;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;

public class Gui
extends GuiComponent {
    private static final ResourceLocation VIGNETTE_LOCATION = new ResourceLocation("textures/misc/vignette.png");
    private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation PUMPKIN_BLUR_LOCATION = new ResourceLocation("textures/misc/pumpkinblur.png");
    private static final ResourceLocation SPYGLASS_SCOPE_LOCATION = new ResourceLocation("textures/misc/spyglass_scope.png");
    private static final ResourceLocation POWDER_SNOW_OUTLINE_LOCATION = new ResourceLocation("textures/misc/powder_snow_outline.png");
    private static final Component DEMO_EXPIRED_TEXT = Component.translatable("demo.demoExpired");
    private static final Component SAVING_TEXT = Component.translatable("menu.savingLevel");
    private static final int COLOR_WHITE = 0xFFFFFF;
    private static final float MIN_CROSSHAIR_ATTACK_SPEED = 5.0f;
    private static final int NUM_HEARTS_PER_ROW = 10;
    private static final int LINE_HEIGHT = 10;
    private static final String SPACER = ": ";
    private static final float PORTAL_OVERLAY_ALPHA_MIN = 0.2f;
    private static final int HEART_SIZE = 9;
    private static final int HEART_SEPARATION = 8;
    private static final float AUTOSAVE_FADE_SPEED_FACTOR = 0.2f;
    private final RandomSource random = RandomSource.create();
    private final Minecraft minecraft;
    private final ItemRenderer itemRenderer;
    private final ChatComponent chat;
    private int tickCount;
    @Nullable
    private Component overlayMessageString;
    private int overlayMessageTime;
    private boolean animateOverlayMessageColor;
    private boolean chatDisabledByPlayerShown;
    public float vignetteBrightness = 1.0f;
    private int toolHighlightTimer;
    private ItemStack lastToolHighlight = ItemStack.EMPTY;
    private final DebugScreenOverlay debugScreen;
    private final SubtitleOverlay subtitleOverlay;
    private final SpectatorGui spectatorGui;
    private final PlayerTabOverlay tabList;
    private final BossHealthOverlay bossOverlay;
    private int titleTime;
    @Nullable
    private Component title;
    @Nullable
    private Component subtitle;
    private int titleFadeInTime;
    private int titleStayTime;
    private int titleFadeOutTime;
    private int lastHealth;
    private int displayHealth;
    private long lastHealthTime;
    private long healthBlinkTime;
    private int screenWidth;
    private int screenHeight;
    private float autosaveIndicatorValue;
    private float lastAutosaveIndicatorValue;
    private float scopeScale;

    public Gui(Minecraft $$0, ItemRenderer $$1) {
        this.minecraft = $$0;
        this.itemRenderer = $$1;
        this.debugScreen = new DebugScreenOverlay($$0);
        this.spectatorGui = new SpectatorGui($$0);
        this.chat = new ChatComponent($$0);
        this.tabList = new PlayerTabOverlay($$0, this);
        this.bossOverlay = new BossHealthOverlay($$0);
        this.subtitleOverlay = new SubtitleOverlay($$0);
        this.resetTitleTimes();
    }

    public void resetTitleTimes() {
        this.titleFadeInTime = 10;
        this.titleStayTime = 70;
        this.titleFadeOutTime = 20;
    }

    public void render(PoseStack $$0, float $$1) {
        float $$6;
        Window $$2 = this.minecraft.getWindow();
        this.screenWidth = $$2.getGuiScaledWidth();
        this.screenHeight = $$2.getGuiScaledHeight();
        Font $$3 = this.getFont();
        RenderSystem.enableBlend();
        if (Minecraft.useFancyGraphics()) {
            this.renderVignette(this.minecraft.getCameraEntity());
        } else {
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.defaultBlendFunc();
        }
        float $$4 = this.minecraft.getDeltaFrameTime();
        this.scopeScale = Mth.lerp(0.5f * $$4, this.scopeScale, 1.125f);
        if (this.minecraft.options.getCameraType().isFirstPerson()) {
            if (this.minecraft.player.isScoping()) {
                this.renderSpyglassOverlay(this.scopeScale);
            } else {
                this.scopeScale = 0.5f;
                ItemStack $$5 = this.minecraft.player.getInventory().getArmor(3);
                if ($$5.is(Blocks.CARVED_PUMPKIN.asItem())) {
                    this.renderTextureOverlay(PUMPKIN_BLUR_LOCATION, 1.0f);
                }
            }
        }
        if (this.minecraft.player.getTicksFrozen() > 0) {
            this.renderTextureOverlay(POWDER_SNOW_OUTLINE_LOCATION, this.minecraft.player.getPercentFrozen());
        }
        if (($$6 = Mth.lerp($$1, this.minecraft.player.oPortalTime, this.minecraft.player.portalTime)) > 0.0f && !this.minecraft.player.hasEffect(MobEffects.CONFUSION)) {
            this.renderPortalOverlay($$6);
        }
        if (this.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            this.spectatorGui.renderHotbar($$0);
        } else if (!this.minecraft.options.hideGui) {
            this.renderHotbar($$1, $$0);
        }
        if (!this.minecraft.options.hideGui) {
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
            RenderSystem.setShaderTexture(0, GUI_ICONS_LOCATION);
            RenderSystem.enableBlend();
            this.renderCrosshair($$0);
            RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
            RenderSystem.defaultBlendFunc();
            this.minecraft.getProfiler().push("bossHealth");
            this.bossOverlay.render($$0);
            this.minecraft.getProfiler().pop();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, GUI_ICONS_LOCATION);
            if (this.minecraft.gameMode.canHurtPlayer()) {
                this.renderPlayerHealth($$0);
            }
            this.renderVehicleHealth($$0);
            RenderSystem.disableBlend();
            int $$7 = this.screenWidth / 2 - 91;
            PlayerRideableJumping $$8 = this.minecraft.player.jumpableVehicle();
            if ($$8 != null) {
                this.renderJumpMeter($$8, $$0, $$7);
            } else if (this.minecraft.gameMode.hasExperience()) {
                this.renderExperienceBar($$0, $$7);
            }
            if (this.minecraft.options.heldItemTooltips && this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR) {
                this.renderSelectedItemName($$0);
            } else if (this.minecraft.player.isSpectator()) {
                this.spectatorGui.renderTooltip($$0);
            }
        }
        if (this.minecraft.player.getSleepTimer() > 0) {
            this.minecraft.getProfiler().push("sleep");
            RenderSystem.disableDepthTest();
            float $$9 = this.minecraft.player.getSleepTimer();
            float $$10 = $$9 / 100.0f;
            if ($$10 > 1.0f) {
                $$10 = 1.0f - ($$9 - 100.0f) / 10.0f;
            }
            int $$11 = (int)(220.0f * $$10) << 24 | 0x101020;
            Gui.fill($$0, 0, 0, this.screenWidth, this.screenHeight, $$11);
            RenderSystem.enableDepthTest();
            this.minecraft.getProfiler().pop();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        }
        if (this.minecraft.isDemo()) {
            this.renderDemoOverlay($$0);
        }
        this.renderEffects($$0);
        if (this.minecraft.options.renderDebug) {
            this.debugScreen.render($$0);
        }
        if (!this.minecraft.options.hideGui) {
            Objective $$27;
            int $$26;
            if (this.overlayMessageString != null && this.overlayMessageTime > 0) {
                this.minecraft.getProfiler().push("overlayMessage");
                float $$12 = (float)this.overlayMessageTime - $$1;
                int $$13 = (int)($$12 * 255.0f / 20.0f);
                if ($$13 > 255) {
                    $$13 = 255;
                }
                if ($$13 > 8) {
                    $$0.pushPose();
                    $$0.translate(this.screenWidth / 2, this.screenHeight - 68, 0.0f);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    int $$14 = 0xFFFFFF;
                    if (this.animateOverlayMessageColor) {
                        $$14 = Mth.hsvToRgb($$12 / 50.0f, 0.7f, 0.6f) & 0xFFFFFF;
                    }
                    int $$15 = $$13 << 24 & 0xFF000000;
                    int $$16 = $$3.width(this.overlayMessageString);
                    this.drawBackdrop($$0, $$3, -4, $$16, 0xFFFFFF | $$15);
                    $$3.drawShadow($$0, this.overlayMessageString, (float)(-$$16 / 2), -4.0f, $$14 | $$15);
                    RenderSystem.disableBlend();
                    $$0.popPose();
                }
                this.minecraft.getProfiler().pop();
            }
            if (this.title != null && this.titleTime > 0) {
                this.minecraft.getProfiler().push("titleAndSubtitle");
                float $$17 = (float)this.titleTime - $$1;
                int $$18 = 255;
                if (this.titleTime > this.titleFadeOutTime + this.titleStayTime) {
                    float $$19 = (float)(this.titleFadeInTime + this.titleStayTime + this.titleFadeOutTime) - $$17;
                    $$18 = (int)($$19 * 255.0f / (float)this.titleFadeInTime);
                }
                if (this.titleTime <= this.titleFadeOutTime) {
                    $$18 = (int)($$17 * 255.0f / (float)this.titleFadeOutTime);
                }
                if (($$18 = Mth.clamp($$18, 0, 255)) > 8) {
                    $$0.pushPose();
                    $$0.translate(this.screenWidth / 2, this.screenHeight / 2, 0.0f);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    $$0.pushPose();
                    $$0.scale(4.0f, 4.0f, 4.0f);
                    int $$20 = $$18 << 24 & 0xFF000000;
                    int $$21 = $$3.width(this.title);
                    this.drawBackdrop($$0, $$3, -10, $$21, 0xFFFFFF | $$20);
                    $$3.drawShadow($$0, this.title, (float)(-$$21 / 2), -10.0f, 0xFFFFFF | $$20);
                    $$0.popPose();
                    if (this.subtitle != null) {
                        $$0.pushPose();
                        $$0.scale(2.0f, 2.0f, 2.0f);
                        int $$22 = $$3.width(this.subtitle);
                        this.drawBackdrop($$0, $$3, 5, $$22, 0xFFFFFF | $$20);
                        $$3.drawShadow($$0, this.subtitle, (float)(-$$22 / 2), 5.0f, 0xFFFFFF | $$20);
                        $$0.popPose();
                    }
                    RenderSystem.disableBlend();
                    $$0.popPose();
                }
                this.minecraft.getProfiler().pop();
            }
            this.subtitleOverlay.render($$0);
            Scoreboard $$23 = this.minecraft.level.getScoreboard();
            Objective $$24 = null;
            PlayerTeam $$25 = $$23.getPlayersTeam(this.minecraft.player.getScoreboardName());
            if ($$25 != null && ($$26 = $$25.getColor().getId()) >= 0) {
                $$24 = $$23.getDisplayObjective(3 + $$26);
            }
            Objective objective = $$27 = $$24 != null ? $$24 : $$23.getDisplayObjective(1);
            if ($$27 != null) {
                this.displayScoreboardSidebar($$0, $$27);
            }
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            int $$28 = Mth.floor(this.minecraft.mouseHandler.xpos() * (double)$$2.getGuiScaledWidth() / (double)$$2.getScreenWidth());
            int $$29 = Mth.floor(this.minecraft.mouseHandler.ypos() * (double)$$2.getGuiScaledHeight() / (double)$$2.getScreenHeight());
            this.minecraft.getProfiler().push("chat");
            this.chat.render($$0, this.tickCount, $$28, $$29);
            this.minecraft.getProfiler().pop();
            $$27 = $$23.getDisplayObjective(0);
            if (this.minecraft.options.keyPlayerList.isDown() && (!this.minecraft.isLocalServer() || this.minecraft.player.connection.getListedOnlinePlayers().size() > 1 || $$27 != null)) {
                this.tabList.setVisible(true);
                this.tabList.render($$0, this.screenWidth, $$23, $$27);
            } else {
                this.tabList.setVisible(false);
            }
            this.renderSavingIndicator($$0);
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void drawBackdrop(PoseStack $$0, Font $$1, int $$2, int $$3, int $$4) {
        int $$5 = this.minecraft.options.getBackgroundColor(0.0f);
        if ($$5 != 0) {
            int $$6 = -$$3 / 2;
            Objects.requireNonNull((Object)$$1);
            Gui.fill($$0, $$6 - 2, $$2 - 2, $$6 + $$3 + 2, $$2 + 9 + 2, FastColor.ARGB32.multiply($$5, $$4));
        }
    }

    private void renderCrosshair(PoseStack $$0) {
        Options $$1 = this.minecraft.options;
        if (!$$1.getCameraType().isFirstPerson()) {
            return;
        }
        if (this.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR && !this.canRenderCrosshairForSpectator(this.minecraft.hitResult)) {
            return;
        }
        if ($$1.renderDebug && !$$1.hideGui && !this.minecraft.player.isReducedDebugInfo() && !$$1.reducedDebugInfo().get().booleanValue()) {
            Camera $$2 = this.minecraft.gameRenderer.getMainCamera();
            PoseStack $$3 = RenderSystem.getModelViewStack();
            $$3.pushPose();
            $$3.translate(this.screenWidth / 2, this.screenHeight / 2, this.getBlitOffset());
            $$3.mulPose(Axis.XN.rotationDegrees($$2.getXRot()));
            $$3.mulPose(Axis.YP.rotationDegrees($$2.getYRot()));
            $$3.scale(-1.0f, -1.0f, -1.0f);
            RenderSystem.applyModelViewMatrix();
            RenderSystem.renderCrosshair(10);
            $$3.popPose();
            RenderSystem.applyModelViewMatrix();
        } else {
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            int $$4 = 15;
            this.blit($$0, (this.screenWidth - 15) / 2, (this.screenHeight - 15) / 2, 0, 0, 15, 15);
            if (this.minecraft.options.attackIndicator().get() == AttackIndicatorStatus.CROSSHAIR) {
                float $$5 = this.minecraft.player.getAttackStrengthScale(0.0f);
                boolean $$6 = false;
                if (this.minecraft.crosshairPickEntity != null && this.minecraft.crosshairPickEntity instanceof LivingEntity && $$5 >= 1.0f) {
                    $$6 = this.minecraft.player.getCurrentItemAttackStrengthDelay() > 5.0f;
                    $$6 &= this.minecraft.crosshairPickEntity.isAlive();
                }
                int $$7 = this.screenHeight / 2 - 7 + 16;
                int $$8 = this.screenWidth / 2 - 8;
                if ($$6) {
                    this.blit($$0, $$8, $$7, 68, 94, 16, 16);
                } else if ($$5 < 1.0f) {
                    int $$9 = (int)($$5 * 17.0f);
                    this.blit($$0, $$8, $$7, 36, 94, 16, 4);
                    this.blit($$0, $$8, $$7, 52, 94, $$9, 4);
                }
            }
        }
    }

    private boolean canRenderCrosshairForSpectator(HitResult $$0) {
        if ($$0 == null) {
            return false;
        }
        if ($$0.getType() == HitResult.Type.ENTITY) {
            return ((EntityHitResult)$$0).getEntity() instanceof MenuProvider;
        }
        if ($$0.getType() == HitResult.Type.BLOCK) {
            ClientLevel $$2 = this.minecraft.level;
            BlockPos $$1 = ((BlockHitResult)$$0).getBlockPos();
            return $$2.getBlockState($$1).getMenuProvider($$2, $$1) != null;
        }
        return false;
    }

    protected void renderEffects(PoseStack $$0) {
        EffectRenderingInventoryScreen $$2;
        Screen screen;
        Collection<MobEffectInstance> $$1 = this.minecraft.player.getActiveEffects();
        if ($$1.isEmpty() || (screen = this.minecraft.screen) instanceof EffectRenderingInventoryScreen && ($$2 = (EffectRenderingInventoryScreen)screen).canSeeEffects()) {
            return;
        }
        RenderSystem.enableBlend();
        int $$3 = 0;
        int $$4 = 0;
        MobEffectTextureManager $$5 = this.minecraft.getMobEffectTextures();
        ArrayList $$6 = Lists.newArrayListWithExpectedSize((int)$$1.size());
        RenderSystem.setShaderTexture(0, AbstractContainerScreen.INVENTORY_LOCATION);
        for (MobEffectInstance $$7 : Ordering.natural().reverse().sortedCopy($$1)) {
            MobEffect $$8 = $$7.getEffect();
            if (!$$7.showIcon()) continue;
            int $$9 = this.screenWidth;
            int $$10 = 1;
            if (this.minecraft.isDemo()) {
                $$10 += 15;
            }
            if ($$8.isBeneficial()) {
                $$9 -= 25 * ++$$3;
            } else {
                $$9 -= 25 * ++$$4;
                $$10 += 26;
            }
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            float $$11 = 1.0f;
            if ($$7.isAmbient()) {
                this.blit($$0, $$9, $$10, 165, 166, 24, 24);
            } else {
                this.blit($$0, $$9, $$10, 141, 166, 24, 24);
                if ($$7.getDuration() <= 200) {
                    int $$12 = 10 - $$7.getDuration() / 20;
                    $$11 = Mth.clamp((float)$$7.getDuration() / 10.0f / 5.0f * 0.5f, 0.0f, 0.5f) + Mth.cos((float)$$7.getDuration() * (float)Math.PI / 5.0f) * Mth.clamp((float)$$12 / 10.0f * 0.25f, 0.0f, 0.25f);
                }
            }
            TextureAtlasSprite $$13 = $$5.get($$8);
            int $$14 = $$9;
            int $$15 = $$10;
            float $$16 = $$11;
            $$6.add(() -> {
                RenderSystem.setShaderTexture(0, $$13.atlasLocation());
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, $$16);
                Gui.blit($$0, $$14 + 3, $$15 + 3, this.getBlitOffset(), 18, 18, $$13);
            });
        }
        $$6.forEach(Runnable::run);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void renderHotbar(float $$0, PoseStack $$1) {
        float $$14;
        Player $$2 = this.getCameraPlayer();
        if ($$2 == null) {
            return;
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        ItemStack $$3 = $$2.getOffhandItem();
        HumanoidArm $$4 = $$2.getMainArm().getOpposite();
        int $$5 = this.screenWidth / 2;
        int $$6 = this.getBlitOffset();
        int $$7 = 182;
        int $$8 = 91;
        this.setBlitOffset(-90);
        this.blit($$1, $$5 - 91, this.screenHeight - 22, 0, 0, 182, 22);
        this.blit($$1, $$5 - 91 - 1 + $$2.getInventory().selected * 20, this.screenHeight - 22 - 1, 0, 22, 24, 22);
        if (!$$3.isEmpty()) {
            if ($$4 == HumanoidArm.LEFT) {
                this.blit($$1, $$5 - 91 - 29, this.screenHeight - 23, 24, 22, 29, 24);
            } else {
                this.blit($$1, $$5 + 91, this.screenHeight - 23, 53, 22, 29, 24);
            }
        }
        this.setBlitOffset($$6);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        int $$9 = 1;
        for (int $$10 = 0; $$10 < 9; ++$$10) {
            int $$11 = $$5 - 90 + $$10 * 20 + 2;
            int $$12 = this.screenHeight - 16 - 3;
            this.renderSlot($$11, $$12, $$0, $$2, $$2.getInventory().items.get($$10), $$9++);
        }
        if (!$$3.isEmpty()) {
            int $$13 = this.screenHeight - 16 - 3;
            if ($$4 == HumanoidArm.LEFT) {
                this.renderSlot($$5 - 91 - 26, $$13, $$0, $$2, $$3, $$9++);
            } else {
                this.renderSlot($$5 + 91 + 10, $$13, $$0, $$2, $$3, $$9++);
            }
        }
        if (this.minecraft.options.attackIndicator().get() == AttackIndicatorStatus.HOTBAR && ($$14 = this.minecraft.player.getAttackStrengthScale(0.0f)) < 1.0f) {
            int $$15 = this.screenHeight - 20;
            int $$16 = $$5 + 91 + 6;
            if ($$4 == HumanoidArm.RIGHT) {
                $$16 = $$5 - 91 - 22;
            }
            RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
            int $$17 = (int)($$14 * 19.0f);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            this.blit($$1, $$16, $$15, 0, 94, 18, 18);
            this.blit($$1, $$16, $$15 + 18 - $$17, 18, 112 - $$17, 18, $$17);
        }
        RenderSystem.disableBlend();
    }

    public void renderJumpMeter(PlayerRideableJumping $$0, PoseStack $$1, int $$2) {
        this.minecraft.getProfiler().push("jumpBar");
        RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
        float $$3 = this.minecraft.player.getJumpRidingScale();
        int $$4 = 182;
        int $$5 = (int)($$3 * 183.0f);
        int $$6 = this.screenHeight - 32 + 3;
        this.blit($$1, $$2, $$6, 0, 84, 182, 5);
        if ($$0.getJumpCooldown() > 0) {
            this.blit($$1, $$2, $$6, 0, 74, 182, 5);
        } else if ($$5 > 0) {
            this.blit($$1, $$2, $$6, 0, 89, $$5, 5);
        }
        this.minecraft.getProfiler().pop();
    }

    public void renderExperienceBar(PoseStack $$0, int $$1) {
        this.minecraft.getProfiler().push("expBar");
        RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
        int $$2 = this.minecraft.player.getXpNeededForNextLevel();
        if ($$2 > 0) {
            int $$3 = 182;
            int $$4 = (int)(this.minecraft.player.experienceProgress * 183.0f);
            int $$5 = this.screenHeight - 32 + 3;
            this.blit($$0, $$1, $$5, 0, 64, 182, 5);
            if ($$4 > 0) {
                this.blit($$0, $$1, $$5, 0, 69, $$4, 5);
            }
        }
        this.minecraft.getProfiler().pop();
        if (this.minecraft.player.experienceLevel > 0) {
            this.minecraft.getProfiler().push("expLevel");
            String $$6 = "" + this.minecraft.player.experienceLevel;
            int $$7 = (this.screenWidth - this.getFont().width($$6)) / 2;
            int $$8 = this.screenHeight - 31 - 4;
            this.getFont().draw($$0, $$6, (float)($$7 + 1), (float)$$8, 0);
            this.getFont().draw($$0, $$6, (float)($$7 - 1), (float)$$8, 0);
            this.getFont().draw($$0, $$6, (float)$$7, (float)($$8 + 1), 0);
            this.getFont().draw($$0, $$6, (float)$$7, (float)($$8 - 1), 0);
            this.getFont().draw($$0, $$6, (float)$$7, (float)$$8, 8453920);
            this.minecraft.getProfiler().pop();
        }
    }

    public void renderSelectedItemName(PoseStack $$0) {
        this.minecraft.getProfiler().push("selectedItemName");
        if (this.toolHighlightTimer > 0 && !this.lastToolHighlight.isEmpty()) {
            int $$5;
            MutableComponent $$1 = Component.empty().append(this.lastToolHighlight.getHoverName()).withStyle(this.lastToolHighlight.getRarity().color);
            if (this.lastToolHighlight.hasCustomHoverName()) {
                $$1.withStyle(ChatFormatting.ITALIC);
            }
            int $$2 = this.getFont().width($$1);
            int $$3 = (this.screenWidth - $$2) / 2;
            int $$4 = this.screenHeight - 59;
            if (!this.minecraft.gameMode.canHurtPlayer()) {
                $$4 += 14;
            }
            if (($$5 = (int)((float)this.toolHighlightTimer * 256.0f / 10.0f)) > 255) {
                $$5 = 255;
            }
            if ($$5 > 0) {
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                Objects.requireNonNull((Object)this.getFont());
                Gui.fill($$0, $$3 - 2, $$4 - 2, $$3 + $$2 + 2, $$4 + 9 + 2, this.minecraft.options.getBackgroundColor(0));
                this.getFont().drawShadow($$0, $$1, (float)$$3, (float)$$4, 0xFFFFFF + ($$5 << 24));
                RenderSystem.disableBlend();
            }
        }
        this.minecraft.getProfiler().pop();
    }

    public void renderDemoOverlay(PoseStack $$0) {
        MutableComponent $$2;
        this.minecraft.getProfiler().push("demo");
        if (this.minecraft.level.getGameTime() >= 120500L) {
            Component $$1 = DEMO_EXPIRED_TEXT;
        } else {
            $$2 = Component.translatable("demo.remainingTime", StringUtil.formatTickDuration((int)(120500L - this.minecraft.level.getGameTime())));
        }
        int $$3 = this.getFont().width($$2);
        this.getFont().drawShadow($$0, $$2, (float)(this.screenWidth - $$3 - 10), 5.0f, 0xFFFFFF);
        this.minecraft.getProfiler().pop();
    }

    private void displayScoreboardSidebar(PoseStack $$02, Objective $$1) {
        int $$7;
        Scoreboard $$2 = $$1.getScoreboard();
        Object $$3 = $$2.getPlayerScores($$1);
        List $$4 = (List)$$3.stream().filter($$0 -> $$0.getOwner() != null && !$$0.getOwner().startsWith("#")).collect(Collectors.toList());
        $$3 = $$4.size() > 15 ? Lists.newArrayList((Iterable)Iterables.skip((Iterable)$$4, (int)($$3.size() - 15))) : $$4;
        ArrayList $$5 = Lists.newArrayListWithCapacity((int)$$3.size());
        Component $$6 = $$1.getDisplayName();
        int $$8 = $$7 = this.getFont().width($$6);
        int $$9 = this.getFont().width(SPACER);
        for (Score $$10 : $$3) {
            PlayerTeam $$11 = $$2.getPlayersTeam($$10.getOwner());
            MutableComponent $$12 = PlayerTeam.formatNameForTeam($$11, Component.literal($$10.getOwner()));
            $$5.add((Object)Pair.of((Object)$$10, (Object)$$12));
            $$8 = Math.max((int)$$8, (int)(this.getFont().width($$12) + $$9 + this.getFont().width(Integer.toString((int)$$10.getScore()))));
        }
        int n = $$3.size();
        Objects.requireNonNull((Object)this.getFont());
        int $$13 = n * 9;
        int $$14 = this.screenHeight / 2 + $$13 / 3;
        int $$15 = 3;
        int $$16 = this.screenWidth - $$8 - 3;
        int $$17 = 0;
        int $$18 = this.minecraft.options.getBackgroundColor(0.3f);
        int $$19 = this.minecraft.options.getBackgroundColor(0.4f);
        for (Pair $$20 : $$5) {
            ++$$17;
            Score $$21 = (Score)$$20.getFirst();
            Component $$22 = (Component)$$20.getSecond();
            String $$23 = "" + ChatFormatting.RED + $$21.getScore();
            int $$24 = $$16;
            Objects.requireNonNull((Object)this.getFont());
            int $$25 = $$14 - $$17 * 9;
            int $$26 = this.screenWidth - 3 + 2;
            Objects.requireNonNull((Object)this.getFont());
            Gui.fill($$02, $$24 - 2, $$25, $$26, $$25 + 9, $$18);
            this.getFont().draw($$02, $$22, (float)$$24, (float)$$25, -1);
            this.getFont().draw($$02, $$23, (float)($$26 - this.getFont().width($$23)), (float)$$25, -1);
            if ($$17 != $$3.size()) continue;
            Objects.requireNonNull((Object)this.getFont());
            Gui.fill($$02, $$24 - 2, $$25 - 9 - 1, $$26, $$25 - 1, $$19);
            Gui.fill($$02, $$24 - 2, $$25 - 1, $$26, $$25, $$18);
            Font font = this.getFont();
            float f = $$24 + $$8 / 2 - $$7 / 2;
            Objects.requireNonNull((Object)this.getFont());
            font.draw($$02, $$6, f, (float)($$25 - 9), -1);
        }
    }

    private Player getCameraPlayer() {
        if (!(this.minecraft.getCameraEntity() instanceof Player)) {
            return null;
        }
        return (Player)this.minecraft.getCameraEntity();
    }

    private LivingEntity getPlayerVehicleWithHealth() {
        Player $$0 = this.getCameraPlayer();
        if ($$0 != null) {
            Entity $$1 = $$0.getVehicle();
            if ($$1 == null) {
                return null;
            }
            if ($$1 instanceof LivingEntity) {
                return (LivingEntity)$$1;
            }
        }
        return null;
    }

    private int getVehicleMaxHearts(LivingEntity $$0) {
        if ($$0 == null || !$$0.showVehicleHealth()) {
            return 0;
        }
        float $$1 = $$0.getMaxHealth();
        int $$2 = (int)($$1 + 0.5f) / 2;
        if ($$2 > 30) {
            $$2 = 30;
        }
        return $$2;
    }

    private int getVisibleVehicleHeartRows(int $$0) {
        return (int)Math.ceil((double)((double)$$0 / 10.0));
    }

    private void renderPlayerHealth(PoseStack $$0) {
        Player $$1 = this.getCameraPlayer();
        if ($$1 == null) {
            return;
        }
        int $$2 = Mth.ceil($$1.getHealth());
        boolean $$3 = this.healthBlinkTime > (long)this.tickCount && (this.healthBlinkTime - (long)this.tickCount) / 3L % 2L == 1L;
        long $$4 = Util.getMillis();
        if ($$2 < this.lastHealth && $$1.invulnerableTime > 0) {
            this.lastHealthTime = $$4;
            this.healthBlinkTime = this.tickCount + 20;
        } else if ($$2 > this.lastHealth && $$1.invulnerableTime > 0) {
            this.lastHealthTime = $$4;
            this.healthBlinkTime = this.tickCount + 10;
        }
        if ($$4 - this.lastHealthTime > 1000L) {
            this.lastHealth = $$2;
            this.displayHealth = $$2;
            this.lastHealthTime = $$4;
        }
        this.lastHealth = $$2;
        int $$5 = this.displayHealth;
        this.random.setSeed(this.tickCount * 312871);
        FoodData $$6 = $$1.getFoodData();
        int $$7 = $$6.getFoodLevel();
        int $$8 = this.screenWidth / 2 - 91;
        int $$9 = this.screenWidth / 2 + 91;
        int $$10 = this.screenHeight - 39;
        float $$11 = Math.max((float)((float)$$1.getAttributeValue(Attributes.MAX_HEALTH)), (float)Math.max((int)$$5, (int)$$2));
        int $$12 = Mth.ceil($$1.getAbsorptionAmount());
        int $$13 = Mth.ceil(($$11 + (float)$$12) / 2.0f / 10.0f);
        int $$14 = Math.max((int)(10 - ($$13 - 2)), (int)3);
        int $$15 = $$10 - ($$13 - 1) * $$14 - 10;
        int $$16 = $$10 - 10;
        int $$17 = $$1.getArmorValue();
        int $$18 = -1;
        if ($$1.hasEffect(MobEffects.REGENERATION)) {
            $$18 = this.tickCount % Mth.ceil($$11 + 5.0f);
        }
        this.minecraft.getProfiler().push("armor");
        for (int $$19 = 0; $$19 < 10; ++$$19) {
            if ($$17 <= 0) continue;
            int $$20 = $$8 + $$19 * 8;
            if ($$19 * 2 + 1 < $$17) {
                this.blit($$0, $$20, $$15, 34, 9, 9, 9);
            }
            if ($$19 * 2 + 1 == $$17) {
                this.blit($$0, $$20, $$15, 25, 9, 9, 9);
            }
            if ($$19 * 2 + 1 <= $$17) continue;
            this.blit($$0, $$20, $$15, 16, 9, 9, 9);
        }
        this.minecraft.getProfiler().popPush("health");
        this.renderHearts($$0, $$1, $$8, $$10, $$14, $$18, $$11, $$2, $$5, $$12, $$3);
        LivingEntity $$21 = this.getPlayerVehicleWithHealth();
        int $$22 = this.getVehicleMaxHearts($$21);
        if ($$22 == 0) {
            this.minecraft.getProfiler().popPush("food");
            for (int $$23 = 0; $$23 < 10; ++$$23) {
                int $$24 = $$10;
                int $$25 = 16;
                int $$26 = 0;
                if ($$1.hasEffect(MobEffects.HUNGER)) {
                    $$25 += 36;
                    $$26 = 13;
                }
                if ($$1.getFoodData().getSaturationLevel() <= 0.0f && this.tickCount % ($$7 * 3 + 1) == 0) {
                    $$24 += this.random.nextInt(3) - 1;
                }
                int $$27 = $$9 - $$23 * 8 - 9;
                this.blit($$0, $$27, $$24, 16 + $$26 * 9, 27, 9, 9);
                if ($$23 * 2 + 1 < $$7) {
                    this.blit($$0, $$27, $$24, $$25 + 36, 27, 9, 9);
                }
                if ($$23 * 2 + 1 != $$7) continue;
                this.blit($$0, $$27, $$24, $$25 + 45, 27, 9, 9);
            }
            $$16 -= 10;
        }
        this.minecraft.getProfiler().popPush("air");
        int $$28 = $$1.getMaxAirSupply();
        int $$29 = Math.min((int)$$1.getAirSupply(), (int)$$28);
        if ($$1.isEyeInFluid(FluidTags.WATER) || $$29 < $$28) {
            int $$30 = this.getVisibleVehicleHeartRows($$22) - 1;
            $$16 -= $$30 * 10;
            int $$31 = Mth.ceil((double)($$29 - 2) * 10.0 / (double)$$28);
            int $$32 = Mth.ceil((double)$$29 * 10.0 / (double)$$28) - $$31;
            for (int $$33 = 0; $$33 < $$31 + $$32; ++$$33) {
                if ($$33 < $$31) {
                    this.blit($$0, $$9 - $$33 * 8 - 9, $$16, 16, 18, 9, 9);
                    continue;
                }
                this.blit($$0, $$9 - $$33 * 8 - 9, $$16, 25, 18, 9, 9);
            }
        }
        this.minecraft.getProfiler().pop();
    }

    private void renderHearts(PoseStack $$0, Player $$1, int $$2, int $$3, int $$4, int $$5, float $$6, int $$7, int $$8, int $$9, boolean $$10) {
        HeartType $$11 = HeartType.forPlayer($$1);
        int $$12 = 9 * ($$1.level.getLevelData().isHardcore() ? 5 : 0);
        int $$13 = Mth.ceil((double)$$6 / 2.0);
        int $$14 = Mth.ceil((double)$$9 / 2.0);
        int $$15 = $$13 * 2;
        for (int $$16 = $$13 + $$14 - 1; $$16 >= 0; --$$16) {
            int $$23;
            boolean $$22;
            int $$17 = $$16 / 10;
            int $$18 = $$16 % 10;
            int $$19 = $$2 + $$18 * 8;
            int $$20 = $$3 - $$17 * $$4;
            if ($$7 + $$9 <= 4) {
                $$20 += this.random.nextInt(2);
            }
            if ($$16 < $$13 && $$16 == $$5) {
                $$20 -= 2;
            }
            this.renderHeart($$0, HeartType.CONTAINER, $$19, $$20, $$12, $$10, false);
            int $$21 = $$16 * 2;
            boolean bl = $$22 = $$16 >= $$13;
            if ($$22 && ($$23 = $$21 - $$15) < $$9) {
                boolean $$24 = $$23 + 1 == $$9;
                this.renderHeart($$0, $$11 == HeartType.WITHERED ? $$11 : HeartType.ABSORBING, $$19, $$20, $$12, false, $$24);
            }
            if ($$10 && $$21 < $$8) {
                boolean $$25 = $$21 + 1 == $$8;
                this.renderHeart($$0, $$11, $$19, $$20, $$12, true, $$25);
            }
            if ($$21 >= $$7) continue;
            boolean $$26 = $$21 + 1 == $$7;
            this.renderHeart($$0, $$11, $$19, $$20, $$12, false, $$26);
        }
    }

    private void renderHeart(PoseStack $$0, HeartType $$1, int $$2, int $$3, int $$4, boolean $$5, boolean $$6) {
        this.blit($$0, $$2, $$3, $$1.getX($$6, $$5), $$4, 9, 9);
    }

    private void renderVehicleHealth(PoseStack $$0) {
        LivingEntity $$1 = this.getPlayerVehicleWithHealth();
        if ($$1 == null) {
            return;
        }
        int $$2 = this.getVehicleMaxHearts($$1);
        if ($$2 == 0) {
            return;
        }
        int $$3 = (int)Math.ceil((double)$$1.getHealth());
        this.minecraft.getProfiler().popPush("mountHealth");
        int $$4 = this.screenHeight - 39;
        int $$5 = this.screenWidth / 2 + 91;
        int $$6 = $$4;
        int $$7 = 0;
        boolean $$8 = false;
        while ($$2 > 0) {
            int $$9 = Math.min((int)$$2, (int)10);
            $$2 -= $$9;
            for (int $$10 = 0; $$10 < $$9; ++$$10) {
                int $$11 = 52;
                int $$12 = 0;
                int $$13 = $$5 - $$10 * 8 - 9;
                this.blit($$0, $$13, $$6, 52 + $$12 * 9, 9, 9, 9);
                if ($$10 * 2 + 1 + $$7 < $$3) {
                    this.blit($$0, $$13, $$6, 88, 9, 9, 9);
                }
                if ($$10 * 2 + 1 + $$7 != $$3) continue;
                this.blit($$0, $$13, $$6, 97, 9, 9, 9);
            }
            $$6 -= 10;
            $$7 += 20;
        }
    }

    private void renderTextureOverlay(ResourceLocation $$0, float $$1) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, $$1);
        RenderSystem.setShaderTexture(0, $$0);
        Tesselator $$2 = Tesselator.getInstance();
        BufferBuilder $$3 = $$2.getBuilder();
        $$3.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        $$3.vertex(0.0, this.screenHeight, -90.0).uv(0.0f, 1.0f).endVertex();
        $$3.vertex(this.screenWidth, this.screenHeight, -90.0).uv(1.0f, 1.0f).endVertex();
        $$3.vertex(this.screenWidth, 0.0, -90.0).uv(1.0f, 0.0f).endVertex();
        $$3.vertex(0.0, 0.0, -90.0).uv(0.0f, 0.0f).endVertex();
        $$2.end();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void renderSpyglassOverlay(float $$0) {
        float $$3;
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, SPYGLASS_SCOPE_LOCATION);
        Tesselator $$1 = Tesselator.getInstance();
        BufferBuilder $$2 = $$1.getBuilder();
        float $$4 = $$3 = (float)Math.min((int)this.screenWidth, (int)this.screenHeight);
        float $$5 = Math.min((float)((float)this.screenWidth / $$3), (float)((float)this.screenHeight / $$4)) * $$0;
        float $$6 = $$3 * $$5;
        float $$7 = $$4 * $$5;
        float $$8 = ((float)this.screenWidth - $$6) / 2.0f;
        float $$9 = ((float)this.screenHeight - $$7) / 2.0f;
        float $$10 = $$8 + $$6;
        float $$11 = $$9 + $$7;
        $$2.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        $$2.vertex($$8, $$11, -90.0).uv(0.0f, 1.0f).endVertex();
        $$2.vertex($$10, $$11, -90.0).uv(1.0f, 1.0f).endVertex();
        $$2.vertex($$10, $$9, -90.0).uv(1.0f, 0.0f).endVertex();
        $$2.vertex($$8, $$9, -90.0).uv(0.0f, 0.0f).endVertex();
        $$1.end();
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionColorShader));
        RenderSystem.disableTexture();
        $$2.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        $$2.vertex(0.0, this.screenHeight, -90.0).color(0, 0, 0, 255).endVertex();
        $$2.vertex(this.screenWidth, this.screenHeight, -90.0).color(0, 0, 0, 255).endVertex();
        $$2.vertex(this.screenWidth, $$11, -90.0).color(0, 0, 0, 255).endVertex();
        $$2.vertex(0.0, $$11, -90.0).color(0, 0, 0, 255).endVertex();
        $$2.vertex(0.0, $$9, -90.0).color(0, 0, 0, 255).endVertex();
        $$2.vertex(this.screenWidth, $$9, -90.0).color(0, 0, 0, 255).endVertex();
        $$2.vertex(this.screenWidth, 0.0, -90.0).color(0, 0, 0, 255).endVertex();
        $$2.vertex(0.0, 0.0, -90.0).color(0, 0, 0, 255).endVertex();
        $$2.vertex(0.0, $$11, -90.0).color(0, 0, 0, 255).endVertex();
        $$2.vertex($$8, $$11, -90.0).color(0, 0, 0, 255).endVertex();
        $$2.vertex($$8, $$9, -90.0).color(0, 0, 0, 255).endVertex();
        $$2.vertex(0.0, $$9, -90.0).color(0, 0, 0, 255).endVertex();
        $$2.vertex($$10, $$11, -90.0).color(0, 0, 0, 255).endVertex();
        $$2.vertex(this.screenWidth, $$11, -90.0).color(0, 0, 0, 255).endVertex();
        $$2.vertex(this.screenWidth, $$9, -90.0).color(0, 0, 0, 255).endVertex();
        $$2.vertex($$10, $$9, -90.0).color(0, 0, 0, 255).endVertex();
        $$1.end();
        RenderSystem.enableTexture();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void updateVignetteBrightness(Entity $$0) {
        if ($$0 == null) {
            return;
        }
        BlockPos $$1 = new BlockPos($$0.getX(), $$0.getEyeY(), $$0.getZ());
        float $$2 = LightTexture.getBrightness($$0.level.dimensionType(), $$0.level.getMaxLocalRawBrightness($$1));
        float $$3 = Mth.clamp(1.0f - $$2, 0.0f, 1.0f);
        this.vignetteBrightness += ($$3 - this.vignetteBrightness) * 0.01f;
    }

    private void renderVignette(Entity $$0) {
        WorldBorder $$1 = this.minecraft.level.getWorldBorder();
        float $$2 = (float)$$1.getDistanceToBorder($$0);
        double $$3 = Math.min((double)($$1.getLerpSpeed() * (double)$$1.getWarningTime() * 1000.0), (double)Math.abs((double)($$1.getLerpTarget() - $$1.getSize())));
        double $$4 = Math.max((double)$$1.getWarningBlocks(), (double)$$3);
        $$2 = (double)$$2 < $$4 ? 1.0f - (float)((double)$$2 / $$4) : 0.0f;
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        if ($$2 > 0.0f) {
            $$2 = Mth.clamp($$2, 0.0f, 1.0f);
            RenderSystem.setShaderColor(0.0f, $$2, $$2, 1.0f);
        } else {
            float $$5 = this.vignetteBrightness;
            $$5 = Mth.clamp($$5, 0.0f, 1.0f);
            RenderSystem.setShaderColor($$5, $$5, $$5, 1.0f);
        }
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, VIGNETTE_LOCATION);
        Tesselator $$6 = Tesselator.getInstance();
        BufferBuilder $$7 = $$6.getBuilder();
        $$7.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        $$7.vertex(0.0, this.screenHeight, -90.0).uv(0.0f, 1.0f).endVertex();
        $$7.vertex(this.screenWidth, this.screenHeight, -90.0).uv(1.0f, 1.0f).endVertex();
        $$7.vertex(this.screenWidth, 0.0, -90.0).uv(1.0f, 0.0f).endVertex();
        $$7.vertex(0.0, 0.0, -90.0).uv(0.0f, 0.0f).endVertex();
        $$6.end();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.defaultBlendFunc();
    }

    private void renderPortalOverlay(float $$0) {
        if ($$0 < 1.0f) {
            $$0 *= $$0;
            $$0 *= $$0;
            $$0 = $$0 * 0.8f + 0.2f;
        }
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, $$0);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        TextureAtlasSprite $$1 = this.minecraft.getBlockRenderer().getBlockModelShaper().getParticleIcon(Blocks.NETHER_PORTAL.defaultBlockState());
        float $$2 = $$1.getU0();
        float $$3 = $$1.getV0();
        float $$4 = $$1.getU1();
        float $$5 = $$1.getV1();
        Tesselator $$6 = Tesselator.getInstance();
        BufferBuilder $$7 = $$6.getBuilder();
        $$7.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        $$7.vertex(0.0, this.screenHeight, -90.0).uv($$2, $$5).endVertex();
        $$7.vertex(this.screenWidth, this.screenHeight, -90.0).uv($$4, $$5).endVertex();
        $$7.vertex(this.screenWidth, 0.0, -90.0).uv($$4, $$3).endVertex();
        $$7.vertex(0.0, 0.0, -90.0).uv($$2, $$3).endVertex();
        $$6.end();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void renderSlot(int $$0, int $$1, float $$2, Player $$3, ItemStack $$4, int $$5) {
        if ($$4.isEmpty()) {
            return;
        }
        PoseStack $$6 = RenderSystem.getModelViewStack();
        float $$7 = (float)$$4.getPopTime() - $$2;
        if ($$7 > 0.0f) {
            float $$8 = 1.0f + $$7 / 5.0f;
            $$6.pushPose();
            $$6.translate($$0 + 8, $$1 + 12, 0.0f);
            $$6.scale(1.0f / $$8, ($$8 + 1.0f) / 2.0f, 1.0f);
            $$6.translate(-($$0 + 8), -($$1 + 12), 0.0f);
            RenderSystem.applyModelViewMatrix();
        }
        this.itemRenderer.renderAndDecorateItem($$3, $$4, $$0, $$1, $$5);
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionColorShader));
        if ($$7 > 0.0f) {
            $$6.popPose();
            RenderSystem.applyModelViewMatrix();
        }
        this.itemRenderer.renderGuiItemDecorations(this.minecraft.font, $$4, $$0, $$1);
    }

    public void tick(boolean $$0) {
        this.tickAutosaveIndicator();
        if (!$$0) {
            this.tick();
        }
    }

    private void tick() {
        if (this.overlayMessageTime > 0) {
            --this.overlayMessageTime;
        }
        if (this.titleTime > 0) {
            --this.titleTime;
            if (this.titleTime <= 0) {
                this.title = null;
                this.subtitle = null;
            }
        }
        ++this.tickCount;
        Entity $$0 = this.minecraft.getCameraEntity();
        if ($$0 != null) {
            this.updateVignetteBrightness($$0);
        }
        if (this.minecraft.player != null) {
            ItemStack $$1 = this.minecraft.player.getInventory().getSelected();
            if ($$1.isEmpty()) {
                this.toolHighlightTimer = 0;
            } else if (this.lastToolHighlight.isEmpty() || !$$1.is(this.lastToolHighlight.getItem()) || !$$1.getHoverName().equals(this.lastToolHighlight.getHoverName())) {
                this.toolHighlightTimer = 40;
            } else if (this.toolHighlightTimer > 0) {
                --this.toolHighlightTimer;
            }
            this.lastToolHighlight = $$1;
        }
        this.chat.tick();
    }

    private void tickAutosaveIndicator() {
        IntegratedServer $$0 = this.minecraft.getSingleplayerServer();
        boolean $$1 = $$0 != null && $$0.isCurrentlySaving();
        this.lastAutosaveIndicatorValue = this.autosaveIndicatorValue;
        this.autosaveIndicatorValue = Mth.lerp(0.2f, this.autosaveIndicatorValue, $$1 ? 1.0f : 0.0f);
    }

    public void setNowPlaying(Component $$0) {
        MutableComponent $$1 = Component.translatable("record.nowPlaying", $$0);
        this.setOverlayMessage($$1, true);
        this.minecraft.getNarrator().sayNow($$1);
    }

    public void setOverlayMessage(Component $$0, boolean $$1) {
        this.setChatDisabledByPlayerShown(false);
        this.overlayMessageString = $$0;
        this.overlayMessageTime = 60;
        this.animateOverlayMessageColor = $$1;
    }

    public void setChatDisabledByPlayerShown(boolean $$0) {
        this.chatDisabledByPlayerShown = $$0;
    }

    public boolean isShowingChatDisabledByPlayer() {
        return this.chatDisabledByPlayerShown && this.overlayMessageTime > 0;
    }

    public void setTimes(int $$0, int $$1, int $$2) {
        if ($$0 >= 0) {
            this.titleFadeInTime = $$0;
        }
        if ($$1 >= 0) {
            this.titleStayTime = $$1;
        }
        if ($$2 >= 0) {
            this.titleFadeOutTime = $$2;
        }
        if (this.titleTime > 0) {
            this.titleTime = this.titleFadeInTime + this.titleStayTime + this.titleFadeOutTime;
        }
    }

    public void setSubtitle(Component $$0) {
        this.subtitle = $$0;
    }

    public void setTitle(Component $$0) {
        this.title = $$0;
        this.titleTime = this.titleFadeInTime + this.titleStayTime + this.titleFadeOutTime;
    }

    public void clear() {
        this.title = null;
        this.subtitle = null;
        this.titleTime = 0;
    }

    public ChatComponent getChat() {
        return this.chat;
    }

    public int getGuiTicks() {
        return this.tickCount;
    }

    public Font getFont() {
        return this.minecraft.font;
    }

    public SpectatorGui getSpectatorGui() {
        return this.spectatorGui;
    }

    public PlayerTabOverlay getTabList() {
        return this.tabList;
    }

    public void onDisconnected() {
        this.tabList.reset();
        this.bossOverlay.reset();
        this.minecraft.getToasts().clear();
        this.minecraft.options.renderDebug = false;
        this.chat.clearMessages(true);
    }

    public BossHealthOverlay getBossOverlay() {
        return this.bossOverlay;
    }

    public void clearCache() {
        this.debugScreen.clearChunkCache();
    }

    private void renderSavingIndicator(PoseStack $$0) {
        int $$1;
        if (this.minecraft.options.showAutosaveIndicator().get().booleanValue() && (this.autosaveIndicatorValue > 0.0f || this.lastAutosaveIndicatorValue > 0.0f) && ($$1 = Mth.floor(255.0f * Mth.clamp(Mth.lerp(this.minecraft.getFrameTime(), this.lastAutosaveIndicatorValue, this.autosaveIndicatorValue), 0.0f, 1.0f))) > 8) {
            Font $$2 = this.getFont();
            int $$3 = $$2.width(SAVING_TEXT);
            int $$4 = 0xFFFFFF | $$1 << 24 & 0xFF000000;
            $$2.drawShadow($$0, SAVING_TEXT, (float)(this.screenWidth - $$3 - 10), (float)(this.screenHeight - 15), $$4);
        }
    }

    static enum HeartType {
        CONTAINER(0, false),
        NORMAL(2, true),
        POISIONED(4, true),
        WITHERED(6, true),
        ABSORBING(8, false),
        FROZEN(9, false);

        private final int index;
        private final boolean canBlink;

        private HeartType(int $$0, boolean $$1) {
            this.index = $$0;
            this.canBlink = $$1;
        }

        public int getX(boolean $$0, boolean $$1) {
            int $$5;
            if (this == CONTAINER) {
                boolean $$2 = $$1;
            } else {
                int $$3 = $$0 ? 1 : 0;
                int $$4 = this.canBlink && $$1 ? 2 : 0;
                $$5 = $$3 + $$4;
            }
            return 16 + (this.index * 2 + $$5) * 9;
        }

        static HeartType forPlayer(Player $$0) {
            HeartType $$4;
            if ($$0.hasEffect(MobEffects.POISON)) {
                HeartType $$1 = POISIONED;
            } else if ($$0.hasEffect(MobEffects.WITHER)) {
                HeartType $$2 = WITHERED;
            } else if ($$0.isFullyFrozen()) {
                HeartType $$3 = FROZEN;
            } else {
                $$4 = NORMAL;
            }
            return $$4;
        }
    }
}