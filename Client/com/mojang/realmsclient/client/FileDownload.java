/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.common.hash.Hashing
 *  com.google.common.io.Files
 *  com.mojang.logging.LogUtils
 *  java.awt.event.ActionEvent
 *  java.awt.event.ActionListener
 *  java.io.BufferedInputStream
 *  java.io.File
 *  java.io.FileInputStream
 *  java.io.FileOutputStream
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.OutputStream
 *  java.lang.CharSequence
 *  java.lang.Exception
 *  java.lang.Integer
 *  java.lang.Long
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Thread
 *  java.lang.Thread$UncaughtExceptionHandler
 *  java.lang.Throwable
 *  java.nio.file.Path
 *  java.util.Locale
 *  java.util.regex.Matcher
 *  java.util.regex.Pattern
 *  javax.annotation.Nullable
 *  org.apache.commons.compress.archivers.tar.TarArchiveEntry
 *  org.apache.commons.compress.archivers.tar.TarArchiveInputStream
 *  org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.io.output.CountingOutputStream
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.client.methods.CloseableHttpResponse
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.impl.client.CloseableHttpClient
 *  org.apache.http.impl.client.HttpClientBuilder
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.client;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsDefaultUncaughtExceptionHandler;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;

public class FileDownload {
    static final Logger LOGGER = LogUtils.getLogger();
    volatile boolean cancelled;
    volatile boolean finished;
    volatile boolean error;
    volatile boolean extracting;
    @Nullable
    private volatile File tempFile;
    volatile File resourcePackPath;
    @Nullable
    private volatile HttpGet request;
    @Nullable
    private Thread currentThread;
    private final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(120000).setConnectTimeout(120000).build();
    private static final String[] INVALID_FILE_NAMES = new String[]{"CON", "COM", "PRN", "AUX", "CLOCK$", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long contentLength(String $$0) {
        CloseableHttpClient $$1 = null;
        HttpGet $$2 = null;
        try {
            $$2 = new HttpGet($$0);
            $$1 = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
            CloseableHttpResponse $$3 = $$1.execute((HttpUriRequest)$$2);
            long l = Long.parseLong((String)$$3.getFirstHeader("Content-Length").getValue());
            return l;
        }
        catch (Throwable $$5) {
            LOGGER.error("Unable to get content length for download");
            long l = 0L;
            return l;
        }
        finally {
            if ($$2 != null) {
                $$2.releaseConnection();
            }
            if ($$1 != null) {
                try {
                    $$1.close();
                }
                catch (IOException $$4) {
                    LOGGER.error("Could not close http client", (Throwable)$$4);
                }
            }
        }
    }

    public void download(WorldDownload $$0, String $$1, RealmsDownloadLatestWorldScreen.DownloadStatus $$2, LevelStorageSource $$3) {
        if (this.currentThread != null) {
            return;
        }
        this.currentThread = new Thread(() -> {
            CloseableHttpClient $$4 = null;
            try {
                this.tempFile = File.createTempFile((String)"backup", (String)".tar.gz");
                this.request = new HttpGet($$0.downloadLink);
                $$4 = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
                CloseableHttpResponse $$5 = $$4.execute((HttpUriRequest)this.request);
                $$1.totalBytes = Long.parseLong((String)$$5.getFirstHeader("Content-Length").getValue());
                if ($$5.getStatusLine().getStatusCode() != 200) {
                    this.error = true;
                    this.request.abort();
                    return;
                }
                FileOutputStream $$12 = new FileOutputStream(this.tempFile);
                ProgressListener $$13 = new ProgressListener($$1.trim(), this.tempFile, $$3, $$2);
                DownloadCountingOutputStream $$14 = new DownloadCountingOutputStream((OutputStream)$$12);
                $$14.setListener($$13);
                IOUtils.copy((InputStream)$$5.getEntity().getContent(), (OutputStream)$$14);
                return;
            }
            catch (Exception $$21) {
                LOGGER.error("Caught exception while downloading: {}", (Object)$$21.getMessage());
                this.error = true;
                return;
            }
            finally {
                block40: {
                    block41: {
                        CloseableHttpResponse $$22;
                        this.request.releaseConnection();
                        if (this.tempFile != null) {
                            this.tempFile.delete();
                        }
                        if (this.error) break block40;
                        if ($$0.resourcePackUrl.isEmpty() || $$0.resourcePackHash.isEmpty()) break block41;
                        try {
                            this.tempFile = File.createTempFile((String)"resources", (String)".tar.gz");
                            this.request = new HttpGet($$0.resourcePackUrl);
                            $$22 = $$4.execute((HttpUriRequest)this.request);
                            $$1.totalBytes = Long.parseLong((String)$$22.getFirstHeader("Content-Length").getValue());
                            if ($$22.getStatusLine().getStatusCode() != 200) {
                                this.error = true;
                                this.request.abort();
                                return;
                            }
                        }
                        catch (Exception $$26) {
                            LOGGER.error("Caught exception while downloading: {}", (Object)$$26.getMessage());
                            this.error = true;
                        }
                        FileOutputStream $$23 = new FileOutputStream(this.tempFile);
                        ResourcePackProgressListener $$24 = new ResourcePackProgressListener(this.tempFile, $$2, $$0);
                        DownloadCountingOutputStream $$25 = new DownloadCountingOutputStream((OutputStream)$$23);
                        $$25.setListener($$24);
                        IOUtils.copy((InputStream)$$22.getEntity().getContent(), (OutputStream)$$25);
                        break block40;
                        finally {
                            this.request.releaseConnection();
                            if (this.tempFile != null) {
                                this.tempFile.delete();
                            }
                        }
                    }
                    this.finished = true;
                }
                if ($$4 != null) {
                    try {
                        $$4.close();
                    }
                    catch (IOException $$27) {
                        LOGGER.error("Failed to close Realms download client");
                    }
                }
            }
        });
        this.currentThread.setUncaughtExceptionHandler((Thread.UncaughtExceptionHandler)new RealmsDefaultUncaughtExceptionHandler(LOGGER));
        this.currentThread.start();
    }

    public void cancel() {
        if (this.request != null) {
            this.request.abort();
        }
        if (this.tempFile != null) {
            this.tempFile.delete();
        }
        this.cancelled = true;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public boolean isError() {
        return this.error;
    }

    public boolean isExtracting() {
        return this.extracting;
    }

    public static String findAvailableFolderName(String $$0) {
        $$0 = $$0.replaceAll("[\\./\"]", "_");
        for (String $$1 : INVALID_FILE_NAMES) {
            if (!$$0.equalsIgnoreCase($$1)) continue;
            $$0 = "_" + $$0 + "_";
        }
        return $$0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    void untarGzipArchive(String $$0, @Nullable File $$1, LevelStorageSource $$2) throws IOException {
        File $$15;
        Object var7_11;
        String $$13;
        block49: {
            TarArchiveInputStream tarArchiveInputStream;
            char $$5;
            Pattern $$3 = Pattern.compile((String)".*-([0-9]+)$");
            int $$4 = 1;
            char[] object = SharedConstants.ILLEGAL_FILE_CHARACTERS;
            int n = object.length;
            for (int n2 = 0; n2 < n; $$0 = $$0.replace($$5, '_'), ++n2) {
                $$5 = object[n2];
            }
            if (StringUtils.isEmpty((CharSequence)$$0)) {
                $$0 = "Realm";
            }
            $$0 = FileDownload.findAvailableFolderName($$0);
            try {
                for (LevelStorageSource.LevelDirectory $$6 : $$2.findLevelCandidates()) {
                    String $$7 = $$6.directoryName();
                    if (!$$7.toLowerCase(Locale.ROOT).startsWith($$0.toLowerCase(Locale.ROOT))) continue;
                    Matcher $$8 = $$3.matcher((CharSequence)$$7);
                    if ($$8.matches()) {
                        int $$9 = Integer.parseInt((String)$$8.group(1));
                        if ($$9 <= $$4) continue;
                        $$4 = $$9;
                        continue;
                    }
                    ++$$4;
                }
            }
            catch (Exception exception) {
                LOGGER.error("Error getting level list", (Throwable)exception);
                this.error = true;
                return;
            }
            if (!$$2.isNewLevelIdAcceptable($$0) || $$4 > 1) {
                String $$11 = $$0 + ($$4 == 1 ? "" : "-" + $$4);
                if (!$$2.isNewLevelIdAcceptable($$11)) {
                    boolean bl;
                    boolean bl2 = false;
                    while (!bl) {
                        if (!$$2.isNewLevelIdAcceptable($$11 = $$0 + (++$$4 == 1 ? "" : "-" + $$4))) continue;
                        bl = true;
                    }
                }
            } else {
                $$13 = $$0;
            }
            var7_11 = null;
            $$15 = new File(Minecraft.getInstance().gameDirectory.getAbsolutePath(), "saves");
            try {
                $$15.mkdir();
                tarArchiveInputStream = new TarArchiveInputStream((InputStream)new GzipCompressorInputStream((InputStream)new BufferedInputStream((InputStream)new FileInputStream($$1))));
                TarArchiveEntry $$16 = tarArchiveInputStream.getNextTarEntry();
                while ($$16 != null) {
                    File $$17 = new File($$15, $$16.getName().replace((CharSequence)"world", (CharSequence)$$13));
                    if ($$16.isDirectory()) {
                        $$17.mkdirs();
                    } else {
                        $$17.createNewFile();
                        try (FileOutputStream $$18 = new FileOutputStream($$17);){
                            IOUtils.copy((InputStream)tarArchiveInputStream, (OutputStream)$$18);
                        }
                    }
                    $$16 = tarArchiveInputStream.getNextTarEntry();
                }
                if (tarArchiveInputStream == null) break block49;
            }
            catch (Exception $$22) {
                LOGGER.error("Error extracting world", (Throwable)$$22);
                this.error = true;
                return;
            }
            tarArchiveInputStream.close();
        }
        if ($$1 != null) {
            $$1.delete();
        }
        try (LevelStorageSource.LevelStorageAccess $$19 = $$2.createAccess($$13);){
            $$19.renameLevel($$13.trim());
            Path $$20 = $$19.getLevelPath(LevelResource.LEVEL_DATA_FILE);
            FileDownload.deletePlayerTag($$20.toFile());
        }
        catch (IOException $$21) {
            LOGGER.error("Failed to rename unpacked realms level {}", (Object)$$13, (Object)$$21);
        }
        this.resourcePackPath = new File($$15, $$13 + File.separator + "resources.zip");
        return;
        finally {
            if (var7_11 != null) {
                var7_11.close();
            }
            if ($$1 != null) {
                $$1.delete();
            }
            try (LevelStorageSource.LevelStorageAccess $$23 = $$2.createAccess($$13);){
                $$23.renameLevel($$13.trim());
                Path $$24 = $$23.getLevelPath(LevelResource.LEVEL_DATA_FILE);
                FileDownload.deletePlayerTag($$24.toFile());
            }
            catch (IOException $$25) {
                LOGGER.error("Failed to rename unpacked realms level {}", (Object)$$13, (Object)$$25);
            }
            this.resourcePackPath = new File($$15, $$13 + File.separator + "resources.zip");
        }
    }

    private static void deletePlayerTag(File $$0) {
        if ($$0.exists()) {
            try {
                CompoundTag $$1 = NbtIo.readCompressed($$0);
                CompoundTag $$2 = $$1.getCompound("Data");
                $$2.remove("Player");
                NbtIo.writeCompressed($$1, $$0);
            }
            catch (Exception $$3) {
                $$3.printStackTrace();
            }
        }
    }

    class ResourcePackProgressListener
    implements ActionListener {
        private final File tempFile;
        private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;
        private final WorldDownload worldDownload;

        ResourcePackProgressListener(File $$0, RealmsDownloadLatestWorldScreen.DownloadStatus $$1, WorldDownload $$2) {
            this.tempFile = $$0;
            this.downloadStatus = $$1;
            this.worldDownload = $$2;
        }

        public void actionPerformed(ActionEvent $$0) {
            this.downloadStatus.bytesWritten = ((DownloadCountingOutputStream)((Object)$$0.getSource())).getByteCount();
            if (this.downloadStatus.bytesWritten >= this.downloadStatus.totalBytes && !FileDownload.this.cancelled) {
                try {
                    String $$1 = Hashing.sha1().hashBytes(Files.toByteArray((File)this.tempFile)).toString();
                    if ($$1.equals((Object)this.worldDownload.resourcePackHash)) {
                        FileUtils.copyFile((File)this.tempFile, (File)FileDownload.this.resourcePackPath);
                        FileDownload.this.finished = true;
                    } else {
                        LOGGER.error("Resourcepack had wrong hash (expected {}, found {}). Deleting it.", (Object)this.worldDownload.resourcePackHash, (Object)$$1);
                        FileUtils.deleteQuietly((File)this.tempFile);
                        FileDownload.this.error = true;
                    }
                }
                catch (IOException $$2) {
                    LOGGER.error("Error copying resourcepack file: {}", (Object)$$2.getMessage());
                    FileDownload.this.error = true;
                }
            }
        }
    }

    static class DownloadCountingOutputStream
    extends CountingOutputStream {
        @Nullable
        private ActionListener listener;

        public DownloadCountingOutputStream(OutputStream $$0) {
            super($$0);
        }

        public void setListener(ActionListener $$0) {
            this.listener = $$0;
        }

        protected void afterWrite(int $$0) throws IOException {
            super.afterWrite($$0);
            if (this.listener != null) {
                this.listener.actionPerformed(new ActionEvent((Object)this, 0, null));
            }
        }
    }

    class ProgressListener
    implements ActionListener {
        private final String worldName;
        private final File tempFile;
        private final LevelStorageSource levelStorageSource;
        private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;

        ProgressListener(String $$0, File $$1, LevelStorageSource $$2, RealmsDownloadLatestWorldScreen.DownloadStatus $$3) {
            this.worldName = $$0;
            this.tempFile = $$1;
            this.levelStorageSource = $$2;
            this.downloadStatus = $$3;
        }

        public void actionPerformed(ActionEvent $$0) {
            this.downloadStatus.bytesWritten = ((DownloadCountingOutputStream)((Object)$$0.getSource())).getByteCount();
            if (this.downloadStatus.bytesWritten >= this.downloadStatus.totalBytes && !FileDownload.this.cancelled && !FileDownload.this.error) {
                try {
                    FileDownload.this.extracting = true;
                    FileDownload.this.untarGzipArchive(this.worldName, this.tempFile, this.levelStorageSource);
                }
                catch (IOException $$1) {
                    LOGGER.error("Error extracting archive", (Throwable)$$1);
                    FileDownload.this.error = true;
                }
            }
        }
    }
}