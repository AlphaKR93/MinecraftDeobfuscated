/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParser
 *  com.mojang.logging.LogUtils
 *  java.io.File
 *  java.io.FileInputStream
 *  java.io.FileNotFoundException
 *  java.io.IOException
 *  java.io.InputStream
 *  java.io.OutputStream
 *  java.lang.Exception
 *  java.lang.InterruptedException
 *  java.lang.Long
 *  java.lang.Math
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Thread
 *  java.lang.Throwable
 *  java.time.Duration
 *  java.util.Optional
 *  java.util.concurrent.CompletableFuture
 *  java.util.concurrent.TimeUnit
 *  java.util.concurrent.atomic.AtomicBoolean
 *  java.util.function.Consumer
 *  javax.annotation.Nullable
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpResponse
 *  org.apache.http.NameValuePair
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.client.methods.CloseableHttpResponse
 *  org.apache.http.client.methods.HttpPost
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.entity.InputStreamEntity
 *  org.apache.http.impl.client.CloseableHttpClient
 *  org.apache.http.impl.client.HttpClientBuilder
 *  org.apache.http.util.Args
 *  org.apache.http.util.EntityUtils
 *  org.slf4j.Logger
 */
package com.mojang.realmsclient.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.UploadStatus;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.gui.screens.UploadResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.User;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;

public class FileUpload {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_RETRIES = 5;
    private static final String UPLOAD_PATH = "/upload";
    private final File file;
    private final long worldId;
    private final int slotId;
    private final UploadInfo uploadInfo;
    private final String sessionId;
    private final String username;
    private final String clientVersion;
    private final UploadStatus uploadStatus;
    private final AtomicBoolean cancelled = new AtomicBoolean(false);
    @Nullable
    private CompletableFuture<UploadResult> uploadTask;
    private final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout((int)TimeUnit.MINUTES.toMillis(10L)).setConnectTimeout((int)TimeUnit.SECONDS.toMillis(15L)).build();

    public FileUpload(File $$0, long $$1, int $$2, UploadInfo $$3, User $$4, String $$5, UploadStatus $$6) {
        this.file = $$0;
        this.worldId = $$1;
        this.slotId = $$2;
        this.uploadInfo = $$3;
        this.sessionId = $$4.getSessionId();
        this.username = $$4.getName();
        this.clientVersion = $$5;
        this.uploadStatus = $$6;
    }

    public void upload(Consumer<UploadResult> $$0) {
        if (this.uploadTask != null) {
            return;
        }
        this.uploadTask = CompletableFuture.supplyAsync(() -> this.requestUpload(0));
        this.uploadTask.thenAccept($$0);
    }

