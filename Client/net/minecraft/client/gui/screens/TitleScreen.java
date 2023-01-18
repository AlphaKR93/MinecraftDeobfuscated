/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.Runnables
 *  com.mojang.authlib.minecraft.BanDetails
 *  com.mojang.logging.LogUtils
 *  java.io.IOException
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Throwable
 *  java.lang.Void
 *  java.lang.invoke.LambdaMetafactory
 *  java.util.Objects
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.Executor
 *  java.util.function.Function
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.client.gui.screens;

import com.google.common.util.concurrent.Runnables;
import com.mojang.authlib.minecraft.BanDetails;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen;
import java.io.IOException;
import java.lang.invoke.LambdaMetafactory;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.LogoRenderer;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.AccessibilityOptionsScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.LanguageSelectScreen;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.SafetyScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.renderer.CubeMap;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PanoramaRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import org.slf4j.Logger;

public class TitleScreen
extends Screen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String DEMO_LEVEL_ID = "Demo_World";
    public static final Component COPYRIGHT_TEXT = Component.literal("Copyright Mojang AB. Do not distribute!");
    public static final CubeMap CUBE_MAP = new CubeMap(new ResourceLocation("textures/gui/title/background/panorama"));
    private static final ResourceLocation PANORAMA_OVERLAY = new ResourceLocation("textures/gui/title/background/panorama_overlay.png");
    private static final ResourceLocation ACCESSIBILITY_TEXTURE = new ResourceLocation("textures/gui/accessibility.png");
    @Nullable
    private String splash;
    private Button resetDemoButton;
    @Nullable
    private RealmsNotificationsScreen realmsNotificationsScreen;
    private final PanoramaRenderer panorama = new PanoramaRenderer(CUBE_MAP);
    private final boolean fading;
    private long fadeInStart;
    @Nullable
    private WarningLabel warningLabel;
    private final LogoRenderer logoRenderer;

    public TitleScreen() {
        this(false);
    }

    public TitleScreen(boolean $$0) {
        this($$0, null);
    }

    public TitleScreen(boolean $$0, @Nullable LogoRenderer $$1) {
        super(Component.translatable("narrator.screen.title"));
        this.fading = $$0;
        this.logoRenderer = (LogoRenderer)Objects.requireNonNullElseGet((Object)$$1, () -> new LogoRenderer(false));
    }

    private boolean realmsNotificationsEnabled() {
        return this.minecraft.options.realmsNotifications().get() != false && this.realmsNotificationsScreen != null;
    }

    @Override
    public void tick() {
        if (this.realmsNotificationsEnabled()) {
            this.realmsNotificationsScreen.tick();
        }
        this.minecraft.getRealms32BitWarningStatus().showRealms32BitWarningIfNeeded(this);
    }

    public static CompletableFuture<Void> preloadResources(TextureManager $$0, Executor $$1) {
        return CompletableFuture.allOf((CompletableFuture[])new CompletableFuture[]{$$0.preload(LogoRenderer.MINECRAFT_LOGO, $$1), $$0.preload(LogoRenderer.MINECRAFT_EDITION, $$1), $$0.preload(PANORAMA_OVERLAY, $$1), CUBE_MAP.preload($$0, $$1)});
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    protected void init() {
        if (this.splash == null) {
            this.splash = this.minecraft.getSplashManager().getSplash();
        }
        int $$02 = this.font.width(COPYRIGHT_TEXT);
        int $$1 = this.width - $$02 - 2;
        int $$2 = 24;
        int $$3 = this.height / 4 + 48;
        if (this.minecraft.isDemo()) {
            this.createDemoMenuOptions($$3, 24);
        } else {
            this.createNormalMenuOptions($$3, 24);
        }
        this.addRenderableWidget(new ImageButton(this.width / 2 - 124, $$3 + 72 + 12, 20, 20, 0, 106, 20, Button.WIDGETS_LOCATION, 256, 256, $$0 -> this.minecraft.setScreen(new LanguageSelectScreen((Screen)this, this.minecraft.options, this.minecraft.getLanguageManager())), Component.translatable("narrator.button.language")));
        this.addRenderableWidget(Button.builder(Component.translatable("menu.options"), $$0 -> this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options))).bounds(this.width / 2 - 100, $$3 + 72 + 12, 98, 20).build());
        this.addRenderableWidget(Button.builder(Component.translatable("menu.quit"), $$0 -> this.minecraft.stop()).bounds(this.width / 2 + 2, $$3 + 72 + 12, 98, 20).build());
        this.addRenderableWidget(new ImageButton(this.width / 2 + 104, $$3 + 72 + 12, 20, 20, 0, 0, 20, ACCESSIBILITY_TEXTURE, 32, 64, $$0 -> this.minecraft.setScreen(new AccessibilityOptionsScreen(this, this.minecraft.options)), Component.translatable("narrator.button.accessibility")));
        this.addRenderableWidget(new PlainTextButton($$1, this.height - 10, $$02, 10, COPYRIGHT_TEXT, $$0 -> this.minecraft.setScreen(new WinScreen(false, this.logoRenderer, Runnables.doNothing())), this.font));
        this.minecraft.setConnectedToRealms(false);
        if (this.minecraft.options.realmsNotifications().get().booleanValue() && this.realmsNotificationsScreen == null) {
            this.realmsNotificationsScreen = new RealmsNotificationsScreen();
        }
        if (this.realmsNotificationsEnabled()) {
            this.realmsNotificationsScreen.init(this.minecraft, this.width, this.height);
        }
        if (!this.minecraft.is64Bit()) {
            this.warningLabel = new WarningLabel(this.font, MultiLineLabel.create(this.font, (FormattedText)Component.translatable("title.32bit.deprecation"), 350, 2), this.width / 2, $$3 - 24);
        }
    }

    private void createNormalMenuOptions(int $$02, int $$1) {
        this.addRenderableWidget(Button.builder(Component.translatable("menu.singleplayer"), $$0 -> this.minecraft.setScreen(new SelectWorldScreen(this))).bounds(this.width / 2 - 100, $$02, 200, 20).build());
        Component $$2 = this.getMultiplayerDisabledReason();
        boolean $$3 = $$2 == null;
        Tooltip $$4 = $$2 != null ? Tooltip.create($$2) : null;
        this.addRenderableWidget(Button.builder((Component)Component.translatable((String)"menu.multiplayer"), (Button.OnPress)(Button.OnPress)LambdaMetafactory.metafactory(null, null, null, (Lnet/minecraft/client/gui/components/Button;)V, lambda$createNormalMenuOptions$7(net.minecraft.client.gui.components.Button ), (Lnet/minecraft/client/gui/components/Button;)V)((TitleScreen)this)).bounds((int)(this.width / 2 - 100), (int)($$02 + $$1 * 1), (int)200, (int)20).tooltip((Tooltip)$$4).build()).active = $$3;
        this.addRenderableWidget(Button.builder((Component)Component.translatable((String)"menu.online"), (Button.OnPress)(Button.OnPress)LambdaMetafactory.metafactory(null, null, null, (Lnet/minecraft/client/gui/components/Button;)V, lambda$createNormalMenuOptions$8(net.minecraft.client.gui.components.Button ), (Lnet/minecraft/client/gui/components/Button;)V)((TitleScreen)this)).bounds((int)(this.width / 2 - 100), (int)($$02 + $$1 * 2), (int)200, (int)20).tooltip((Tooltip)$$4).build()).active = $$3;
    }

    @Nullable
    private Component getMultiplayerDisabledReason() {
        if (this.minecraft.allowsMultiplayer()) {
            return null;
        }
        BanDetails $$0 = this.minecraft.multiplayerBan();
        if ($$0 != null) {
            if ($$0.expires() != null) {
                return Component.translatable("title.multiplayer.disabled.banned.temporary");
            }
            return Component.translatable("title.multiplayer.disabled.banned.permanent");
        }
        return Component.translatable("title.multiplayer.disabled");
    }

    private void createDemoMenuOptions(int $$02, int $$12) {
        boolean $$2 = this.checkDemoWorldPresence();
        this.addRenderableWidget(Button.builder(Component.translatable("menu.playdemo"), $$1 -> {
            if ($$2) {
                this.minecraft.createWorldOpenFlows().loadLevel(this, DEMO_LEVEL_ID);
            } else {
                this.minecraft.createWorldOpenFlows().createFreshLevel(DEMO_LEVEL_ID, MinecraftServer.DEMO_SETTINGS, WorldOptions.DEMO_OPTIONS, (Function<RegistryAccess, WorldDimensions>)((Function)WorldPresets::createNormalWorldDimensions));
            }
        }).bounds(this.width / 2 - 100, $$02, 200, 20).build());
        this.resetDemoButton = this.addRenderableWidget(Button.builder(Component.translatable("menu.resetdemo"), $$0 -> {
            LevelStorageSource $$1 = this.minecraft.getLevelSource();
            try (LevelStorageSource.LevelStorageAccess $$2 = $$1.createAccess(DEMO_LEVEL_ID);){
                LevelSummary $$3 = $$2.getSummary();
                if ($$3 != null) {
                    this.minecraft.setScreen(new ConfirmScreen(this::confirmDemo, Component.translatable("selectWorld.deleteQuestion"), Component.translatable("selectWorld.deleteWarning", $$3.getLevelName()), Component.translatable("selectWorld.deleteButton"), CommonComponents.GUI_CANCEL));
                }
            }
            catch (IOException $$4) {
                SystemToast.onWorldAccessFailure(this.minecraft, DEMO_LEVEL_ID);
                LOGGER.warn("Failed to access demo world", (Throwable)$$4);
            }
        }).bounds(this.width / 2 - 100, $$02 + $$12 * 1, 200, 20).build());
        this.resetDemoButton.active = $$2;
    }

    private boolean checkDemoWorldPresence() {
        boolean bl;
        block8: {
            LevelStorageSource.LevelStorageAccess $$0 = this.minecraft.getLevelSource().createAccess(DEMO_LEVEL_ID);
            try {
                boolean bl2 = bl = $$0.getSummary() != null;
                if ($$0 == null) break block8;
            }
            catch (Throwable throwable) {
                try {
                    if ($$0 != null) {
                        try {
                            $$0.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException $$1) {
                    SystemToast.onWorldAccessFailure(this.minecraft, DEMO_LEVEL_ID);
                    LOGGER.warn("Failed to read demo world data", (Throwable)$$1);
                    return false;
                }
            }
            $$0.close();
        }
        return bl;
    }

    private void realmsButtonClicked() {
        this.minecraft.setScreen(new RealmsMainScreen(this));
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        if (this.fadeInStart == 0L && this.fading) {
            this.fadeInStart = Util.getMillis();
        }
        float $$4 = this.fading ? (float)(Util.getMillis() - this.fadeInStart) / 1000.0f : 1.0f;
        this.panorama.render($$3, Mth.clamp($$4, 0.0f, 1.0f));
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderTexture(0, PANORAMA_OVERLAY);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, this.fading ? (float)Mth.ceil(Mth.clamp($$4, 0.0f, 1.0f)) : 1.0f);
        TitleScreen.blit($$0, 0, 0, this.width, this.height, 0.0f, 0.0f, 16, 128, 16, 128);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        float $$5 = this.fading ? Mth.clamp($$4 - 1.0f, 0.0f, 1.0f) : 1.0f;
        this.logoRenderer.renderLogo($$0, this.width, $$5);
        int $$6 = Mth.ceil($$5 * 255.0f) << 24;
        if (($$6 & 0xFC000000) == 0) {
            return;
        }
        if (this.warningLabel != null) {
            this.warningLabel.render($$0, $$6);
        }
        if (this.splash != null) {
            $$0.pushPose();
            $$0.translate(this.width / 2 + 90, 70.0f, 0.0f);
            $$0.mulPose(Axis.ZP.rotationDegrees(-20.0f));
            float $$7 = 1.8f - Mth.abs(Mth.sin((float)(Util.getMillis() % 1000L) / 1000.0f * ((float)Math.PI * 2)) * 0.1f);
            $$7 = $$7 * 100.0f / (float)(this.font.width(this.splash) + 32);
            $$0.scale($$7, $$7, $$7);
            TitleScreen.drawCenteredString($$0, this.font, this.splash, 0, -8, 0xFFFF00 | $$6);
            $$0.popPose();
        }
        String $$8 = "Minecraft " + SharedConstants.getCurrentVersion().getName();
        $$8 = this.minecraft.isDemo() ? $$8 + " Demo" : $$8 + ("release".equalsIgnoreCase(this.minecraft.getVersionType()) ? "" : "/" + this.minecraft.getVersionType());
        if (Minecraft.checkModStatus().shouldReportAsModified()) {
            $$8 = $$8 + I18n.get("menu.modded", new Object[0]);
        }
        TitleScreen.drawString($$0, this.font, $$8, 2, this.height - 10, 0xFFFFFF | $$6);
        for (GuiEventListener $$9 : this.children()) {
            if (!($$9 instanceof AbstractWidget)) continue;
            ((AbstractWidget)$$9).setAlpha($$5);
        }
        super.render($$0, $$1, $$2, $$3);
        if (this.realmsNotificationsEnabled() && $$5 >= 1.0f) {
            RenderSystem.enableDepthTest();
            this.realmsNotificationsScreen.render($$0, $$1, $$2, $$3);
        }
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        if (super.mouseClicked($$0, $$1, $$2)) {
            return true;
        }
        return this.realmsNotificationsEnabled() && this.realmsNotificationsScreen.mouseClicked($$0, $$1, $$2);
    }

    @Override
    public void removed() {
        if (this.realmsNotificationsScreen != null) {
            this.realmsNotificationsScreen.removed();
        }
    }

    private void confirmDemo(boolean $$0) {
        if ($$0) {
            try (LevelStorageSource.LevelStorageAccess $$1 = this.minecraft.getLevelSource().createAccess(DEMO_LEVEL_ID);){
                $$1.deleteLevel();
            }
            catch (IOException $$2) {
                SystemToast.onWorldDeleteFailure(this.minecraft, DEMO_LEVEL_ID);
                LOGGER.warn("Failed to delete demo world", (Throwable)$$2);
            }
        }
        this.minecraft.setScreen(this);
    }

    private /* synthetic */ void lambda$createNormalMenuOptions$8(Button $$0) {
        this.realmsButtonClicked();
    }

    private /* synthetic */ void lambda$createNormalMenuOptions$7(Button $$0) {
        Screen $$1 = this.minecraft.options.skipMultiplayerWarning ? new JoinMultiplayerScreen(this) : new SafetyScreen(this);
        this.minecraft.setScreen($$1);
    }

    record WarningLabel(Font font, MultiLineLabel label, int x, int y) {
        public void render(PoseStack $$0, int $$1) {
            Objects.requireNonNull((Object)this.font);
            this.label.renderBackgroundCentered($$0, this.x, this.y, 9, 2, 0x200000 | Math.min((int)$$1, (int)0x55000000));
            Objects.requireNonNull((Object)this.font);
            this.label.renderCentered($$0, this.x, this.y, 9, 0xFFFFFF | $$1);
        }
    }
}