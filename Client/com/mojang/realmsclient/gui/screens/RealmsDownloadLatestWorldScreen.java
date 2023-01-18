/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.util.concurrent.RateLimiter
 *  com.mojang.logging.LogUtils
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  java.lang.Exception
 *  java.lang.InterruptedException
 *  java.lang.Long
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.String
 *  java.lang.Thread
 *  java.util.ArrayList
 *  java.util.Collection
 *  java.util.Locale
 *  java.util.concurrent.TimeUnit
 *  java.util.concurrent.locks.ReentrantLock
 *  java.util.function.Supplier
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.Unit;
import com.mojang.realmsclient.client.FileDownload;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsScreen;
import org.slf4j.Logger;

public class RealmsDownloadLatestWorldScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ReentrantLock DOWNLOAD_LOCK = new ReentrantLock();
    private final Screen lastScreen;
    private final WorldDownload worldDownload;
    private final Component downloadTitle;
    private final RateLimiter narrationRateLimiter;
    private Button cancelButton;
    private final String worldName;
    private final DownloadStatus downloadStatus;
    @Nullable
    private volatile Component errorMessage;
    private volatile Component status = Component.translatable("mco.download.preparing");
    @Nullable
    private volatile String progress;
    private volatile boolean cancelled;
    private volatile boolean showDots = true;
    private volatile boolean finished;
    private volatile boolean extracting;
    @Nullable
    private Long previousWrittenBytes;
    @Nullable
    private Long previousTimeSnapshot;
    private long bytesPersSecond;
    private int animTick;
    private static final String[] DOTS = new String[]{"", ".", ". .", ". . ."};
    private int dotIndex;
    private boolean checked;
    private final BooleanConsumer callback;

    public RealmsDownloadLatestWorldScreen(Screen $$0, WorldDownload $$1, String $$2, BooleanConsumer $$3) {
        super(GameNarrator.NO_TITLE);
        this.callback = $$3;
        this.lastScreen = $$0;
        this.worldName = $$2;
        this.worldDownload = $$1;
        this.downloadStatus = new DownloadStatus();
        this.downloadTitle = Component.translatable("mco.download.title");
        this.narrationRateLimiter = RateLimiter.create((double)0.1f);
    }

    @Override
    public void init() {
        this.cancelButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> {
            this.cancelled = true;
            this.backButtonClicked();
        }).bounds(this.width / 2 - 100, this.height - 42, 200, 20).build());
        this.checkDownloadSize();
    }

    private void checkDownloadSize() {
        if (this.finished) {
            return;
        }
        if (!this.checked && this.getContentLength(this.worldDownload.downloadLink) >= 0x140000000L) {
            MutableComponent $$02 = Component.translatable("mco.download.confirmation.line1", Unit.humanReadable(0x140000000L));
            MutableComponent $$1 = Component.translatable("mco.download.confirmation.line2");
            this.minecraft.setScreen(new RealmsLongConfirmationScreen($$0 -> {
                this.checked = true;
                this.minecraft.setScreen(this);
                this.downloadSave();
            }, RealmsLongConfirmationScreen.Type.Warning, $$02, $$1, false));
        } else {
            this.downloadSave();
        }
    }

    private long getContentLength(String $$0) {
        FileDownload $$1 = new FileDownload();
        return $$1.contentLength($$0);
    }

    @Override
    public void tick() {
        super.tick();
        ++this.animTick;
        if (this.status != null && this.narrationRateLimiter.tryAcquire(1)) {
            Component $$0 = this.createProgressNarrationMessage();
            this.minecraft.getNarrator().sayNow($$0);
        }
    }

    private Component createProgressNarrationMessage() {
        ArrayList $$0 = Lists.newArrayList();
        $$0.add((Object)this.downloadTitle);
        $$0.add((Object)this.status);
        if (this.progress != null) {
            $$0.add((Object)Component.literal(this.progress + "%"));
            $$0.add((Object)Component.literal(Unit.humanReadable(this.bytesPersSecond) + "/s"));
        }
        if (this.errorMessage != null) {
            $$0.add((Object)this.errorMessage);
        }
        return CommonComponents.joinLines((Collection<? extends Component>)$$0);
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 256) {
            this.cancelled = true;
            this.backButtonClicked();
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    private void backButtonClicked() {
        if (this.finished && this.callback != null && this.errorMessage == null) {
            this.callback.accept(true);
        }
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        RealmsDownloadLatestWorldScreen.drawCenteredString($$0, this.font, this.downloadTitle, this.width / 2, 20, 0xFFFFFF);
        RealmsDownloadLatestWorldScreen.drawCenteredString($$0, this.font, this.status, this.width / 2, 50, 0xFFFFFF);
        if (this.showDots) {
            this.drawDots($$0);
        }
        if (this.downloadStatus.bytesWritten != 0L && !this.cancelled) {
            this.drawProgressBar($$0);
            this.drawDownloadSpeed($$0);
        }
        if (this.errorMessage != null) {
            RealmsDownloadLatestWorldScreen.drawCenteredString($$0, this.font, this.errorMessage, this.width / 2, 110, 0xFF0000);
        }
        super.render($$0, $$1, $$2, $$3);
    }

    private void drawDots(PoseStack $$0) {
        int $$1 = this.font.width(this.status);
        if (this.animTick % 10 == 0) {
            ++this.dotIndex;
        }
        this.font.draw($$0, DOTS[this.dotIndex % DOTS.length], (float)(this.width / 2 + $$1 / 2 + 5), 50.0f, 0xFFFFFF);
    }

    private void drawProgressBar(PoseStack $$0) {
        double $$1 = Math.min((double)((double)this.downloadStatus.bytesWritten / (double)this.downloadStatus.totalBytes), (double)1.0);
        this.progress = String.format((Locale)Locale.ROOT, (String)"%.1f", (Object[])new Object[]{$$1 * 100.0});
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionColorShader));
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableTexture();
        Tesselator $$2 = Tesselator.getInstance();
        BufferBuilder $$3 = $$2.getBuilder();
        $$3.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        double $$4 = this.width / 2 - 100;
        double $$5 = 0.5;
        $$3.vertex($$4 - 0.5, 95.5, 0.0).color(217, 210, 210, 255).endVertex();
        $$3.vertex($$4 + 200.0 * $$1 + 0.5, 95.5, 0.0).color(217, 210, 210, 255).endVertex();
        $$3.vertex($$4 + 200.0 * $$1 + 0.5, 79.5, 0.0).color(217, 210, 210, 255).endVertex();
        $$3.vertex($$4 - 0.5, 79.5, 0.0).color(217, 210, 210, 255).endVertex();
        $$3.vertex($$4, 95.0, 0.0).color(128, 128, 128, 255).endVertex();
        $$3.vertex($$4 + 200.0 * $$1, 95.0, 0.0).color(128, 128, 128, 255).endVertex();
        $$3.vertex($$4 + 200.0 * $$1, 80.0, 0.0).color(128, 128, 128, 255).endVertex();
        $$3.vertex($$4, 80.0, 0.0).color(128, 128, 128, 255).endVertex();
        $$2.end();
        RenderSystem.enableTexture();
        RealmsDownloadLatestWorldScreen.drawCenteredString($$0, this.font, this.progress + " %", this.width / 2, 84, 0xFFFFFF);
    }

    private void drawDownloadSpeed(PoseStack $$0) {
        if (this.animTick % 20 == 0) {
            if (this.previousWrittenBytes != null) {
                long $$1 = Util.getMillis() - this.previousTimeSnapshot;
                if ($$1 == 0L) {
                    $$1 = 1L;
                }
                this.bytesPersSecond = 1000L * (this.downloadStatus.bytesWritten - this.previousWrittenBytes) / $$1;
                this.drawDownloadSpeed0($$0, this.bytesPersSecond);
            }
            this.previousWrittenBytes = this.downloadStatus.bytesWritten;
            this.previousTimeSnapshot = Util.getMillis();
        } else {
            this.drawDownloadSpeed0($$0, this.bytesPersSecond);
        }
    }

    private void drawDownloadSpeed0(PoseStack $$0, long $$1) {
        if ($$1 > 0L) {
            int $$2 = this.font.width(this.progress);
            String $$3 = "(" + Unit.humanReadable($$1) + "/s)";
            this.font.draw($$0, $$3, (float)(this.width / 2 + $$2 / 2 + 15), 84.0f, 0xFFFFFF);
        }
    }

    private void downloadSave() {
        new Thread(() -> {
            try {
                if (!DOWNLOAD_LOCK.tryLock(1L, TimeUnit.SECONDS)) {
                    this.status = Component.translatable("mco.download.failed");
                    return;
                }
                if (this.cancelled) {
                    this.downloadCancelled();
                    return;
                }
                this.status = Component.translatable("mco.download.downloading", this.worldName);
                FileDownload $$0 = new FileDownload();
                $$0.contentLength(this.worldDownload.downloadLink);
                $$0.download(this.worldDownload, this.worldName, this.downloadStatus, this.minecraft.getLevelSource());
                while (!$$0.isFinished()) {
                    if ($$0.isError()) {
                        $$0.cancel();
                        this.errorMessage = Component.translatable("mco.download.failed");
                        this.cancelButton.setMessage(CommonComponents.GUI_DONE);
                        return;
                    }
                    if ($$0.isExtracting()) {
                        if (!this.extracting) {
                            this.status = Component.translatable("mco.download.extracting");
                        }
                        this.extracting = true;
                    }
                    if (this.cancelled) {
                        $$0.cancel();
                        this.downloadCancelled();
                        return;
                    }
                    try {
                        Thread.sleep((long)500L);
                    }
                    catch (InterruptedException $$1) {
                        LOGGER.error("Failed to check Realms backup download status");
                    }
                }
                this.finished = true;
                this.status = Component.translatable("mco.download.done");
                this.cancelButton.setMessage(CommonComponents.GUI_DONE);
            }
            catch (InterruptedException $$2) {
                LOGGER.error("Could not acquire upload lock");
            }
            catch (Exception $$3) {
                this.errorMessage = Component.translatable("mco.download.failed");
                $$3.printStackTrace();
            }
            finally {
                if (!DOWNLOAD_LOCK.isHeldByCurrentThread()) {
                    return;
                }
                DOWNLOAD_LOCK.unlock();
                this.showDots = false;
                this.finished = true;
            }
        }).start();
    }

    private void downloadCancelled() {
        this.status = Component.translatable("mco.download.cancelled");
    }

    public static class DownloadStatus {
        public volatile long bytesWritten;
        public volatile long totalBytes;
    }
}