    public void cancel() {
        this.cancelled.set(true);
        if (this.uploadTask != null) {
            this.uploadTask.cancel(false);
            this.uploadTask = null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private UploadResult requestUpload(int $$0) {
        UploadResult.Builder $$1 = new UploadResult.Builder();
        if (this.cancelled.get()) {
            return $$1.build();
        }
        this.uploadStatus.totalBytes = this.file.length();
        HttpPost $$2 = new HttpPost(this.uploadInfo.getUploadEndpoint().resolve("/upload/" + this.worldId + "/" + this.slotId));
        CloseableHttpClient $$3 = HttpClientBuilder.create().setDefaultRequestConfig(this.requestConfig).build();
        try {
            this.setupRequest($$2);
            CloseableHttpResponse $$4 = $$3.execute((HttpUriRequest)$$2);
            long $$5 = this.getRetryDelaySeconds((HttpResponse)$$4);
            if (this.shouldRetry($$5, $$0)) {
                UploadResult uploadResult = this.retryUploadAfter($$5, $$0);
                return uploadResult;
            }
            this.handleResponse((HttpResponse)$$4, $$1);
        }
        catch (Exception $$6) {
            if (!this.cancelled.get()) {
                LOGGER.error("Caught exception while uploading: ", (Throwable)$$6);
            }
        }
        finally {
            this.cleanup($$2, $$3);
        }
        return $$1.build();
    }

    private void cleanup(HttpPost $$0, @Nullable CloseableHttpClient $$1) {
        $$0.releaseConnection();
        if ($$1 != null) {
            try {
                $$1.close();
            }
            catch (IOException $$2) {
                LOGGER.error("Failed to close Realms upload client");
            }
        }
    }

    private void setupRequest(HttpPost $$0) throws FileNotFoundException {
        $$0.setHeader("Cookie", "sid=" + this.sessionId + ";token=" + this.uploadInfo.getToken() + ";user=" + this.username + ";version=" + this.clientVersion);
        CustomInputStreamEntity $$1 = new CustomInputStreamEntity((InputStream)new FileInputStream(this.file), this.file.length(), this.uploadStatus);
        $$1.setContentType("application/octet-stream");
        $$0.setEntity((HttpEntity)$$1);
    }

    private void handleResponse(HttpResponse $$0, UploadResult.Builder $$1) throws IOException {
        String $$3;
        int $$2 = $$0.getStatusLine().getStatusCode();
        if ($$2 == 401) {
            LOGGER.debug("Realms server returned 401: {}", (Object)$$0.getFirstHeader("WWW-Authenticate"));
        }
        $$1.withStatusCode($$2);
        if ($$0.getEntity() != null && ($$3 = EntityUtils.toString((HttpEntity)$$0.getEntity(), (String)"UTF-8")) != null) {
            try {
                JsonParser $$4 = new JsonParser();
                JsonElement $$5 = $$4.parse($$3).getAsJsonObject().get("errorMsg");
                Optional $$6 = Optional.ofNullable((Object)$$5).map(JsonElement::getAsString);
                $$1.withErrorMessage((String)$$6.orElse(null));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    private boolean shouldRetry(long $$0, int $$1) {
        return $$0 > 0L && $$1 + 1 < 5;
    }

    private UploadResult retryUploadAfter(long $$0, int $$1) throws InterruptedException {
        Thread.sleep((long)Duration.ofSeconds((long)$$0).toMillis());
        return this.requestUpload($$1 + 1);
    }

    private long getRetryDelaySeconds(HttpResponse $$0) {
        return (Long)Optional.ofNullable((Object)$$0.getFirstHeader("Retry-After")).map(NameValuePair::getValue).map(Long::valueOf).orElse((Object)0L);
    }

    public boolean isFinished() {
        return this.uploadTask.isDone() || this.uploadTask.isCancelled();
    }

    static class CustomInputStreamEntity
    extends InputStreamEntity {
        private final long length;
        private final InputStream content;
        private final UploadStatus uploadStatus;

        public CustomInputStreamEntity(InputStream $$0, long $$1, UploadStatus $$2) {
            super($$0);
            this.content = $$0;
            this.length = $$1;
            this.uploadStatus = $$2;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void writeTo(OutputStream $$0) throws IOException {
            block7: {
                Args.notNull((Object)$$0, (String)"Output stream");
                try (InputStream $$1 = this.content;){
                    int $$5;
                    byte[] $$2 = new byte[4096];
                    if (this.length < 0L) {
                        int $$3;
                        while (($$3 = $$1.read($$2)) != -1) {
                            $$0.write($$2, 0, $$3);
                            this.uploadStatus.bytesWritten += (long)$$3;
                        }
                        break block7;
                    }
                    for (long $$4 = this.length; $$4 > 0L; $$4 -= (long)$$5) {
                        $$5 = $$1.read($$2, 0, (int)Math.min((long)4096L, (long)$$4));
                        if ($$5 == -1) {
                            break;
                        }
                        $$0.write($$2, 0, $$5);
                        this.uploadStatus.bytesWritten += (long)$$5;
                        $$0.flush();
                    }
                }
            }
        }
    }
}