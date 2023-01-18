/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.util.concurrent.RateLimiter
 *  com.mojang.logging.LogUtils
 *  java.io.File
 *  java.io.FileInputStream
 *  java.io.FileOutputStream
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.OutputStream
 *  java.lang.InterruptedException
 *  java.lang.Long
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.Override
 *  java.lang.Runnable
 *  java.lang.String
 *  java.lang.Thread
 *  java.util.ArrayList
 *  java.util.Arrays
 *  java.util.Collection
 *  java.util.Locale
 *  java.util.concurrent.TimeUnit
 *  java.util.concurrent.locks.ReentrantLock
 *  java.util.function.Consumer
 *  java.util.function.Supplier
 *  java.util.zip.GZIPOutputStream
 *  javax.annotation.Nullable
 *  org.apache.commons.compress.archivers.ArchiveEntry
 *  org.apache.commons.compress.archivers.tar.TarArchiveEntry
 *  org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
 *  org.apache.commons.compress.utils.IOUtils
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
import com.mojang.realmsclient.client.FileUpload;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.client.UploadStatus;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsResetWorldScreen;
import com.mojang.realmsclient.gui.screens.UploadResult;
import com.mojang.realmsclient.util.UploadTokenCache;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;

public class RealmsUploadScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ReentrantLock UPLOAD_LOCK = new ReentrantLock();
    private static final String[] DOTS = new String[]{"", ".", ". .", ". . ."};
    private static final Component VERIFYING_TEXT = Component.translatable("mco.upload.verifying");
    private final RealmsResetWorldScreen lastScreen;
    private final LevelSummary selectedLevel;
    private final long worldId;
    private final int slotId;
    private final UploadStatus uploadStatus;
    private final RateLimiter narrationRateLimiter;
    @Nullable
    private volatile Component[] errorMessage;
    private volatile Component status = Component.translatable("mco.upload.preparing");
    private volatile String progress;
    private volatile boolean cancelled;
    private volatile boolean uploadFinished;
    private volatile boolean showDots = true;
    private volatile boolean uploadStarted;
    private Button backButton;
    private Button cancelButton;
    private int tickCount;
    @Nullable
    private Long previousWrittenBytes;
    @Nullable
    private Long previousTimeSnapshot;
    private long bytesPersSecond;
    private final Runnable callback;

    public RealmsUploadScreen(long $$0, int $$1, RealmsResetWorldScreen $$2, LevelSummary $$3, Runnable $$4) {
        super(GameNarrator.NO_TITLE);
        this.worldId = $$0;
        this.slotId = $$1;
        this.lastScreen = $$2;
        this.selectedLevel = $$3;
        this.uploadStatus = new UploadStatus();
        this.narrationRateLimiter = RateLimiter.create((double)0.1f);
        this.callback = $$4;
    }

    @Override
    public void init() {
        this.backButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, $$0 -> this.onBack()).bounds(this.width / 2 - 100, this.height - 42, 200, 20).build());
        this.backButton.visible = false;
        this.cancelButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, $$0 -> this.onCancel()).bounds(this.width / 2 - 100, this.height - 42, 200, 20).build());
        if (!this.uploadStarted) {
            if (this.lastScreen.slot == -1) {
                this.upload();
            } else {
                this.lastScreen.switchSlot(() -> {
                    if (!this.uploadStarted) {
                        this.uploadStarted = true;
                        this.minecraft.setScreen(this);
                        this.upload();
                    }
                });
            }
        }
    }

    private void onBack() {
        this.callback.run();
    }

    private void onCancel() {
        this.cancelled = true;
        this.minecraft.setScreen(this.lastScreen);
    }

    @Override
    public boolean keyPressed(int $$0, int $$1, int $$2) {
        if ($$0 == 256) {
            if (this.showDots) {
                this.onCancel();
            } else {
                this.onBack();
            }
            return true;
        }
        return super.keyPressed($$0, $$1, $$2);
    }

    @Override
    public void render(PoseStack $$0, int $$1, int $$2, float $$3) {
        this.renderBackground($$0);
        if (!this.uploadFinished && this.uploadStatus.bytesWritten != 0L && this.uploadStatus.bytesWritten == this.uploadStatus.totalBytes) {
            this.status = VERIFYING_TEXT;
            this.cancelButton.active = false;
        }
        RealmsUploadScreen.drawCenteredString($$0, this.font, this.status, this.width / 2, 50, 0xFFFFFF);
        if (this.showDots) {
            this.drawDots($$0);
        }
        if (this.uploadStatus.bytesWritten != 0L && !this.cancelled) {
            this.drawProgressBar($$0);
            this.drawUploadSpeed($$0);
        }
        if (this.errorMessage != null) {
            for (int $$4 = 0; $$4 < this.errorMessage.length; ++$$4) {
                RealmsUploadScreen.drawCenteredString($$0, this.font, this.errorMessage[$$4], this.width / 2, 110 + 12 * $$4, 0xFF0000);
            }
        }
        super.render($$0, $$1, $$2, $$3);
    }

    private void drawDots(PoseStack $$0) {
        int $$1 = this.font.width(this.status);
        this.font.draw($$0, DOTS[this.tickCount / 10 % DOTS.length], (float)(this.width / 2 + $$1 / 2 + 5), 50.0f, 0xFFFFFF);
    }

    private void drawProgressBar(PoseStack $$0) {
        double $$1 = Math.min((double)((double)this.uploadStatus.bytesWritten / (double)this.uploadStatus.totalBytes), (double)1.0);
        this.progress = String.format((Locale)Locale.ROOT, (String)"%.1f", (Object[])new Object[]{$$1 * 100.0});
        RenderSystem.setShader((Supplier<ShaderInstance>)((Supplier)GameRenderer::getPositionColorShader));
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableTexture();
        double $$2 = this.width / 2 - 100;
        double $$3 = 0.5;
        Tesselator $$4 = Tesselator.getInstance();
        BufferBuilder $$5 = $$4.getBuilder();
        $$5.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        $$5.vertex($$2 - 0.5, 95.5, 0.0).color(217, 210, 210, 255).endVertex();
        $$5.vertex($$2 + 200.0 * $$1 + 0.5, 95.5, 0.0).color(217, 210, 210, 255).endVertex();
        $$5.vertex($$2 + 200.0 * $$1 + 0.5, 79.5, 0.0).color(217, 210, 210, 255).endVertex();
        $$5.vertex($$2 - 0.5, 79.5, 0.0).color(217, 210, 210, 255).endVertex();
        $$5.vertex($$2, 95.0, 0.0).color(128, 128, 128, 255).endVertex();
        $$5.vertex($$2 + 200.0 * $$1, 95.0, 0.0).color(128, 128, 128, 255).endVertex();
        $$5.vertex($$2 + 200.0 * $$1, 80.0, 0.0).color(128, 128, 128, 255).endVertex();
        $$5.vertex($$2, 80.0, 0.0).color(128, 128, 128, 255).endVertex();
        $$4.end();
        RenderSystem.enableTexture();
        RealmsUploadScreen.drawCenteredString($$0, this.font, this.progress + " %", this.width / 2, 84, 0xFFFFFF);
    }

    private void drawUploadSpeed(PoseStack $$0) {
        if (this.tickCount % 20 == 0) {
            if (this.previousWrittenBytes != null) {
                long $$1 = Util.getMillis() - this.previousTimeSnapshot;
                if ($$1 == 0L) {
                    $$1 = 1L;
                }
                this.bytesPersSecond = 1000L * (this.uploadStatus.bytesWritten - this.previousWrittenBytes) / $$1;
                this.drawUploadSpeed0($$0, this.bytesPersSecond);
            }
            this.previousWrittenBytes = this.uploadStatus.bytesWritten;
            this.previousTimeSnapshot = Util.getMillis();
        } else {
            this.drawUploadSpeed0($$0, this.bytesPersSecond);
        }
    }

    private void drawUploadSpeed0(PoseStack $$0, long $$1) {
        if ($$1 > 0L) {
            int $$2 = this.font.width(this.progress);
            String $$3 = "(" + Unit.humanReadable($$1) + "/s)";
            this.font.draw($$0, $$3, (float)(this.width / 2 + $$2 / 2 + 15), 84.0f, 0xFFFFFF);
        }
    }

    @Override
    public void tick() {
        super.tick();
        ++this.tickCount;
        if (this.status != null && this.narrationRateLimiter.tryAcquire(1)) {
            Component $$0 = this.createProgressNarrationMessage();
            this.minecraft.getNarrator().sayNow($$0);
        }
    }

    private Component createProgressNarrationMessage() {
        ArrayList $$0 = Lists.newArrayList();
        $$0.add((Object)this.status);
        if (this.progress != null) {
            $$0.add((Object)Component.literal(this.progress + "%"));
        }
        if (this.errorMessage != null) {
            $$0.addAll((Collection)Arrays.asList((Object[])this.errorMessage));
        }
        return CommonComponents.joinLines((Collection<? extends Component>)$$0);
    }

    private void upload() {
        this.uploadStarted = true;
        new Thread(() -> {
            File $$0 = null;
            RealmsClient $$12 = RealmsClient.create();
            long $$2 = this.worldId;
            try {
                if (!UPLOAD_LOCK.tryLock(1L, TimeUnit.SECONDS)) {
                    this.status = Component.translatable("mco.upload.close.failure");
                    return;
                }
                UploadInfo $$3 = null;
                for (int $$4 = 0; $$4 < 20; ++$$4) {
                    block35: {
                        if (!this.cancelled) break block35;
                        this.uploadCancelled();
                        return;
                    }
                    try {
                        $$3 = $$12.requestUploadInfo($$2, UploadTokenCache.get($$2));
                        if ($$3 == null) continue;
                        break;
                    }
                    catch (RetryCallException $$5) {
                        Thread.sleep((long)($$5.delaySeconds * 1000));
                    }
                }
                if ($$3 == null) {
                    this.status = Component.translatable("mco.upload.close.failure");
                    return;
                }
                UploadTokenCache.put($$2, $$3.getToken());
                if (!$$3.isWorldClosed()) {
                    this.status = Component.translatable("mco.upload.close.failure");
                    return;
                }
                if (this.cancelled) {
                    this.uploadCancelled();
                    return;
                }
                File $$6 = new File(this.minecraft.gameDirectory.getAbsolutePath(), "saves");
                $$0 = this.tarGzipArchive(new File($$6, this.selectedLevel.getLevelId()));
                if (this.cancelled) {
                    this.uploadCancelled();
                    return;
                }
                if (!this.verify($$0)) {
                    long $$7 = $$0.length();
                    Unit $$8 = Unit.getLargest($$7);
                    Unit $$9 = Unit.getLargest(0x140000000L);
                    if (Unit.humanReadable($$7, $$8).equals((Object)Unit.humanReadable(0x140000000L, $$9)) && $$8 != Unit.B) {
                        Unit $$10 = Unit.values()[$$8.ordinal() - 1];
                        this.setErrorMessage(Component.translatable("mco.upload.size.failure.line1", this.selectedLevel.getLevelName()), Component.translatable("mco.upload.size.failure.line2", Unit.humanReadable($$7, $$10), Unit.humanReadable(0x140000000L, $$10)));
                        return;
                    }
                    this.setErrorMessage(Component.translatable("mco.upload.size.failure.line1", this.selectedLevel.getLevelName()), Component.translatable("mco.upload.size.failure.line2", Unit.humanReadable($$7, $$8), Unit.humanReadable(0x140000000L, $$9)));
                    return;
                }
                this.status = Component.translatable("mco.upload.uploading", this.selectedLevel.getLevelName());
                FileUpload $$11 = new FileUpload($$0, this.worldId, this.slotId, $$3, this.minecraft.getUser(), SharedConstants.getCurrentVersion().getName(), this.uploadStatus);
                $$11.upload((Consumer<UploadResult>)((Consumer)$$1 -> {
                    if ($$1.statusCode >= 200 && $$1.statusCode < 300) {
                        this.uploadFinished = true;
                        this.status = Component.translatable("mco.upload.done");
                        this.backButton.setMessage(CommonComponents.GUI_DONE);
                        UploadTokenCache.invalidate($$2);
                    } else if ($$1.statusCode == 400 && $$1.errorMessage != null) {
                        this.setErrorMessage(Component.translatable("mco.upload.failed", $$1.errorMessage));
                    } else {
                        this.setErrorMessage(Component.translatable("mco.upload.failed", $$1.statusCode));
                    }
                }));
                while (!$$11.isFinished()) {
                    if (this.cancelled) {
                        $$11.cancel();
                        this.uploadCancelled();
                        return;
                    }
                    try {
                        Thread.sleep((long)500L);
                    }
                    catch (InterruptedException $$122) {
                        LOGGER.error("Failed to check Realms file upload status");
                    }
                }
            }
            catch (IOException $$13) {
                this.setErrorMessage(Component.translatable("mco.upload.failed", $$13.getMessage()));
            }
            catch (RealmsServiceException $$14) {
                this.setErrorMessage(Component.translatable("mco.upload.failed", $$14.toString()));
            }
            catch (InterruptedException $$15) {
                LOGGER.error("Could not acquire upload lock");
            }
            finally {
                this.uploadFinished = true;
                if (!UPLOAD_LOCK.isHeldByCurrentThread()) {
                    return;
                }
                UPLOAD_LOCK.unlock();
                this.showDots = false;
                this.backButton.visible = true;
                this.cancelButton.visible = false;
                if ($$0 != null) {
                    LOGGER.debug("Deleting file {}", (Object)$$0.getAbsolutePath());
                    $$0.delete();
                }
            }
        }).start();
    }

    private void setErrorMessage(Component ... $$0) {
        this.errorMessage = $$0;
    }

    private void uploadCancelled() {
        this.status = Component.translatable("mco.upload.cancelled");
        LOGGER.debug("Upload was cancelled");
    }

    private boolean verify(File $$0) {
        return $$0.length() < 0x140000000L;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private File tarGzipArchive(File $$0) throws IOException {
        try (TarArchiveOutputStream $$1 = null;){
            File $$2 = File.createTempFile((String)"realms-upload-file", (String)".tar.gz");
            $$1 = new TarArchiveOutputStream((OutputStream)new GZIPOutputStream((OutputStream)new FileOutputStream($$2)));
            $$1.setLongFileMode(3);
            this.addFileToTarGz($$1, $$0.getAbsolutePath(), "world", true);
            $$1.finish();
            File file = $$2;
            return file;
        }
    }

    private void addFileToTarGz(TarArchiveOutputStream $$0, String $$1, String $$2, boolean $$3) throws IOException {
        if (this.cancelled) {
            return;
        }
        File $$4 = new File($$1);
        String $$5 = $$3 ? $$2 : $$2 + $$4.getName();
        TarArchiveEntry $$6 = new TarArchiveEntry($$4, $$5);
        $$0.putArchiveEntry((ArchiveEntry)$$6);
        if ($$4.isFile()) {
            IOUtils.copy((InputStream)new FileInputStream($$4), (OutputStream)$$0);
            $$0.closeArchiveEntry();
        } else {
            $$0.closeArchiveEntry();
            File[] $$7 = $$4.listFiles();
            if ($$7 != null) {
                for (File $$8 : $$7) {
                    this.addFileToTarGz($$0, $$8.getAbsolutePath(), $$5 + "/", false);
                }
            }
        }
    }
}