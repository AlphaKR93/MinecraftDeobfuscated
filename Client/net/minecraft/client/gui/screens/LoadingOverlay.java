/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  java.io.FileNotFoundException
 *  java.io.IOException
 *  java.io.InputStream
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Throwable
 *  java.util.Optional
 *  java.util.function.Consumer
 *  java.util.function.IntSupplier
 *  java.util.function.Supplier
 */
package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

public class LoadingOverlay
extends Overlay {
    static final ResourceLocation MOJANG_STUDIOS_LOGO_LOCATION = new ResourceLocation("textures/gui/title/mojangstudios.png");
    private static final int LOGO_BACKGROUND_COLOR = FastColor.ARGB32.color(255, 239, 50, 61);
    private static final int LOGO_BACKGROUND_COLOR_DARK = FastColor.ARGB32.color(255, 0, 0, 0);
    private static final IntSupplier BRAND_BACKGROUND = () -> Minecraft.getInstance().options.darkMojangStudiosBackground().get() != false ? LOGO_BACKGROUND_COLOR_DARK : LOGO_BACKGROUND_COLOR;
    private static final int LOGO_SCALE = 240;
    private static final float LOGO_QUARTER_FLOAT = 60.0f;
    private static final int LOGO_QUARTER = 60;
    private static final int LOGO_HALF = 120;
    private static final float LOGO_OVERLAP = 0.0625f;
    private static final float SMOOTHING = 0.95f;
    public static final long FADE_OUT_TIME = 1000L;
    public static final long FADE_IN_TIME = 500L;
    private final Minecraft minecraft;
    private final ReloadInstance reload;
    private final Consumer<Optional<Throwable>> onFinish;
    private final boolean fadeIn;
    private float currentProgress;
    private long fadeOutStart = -1L;
    private long fadeInStart = -1L;

    public LoadingOverlay(Minecraft $$0, ReloadInstance $$1, Consumer<Optional<Throwable>> $$2, boolean $$3) {
        this.minecraft = $$0;
        this.reload = $$1;
        this.onFinish = $$2;
        this.fadeIn = $$3;
    }

    public static void registerTextures(Minecraft $$0) {
        $$0.getTextureManager().register(MOJANG_STUDIOS_LOGO_LOCATION, new LogoTexture());
    }

    private static int replaceAlpha(int $$0, int $$1) {
        return $$0 & 0xFFFFFF | $$1 << 24;
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        float $$17;
        float $$8;
        int $$4 = this.minecraft.getWindow().getGuiScaledWidth();
        int $$5 = this.minecraft.getWindow().getGuiScaledHeight();
        long $$6 = Util.getMillis();
        if (this.fadeIn && this.fadeInStart == -1L) {
            this.fadeInStart = $$6;
        }
        float $$7 = this.fadeOutStart > -1L ? (float)($$6 - this.fadeOutStart) / 1000.0f : -1.0f;
        float f = $$8 = this.fadeInStart > -1L ? (float)($$6 - this.fadeInStart) / 500.0f : -1.0f;
        if ($$7 >= 1.0f) {
            if (this.minecraft.screen != null) {
                this.minecraft.screen.render($$0, 0, 0, $$3);
            }
            int $$9 = Mth.ceil((1.0f - Mth.clamp($$7 - 1.0f, 0.0f, 1.0f)) * 255.0f);
            LoadingOverlay.fill($$0, 0, 0, $$4, $$5, LoadingOverlay.replaceAlpha(BRAND_BACKGROUND.getAsInt(), $$9));
            float $$10 = 1.0f - Mth.clamp($$7 - 1.0f, 0.0f, 1.0f);
        } else if (this.fadeIn) {
            if (this.minecraft.screen != null && $$8 < 1.0f) {
                this.minecraft.screen.render($$0, $$1, $$2, $$3);
            }
            int $$11 = Mth.ceil(Mth.clamp((double)$$8, 0.15, 1.0) * 255.0);
            LoadingOverlay.fill($$0, 0, 0, $$4, $$5, LoadingOverlay.replaceAlpha(BRAND_BACKGROUND.getAsInt(), $$11));
            float $$12 = Mth.clamp($$8, 0.0f, 1.0f);
        } else {
            int $$13 = BRAND_BACKGROUND.getAsInt();
            float $$14 = (float)($$13 >> 16 & 0xFF) / 255.0f;
            float $$15 = (float)($$13 >> 8 & 0xFF) / 255.0f;
            float $$16 = (float)($$13 & 0xFF) / 255.0f;
            GlStateManager._clearColor($$14, $$15, $$16, 1.0f);
            GlStateManager._clear(16384, Minecraft.ON_OSX);
            $$17 = 1.0f;
        }
        int $$18 = (int)((double)this.minecraft.getWindow().getGuiScaledWidth() * 0.5);
        int $$19 = (int)((double)this.minecraft.getWindow().getGuiScaledHeight() * 0.5);
        double $$20 = Math.min((double)((double)this.minecraft.getWindow().getGuiScaledWidth() * 0.75), (double)this.minecraft.getWindow().getGuiScaledHeight()) * 0.25;
        int $$21 = (int)($$20 * 0.5);
        double $$22 = $$20 * 4.0;
        int $$23 = (int)($$22 * 0.5);
        RenderSystem.setShaderTexture(0, MOJANG_STUDIOS_LOGO_LOCATION);
        RenderSystem.enableBlend();
        RenderSystem.blendEquation(32774);
        RenderSystem.blendFunc(770, 1);
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionTexShader));
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, $$17);
        LoadingOverlay.blit($$0, $$18 - $$23, $$19 - $$21, $$23, (int)$$20, -0.0625f, 0.0f, 120, 60, 120, 120);
        LoadingOverlay.blit($$0, $$18, $$19 - $$21, $$23, (int)$$20, 0.0625f, 60.0f, 120, 60, 120, 120);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        int $$24 = (int)((double)this.minecraft.getWindow().getGuiScaledHeight() * 0.8325);
        float $$25 = this.reload.getActualProgress();
        this.currentProgress = Mth.clamp(this.currentProgress * 0.95f + $$25 * 0.050000012f, 0.0f, 1.0f);
        if ($$7 < 1.0f) {
            this.drawProgressBar($$0, $$4 / 2 - $$23, $$24 - 5, $$4 / 2 + $$23, $$24 + 5, 1.0f - Mth.clamp($$7, 0.0f, 1.0f));
        }
        if ($$7 >= 2.0f) {
            this.minecraft.setOverlay(null);
        }
        if (this.fadeOutStart == -1L && this.reload.isDone() && (!this.fadeIn || $$8 >= 2.0f)) {
            try {
                this.reload.checkExceptions();
                this.onFinish.accept((Object)Optional.empty());
            }
            catch (Throwable $$26) {
                this.onFinish.accept((Object)Optional.of((Object)((Object)$$26)));
            }
            this.fadeOutStart = Util.getMillis();
            if (this.minecraft.screen != null) {
                this.minecraft.screen.init(this.minecraft, this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight());
            }
        }
    }

    private void drawProgressBar(PoseStack $$0, int $$1, int $$2, int $$3, int $$4, float $$5) {
        int $$6 = Mth.ceil((float)($$3 - $$1 - 2) * this.currentProgress);
        int $$7 = Math.round((float)($$5 * 255.0f));
        int $$8 = FastColor.ARGB32.color($$7, 255, 255, 255);
        LoadingOverlay.fill($$0, $$1 + 2, $$2 + 2, $$1 + $$6, $$4 - 2, $$8);
        LoadingOverlay.fill($$0, $$1 + 1, $$2, $$3 - 1, $$2 + 1, $$8);
        LoadingOverlay.fill($$0, $$1 + 1, $$4, $$3 - 1, $$4 - 1, $$8);
        LoadingOverlay.fill($$0, $$1, $$2, $$1 + 1, $$4, $$8);
        LoadingOverlay.fill($$0, $$3, $$2, $$3 - 1, $$4, $$8);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    static class LogoTexture
    extends SimpleTexture {
        public LogoTexture() {
            super(MOJANG_STUDIOS_LOGO_LOCATION);
        }

        @Override
        protected SimpleTexture.TextureImage getTextureImage(ResourceManager $$0) {
            SimpleTexture.TextureImage textureImage;
            block9: {
                VanillaPackResources $$1 = Minecraft.getInstance().getVanillaPackResources();
                IoSupplier<InputStream> $$2 = $$1.getResource(PackType.CLIENT_RESOURCES, MOJANG_STUDIOS_LOGO_LOCATION);
                if ($$2 == null) {
                    return new SimpleTexture.TextureImage((IOException)new FileNotFoundException(MOJANG_STUDIOS_LOGO_LOCATION.toString()));
                }
                InputStream $$3 = $$2.get();
                try {
                    textureImage = new SimpleTexture.TextureImage(new TextureMetadataSection(true, true), NativeImage.read($$3));
                    if ($$3 == null) break block9;
                }
                catch (Throwable throwable) {
                    try {
                        if ($$3 != null) {
                            try {
                                $$3.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (IOException $$4) {
                        return new SimpleTexture.TextureImage($$4);
                    }
                }
                $$3.close();
            }
            return textureImage;
        }
    }
}